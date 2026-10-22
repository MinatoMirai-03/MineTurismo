package com.examplebett.inventory;

import com.examplebett.AiObjectMod;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import java.lang.reflect.Constructor;
import java.lang.reflect.Proxy;

public final class AiObjectModMenus {
    private AiObjectModMenus() {
    }

    private interface SimpleMenuFactory<T extends AbstractContainerMenu> {
        T create(int containerId, Inventory inventory);
    }

    //? if >=1.19.4 {
    public static final MenuType<RailwayFormationMenu> RAILWAY_FORMATION_MENU_TYPE =
            new MenuType<>(RailwayFormationMenu::new, net.minecraft.world.flag.FeatureFlags.VANILLA_SET);
    //?} else
    /*public static final MenuType<RailwayFormationMenu> RAILWAY_FORMATION_MENU_TYPE =
            createLegacyMenuType(RailwayFormationMenu::new);*/

    //? if >=1.19.4 {
    public static final MenuType<MassiveAutoAppraisalMenu> MASSIVE_AUTO_APPRAISAL_MENU_TYPE =
            new MenuType<>(MassiveAutoAppraisalMenu::new, net.minecraft.world.flag.FeatureFlags.VANILLA_SET);
    //?} else
    /*public static final MenuType<MassiveAutoAppraisalMenu> MASSIVE_AUTO_APPRAISAL_MENU_TYPE =
            createLegacyMenuType(MassiveAutoAppraisalMenu::new);*/

    //? if <1.19.4 {
    /*@SuppressWarnings("unchecked")
    private static <T extends AbstractContainerMenu> MenuType<T> createLegacyMenuType(SimpleMenuFactory<T> factory) {
        try {
            Constructor<?> chosen = null;
            for (Constructor<?> candidate : MenuType.class.getDeclaredConstructors()) {
                if (candidate.getParameterCount() == 1) {
                    chosen = candidate;
                    break;
                }
            }
            if (chosen == null) {
                throw new IllegalStateException("No 1-arg MenuType constructor found");
            }
            chosen.setAccessible(true);
            Class<?> supplierInterface = chosen.getParameterTypes()[0];
            Object proxy = Proxy.newProxyInstance(supplierInterface.getClassLoader(), new Class<?>[]{supplierInterface},
                    (proxyInstance, method, args) -> factory.create((Integer) args[0], (Inventory) args[1]));
            return (MenuType<T>) chosen.newInstance(proxy);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to construct RailwayFormationMenu's MenuType", e);
        }
    }*/
    //?}

    public static void register() {
        //? if >=1.19.3 {
        Registry.register(net.minecraft.core.registries.BuiltInRegistries.MENU, AiObjectMod.id("railway_formation"), RAILWAY_FORMATION_MENU_TYPE);
        Registry.register(net.minecraft.core.registries.BuiltInRegistries.MENU, AiObjectMod.id("massive_auto_appraisal"), MASSIVE_AUTO_APPRAISAL_MENU_TYPE);
        //?} else
        /*Registry.register(Registry.MENU, AiObjectMod.id("railway_formation"), RAILWAY_FORMATION_MENU_TYPE);
        Registry.register(Registry.MENU, AiObjectMod.id("massive_auto_appraisal"), MASSIVE_AUTO_APPRAISAL_MENU_TYPE);*/
    }
}
