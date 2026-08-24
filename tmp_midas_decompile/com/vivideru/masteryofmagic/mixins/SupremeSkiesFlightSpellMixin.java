/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.common.magic.spells.wind.FlyingSpell
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package com.vivideru.masteryofmagic.mixins;

import com.Polarice3.Goety.common.magic.spells.wind.FlyingSpell;
import com.vivideru.masteryofmagic.MasteryData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={FlyingSpell.class})
public class SupremeSkiesFlightSpellMixin {
    @Redirect(method={"useSpell"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/entity/LivingEntity;setDeltaMovement(DDD)V"), remap=false)
    private void gmom$doubleFlight(LivingEntity caster, double x, double y, double z) {
        Player p;
        double m = caster instanceof Player && MasteryData.hasSupreme(p = (Player)caster, MasteryData.SupremeSchool.SKIES) ? 2.0 : 1.0;
        caster.m_20334_(x * m, y * m, z * m);
    }
}

