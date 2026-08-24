/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.common.magic.SpellStat
 *  com.Polarice3.Goety.common.magic.spells.SoulBoltSpell
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.vivideru.masteryofmagic.mixins;

import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.spells.SoulBoltSpell;
import com.vivideru.masteryofmagic.MasteryData;
import com.vivideru.masteryofmagic.util.SupremeWizardryBeamHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={SoulBoltSpell.class})
public class SupremeWizardrySoulBoltMixin {
    @Inject(method={"SpellResult"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private void gmom$replaceBoltWithBeam(ServerLevel level, LivingEntity caster, ItemStack focus, SpellStat stat, CallbackInfo ci) {
        Player player;
        if (caster instanceof Player && MasteryData.getWizardry(player = (Player)caster) >= 1) {
            SupremeWizardryBeamHelper.spawnWeakBeam(level, caster, stat);
            ci.cancel();
        }
    }
}

