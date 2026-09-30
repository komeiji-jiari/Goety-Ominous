package com.qiuyue.goetyominous.client.render.model.lm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.common.entities.ally.lm.ResurrectedKnightServant;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Animations.PossessedArmor.ResurrectedKnight.ResurrectedKnightAnimations;
import net.miauczel.legendary_monsters.entity.client.Model.ResurrectedKnightModel;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AnimationState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ResurrectedKnightServantModel<T extends ResurrectedKnightServant> extends HierarchicalModel<T> {

    private static final AnimationDefinition SLEEP_IN = reverse(ResurrectedKnightAnimations.awaken);
    private static final long SLEEP_IN_MILLIS = (long) (lastTimestamp(SLEEP_IN) * 1000.0F);
    private static final Vector3f ANIMATION_VECTOR_CACHE = new Vector3f();

    private static float lastTimestamp(AnimationDefinition definition) {
        float last = 0.0F;
        for (List<AnimationChannel> channels : definition.boneAnimations().values()) {
            for (AnimationChannel channel : channels) {
                Keyframe[] frames = channel.keyframes();
                if (frames.length > 0) {
                    last = Math.max(last, frames[frames.length - 1].timestamp());
                }
            }
        }
        return last;
    }

    private static AnimationDefinition reverse(AnimationDefinition definition) {
        float end = lastTimestamp(definition);
        AnimationDefinition.Builder builder = AnimationDefinition.Builder.withLength(definition.lengthInSeconds());
        definition.boneAnimations().forEach((bone, channels) -> {
            for (AnimationChannel channel : channels) {
                Keyframe[] frames = channel.keyframes();
                Keyframe[] flipped = new Keyframe[frames.length];
                for (int i = 0; i < frames.length; ++i) {
                    Keyframe frame = frames[frames.length - 1 - i];
                    flipped[i] = new Keyframe(end - frame.timestamp(), frame.target(), frame.interpolation());
                }
                builder.addAnimation(bone, new AnimationChannel(channel.target(), flipped));
            }
        });
        return builder.build();
    }

    private void animateSleep(T entity, float ageInTicks) {
        AnimationState sleep = entity.getAnimationState("sleep");
        sleep.updateTime(ageInTicks, 1.0F);
        sleep.ifStarted(state -> {
            long elapsed = state.getAccumulatedTime();
            if (elapsed < SLEEP_IN_MILLIS) {
                KeyframeAnimations.animate(this, SLEEP_IN, elapsed, 1.0F, ANIMATION_VECTOR_CACHE);
            } else {
                KeyframeAnimations.animate(this, ResurrectedKnightAnimations.sleep,
                        elapsed - SLEEP_IN_MILLIS, 1.0F, ANIMATION_VECTOR_CACHE);
            }
        });
    }

    private void animateAwake(T entity, float ageInTicks) {
        AnimationState awake = entity.getAnimationState("awaken");
        AnimationState sleep = entity.getAnimationState("sleep");
        awake.updateTime(ageInTicks, 1.0F);
        long sleptMillis = sleep.isStarted() ? 0L : sleep.getAccumulatedTime();
        long skippedMillis = sleptMillis > 0L ? Math.max(0L, SLEEP_IN_MILLIS - sleptMillis) : 0L;
        awake.ifStarted(state -> KeyframeAnimations.animate(this, ResurrectedKnightAnimations.awaken,
                state.getAccumulatedTime() + skippedMillis, 1.0F, ANIMATION_VECTOR_CACHE));
    }

    private final ModelPart root;
    private final ModelPart head;

    public ResurrectedKnightServantModel(ModelPart root) {
        this.root = root.getChild("root");
        this.head = this.root.getChild("rotator").getChild("body").getChild("head");
    }

    public static LayerDefinition createBodyLayer() {
        return ResurrectedKnightModel.createBodyLayer();
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        float yaw = Mth.clamp(netHeadYaw, -30.0F, 30.0F);
        float pitch = Mth.clamp(headPitch, -25.0F, 25.0F);
        this.head.yRot = yaw * ((float) Math.PI / 180F);
        this.head.xRot = -pitch * ((float) Math.PI / 180F);

        this.animate(entity.getAnimationState("idle"), ResurrectedKnightAnimations.idle, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("stab"), ResurrectedKnightAnimations.stabCut, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("left_sidestep"), ResurrectedKnightAnimations.LeftsideStep, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("right_sidestep"), ResurrectedKnightAnimations.RightsideStep, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("forwardstep"), ResurrectedKnightAnimations.forwardStep, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("double_stab"), ResurrectedKnightAnimations.stabDoubleEnd, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("death"), ResurrectedKnightAnimations.death, ageInTicks, 1.0F);
        this.animateSleep(entity, ageInTicks);
        this.animateAwake(entity, ageInTicks);
        this.animate(entity.getAnimationState("throw"), ResurrectedKnightAnimations.Javelinthrow, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("stab_end"), ResurrectedKnightAnimations.stabEnd, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("shield_combo"), ResurrectedKnightAnimations.shieldCombo, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("synergy"), ResurrectedKnightAnimations.synergy3, ageInTicks, 1.0F);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }
}
