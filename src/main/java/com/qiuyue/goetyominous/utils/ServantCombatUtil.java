package com.qiuyue.goetyominous.utils;

import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.utils.EffectsUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;

public final class ServantCombatUtil {

    private ServantCombatUtil() {
    }

    public static float getSpecialAttackDamage(LivingEntity attacker, float baseDamage) {
        float bonus = 0.0F;
        AttributeInstance attribute = attacker.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attribute != null) {
            bonus = (float) (attribute.getValue() - attribute.getBaseValue());
        } else if (attacker.hasEffect(GoetyEffects.BUFF.get())) {
            bonus = EffectsUtil.getAmplifier(attacker, GoetyEffects.BUFF.get()) + 1.0F;
        }
        return Math.max(0.0F, baseDamage + bonus);
    }
}
