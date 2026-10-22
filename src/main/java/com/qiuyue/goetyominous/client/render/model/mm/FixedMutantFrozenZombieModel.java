package com.qiuyue.goetyominous.client.render.model.mm;

import com.alexander.mutantmore.animation.keyframe_animations.definition.MutantFrozenZombieKeyframeAnimations;
import com.alexander.mutantmore.animation.sine_wave_animations.SineWaveAnimationUtils;
import com.alexander.mutantmore.entities.MutantFrozenZombie;
import com.alexander.mutantmore.models.entities.MutantFrozenZombieModel;
import com.qiuyue.goetyominous.client.render.model.animation.mm.MutantFrozenZombieSineWaveAnimations;
import com.qiuyue.goetyominous.common.entities.ally.mm.MutantFrozenZombieServant;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FixedMutantFrozenZombieModel extends MutantFrozenZombieModel<MutantFrozenZombie> {
    private final MutantFrozenZombieServantModel<MutantFrozenZombieServant> parts;

    public FixedMutantFrozenZombieModel(ModelPart root) {
        super(root);
        this.parts = new MutantFrozenZombieServantModel<>(root);
    }

    @Override
    public void setupAnim(MutantFrozenZombie entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
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
        boolean keyframeActive = !entity.notCurrentlyPlayingKeyframeAnimation();
        boolean shouldPlayJumpingAnimation = entity.jumping && !keyframeActive;
        boolean shouldPlayWalkAnimation = movingOnLand && !shouldPlayJumpingAnimation && !keyframeActive;
        boolean shouldPlayIdleAnimation = !shouldPlayWalkAnimation && !shouldPlayJumpingAnimation && !keyframeActive;
        float blendSpeed = 0.12F;
        state.jumpingAnimationAmount = SineWaveAnimationUtils.tickAmountMultiplierChange(state.jumpingAnimationAmount, shouldPlayJumpingAnimation, deltaTicks * blendSpeed);
        state.walkAnimationAmount = SineWaveAnimationUtils.tickAmountMultiplierChange(state.walkAnimationAmount, shouldPlayWalkAnimation, deltaTicks * blendSpeed);
        state.idleAnimationAmount = SineWaveAnimationUtils.tickAmountMultiplierChange(state.idleAnimationAmount, shouldPlayIdleAnimation, deltaTicks * blendSpeed);
        state.keyframeAnimationAmount = SineWaveAnimationUtils.tickAmountMultiplierChange(state.keyframeAnimationAmount, keyframeActive, deltaTicks * 0.2F);
        this.parts.head.xRot = headPitch * ((float) Math.PI / 180);
        this.parts.head.yRot = netHeadYaw * ((float) Math.PI / 180);
        float sineTick = SineWaveAnimationUtils.getTick(entity.tickCount, true);
        MutantFrozenZombieSineWaveAnimations.mutantFrozenZombieJumpingAnimation(this.parts, sineTick, 1.0F, state.jumpingAnimationAmount);
        MutantFrozenZombieSineWaveAnimations.mutantFrozenZombieWalkAnimation(this.parts, walkTick, 1.0F, state.walkAnimationAmount);
        MutantFrozenZombieSineWaveAnimations.mutantFrozenZombieIdleAnimation(this.parts, sineTick, 1.0F, state.idleAnimationAmount);
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
            this.animate(entity.downAnimationState, MutantFrozenZombieKeyframeAnimations.MUTANT_FROZEN_ZOMBIE_DOWN(entity), ageInTicks);
        } else if (entity.roarAnimationTick > 0) {
            this.animate(entity.roarAnimationState, MutantFrozenZombieKeyframeAnimations.MUTANT_FROZEN_ZOMBIE_ROAR, ageInTicks);
        } else if (entity.snowThrowAnimationTick > 0) {
            this.animate(entity.snowThrowAnimationState, MutantFrozenZombieKeyframeAnimations.MUTANT_FROZEN_ZOMBIE_SNOW_THROW, ageInTicks);
        } else if (entity.slamAnimationTick > 0) {
            this.animate(entity.slamAnimationState, MutantFrozenZombieKeyframeAnimations.MUTANT_FROZEN_ZOMBIE_SLAM, ageInTicks);
        } else if (entity.jumpAnimationTick > 0) {
            this.animate(entity.jumpAnimationState, MutantFrozenZombieKeyframeAnimations.MUTANT_FROZEN_ZOMBIE_JUMP, ageInTicks);
        } else if (entity.iciclesAnimationTick > 0) {
            this.animate(entity.iciclesAnimationState, MutantFrozenZombieKeyframeAnimations.MUTANT_FROZEN_ZOMBIE_ICICLES(entity), ageInTicks);
        } else if (entity.throwAnimationTick > 0) {
            this.animate(entity.throwAnimationState, MutantFrozenZombieKeyframeAnimations.MUTANT_FROZEN_ZOMBIE_THROW, ageInTicks);
        } else if (entity.attackAnimationTick > 0) {
            this.animate(entity.attackAnimationState, MutantFrozenZombieKeyframeAnimations.MUTANT_FROZEN_ZOMBIE_ATTACK, ageInTicks);
        } else if (entity.introAnimationTick > 0) {
            this.animate(entity.introAnimationState, MutantFrozenZombieKeyframeAnimations.MUTANT_FROZEN_ZOMBIE_INTRO, ageInTicks);
        } else if (entity.noveltyAnimationTick > 0) {
            this.animate(entity.noveltyAnimationState, MutantFrozenZombieKeyframeAnimations.MUTANT_FROZEN_ZOMBIE_NOVELTY, ageInTicks);
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
        if (entity.throwAnimationTick <= 0) {
            this.parts.projectile.xScale = 0.0F;
            this.parts.projectile.yScale = 0.0F;
            this.parts.projectile.zScale = 0.0F;
        }
        if (entity.snowThrowAnimationTick <= 0) {
            this.parts.snowBlock.xScale = 0.0F;
            this.parts.snowBlock.yScale = 0.0F;
            this.parts.snowBlock.zScale = 0.0F;
        }
    }
}
