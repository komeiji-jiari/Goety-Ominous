package com.Polarice3.Goety.common.effects;

import com.Polarice3.Goety.utils.ModDamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class UnholyRageEffect extends GoetyBaseEffect{
    public UnholyRageEffect() {
        super(MobEffectCategory.NEUTRAL, 0);
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE,
                "08cdfeab-dca7-40a1-ad22-ae8909c2c7a3",
                0.25D, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED,
                "da84b2ce-6812-4d03-bae5-7052e80e439b",
                0.25D, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    public void applyEffectTick(LivingEntity living, int amplify) {
        if (living.tickCount % 20 == 0) {
            float damage = living.getMaxHealth() * 0.1F;
            living.hurt(ModDamageSource.getDamageSource(living.level, ModDamageSource.BOILING), damage);
        }
    }

    public boolean isDurationEffectTick(int tick, int amplify) {
        return true;
    }
}
