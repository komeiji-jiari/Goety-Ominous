package com.qiuyue.someillagerservants.common.mixin;

import com.Polarice3.Goety.common.ritual.Ritual;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Ritual.class)
public class RitualMixin {

    @Inject(method = "isValidSacrifice", at = @At("RETURN"), cancellable = true, remap = false)
    private void someillagerservants$isValidSacrifice(LivingEntity livingEntity, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() && livingEntity instanceof Villager villager && !villager.isBaby()) {
            Ritual ritual = (Ritual) (Object) this;
            if (ritual.recipe != null && ritual.recipe.getId() != null
                    && "someillagerservants:urbhadhach_focus".equals(ritual.recipe.getId().toString())) {
                cir.setReturnValue(false);
            }
        }
    }
}