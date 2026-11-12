package com.qiuyue.goetyominous.client.render;

import com.qiuyue.goetyominous.client.render.model.GrimMonarchModel;
import com.qiuyue.goetyominous.common.entities.hostile.grimmonarch.GrimMonarchAction;
import com.qiuyue.goetyominous.common.entities.hostile.grimmonarch.GrimMonarchAnimationTable;
import com.qiuyue.goetyominous.common.entities.hostile.grimmonarch.GrimMonarchEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;
import java.util.WeakHashMap;

@OnlyIn(Dist.CLIENT)
public final class GrimMonarchAnimator {
    private static final Map<GrimMonarchEntity, WalkState> WALK_STATES = new WeakHashMap<>();
    private static final Map<GrimMonarchEntity, TransitionState> TRANSITIONS = new WeakHashMap<>();
    private static final float TRANSITION_TICKS = 4.0F;
    private static final float WALK_PHASE_SCALE = 0.05F;

    private GrimMonarchAnimator() {
    }

    public static void setup(GrimMonarchModel model, GrimMonarchEntity entity, float limbSwing,
                             float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float partial = ageInTicks - (float) entity.tickCount;
        float[][] pose;
        if (entity.action() == GrimMonarchAction.IDLE) {
            WalkState state = WALK_STATES.computeIfAbsent(entity, value -> new WalkState());
            float delta = state.lastAge < 0.0F ? 0.0F : Mth.clamp(ageInTicks - state.lastAge, 0.0F, 2.0F);
            float target = Mth.clamp(limbSwingAmount * 4.0F, 0.0F, 1.0F);
            float weight = state.walkWeight
                    + (target - state.walkWeight) * (1.0F - (float) Math.exp(-delta * 0.3F));
            float clock = limbSwing * WALK_PHASE_SCALE;
            pose = GrimMonarchClips.sample("idle", ageInTicks * 0.05F);
            float[][] walk = GrimMonarchClips.sample("walk", clock);
            GrimMonarchClips.blend(pose, walk, weight);
            state.lastAge = ageInTicks;
            state.walkWeight = weight;
        } else {
            GrimMonarchAnimationTable.Sample sample =
                    GrimMonarchAnimationTable.sample(entity.action(), entity.actionSeconds(partial));
            pose = GrimMonarchClips.sample(sample.clip(), sample.time());
        }
        String key = entity.action() == GrimMonarchAction.IDLE ? "idle" : entity.action().name();
        TransitionState transition = TRANSITIONS.computeIfAbsent(entity, value -> new TransitionState());
        if (!key.equals(transition.action)) {
            if (transition.pose != null) {
                transition.from = GrimMonarchClips.copy(transition.pose);
                transition.endAge = ageInTicks + TRANSITION_TICKS;
            }
            transition.action = key;
        }
        if (transition.from != null && ageInTicks < transition.endAge) {
            float weight = Mth.clamp((transition.endAge - ageInTicks) / TRANSITION_TICKS, 0.0F, 1.0F);
            GrimMonarchClips.blend(pose, transition.from, weight);
        } else {
            transition.from = null;
        }
        transition.pose = GrimMonarchClips.copy(pose);
        GrimMonarchClips.apply(model, pose);
        ModelPart head = model.part("head");
        if (head != null) {
            head.yRot += netHeadYaw * ((float) Math.PI / 180.0F);
            head.xRot += headPitch * ((float) Math.PI / 180.0F);
        }
    }

    private static final class TransitionState {
        private String action = "";
        private float[][] from;
        private float[][] pose;
        private float endAge;
    }

    private static final class WalkState {
        private float lastAge = -1.0F;
        private float walkWeight;
    }
}
