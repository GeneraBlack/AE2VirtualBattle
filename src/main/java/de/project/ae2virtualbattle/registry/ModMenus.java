package de.project.ae2virtualbattle.registry;

import de.project.ae2virtualbattle.AE2VirtualBattle;
import de.project.ae2virtualbattle.menu.VirtualPartitionerMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, AE2VirtualBattle.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<VirtualPartitionerMenu>> PARTITIONER_MENU =
            MENUS.register("virtual_partitioner", () -> IMenuTypeExtension.create(VirtualPartitionerMenu::new));
}
