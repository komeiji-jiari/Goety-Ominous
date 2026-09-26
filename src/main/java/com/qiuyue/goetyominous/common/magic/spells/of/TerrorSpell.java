package com.qiuyue.goetyominous.common.magic.spells.of;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.SummonSpell;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import com.qiuyue.goetyominous.common.entities.ally.of.TerrorServant;
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

public class TerrorSpell extends SummonSpell {
    public TerrorSpell() {
    }

    public int defaultSoulCost() {
        return SpellConfig.TerrorSoulCost.get();
    }

    public int defaultCastDuration() {
        return SpellConfig.TerrorCastDuration.get();
    }

    public int SummonDownDuration() {
        return SpellConfig.TerrorSummonDown.get();
    }

    public int defaultSpellCooldown() {
        return SpellConfig.TerrorCoolDown.get();
    }

    public SoundEvent CastingSound() {
        return ModSounds.ABYSS_PREPARE_SPELL.get();
    }

    public SpellType getSpellType() {
        return SpellType.ABYSS;
    }

    public Predicate<LivingEntity> summonPredicate() {
        return livingEntity -> livingEntity instanceof TerrorServant;
    }

    public int summonLimit() {
        return MobsConfig.TerrorServantLimit.get();
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
            int count = this.rightStaff(staff) ? 2 : 1;
            boolean elite = CuriosFinder.hasAbyssSet(caster);

            for (int i = 0; i < count; ++i) {
                BlockPos blockpos = caster.blockPosition().offset(-2 + caster.getRandom().nextInt(5), 1, -2 + caster.getRandom().nextInt(5));
                TerrorServant terror = new TerrorServant(OfEntityRegistry.TERROR_SERVANT.get(), worldIn);
                terror.setTrueOwner(caster);
                terror.moveTo(blockpos, caster.getYRot(), 0.0F);
                terror.setLimitedLife(MobUtil.getSummonLifespan(worldIn) * duration);
                terror.finalizeSpawn(worldIn, caster.level().getCurrentDifficultyAt(blockpos), MobSpawnType.MOB_SUMMONED, (SpawnGroupData) null, (CompoundTag) null);
                if (elite && !terror.isElite()) {
                    terror.setElite(true);
                    terror.setEliteStats(terror);
                }
                this.buffSummon(caster, terror, potency);
                this.SummonSap(caster, terror);
                this.setTarget(caster, terror);
                if (worldIn.addFreshEntity(terror)) {
                    this.uponSummon(worldIn, caster, staff, terror);
                }
                this.summonAdvancement(caster, terror);
            }

            this.SummonDown(caster);
            this.playSound(worldIn, caster, ModSounds.SUMMON_SPELL.get());
        }
    }
}
