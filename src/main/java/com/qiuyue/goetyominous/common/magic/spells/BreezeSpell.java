package com.qiuyue.goetyominous.common.magic.spells;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.SummonSpell;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import com.qiuyue.goetyominous.common.entities.ally.mobs.BreezeServant;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import com.qiuyue.goetyominous.config.MobsConfig;
import com.qiuyue.goetyominous.config.SpellConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import com.qiuyue.goetyominous.utils.ServantAllyUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public class BreezeSpell extends SummonSpell {
    public BreezeSpell() {
    }

    public int defaultSoulCost() {
        return SpellConfig.BreezeCost.get();
    }

    public int defaultCastDuration() {
        return SpellConfig.BreezeDuration.get();
    }

    public int SummonDownDuration() {
        return SpellConfig.BreezeSummonDown.get();
    }

    public SoundEvent CastingSound() {
        return (SoundEvent) ModSounds.PREPARE_SUMMON.get();
    }

    public int defaultSpellCooldown() {
        return SpellConfig.BreezeCoolDown.get();
    }

    public SpellType getSpellType() {
        return SpellType.WIND;
    }

    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList();
        list.add((Enchantment) ModEnchantments.POTENCY.get());
        list.add((Enchantment) ModEnchantments.DURATION.get());
        return list;
    }

    public Predicate<LivingEntity> summonPredicate() {
        return livingEntity -> livingEntity instanceof BreezeServant;
    }

    public int summonLimit() {
        return (Integer) MobsConfig.BreezeServantLimit.get();
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        this.commonResult(worldIn, caster);
        int potency = spellStat.getPotency();
        int duration = spellStat.getDuration();
        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getPotencyLevel(caster);
            duration += WandUtil.getLevels(ModEnchantments.DURATION.get(), caster) + 1;
        }
        if (!this.isShifting(caster)) {
            int count = this.rightStaff(staff) ? 2 : 1;
            for (int i = 0; i < count; ++i) {
                BreezeServant breeze = new BreezeServant(ModEntityTypes.BREEZE_SERVANT.get(), worldIn);
                BlockPos blockPos = BlockFinder.SummonRadius(caster.blockPosition(), breeze, worldIn);
                if (caster.isUnderWater()) {
                    blockPos = BlockFinder.SummonWaterRadius(caster, worldIn);
                }
                breeze.setTrueOwner(ServantAllyUtil.getSummonOwner(caster));
                breeze.moveTo(blockPos, 0.0F, 0.0F);
                MobUtil.moveDownToGround(breeze);
                breeze.setLimitedLife(MobUtil.getSummonLifespan(worldIn) * duration);
                breeze.setPersistenceRequired();
                breeze.finalizeSpawn(worldIn, caster.level().getCurrentDifficultyAt(caster.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
                this.buffSummon(caster, breeze, potency);
                this.SummonSap(caster, breeze);
                this.setTarget(caster, breeze);
                if (worldIn.addFreshEntity(breeze)) {
                    this.uponSummon(worldIn, caster, staff, breeze);
                }
                this.summonAdvancement(caster, breeze);
            }
            this.SummonDown(caster);
            this.playSound(worldIn, caster, (SoundEvent) ModSounds.SUMMON_SPELL.get());
        }
    }
}
