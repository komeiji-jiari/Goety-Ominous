package com.qiuyue.goetyominous.common.mixin;

import com.Polarice3.Goety.common.magic.construct.LeapleafMold;
import com.qiuyue.goetyominous.common.magic.construct.LeapkelpMold;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LeapleafMold.class)
public class LeapleafMoldMixin {

    @Inject(method = "spawnServant", at = @At("HEAD"), cancellable = true, remap = false)
    private void goetyominous$spawnLeapkelpInWater(Player player, ItemStack stack, Level level, BlockPos blockPos, CallbackInfoReturnable<Boolean> cir) {
        if (!LeapkelpMold.isKelpAltar(level, blockPos)) {
            return;
        }
        cir.setReturnValue(new LeapkelpMold().spawnServant(player, stack, level, blockPos));
    }
}
