package com.qiuyue.goetyominous.common.magic.spells.mm;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.BreathingSpell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import com.Polarice3.Goety.utils.WandUtil;
import com.alexander.mutantmore.init.ParticleTypeInit;
import com.qiuyue.goetyominous.config.mm.MmConfig;
import java.util.List;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

public class PoisonBreathSpell extends BreathingSpell {

    @Override
    public SpellStat defaultStats() {
        return super.defaultStats().setRange(8).setDuration(5);
    }

    @Override
    public int defaultSoulCost() {
        return MmConfig.PoisonBreathSoulCost.get();
    }

    @Override
    public int shotsNumber() {
        return MmConfig.PoisonBreathDuration.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return MmConfig.PoisonBreathCooldown.get();
    }

    @Override
    public SoundEvent CastingSound() {
        return ModSounds.WILD_PREPARE_SPELL.get();
    }

    @Override
    public SoundEvent loopSound(LivingEntity caster) {
        return SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT;
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.WILD;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        return List.of(ModEnchantments.POTENCY.get(), ModEnchantments.RANGE.get(), ModEnchantments.DURATION.get());
    }

    @Override
    public void useParticle(Level level, LivingEntity caster, ItemStack staff) {
        if (level instanceof ServerLevel serverLevel) {
            ServerParticleUtil.addParticlesAroundMiddleSelf(serverLevel, ParticleTypeInit.POISON_CLOUD.get(), caster);
        }
    }

    @Override
    public void showWandBreath(LivingEntity caster, ItemStack staff, SpellStat stat) {
        int range = stat.getRange();
        if (WandUtil.enchantedFocus(caster)) {
            range += WandUtil.getRangeLevel(caster);
        }
        ParticleOptions poison = ParticleTypeInit.POISON_CLOUD.get();
        if (this.rightStaff(staff)) {
            this.dragonBreathAttack(poison, caster, (double) range / 10.0D * 0.35D);
        } else {
            this.dragonBreathAttack(poison, caster, 10, (double) range / 10.0D * 0.55D, 0.05D);
        }
    }

    @Override
    public boolean conditionsMet(ServerLevel level, LivingEntity caster, SpellStat stat) {
        if (caster instanceof Mob mob && mob.getTarget() != null) {
            int range = stat.getRange();
            if (WandUtil.enchantedFocus(caster)) {
                range += WandUtil.getRangeLevel(caster);
            }
            return mob.hasLineOfSight(mob.getTarget()) && (double) mob.distanceTo(mob.getTarget()) <= (double) range + 4.0D;
        }
        return super.conditionsMet(level, caster, stat);
    }

    @Override
    public void SpellResult(ServerLevel level, LivingEntity caster, ItemStack staff, SpellStat stat) {
        int range = stat.getRange();
        int duration = stat.getDuration();
        int potency = stat.getPotency();
        if (WandUtil.enchantedFocus(caster)) {
            range += WandUtil.getRangeLevel(caster);
            duration += WandUtil.getLevels(ModEnchantments.DURATION.get(), caster);
            potency += WandUtil.getPotencyLevel(caster);
        }

        float damage = MmConfig.PoisonBreathDamage.get().floatValue() + potency;
        if (this.rightStaff(staff)) {
            damage *= 2.0F;
        }
        int effectTicks = MathHelper.secondsToTicks(1) * duration;
        MobEffect effect = CuriosFinder.hasWildRobe(caster) ? GoetyEffects.ACID_VENOM.get() : MobEffects.POISON;
        DamageSource source = ModDamageSource.noKnockbackDamageSource(level, ModDamageSource.VENOM, caster, caster);

        for (Entity target : this.getBreathTarget(caster, (double) range)) {
            LivingEntity living = MobUtil.getLivingTarget(target);
            if (living == null) {
                continue;
            }
            if (living.hurt(source, damage)) {
                living.addEffect(new MobEffectInstance(effect, effectTicks));
            }
        }
    }
}
