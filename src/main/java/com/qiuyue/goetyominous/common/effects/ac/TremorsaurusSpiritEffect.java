package com.qiuyue.goetyominous.common.effects.ac;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class TremorsaurusSpiritEffect extends MobEffect {

    public TremorsaurusSpiritEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFFC107);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return false;
    }
}
