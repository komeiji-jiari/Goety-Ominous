package com.qiuyue.someillagerservants.common.magic.spells;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.spider.BroodMotherServant;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.SummonSpell;
import com.Polarice3.Goety.config.SpellConfig;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public class BroodSpell extends SummonSpell {
    public BroodSpell() {
    }

    public SpellStat defaultStats() {
        return super.defaultStats().setDuration(1);
    }

    public int defaultSoulCost() {
        return com.qiuyue.someillagerservants.config.SpellConfig.BroodSoulCost.get();
    }

    public int defaultCastDuration() {
        return 120;
    }

    public int SummonDownDuration() {
        return com.qiuyue.someillagerservants.config.SpellConfig.BroodSummonDown.get();
    }

    public SoundEvent CastingSound() {
        return (SoundEvent)ModSounds.WILD_PREPARE_SPELL.get();
    }

    public int defaultSpellCooldown() {
        return com.qiuyue.someillagerservants.config.SpellConfig.BroodCoolDown.get();
    }

    public SpellType getSpellType() {
        return SpellType.WILD;
    }

    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList();
        list.add((Enchantment)ModEnchantments.POTENCY.get());
        list.add((Enchantment)ModEnchantments.DURATION.get());
        return list;
    }

    public Predicate<LivingEntity> summonPredicate() {
        return (livingEntity) -> {
            return livingEntity instanceof BroodMotherServant;
        };
    }

    public int summonLimit() {
        return (Integer)SpellConfig.BroodMotherLimit.get();
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
                i += caster.getRandom().nextBoolean() ? 1 : 0;
            }

            for(int i1 = 0; i1 < i; ++i1) {
                BroodMotherServant summonedentity = new BroodMotherServant((EntityType)ModEntityType.BROOD_MOTHER_SERVANT.get(), worldIn);
                summonedentity.setTrueOwner(caster);
                summonedentity.moveTo(BlockFinder.SummonRadius(caster.blockPosition(), summonedentity, worldIn), 0.0F, 0.0F);
                MobUtil.moveDownToGround(summonedentity);
                summonedentity.setPersistenceRequired();
                summonedentity.setLimitedLife(MobUtil.getSummonLifespan(worldIn) * duration);
                summonedentity.finalizeSpawn(worldIn, caster.level().getCurrentDifficultyAt(caster.blockPosition()), MobSpawnType.MOB_SUMMONED, (SpawnGroupData)null, (CompoundTag)null);
                this.buffSummon(caster, summonedentity, potency);
                this.SummonSap(caster, summonedentity);
                this.setTarget(caster, summonedentity);
                if (worldIn.addFreshEntity(summonedentity)) {
                    this.uponSummon(worldIn, caster, staff, summonedentity);
                }

                this.summonAdvancement(caster, summonedentity);
            }

            this.SummonDown(caster);
            this.playSound(worldIn, caster, (SoundEvent)ModSounds.SUMMON_SPELL.get());
        }

    }

    public void summonParticles(ServerLevel worldIn, LivingEntity caster, ItemStack staff, LivingEntity summoned) {
        ColorUtil colorUtil = new ColorUtil(657418);
        ServerParticleUtil.summonUndeadParticles(worldIn, summoned, colorUtil, 657418, 657418);
    }

}
