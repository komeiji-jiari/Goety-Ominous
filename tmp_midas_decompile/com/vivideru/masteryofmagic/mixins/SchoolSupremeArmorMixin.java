/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.LivingEntity
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.vivideru.masteryofmagic.mixins;

import com.vivideru.masteryofmagic.SchoolSupremeDamageHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={LivingEntity.class})
public class SchoolSupremeArmorMixin {
    @Inject(method={"getDamageAfterArmorAbsorb"}, at={@At(value="RETURN")}, cancellable=true)
    private void gmom$halfArmor(DamageSource source, float original, CallbackInfoReturnable<Float> cir) {
        if (SchoolSupremeDamageHelper.isEmpoweredSkyDamage(source)) {
            float normal = ((Float)cir.getReturnValue()).floatValue();
            cir.setReturnValue((Object)Float.valueOf(Math.max(1.0f, normal + (original - normal) * 0.5f)));
        }
    }
}

