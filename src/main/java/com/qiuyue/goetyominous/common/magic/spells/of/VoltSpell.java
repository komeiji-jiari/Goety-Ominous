package com.qiuyue.goetyominous.common.magic.spells.of;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.SummonSpell;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import com.qiuyue.goetyominous.common.entities.ally.of.VoltServant;
import com.qiuyue.goetyominous.common.init.of.OfEntityRegistry;
import com.qiuyue.goetyominous.config.MobsConfig;
import com.qiuyue.goetyominous.config.SpellConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public class VoltSpell extends SummonSpell {
    public VoltSpell() {
    }

    public int defaultSoulCost() {
        return (Integer) SpellConfig.VoltSoulCost.get();
    }

    public int defaultCastDuration() {
        return (Integer)SpellConfig.VoltCastDuration.get();
    }

    public int SummonDownDuration() {
        return (Integer)SpellConfig.VoltSummonDown.get();
    }

    public int defaultSpellCooldown() {
        return (Integer)SpellConfig.VoltCoolDown.get();
    }

    public SoundEvent CastingSound(LivingEntity caster) {
        return (SoundEvent)ModSounds.PREPARE_SUMMON.get();
    }

    public SpellType getSpellType() {
        return SpellType.ABYSS;
    }

    public Predicate<LivingEntity> summonPredicate() {
        return (livingEntity) -> {
            return livingEntity instanceof VoltServant;
        };
    }

    public int summonLimit() {
        return (Integer) MobsConfig.VoltServantLimit.get();
    }

    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList();
        list.add((Enchantment)ModEnchantments.POTENCY.get());
        list.add((Enchantment)ModEnchantments.DURATION.get());
        return list;
    }

    @Override
    public boolean rightStaff(ItemStack staff) {
        return super.rightStaff(staff)
                || this.typeStaff(staff, SpellType.STORM)
                || staff.is(ModItems.STORM_STAFF.get());
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
            int count = 1;
            boolean elite = false;
            if (this.rightStaff(staff)) {
                count = 2;
                elite = true;
            }

            for (int i = 0; i < count; ++i) {
                BlockPos blockpos = caster.blockPosition().offset(-2 + caster.getRandom().nextInt(5), 1, -2 + caster.getRandom().nextInt(5));
                VoltServant volt = new VoltServant((EntityType) OfEntityRegistry.VOLT_SERVANT.get(), worldIn);
                volt.setTrueOwner(caster);
                volt.moveTo(blockpos, caster.getYRot(), 0.0F);
                volt.setLimitedLife(MobUtil.getSummonLifespan(worldIn) * duration);
                volt.finalizeSpawn(worldIn, caster.level().getCurrentDifficultyAt(blockpos), MobSpawnType.MOB_SUMMONED, (SpawnGroupData)null, (CompoundTag)null);
                if (elite && !volt.isElite()) {
                    volt.setElite(true);
                    volt.setEliteStats(volt);
                }
                this.buffSummon(caster, volt, potency);
                this.SummonSap(caster, volt);
                this.setTarget(caster, volt);
                worldIn.addFreshEntity(volt);
                this.summonAdvancement(caster, volt);
            }

            this.SummonDown(caster);
            this.playSound(worldIn, caster, (SoundEvent)ModSounds.SUMMON_SPELL.get());
        }

    }
}
