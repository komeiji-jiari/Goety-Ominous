package com.qiuyue.goetyominous.common.mixin.trial;

import com.qiuyue.goetyominous.common.init.ModEffects;
import com.qiuyue.goetyominous.compat.mod.IronsSpellbooksCompat;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityTrialOmenMixin {

    @Inject(method = "getEffect(Lnet/minecraft/world/effect/MobEffect;)Lnet/minecraft/world/effect/MobEffectInstance;",
            at = @At("HEAD"), cancellable = true)
    private void goetyominous$trialOmenGetEffectCompat(MobEffect effect, CallbackInfoReturnable<MobEffectInstance> cir) {
        MobEffect issTrialOmen = IronsSpellbooksCompat.trialOmen();
        if (issTrialOmen == null || effect != issTrialOmen) {
            return;
        }
        MobEffectInstance instance = ((LivingEntity) (Object) this).getEffect(ModEffects.TRIAL_OMEN.get());
        if (instance != null) {
            cir.setReturnValue(instance);
        }
    }
}
