package com.qiuyue.goetyominous.common.mixin.trial;

import com.qiuyue.goetyominous.common.worldgen.protect.StructureProtection;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldGenRegion.class)
public abstract class WorldGenRegionProtectionMixin {

    @Inject(method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z",
            at = @At("HEAD"), cancellable = true, require = 1)
    private void goetyominous$keepFeaturesOutOfTrialChambers(BlockPos pos, BlockState state, int flags, int recursion,
                                                             CallbackInfoReturnable<Boolean> cir) {
        if (StructureProtection.protects((WorldGenLevel) this, pos, state)) {
            cir.setReturnValue(false);
        }
    }
}
