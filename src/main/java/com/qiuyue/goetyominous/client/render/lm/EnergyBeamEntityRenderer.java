package com.qiuyue.goetyominous.client.render.lm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.common.entities.ally.lm.projectile.EnergyBeamEntity;
import net.miauczel.legendary_monsters.Particle.custom.LightningBoltData;
import net.miauczel.legendary_monsters.Particle.custom.LightningRender;
import net.miauczel.legendary_monsters.entity.client.Render.LMRenderTypes;
import net.miauczel.legendary_monsters.util.MathUtils;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector4f;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class EnergyBeamEntityRenderer extends EntityRenderer<EnergyBeamEntity> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(
            "legendary_monsters", "textures/entity/energy_beam.png");

    private final Map<UUID, LightningRender> lightningRenderMap = new HashMap<>();
    private boolean clearerView = true;

    public EnergyBeamEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(EnergyBeamEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        this.clearerView = entity.caster instanceof Player
                && Minecraft.getInstance().player == entity.caster
                && Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON;
        double collidePosX = entity.prevCollidePosX + (entity.collidePosX - entity.prevCollidePosX) * (double) partialTicks;
        double collidePosY = entity.prevCollidePosY + (entity.collidePosY - entity.prevCollidePosY) * (double) partialTicks;
        double collidePosZ = entity.prevCollidePosZ + (entity.collidePosZ - entity.prevCollidePosZ) * (double) partialTicks;
        double posX = entity.xo + (entity.getX() - entity.xo) * (double) partialTicks;
        double posY = entity.yo + (entity.getY() - entity.yo) * (double) partialTicks;
        double posZ = entity.zo + (entity.getZ() - entity.zo) * (double) partialTicks;
        int frame = Mth.floor((float) (((float) (entity.appear.getTimer() - 1) + partialTicks) * 2.0F));
        if (frame < 0) {
            frame = 6;
        }
        VertexConsumer vertexConsumer = bufferSource.getBuffer(LMRenderTypes.getGlowingEffect(this.getTextureLocation(entity)));
        this.renderStart(frame, poseStack, vertexConsumer, packedLight);
        poseStack.pushPose();
        poseStack.translate(collidePosX - posX, collidePosY - posY, collidePosZ - posZ);
        this.renderEnd(frame, entity.blockSide, poseStack, vertexConsumer, packedLight);
        poseStack.popPose();
        this.renderLightningBeam(entity, partialTicks, poseStack, bufferSource);
    }

    private void renderLightningBeam(EnergyBeamEntity entity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource) {
        if (entity.tickCount > 20) {
            float startX = (float) Mth.lerp((double) partialTicks, entity.xOld, entity.getX());
            float startY = (float) Mth.lerp((double) partialTicks, entity.yOld, entity.getY());
            float startZ = (float) Mth.lerp((double) partialTicks, entity.zOld, entity.getZ());
            Vec3 start = new Vec3(startX, startY, startZ);
            Vec3 end = new Vec3(entity.collidePosX, entity.collidePosY, entity.collidePosZ);
            poseStack.pushPose();
            poseStack.translate(-startX, -startY, -startZ);
            LightningBoltData.BoltRenderInfo bigBoltData = new LightningBoltData.BoltRenderInfo(
                    1.0F, 0.1F, 0.1F, 0.25F, new Vector4f(1.0F, 1.0F, 1.0F, 0.7F), 0.6F);
            LightningBoltData bigBolt = new LightningBoltData(bigBoltData, start, end, 3)
                    .size(0.5F).lifespan(1).spawn(LightningBoltData.SpawnFunction.NO_DELAY).fade(LightningBoltData.FadeFunction.NONE);
            LightningBoltData.BoltRenderInfo smallBoltData = new LightningBoltData.BoltRenderInfo(
                    0.25F, 0.1F, 0.1F, 0.25F, new Vector4f(156.0F / 255.0F, 246.0F / 255.0F, 1.0F, 0.7F), 0.35F);
            LightningBoltData smallBolt = new LightningBoltData(smallBoltData, start, end, 6)
                    .size(0.2F).lifespan(1).spawn(LightningBoltData.SpawnFunction.NO_DELAY).fade(LightningBoltData.FadeFunction.NONE);
            LightningRender lightningRender = this.getLightningRender(entity.getUUID());
            lightningRender.update(this, bigBolt, partialTicks);
            lightningRender.update(this, smallBolt, partialTicks);
            lightningRender.render(partialTicks, poseStack, bufferSource);
            poseStack.popPose();
            if (entity.isRemoved() && this.lightningRenderMap.containsKey(entity.getUUID())) {
                this.lightningRenderMap.remove(entity.getUUID());
            }
        }
    }

    private LightningRender getLightningRender(UUID uuid) {
        if (this.lightningRenderMap.get(uuid) == null) {
            this.lightningRenderMap.put(uuid, new LightningRender());
        }
        return this.lightningRenderMap.get(uuid);
    }

    private void renderFlatQuad(int frame, PoseStack poseStack, VertexConsumer builder, int packedLight) {
        float minU = 0.0625F * (float) frame;
        float minV = 0.0F;
        float maxU = minU + 0.0625F;
        float maxV = minV + 0.5F;
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix = pose.pose();
        Matrix3f normals = pose.normal();
        this.drawVertex(matrix, normals, builder, -1.3F, -1.3F, 0.0F, minU, minV, 1.0F, packedLight);
        this.drawVertex(matrix, normals, builder, -1.3F, 1.3F, 0.0F, minU, maxV, 1.0F, packedLight);
        this.drawVertex(matrix, normals, builder, 1.3F, 1.3F, 0.0F, maxU, maxV, 1.0F, packedLight);
        this.drawVertex(matrix, normals, builder, 1.3F, -1.3F, 0.0F, maxU, minV, 1.0F, packedLight);
    }

    private void renderStart(int frame, PoseStack poseStack, VertexConsumer builder, int packedLight) {
        if (!this.clearerView) {
            poseStack.pushPose();
            poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
            this.renderFlatQuad(frame, poseStack, builder, packedLight);
            poseStack.popPose();
        }
    }

    private void renderEnd(int frame, Direction side, PoseStack poseStack, VertexConsumer builder, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        this.renderFlatQuad(frame, poseStack, builder, packedLight);
        poseStack.popPose();
        if (side != null) {
            poseStack.pushPose();
            Quaternionf sideQuat = new Quaternionf(side.getRotation());
            sideQuat.mul(MathUtils.quatFromRotationXYZ(90.0F, 0.0F, 0.0F, true));
            poseStack.mulPose(sideQuat);
            poseStack.translate(0.0F, 0.0F, -0.01F);
            this.renderFlatQuad(frame, poseStack, builder, packedLight);
            poseStack.popPose();
        }
    }

    private void drawVertex(Matrix4f matrix, Matrix3f normals, VertexConsumer builder, float offsetX, float offsetY, float offsetZ, float textureX, float textureY, float alpha, int packedLight) {
        builder.vertex(matrix, offsetX, offsetY, offsetZ).color(1.0F, 1.0F, 1.0F, alpha).uv(textureX, textureY)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normals, 0.0F, 1.0F, 0.0F).endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(EnergyBeamEntity entity) {
        return TEXTURE;
    }
}
