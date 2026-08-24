/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.api.magic.ISpell
 *  com.Polarice3.Goety.api.magic.SpellType
 *  com.Polarice3.Goety.common.enchantments.ModEnchantments
 *  com.Polarice3.Goety.common.items.magic.DarkWand
 *  com.Polarice3.Goety.common.magic.SpellStat
 *  com.Polarice3.Goety.common.magic.spells.nether.FlameStrikeSpell
 *  com.Polarice3.Goety.utils.WandUtil
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.vivideru.masteryofmagic.mixins;

import com.Polarice3.Goety.api.magic.ISpell;
import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.items.magic.DarkWand;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.spells.nether.FlameStrikeSpell;
import com.Polarice3.Goety.utils.WandUtil;
import com.vivideru.masteryofmagic.MasteryData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public final class SchoolSupremeSpellMixin {

    @Mixin(value={DarkWand.class})
    public static class CastTime {
        @Inject(method={"setSpellConditions"}, at={@At(value="RETURN")}, remap=false)
        private void gmom$deathFlameStrike(ISpell spell, ItemStack wand, LivingEntity caster, CallbackInfo ci) {
            Player player;
            if (spell instanceof FlameStrikeSpell && caster instanceof Player && MasteryData.hasSupreme(player = (Player)caster, MasteryData.SupremeSchool.NETHER)) {
                int duration = wand.m_41784_().m_128451_("Duration");
                wand.m_41784_().m_128405_("Duration", Math.max(10, (int)Math.ceil((double)duration * 0.1)));
            }
        }
    }

    @Mixin(value={WandUtil.class})
    public static class Stats {
        @Inject(method={"getStats"}, at={@At(value="RETURN")}, cancellable=true, remap=false)
        private static void gmom$skiesStats(LivingEntity caster, ISpell spell, CallbackInfoReturnable<SpellStat> cir) {
            Player player;
            if (!(caster instanceof Player) || !MasteryData.hasSupreme(player = (Player)caster, MasteryData.SupremeSchool.SKIES)) {
                return;
            }
            SpellStat stat = (SpellStat)cir.getReturnValue();
            if (spell.acceptedEnchantments().contains(ModEnchantments.VELOCITY.get())) {
                stat.increaseVelocity(2.0f);
            }
            if (spell.getSpellTypes().contains(SpellType.STORM)) {
                stat.setRange(stat.getRange() * 3);
                stat.setRadius(stat.getRadius() * 3.0);
            }
        }
    }
}

