package com.qiuyue.goetyominous.common.magic.spells.mm;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.SummonSpell;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.*;
import com.alexander.mutantmore.config.mutant_blaze.RodlingCommonConfig;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.RodlingServant;
import com.qiuyue.goetyominous.common.init.mm.MmEntityRegistry;
import com.qiuyue.goetyominous.config.MobsConfig;
import com.qiuyue.goetyominous.config.SpellConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class RodStrikeSpell extends SummonSpell {
    public RodStrikeSpell() {
    }

    public int defaultSoulCost() {
        return (Integer) SpellConfig.RodStrikeSoulCost.get();
    }

    public int defaultCastDuration() {
        return (Integer) SpellConfig.RodStrikeCastDuration.get();
    }

    public int SummonDownDuration() {
        return (Integer) SpellConfig.RodStrikeSummonDown.get();
    }

    public SoundEvent CastingSound(LivingEntity caster) {
        return CuriosFinder.hasUnholySet(caster) ? (SoundEvent)ModSounds.APOSTLE_PREPARE_SUMMON.get() : (SoundEvent)ModSounds.PREPARE_SUMMON.get();
    }

    public int defaultSpellCooldown() {
        return (Integer) SpellConfig.RodStrikeCooldown.get();
    }

    public SpellType getSpellType() {
        return SpellType.NETHER;
    }

    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList();
        list.add((Enchantment) ModEnchantments.POTENCY.get());
        list.add((Enchantment) ModEnchantments.DURATION.get());
        return list;
    }

    public Predicate<LivingEntity> summonPredicate() {
        return (livingEntity) -> {
            return livingEntity instanceof RodlingServant;
        };
    }

    public int summonLimit() {
        return (Integer) MobsConfig.RodlingServantLimit.get();
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        this.commonResult(worldIn, caster);
        int potency = spellStat.getPotency();
        int duration = spellStat.getDuration();
        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getPotencyLevel(caster);
            duration += WandUtil.getLevels((Enchantment) ModEnchantments.DURATION.get(), caster) + 1;
        }

        if (!this.isShifting(caster)) {
            int i = 2;
            if (this.rightStaff(staff)) {
                i = 2 + caster.level().random.nextInt(4);
            }

            for (int i1 = 0; i1 < i; ++i1) {
                RodlingServant summonedentity = new RodlingServant(MmEntityRegistry.RODLING_SERVANT.get(), worldIn);
                summonedentity.setTrueOwner(caster);
                summonedentity.moveTo(BlockFinder.SummonRadius(caster.blockPosition(), summonedentity, worldIn), 0.0F, 0.0F);
                MobUtil.moveDownToGround(summonedentity);
                summonedentity.setPersistenceRequired();
                summonedentity.setLimitedLife(MobUtil.getSummonLifespan(worldIn) * duration);
                summonedentity.finalizeSpawn(worldIn, caster.level().getCurrentDifficultyAt(caster.blockPosition()), MobSpawnType.MOB_SUMMONED, (SpawnGroupData) null, (CompoundTag) null);
                if (potency > 0) {
                    summonedentity.setFireBallDamage(
                            RodlingCommonConfig.tamed_fireball_damage.get().floatValue() + potency);
                }
                this.buffSummon(caster, summonedentity, potency);
                this.SummonSap(caster, summonedentity);
                this.setTarget(caster, summonedentity);
                if (worldIn.addFreshEntity(summonedentity)) {
                    this.uponSummon(worldIn, caster, staff, summonedentity);
                }

                this.summonAdvancement(caster, summonedentity);
            }

            this.SummonDown(caster);
            this.playSound(worldIn, caster, (SoundEvent)ModSounds.SUMMON_SPELL_FIERY.get());
        }

    }

    public void buffSummon(LivingEntity caster, LivingEntity summoned, int potency) {
        if (potency > 0 && !this.hasSummonDown(caster)) {
            int boost = Mth.clamp(potency - 1, 0, 10);
            summoned.addEffect(new MobEffectInstance((MobEffect) GoetyEffects.BUFF.get(), EffectsUtil.infiniteEffect(), boost, false, false));
            if (summoned instanceof RodlingServant) {
                RodlingServant rodlingServant = (RodlingServant)summoned;
                rodlingServant.setFireBallDamage(rodlingServant.getFireBallDamage() + (float)potency);
            }
        }

    }

}
