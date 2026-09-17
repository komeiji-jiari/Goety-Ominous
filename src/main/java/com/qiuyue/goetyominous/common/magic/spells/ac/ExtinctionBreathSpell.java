package com.qiuyue.goetyominous.common.magic.spells.ac;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.BreathingSpell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import com.Polarice3.Goety.utils.WandUtil;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.qiuyue.goetyominous.config.SpellConfig;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class ExtinctionBreathSpell extends BreathingSpell {

    public float damage;

    public ExtinctionBreathSpell() {
        this.damage = SpellConfig.ExtinctionBreathDamage.get().floatValue() * WandUtil.damageMultiply();
    }

    @Override
    public SpellStat defaultStats() {
        return super.defaultStats().setRange(8).setBurning(1);
    }

    @Override
    public int defaultSoulCost() {
        return SpellConfig.ExtinctionBreathSoulCost.get();
    }

    @Override
    public int defaultCastUp() {
        return SpellConfig.ExtinctionBreathChargeUp.get();
    }

    @Override
    public int shotsNumber() {
        return SpellConfig.ExtinctionBreathDuration.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.ExtinctionBreathCoolDown.get();
    }

    @Override
    public SoundEvent CastingSound() {
        return ModSounds.FIRE_BREATH_START.get();
    }

    @Override
    public SoundEvent loopSound(LivingEntity caster) {
        return ModSounds.FIRE_BREATH.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.NETHER;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.RANGE.get());
        list.add(ModEnchantments.BURNING.get());
        return list;
    }

    @Override
    public void useParticle(Level level, LivingEntity caster, ItemStack staff) {
        if (level instanceof ServerLevel serverLevel) {
            ServerParticleUtil.addParticlesAroundMiddleSelf(serverLevel, ParticleTypes.SMOKE, caster);
        }
    }

    @Override
    public void showWandBreath(LivingEntity caster, ItemStack staff, SpellStat stat) {
        int range = stat.getRange();
        if (WandUtil.enchantedFocus(caster)) {
            range += WandUtil.getRangeLevel(caster);
        }
        ParticleOptions flame = ACParticleRegistry.TEPHRA_FLAME.get();
        if (this.rightStaff(staff)) {
            this.dragonBreathAttack(flame, caster, (double) range / 10.0D * 0.35D);
        } else {
            this.dragonBreathAttack(flame, caster, 10, (double) range / 10.0D * 0.55D, 0.05D);
        }
    }

    @Override
    public boolean conditionsMet(ServerLevel level, LivingEntity caster, SpellStat stat) {
        if (caster instanceof Mob mob && mob.getTarget() != null) {
            int range = stat.getRange();
            if (WandUtil.enchantedFocus(caster)) {
                range += WandUtil.getRangeLevel(caster);
            }
            return mob.hasLineOfSight(mob.getTarget())
                    && (double) mob.distanceTo(mob.getTarget()) <= (double) range + 4.0D;
        }
        return super.conditionsMet(level, caster, stat);
    }

    @Override
    public void SpellResult(ServerLevel level, LivingEntity caster, ItemStack staff, SpellStat stat) {
        float potency = (float) stat.getPotency();
        int burning = stat.getBurning();
        int range = stat.getRange();
        if (WandUtil.enchantedFocus(caster)) {
            potency += (float) WandUtil.getPotencyLevel(caster);
            burning += WandUtil.getLevels(ModEnchantments.BURNING.get(), caster);
            range += WandUtil.getRangeLevel(caster);
        }
        float damage = this.damage + potency;

        if (!level.isClientSide) {
            if (this.rightStaff(staff)) {
                damage *= 2.0F;
            }
            boolean fullPower = CuriosFinder.hasNetherRobe(caster);
            for (Entity entity : this.getBreathTarget(caster, (double) range)) {
                if (entity == null) {
                    continue;
                }
                float finalDamage = damage;
                if (!fullPower && entity.fireImmune()) {
                    finalDamage *= 0.5F;
                }
                if (entity.hurt(ModDamageSource.magicFireBreath(caster, caster), finalDamage)) {
                    entity.setSecondsOnFire(5 * burning);
                }
            }
        }
    }
}
