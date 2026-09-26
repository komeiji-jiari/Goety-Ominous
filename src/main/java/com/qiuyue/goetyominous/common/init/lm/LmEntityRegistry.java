package com.qiuyue.goetyominous.common.init.lm;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.entities.ally.lm.AnnihilationPursuerServant;
import com.qiuyue.goetyominous.common.entities.ally.lm.CloudGolemServant;
import com.qiuyue.goetyominous.common.entities.ally.lm.HoveringHurricaneServant;
import com.qiuyue.goetyominous.common.entities.ally.lm.OvergrownColossusServant;
import com.qiuyue.goetyominous.common.entities.ally.lm.ShulkerMimicServant;
import com.qiuyue.goetyominous.common.entities.ally.lm.projectile.AnnihilationExplosion;
import com.qiuyue.goetyominous.common.entities.ally.lm.projectile.AnnihilationFlameStrike;
import com.qiuyue.goetyominous.common.entities.ally.lm.projectile.BigShulkerBullet;
import com.qiuyue.goetyominous.common.entities.ally.lm.projectile.CloudEntity;
import com.qiuyue.goetyominous.common.entities.ally.lm.projectile.EntityThrown;
import com.qiuyue.goetyominous.common.entities.ally.lm.projectile.GravityBigShulkerBullet;
import com.qiuyue.goetyominous.common.entities.ally.lm.projectile.ElectricityEntity;
import com.qiuyue.goetyominous.common.entities.ally.lm.projectile.EnergyBeamEntity;
import com.qiuyue.goetyominous.common.entities.ally.lm.projectile.LightningBoltEntity;
import com.qiuyue.goetyominous.common.entities.ally.lm.projectile.PoisonousShockwave;
import com.qiuyue.goetyominous.common.entities.ally.lm.projectile.SmallAnnihilationBomb;
import com.qiuyue.goetyominous.common.entities.ally.lm.projectile.Tornado;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class LmEntityRegistry {

    private static final DeferredRegister<EntityType<?>> LM_ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, GoetyOminous.MOD_ID);

    public static final RegistryObject<EntityType<OvergrownColossusServant>> OVERGROWN_COLOSSUS_SERVANT =
            LM_ENTITIES.register("overgrown_colossus_servant",
                    () -> EntityType.Builder.of(OvergrownColossusServant::new, MobCategory.MONSTER)
                            .sized(3.0F, 5.0F).fireImmune()
                            .build(GoetyOminous.MOD_ID + ":overgrown_colossus_servant"));

    public static final RegistryObject<EntityType<PoisonousShockwave>> POISONOUS_SHOCKWAVE =
            LM_ENTITIES.register("poisonous_shockwave",
                    () -> EntityType.Builder.<PoisonousShockwave>of(PoisonousShockwave::new, MobCategory.MISC)
                            .sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(1)
                            .build(GoetyOminous.MOD_ID + ":poisonous_shockwave"));

    public static final RegistryObject<EntityType<HoveringHurricaneServant>> HOVERING_HURRICANE_SERVANT =
            LM_ENTITIES.register("hovering_hurricane_servant",
                    () -> EntityType.Builder.of(HoveringHurricaneServant::new, MobCategory.MONSTER)
                            .sized(1.0F, 2.0F)
                            .build(GoetyOminous.MOD_ID + ":hovering_hurricane_servant"));

    public static final RegistryObject<EntityType<Tornado>> TORNADO =
            LM_ENTITIES.register("tornado",
                    () -> EntityType.Builder.<Tornado>of(Tornado::new, MobCategory.MISC)
                            .sized(1.5F, 3.0F).clientTrackingRange(6).updateInterval(2).fireImmune()
                            .build(GoetyOminous.MOD_ID + ":tornado"));

    public static final RegistryObject<EntityType<CloudGolemServant>> CLOUD_GOLEM_SERVANT =
            LM_ENTITIES.register("cloud_golem_servant",
                    () -> EntityType.Builder.of(CloudGolemServant::new, MobCategory.MONSTER)
                            .sized(1.5F, 2.5F).fireImmune()
                            .build(GoetyOminous.MOD_ID + ":cloud_golem_servant"));

    public static final RegistryObject<EntityType<ShulkerMimicServant>> SHULKER_MIMIC_SERVANT =
            LM_ENTITIES.register("shulker_mimic_servant",
                    () -> EntityType.Builder.of(ShulkerMimicServant::new, MobCategory.MONSTER)
                            .sized(1.5F, 2.5F).fireImmune()
                            .build(GoetyOminous.MOD_ID + ":shulker_mimic_servant"));

    public static final RegistryObject<EntityType<BigShulkerBullet>> BIG_SHULKER_BULLET =
            LM_ENTITIES.register("big_shulker_bullet",
                    () -> EntityType.Builder.<BigShulkerBullet>of(BigShulkerBullet::new, MobCategory.MISC)
                            .sized(1.0F, 1.0F).clientTrackingRange(6).updateInterval(2).fireImmune()
                            .build(GoetyOminous.MOD_ID + ":big_shulker_bullet"));

    public static final RegistryObject<EntityType<GravityBigShulkerBullet>> GRAVITY_BIG_SHULKER_BULLET =
            LM_ENTITIES.register("gravity_big_shulker_bullet",
                    () -> EntityType.Builder.<GravityBigShulkerBullet>of(GravityBigShulkerBullet::new, MobCategory.MISC)
                            .sized(1.5F, 1.5F).clientTrackingRange(6).updateInterval(2).fireImmune()
                            .build(GoetyOminous.MOD_ID + ":gravity_big_shulker_bullet"));

    public static final RegistryObject<EntityType<CloudEntity>> CLOUD =
            LM_ENTITIES.register("cloud",
                    () -> EntityType.Builder.<CloudEntity>of(CloudEntity::new, MobCategory.MISC)
                            .sized(1.0F, 1.0F).clientTrackingRange(4).updateInterval(20)
                            .build(GoetyOminous.MOD_ID + ":cloud"));

    public static final RegistryObject<EntityType<EnergyBeamEntity>> ENERGY_BEAM =
            LM_ENTITIES.register("energy_beam",
                    () -> EntityType.Builder.<EnergyBeamEntity>of(EnergyBeamEntity::new, MobCategory.MISC)
                            .sized(1.0F, 1.0F).clientTrackingRange(6).updateInterval(2).fireImmune()
                            .build(GoetyOminous.MOD_ID + ":energy_beam"));

    public static final RegistryObject<EntityType<ElectricityEntity>> ELECTRIC_BURST =
            LM_ENTITIES.register("electric_burst",
                    () -> EntityType.Builder.<ElectricityEntity>of(ElectricityEntity::new, MobCategory.MISC)
                            .sized(1.0F, 4.0F).clientTrackingRange(6).updateInterval(2).fireImmune()
                            .build(GoetyOminous.MOD_ID + ":electric_burst"));

    public static final RegistryObject<EntityType<LightningBoltEntity>> LIGHTNING_STRIKE =
            LM_ENTITIES.register("lightning_strike",
                    () -> EntityType.Builder.<LightningBoltEntity>of(LightningBoltEntity::new, MobCategory.MISC)
                            .sized(1.0F, 4.0F).clientTrackingRange(6).updateInterval(2).fireImmune()
                            .build(GoetyOminous.MOD_ID + ":lightning_strike"));

    public static final RegistryObject<EntityType<AnnihilationPursuerServant>> ANNIHILATION_PURSUER_SERVANT =
            LM_ENTITIES.register("annihilation_pursuer_servant",
                    () -> EntityType.Builder.of(AnnihilationPursuerServant::new, MobCategory.MONSTER)
                            .sized(1.5F, 5.0F).fireImmune()
                            .build(GoetyOminous.MOD_ID + ":annihilation_pursuer_servant"));

    public static final RegistryObject<EntityType<SmallAnnihilationBomb>> SMALL_ANNIHILATION_BOMB =
            LM_ENTITIES.register("small_annihilation_bomb",
                    () -> EntityType.Builder.<SmallAnnihilationBomb>of(SmallAnnihilationBomb::new, MobCategory.MISC)
                            .sized(0.5F, 0.5F).clientTrackingRange(6).updateInterval(2).fireImmune()
                            .build(GoetyOminous.MOD_ID + ":small_annihilation_bomb"));

    public static final RegistryObject<EntityType<AnnihilationExplosion>> ANNIHILATION_EXPLOSION =
            LM_ENTITIES.register("annihilation_explosion",
                    () -> EntityType.Builder.<AnnihilationExplosion>of(AnnihilationExplosion::new, MobCategory.MISC)
                            .sized(2.25F, 3.0F).clientTrackingRange(6).updateInterval(2).fireImmune()
                            .build(GoetyOminous.MOD_ID + ":annihilation_explosion"));

    public static final RegistryObject<EntityType<AnnihilationFlameStrike>> ANNIHILATION_FLAME_STRIKE =
            LM_ENTITIES.register("annihilation_flame_strike",
                    () -> EntityType.Builder.<AnnihilationFlameStrike>of(AnnihilationFlameStrike::new, MobCategory.MISC)
                            .sized(1.0F, 2.0F).clientTrackingRange(6).updateInterval(2).fireImmune()
                            .build(GoetyOminous.MOD_ID + ":annihilation_flame_strike"));

    public static final RegistryObject<EntityType<EntityThrown>> ENTITY_THROWN =
            LM_ENTITIES.register("entity_thrown",
                    () -> EntityType.Builder.<EntityThrown>of(EntityThrown::new, MobCategory.MISC)
                            .sized(1.5F, 1.5F).clientTrackingRange(6).updateInterval(2).fireImmune()
                            .build(GoetyOminous.MOD_ID + ":entity_thrown"));

    public static void register(IEventBus modEventBus) {
        LM_ENTITIES.register(modEventBus);
    }
}
