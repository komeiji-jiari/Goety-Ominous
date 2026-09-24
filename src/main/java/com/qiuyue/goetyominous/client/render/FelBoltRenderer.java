package com.qiuyue.goetyominous.client.render;

import com.Polarice3.Goety.Goety;
import com.Polarice3.Goety.client.render.ModModelLayer;
import com.Polarice3.Goety.client.render.model.SoulBoltModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.common.entities.projectile.FelBolt;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class FelBoltRenderer extends EntityRenderer<FelBolt> {
    private static final ResourceLocation TRAIL = Goety.location("textures/entity/projectiles/trail.png");
    private final SoulBoltModel<FelBolt> model;

    public FelBoltRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new SoulBoltModel<>(context.bakeLayer(ModModelLayer.SOUL_BOLT));
    }

    @Override
    protected int getBlockLightLevel(FelBolt entity, BlockPos pos) {
        return 15;
    }

    @Override
    public void render(FelBolt entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.scale(-1.25F, -1.25F, 1.25F);
        float f = Mth.rotLerp(partialTicks, entity.yRotO, entity.getYRot());
        float f1 = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());
        VertexConsumer consumer = buffer.getBuffer(RenderType.eyes(this.getTextureLocation(entity)));
        this.model.setupAnim(0.0F, f, f1);
        this.model.renderToBuffer(poseStack, consumer, 15728640, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 0.5F);
        poseStack.popPose();
        if (entity.hasTrail()) {
            double x = Mth.lerp((double) partialTicks, entity.xo, entity.getX());
            double y = Mth.lerp((double) partialTicks, entity.yo, entity.getY());
            double z = Mth.lerp((double) partialTicks, entity.zo, entity.getZ());
            poseStack.pushPose();
            poseStack.translate(-x, -y, -z);
            this.renderTrail(entity, partialTicks, poseStack, buffer, 0.66F, 0.2F, 0.86F, 0.6F, packedLight);
            poseStack.popPose();
        }
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private void renderTrail(FelBolt entity, float partialTicks, PoseStack poseStack, MultiBufferSource buffer,
                             float red, float green, float blue, float alpha, int packedLight) {
        int samples = 0;
        int sampleSize = 1;
        double trailHeight = 0.25D;
        float trailZRot = 0.0F;
        Vec3 topAngleVec = new Vec3(0.0D, trailHeight, 0.0D).xRot(trailZRot);
        Vec3 bottomAngleVec = new Vec3(0.0D, -trailHeight, 0.0D).xRot(trailZRot);
        Vec3 drawFrom = entity.getTrailPosition(0, partialTicks);

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucent(TRAIL));
        Vec3 sample;
        while (samples < sampleSize) {
            sample = entity.getTrailPosition(samples + 8, partialTicks);
            PoseStack.Pose pose = poseStack.last();
            Matrix4f matrix4f = pose.pose();
            Matrix3f matrix3f = pose.normal();
            float f2 = (float) (entity.tickCount % 8) / 8.0F;
            float f3 = f2 + 0.5F;
            consumer.vertex(matrix4f, (float) drawFrom.x + (float) bottomAngleVec.x, (float) drawFrom.y + (float) bottomAngleVec.y, (float) drawFrom.z + (float) bottomAngleVec.z).color(red, green, blue, alpha).uv(f2, 1.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            consumer.vertex(matrix4f, (float) sample.x + (float) bottomAngleVec.x, (float) sample.y + (float) bottomAngleVec.y, (float) sample.z + (float) bottomAngleVec.z).color(red, green, blue, alpha).uv(f3, 1.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            consumer.vertex(matrix4f, (float) sample.x + (float) topAngleVec.x, (float) sample.y + (float) topAngleVec.y, (float) sample.z + (float) topAngleVec.z).color(red, green, blue, alpha).uv(f3, 0.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            consumer.vertex(matrix4f, (float) drawFrom.x + (float) topAngleVec.x, (float) drawFrom.y + (float) topAngleVec.y, (float) drawFrom.z + (float) topAngleVec.z).color(red, green, blue, alpha).uv(f2, 0.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            ++samples;
            drawFrom = sample;
        }
    }

    @Override
    public ResourceLocation getTextureLocation(FelBolt entity) {
        return entity.getResourceLocation();
    }
}
