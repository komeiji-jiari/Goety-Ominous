package com.qiuyue.goetyominous.client.events.pp;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.item.TravelerCompassAngleProperty;
import com.qiuyue.goetyominous.client.render.model.pp.PiglinAlchemistServantModel;
import com.qiuyue.goetyominous.client.render.model.pp.PiglinTravelerServantModel;
import com.qiuyue.goetyominous.client.render.pp.PiglinAlchemistServantRenderer;
import com.qiuyue.goetyominous.client.render.pp.PiglinTravelerServantRenderer;
import com.qiuyue.goetyominous.client.render.pp.ZPiglinAlchemistServantRenderer;
import com.qiuyue.goetyominous.client.render.pp.ZPiglinTravelerServantRenderer;
import com.qiuyue.goetyominous.common.init.pp.PpEntityRegistry;
import com.qiuyue.goetyominous.common.items.pp.PpItems;
import com.qiuyue.goetyominous.compat.mod.PiglinProliferationCompat;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class PpClientEvents {

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        if (!PiglinProliferationCompat.isLoaded()) {
            return;
        }
        event.enqueueWork(() -> ItemProperties.register(
                PpItems.TRAVELER_COMPASS.get(),
                new ResourceLocation("angle"),
                TravelerCompassAngleProperty.INSTANCE));
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        if (!PiglinProliferationCompat.isLoaded()) {
            return;
        }

        event.registerLayerDefinition(ModEntityLayers.PIGLIN_ALCHEMIST_SERVANT_LAYER,
                () -> LayerDefinition.create(PiglinAlchemistServantModel.createBodyLayer(
                        new CubeDeformation(0.25F), new CubeDeformation(0.7F), new CubeDeformation(1.05F)), 120, 64));

        event.registerLayerDefinition(ModEntityLayers.PIGLIN_ALCHEMIST_SERVANT_BELT_LAYER,
                () -> LayerDefinition.create(PiglinAlchemistServantModel.createBodyLayer(
                        new CubeDeformation(0.4F), new CubeDeformation(1.0F), new CubeDeformation(1.4F)), 120, 64));

        event.registerLayerDefinition(ModEntityLayers.PIGLIN_TRAVELER_SERVANT_LAYER,
                PiglinTravelerServantModel::createBodyLayer);

        event.registerLayerDefinition(ModEntityLayers.PIGLIN_SERVANT_PP_OUTER_ARMOR_LAYER,
                () -> LayerDefinition.create(HumanoidArmorModel.createBodyLayer(new CubeDeformation(1.3F)), 64, 32));
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        if (!PiglinProliferationCompat.isLoaded()) {
            return;
        }

        event.registerEntityRenderer(PpEntityRegistry.PIGLIN_ALCHEMIST_SERVANT.get(),
                context -> new PiglinAlchemistServantRenderer<>(context));
        event.registerEntityRenderer(PpEntityRegistry.PIGLIN_TRAVELER_SERVANT.get(),
                context -> new PiglinTravelerServantRenderer<>(context));
        event.registerEntityRenderer(PpEntityRegistry.ZPIGLIN_ALCHEMIST_SERVANT.get(),
                ZPiglinAlchemistServantRenderer::new);
        event.registerEntityRenderer(PpEntityRegistry.ZPIGLIN_TRAVELER_SERVANT.get(),
                ZPiglinTravelerServantRenderer::new);
    }
}
