package de.project.ae2virtualbattle.cell;

import appeng.api.config.FuzzyMode;
import appeng.api.ids.AEComponents;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.StorageCells;
import appeng.api.storage.cells.ICellWorkbenchItem;
import appeng.api.storage.cells.StorageCell;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.UpgradeInventories;
import appeng.core.AEConfig;
import appeng.core.definitions.AEItems;
import appeng.core.localization.Tooltips;
import appeng.items.storage.StorageCellTooltipComponent;
import appeng.util.ConfigInventory;
import de.project.ae2virtualbattle.config.VirtualBattleConfig;
import de.project.ae2virtualbattle.recipe.BattleDropRegistry;
import de.project.ae2virtualbattle.registry.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.*;
import java.util.function.Consumer;

public class VirtualBattleCellItem extends Item implements ICellWorkbenchItem {

    private final BattleCellTier tier;

    public VirtualBattleCellItem(BattleCellTier tier, Properties properties) {
        super(properties.stacksTo(1));
        this.tier = tier;
    }

    public BattleCellTier getTier() {
        return tier;
    }

    public int getBytes(ItemStack stack) {
        return tier.getTotalBytes();
    }

    public int getBytesPerType(ItemStack stack) {
        return tier.getBytesPerType();
    }

    public int getTotalTypes(ItemStack stack) {
        return tier.getTotalTypes();
    }

    public double getIdleDrain() {
        return tier.getIdleDrain();
    }

    @Override
    public IUpgradeInventory getUpgrades(ItemStack stack) {
        return UpgradeInventories.forItem(stack, 5);
    }

    @Override
    public ConfigInventory getConfigInventory(ItemStack stack) {
        return BattleCellConfig.create(stack);
    }

    @Override
    public FuzzyMode getFuzzyMode(ItemStack stack) {
        return stack.getOrDefault(AEComponents.STORAGE_CELL_FUZZY_MODE, FuzzyMode.IGNORE_ALL);
    }

    @Override
    public void setFuzzyMode(ItemStack stack, FuzzyMode mode) {
        stack.set(AEComponents.STORAGE_CELL_FUZZY_MODE, mode);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltipAdder, flag);

        StorageCell cell = StorageCells.getCellInventory(stack, null);
        if (cell instanceof VirtualBattleCellInventory battleInv) {
            tooltipAdder.accept(Tooltips.bytesUsed(battleInv.getUsedBytes(), battleInv.getTotalBytes()));
            tooltipAdder.accept(Tooltips.typesUsed(battleInv.getStoredItemTypes(), battleInv.getTotalItemTypes()));
        } else {
            tooltipAdder.accept(Tooltips.bytesUsed(0, tier.getTotalBytes()));
            tooltipAdder.accept(Tooltips.typesUsed(0, tier.getTotalTypes()));
        }

        IUpgradeInventory upgrades = getUpgrades(stack);
        int speedCards = Math.min(4, upgrades.getInstalledUpgrades(AEItems.SPEED_CARD.asItem()));
        int baseInterval = VirtualBattleConfig.SPEC.isLoaded() ? VirtualBattleConfig.BASE_TICK_INTERVAL.get() : 100;
        int intervalTicks = switch (speedCards) {
            case 1 -> (int) (baseInterval * 0.70);
            case 2 -> (int) (baseInterval * 0.45);
            case 3 -> (int) (baseInterval * 0.30);
            case 4 -> Math.max(10, (int) (baseInterval * 0.20));
            default -> baseInterval;
        };
        int drops = tier.getDropCount();
        double seconds = intervalTicks / 20.0;

        tooltipAdder.accept(Component.translatable("tooltip.ae2virtualbattle.tier", tier.getTierName())
                .withStyle(ChatFormatting.GOLD));

        if (speedCards > 0) {
            tooltipAdder.accept(Component.translatable("tooltip.ae2virtualbattle.production_speed", drops, String.format(Locale.ROOT, "%.1f", seconds), speedCards)
                    .withStyle(ChatFormatting.AQUA));
        } else {
            tooltipAdder.accept(Component.translatable("tooltip.ae2virtualbattle.production", drops, String.format(Locale.ROOT, "%.1f", seconds))
                    .withStyle(ChatFormatting.GRAY));
        }

        boolean hasVoidSecondary = de.project.ae2virtualbattle.util.VirtualCellAdapter.hasVoidSecondaryCard(upgrades);
        if (hasVoidSecondary) {
            tooltipAdder.accept(Component.translatable("tooltip.ae2virtualbattle.void_secondary_active")
                    .withStyle(ChatFormatting.DARK_PURPLE));
        }

        if (stack.has(ModDataComponents.PARTITIONS.get())) {
            var partitionList = stack.get(ModDataComponents.PARTITIONS.get());
            if (partitionList != null && !partitionList.isEmpty()) {
                tooltipAdder.accept(Component.translatable("tooltip.ae2virtualbattle.partitions_header", partitionList.size())
                        .withStyle(ChatFormatting.AQUA));
                for (var p : partitionList.partitions()) {
                    var line = Component.literal(" ▪ ")
                            .append(Component.translatable(p.target().getDescriptionId()).withStyle(ChatFormatting.YELLOW))
                            .append(Component.literal(" (" + p.percent() + "%)").withStyle(ChatFormatting.GRAY));
                    if (p.voidSecondary()) {
                        line.append(Component.literal(" [Void]").withStyle(ChatFormatting.DARK_PURPLE));
                    }
                    tooltipAdder.accept(line);
                }
                if (partitionList.getUnallocatedPercent() > 0) {
                    tooltipAdder.accept(Component.literal(" ▪ Unallocated: " + partitionList.getUnallocatedPercent() + "%")
                            .withStyle(ChatFormatting.DARK_GRAY));
                }
            } else {
                tooltipAdder.accept(Component.translatable("tooltip.ae2virtualbattle.not_configured")
                        .withStyle(ChatFormatting.DARK_GRAY));
            }
        } else {
            List<GenericStack> config = stack.get(AEComponents.STORAGE_CELL_CONFIG_INV);
            Item configuredItem = null;
            if (config != null && !config.isEmpty()) {
                for (GenericStack entry : config) {
                    if (entry != null && entry.what() instanceof AEItemKey itemKey) {
                        configuredItem = itemKey.getItem();
                        break;
                    }
                }
            }

            if (configuredItem != null) {
                tooltipAdder.accept(Component.translatable("tooltip.ae2virtualbattle.configured_target",
                                Component.translatable(configuredItem.getDescriptionId()))
                        .withStyle(ChatFormatting.YELLOW));
            } else {
                tooltipAdder.accept(Component.translatable("tooltip.ae2virtualbattle.not_configured")
                        .withStyle(ChatFormatting.DARK_GRAY));
            }
        }
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        StorageCell cell = StorageCells.getCellInventory(stack, null);
        if (!(cell instanceof VirtualBattleCellInventory battleInv)) {
            return Optional.empty();
        }

        // Show upgrades
        IUpgradeInventory upgrades = getUpgrades(stack);
        List<ItemStack> upgradeStacks = new ArrayList<>();
        try {
            if (AEConfig.instance().isTooltipShowCellUpgrades()) {
                for (ItemStack upgrade : upgrades) {
                    if (!upgrade.isEmpty()) {
                        upgradeStacks.add(upgrade);
                    }
                }
            }
        } catch (Throwable ignored) {
        }

        // Show contents
        KeyCounter keyCounter = new KeyCounter();
        battleInv.getAvailableStacks(keyCounter);

        List<GenericStack> content = new ArrayList<>();
        try {
            if (AEConfig.instance().isTooltipShowCellContent()) {
                int maxCountShown = AEConfig.instance().getTooltipMaxCellContentShown();
                for (var entry : keyCounter) {
                    content.add(new GenericStack(entry.getKey(), entry.getLongValue()));
                }

                content.sort(Comparator.comparingLong(GenericStack::amount).reversed());
                boolean hasMoreContent = content.size() > maxCountShown;
                if (content.size() > maxCountShown) {
                    content = new ArrayList<>(content.subList(0, maxCountShown));
                }
                return Optional.of(new StorageCellTooltipComponent(upgradeStacks, content, hasMoreContent, true));
            }
        } catch (Throwable ignored) {
        }

        return Optional.of(new StorageCellTooltipComponent(upgradeStacks, Collections.emptyList(), false, true));
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        InteractionHand otherHand = hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        ItemStack otherStack = player.getItemInHand(otherHand);

        if (player.isShiftKeyDown()) {
            if (!otherStack.isEmpty()) {
                // Quick-partition using mob item / spawn egg in off-hand
                if (BattleDropRegistry.isValidBattleTarget(otherStack.getItem(), level)) {
                    if (!level.isClientSide()) {
                        AEItemKey key = AEItemKey.of(otherStack.getItem());
                        stack.set(AEComponents.STORAGE_CELL_CONFIG_INV, List.of(new GenericStack(key, 1)));
                        stack.remove(ModDataComponents.PARTITIONS.get());
                        player.sendOverlayMessage(Component.translatable("message.ae2virtualbattle.configured",
                                Component.translatable(otherStack.getItem().getDescriptionId())).withStyle(ChatFormatting.GOLD));
                    }
                    return InteractionResult.SUCCESS;
                }
            } else {
                // Clear configuration — guarded if cell has items
                if (!level.isClientSide()) {
                    StorageCell cell = StorageCells.getCellInventory(stack, null);
                    if (cell instanceof VirtualBattleCellInventory battleInv && battleInv.getStoredItemTypes() > 0) {
                        player.sendOverlayMessage(Component.translatable("message.ae2virtualbattle.clear_blocked").withStyle(ChatFormatting.RED));
                        return InteractionResult.FAIL;
                    }
                    stack.remove(AEComponents.STORAGE_CELL_CONFIG_INV);
                    stack.remove(ModDataComponents.PARTITIONS.get());
                    player.sendOverlayMessage(Component.translatable("message.ae2virtualbattle.cleared")
                            .withStyle(ChatFormatting.RED));
                }
                return InteractionResult.SUCCESS;
            }
        }

        return super.use(level, player, hand);
    }
}
