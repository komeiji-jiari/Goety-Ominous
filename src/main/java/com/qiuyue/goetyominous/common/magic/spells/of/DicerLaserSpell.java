package com.qiuyue.goetyominous.common.magic.spells.of;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.utils.WandUtil;
import com.qiuyue.goetyominous.common.entities.projectile.DicerServantLaser;
import com.qiuyue.goetyominous.config.SpellConfig;
import com.unusualmodding.opposing_force.registry.OPSoundEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.List;

public class DicerLaserSpell extends Spell {

    private static final int BASE_DURATION = 89;

    private static final float BASE_DAMAGE = 4.0F;

    @Override
    public int defaultSoulCost() {
        return SpellConfig.DicerLaserCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return SpellConfig.DicerLaserCastDuration.get();
    }

    @Override
    public int defaultSpellCooldown() {
        return SpellConfig.DicerLaserCoolDown.get();
    }

    @Override
    public SoundEvent CastingSound() {
        return OPSoundEvents.DICER_IDLE.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.STORM;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.DURATION.get());
        return list;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        int potency = spellStat.getPotency();
        int duration = spellStat.getDuration();
        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getPotencyLevel(caster);
            duration += WandUtil.getLevels(ModEnchantments.DURATION.get(), caster) + 1;
        }

        DicerServantLaser laser = new DicerServantLaser(
                worldIn, caster,
                caster.getX(), caster.getEyeY() - 0.2D, caster.getZ(),
                (float) (((double) caster.yHeadRot + 90.0D) * Math.PI / 180.0D),
                (float) (-(double) caster.getXRot() * Math.PI / 180.0D),
                BASE_DURATION * (duration + 1),
                BASE_DAMAGE + potency);
        laser.setEyeSpawn(true);
        laser.setImmediate(true);
        laser.setStorm(this.rightStaff(staff));
        laser.setFiery(this.typeStaff(staff, SpellType.NETHER));
        worldIn.addFreshEntity(laser);
        this.playSound(worldIn, caster, OPSoundEvents.DICER_LASER.get());
    }
}
