package com.qiuyue.goetyominous.client.particle.ac;

import com.github.alexmodguy.alexscaves.client.particle.MushroomCloudParticle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.SimpleParticleType;


public class NucleeperMushroomCloudParticle extends MushroomCloudParticle {

    public NucleeperMushroomCloudParticle(ClientLevel level, double x, double y, double z, float size, boolean pink) {
        super(level, x, y, z, size, pink);
    }

    @Override
    public int getLightColor(float partialTick) {
        return 0xF000F0;
    }

    
    public static class Provider implements ParticleProvider<SimpleParticleType> {

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new NucleeperMushroomCloudParticle(level, x, y, z, (float) xSpeed, ySpeed != 0.0);
        }
    }
}
