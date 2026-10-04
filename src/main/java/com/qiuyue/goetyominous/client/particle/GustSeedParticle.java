package com.qiuyue.goetyominous.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import com.qiuyue.goetyominous.common.init.ModParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;

public class GustSeedParticle extends NoRenderParticle {
    private final double goetyominous$scale;
    private final int goetyominous$lifetime;
    private final int goetyominous$interval;

    GustSeedParticle(ClientLevel level, double x, double y, double z, double scale, int lifetime, int interval) {
        super(level, x, y, z);
        this.goetyominous$scale = scale;
        this.goetyominous$lifetime = lifetime;
        this.goetyominous$interval = interval;
    }

    @Override
    public void tick() {
        if (this.age % (this.goetyominous$interval + 1) == 0) {
            for (int i = 0; i < 3; ++i) {
                double dx = this.x + (this.random.nextDouble() - this.random.nextDouble()) * this.goetyominous$scale;
                double dy = this.y + (this.random.nextDouble() - this.random.nextDouble()) * this.goetyominous$scale;
                double dz = this.z + (this.random.nextDouble() - this.random.nextDouble()) * this.goetyominous$scale;
                this.level.addParticle(ModParticleTypes.GUST.get(), dx, dy, dz,
                        (double) ((float) this.age / (float) this.goetyominous$lifetime), 0.0D, 0.0D);
            }
        }
        if (this.age++ == this.goetyominous$lifetime) {
            this.remove();
        }
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final double scale;
        private final int lifetime;
        private final int interval;

        public Provider(double scale, int lifetime, int interval) {
            this.scale = scale;
            this.lifetime = lifetime;
            this.interval = interval;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z,
                                       double dx, double dy, double dz) {
            return new GustSeedParticle(level, x, y, z, this.scale, this.lifetime, this.interval);
        }
    }
}
