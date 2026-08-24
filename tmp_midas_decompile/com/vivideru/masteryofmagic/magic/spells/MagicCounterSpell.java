/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.api.magic.SpellType
 *  com.Polarice3.Goety.common.enchantments.ModEnchantments
 *  com.Polarice3.Goety.common.magic.Spell
 *  com.Polarice3.Goety.common.magic.SpellStat
 *  com.Polarice3.Goety.utils.WandUtil
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.enchantment.Enchantment
 */
package com.vivideru.masteryofmagic.magic.spells;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.utils.WandUtil;
import com.vivideru.masteryofmagic.handler.MagicCounterHandler;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModSounds;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public final class MagicCounterSpell
extends Spell {
    public int defaultSoulCost() {
        return 100;
    }

    public int defaultCastDuration() {
        return 0;
    }

    public int defaultSpellCooldown() {
        return 40;
    }

    public SoundEvent CastingSound() {
        return (SoundEvent)GoetyMasteryOfMagicModSounds.MAGIC_COUNTER_ACTIVATE.get();
    }

    public SpellType getSpellType() {
        return SpellType.NONE;
    }

    public List<Enchantment> acceptedEnchantments() {
        return List.of((Enchantment)ModEnchantments.DURATION.get());
    }

    public void SpellResult(ServerLevel level, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        int duration = spellStat.getDuration();
        if (WandUtil.enchantedFocus((LivingEntity)caster)) {
            duration += WandUtil.getLevels((Enchantment)((Enchantment)ModEnchantments.DURATION.get()), (LivingEntity)caster);
        }
        MagicCounterHandler.activate(caster, 10 + Math.max(0, duration) * 2);
        level.m_5594_(null, caster.m_20183_(), (SoundEvent)GoetyMasteryOfMagicModSounds.MAGIC_COUNTER_ACTIVATE.get(), SoundSource.PLAYERS, 1.1f, 1.0f);
        level.m_5594_(null, caster.m_20183_(), SoundEvents.f_144243_, SoundSource.PLAYERS, 0.8f, 0.72f);
    }
}

