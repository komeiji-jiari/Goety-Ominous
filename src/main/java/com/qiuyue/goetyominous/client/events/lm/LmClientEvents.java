package com.qiuyue.goetyominous.client.events.lm;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.particle.lm.Circle;
import com.qiuyue.goetyominous.client.particle.lm.GhostlySoul;
import com.qiuyue.goetyominous.client.particle.lm.GroundSoulParticle;
import com.qiuyue.goetyominous.client.particle.lm.PhantomDaggerTrail;
import com.qiuyue.goetyominous.client.particle.lm.SmallGreenFlame;
import com.qiuyue.goetyominous.client.particle.lm.SoulExplosion;
import com.qiuyue.goetyominous.client.particle.lm.SoulPillarExplosion;
import com.qiuyue.goetyominous.client.render.EmptyRenderer;
import com.qiuyue.goetyominous.common.init.lm.LmParticles;
import com.qiuyue.goetyominous.compat.mod.LegendaryMonstersCompat;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class LmClientEvents {

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        if (!LegendaryMonstersCompat.isLegendaryMonstersLoaded()) {
            return;
        }

        event.registerLayerDefinition(ModEntityLayers.OVERGROWN_COLOSSUS_SERVANT_LAYER,
                com.qiuyue.goetyominous.client.render.model.lm.OvergrownColossusServantModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.POSSESSED_PALADIN_SERVANT_LAYER,
                com.qiuyue.goetyominous.client.render.model.lm.PossessedPaladinServantModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.FROSTBITTEN_GOLEM_SERVANT_LAYER,
                com.qiuyue.goetyominous.client.render.model.lm.FrostbittenGolemServantModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.ANCIENT_GUARDIAN_SERVANT_LAYER,
                com.qiuyue.goetyominous.client.render.model.lm.AncientGuardianServantModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.ICE_SPIKE_LAYER,
                com.qiuyue.goetyominous.client.render.model.lm.IceSpikeModel::createLayerDefinition);

        event.registerLayerDefinition(ModEntityLayers.PHANTOM_DAGGER,
                com.qiuyue.goetyominous.client.render.model.lm.ThrownPhantomDaggerModel::createBodyLayer);
        event.registerLayerDefinition(ModEntityLayers.HOVERING_HURRICANE_SERVANT_LAYER,
                com.qiuyue.goetyominous.client.render.model.lm.HoveringHurricaneServantModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.CLOUD_GOLEM_SERVANT_LAYER,
                com.qiuyue.goetyominous.client.render.model.lm.CloudGolemServantModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.WANDERING_EYE_SERVANT_LAYER,
                com.qiuyue.goetyominous.client.render.model.lm.WanderingEyeServantModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.FLAME_DRIFTER_SERVANT_LAYER,
                com.qiuyue.goetyominous.client.render.model.lm.FlameDrifterServantModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.BEHEADED_KNIGHT_SERVANT_LAYER,
                com.qiuyue.goetyominous.client.render.model.lm.BeheadedKnightServantModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.RESURRECTED_KNIGHT_SERVANT_LAYER,
                com.qiuyue.goetyominous.client.render.model.lm.ResurrectedKnightServantModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.ENDERSENT_SERVANT_LAYER,
                com.qiuyue.goetyominous.client.render.model.lm.EndersentServantModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.CHORUSLING_SERVANT_LAYER,
                com.qiuyue.goetyominous.client.render.model.lm.ChoruslingServantModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.FRACTURED_SERVANT_LAYER,
                com.qiuyue.goetyominous.client.render.model.lm.FracturedServantModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.FRACTURED_APOSTLE_SERVANT_LAYER,
                com.qiuyue.goetyominous.client.render.model.lm.FracturedApostleServantModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.HAUNTED_KNIGHT_SERVANT_LAYER,
                com.qiuyue.goetyominous.client.render.model.lm.HauntedKnightServantModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.HAUNTED_GUARD_SERVANT_LAYER,
                com.qiuyue.goetyominous.client.render.model.lm.HauntedGuardServantModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.FLAMEBORN_WARRIOR_SERVANT_LAYER,
                com.qiuyue.goetyominous.client.render.model.lm.FlamebornWarriorServantModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.FLAMEBORN_GUARD_SERVANT_LAYER,
                com.qiuyue.goetyominous.client.render.model.lm.FlamebornGuardServantModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.LM_CLOUD_LAYER,
                net.miauczel.legendary_monsters.entity.ProjectileEntityRenderer.CloudModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.LM_LIGHTNING_STRIKE_LAYER,
                net.miauczel.legendary_monsters.entity.client.Model.LightningStrikeModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.SHULKER_MIMIC_SERVANT_LAYER,
                com.qiuyue.goetyominous.client.render.model.lm.ShulkerMimicServantModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.BIG_SHULKER_BULLET_LAYER,
                net.miauczel.legendary_monsters.entity.client.Model.BigShulkerBulletModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.GRAVITY_BIG_SHULKER_BULLET_LAYER,
                net.miauczel.legendary_monsters.entity.client.Model.GravityBigShulkerBulletModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.ANNIHILATION_PURSUER_SERVANT_LAYER,
                com.qiuyue.goetyominous.client.render.model.lm.AnnihilationPursuerServantModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.THE_OBLITERATOR_SERVANT_LAYER,
                com.qiuyue.goetyominous.client.render.model.lm.TheObliteratorServantModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.THE_OBLITERATOR_CLONE_LAYER,
                com.qiuyue.goetyominous.client.render.model.lm.TheObliteratorCloneModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.THE_OBLITERATOR_ARMED_CLONE_LAYER,
                com.qiuyue.goetyominous.client.render.model.lm.TheObliteratorArmedCloneModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        if (!LegendaryMonstersCompat.isLegendaryMonstersLoaded()) {
            return;
        }

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry.OVERGROWN_COLOSSUS_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.lm.OvergrownColossusServantRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry.POSSESSED_PALADIN_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.lm.PossessedPaladinServantRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry.FROSTBITTEN_GOLEM_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.lm.FrostbittenGolemServantRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry.ANCIENT_GUARDIAN_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.lm.AncientGuardianServantRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry.ICE_SHOCKWAVE_SPAWNER.get(),
                EmptyRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry.ICE_SHOCKWAVE.get(),
                EmptyRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry.ICE_SPIKE.get(),
                com.qiuyue.goetyominous.client.render.lm.IceSpikeRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry.FIRE_PILLAR.get(),
                EmptyRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry.FIRE_BREATH.get(),
                EmptyRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry.THE_OBLITERATOR_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.lm.TheObliteratorServantRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry.THE_OBLITERATOR_CLONE.get(),
                com.qiuyue.goetyominous.client.render.lm.TheObliteratorCloneRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry.THE_OBLITERATOR_CLONE_ARMED.get(),
                com.qiuyue.goetyominous.client.render.lm.TheObliteratorArmedCloneRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry.ANNIHILATION_BOMB.get(),
                EmptyRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry.POISONOUS_SHOCKWAVE.get(),
                EmptyRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry.THROWN_PHANTOM_DAGGER.get(),
                com.qiuyue.goetyominous.client.render.lm.ThrownPhantomDaggerRenderer::new);

        // 灵魂冲击和上面的毒素冲击波一样，是「没有模型、没有贴图、纯粒子」的实体。
        // 这不是我们偷懒 —— 传奇怪物原版给它注册的渲染器就是个空壳。
        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry.SOUL_STRIKE.get(),
                EmptyRenderer::new);

        // 招 38 扔出去的三叉戟。模型和贴图都是借传奇怪物的，见渲染器类注释。
        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry.SOUL_TRIDENT.get(),
                com.qiuyue.goetyominous.client.render.lm.SoulTridentServantRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry.SOUL_JAVELIN.get(),
                com.qiuyue.goetyominous.client.render.lm.SoulJavelinServantRenderer::new);

        // 三叉戟插地后炸出来的「灵魂柱爆炸」。同样是「没有模型、没有贴图、纯粒子」，
        // 传奇怪物原版压根没给它注册渲染器（连空壳都没有）—— 我们补个空壳，
        // 免得 Forge 找不到渲染器时刷警告。
        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry.SOUL_PILLAR_EXPLOSION.get(),
                EmptyRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry.SOUL_PILLAR.get(),
                EmptyRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry.SOUL_BLADE_UNDERGROUND.get(),
                com.qiuyue.goetyominous.client.render.lm.SoulBladeServantRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry.FALLING_SOUL_BLADE.get(),
                com.qiuyue.goetyominous.client.render.lm.FallingSoulBladeServantRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry.SOUL_SHIELD.get(),
                com.qiuyue.goetyominous.client.render.lm.SoulShieldServantRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry.HOVERING_HURRICANE_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.lm.HoveringHurricaneServantRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry.TORNADO.get(),
                EmptyRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry.CLOUD_GOLEM_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.lm.CloudGolemServantRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry.WANDERING_EYE_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.lm.WanderingEyeServantRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry.FLAME_DRIFTER_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.lm.FlameDrifterServantRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry.FLAMEBORN_WARRIOR_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.lm.FlamebornWarriorServantRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry.FLAMEBORN_GUARD_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.lm.FlamebornGuardServantRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry.CLOUD.get(),
                com.qiuyue.goetyominous.client.render.lm.CloudEntityRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry.ENERGY_BEAM.get(),
                com.qiuyue.goetyominous.client.render.lm.EnergyBeamEntityRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry.ELECTRIC_BURST.get(),
                EmptyRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry.LIGHTNING_STRIKE.get(),
                com.qiuyue.goetyominous.client.render.lm.LightningBoltEntityRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry.SHULKER_MIMIC_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.lm.ShulkerMimicServantRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry.BIG_SHULKER_BULLET.get(),
                com.qiuyue.goetyominous.client.render.lm.BigShulkerBulletRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry.GRAVITY_BIG_SHULKER_BULLET.get(),
                com.qiuyue.goetyominous.client.render.lm.GravityBigShulkerBulletRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry.ANNIHILATION_PURSUER_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.lm.AnnihilationPursuerServantRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry.SMALL_ANNIHILATION_BOMB.get(),
                EmptyRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry.ANNIHILATION_EXPLOSION.get(),
                EmptyRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry.ANNIHILATION_FLAME_STRIKE.get(),
                EmptyRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry.ENTITY_THROWN.get(),
                EmptyRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry.BEHEADED_KNIGHT_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.lm.BeheadedKnightServantRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry.RESURRECTED_KNIGHT_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.lm.ResurrectedKnightServantRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry.THROWN_KNIGHT.get(),
                EmptyRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry.ENDERSENT_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.lm.EndersentServantRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry.CHORUSLING_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.lm.ChoruslingServantRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry.FRACTURED_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.lm.FracturedServantRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry.FRACTURED_APOSTLE_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.lm.FracturedApostleServantRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry.HAUNTED_KNIGHT_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.lm.HauntedKnightServantRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry.HAUNTED_GUARD_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.lm.HauntedGuardServantRenderer::new);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        if (!LegendaryMonstersCompat.isLegendaryMonstersLoaded()) {
            return;
        }

        MinecraftForge.EVENT_BUS.register(PossessedPaladinGrabRenderEvents.class);
        MinecraftForge.EVENT_BUS.register(BeheadedKnightGrabRenderEvents.class);
    }

    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        if (!LegendaryMonstersCompat.isLegendaryMonstersLoaded()) {
            return;
        }

        event.registerSpriteSet(LmParticles.GHOSTLY_SOUL.get(), GhostlySoul.Provider::new);
        event.registerSpriteSet(LmParticles.GHOSTLY_SOUL_RED.get(), GhostlySoul.Provider::new);
        event.registerSpriteSet(LmParticles.RED_SOUL_FLAME.get(), SmallGreenFlame.Provider::new);
        event.registerSpriteSet(LmParticles.SOUL_EXPLOSION_RED.get(), SoulExplosion.Provider::new);
        event.registerSpriteSet(LmParticles.SOUL_EXPLOSION.get(), SoulExplosion.Provider::new);
        event.registerSpriteSet(LmParticles.GROUNDSOUL_RED.get(), GroundSoulParticle.Factory::new);
        event.registerSpriteSet(LmParticles.SOUL_PILLAR_EXPLOSION.get(), SoulPillarExplosion.Factory::new);
        event.registerSpriteSet(LmParticles.CIRCLE.get(), Circle.RingFactory::new);
        event.registerSpecial(LmParticles.PHANTOM_DAGGER_TRAIL.get(), new PhantomDaggerTrail.OrbFactory());
    }
}
