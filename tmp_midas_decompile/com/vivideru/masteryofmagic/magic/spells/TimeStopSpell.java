/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.api.magic.SpellType
 *  com.Polarice3.Goety.common.enchantments.ModEnchantments
 *  com.Polarice3.Goety.common.magic.Spell
 *  com.Polarice3.Goety.common.magic.SpellStat
 *  com.Polarice3.Goety.init.ModSounds
 *  com.Polarice3.Goety.utils.WandUtil
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.enchantment.Enchantment
 *  net.minecraftforge.common.ForgeConfigSpec$IntValue
 *  net.minecraftforge.registries.ForgeRegistries
 */
package com.vivideru.masteryofmagic.magic.spells;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.WandUtil;
import com.vivideru.masteryofmagic.TimeFreezeManager;
import com.vivideru.masteryofmagic.config.SpellConfig;
import com.vivideru.masteryofmagic.config.SpellConfigCache;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;

public class TimeStopSpell
extends Spell {
    public int defaultSoulCost() {
        return (Integer)SpellConfig.TIME_STOP_SOUL_COST.get();
    }

    public int soulCost(LivingEntity caster, ItemStack staff) {
        return this.defaultSoulCost();
    }

    public int defaultCastDuration() {
        return SpellConfigCache.TIME_STOP_CAST_TIME > 0 ? SpellConfigCache.TIME_STOP_CAST_TIME : 60;
    }

    public int defaultSpellCooldown() {
        return SpellConfigCache.TIME_STOP_COOLDOWN > 0 ? SpellConfigCache.TIME_STOP_COOLDOWN : 600;
    }

    public int castDuration(LivingEntity caster, ItemStack staff) {
        return this.defaultCastDuration();
    }

    public int spellCooldown(LivingEntity caster, ItemStack staff) {
        return this.defaultSpellCooldown();
    }

    private int safeConfig(ForgeConfigSpec.IntValue value, int fallback) {
        try {
            return (Integer)value.get();
        }
        catch (IllegalStateException exception) {
            return fallback;
        }
    }

    public SoundEvent CastingSound(LivingEntity caster) {
        return (SoundEvent)ModSounds.APOSTLE_CAST_SPELL.get();
    }

    public SpellType getSpellType() {
        return SpellType.FROST;
    }

    public List<Enchantment> acceptedEnchantments() {
        ArrayList<Enchantment> list = new ArrayList<Enchantment>();
        list.add((Enchantment)ModEnchantments.RADIUS.get());
        list.add((Enchantment)ModEnchantments.DURATION.get());
        return list;
    }

    public boolean rightStaff(ItemStack staff) {
        return ForgeRegistries.ITEMS.getKey((Object)staff.m_41720_()).equals((Object)new ResourceLocation("goety", "frost_staff"));
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        int radiusLevel = (int)spellStat.getRadius();
        int durationLevel = spellStat.getDuration();
        if (WandUtil.enchantedFocus((LivingEntity)caster)) {
            radiusLevel += WandUtil.getLevels((Enchantment)((Enchantment)ModEnchantments.RADIUS.get()), (LivingEntity)caster);
            durationLevel += WandUtil.getLevels((Enchantment)((Enchantment)ModEnchantments.DURATION.get()), (LivingEntity)caster);
        }
        double radius = SpellConfigCache.TIME_STOP_BASE_RADIUS + radiusLevel * SpellConfigCache.TIME_STOP_RADIUS_PER_LEVEL;
        if (this.rightStaff(staff)) {
            radius += 4.0;
        }
        int duration = (SpellConfigCache.TIME_STOP_BASE_DURATION + durationLevel * SpellConfigCache.TIME_STOP_DURATION_PER_LEVEL) * 20;
        TimeFreezeManager.createFromCaster(worldIn, caster, radius, duration);
        worldIn.m_8767_((ParticleOptions)ParticleTypes.f_175821_, caster.m_20185_(), caster.m_20186_() + 1.0, caster.m_20189_(), 80, radius * 0.35, 1.5, radius * 0.35, 0.04);
        this.playSound(worldIn, (Entity)caster, (SoundEvent)ModSounds.FROST_PREPARE_SPELL.get(), 2.0f, 0.65f);
    }
}

