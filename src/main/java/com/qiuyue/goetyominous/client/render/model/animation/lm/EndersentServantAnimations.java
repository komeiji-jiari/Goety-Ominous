package com.qiuyue.goetyominous.client.render.model.animation.lm;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EndersentServantAnimations {

    public static final AnimationDefinition TELEPORT_IN = AnimationDefinition.Builder.withLength(1.92F)
            .addAnimation("upperBody",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.0F, KeyframeAnimations.degreeVec(-22.5F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(0.28F, KeyframeAnimations.degreeVec(-22.5F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.44F, KeyframeAnimations.degreeVec(47.5F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(1.52F, KeyframeAnimations.degreeVec(47.5F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(1.92F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.CATMULLROM)))
            .addAnimation("head",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.0F, KeyframeAnimations.degreeVec(-12.5F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(1.52F, KeyframeAnimations.degreeVec(-12.5F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(1.92F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.CATMULLROM)))
            .addAnimation("leftArm",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.0F, KeyframeAnimations.degreeVec(-224.89F, -3.21F, -3.83F),
                                    AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(0.28F, KeyframeAnimations.degreeVec(-224.89F, -3.21F, -3.83F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.44F, KeyframeAnimations.degreeVec(-47.29F, 5.65F, 0.8F),
                                    AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(1.52F, KeyframeAnimations.degreeVec(-47.29F, 5.65F, 0.8F),
                                    AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(1.92F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.CATMULLROM)))
            .addAnimation("leftArmBone",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.0F, KeyframeAnimations.degreeVec(-55.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(0.28F, KeyframeAnimations.degreeVec(-55.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.44F, KeyframeAnimations.degreeVec(-40.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(1.52F, KeyframeAnimations.degreeVec(-40.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(1.92F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.CATMULLROM)))
            .addAnimation("rightArm",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.0F, KeyframeAnimations.degreeVec(-222.39F, 3.21F, 3.83F),
                                    AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(0.28F, KeyframeAnimations.degreeVec(-222.39F, 3.21F, 3.83F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.44F, KeyframeAnimations.degreeVec(-42.29F, -5.44F, -1.19F),
                                    AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(1.52F, KeyframeAnimations.degreeVec(-42.29F, -5.44F, -1.19F),
                                    AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(1.92F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.CATMULLROM)))
            .addAnimation("rightArmBone",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.0F, KeyframeAnimations.degreeVec(-62.5F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(0.28F, KeyframeAnimations.degreeVec(-62.5F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.LINEAR),
                            new Keyframe(0.44F, KeyframeAnimations.degreeVec(-50.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(1.52F, KeyframeAnimations.degreeVec(-50.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(1.92F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.CATMULLROM)))
            .build();

    public static final AnimationDefinition TELEPORT_OUT = AnimationDefinition.Builder.withLength(1.92F)
            .addAnimation("upperBody",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.0F, KeyframeAnimations.degreeVec(12.5F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(0.625F, KeyframeAnimations.degreeVec(-7.81F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(0.8343334F, KeyframeAnimations.degreeVec(-20.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(1.25F, KeyframeAnimations.degreeVec(-22.5F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(1.92F, KeyframeAnimations.degreeVec(-22.5F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.CATMULLROM)))
            .addAnimation("head",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.0F, KeyframeAnimations.degreeVec(-12.5F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(1.92F, KeyframeAnimations.degreeVec(-12.5F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.CATMULLROM)))
            .addAnimation("leftArm",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.0F, KeyframeAnimations.degreeVec(-12.5F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(0.625F, KeyframeAnimations.degreeVec(-142.12F, -2.01F, -2.4F),
                                    AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(0.8343334F, KeyframeAnimations.degreeVec(-219.89F, -3.21F, -3.83F),
                                    AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(1.25F, KeyframeAnimations.degreeVec(-224.89F, -3.21F, -3.83F),
                                    AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(1.92F, KeyframeAnimations.degreeVec(-224.89F, -3.21F, -3.83F),
                                    AnimationChannel.Interpolations.CATMULLROM)))
            .addAnimation("leftArmBone",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(0.625F, KeyframeAnimations.degreeVec(-55.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(1.92F, KeyframeAnimations.degreeVec(-55.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.CATMULLROM)))
            .addAnimation("rightArm",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.0F, KeyframeAnimations.degreeVec(-12.5F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(0.625F, KeyframeAnimations.degreeVec(-142.12F, 2.01F, 2.4F),
                                    AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(0.8343334F, KeyframeAnimations.degreeVec(-219.89F, 3.21F, 3.83F),
                                    AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(1.25F, KeyframeAnimations.degreeVec(-222.39F, 3.21F, 3.83F),
                                    AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(1.92F, KeyframeAnimations.degreeVec(-222.39F, 3.21F, 3.83F),
                                    AnimationChannel.Interpolations.CATMULLROM)))
            .addAnimation("rightArmBone",
                    new AnimationChannel(AnimationChannel.Targets.ROTATION,
                            new Keyframe(0.0F, KeyframeAnimations.degreeVec(0.0F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(0.8343334F, KeyframeAnimations.degreeVec(-62.5F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.CATMULLROM),
                            new Keyframe(1.92F, KeyframeAnimations.degreeVec(-62.5F, 0.0F, 0.0F),
                                    AnimationChannel.Interpolations.CATMULLROM)))
            .build();
}
