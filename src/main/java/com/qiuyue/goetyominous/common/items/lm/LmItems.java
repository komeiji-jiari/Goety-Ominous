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

    public static void register(IEventBus modEventBus) {
        LM_ITEMS.register(modEventBus);
    }
}
