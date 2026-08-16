package com.qiuyue.goetyominous.client.render.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.common.entities.projectile.IceShard;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class RenderIceShard extends EntityRenderer<IceShard> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs", "textures/entity/ice_shard.png");

    public RenderIceShard(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(IceShard shard, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int light) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, shard.yRotO, shard.getYRot()) - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, shard.xRotO, shard.getXRot())));
        float f = 0.0F;
        if (f > 0.0F) {
            float f1 = -Mth.sin(f * 3.0F) * f;
            poseStack.mulPose(Axis.ZP.rotationDegrees(f1));
        }
        poseStack.mulPose(Axis.YP.rotationDegrees(45.0F));
        poseStack.scale(0.05625F, 0.05625F, 0.05625F);
        poseStack.translate(-4.0D, 0.0D, 0.0D);
        VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.entityCutout(this.getTextureLocation(shard)));
        PoseStack.Pose posestack$pose = poseStack.last();
        Matrix4f matrix4f = posestack$pose.pose();
        Matrix3f matrix3f = posestack$pose.normal();
        this.drawVertex(matrix4f, matrix3f, vertexconsumer, -7, -2, -2, 0.15625F, 0.0F, -1, 0, 0, light);
        this.drawVertex(matrix4f, matrix3f, vertexconsumer, -7, -2, 2, 0.15625F, 0.15625F, -1, 0, 0, light);
        this.drawVertex(matrix4f, matrix3f, vertexconsumer, -7, 2, 2, 0.15625F, 0.3125F, -1, 0, 0, light);
        this.drawVertex(matrix4f, matrix3f, vertexconsumer, -7, 2, -2, 0.3125F, 0.15625F, -1, 0, 0, light);
        this.drawVertex(matrix4f, matrix3f, vertexconsumer, -7, 2, -2, 0.15625F, 0.0F, 1, 0, 0, light);
        this.drawVertex(matrix4f, matrix3f, vertexconsumer, -7, 2, 2, 0.15625F, 0.15625F, 1, 0, 0, light);
        this.drawVertex(matrix4f, matrix3f, vertexconsumer, -7, -2, 2, 0.15625F, 0.3125F, 1, 0, 0, light);
        this.drawVertex(matrix4f, matrix3f, vertexconsumer, -7, -2, -2, 0.3125F, 0.15625F, 1, 0, 0, light);
        int i = 0;
        while (i < 4) {
            poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
            this.drawVertex(matrix4f, matrix3f, vertexconsumer, -8, -2, 0, 0.0F, 0.0F, 0, 1, 0, light);
            this.drawVertex(matrix4f, matrix3f, vertexconsumer, 8, -2, 0, 0.5F, 0.0F, 0, 1, 0, light);
            this.drawVertex(matrix4f, matrix3f, vertexconsumer, 8, 2, 0, 0.5F, 0.15625F, 0, 1, 0, light);
            this.drawVertex(matrix4f, matrix3f, vertexconsumer, -8, 2, 0, 0.15625F, 0.0F, 0, 1, 0, light);
            i++;
        }
        poseStack.popPose();
        super.render(shard, entityYaw, partialTicks, poseStack, buffer, light);
    }

    private void drawVertex(Matrix4f matrix, Matrix3f normal, VertexConsumer builder, int x, int y, int z, float u, float v, int nx, int ny, int nz, int light) {
        builder.vertex(matrix, (float)x, (float)y, (float)z).color(255, 255, 255, 255)
                .uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light)
                .normal(normal, (float)nx, (float)ny, (float)nz).endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(IceShard entity) {
        return TEXTURE;
    }
}
