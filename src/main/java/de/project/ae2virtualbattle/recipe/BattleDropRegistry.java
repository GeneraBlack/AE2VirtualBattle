package de.project.ae2virtualbattle.recipe;

import de.project.ae2virtualbattle.registry.ModRecipes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

import java.util.*;

public class BattleDropRegistry {

    private static final Map<Item, List<BattleDropEntry>> BUILTIN_DROPS = new HashMap<>();
    private static final Map<Item, List<BattleDropEntry>> DYNAMIC_CACHE = new HashMap<>();

    static {
        registerHostileDefaults();
        registerPassiveDefaults();
        registerBossDefaults();
    }

    private static void registerHostileDefaults() {
        // Zombie & Variants
        List<BattleDropEntry> zombieDrops = List.of(
                new BattleDropEntry(new ItemStack(Items.ROTTEN_FLESH), 70, 1, 3),
                new BattleDropEntry(new ItemStack(Items.IRON_INGOT), 15, 1, 1),
                new BattleDropEntry(new ItemStack(Items.CARROT), 8, 1, 1),
                new BattleDropEntry(new ItemStack(Items.POTATO), 7, 1, 1)
        );
        addBuiltin(Items.ROTTEN_FLESH, zombieDrops);
        addBuiltin(Items.ZOMBIE_SPAWN_EGG, zombieDrops);
        addBuiltin(Items.ZOMBIE_HEAD, zombieDrops);
        addBuiltin(Items.HUSK_SPAWN_EGG, List.of(
                new BattleDropEntry(new ItemStack(Items.ROTTEN_FLESH), 70, 1, 3),
                new BattleDropEntry(new ItemStack(Items.IRON_INGOT), 15, 1, 1),
                new BattleDropEntry(new ItemStack(Items.SAND), 15, 1, 2)
        ));
        List<BattleDropEntry> drownedDrops = List.of(
                new BattleDropEntry(new ItemStack(Items.ROTTEN_FLESH), 65, 1, 3),
                new BattleDropEntry(new ItemStack(Items.COPPER_INGOT), 25, 1, 2),
                new BattleDropEntry(new ItemStack(Items.NAUTILUS_SHELL), 10, 1, 1)
        );
        addBuiltin(Items.DROWNED_SPAWN_EGG, drownedDrops);
        addBuiltin(Items.NAUTILUS_SHELL, drownedDrops);

        // Skeleton & Variants
        List<BattleDropEntry> skeletonDrops = List.of(
                new BattleDropEntry(new ItemStack(Items.BONE), 65, 1, 3),
                new BattleDropEntry(new ItemStack(Items.ARROW), 30, 1, 3),
                new BattleDropEntry(new ItemStack(Items.BOW), 5, 1, 1)
        );
        addBuiltin(Items.BONE, skeletonDrops);
        addBuiltin(Items.SKELETON_SPAWN_EGG, skeletonDrops);
        addBuiltin(Items.SKELETON_SKULL, skeletonDrops);
        addBuiltin(Items.STRAY_SPAWN_EGG, skeletonDrops);

        // Wither Skeleton
        List<BattleDropEntry> witherSkeletonDrops = List.of(
                new BattleDropEntry(new ItemStack(Items.COAL), 60, 1, 2),
                new BattleDropEntry(new ItemStack(Items.BONE), 30, 1, 2),
                new BattleDropEntry(new ItemStack(Items.WITHER_SKELETON_SKULL), 10, 1, 1)
        );
        addBuiltin(Items.WITHER_SKELETON_SKULL, witherSkeletonDrops);
        addBuiltin(Items.WITHER_SKELETON_SPAWN_EGG, witherSkeletonDrops);

        // Creeper
        List<BattleDropEntry> creeperDrops = List.of(
                new BattleDropEntry(new ItemStack(Items.GUNPOWDER), 90, 1, 3),
                new BattleDropEntry(new ItemStack(Items.MUSIC_DISC_CAT), 5, 1, 1),
                new BattleDropEntry(new ItemStack(Items.TNT), 5, 1, 1)
        );
        addBuiltin(Items.GUNPOWDER, creeperDrops);
        addBuiltin(Items.CREEPER_SPAWN_EGG, creeperDrops);
        addBuiltin(Items.CREEPER_HEAD, creeperDrops);

        // Spider & Cave Spider
        List<BattleDropEntry> spiderDrops = List.of(
                new BattleDropEntry(new ItemStack(Items.STRING), 70, 1, 3),
                new BattleDropEntry(new ItemStack(Items.SPIDER_EYE), 30, 1, 2)
        );
        addBuiltin(Items.STRING, spiderDrops);
        addBuiltin(Items.SPIDER_EYE, spiderDrops);
        addBuiltin(Items.SPIDER_SPAWN_EGG, spiderDrops);
        addBuiltin(Items.CAVE_SPIDER_SPAWN_EGG, spiderDrops);

        // Enderman
        List<BattleDropEntry> endermanDrops = List.of(
                new BattleDropEntry(new ItemStack(Items.ENDER_PEARL), 85, 1, 2),
                new BattleDropEntry(new ItemStack(Items.END_STONE), 15, 1, 1)
        );
        addBuiltin(Items.ENDER_PEARL, endermanDrops);
        addBuiltin(Items.ENDERMAN_SPAWN_EGG, endermanDrops);

        // Blaze
        List<BattleDropEntry> blazeDrops = List.of(
                new BattleDropEntry(new ItemStack(Items.BLAZE_ROD), 80, 1, 2),
                new BattleDropEntry(new ItemStack(Items.GLOWSTONE_DUST), 20, 1, 2)
        );
        addBuiltin(Items.BLAZE_ROD, blazeDrops);
        addBuiltin(Items.BLAZE_SPAWN_EGG, blazeDrops);

        // Slime & Magma Cube
        addBuiltin(Items.SLIME_BALL, List.of(new BattleDropEntry(new ItemStack(Items.SLIME_BALL), 100, 1, 3)));
        addBuiltin(Items.SLIME_SPAWN_EGG, List.of(new BattleDropEntry(new ItemStack(Items.SLIME_BALL), 100, 1, 3)));
        addBuiltin(Items.MAGMA_CREAM, List.of(new BattleDropEntry(new ItemStack(Items.MAGMA_CREAM), 100, 1, 3)));
        addBuiltin(Items.MAGMA_CUBE_SPAWN_EGG, List.of(new BattleDropEntry(new ItemStack(Items.MAGMA_CREAM), 100, 1, 3)));

        // Ghast
        List<BattleDropEntry> ghastDrops = List.of(
                new BattleDropEntry(new ItemStack(Items.GUNPOWDER), 65, 1, 3),
                new BattleDropEntry(new ItemStack(Items.GHAST_TEAR), 35, 1, 1)
        );
        addBuiltin(Items.GHAST_TEAR, ghastDrops);
        addBuiltin(Items.GHAST_SPAWN_EGG, ghastDrops);

        // Witch
        List<BattleDropEntry> witchDrops = List.of(
                new BattleDropEntry(new ItemStack(Items.REDSTONE), 25, 1, 2),
                new BattleDropEntry(new ItemStack(Items.GLOWSTONE_DUST), 25, 1, 2),
                new BattleDropEntry(new ItemStack(Items.SUGAR), 20, 1, 2),
                new BattleDropEntry(new ItemStack(Items.GUNPOWDER), 15, 1, 2),
                new BattleDropEntry(new ItemStack(Items.GLASS_BOTTLE), 10, 1, 2),
                new BattleDropEntry(new ItemStack(Items.STICK), 5, 1, 2)
        );
        addBuiltin(Items.WITCH_SPAWN_EGG, witchDrops);
        addBuiltin(Items.GLASS_BOTTLE, witchDrops);

        // Guardian & Elder Guardian
        List<BattleDropEntry> guardianDrops = List.of(
                new BattleDropEntry(new ItemStack(Items.PRISMARINE_SHARD), 60, 1, 2),
                new BattleDropEntry(new ItemStack(Items.PRISMARINE_CRYSTALS), 30, 1, 1),
                new BattleDropEntry(new ItemStack(Items.COD), 10, 1, 1)
        );
        addBuiltin(Items.PRISMARINE_SHARD, guardianDrops);
        addBuiltin(Items.PRISMARINE_CRYSTALS, guardianDrops);
        addBuiltin(Items.GUARDIAN_SPAWN_EGG, guardianDrops);
        addBuiltin(Items.ELDER_GUARDIAN_SPAWN_EGG, List.of(
                new BattleDropEntry(new ItemStack(Items.PRISMARINE_SHARD), 50, 1, 3),
                new BattleDropEntry(new ItemStack(Items.PRISMARINE_CRYSTALS), 30, 1, 2),
                new BattleDropEntry(new ItemStack(Items.WET_SPONGE), 20, 1, 1)
        ));

        // Phantom
        List<BattleDropEntry> phantomDrops = List.of(
                new BattleDropEntry(new ItemStack(Items.PHANTOM_MEMBRANE), 85, 1, 2),
                new BattleDropEntry(new ItemStack(Items.LEATHER), 15, 1, 1)
        );
        addBuiltin(Items.PHANTOM_MEMBRANE, phantomDrops);
        addBuiltin(Items.PHANTOM_SPAWN_EGG, phantomDrops);

        // Shulker
        List<BattleDropEntry> shulkerDrops = List.of(
                new BattleDropEntry(new ItemStack(Items.SHULKER_SHELL), 100, 1, 2)
        );
        addBuiltin(Items.SHULKER_SHELL, shulkerDrops);
        addBuiltin(Items.SHULKER_SPAWN_EGG, shulkerDrops);

        // Piglin & Variants
        List<BattleDropEntry> piglinDrops = List.of(
                new BattleDropEntry(new ItemStack(Items.GOLD_NUGGET), 65, 2, 5),
                new BattleDropEntry(new ItemStack(Items.PORKCHOP), 20, 1, 2),
                new BattleDropEntry(new ItemStack(Items.GOLD_INGOT), 15, 1, 1)
        );
        addBuiltin(Items.PIGLIN_SPAWN_EGG, piglinDrops);
        addBuiltin(Items.PIGLIN_BRUTE_SPAWN_EGG, piglinDrops);
        addBuiltin(Items.PIGLIN_HEAD, piglinDrops);
        addBuiltin(Items.ZOMBIFIED_PIGLIN_SPAWN_EGG, List.of(
                new BattleDropEntry(new ItemStack(Items.GOLD_NUGGET), 65, 2, 5),
                new BattleDropEntry(new ItemStack(Items.ROTTEN_FLESH), 25, 1, 2),
                new BattleDropEntry(new ItemStack(Items.GOLD_INGOT), 10, 1, 1)
        ));

        // Hoglin
        List<BattleDropEntry> hoglinDrops = List.of(
                new BattleDropEntry(new ItemStack(Items.PORKCHOP), 70, 1, 3),
                new BattleDropEntry(new ItemStack(Items.LEATHER), 30, 1, 2)
        );
        addBuiltin(Items.HOGLIN_SPAWN_EGG, hoglinDrops);

        // Breeze
        addBuiltin(Items.BREEZE_ROD, List.of(new BattleDropEntry(new ItemStack(Items.BREEZE_ROD), 100, 1, 2)));
        addBuiltin(Items.BREEZE_SPAWN_EGG, List.of(new BattleDropEntry(new ItemStack(Items.BREEZE_ROD), 100, 1, 2)));

        // Warden
        List<BattleDropEntry> wardenDrops = List.of(
                new BattleDropEntry(new ItemStack(Items.SCULK_CATALYST), 50, 1, 1),
                new BattleDropEntry(new ItemStack(Items.ECHO_SHARD), 50, 1, 2)
        );
        addBuiltin(Items.SCULK_CATALYST, wardenDrops);
        addBuiltin(Items.ECHO_SHARD, wardenDrops);
        addBuiltin(Items.WARDEN_SPAWN_EGG, wardenDrops);
    }

    private static void registerBossDefaults() {
        // Wither
        addBuiltin(Items.NETHER_STAR, List.of(new BattleDropEntry(new ItemStack(Items.NETHER_STAR), 100, 1, 1)));

        // Ender Dragon
        List<BattleDropEntry> dragonDrops = List.of(
                new BattleDropEntry(new ItemStack(Items.DRAGON_BREATH), 70, 1, 2),
                new BattleDropEntry(new ItemStack(Items.OBSIDIAN), 20, 1, 2),
                new BattleDropEntry(new ItemStack(Items.DRAGON_HEAD), 10, 1, 1)
        );
        addBuiltin(Items.DRAGON_BREATH, dragonDrops);
        addBuiltin(Items.DRAGON_HEAD, dragonDrops);
        addBuiltin(Items.DRAGON_EGG, dragonDrops);
    }

    private static void registerPassiveDefaults() {
        // Cow
        List<BattleDropEntry> cowDrops = List.of(
                new BattleDropEntry(new ItemStack(Items.BEEF), 70, 1, 3),
                new BattleDropEntry(new ItemStack(Items.LEATHER), 30, 1, 2)
        );
        addBuiltin(Items.BEEF, cowDrops);
        addBuiltin(Items.COW_SPAWN_EGG, cowDrops);
        addBuiltin(Items.LEATHER, cowDrops);

        // Pig
        addBuiltin(Items.PORKCHOP, List.of(new BattleDropEntry(new ItemStack(Items.PORKCHOP), 100, 1, 3)));
        addBuiltin(Items.PIG_SPAWN_EGG, List.of(new BattleDropEntry(new ItemStack(Items.PORKCHOP), 100, 1, 3)));

        // Sheep
        List<BattleDropEntry> sheepDrops = List.of(
                new BattleDropEntry(new ItemStack(Items.MUTTON), 70, 1, 3),
                new BattleDropEntry(new ItemStack(Items.WHITE_WOOL), 30, 1, 2)
        );
        addBuiltin(Items.MUTTON, sheepDrops);
        addBuiltin(Items.SHEEP_SPAWN_EGG, sheepDrops);
        addBuiltin(Items.WHITE_WOOL, sheepDrops);

        // Chicken
        List<BattleDropEntry> chickenDrops = List.of(
                new BattleDropEntry(new ItemStack(Items.CHICKEN), 50, 1, 2),
                new BattleDropEntry(new ItemStack(Items.FEATHER), 35, 1, 2),
                new BattleDropEntry(new ItemStack(Items.EGG), 15, 1, 2)
        );
        addBuiltin(Items.CHICKEN, chickenDrops);
        addBuiltin(Items.FEATHER, chickenDrops);
        addBuiltin(Items.EGG, chickenDrops);
        addBuiltin(Items.CHICKEN_SPAWN_EGG, chickenDrops);

        // Squid & Glow Squid
        addBuiltin(Items.INK_SAC, List.of(new BattleDropEntry(new ItemStack(Items.INK_SAC), 100, 1, 3)));
        addBuiltin(Items.SQUID_SPAWN_EGG, List.of(new BattleDropEntry(new ItemStack(Items.INK_SAC), 100, 1, 3)));
        addBuiltin(Items.GLOW_INK_SAC, List.of(new BattleDropEntry(new ItemStack(Items.GLOW_INK_SAC), 100, 1, 3)));
        addBuiltin(Items.GLOW_SQUID_SPAWN_EGG, List.of(new BattleDropEntry(new ItemStack(Items.GLOW_INK_SAC), 100, 1, 3)));

        // Rabbit
        List<BattleDropEntry> rabbitDrops = List.of(
                new BattleDropEntry(new ItemStack(Items.RABBIT), 60, 1, 2),
                new BattleDropEntry(new ItemStack(Items.RABBIT_HIDE), 30, 1, 2),
                new BattleDropEntry(new ItemStack(Items.RABBIT_FOOT), 10, 1, 1)
        );
        addBuiltin(Items.RABBIT, rabbitDrops);
        addBuiltin(Items.RABBIT_HIDE, rabbitDrops);
        addBuiltin(Items.RABBIT_FOOT, rabbitDrops);
        addBuiltin(Items.RABBIT_SPAWN_EGG, rabbitDrops);

        // Iron Golem
        List<BattleDropEntry> golemDrops = List.of(
                new BattleDropEntry(new ItemStack(Items.IRON_INGOT), 80, 3, 5),
                new BattleDropEntry(new ItemStack(Items.POPPY), 20, 1, 2)
        );
        addBuiltin(Items.IRON_GOLEM_SPAWN_EGG, golemDrops);
    }

    private static void addBuiltin(Item target, List<BattleDropEntry> drops) {
        BUILTIN_DROPS.put(target, drops);
    }

    public static boolean isValidBattleTarget(Item item, Level level) {
        if (BUILTIN_DROPS.containsKey(item) || DYNAMIC_CACHE.containsKey(item)) {
            return true;
        }
        if (item instanceof SpawnEggItem) {
            return true;
        }
        if (level != null) {
            SingleRecipeInput input = new SingleRecipeInput(new ItemStack(item));
            if (level.getRecipeManager().getRecipeFor(ModRecipes.BATTLE_DROP_TYPE.get(), input, level).isPresent()) {
                return true;
            }
        }
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
        String path = id.getPath();
        return path.contains("spawn_egg") || path.contains("head") || path.contains("skull") || path.contains("meat") || path.contains("flesh");
    }

    public static List<BattleDropEntry> getDropEntries(Item target, Level level) {
        if (BUILTIN_DROPS.containsKey(target)) {
            return BUILTIN_DROPS.get(target);
        }

        if (DYNAMIC_CACHE.containsKey(target)) {
            return DYNAMIC_CACHE.get(target);
        }

        if (level != null) {
            SingleRecipeInput input = new SingleRecipeInput(new ItemStack(target));
            Optional<RecipeHolder<BattleDropRecipe>> recipe = level.getRecipeManager()
                    .getRecipeFor(ModRecipes.BATTLE_DROP_TYPE.get(), input, level);
            if (recipe.isPresent()) {
                List<BattleDropEntry> drops = recipe.get().value().drops();
                DYNAMIC_CACHE.put(target, drops);
                return drops;
            }
        }

        // Dynamic fallback for any unknown SpawnEggItem: produces its default drop if possible or 1 item
        if (target instanceof SpawnEggItem) {
            List<BattleDropEntry> fallback = List.of(new BattleDropEntry(new ItemStack(Items.ROTTEN_FLESH), 100, 1, 2));
            DYNAMIC_CACHE.put(target, fallback);
            return fallback;
        }

        return List.of();
    }

    public static ItemStack rollDrop(List<BattleDropEntry> entries, RandomSource random) {
        if (entries == null || entries.isEmpty()) {
            return ItemStack.EMPTY;
        }

        int totalWeight = 0;
        for (BattleDropEntry entry : entries) {
            totalWeight += entry.weight();
        }

        if (totalWeight <= 0) {
            return ItemStack.EMPTY;
        }

        int roll = random.nextInt(totalWeight);
        int current = 0;
        for (BattleDropEntry entry : entries) {
            current += entry.weight();
            if (roll < current) {
                int min = entry.minCount();
                int max = entry.maxCount();
                int count = (min >= max) ? min : (min + random.nextInt(max - min + 1));
                ItemStack result = entry.item().copy();
                result.setCount(count);
                return result;
            }
        }

        return ItemStack.EMPTY;
    }
}
