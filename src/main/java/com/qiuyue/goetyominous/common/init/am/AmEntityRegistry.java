package com.qiuyue.goetyominous.common.init.am;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.entities.ally.am.CrimsonMosquitoServant;
import com.qiuyue.goetyominous.common.entities.ally.am.MurmurServant;
import com.qiuyue.goetyominous.common.entities.ally.am.MurmurServantHead;
import com.qiuyue.goetyominous.common.entities.projectile.EntityMosquitoServantSpit;
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
