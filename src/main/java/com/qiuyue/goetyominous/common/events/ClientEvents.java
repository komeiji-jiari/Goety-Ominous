package com.qiuyue.goetyominous.common.events;

import com.Polarice3.Goety.client.render.block.TrainingBlockRenderer;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.particle.*;
import com.qiuyue.goetyominous.client.render.block.PlushieBlockEntityRenderer;
import com.qiuyue.goetyominous.client.particle.ac.CandicornServantChargeParticle;
import com.qiuyue.goetyominous.client.particle.ac.ForsakenServantSpitParticle;
import com.qiuyue.goetyominous.client.particle.ac.LuxtructosaurusServantAshParticle;
import com.qiuyue.goetyominous.client.particle.ac.LuxtructosaurusServantSpitParticle;
import com.qiuyue.goetyominous.client.particle.ac.TremorzillaServantSteamParticle;
import com.qiuyue.goetyominous.client.particle.ac.NucleeperMushroomCloudParticle;
import com.qiuyue.goetyominous.client.particle.lm.Circle;
import com.qiuyue.goetyominous.client.particle.lm.GhostlySoul;
import com.qiuyue.goetyominous.client.particle.lm.GroundSoulParticle;
import com.qiuyue.goetyominous.client.particle.lm.PhantomDaggerTrail;
import com.qiuyue.goetyominous.client.particle.lm.SmallGreenFlame;
import com.qiuyue.goetyominous.client.particle.lm.SoulExplosion;
import com.qiuyue.goetyominous.client.particle.lm.SoulPillarExplosion;
import com.qiuyue.goetyominous.client.render.EmptyRenderer;
import com.qiuyue.goetyominous.client.render.block.VaultRenderer;
import com.qiuyue.goetyominous.client.render.curios.PlushieCurioRenderer;
import com.qiuyue.goetyominous.common.init.ModBlockEntities;
import com.qiuyue.goetyominous.common.init.ac.AcBlockEntityRegistry;
import com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry;
import com.qiuyue.goetyominous.common.init.ac.AcParticles;
import com.qiuyue.goetyominous.common.init.lm.LmParticles;
import com.qiuyue.goetyominous.common.init.mm.MmEntityRegistry;
import com.qiuyue.goetyominous.compat.mod.AlexCavesCompat;
import com.qiuyue.goetyominous.compat.mod.LegendaryMonstersCompat;
import com.qiuyue.goetyominous.compat.mod.MutantMoreCompat;
import net.minecraft.client.particle.SpellParticle;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
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
        event.registerBlockEntityRenderer(ModBlockEntities.PLUSHIE.get(), PlushieBlockEntityRenderer::new);
        PlushieCurioRenderer.register();

        if (MutantMoreCompat.isMutantMoreLoaded()) {
            event.registerEntityRenderer(MmEntityRegistry.AREA_DAMAGE.get(), EmptyRenderer::new);
        }

        if (AlexCavesCompat.isAlexCavesLoaded()) {
            event.registerEntityRenderer(AcEntityRegistry.EXTINCTION_CATALYST.get(), ItemEntityRenderer::new);
            event.registerBlockEntityRenderer(AcBlockEntityRegistry.GAMMAROACH_NEST.get(), TrainingBlockRenderer::new);
        }
    }

    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        registerLmParticles(event);

        event.registerSpecial(ModParticleTypes.GUST_EMITTER_SMALL.get(), new GustSeedParticle.Provider(1.0D, 3, 2));
        event.registerSpecial(ModParticleTypes.GUST_EMITTER_LARGE.get(), new GustSeedParticle.Provider(3.0D, 7, 0));
        event.registerSpriteSet(ModParticleTypes.GUST.get(), GustParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.SMALL_GUST.get(), GustParticle.SmallProvider::new);
        event.registerSpriteSet(ModParticleTypes.INFESTED.get(), SpellParticle.Provider::new);
        event.registerSpecial(ModParticleTypes.DUST_PILLAR.get(), new DustPillarProvider());
        event.registerSpriteSet(ModParticleTypes.TRIAL_OMEN.get(), SpellParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.VAULT_CONNECTION.get(), FlyTowardsPositionParticle.VaultConnectionProvider::new);
        event.registerSpriteSet(ModParticleTypes.TRIAL_SPAWNER_DETECTION_OMINOUS.get(), TrialSpawnerDetectionParticle.Provider::new);

        if (!AlexCavesCompat.isAlexCavesLoaded()) {
            return;
        }
        event.registerSpecial((ParticleType<SimpleParticleType>) AcParticles.NUCLEEPER_MUSHROOM_CLOUD.get(),
                new NucleeperMushroomCloudParticle.Provider());
        event.registerSpriteSet((ParticleType<SimpleParticleType>) AcParticles.CANDICORN_CHARGE.get(),
                CandicornServantChargeParticle.Factory::new);
        event.registerSpriteSet((ParticleType<SimpleParticleType>) AcParticles.FORSAKEN_SERVANT_SPIT.get(),
                ForsakenServantSpitParticle.Factory::new);
        event.registerSpriteSet(AcParticles.LUXTRUCTOSAURUS_SERVANT_SPIT.get(),
                LuxtructosaurusServantSpitParticle.Factory::new);
        event.registerSpriteSet(AcParticles.LUXTRUCTOSAURUS_SERVANT_ASH.get(),
                LuxtructosaurusServantAshParticle.Factory::new);
        event.registerSpriteSet(AcParticles.TREMORZILLA_SERVANT_STEAM.get(),
                TremorzillaServantSteamParticle.Factory::new);
    }

    private static void registerLmParticles(RegisterParticleProvidersEvent event) {
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
