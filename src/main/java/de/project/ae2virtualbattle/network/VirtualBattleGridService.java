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
import de.project.ae2virtualbattle.cell.IVirtualBattleCell;
import de.project.ae2virtualbattle.config.VirtualBattleConfig;
import de.project.ae2virtualbattle.recipe.BattleDropEntry;
import de.project.ae2virtualbattle.recipe.BattleDropRegistry;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VirtualBattleGridService implements IGridServiceProvider, IVirtualBattleGridService {

    private final IGrid grid;
    private int tickCounter = 0;

    public VirtualBattleGridService(IGrid grid) {
        this.grid = grid;
    }

    @Override
    public void onLevelEndTick(Level level) {
        if (level.isClientSide()) {
            return;
        }

        tickCounter++;
        int interval = VirtualBattleConfig.BASE_TICK_INTERVAL.get();
        if (tickCounter < interval) {
            return;
        }
        tickCounter = 0;

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
                        altered |= processCell(battleCell, level, energyService, requireEnergy, random);
                    }
                }
            }
        }

        if (altered) {
            grid.getStorageService().invalidateCache();
        }
    }

    private boolean processCell(IVirtualBattleCell battleCell, Level level, IEnergyService energyService, boolean requireEnergy, RandomSource random) {
        // 1. If cell is full, stop immediately and do not generate or consume power
        if (battleCell.isFull() || battleCell.getStatus() == CellState.FULL) {
            return false;
        }

        Item target = battleCell.getConfiguredTarget();
        if (target == null) {
            return false;
        }

        List<BattleDropEntry> dropEntries = BattleDropRegistry.getDropEntries(target, level);
        if (dropEntries.isEmpty()) {
            return false;
        }

        int dropCycles = battleCell.getTier().getDropCount();
        if (dropCycles <= 0) {
            return false;
        }

        double energyPerDrop = VirtualBattleConfig.ENERGY_PER_DROP.get();
        boolean anyInserted = false;

        for (int c = 0; c < dropCycles; c++) {
            // Check if cell has become full during the cycle
            if (battleCell.isFull() || battleCell.getStatus() == CellState.FULL) {
                break; // Stop generating, cell is full!
            }

            ItemStack dropStack = BattleDropRegistry.rollDrop(dropEntries, random);
            if (dropStack.isEmpty()) {
                continue;
            }

            AEItemKey key = AEItemKey.of(dropStack);

            // Test if the cell has space to accept this item
            long canInsert = battleCell.injectGeneratedDrop(key, dropStack.getCount(), Actionable.SIMULATE);
            if (canInsert <= 0) {
                // Cell is full or cannot accept this drop, stop immediately
                break;
            }

            // Only consume AE power if the item actually fits into the cell
            if (requireEnergy && energyPerDrop > 0) {
                double extracted = energyService.extractAEPower(energyPerDrop, Actionable.SIMULATE, PowerMultiplier.CONFIG);
                if (extracted < energyPerDrop) {
                    break; // Network ran out of power
                }
                energyService.extractAEPower(energyPerDrop, Actionable.MODULATE, PowerMultiplier.CONFIG);
            }

            long inserted = battleCell.injectGeneratedDrop(key, canInsert, Actionable.MODULATE);
            if (inserted > 0) {
                anyInserted = true;
            }
        }

        if (anyInserted) {
            battleCell.persist();
        }

        return anyInserted;
    }
}
