package com.qiuyue.goetyominus.common.magic.spells;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.ally.golem.RedstoneCube;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.SummonSpell;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import com.qiuyue.goetyominus.config.SpellConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.Nullable;

public class RedstoneSpell extends SummonSpell {
    public RedstoneSpell() {
    }

    public int defaultSoulCost() {
        return (Integer)SpellConfig.RedstoneCost.get();
    }

    public int defaultCastDuration() {
        return (Integer) SpellConfig.RedstoneDuration.get();
    }

    public @Nullable SoundEvent CastingSound() {
        return (SoundEvent)ModSounds.PREPARE_SUMMON.get();
    }

    public int defaultSpellCooldown() {
        return (Integer)SpellConfig.RedstoneCoolDown.get();
    }

    public int SummonDownDuration() {
        return (Integer)SpellConfig.RedstoneSummonDown.get();
    }

    public SpellType getSpellType() {
        return SpellType.GEOMANCY;
    }

    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList();
        list.add((Enchantment)ModEnchantments.POTENCY.get());
        list.add((Enchantment)ModEnchantments.DURATION.get());
        return list;
    }

    public Predicate<LivingEntity> summonPredicate() {
        return (livingEntity) -> {
            return livingEntity instanceof RedstoneCube;
        };
    }

    public int summonLimit() {
        return (Integer)SpellConfig.RedstoneCubeLimit.get();
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        this.commonResult(worldIn, caster);
        int potency = spellStat.getPotency();
        int duration = spellStat.getDuration();
        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getPotencyLevel(caster);
            duration += WandUtil.getLevels((Enchantment)ModEnchantments.DURATION.get(), caster) + 1;
        }

        if (!this.isShifting(caster)) {
            int i = 1;
            if (this.rightStaff(staff)) {
                i = 2 + caster.level().getRandom().nextInt(2);
            }

            for(int i1 = 0; i1 < i; ++i1) {
                Summoned summonedentity = new RedstoneCube((EntityType)ModEntityType.REDSTONE_CUBE.get(), worldIn);

                BlockPos blockPos = BlockFinder.SummonRadius(caster.blockPosition(), (Entity)summonedentity, worldIn);
                ((Summoned)summonedentity).setTrueOwner(caster);
                ((Summoned)summonedentity).moveTo(blockPos, 0.0F, 0.0F);
                MobUtil.moveDownToGround((Entity)summonedentity);
                int life = MobUtil.getSummonLifespan(worldIn) * duration;
                if (com.Polarice3.Goety.utils.CuriosFinder.hasAmethystNecklace(caster)) {
                    life *= 3;
                }
                ((Summoned)summonedentity).setLimitedLife(life);
                ((Summoned)summonedentity).setPersistenceRequired();
                ((Summoned)summonedentity).finalizeSpawn(worldIn, caster.level().getCurrentDifficultyAt(caster.blockPosition()), MobSpawnType.MOB_SUMMONED, (SpawnGroupData)null, (CompoundTag)null);
                if (com.Polarice3.Goety.utils.CuriosFinder.hasGeoRobe(caster)) {
                    summonedentity.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                            com.Polarice3.Goety.common.effects.GoetyEffects.IRON_HIDE.get(),
                            com.Polarice3.Goety.utils.EffectsUtil.infiniteEffect(), 1, false, false));
                }
                this.buffSummon(caster, (LivingEntity)summonedentity, potency);
                this.SummonSap(caster, (LivingEntity)summonedentity);
                this.setTarget(caster, (Mob)summonedentity);
                if (worldIn.addFreshEntity((Entity)summonedentity)) {
                    this.uponSummon(worldIn, caster, staff, (LivingEntity)summonedentity);
                }

                this.summonAdvancement(caster, (LivingEntity)summonedentity);
            }

            this.SummonDown(caster);
            this.playSound(worldIn, caster, (SoundEvent)ModSounds.SUMMON_SPELL.get());
        }

    }

}