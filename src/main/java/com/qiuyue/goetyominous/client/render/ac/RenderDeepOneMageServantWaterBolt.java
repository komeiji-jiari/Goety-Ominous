package com.qiuyue.goetyominous.client.render.ac;

import com.github.alexmodguy.alexscaves.client.render.ACRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.client.render.model.ac.ModelDeepOneMageServantWaterBolt;
import com.qiuyue.goetyominous.common.entities.projectile.DeepOneMageServantWaterBolt;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class RenderDeepOneMageServantWaterBolt extends EntityRenderer<DeepOneMageServantWaterBolt> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("alexscaves", "textures/entity/deep_one/water_bolt.png");
    private static final ResourceLocation OVERLAY_TEXTURE = new ResourceLocation("alexscaves", "textures/entity/deep_one/water_bolt_overlay.png");
    private static final ResourceLocation TRAIL_TEXTURE = new ResourceLocation("alexscaves", "textures/particle/trail.png");
    private static final ModelDeepOneMageServantWaterBolt MODEL = new ModelDeepOneMageServantWaterBolt();

    public RenderDeepOneMageServantWaterBolt(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(DeepOneMageServantWaterBolt entity, float entityYaw, float partialTicks, PoseStack matrixStack,
                       MultiBufferSource buffer, int packedLight) {
        int waterColor = entity.level().getBiome(entity.blockPosition()).get().getWaterColor();
        float colorR = (float) (waterColor >> 16 & 255) / 255.0F;
        float colorG = (float) (waterColor >> 8 & 255) / 255.0F;
        float colorB = (float) (waterColor & 255) / 255.0F;
        matrixStack.pushPose();
        matrixStack.translate(0.0D, 0.25D, 0.0D);
        matrixStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) - 180.0F));
        matrixStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTicks, entity.xRotO, entity.getXRot())));
        MODEL.setupAnim(entity, 0.0F, 0.0F, (float) entity.tickCount + partialTicks, 0.0F, 0.0F);
        VertexConsumer vertexConsumer = buffer.getBuffer(ACRenderTypes.getBubbledNoCull(TEXTURE));
        MODEL.renderToBuffer(matrixStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, colorR, colorG, colorB, 1.0F);
        VertexConsumer vertexConsumer2 = buffer.getBuffer(ACRenderTypes.getBubbledNoCull(OVERLAY_TEXTURE));
        MODEL.renderToBuffer(matrixStack, vertexConsumer2, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        matrixStack.popPose();
        if (entity.hasTrail()) {
            double lerpX = Mth.lerp((double) partialTicks, entity.xOld, entity.getX());
            double lerpY = Mth.lerp((double) partialTicks, entity.yOld, entity.getY());
            double lerpZ = Mth.lerp((double) partialTicks, entity.zOld, entity.getZ());
            matrixStack.pushPose();
            matrixStack.translate(-lerpX, -lerpY, -lerpZ);
            this.renderTrail(entity, partialTicks, matrixStack, buffer, colorR, colorG, colorB, 0.6F, packedLight);
            matrixStack.popPose();
        }
        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
    }

    private void renderTrail(DeepOneMageServantWaterBolt entity, float partialTicks, PoseStack matrixStack,
                             MultiBufferSource buffer, float colorR, float colorG, float colorB, float alpha, int packedLight) {
        Vec3 vec3 = new Vec3(0.0D, 0.5D, 0.0D).zRot(0.0F);
        Vec3 vec31 = new Vec3(0.0D, -0.5D, 0.0D).zRot(0.0F);
        Vec3 prevPos = entity.getTrailPosition(0, partialTicks);
        VertexConsumer vertexConsumer = buffer.getBuffer(ACRenderTypes.getBubbledNoCull(TRAIL_TEXTURE));
        for (int i = 0; i < 10; i++) {
            Vec3 nextPos = entity.getTrailPosition(i + 2, partialTicks);
            float u0 = (float) i / 10.0F;
            float u1 = u0 + 1.0F / 10.0F;
            PoseStack.Pose pose = matrixStack.last();
            Matrix4f matrix4f = pose.pose();
            Matrix3f matrix3f = pose.normal();
            vertexConsumer.vertex(matrix4f, (float) (prevPos.x + vec31.x), (float) (prevPos.y + vec31.y), (float) (prevPos.z + vec31.z))
                    .color(colorR, colorG, colorB, alpha).uv(u0, 1.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight)
                    .normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            vertexConsumer.vertex(matrix4f, (float) (nextPos.x + vec31.x), (float) (nextPos.y + vec31.y), (float) (nextPos.z + vec31.z))
                    .color(colorR, colorG, colorB, alpha).uv(u1, 1.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight)
                    .normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            vertexConsumer.vertex(matrix4f, (float) (nextPos.x + vec3.x), (float) (nextPos.y + vec3.y), (float) (nextPos.z + vec3.z))
                    .color(colorR, colorG, colorB, alpha).uv(u1, 0.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight)
                    .normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            vertexConsumer.vertex(matrix4f, (float) (prevPos.x + vec3.x), (float) (prevPos.y + vec3.y), (float) (prevPos.z + vec3.z))
                    .color(colorR, colorG, colorB, alpha).uv(u0, 0.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight)
                    .normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            prevPos = nextPos;
        }
    }

    @Override
    public ResourceLocation getTextureLocation(DeepOneMageServantWaterBolt entity) {
        return TEXTURE;
    }
}
