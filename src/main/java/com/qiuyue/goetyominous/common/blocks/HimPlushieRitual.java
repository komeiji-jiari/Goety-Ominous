package com.qiuyue.goetyominous.common.blocks;

import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.qiuyue.goetyominous.common.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public final class HimPlushieRitual {

    public static final int LIGHTNING_RADIUS = 3;
    public static final float CONVERT_CHANCE = 0.06F;
    public static final int BURST_COUNT = 80;

    private HimPlushieRitual() {
    }

    public static boolean isPlushie(BlockState state) {
        return state.getBlock() instanceof PlushieBlock
                || state.getBlock() instanceof com.Polarice3.Goety.common.blocks.PlushieBlock;
    }

    public static boolean isValidStructure(Level level, BlockPos plushiePos) {
        if (!isPlushie(level.getBlockState(plushiePos))) {
            return false;
        }

        BlockPos second = plushiePos.below();
        if (!level.getBlockState(second).is(Blocks.NETHERRACK)) {
            return false;
        }
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockState side = level.getBlockState(second.relative(dir));
            if (!side.is(Blocks.REDSTONE_TORCH) && !side.is(Blocks.REDSTONE_WALL_TORCH)) {
                return false;
            }
        }

        BlockPos base = plushiePos.below(2);
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (!level.getBlockState(base.offset(dx, 0, dz)).is(Blocks.GOLD_BLOCK)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void onLightningStrike(ServerLevel level, BlockPos center) {
        int r = LIGHTNING_RADIUS;
        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-r, -r, -r), center.offset(r, r, r))) {
            BlockPos plushiePos = pos.immutable();
            if (!isPlushie(level.getBlockState(plushiePos))) {
                continue;
            }
            if (!isValidStructure(level, plushiePos)) {
                continue;
            }
            if (level.random.nextFloat() < CONVERT_CHANCE) {
                convertToHim(level, plushiePos);
            }
        }
    }

    public static void convertToHim(ServerLevel level, BlockPos pos) {
        BlockState old = level.getBlockState(pos);
        BlockState him = ModBlocks.PLUSHIE_HIM.get().defaultBlockState();
        if (old.hasProperty(PlushieBlock.ROTATION)) {
            him = him.setValue(PlushieBlock.ROTATION, old.getValue(PlushieBlock.ROTATION));
        }
        if (old.hasProperty(PlushieBlock.WATERLOGGED)) {
            him = him.setValue(PlushieBlock.WATERLOGGED, old.getValue(PlushieBlock.WATERLOGGED));
        }
        level.setBlockAndUpdate(pos, him);

        level.sendParticles(ModParticleTypes.ELECTRIC.get(),
                pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                BURST_COUNT,
                0.5D, 0.5D, 0.5D,
                0.3D);
    }
}
