package com.qiuyue.goetyominous.common.mixin.crypt;

import com.qiuyue.goetyominous.common.blocks.PolarCrystalBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBasePolarCrystalMixin {

    @Inject(method = "getDirectSignal", at = @At("RETURN"), cancellable = true, require = 1)
    private void goetyominous$polarCrystalSupport(BlockGetter level, BlockPos pos, Direction direction,
                                                  CallbackInfoReturnable<Integer> cir) {
        if (cir.getReturnValueI() < 15 && PolarCrystalBlock.carriesCrystalSignal(level, (BlockState) (Object) this, pos)) {
            cir.setReturnValue(15);
        }
    }
}
