package com.qiuyue.goetyominous.client.events.ac;

import com.Polarice3.Goety.client.render.block.TrainingBlockRenderer;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.particle.ac.CandicornServantChargeParticle;
import com.qiuyue.goetyominous.client.particle.ac.ForsakenServantSpitParticle;
import com.qiuyue.goetyominous.client.particle.ac.LuxtructosaurusServantAshParticle;
import com.qiuyue.goetyominous.client.particle.ac.LuxtructosaurusServantSpitParticle;
import com.qiuyue.goetyominous.client.particle.ac.NucleeperMushroomCloudParticle;
import com.qiuyue.goetyominous.client.particle.ac.TremorzillaServantSteamParticle;
import com.qiuyue.goetyominous.client.render.curios.ac.RaycatAmuletRenderer;
import com.qiuyue.goetyominous.common.init.ac.AcBlockEntityRegistry;
import com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry;
import com.qiuyue.goetyominous.common.init.ac.AcParticles;
import com.qiuyue.goetyominous.compat.mod.AlexCavesCompat;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import com.qiuyue.goetyominous.client.render.ac.TremorzillaBreathRenderer;
import com.qiuyue.goetyominous.client.render.ac.XRayRenderer;
import com.qiuyue.goetyominous.common.events.ac.AtlatitanRenderEvents;
import com.qiuyue.goetyominous.common.events.ac.CorrodentDigCrackRenderEvents;
import com.qiuyue.goetyominous.common.events.ac.ForsakenSpitParticleRenderEvents;
import com.qiuyue.goetyominous.common.events.ac.LicowitchTeleportRenderEvents;
import com.qiuyue.goetyominous.common.events.ac.LuxtructosaurusRenderEvents;
import com.qiuyue.goetyominous.common.events.ac.TremorzillaRenderEvents;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AcClientEvents {

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        if (!AlexCavesCompat.isAlexCavesLoaded()) {
            return;
        }

        RaycatAmuletRenderer.register();
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        if (!AlexCavesCompat.isAlexCavesLoaded()) {
            return;
        }

        event.registerEntityRenderer(AcEntityRegistry.EXTINCTION_CATALYST.get(), ItemEntityRenderer::new);
        event.registerBlockEntityRenderer(AcBlockEntityRegistry.GAMMAROACH_NEST.get(), TrainingBlockRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry.GROTTOCERATOPS_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.ac.RenderGrottoceratopsServant::new);
        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry.TREMORSAURUS_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.ac.RenderTremorsaurusServant::new);
        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry.VALLUMRAPTOR_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.ac.RenderVallumraptorServant::new);
        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry.NUCLEEPER_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.ac.RenderNucleeperServant::new);
        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry.BRAINIAC_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.ac.RenderBrainiacServant::new);
        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry.CANIAC_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.ac.RenderCaniacServant::new);
        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry.GAMMAROACH_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.ac.RenderGammaroachServant::new);
        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry.CORRODENT_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.ac.RenderCorrodentServant::new);
        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry.GUMMY_BEAR_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.ac.RenderGummyBearServant::new);
        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry.CARAMEL_CUBE_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.ac.RenderCaramelCubeServant::new);
        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry.MELTED_CARAMEL_SERVANT.get(),
                com.github.alexmodguy.alexscaves.client.render.entity.MeltedCaramelRenderer::new);
        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry.TITAN_BOMB.get(),
                com.qiuyue.goetyominous.client.render.ac.RenderTitanBomb::new);
        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry.MINE_GUARDIAN_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.ac.RenderMineGuardianServant::new);
        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry.NUCLEAR_GUARDIAN_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.ac.RenderNuclearGuardianServant::new);
        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry.MINE_GUARDIAN_ANCHOR_SERVANT.get(),
                com.github.alexmodguy.alexscaves.client.render.entity.MineGuardianAnchorRenderer::new);
        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry.HULLBREAKER_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.ac.RenderHullbreakerServant::new);
        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry.DEEP_ONE_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.ac.RenderDeepOneServant::new);
        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry.DEEP_ONE_KNIGHT_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.ac.RenderDeepOneKnightServant::new);
        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry.DEEP_ONE_MAGE_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.ac.RenderDeepOneMageServant::new);
        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry.LICOWITCH_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.ac.RenderLicowitchServant::new);
        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry.DEEP_ONE_SERVANT_WAVE.get(),
                com.qiuyue.goetyominous.client.render.ac.RenderDeepOneServantWave::new);
        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry.DEEP_ONE_MAGE_SERVANT_WAVE.get(),
                com.qiuyue.goetyominous.client.render.ac.RenderDeepOneMageServantWave::new);
        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry.DEEP_ONE_MAGE_SERVANT_WATER_BOLT.get(),
                com.qiuyue.goetyominous.client.render.ac.RenderDeepOneMageServantWaterBolt::new);
        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry.WATER_BOLT.get(),
                com.qiuyue.goetyominous.client.render.ac.RenderWaterBoltProjectile::new);
        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry.LICOWITCH_SERVANT_HEX.get(),
                com.qiuyue.goetyominous.client.render.ac.RenderLicowitchServantHex::new);
        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry.LICOWITCH_SERVANT_PEPPERMINT.get(),
                com.qiuyue.goetyominous.client.render.ac.RenderLicowitchServantPeppermint::new);
        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry.TREMORZILLA_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.ac.RenderTremorzillaServant::new);
        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry.ATLATITAN_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.ac.RenderAtlatitanServant::new);
        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry.RELICHEIRUS_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.ac.RenderRelicheirusServant::new);
        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry.LUXTRUCTOSAURUS_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.ac.RenderLuxtructosaurusServant::new);
        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry.GUMBEEPER_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.ac.RenderGumbeeperServant::new);
        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry.GUM_WORM_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.ac.RenderGumWormServant::new);
        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry.GUM_WORM_SEGMENT_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.ac.RenderGumWormSegmentServant::new);
        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry.GUMBALL_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.ac.RenderGumballServantEntity::new);
        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry.VESPER_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.ac.RenderVesperServant::new);
        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry.TELETOR_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.ac.RenderTeletorServant::new);
        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry.TELETOR_WEAPON_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.ac.RenderTeletorWeaponServant::new);
        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry.FORSAKEN_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.ac.RenderForsakenServant::new);
        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry.CANDICORN_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.ac.RenderCandicornServant::new);
        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry.PURE_DARK_VOID.get(),
                com.qiuyue.goetyominous.client.render.ac.PureDarkVoidRenderer::new);
        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry.SERVANT_TEPHRA.get(),
                com.qiuyue.goetyominous.client.render.ac.ServantTephraRenderer::new);
        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry.TREMORSAURUS_SPIRIT.get(),
                com.qiuyue.goetyominous.client.render.ac.RenderTremorsaurusSpirit::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry.GROTTOCERATOPS_SPIRIT.get(),
                com.qiuyue.goetyominous.client.render.ac.RenderGrottoceratopsSpirit::new);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        if (!AlexCavesCompat.isAlexCavesLoaded()) {
            return;
        }

        MinecraftForge.EVENT_BUS.register(TremorsaurusHudEvents.class);
        MinecraftForge.EVENT_BUS.register(TremorzillaHudEvents.class);
        MinecraftForge.EVENT_BUS.register(AtlatitanHudEvents.class);
        MinecraftForge.EVENT_BUS.register(LuxtructosaurusHudEvents.class);
        MinecraftForge.EVENT_BUS.register(ForsakenRiderHudEvents.class);
        MinecraftForge.EVENT_BUS.register(CandicornRiderHudEvents.class);
        MinecraftForge.EVENT_BUS.register(TremorzillaRenderEvents.class);
        MinecraftForge.EVENT_BUS.register(TremorzillaBreathRenderer.class);
        MinecraftForge.EVENT_BUS.register(XRayRenderer.class);
        MinecraftForge.EVENT_BUS.register(AtlatitanRenderEvents.class);
        MinecraftForge.EVENT_BUS.register(LuxtructosaurusRenderEvents.class);
        MinecraftForge.EVENT_BUS.register(CorrodentDigCrackRenderEvents.class);
        MinecraftForge.EVENT_BUS.register(ForsakenSpitParticleRenderEvents.class);
        MinecraftForge.EVENT_BUS.register(LicowitchTeleportRenderEvents.class);
    }

    @SubscribeEvent
    @SuppressWarnings("unchecked")
    public static void registerParticles(RegisterParticleProvidersEvent event) {
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
}
