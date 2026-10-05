package com.qiuyue.goetyominous.client.particle;

import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DustPillarProvider implements ParticleProvider<BlockParticleOption> {
    @Nullable
    @Override
    public Particle createParticle(BlockParticleOption option, ClientLevel level, double x, double y, double z, double vx, double vy, double vz) {
        Particle particle = createTerrainParticle(option, level, x, y, z, vx, vy, vz);
        if (particle != null) {
            particle.setParticleSpeed(level.random.nextGaussian() / 30.0D, vy + level.random.nextGaussian() / 2.0D, level.random.nextGaussian() / 30.0D);
            particle.setLifetime(level.random.nextInt(20) + 20);
        }
        return particle;
    }

    @Nullable
    private static Particle createTerrainParticle(BlockParticleOption option, ClientLevel level, double x, double y, double z, double vx, double vy, double vz) {
        BlockState state = option.getState();
        if (state.isAir() || state.is(Blocks.MOVING_PISTON)) {
            return null;
        }
        return new TerrainParticle(level, x, y, z, vx, vy, vz, state).updateSprite(state, option.getPos());
    }
}
