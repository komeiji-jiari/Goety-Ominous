package com.qiuyue.goetyominous.client.events.of;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.of.RamblerServantModel;
import com.qiuyue.goetyominous.compat.mod.OpposingForceCompat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class OfClientEvents {

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        if (!OpposingForceCompat.isOpposingForceLoaded()) {
            return;
        }

        event.registerLayerDefinition(ModEntityLayers.RAMBLER_SERVANT_LAYER,
                RamblerServantModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.DICER_SERVANT_LAYER,
                com.qiuyue.goetyominous.client.render.model.of.DicerServantModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.VOLT_SERVANT_LAYER,
                com.qiuyue.goetyominous.client.render.model.of.VoltServantModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.VOLT_SERVANT_CHARGED_LAYER,
                () -> com.qiuyue.goetyominous.client.render.model.of.VoltServantModel.createBodyLayer(
                        new net.minecraft.client.model.geom.builders.CubeDeformation(1.0F)));

        event.registerLayerDefinition(ModEntityLayers.TREMBLER_SERVANT_LAYER,
                com.qiuyue.goetyominous.client.render.model.of.TremblerServantModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.UMBER_SPIDER_SERVANT_LAYER,
                com.qiuyue.goetyominous.client.render.model.of.UmberSpiderServantModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.TERROR_SERVANT_LAYER,
                com.qiuyue.goetyominous.client.render.model.of.TerrorServantModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.FIRE_SLIME_SERVANT_LAYER,
                com.qiuyue.goetyominous.client.render.model.of.FireSlimeServantModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.GUZZLER_SERVANT_LAYER,
                com.qiuyue.goetyominous.client.render.model.of.GuzzlerServantModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.SKYVERN_SERVANT_LAYER,
                com.qiuyue.goetyominous.client.render.model.of.SkyvernServantHeadModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.SKYVERN_SERVANT_BODY_LAYER,
                com.qiuyue.goetyominous.client.render.model.of.SkyvernServantBodyModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.SKYVERN_SERVANT_TAIL_LAYER,
                com.qiuyue.goetyominous.client.render.model.of.SkyvernServantTailModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        if (!OpposingForceCompat.isOpposingForceLoaded()) {
            return;
        }

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.of.OfEntityRegistry.RAMBLER_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.of.RamblerServantRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.of.OfEntityRegistry.DICER_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.of.DicerServantRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.of.OfEntityRegistry.DICER_SERVANT_LASER.get(),
                com.qiuyue.goetyominous.client.render.of.DicerServantLaserRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.of.OfEntityRegistry.VOLT_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.of.VoltServantRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.of.OfEntityRegistry.VOLT_SERVANT_ELECTRIC_CHARGE.get(),
                com.qiuyue.goetyominous.client.render.of.VoltServantElectricChargeRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.of.OfEntityRegistry.TREMBLER_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.of.TremblerServantRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.of.OfEntityRegistry.UMBER_SPIDER_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.of.UmberSpiderServantRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.of.OfEntityRegistry.TERROR_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.of.TerrorServantRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.of.OfEntityRegistry.FIRE_SLIME_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.of.FireSlimeServantRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.of.OfEntityRegistry.GUZZLER_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.of.GuzzlerServantRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.of.OfEntityRegistry.SKYVERN_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.of.SkyvernServantRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.of.OfEntityRegistry.SKYVERN_SEGMENT_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.of.SkyvernSegmentServantRenderer::new);
    }
}
