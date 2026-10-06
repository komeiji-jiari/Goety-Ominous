package com.qiuyue.goetyominous.common.mixin;

import com.qiuyue.goetyominous.common.init.ModEffects;
import com.qiuyue.goetyominous.common.init.ModParticleTypes;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public class TrialOmenParticleMixin {

    @Redirect(method = "tickEffects", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"), require = 1)
    private void goetyominous$trialOmenAmbientParticle(Level level, ParticleOptions options,
                                                       double x, double y, double z,
                                                       double xSpeed, double ySpeed, double zSpeed) {
        LivingEntity self = (LivingEntity) (Object) this;
        MobEffectInstance trialOmen = self.getEffect(ModEffects.TRIAL_OMEN.get());
        if (trialOmen != null && trialOmen.isVisible()) {
            if (!self.isInvisible() && self.getRandom().nextBoolean()) {
                return;
            }
            level.addParticle(ModParticleTypes.TRIAL_OMEN.get(), x, y, z, 1.0D, 1.0D, 1.0D);
        } else {
            level.addParticle(options, x, y, z, xSpeed, ySpeed, zSpeed);
        }
    }
}
