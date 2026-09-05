package de.project.ae2virtualbattle.registry;

import de.project.ae2virtualbattle.AE2VirtualBattle;
import de.project.ae2virtualbattle.recipe.BattleDropRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, AE2VirtualBattle.MODID);

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, AE2VirtualBattle.MODID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<BattleDropRecipe>> BATTLE_DROP_TYPE =
            RECIPE_TYPES.register("battle_drop", () -> RecipeType.simple(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(AE2VirtualBattle.MODID, "battle_drop")));

    public static final DeferredHolder<RecipeSerializer<?>, BattleDropRecipe.Serializer> BATTLE_DROP_SERIALIZER =
            SERIALIZERS.register("battle_drop", BattleDropRecipe.Serializer::new);
}
