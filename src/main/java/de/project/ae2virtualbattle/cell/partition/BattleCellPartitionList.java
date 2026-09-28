package de.project.ae2virtualbattle.cell.partition;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public record BattleCellPartitionList(List<BattleCellPartition> partitions) {

    public static final BattleCellPartitionList EMPTY = new BattleCellPartitionList(List.of());

    public static final Codec<BattleCellPartitionList> CODEC =
            BattleCellPartition.CODEC.listOf().xmap(BattleCellPartitionList::new, BattleCellPartitionList::partitions);

    public static final StreamCodec<RegistryFriendlyByteBuf, BattleCellPartitionList> STREAM_CODEC =
            BattleCellPartition.STREAM_CODEC.apply(ByteBufCodecs.list()).map(BattleCellPartitionList::new, BattleCellPartitionList::partitions);

    public boolean isEmpty() {
        return partitions == null || partitions.isEmpty();
    }

    public int size() {
        return partitions == null ? 0 : partitions.size();
    }

    public int getTotalPercent() {
        if (partitions == null) return 0;
        int total = 0;
        for (BattleCellPartition p : partitions) {
            total += p.percent();
        }
        return total;
    }

    public int getUnallocatedPercent() {
        return Math.max(0, 100 - getTotalPercent());
    }

    @Nullable
    public BattleCellPartition getPartition(Item item) {
        if (partitions == null) return null;
        for (BattleCellPartition p : partitions) {
            if (p.target() == item) {
                return p;
            }
        }
        return null;
    }

    public boolean contains(Item item) {
        return getPartition(item) != null;
    }

    public long getAllocatedByteLimit(BattleCellPartition partition, long totalBytes) {
        if (partition == null || partition.percent() <= 0) {
            return 0;
        }
        return (totalBytes * partition.percent()) / 100L;
    }

    public BattleCellPartitionList withUpdated(List<BattleCellPartition> newPartitions) {
        return new BattleCellPartitionList(new ArrayList<>(newPartitions));
    }
}
