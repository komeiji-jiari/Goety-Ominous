package com.qiuyue.goetyominous.client.render.model.mm;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MutantMobAnimationState {
    public float walkPhase;
    public float walkPhaseAge = -1.0F;
    public float chargeAnimationAmount;
    public float walkAnimationAmount;
    public float idleAnimationAmount = 1.0F;
    public float jumpingAnimationAmount;
    public float restingAnimationAmount;
    public float keyframeAnimationAmount;
    public float[][] heldPose;
    public float lungingXRot;
    public float lungingXRotO;
    public int lastAnimTick = Integer.MIN_VALUE;
    public float walkAmount;
    public boolean wasWalking;
    public float walkScale;
    public float walkPhaseSeconds;
    public float smoothedWalkSpeed;
}
