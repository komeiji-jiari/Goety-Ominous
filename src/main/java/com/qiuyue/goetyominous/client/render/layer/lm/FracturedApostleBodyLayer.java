package com.qiuyue.goetyominous.client.render.layer.lm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.client.render.model.lm.FracturedApostleServantModel;
import com.qiuyue.goetyominous.common.entities.ally.lm.FracturedApostleServant;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FracturedApostleBodyLayer
        extends RenderLayer<FracturedApostleServant, FracturedApostleServantModel<FracturedApostleServant>> {

    private static final ResourceLocation BODY = new ResourceLocation(
            "legendary_monsters", "textures/entity/fractured_apostle/fractured_apostle_body.png");

    public FracturedApostleBodyLayer(
            RenderLayerParent<FracturedApostleServant, FracturedApostleServantModel<FracturedApostleServant>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                       FracturedApostleServant entity, float limbSwing, float limbSwingAmount,
                       float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity.getAttackState() == 8 && entity.getAttackTicks() < 60) {
            return;
        }
        float alpha = Math.max(1.0F - Math.min(entity.bodyFadeAway.getAnimationFraction(), 1.0F) - 0.5F, 0.0F);
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucentEmissive(BODY));
        this.getParentModel().renderToBuffer(poseStack, consumer, 0xF00000, OverlayTexture.NO_OVERLAY,
                1.0F, 1.0F, 1.0F, alpha);
    }
}
