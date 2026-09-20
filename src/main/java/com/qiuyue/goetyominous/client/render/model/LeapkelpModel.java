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
    private final ModelPart head;
    private final Map<ModelPart, float[]> landPose = new IdentityHashMap<>();

    public LeapkelpModel(ModelPart root) {
        super(root);
        this.head = root.getChild("body").getChild("main").getChild("chest").getChild("head");
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
            this.applyStatic(LeapkelpAnimations.SWIM_POSE);
            if (alert || entity.canAnimateMove()) {
                this.animateWalk(LeapkelpAnimations.SWIM, ageInTicks, 1.0F, 1.0F, 0.5F);
            }
            if (blend) {
                this.blendLandPose(swimAmount);
            }
        }
        this.animate(entity.smashAnimationState, LeapleafAnimations.SMASH, ageInTicks);
        this.animate(entity.chargeAnimationState, LeapleafAnimations.CHARGE, ageInTicks);
        this.animate(entity.leapAnimationState, LeapleafAnimations.LEAP, ageInTicks, entity.getLeapAnimSpeed());
        this.animate(entity.restAnimationState, LeapleafAnimations.REST, ageInTicks);
        if (swimAmount < 1.0F) {
            this.animate(entity.alertAnimationState, LeapleafAnimations.ALERT, ageInTicks);
        }
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
