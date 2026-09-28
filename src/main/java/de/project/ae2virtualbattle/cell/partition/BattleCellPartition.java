package de.project.ae2virtualbattle.cell.partition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;

public record BattleCellPartition(
        Item target,
        int percent,
        boolean voidSecondary
) {
    public static final Codec<BattleCellPartition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.ITEM.byNameCodec().fieldOf("target").forGetter(BattleCellPartition::target),
            Codec.INT.fieldOf("percent").forGetter(BattleCellPartition::percent),
            Codec.BOOL.optionalFieldOf("void_secondary", false).forGetter(BattleCellPartition::voidSecondary)
    ).apply(instance, BattleCellPartition::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BattleCellPartition> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.registry(net.minecraft.core.registries.Registries.ITEM), BattleCellPartition::target,
            ByteBufCodecs.VAR_INT, BattleCellPartition::percent,
            ByteBufCodecs.BOOL, BattleCellPartition::voidSecondary,
            BattleCellPartition::new
    );
}
