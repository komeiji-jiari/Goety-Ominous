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
            () -> new ServantSpawnEggItem(LmEntityRegistry.OVERGROWN_COLOSSUS_SERVANT, 0x999999, 0x669900, egg()));

    public static final RegistryObject<ServantSpawnEggItem> HOVERING_HURRICANE_SPAWN_EGG = LM_ITEMS.register(
            "hovering_hurricane_servant_spawn_egg",
            () -> new ServantSpawnEggItem(LmEntityRegistry.HOVERING_HURRICANE_SERVANT, 0xA3E5FF, 0x4E739C, egg()));

    public static final RegistryObject<ServantSpawnEggItem> CLOUD_GOLEM_SPAWN_EGG = LM_ITEMS.register(
            "cloud_golem_servant_spawn_egg",
            () -> new ServantSpawnEggItem(LmEntityRegistry.CLOUD_GOLEM_SERVANT, 0xFFFFFF, 0xCCFFFF, egg()));

    public static final RegistryObject<ServantSpawnEggItem> WANDERING_EYE_SPAWN_EGG = LM_ITEMS.register(
            "wandering_eye_servant_spawn_egg",
            () -> new ServantSpawnEggItem(LmEntityRegistry.WANDERING_EYE_SERVANT, 0xD5DA94, 0x659B7D, egg()));

    public static final RegistryObject<ServantSpawnEggItem> SHULKER_MIMIC_SPAWN_EGG = LM_ITEMS.register(
            "shulker_mimic_servant_spawn_egg",
            () -> new ServantSpawnEggItem(LmEntityRegistry.SHULKER_MIMIC_SERVANT, 0x914A91, 0x543054, egg()));

    public static final RegistryObject<ServantSpawnEggItem> ANNIHILATION_PURSUER_SPAWN_EGG = LM_ITEMS.register(
            "annihilation_pursuer_servant_spawn_egg",
            () -> new ServantSpawnEggItem(LmEntityRegistry.ANNIHILATION_PURSUER_SERVANT, 0x132E37, 0x579143, egg()));

    public static final RegistryObject<ServantSpawnEggItem> FLAME_DRIFTER_SPAWN_EGG = LM_ITEMS.register(
            "flame_drifter_servant_spawn_egg",
            () -> new ServantSpawnEggItem(LmEntityRegistry.FLAME_DRIFTER_SERVANT, 0xD7DA94, 0x88C35A, egg()));

    public static final RegistryObject<ServantSpawnEggItem> FLAMEBORN_WARRIOR_SPAWN_EGG = LM_ITEMS.register(
            "flameborn_warrior_servant_spawn_egg",
            () -> new ServantSpawnEggItem(LmEntityRegistry.FLAMEBORN_WARRIOR_SERVANT, 0x161616, 0x579143, egg()));

    public static final RegistryObject<ServantSpawnEggItem> FLAMEBORN_GUARD_SPAWN_EGG = LM_ITEMS.register(
            "flameborn_guard_servant_spawn_egg",
            () -> new ServantSpawnEggItem(LmEntityRegistry.FLAMEBORN_GUARD_SERVANT, 0x161616, 0x427367, egg()));

    public static void register(IEventBus modEventBus) {
        LM_ITEMS.register(modEventBus);
    }
}
