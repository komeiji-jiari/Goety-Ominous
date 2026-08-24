/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.api.magic.ISpell
 *  com.Polarice3.Goety.api.magic.SpellType
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
import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.init.ModAttributes;
import com.vivideru.masteryofmagic.MasteryData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={ModAttributes.class})
public class SupremeWizardryPotencyMixin {
    @Inject(method={"getPotency(Lnet/minecraft/world/entity/LivingEntity;Lcom/Polarice3/Goety/api/magic/ISpell;)I"}, at={@At(value="RETURN")}, cancellable=true, remap=false)
    private static void gmom$genericWizardryPotency(LivingEntity caster, ISpell spell, CallbackInfoReturnable<Integer> cir) {
        Player player;
        block5: {
            block4: {
                if (!(caster instanceof Player)) break block4;
                player = (Player)caster;
                if (spell != null) break block5;
            }
            return;
        }
        if (spell.getSpellTypes().stream().allMatch(type -> type == SpellType.NONE)) {
            cir.setReturnValue((Object)((Integer)cir.getReturnValue() + MasteryData.getWizardry(player)));
        }
    }
}

