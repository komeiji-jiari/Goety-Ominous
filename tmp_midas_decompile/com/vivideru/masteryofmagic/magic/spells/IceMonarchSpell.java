/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.api.magic.SpellType
 *  com.Polarice3.Goety.common.effects.GoetyEffects
 *  com.Polarice3.Goety.common.enchantments.ModEnchantments
 *  com.Polarice3.Goety.common.magic.SpellStat
 *  com.Polarice3.Goety.common.magic.SummonSpell
 *  com.Polarice3.Goety.init.ModSounds
 *  com.Polarice3.Goety.utils.BlockFinder
 *  com.Polarice3.Goety.utils.EffectsUtil
 *  com.Polarice3.Goety.utils.MobUtil
 *  com.Polarice3.Goety.utils.WandUtil
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.util.Mth
 *  net.minecraft.world.effect.MobEffect
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.MobSpawnType
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.enchantment.Enchantment
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.ServerLevelAccessor
 */
package com.vivideru.masteryofmagic.magic.spells;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.SummonSpell;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.EffectsUtil;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import com.vivideru.masteryofmagic.config.SpellConfig;
import com.vivideru.masteryofmagic.entity.IceMonarchEntity;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModEntities;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public class IceMonarchSpell
extends SummonSpell {
    public int defaultSoulCost() {
        return (Integer)SpellConfig.ICE_MONARCH_SOUL_COST.get();
    }

    public int defaultCastDuration() {
        return (Integer)SpellConfig.ICE_MONARCH_CAST_TIME.get();
    }

    public int SummonDownDuration() {
        return (Integer)SpellConfig.ICE_MONARCH_SUMMON_DOWN.get();
    }

    public int defaultSpellCooldown() {
        return (Integer)SpellConfig.ICE_MONARCH_COOLDOWN.get();
    }

    public int summonLimit() {
        return (Integer)SpellConfig.ICE_MONARCH_LIMIT.get();
    }

    public SpellType getSpellType() {
        return SpellType.FROST;
    }

    public List<Enchantment> acceptedEnchantments() {
        ArrayList<Enchantment> list = new ArrayList<Enchantment>();
        list.add((Enchantment)ModEnchantments.POTENCY.get());
        list.add((Enchantment)ModEnchantments.DURATION.get());
        return list;
    }

    public Predicate<LivingEntity> summonPredicate() {
        return livingEntity -> livingEntity instanceof IceMonarchEntity;
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        this.commonResult(worldIn, caster);
        int potency = spellStat.getPotency();
        int duration = spellStat.getDuration();
        if (WandUtil.enchantedFocus((LivingEntity)caster)) {
            potency += WandUtil.getPotencyLevel((LivingEntity)caster);
            duration += WandUtil.getLevels((Enchantment)((Enchantment)ModEnchantments.DURATION.get()), (LivingEntity)caster) + 1;
        }
        if (!this.isShifting(caster)) {
            IceMonarchEntity monarch = new IceMonarchEntity((EntityType<? extends IceMonarchEntity>)((EntityType)GoetyMasteryOfMagicModEntities.ICE_MONARCH.get()), (Level)worldIn);
            BlockPos blockPos = BlockFinder.SummonRadius((BlockPos)caster.m_20183_(), (Entity)monarch, (Level)worldIn);
            if (caster.m_5842_()) {
                blockPos = BlockFinder.SummonWaterRadius((LivingEntity)caster, (Level)worldIn);
            }
            monarch.setTrueOwner(caster);
            monarch.m_7678_((double)blockPos.m_123341_() + 0.5, (double)blockPos.m_123342_() + 2.0, (double)blockPos.m_123343_() + 0.5, 0.0f, 0.0f);
            monarch.setLimitedLife(MobUtil.getSummonLifespan((Level)worldIn) * duration);
            monarch.m_21530_();
            monarch.m_6518_((ServerLevelAccessor)worldIn, caster.m_9236_().m_6436_(caster.m_20183_()), MobSpawnType.MOB_SUMMONED, null, null);
            if (!this.rightStaff(staff) && monarch.m_21051_(Attributes.f_22276_) != null) {
                monarch.m_21051_(Attributes.f_22276_).m_22100_(120.0);
                monarch.m_21153_(120.0f);
            }
            this.buffSummon(caster, (LivingEntity)monarch, potency);
            this.SummonSap(caster, (LivingEntity)monarch);
            this.setTarget(caster, (Mob)monarch);
            if (worldIn.m_7967_((Entity)monarch)) {
                this.uponSummon(worldIn, caster, staff, (LivingEntity)monarch);
            }
            this.summonAdvancement(caster, (LivingEntity)monarch);
            this.SummonDown(caster);
            this.playSound(worldIn, (Entity)caster, (SoundEvent)ModSounds.SUMMON_SPELL.get());
        }
    }

    public void buffSummon(LivingEntity caster, LivingEntity summoned, int potency) {
        if (potency > 0 && !this.hasSummonDown(caster)) {
            int boost = Mth.m_14045_((int)(potency - 1), (int)0, (int)10);
            summoned.m_7292_(new MobEffectInstance((MobEffect)GoetyEffects.BUFF.get(), EffectsUtil.infiniteEffect(), boost, false, false));
        }
    }
}

