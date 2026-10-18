package com.qiuyue.goetyominous.client.render.model.lm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.common.entities.ally.lm.FrostbittenGolemServant;
import net.miauczel.legendary_monsters.entity.animations.FrostbittenGolemAnimationTest;
import net.miauczel.legendary_monsters.entity.animations.Frostbitten_GolemAnimations;
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
public class FrostbittenGolemServantModel<T extends FrostbittenGolemServant> extends HierarchicalModel<T> {
    private static final float WALK_UPPER_BODY_SWAY = 0.3F;
    private static final AnimationDefinition SLEEP_IN = reverse(FrostbittenGolemAnimationTest.awake);
    private static final long SLEEP_IN_MILLIS = (long) (lastTimestamp(SLEEP_IN) * 1000.0F);
    private static final Vector3f ANIMATION_VECTOR_CACHE = new Vector3f();

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart leftArm;
    private final ModelPart rightArm;

    public FrostbittenGolemServantModel(ModelPart root) {
        this.root = root.getChild("root");
        this.head = root.getChild("root").getChild("bone").getChild("body").getChild("head");
        this.body = root.getChild("root").getChild("bone").getChild("body");
        this.leftArm = this.body.getChild("leftarm2");
        this.rightArm = this.body.getChild("rightarm");
    }

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
                KeyframeAnimations.animate(this, FrostbittenGolemAnimationTest.sleep,
                        elapsed - SLEEP_IN_MILLIS, 1.0F, ANIMATION_VECTOR_CACHE);
            }
        });
    }

    private void animateAwake(T entity, float ageInTicks) {
        AnimationState awake = entity.getAnimationState("awake");
        AnimationState sleep = entity.getAnimationState("sleep");
        awake.updateTime(ageInTicks, 1.0F);
        long sleptMillis = sleep.isStarted() ? 0L : sleep.getAccumulatedTime();
        long skippedMillis = sleptMillis > 0L ? Math.max(0L, SLEEP_IN_MILLIS - sleptMillis) : 0L;
        awake.ifStarted(state -> KeyframeAnimations.animate(this, FrostbittenGolemAnimationTest.awake,
                state.getAccumulatedTime() + skippedMillis, 1.0F, ANIMATION_VECTOR_CACHE));
    }

    public static LayerDefinition createBodyLayer() {
        return net.miauczel.legendary_monsters.entity.client.Model.Frostbitten_GolemModel.createBodyLayer();
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        if (!entity.isSleep()) {
            this.applyHeadRotation(netHeadYaw, headPitch);
        }
        this.animateWalk(Frostbitten_GolemAnimations.WALK5, limbSwing, limbSwingAmount, 1.5F, 2.5F);
        this.body.yRot *= WALK_UPPER_BODY_SWAY;
        this.leftArm.yRot *= WALK_UPPER_BODY_SWAY;
        this.leftArm.zRot *= WALK_UPPER_BODY_SWAY;
        this.rightArm.yRot *= WALK_UPPER_BODY_SWAY;
        this.rightArm.zRot *= WALK_UPPER_BODY_SWAY;
        this.animate(entity.getAnimationState("death"), FrostbittenGolemAnimationTest.death, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("idle"), FrostbittenGolemAnimationTest.idle, ageInTicks, 1.0F);
        this.animateSleep(entity, ageInTicks);
        this.animateAwake(entity, ageInTicks);
        this.animate(entity.getAnimationState("attackarmright"), FrostbittenGolemAnimationTest.rightArmSlam, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("attackarmleft"), FrostbittenGolemAnimationTest.leftArmSlam, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("attackarms"), FrostbittenGolemAnimationTest.slamAttack, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("attackaggresive"), FrostbittenGolemAnimationTest.IceSpikeBigSummon, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("attackicespike"), FrostbittenGolemAnimationTest.IceSpikeSummon, ageInTicks, 1.0F);
    }

    private void applyHeadRotation(float netHeadYaw, float headPitch) {
        netHeadYaw = Mth.clamp(netHeadYaw, -30.0F, 30.0F);
        headPitch = Mth.clamp(headPitch, -25.0F, 25.0F);
        this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
        this.head.xRot = headPitch * ((float) Math.PI / 180F);
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
