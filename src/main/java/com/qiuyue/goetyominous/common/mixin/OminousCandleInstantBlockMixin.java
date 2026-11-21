package com.qiuyue.goetyominous.common.mixin;

import com.qiuyue.goetyominous.common.items.ModItems;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class OminousCandleInstantBlockMixin {

    @Inject(method = "isBlocking", at = @At("HEAD"), cancellable = true)
    private void goetyominous$instantBlock(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.isUsingItem() && self.getUseItem().is(ModItems.OMINOUS_CANDLE.get())) {
            cir.setReturnValue(true);
        }
    }
}
