package com.qiuyue.goetyominous.client.render;

import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.layer.BoggedClothingLayer;
import com.qiuyue.goetyominous.client.render.model.BoggedModel;
import com.qiuyue.goetyominous.common.entities.ally.mobs.BoggedServant;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;

public class BoggedServantRenderer extends HumanoidMobRenderer<BoggedServant, BoggedModel<BoggedServant>> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation("goetyominous", "textures/entity/bogged_servant.png");
    private static final ResourceLocation OVERLAY =
            new ResourceLocation("goetyominous", "textures/entity/bogged_servant_overlay.png");

    public BoggedServantRenderer(EntityRendererProvider.Context context) {
        super(context, new BoggedModel<>(context.bakeLayer(ModEntityLayers.BOGGED_LAYER)), 0.5F);
        this.addLayer(new HumanoidArmorLayer<>(this,
                new HumanoidModel<>(context.bakeLayer(ModelLayers.SKELETON_INNER_ARMOR)),
                new HumanoidModel<>(context.bakeLayer(ModelLayers.SKELETON_OUTER_ARMOR)),
                context.getModelManager()));
        this.addLayer(new BoggedClothingLayer<>(this, context.getModelSet(),
                entity -> entity.isHostile() ? BoggedRenderer.OVERLAY : OVERLAY));
    }

    @Override
    public ResourceLocation getTextureLocation(BoggedServant entity) {
        return entity.isHostile() ? BoggedRenderer.TEXTURE : TEXTURE;
    }
}
