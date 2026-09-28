package de.project.ae2virtualbattle.network;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.implementations.blockentities.IChestOrDrive;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridServiceProvider;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.stacks.AEItemKey;
import appeng.api.storage.cells.CellState;
import appeng.api.storage.cells.StorageCell;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.UpgradeInventories;
import appeng.core.definitions.AEItems;
import de.project.ae2virtualbattle.cell.IVirtualBattleCell;
import de.project.ae2virtualbattle.cell.partition.BattleCellPartition;
import de.project.ae2virtualbattle.cell.partition.BattleCellPartitionList;
import de.project.ae2virtualbattle.config.VirtualBattleConfig;
import de.project.ae2virtualbattle.recipe.BattleDropEntry;
import de.project.ae2virtualbattle.recipe.BattleDropRegistry;
import de.project.ae2virtualbattle.registry.ModItems;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class VirtualBattleGridService implements IGridServiceProvider, IVirtualBattleGridService {

    private final IGrid grid;
    private final Map<Integer, Integer> cellProgress = new HashMap<>();

    public VirtualBattleGridService(IGrid grid) {
        this.grid = grid;
    }

    @Override
    public void onLevelEndTick(Level level) {
        if (level.isClientSide()) {
            return;
        }

        IEnergyService energyService = grid.getEnergyService();
        boolean requireEnergy = VirtualBattleConfig.REQUIRE_AE_ENERGY.get();
        if (requireEnergy && !energyService.isNetworkPowered()) {
            return;
        }

        boolean altered = false;
        RandomSource random = level.getRandom();
        Set<IChestOrDrive> visitedDrives = new HashSet<>();

        for (IGridNode node : grid.getNodes()) {
            if (!node.isActive()) {
                continue;
            }
            if (node.getOwner() instanceof IChestOrDrive drive && visitedDrives.add(drive)) {
                if (!drive.isPowered()) {
                    continue;
                }

                for (int i = 0; i < drive.getCellCount(); i++) {
                    StorageCell cell = drive.getOriginalCellInventory(i);
                    if (cell instanceof IVirtualBattleCell battleCell) {
                        altered |= tickCell(battleCell, level, energyService, requireEnergy, random);
                    }
                }
            }
        }

        if (altered) {
            grid.getStorageService().invalidateCache();
        }
    }

    private boolean tickCell(IVirtualBattleCell battleCell, Level level, IEnergyService energyService, boolean requireEnergy, RandomSource random) {
        IUpgradeInventory upgrades = UpgradeInventories.forItem(battleCell.getItemStack(), 5);
        int speedCards = Math.min(4, upgrades.getInstalledUpgrades(AEItems.SPEED_CARD.asItem()));
        int baseInterval = VirtualBattleConfig.BASE_TICK_INTERVAL.get();

        int targetInterval = switch (speedCards) {
            case 1 -> (int) (baseInterval * 0.70);
            case 2 -> (int) (baseInterval * 0.45);
            case 3 -> (int) (baseInterval * 0.30);
            case 4 -> Math.max(10, (int) (baseInterval * 0.20));
            default -> baseInterval;
        };

        int cellKey = System.identityHashCode(battleCell.getItemStack());
        int progress = cellProgress.getOrDefault(cellKey, 0) + 5;
        if (progress >= targetInterval) {
            cellProgress.put(cellKey, 0);
            return processCell(battleCell, level, energyService, requireEnergy, random, speedCards, upgrades);
        } else {
            cellProgress.put(cellKey, progress);
            return false;
        }
    }

    private boolean processCell(IVirtualBattleCell battleCell, Level level, IEnergyService energyService, boolean requireEnergy, RandomSource random, int speedCards, IUpgradeInventory upgrades) {
        // 1. If whole cell is full, stop immediately
        if (battleCell.isFull() || battleCell.getStatus() == CellState.FULL) {
            return false;
        }

        BattleCellPartitionList partitionList = battleCell.getPartitions();
        if (partitionList.isEmpty()) {
            return false;
        }

        int dropCycles = battleCell.getTier().getDropCount();
        if (dropCycles <= 0) {
            return false;
        }

        double baseEnergy = VirtualBattleConfig.ENERGY_PER_DROP.get();
        double energyMultiplier = Math.pow(1.5, speedCards);
        double energyPerDrop = baseEnergy * energyMultiplier;
        boolean anyInserted = false;

        boolean globalVoidSecondary = de.project.ae2virtualbattle.util.VirtualCellAdapter.hasVoidSecondaryCard(upgrades);

        for (int c = 0; c < dropCycles; c++) {
            if (battleCell.isFull() || battleCell.getStatus() == CellState.FULL) {
                break;
            }

            // Weighted selection across partitions (0 to 99)
            int roll = random.nextInt(100);
            int cumulative = 0;
            BattleCellPartition selectedPartition = null;

            for (BattleCellPartition p : partitionList.partitions()) {
                cumulative += p.percent();
                if (roll < cumulative) {
                    selectedPartition = p;
                    break;
                }
            }

            // If roll falls into unallocated space (or no partition selected), cycle is idle
            if (selectedPartition == null) {
                continue;
            }

            // If this specific partition has reached its capacity, skip it (other partitions can still produce!)
            if (battleCell.isPartitionFull(selectedPartition)) {
                continue;
            }

            Item target = selectedPartition.target();
            if (target == null || !BattleDropRegistry.isValidBattleTarget(target, level)) {
                continue;
            }

            List<BattleDropEntry> dropEntries = BattleDropRegistry.getDropEntries(target, level, battleCell.getTier());
            if (dropEntries.isEmpty()) {
                continue;
            }

            BattleDropRegistry.RolledDrop rolledDrop = BattleDropRegistry.rollDropWithIndex(dropEntries, random);
            if (rolledDrop.stack().isEmpty()) {
                continue;
            }

            boolean voidThisSecondary = globalVoidSecondary || selectedPartition.voidSecondary();

            // A drop is secondary if its entry index > 0 (not the primary drop)
            if (voidThisSecondary && rolledDrop.isSecondary()) {
                // Secondary output is voided — still costs energy
                if (requireEnergy && energyPerDrop > 0) {
                    energyService.extractAEPower(energyPerDrop, Actionable.MODULATE, PowerMultiplier.CONFIG);
                }
                continue;
            }

            AEItemKey key = AEItemKey.of(rolledDrop.stack());
            int dropCount = rolledDrop.stack().getCount();

            // Test if the cell has space to accept this item
            long canInsert = battleCell.injectGeneratedDrop(key, dropCount, Actionable.SIMULATE);
            if (canInsert <= 0) {
                continue;
            }

            // Scale energy proportionally to actual insertion amount
            double scaledEnergy = (canInsert < dropCount) ? energyPerDrop * ((double) canInsert / dropCount) : energyPerDrop;

            // Only consume AE power if the item actually fits into the cell
            if (requireEnergy && scaledEnergy > 0) {
                double extracted = energyService.extractAEPower(scaledEnergy, Actionable.SIMULATE, PowerMultiplier.CONFIG);
                if (extracted < scaledEnergy) {
                    break; // Network ran out of power
                }
                energyService.extractAEPower(scaledEnergy, Actionable.MODULATE, PowerMultiplier.CONFIG);
            }

            long inserted = battleCell.injectGeneratedDrop(key, canInsert, Actionable.MODULATE);
            if (inserted > 0) {
                anyInserted = true;
            }
        }

        return anyInserted;
    }
}
