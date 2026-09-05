package de.project.ae2virtualbattle.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record BattleDropEntry(ItemStack item, int weight, int minCount, int maxCount) {
    public static final Codec<BattleDropEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStack.CODEC.fieldOf("item").forGetter(BattleDropEntry::item),
            Codec.INT.optionalFieldOf("weight", 1).forGetter(BattleDropEntry::weight),
            Codec.INT.optionalFieldOf("min_count", 1).forGetter(BattleDropEntry::minCount),
            Codec.INT.optionalFieldOf("max_count", 1).forGetter(BattleDropEntry::maxCount)
    ).apply(instance, BattleDropEntry::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BattleDropEntry> STREAM_CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC, BattleDropEntry::item,
            ByteBufCodecs.VAR_INT, BattleDropEntry::weight,
            ByteBufCodecs.VAR_INT, BattleDropEntry::minCount,
            ByteBufCodecs.VAR_INT, BattleDropEntry::maxCount,
            BattleDropEntry::new
    );
}
