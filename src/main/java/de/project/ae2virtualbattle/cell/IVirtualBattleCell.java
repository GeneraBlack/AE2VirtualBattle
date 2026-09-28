package de.project.ae2virtualbattle.cell;

import appeng.api.config.Actionable;
import appeng.api.stacks.AEKey;
import appeng.api.storage.cells.CellState;
import appeng.api.storage.cells.ISaveProvider;
import appeng.api.storage.cells.StorageCell;
import de.project.ae2virtualbattle.cell.partition.BattleCellPartition;
import de.project.ae2virtualbattle.cell.partition.BattleCellPartitionList;
import de.project.ae2virtualbattle.registry.ModDataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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

    default BattleCellPartitionList getPartitions() {
        ItemStack stack = getItemStack();
        if (stack.has(ModDataComponents.PARTITIONS.get())) {
            BattleCellPartitionList list = stack.get(ModDataComponents.PARTITIONS.get());
            if (list != null && !list.isEmpty()) {
                return list;
            }
        }
        Item single = getConfiguredTarget();
        if (single != null) {
            return new BattleCellPartitionList(List.of(new BattleCellPartition(single, 100, false)));
        }
        return BattleCellPartitionList.EMPTY;
    }

    default long getStoredCountForTarget(Item target) {
        return 0;
    }

    default boolean isPartitionFull(BattleCellPartition partition) {
        if (isFull()) {
            return true;
        }
        if (partition == null || partition.percent() <= 0) {
            return true;
        }
        long allocatedBytes = (getTier().getTotalBytes() * partition.percent()) / 100L;
        long storedCount = getStoredCountForTarget(partition.target());
        long storedBytes = (storedCount + 7L) / 8L + (long) getTier().getBytesPerType();
        return storedBytes >= allocatedBytes;
    }
}
