package com.qiuyue.goetyominous.common.entities.ally.mobs;

import net.minecraft.world.entity.AnimationState;

public interface DrummerAnimatable {
    AnimationState getIdleAnimationState();

    AnimationState getWalkAnimationState();

    AnimationState getDrumAnimationState();

    AnimationState getCheerAnimationState();

    boolean isDrumming();

    boolean isCheering();

    float getWalkWeight();
}
