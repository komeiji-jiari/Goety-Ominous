package com.qiuyue.goetyominous.common.entities.ally.mobs;

import net.minecraft.world.entity.AnimationState;

public interface ThrowerAnimatable {
    AnimationState getIdleAnimationState();

    AnimationState getWalkAnimationState();

    AnimationState getThrowAnimationState();

    AnimationState getHealAnimationState();

    boolean isThrowing();

    boolean isHealing();

    float getWalkWeight();
}
