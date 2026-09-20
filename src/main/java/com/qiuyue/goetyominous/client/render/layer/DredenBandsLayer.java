package com.qiuyue.goetyominous.client.render.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.client.render.model.DredenModel;
import com.qiuyue.goetyominous.common.entities.ally.neutral.AbstractDredenEntity;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class DredenBandsLayer<T extends AbstractDredenEntity> extends RenderLayer<T, DredenModel<T>> {
    private static final ResourceLocation TEXTURES =
            new ResourceLocation("goetyominous", "textures/entity/dreden_minion_bands.png");

    private final DredenModel<T> layerModel;

    public DredenBandsLayer(RenderLayerParent<T, DredenModel<T>> renderer, EntityModelSet modelSet) {
        super(renderer);
        this.layerModel = new DredenModel<>(modelSet.bakeLayer(DredenModel.LAYER_LOCATION));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T entity,
                       float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks,
                       float netHeadYaw, float headPitch) {
        if (entity.isHostile()) {
            return;
        }
        coloredCutoutModelCopyLayerRender(this.getParentModel(), this.layerModel, TEXTURES,
                poseStack, buffer, packedLight, entity,
                limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch,
                partialTicks, 1.0F, 1.0F, 1.0F);
    }
}
