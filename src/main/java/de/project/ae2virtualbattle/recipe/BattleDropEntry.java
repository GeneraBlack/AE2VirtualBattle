package de.project.ae2virtualbattle.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;

public record BattleDropEntry(ItemStackTemplate item, int weight, int minCount, int maxCount) {

    public BattleDropEntry(ItemStack stack, int weight, int minCount, int maxCount) {
        this(ItemStackTemplate.fromNonEmptyStack(stack), weight, minCount, maxCount);
    }

    public static final Codec<BattleDropEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStackTemplate.CODEC.fieldOf("item").forGetter(BattleDropEntry::item),
            Codec.INT.optionalFieldOf("weight", 1).forGetter(BattleDropEntry::weight),
            Codec.INT.optionalFieldOf("min_count", 1).forGetter(BattleDropEntry::minCount),
            Codec.INT.optionalFieldOf("max_count", 1).forGetter(BattleDropEntry::maxCount)
    ).apply(instance, BattleDropEntry::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BattleDropEntry> STREAM_CODEC = StreamCodec.composite(
            ItemStackTemplate.STREAM_CODEC, BattleDropEntry::item,
            ByteBufCodecs.VAR_INT, BattleDropEntry::weight,
            ByteBufCodecs.VAR_INT, BattleDropEntry::minCount,
            ByteBufCodecs.VAR_INT, BattleDropEntry::maxCount,
            BattleDropEntry::new
    );

    public ItemStack createStack() {
        return item.create();
    }
}

