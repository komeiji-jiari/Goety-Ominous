package com.qiuyue.goetyominous.common.magic.spells.of;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.SummonSpell;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import com.qiuyue.goetyominous.common.entities.ally.of.TremblerServant;
import com.qiuyue.goetyominous.common.init.of.OfEntityRegistry;
import com.qiuyue.goetyominous.config.MobsConfig;
import com.qiuyue.goetyominous.config.SpellConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class TremblerSpell extends SummonSpell {
    public TremblerSpell() {
    }

    public int defaultSoulCost() {
        return SpellConfig.TremblerSoulCost.get();
    }

    public int defaultCastDuration() {
        return SpellConfig.TremblerCastDuration.get();
    }

    public int SummonDownDuration() {
        return SpellConfig.TremblerSummonDown.get();
    }

    public int defaultSpellCooldown() {
        return SpellConfig.TremblerCoolDown.get();
    }

    public SoundEvent CastingSound() {
        return ModSounds.PREPARE_SUMMON.get();
    }

    public SpellType getSpellType() {
        return SpellType.GEOMANCY;
    }

    public Predicate<LivingEntity> summonPredicate() {
        return livingEntity -> livingEntity instanceof TremblerServant;
    }

    public int summonLimit() {
        return MobsConfig.TremblerServantLimit.get();
    }

    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.DURATION.get());
        return list;
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
            int count = 1;
            boolean elite = false;
            if (this.rightStaff(staff)) {
                count = worldIn.getRandom().nextIntBetweenInclusive(2, 5);
                elite = true;
            }

            for (int i = 0; i < count; ++i) {
                TremblerServant trembler = new TremblerServant(OfEntityRegistry.TREMBLER_SERVANT.get(), worldIn);
                BlockPos blockpos = BlockFinder.SummonRadius(caster.blockPosition(), trembler, worldIn);
                trembler.setTrueOwner(caster);
                trembler.moveTo(blockpos, caster.getYRot(), 0.0F);
                MobUtil.moveDownToGround(trembler);
                trembler.setLimitedLife(MobUtil.getSummonLifespan(worldIn) * duration);
                trembler.finalizeSpawn(worldIn, caster.level().getCurrentDifficultyAt(blockpos), MobSpawnType.MOB_SUMMONED, (SpawnGroupData) null, (CompoundTag) null);
                if (elite && !trembler.isElite()) {
                    trembler.setElite(true);
                    trembler.setEliteStats(trembler);
                }
                this.buffSummon(caster, trembler, potency);
                this.SummonSap(caster, trembler);
                this.setTarget(caster, trembler);
                if (worldIn.addFreshEntity(trembler)) {
                    this.uponSummon(worldIn, caster, staff, trembler);
                }
                this.summonAdvancement(caster, trembler);
            }

            this.SummonDown(caster);
            this.playSound(worldIn, caster, ModSounds.SUMMON_SPELL.get());
        }
    }
}
