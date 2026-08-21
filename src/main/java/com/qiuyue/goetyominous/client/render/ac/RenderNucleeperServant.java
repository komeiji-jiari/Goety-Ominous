package com.qiuyue.goetyominous.client.render.ac;

import com.github.alexmodguy.alexscaves.client.render.ACRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.client.render.model.ac.ModelNucleeperServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.NucleeperServant;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EnergySwirlLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeRenderTypes;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class RenderNucleeperServant extends MobRenderer<NucleeperServant, ModelNucleeperServant> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("alexscaves:textures/entity/nucleeper/nucleeper.png");
    private static final ResourceLocation TEXTURE_GLOW = new ResourceLocation("alexscaves:textures/entity/nucleeper/nucleeper_glow.png");
    private static final ResourceLocation TEXTURE_GLASS = new ResourceLocation("alexscaves:textures/entity/nucleeper/nucleeper_glass.png");
    private static final ResourceLocation TEXTURE_BUTTONS_0 = new ResourceLocation("alexscaves:textures/entity/nucleeper/nucleeper_buttons_0.png");
    private static final ResourceLocation TEXTURE_BUTTONS_1 = new ResourceLocation("alexscaves:textures/entity/nucleeper/nucleeper_buttons_1.png");
    private static final ResourceLocation TEXTURE_BUTTONS_2 = new ResourceLocation("alexscaves:textures/entity/nucleeper/nucleeper_buttons_2.png");
    private static final ResourceLocation TEXTURE_EXPLODE = new ResourceLocation("alexscaves:textures/entity/nucleeper/nucleeper_explode.png");

    public RenderNucleeperServant(EntityRendererProvider.Context context) {
        super(context, new ModelNucleeperServant(0.0F), 0.8F);
        this.addLayer(new LayerGlow(this));
        this.addLayer(new NucleeperServantEnergySwirlLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(NucleeperServant entity) {
        return TEXTURE;
    }

    @Override
    public void render(NucleeperServant entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        float closeProgress = entity.getCloseProgress(partialTicks);
        if (closeProgress > 0 && entity.isAlive() && !entity.isExploding()) {
            float f = Mth.sqrt(closeProgress);
            float shineWidth = f * 3.0F;
            float shineHeight = (1.0F - f) * 1.25F;
            Vec3 sirenPos = this.model.getSirenPosition(new Vec3(0.0D, -0.5D, 0.0D));
            poseStack.pushPose();
            poseStack.translate(sirenPos.x, sirenPos.y, sirenPos.z);
            poseStack.mulPose(Axis.YP.rotationDegrees(entity.getSirenAngle(partialTicks)));
            poseStack.pushPose();
            poseStack.mulPose(Axis.ZN.rotationDegrees(90.0F));
            PoseStack.Pose pose = poseStack.last();
            Matrix4f matrix4f = pose.pose();
            Matrix3f matrix3f = pose.normal();
            VertexConsumer vertexConsumer = buffer.getBuffer(ACRenderTypes.getNucleeperLights());
            shineOriginVertex(vertexConsumer, matrix4f, matrix3f, 0.0F, 0.0F);
            shineLeftCornerVertex(vertexConsumer, matrix4f, matrix3f, shineWidth, shineHeight, 0.0F, 0.0F);
            shineRightCornerVertex(vertexConsumer, matrix4f, matrix3f, shineWidth, shineHeight, 0.0F, 0.0F);
            shineLeftCornerVertex(vertexConsumer, matrix4f, matrix3f, shineWidth, shineHeight, 0.0F, 0.0F);
            matrix4f = poseStack.last().pose();
            matrix3f = poseStack.last().normal();
            poseStack.mulPose(Axis.ZN.rotationDegrees(180.0F));
            shineOriginVertex(vertexConsumer, matrix4f, matrix3f, 0.0F, 0.0F);
            shineLeftCornerVertex(vertexConsumer, matrix4f, matrix3f, shineWidth, shineHeight, 0.0F, 0.0F);
            shineRightCornerVertex(vertexConsumer, matrix4f, matrix3f, shineWidth, shineHeight, 0.0F, 0.0F);
            shineLeftCornerVertex(vertexConsumer, matrix4f, matrix3f, shineWidth, shineHeight, 0.0F, 0.0F);
            poseStack.popPose();
            poseStack.popPose();
        }
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private static void shineOriginVertex(VertexConsumer vertexConsumer, Matrix4f matrix4f, Matrix3f matrix3f, float uvX, float uvY) {
        vertexConsumer.vertex(matrix4f, 0.0F, 0.0F, 0.0F)
                .color(0, 255, 0, 230)
                .uv(uvX + 0.5F, uvY)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(240)
                .normal(matrix3f, 0.0F, 1.0F, 0.0F)
                .endVertex();
    }

    private static void shineLeftCornerVertex(VertexConsumer vertexConsumer, Matrix4f matrix4f, Matrix3f matrix3f, float f3, float f4, float f5, float f6) {
        vertexConsumer.vertex(matrix4f, -HALF_SQRT_3 * f4, f3, 0.0F)
                .color(0, 255, 0, 0)
                .uv(f5, f6 + 1.0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(240)
                .normal(matrix3f, 0.0F, -1.0F, 0.0F)
                .endVertex();
    }

    private static void shineRightCornerVertex(VertexConsumer vertexConsumer, Matrix4f matrix4f, Matrix3f matrix3f, float f3, float f4, float f5, float f6) {
        vertexConsumer.vertex(matrix4f, HALF_SQRT_3 * f4, f3, 0.0F)
                .color(0, 255, 0, 0)
                .uv(f5 + 1.0F, f6 + 1.0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(240)
                .normal(matrix3f, 0.0F, -1.0F, 0.0F)
                .endVertex();
    }

    private static final float HALF_SQRT_3 = (float) (Math.sqrt(3.0D) / 2.0D);

    public static class LayerGlow extends RenderLayer<NucleeperServant, ModelNucleeperServant> {

        public LayerGlow(RenderLayerParent<NucleeperServant, ModelNucleeperServant> renderLayerParent) {
            super(renderLayerParent);
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, NucleeperServant entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            float glowAlpha = (float) (1.0D + Math.sin(ageInTicks * 0.3F)) * 0.25F + 0.5F;
            float explodeProgress = entity.getExplodeProgress(partialTicks);
            VertexConsumer glowBuffer = bufferSource.getBuffer(ACRenderTypes.getEyesAlphaEnabled(TEXTURE_GLOW));
            this.getParentModel().renderToBuffer(poseStack, glowBuffer, packedLight, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), 1.0F, 1.0F, 1.0F, glowAlpha);
            VertexConsumer glassBuffer = bufferSource.getBuffer(ForgeRenderTypes.getUnlitTranslucent(TEXTURE_GLASS));
            this.getParentModel().renderToBuffer(poseStack, glassBuffer, packedLight, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
            int buttonIndex;
            if (entity.isCharged()) {
                buttonIndex = entity.tickCount / 2 % 6;
            } else {
                buttonIndex = entity.tickCount / 5 % 6;
            }
            ResourceLocation buttonsTexture;
            if (buttonIndex < 2) {
                buttonsTexture = TEXTURE_BUTTONS_0;
            } else if (buttonIndex < 4) {
                buttonsTexture = TEXTURE_BUTTONS_1;
            } else {
                buttonsTexture = TEXTURE_BUTTONS_2;
            }
            VertexConsumer buttonsBuffer = bufferSource.getBuffer(RenderType.eyes(buttonsTexture));
            this.getParentModel().renderToBuffer(poseStack, buttonsBuffer, packedLight, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
            VertexConsumer explodeBuffer = bufferSource.getBuffer(ACRenderTypes.getEyesAlphaEnabled(TEXTURE_EXPLODE));
            this.getParentModel().renderToBuffer(poseStack, explodeBuffer, packedLight, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), 1.0F, 1.0F, 1.0F, explodeProgress);
        }
    }

    public static class NucleeperServantEnergySwirlLayer extends EnergySwirlLayer<NucleeperServant, ModelNucleeperServant> {

        private static final ResourceLocation POWER_LOCATION = new ResourceLocation("alexscaves", "textures/entity/nucleeper/nucleeper_charged.png");
        private final ModelNucleeperServant model;

        public NucleeperServantEnergySwirlLayer(RenderLayerParent<NucleeperServant, ModelNucleeperServant> renderLayerParent) {
            super(renderLayerParent);
            this.model = new ModelNucleeperServant(1.0F);
        }

        @Override
        protected float xOffset(float f) {
            return f * 0.01F;
        }

        @Override
        protected ResourceLocation getTextureLocation() {
            return POWER_LOCATION;
        }

        @Override
        protected ModelNucleeperServant model() {
            return this.model;
        }
    }
}
