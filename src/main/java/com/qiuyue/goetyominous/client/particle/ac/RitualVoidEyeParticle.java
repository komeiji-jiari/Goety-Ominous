package com.qiuyue.goetyominous.client.particle.ac;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeRenderTypes;
import org.joml.Matrix3f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * Small glowing watcher eye circling the PureDark void cloud, ported from AC's
 * {@code VoidBeingEyeParticle} but self-anchored: it orbits the cloud anchor in a real
 * world-space ring (no entity, no camera-space offset hack) and spirals inward as the
 * ritual nears its end.
 */
@OnlyIn(Dist.CLIENT)
public class RitualVoidEyeParticle extends Particle {

    private static final ResourceLocation[] TEXTURES = {
            new ResourceLocation("goetyominous", "textures/particle/ritual_void_eye_0.png"),
            new ResourceLocation("goetyominous", "textures/particle/ritual_void_eye_1.png"),
            new ResourceLocation("goetyominous", "textures/particle/ritual_void_eye_2.png")
    };

    /** When the eyes start being drawn back into the cloud. */
    private static final int CONVERGE_START = RitualVoidCloudParticle.LIFETIME - 150;

    private final int textureIndex;
    private final double centerX;
    private final double centerY;
    private final double centerZ;
    private final float heightOffset;
    private final double angleSpeed;
    private final float animationOffset;
    private double radius;
    private double angle;

    public RitualVoidEyeParticle(ClientLevel world, double x, double y, double z,
                                 double ringRadius, int variant, double startAngle) {
        super(world, x, y, z);
        this.centerX = x;
        this.centerY = y;
        this.centerZ = z;
        this.textureIndex = Mth.clamp(variant, 0, TEXTURES.length - 1);
        this.radius = Math.max(ringRadius, 0.8);
        this.angle = startAngle;
        this.angleSpeed = (this.random.nextBoolean() ? 1 : -1) * (0.015 + this.random.nextDouble() * 0.02);
        this.heightOffset = (float) (this.random.nextFloat() * 1.4 - 1.1);
        this.animationOffset = this.random.nextFloat() * 6.2831855F;
        this.xd = 0.0;
        this.yd = 0.0;
        this.zd = 0.0;
        this.gravity = 0.0F;
        this.lifetime = RitualVoidCloudParticle.LIFETIME;
        this.setSize(2.0F, 2.0F);
        this.updateOrbit();
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.age > CONVERGE_START) {
            // Spiralling back into the cloud for the finale.
            this.radius *= 0.94;
            this.angle += this.angleSpeed * 2.5;
        } else {
            this.angle += this.angleSpeed;
        }
        this.updateOrbit();
    }

    private void updateOrbit() {
        this.x = this.centerX + Math.cos(this.angle) * this.radius;
        this.z = this.centerZ + Math.sin(this.angle) * this.radius;
        this.y = this.centerY + this.heightOffset
                + 0.15 * Math.sin(this.age * 0.08 + this.animationOffset);
    }

    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float partialTick) {
        this.alpha = RitualVoidCloudParticle.getAlphaFromAge(this.age, this.lifetime);
        Vec3 cameraPos = camera.getPosition();
        float f = (float) (Mth.lerp(partialTick, this.xo, this.x) - cameraPos.x());
        float f1 = (float) (Mth.lerp(partialTick, this.yo, this.y) - cameraPos.y());
        float f2 = (float) (Mth.lerp(partialTick, this.zo, this.z) - cameraPos.z());

        Quaternionf quaternion = camera.rotation();
        MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer buffer = buffers.getBuffer(ForgeRenderTypes.getUnlitTranslucent(TEXTURES[this.textureIndex]));
        Matrix3f normal = new Matrix3f();

        // Eyes tremble once they start being drawn in, like a creature growing agitated.
        float shakeX = this.age > CONVERGE_START
                ? 0.3F * (float) Math.sin((this.age + partialTick + 3.0F * this.animationOffset) * 0.54F) : 0.0F;
        float shakeY = this.age > CONVERGE_START
                ? -0.3F * (float) Math.sin((this.age + partialTick + 3.0F * this.animationOffset) * 0.54F + 2.0F) : 0.0F;

        float scale = 0.6F;
        Vector3f[] corners = {
                new Vector3f(-1.0F, -1.0F, -0.05F),
                new Vector3f(-1.0F, 1.0F, -0.05F),
                new Vector3f(1.0F, 1.0F, -0.05F),
                new Vector3f(1.0F, -1.0F, -0.05F)
        };
        for (int i = 0; i < 4; i++) {
            corners[i].add(shakeX, shakeY, 0.0F);
            corners[i].rotate(quaternion);
            corners[i].mul(scale);
            corners[i].add(f, f1, f2);
        }

        int light = 240;
        buffer.vertex(corners[0].x(), corners[0].y(), corners[0].z()).color(this.rCol, this.gCol, this.bCol, this.alpha)
                .uv(1.0F, 1.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0.0F, -1.0F, 0.0F).endVertex();
        buffer.vertex(corners[1].x(), corners[1].y(), corners[1].z()).color(this.rCol, this.gCol, this.bCol, this.alpha)
                .uv(1.0F, 0.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0.0F, -1.0F, 0.0F).endVertex();
        buffer.vertex(corners[2].x(), corners[2].y(), corners[2].z()).color(this.rCol, this.gCol, this.bCol, this.alpha)
                .uv(0.0F, 0.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0.0F, -1.0F, 0.0F).endVertex();
        buffer.vertex(corners[3].x(), corners[3].y(), corners[3].z()).color(this.rCol, this.gCol, this.bCol, this.alpha)
                .uv(0.0F, 1.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, 0.0F, -1.0F, 0.0F).endVertex();
        buffers.endBatch();
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
            return new RitualVoidEyeParticle(level, x, y, z, xSpeed, (int) ySpeed, zSpeed);
        }
    }
}
