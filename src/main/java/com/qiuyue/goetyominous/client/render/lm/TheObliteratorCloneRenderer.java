package com.qiuyue.goetyominous.client.render.lm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.lm.TheObliteratorCloneModel;
import com.qiuyue.goetyominous.common.entities.ally.lm.obliterator.TheObliteratorClone;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class TheObliteratorCloneRenderer extends EntityRenderer<TheObliteratorClone> {

    private static final ResourceLocation BASE_LAYER = new ResourceLocation(
            "legendary_monsters", "textures/entity/the_warped_one/the_warped_one.png");
    public static final ResourceLocation INNER_LAYER = new ResourceLocation(
            "legendary_monsters", "textures/entity/the_warped_one/ball/inner_ball_layer.png");
    public static final ResourceLocation OUTER_LAYER = new ResourceLocation(
            "legendary_monsters", "textures/entity/the_warped_one/ball/outer_ball_layer.png");

    private final TheObliteratorCloneModel<TheObliteratorClone> model;

    public TheObliteratorCloneRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new TheObliteratorCloneModel<>(context.bakeLayer(ModEntityLayers.THE_OBLITERATOR_CLONE_LAYER));
    }

    @Override
    public void render(TheObliteratorClone entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        float f = Mth.cos(entity.getYRot() * ((float) Math.PI / 180));
        float f12 = Mth.sin(entity.getYRot() * ((float) Math.PI / 180));
        double theta = (double) entity.getYRot() * (Math.PI / 180);
        double vecX = Math.cos(theta += 1.5707963267948966);
        double vecZ = Math.sin(theta);
        float vec = 0.0F;
        float offset = 0.0F;
        double x = 0.0 + (double) vec * vecX + (double) (f * offset);
        double z = 0.0 + (double) vec * vecZ + (double) (f12 * offset);
        float yaw = -entity.getYRot() + 90.0F;
        VertexConsumer lightningConsumer = buffer.getBuffer(LmServantRenderTypes.LIGHTNING_NO_CULL);
        float uniA = 0.65F;
        float animationProgress1 = Math.min(entity.RendercontrolledAnim.getAnimationFraction(), 1.0F);
        float f_0_245976 = 1.0F - animationProgress1;
        float uniY = 1.0F;
        if (f_0_245976 >= 0.0F && entity.AnimationTicks >= 3) {
            LmServantRenderUtils.renderPivotedQuad(8.0F, 0.35F, x, uniY, z, 90.0D, yaw, 0.0D, lightningConsumer,
                    poseStack, OverlayTexture.NO_OVERLAY, packedLight, 0.25F, 1.0F, 0.25F,
                    Mth.clamp(f_0_245976 - uniA, 0.0F, 1.0F));
        }
        poseStack.popPose();

        double dx = (double) entity.getDestinationX() - entity.getX();
        double dy = (double) entity.getDestinationY() - entity.getY();
        double dz = (double) entity.getDestinationZ() - entity.getZ();
        float yawDeg = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90.0F;
        float horizontal = (float) Math.sqrt(dx * dx + dz * dz);
        float pitchDeg = -((float) Math.toDegrees(Math.atan2(dy, horizontal)));
        float yawRad = (float) Math.toRadians(yawDeg);
        float pitchRad = (float) Math.toRadians(pitchDeg);
        float fadeProgress = Math.min(entity.fade.getAnimationFraction(), 1.0F);

        poseStack.pushPose();
        float uniformScale = 0.8F;
        poseStack.scale(uniformScale, uniformScale, uniformScale);
        poseStack.mulPose(Axis.YP.rotation(-yawRad + (float) Math.PI));
        poseStack.mulPose(Axis.XP.rotation(-pitchRad));
        poseStack.mulPose(Axis.ZP.rotation((float) Math.PI));

        Minecraft minecraft = Minecraft.getInstance();
        boolean flag2 = minecraft.shouldEntityAppearGlowing(entity);
        RenderType renderType = this.getRenderType(entity, flag2);
        if (renderType != null) {
            VertexConsumer outlineConsumer = buffer.getBuffer(renderType);
            int overlay = TheObliteratorCloneRenderer.getOverlayCoords(entity,
                    this.getWhiteOverlayProgress(entity, partialTicks));
            this.model.renderToBuffer(poseStack, outlineConsumer, packedLight, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
        }

        this.model.setupAnim(entity, 0.0F, 0.0F, (float) entity.tickCount + partialTicks, 0.0F, 0.0F);
        float f_0_2459762 = 1.0F - fadeProgress;
        VertexConsumer translucentConsumer = buffer.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(entity)));
        this.model.renderToBuffer(poseStack, translucentConsumer, packedLight, OverlayTexture.NO_OVERLAY,
                1.0F, 1.0F, 1.0F, Mth.clamp(f_0_2459762 - 0.3F, 0.0F, 1.0F));

        VertexConsumer outerConsumer = buffer.getBuffer(RenderType.eyes(OUTER_LAYER));
        this.model.renderToBuffer(poseStack, outerConsumer, packedLight, OverlayTexture.NO_OVERLAY,
                1.0F, 1.0F, 1.0F, 0.4F);

        if (entity.getAnimationTicks() <= 13) {
            VertexConsumer innerConsumer = buffer.getBuffer(RenderType.eyes(INNER_LAYER));
            this.model.renderToBuffer(poseStack, innerConsumer, packedLight, OverlayTexture.NO_OVERLAY,
                    1.0F, 1.0F, 1.0F, 1.0F);
        }
        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(TheObliteratorClone entity) {
        return BASE_LAYER;
    }

    @Nullable
    protected RenderType getRenderType(TheObliteratorClone entity, boolean glowing) {
        ResourceLocation resourcelocation = this.getTextureLocation(entity);
        return glowing ? RenderType.outline(resourcelocation) : null;
    }

    protected float getWhiteOverlayProgress(TheObliteratorClone entity, float partialTicks) {
        return 0.0F;
    }

    public static int getOverlayCoords(TheObliteratorClone entity, float partialTicks) {
        return OverlayTexture.pack(OverlayTexture.u(partialTicks), OverlayTexture.v(true));
    }
}
