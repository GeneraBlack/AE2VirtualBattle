package de.project.ae2virtualbattle.network;

import de.project.ae2virtualbattle.AE2VirtualBattle;
import de.project.ae2virtualbattle.cell.partition.BattleCellPartitionList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record SetPartitionsPayload(BattleCellPartitionList partitions) implements CustomPacketPayload {
    public static final Type<SetPartitionsPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(AE2VirtualBattle.MODID, "set_partitions"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SetPartitionsPayload> STREAM_CODEC =
            StreamCodec.composite(
                    BattleCellPartitionList.STREAM_CODEC,
                    SetPartitionsPayload::partitions,
                    SetPartitionsPayload::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
