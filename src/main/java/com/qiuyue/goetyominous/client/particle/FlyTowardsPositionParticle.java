package com.qiuyue.goetyominous.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

public class FlyTowardsPositionParticle extends TextureSheetParticle {
    private final double xStart;
    private final double yStart;
    private final double zStart;
    private final boolean isGlowing;
    private final LifetimeAlpha lifetimeAlpha;

    FlyTowardsPositionParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
        this(level, x, y, z, xd, yd, zd, false, LifetimeAlpha.ALWAYS_OPAQUE);
    }

    FlyTowardsPositionParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, boolean isGlowing, LifetimeAlpha lifetimeAlpha) {
        super(level, x, y, z);
        this.isGlowing = isGlowing;
        this.lifetimeAlpha = lifetimeAlpha;
        this.setAlpha(lifetimeAlpha.startAlpha());
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.xStart = x;
        this.yStart = y;
        this.zStart = z;
        this.x = x + xd;
        this.y = y + yd;
        this.z = z + zd;
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.quadSize = 0.1F * (this.random.nextFloat() * 0.5F + 0.2F);
        float f = this.random.nextFloat() * 0.6F + 0.4F;
        this.rCol = 0.9F * f;
        this.gCol = 0.9F * f;
        this.bCol = f;
        this.hasPhysics = false;
        this.lifetime = (int) (Math.random() * 10.0D) + 30;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return this.lifetimeAlpha.isOpaque()
                ? ParticleRenderType.PARTICLE_SHEET_OPAQUE
                : ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void move(double xa, double ya, double za) {
        this.setBoundingBox(this.getBoundingBox().move(xa, ya, za));
        this.setLocationFromBoundingbox();
    }

    @Override
    public int getLightColor(float partialTick) {
        if (this.isGlowing) {
            return 240;
        }
        int packedLight = super.getLightColor(partialTick);
        float f = (float) this.age / (float) this.lifetime;
        f *= f;
        f *= f;
        int blockLight = packedLight & 0xFF;
        int skyLight = packedLight >> 16 & 0xFF;
        skyLight += (int) (f * 15.0F * 16.0F);
        if (skyLight > 240) {
            skyLight = 240;
        }
        return blockLight | skyLight << 16;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }
        float f = (float) this.age / (float) this.lifetime;
        f = 1.0F - f;
        float f1 = 1.0F - f;
        f1 *= f1;
        f1 *= f1;
        this.x = this.xStart + this.xd * (double) f;
        this.y = this.yStart + this.yd * (double) f - (double) (f1 * 1.2F);
        this.z = this.zStart + this.zd * (double) f;
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTick) {
        this.setAlpha(this.lifetimeAlpha.currentAlphaForAge(this.age, this.lifetime, partialTick));
        super.render(buffer, camera, partialTick);
    }

    public record LifetimeAlpha(float startAlpha, float endAlpha, float startAtNormalizedAge, float endAtNormalizedAge) {
        public static final LifetimeAlpha ALWAYS_OPAQUE = new LifetimeAlpha(1.0F, 1.0F, 0.0F, 1.0F);

        public boolean isOpaque() {
            return this.startAlpha >= 1.0F && this.endAlpha >= 1.0F;
        }

        public float currentAlphaForAge(int age, int lifetime, float partialTick) {
            if (Mth.equal(this.startAlpha, this.endAlpha)) {
                return this.startAlpha;
            }
            float f = Mth.inverseLerp(((float) age + partialTick) / (float) lifetime,
                    this.startAtNormalizedAge, this.endAtNormalizedAge);
            return Mth.clampedLerp(this.startAlpha, this.endAlpha, f);
        }
    }

    public static class VaultConnectionProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public VaultConnectionProvider(SpriteSet sprite) {
            this.sprite = sprite;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
            FlyTowardsPositionParticle particle = new FlyTowardsPositionParticle(level, x, y, z, xd, yd, zd, true,
                    new LifetimeAlpha(0.0F, 0.6F, 0.25F, 1.0F));
            particle.scale(1.5F);
            particle.pickSprite(this.sprite);
            return particle;
        }
    }
}
