package com.qiuyue.goetyominous.client.render.model.animation;

import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.world.entity.AnimationState;
import org.joml.Vector3f;

public final class PoseBlend {
    private static final Vector3f VECTOR_CACHE = new Vector3f();

    private PoseBlend() {
    }

    public static void animate(HierarchicalModel<?> model, AnimationState state, AnimationDefinition definition, float ageInTicks, float weight) {
        animate(model, state, definition, ageInTicks, 1.0F, weight);
    }

    public static void animate(HierarchicalModel<?> model, AnimationState state, AnimationDefinition definition, float ageInTicks, float speed, float weight) {
        state.updateTime(ageInTicks, speed);
        if (weight <= 0.0F) {
            return;
        }
        state.ifStarted(animation -> KeyframeAnimations.animate(model, definition, animation.getAccumulatedTime(), weight, VECTOR_CACHE));
    }
}
