package com.qiuyue.goetyominous.client.render.lm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.lm.TheObliteratorServantModel;
import com.qiuyue.goetyominous.common.entities.ally.lm.TheObliteratorServant;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class TheObliteratorServantRenderer extends MobRenderer<TheObliteratorServant, TheObliteratorServantModel<TheObliteratorServant>> {

    private static final ResourceLocation ARMORED = new ResourceLocation("legendary_monsters", "textures/entity/the_warped_one/the_warped_one.png");
    private static final ResourceLocation NO_ARMOR = new ResourceLocation("legendary_monsters", "textures/entity/the_warped_one/the_warped_one_no_armor.png");
    private static final ResourceLocation PHASE_3 = new ResourceLocation("legendary_monsters", "textures/entity/the_warped_one/the_warped_one_phase_3.png");
    private static final float HALF_SQRT_3 = (float) (Math.sqrt(3.0D) / 2.0D);

    public TheObliteratorServantRenderer(EntityRendererProvider.Context context) {
        super(context, new TheObliteratorServantModel<>(context.bakeLayer(ModEntityLayers.THE_OBLITERATOR_SERVANT_LAYER)), 2.25F);
        this.addLayer(new TheObliteratorServantUltimateFlameLayer(this));
        this.addLayer(new TheObliteratorServantFlameyBladesLayer(this));
        this.addLayer(new TheObliteratorServantPowerBallInnerLayer(this));
        this.addLayer(new TheObliteratorServantPowerBallOuterLayer(this));
        this.addLayer(new TheObliteratorServantGrabLayer(this, context.getEntityRenderDispatcher()));
    }

    @Override
    public void render(TheObliteratorServant entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        float bodyXRotDeg = (this.model.body.xRot + this.model.bigCube.xRot + this.model.root.xRot) * 57.295776F;
        float bodyYRotDeg = (this.model.body.yRot + this.model.bigCube.yRot + this.model.root.yRot) * 57.295776F;
        entity.setPartXRot(-bodyXRotDeg);
        entity.setPartYRot(-bodyYRotDeg);
        poseStack.scale(0.8F, 0.8F, 0.8F);

        poseStack.pushPose();
        float renderProgress = !entity.getIsQuadBeamRight()
                ? Math.max((float) (entity.renderProgress * 5) + partialTicks, -90.0F)
                : Math.min((float) (entity.renderProgress * 5) + partialTicks, 90.0F);
        float yaw = -entity.yBodyRot + 90.0F + renderProgress;
        float yaw2 = -entity.yBodyRot + 180.0F + renderProgress;
        float yaw3 = -entity.yBodyRot + 270.0F + renderProgress;
        float yaw4 = -entity.yBodyRot + renderProgress;
        VertexConsumer lightningConsumer = buffer.getBuffer(LmServantRenderTypes.LIGHTNING_NO_CULL);
        if (entity.getAttackState() == 46 && entity.attackTicks < 46 && entity.attackTicks > 12) {
            double shine = Math.min((double) entity.QuadLaserShineUp.getTimer() * 0.015D, 7.5D);
            double shine2 = Math.min((double) entity.QuadLaserShineUp.getTimer() * 0.015D, 6.5D);
            float alpha = (float) (0.35D + shine2);
            float uniY = 2.5F;
            LmServantRenderUtils.renderPivotedQuad(16.0F, 0.25F, 0.0D, uniY, 0.0D, 0.0D, yaw, 0.0D, lightningConsumer, poseStack, OverlayTexture.NO_OVERLAY, packedLight, (float) (0.25D + shine), 1.0F, (float) (0.25D + shine), alpha);
            LmServantRenderUtils.renderPivotedQuad(16.0F, 0.25F, 0.0D, uniY, 0.0D, 0.0D, yaw2, 0.0D, lightningConsumer, poseStack, OverlayTexture.NO_OVERLAY, packedLight, (float) (0.25D + shine), 1.0F, (float) (0.25D + shine), alpha);
            LmServantRenderUtils.renderPivotedQuad(16.0F, 0.25F, 0.0D, uniY, 0.0D, 0.0D, yaw3, 0.0D, lightningConsumer, poseStack, OverlayTexture.NO_OVERLAY, packedLight, (float) (0.25D + shine), 1.0F, (float) (0.25D + shine), alpha);
            LmServantRenderUtils.renderPivotedQuad(16.0F, 0.25F, 0.0D, uniY, 0.0D, 0.0D, yaw4, 0.0D, lightningConsumer, poseStack, OverlayTexture.NO_OVERLAY, packedLight, (float) (0.25D + shine), 1.0F, (float) (0.25D + shine), alpha);
        }
        poseStack.popPose();

        if (entity.attackTicks > 0 && entity.attackTicks < 45 && entity.getAttackState() == 2) {
            float f5 = ((float) entity.attackTicks + partialTicks) / 200.0F;
            float f7 = Math.min(f5 > 0.8F ? (f5 - 0.8F) / 0.2F : 0.0F, 1.0F);
            RandomSource randomSource = RandomSource.create(432L);
            VertexConsumer lightning = buffer.getBuffer(RenderType.lightning());
            poseStack.pushPose();
            poseStack.translate(0.0D, 3.0D + (double) entity.attackTicks * 0.1D, 0.0D);
            float t = Mth.clamp(((float) entity.attackTicks + partialTicks) / 40.0F, 0.0F, 1.0F);
            float scale = Mth.clamp((float) Math.sin(Math.PI * (double) t), 0.0F, 1.0F);
            poseStack.scale(scale, scale, scale);
            for (int i = 0; (float) i < (f5 + f5 * f5) / 2.0F * 30.0F; i++) {
                poseStack.mulPose(Axis.XP.rotationDegrees(randomSource.nextFloat() * 360.0F));
                poseStack.mulPose(Axis.YP.rotationDegrees(randomSource.nextFloat() * 360.0F));
                poseStack.mulPose(Axis.ZP.rotationDegrees(randomSource.nextFloat() * 360.0F));
                poseStack.mulPose(Axis.XP.rotationDegrees(randomSource.nextFloat() * 360.0F));
                poseStack.mulPose(Axis.YP.rotationDegrees(randomSource.nextFloat() * 360.0F));
                poseStack.mulPose(Axis.ZP.rotationDegrees(randomSource.nextFloat() * 360.0F + f5 * 90.0F));
                float f3 = randomSource.nextFloat() * 20.0F + 5.0F + f7 * 10.0F;
                float f4 = randomSource.nextFloat() * 2.0F + 1.0F + f7 * 2.0F;
                Matrix4f matrix4f = poseStack.last().pose();
                int alpha = (int) (255.0F * (1.0F - f7));
                vertex01(lightning, matrix4f, alpha);
                vertex2(lightning, matrix4f, f3, f4);
                vertex3(lightning, matrix4f, f3, f4);
                vertex01(lightning, matrix4f, alpha);
                vertex3(lightning, matrix4f, f3, f4);
                vertex4(lightning, matrix4f, f3, f4);
                vertex01(lightning, matrix4f, alpha);
                vertex4(lightning, matrix4f, f3, f4);
                vertex2(lightning, matrix4f, f3, f4);
            }
            poseStack.popPose();
        }

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(TheObliteratorServant entity) {
        if (entity.getIsThirdPhase()) {
            return PHASE_3;
        }
        if (entity.getIsSecondPhase()) {
            return NO_ARMOR;
        }
        return ARMORED;
    }

    private static void vertex01(VertexConsumer consumer, Matrix4f matrix, int alpha) {
        consumer.vertex(matrix, 0.0F, 4.0F, 0.0F).color(0, 255, 0, alpha).endVertex();
    }

    private static void vertex2(VertexConsumer consumer, Matrix4f matrix, float y, float scale) {
        consumer.vertex(matrix, -HALF_SQRT_3 * scale, y, -0.5F * scale).color(0, 255, 0, 0).endVertex();
    }

    private static void vertex3(VertexConsumer consumer, Matrix4f matrix, float y, float scale) {
        consumer.vertex(matrix, -HALF_SQRT_3 * scale, y, -0.5F * scale).color(0, 255, 0, 0).endVertex();
    }

    private static void vertex4(VertexConsumer consumer, Matrix4f matrix, float y, float scale) {
        consumer.vertex(matrix, 0.0F, y, 1.0F * scale).color(0, 255, 0, 0).endVertex();
    }
}
