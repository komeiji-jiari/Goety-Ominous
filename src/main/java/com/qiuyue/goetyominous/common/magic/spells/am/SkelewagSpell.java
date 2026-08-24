package com.qiuyue.goetyominous.common.magic.spells.am;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.SummonSpell;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.*;
import com.qiuyue.goetyominous.common.entities.ally.am.SkelewagServant;
import com.qiuyue.goetyominous.common.entities.ally.am.StraySkelewagServant;
import com.qiuyue.goetyominous.common.entities.ally.am.WitherSkelewagServant;
import com.qiuyue.goetyominous.common.init.am.AmEntityRegistry;
import com.qiuyue.goetyominous.config.MobsConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.Tags;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class SkelewagSpell extends SummonSpell {
    public SkelewagSpell() {
    }

    public int defaultSoulCost() {
        return com.qiuyue.goetyominous.config.SpellConfig.SkelewagCost.get();
    }

    public int defaultCastDuration() {
        return com.qiuyue.goetyominous.config.SpellConfig.SkelewagDuration.get();
    }

    public SoundEvent CastingSound() {
        return (SoundEvent) ModSounds.PREPARE_SUMMON.get();
    }

    public int defaultSpellCooldown() {
        return com.qiuyue.goetyominous.config.SpellConfig.SkelewagCoolDown.get();
    }

    public int SummonDownDuration() {
        return com.qiuyue.goetyominous.config.SpellConfig.SkelewagSummonDown.get();
    }

    public SpellType getSpellType() {
        return SpellType.NECROMANCY;
    }

    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList();
        list.add((Enchantment) ModEnchantments.POTENCY.get());
        list.add((Enchantment) ModEnchantments.DURATION.get());
        return list;
    }

    public Predicate<LivingEntity> summonPredicate() {
        return (livingEntity) -> {
            return livingEntity instanceof SkelewagServant;
        };
    }

    public int summonLimit() {
        return (Integer) MobsConfig.SkelewagLimit.get();
    }

    public boolean specialStaffs(ItemStack stack) {
        return this.typeStaff(stack, SpellType.FROST) || this.typeStaff(stack, SpellType.NETHER);
    }

    @Override
    public boolean rightStaff(ItemStack stack) {
        return super.rightStaff(stack) || this.typeStaff(stack, SpellType.ABYSS);
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
            int i = 1;
            if (this.rightStaff(staff)) {
                i = 2 + caster.level().random.nextInt(2);
            } else if (this.specialStaffs(staff)) {
                i = 2;
            }

            for (int i1 = 0; i1 < i; ++i1) {
                SkelewagServant skelewag = new SkelewagServant(AmEntityRegistry.SKELEWAG_SERVANT.get(), worldIn);
                BlockPos blockPos = BlockFinder.SummonRadius(caster.blockPosition(), skelewag, worldIn);
                if (caster.isUnderWater()) {
                    blockPos = BlockFinder.SummonWaterRadius(caster, worldIn);
                }

                if (this.typeStaff(staff, SpellType.FROST)) {
                    skelewag = new StraySkelewagServant(AmEntityRegistry.STRAY_SKELEWAG_SERVANT.get(), worldIn);
                } else if (this.typeStaff(staff, SpellType.NETHER)) {
                    skelewag = new WitherSkelewagServant(AmEntityRegistry.WITHER_SKELEWAG_SERVANT.get(), worldIn);
                } else if (worldIn.getBiome(blockPos).is(Tags.Biomes.IS_SNOWY)) {
                    skelewag = new StraySkelewagServant(AmEntityRegistry.STRAY_SKELEWAG_SERVANT.get(), worldIn);
                } else if (worldIn.dimension() == Level.NETHER) {
                    skelewag = new WitherSkelewagServant(AmEntityRegistry.WITHER_SKELEWAG_SERVANT.get(), worldIn);
                }

                skelewag.setTrueOwner(caster);
                skelewag.moveTo(blockPos, 0.0F, 0.0F);
                skelewag.setLimitedLife(MobUtil.getSummonLifespan(worldIn) * duration);
                skelewag.setPersistenceRequired();
                skelewag.finalizeSpawn(worldIn, caster.level().getCurrentDifficultyAt(caster.blockPosition()), MobSpawnType.MOB_SUMMONED, (SpawnGroupData) null, (CompoundTag) null);
                this.buffSummon(caster, skelewag, potency);
                this.SummonSap(caster, skelewag);
                this.setTarget(caster, skelewag);
                if (worldIn.addFreshEntity(skelewag)) {
                    this.uponSummon(worldIn, caster, staff, skelewag);
                }

                this.summonAdvancement(caster, skelewag);
            }

            this.SummonDown(caster);
            this.playSound(worldIn, caster, (SoundEvent) ModSounds.SUMMON_SPELL.get());
        }

    }

}
