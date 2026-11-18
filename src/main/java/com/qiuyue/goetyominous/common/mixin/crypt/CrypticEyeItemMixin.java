package com.qiuyue.goetyominous.common.mixin.crypt;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.Polarice3.Goety.common.entities.projectiles.EyeItemEntity;
import com.Polarice3.Goety.common.items.CrypticEyeItem;
import com.qiuyue.goetyominous.common.worldgen.crypt.CryptEntrance;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CrypticEyeItem.class)
public abstract class CrypticEyeItemMixin {

    @WrapOperation(method = "use", require = 1,
            at = @At(value = "INVOKE",
                    target = "Lcom/Polarice3/Goety/common/entities/projectiles/EyeItemEntity;signalTo(Lnet/minecraft/core/BlockPos;)V"))
    private void goetyominous$signalToDoor(EyeItemEntity eyeItem, BlockPos pos, Operation<Void> original,
                                          @Local(argsOnly = true) Level level) {
        BlockPos target = level instanceof ServerLevel server ? CryptEntrance.doorFor(server, pos) : pos;
        original.call(eyeItem, target);
    }
}
