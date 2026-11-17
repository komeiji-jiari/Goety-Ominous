package com.qiuyue.goetyominous.common.entities;

import net.minecraft.world.entity.AnimationState;

public interface FungusThrowerAnimated {
    AnimationState getIdleAnimationState();

    AnimationState getWalkAnimationState();

    AnimationState getThrowAnimationState();

    boolean isThrowing();

    float getWalkWeight();
}
