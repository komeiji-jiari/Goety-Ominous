package com.qiuyue.goetyominous.common.mixin;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.utils.MobUtil;
import com.qiuyue.goetyominous.common.init.ModTags;
import com.qiuyue.goetyominous.utils.CroneCuriosUtil;
import com.qiuyue.goetyominous.utils.ModMobType;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Summoned.class)
public class SummonedFelCrownMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void goetyominous$felCroneCrown(CallbackInfo ci) {
        Summoned self = (Summoned) (Object) this;
        if (self.level().isClientSide) {
            return;
        }
        LivingEntity owner = MobUtil.getOwner(self);
        if (owner == null || !CroneCuriosUtil.hasCroneHat(owner)) {
            return;
        }
        if (self.getMobType() != ModMobType.FEL
                && !self.getType().is(ModTags.EntityTypes.FEL_HEAL)) {
            return;
        }
        self.setHasLifespan(false);
    }
}
