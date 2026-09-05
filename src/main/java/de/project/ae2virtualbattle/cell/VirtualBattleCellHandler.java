package de.project.ae2virtualbattle.cell;

import appeng.api.storage.cells.ICellHandler;
import appeng.api.storage.cells.ISaveProvider;
import appeng.api.storage.cells.StorageCell;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class VirtualBattleCellHandler implements ICellHandler {

    @Override
    public boolean isCell(ItemStack is) {
        return !is.isEmpty() && is.getItem() instanceof VirtualBattleCellItem;
    }

    @Nullable
    @Override
    public StorageCell getCellInventory(ItemStack is, @Nullable ISaveProvider host) {
        if (is.getItem() instanceof VirtualBattleCellItem cellItem) {
            return new VirtualBattleCellInventory(is, host, cellItem.getTier());
        }
        return null;
    }
}
