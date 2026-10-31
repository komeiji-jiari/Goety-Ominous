package com.qiuyue.goetyominous.common.mixin;

import com.Polarice3.Goety.common.entities.ally.BlackBeast;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.qiuyue.goetyominous.common.blocks.entities.WolfTotemHooks;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.ForgeConfigSpec;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlackBeast.class)
public abstract class MixinBlackBeastHowlingSoul {

    @Unique
    private boolean goetyominous$wandDismissed;

    @Inject(method = "die", at = @At("HEAD"), require = 1)
    private void goetyominous$captureDismissSource(DamageSource source, CallbackInfo ci) {
        this.goetyominous$wandDismissed = source != null && source.is(ModDamageSource.DISMISSED);
    }

    @Redirect(method = "tickDeath",
            at = @At(value = "INVOKE", remap = false,
                    target = "Lnet/minecraftforge/common/ForgeConfigSpec$ConfigValue;get()Ljava/lang/Object;"),
            require = 1)
    private Object goetyominous$gateHowlingSoul(ForgeConfigSpec.ConfigValue<?> configValue) {
        if ((Object) this instanceof LivingEntity living
                && WolfTotemHooks.isAssignedToWolfTotem(living)
                && !living.hasEffect(GoetyEffects.WOUNDED.get())
                && !this.goetyominous$wandDismissed) {
            return false;
        }
        return configValue.get();
    }
}
