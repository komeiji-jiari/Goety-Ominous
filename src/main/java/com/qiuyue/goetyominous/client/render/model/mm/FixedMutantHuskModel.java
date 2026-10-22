package com.qiuyue.goetyominous.client.render.model.mm;

import com.alexander.mutantmore.animation.keyframe_animations.definition.MutantHuskKeyframeAnimations;
import com.alexander.mutantmore.animation.sine_wave_animations.SineWaveAnimationUtils;
import com.alexander.mutantmore.config.mutant_husk.MutantHuskClientConfig;
import com.alexander.mutantmore.entities.MutantHusk;
import com.alexander.mutantmore.models.entities.MutantHuskModel;
import com.qiuyue.goetyominous.client.render.model.animation.mm.MutantHuskSineWaveAnimations;
import com.qiuyue.goetyominous.common.entities.ally.mm.MutantHuskServant;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FixedMutantHuskModel extends MutantHuskModel<MutantHusk> {
    private final MutantHuskServantModel<MutantHuskServant> parts;

    public FixedMutantHuskModel(ModelPart root) {
        super(root);
        this.parts = new MutantHuskServantModel<>(root);
    }

    @Override
    public void setupAnim(MutantHusk entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
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
        MutantHuskSineWaveAnimations.mutantHuskJumpingAnimation(this.parts, sineTick, 1.0F, state.jumpingAnimationAmount);
        MutantHuskSineWaveAnimations.mutantHuskWalkAnimation(this.parts, walkTick, 1.0F, state.walkAnimationAmount);
        MutantHuskSineWaveAnimations.mutantHuskIdleAnimation(this.parts, sineTick, 1.0F, state.idleAnimationAmount);
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
            this.animate(entity.downAnimationState, MutantHuskKeyframeAnimations.MUTANT_HUSK_DOWN(entity), ageInTicks);
        } else if (entity.roarAnimationTick > 0) {
            this.animate(entity.roarAnimationState, MutantHuskKeyframeAnimations.MUTANT_HUSK_ROAR, ageInTicks);
        } else if (entity.geyserAnimationTick > 0) {
            this.animate(entity.geyserAnimationState, MutantHuskKeyframeAnimations.MUTANT_HUSK_GEYSER(entity), ageInTicks);
        } else if (entity.slamAnimationTick > 0) {
            this.animate(entity.slamAnimationState, MutantHuskKeyframeAnimations.MUTANT_HUSK_SLAM, ageInTicks);
        } else if (entity.jumpAnimationTick > 0) {
            this.animate(entity.jumpAnimationState, MutantHuskKeyframeAnimations.MUTANT_HUSK_JUMP, ageInTicks);
        } else if (entity.trapAnimationTick > 0) {
            this.animate(entity.trapAnimationState, MutantHuskKeyframeAnimations.MUTANT_HUSK_TRAP, ageInTicks);
        } else if (entity.throwSandAnimationTick > 0) {
            this.animate(entity.throwSandAnimationState, MutantHuskKeyframeAnimations.MUTANT_HUSK_THROW_SAND, ageInTicks);
        } else if (entity.attackAnimationTick > 0) {
            this.animate(entity.attackAnimationState, MutantHuskKeyframeAnimations.MUTANT_HUSK_ATTACK, ageInTicks);
        } else if (entity.introAnimationTick > 0) {
            this.animate(entity.introAnimationState, MutantHuskKeyframeAnimations.MUTANT_HUSK_INTRO, ageInTicks);
        } else if (entity.noveltyAnimationTick > 0) {
            this.animate(entity.noveltyAnimationState, MutantHuskKeyframeAnimations.MUTANT_HUSK_NOVELTY(entity), ageInTicks);
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
        if (entity.throwSandAnimationTick <= 0) {
            this.parts.sandBlock.xScale = 0.0F;
            this.parts.sandBlock.yScale = 0.0F;
            this.parts.sandBlock.zScale = 0.0F;
        }
        if (MutantHuskClientConfig.cacti.get()) {
            this.parts.cacti.visible = true;
            this.parts.cactus4.visible = true;
            this.parts.cactus5.visible = true;
        } else {
            this.parts.cacti.visible = false;
            this.parts.cactus4.visible = false;
            this.parts.cactus5.visible = false;
        }
    }
}
