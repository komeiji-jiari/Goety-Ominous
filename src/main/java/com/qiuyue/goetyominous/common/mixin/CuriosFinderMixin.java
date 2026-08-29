package com.qiuyue.goetyominous.common.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(com.Polarice3.Goety.utils.CuriosFinder.class)
public class CuriosFinderMixin {
    @Inject(method = "isWitchFriendly", at = @At("RETURN"), cancellable = true, remap = false)
    private static void goetyominous$croneWitchFriendly(net.minecraft.world.entity.LivingEntity livingEntity,
                                                        org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() && com.qiuyue.goetyominous.utils.CroneCuriosUtil.hasCroneSet(livingEntity)) {
            cir.setReturnValue(true);
        }
    }
}
