package com.qiuyue.goetyominous.client.render.of;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.common.entities.projectile.DicerServantLaser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

@OnlyIn(Dist.CLIENT)
public class DicerServantLaserRenderer extends EntityRenderer<DicerServantLaser> {

    private static final ResourceLocation LASER = new ResourceLocation("opposing_force", "textures/entity/dicer/laser.png");
    private static final ResourceLocation ARCH_LASER = new ResourceLocation("opposing_force", "textures/entity/dicer/arch_laser.png");
    private static final float START_RADIUS = 0.7F;
    private static final float BEAM_RADIUS = 0.9F;

    public DicerServantLaserRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull DicerServantLaser entity) {
        return entity.isFiery() ? ARCH_LASER : LASER;
    }

    @Override
    public void render(@NotNull DicerServantLaser laser, float entityYaw, float partialTicks, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource bufferSource, int packedLight) {
        double collidePosX = laser.prevCollidePosX + (laser.collidePosX - laser.prevCollidePosX) * (double) partialTicks;
        double collidePosY = laser.prevCollidePosY + (laser.collidePosY - laser.prevCollidePosY) * (double) partialTicks;
        double collidePosZ = laser.prevCollidePosZ + (laser.collidePosZ - laser.prevCollidePosZ) * (double) partialTicks;
        double posX = laser.xOld + (laser.getX() - laser.xOld) * (double) partialTicks;
        double posY = laser.yOld + (laser.getY() - laser.yOld) * (double) partialTicks;
        double posZ = laser.zOld + (laser.getZ() - laser.zOld) * (double) partialTicks;
        float yaw = laser.prevYaw + (laser.renderYaw - laser.prevYaw) * partialTicks;
        float pitch = laser.prevPitch + (laser.renderPitch - laser.prevPitch) * partialTicks;
        float length = (float) Math.sqrt(Math.pow(collidePosX - posX, 2.0D) + Math.pow(collidePosY - posY, 2.0D)
                + Math.pow(collidePosZ - posZ, 2.0D));
        int frame = Mth.floor(((float) (laser.appear.getTimer() - 1) + partialTicks) * 2.0F);
        if (frame < 0) {
            frame = 6;
        }
        VertexConsumer vertexConsumer = bufferSource.getBuffer(ServantLaserRenderType.laser(this.getTextureLocation(laser)));
        this.renderStart(frame, poseStack, vertexConsumer, packedLight);
        this.renderBeam(length, 57.295776F * yaw, 57.295776F * pitch, frame, poseStack, vertexConsumer, packedLight);
        poseStack.pushPose();
        poseStack.translate(collidePosX - posX, collidePosY - posY, collidePosZ - posZ);
        this.renderEnd(frame, laser.blockSide, poseStack, vertexConsumer, packedLight);
        poseStack.popPose();
    }

    private void renderFlatQuad(int frame, PoseStack poseStack, VertexConsumer consumer, int packedLight) {
        float minU = 0.0F + 0.0625F * (float) frame;
        float minV = 0.0F;
        float maxU = minU + 0.0625F;
        float maxV = minV + 0.5F;
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix4f = pose.pose();
        Matrix3f matrix3f = pose.normal();
        this.drawVertex(matrix4f, matrix3f, consumer, -START_RADIUS, -START_RADIUS, 0.0F, minU, minV, 1.0F, packedLight);
        this.drawVertex(matrix4f, matrix3f, consumer, -START_RADIUS, START_RADIUS, 0.0F, minU, maxV, 1.0F, packedLight);
        this.drawVertex(matrix4f, matrix3f, consumer, START_RADIUS, START_RADIUS, 0.0F, maxU, maxV, 1.0F, packedLight);
        this.drawVertex(matrix4f, matrix3f, consumer, START_RADIUS, -START_RADIUS, 0.0F, maxU, minV, 1.0F, packedLight);
    }

    private void renderStart(int frame, PoseStack poseStack, VertexConsumer consumer, int packedLight) {
        poseStack.pushPose();
        Quaternionf quat = this.entityRenderDispatcher.cameraOrientation();
        poseStack.mulPose(quat);
        this.renderFlatQuad(frame, poseStack, consumer, packedLight);
        poseStack.popPose();
    }

    private void renderEnd(int frame, Direction direction, PoseStack poseStack, VertexConsumer consumer, int packedLight) {
        poseStack.pushPose();
        Quaternionf quat = this.entityRenderDispatcher.cameraOrientation();
        poseStack.mulPose(quat);
        this.renderFlatQuad(frame, poseStack, consumer, packedLight);
        poseStack.popPose();
        if (direction == null) {
            return;
        }
        poseStack.pushPose();
        Quaternionf sideQuat = direction.getRotation();
        sideQuat.mul(quatFromRotationXYZ(90.0F, 0.0F, 0.0F, true));
        poseStack.mulPose(sideQuat);
        poseStack.translate(0.0F, 0.0F, -0.01F);
        this.renderFlatQuad(frame, poseStack, consumer, packedLight);
        poseStack.popPose();
    }

    private void drawBeam(float length, int frame, PoseStack poseStack, VertexConsumer consumer, int packedLight) {
        float minU = 0.0F;
        float minV = 0.5F + 0.03125F * (float) frame;
        float maxU = minU + 0.078125F;
        float maxV = minV + 0.03125F;
        float offset = 0.0F;
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix4f = pose.pose();
        Matrix3f matrix3f = pose.normal();
        this.drawVertex(matrix4f, matrix3f, consumer, -BEAM_RADIUS, offset, 0.0F, minU, minV, 1.0F, packedLight);
        this.drawVertex(matrix4f, matrix3f, consumer, -BEAM_RADIUS, length, 0.0F, minU, maxV, 1.0F, packedLight);
        this.drawVertex(matrix4f, matrix3f, consumer, BEAM_RADIUS, length, 0.0F, maxU, maxV, 1.0F, packedLight);
        this.drawVertex(matrix4f, matrix3f, consumer, BEAM_RADIUS, offset, 0.0F, maxU, minV, 1.0F, packedLight);
    }

    private void renderBeam(float length, float yaw, float pitch, int frame, PoseStack poseStack, VertexConsumer consumer, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(quatFromRotationXYZ(90.0F, 0.0F, 0.0F, true));
        poseStack.mulPose(quatFromRotationXYZ(0.0F, 0.0F, yaw - 90.0F, true));
        poseStack.mulPose(quatFromRotationXYZ(-pitch, 0.0F, 0.0F, true));
        poseStack.pushPose();
        poseStack.mulPose(quatFromRotationXYZ(0.0F, Minecraft.getInstance().gameRenderer.getMainCamera().getXRot() + 90.0F, 0.0F, true));
        this.drawBeam(length, frame, poseStack, consumer, packedLight);
        poseStack.popPose();
        poseStack.pushPose();
        poseStack.mulPose(quatFromRotationXYZ(0.0F, -Minecraft.getInstance().gameRenderer.getMainCamera().getXRot() - 90.0F, 0.0F, true));
        this.drawBeam(length, frame, poseStack, consumer, packedLight);
        poseStack.popPose();
        poseStack.popPose();
    }

    public void drawVertex(Matrix4f matrix, Matrix3f normals, VertexConsumer consumer, float offsetX, float offsetY,
                           float offsetZ, float textureX, float textureY, float alpha, int packedLight) {
        consumer.vertex(matrix, offsetX, offsetY, offsetZ)
                .color(1.0F, 1.0F, 1.0F, 1.0F * alpha)
                .uv(textureX, textureY)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(normals, 0.0F, 1.0F, 0.0F)
                .endVertex();
    }

    public static Quaternionf quatFromRotationXYZ(float x, float y, float z, boolean degrees) {
        if (degrees) {
            x *= (float) Math.PI / 180.0F;
            y *= (float) Math.PI / 180.0F;
            z *= (float) Math.PI / 180.0F;
        }
        return new Quaternionf().rotationXYZ(x, y, z);
    }
}
