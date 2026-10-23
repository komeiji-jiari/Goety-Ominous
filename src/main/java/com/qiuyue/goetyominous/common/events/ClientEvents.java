package com.qiuyue.goetyominous.common.events;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.particle.*;
import com.qiuyue.goetyominous.client.render.block.BoggedSkullBlockEntityRenderer;
import com.qiuyue.goetyominous.client.render.block.MiredSkullBlockEntityRenderer;
import com.qiuyue.goetyominous.client.render.block.PlushieBlockEntityRenderer;
import com.qiuyue.goetyominous.client.render.block.TrialSpawnerRenderer;
import com.qiuyue.goetyominous.client.render.block.VaultRenderer;
import com.qiuyue.goetyominous.client.render.entity.OminousItemSpawnerRenderer;
import com.qiuyue.goetyominous.client.render.curios.PlushieCurioRenderer;
import com.qiuyue.goetyominous.common.init.ModBlockEntities;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import net.minecraft.client.particle.SpellParticle;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import com.qiuyue.goetyominous.common.init.ModParticleTypes;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientEvents {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.VAULT.get(), VaultRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.TRIAL_SPAWNER.get(), TrialSpawnerRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.OMINOUS_ITEM_SPAWNER.get(), OminousItemSpawnerRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.PLUSHIE.get(), PlushieBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.BOGGED_SKULL.get(), BoggedSkullBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.MIRED_SKULL.get(), MiredSkullBlockEntityRenderer::new);
        PlushieCurioRenderer.register();
    }

    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        event.registerSpecial(ModParticleTypes.GUST_EMITTER_SMALL.get(), new GustSeedParticle.Provider(1.0D, 3, 2));
        event.registerSpecial(ModParticleTypes.GUST_EMITTER_LARGE.get(), new GustSeedParticle.Provider(3.0D, 7, 0));
        event.registerSpriteSet(ModParticleTypes.GUST.get(), GustParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.SMALL_GUST.get(), GustParticle.SmallProvider::new);
        event.registerSpriteSet(ModParticleTypes.INFESTED.get(), SpellParticle.Provider::new);
        event.registerSpecial(ModParticleTypes.DUST_PILLAR.get(), new DustPillarProvider());
        event.registerSpriteSet(ModParticleTypes.TRIAL_OMEN.get(), SpellParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.VAULT_CONNECTION.get(), FlyTowardsPositionParticle.VaultConnectionProvider::new);
        event.registerSpriteSet(ModParticleTypes.TRIAL_SPAWNER_DETECTION.get(), TrialSpawnerDetectionParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.TRIAL_SPAWNER_DETECTION_OMINOUS.get(), TrialSpawnerDetectionParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.OMINOUS_SPAWNING.get(), FlyStraightTowardsParticle.OminousSpawnerProvider::new);
        event.registerSpriteSet(ModParticleTypes.WIND_TYPHOON.get(), WindTyphoonParticle.Provider::new);
    }
}
