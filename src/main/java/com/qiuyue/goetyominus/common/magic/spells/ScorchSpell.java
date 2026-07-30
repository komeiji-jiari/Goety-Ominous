package com.qiuyue.goetyominus.common.magic.spells;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.entities.neutral.Minion;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.common.magic.SummonSpell;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.WandUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import com.qiuyue.goetyominus.common.entities.hostile.Scorch;
import com.qiuyue.goetyominus.common.init.ModEntityTypes;
import com.qiuyue.goetyominus.config.SpellConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public class ScorchSpell extends SummonSpell {
    public ScorchSpell() {
    }

    public int defaultSoulCost() {
        return (Integer)SpellConfig.ScorchSoulCost.get();
    }

    public int defaultCastDuration() {
        return (Integer)SpellConfig.ScorchCastDuration.get();
    }

    public int SummonDownDuration() {
        return (Integer)SpellConfig.ScorchSummonDown.get();
    }

    public SoundEvent CastingSound() {
        return SoundEvents.EVOKER_PREPARE_SUMMON;
    }

    public int defaultSpellCooldown() {
        return (Integer)SpellConfig.ScorchCoolDown.get();
    }

    public SpellType getSpellType() {
        return SpellType.NETHER;
    }

    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList();
        list.add((Enchantment)ModEnchantments.POTENCY.get());
        list.add((Enchantment)ModEnchantments.DURATION.get());
        return list;
    }

    public ColorUtil particleColors(LivingEntity caster) {
        return new ColorUtil(0.7F, 0.7F, 0.8F);
    }

    public int summonLimit() {
        return (Integer) com.Polarice3.Goety.config.SpellConfig.VexLimit.get();
    }

    public Predicate<LivingEntity> summonPredicate() {
        return livingEntity -> livingEntity instanceof Scorch;
    }

    public void commonResult(ServerLevel worldIn, LivingEntity caster) {
        if (this.isShifting(caster)) {
            for (Entity entity : worldIn.getAllEntities()) {
                if (entity instanceof Minion minion
                        && minion instanceof Scorch
                        && minion.getTrueOwner() == caster) {
                    caster.heal(2.0F);
                    entity.kill();
                }
            }
            for (int i = 0; i < caster.level().random.nextInt(35) + 10; ++i) {
                worldIn.sendParticles(ParticleTypes.POOF,
                        caster.getX(), caster.getEyeY(), caster.getZ(),
                        1, 0.0, 0.0, 0.0, 0.0);
            }
            this.playSound(worldIn, caster, SoundEvents.EVOKER_CAST_SPELL);
        }
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
            int count = staff.is(com.Polarice3.Goety.common.items.ModItems.NETHER_STAFF.get()) ? 3 + worldIn.random.nextInt(3) : 3;

            for (int i = 0; i < count; ++i) {
                BlockPos blockpos = caster.blockPosition().offset(
                        -2 + caster.getRandom().nextInt(5), 1,
                        -2 + caster.getRandom().nextInt(5));
                Scorch scorch = new Scorch(ModEntityTypes.SCORCH.get(), worldIn);
                scorch.setTrueOwner(caster);
                scorch.moveTo(blockpos, 0.0F, 0.0F);
                scorch.finalizeSpawn(worldIn, caster.level().getCurrentDifficultyAt(blockpos),
                        MobSpawnType.MOB_SUMMONED, null, null);
                scorch.setBoundOrigin(blockpos);
                scorch.setLimitedLife(MobUtil.getSummonLifespan(worldIn) * duration);

                this.SummonSap(caster, scorch);
                this.setTarget(caster, scorch);
                worldIn.addFreshEntity(scorch);
                this.summonAdvancement(caster, scorch);
            }

            this.playSound(worldIn, caster, SoundEvents.EVOKER_CAST_SPELL);
            this.SummonDown(caster);
        }
    }
}