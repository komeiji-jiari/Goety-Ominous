package com.qiuyue.goetyominous.common.mixin;

import com.qiuyue.goetyominous.common.init.ModEffects;
import com.qiuyue.goetyominous.common.init.ModSounds;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class EffectApplySoundMixin {

    @Shadow
    @Final
    private Map<MobEffect, MobEffectInstance> activeEffects;

    @Inject(method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z", at = @At("RETURN"))
    private void goetyominous$playApplySound(MobEffectInstance instance, @Nullable Entity source, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) {
            return;
        }
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.level().isClientSide) {
            return;
        }
        if (this.activeEffects.get(instance.getEffect()) != instance) {
            return;
        }
        SoundEvent sound = null;
        if (instance.getEffect() == MobEffects.BAD_OMEN) {
            sound = ModSounds.APPLY_EFFECT_BAD_OMEN.get();
        } else if (instance.getEffect() == ModEffects.TRIAL_OMEN.get()) {
            sound = ModSounds.APPLY_EFFECT_TRIAL_OMEN.get();
        }
        if (sound != null) {
            self.level().playSound(null, self.getX(), self.getY(), self.getZ(), sound, self.getSoundSource(), 1.0F, 1.0F);
        }
    }
}
