package com.qiuyue.goetyominous.common.magic.spells.am;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.utils.WandUtil;
import com.qiuyue.goetyominous.common.entities.projectile.EntityServentSandShot;
import com.qiuyue.goetyominous.config.SpellConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.List;

public class SandSpell extends Spell {
    public SandSpell() {
    }

    public SpellStat defaultStats() {
        return super.defaultStats().setVelocity(1.6F);
    }

    public int defaultSoulCost() {
        return (Integer) SpellConfig.SandSoulCost.get();
    }

    public int defaultCastDuration() {
        return (Integer) SpellConfig.SandCastDuration.get();
    }

    public SoundEvent CastingSound() {
        return (SoundEvent) SoundEvents.SAND_BREAK;
    }

    public int defaultSpellCooldown() {
        return (Integer) SpellConfig.SandCoolDown.get();
    }

    public SpellType getSpellType() {
        return SpellType.WIND;
    }

    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList();
        list.add((Enchantment) ModEnchantments.POTENCY.get());
        list.add((Enchantment) ModEnchantments.VELOCITY.get());
        list.add((Enchantment) ModEnchantments.DURATION.get());
        return list;
    }

    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        float velocity = spellStat.getVelocity();
        int potency = spellStat.getPotency();
        int duration = spellStat.getDuration();
        if (WandUtil.enchantedFocus(caster)) {
            velocity += WandUtil.getLevels((Enchantment) ModEnchantments.VELOCITY.get(), caster) / 3.0F;
            potency += WandUtil.getPotencyLevel(caster);
            duration += WandUtil.getLevels((Enchantment) ModEnchantments.DURATION.get(), caster);
        }

        boolean right = this.rightStaff(staff);
        EntityServentSandShot sandShot = new EntityServentSandShot(worldIn, caster, right);
        float damage = SpellConfig.SandExtraDamage.get().floatValue() + potency;
        if (right) {
            damage *= 2.0F;
            sandShot.setKnockback(1.5F);
        }
        sandShot.setExtraDamage(damage);
        sandShot.setBlindnessDuration(100 * Math.max(1, duration));
        sandShot.shootFromRotation(caster, caster.getXRot(), caster.getYRot(), 0.0F, velocity, 1.0F);
        worldIn.addFreshEntity(sandShot);

        this.playSound(worldIn, caster, (SoundEvent) SoundEvents.SAND_BREAK, 1.0F, this.projPitch(worldIn.getRandom()));
    }
}
