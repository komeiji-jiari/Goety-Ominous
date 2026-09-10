package com.qiuyue.goetyominous.client.render.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.common.entities.projectile.FrostBallEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class FrostBallRenderer extends EntityRenderer<FrostBallEntity> {
    public FrostBallRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(FrostBallEntity entity, float yaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.scale(1.0F, 1.0F, 1.0F);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180.0F));
        PoseStack.Pose pose = poseStack.last();
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(this.getTextureLocation(entity)));
        quad(consumer, pose, packedLight, 0.0F, 0, 0, 1);
        quad(consumer, pose, packedLight, 1.0F, 0, 1, 1);
        quad(consumer, pose, packedLight, 1.0F, 1, 1, 0);
        quad(consumer, pose, packedLight, 0.0F, 1, 0, 0);
        poseStack.popPose();
        super.render(entity, yaw, partialTicks, poseStack, buffer, packedLight);
    }

    private static void quad(VertexConsumer consumer, PoseStack.Pose pose, int light,
                             float u, int x, int v, int z) {
        consumer.vertex(pose.pose(), u - 0.5F, (float) x - 0.25F, 0.0F)
                .color(255, 255, 255, 255)
                .uv(v, z)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(pose.normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(FrostBallEntity entity) {
        return entity.getResourceLocation();
    }
}
