/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.api.magic.SpellType
 *  com.Polarice3.Goety.common.enchantments.ModEnchantments
 *  com.Polarice3.Goety.common.magic.Spell
 *  com.Polarice3.Goety.common.magic.SpellStat
 *  com.Polarice3.Goety.init.ModSounds
 *  com.Polarice3.Goety.utils.MathHelper
 *  com.Polarice3.Goety.utils.MobUtil
 *  com.Polarice3.Goety.utils.WandUtil
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.enchantment.Enchantment
 *  org.jetbrains.annotations.Nullable
 */
package com.vivideru.masteryofmagic.magic.spells;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import com.vivideru.masteryofmagic.config.SpellConfig;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.Nullable;

public class MiningCurseSpell
extends Spell {
    public SpellStat defaultStats() {
        return super.defaultStats().setDuration(0).setRadius(12.0);
    }

    public int defaultSoulCost() {
        return (Integer)SpellConfig.MINING_CURSE_SOUL_COST.get();
    }

    public int defaultCastDuration() {
        return (Integer)SpellConfig.MINING_CURSE_CAST_TIME.get();
    }

    public int defaultSpellCooldown() {
        return (Integer)SpellConfig.MINING_CURSE_COOLDOWN.get();
    }

    @Nullable
    public SoundEvent CastingSound() {
        return (SoundEvent)ModSounds.WEAKEN_CAST.get();
    }

    public SpellType getSpellType() {
        return SpellType.ABYSS;
    }

    public List<Enchantment> acceptedEnchantments() {
        ArrayList<Enchantment> list = new ArrayList<Enchantment>();
        list.add((Enchantment)ModEnchantments.POTENCY.get());
        list.add((Enchantment)ModEnchantments.RADIUS.get());
        list.add((Enchantment)ModEnchantments.DURATION.get());
        return list;
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        if (worldIn.m_5776_()) {
            return;
        }
        worldIn.m_5594_(null, caster.m_20183_(), (SoundEvent)ModSounds.WEAKEN_CAST.get(), SoundSource.PLAYERS, 0.6f, 0.7f);
        int potency = spellStat.getPotency();
        int durationSeconds = 30 + 30 * spellStat.getDuration();
        int durationTicks = MathHelper.secondsToTicks((int)durationSeconds);
        int radius = (int)spellStat.getRadius();
        if (WandUtil.enchantedFocus((LivingEntity)caster)) {
            potency += WandUtil.getPotencyLevel((LivingEntity)caster);
            durationTicks += MathHelper.secondsToTicks((int)(30 * WandUtil.getLevels((Enchantment)((Enchantment)ModEnchantments.DURATION.get()), (LivingEntity)caster)));
            radius += WandUtil.getLevels((Enchantment)((Enchantment)ModEnchantments.RADIUS.get()), (LivingEntity)caster) * 12;
        }
        if (this.rightStaff(staff)) {
            radius *= 2;
        }
        for (Player player : worldIn.m_45976_(Player.class, caster.m_20191_().m_82400_((double)radius))) {
            if (player == caster || MobUtil.areAllies((Entity)caster, (Entity)player)) continue;
            boolean hadMiningFatigue = player.m_21023_(MobEffects.f_19599_);
            player.m_7292_(new MobEffectInstance(MobEffects.f_19599_, durationTicks, potency));
            if (hadMiningFatigue) continue;
            worldIn.m_5594_(null, player.m_20183_(), SoundEvents.f_11880_, SoundSource.PLAYERS, 0.35f, 0.6f);
        }
    }
}

