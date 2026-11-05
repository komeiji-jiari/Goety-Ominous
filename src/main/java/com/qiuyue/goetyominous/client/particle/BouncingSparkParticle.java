package com.qiuyue.goetyominous.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class BouncingSparkParticle extends TextureSheetParticle {
    private final float targetR;
    private final float targetG;
    private final float targetB;
    private final int colorTime;
    private boolean touchedGround;
    private float bounciness;

    protected BouncingSparkParticle(ClientLevel level, double x, double y, double z,
                                    double dx, double dy, double dz) {
        super(level, x, y, z, dx, dy, dz);
        this.scale(this.random.nextFloat() * 0.65F + 0.4F);
        this.lifetime = 30 + this.random.nextInt(50);
        this.gravity = 0.8F;
        this.friction = 0.99F;
        this.quadSize = 0.0625F;
        this.targetR = 0.9F + this.random.nextFloat() * 0.1F;
        this.targetG = 0.6F * (0.9F + this.random.nextFloat() * 0.1F);
        this.targetB = 0.3F * (0.9F + this.random.nextFloat() * 0.1F);
        this.bounciness = 0.75F + this.random.nextFloat() * 0.15F;
        this.colorTime = 5 + this.random.nextInt(20);
    }

    @Override
    public void tick() {
        if (!this.touchedGround && this.lifetime < 40) {
            this.lifetime++;
        }
        float t = Mth.clamp((float) this.age / (float) this.colorTime, 0.0F, 1.0F);
        this.rCol = Mth.lerp(t, 1.0F, this.targetR);
        this.gCol = Mth.lerp(t, 1.0F, this.targetG);
        this.bCol = Mth.lerp(t, 1.0F, this.targetB);
        if (this.onGround) {
            this.touchedGround = true;
            this.yd *= -this.bounciness;
            this.bounciness *= 0.9F;
            this.quadSize *= 0.95F;
        }
        super.tick();
        if (new Vec3(this.x - this.xo, this.y - this.yo, this.z - this.zo).length() < 0.001D) {
            this.remove();
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public int getLightColor(float partialTick) {
        return 0xF000F0;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z,
                                       double dx, double dy, double dz) {
            BouncingSparkParticle particle = new BouncingSparkParticle(level, x, y, z, dx, dy, dz);
            particle.pickSprite(this.sprites);
            return particle;
        }
    }
}
