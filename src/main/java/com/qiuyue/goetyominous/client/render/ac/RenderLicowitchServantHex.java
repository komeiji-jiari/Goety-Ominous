package com.qiuyue.goetyominous.client.render.ac;

import com.github.alexmodguy.alexscaves.client.ClientProxy;
import com.github.alexmodguy.alexscaves.client.render.ACRenderTypes;
import com.github.alexthe666.citadel.client.shader.PostEffectRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.common.entities.projectile.LicowitchServantHex;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class RenderLicowitchServantHex extends EntityRenderer<LicowitchServantHex> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("alexscaves", "textures/entity/sugar_staff_hex.png");

    public RenderLicowitchServantHex(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(LicowitchServantHex entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource multiBufferSource, int packedLight) {
        PostEffectRegistry.renderEffectForNextTick(ClientProxy.PURPLE_WITCH_SHADER);
        poseStack.pushPose();
        float despawnsIn = entity.getDespawnTime(partialTicks);
        float randomRotation = entity.getId() % 4 * 90;
        float randomYOffset = entity.getYRenderOffset();
        float tickCount = (float) entity.tickCount + partialTicks;
        float alpha = Math.min(1.0F, Math.min(tickCount, despawnsIn) / 10.0F);
        float alpha2 = alpha * alpha;
        float scale = 4.0F * entity.getHexScale();
        poseStack.translate(0.0D, randomYOffset, 0.0D);
        poseStack.mulPose(Axis.YP.rotationDegrees(randomRotation + 3.0F * tickCount));
        poseStack.scale(scale, scale, scale);
        renderHex(poseStack, multiBufferSource, ACRenderTypes.getPurpleWitch(TEXTURE), 1.0F, 1.0F, 1.0F - alpha2, 1.0F);
        for (int i = 0; i < 5; ++i) {
            float f = (1.0F - (float) i / 5.0F) * 0.5F;
            float bob = (float) (Math.sin(tickCount * 0.2D + (double) i) * 0.005D);
            renderHex(poseStack, multiBufferSource, ACRenderTypes.getVoidBeingCloud(TEXTURE), 1.0F * f, 1.0F, 1.0F - alpha2 * f, 1.0F);
            poseStack.translate(0.0D, 0.01D + (double) bob + (double) (1.0F - alpha2) * 0.03D, 0.0D);
        }
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, multiBufferSource, packedLight);
    }

    private static void renderHex(PoseStack poseStack, MultiBufferSource multiBufferSource, RenderType renderType,
                                  float alpha, float r, float g, float b) {
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix4f = pose.pose();
        Matrix3f matrix3f = pose.normal();
        VertexConsumer vertexconsumer = multiBufferSource.getBuffer(renderType);
        vertex(vertexconsumer, matrix4f, matrix3f, 240, 0.0F, 0, 0, 1, alpha, r, g, b);
        vertex(vertexconsumer, matrix4f, matrix3f, 240, 1.0F, 0, 1, 1, alpha, r, g, b);
        vertex(vertexconsumer, matrix4f, matrix3f, 240, 1.0F, 1, 1, 0, alpha, r, g, b);
        vertex(vertexconsumer, matrix4f, matrix3f, 240, 0.0F, 1, 0, 0, alpha, r, g, b);
    }

    private static void vertex(VertexConsumer consumer, Matrix4f matrix4f, Matrix3f matrix3f, int light,
                               float x, int y, int u, int v, float alpha, float r, float g, float b) {
        consumer.vertex(matrix4f, x - 0.5F, 0.01F, (float) y - 0.5F).color(r, g, b, alpha)
                .uv(u, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light)
                .normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(LicowitchServantHex entity) {
        return TEXTURE;
    }
}
