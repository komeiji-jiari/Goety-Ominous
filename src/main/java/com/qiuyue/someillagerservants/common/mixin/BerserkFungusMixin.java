package com.qiuyue.someillagerservants.common.mixin;

import com.Polarice3.Goety.common.entities.projectiles.BerserkFungus;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.utils.MathHelper;
import com.qiuyue.someillagerservants.common.items.FungusPackHelper;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BerserkFungus.class)
public class BerserkFungusMixin {

    @Inject(method = "applySplash", at = @At("HEAD"), cancellable = true, remap = false)
    private void someillagerservants$onApplySplash(CallbackInfo ci) {
        BerserkFungus self = (BerserkFungus) (Object) this;
        if (self.level().isClientSide) return;
        if (!(self.getOwner() instanceof LivingEntity owner)) return;
        if (!FungusPackHelper.hasMatchingFungus(owner, ModItems.BERSERK_FUNGUS.get())) return;

        AABB aabb = self.getBoundingBox().inflate(1.5);
        for (LivingEntity living : self.level().getEntitiesOfClass(LivingEntity.class, aabb)) {
            if (!living.isSpectator() && self.distanceToSqr(living) < 16.0) {
                int duration = MathHelper.secondsToTicks(10);
                living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, duration, 1));
                living.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, duration, 1));
            }
        }

        ci.cancel();
    }
}
