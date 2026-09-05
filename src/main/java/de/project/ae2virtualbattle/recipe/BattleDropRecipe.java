package de.project.ae2virtualbattle.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.project.ae2virtualbattle.registry.ModRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

import java.util.List;

public record BattleDropRecipe(Ingredient target, int minTier, List<BattleDropEntry> drops) implements Recipe<SingleRecipeInput> {

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return target.test(input.item());
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) {
        if (!drops.isEmpty()) {
            return drops.get(0).item().copy();
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        if (!drops.isEmpty()) {
            return drops.get(0).item();
        }
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.BATTLE_DROP_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.BATTLE_DROP_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<BattleDropRecipe> {
        public static final MapCodec<BattleDropRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC.fieldOf("target").forGetter(BattleDropRecipe::target),
                Codec.INT.optionalFieldOf("min_tier", 1).forGetter(BattleDropRecipe::minTier),
                BattleDropEntry.CODEC.listOf().fieldOf("drops").forGetter(BattleDropRecipe::drops)
        ).apply(instance, BattleDropRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, BattleDropRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, BattleDropRecipe::target,
                ByteBufCodecs.VAR_INT, BattleDropRecipe::minTier,
                BattleDropEntry.STREAM_CODEC.apply(ByteBufCodecs.list()), BattleDropRecipe::drops,
                BattleDropRecipe::new
        );

        @Override
        public MapCodec<BattleDropRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, BattleDropRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
