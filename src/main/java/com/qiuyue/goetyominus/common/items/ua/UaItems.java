package com.qiuyue.goetyominus.common.items.ua;

import com.Polarice3.Goety.common.items.ServantSpawnEggItem;
import com.Polarice3.Goety.common.items.magic.MagicFocus;
import com.qiuyue.goetyominus.GoetyOminous;
import com.qiuyue.goetyominus.common.init.ua.UaEntityRegistry;
import com.qiuyue.goetyominus.common.magic.spells.ua.FlareSpell;
import com.qiuyue.goetyominus.common.magic.spells.ua.ThrasherSpell;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
/**
 * UA 联动物品注册类
 * 负责注册所有 UA 仆从相关的物品（如刷怪蛋）
 * 注意：这个类只在 UA 模组加载时才会被调用
 */
public class UaItems {

    /**
     * UA 物品延迟注册表
     */
    public static final DeferredRegister<Item> UA_ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, GoetyOminous.MOD_ID);

    public static final RegistryObject<ServantSpawnEggItem> THRASHER_SERVANT_SPAWN_EGG = UA_ITEMS.register(
            "thrasher_servant_spawn_egg",
            () -> new ServantSpawnEggItem(UaEntityRegistry.THRASHER_SERVANT, 0x4A7C9E, 0x2C5F7D, egg()));

    public static final RegistryObject<ServantSpawnEggItem> GREAT_THRASHER_SERVANT_SPAWN_EGG = UA_ITEMS.register(
            "great_thrasher_servant_spawn_egg",
            () -> new ServantSpawnEggItem(UaEntityRegistry.GREAT_THRASHER_SERVANT, 0x2C5F7D, 0x1A3A4D, egg()));

    public static final RegistryObject<ServantSpawnEggItem> FLARE_SERVANT_SPAWN_EGG = UA_ITEMS.register(
            "flare_servant_spawn_egg",
            () -> new ServantSpawnEggItem(UaEntityRegistry.FLARE_SERVANT, 0xFF6B35, 0xF7C59F, egg()));

    public static final RegistryObject<MagicFocus> THRASHER_FOCUS = UA_ITEMS.register(
            "thrasher_focus",
            () -> new MagicFocus(new ThrasherSpell()));

    public static final RegistryObject<MagicFocus> FLARE_FOCUS = UA_ITEMS.register(
            "flare_focus",
            () -> new MagicFocus(new FlareSpell()));


    /**
     * 物品属性配置方法
     * @return 基础物品属性配置
     */
    public static Item.Properties egg() {
        return new Item.Properties();
    }

    /**
     * 注册 UA 物品到模组事件总线
     * @param modEventBus 模组事件总线
     */
    public static void register(net.minecraftforge.eventbus.api.IEventBus modEventBus) {
        UA_ITEMS.register(modEventBus);
    }
}
