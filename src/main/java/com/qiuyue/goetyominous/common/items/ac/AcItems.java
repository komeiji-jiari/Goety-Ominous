package com.qiuyue.goetyominous.common.items.ac;

import com.Polarice3.Goety.common.items.ServantSpawnEggItem;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.init.ac.AcBlockRegistry;
import com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class AcItems {

    public static final DeferredRegister<Item> AC_ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, GoetyOminous.MOD_ID);

    public static final RegistryObject<ServantSpawnEggItem> GROTTOCERATOPS_SERVANT_SPAWN_EGG =
            AC_ITEMS.register("grottoceratops_servant_spawn_egg",
                                        () -> new ServantSpawnEggItem(AcEntityRegistry.GROTTOCERATOPS_SERVANT, 0xAC3B03, 0x3B1C14, egg()));

    public static final RegistryObject<BlockItem> GROTTOCERATOPS_SERVANT_EGG =
            AC_ITEMS.register("grottoceratops_servant_egg",
                    () -> new BlockItem(AcBlockRegistry.GROTTOCERATOPS_SERVANT_EGG.get(), egg()));

    public static final RegistryObject<ServantSpawnEggItem> TREMORSAURUS_SERVANT_SPAWN_EGG =
            AC_ITEMS.register("tremorsaurus_servant_spawn_egg",
                                        () -> new ServantSpawnEggItem(AcEntityRegistry.TREMORSAURUS_SERVANT, 0xBA8604, 0x3F6E14, egg()));

    public static final RegistryObject<BlockItem> TREMORSAURUS_SERVANT_EGG =
            AC_ITEMS.register("tremorsaurus_servant_egg",
                    () -> new BlockItem(AcBlockRegistry.TREMORSAURUS_SERVANT_EGG.get(), egg()));

    public static Item.Properties egg() {
        return new Item.Properties();
    }

    public static void register(IEventBus modEventBus) {
        AC_ITEMS.register(modEventBus);
    }
}
