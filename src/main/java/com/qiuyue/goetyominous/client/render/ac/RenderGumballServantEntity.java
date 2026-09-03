package com.qiuyue.goetyominous.client.render.ac;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.common.entities.projectile.GumballServantEntity;
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

/**
 * 糖球弹丸渲染:逐字移植 AC GumballRenderer —— 始终面向摄像机的公告板四边形,
 * 按随机色号取 11 色 gumball_*.png;EXPLOSIVE 形态随引爆进度放大并在顶点闪烁 explode 贴图。
 */
@OnlyIn(Dist.CLIENT)
public class RenderGumballServantEntity extends EntityRenderer<GumballServantEntity> {

    private static final ResourceLocation[] TEXTURES = new ResourceLocation[11];

    private static final ResourceLocation TEXTURE_EXPLODING = new ResourceLocation("alexscaves:textures/entity/gumball/gumball_exploding.png");

    static {
        for (int i = 0; i < TEXTURES.length; i++) {
            TEXTURES[i] = new ResourceLocation("alexscaves:textures/entity/gumball/gumball_" + i + ".png");
        }
    }

    public RenderGumballServantEntity(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(GumballServantEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        float explodeAmount = entity.getExplodeProgress(partialTicks);
        float scale = entity.isExplosive() ? 0.5F + explodeAmount * 0.2F : 0.25F;
        poseStack.scale(scale, scale, scale);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        PoseStack.Pose posestack$pose = poseStack.last();
        Matrix4f matrix4f = posestack$pose.pose();
        Matrix3f matrix3f = posestack$pose.normal();
        VertexConsumer vertexconsumer = bufferSource.getBuffer(RenderType.entityCutout(this.getTextureLocation(entity)));
        vertex(vertexconsumer, matrix4f, matrix3f, packedLight, 0.0F, 0, 0, 1, 1.0F);
        vertex(vertexconsumer, matrix4f, matrix3f, packedLight, 1.0F, 0, 1, 1, 1.0F);
        vertex(vertexconsumer, matrix4f, matrix3f, packedLight, 1.0F, 1, 1, 0, 1.0F);
        vertex(vertexconsumer, matrix4f, matrix3f, packedLight, 0.0F, 1, 0, 0, 1.0F);
        if (entity.isExplosive()) {
            float explodeColorChange = entity.getBounces() >= entity.getMaximumBounces() ? 1.0F - 0.5F * (1.0F + Mth.sin((entity.tickCount + partialTicks) * 0.9F)) : 0.0F;
            VertexConsumer vertexconsumer2 = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(TEXTURE_EXPLODING));
            vertex(vertexconsumer2, matrix4f, matrix3f, packedLight, 0.0F, 0, 0, 1, explodeColorChange);
            vertex(vertexconsumer2, matrix4f, matrix3f, packedLight, 1.0F, 0, 1, 1, explodeColorChange);
            vertex(vertexconsumer2, matrix4f, matrix3f, packedLight, 1.0F, 1, 1, 0, explodeColorChange);
            vertex(vertexconsumer2, matrix4f, matrix3f, packedLight, 0.0F, 1, 0, 0, explodeColorChange);
        }
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }

    private static void vertex(VertexConsumer vertexConsumer, Matrix4f matrix4f, Matrix3f matrix3f, int packedLight, float x, int y, int u, int v, float alpha) {
        vertexConsumer.vertex(matrix4f, x - 0.5F, (float) y - 0.25F, 0.0F).color(1.0F, 1.0F, 1.0F, alpha).uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(GumballServantEntity entity) {
        return TEXTURES[Mth.clamp(entity.getColor(), 0, TEXTURES.length - 1)];
    }
}
