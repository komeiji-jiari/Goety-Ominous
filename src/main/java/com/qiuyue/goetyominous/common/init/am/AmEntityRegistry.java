package com.qiuyue.goetyominous.common.init.am;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.entities.ally.am.BunfungusServant;
import com.qiuyue.goetyominous.common.entities.ally.am.CrimsonMosquitoServant;
import com.qiuyue.goetyominous.common.entities.ally.am.IllagerElephantServant;
import com.qiuyue.goetyominous.common.entities.ally.am.ZombieCrocodileServant;
import com.qiuyue.goetyominous.common.entities.ally.am.FarseerServant;
import com.qiuyue.goetyominous.common.entities.ally.am.FroststalkerServant;
import com.qiuyue.goetyominous.common.entities.ally.am.MurmurServant;
import com.qiuyue.goetyominous.common.entities.ally.am.SkelewagServant;
import com.qiuyue.goetyominous.common.entities.ally.am.StraySkelewagServant;
import com.qiuyue.goetyominous.common.entities.ally.am.WitherSkelewagServant;
import com.qiuyue.goetyominous.common.entities.ally.am.MurmurServantHead;
import com.qiuyue.goetyominous.common.entities.ally.am.RockyRollerServant;
import com.qiuyue.goetyominous.common.entities.ally.am.TusklinServant;
import com.qiuyue.goetyominous.common.entities.ally.am.WarpedMoscoServant;
import com.qiuyue.goetyominous.common.entities.projectile.EntityMosquitoServantSpit;
import com.qiuyue.goetyominous.common.entities.projectile.EntityServantHemolymph;
import java.util.function.Predicate;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.block.Blocks;
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
                            .sized(1.3F, 1.8F)
                            .setTrackingRange(8)
                            .build(GoetyOminous.MOD_ID + ":tusklin_servant"));

    public static final RegistryObject<EntityType<FroststalkerServant>> FROSTSTALKER_SERVANT =
            AM_ENTITIES.register("froststalker_servant",
                    () -> EntityType.Builder.<FroststalkerServant>of((type, worldIn) -> new FroststalkerServant(type, worldIn), MobCategory.MISC)
                            .sized(0.95F, 1.15F)
                            .immuneTo(Blocks.POWDER_SNOW)
                            .setTrackingRange(8)
                            .build(GoetyOminous.MOD_ID + ":froststalker_servant"));

    public static final RegistryObject<EntityType<RockyRollerServant>> ROCKY_ROLLER_SERVANT =
            AM_ENTITIES.register("rocky_roller_servant",
                    () -> EntityType.Builder.<RockyRollerServant>of((type, worldIn) -> new RockyRollerServant(type, worldIn), MobCategory.MISC)
                            .sized(1.0F, 1.0F)
                            .setTrackingRange(8)
                            .build(GoetyOminous.MOD_ID + ":rocky_roller_servant"));

    public static final RegistryObject<EntityType<ZombieCrocodileServant>> ZOMBIE_CROCODILE_SERVANT =
            AM_ENTITIES.register("zombie_crocodile_servant",
                    () -> EntityType.Builder.<ZombieCrocodileServant>of((type, worldIn) -> new ZombieCrocodileServant(type, worldIn), MobCategory.MISC)
                            .sized(2.15F, 0.75F)
                            .setTrackingRange(10)
                            .build(GoetyOminous.MOD_ID + ":zombie_crocodile_servant"));

    public static final RegistryObject<EntityType<BunfungusServant>> BUNFUNGUS_SERVANT =
            AM_ENTITIES.register("bunfungus_servant",
                    () -> EntityType.Builder.<BunfungusServant>of((type, worldIn) -> new BunfungusServant(type, worldIn), MobCategory.MISC)
                            .sized(1.85F, 2.1F)
                            .setTrackingRange(10)
                            .build(GoetyOminous.MOD_ID + ":bunfungus_servant"));

    public static final RegistryObject<EntityType<IllagerElephantServant>> ILLAGER_ELEPHANT_SERVANT =
            AM_ENTITIES.register("illager_elephant_servant",
                    () -> EntityType.Builder.<IllagerElephantServant>of((type, worldIn) -> new IllagerElephantServant(type, worldIn), MobCategory.MISC)
                            .sized(3.7F, 3.75F)
                            .setTrackingRange(10)
                            .build(GoetyOminous.MOD_ID + ":illager_elephant_servant"));

    public static final RegistryObject<EntityType<SkelewagServant>> SKELEWAG_SERVANT =
            AM_ENTITIES.register("skelewag_servant",
                    () -> EntityType.Builder.<SkelewagServant>of((type, worldIn) -> new SkelewagServant(type, worldIn), MobCategory.MISC)
                            .sized(2.0F, 1.2F)
                            .setShouldReceiveVelocityUpdates(true)
                            .setUpdateInterval(1)
                            .setTrackingRange(8)
                            .build(GoetyOminous.MOD_ID + ":skelewag_servant"));

    public static final RegistryObject<EntityType<WitherSkelewagServant>> WITHER_SKELEWAG_SERVANT =
            AM_ENTITIES.register("wither_skelewag_servant",
                    () -> EntityType.Builder.<WitherSkelewagServant>of((type, worldIn) -> new WitherSkelewagServant(type, worldIn), MobCategory.MISC)
                            .sized(2.0F, 1.2F)
                            .fireImmune()
                            .setShouldReceiveVelocityUpdates(true)
                            .setUpdateInterval(1)
                            .setTrackingRange(8)
                            .build(GoetyOminous.MOD_ID + ":wither_skelewag_servant"));

    public static final RegistryObject<EntityType<StraySkelewagServant>> STRAY_SKELEWAG_SERVANT =
            AM_ENTITIES.register("stray_skelewag_servant",
                    () -> EntityType.Builder.<StraySkelewagServant>of((type, worldIn) -> new StraySkelewagServant(type, worldIn), MobCategory.MISC)
                            .sized(2.0F, 1.2F)
                            .setShouldReceiveVelocityUpdates(true)
                            .setUpdateInterval(1)
                            .setTrackingRange(8)
                            .build(GoetyOminous.MOD_ID + ":stray_skelewag_servant"));

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
