package com.qiuyue.goetyominous.common.items.am;

import com.Polarice3.Goety.common.items.ServantSpawnEggItem;
import com.Polarice3.Goety.common.items.magic.MagicFocus;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.init.am.AmEntityRegistry;
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

    public static final RegistryObject<MagicFocus> MURMUR_FOCUS = AM_ITEMS.register(
            "murmur_focus",
            () -> new MagicFocus(new MurmurSpell()));

    public static final RegistryObject<ServantSpawnEggItem> CRIMSON_MOSQUITO_SERVANT_SPAWN_EGG =
            AM_ITEMS.register("crimson_mosquito_servant_spawn_egg",
                    () -> new ServantSpawnEggItem(AmEntityRegistry.CRIMSON_MOSQUITO_SERVANT, 0xC21E2B, 0x1C1C1C, egg()));

    // === 在这里添加物品注册 ===
    // 示例：
    // public static final RegistryObject<ServantSpawnEggItem> XXX_SERVANT_SPAWN_EGG =
    //         AM_ITEMS.register("xxx_servant_spawn_egg",
    //                 () -> new ServantSpawnEggItem(AmEntityRegistry.XXX_SERVANT, 0xFFFFFF, 0x000000, egg()));

    public static Item.Properties egg() {
        return new Item.Properties();
    }

    public static void register(IEventBus modEventBus) {
        AM_ITEMS.register(modEventBus);
    }
}
