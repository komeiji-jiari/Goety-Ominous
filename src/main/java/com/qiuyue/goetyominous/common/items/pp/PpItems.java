package com.qiuyue.goetyominous.common.items.pp;

import com.Polarice3.Goety.common.items.ServantSpawnEggItem;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.init.pp.PpEntityRegistry;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class PpItems {

    public static final DeferredRegister<Item> PP_ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, GoetyOminous.MOD_ID);

    public static final RegistryObject<ServantSpawnEggItem> PIGLIN_ALCHEMIST_SERVANT_SPAWN_EGG = PP_ITEMS.register(
            "piglin_alchemist_servant_spawn_egg",
            () -> new ServantSpawnEggItem(PpEntityRegistry.PIGLIN_ALCHEMIST_SERVANT, 10944611, 16380836, egg()));

    public static final RegistryObject<ServantSpawnEggItem> PIGLIN_TRAVELER_SERVANT_SPAWN_EGG = PP_ITEMS.register(
            "piglin_traveler_servant_spawn_egg",
            () -> new ServantSpawnEggItem(PpEntityRegistry.PIGLIN_TRAVELER_SERVANT, 3848107, 16380836, egg()));

    public static final RegistryObject<ServantSpawnEggItem> ZPIGLIN_ALCHEMIST_SERVANT_SPAWN_EGG = PP_ITEMS.register(
            "zpiglin_alchemist_servant_spawn_egg",
            () -> new ServantSpawnEggItem(PpEntityRegistry.ZPIGLIN_ALCHEMIST_SERVANT, 9413466, 3955242, egg()));

    public static final RegistryObject<ServantSpawnEggItem> ZPIGLIN_TRAVELER_SERVANT_SPAWN_EGG = PP_ITEMS.register(
            "zpiglin_traveler_servant_spawn_egg",
            () -> new ServantSpawnEggItem(PpEntityRegistry.ZPIGLIN_TRAVELER_SERVANT, 9413466, 7027242, egg()));

    public static Item.Properties egg() {
        return new Item.Properties();
    }

    public static void register(IEventBus modEventBus) {
        PP_ITEMS.register(modEventBus);
    }
}
