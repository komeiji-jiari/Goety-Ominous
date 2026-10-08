package com.qiuyue.goetyominous.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;

public class FlyStraightTowardsParticle extends TextureSheetParticle {
    private static final int OMINOUS_SPAWNING_START_COLOR = -12210434;
    private static final int OMINOUS_SPAWNING_END_COLOR = -1;
    private final double xStart;
    private final double yStart;
    private final double zStart;
    private final int startColor;
    private final int endColor;

    FlyStraightTowardsParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd,
                               int startColor, int endColor) {
        super(level, x, y, z);
        this.startColor = startColor;
        this.endColor = endColor;
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
        this.hasPhysics = false;
        this.lifetime = (int) (Math.random() * 5.0D) + 25;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void move(double xa, double ya, double za) {
        this.setBoundingBox(this.getBoundingBox().move(xa, ya, za));
        this.setLocationFromBoundingbox();
    }

    @Override
    public int getLightColor(float partialTick) {
        return 240;
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
        float progress = (float) this.age / (float) this.lifetime;
        float remaining = 1.0F - progress;
        this.x = this.xStart + this.xd * (double) remaining;
        this.y = this.yStart + this.yd * (double) remaining;
        this.z = this.zStart + this.zd * (double) remaining;
        int color = FastColor.ARGB32.lerp(progress, this.startColor, this.endColor);
        this.setColor((float) FastColor.ARGB32.red(color) / 255.0F,
                (float) FastColor.ARGB32.green(color) / 255.0F,
                (float) FastColor.ARGB32.blue(color) / 255.0F);
        this.setAlpha((float) FastColor.ARGB32.alpha(color) / 255.0F);
    }

    public static class OminousSpawnerProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public OminousSpawnerProvider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z,
                                       double xd, double yd, double zd) {
            FlyStraightTowardsParticle particle = new FlyStraightTowardsParticle(level, x, y, z, xd, yd, zd,
                    OMINOUS_SPAWNING_START_COLOR, OMINOUS_SPAWNING_END_COLOR);
            particle.scale(Mth.randomBetween(level.random, 3.0F, 5.0F));
            particle.pickSprite(this.sprites);
            return particle;
        }
    }
}
