package com.qiuyue.goetyominous.common.init;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.inventory.container.FungusPackContainer;
import com.qiuyue.goetyominous.common.inventory.container.NetherEternalCauldronContainer;
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

    public static final RegistryObject<MenuType<NetherEternalCauldronContainer>> NETHER_ETERNAL_CAULDRON =
            CONTAINER_TYPES.register("nether_eternal_cauldron",
                    () -> IForgeMenuType.create(NetherEternalCauldronContainer::createContainerClientSide));

    public static void register(IEventBus modEventBus) {
        CONTAINER_TYPES.register(modEventBus);
    }
}
