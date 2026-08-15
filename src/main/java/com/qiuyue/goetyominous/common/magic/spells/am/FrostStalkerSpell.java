package com.qiuyue.goetyominous.common.magic.spells.am;

import com.qiuyue.goetyominous.common.entities.ally.am.FroststalkerServant;
import com.qiuyue.goetyominous.common.init.am.AmEntityRegistry;
import com.qiuyue.goetyominous.config.MobsConfig;
import com.qiuyue.goetyominous.config.SpellConfig;
import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.SummonSpell;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public class FrostStalkerSpell extends SummonSpell {
    public FrostStalkerSpell() {
    }

    public int defaultSoulCost() {
        return (Integer)SpellConfig.FrostStalkerSoulCost.get();
    }

    public int defaultCastDuration() {
        return (Integer)SpellConfig.FrostStalkerCastDuration.get();
    }

    public int SummonDownDuration() {
        return (Integer)SpellConfig.FrostStalkerSummonDown.get();
    }

    public SoundEvent CastingSound() {
        return (SoundEvent)ModSounds.FROST_PREPARE_SPELL.get();
    }

    public int defaultSpellCooldown() {
        return (Integer)SpellConfig.FrostStalkerCoolDown.get();
    }

    public SpellType getSpellType() {
        return SpellType.FROST;
    }

    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList();
        list.add((Enchantment)ModEnchantments.POTENCY.get());
        list.add((Enchantment)ModEnchantments.DURATION.get());
        return list;
    }

    public Predicate<LivingEntity> summonPredicate() {
        return (livingEntity) -> {
            return livingEntity instanceof FroststalkerServant;
        };
    }

    public int summonLimit() {
        return (Integer) MobsConfig.FrostStalkerLimit.get();
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
                i = 2 + caster.getRandom().nextInt(2);
            }

            for(int i1 = 0; i1 < i; ++i1) {
                FroststalkerServant summonedentity = new FroststalkerServant(AmEntityRegistry.FROSTSTALKER_SERVANT.get(), worldIn);
                summonedentity.setTrueOwner(caster);
                summonedentity.setTemporary(true);
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
}
