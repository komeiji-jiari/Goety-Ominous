package com.qiuyue.goetyominous.client.render.model.animation.mm;

import com.alexander.mutantmore.animation.sine_wave_animations.SineWaveAnimationUtils;
import com.alexander.mutantmore.animation.sine_wave_animations.SineWaveMotionTypes;
import com.qiuyue.goetyominous.client.render.model.mm.MutantFrozenZombieServantModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MutantFrozenZombieSineWaveAnimations {
    private static float floatMax = Float.MAX_VALUE;

    public static void mutantFrozenZombieIdleAnimation(MutantFrozenZombieServantModel<?> model, float tick, float speedMultiplier, float amountMultiplier) {
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.pelvis, 2.5F, 25.0F, tick, -floatMax, floatMax, 0.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.pelvis, 0.1F, 2000.0F, tick, -floatMax, floatMax, 0.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_Z);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.pelvis, 23.0654F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.pelvis, -9.589F, amountMultiplier, SineWaveMotionTypes.ROTATION_Y);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.pelvis, -1.5102F, amountMultiplier, SineWaveMotionTypes.ROTATION_Z);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.body, 5.0F, 25.0F, tick, -floatMax, floatMax, -50.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.body, 32.6344F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.body, 6.0935F, amountMultiplier, SineWaveMotionTypes.ROTATION_Y);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.body, 1.1885F, amountMultiplier, SineWaveMotionTypes.ROTATION_Z);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.head, -5.0F, 25.0F, tick, -floatMax, floatMax, 0.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.head, -48.9648F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.head, 0.8778F, amountMultiplier, SineWaveMotionTypes.ROTATION_Y);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.head, -1.7604F, amountMultiplier, SineWaveMotionTypes.ROTATION_Z);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.leftArm, -7.5F, 25.0F, tick, -floatMax, floatMax, -50.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.leftArm, -0.1F, 2000.0F, tick, -floatMax, floatMax, 0.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_Z);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftArm, -36.975F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftArm, -5.0461F, amountMultiplier, SineWaveMotionTypes.ROTATION_Y);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftArm, -11.4427F, amountMultiplier, SineWaveMotionTypes.ROTATION_Z);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftArm, 2.0F, amountMultiplier, SineWaveMotionTypes.POSITION_X);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.leftArmLower, 2.5F, 25.0F, tick, -floatMax, floatMax, -50.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftArmLower, -22.4842F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftArmLower, -0.7518F, amountMultiplier, SineWaveMotionTypes.ROTATION_Y);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftArmLower, 0.4339F, amountMultiplier, SineWaveMotionTypes.ROTATION_Z);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.rightArm, -10.0F, 25.0F, tick, -floatMax, floatMax, -100.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.rightArm, -15.7772F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.rightArm, 10.1991F, amountMultiplier, SineWaveMotionTypes.ROTATION_Y);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.rightArm, 22.8505F, amountMultiplier, SineWaveMotionTypes.ROTATION_Z);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.rightArmLower, -57.5F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftLeg, -37.5F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftLegLower, 37.5F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.rightLeg, -37.5F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.rightLegLower, 37.5F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
    }

    public static void mutantFrozenZombieWalkAnimation(MutantFrozenZombieServantModel<?> model, float tick, float speedMultiplier, float amountMultiplier) {
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.pelvis, -2.0F, 500.0F, tick, -floatMax, floatMax, 25.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.pelvis, -1.5F, 250.0F, tick, -floatMax, floatMax, -25.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_Z);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.pelvis, 0.3F, 2000.0F, tick, -floatMax, floatMax, 0.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_Z);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.pelvis, 22.232F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.pelvis, -3.9848F, amountMultiplier, SineWaveMotionTypes.ROTATION_Y);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.pelvis, 4.3371F, amountMultiplier, SineWaveMotionTypes.ROTATION_Z);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.pelvis, 1.0F, 500.0F, tick, -floatMax, floatMax, 50.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.POSITION_Y);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.body, -0.5F, 500.0F, tick, -floatMax, floatMax, 75.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.body, -1.5F, 250.0F, tick, -floatMax, floatMax, -75.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_Z);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.body, 39.4343F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.body, -7.3482F, amountMultiplier, SineWaveMotionTypes.ROTATION_Y);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.body, 7.2007F, amountMultiplier, SineWaveMotionTypes.ROTATION_Z);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.body, 0.25F, 500.0F, tick, -floatMax, floatMax, 75.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.POSITION_Y);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.head, 2.0F, 250.0F, tick, -floatMax, floatMax, -25.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_Z);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.head, -56.7205F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.head, 4.2576F, amountMultiplier, SineWaveMotionTypes.ROTATION_Y);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.head, -17.6927F, amountMultiplier, SineWaveMotionTypes.ROTATION_Z);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.head, 0.05F, 500.0F, tick, -floatMax, floatMax, 100.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.POSITION_Y);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.leftArm, 1.5F, 500.0F, tick, -floatMax, floatMax, 75.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.leftArm, 2.5F, 250.0F, tick, -floatMax, floatMax, -75.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_Z);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftArm, -30.7004F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftArm, -10.5602F, amountMultiplier, SineWaveMotionTypes.ROTATION_Y);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftArm, -19.9599F, amountMultiplier, SineWaveMotionTypes.ROTATION_Z);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftArm, -0.5F, amountMultiplier, SineWaveMotionTypes.POSITION_Y);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftArm, -2.0F, amountMultiplier, SineWaveMotionTypes.POSITION_Z);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.leftArm, -1.0F, 500.0F, tick, -floatMax, floatMax, 100.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.POSITION_Y);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.leftArm, -1.0F, 500.0F, tick, -floatMax, floatMax, 100.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.POSITION_Z);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.leftArmLower, 2.5F, 500.0F, tick, -floatMax, floatMax, 75.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftArmLower, 10.0F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
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
    }

    public static void mutantFrozenZombieJumpingAnimation(MutantFrozenZombieServantModel<?> model, float tick, float speedMultiplier, float amountMultiplier) {
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.pelvis, 7.5F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.body, -2.5F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.head, -42.5F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftArm, -219.1034F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftArm, -0.703F, amountMultiplier, SineWaveMotionTypes.ROTATION_Y);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftArm, 4.9476F, amountMultiplier, SineWaveMotionTypes.ROTATION_Z);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftArmLower, -57.3925F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
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
    }
}
