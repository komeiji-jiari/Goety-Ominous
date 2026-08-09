package com.qiuyue.goetyominous.common.init.am;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.entities.ally.am.CrimsonMosquitoServant;
import com.qiuyue.goetyominous.common.entities.ally.am.FarseerServant;
import com.qiuyue.goetyominous.common.entities.ally.am.MurmurServant;
import com.qiuyue.goetyominous.common.entities.ally.am.MurmurServantHead;
import com.qiuyue.goetyominous.common.entities.ally.am.TusklinServant;
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

    public static final RegistryObject<EntityType<WarpedMoscoServant>> WARPED_MOSCO_SERVANT =
            AM_ENTITIES.register("warped_mosco_servant",
                    () -> EntityType.Builder.<WarpedMoscoServant>of((type, worldIn) -> new WarpedMoscoServant(type, worldIn), MobCategory.MISC)
                            .sized(1.99F, 3.25F)
                            .fireImmune()
                            .setTrackingRange(10)
                            .build(GoetyOminous.MOD_ID + ":warped_mosco_servant"));

    public static final RegistryObject<EntityType<EntityServantHemolymph>> SERVANT_HEMOLYMPH =
            AM_ENTITIES.register("servant_hemolymph",
                    () -> EntityType.Builder.<EntityServantHemolymph>of((type, worldIn) -> new EntityServantHemolymph(type, worldIn), MobCategory.MISC)
                            .sized(0.5F, 0.5F)
                            .fireImmune()
                            .setTrackingRange(8)
                            .build(GoetyOminous.MOD_ID + ":servant_hemolymph"));

    public static final RegistryObject<EntityType<FarseerServant>> FARSEER_SERVANT =
            AM_ENTITIES.register("farseer_servant",
                    () -> EntityType.Builder.<FarseerServant>of((type, worldIn) -> new FarseerServant(type, worldIn), MobCategory.MISC)
                            .sized(0.99F, 1.5F)
                            .fireImmune()
                            .setShouldReceiveVelocityUpdates(true)
                            .setUpdateInterval(1)
                            .setTrackingRange(8)
                            .build(GoetyOminous.MOD_ID + ":farseer_servant"));

    public static final RegistryObject<EntityType<TusklinServant>> TUSKLIN_SERVANT =
            AM_ENTITIES.register("tusklin_servant",
                    () -> EntityType.Builder.<TusklinServant>of((type, worldIn) -> new TusklinServant(type, worldIn), MobCategory.MISC)
                            // 碰撞箱贴合模型实际尺寸（1.3F 宽≈模型躯干横截面 1.125，1.8F 高≈盖住头 1.63+行走起伏）：
                            // 原版 2.2F 宽度对 1.125 宽的模型每侧多出约 0.54 格，看起来明显偏大。
                            // 注：模型全长约 3.56（吻部 -2.44~臀部 +1.13），碰撞箱为正方形棱柱无法同时贴合
                            // 横截面与长度，此处按原版惯例取宽≈横截面（同牛/疣猪），转身时吻部/尾部会略穿出箱体。
                            .sized(1.3F, 1.8F)
                            .setTrackingRange(8)
                            .build(GoetyOminous.MOD_ID + ":tusklin_servant"));

    public static Predicate<LivingEntity> buildPredicateFromTag(TagKey<EntityType<?>> tagKey) {
        if (tagKey == null) {
            return (mob) -> false;
        }
        return (mob) -> mob.isAlive() && mob.getType().is(tagKey);
    }

    public static void register(IEventBus modEventBus) {
        AM_ENTITIES.register(modEventBus);
    }
}
