package com.qiuyue.goetyominous.common.mixin.trial;

import com.qiuyue.goetyominous.common.blocks.trial.PotLootHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DecoratedPotBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockBehaviour.class)
public abstract class PotLootOnRemoveMixin {

    @Inject(method = "onRemove", at = @At("HEAD"))
    private void goetyominous$dropPotLoot(BlockState state, Level level, BlockPos pos, BlockState newState,
                                          boolean isMoving, CallbackInfo ci) {
        if (level.isClientSide || state.is(newState.getBlock()) || !(state.getBlock() instanceof DecoratedPotBlock)) {
            return;
        }
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof PotLootHolder holder) {
            holder.goetyominous$unpackLoot((ServerLevel) level, pos, null);
        }
    }
}
