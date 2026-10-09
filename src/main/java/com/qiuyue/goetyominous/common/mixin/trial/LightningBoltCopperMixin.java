package com.qiuyue.goetyominous.common.mixin.trial;

import com.qiuyue.goetyominous.common.blocks.trial.OminousWeatheringCopper;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightningRodBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightningBolt.class)
public class LightningBoltCopperMixin {

    @Inject(method = "clearCopperOnLightningStrike(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)V", at = @At("TAIL"))
    private static void goetyominous$clearOminousCopper(Level level, BlockPos pos, CallbackInfo ci) {
        BlockState state = level.getBlockState(pos);
        BlockPos origin;
        BlockState originState;
        if (state.is(Blocks.LIGHTNING_ROD)) {
            origin = pos.relative(state.getValue(LightningRodBlock.FACING).getOpposite());
            originState = level.getBlockState(origin);
        } else {
            origin = pos;
            originState = state;
        }
        if (!(originState.getBlock() instanceof OminousWeatheringCopper)) {
            return;
        }
        level.setBlockAndUpdate(origin, OminousWeatheringCopper.getFirst(level.getBlockState(origin)));
        BlockPos.MutableBlockPos cursor = pos.mutable();
        int walks = level.random.nextInt(3) + 3;
        for (int i = 0; i < walks; ++i) {
            goetyominous$randomWalkCleaningCopper(level, origin, cursor, level.random.nextInt(8) + 1);
        }
    }

    @Unique
    private static void goetyominous$randomWalkCleaningCopper(Level level, BlockPos origin, BlockPos.MutableBlockPos cursor, int length) {
        cursor.set(origin);
        for (int i = 0; i < length; ++i) {
            Optional<BlockPos> step = goetyominous$randomStepCleaningCopper(level, cursor);
            if (step.isEmpty()) {
                return;
            }
            cursor.set(step.get());
        }
    }

    @Unique
    private static Optional<BlockPos> goetyominous$randomStepCleaningCopper(Level level, BlockPos pos) {
        for (BlockPos candidate : BlockPos.randomInCube(level.random, 10, pos, 1)) {
            BlockState state = level.getBlockState(candidate);
            if (state.getBlock() instanceof OminousWeatheringCopper) {
                OminousWeatheringCopper.getPrevious(state).ifPresent(previous -> level.setBlockAndUpdate(candidate, previous));
                level.levelEvent(3002, candidate, -1);
                return Optional.of(candidate);
            }
        }
        return Optional.empty();
    }
}
