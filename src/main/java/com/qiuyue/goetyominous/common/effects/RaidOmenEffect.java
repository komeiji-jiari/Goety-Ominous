package com.qiuyue.goetyominous.common.effects;

import com.qiuyue.goetyominous.common.blocks.trial.RaidOmenController;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class RaidOmenEffect extends MobEffect {

    public RaidOmenEffect() {
        super(MobEffectCategory.NEUTRAL, 0xDE4D58);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration == 1;
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity instanceof ServerPlayer player) {
            RaidOmenController.finish(player);
        }
    }
}
