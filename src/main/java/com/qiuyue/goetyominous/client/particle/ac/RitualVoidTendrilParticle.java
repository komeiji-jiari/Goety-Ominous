package com.qiuyue.goetyominous.client.particle.ac;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

/**
 * Ink tendril hanging from the PureDark void cloud down to the altar, ported from AC's
 * {@code VoidBeingTendrilParticle} but self-anchored. Its root sits at the cloud anchor
 * and its tip droops toward a point on the altar top directly below; near the end of the
 * ritual the tip is pulled back up into the cloud. No target entity is ever tracked.
 */
@OnlyIn(Dist.CLIENT)
public class RitualVoidTendrilParticle extends Particle {

    private static final ResourceLocation TENDRIL_TEXTURE =
            new ResourceLocation("goetyominous", "textures/particle/ritual_void_tendril.png");

    /** When tendril tips start lifting back up into the cloud. */
    private static final int RETREAT_START = RitualVoidCloudParticle.LIFETIME - 120;

    private final double rootX;
    private final double rootY;
    private final double rootZ;
    private final double altarY;
    private final float animationOffset;
    private final int seekByTime;
    private double tipX;
    private double tipY;
    private double tipZ;
    private double prevTipX;
    private double prevTipY;
    private double prevTipZ;
    private Vec3 wander;

    public RitualVoidTendrilParticle(ClientLevel world, double x, double y, double z, int seekByTime, double dropY) {
        super(world, x, y, z);
        this.rootX = x;
        this.rootY = y;
        this.rootZ = z;
        this.altarY = y - (dropY > 0 ? dropY : 4.0);
        this.seekByTime = Math.max(1, seekByTime);
        this.animationOffset = (float) (this.random.nextFloat() * Math.PI);
        this.tipX = x;
        this.tipY = y;
        this.tipZ = z;
        this.prevTipX = x;
        this.prevTipY = y;
        this.prevTipZ = z;
        this.wander = this.altarPoint();
        this.xd = 0.0;
        this.yd = 0.0;
        this.zd = 0.0;
        this.gravity = 0.0F;
        this.lifetime = RitualVoidCloudParticle.LIFETIME;
        this.setSize(1.0F, 1.0F);
    }

    /** A loose aim point a little off the altar centre so the tendrils fan out. */
    private Vec3 altarPoint() {
        return new Vec3(
                this.rootX + (this.random.nextFloat() - 0.5F) * 1.2,
                this.altarY,
                this.rootZ + (this.random.nextFloat() - 0.5F) * 1.2);
    }

    @Override
    public void tick() {
        super.tick();
        this.prevTipX = this.tipX;
        this.prevTipY = this.tipY;
        this.prevTipZ = this.tipZ;
        if (this.age < this.seekByTime) {
            return;
        }
        if (this.random.nextFloat() < 0.05F) {
            this.wander = this.altarPoint();
        }
        // Near the end the aim point rises until the tendril is sucked back into the cloud.
        double lift = 0.0;
        if (this.age > RETREAT_START) {
            lift = (double) (this.age - RETREAT_START) / (double) (this.lifetime - RETREAT_START)
                    * (this.rootY - 1.0 - this.altarY);
        }
        double tx = this.wander.x + Math.sin(this.age * 0.05 + this.animationOffset * 2.0) * 0.1;
        double ty = this.wander.y + lift + Math.sin(this.age * 0.07 + this.animationOffset) * 0.1;
        double tz = this.wander.z + Math.cos(this.age * 0.05 + this.animationOffset) * 0.1;
        Vec3 delta = new Vec3(tx - this.tipX, ty - this.tipY, tz - this.tipZ);
        if (delta.length() > 1.0) {
            delta = delta.normalize();
        }
        this.tipX += delta.x * 0.2;
        this.tipY += delta.y * 0.2;
        this.tipZ += delta.z * 0.2;
    }

    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float partialTick) {
        Vec3 cameraPos = camera.getPosition();
        float width = 1.1F;
        float rootBob = 0.2F * (float) Math.sin((this.age + partialTick + this.animationOffset) * 0.1F);
        MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer buffer = buffers.getBuffer(RenderType.itemEntityTranslucentCull(TENDRIL_TEXTURE));
        PoseStack poseStack = new PoseStack();
        poseStack.pushPose();
        poseStack.translate(this.rootX - cameraPos.x, this.rootY - cameraPos.y + rootBob, this.rootZ - cameraPos.z);
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix4f = pose.pose();
        Matrix3f matrix3f = pose.normal();

        float sampleCount = 20.0F;
        Vec3 topAngleVec = new Vec3(0.0, width, 0.0);
        Vec3 bottomAngleVec = new Vec3(0.0, -width, 0.0);
        Vec3 drawFrom = new Vec3(0.0, 0.0, 0.0);
        float overallAlpha = RitualVoidCloudParticle.getAlphaFromAge(this.age, this.lifetime);
        int light = 240;
        for (int samples = 1; samples <= sampleCount; samples++) {
            float sampleScale = samples / sampleCount;
            float wiggleAmount = (float) Math.sin(sampleScale * Math.PI);
            Vec3 drawTo = this.getTendrilPosition(sampleScale, wiggleAmount, partialTick);
            float u1 = (samples - 1) / sampleCount;
            float u2 = samples / sampleCount;
            // Tip fades out first so the tendrils look like they dissolve into the void.
            float startA = Math.min(1.0F, samples / (sampleCount - 8.0F)) * overallAlpha;
            float endA = Math.min(1.0F, (samples + 1) / (sampleCount - 8.0F)) * overallAlpha;
            buffer.vertex(matrix4f,
                            (float) drawFrom.x + (float) bottomAngleVec.x,
                            (float) drawFrom.y + (float) bottomAngleVec.y,
                            (float) drawFrom.z + (float) bottomAngleVec.z)
                    .color(1.0F, 1.0F, 1.0F, startA).uv(u1, 1.0F)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            buffer.vertex(matrix4f,
                            (float) drawTo.x + (float) bottomAngleVec.x,
                            (float) drawTo.y + (float) bottomAngleVec.y,
                            (float) drawTo.z + (float) bottomAngleVec.z)
                    .color(1.0F, 1.0F, 1.0F, endA).uv(u2, 1.0F)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            buffer.vertex(matrix4f,
                            (float) drawTo.x + (float) topAngleVec.x,
                            (float) drawTo.y + (float) topAngleVec.y,
                            (float) drawTo.z + (float) topAngleVec.z)
                    .color(1.0F, 1.0F, 1.0F, endA).uv(u2, 0.0F)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            buffer.vertex(matrix4f,
                            (float) drawFrom.x + (float) topAngleVec.x,
                            (float) drawFrom.y + (float) topAngleVec.y,
                            (float) drawFrom.z + (float) topAngleVec.z)
                    .color(1.0F, 1.0F, 1.0F, startA).uv(u1, 0.0F)
                    .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
            drawFrom = drawTo;
        }
        buffers.endBatch();
        poseStack.popPose();
    }

    /** Ribbon point at fraction {@code f} of the way from root to (interpolated) tip. */
    private Vec3 getTendrilPosition(float f, float wiggleAmount, float partialTick) {
        float ageSmooth = ((float) this.age + partialTick) * 0.04F;
        Vec3 wiggleVec = new Vec3(
                Math.sin(ageSmooth + f + this.animationOffset),
                Math.cos(ageSmooth - 1.5707964F + f + this.animationOffset),
                -Math.cos(ageSmooth + f + this.animationOffset)).scale(wiggleAmount * 0.2F);
        Vec3 lerpedTip = new Vec3(
                Mth.lerp(partialTick, this.prevTipX, this.tipX),
                Mth.lerp(partialTick, this.prevTipY, this.tipY),
                Mth.lerp(partialTick, this.prevTipZ, this.tipZ));
        return lerpedTip.subtract(this.rootX, this.rootY, this.rootZ).scale(f).add(wiggleVec);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new RitualVoidTendrilParticle(level, x, y, z, (int) xSpeed, ySpeed);
        }
    }
}
