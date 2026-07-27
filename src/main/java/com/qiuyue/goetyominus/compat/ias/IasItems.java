package com.qiuyue.goetyominus.compat.ias;

import com.Polarice3.Goety.common.items.ServantSpawnEggItem;
import com.qiuyue.goetyominus.GoetyOminous;
import com.qiuyue.goetyominus.common.items.revive.MysteriousContract;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * IllageAndSpillage 联动物品注册类
 * 负责注册所有 I&S 仆从相关的物品（如刷怪蛋）
 * 注意：这个类只在 IllageAndSpillage 模组加载时才会被调用
 */
public class IasItems {

    /**
     * I&S 物品延迟注册表
     */
    public static final DeferredRegister<Item> IAS_ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, GoetyOminous.MOD_ID);

    public static final RegistryObject<MysteriousContract> MYSTERIOUS_CONTRACT = IAS_ITEMS.register(
            "mysterious_contract",
            () -> new MysteriousContract());

    public static final RegistryObject<ServantSpawnEggItem> TWITTOLLAGER_SERVANT_SPAWN_EGG = IAS_ITEMS.register(
            "twittollager_servant_spawn_egg",
            () -> new ServantSpawnEggItem(IasEntityRegistry.TWITTOLLAGER_SERVANT, 0xD3D3D3, 0x5C121B, egg()));

    public static final RegistryObject<ServantSpawnEggItem> ABSORBER_SERVANT_SPAWN_EGG = IAS_ITEMS.register(
            "absorber_servant_spawn_egg",
            () -> new ServantSpawnEggItem(IasEntityRegistry.ABSORBER_SERVANT, 0x8B8000, 0x4A235A, egg()));

    public static final RegistryObject<ServantSpawnEggItem> MAGISPELLER_SERVANT_SPAWN_EGG = IAS_ITEMS.register(
            "magispeller_servant_spawn_egg",
            () -> new ServantSpawnEggItem(IasEntityRegistry.MAGISPELLER_SERVANT, 0x2E0854, 0xE6E6FA, egg()));

    /**
     * 物品属性配置方法
     * @return 基础物品属性配置
     */
    public static Item.Properties egg() {
        return new Item.Properties();
    }

    /**
     * 注册 I&S 物品到模组事件总线
     * @param modEventBus 模组事件总线
     */
    public static void register(IEventBus modEventBus) {
        IAS_ITEMS.register(modEventBus);
    }
}
