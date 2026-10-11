package com.qiuyue.goetyominous.client.events.ua;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.compat.mod.UpgradeAquaticCompat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class UaClientEvents {

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        if (!UpgradeAquaticCompat.isUpgradeAquaticLoaded()) {
            return;
        }

        event.registerLayerDefinition(ModEntityLayers.THRASHER_SERVANT_LAYER,
                com.qiuyue.goetyominous.client.render.model.ua.ThrasherServantModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.GREAT_THRASHER_SERVANT_LAYER,
                com.qiuyue.goetyominous.client.render.model.ua.ThrasherServantModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.FLARE_SERVANT_LAYER,
                com.qiuyue.goetyominous.client.render.model.ua.FlareServantModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        if (!UpgradeAquaticCompat.isUpgradeAquaticLoaded()) {
            return;
        }

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.ua.UaEntityRegistry.THRASHER_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.ua.ThrasherServantRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.ua.UaEntityRegistry.GREAT_THRASHER_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.ua.ThrasherServantRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.ua.UaEntityRegistry.FLARE_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.ua.FlareServantRenderer::new);
    }
}
