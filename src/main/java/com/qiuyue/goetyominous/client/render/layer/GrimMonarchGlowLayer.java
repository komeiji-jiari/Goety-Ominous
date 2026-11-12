package com.qiuyue.goetyominous.client.render.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.render.GrimMonarchEchoRenderTypes;
import com.qiuyue.goetyominous.client.render.GrimMonarchRenderer;
import com.qiuyue.goetyominous.client.render.model.GrimMonarchModel;
import com.qiuyue.goetyominous.common.entities.hostile.grimmonarch.GrimMonarchEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class GrimMonarchGlowLayer extends RenderLayer<GrimMonarchEntity, GrimMonarchModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(
            GoetyOminous.MOD_ID, "textures/entity/grim_monarch_glow.png");
    private static final RenderType GLOW = RenderType.eyes(TEXTURE);

    public GrimMonarchGlowLayer(RenderLayerParent<GrimMonarchEntity, GrimMonarchModel> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight,
                       GrimMonarchEntity entity, float limbSwing, float limbSwingAmount,
                       float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity.isInvisible()) {
            return;
        }
        float pulse = 0.5F + 0.5F * Mth.sin(ageInTicks * (float) (Math.PI * 2.0D / 100.0D));
        float brightness = 0.15F + 0.85F * pulse;
        if (entity.isEcho()) {
            VertexConsumer echoConsumer = bufferSource.getBuffer(
                    GrimMonarchEchoRenderTypes.glow(GrimMonarchRenderer.ECHO_TEXTURE));
            this.getParentModel().renderToBuffer(poseStack, echoConsumer, 0xF000F0,
                    OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            return;
        }
        VertexConsumer consumer = bufferSource.getBuffer(GLOW);
        this.getParentModel().renderToBuffer(poseStack, consumer, 0xF000F0,
                OverlayTexture.NO_OVERLAY, brightness, brightness, brightness, 1.0F);
    }
}
