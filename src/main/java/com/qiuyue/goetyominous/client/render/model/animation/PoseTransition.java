package com.qiuyue.goetyominous.client.render.model.animation;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import org.joml.Vector3f;

final class PoseTransition {
    private PoseTransition() {
    }

    static AnimationDefinition build(AnimationDefinition pose, float length, boolean intoPose) {
        AnimationDefinition.Builder builder = AnimationDefinition.Builder.withLength(length);
        pose.boneAnimations().forEach((bone, channels) -> {
            for (AnimationChannel channel : channels) {
                Vector3f held = channel.keyframes()[0].target();
                if (held.equals(0.0F, 0.0F, 0.0F)) {
                    continue;
                }
                Keyframe from = intoPose
                        ? new Keyframe(0.0F, new Vector3f(), AnimationChannel.Interpolations.LINEAR)
                        : new Keyframe(0.0F, new Vector3f(held), AnimationChannel.Interpolations.LINEAR);
                Keyframe to = intoPose
                        ? new Keyframe(length, new Vector3f(held), AnimationChannel.Interpolations.LINEAR)
                        : new Keyframe(length, new Vector3f(), AnimationChannel.Interpolations.LINEAR);
                builder.addAnimation(bone, new AnimationChannel(channel.target(), from, to));
            }
        });
        return builder.build();
    }
}
