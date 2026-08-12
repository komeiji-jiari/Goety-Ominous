package com.qiuyue.goetyominous.client.render.layer.mm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.client.render.model.mm.MutantWitherSkeletonServantModel;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.MutantWitherSkeletonServant;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class WitherShieldLayer extends RenderLayer<MutantWitherSkeletonServant, MutantWitherSkeletonServantModel<MutantWitherSkeletonServant>> {
    private static final ResourceLocation WITHER_ARMOR_LOCATION = new ResourceLocation("textures/entity/wither/wither_armor.png");

    public WitherShieldLayer(RenderLayerParent<MutantWitherSkeletonServant, MutantWitherSkeletonServantModel<MutantWitherSkeletonServant>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                       MutantWitherSkeletonServant servant, float limbSwing, float limbSwingAmount,
                       float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!servant.isSoulShieldActive()) {
            return;
        }
        float f = (float) servant.tickCount + partialTicks;
        VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.energySwirl(WITHER_ARMOR_LOCATION, f * 0.01F, f * 0.01F));
        this.getParentModel().renderToBuffer(poseStack, vertexconsumer, packedLight,
                OverlayTexture.NO_OVERLAY, 0.5F, 0.5F, 0.5F, 1.0F);
    }
}
