package com.qiuyue.goetyominous.client.render.model.mm;

import com.alexander.mutantmore.animation.keyframe_animations.definition.MutantJungleZombieKeyframeAnimations;
import com.alexander.mutantmore.animation.sine_wave_animations.SineWaveAnimationUtils;
import com.alexander.mutantmore.entities.MutantJungleZombie;
import com.alexander.mutantmore.models.entities.MutantJungleZombieModel;
import com.qiuyue.goetyominous.client.render.model.animation.mm.MutantJungleZombieSineWaveAnimations;
import com.qiuyue.goetyominous.common.entities.ally.mm.MutantJungleZombieServant;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FixedMutantJungleZombieModel extends MutantJungleZombieModel<MutantJungleZombie> {
    private final MutantJungleZombieServantModel<MutantJungleZombieServant> parts;

    public FixedMutantJungleZombieModel(ModelPart root) {
        super(root);
        this.parts = new MutantJungleZombieServantModel<>(root);
    }

    @Override
    public void setupAnim(MutantJungleZombie entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        MutantMobAnimationState state = MutantMobAnimationStates.get(entity);
        float deltaTicks;
        double xDiff = entity.getX() - entity.xo;
        double zDiff = entity.getZ() - entity.zo;
        float targetWalkSpeed = Math.min((float) Math.sqrt(xDiff * xDiff + zDiff * zDiff) * 8.64F, 6.0F);
        if (state.walkPhaseAge < 0.0F) {
            deltaTicks = 1.0F;
            state.smoothedWalkSpeed = targetWalkSpeed;
        } else {
            deltaTicks = Mth.clamp(ageInTicks - state.walkPhaseAge, 0.0F, 2.0F);
            state.smoothedWalkSpeed += (targetWalkSpeed - state.smoothedWalkSpeed) * Math.min(1.0F, deltaTicks * 0.35F);
        }
        state.walkPhaseAge = ageInTicks;
        float walkSpeedFactor = state.smoothedWalkSpeed;
        state.walkPhase += walkSpeedFactor * deltaTicks;
        float walkTick = state.walkPhase * ((float) Math.PI / 180.0F) / 20.0F;
        boolean movingOnLand = entity.onGround() && !entity.isInWaterOrBubble() && walkSpeedFactor > 0.3F;
        boolean keyframeActive = !entity.notCurrentlyPlayingKeyframeAnimation() || entity.stopRestingAnimationTick > 0;
        boolean shouldPlayRestingAnimation = entity.restTick > 0 && !keyframeActive;
        boolean shouldPlayWalkAnimation = movingOnLand && !shouldPlayRestingAnimation && !keyframeActive;
        boolean shouldPlayIdleAnimation = !shouldPlayWalkAnimation && !shouldPlayRestingAnimation && !keyframeActive;
        float blendSpeed = 0.12F;
        state.restingAnimationAmount = SineWaveAnimationUtils.tickAmountMultiplierChange(state.restingAnimationAmount, shouldPlayRestingAnimation, deltaTicks * blendSpeed);
        state.walkAnimationAmount = SineWaveAnimationUtils.tickAmountMultiplierChange(state.walkAnimationAmount, shouldPlayWalkAnimation, deltaTicks * blendSpeed);
        state.idleAnimationAmount = SineWaveAnimationUtils.tickAmountMultiplierChange(state.idleAnimationAmount, shouldPlayIdleAnimation, deltaTicks * blendSpeed);
        state.keyframeAnimationAmount = SineWaveAnimationUtils.tickAmountMultiplierChange(state.keyframeAnimationAmount, keyframeActive, deltaTicks * 0.2F);
        this.parts.head.xRot = headPitch * ((float) Math.PI / 180);
        this.parts.head.yRot = netHeadYaw * ((float) Math.PI / 180);
        float sineTick = SineWaveAnimationUtils.getTick(entity.tickCount, true);
        MutantJungleZombieSineWaveAnimations.mutantJungleZombieRestingAnimation(this.parts, sineTick, 1.0F, state.restingAnimationAmount);
        MutantJungleZombieSineWaveAnimations.mutantJungleZombieWalkAnimation(this.parts, walkTick, 1.0F, state.walkAnimationAmount);
        MutantJungleZombieSineWaveAnimations.mutantJungleZombieIdleAnimation(this.parts, sineTick, 1.0F, state.idleAnimationAmount);
        List<ModelPart> parts = this.root().getAllParts().collect(Collectors.toList());
        float keyframeAnimationAmount = state.keyframeAnimationAmount;
        boolean needBlend = keyframeAnimationAmount > 0.0F || keyframeActive;
        float[][] pre = null;
        if (needBlend) {
            if (state.heldPose == null || state.heldPose.length != parts.size()) {
                state.heldPose = new float[parts.size()][6];
            }
            pre = new float[parts.size()][6];
            for (int i = 0; i < parts.size(); i++) {
                ModelPart part = parts.get(i);
                pre[i][0] = part.xRot;
                pre[i][1] = part.yRot;
                pre[i][2] = part.zRot;
                pre[i][3] = part.x;
                pre[i][4] = part.y;
                pre[i][5] = part.z;
            }
        }
        if (entity.downAnimationTick > 0) {
            this.animate(entity.downAnimationState, MutantJungleZombieKeyframeAnimations.MUTANT_JUNGLE_ZOMBIE_DOWN(entity), ageInTicks);
        } else if (entity.introAnimationTick > 0) {
            this.animate(entity.introAnimationState, MutantJungleZombieKeyframeAnimations.MUTANT_JUNGLE_ZOMBIE_INTRO, ageInTicks);
        } else if (entity.stopRestingAnimationTick > 0) {
            this.animate(entity.stopRestingAnimationState, MutantJungleZombieKeyframeAnimations.MUTANT_JUNGLE_ZOMBIE_STOP_RESTING, ageInTicks);
        } else if (entity.jumpAnimationTick > 0) {
            this.animate(entity.jumpAnimationState, MutantJungleZombieKeyframeAnimations.MUTANT_JUNGLE_ZOMBIE_JUMP, ageInTicks);
        } else if (entity.trySlamAnimationTick > 0) {
            this.animate(entity.trySlamAnimationState, MutantJungleZombieKeyframeAnimations.MUTANT_JUNGLE_ZOMBIE_TRY_SLAM, ageInTicks);
            this.parts.leftArm.xRot += entity.getGrabArmXRot() * ((float) Math.PI / 180);
        } else if (entity.tryChokeAnimationTick > 0) {
            this.animate(entity.tryChokeAnimationState, MutantJungleZombieKeyframeAnimations.MUTANT_JUNGLE_ZOMBIE_TRY_CHOKE, ageInTicks);
            this.parts.leftArm.xRot += entity.getGrabArmXRot() * ((float) Math.PI / 180);
        } else if (entity.punchAnimationTick > 0) {
            this.animate(entity.punchAnimationState, MutantJungleZombieKeyframeAnimations.MUTANT_JUNGLE_ZOMBIE_PUNCH, ageInTicks);
        } else if (entity.highAttackAnimationTick > 0) {
            this.animate(entity.highAttackAnimationState, MutantJungleZombieKeyframeAnimations.MUTANT_JUNGLE_ZOMBIE_HIGH_ATTACK_SLOWED, ageInTicks);
        } else if (entity.lowAttackAnimationTick > 0) {
            this.animate(entity.lowAttackAnimationState, MutantJungleZombieKeyframeAnimations.MUTANT_JUNGLE_ZOMBIE_LOW_ATTACK_SLOWED, ageInTicks);
        } else if (entity.slamAnimationTick > 0) {
            this.animate(entity.slamAnimationState, MutantJungleZombieKeyframeAnimations.MUTANT_JUNGLE_ZOMBIE_SLAM, ageInTicks);
        } else if (entity.chokeAnimationTick > 0) {
            this.animate(entity.chokeAnimationState, MutantJungleZombieKeyframeAnimations.MUTANT_JUNGLE_ZOMBIE_CHOKE(entity), ageInTicks);
        } else if (entity.noveltyAnimationTick > 0) {
            this.animate(entity.noveltyAnimationState, MutantJungleZombieKeyframeAnimations.MUTANT_JUNGLE_ZOMBIE_NOVELTY, ageInTicks);
        }
        if (keyframeActive) {
            for (int i = 0; i < parts.size(); i++) {
                ModelPart part = parts.get(i);
                state.heldPose[i][0] = part.xRot;
                state.heldPose[i][1] = part.yRot;
                state.heldPose[i][2] = part.zRot;
                state.heldPose[i][3] = part.x;
                state.heldPose[i][4] = part.y;
                state.heldPose[i][5] = part.z;
            }
        }
        if (needBlend) {
            for (int i = 0; i < parts.size(); i++) {
                ModelPart part = parts.get(i);
                part.xRot = pre[i][0] + keyframeAnimationAmount * (state.heldPose[i][0] - pre[i][0]);
                part.yRot = pre[i][1] + keyframeAnimationAmount * (state.heldPose[i][1] - pre[i][1]);
                part.zRot = pre[i][2] + keyframeAnimationAmount * (state.heldPose[i][2] - pre[i][2]);
                part.x = pre[i][3] + keyframeAnimationAmount * (state.heldPose[i][3] - pre[i][3]);
                part.y = pre[i][4] + keyframeAnimationAmount * (state.heldPose[i][4] - pre[i][4]);
                part.z = pre[i][5] + keyframeAnimationAmount * (state.heldPose[i][5] - pre[i][5]);
            }
        }
    }
}
