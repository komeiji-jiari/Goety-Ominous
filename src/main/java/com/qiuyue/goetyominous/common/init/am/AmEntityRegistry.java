package com.qiuyue.goetyominous.common.init.am;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.entities.ally.am.CrimsonMosquitoServant;
import com.qiuyue.goetyominous.common.entities.ally.am.FarseerServant;
import com.qiuyue.goetyominous.common.entities.ally.am.MurmurServant;
import com.qiuyue.goetyominous.common.entities.ally.am.MurmurServantHead;
import com.qiuyue.goetyominous.common.entities.ally.am.WarpedMoscoServant;
import com.qiuyue.goetyominous.common.entities.projectile.EntityMosquitoServantSpit;
import com.qiuyue.goetyominous.common.entities.projectile.EntityServantHemolymph;
import java.util.function.Predicate;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * AlexMobs 联动实体注册类，在这里添加 AlexMobs 仆从的实体类型注册
 */
public class AmEntityRegistry {

    private static final DeferredRegister<EntityType<?>> AM_ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, GoetyOminous.MOD_ID);

    public static final RegistryObject<EntityType<MurmurServant>> MURMUR_SERVANT =
            AM_ENTITIES.register("murmur_servant",
                    () -> EntityType.Builder.of(MurmurServant::new, MobCategory.MISC)
                            .sized(0.7F, 1.45F)
                            .setTrackingRange(8)
                            .build(GoetyOminous.MOD_ID + ":murmur_servant"));

    public static final RegistryObject<EntityType<MurmurServantHead>> MURMUR_SERVANT_HEAD =
            AM_ENTITIES.register("murmur_servant_head",
                    () -> EntityType.Builder.of(MurmurServantHead::new, MobCategory.MISC)
                            .sized(0.55F, 0.55F)
                            .setTrackingRange(8)
                            .build(GoetyOminous.MOD_ID + ":murmur_servant_head"));

    public static final RegistryObject<EntityType<CrimsonMosquitoServant>> CRIMSON_MOSQUITO_SERVANT =
            AM_ENTITIES.register("crimson_mosquito_servant",
                    () -> EntityType.Builder.<CrimsonMosquitoServant>of((type, worldIn) -> new CrimsonMosquitoServant(type, worldIn), MobCategory.MISC)
                            .sized(1.25F, 1.15F)
                            .setTrackingRange(8)
                            .build(GoetyOminous.MOD_ID + ":crimson_mosquito_servant"));

    public static final RegistryObject<EntityType<EntityMosquitoServantSpit>> MOSQUITO_SERVANT_SPIT =
            AM_ENTITIES.register("mosquito_servant_spit",
                    () -> EntityType.Builder.<EntityMosquitoServantSpit>of((type, worldIn) -> new EntityMosquitoServantSpit(type, worldIn), MobCategory.MISC)
                            .sized(0.25F, 0.25F)
                            .setTrackingRange(8)
                            .build(GoetyOminous.MOD_ID + ":mosquito_servant_spit"));

    /**
     * 疣猪蚊仆从，由患病（变蓝）的 CrimsonMosquitoServant 转化而来。
     * 体型/追踪范围对齐原版 AlexMobs 的 warped_mosco（1.99 x 3.25，tracking 10）。
     */
    public static final RegistryObject<EntityType<WarpedMoscoServant>> WARPED_MOSCO_SERVANT =
            AM_ENTITIES.register("warped_mosco_servant",
                    () -> EntityType.Builder.<WarpedMoscoServant>of((type, worldIn) -> new WarpedMoscoServant(type, worldIn), MobCategory.MISC)
                            .sized(1.99F, 3.25F)
                            .fireImmune()
                            .setTrackingRange(10)
                            .build(GoetyOminous.MOD_ID + ":warped_mosco_servant"));

    /**
     * 蚊仆从喷射的血红液投射物（EntityServantHemolymph）。
     * 几何/类别对齐 AlexMobs 原版 HEMOLYMPH（MobCategory.MISC、0.5 x 0.5、fireImmune）。
     * 注册本模组自己的实体类型，避免借用 AlexMobs 的 HEMOLYMPH——其客户端工厂会生成
     * AlexMobs 的 EntityHemolymph，与服务端的 EntityServantHemolymph 类不一致，导致
     * 位置/粒子/命中存在客户端-服务端不一致风险。
     */
    public static final RegistryObject<EntityType<EntityServantHemolymph>> SERVANT_HEMOLYMPH =
            AM_ENTITIES.register("servant_hemolymph",
                    () -> EntityType.Builder.<EntityServantHemolymph>of((type, worldIn) -> new EntityServantHemolymph(type, worldIn), MobCategory.MISC)
                            .sized(0.5F, 0.5F)
                            .fireImmune()
                            .setTrackingRange(8)
                            .build(GoetyOminous.MOD_ID + ":servant_hemolymph"));

    /**
     * 先知仆从，由 AlexMobs 的 farseer 转化而来。
     * 体型/追踪范围对齐原版 AlexMobs 的 farseer（0.99 x 1.5，tracking 8，fireImmune，
     * setUpdateInterval(1) 保证延迟偏移/残影渲染的位置历史精度）。
     */
    public static final RegistryObject<EntityType<FarseerServant>> FARSEER_SERVANT =
            AM_ENTITIES.register("farseer_servant",
                    () -> EntityType.Builder.<FarseerServant>of((type, worldIn) -> new FarseerServant(type, worldIn), MobCategory.MISC)
                            .sized(0.99F, 1.5F)
                            .fireImmune()
                            .setShouldReceiveVelocityUpdates(true)
                            .setUpdateInterval(1)
                            .setTrackingRange(8)
                            .build(GoetyOminous.MOD_ID + ":farseer_servant"));

    public static Predicate<LivingEntity> buildPredicateFromTag(TagKey<EntityType<?>> tagKey) {
        if (tagKey == null) {
            return (mob) -> false;
        }
        return (mob) -> mob.isAlive() && mob.getType().is(tagKey);
    }

    // === 在这里添加实体注册 ===
    // 示例：
    // public static final RegistryObject<EntityType<XXXServant>> XXX_SERVANT =
    //         AM_ENTITIES.register("xxx_servant",
    //                 () -> EntityType.Builder.of(XXXServant::new, MobCategory.MISC)
    //                         .sized(0.6F, 1.95F)
    //                         .clientTrackingRange(8)
    //                         .build(GoetyOminous.MOD_ID + ":xxx_servant"));

    public static void register(IEventBus modEventBus) {
        AM_ENTITIES.register(modEventBus);
    }
}
