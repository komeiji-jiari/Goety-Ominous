package com.qiuyue.someillagerservants.common.items;

import com.Polarice3.Goety.common.items.ServantSpawnEggItem;
import com.qiuyue.someillagerservants.SomeIllagerServants;
import com.qiuyue.someillagerservants.common.init.ModEntityTypes;
import com.qiuyue.someillagerservants.common.items.curios.DarkAnkh;
import com.qiuyue.someillagerservants.common.items.curios.ScreamingSkullJar;
import com.qiuyue.someillagerservants.common.items.revive.SunkenSoulJar;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * 模组物品注册类
 * 负责注册本模组的所有物品到 Forge 注册表
 * 包括刷怪蛋等特殊物品
 */
public class ModItems {
    /**
     * 物品延迟注册表
     * 使用 ForgeRegistries.ITEMS 指定注册表类型为物品注册表
     */
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
            SomeIllagerServants.MOD_ID);

    /**
     * 初始化方法
     * 将物品注册表注册到模组事件总线
     * 必须在模组构造函数中调用此方法以完成注册
     */
    public static void init() {
        ModItems.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static final RegistryObject<Item> SUNKEN_SOUL_JAR = ITEMS.register("sunken_soul_jar",
            () -> new SunkenSoulJar());

    public static final RegistryObject<Item> DARK_ANKH = ITEMS.register("dark_ankh",
            () -> new DarkAnkh());

    public static final RegistryObject<Item> SCREAMING_SKULL_JAR = ITEMS.register("screaming_skull_jar",
            () -> new ScreamingSkullJar());

    /**
     * 征服者仆从刷怪蛋
     * 使用 ServantSpawnEggItem (这是北极冰的仆从刷怪蛋)类型，继承自 Forge 的原版刷怪蛋
     * 参数说明:
     * - ModEntityTypes.CONQUILLAGER_SERVANT: 对应的实体类型
     * - 0xD3D3D3: 刷怪蛋主颜色
     * - 0x0F0F0F: 刷怪蛋副颜色
     * - egg(): 物品属性配置
     */
    public static final RegistryObject<ServantSpawnEggItem> CONQUILLAGER_SERVANT_SPAWN_EGG = ITEMS.register(
            "conquillager_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityTypes.CONQUILLAGER_SERVANT, 0xD3D3D3, 0x0F0F0F, egg()));

    /**
     * 巡查官仆从刷怪蛋
     * - 0xBCA341: 主颜色
     * - 0x5C121B: 副颜色
     */
    public static final RegistryObject<ServantSpawnEggItem> INQUILLAGER_SERVANT_SPAWN_EGG = ITEMS.register(
            "inquillager_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityTypes.INQUILLAGER_SERVANT, 0xBCA341, 0x5C121B, egg()));

    // IllageAndSpillage 联动刷怪蛋已移至 com.qiuyue.someillagerservants.compat.ias.IasItems

    public static final RegistryObject<ServantSpawnEggItem> SUNKEN_NECROMANCER_SERVANT_SPAWN_EGG = ITEMS.register(
            "sunken_necromancer_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityTypes.SUNKEN_NECROMANCER_SERVANT, 0x4A235A, 0xE67E22, egg()));

    public static final RegistryObject<ForgeSpawnEggItem> SUNKEN_NECROMANCER_SPAWN_EGG = ITEMS.register(
            "sunken_necromancer_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntityTypes.SUNKEN_NECROMANCER, 0x1B4F72, 0x7D6608, egg()));

    public static final RegistryObject<ServantSpawnEggItem> HERESIARCH_SERVANT_SPAWN_EGG = ITEMS.register(
            "heresiarch_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityTypes.HERESIARCH_SERVANT, 0x0f1119, 0x4A235A, egg()));

    public static final RegistryObject<ForgeSpawnEggItem> ACOLYTE_SPAWN_EGG = ITEMS.register(
            "acolyte_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntityTypes.ACOLYTE, 0x1B4F72, 0xE67E22, egg()));

    public static final RegistryObject<ServantSpawnEggItem> ACOLYTE_SERVANT_SPAWN_EGG = ITEMS.register(
            "acolyte_servant_spawn_egg",
            () -> new ServantSpawnEggItem(ModEntityTypes.ACOLYTE_SERVANT, 0x1B4F72, 0xE6E6FA, egg()));


    /**
     * 物品属性配置方法
     * 返回基础的 Item.Properties 对象
     * 可在此处添加堆叠数量、耐久度等属性
     *
     * @return 基础物品属性配置
     */
    public static Item.Properties egg() {
        return new Item.Properties();
    }
}
