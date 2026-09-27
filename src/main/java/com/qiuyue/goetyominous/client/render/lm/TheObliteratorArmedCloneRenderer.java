package com.qiuyue.goetyominous.client.render.lm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.lm.TheObliteratorArmedCloneModel;
import com.qiuyue.goetyominous.common.entities.ally.lm.obliterator.TheObliteratorCloneArmed;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TheObliteratorArmedCloneRenderer extends EntityRenderer<TheObliteratorCloneArmed> {

    private static final ResourceLocation BASE_LAYER = new ResourceLocation(
            "legendary_monsters", "textures/entity/the_warped_one/the_warped_one.png");
    public static final ResourceLocation INNER_LAYER = new ResourceLocation(
            "legendary_monsters", "textures/entity/the_warped_one/ball/inner_ball_layer.png");
    public static final ResourceLocation OUTER_LAYER = new ResourceLocation(
            "legendary_monsters", "textures/entity/the_warped_one/ball/outer_ball_layer.png");

    private final TheObliteratorArmedCloneModel<TheObliteratorCloneArmed> model;

    public TheObliteratorArmedCloneRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new TheObliteratorArmedCloneModel<>(context.bakeLayer(ModEntityLayers.THE_OBLITERATOR_ARMED_CLONE_LAYER));
    }

    @Override
    public void render(TheObliteratorCloneArmed entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        float pitchRad;
        float yawRad;
        float f = Mth.cos(entity.getYRot() * ((float) Math.PI / 180));
        float f12 = Mth.sin(entity.getYRot() * ((float) Math.PI / 180));
        double theta = (double) entity.getYRot() * (Math.PI / 180);
        double vecX = Math.cos(theta += 1.5707963267948966);
        double vecZ = Math.sin(theta);
        float vec = 0.0F;
        float offset = entity.getAnimationState() == 2 || entity.getAnimationState() == 3 ? -1.5F : 1.5F;
        double x = 0.0 + (double) vec * vecX + (double) (f * offset);
        double z = 0.0 + (double) vec * vecZ + (double) (f12 * offset);
        float uniY = 0.25F;
        float yaw = -entity.getYRot() + 90.0F;
        VertexConsumer lightningConsumer = buffer.getBuffer(LmServantRenderTypes.LIGHTNING_NO_CULL);
        float uniA = 0.65F;
        float animationProgress1 = Math.min(entity.RendercontrolledAnim.getAnimationFraction(), 1.0F);
        float f_0_245976 = 1.0F - animationProgress1;
        if (f_0_245976 >= 0.0F && entity.AnimationTicks >= 3) {
            LmServantRenderUtils.renderPivotedQuad(8.0F, 1.75F, x, uniY, z, 90.0D, yaw, 0.0D, lightningConsumer,
                    poseStack, OverlayTexture.NO_OVERLAY, packedLight, 0.25F, 1.0F, 0.25F,
                    Mth.clamp(f_0_245976 - uniA, 0.0F, 1.0F));
        }

        float animationProgress = Math.min(entity.controlledAnim.getAnimationFraction(), 1.0F);
        float f1 = 1.0F - animationProgress;
        double dx = (double) entity.getDestinationX() - entity.getX();
        double dy = (double) entity.getDestinationY() - entity.getY();
        double dz = (double) entity.getDestinationZ() - entity.getZ();
        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
        float yawDeg = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90.0F;
        float horizontal = (float) Math.sqrt(dx * dx + dz * dz);
        float pitchDeg = -((float) Math.toDegrees(Math.atan2(dy, horizontal)));
        float computedYawRad = (float) Math.toRadians(yawDeg);
        float computedPitchRad = (float) Math.toRadians(pitchDeg);
        double stopDistance = 2.0D;
        boolean shouldRotate = dist > stopDistance;
        if (shouldRotate) {
            entity.lastYawToDest = computedYawRad;
            entity.lastPitchToDest = computedPitchRad;
            yawRad = computedYawRad;
            pitchRad = computedPitchRad;
        } else {
            yawRad = entity.lastYawToDest;
            pitchRad = entity.lastPitchToDest;
        }

        poseStack.pushPose();
        float uniformScale = 0.8F;
        poseStack.scale(uniformScale, uniformScale, uniformScale);
        poseStack.mulPose(Axis.YP.rotation(-yawRad + (float) Math.PI));
        poseStack.mulPose(Axis.XP.rotation(-pitchRad));
        poseStack.mulPose(Axis.ZP.rotation((float) Math.PI));
        VertexConsumer translucentConsumer = buffer.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(entity)));
        this.model.setupAnim(entity, 0.0F, 0.0F, (float) entity.tickCount + partialTicks, 0.0F, 0.0F);
        this.model.renderToBuffer(poseStack, translucentConsumer, packedLight, OverlayTexture.NO_OVERLAY,
                1.0F, 1.0F, 1.0F, f1 - 0.3F);
        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(TheObliteratorCloneArmed entity) {
        return BASE_LAYER;
    }
}
