/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.api.magic.SpellType
 *  com.Polarice3.Goety.common.effects.GoetyEffects
 *  com.Polarice3.Goety.common.enchantments.ModEnchantments
 *  com.Polarice3.Goety.common.entities.neutral.Wildfire
 *  com.Polarice3.Goety.common.magic.SpellStat
 *  com.Polarice3.Goety.common.magic.SummonSpell
 *  com.Polarice3.Goety.init.ModSounds
 *  com.Polarice3.Goety.utils.BlockFinder
 *  com.Polarice3.Goety.utils.CuriosFinder
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
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.enchantment.Enchantment
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.ServerLevelAccessor
 */
package com.vivideru.masteryofmagic.magic.spells;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.neutral.Wildfire;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.SummonSpell;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.EffectsUtil;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import com.vivideru.masteryofmagic.FocusWildfireEntity;
import com.vivideru.masteryofmagic.config.SpellConfig;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public class FocusWildfireSpell
extends SummonSpell {
    public int defaultSoulCost() {
        return (Integer)SpellConfig.FOCUS_WILDFIRE_SOUL_COST.get();
    }

    public int defaultCastDuration() {
        return (Integer)SpellConfig.FOCUS_WILDFIRE_CAST_TIME.get();
    }

    public int SummonDownDuration() {
        return (Integer)SpellConfig.FOCUS_WILDFIRE_SUMMON_DOWN.get();
    }

    public int defaultSpellCooldown() {
        return (Integer)SpellConfig.FOCUS_WILDFIRE_COOLDOWN.get();
    }

    public int summonLimit() {
        return (Integer)SpellConfig.FOCUS_WILDFIRE_LIMIT.get();
    }

    public SpellType getSpellType() {
        return SpellType.NETHER;
    }

    public List<Enchantment> acceptedEnchantments() {
        ArrayList<Enchantment> list = new ArrayList<Enchantment>();
        list.add((Enchantment)ModEnchantments.POTENCY.get());
        list.add((Enchantment)ModEnchantments.DURATION.get());
        return list;
    }

    public Predicate<LivingEntity> summonPredicate() {
        return livingEntity -> livingEntity instanceof FocusWildfireEntity;
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
            int summonCount = 1;
            if (this.rightStaff(staff) && CuriosFinder.hasUnholySet((LivingEntity)caster)) {
                summonCount = 2;
            }
            for (int i = 0; i < summonCount; ++i) {
                FocusWildfireEntity wildfire = new FocusWildfireEntity((EntityType<? extends Wildfire>)((EntityType)GoetyMasteryOfMagicModEntities.FOCUS_WILDFIRE.get()), (Level)worldIn);
                BlockPos blockPos = BlockFinder.SummonRadius((BlockPos)caster.m_20183_(), (Entity)wildfire, (Level)worldIn);
                if (caster.m_5842_()) {
                    blockPos = BlockFinder.SummonWaterRadius((LivingEntity)caster, (Level)worldIn);
                }
                wildfire.setTrueOwner(caster);
                wildfire.m_20035_(blockPos, 0.0f, 0.0f);
                MobUtil.moveDownToGround((Entity)wildfire);
                wildfire.setLimitedLife(MobUtil.getSummonLifespan((Level)worldIn) * duration);
                wildfire.m_21530_();
                wildfire.m_6518_((ServerLevelAccessor)worldIn, caster.m_9236_().m_6436_(caster.m_20183_()), MobSpawnType.MOB_SUMMONED, null, null);
                this.buffSummon(caster, (LivingEntity)wildfire, potency);
                this.SummonSap(caster, (LivingEntity)wildfire);
                this.setTarget(caster, (Mob)wildfire);
                if (worldIn.m_7967_((Entity)wildfire)) {
                    this.uponSummon(worldIn, caster, staff, (LivingEntity)wildfire);
                }
                this.summonAdvancement(caster, (LivingEntity)wildfire);
            }
            this.SummonDown(caster);
            this.playSound(worldIn, (Entity)caster, (SoundEvent)ModSounds.SUMMON_SPELL_FIERY.get());
        }
    }

    public void buffSummon(LivingEntity caster, LivingEntity summoned, int potency) {
        if (potency > 0 && !this.hasSummonDown(caster)) {
            int boost = Mth.m_14045_((int)(potency - 1), (int)0, (int)10);
            summoned.m_7292_(new MobEffectInstance((MobEffect)GoetyEffects.BUFF.get(), EffectsUtil.infiniteEffect(), boost, false, false));
            if (summoned instanceof FocusWildfireEntity) {
                FocusWildfireEntity wildfire = (FocusWildfireEntity)summoned;
                wildfire.setFireBallDamage(wildfire.getFireBallDamage() + (float)potency);
            }
        }
    }
}

