package com.qiuyue.goetyominous.client.render.layer.lm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.client.render.model.lm.ResurrectedKnightServantModel;
import com.qiuyue.goetyominous.common.entities.ally.lm.ResurrectedKnightServant;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ResurrectedKnightServantBodyLayer
        extends RenderLayer<ResurrectedKnightServant, ResurrectedKnightServantModel<ResurrectedKnightServant>> {

    private static final ResourceLocation GHOST_BODY = new ResourceLocation(
            "legendary_monsters", "textures/entity/resurrected_knight/ghost_body.png");
    private static final ResourceLocation GHOST_BODY_RED = new ResourceLocation(
            "goetyominous", "textures/entity/lm_knight/resurrected_knight_ghost_body_red.png");

    public ResurrectedKnightServantBodyLayer(
            RenderLayerParent<ResurrectedKnightServant, ResurrectedKnightServantModel<ResurrectedKnightServant>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                       ResurrectedKnightServant entity, float limbSwing, float limbSwingAmount,
                       float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        float alpha = Math.max(1.0F - Math.min(entity.bodyFadeAway.getAnimationFraction(), 1.0F) - 0.5F, 0.0F);
        if (entity.getAttackState() == 8 && entity.getAttackTicks() >= 60) {
            return;
        }

        VertexConsumer consumer = buffer.getBuffer(
                RenderType.entityTranslucentEmissive(entity.isEnhanced() ? GHOST_BODY_RED : GHOST_BODY));
        this.getParentModel().renderToBuffer(poseStack, consumer, 0xF00000, OverlayTexture.NO_OVERLAY,
                1.0F, 1.0F, 1.0F, alpha);
    }
}
