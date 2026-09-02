package com.qiuyue.goetyominous.common.init.mm;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * MutantMore 联动实体注册类
 * 负责注册所有 MutantMore 仆从实体类型
 * 注意：这个类只在 MutantMore 模组加载时才会被调用
 */
public class MmEntityRegistry {

    /**
     * MutantMore 实体延迟注册表
     */
    private static final DeferredRegister<EntityType<?>> MM_ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, GoetyOminous.MOD_ID);

    public static final RegistryObject<EntityType<MutantWitherSkeletonServant>> MUTANT_WITHER_SKELETON_SERVANT =
            MM_ENTITIES.register("mutant_wither_skeleton_servant",
                    () -> EntityType.Builder.of(MutantWitherSkeletonServant::new, MobCategory.MISC)
                            .sized(1.25F, 3.9F).fireImmune()
                            .clientTrackingRange(10)
                            .build(GoetyOminous.MOD_ID + ":mutant_wither_skeleton_servant"));

    public static final RegistryObject<EntityType<AreaDamage>> AREA_DAMAGE =
            MM_ENTITIES.register("area_damage",
                    () -> EntityType.Builder.of(AreaDamage::new, MobCategory.MISC)
                            .sized(1.0F, 1.0F).fireImmune()
                            .clientTrackingRange(10)
                            .build(GoetyOminous.MOD_ID + ":area_damage"));

    public static final RegistryObject<EntityType<WitherSlash>> WITHER_SLASH =
            MM_ENTITIES.register("wither_slash",
                    () -> EntityType.Builder.<WitherSlash>of(WitherSlash::new, MobCategory.MISC)
                            .sized(1.25F, 0.25F).fireImmune()
                            .clientTrackingRange(10)
                            .build(GoetyOminous.MOD_ID + ":wither_slash"));

    public static final RegistryObject<EntityType<MutantHoglinServant>> MUTANT_HOGLIN_SERVANT =
            MM_ENTITIES.register("mutant_hoglin_servant",
                    () -> EntityType.Builder.of(MutantHoglinServant::new, MobCategory.MISC)
                            .sized(2.9F, 3.0F).fireImmune()
                            .clientTrackingRange(10)
                            .build(GoetyOminous.MOD_ID + ":mutant_hoglin_servant"));

    public static final RegistryObject<EntityType<MutantShulkerServant>> MUTANT_SHULKER_SERVANT =
            MM_ENTITIES.register("mutant_shulker_servant",
                    () -> EntityType.Builder.of(MutantShulkerServant::new, MobCategory.MISC)
                            .sized(2.75F, 2.75F).fireImmune()
                            .clientTrackingRange(10)
                            .build(GoetyOminous.MOD_ID + ":mutant_shulker_servant"));

    public static final RegistryObject<EntityType<MutantShulkerServantBullet>> MUTANT_SHULKER_SERVANT_BULLET =
            MM_ENTITIES.register("mutant_shulker_servant_bullet",
                    () -> EntityType.Builder.of(MutantShulkerServantBullet::new, MobCategory.MISC)
                            .sized(0.85F, 0.85F)
                            .build(GoetyOminous.MOD_ID + ":mutant_shulker_servant_bullet"));

    public static final RegistryObject<EntityType<MutantShulkerServantTrap>> MUTANT_SHULKER_SERVANT_TRAP =
            MM_ENTITIES.register("mutant_shulker_servant_trap",
                    () -> EntityType.Builder.of(MutantShulkerServantTrap::new, MobCategory.MISC)
                            .sized(0.95F, 0.5F).fireImmune()
                            .clientTrackingRange(8)
                            .build(GoetyOminous.MOD_ID + ":mutant_shulker_servant_trap"));

    /**
     * 注册 MutantMore 实体到模组事件总线
     * @param modEventBus 模组事件总线
     */
    public static void register(IEventBus modEventBus) {
        MM_ENTITIES.register(modEventBus);
    }
}
