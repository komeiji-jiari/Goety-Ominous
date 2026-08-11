package com.qiuyue.goetyominous.common.items.am;

import com.Polarice3.Goety.common.items.ServantSpawnEggItem;
import com.Polarice3.Goety.common.items.magic.MagicFocus;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.init.am.AmEntityRegistry;
import com.qiuyue.goetyominous.common.magic.spells.am.BloodSpraySpell;
import com.qiuyue.goetyominous.common.magic.spells.am.CrimsonSpell;
import com.qiuyue.goetyominous.common.magic.spells.am.FarseerSpell;
import com.qiuyue.goetyominous.common.magic.spells.am.MurmurSpell;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * AlexMobs 联动物品注册类，在这里添加刷怪蛋等物品的注册
 */
public class AmItems {

    public static final DeferredRegister<Item> AM_ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, GoetyOminous.MOD_ID);

    public static final RegistryObject<ServantSpawnEggItem> MURMUR_SERVANT_SPAWN_EGG =
            AM_ITEMS.register("murmur_servant_spawn_egg",
                    () -> new ServantSpawnEggItem(AmEntityRegistry.MURMUR_SERVANT, 0x445566, 0x8899AA, egg()));

    public static final RegistryObject<ServantSpawnEggItem> CRIMSON_MOSQUITO_SERVANT_SPAWN_EGG =
            AM_ITEMS.register("crimson_mosquito_servant_spawn_egg",
                    () -> new ServantSpawnEggItem(AmEntityRegistry.CRIMSON_MOSQUITO_SERVANT, 0x53403F, 0xC11A1A, egg()));

    public static final RegistryObject<ServantSpawnEggItem> WARPED_MOSCO_SERVANT_SPAWN_EGG =
            AM_ITEMS.register("warped_mosco_servant_spawn_egg",
                    () -> new ServantSpawnEggItem(AmEntityRegistry.WARPED_MOSCO_SERVANT, 0x322F58, 0x5B5EF1, egg()));

    public static final RegistryObject<ServantSpawnEggItem> FARSEER_SERVANT_SPAWN_EGG =
            AM_ITEMS.register("farseer_servant_spawn_egg",
                    () -> new ServantSpawnEggItem(AmEntityRegistry.FARSEER_SERVANT, 0x33374F, 0x91FF59, egg()));

    public static final RegistryObject<ServantSpawnEggItem> TUSKLIN_SERVANT_SPAWN_EGG =
            AM_ITEMS.register("tusklin_servant_spawn_egg",
                    () -> new ServantSpawnEggItem(AmEntityRegistry.TUSKLIN_SERVANT, 0x5A3B24, 0xC1A15C, egg()));

    public static final RegistryObject<ServantSpawnEggItem> ZOMBIE_CROCODILE_SERVANT_SPAWN_EGG =
            AM_ITEMS.register("zombie_crocodile_servant_spawn_egg",
                    () -> new ServantSpawnEggItem(AmEntityRegistry.ZOMBIE_CROCODILE_SERVANT, 0x4E5B31, 0xD6C77E, egg()));

    public static final RegistryObject<ServantSpawnEggItem> BUNFUNGUS_SERVANT_SPAWN_EGG =
            AM_ITEMS.register("bunfungus_servant_spawn_egg",
                    () -> new ServantSpawnEggItem(AmEntityRegistry.BUNFUNGUS_SERVANT, 0x6F6D91, 0xC92B29, egg()));

    public static final RegistryObject<ServantSpawnEggItem> ILLAGER_ELEPHANT_SERVANT_SPAWN_EGG =
            AM_ITEMS.register("illager_elephant_servant_spawn_egg",
                    // 基色 0x3E2E2F = 贴图主色(深红棕身体)；高光 0xEDE5D1 = 贴图象牙/肚皮奶油色
                    () -> new ServantSpawnEggItem(AmEntityRegistry.ILLAGER_ELEPHANT_SERVANT, 0x3E2E2F, 0xEDE5D1, egg()));

    public static final RegistryObject<WarpedSteroidsItem> WARPED_STEROIDS =
            AM_ITEMS.register("warped_steroids", WarpedSteroidsItem::new);

    public static final RegistryObject<MagicFocus> MURMUR_FOCUS = AM_ITEMS.register(
            "murmur_focus",
            () -> new MagicFocus(new MurmurSpell()));

    public static final RegistryObject<MagicFocus> CRIMSON_FOCUS = AM_ITEMS.register(
            "crimson_focus",
            () -> new MagicFocus(new CrimsonSpell()));

    public static final RegistryObject<MagicFocus> BLOODSPRAY_FOCUS = AM_ITEMS.register(
            "bloodspray_focus",
            () -> new MagicFocus(new BloodSpraySpell()));

    public static final RegistryObject<MagicFocus> FARSEER_FOCUS = AM_ITEMS.register(
            "farseer_focus",
            () -> new MagicFocus(new FarseerSpell()));


    public static Item.Properties egg() {
        return new Item.Properties();
    }

    public static void register(IEventBus modEventBus) {
        AM_ITEMS.register(modEventBus);
    }
}
