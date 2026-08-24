/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.api.magic.ISpell
 *  com.Polarice3.Goety.init.ModAttributes
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.vivideru.masteryofmagic.mixins;

import com.Polarice3.Goety.api.magic.ISpell;
import com.Polarice3.Goety.init.ModAttributes;
import com.vivideru.masteryofmagic.MasteryData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={ModAttributes.class})
public class SupremeWizardrySoulCostMixin {
    @Inject(method={"getSoulDiscount"}, at={@At(value="RETURN")}, cancellable=true, remap=false)
    private static void gmom$discount(LivingEntity caster, ISpell spell, CallbackInfoReturnable<Double> cir) {
        Player player;
        if (caster instanceof Player && MasteryData.getWizardry(player = (Player)caster) >= 3) {
            cir.setReturnValue((Object)((Double)cir.getReturnValue() * 0.8));
        }
    }
}

