package com.qiuyue.goetyominous.common.items.lm;

import com.Polarice3.Goety.common.items.ServantSpawnEggItem;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry;
import com.qiuyue.goetyominous.common.init.lm.LmSounds;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.RecordItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.Polarice3.Goety.common.items.ServantSpawnEggs.egg;

public class LmItems {

    public static final DeferredRegister<Item> LM_ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, GoetyOminous.MOD_ID);

    public static final RegistryObject<RecordItem> LM_MUSIC_DISC = LM_ITEMS.register(
            "lm_music_disc",
            () -> new RecordItem(15, LmSounds.LM_MUSIC_DISC.get(),
                    new Item.Properties().stacksTo(1).rarity(Rarity.RARE), 260));

    public static final RegistryObject<ServantSpawnEggItem> OVERGROWN_COLOSSUS_SPAWN_EGG = LM_ITEMS.register(
            "overgrown_colossus_servant_spawn_egg",
            () -> new ServantSpawnEggItem(LmEntityRegistry.OVERGROWN_COLOSSUS_SERVANT, 0x6B8E23, 0x00AA00, egg()));

    public static final RegistryObject<ServantSpawnEggItem> HOVERING_HURRICANE_SPAWN_EGG = LM_ITEMS.register(
            "hovering_hurricane_servant_spawn_egg",
            () -> new ServantSpawnEggItem(LmEntityRegistry.HOVERING_HURRICANE_SERVANT, 0xA3E5FF, 0x4E739C, egg()));

    public static final RegistryObject<ServantSpawnEggItem> CLOUD_GOLEM_SPAWN_EGG = LM_ITEMS.register(
            "cloud_golem_servant_spawn_egg",
            () -> new ServantSpawnEggItem(LmEntityRegistry.CLOUD_GOLEM_SERVANT, 0xD8DCE6, 0x5B6B8C, egg()));

    public static final RegistryObject<ServantSpawnEggItem> SHULKER_MIMIC_SPAWN_EGG = LM_ITEMS.register(
            "shulker_mimic_servant_spawn_egg",
            () -> new ServantSpawnEggItem(LmEntityRegistry.SHULKER_MIMIC_SERVANT, 0x9C6BB0, 0x2B1B36, egg()));

    public static void register(IEventBus modEventBus) {
        LM_ITEMS.register(modEventBus);
    }
}
