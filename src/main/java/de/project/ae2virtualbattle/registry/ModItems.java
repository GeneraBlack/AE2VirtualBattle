package de.project.ae2virtualbattle.registry;

import de.project.ae2virtualbattle.AE2VirtualBattle;
import de.project.ae2virtualbattle.cell.BattleCellTier;
import de.project.ae2virtualbattle.cell.VirtualBattleCellItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(AE2VirtualBattle.MODID);

    // Housing
    public static final DeferredHolder<Item, Item> BATTLE_CELL_HOUSING =
            ITEMS.register("battle_cell_housing", () -> new Item(new Item.Properties()));

    // Storage Components
    public static final DeferredHolder<Item, Item> BATTLE_COMPONENT_1K =
            ITEMS.register("battle_cell_component_1k", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> BATTLE_COMPONENT_4K =
            ITEMS.register("battle_cell_component_4k", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> BATTLE_COMPONENT_16K =
            ITEMS.register("battle_cell_component_16k", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> BATTLE_COMPONENT_64K =
            ITEMS.register("battle_cell_component_64k", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, Item> BATTLE_COMPONENT_256K =
            ITEMS.register("battle_cell_component_256k", () -> new Item(new Item.Properties()));

    // Complete Storage Cells
    public static final DeferredHolder<Item, VirtualBattleCellItem> BATTLE_CELL_1K =
            ITEMS.register("battle_storage_cell_1k", () -> new VirtualBattleCellItem(BattleCellTier.TIER_1K, new Item.Properties()));
    public static final DeferredHolder<Item, VirtualBattleCellItem> BATTLE_CELL_4K =
            ITEMS.register("battle_storage_cell_4k", () -> new VirtualBattleCellItem(BattleCellTier.TIER_4K, new Item.Properties()));
    public static final DeferredHolder<Item, VirtualBattleCellItem> BATTLE_CELL_16K =
            ITEMS.register("battle_storage_cell_16k", () -> new VirtualBattleCellItem(BattleCellTier.TIER_16K, new Item.Properties()));
    public static final DeferredHolder<Item, VirtualBattleCellItem> BATTLE_CELL_64K =
            ITEMS.register("battle_storage_cell_64k", () -> new VirtualBattleCellItem(BattleCellTier.TIER_64K, new Item.Properties()));
    public static final DeferredHolder<Item, VirtualBattleCellItem> BATTLE_CELL_256K =
            ITEMS.register("battle_storage_cell_256k", () -> new VirtualBattleCellItem(BattleCellTier.TIER_256K, new Item.Properties()));
}
