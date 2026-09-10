package de.project.ae2virtualbattle.registry;

import de.project.ae2virtualbattle.AE2VirtualBattle;
import de.project.ae2virtualbattle.cell.BattleCellTier;
import de.project.ae2virtualbattle.cell.VirtualBattleCellItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(AE2VirtualBattle.MODID);

    // Housing
    public static final DeferredItem<Item> BATTLE_CELL_HOUSING =
            ITEMS.registerSimpleItem("battle_cell_housing");

    // Storage Components
    public static final DeferredItem<Item> BATTLE_COMPONENT_1K =
            ITEMS.registerSimpleItem("battle_cell_component_1k");
    public static final DeferredItem<Item> BATTLE_COMPONENT_4K =
            ITEMS.registerSimpleItem("battle_cell_component_4k");
    public static final DeferredItem<Item> BATTLE_COMPONENT_16K =
            ITEMS.registerSimpleItem("battle_cell_component_16k");
    public static final DeferredItem<Item> BATTLE_COMPONENT_64K =
            ITEMS.registerSimpleItem("battle_cell_component_64k");
    public static final DeferredItem<Item> BATTLE_COMPONENT_256K =
            ITEMS.registerSimpleItem("battle_cell_component_256k");

    // Complete Storage Cells
    public static final DeferredItem<VirtualBattleCellItem> BATTLE_CELL_1K =
            ITEMS.registerItem("battle_storage_cell_1k", props -> new VirtualBattleCellItem(BattleCellTier.TIER_1K, props));
    public static final DeferredItem<VirtualBattleCellItem> BATTLE_CELL_4K =
            ITEMS.registerItem("battle_storage_cell_4k", props -> new VirtualBattleCellItem(BattleCellTier.TIER_4K, props));
    public static final DeferredItem<VirtualBattleCellItem> BATTLE_CELL_16K =
            ITEMS.registerItem("battle_storage_cell_16k", props -> new VirtualBattleCellItem(BattleCellTier.TIER_16K, props));
    public static final DeferredItem<VirtualBattleCellItem> BATTLE_CELL_64K =
            ITEMS.registerItem("battle_storage_cell_64k", props -> new VirtualBattleCellItem(BattleCellTier.TIER_64K, props));
    public static final DeferredItem<VirtualBattleCellItem> BATTLE_CELL_256K =
            ITEMS.registerItem("battle_storage_cell_256k", props -> new VirtualBattleCellItem(BattleCellTier.TIER_256K, props));
}
