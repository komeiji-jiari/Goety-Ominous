package com.qiuyue.goetyominous.common.items.of;

import com.Polarice3.Goety.common.items.ServantSpawnEggItem;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.init.of.OfEntityRegistry;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class OfItems {

    public static final DeferredRegister<Item> OF_ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, GoetyOminous.MOD_ID);

    public static final RegistryObject<ServantSpawnEggItem> RAMBLER_SERVANT_SPAWN_EGG = OF_ITEMS.register(
            "rambler_servant_spawn_egg",
            () -> new ServantSpawnEggItem(OfEntityRegistry.RAMBLER_SERVANT, 0xE8E0D0, 0xC4BBA8, egg()));

    public static final RegistryObject<ServantSpawnEggItem> DICER_SERVANT_SPAWN_EGG = OF_ITEMS.register(
            "dicer_servant_spawn_egg",
            () -> new ServantSpawnEggItem(OfEntityRegistry.DICER_SERVANT, 0xD8D8D8, 0x2A8BD6, egg()));

    public static final RegistryObject<ServantSpawnEggItem> VOLT_SERVANT_SPAWN_EGG = OF_ITEMS.register(
            "volt_servant_spawn_egg",
            () -> new ServantSpawnEggItem(OfEntityRegistry.VOLT_SERVANT, 0x9B5DE5, 0x2EC4F6, egg()));

    public static final RegistryObject<ServantSpawnEggItem> TREMBLER_SERVANT_SPAWN_EGG = OF_ITEMS.register(
            "trembler_servant_spawn_egg",
            () -> new ServantSpawnEggItem(OfEntityRegistry.TREMBLER_SERVANT, 0x9C8F82, 0x5A4636, egg()));


    public static Item.Properties egg() {
        return new Item.Properties();
    }

    public static void register(IEventBus modEventBus) {
        OF_ITEMS.register(modEventBus);
    }
}
