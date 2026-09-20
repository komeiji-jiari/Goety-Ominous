package com.qiuyue.goetyominous.client.particle.ac;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.common.init.ac.AcParticles;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeRenderTypes;
import org.joml.Matrix3f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * Self-anchored port of AC's {@code VoidBeingCloudParticle} for the PureDark ritual.
 * <p>
 * The server drops one of these on the altar-top (position + speeds payload: x = size,
 * y = how far the tendrils should dangle). It does NOT lock onto any entity like AC's
 * cloud locks onto an {@code UnderzealotSacrifice}: it just sits at its spawn point and
 * lives {@link #LIFETIME} ticks, so nothing can despawn it early.
 * <p>
 * On its first tick it scatters orbiting eyes and downward-hanging tendrils around its
 * anchor; every tick it redraws a black-alpha "void" texture via {@link RitualNoise}.
 */
@OnlyIn(Dist.CLIENT)
public class RitualVoidCloudParticle extends Particle {

    /** How many ticks the whole spectacle (cloud + spawned eyes + tendrils) runs. */
    public static final int LIFETIME = 380;

    /** Shared fade for every piece of the effect so they appear/disappear as one. */
    public static float getAlphaFromAge(int age, int lifetime) {
        float fadeIn = Math.min(20, age) / 20.0F;
        float fadeOut = age > lifetime - 10 ? (float) (lifetime - age) / 10.0F : 1.0F;
        return fadeIn * fadeOut;
    }

    private static int currentlyUsedTextures = 0;

    private final int textureSize;
    private final DynamicTexture dynamicTexture;
    private final RenderType renderType;
    private final float dropY;
    private final int id;
    private float size;
    private boolean requiresUpload = true;
    private boolean spawnedExtras = false;
    private int idleSmokeTime = 30;

    public RitualVoidCloudParticle(ClientLevel world, double x, double y, double z, int cloudSize, double dropY) {
        super(world, x, y, z);
        this.gravity = 0.0F;
        this.x = x;
        this.y = y;
        this.z = z;
        this.xd = 0.0;
        this.yd = 0.0;
        this.zd = 0.0;
        this.lifetime = LIFETIME;
        int sizeParam = Mth.clamp(cloudSize, 0, 2);
        this.size = sizeParam + 1.0F;
        this.setSize(this.size, this.size);
        this.textureSize = 32 + sizeParam * 32;
        this.dropY = (float) (dropY > 0 ? dropY : 4.0);
        this.dynamicTexture = new DynamicTexture(this.textureSize, this.textureSize, true);
        this.id = currentlyUsedTextures++;
        ResourceLocation texture = new ResourceLocation("goetyominous", "ritual_void_cloud_" + this.id);
        Minecraft.getInstance().textureManager.register(texture, this.dynamicTexture);
        this.renderType = ForgeRenderTypes.getUnlitTranslucent(texture);
    }

    @Override
    public void tick() {
        if (this.age <= 0 && !this.spawnedExtras) {
            this.onSpawn();
            this.spawnedExtras = true;
        }
        super.tick();
        this.xd *= 0.97;
        this.yd *= 0.97;
        this.zd *= 0.97;
        this.updateTexture();
        // Occasional wisp spilling out of the cloud keeps it feeling alive.
        if (this.idleSmokeTime-- <= 0) {
            this.idleSmokeTime = 50 + this.random.nextInt(40);
            this.level.addParticle(ParticleTypes.SMOKE,
                    this.x + (this.random.nextFloat() - 0.5F) * this.size,
                    this.y - 0.5 + (this.random.nextFloat() - 0.5F) * this.size,
                    this.z + (this.random.nextFloat() - 0.5F) * this.size,
                    (this.random.nextFloat() - 0.5F) * 0.1, 0.03, (this.random.nextFloat() - 0.5F) * 0.1);
        }
    }

    /**
     * Scatter the eyes around the cloud and hang the tendrils from it, all anchored to
     * this cloud's own position (no entity reference anywhere).
     */
    private void onSpawn() {
        int circleOffset = this.random.nextInt(360);
        int eyes = 3 + this.random.nextInt(2);
        for (int j = 0; j < eyes; j++) {
            double ringRadius = (0.55 + this.random.nextFloat() * 0.85) * this.size;
            double startAngle = Math.toRadians(circleOffset + j * 360.0 / eyes);
            this.level.addParticle(AcParticles.RITUAL_VOID_EYE.get(),
                    this.x, this.y, this.z, ringRadius, this.random.nextInt(3), startAngle);
        }
        int tendrils = 6 + this.random.nextInt(3);
        for (int j = 0; j < tendrils; j++) {
            int seekByTime = 200 / tendrils * (j + 1);
            this.level.addParticle(AcParticles.RITUAL_VOID_TENDRIL.get(),
                    this.x, this.y, this.z, seekByTime, this.dropY, 0.0);
        }
    }

    /**
     * Redraw the per-particle noise texture. The (x, y, z) of the noise field are
     * (pixel, age, pixel) so each tick samples a fresh, smoothly-evolving cross-section:
     * the cloud boils instead of flickering.
     */
    private void updateTexture() {
        int center = this.textureSize / 2;
        int black = 0;
        double radiusSq = (double) (center * center) * getAlphaFromAge(this.age, this.lifetime);
        for (int i = 0; i < this.textureSize; i++) {
            for (int j = 0; j < this.textureSize; j++) {
                double d0 = center - i;
                double d1 = center - j;
                double d2 = d0 * d0 + d1 * d1;
                double f1 = RitualNoise.noise(i / 15.0, this.age / 15.0, j / 15.0);
                double denom = radiusSq * (1.0 - f1 * f1);
                double alpha = denom <= 0 ? 0 : (1.0 - d2 / denom);
                if (alpha <= 0.0) {
                    this.dynamicTexture.getPixels().setPixelRGBA(j, i, 0);
                    continue;
                }
                int a = (int) Math.min(alpha * 255.0, 255.0);
                this.dynamicTexture.getPixels().setPixelRGBA(j, i, FastColor.ARGB32.color(a, black, black, black));
            }
        }
        this.dynamicTexture.upload();
    }

    @Override
    public void remove() {
        this.removed = true;
        this.dynamicTexture.close();
        --currentlyUsedTextures;
    }

    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float partialTick) {
        if (this.requiresUpload) {
            this.updateTexture();
            this.requiresUpload = false;
        }
        Vec3 cameraPos = camera.getPosition();
        float f = (float) (Mth.lerp(partialTick, this.xo, this.x) - cameraPos.x());
        float f1 = (float) (Mth.lerp(partialTick, this.yo, this.y) - cameraPos.y());
        float f2 = (float) (Mth.lerp(partialTick, this.zo, this.z) - cameraPos.z());

        Quaternionf quaternion;
        if (this.roll == 0.0F) {
            quaternion = camera.rotation();
        } else {
            quaternion = new Quaternionf(camera.rotation());
            float roll = Mth.lerp(partialTick, this.oRoll, this.roll);
            quaternion.mul(new Quaternionf().rotationZ(roll));
        }

        MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer buffer = buffers.getBuffer(this.renderType);
        Matrix3f normal = new Matrix3f();

        float bob = 0.2F * (float) Math.sin((this.age + partialTick) * 0.1);
        Vector3f[] corners = {
                new Vector3f(-1.0F, -1.0F, 0.0F),
                new Vector3f(-1.0F, 1.0F, 0.0F),
                new Vector3f(1.0F, 1.0F, 0.0F),
                new Vector3f(1.0F, -1.0F, 0.0F)
        };
        for (int i = 0; i < 4; i++) {
            corners[i].add(0.0F, bob, 0.0F);
            corners[i].rotate(quaternion);
            corners[i].mul(this.size);
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
            return new RitualVoidCloudParticle(level, x, y, z, (int) xSpeed, ySpeed);
        }
    }
}
