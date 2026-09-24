package com.qiuyue.goetyominous.client.render;

import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.layer.BoggedServantClothingLayer;
import com.qiuyue.goetyominous.client.render.model.BoggedServantModel;
import com.qiuyue.goetyominous.common.entities.ally.mobs.BoggedServant;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;

public class BoggedServantRenderer extends HumanoidMobRenderer<BoggedServant, BoggedServantModel<BoggedServant>> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation("goetyominous", "textures/entity/bogged_servant.png");
    private static final ResourceLocation HOSTILE_TEXTURE =
            new ResourceLocation("goetyominous", "textures/entity/bogged_servant_original.png");

    public BoggedServantRenderer(EntityRendererProvider.Context context) {
        super(context, new BoggedServantModel<>(context.bakeLayer(ModEntityLayers.BOGGED_SERVANT_LAYER)), 0.5F);
        this.addLayer(new HumanoidArmorLayer<>(this,
                new HumanoidModel<>(context.bakeLayer(ModelLayers.SKELETON_INNER_ARMOR)),
                new HumanoidModel<>(context.bakeLayer(ModelLayers.SKELETON_OUTER_ARMOR)),
                context.getModelManager()));
        this.addLayer(new BoggedServantClothingLayer<>(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(BoggedServant entity) {
        return entity.isHostile() ? HOSTILE_TEXTURE : TEXTURE;
    }
}
