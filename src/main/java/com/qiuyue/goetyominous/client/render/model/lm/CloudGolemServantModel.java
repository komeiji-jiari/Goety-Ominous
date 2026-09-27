package com.qiuyue.goetyominous.client.render.model.lm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.common.entities.ally.lm.CloudGolemServant;
import net.miauczel.legendary_monsters.entity.animations.CloudGolemAnimations;
import net.miauczel.legendary_monsters.entity.animations.replacer.CGAnims;
import net.miauczel.legendary_monsters.entity.client.Model.Cloud_GolemModel;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AnimationState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class CloudGolemServantModel<T extends CloudGolemServant> extends HierarchicalModel<T> {

    private static final float WALK_UPPER_BODY_SWAY = 0.3F;
    private static final AnimationDefinition SLEEP_IN = reverse(CloudGolemAnimations.awake);
    private static final long SLEEP_IN_MILLIS = (long) (lastTimestamp(SLEEP_IN) * 1000.0F);
    private static final Vector3f ANIMATION_VECTOR_CACHE = new Vector3f();

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart u1;
    private final ModelPart u2;
    private final ModelPart body;
    private final ModelPart runeLower;
    private final ModelPart runeUpper;
    private final ModelPart rightArm;
    private final ModelPart leftArm;

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
                KeyframeAnimations.animate(this, CloudGolemAnimations.sleep,
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
        awake.ifStarted(state -> KeyframeAnimations.animate(this, CloudGolemAnimations.awake,
                state.getAccumulatedTime() + skippedMillis, 1.0F, ANIMATION_VECTOR_CACHE));
    }

    public CloudGolemServantModel(ModelPart root) {
        this.root = root.getChild("root");
        this.u1 = root.getChild("root").getChild("body").getChild("left_arm").getChild("upper");
        this.u2 = root.getChild("root").getChild("body").getChild("rightarm").getChild("upper2");
        this.body = root.getChild("root").getChild("body");
        this.head = this.body.getChild("head");
        this.runeLower = this.body.getChild("rune_lower");
        this.runeUpper = this.body.getChild("rune_upper");
        this.rightArm = this.body.getChild("rightarm");
        this.leftArm = this.body.getChild("left_arm");
    }

    public static LayerDefinition createBodyLayer() {
        return Cloud_GolemModel.createBodyLayer();
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.applyHeadRotation(netHeadYaw, headPitch);
        if (entity.getAttackState() != 28) {
            this.animateWalk(CloudGolemAnimations.walk4, limbSwing, limbSwingAmount, 1.5F, 2.5F);
            this.body.yRot *= WALK_UPPER_BODY_SWAY;
            this.runeLower.xRot *= WALK_UPPER_BODY_SWAY;
            this.runeLower.zRot *= WALK_UPPER_BODY_SWAY;
            this.runeUpper.xRot *= WALK_UPPER_BODY_SWAY;
            this.runeUpper.zRot *= WALK_UPPER_BODY_SWAY;
            this.rightArm.yRot *= WALK_UPPER_BODY_SWAY;
            this.rightArm.zRot *= WALK_UPPER_BODY_SWAY;
            this.leftArm.yRot *= WALK_UPPER_BODY_SWAY;
            this.leftArm.zRot *= WALK_UPPER_BODY_SWAY;
        }
        this.animate(entity.getAnimationState("death"), CloudGolemAnimations.death, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("idle"), CloudGolemAnimations.idle, ageInTicks, 1.0F);
        this.animateSleep(entity, ageInTicks);
        this.animateAwake(entity, ageInTicks);
        this.animate(entity.getAnimationState("p2"), CloudGolemAnimations.stompp2, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("cloudattackbig"), CloudGolemAnimations.BigSummon, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("blockhitdb"), CloudGolemAnimations.blockhitDB2, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("blockhit"), CloudGolemAnimations.blockhit, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("explode"), CGAnims.explode2, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("fractureland"), CGAnims.LandFracture2, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("aendcharge"), CGAnims.chargeEndAggresive, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("charge"), CGAnims.charge, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("precharge"), CGAnims.chargePre, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("block"), CloudGolemAnimations.block, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("stompleft"), CGAnims.stompsLeft, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("stomp"), CGAnims.stomps3, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("fly"), CloudGolemAnimations.fly, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("land"), CloudGolemAnimations.heroFallEnd, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("prefracturefall"), CloudGolemAnimations.heroFall, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("fall"), CloudGolemAnimations.heroFall, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("laser"), CloudGolemAnimations.laser4, ageInTicks, 1.0F);
        this.animate(entity.getAnimationState("mhit"), CGAnims.lightningSummon5, ageInTicks, 1.0F);
    }

    private void applyHeadRotation(float pNetHeadYaw, float pHeadPitch) {
        pNetHeadYaw = Mth.clamp(pNetHeadYaw, -300.0F, 300.0F);
        pHeadPitch = Mth.clamp(pHeadPitch, -180.0F, 220.0F);
        this.head.yRot = pNetHeadYaw * 0.00826735F;
        this.head.xRot = pHeadPitch * ((float) Math.PI / 360);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart root() {
        return root;
    }
}
