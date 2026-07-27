package com.qiuyue.goetyominus.common.init;

import com.qiuyue.goetyominus.GoetyOminous;
import com.qiuyue.goetyominus.client.inventory.container.FungusPackContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModContainerTypes {
    public static final DeferredRegister<MenuType<?>> CONTAINER_TYPES =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, GoetyOminous.MOD_ID);

    public static final RegistryObject<MenuType<FungusPackContainer>> FUNGUS_PACK =
            CONTAINER_TYPES.register("fungus_pack",
                    () -> IForgeMenuType.create(FungusPackContainer::createContainerClientSide));

    public static void register(IEventBus modEventBus) {
        CONTAINER_TYPES.register(modEventBus);
    }
}
