package com.qiuyue.goetyominus.client.render.model.animation.mm;

import com.alexander.mutantmore.animation.sine_wave_animations.SineWaveAnimationUtils;
import com.alexander.mutantmore.animation.sine_wave_animations.SineWaveMotionTypes;
import com.qiuyue.goetyominus.client.render.model.mm.MutantHoglinServantModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MutantHoglinSineWaveAnimations {
    private static float floatMax = Float.MAX_VALUE;

    public MutantHoglinSineWaveAnimations() {
    }

    public static void mutantHoglinIdleAnimation(MutantHoglinServantModel<?> model, float tick, float speedMultiplier, float amountMultiplier) {
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.rightFrontLegLower, 0.15F, 100.0F, tick, -floatMax, floatMax, -200.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.POSITION_X);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.rightFrontLegLower, -0.35F, 100.0F, tick, -floatMax, floatMax, -200.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.POSITION_Y);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.rightFrontLegLower, -25.0F, amountMultiplier, SineWaveMotionTypes.ROTATION_Z);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.rightFrontLegLower, 0.7F, amountMultiplier, SineWaveMotionTypes.POSITION_Y);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.rightFrontLeg, 0.5F, 100.0F, tick, -floatMax, floatMax, -200.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.POSITION_Y);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.rightFrontLeg, 25.0F, amountMultiplier, SineWaveMotionTypes.ROTATION_Z);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.rightFrontLeg, 4.0F, amountMultiplier, SineWaveMotionTypes.POSITION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.rightFrontLeg, -3.0F, amountMultiplier, SineWaveMotionTypes.POSITION_Y);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.leftFrontLegLower, -0.15F, 100.0F, tick, -floatMax, floatMax, -200.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.POSITION_X);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.leftFrontLegLower, -0.35F, 100.0F, tick, -floatMax, floatMax, -200.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.POSITION_Y);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftFrontLegLower, 25.0F, amountMultiplier, SineWaveMotionTypes.ROTATION_Z);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftFrontLegLower, 0.7F, amountMultiplier, SineWaveMotionTypes.POSITION_Y);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.leftFrontLeg, 0.5F, 100.0F, tick, -floatMax, floatMax, -200.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.POSITION_Y);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftFrontLeg, -25.0F, amountMultiplier, SineWaveMotionTypes.ROTATION_Z);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftFrontLeg, -4.0F, amountMultiplier, SineWaveMotionTypes.POSITION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftFrontLeg, -3.0F, amountMultiplier, SineWaveMotionTypes.POSITION_Y);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.tail, 7.5F, 100.0F, tick, -floatMax, floatMax, -100.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.tail, 15.0F, 25.0F, tick, -floatMax, floatMax, -150.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_Z);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.tail, 157.5F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.rightEar, -5.0F, 100.0F, tick, -floatMax, floatMax, -250.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_Y);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.rightEar, 20.1021F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.rightEar, -63.2047F, amountMultiplier, SineWaveMotionTypes.ROTATION_Y);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.rightEar, -18.3342F, amountMultiplier, SineWaveMotionTypes.ROTATION_Z);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.leftEar, 5.0F, 100.0F, tick, -floatMax, floatMax, -250.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_Y);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftEar, 20.1021F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftEar, 63.2047F, amountMultiplier, SineWaveMotionTypes.ROTATION_Y);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftEar, 18.3342F, amountMultiplier, SineWaveMotionTypes.ROTATION_Z);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.head, 1.0F, 100.0F, tick, -floatMax, floatMax, -200.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.head, -15.0F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.neck, 2.5F, 100.0F, tick, -floatMax, floatMax, -150.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.neck, 15.0F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.chest, 1.25F, 100.0F, tick, -floatMax, floatMax, -100.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.chest, 32.5F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.body, 1.0F, 100.0F, tick, -floatMax, floatMax, 0.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.body, -27.5F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
    }

    public static void mutantHoglinWalkAnimation(MutantHoglinServantModel<?> model, float tick, float speedMultiplier, float amountMultiplier) {
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.rightFrontLegLower, 35.0F, 250.0F, tick, 0.0F, floatMax, -125.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.rightFrontLegLower, -15.0F, amountMultiplier, SineWaveMotionTypes.ROTATION_Z);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.rightFrontLeg, 25.0F, 250.0F, tick, -floatMax, floatMax, 0.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.rightFrontLeg, 6.0F, 250.0F, tick, 0.0F, floatMax, 60.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.POSITION_Y);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.rightFrontLeg, 2.5F, 250.0F, tick, -floatMax, floatMax, -50.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.POSITION_Z);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.rightFrontLeg, 15.0F, amountMultiplier, SineWaveMotionTypes.ROTATION_Z);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.rightFrontLeg, 4.0F, amountMultiplier, SineWaveMotionTypes.POSITION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.rightFrontLeg, -1.0F, amountMultiplier, SineWaveMotionTypes.POSITION_Y);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.rightFrontLeg, 2.0F, amountMultiplier, SineWaveMotionTypes.POSITION_Z);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.leftFrontLegLower, -35.0F, 250.0F, tick, 0.0F, floatMax, -125.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftFrontLegLower, 15.0F, amountMultiplier, SineWaveMotionTypes.ROTATION_Z);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.leftFrontLeg, -25.0F, 250.0F, tick, -floatMax, floatMax, 0.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.leftFrontLeg, -6.0F, 250.0F, tick, 0.0F, floatMax, 60.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.POSITION_Y);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.leftFrontLeg, -2.5F, 250.0F, tick, -floatMax, floatMax, -50.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.POSITION_Z);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftFrontLeg, -15.0F, amountMultiplier, SineWaveMotionTypes.ROTATION_Z);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftFrontLeg, -4.0F, amountMultiplier, SineWaveMotionTypes.POSITION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftFrontLeg, -1.0F, amountMultiplier, SineWaveMotionTypes.POSITION_Y);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftFrontLeg, 2.0F, amountMultiplier, SineWaveMotionTypes.POSITION_Z);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.rightBackLeg, -25.0F, 250.0F, tick, -floatMax, floatMax, 0.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.rightBackLeg, -6.0F, 250.0F, tick, 0.0F, floatMax, 60.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.POSITION_Y);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.rightBackLeg, -2.5F, 250.0F, tick, -floatMax, floatMax, -50.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.POSITION_Z);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.rightBackLeg, -1.0F, amountMultiplier, SineWaveMotionTypes.POSITION_Z);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.leftBackLeg, 25.0F, 250.0F, tick, -floatMax, floatMax, 0.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.leftBackLeg, 6.0F, 250.0F, tick, 0.0F, floatMax, 60.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.POSITION_Y);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.leftBackLeg, 2.5F, 250.0F, tick, -floatMax, floatMax, -50.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.POSITION_Z);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftBackLeg, -1.0F, amountMultiplier, SineWaveMotionTypes.POSITION_Z);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.tail, 7.5F, 500.0F, tick, -floatMax, floatMax, 50.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.tail, 25.0F, 100.0F, tick, -floatMax, floatMax, 50.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_Z);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.tail, 157.5F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.rightEar, 7.5F, 500.0F, tick, -floatMax, floatMax, 150.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_Y);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.rightEar, 17.411F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.rightEar, -58.4686F, amountMultiplier, SineWaveMotionTypes.ROTATION_Y);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.rightEar, -15.2017F, amountMultiplier, SineWaveMotionTypes.ROTATION_Z);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.leftEar, -7.5F, 500.0F, tick, -floatMax, floatMax, 150.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_Y);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftEar, 17.411F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftEar, 58.4686F, amountMultiplier, SineWaveMotionTypes.ROTATION_Y);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftEar, 15.2017F, amountMultiplier, SineWaveMotionTypes.ROTATION_Z);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.head, 2.5F, 500.0F, tick, -floatMax, floatMax, 50.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.head, -2.5F, 250.0F, tick, -floatMax, floatMax, 0.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_Z);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.head, -15.0F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.neck, 2.5F, 500.0F, tick, -floatMax, floatMax, 100.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.neck, 10.0F, 250.0F, tick, -floatMax, floatMax, 25.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_Z);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.neck, 15.0F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.chest, 1.0F, 500.0F, tick, -floatMax, floatMax, 500.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.chest, 10.0F, 250.0F, tick, -floatMax, floatMax, 60.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_Y);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.chest, -10.0F, 250.0F, tick, -floatMax, floatMax, 60.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_Z);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.chest, 35.0F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.body, 2.5F, 500.0F, tick, -floatMax, floatMax, 100.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.body, 5.0F, 250.0F, tick, -floatMax, floatMax, 60.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_Z);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.body, 1.5F, 500.0F, tick, -floatMax, floatMax, 60.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.POSITION_Y);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.body, -30.0F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
    }

    public static void mutantHoglinChargingAnimation(MutantHoglinServantModel<?> model, float tick, float speedMultiplier, float amountMultiplier) {
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.rightFrontLegLower, 50.0F, 250.0F, tick, 0.0F, floatMax, -50.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.rightFrontLeg, -30.0F, 250.0F, tick, -floatMax, floatMax, -100.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.rightFrontLeg, 7.5F, 250.0F, tick, 0.0F, floatMax, -40.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.POSITION_Y);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.rightFrontLeg, -5.0F, 250.0F, tick, -floatMax, floatMax, -150.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.POSITION_Z);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.rightFrontLeg, 5.0F, amountMultiplier, SineWaveMotionTypes.ROTATION_Z);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.rightFrontLeg, 2.0F, amountMultiplier, SineWaveMotionTypes.POSITION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.rightFrontLeg, -3.0F, amountMultiplier, SineWaveMotionTypes.POSITION_Y);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.leftFrontLegLower, 50.0F, 250.0F, tick, 0.0F, floatMax, -150.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.leftFrontLeg, -30.0F, 250.0F, tick, -floatMax, floatMax, -200.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.leftFrontLeg, 7.5F, 250.0F, tick, 0.0F, floatMax, -140.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.POSITION_Y);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.leftFrontLeg, -5.0F, 250.0F, tick, -floatMax, floatMax, -250.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.POSITION_Z);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftFrontLeg, -5.0F, amountMultiplier, SineWaveMotionTypes.ROTATION_Z);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftFrontLeg, -2.0F, amountMultiplier, SineWaveMotionTypes.POSITION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftFrontLeg, -3.0F, amountMultiplier, SineWaveMotionTypes.POSITION_Y);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.rightBackLeg, -40.0F, 250.0F, tick, -floatMax, floatMax, -200.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.rightBackLeg, 5.0F, 250.0F, tick, 0.0F, floatMax, -140.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.POSITION_Y);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.rightBackLeg, -2.5F, 250.0F, tick, -floatMax, floatMax, -250.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.POSITION_Z);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.rightBackLeg, -2.0F, amountMultiplier, SineWaveMotionTypes.POSITION_Z);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.leftBackLeg, -40.0F, 250.0F, tick, -floatMax, floatMax, -100.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.leftBackLeg, 5.0F, 250.0F, tick, 0.0F, floatMax, -40.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.POSITION_Y);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.leftBackLeg, -2.5F, 250.0F, tick, -floatMax, floatMax, -150.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.POSITION_Z);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftBackLeg, -2.0F, amountMultiplier, SineWaveMotionTypes.POSITION_Z);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.tail, -7.5F, 500.0F, tick, -floatMax, floatMax, -50.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.tail, 135.0F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.rightEar, 7.5F, 500.0F, tick, -floatMax, floatMax, -200.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_Y);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.rightEar, 64.0847F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.rightEar, -21.6372F, amountMultiplier, SineWaveMotionTypes.ROTATION_Y);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.rightEar, -67.4419F, amountMultiplier, SineWaveMotionTypes.ROTATION_Z);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.rightEar, -2.0F, amountMultiplier, SineWaveMotionTypes.POSITION_Y);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.rightEar, -0.5F, amountMultiplier, SineWaveMotionTypes.POSITION_Z);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.leftEar, 7.5F, 500.0F, tick, -floatMax, floatMax, -200.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_Y);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftEar, 64.0847F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftEar, 21.6372F, amountMultiplier, SineWaveMotionTypes.ROTATION_Y);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftEar, 67.4419F, amountMultiplier, SineWaveMotionTypes.ROTATION_Z);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftEar, -2.0F, amountMultiplier, SineWaveMotionTypes.POSITION_Y);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.leftEar, -0.5F, amountMultiplier, SineWaveMotionTypes.POSITION_Z);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.head, -5.0F, 250.0F, tick, -floatMax, floatMax, -200.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.head, -15.0F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.neck, -5.0F, 250.0F, tick, -floatMax, floatMax, -100.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.neck, 15.0F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.chest, -2.5F, 250.0F, tick, -floatMax, floatMax, -50.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.chest, 17.5F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.body, -2.5F, 250.0F, tick, -floatMax, floatMax, 0.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.addSineWaveMotionToModelPart(model.body, 2.5F, 250.0F, tick, -floatMax, floatMax, -140.0F, speedMultiplier, amountMultiplier, SineWaveMotionTypes.POSITION_Y);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.body, -15.0F, amountMultiplier, SineWaveMotionTypes.ROTATION_X);
        SineWaveAnimationUtils.adjustPositionOfModelPart(model.body, 3.0F, amountMultiplier, SineWaveMotionTypes.POSITION_Y);
    }
}
