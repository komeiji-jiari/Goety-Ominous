package com.qiuyue.goetyominous.client.render.ac;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.ClientProxy;
import com.github.alexmodguy.alexscaves.client.model.TremorzillaBeamModel;
import com.github.alexmodguy.alexscaves.client.render.ACRenderTypes;
import com.github.alexthe666.citadel.client.shader.PostEffectRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.client.render.model.ac.ModelTremorzillaServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.TremorzillaServant;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.entity.PartEntity;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import javax.annotation.Nullable;
import java.util.HashMap;

/**
 * 特雷莫兹拉仆从渲染器。忠实移植 AC 原版 TremorzillaRenderer 的三层光束/嘴部定位/尖刺辉光逻辑。
 * 丢弃 AC 的 CustomBookEntityRenderer(图书)与 sepia(褐变滤镜)机制。
 */
@OnlyIn(Dist.CLIENT)
public class RenderTremorzillaServant extends MobRenderer<TremorzillaServant, ModelTremorzillaServant> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("alexscaves:textures/entity/tremorzilla/tremorzilla.png");
    private static final ResourceLocation TEXTURE_RETRO = new ResourceLocation("alexscaves:textures/entity/tremorzilla/tremorzilla_retro.png");
    private static final ResourceLocation TEXTURE_TECTONIC = new ResourceLocation("alexscaves:textures/entity/tremorzilla/tremorzilla_tectonic.png");
    private static final ResourceLocation TEXTURE_GLOW = new ResourceLocation("alexscaves:textures/entity/tremorzilla/tremorzilla_glow.png");
    private static final ResourceLocation TEXTURE_RETRO_GLOW = new ResourceLocation("alexscaves:textures/entity/tremorzilla/tremorzilla_retro_glow.png");
    private static final ResourceLocation TEXTURE_TECTONIC_GLOW = new ResourceLocation("alexscaves:textures/entity/tremorzilla/tremorzilla_tectonic_glow.png");
    private static final ResourceLocation TEXTURE_GLOW_POWERED = new ResourceLocation("alexscaves:textures/entity/tremorzilla/tremorzilla_glow_powered.png");
    private static final ResourceLocation TEXTURE_RETRO_GLOW_POWERED = new ResourceLocation("alexscaves:textures/entity/tremorzilla/tremorzilla_retro_glow_powered.png");
    private static final ResourceLocation TEXTURE_TECTONIC_GLOW_POWERED = new ResourceLocation("alexscaves:textures/entity/tremorzilla/tremorzilla_tectonic_glow_powered.png");
    private static final ResourceLocation TEXTURE_BEAM_INNER = new ResourceLocation("alexscaves:textures/entity/tremorzilla/tremorzilla_beam_inner.png");
    private static final ResourceLocation TEXTURE_RETRO_BEAM_INNER = new ResourceLocation("alexscaves:textures/entity/tremorzilla/tremorzilla_retro_beam_inner.png");
    private static final ResourceLocation TEXTURE_TECTONIC_BEAM_INNER = new ResourceLocation("alexscaves:textures/entity/tremorzilla/tremorzilla_tectonic_beam_inner.png");
    private static final ResourceLocation TEXTURE_BEAM_OUTER = new ResourceLocation("alexscaves:textures/entity/tremorzilla/tremorzilla_beam_outer.png");
    private static final ResourceLocation TEXTURE_RETRO_BEAM_OUTER = new ResourceLocation("alexscaves:textures/entity/tremorzilla/tremorzilla_retro_beam_outer.png");
    private static final ResourceLocation TEXTURE_TECTONIC_BEAM_OUTER = new ResourceLocation("alexscaves:textures/entity/tremorzilla/tremorzilla_tectonic_beam_outer.png");
    private static final ResourceLocation TEXTURE_BEAM_END_0 = new ResourceLocation("alexscaves:textures/entity/tremorzilla/tremorzilla_beam_end_0.png");
    private static final ResourceLocation TEXTURE_RETRO_BEAM_END_0 = new ResourceLocation("alexscaves:textures/entity/tremorzilla/tremorzilla_retro_beam_end_0.png");
    private static final ResourceLocation TEXTURE_TECTONIC_BEAM_END_0 = new ResourceLocation("alexscaves:textures/entity/tremorzilla/tremorzilla_tectonic_beam_end_0.png");
    private static final ResourceLocation TEXTURE_BEAM_END_1 = new ResourceLocation("alexscaves:textures/entity/tremorzilla/tremorzilla_beam_end_1.png");
    private static final ResourceLocation TEXTURE_RETRO_BEAM_END_1 = new ResourceLocation("alexscaves:textures/entity/tremorzilla/tremorzilla_retro_beam_end_1.png");
    private static final ResourceLocation TEXTURE_TECTONIC_BEAM_END_1 = new ResourceLocation("alexscaves:textures/entity/tremorzilla/tremorzilla_tectonic_beam_end_1.png");
    private static final ResourceLocation TEXTURE_BEAM_END_2 = new ResourceLocation("alexscaves:textures/entity/tremorzilla/tremorzilla_beam_end_2.png");
    private static final ResourceLocation TEXTURE_RETRO_BEAM_END_2 = new ResourceLocation("alexscaves:textures/entity/tremorzilla/tremorzilla_retro_beam_end_2.png");
    private static final ResourceLocation TEXTURE_TECTONIC_BEAM_END_2 = new ResourceLocation("alexscaves:textures/entity/tremorzilla/tremorzilla_tectonic_beam_end_2.png");

    private static final HashMap<Integer, Vec3> mouthParticlePositions = new HashMap<>();
    private static final Vec3 MOUTH_TRANSFORM_POS = new Vec3(0.0D, 1.0D, -1.0D);
    private static final TremorzillaBeamModel BEAM_END_MODEL = new TremorzillaBeamModel();

    public RenderTremorzillaServant(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelTremorzillaServant(), 4.0F);
        this.addLayer(new LayerGlow());
        this.addLayer(new TremorzillaServantRiderLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(TremorzillaServant entity) {
        return entity.getAltSkin() == 2 ? TEXTURE_TECTONIC : (entity.getAltSkin() == 1 ? TEXTURE_RETRO : TEXTURE);
    }

    @Override
    public void render(TremorzillaServant entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource source, int packedLight) {
        this.shadowRadius = 4.0F * entity.getScale();
        super.render(entity, entityYaw, partialTicks, poseStack, source, packedLight);
        float bodyYaw = Mth.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
        float beamProgress = entity.getBeamProgress(partialTicks);
        Vec3 beamEndVec = entity.getClientBeamEndPosition(partialTicks);
        if (beamProgress > 0.0F && entity.isAlive() && beamEndVec != null) {
            Vec3 modelOffset = this.getModel().getMouthPosition(new Vec3(0.0D, 0.1F, 0.0D)).yRot((float) (Math.PI - (double) (bodyYaw * ((float) Math.PI / 180))));
            float ageInTicks = (float) entity.tickCount + partialTicks;
            float shakeByX = (float) Math.sin(ageInTicks * 4.0F) * 0.075F;
            float shakeByY = (float) Math.sin(ageInTicks * 4.0F + 1.2F) * 0.075F;
            float shakeByZ = (float) Math.sin(ageInTicks * 4.0F + 2.4F) * 0.075F;
            Vec3 rawBeamPosition = beamEndVec.subtract(entity.getPosition(partialTicks).add(modelOffset));
            float length = (float) rawBeamPosition.length();
            Vec3 vec3 = rawBeamPosition.normalize();
            float xRot = (float) Math.acos(vec3.y);
            float yRot = (float) Math.atan2(vec3.z, vec3.x);
            float width = beamProgress * 1.5F;
            poseStack.pushPose();
            poseStack.translate(modelOffset.x + (double) shakeByX, modelOffset.y + (double) shakeByY, modelOffset.z + (double) shakeByZ);
            poseStack.mulPose(Axis.YP.rotationDegrees((1.5707964F - yRot) * 57.295776F));
            poseStack.mulPose(Axis.XP.rotationDegrees((-1.5707964F + xRot) * 57.295776F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(45.0F));
            this.renderBeam(entity, poseStack, source, partialTicks, width, length, true, false);
            if (AlexsCaves.CLIENT_CONFIG.radiationGlowEffect.get()) {
                this.renderBeam(entity, poseStack, source, partialTicks, width, length, true, true);
            }
            this.renderBeam(entity, poseStack, source, partialTicks, width, length, false, false);
            poseStack.popPose();
        }
        mouthParticlePositions.put(entity.getId(), this.getModel().getMouthPosition(MOUTH_TRANSFORM_POS));
    }

    public static Vec3 getMouthPositionFor(int entityId) {
        return mouthParticlePositions.get(entityId);
    }

    @Nullable
    @Override
    protected RenderType getRenderType(TremorzillaServant mob, boolean normal, boolean translucent, boolean outline) {
        ResourceLocation resourcelocation = this.getTextureLocation(mob);
        if (translucent) {
            return RenderType.itemEntityTranslucentCull(resourcelocation);
        }
        if (normal) {
            return RenderType.entityTranslucent(resourcelocation);
        }
        return outline ? RenderType.outline(resourcelocation) : null;
    }

    private void renderBeam(TremorzillaServant entity, PoseStack poseStack, MultiBufferSource source, float partialTicks, float width, float length, boolean inner, boolean glowSecondPass) {
        float speed;
        VertexConsumer vertexconsumer;
        int vertices;
        poseStack.pushPose();
        float startAlpha = 1.0F;
        float endAlpha = 1.0F;
        if (inner) {
            vertices = 4;
            ResourceLocation resourceLocation;
            if (entity.getAltSkin() == 2) {
                resourceLocation = TEXTURE_TECTONIC_BEAM_INNER;
            } else if (entity.getAltSkin() == 1) {
                resourceLocation = TEXTURE_RETRO_BEAM_INNER;
            } else {
                resourceLocation = TEXTURE_BEAM_INNER;
            }
            if (AlexsCaves.CLIENT_CONFIG.radiationGlowEffect.get() && glowSecondPass) {
                PostEffectRegistry.renderEffectForNextTick(ClientProxy.IRRADIATED_SHADER);
                vertexconsumer = source.getBuffer(ACRenderTypes.getTremorzillaBeam(resourceLocation, true));
                endAlpha = 0.5F;
            } else {
                vertexconsumer = source.getBuffer(ACRenderTypes.getTremorzillaBeam(resourceLocation, false));
            }
            speed = 0.5F;
        } else {
            vertices = 8;
            ResourceLocation resourceLocation;
            if (entity.getAltSkin() == 2) {
                resourceLocation = TEXTURE_TECTONIC_BEAM_OUTER;
            } else if (entity.getAltSkin() == 1) {
                resourceLocation = TEXTURE_RETRO_BEAM_OUTER;
            } else {
                resourceLocation = TEXTURE_BEAM_OUTER;
            }
            if (AlexsCaves.CLIENT_CONFIG.radiationGlowEffect.get()) {
                PostEffectRegistry.renderEffectForNextTick(ClientProxy.IRRADIATED_SHADER);
                vertexconsumer = source.getBuffer(ACRenderTypes.getTremorzillaBeam(resourceLocation, true));
            } else {
                vertexconsumer = source.getBuffer(ACRenderTypes.getTremorzillaBeam(resourceLocation, false));
            }
            width += 0.25F;
            speed = 1.0F;
            endAlpha = 0.0F;
        }
        float v = ((float) entity.tickCount + partialTicks) * -0.25F * speed;
        float v1 = v + length * (inner ? 0.5F : 0.15F);
        float f4 = -width;
        float f5 = 0.0F;
        float f6 = 0.0F;
        PoseStack.Pose posestack$pose = poseStack.last();
        Matrix4f matrix4f = posestack$pose.pose();
        for (int j = 0; j <= vertices; ++j) {
            Matrix3f matrix3f = posestack$pose.normal();
            float f7 = Mth.cos((float) Math.PI + (float) j * ((float) Math.PI * 2) / (float) vertices) * width;
            float f8 = Mth.sin((float) Math.PI + (float) j * ((float) Math.PI * 2) / (float) vertices) * width;
            float f9 = (float) j + 1.0F;
            vertexconsumer.vertex(matrix4f, f4 * 0.55F, f5 * 0.55F, 0.0F).color(1.0F, 1.0F, 1.0F, startAlpha).uv(f6, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
            vertexconsumer.vertex(matrix4f, f4, f5, length).color(1.0F, 1.0F, 1.0F, endAlpha).uv(f6, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
            vertexconsumer.vertex(matrix4f, f7, f8, length).color(1.0F, 1.0F, 1.0F, endAlpha).uv(f9, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
            vertexconsumer.vertex(matrix4f, f7 * 0.55F, f8 * 0.55F, 0.0F).color(1.0F, 1.0F, 1.0F, startAlpha).uv(f9, v).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
            f4 = f7;
            f5 = f8;
            f6 = f9;
        }
        if (inner) {
            VertexConsumer endVertexConsumer;
            if (AlexsCaves.CLIENT_CONFIG.radiationGlowEffect.get() && glowSecondPass) {
                PostEffectRegistry.renderEffectForNextTick(ClientProxy.IRRADIATED_SHADER);
                endVertexConsumer = source.getBuffer(ACRenderTypes.getTremorzillaBeam(this.getEndBeamTexture(entity), true));
            } else {
                endVertexConsumer = source.getBuffer(ACRenderTypes.getTremorzillaBeam(this.getEndBeamTexture(entity), false));
            }
            poseStack.pushPose();
            poseStack.translate(0.0F, 0.0F, length - 1.5F);
            poseStack.mulPose(Axis.ZP.rotationDegrees(45.0F));
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            poseStack.scale(width, width, width);
            BEAM_END_MODEL.resetToDefaultPose();
            BEAM_END_MODEL.renderToBuffer(poseStack, endVertexConsumer, 240, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            poseStack.popPose();
        }
        poseStack.popPose();
    }

    private ResourceLocation getEndBeamTexture(TremorzillaServant entity) {
        int time = entity.tickCount / 2 % 3;
        if (time == 1) {
            return entity.getAltSkin() == 2 ? TEXTURE_TECTONIC_BEAM_END_1 : (entity.getAltSkin() == 1 ? TEXTURE_RETRO_BEAM_END_1 : TEXTURE_BEAM_END_1);
        } else if (time == 2) {
            return entity.getAltSkin() == 2 ? TEXTURE_TECTONIC_BEAM_END_2 : (entity.getAltSkin() == 1 ? TEXTURE_RETRO_BEAM_END_2 : TEXTURE_BEAM_END_2);
        }
        return entity.getAltSkin() == 2 ? TEXTURE_TECTONIC_BEAM_END_0 : (entity.getAltSkin() == 1 ? TEXTURE_RETRO_BEAM_END_0 : TEXTURE_BEAM_END_0);
    }

    @Override
    public boolean shouldRender(TremorzillaServant entity, Frustum camera, double x, double y, double z) {
        if (super.shouldRender(entity, camera, x, y, z)) {
            return true;
        }
        for (PartEntity<?> part : entity.getParts()) {
            if (!camera.isVisible(part.getBoundingBoxForCulling())) {
                continue;
            }
            return true;
        }
        if (entity.isFiring()) {
            Vec3 endBeam = entity.getClientBeamEndPosition(1.0F);
            if (endBeam != null) {
                Vec3 vec3 = entity.getBeamShootFrom(1.0F);
                return camera.isVisible(new AABB(endBeam.x, endBeam.y, endBeam.z, vec3.x, vec3.y, vec3.z));
            }
        }
        return false;
    }

    class LayerGlow extends RenderLayer<TremorzillaServant, ModelTremorzillaServant> {

        public LayerGlow() {
            super(RenderTremorzillaServant.this);
        }

        @Override
        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, TremorzillaServant tremorzilla, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            float normalAlpha = (float) Math.sin(ageInTicks * 0.2F) * 0.15F + 0.5F;
            float spikeDownAmount = tremorzilla.getClientSpikeDownAmount(partialTicks);
            ResourceLocation glowTexture;
            if (tremorzilla.isPowered()) {
                glowTexture = tremorzilla.getAltSkin() == 2 ? TEXTURE_TECTONIC_GLOW_POWERED : (tremorzilla.getAltSkin() == 1 ? TEXTURE_RETRO_GLOW_POWERED : TEXTURE_GLOW_POWERED);
            } else {
                glowTexture = tremorzilla.getAltSkin() == 2 ? TEXTURE_TECTONIC_GLOW : (tremorzilla.getAltSkin() == 1 ? TEXTURE_RETRO_GLOW : TEXTURE_GLOW);
            }
            VertexConsumer normalGlowConsumer = bufferIn.getBuffer(ACRenderTypes.getEyesAlphaEnabled(glowTexture));
            this.getParentModel().renderToBuffer(matrixStackIn, normalGlowConsumer, packedLightIn, LivingEntityRenderer.getOverlayCoords(tremorzilla, 0.0F), 1.0F, 1.0F, 1.0F, normalAlpha);
            if (spikeDownAmount > 0.0F) {
                VertexConsumer spikeGlowConsumer;
                if (AlexsCaves.CLIENT_CONFIG.radiationGlowEffect.get()) {
                    PostEffectRegistry.renderEffectForNextTick(ClientProxy.IRRADIATED_SHADER);
                    spikeGlowConsumer = bufferIn.getBuffer(ACRenderTypes.getTremorzillaBeam(tremorzilla.getAltSkin() == 2 ? TEXTURE_TECTONIC_GLOW_POWERED : (tremorzilla.getAltSkin() == 1 ? TEXTURE_RETRO_GLOW_POWERED : TEXTURE_GLOW_POWERED), true));
                } else {
                    spikeGlowConsumer = normalGlowConsumer;
                }
                this.getParentModel().showSpikesBasedOnProgress(spikeDownAmount, 0.0F);
                this.getParentModel().renderToBuffer(matrixStackIn, spikeGlowConsumer, packedLightIn, LivingEntityRenderer.getOverlayCoords(tremorzilla, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
                this.getParentModel().showAllSpikes();
            }
        }
    }
}
