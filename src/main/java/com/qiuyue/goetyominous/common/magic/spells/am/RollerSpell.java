package com.qiuyue.goetyominous.common.magic.spells.am;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.SummonSpell;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import com.qiuyue.goetyominous.common.entities.ally.am.RockyRollerServant;
import com.qiuyue.goetyominous.common.init.am.AmEntityRegistry;
import com.qiuyue.goetyominous.config.MobsConfig;
import com.qiuyue.goetyominous.config.SpellConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class RollerSpell extends SummonSpell {
    public RollerSpell() {
    }

    public int defaultSoulCost() {
        return (Integer)SpellConfig.RollerCost.get();
    }

    public int defaultCastDuration() {
        return (Integer) SpellConfig.RollerDuration.get();
    }

    public @Nullable SoundEvent CastingSound() {
        return (SoundEvent)ModSounds.PREPARE_SUMMON.get();
    }

    public int defaultSpellCooldown() {
        return (Integer)SpellConfig.RollerCoolDown.get();
    }

    public int SummonDownDuration() {
        return (Integer)SpellConfig.RollerSummonDown.get();
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
            return livingEntity instanceof RockyRollerServant;
        };
    }

    public int summonLimit() {
        return (Integer) MobsConfig.RockyRollerLimit.get();
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
                i = 2;
            }

            for(int i1 = 0; i1 < i; ++i1) {
                Summoned summonedentity = new RockyRollerServant((EntityType) AmEntityRegistry.ROCKY_ROLLER_SERVANT.get(), worldIn);

                BlockPos blockPos = BlockFinder.SummonRadius(caster.blockPosition(), (Entity)summonedentity, worldIn);
                ((Summoned)summonedentity).setTrueOwner(caster);
                ((Summoned)summonedentity).moveTo(blockPos, 0.0F, 0.0F);
                MobUtil.moveDownToGround((Entity)summonedentity);
                int life = MobUtil.getSummonLifespan(worldIn) * duration;

                ((Summoned)summonedentity).setLimitedLife(life);
                ((Summoned)summonedentity).setPersistenceRequired();
                ((Summoned)summonedentity).finalizeSpawn(worldIn, caster.level().getCurrentDifficultyAt(caster.blockPosition()), MobSpawnType.MOB_SUMMONED, (SpawnGroupData)null, (CompoundTag)null);

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