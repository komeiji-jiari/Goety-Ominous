/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.animation.AnimationChannel
 *  net.minecraft.client.animation.AnimationChannel$Interpolations
 *  net.minecraft.client.animation.AnimationChannel$Targets
 *  net.minecraft.client.animation.AnimationDefinition
 *  net.minecraft.client.animation.AnimationDefinition$Builder
 *  net.minecraft.client.animation.Keyframe
 *  net.minecraft.client.animation.KeyframeAnimations
 */
package com.vivideru.masteryofmagic.client.model.animations;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;

public class VampireNoviceAnimation {
    public static final AnimationDefinition casting = AnimationDefinition.Builder.m_232275_((float)1.2917f).m_232279_("left_arm", new AnimationChannel(AnimationChannel.Targets.f_232251_, new Keyframe[]{new Keyframe(0.0f, KeyframeAnimations.m_253186_((float)-90.0f, (float)0.0f, (float)0.0f), AnimationChannel.Interpolations.f_232229_), new Keyframe(0.3333f, KeyframeAnimations.m_253186_((float)-95.0f, (float)0.0f, (float)0.0f), AnimationChannel.Interpolations.f_232229_), new Keyframe(0.875f, KeyframeAnimations.m_253186_((float)-87.5f, (float)0.0f, (float)0.0f), AnimationChannel.Interpolations.f_232229_), new Keyframe(1.2917f, KeyframeAnimations.m_253186_((float)-90.0f, (float)0.0f, (float)0.0f), AnimationChannel.Interpolations.f_232229_)})).m_232279_("right_arm", new AnimationChannel(AnimationChannel.Targets.f_232251_, new Keyframe[]{new Keyframe(0.0f, KeyframeAnimations.m_253186_((float)7.5f, (float)0.0f, (float)0.0f), AnimationChannel.Interpolations.f_232229_), new Keyframe(0.625f, KeyframeAnimations.m_253186_((float)2.5f, (float)0.0f, (float)0.0f), AnimationChannel.Interpolations.f_232229_), new Keyframe(1.2917f, KeyframeAnimations.m_253186_((float)7.5f, (float)0.0f, (float)0.0f), AnimationChannel.Interpolations.f_232229_)})).m_232282_();
}

