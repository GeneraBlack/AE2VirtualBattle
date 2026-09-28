package de.project.ae2virtualbattle;

import appeng.api.networking.GridServices;
import appeng.api.storage.StorageCells;
import de.project.ae2virtualbattle.cell.VirtualBattleCellHandler;
import de.project.ae2virtualbattle.client.VirtualPartitionerClient;
import de.project.ae2virtualbattle.config.VirtualBattleConfig;
import de.project.ae2virtualbattle.network.IVirtualBattleGridService;
import de.project.ae2virtualbattle.network.VirtualBattleGridService;
import de.project.ae2virtualbattle.network.VirtualPartitionerNetworking;
import de.project.ae2virtualbattle.registry.ModBlockEntities;
import de.project.ae2virtualbattle.registry.ModBlocks;
import de.project.ae2virtualbattle.registry.ModCreativeTabs;
import de.project.ae2virtualbattle.registry.ModDataComponents;
import de.project.ae2virtualbattle.registry.ModItems;
import de.project.ae2virtualbattle.registry.ModMenus;
import de.project.ae2virtualbattle.registry.ModRecipes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(AE2VirtualBattle.MODID)
public class AE2VirtualBattle {
    public static final String MODID = "ae2virtualbattle";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public AE2VirtualBattle(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("Initializing AE2 Virtual Battle");

        // Register Config
        modContainer.registerConfig(ModConfig.Type.COMMON, VirtualBattleConfig.SPEC);

        // Register Registries
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModMenus.MENUS.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        ModRecipes.SERIALIZERS.register(modEventBus);
        ModRecipes.RECIPE_TYPES.register(modEventBus);
        ModDataComponents.DATA_COMPONENTS.register(modEventBus);

        // Register Grid Service during mod init
        GridServices.register(IVirtualBattleGridService.class, VirtualBattleGridService.class);

        // Register Setup Listener
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(VirtualPartitionerNetworking::onRegisterPayloadHandlers);
        if (FMLEnvironment.dist.isClient()) {
            modEventBus.addListener(VirtualPartitionerClient::onRegisterMenuScreens);
        }

        // Refresh recipe cache and clear dynamic cache when tags/datapacks update
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener((net.neoforged.neoforge.event.TagsUpdatedEvent event) -> {
            de.project.ae2virtualbattle.recipe.BattleDropRegistry.clearCache();
            var server = net.neoforged.neoforge.server.ServerLifecycleHooks.getCurrentServer();
            if (server != null) {
                de.project.ae2virtualbattle.recipe.BattleDropRegistry.refreshRecipeCache(server.getRecipeManager());
                LOGGER.info("AE2 Virtual Battle: Refreshed recipe cache");
            }
        });
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            LOGGER.info("Registering AE2 Virtual Battle Storage Cell Handler");
            StorageCells.addCellHandler(new VirtualBattleCellHandler());

            // Register Upgrades on all Battle Storage Cells
            for (var cell : java.util.List.of(
                    ModItems.BATTLE_CELL_1K,
                    ModItems.BATTLE_CELL_4K,
                    ModItems.BATTLE_CELL_16K,
                    ModItems.BATTLE_CELL_64K,
                    ModItems.BATTLE_CELL_256K
            )) {
                appeng.api.upgrades.Upgrades.add(appeng.core.definitions.AEItems.SPEED_CARD.asItem(), cell.get(), 4);
                appeng.api.upgrades.Upgrades.add(ModItems.VOID_SECONDARY_CARD.get(), cell.get(), 1);
                appeng.api.upgrades.Upgrades.add(appeng.core.definitions.AEItems.VOID_CARD.asItem(), cell.get(), 1);
                for (String ns : java.util.List.of("ae2virtualmine", "ae2virtualgarden", "ae2virtualwell")) {
                    net.minecraft.resources.ResourceLocation loc = net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(ns, "void_secondary_card");
                    if (net.minecraft.core.registries.BuiltInRegistries.ITEM.containsKey(loc)) {
                        net.minecraft.world.item.Item sisterCard = net.minecraft.core.registries.BuiltInRegistries.ITEM.get(loc);
                        appeng.api.upgrades.Upgrades.add(sisterCard, cell.get(), 1);
                    }
                }
            }
        });
    }
}
