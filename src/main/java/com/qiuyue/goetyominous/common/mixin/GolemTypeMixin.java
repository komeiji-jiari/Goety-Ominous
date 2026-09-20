package com.qiuyue.goetyominous.common.mixin;

import com.Polarice3.Goety.api.magic.GolemType;
import com.Polarice3.Goety.api.magic.IMold;
import com.Polarice3.Goety.common.blocks.ModBlocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(GolemType.class)
public class GolemTypeMixin {

    @Inject(method = "getGolemList", at = @At("RETURN"), remap = false, require = 1)
    private static void goetyominous$allowWaterloggedRoots(CallbackInfoReturnable<Map<BlockState, IMold>> cir) {
        Map<BlockState, IMold> map = cir.getReturnValue();
        BlockState roots = ModBlocks.OVERGROWN_ROOTS.get().defaultBlockState();
        if (!roots.hasProperty(BlockStateProperties.WATERLOGGED)) {
            return;
        }
        IMold mold = map.get(roots);
        if (mold != null) {
            map.putIfAbsent(roots.setValue(BlockStateProperties.WATERLOGGED, true), mold);
        }
    }
}
