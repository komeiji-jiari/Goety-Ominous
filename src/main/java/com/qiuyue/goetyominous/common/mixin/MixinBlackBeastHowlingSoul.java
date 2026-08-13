package com.qiuyue.goetyominous.common.mixin;

import com.Polarice3.Goety.common.entities.ally.BlackBeast;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.qiuyue.goetyominous.common.blocks.entities.WolfTotemHooks;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.ForgeConfigSpec;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlackBeast.class)
public abstract class MixinBlackBeastHowlingSoul {

    @Redirect(method = "tickDeath",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraftforge/common/ForgeConfigSpec$ConfigValue;get()Ljava/lang/Object;"),
            remap = false)
    private Object goetyominous$gateHowlingSoul(ForgeConfigSpec.ConfigValue<?> configValue) {
        if ((Object) this instanceof LivingEntity living
                && WolfTotemHooks.isAssignedToWolfTotem(living)
                && !living.hasEffect(GoetyEffects.WOUNDED.get())) {
            return false;
        }
        return configValue.get();
    }
}
