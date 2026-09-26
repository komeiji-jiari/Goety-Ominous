package com.qiuyue.goetyominous.common.items.of;

import com.Polarice3.Goety.common.items.ServantSpawnEggItem;
import com.Polarice3.Goety.common.items.magic.MagicFocus;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.init.of.OfEntityRegistry;
import com.qiuyue.goetyominous.common.magic.spells.ac.ExtinctionBreathSpell;
import com.qiuyue.goetyominous.common.magic.spells.of.VoltSpell;
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

    public static final RegistryObject<ServantSpawnEggItem> UMBER_SPIDER_SERVANT_SPAWN_EGG = OF_ITEMS.register(
            "umber_spider_servant_spawn_egg",
            () -> new ServantSpawnEggItem(OfEntityRegistry.UMBER_SPIDER_SERVANT, 0x241631, 0xB03BE0, egg()));


    /**
     * 伏特瑶风暴召唤聚晶。
     * <p>
     * 必须注册在这里（OF 联动物品类）而不是 ModItems。它的法术 {@code VoltSummonSpell} 会构造
     * {@code VoltServant}，而 VoltServant 实现了 OF 的 AttackState / EliteVariant 接口。
     * 只要该类被加载，JVM 在链接时就会去解析这两个接口，未安装 OF 的整合包里直接
     * NoClassDefFoundError 崩溃（本 mod 0.3.0 之前的启动崩溃就是这个原因）。
     * 放在 OfItems 里，它就只会在 {@code OpposingForceCompat.isOpposingForceLoaded()} 为真时加载。
     */
    public static final RegistryObject<Item> VOLT_FOCUS = OF_ITEMS.register(
            "volt_focus",
            () -> new com.Polarice3.Goety.common.items.magic.MagicFocus(
                    new com.qiuyue.goetyominous.common.magic.spells.VoltSummonSpell()));

    public static final RegistryObject<ServantSpawnEggItem> TERROR_SERVANT_SPAWN_EGG = OF_ITEMS.register(
            "terror_servant_spawn_egg",
            () -> new ServantSpawnEggItem(OfEntityRegistry.TERROR_SERVANT, 0x5B6770, 0xC9A227, egg()));


    public static final RegistryObject<ServantSpawnEggItem> FIRE_SLIME_SERVANT_SPAWN_EGG = OF_ITEMS.register(
            "fire_slime_servant_spawn_egg",
            () -> new ServantSpawnEggItem(OfEntityRegistry.FIRE_SLIME_SERVANT, 0xFB921B, 0xDB3709, egg()));

    public static final RegistryObject<ServantSpawnEggItem> GUZZLER_SERVANT_SPAWN_EGG = OF_ITEMS.register(
            "guzzler_servant_spawn_egg",
            () -> new ServantSpawnEggItem(OfEntityRegistry.GUZZLER_SERVANT, 0x160E2C, 0x8956C2, egg()));

    public static final RegistryObject<ServantSpawnEggItem> SKYVERN_SERVANT_SPAWN_EGG = OF_ITEMS.register(
            "skyvern_servant_spawn_egg",
            () -> new ServantSpawnEggItem(OfEntityRegistry.SKYVERN_SERVANT, 0x2E3A4D, 0x8FD6E8, egg()));

    public static final RegistryObject<Item> VOLT_FOCUS = OF_ITEMS.register("volt_focus",
            () -> new MagicFocus(new VoltSpell()));


    public static Item.Properties egg() {
        return new Item.Properties();
    }

    public static void register(IEventBus modEventBus) {
        OF_ITEMS.register(modEventBus);
    }
}
