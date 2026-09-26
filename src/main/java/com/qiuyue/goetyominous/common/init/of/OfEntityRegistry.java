package com.qiuyue.goetyominous.common.init.of;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.entities.ally.of.DicerServant;
import com.qiuyue.goetyominous.common.entities.ally.of.FireSlimeServant;
import com.qiuyue.goetyominous.common.entities.ally.of.GuzzlerServant;
import com.qiuyue.goetyominous.common.entities.ally.of.RamblerServant;
import com.qiuyue.goetyominous.common.entities.ally.of.SkyvernSegmentServant;
import com.qiuyue.goetyominous.common.entities.ally.of.SkyvernServant;
import com.qiuyue.goetyominous.common.entities.ally.of.TerrorServant;
import com.qiuyue.goetyominous.common.entities.ally.of.TremblerServant;
import com.qiuyue.goetyominous.common.entities.ally.of.UmberSpiderServant;
import com.qiuyue.goetyominous.common.entities.ally.of.VoltServant;
import com.qiuyue.goetyominous.common.entities.projectile.DicerServantLaser;
import com.qiuyue.goetyominous.common.entities.projectile.VoltServantElectricCharge;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class OfEntityRegistry {

    private static final DeferredRegister<EntityType<?>> OF_ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, GoetyOminous.MOD_ID);

    public static final RegistryObject<EntityType<RamblerServant>> RAMBLER_SERVANT =
            OF_ENTITIES.register("rambler_servant",
                    () -> EntityType.Builder.<RamblerServant>of(RamblerServant::new, MobCategory.MISC)
                            .sized(1.98F, 2.25F)
                            .setTrackingRange(10)
                            .build(GoetyOminous.MOD_ID + ":rambler_servant"));

    public static final RegistryObject<EntityType<DicerServant>> DICER_SERVANT =
            OF_ENTITIES.register("dicer_servant",
                    () -> EntityType.Builder.<DicerServant>of(DicerServant::new, MobCategory.MISC)
                            .sized(0.7F, 2.8F)
                            .setTrackingRange(10)
                            .build(GoetyOminous.MOD_ID + ":dicer_servant"));
    public static final RegistryObject<EntityType<DicerServantLaser>> DICER_SERVANT_LASER =
            OF_ENTITIES.register("dicer_servant_laser",
                    () -> EntityType.Builder.<DicerServantLaser>of(DicerServantLaser::new, MobCategory.MISC)
                            .sized(0.1F, 0.1F)
                            .fireImmune()
                            .setUpdateInterval(1)
                            .build(GoetyOminous.MOD_ID + ":dicer_servant_laser"));

    public static final RegistryObject<EntityType<VoltServant>> VOLT_SERVANT =
            OF_ENTITIES.register("volt_servant",
                    () -> EntityType.Builder.<VoltServant>of(VoltServant::new, MobCategory.MISC)
                            .sized(1.1F, 1.8F)
                            .setTrackingRange(10)
                            .build(GoetyOminous.MOD_ID + ":volt_servant"));

    public static final RegistryObject<EntityType<VoltServantElectricCharge>> VOLT_SERVANT_ELECTRIC_CHARGE =
            OF_ENTITIES.register("volt_servant_electric_charge",
                    () -> EntityType.Builder.<VoltServantElectricCharge>of(VoltServantElectricCharge::new, MobCategory.MISC)
                            .sized(0.75F, 0.75F)
                            .fireImmune()
                            .setTrackingRange(4)
                            .setShouldReceiveVelocityUpdates(true)
                            .build(GoetyOminous.MOD_ID + ":volt_servant_electric_charge"));

    public static final RegistryObject<EntityType<TremblerServant>> TREMBLER_SERVANT =
            OF_ENTITIES.register("trembler_servant",
                    () -> EntityType.Builder.<TremblerServant>of(TremblerServant::new, MobCategory.MISC)
                            .sized(0.8F, 0.9F)
                            .setTrackingRange(10)
                            .build(GoetyOminous.MOD_ID + ":trembler_servant"));

    public static final RegistryObject<EntityType<UmberSpiderServant>> UMBER_SPIDER_SERVANT =
            OF_ENTITIES.register("umber_spider_servant",
                    () -> EntityType.Builder.<UmberSpiderServant>of(UmberSpiderServant::new, MobCategory.MISC)
                            .sized(1.4F, 0.9F)
                            .setTrackingRange(10)
                            .build(GoetyOminous.MOD_ID + ":umber_spider_servant"));

    public static final RegistryObject<EntityType<TerrorServant>> TERROR_SERVANT =
            OF_ENTITIES.register("terror_servant",
                    () -> EntityType.Builder.<TerrorServant>of(TerrorServant::new, MobCategory.MISC)
                            .sized(1.3F, 1.1F)
                            .setTrackingRange(10)
                            .build(GoetyOminous.MOD_ID + ":terror_servant"));

    public static final RegistryObject<EntityType<FireSlimeServant>> FIRE_SLIME_SERVANT =
            OF_ENTITIES.register("fire_slime_servant",
                    () -> EntityType.Builder.<FireSlimeServant>of(FireSlimeServant::new, MobCategory.MISC)
                            .sized(0.6875F, 0.6875F)
                            .fireImmune()
                            .setTrackingRange(10)
                            .build(GoetyOminous.MOD_ID + ":fire_slime_servant"));

    public static final RegistryObject<EntityType<GuzzlerServant>> GUZZLER_SERVANT =
            OF_ENTITIES.register("guzzler_servant",
                    () -> EntityType.Builder.<GuzzlerServant>of(GuzzlerServant::new, MobCategory.MISC)
                            .sized(1.98F, 2.5F)
                            .setTrackingRange(10)
                            .build(GoetyOminous.MOD_ID + ":guzzler_servant"));

    public static final RegistryObject<EntityType<SkyvernServant>> SKYVERN_SERVANT =
            OF_ENTITIES.register("skyvern_servant",
                    () -> EntityType.Builder.<SkyvernServant>of(SkyvernServant::new, MobCategory.MISC)
                            .sized(1.2F, 1.2F)
                            .setTrackingRange(10)
                            .build(GoetyOminous.MOD_ID + ":skyvern_servant"));

    public static final RegistryObject<EntityType<SkyvernSegmentServant>> SKYVERN_SEGMENT_SERVANT =
            OF_ENTITIES.register("skyvern_segment_servant",
                    () -> EntityType.Builder.<SkyvernSegmentServant>of(SkyvernSegmentServant::new, MobCategory.MISC)
                            .sized(1.2F, 1.2F)
                            .setTrackingRange(10)
                            .build(GoetyOminous.MOD_ID + ":skyvern_segment_servant"));

    public static void register(IEventBus modEventBus) {
        OF_ENTITIES.register(modEventBus);
    }
}
