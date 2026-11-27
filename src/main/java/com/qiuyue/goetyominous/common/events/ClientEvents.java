package com.qiuyue.goetyominous.common.events;

import com.Polarice3.Goety.client.particles.GatheringParticle;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.particle.*;
import com.qiuyue.goetyominous.client.render.ChasseurArrowRenderer;
import com.qiuyue.goetyominous.client.render.block.PlushieBlockEntityRenderer;
import com.qiuyue.goetyominous.client.render.projectile.QuadStarRenderer;
import com.qiuyue.goetyominous.client.render.block.TrialSpawnerRenderer;
import com.qiuyue.goetyominous.client.render.block.VaultRenderer;
import com.qiuyue.goetyominous.client.gui.overlay.NetherEternalCauldronGui;
import com.qiuyue.goetyominous.client.render.model.HandheldSeparateTransformsModel;
import com.qiuyue.goetyominous.client.render.model.VaultVariantModel;
import com.qiuyue.goetyominous.common.blocks.trial.TrialBlocks;
import com.qiuyue.goetyominous.client.render.entity.OminousItemSpawnerRenderer;
import com.qiuyue.goetyominous.client.render.curios.NetherEternalCauldronCurioRenderer;
import com.qiuyue.goetyominous.client.render.curios.PlushieCurioRenderer;
import com.qiuyue.goetyominous.common.init.ModBlockEntities;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import com.qiuyue.goetyominous.common.items.ModItems;
import net.minecraft.client.particle.SpellParticle;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import com.qiuyue.goetyominous.common.init.ModParticleTypes;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

import java.util.List;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientEvents {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.VAULT.get(), VaultRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.TRIAL_SPAWNER.get(), TrialSpawnerRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.OMINOUS_ITEM_SPAWNER.get(), OminousItemSpawnerRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.CHASSEUR_ARROW.get(), ChasseurArrowRenderer::new);
        event.registerEntityRenderer(ModEntityTypes.QUAD_STAR.get(), QuadStarRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.PLUSHIE.get(), PlushieBlockEntityRenderer::new);
        PlushieCurioRenderer.register();
        CuriosRendererRegistry.register(ModItems.NETHER_ETERNAL_CAULDRON.get(),
                NetherEternalCauldronCurioRenderer::new);
    }

    @SubscribeEvent
    public static void registerOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("nether_eternal_cauldron", NetherEternalCauldronGui.OVERLAY);
    }

    @SubscribeEvent
    public static void modifyBakingResult(ModelEvent.ModifyBakingResult event) {
        for (Block block : List.of(TrialBlocks.VAULT.get(), TrialBlocks.TRIAL_SPAWNER.get())) {
            for (BlockState state : block.getStateDefinition().getPossibleStates()) {
                event.getModels().computeIfPresent(BlockModelShaper.stateToModelLocation(state),
                        (location, model) -> model instanceof VaultVariantModel ? model : new VaultVariantModel(model));
            }
        }
    }

    @SubscribeEvent
    public static void registerGeometryLoaders(ModelEvent.RegisterGeometryLoaders event) {
        event.register("handheld_separate_transforms", HandheldSeparateTransformsModel.Loader.INSTANCE);
    }

    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        event.registerSpecial(ModParticleTypes.GUST_EMITTER_SMALL.get(), new GustSeedParticle.Provider(1.0D, 3, 2));
        event.registerSpecial(ModParticleTypes.GUST_EMITTER_LARGE.get(), new GustSeedParticle.Provider(3.0D, 7, 0));
        event.registerSpriteSet(ModParticleTypes.GUST.get(), GustParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.SMALL_GUST.get(), GustParticle.SmallProvider::new);
        event.registerSpriteSet(ModParticleTypes.INFESTED.get(), SpellParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.FLAME_GATHER.get(), GatheringParticle.Provider::new);
        event.registerSpecial(ModParticleTypes.DUST_PILLAR.get(), new DustPillarProvider());
        event.registerSpriteSet(ModParticleTypes.TRIAL_OMEN.get(), SpellParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.VAULT_CONNECTION.get(), FlyTowardsPositionParticle.VaultConnectionProvider::new);
        event.registerSpriteSet(ModParticleTypes.CRYPT_VAULT_CONNECTION.get(), FlyTowardsPositionParticle.CryptVaultConnectionProvider::new);
        event.registerSpriteSet(ModParticleTypes.TRIAL_SPAWNER_DETECTION.get(), TrialSpawnerDetectionParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.TRIAL_SPAWNER_DETECTION_OMINOUS.get(), TrialSpawnerDetectionParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.OMINOUS_SPAWNING.get(), FlyStraightTowardsParticle.OminousSpawnerProvider::new);
        event.registerSpriteSet(ModParticleTypes.WIND_TYPHOON.get(), WindTyphoonParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.BOUNCING_SPARK.get(), BouncingSparkParticle.Provider::new);
        event.registerSpriteSet(ModParticleTypes.SOUL_FLAME_GATHER.get(), GatheringParticle.Provider::new);
        event.registerSpecial(ModParticleTypes.SHAMAN_SOUL.get(), new ShamanSoulParticle.Provider());
    }
}
