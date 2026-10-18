package com.qiuyue.goetyominous.common.mixin.trial;

import com.qiuyue.goetyominous.common.init.ModEffects;
import com.qiuyue.goetyominous.compat.mod.IronsSpellbooksCompat;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityTrialOmenMixin {

    @Inject(method = "hasEffect(Lnet/minecraft/world/effect/MobEffect;)Z", at = @At("HEAD"), cancellable = true)
    private void goetyominous$trialOmenCompat(MobEffect effect, CallbackInfoReturnable<Boolean> cir) {
        MobEffect issTrialOmen = IronsSpellbooksCompat.trialOmen();
        if (issTrialOmen != null
                && effect == issTrialOmen
                && ((LivingEntity) (Object) this).hasEffect(ModEffects.TRIAL_OMEN.get())) {
            cir.setReturnValue(true);
        }
    }
}
