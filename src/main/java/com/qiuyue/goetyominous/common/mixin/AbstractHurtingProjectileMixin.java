package com.qiuyue.goetyominous.common.mixin;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractHurtingProjectile.class)
public abstract class AbstractHurtingProjectileMixin {
    @Shadow
    protected abstract ParticleOptions getTrailParticle();

    @Inject(method = "tick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"), cancellable = true)
    private void goetyominous$guardTrailParticle(CallbackInfo ci) {
        if (this.getTrailParticle() == null) {
            ci.cancel();
        }
    }
}
