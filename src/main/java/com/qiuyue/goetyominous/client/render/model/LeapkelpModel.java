package com.qiuyue.goetyominous.client.render.model;

import com.Polarice3.Goety.client.render.animation.LeapleafAnimations;
import com.Polarice3.Goety.client.render.model.LeapleafModel;
import com.qiuyue.goetyominous.client.render.model.animation.LeapkelpAnimations;
import com.qiuyue.goetyominous.common.entities.ally.mobs.Leapkelp;
import net.minecraft.client.model.geom.ModelPart;

public class LeapkelpModel<T extends Leapkelp> extends LeapleafModel<T> {
    private final ModelPart head;

    public LeapkelpModel(ModelPart root) {
        super(root);
        this.head = root.getChild("body").getChild("main").getChild("chest").getChild("head");
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        boolean inWater = entity.isInWater();
        boolean alert = entity.getCurrentAnimation() == entity.getAnimationState(Leapkelp.ALERT);
        if (!entity.isDeadOrDying() && !entity.isResting()) {
            this.head.yRot = netHeadYaw * ((float) Math.PI / 180.0F);
            this.head.xRot = headPitch * ((float) Math.PI / 180.0F);
        }
        if (inWater) {
            this.applyStatic(LeapkelpAnimations.SWIM_POSE);
            if (alert || entity.canAnimateMove()) {
                this.animateWalk(LeapkelpAnimations.SWIM, ageInTicks, 1.0F, 1.0F, 0.5F);
            }
        } else {
            this.animate(entity.idleAnimationState, LeapleafAnimations.IDLE, ageInTicks);
            if (entity.canAnimateMove() && entity.isMoving()) {
                this.animateWalk(LeapleafAnimations.WALK, limbSwing, limbSwingAmount, 1.5F, 2.5F);
            }
        }
        this.animate(entity.smashAnimationState, LeapleafAnimations.SMASH, ageInTicks);
        this.animate(entity.chargeAnimationState, LeapleafAnimations.CHARGE, ageInTicks);
        this.animate(entity.leapAnimationState, LeapleafAnimations.LEAP, ageInTicks);
        this.animate(entity.restAnimationState, LeapleafAnimations.REST, ageInTicks);
        if (!inWater) {
            this.animate(entity.alertAnimationState, LeapleafAnimations.ALERT, ageInTicks);
        }
    }
}
