package de.project.ae2virtualbattle.cell;

import appeng.api.ids.AEComponents;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.GenericStack;
import appeng.core.AppEng;
import appeng.menu.implementations.CellWorkbenchMenu;
import appeng.util.ConfigInventory;
import appeng.util.Platform;
import de.project.ae2virtualbattle.config.VirtualBattleConfig;
import de.project.ae2virtualbattle.recipe.BattleDropRegistry;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Set;

public class BattleCellConfig {

    public static ConfigInventory create(ItemStack stack) {
        Holder holder = new Holder(stack);
        ConfigInventory inv = ConfigInventory.configTypes(63)
                .supportedTypes(Set.of(AEKeyType.items()))
                .slotFilter((slot, key) -> isAllowed(key, stack))
                .changeListener(holder::save)
                .build();
        holder.inv = inv;
        holder.load();
        return inv;
    }

    private static boolean isAllowed(AEKey key, ItemStack cellStack) {
        if (!(key instanceof AEItemKey itemKey)) {
            return false;
        }

        // 1. Target validation: Must be a valid battle target
        if (!BattleDropRegistry.isValidBattleTarget(itemKey.getItem(), null)) {
            return false;
        }

        // 2. Inventory check (if enabled): Player must actually possess the item
        if (VirtualBattleConfig.ENFORCE_INVENTORY_CHECK.get()) {
            Player player = findPlayerInteractingWith(cellStack);
            if (player != null && !player.isCreative()) {
                boolean hasItem = player.getInventory().contains(s -> s.is(itemKey.getItem()))
                        || (player.containerMenu != null && player.containerMenu.getCarried().is(itemKey.getItem()));
                if (!hasItem) {
                    return false;
                }
            }
        }

        return true;
    }

    private static Player findPlayerInteractingWith(ItemStack cellStack) {
        if (Platform.isClient()) {
            if (Platform.hasClientClasses()) {
                return ClientPlayerHelper.getClientPlayer();
            }
        } else {
            var server = AppEng.instance().getCurrentServer();
            if (server != null) {
                for (var player : server.getPlayerList().getPlayers()) {
                    if (player.containerMenu instanceof CellWorkbenchMenu cwm) {
                        if (cwm.getWorkbenchItem() == cellStack || ItemStack.matches(cwm.getWorkbenchItem(), cellStack)) {
                            return player;
                        }
                    }
                }
            }
        }
        return null;
    }

    private static class Holder {
        private final ItemStack stack;
        private ConfigInventory inv;

        public Holder(ItemStack stack) {
            this.stack = stack;
        }

        public void load() {
            List<GenericStack> list = this.stack.getOrDefault(AEComponents.STORAGE_CELL_CONFIG_INV, List.of());
            this.inv.readFromList(list);
        }

        public void save() {
            this.stack.set(AEComponents.STORAGE_CELL_CONFIG_INV, this.inv.toList());
        }
    }

    private static class ClientPlayerHelper {
        public static Player getClientPlayer() {
            return net.minecraft.client.Minecraft.getInstance().player;
        }
    }
}
