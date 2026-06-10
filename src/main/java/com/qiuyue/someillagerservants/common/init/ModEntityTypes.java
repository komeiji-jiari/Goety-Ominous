package com.qiuyue.someillagerservants.common.init;

import com.qiuyue.someillagerservants.common.entities.ally.illager.*;
import com.qiuyue.someillagerservants.common.entities.ally.mobs.*;

import com.qiuyue.someillagerservants.common.entities.hostile.Acolyte;
import com.qiuyue.someillagerservants.common.entities.hostile.SunkenNecromancer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import static com.qiuyue.someillagerservants.SomeIllagerServants.MOD_ID;

/**
 * 模组实体类型注册类
 * 负责注册本模组的所有实体类型到 Forge 注册表
 * 同时定义模型层位置，用于客户端渲染器绑定模型
 */
public class ModEntityTypes {
    /**
     * 实体类型延迟注册表
     * 使用 DeferredRegister 可以安全地在模组事件总线上注册实体
     * 注册表类型为 ForgeRegistries.ENTITY_TYPES(实体类型注册表)
     */
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister
            .create(ForgeRegistries.ENTITY_TYPES, MOD_ID);

    /**
     * 模型层位置已移至客户端类 ModEntityLayers 中
     */

    /**
     * 征服者仆从实体类型注册对象
     * 注册名为"conquillager_servant"的实体
     * 使用 EntityType.Builder 配置实体属性:
     * - 实体工厂方法：ConquillagerServant::new
     * - 生物分类：MobCategory.MISC(杂项生物)
     * - 实体大小：宽 0.6F, 高 1.95F
     * - 客户端追踪范围：8 个区块
     */
    public static final RegistryObject<EntityType<ConquillagerServant>> CONQUILLAGER_SERVANT = ENTITY_TYPES
            .register(
                    "conquillager_servant",
                    () -> EntityType.Builder.of(ConquillagerServant::new, MobCategory.MISC)
                            .sized(0.6F, 1.95F) // 设置实体碰撞箱大小
                            .clientTrackingRange(8) // 设置客户端同步距离
                            .build(MOD_ID + ":conquillager_servant"));

    /**
     * 巡查官仆从实体类型注册对象
     * 配置与征服者仆从类似，但使用 InquillagerServant 类
     */
    public static final RegistryObject<EntityType<InquillagerServant>> INQUILLAGER_SERVANT = ENTITY_TYPES.register(
            "inquillager_servant",
            () -> EntityType.Builder.of(InquillagerServant::new, MobCategory.MISC)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8)
                    .build(MOD_ID + ":inquillager_servant"));

    public static final RegistryObject<EntityType<SunkenNecromancerServant>> SUNKEN_NECROMANCER_SERVANT = ENTITY_TYPES.register(
            "sunken_necromancer_servant",
            () -> EntityType.Builder.of(SunkenNecromancerServant::new, MobCategory.MONSTER)
                    .sized(0.75F, 2.4875F)
                    .clientTrackingRange(8)
                    .build(MOD_ID + ":sunken_necromancer_servant"));

    public static final RegistryObject<EntityType<SunkenNecromancer>> SUNKEN_NECROMANCER = ENTITY_TYPES.register(
            "sunken_necromancer",
            () -> EntityType.Builder.of(SunkenNecromancer::new, MobCategory.MONSTER)
                    .sized(0.75F, 2.4875F)
                    .clientTrackingRange(8)
                    .build(MOD_ID + ":sunken_necromancer"));

    public static final RegistryObject<EntityType<AxolotlServant>> AXOLOTL_SERVANT = ENTITY_TYPES.register(
            "axolotl_servant",
            () -> EntityType.Builder.of(AxolotlServant::new, MobCategory.UNDERGROUND_WATER_CREATURE)
                    .sized(0.75F, 0.42F)
                    .clientTrackingRange(8)
                    .build(MOD_ID + ":axolotl_servant"));

    public static final RegistryObject<EntityType<HeresiarchServant>> HERESIARCH_SERVANT = ENTITY_TYPES.register(
            "heresiarch_servant",
            () -> EntityType.Builder.of(HeresiarchServant::new, MobCategory.MONSTER)
                    .sized(0.75F, 2.4375F)
                    .clientTrackingRange(8)
                    .build(MOD_ID + ":heresiarch_servant"));

    public static final RegistryObject<EntityType<Acolyte>> ACOLYTE = ENTITY_TYPES.register(
            "acolyte",
            () -> EntityType.Builder.of(Acolyte::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8)
                    .build(MOD_ID + ":acolyte"));

    public static final RegistryObject<EntityType<AcolyteServant>> ACOLYTE_SERVANT = ENTITY_TYPES.register(
            "acolyte_servant",
            () -> EntityType.Builder.of(AcolyteServant::new, MobCategory.MISC)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8)
                    .build(MOD_ID + ":acolyte_servant"));



    /**
     * 注册实体类型到模组事件总线
     * 必须在模组构造函数中调用此方法以完成注册
     *
     * @param modEventBus 模组事件总线对象
     */
    public static void register(IEventBus modEventBus) {
        ENTITY_TYPES.register(modEventBus);
    }
}
