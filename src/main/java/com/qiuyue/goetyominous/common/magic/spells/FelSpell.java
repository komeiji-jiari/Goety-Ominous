package com.qiuyue.goetyominous.common.magic.spells;

import com.Polarice3.Goety.common.magic.Spell;
import com.qiuyue.goetyominous.utils.CroneCuriosUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public abstract class FelSpell extends Spell {

    @Override
    public int soulCost(LivingEntity caster, ItemStack staff) {
        int cost = super.soulCost(caster, staff);
        return CroneCuriosUtil.hasCroneRobe(caster) ? cost / 2 : cost;
    }

    @Override
    public boolean ReduceCastTime(LivingEntity caster) {
        return super.ReduceCastTime(caster) || CroneCuriosUtil.hasCroneHat(caster);
    }
}
