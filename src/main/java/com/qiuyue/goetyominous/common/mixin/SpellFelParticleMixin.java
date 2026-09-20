package com.qiuyue.goetyominous.common.mixin;

import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import com.qiuyue.goetyominous.GoetyOminous;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Spell.class)
public class SpellFelParticleMixin {

    @Inject(method = "useParticle", at = @At("HEAD"), cancellable = true, remap = false)
    private void goetyominous$felCastParticle(Level level, LivingEntity caster, ItemStack stack, CallbackInfo ci) {
        if (((Spell) (Object) this).getSpellType() != GoetyOminous.FEL) {
            return;
        }
        if (level instanceof ServerLevel serverLevel && caster.tickCount % 5 == 0) {
            ServerParticleUtil.addParticlesAroundMiddleSelf(serverLevel, ParticleTypes.WITCH, caster);
        }
        ci.cancel();
    }
}
