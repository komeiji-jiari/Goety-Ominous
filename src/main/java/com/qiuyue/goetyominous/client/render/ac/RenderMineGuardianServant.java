package com.qiuyue.goetyominous.client.render.ac;

import com.github.alexmodguy.alexscaves.client.render.ACRenderTypes;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.client.render.model.ac.ModelMineGuardianServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.MineGuardianServant;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class RenderMineGuardianServant extends MobRenderer<MineGuardianServant, ModelMineGuardianServant> {

    protected static final float DEFAULT_MODEL_SCALE = 1.5F;
    protected static final ResourceLocation TEXTURE = new ResourceLocation("alexscaves:textures/entity/mine_guardian.png");
    protected static final ResourceLocation TEXTURE_SLEEPING = new ResourceLocation("alexscaves:textures/entity/mine_guardian_sleeping.png");
    protected static final ResourceLocation TEXTURE_EYE = new ResourceLocation("alexscaves:textures/entity/mine_guardian_eye.png");
    protected static final ResourceLocation TEXTURE_EXPLODE = new ResourceLocation("alexscaves:textures/entity/mine_guardian_explode.png");

    private final ResourceLocation texture;
    private final ResourceLocation sleepingTexture;

    public RenderMineGuardianServant(EntityRendererProvider.Context context) {
        this(context, TEXTURE, TEXTURE_SLEEPING, TEXTURE_EYE);
    }

    protected RenderMineGuardianServant(EntityRendererProvider.Context context, ResourceLocation texture, ResourceLocation sleepingTexture, ResourceLocation eyeTexture) {
        super(context, new ModelMineGuardianServant(), 0.8F);
        this.texture = texture;
        this.sleepingTexture = sleepingTexture;
        this.addLayer(new LayerGlow(this, eyeTexture));
    }

    protected float getModelScale() {
        return DEFAULT_MODEL_SCALE;
    }

    @Override
    protected void scale(MineGuardianServant entity, PoseStack poseStack, float partialTicks) {
        float modelScale = this.getModelScale();
        poseStack.scale(modelScale, modelScale, modelScale);
    }

    @Override
    public ResourceLocation getTextureLocation(MineGuardianServant entity) {
        return entity.isEyeClosed() ? this.sleepingTexture : this.texture;
    }

    @Override
    public void render(MineGuardianServant entityIn, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn) {
        super.render(entityIn, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);
        float bodyYaw = Mth.rotLerp(partialTicks, entityIn.yBodyRotO, entityIn.yBodyRot);
        float scanProgress = entityIn.getScanProgress(partialTicks);
        if (scanProgress > 0.0F && entityIn.isAlive() && !entityIn.isExploding()) {
            float ticks = (float) entityIn.tickCount + partialTicks;
            float length = (float) ((double) scanProgress * ((double) 4.0F + Math.sin((double) (ticks * 0.2F + 2.0F))));
            float width = scanProgress * scanProgress * 1.0F;
            float extraX = (float) ((double) scanProgress * Math.sin((double) (ticks * 0.1F)) * (double) 3.0F);
            poseStack.pushPose();
            float coneScale = this.getModelScale() / DEFAULT_MODEL_SCALE;
            poseStack.scale(coneScale, coneScale, coneScale);
            poseStack.translate(0.0F, 0.5F, 0.0F);
            poseStack.mulPose(Axis.YN.rotationDegrees(bodyYaw));
            poseStack.translate(extraX * 0.5F / 16.0F, 0.25F, 0.75F);
            poseStack.mulPose(Axis.YP.rotationDegrees((float) (Math.sin((double) (ticks * 0.1F)) * (double) 32.0F * (double) scanProgress)));
            ((ModelMineGuardianServant) this.model).translateToEye(poseStack);
            poseStack.pushPose();
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
            poseStack.translate(0.0F, -0.5F, 0.0F);
            PoseStack.Pose pose = poseStack.last();
            Matrix4f matrix4f = pose.pose();
            Matrix3f matrix3f = pose.normal();
            VertexConsumer lightConsumer = bufferIn.getBuffer(ACRenderTypes.getSubmarineLights());
            shineOriginVertex(lightConsumer, matrix4f, matrix3f, 0.0F, 0.0F);
            shineLeftCornerVertex(lightConsumer, matrix4f, matrix3f, length, width, 0.0F, 0.0F);
            shineRightCornerVertex(lightConsumer, matrix4f, matrix3f, length, width, 0.0F, 0.0F);
            shineLeftCornerVertex(lightConsumer, matrix4f, matrix3f, length, width, 0.0F, 0.0F);
            poseStack.popPose();
            poseStack.popPose();
        }
    }

    private static void shineOriginVertex(VertexConsumer consumer, Matrix4f matrix, Matrix3f normal, float xOffset, float yOffset) {
        consumer.vertex(matrix, 0.0F, 0.0F, 0.0F).color(230, 0, 0, 230).uv(xOffset + 0.5F, yOffset).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
    }

    private static void shineLeftCornerVertex(VertexConsumer consumer, Matrix4f matrix, Matrix3f normal, float height, float width, float xOffset, float yOffset) {
        consumer.vertex(matrix, -ACMath.HALF_SQRT_3 * width, height, 0.0F).color(255, 0, 0, 0).uv(xOffset, yOffset + 1.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(normal, 0.0F, -1.0F, 0.0F).endVertex();
    }

    private static void shineRightCornerVertex(VertexConsumer consumer, Matrix4f matrix, Matrix3f normal, float height, float width, float xOffset, float yOffset) {
        consumer.vertex(matrix, ACMath.HALF_SQRT_3 * width, height, 0.0F).color(255, 0, 0, 0).uv(xOffset + 1.0F, yOffset + 1.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(normal, 0.0F, -1.0F, 0.0F).endVertex();
    }

    public static class LayerGlow extends RenderLayer<MineGuardianServant, ModelMineGuardianServant> {

        private final ResourceLocation eyeTexture;

        public LayerGlow(RenderLayerParent<MineGuardianServant, ModelMineGuardianServant> renderLayerParent, ResourceLocation eyeTexture) {
            super(renderLayerParent);
            this.eyeTexture = eyeTexture;
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, MineGuardianServant entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            float explodeProgress = entity.getExplodeProgress(partialTicks);
            
            if (!entity.isEyeClosed()) {
                VertexConsumer glowBuffer = bufferSource.getBuffer(RenderType.eyes(this.eyeTexture));
                this.getParentModel().renderToBuffer(poseStack, glowBuffer, packedLight, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
            }
            
            VertexConsumer explodeBuffer = bufferSource.getBuffer(ACRenderTypes.getEyesAlphaEnabled(TEXTURE_EXPLODE));
            this.getParentModel().renderToBuffer(poseStack, explodeBuffer, packedLight, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), 1.0F, 1.0F, 1.0F, explodeProgress);
        }
    }
}
