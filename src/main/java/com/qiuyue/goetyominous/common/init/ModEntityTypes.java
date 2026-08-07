package com.qiuyue.goetyominous.common.init;

import com.qiuyue.goetyominous.common.entities.ally.illager.*;
import com.qiuyue.goetyominous.common.entities.ally.mobs.*;

import com.qiuyue.goetyominous.common.entities.ally.mobs.StormNecromancerServant;
import com.qiuyue.goetyominous.common.entities.ally.spider.CrimsonSpiderServant;
import com.qiuyue.goetyominous.common.entities.hostile.*;
import com.qiuyue.goetyominous.common.entities.hostile.cultists.*;
import com.qiuyue.goetyominous.common.entities.hostile.illagers.ArchGeomancerEntity;
import com.qiuyue.goetyominous.common.entities.projectile.AcidFungus;
import com.qiuyue.goetyominous.common.entities.projectile.BurningPotionEntity;
import com.qiuyue.goetyominous.common.entities.projectile.ImpactBlockEntity;
import com.qiuyue.goetyominous.common.entities.projectile.TremorBlockEntity;
import com.qiuyue.goetyominous.common.entities.projectile.PitchforkEntity;
import com.qiuyue.goetyominous.common.entities.projectile.WitchBombEntity;
import com.qiuyue.goetyominous.common.entities.util.BurningGroundEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import static com.qiuyue.goetyominous.GoetyOminous.MOD_ID;

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

    public static final RegistryObject<EntityType<PiglinServant>> PIGLIN_SERVANT = ENTITY_TYPES.register(
            "piglin_servant",
            () -> EntityType.Builder.of(PiglinServant::new, MobCategory.MISC)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8)
                    .build(MOD_ID + ":piglin_servant"));

    public static final RegistryObject<EntityType<PiglinBruteServant>> PIGLIN_BRUTE_SERVANT = ENTITY_TYPES.register(
            "piglin_brute_servant",
            () -> EntityType.Builder.of(PiglinBruteServant::new, MobCategory.MISC)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8)
                    .build(MOD_ID + ":piglin_brute_servant"));

    public static final RegistryObject<EntityType<StrongPiglinBruteServant>> STRONG_PIGLIN_BRUTE_SERVANT = ENTITY_TYPES.register(
            "piglin_brute_servant_strong",
            () -> EntityType.Builder.of(StrongPiglinBruteServant::new, MobCategory.MISC)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8)
                    .build(MOD_ID + ":piglin_brute_servant_strong"));

    public static final RegistryObject<EntityType<ElitePiglinBruteServant>> ELITE_PIGLIN_BRUTE_SERVANT = ENTITY_TYPES.register(
            "piglin_brute_servant_elite",
            () -> EntityType.Builder.of(ElitePiglinBruteServant::new, MobCategory.MISC)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8)
                    .build(MOD_ID + ":piglin_brute_servant_elite"));

    public static final RegistryObject<EntityType<StrongZPiglinBruteServant>> STRONG_ZPIGLIN_BRUTE_SERVANT = ENTITY_TYPES.register(
            "strong_zpiglin_brute_servant",
            () -> EntityType.Builder.of(StrongZPiglinBruteServant::new, MobCategory.MONSTER)
                    .fireImmune()
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8)
                    .build(MOD_ID + ":strong_zpiglin_brute_servant"));

    public static final RegistryObject<EntityType<EliteZPiglinBruteServant>> ELITE_ZPIGLIN_BRUTE_SERVANT = ENTITY_TYPES.register(
            "elite_zpiglin_brute_servant",
            () -> EntityType.Builder.of(EliteZPiglinBruteServant::new, MobCategory.MONSTER)
                    .fireImmune()
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8)
                    .build(MOD_ID + ":elite_zpiglin_brute_servant"));

    public static final RegistryObject<EntityType<PiglinHunterServant>> PIGLIN_HUNTER_SERVANT = ENTITY_TYPES.register(
            "piglin_hunter_servant",
            () -> EntityType.Builder.of(PiglinHunterServant::new, MobCategory.MISC)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8)
                    .build(MOD_ID + ":piglin_hunter_servant"));

    public static final RegistryObject<EntityType<StrongPiglinHunterServant>> STRONG_PIGLIN_HUNTER_SERVANT = ENTITY_TYPES.register(
            "strong_piglin_hunter_servant",
            () -> EntityType.Builder.of(StrongPiglinHunterServant::new, MobCategory.MISC)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8)
                    .build(MOD_ID + ":strong_piglin_hunter_servant"));

    public static final RegistryObject<EntityType<ElitePiglinHunterServant>> ELITE_PIGLIN_HUNTER_SERVANT = ENTITY_TYPES.register(
            "elite_piglin_hunter_servant",
            () -> EntityType.Builder.of(ElitePiglinHunterServant::new, MobCategory.MISC)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8)
                    .build(MOD_ID + ":elite_piglin_hunter_servant"));

    public static final RegistryObject<EntityType<ZPiglinHunterServant>> ZPIGLIN_HUNTER_SERVANT = ENTITY_TYPES.register(
            "zpiglin_hunter_servant",
            () -> EntityType.Builder.of(ZPiglinHunterServant::new, MobCategory.MONSTER)
                    .fireImmune()
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8)
                    .build(MOD_ID + ":zpiglin_hunter_servant"));

    public static final RegistryObject<EntityType<StrongZPiglinHunterServant>> STRONG_ZPIGLIN_HUNTER_SERVANT = ENTITY_TYPES.register(
            "strong_zpiglin_hunter_servant",
            () -> EntityType.Builder.of(StrongZPiglinHunterServant::new, MobCategory.MONSTER)
                    .fireImmune()
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8)
                    .build(MOD_ID + ":strong_zpiglin_hunter_servant"));

    public static final RegistryObject<EntityType<EliteZPiglinHunterServant>> ELITE_ZPIGLIN_HUNTER_SERVANT = ENTITY_TYPES.register(
            "elite_zpiglin_hunter_servant",
            () -> EntityType.Builder.of(EliteZPiglinHunterServant::new, MobCategory.MONSTER)
                    .fireImmune()
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8)
                    .build(MOD_ID + ":elite_zpiglin_hunter_servant"));

    public static final RegistryObject<EntityType<PiglinMerchant>> PIGLIN_MERCHANT = ENTITY_TYPES.register("piglin_merchant",
            () -> EntityType.Builder.of(PiglinMerchant::new, MobCategory.CREATURE)
                    .sized(1.0F, 2.4375F)
                    .clientTrackingRange(10)
                    .build(MOD_ID + ":piglin_merchant"));

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

    public static final RegistryObject<EntityType<StormNecromancerServant>> STORM_NECROMANCER_SERVANT = ENTITY_TYPES.register(
            "storm_necromancer_servant",
            () -> EntityType.Builder.of(StormNecromancerServant::new, MobCategory.MONSTER)
                    .sized(0.75F, 2.4875F)
                    .clientTrackingRange(8)
                    .build(MOD_ID + ":storm_necromancer_servant"));

    public static final RegistryObject<EntityType<StormNecromancer>> STORM_NECROMANCER = ENTITY_TYPES.register(
            "storm_necromancer",
            () -> EntityType.Builder.of(StormNecromancer::new, MobCategory.MONSTER)
                    .sized(0.75F, 2.4875F)
                    .clientTrackingRange(8)
                    .build(MOD_ID + ":storm_necromancer"));

    public static final RegistryObject<EntityType<Disciple>> DISCIPLE = ENTITY_TYPES.register(
            "disciple",
            () -> EntityType.Builder.of(Disciple::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8)
                    .build(MOD_ID + ":disciple"));

    public static final RegistryObject<EntityType<DiscipleServant>> DISCIPLE_SERVANT = ENTITY_TYPES.register(
            "disciple_servant",
            () -> EntityType.Builder.of(DiscipleServant::new, MobCategory.MISC)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8)
                    .build(MOD_ID + ":disciple_servant"));

    public static final RegistryObject<EntityType<FungusThrower>> FUNGUS_THROWER = ENTITY_TYPES.register(
            "fungus_thrower",
            () -> EntityType.Builder.of(FungusThrower::new, MobCategory.MISC)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8)
                    .build(MOD_ID + ":fungus_thrower"));

    public static final RegistryObject<EntityType<ZFungusThrower>> ZFUNGUS_THROWER = ENTITY_TYPES.register(
            "zfungus_thrower",
            () -> EntityType.Builder.of(ZFungusThrower::new, MobCategory.MISC)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8)
                    .build(MOD_ID + ":zfungus_thrower"));

    public static final RegistryObject<EntityType<AcidFungus>> ACID_FUNGUS = ENTITY_TYPES.register(
            "acid_fungus",
            () -> EntityType.Builder.<AcidFungus>of(AcidFungus::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .build(MOD_ID + ":acid_fungus"));

    public static final RegistryObject<EntityType<Fanatic>> FANATIC = ENTITY_TYPES.register("fanatic",
            () -> EntityType.Builder.of(Fanatic::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8)
                    .build(MOD_ID + ":fanatic"));

    public static final RegistryObject<EntityType<Zealot>> ZEALOT = ENTITY_TYPES.register("zealot",
            () -> EntityType.Builder.of(Zealot::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8)
                    .build(MOD_ID + ":zealot"));

    public static final RegistryObject<EntityType<Beldam>> BELDAM = ENTITY_TYPES.register("beldam",
            () -> EntityType.Builder.of(Beldam::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8)
                    .build(MOD_ID + ":beldam"));

    public static final RegistryObject<EntityType<UrbhadhachEntity>> URBHADHACH = ENTITY_TYPES.register("urbhadhach",
            () -> EntityType.Builder.of(UrbhadhachEntity::new, MobCategory.MONSTER)
                    .sized(1.7F, 1.7F)
                    .clientTrackingRange(10)
                    .build(MOD_ID + ":urbhadhach"));

    public static final RegistryObject<EntityType<UrbhadhachServant>> URBHADHACH_SERVANT = ENTITY_TYPES.register("urbhadhach_servant",
            () -> EntityType.Builder.of(UrbhadhachServant::new, MobCategory.MISC)
                    .sized(1.7F, 1.7F)
                    .clientTrackingRange(10)
                    .build(MOD_ID + ":urbhadhach_servant"));

    // No longer used:Martyr
    // public static final RegistryObject<EntityType<Martyr>> MARTYR = ENTITY_TYPES.register("martyr",
    //         () -> EntityType.Builder.of(Martyr::new, MobCategory.MONSTER)
    //                 .sized(0.6F, 1.95F)
    //                 .clientTrackingRange(8)
    //                 .build(MOD_ID + ":martyr"));

    public static final RegistryObject<EntityType<Thug>> THUG = ENTITY_TYPES.register("thug",
            () -> EntityType.Builder.of(Thug::new, MobCategory.MONSTER)
                    .sized(1.4F, 2.7F)
                    .clientTrackingRange(10)
                    .build(MOD_ID + ":thug"));

    public static final RegistryObject<EntityType<Channeller>> CHANNELLER = ENTITY_TYPES.register("channeller",
            () -> EntityType.Builder.of(Channeller::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8)
                    .build(MOD_ID + ":channeller"));

    public static final RegistryObject<EntityType<Scorch>> SCORCH = ENTITY_TYPES.register(
            "scorch",
            () -> EntityType.Builder.of(Scorch::new, MobCategory.MONSTER)
                    .sized(0.4F, 0.8F)
                    .fireImmune()
                    .clientTrackingRange(8)
                    .build(MOD_ID + ":scorch"));

    public static final RegistryObject<EntityType<CrimsonSpiderServant>> CRIMSON_SPIDER_SERVANT = ENTITY_TYPES.register("crimson_spider_servant",
            () -> EntityType.Builder.of(CrimsonSpiderServant::new, MobCategory.MISC)
                    .sized(1.4F, 0.9F)
                    .clientTrackingRange(8)
                    .fireImmune()
                    .build(MOD_ID + ":crimson_spider_servant"));

    public static final RegistryObject<EntityType<Returned>> RETURNED = ENTITY_TYPES.register(
            "returned",
            () -> EntityType.Builder.of(Returned::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .fireImmune()
                    .clientTrackingRange(8)
                    .build(MOD_ID + ":returned"));

    public static final RegistryObject<EntityType<Agony>> AGONY = ENTITY_TYPES.register(
            "agony",
            () -> EntityType.Builder.of(Agony::new, MobCategory.MISC)
                    .sized(0.6F, 1.95F)
                    .fireImmune()
                    .clientTrackingRange(8)
                    .build(MOD_ID + ":agony"));

    public static final RegistryObject<EntityType<BurningPotionEntity>> BURNING_POTION = ENTITY_TYPES.register("burning_potion",
            () -> EntityType.Builder.<BurningPotionEntity>of(BurningPotionEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build(MOD_ID + ":burning_potion"));

    public static final RegistryObject<EntityType<BurningGroundEntity>> BURNING_GROUND = ENTITY_TYPES.register("burning_ground",
            () -> EntityType.Builder.<BurningGroundEntity>of(BurningGroundEntity::new, MobCategory.MISC)
                    .sized(0.0F, 0.0F)
                    .clientTrackingRange(10)
                    .updateInterval(Integer.MAX_VALUE)
                    .build(MOD_ID + ":burning_ground"));

    public static final RegistryObject<EntityType<WitchBombEntity>> WITCH_BOMB = ENTITY_TYPES.register("witch_bomb",
            () -> EntityType.Builder.<WitchBombEntity>of(WitchBombEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build(MOD_ID + ":witch_bomb"));

    public static final RegistryObject<EntityType<PitchforkEntity>> PITCHFORK = ENTITY_TYPES.register("pitchfork",
            () -> EntityType.Builder.<PitchforkEntity>of(PitchforkEntity::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(4)
                    .updateInterval(20)
                    .build(MOD_ID + ":pitchfork"));

    public static final RegistryObject<EntityType<ArchGeomancerEntity>> ARCH_GEOMANCER = ENTITY_TYPES.register(
            "arch_geomancer",
            () -> EntityType.Builder.of(ArchGeomancerEntity::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8)
                    .build(MOD_ID + ":arch_geomancer"));

    public static final RegistryObject<EntityType<ImpactBlockEntity>> IMPACT_BLOCK = ENTITY_TYPES.register(
            "impact_block",
            () -> EntityType.Builder.<ImpactBlockEntity>of(ImpactBlockEntity::new, MobCategory.MISC)
                    .sized(0.98F, 0.98F)
                    .clientTrackingRange(10)
                    .updateInterval(1)
                    .build(MOD_ID + ":impact_block"));

    public static final RegistryObject<EntityType<TremorBlockEntity>> TREMOR_BLOCK = ENTITY_TYPES.register(
            "tremor_block",
            () -> EntityType.Builder.<TremorBlockEntity>of(TremorBlockEntity::new, MobCategory.MISC)
                    .sized(0.98F, 0.98F)
                    .clientTrackingRange(10)
                    .updateInterval(1)
                    .build(MOD_ID + ":tremor_block"));

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
