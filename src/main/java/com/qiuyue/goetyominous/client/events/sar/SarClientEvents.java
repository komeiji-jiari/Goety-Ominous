package com.qiuyue.goetyominous.client.events.sar;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.compat.mod.SavageRavageCompat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SarClientEvents {

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        if (!SavageRavageCompat.isSavageRavageLoaded()) {
            return;
        }

        event.registerLayerDefinition(ModEntityLayers.CREEPIE_SERVANT_LAYER,
                () -> com.qiuyue.goetyominous.client.render.model.sar.CreepieServantModel.createBodyLayer(
                        net.minecraft.client.model.geom.builders.CubeDeformation.NONE));

        event.registerLayerDefinition(ModEntityLayers.GRIEFER_SERVANT_LAYER,
                com.qiuyue.goetyominous.client.render.model.sar.GrieferServantModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.VILLAGER_INNER_ARMOR_LAYER,
                com.qiuyue.goetyominous.client.render.model.sar.VillagerArmorModel::createInnerArmorLayer);

        event.registerLayerDefinition(ModEntityLayers.VILLAGER_OUTER_ARMOR_LAYER,
                com.qiuyue.goetyominous.client.render.model.sar.VillagerArmorModel::createOuterArmorLayer);

        event.registerLayerDefinition(ModEntityLayers.EXECUTIONER_SERVANT_LAYER,
                com.qiuyue.goetyominous.client.render.model.sar.ExecutionerServantModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.SKELETON_VILLAGER_SERVANT_LAYER,
                com.qiuyue.goetyominous.client.render.model.sar.SkeletonVillagerServantModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.RUNE_PRISON_LAYER,
                com.qiuyue.goetyominous.client.render.model.sar.RunePrisonModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.TRICKSTER_SERVANT_LAYER,
                com.qiuyue.goetyominous.client.render.model.sar.TricksterServantModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        if (!SavageRavageCompat.isSavageRavageLoaded()) {
            return;
        }

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.sar.SarEntityRegistry.CREEPIE_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.sar.CreepieServantRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.sar.SarEntityRegistry.SPORE_CLOUD.get(),
                com.qiuyue.goetyominous.client.render.sar.NoModelRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.sar.SarEntityRegistry.GRIEFER_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.sar.GrieferServantRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.sar.SarEntityRegistry.EXECUTIONER_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.sar.ExecutionerServantRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.sar.SarEntityRegistry.SKELETON_VILLAGER_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.sar.SkeletonVillagerServantRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.sar.SarEntityRegistry.RUNE_PRISON.get(),
                com.qiuyue.goetyominous.client.render.sar.RunePrisonRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.sar.SarEntityRegistry.CONFUSION_BOLT.get(),
                com.qiuyue.goetyominous.client.render.sar.NoModelRenderer::new);

        event.registerEntityRenderer(
                com.qiuyue.goetyominous.common.init.sar.SarEntityRegistry.TRICKSTER_SERVANT.get(),
                com.qiuyue.goetyominous.client.render.sar.TricksterServantRenderer::new);
    }
}
