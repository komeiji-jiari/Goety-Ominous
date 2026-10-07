package com.qiuyue.goetyominous.common.mixin;

import com.qiuyue.goetyominous.common.effects.ModEffectParticles;
import java.util.List;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public class ModEffectParticleMixin {

    @Redirect(method = "tickEffects", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"), require = 1)
    private void goetyominous$modEffectAmbientParticle(Level level, ParticleOptions options,
                                                       double x, double y, double z,
                                                       double xSpeed, double ySpeed, double zSpeed) {
        LivingEntity self = (LivingEntity) (Object) this;
        List<ParticleOptions> particles = ModEffectParticles.collect(self);
        if (particles.isEmpty()) {
            level.addParticle(options, x, y, z, xSpeed, ySpeed, zSpeed);
            return;
        }
        if (!self.isInvisible() && self.getRandom().nextBoolean()) {
            return;
        }
        level.addParticle(particles.get(self.getRandom().nextInt(particles.size())), x, y, z, 1.0D, 1.0D, 1.0D);
    }
}
