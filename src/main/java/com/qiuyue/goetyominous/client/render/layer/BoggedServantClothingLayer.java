package com.qiuyue.goetyominous.client.render.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.common.entities.ally.mobs.BoggedServant;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class BoggedServantClothingLayer<T extends BoggedServant, M extends EntityModel<T>> extends RenderLayer<T, M> {

    private static final ResourceLocation TEXTURES =
            new ResourceLocation("goetyominous", "textures/entity/bogged_servant_overlay.png");
    private static final ResourceLocation HOSTILE_TEXTURES =
            new ResourceLocation("goetyominous", "textures/entity/bogged_servant_original_overlay.png");

    private final SkeletonModel<T> layerModel;

    public BoggedServantClothingLayer(RenderLayerParent<T, M> renderer, EntityModelSet modelSet) {
        super(renderer);
        this.layerModel = new SkeletonModel<>(modelSet.bakeLayer(ModEntityLayers.BOGGED_SERVANT_OUTER_LAYER));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T entity,
                       float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks,
                       float netHeadYaw, float headPitch) {
        coloredCutoutModelCopyLayerRender(this.getParentModel(), this.layerModel,
                entity.isHostile() ? HOSTILE_TEXTURES : TEXTURES,
                poseStack, buffer, packedLight, entity,
                limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch,
                partialTicks, 1.0F, 1.0F, 1.0F);
    }
}
