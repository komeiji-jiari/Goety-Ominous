package com.qiuyue.goetyominous.client.render.model.animation.mm;

import com.alexander.mutantmore.animation.sine_wave_animations.SineWaveAnimationUtils;
import com.alexander.mutantmore.animation.sine_wave_animations.SineWaveMotionTypes;
import com.qiuyue.goetyominous.client.render.model.mm.MutantHuskServantModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MutantHuskSineWaveAnimations {
    private static float floatMax = Float.MAX_VALUE;

    public static void mutantHuskIdleAnimation(MutantHuskServantModel<?> model, float tick, float speedMultiplier, float amountMultiplier) {
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.pelvis, 2.5F, 50.0F, tick, -floatMax, floatMax, 0.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.pelvis, 0.5F, 50.0F, tick, -floatMax, floatMax, 25.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_Z);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.pelvis, 14.8959F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.pelvis, -2.9799F, amountMultiplier, SineWaveMotionTypes.ROTATION_Y);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.pelvis, 1.9025F, amountMultiplier, SineWaveMotionTypes.ROTATION_Z);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.pelvis, -1.0F, amountMultiplier, SineWaveMotionTypes.POSITION_Y);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.body, 2.5F, 50.0F, tick, -floatMax, floatMax, -100.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.body, 0.5F, 50.0F, tick, -floatMax, floatMax, 75.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_Z);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.body, 30.0F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.head, 5.0F, 50.0F, tick, -floatMax, floatMax, -75.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.head, -44.9526F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.head, 0.23F, amountMultiplier, SineWaveMotionTypes.ROTATION_Y);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.head, -3.5275F, amountMultiplier, SineWaveMotionTypes.ROTATION_Z);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.leftArm, 5.0F, 50.0F, tick, -floatMax, floatMax, 75.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftArm, -20.0F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftArm, 2.5F, amountMultiplier, SineWaveMotionTypes.ROTATION_Y);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftArm, -20.0F, amountMultiplier, SineWaveMotionTypes.ROTATION_Z);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftArmLower, -60.0F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.rightArm, 5.0F, 50.0F, tick, -floatMax, floatMax, 50.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.rightArm, -17.2212F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.rightArm, 3.3439F, amountMultiplier, SineWaveMotionTypes.ROTATION_Y);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.rightArm, 13.3875F, amountMultiplier, SineWaveMotionTypes.ROTATION_Z);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.rightArmLower, -60.0F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftLeg, -37.5F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftLegLower, 37.5F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.rightLeg, -37.5F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.rightLegLower, 37.5F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.sandBlock, -1.0F, amountMultiplier, SineWaveMotionTypes.SCALE_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.sandBlock, -1.0F, amountMultiplier, SineWaveMotionTypes.SCALE_Y);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.sandBlock, -1.0F, amountMultiplier, SineWaveMotionTypes.SCALE_Z);
    }

    public static void mutantHuskWalkAnimation(MutantHuskServantModel<?> model, float tick, float speedMultiplier, float amountMultiplier) {
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.pelvis, -2.0F, 500.0F, tick, -floatMax, floatMax, 25.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.pelvis, -10.0F, 25.0F, tick, -floatMax, floatMax, -25.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_Y);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.pelvis, -1.5F, 250.0F, tick, -floatMax, floatMax, -25.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_Z);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.pelvis, -5.0F, 50.0F, tick, -floatMax, floatMax, -25.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_Z);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.pelvis, 20.0121F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.pelvis, -2.5367F, amountMultiplier, SineWaveMotionTypes.ROTATION_Y);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.pelvis, -0.4602F, amountMultiplier, SineWaveMotionTypes.ROTATION_Z);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.pelvis, 1.0F, 500.0F, tick, -floatMax, floatMax, 50.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.POSITION_Y);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.body, -0.5F, 500.0F, tick, -floatMax, floatMax, 75.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.body, -1.5F, 250.0F, tick, -floatMax, floatMax, -75.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_Z);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.body, 32.5304F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.body, -3.3582F, amountMultiplier, SineWaveMotionTypes.ROTATION_Y);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.body, 0.821F, amountMultiplier, SineWaveMotionTypes.ROTATION_Z);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.body, 0.25F, 500.0F, tick, -floatMax, floatMax, 75.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.POSITION_Y);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.head, 2.0F, 250.0F, tick, -floatMax, floatMax, -25.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_Z);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.head, -47.6076F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.head, 3.8282F, amountMultiplier, SineWaveMotionTypes.ROTATION_Y);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.head, -3.2187F, amountMultiplier, SineWaveMotionTypes.ROTATION_Z);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.head, 0.05F, 500.0F, tick, -floatMax, floatMax, 100.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.POSITION_Y);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.leftArm, -15.0F, 250.0F, tick, -floatMax, floatMax, 0.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftArm, -20.0431F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftArm, -3.6449F, amountMultiplier, SineWaveMotionTypes.ROTATION_Y);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftArm, -15.6977F, amountMultiplier, SineWaveMotionTypes.ROTATION_Z);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.leftArm, 0.5F, 500.0F, tick, -floatMax, floatMax, 100.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.POSITION_Y);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.leftArm, 0.5F, 500.0F, tick, -floatMax, floatMax, 100.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.POSITION_Z);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftArmLower, -60.0F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.rightArm, 15.0F, 250.0F, tick, -floatMax, floatMax, 0.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.rightArm, -20.038F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.rightArm, 7.4619F, amountMultiplier, SineWaveMotionTypes.ROTATION_Y);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.rightArm, 19.1254F, amountMultiplier, SineWaveMotionTypes.ROTATION_Z);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.rightArm, 0.5F, 500.0F, tick, -floatMax, floatMax, 100.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.POSITION_Y);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.rightArm, 0.5F, 500.0F, tick, -floatMax, floatMax, 100.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.POSITION_Z);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.rightArmLower, -60.0F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.leftLeg, -35.0F, 250.0F, tick, -floatMax, floatMax, 0.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftLeg, -5.0F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.leftLeg, -4.0F, 250.0F, tick, 1.25F, floatMax, 50.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.POSITION_Y);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftLeg, -2.0F, amountMultiplier, SineWaveMotionTypes.POSITION_Z);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.leftLegLower, 45.0F, 250.0F, tick, -17.5F, floatMax, 330.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftLegLower, 17.5F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.rightLeg, 35.0F, 250.0F, tick, -floatMax, floatMax, 0.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.rightLeg, -5.0F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.rightLeg, 4.0F, 250.0F, tick, 1.25F, floatMax, 50.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.POSITION_Y);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.rightLeg, -2.0F, amountMultiplier, SineWaveMotionTypes.POSITION_Z);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.rightLegLower, -45.0F, 250.0F, tick, -17.5F, floatMax, 330.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.rightLegLower, 17.5F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.sandBlock, -1.0F, amountMultiplier, SineWaveMotionTypes.SCALE_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.sandBlock, -1.0F, amountMultiplier, SineWaveMotionTypes.SCALE_Y);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.sandBlock, -1.0F, amountMultiplier, SineWaveMotionTypes.SCALE_Z);
    }

    public static void mutantHuskJumpingAnimation(MutantHuskServantModel<?> model, float tick, float speedMultiplier, float amountMultiplier) {
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.pelvis, 7.5F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.body, -2.5F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.head, -42.5F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftArm, -176.6809F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftArm, -14.6364F, amountMultiplier, SineWaveMotionTypes.ROTATION_Y);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftArm, -12.9264F, amountMultiplier, SineWaveMotionTypes.ROTATION_Z);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftArmLower, -39.8925F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftArmLower, 3.2115F, amountMultiplier, SineWaveMotionTypes.ROTATION_Y);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftArmLower, 3.8342F, amountMultiplier, SineWaveMotionTypes.ROTATION_Z);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.rightArm, -176.6809F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.rightArm, 14.6364F, amountMultiplier, SineWaveMotionTypes.ROTATION_Y);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.rightArm, 12.9264F, amountMultiplier, SineWaveMotionTypes.ROTATION_Z);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.rightArmLower, -39.8925F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.rightArmLower, -3.2115F, amountMultiplier, SineWaveMotionTypes.ROTATION_Y);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.rightArmLower, -3.8342F, amountMultiplier, SineWaveMotionTypes.ROTATION_Z);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftLeg, 10.0F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftLegLower, 17.5F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.rightLeg, 10.0F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.rightLegLower, 17.5F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.sandBlock, -1.0F, amountMultiplier, SineWaveMotionTypes.SCALE_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.sandBlock, -1.0F, amountMultiplier, SineWaveMotionTypes.SCALE_Y);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.sandBlock, -1.0F, amountMultiplier, SineWaveMotionTypes.SCALE_Z);
    }
}

