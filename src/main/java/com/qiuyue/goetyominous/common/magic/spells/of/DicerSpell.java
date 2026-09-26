package com.qiuyue.goetyominous.common.magic.spells.of;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.SummonSpell;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import com.qiuyue.goetyominous.common.entities.ally.of.DicerServant;
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

public class DicerSpell extends SummonSpell {
    public DicerSpell() {
    }

    public int defaultSoulCost() {
        return SpellConfig.DicerSoulCost.get();
    }

    public int defaultCastDuration() {
        return SpellConfig.DicerCastDuration.get();
    }

    public int SummonDownDuration() {
        return SpellConfig.DicerSummonDown.get();
    }

    public int defaultSpellCooldown() {
        return SpellConfig.DicerCoolDown.get();
    }

    public SoundEvent CastingSound() {
        return ModSounds.PREPARE_SUMMON.get();
    }

    public SpellType getSpellType() {
        return SpellType.STORM;
    }

    public Predicate<LivingEntity> summonPredicate() {
        return livingEntity -> livingEntity instanceof DicerServant;
    }

    public int summonLimit() {
        return MobsConfig.DicerServantLimit.get();
    }

    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.DURATION.get());
        return list;
    }

    @Override
    public boolean rightStaff(ItemStack staff) {
        return super.rightStaff(staff) || this.typeStaff(staff, SpellType.NETHER);
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
            boolean elite = CuriosFinder.hasNetherSet(caster);

            for (int i = 0; i < count; ++i) {
                DicerServant dicer = new DicerServant(OfEntityRegistry.DICER_SERVANT.get(), worldIn);
                BlockPos blockpos = BlockFinder.SummonRadius(caster.blockPosition(), dicer, worldIn);
                dicer.setTrueOwner(caster);
                dicer.moveTo(blockpos, caster.getYRot(), 0.0F);
                MobUtil.moveDownToGround(dicer);
                dicer.setLimitedLife(MobUtil.getSummonLifespan(worldIn) * duration);
                dicer.finalizeSpawn(worldIn, caster.level().getCurrentDifficultyAt(blockpos), MobSpawnType.MOB_SUMMONED, (SpawnGroupData) null, (CompoundTag) null);
                if (elite && !dicer.isElite()) {
                    dicer.setElite(true);
                    dicer.setEliteStats(dicer);
                }
                this.buffSummon(caster, dicer, potency);
                this.SummonSap(caster, dicer);
                this.setTarget(caster, dicer);
                if (worldIn.addFreshEntity(dicer)) {
                    this.uponSummon(worldIn, caster, staff, dicer);
                }
                this.summonAdvancement(caster, dicer);
            }

            this.SummonDown(caster);
            this.playSound(worldIn, caster, ModSounds.SUMMON_SPELL.get());
        }
    }
}
