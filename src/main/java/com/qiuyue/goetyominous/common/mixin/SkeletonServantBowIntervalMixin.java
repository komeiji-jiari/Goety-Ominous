package com.qiuyue.goetyominous.common.mixin;

import com.Polarice3.Goety.common.entities.ai.CreatureBowAttackGoal;
import com.Polarice3.Goety.common.entities.ally.undead.skeleton.AbstractSkeletonServant;
import com.qiuyue.goetyominous.common.entities.ally.mobs.BoggedServant;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractSkeletonServant.class)
public class SkeletonServantBowIntervalMixin {

    @Redirect(
            method = "reassessWeaponGoal",
            at = @At(value = "INVOKE",
                    target = "Lcom/Polarice3/Goety/common/entities/ai/CreatureBowAttackGoal;setMinAttackInterval(I)V"),
            remap = false)
    private void goetyominous$boggedBowInterval(CreatureBowAttackGoal<?> goal, int interval) {
        if (((Object) this) instanceof BoggedServant bogged && !bogged.hasNecroCapeOwner()) {
            goal.setMinAttackInterval(70);
        } else {
            goal.setMinAttackInterval(interval);
        }
    }
}
