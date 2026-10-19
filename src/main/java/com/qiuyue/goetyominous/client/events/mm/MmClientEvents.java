package com.qiuyue.goetyominous.client.events.mm;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.EmptyRenderer;
import com.qiuyue.goetyominous.client.render.mm.MutantHoglinServantRenderer;
import com.qiuyue.goetyominous.client.render.mm.MutantShulkerServantRenderer;
import com.qiuyue.goetyominous.client.render.mm.MutantShulkerServantTrapRenderer;
import com.qiuyue.goetyominous.client.render.model.mm.MutantHoglinServantModel;
import com.qiuyue.goetyominous.client.render.model.mm.MutantShulkerServantBulletModel;
import com.qiuyue.goetyominous.client.render.model.mm.MutantShulkerServantModel;
import com.qiuyue.goetyominous.client.render.model.mm.MutantShulkerServantTrapModel;
import com.qiuyue.goetyominous.common.init.mm.MmEntityRegistry;
import com.qiuyue.goetyominous.common.init.mm.ModSpellControllers;
import com.qiuyue.goetyominous.compat.mod.MutantMoreCompat;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MmClientEvents {

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        if (!MutantMoreCompat.isMutantMoreLoaded()) {
            return;
        }

        event.registerLayerDefinition(com.qiuyue.goetyominous.client.render.model.mm.MutantWitherSkeletonServantModel.MAIN,
                com.qiuyue.goetyominous.client.render.model.mm.MutantWitherSkeletonServantModel::createBodyLayer);

        event.registerLayerDefinition(com.qiuyue.goetyominous.client.render.model.mm.MutantWitherSkeletonServantModel.INNER_ARMOUR,
                com.alexander.mutantmore.advanced_animation_utils.armour_utils.AdvancedArmourLayer::createInnerArmourLayer);

        event.registerLayerDefinition(com.qiuyue.goetyominous.client.render.model.mm.MutantWitherSkeletonServantModel.OUTER_ARMOUR,
                com.alexander.mutantmore.advanced_animation_utils.armour_utils.AdvancedArmourLayer::createOuterArmourLayer);

        event.registerLayerDefinition(com.qiuyue.goetyominous.client.init.ModEntityLayers.WITHER_SLASH_LAYER,
                com.qiuyue.goetyominous.client.render.model.mm.WitherSlashModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.MUTANT_HOGLIN_SERVANT_LAYER,
                MutantHoglinServantModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.MUTANT_SHULKER_SERVANT_LAYER,
                MutantShulkerServantModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.MUTANT_SHULKER_SERVANT_TRAP_LAYER,
                MutantShulkerServantTrapModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.MUTANT_JUNGLE_ZOMBIE_SERVANT_LAYER,
                com.qiuyue.goetyominous.client.render.model.mm.MutantJungleZombieServantModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.MUTANT_HUSK_SERVANT_LAYER,
                com.qiuyue.goetyominous.client.render.model.mm.MutantHuskServantModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.SAND_BOULDER_SERVANT_LAYER,
                com.qiuyue.goetyominous.client.render.model.mm.SandBoulderServantModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.ZOMBIE_RESURRECTION_SERVANT_LAYER,
                com.qiuyue.goetyominous.client.render.model.mm.ZombieResurrectionServantModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.MUTANT_FROZEN_ZOMBIE_SERVANT_LAYER,
                com.qiuyue.goetyominous.client.render.model.mm.MutantFrozenZombieServantModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.ICICLE_SPIKE_SERVANT_LAYER,
                com.qiuyue.goetyominous.client.render.model.mm.IcicleSpikeServantModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.THROWN_ICICLE_SERVANT_LAYER,
                com.qiuyue.goetyominous.client.render.model.mm.ThrownIcicleServantModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.GIANT_SNOWBALL_SERVANT_LAYER,
                com.qiuyue.goetyominous.client.render.model.mm.GiantSnowballServantModel::createBodyLayer);

        event.registerLayerDefinition(MutantShulkerServantBulletModel.LAYER_LOCATION,
                MutantShulkerServantBulletModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        if (!MutantMoreCompat.isMutantMoreLoaded()) {
            return;
        }

        event.registerEntityRenderer(MmEntityRegistry.MUTANT_JUNGLE_ZOMBIE_VINE_SWEEP.get(), EmptyRenderer::new);
        event.registerEntityRenderer(MmEntityRegistry.MUTANT_HUSK_SAND_BURST.get(), EmptyRenderer::new);
        event.registerEntityRenderer(MmEntityRegistry.MUTANT_HUSK_SAND_GEYSER.get(), EmptyRenderer::new);
        event.registerEntityRenderer(MmEntityRegistry.MUTANT_JUNGLE_ZOMBIE_POISON_CLOUD.get(), EmptyRenderer::new);
        event.registerEntityRenderer(MmEntityRegistry.MUTANT_WITHER_BREATH.get(), EmptyRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.mm.MmEntityRegistry.MUTANT_WITHER_SKELETON_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.mm.MutantWitherSkeletonServantRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.mm.MmEntityRegistry.WITHER_SLASH.get(),
                com.qiuyue.goetyominous.client.render.mm.WitherSlashRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.mm.MmEntityRegistry.MUTANT_HOGLIN_SERVANT.get(),
                MutantHoglinServantRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.mm.MmEntityRegistry.MUTANT_SHULKER_SERVANT.get(),
                MutantShulkerServantRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.mm.MmEntityRegistry.MUTANT_SHULKER_SERVANT_TRAP.get(),
                MutantShulkerServantTrapRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.mm.MmEntityRegistry.MUTANT_JUNGLE_ZOMBIE_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.mm.MutantJungleZombieServantRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.mm.MmEntityRegistry.MUTANT_HUSK_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.mm.MutantHuskServantRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.mm.MmEntityRegistry.SAND_BOULDER_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.mm.SandBoulderServantRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.mm.MmEntityRegistry.ZOMBIE_RESURRECTION_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.mm.ZombieResurrectionServantRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.mm.MmEntityRegistry.MUTANT_FROZEN_ZOMBIE_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.mm.MutantFrozenZombieServantRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.mm.MmEntityRegistry.ICICLE_SPIKE_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.mm.IcicleSpikeServantRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.mm.MmEntityRegistry.THROWN_ICICLE_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.mm.ThrownIcicleServantRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.mm.MmEntityRegistry.GIANT_SNOWBALL_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.mm.GiantSnowballServantRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.mm.MmEntityRegistry.MUTANT_SHULKER_SERVANT_BULLET.get(),
                com.qiuyue.goetyominous.client.render.projectile.mm.MutantShulkerServantBulletRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.mm.MmEntityRegistry.MUTANT_BLAZE_SERVANT_FIREBALL.get(),
                context -> new ThrownItemRenderer<>(context, 3.0F, true));

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.mm.MmEntityRegistry.MUTANT_BLAZE_SERVANT_ROD_PROJECTILE.get(),
                com.qiuyue.goetyominous.client.render.projectile.mm.MutantBlazeServantRodProjectileRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.mm.MmEntityRegistry.RODLING_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.mm.RodlingServantRenderer::new);
        event.registerEntityRenderer(

                com.qiuyue.goetyominous.common.init.mm.MmEntityRegistry.RODLING_SERVANT_FIREBALL.get(),
                context -> new net.minecraft.client.renderer.entity.ThrownItemRenderer<>(context, 0.5F, true));

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.mm.MmEntityRegistry.MUTANT_BLAZE_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.mm.MutantBlazeServantRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.mm.MmEntityRegistry.GIANT_HELL_BLAST.get(),
                com.qiuyue.goetyominous.client.render.projectile.mm.GiantHellBlastRenderer::new);

        event.registerEntityRenderer(ModSpellControllers.WITHER_BREATH_CONTROLLER.get(), EmptyRenderer::new);
    }
}
