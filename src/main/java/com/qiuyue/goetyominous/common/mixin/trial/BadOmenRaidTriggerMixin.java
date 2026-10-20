package com.qiuyue.goetyominous.common.mixin.trial;

import com.qiuyue.goetyominous.config.MobsConfig;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.world.effect.MobEffects$1")
public abstract class BadOmenRaidTriggerMixin {

    @Inject(method = "applyEffectTick(Lnet/minecraft/world/entity/LivingEntity;I)V", at = @At("HEAD"), cancellable = true)
    private void goetyominous$skipVanillaRaidTrigger(LivingEntity entity, int amplifier, CallbackInfo ci) {
        if (MobsConfig.RaidOmen.get()) {
            ci.cancel();
        }
    }
}
