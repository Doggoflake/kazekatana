package net.phospherion.kazekatana.screen;

import net.phospherion.kazekatana.KazeKatana;
import net.phospherion.kazekatana.screen.specialz.AppakuTataraFurnaceMenu;
import net.phospherion.kazekatana.screen.specialz.TataraFurnaceMenu;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, KazeKatana.MOD_ID);

    public static final RegistryObject<MenuType<TataraFurnaceMenu>> TATARA_FURNACE_MENU =
            registerMenuType("tatarafurnace_menu", TataraFurnaceMenu::new);

    public static final RegistryObject<MenuType<AppakuTataraFurnaceMenu>> APPAKU_TATARA_FURNACE_MENU =
            registerMenuType("appaku_tatara_furnace_menu", AppakuTataraFurnaceMenu::new);

    private static <T extends AbstractContainerMenu>RegistryObject<MenuType<T>> registerMenuType(String name,
                                                                                                 IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IForgeMenuType.create(factory));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}