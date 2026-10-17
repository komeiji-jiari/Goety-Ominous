package com.qiuyue.goetyominous.common.effects.ac;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class CurseOfRlyehEffect extends MobEffect {

    private static final String MOVEMENT_SPEED_UUID = "8c1a3f52-2b6e-4f0a-9d7c-5e4b3a2c1d90";
    private static final String ATTACK_DAMAGE_UUID = "b7e5d924-6f1c-4a82-9e03-1d2c8f7a5b64";
    private static final double PENALTY_PER_LEVEL = -0.15D;
    private static final int MAX_LEVELS = 3;

    public CurseOfRlyehEffect() {
        super(MobEffectCategory.HARMFUL, 0x2E8B84);
        this.addAttributeModifier(Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED_UUID,
                PENALTY_PER_LEVEL, AttributeModifier.Operation.MULTIPLY_TOTAL);
        this.addAttributeModifier(Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE_UUID,
                PENALTY_PER_LEVEL, AttributeModifier.Operation.MULTIPLY_TOTAL);
    }

    @Override
    public double getAttributeModifierValue(int amplifier, AttributeModifier modifier) {
        return modifier.getAmount() * Math.min(amplifier + 1, MAX_LEVELS);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return false;
    }
}
