package com.qiuyue.goetyominous.client.render.model.lm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.common.entities.ally.lm.BeheadedKnightServant;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Animations.PossessedArmor.BeheadedKnight.BeheadedKnightAnimations;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Animations.PossessedArmor.BeheadedKnight.BeheadedKnightAnimations2;
import net.miauczel.legendary_monsters.entity.client.Model.BeheadedKnightModel;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.world.entity.AnimationState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class BeheadedKnightServantModel<T extends BeheadedKnightServant> extends HierarchicalModel<T> {

    private static final AnimationDefinition SLEEP_IN = reverse(BeheadedKnightAnimations.awaken);
    private static final long SLEEP_IN_MILLIS = (long) (lastTimestamp(SLEEP_IN) * 1000.0F);
    private static final Vector3f ANIMATION_VECTOR_CACHE = new Vector3f();

    public static final float NECK_OFFSET_X = 0.0F;
    public static final float NECK_OFFSET_Y = 24.0F;
    public static final float NECK_OFFSET_Z = 0.0F;

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
                KeyframeAnimations.animate(this, BeheadedKnightAnimations.sleep,
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
        awake.ifStarted(state -> KeyframeAnimations.animate(this, BeheadedKnightAnimations.awaken,
                state.getAccumulatedTime() + skippedMillis, 1.0F, ANIMATION_VECTOR_CACHE));
    }

    private final ModelPart root;
    private final ModelPart rotator;
    private final ModelPart core_body;
    private final ModelPart upper_body;
    private final ModelPart left_arm;
    private final ModelPart forearm7;
    private final ModelPart fist7;

    public BeheadedKnightServantModel(ModelPart root) {
        this.root = root.getChild("root");
        this.rotator = this.root.getChild("rotator");
        this.core_body = this.rotator.getChild("core_body");
        this.upper_body = this.core_body.getChild("upper_body");
        this.left_arm = this.upper_body.getChild("left_arm");
        this.forearm7 = this.left_arm.getChild("forearm7");
        this.fist7 = this.forearm7.getChild("fist7");
    }

    public static LayerDefinition createBodyLayer() {
        return BeheadedKnightModel.createBodyLayer();
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        if (entity.getAttackState() == 0 || entity.getAttackState() == 9) {
            this.animateWalk(BeheadedKnightAnimations.walk2, limbSwing, limbSwingAmount, 1.0F, 4.0F);
        }

        this.animate(entity.getAnimationState("idle"), BeheadedKnightAnimations.idle, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("uppercut"), BeheadedKnightAnimations.upperCut3, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("ghost_combo"), BeheadedKnightAnimations.headSlash_combo, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("ghost_uppercut"), BeheadedKnightAnimations.headUppercutTriple2, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("stab_double"), BeheadedKnightAnimations.stabDouble, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("stab"), BeheadedKnightAnimations.stab3, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("death"), BeheadedKnightAnimations.death, ageInTicks, 1.0F);
        this.animateSleep(entity, ageInTicks);
        this.animateAwake(entity, ageInTicks);
        this.animate(entity.getAnimationState("synergy"), BeheadedKnightAnimations2.synergy5, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("grab_and_throw"), BeheadedKnightAnimations2.grabAndThrowCut, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("grab_and_throw_success"), BeheadedKnightAnimations2.grabAndThrowSuccess2, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("grab_and_throw_fail"), BeheadedKnightAnimations2.grabAndThrowFail, ageInTicks, 1.0F);
    }

    public void translateModel(PoseStack poseStack) {
        this.root.translateAndRotate(poseStack);
        this.rotator.translateAndRotate(poseStack);
        this.core_body.translateAndRotate(poseStack);
        this.upper_body.translateAndRotate(poseStack);
        this.left_arm.translateAndRotate(poseStack);
        this.forearm7.translateAndRotate(poseStack);
        this.fist7.translateAndRotate(poseStack);
    }

    public void translateToNeck(PoseStack poseStack) {
        this.root.translateAndRotate(poseStack);
        this.rotator.translateAndRotate(poseStack);
        this.core_body.translateAndRotate(poseStack);
        this.upper_body.translateAndRotate(poseStack);
        poseStack.translate(NECK_OFFSET_X, NECK_OFFSET_Y, NECK_OFFSET_Z);
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
