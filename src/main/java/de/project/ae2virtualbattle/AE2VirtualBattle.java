package de.project.ae2virtualbattle;

import appeng.api.networking.GridServices;
import appeng.api.storage.StorageCells;
import de.project.ae2virtualbattle.cell.VirtualBattleCellHandler;
import de.project.ae2virtualbattle.config.VirtualBattleConfig;
import de.project.ae2virtualbattle.network.IVirtualBattleGridService;
import de.project.ae2virtualbattle.network.VirtualBattleGridService;
import de.project.ae2virtualbattle.registry.ModCreativeTabs;
import de.project.ae2virtualbattle.registry.ModItems;
import de.project.ae2virtualbattle.registry.ModRecipes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
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
        ModItems.ITEMS.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        ModRecipes.SERIALIZERS.register(modEventBus);
        ModRecipes.RECIPE_TYPES.register(modEventBus);

        // Register Grid Service during mod init
        GridServices.register(IVirtualBattleGridService.class, VirtualBattleGridService.class);

        // Register Setup Listener
        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            LOGGER.info("Registering AE2 Virtual Battle Storage Cell Handler");
            StorageCells.addCellHandler(new VirtualBattleCellHandler());
        });
    }
}
