package com.qiuyue.goetyominous.common.entities.hostile.grimmonarch;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;

public final class GrimMonarchFluidSurface {
    private GrimMonarchFluidSurface() {
    }

    public static double top(BlockGetter level, BlockPos pos) {
        FluidState fluid = level.getFluidState(pos);
        if (fluid.isEmpty()) {
            return Double.NEGATIVE_INFINITY;
        }
        if (!level.getFluidState(pos.below()).isEmpty()) {
            return Double.NEGATIVE_INFINITY;
        }
        return (float) pos.getY() + fluid.getHeight(level, pos);
    }

    public static double column(BlockGetter level, double x, double z, double low, double high) {
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        int blockX = Mth.floor(x);
        int blockZ = Mth.floor(z);
        int bottom = Mth.floor(low) - 1;
        for (int blockY = Mth.floor(high); blockY >= bottom; --blockY) {
            cursor.set(blockX, blockY, blockZ);
            double surface = top(level, cursor);
            if (surface >= low - 1.0E-6 && surface <= high + 1.0E-6) {
                return surface;
            }
        }
        return Double.NEGATIVE_INFINITY;
    }

    public static double beneath(BlockGetter level, AABB footprint, double low, double high) {
        double surface = Double.NEGATIVE_INFINITY;
        int minX = Mth.floor(footprint.minX + 1.0E-5);
        int maxX = Mth.floor(footprint.maxX - 1.0E-5);
        int minZ = Mth.floor(footprint.minZ + 1.0E-5);
        int maxZ = Mth.floor(footprint.maxZ - 1.0E-5);
        for (int x = minX; x <= maxX; ++x) {
            for (int z = minZ; z <= maxZ; ++z) {
                surface = Math.max(surface, column(level, (double) x + 0.5, (double) z + 0.5, low, high));
            }
        }
        return surface;
    }
}
