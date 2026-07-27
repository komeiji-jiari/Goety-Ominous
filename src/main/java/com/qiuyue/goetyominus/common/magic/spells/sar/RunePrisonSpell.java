package com.qiuyue.goetyominus.common.magic.spells.sar;

import com.Polarice3.Goety.api.magic.SpellType;
import com.Polarice3.Goety.common.enchantments.ModEnchantments;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.common.magic.Spell;
import com.Polarice3.Goety.common.magic.SpellStat;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.Polarice3.Goety.utils.WandUtil;
import com.qiuyue.goetyominus.common.entities.ally.sar.RunePrison;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class RunePrisonSpell extends Spell {

    @Override
    public SpellStat defaultStats() {
        return super.defaultStats().setPotency(0).setRange(10);
    }

    @Override
    public int defaultSoulCost() {
        return com.qiuyue.goetyominus.config.SpellConfig.RunePrisonSoulCost.get();
    }

    @Override
    public int defaultCastDuration() {
        return 10;
    }

    @Override
    public int defaultSpellCooldown() {
        return com.qiuyue.goetyominus.config.SpellConfig.RunePrisonCooldown.get();
    }

    @Override
    public SoundEvent CastingSound() {
        return ModSounds.PREPARE_SPELL.get();
    }

    @Override
    public SpellType getSpellType() {
        return SpellType.ILL;
    }

    @Override
    public List<Enchantment> acceptedEnchantments() {
        List<Enchantment> list = new ArrayList<>();
        list.add(ModEnchantments.POTENCY.get());
        list.add(ModEnchantments.RANGE.get());
        return list;
    }

    @Override
    public boolean conditionsMet(ServerLevel worldIn, LivingEntity caster) {
        LivingEntity target = this.getTarget(caster);
        if (target == null) {
            return false;
        }

        if (target instanceof com.Polarice3.Goety.common.entities.neutral.Owned owned) {
            LivingEntity trueOwner = owned.getTrueOwner();
            if (trueOwner != null && (trueOwner == caster || caster.isAlliedTo(trueOwner))) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void SpellResult(ServerLevel worldIn, LivingEntity caster, ItemStack staff, SpellStat spellStat) {
        LivingEntity target = this.getTarget(caster);
        if (target == null) {
            return;
        }

        int potency = spellStat.getPotency();
        int range = spellStat.getRange();

        if (WandUtil.enchantedFocus(caster)) {
            potency += WandUtil.getPotencyLevel(caster);
            range += WandUtil.getRangeLevel(caster);
        }

        Vec3 prisonPos = target.position();

        int prisonDuration = 60;
        boolean fromTrap = false;

        RunePrison runePrison = new RunePrison(worldIn, null, prisonDuration, fromTrap, caster);
        runePrison.moveTo(prisonPos.x, prisonPos.y + 0.5, prisonPos.z, 0.0F, 0.0F);
        worldIn.addFreshEntity(runePrison);

        this.playSound(worldIn, caster, 1.0F, 1.0F);

        if (staff.is(ModItems.OMINOUS_STAFF.get())) {
            float damage = com.qiuyue.goetyominus.config.SpellConfig.RunePrisonDamage.get().floatValue() + potency;
            for (LivingEntity entity : worldIn.getEntitiesOfClass(LivingEntity.class, runePrison.getBoundingBox())) {
                if (entity == caster) {
                    continue;
                }

                if (caster.isAlliedTo(entity)) {
                    continue;
                }

                if (entity instanceof com.Polarice3.Goety.common.entities.neutral.Owned owned) {
                    LivingEntity trueOwner = owned.getTrueOwner();
                    if (trueOwner != null && (trueOwner == caster || caster.isAlliedTo(trueOwner))) {
                        continue;
                    }
                }

                entity.hurt(ModDamageSource.noKnockbackDamageSource(worldIn, ModDamageSource.MAGIC_BOLT, caster, caster), damage);
            }
        }
    }
}