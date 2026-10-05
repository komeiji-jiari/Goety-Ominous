package com.qiuyue.goetyominous.client.particle;

import com.qiuyue.goetyominous.common.init.ModParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SmashParticleUtils {
    public static void spawnSmashAttackParticles(LevelAccessor level, BlockPos pos, int count) {
        Vec3 center = Vec3.atBottomCenterOf(pos).add(0.0D, 0.5D, 0.0D);
        BlockParticleOption dustPillar = new BlockParticleOption(ModParticleTypes.DUST_PILLAR.get(), level.getBlockState(pos));

        for (int i = 0; (float)i < (float)count / 3.0F; ++i) {
            double x = center.x + level.getRandom().nextGaussian() / 2.0D;
            double y = center.y;
            double z = center.z + level.getRandom().nextGaussian() / 2.0D;
            double xd = level.getRandom().nextGaussian() * 0.2F;
            double yd = level.getRandom().nextGaussian() * 0.2F;
            double zd = level.getRandom().nextGaussian() * 0.2F;
            level.addParticle(dustPillar, x, y, z, xd, yd, zd);
        }

        for (int j = 0; (float)j < (float)count / 1.5F; ++j) {
            double x = center.x + 3.5D * Math.cos(j) + level.getRandom().nextGaussian() / 2.0D;
            double y = center.y;
            double z = center.z + 3.5D * Math.sin(j) + level.getRandom().nextGaussian() / 2.0D;
            double xd = level.getRandom().nextGaussian() * 0.05F;
            double yd = level.getRandom().nextGaussian() * 0.05F;
            double zd = level.getRandom().nextGaussian() * 0.05F;
            level.addParticle(dustPillar, x, y, z, xd, yd, zd);
        }
    }
}
