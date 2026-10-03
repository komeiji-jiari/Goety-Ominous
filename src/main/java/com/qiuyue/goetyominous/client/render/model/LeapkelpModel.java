package com.qiuyue.goetyominous.client.render.model;

import com.Polarice3.Goety.client.render.animation.LeapleafAnimations;
import com.Polarice3.Goety.client.render.model.LeapleafModel;
import com.qiuyue.goetyominous.client.render.model.animation.LeapkelpAnimations;
import com.qiuyue.goetyominous.common.entities.ally.mobs.Leapkelp;
import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

public class LeapkelpModel<T extends Leapkelp> extends LeapleafModel<T> {
    private static final float SWIM_POSE_ROOT_Z = -10.0F;
    private static final float SWIM_POSE_BODY_X = 17.5F;
    private static final float SWIM_POSE_ARM_X = 35.0F;
    private static final float SWIM_POSE_ARM_Z = 50.0F;
    private static final float SWIM_POSE_RIGHT_LEG_X = 90.0F;
    private static final float SWIM_POSE_LEFT_LEG_X = 117.5F;
    private static final float SWIM_STROKE_SPEED = 0.25F;
    private static final float SWIM_STROKE_DEGREE = 0.5F;
    private static final float SWIM_LEG_SPEED = 0.375F;
    private static final float SWIM_STROKE_GAIN = 0.7F;

    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;
    private final Map<ModelPart, float[]> landPose = new IdentityHashMap<>();

    public LeapkelpModel(ModelPart root) {
        super(root);
        this.root = root;
        this.body = root.getChild("body");
        this.head = this.body.getChild("main").getChild("chest").getChild("head");
        this.rightArm = this.body.getChild("right_arm");
        this.leftArm = this.body.getChild("left_arm");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        float swimAmount = entity.getSwimPoseAmount(ageInTicks - entity.tickCount);
        boolean alert = entity.getCurrentAnimation() == entity.getAnimationState(Leapkelp.ALERT);
        boolean blend = swimAmount > 0.0F && swimAmount < 1.0F;
        if (!entity.isDeadOrDying() && !entity.isResting()) {
            this.head.yRot = netHeadYaw * ((float) Math.PI / 180.0F);
            this.head.xRot = headPitch * ((float) Math.PI / 180.0F);
        }
        if (swimAmount < 1.0F) {
            this.animate(entity.idleAnimationState, LeapleafAnimations.IDLE, ageInTicks);
            if (entity.canAnimateMove() && entity.isMoving()) {
                this.animateWalk(LeapleafAnimations.WALK, limbSwing, limbSwingAmount, 1.5F, 2.5F);
            }
            if (blend) {
                this.captureLandPose();
                this.root().getAllParts().forEach(ModelPart::resetPose);
            }
        }
        if (swimAmount > 0.0F) {
            this.applySwimPose(swimAmount);
            this.applySwimMotion(limbSwing, limbSwingAmount * swimAmount * SWIM_STROKE_GAIN);
            if (blend) {
                this.blendLandPose(swimAmount);
            }
        }
        this.animate(entity.smashAnimationState, LeapleafAnimations.SMASH, ageInTicks);
        this.animate(entity.chargeAnimationState, LeapleafAnimations.CHARGE, ageInTicks);
        this.animate(entity.leapAnimationState, LeapleafAnimations.LEAP, ageInTicks);
        this.animate(entity.restAnimationState, LeapleafAnimations.REST, ageInTicks);
        this.animate(entity.rightSmashAnimationState, LeapkelpAnimations.RIGHT_SMASH, ageInTicks);
        this.animate(entity.leftSmashAnimationState, LeapkelpAnimations.LEFT_SMASH, ageInTicks);
        if (swimAmount < 1.0F) {
            this.animate(entity.alertAnimationState, LeapleafAnimations.ALERT, ageInTicks);
        }
    }

    private void applySwimPose(float swim) {
        this.root.zRot += swim * toRadians(SWIM_POSE_ROOT_Z);
        this.body.xRot += swim * toRadians(SWIM_POSE_BODY_X);
        this.rightArm.xRot += swim * toRadians(SWIM_POSE_ARM_X);
        this.rightArm.zRot += swim * toRadians(SWIM_POSE_ARM_Z);
        this.leftArm.xRot += swim * toRadians(SWIM_POSE_ARM_X);
        this.leftArm.zRot -= swim * toRadians(SWIM_POSE_ARM_Z);
        this.rightLeg.xRot += swim * toRadians(SWIM_POSE_RIGHT_LEG_X);
        this.leftLeg.xRot += swim * toRadians(SWIM_POSE_LEFT_LEG_X);
    }

    private void applySwimMotion(float limbSwing, float amount) {
        this.root.zRot += stroke(SWIM_STROKE_SPEED, SWIM_STROKE_DEGREE, true, 0.0F, limbSwing, amount);
        this.leftArm.xRot += stroke(SWIM_STROKE_SPEED, SWIM_STROKE_DEGREE * 2.75F, true, -0.5F, limbSwing, amount);
        this.rightArm.xRot += stroke(SWIM_STROKE_SPEED, SWIM_STROKE_DEGREE * 2.75F, true, -0.5F, limbSwing, amount);
        this.rightLeg.xRot += stroke(SWIM_LEG_SPEED, SWIM_STROKE_DEGREE, true, 2.0F, limbSwing, amount);
        this.leftLeg.xRot += stroke(SWIM_LEG_SPEED, SWIM_STROKE_DEGREE, false, 2.0F, limbSwing, amount);
    }

    private static float stroke(float speed, float degree, boolean invert, float offset, float swing, float amount) {
        float rotation = Mth.cos(swing * speed + offset) * degree * amount;
        return invert ? -rotation : rotation;
    }

    private static float toRadians(float degrees) {
        return degrees * ((float) Math.PI / 180.0F);
    }

    private void captureLandPose() {
        this.root().getAllParts().forEach(part -> {
            float[] values = this.landPose.computeIfAbsent(part, key -> new float[6]);
            values[0] = part.xRot;
            values[1] = part.yRot;
            values[2] = part.zRot;
            values[3] = part.x;
            values[4] = part.y;
            values[5] = part.z;
        });
    }

    private void blendLandPose(float amount) {
        for (Map.Entry<ModelPart, float[]> entry : this.landPose.entrySet()) {
            ModelPart part = entry.getKey();
            float[] values = entry.getValue();
            part.xRot = Mth.lerp(amount, values[0], part.xRot);
            part.yRot = Mth.lerp(amount, values[1], part.yRot);
            part.zRot = Mth.lerp(amount, values[2], part.zRot);
            part.x = Mth.lerp(amount, values[3], part.x);
            part.y = Mth.lerp(amount, values[4], part.y);
            part.z = Mth.lerp(amount, values[5], part.z);
        }
    }
}
