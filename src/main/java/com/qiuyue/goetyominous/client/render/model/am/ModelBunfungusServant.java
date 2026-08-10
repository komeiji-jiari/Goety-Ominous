package com.qiuyue.goetyominous.client.render.model.am;

import com.github.alexthe666.alexsmobs.entity.util.Maths;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.github.alexthe666.citadel.client.model.AdvancedModelBox;
import com.github.alexthe666.citadel.client.model.ModelAnimator;
import com.github.alexthe666.citadel.client.model.basic.BasicModelPart;
import com.google.common.collect.ImmutableList;
import com.qiuyue.goetyominous.common.entities.ally.am.BunfungusServant;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelBunfungusServant extends AdvancedEntityModel<BunfungusServant> {
    public final AdvancedModelBox root;
    public final AdvancedModelBox body;
    public final AdvancedModelBox belly;
    public final AdvancedModelBox tail;
    public final AdvancedModelBox head;
    public final AdvancedModelBox left_brow;
    public final AdvancedModelBox right_brow;
    public final AdvancedModelBox shroom_cap;
    public final AdvancedModelBox left_ear;
    public final AdvancedModelBox right_ear;
    public final AdvancedModelBox snout;
    public final AdvancedModelBox snout_r1;
    public final AdvancedModelBox left_arm;
    public final AdvancedModelBox right_arm;
    public final AdvancedModelBox left_leg;
    public final AdvancedModelBox left_foot;
    public final AdvancedModelBox right_leg;
    public final AdvancedModelBox right_foot;
    private final ModelAnimator animator;

    public ModelBunfungusServant() {
        texWidth = 256;
        texHeight = 256;

        root = new AdvancedModelBox(this, "root");
        root.setRotationPoint(0.0F, 24.0F, 0.0F);

        body = new AdvancedModelBox(this, "body");
        body.setRotationPoint(0.0F, -13.0F, 0.0F);
        root.addChild(body);
        body.setTextureOffset(0, 0).addBox(-10.0F, -10.0F, -10.0F, 20.0F, 20.0F, 19.0F, 0.0F, false);

        belly = new AdvancedModelBox(this, "belly");
        belly.setRotationPoint(0.0F, 4.0F, -4.3F);
        body.addChild(belly);
        belly.setTextureOffset(64, 25).addBox(-11.0F, -7.0F, -7.5F, 22.0F, 14.0F, 15.0F, -2.0F, false);

        tail = new AdvancedModelBox(this, "tail");
        tail.setRotationPoint(0.0F, 10.0F, 9.0F);
        body.addChild(tail);
        tail.setTextureOffset(60, 0).addBox(-3.0F, -5.0F, -1.0F, 6.0F, 6.0F, 6.0F, 0.0F, false);

        head = new AdvancedModelBox(this, "head");
        head.setRotationPoint(0.0F, -10.0F, -6.0F);
        body.addChild(head);
        head.setTextureOffset(0, 66).addBox(-6.0F, -5.0F, -9.0F, 12.0F, 8.0F, 13.0F, 0.0F, false);

        left_brow = new AdvancedModelBox(this, "left_brow");
        left_brow.setRotationPoint(3.5F, -3.5F, -9.1F);
        head.addChild(left_brow);
        left_brow.setTextureOffset(90, 2).addBox(-2.5F, -0.5F, 0.0F, 5.0F, 1.0F, 0.0F, 0.0F, false);

        right_brow = new AdvancedModelBox(this, "right_brow");
        right_brow.setRotationPoint(-3.5F, -3.5F, -9.1F);
        head.addChild(right_brow);
        right_brow.setTextureOffset(90, 2).addBox(-2.5F, -0.5F, 0.0F, 5.0F, 1.0F, 0.0F, 0.0F, true);

        shroom_cap = new AdvancedModelBox(this, "shroom_cap");
        shroom_cap.setRotationPoint(0.0F, -5.0F, -4.0F);
        head.addChild(shroom_cap);
        shroom_cap.setTextureOffset(0, 40).addBox(-10.0F, -5.0F, -8.0F, 20.0F, 5.0F, 20.0F, 0.0F, false);

        left_ear = new AdvancedModelBox(this, "left_ear");
        left_ear.setRotationPoint(3.0F, -4.0F, 1.0F);
        shroom_cap.addChild(left_ear);
        setRotationAngle(left_ear, 0.0F, -0.6981F, 0.2182F);
        left_ear.setTextureOffset(0, 0).addBox(-2.0F, -12.0F, -1.0F, 4.0F, 12.0F, 2.0F, 0.0F, false);

        right_ear = new AdvancedModelBox(this, "right_ear");
        right_ear.setRotationPoint(-3.0F, -4.0F, 1.0F);
        shroom_cap.addChild(right_ear);
        setRotationAngle(right_ear, 0.0F, 0.6981F, -0.2182F);
        right_ear.setTextureOffset(0, 0).addBox(-2.0F, -12.0F, -1.0F, 4.0F, 12.0F, 2.0F, 0.0F, true);

        snout = new AdvancedModelBox(this, "snout");
        snout.setRotationPoint(0.0F, 0.0F, -10.0F);
        head.addChild(snout);
        snout.setTextureOffset(0, 40).addBox(-3.0F, -1.0F, -1.0F, 6.0F, 4.0F, 2.0F, 0.0F, false);

        snout_r1 = new AdvancedModelBox(this, "snout_r1");
        snout_r1.setRotationPoint(0.0F, 0.0F, -1.0F);
        snout.addChild(snout_r1);
        setRotationAngle(snout_r1, -0.1309F, 0.0F, 0.0F);
        snout_r1.setTextureOffset(0, 48).addBox(-4.0F, 0.0F, 0.0F, 8.0F, 2.0F, 0.0F, 0.0F, false);

        left_arm = new AdvancedModelBox(this, "left_arm");
        left_arm.setRotationPoint(9.5F, -4.0F, -9.5F);
        body.addChild(left_arm);
        setRotationAngle(left_arm, 0.0F, 0.0F, 0.1745F);
        left_arm.setTextureOffset(51, 77).addBox(-2.5F, -1.0F, -2.5F, 5.0F, 8.0F, 5.0F, 0.0F, false);

        right_arm = new AdvancedModelBox(this, "right_arm");
        right_arm.setRotationPoint(-9.5F, -4.0F, -9.5F);
        body.addChild(right_arm);
        setRotationAngle(right_arm, 0.0F, 0.0F, -0.1745F);
        right_arm.setTextureOffset(51, 77).addBox(-2.5F, -1.0F, -2.5F, 5.0F, 8.0F, 5.0F, 0.0F, true);

        left_leg = new AdvancedModelBox(this, "left_leg");
        left_leg.setRotationPoint(6.0F, 2.0F, 0.0F);
        body.addChild(left_leg);

        left_foot = new AdvancedModelBox(this, "left_foot");
        left_foot.setRotationPoint(0.0F, 8.0F, 0.0F);
        left_leg.addChild(left_foot);
        setRotationAngle(left_foot, 0.0F, -1.1345F, 0.0F);
        left_foot.setTextureOffset(64, 55).addBox(-3.0F, -1.0F, -15.0F, 6.0F, 4.0F, 17.0F, 0.0F, false);

        right_leg = new AdvancedModelBox(this, "right_leg");
        right_leg.setRotationPoint(-6.0F, 2.0F, 0.0F);
        body.addChild(right_leg);

        right_foot = new AdvancedModelBox(this, "right_foot");
        right_foot.setRotationPoint(0.0F, 8.0F, 0.0F);
        right_leg.addChild(right_foot);
        setRotationAngle(right_foot, 0.0F, 1.1345F, 0.0F);
        right_foot.setTextureOffset(64, 55).addBox(-3.0F, -1.0F, -15.0F, 6.0F, 4.0F, 17.0F, 0.0F, true);
        this.updateDefaultPose();
        animator = ModelAnimator.create();
    }

    public void animate(IAnimatedEntity entity, float f, float f1, float f2, float f3, float f4) {
        animator.update(entity);
        animator.setAnimation(BunfungusServant.ANIMATION_EAT);
        animator.startKeyframe(4);
        animator.rotate(head, Maths.rad(30), 0, 0);
        animator.rotate(right_arm, Maths.rad(-140), Maths.rad(-20), Maths.rad(70));
        animator.rotate(left_arm, Maths.rad(-140), Maths.rad(20), Maths.rad(-70));
        animator.move(head, 0, -2, -1);
        animator.move(right_arm, 1, 2, 0);
        animator.move(left_arm, -1, 2, 0);
        animator.endKeyframe();
        animator.startKeyframe(4);
        animator.rotate(head, Maths.rad(20), 0, 0);
        animator.rotate(right_arm, Maths.rad(-140), Maths.rad(-10), Maths.rad(70));
        animator.rotate(left_arm, Maths.rad(-140), Maths.rad(10), Maths.rad(-70));
        animator.move(head, 0, -2, -1);
        animator.move(right_arm, 1, 2, 0);
        animator.move(left_arm, -1, 2, 0);
        animator.endKeyframe();
        animator.startKeyframe(4);
        animator.rotate(head, Maths.rad(30), 0, 0);
        animator.rotate(right_arm, Maths.rad(-140), Maths.rad(-20), Maths.rad(70));
        animator.rotate(left_arm, Maths.rad(-140), Maths.rad(20), Maths.rad(-70));
        animator.move(head, 0, -2, -1);
        animator.move(right_arm, 1, 2, 0);
        animator.move(left_arm, -1, 2, 0);
        animator.endKeyframe();
        animator.startKeyframe(4);
        animator.rotate(head, Maths.rad(20), 0, 0);
        animator.rotate(right_arm, Maths.rad(-140), Maths.rad(-10), Maths.rad(70));
        animator.rotate(left_arm, Maths.rad(-140), Maths.rad(10), Maths.rad(-70));
        animator.move(head, 0, -2, -1);
        animator.move(right_arm, 1, 2, 0);
        animator.move(left_arm, -1, 2, 0);
        animator.endKeyframe();
        animator.resetKeyframe(4);
        animator.endKeyframe();
        animator.setAnimation(BunfungusServant.ANIMATION_BELLY);
        animator.startKeyframe(5);
        animator.rotate(head, Maths.rad(20), 0, 0);
        animator.rotate(body, Maths.rad(-20), 0, 0);
        animator.rotate(right_arm, Maths.rad(-120), Maths.rad(30), Maths.rad(-10));
        animator.rotate(left_arm, Maths.rad(-120), Maths.rad(-30), Maths.rad(10));
        animator.move(belly, 0, 0, -10);
        animator.endKeyframe();
        animator.setStaticKeyframe(2);
        animator.resetKeyframe(5);
        animator.setAnimation(BunfungusServant.ANIMATION_SLAM);
        animator.startKeyframe(5);
        animator.move(root, 0, 0, -20);
        animator.endKeyframe();
        animator.setStaticKeyframe(5);
        animator.resetKeyframe(5);
    }

    @Override
    public void setupAnim(BunfungusServant entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetToDefaultPose();
        animate(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        float walkSpeed = 0.1F;
        float walkDegree = 0.1F;
        float speed = 0.7F;
        float degree = 2.0F;
        float partialTick = ageInTicks - entityIn.tickCount;
        float sleepProgress = entityIn.prevSleepProgress + (entityIn.sleepProgress - entityIn.prevSleepProgress) * partialTick;
        float reboundProgress = entityIn.prevReboundProgress + (entityIn.reboundProgress - entityIn.prevReboundProgress) * partialTick;
        float jumpProgress = Math.max(0.0F, entityIn.prevJumpProgress + (entityIn.jumpProgress - entityIn.prevJumpProgress) * partialTick - reboundProgress);
        float interestedProgress = entityIn.prevInterestedProgress + (entityIn.interestedProgress - entityIn.prevInterestedProgress) * partialTick;
        float constant = 1.0F - Math.max(jumpProgress, reboundProgress) * 0.2F;
        float bounce = Math.min(limbSwingAmount, 0.38F) * constant;
        progressRotationPrev(body, sleepProgress, Maths.rad(90), 0, 0, 5F);
        progressRotationPrev(tail, sleepProgress, Maths.rad(-70), 0, 0, 5F);
        progressRotationPrev(right_ear, sleepProgress, Maths.rad(50), Maths.rad(80), 0, 5F);
        progressRotationPrev(left_ear, sleepProgress, Maths.rad(50), Maths.rad(-80), 0, 5F);
        progressRotationPrev(head, sleepProgress, Maths.rad(-95), 0, Maths.rad(-5), 5F);
        progressRotationPrev(left_arm, sleepProgress, Maths.rad(-170), Maths.rad(-10), Maths.rad(35), 5F);
        progressRotationPrev(right_arm, sleepProgress, Maths.rad(-170), Maths.rad(10), Maths.rad(-35), 5F);
        progressRotationPrev(left_foot, sleepProgress, Maths.rad(70), Maths.rad(-30), 0, 5F);
        progressRotationPrev(right_foot, sleepProgress, Maths.rad(70), Maths.rad(30), 0, 5F);
        progressPositionPrev(body, sleepProgress, 0, 3, 0, 5F);
        progressPositionPrev(left_leg, sleepProgress, 0, 0, -8, 5F);
        progressPositionPrev(right_leg, sleepProgress, 0, 0, -8, 5F);
        progressPositionPrev(left_arm, sleepProgress, 0, -3, 2, 5F);
        progressPositionPrev(right_arm, sleepProgress, 0, -3, 2, 5F);
        progressPositionPrev(head, sleepProgress, 0, -3, -1, 5F);
        progressRotationPrev(left_foot, bounce, 0, Maths.rad(40), 0, 0.38F);
        progressRotationPrev(right_foot, bounce, 0, Maths.rad(-40), 0, 0.38F);
        progressRotationPrev(left_ear, bounce, Maths.rad(-30), Maths.rad(30), 0, 0.38F);
        progressRotationPrev(right_ear, bounce, Maths.rad(-30), Maths.rad(-30), 0, 0.38F);
        progressRotationPrev(body, jumpProgress, Maths.rad(20), 0, 0, 5F);
        progressRotationPrev(left_foot, jumpProgress, Maths.rad(70), Maths.rad(40), 0, 5F);
        progressRotationPrev(right_foot, jumpProgress, Maths.rad(70), Maths.rad(-40), 0, 5F);
        progressRotationPrev(right_arm, jumpProgress, Maths.rad(-70), Maths.rad(40), 0, 5F);
        progressRotationPrev(left_arm, jumpProgress, Maths.rad(-70), Maths.rad(-40), 0, 5F);
        progressPositionPrev(body, jumpProgress, 0, -3, 0, 5F);
        progressPositionPrev(head, jumpProgress, 0, -1, 3, 5F);
        progressRotationPrev(body, reboundProgress, Maths.rad(20), 0, 0, 5F);
        progressRotationPrev(left_foot, reboundProgress, Maths.rad(-20), 0, 0, 5F);
        progressRotationPrev(right_foot, reboundProgress, Maths.rad(-20), 0, 0, 5F);
        progressRotationPrev(tail, reboundProgress, Maths.rad(20), 0, 0, 5F);
        progressRotationPrev(head, reboundProgress, Maths.rad(-20), 0, 0, 5F);
        progressRotationPrev(right_arm, reboundProgress, Maths.rad(-130), Maths.rad(20), 0, 5F);
        progressRotationPrev(left_arm, reboundProgress, Maths.rad(-130), Maths.rad(-20), 0, 5F);
        progressPositionPrev(body, reboundProgress, 0, -1, 0, 5F);
        progressPositionPrev(left_foot, reboundProgress, 0, 1, -1, 5F);
        progressPositionPrev(right_foot, reboundProgress, 0, 1, -1, 5F);
        progressRotationPrev(head, interestedProgress, 0, Maths.rad(-20), Maths.rad(-10), 5F);
        progressRotationPrev(right_brow, interestedProgress, 0, 0, Maths.rad(10), 5F);
        progressPositionPrev(right_brow, interestedProgress, -0.5F, -0.75F, 0, 5F);
        progressPositionPrev(left_brow, interestedProgress, 0, 0.5F, 0, 5F);
        if (sleepProgress == 0.0F) {
            this.faceTarget(netHeadYaw, headPitch, 1.3F, head);
        }
        this.flap(left_ear, walkSpeed, walkDegree, false, 1.0F, 0.2F, ageInTicks, 1.0F);
        this.flap(right_ear, walkSpeed, walkDegree, true, 1.0F, 0.2F, ageInTicks, 1.0F);
        this.swing(left_ear, walkSpeed, walkDegree, false, 2.0F, 0.2F, ageInTicks, 1.0F);
        this.swing(right_ear, walkSpeed, walkDegree, true, 2.0F, 0.2F, ageInTicks, 1.0F);
        this.walk(tail, walkSpeed, walkDegree, false, 2.0F, 0.2F, ageInTicks, 1.0F);
        this.walk(right_arm, walkSpeed, walkDegree, false, -2.0F, -0.1F, ageInTicks, 1.0F);
        this.walk(left_arm, walkSpeed, walkDegree, false, -2.0F, -0.1F, ageInTicks, 1.0F);
        this.flap(snout_r1, walkSpeed * 8.0F, walkDegree, false, -2.0F, 0.0F, ageInTicks, 1.0F);
        this.flap(body, speed, degree * 0.5F, false, 0.0F, 0.0F, limbSwing, bounce);
        this.swing(body, speed, degree * 0.5F, false, 1.0F, 0.0F, limbSwing, bounce);
        this.swing(right_foot, speed, degree * 0.5F, false, -2.5F, 0.0F, limbSwing, bounce);
        this.swing(left_foot, speed, degree * 0.5F, false, -2.5F, 0.0F, limbSwing, bounce);
        left_foot.rotateAngleX -= (left_leg.rotateAngleX + body.rotateAngleX);
        left_foot.rotateAngleZ -= body.rotateAngleZ;
        right_foot.rotateAngleX -= (right_leg.rotateAngleX + body.rotateAngleX);
        right_foot.rotateAngleZ -= body.rotateAngleZ;
        left_leg.rotationPointY += 2.0F * (float) (Math.sin(limbSwing * speed + 2.5D) * (double) bounce * (double) degree - (double) (bounce * degree));
        right_leg.rotationPointY += 2.0F * (float) (Math.sin(-limbSwing * speed + 2.5D) * (double) bounce * (double) degree - (double) (bounce * degree));
        this.flap(head, speed, degree * 0.5F, true, 0.0F, 0.0F, limbSwing, bounce);
        this.swing(head, speed, degree * 0.5F, true, 1.0F, 0.0F, limbSwing, bounce);
        this.flap(tail, speed, degree * 0.5F, true, 0.0F, 0.0F, limbSwing, bounce);
        this.swing(tail, speed, degree * 0.5F, false, 2.0F, 0.0F, limbSwing, bounce);
    }

    @Override
    public Iterable<BasicModelPart> parts() {
        return ImmutableList.of(root);
    }

    @Override
    public Iterable<AdvancedModelBox> getAllParts() {
        return ImmutableList.of(root, body, head, shroom_cap, left_ear, right_ear, left_brow, right_brow, snout, snout_r1, left_arm, right_arm, left_leg, right_leg, tail, belly, left_foot, right_foot);
    }

    public void setRotationAngle(AdvancedModelBox advancedModelBox, float x, float y, float z) {
        advancedModelBox.rotateAngleX = x;
        advancedModelBox.rotateAngleY = y;
        advancedModelBox.rotateAngleZ = z;
    }
}
