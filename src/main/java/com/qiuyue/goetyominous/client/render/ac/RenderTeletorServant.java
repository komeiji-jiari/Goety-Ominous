package com.qiuyue.goetyominous.client.render.ac;

import com.github.alexmodguy.alexscaves.client.render.ACRenderTypes;
import com.github.alexthe666.citadel.client.render.LightningBoltData;
import com.github.alexthe666.citadel.client.render.LightningRender;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.client.render.model.ac.ModelTeletorServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.TeletorServant;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 传送使徒仆从渲染:逐字移植 AC TeletorRenderer,复用 AC 原版 teletor 贴图与光源层。
 * 额外绘制:
 * <ul>
 *   <li>双手悬浮引导武器时,沿头部两侧(helmetPosition 0/1)到武器之间画出蓝/红双色连锁闪电;</li>
 *   <li>hasTrail 时沿头部正后方拖出半透明色带(由实体 trailPositions 驱动,仅当被击中有轨迹时)。</li>
 * </ul>
 * 闪电与色带的坐标全部换算到「实体旋转 180° 后的头部模型空间」,与原版表现一致。
 */
@OnlyIn(Dist.CLIENT)
public class RenderTeletorServant extends MobRenderer<TeletorServant, ModelTeletorServant> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexscaves:textures/entity/teletor.png");
    private static final ResourceLocation TEXTURE_GLOW = new ResourceLocation("alexscaves:textures/entity/teletor_glow.png");
    private static final ResourceLocation TRAIL_TEXTURE = new ResourceLocation("alexscaves", "textures/particle/teletor_trail.png");

    private final Map<UUID, LightningRender> lightningRenderMap = new HashMap<>();

    public RenderTeletorServant(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelTeletorServant(), 0.5F);
        this.addLayer(new LayerGlow(this));
    }

    @Override
    protected void scale(TeletorServant mob, PoseStack matrixStackIn, float partialTicks) {
        matrixStackIn.scale(0.9F, 0.9F, 0.9F);
    }

    @Override
    public boolean shouldRender(TeletorServant entity, Frustum camera, double camX, double camY, double camZ) {
        if (super.shouldRender(entity, camera, camX, camY, camZ)) {
            return true;
        }
        Entity weapon = entity.getWeapon();
        if (weapon != null) {
            Vec3 vec3 = entity.position();
            Vec3 vec31 = weapon.position();
            return camera.isVisible(new AABB(vec31.x, vec31.y, vec31.z, vec3.x, vec3.y, vec3.z));
        }
        return false;
    }

    @Override
    public ResourceLocation getTextureLocation(TeletorServant entity) {
        return TEXTURE;
    }

    @Override
    public void render(TeletorServant entityIn, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn) {
        super.render(entityIn, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);
        double x = Mth.lerp(partialTicks, entityIn.xOld, entityIn.getX());
        double y = Mth.lerp(partialTicks, entityIn.yOld, entityIn.getY());
        double z = Mth.lerp(partialTicks, entityIn.zOld, entityIn.getZ());
        float yaw = entityIn.yBodyRotO + (entityIn.yBodyRot - entityIn.yBodyRotO) * partialTicks;
        if (entityIn.hasTrail()) {
            poseStack.pushPose();
            poseStack.translate(-x, -y, -z);
            this.setupRotations(entityIn, poseStack, 0.0F, 180.0F, partialTicks);
            Vec3 headModelPos = this.model.translateToHead(new Vec3(0.0D, -0.4D, 0.0D), yaw).scale(-1.0D);
            poseStack.translate(headModelPos.x, headModelPos.y, headModelPos.z);
            this.renderTrail(entityIn, 0, partialTicks, poseStack, bufferIn, 0.2F, 0.2F, 0.8F, 0.8F, 240);
            this.renderTrail(entityIn, 1, partialTicks, poseStack, bufferIn, 0.8F, 0.2F, 0.2F, 0.8F, 240);
            poseStack.popPose();
        }
        Entity weapon = entityIn.getWeapon();
        if (weapon != null && entityIn.isAlive() && weapon.isAlive()) {
            poseStack.pushPose();
            poseStack.translate(-x, -y, -z);
            this.setupRotations(entityIn, poseStack, 0.0F, 180.0F, partialTicks);
            Vec3 headModelPos = this.model.translateToHead(new Vec3(0.0D, -0.4D, 0.0D), yaw).scale(-1.0D);
            Vec3 fromVec1 = entityIn.getHelmetPosition(0).add(headModelPos);
            Vec3 fromVec2 = entityIn.getHelmetPosition(1).add(headModelPos);
            Vec3 toVec = weapon.getPosition(partialTicks).add(0.0D, weapon.getBbHeight() * 0.5F - 0.1F + Math.sin((weapon.tickCount + partialTicks) * 0.1F) * 0.1D, 0.0D);
            int segCount = (int) Mth.clamp(weapon.distanceTo(entityIn) + 2.0F, 3.0F, 30.0F);
            float spreadFactor = Mth.clamp((10.0F - weapon.distanceTo(entityIn)) / 10.0F * 0.2F, 0.01F, 0.2F);
            LightningBoltData.BoltRenderInfo blueBoltData = new LightningBoltData.BoltRenderInfo(0.0F, spreadFactor, 0.0F, 0.0F, new Vector4f(0.2F, 0.2F, 0.8F, 0.8F), 0.1F);
            LightningBoltData.BoltRenderInfo redBoltData = new LightningBoltData.BoltRenderInfo(0.0F, spreadFactor, 0.0F, 0.0F, new Vector4f(0.8F, 0.2F, 0.2F, 0.8F), 0.1F);
            LightningBoltData bolt1 = new LightningBoltData(blueBoltData, fromVec1, toVec, segCount).size(0.1F).lifespan(1).spawn(LightningBoltData.SpawnFunction.CONSECUTIVE).fade(LightningBoltData.FadeFunction.NONE);
            LightningBoltData bolt2 = new LightningBoltData(redBoltData, fromVec2, toVec, segCount).size(0.1F).lifespan(1).spawn(LightningBoltData.SpawnFunction.CONSECUTIVE).fade(LightningBoltData.FadeFunction.NONE);
            LightningRender lightningRender = this.getLightningRender(entityIn.getUUID());
            lightningRender.update(entityIn, bolt1, partialTicks);
            lightningRender.update(weapon, bolt2, partialTicks);
            lightningRender.render(partialTicks, poseStack, bufferIn);
            poseStack.popPose();
        }
        if (!entityIn.isAlive() && this.lightningRenderMap.containsKey(entityIn.getUUID())) {
            this.lightningRenderMap.remove(entityIn.getUUID());
        }
    }

    private LightningRender getLightningRender(UUID uuid) {
        if (this.lightningRenderMap.get(uuid) == null) {
            this.lightningRenderMap.put(uuid, new LightningRender());
        }
        return this.lightningRenderMap.get(uuid);
    }

    private void renderTrail(TeletorServant entityIn, int side, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, float trailR, float trailG, float trailB, float trailA, int packedLightIn) {
        int sampleSize = 10;
        float trailHeight = 0.2F;
        float trailZRot = 0.0F;
        Vec3 topAngleVec = new Vec3(0.0D, trailHeight, 0.0D).zRot(trailZRot);
        Vec3 bottomAngleVec = new Vec3(0.0D, -trailHeight, 0.0D).zRot(trailZRot);
        Vec3 drawFrom = entityIn.getTrailPosition(0, side, partialTicks);
        VertexConsumer vertexconsumer = bufferIn.getBuffer(RenderType.entityTranslucent(TRAIL_TEXTURE));
        for (int samples = 0; samples < sampleSize; ++samples) {
            Vec3 sample = entityIn.getTrailPosition(samples + 2, side, partialTicks);
            float u1 = (float) samples / (float) sampleSize;
            float u2 = u1 + 1.0F / (float) sampleSize;
            Vec3 draw1 = drawFrom;
            Vec3 draw2 = sample;
            PoseStack.Pose posestack$pose = poseStack.last();
            Matrix4f matrix4f = posestack$pose.pose();
            Matrix3f matrix3f = posestack$pose.normal();
            vertexconsumer.vertex(matrix4f, (float) (draw1.x + bottomAngleVec.x), (float) (draw1.y + bottomAngleVec.y), (float) (draw1.z + bottomAngleVec.z)).color(trailR, trailG, trailB, trailA).uv(u1, 1.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            vertexconsumer.vertex(matrix4f, (float) (draw2.x + bottomAngleVec.x), (float) (draw2.y + bottomAngleVec.y), (float) (draw2.z + bottomAngleVec.z)).color(trailR, trailG, trailB, trailA).uv(u2, 1.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            vertexconsumer.vertex(matrix4f, (float) (draw2.x + topAngleVec.x), (float) (draw2.y + topAngleVec.y), (float) (draw2.z + topAngleVec.z)).color(trailR, trailG, trailB, trailA).uv(u2, 0.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            vertexconsumer.vertex(matrix4f, (float) (draw1.x + topAngleVec.x), (float) (draw1.y + topAngleVec.y), (float) (draw1.z + topAngleVec.z)).color(trailR, trailG, trailB, trailA).uv(u1, 0.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLightIn).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            drawFrom = sample;
        }
    }

    public class LayerGlow extends RenderLayer<TeletorServant, ModelTeletorServant> {

        public LayerGlow(RenderLayerParent<TeletorServant, ModelTeletorServant> renderLayerParent) {
            super(renderLayerParent);
        }

        @Override
        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, TeletorServant entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            VertexConsumer ivertexbuilder = bufferIn.getBuffer(ACRenderTypes.getEyesAlphaEnabled(TEXTURE_GLOW));
            float alpha = (float) (1.0D + Math.sin(ageInTicks * 0.3F)) * 0.1F + 0.8F;
            this.getParentModel().renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, LivingEntityRenderer.getOverlayCoords(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, alpha);
        }
    }
}
