package com.qiuyue.goetyominous.client.render.layer.lm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.client.render.model.lm.BeheadedKnightServantModel;
import com.qiuyue.goetyominous.common.entities.ally.lm.BeheadedKnightServant;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BeheadedKnightServantGhostArmLayer
        extends RenderLayer<BeheadedKnightServant, BeheadedKnightServantModel<BeheadedKnightServant>> {

    private static final ResourceLocation GHOST_ARM = new ResourceLocation(
            "legendary_monsters", "textures/entity/beheaded_knight/ghost_arm.png");
    private static final ResourceLocation GHOST_ARM_RED = new ResourceLocation(
            "goetyominous", "textures/entity/lm_knight/beheaded_knight_ghost_arm_red.png");

    public BeheadedKnightServantGhostArmLayer(
            RenderLayerParent<BeheadedKnightServant, BeheadedKnightServantModel<BeheadedKnightServant>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                       BeheadedKnightServant entity, float limbSwing, float limbSwingAmount,
                       float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        float alpha = Math.max(1.0F - Math.min(entity.armFade.getAnimationFraction(), 1.0F) - 0.5F, 0.0F);
        if (!entity.hasGhostArm()) {
            return;
        }

        VertexConsumer consumer = buffer.getBuffer(
                RenderType.entityTranslucentEmissive(entity.isEnhanced() ? GHOST_ARM_RED : GHOST_ARM));
        this.getParentModel().renderToBuffer(poseStack, consumer, 0xF00000, OverlayTexture.NO_OVERLAY,
                1.0F, 1.0F, 1.0F, alpha);
    }
}
