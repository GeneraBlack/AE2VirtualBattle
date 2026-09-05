package de.project.ae2virtualbattle.cell;

import appeng.api.config.Actionable;
import appeng.api.stacks.AEKey;
import appeng.api.storage.cells.CellState;
import appeng.api.storage.cells.ISaveProvider;
import appeng.api.storage.cells.StorageCell;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface IVirtualBattleCell extends StorageCell {

    ItemStack getItemStack();

    @Nullable
    ISaveProvider getSaveProvider();

    @Nullable
    Item getConfiguredTarget();

    BattleCellTier getTier();

    @Override
    CellState getStatus();

    boolean isFull();

    long injectGeneratedDrop(AEKey key, long amount, Actionable mode);

    void persist();
}
