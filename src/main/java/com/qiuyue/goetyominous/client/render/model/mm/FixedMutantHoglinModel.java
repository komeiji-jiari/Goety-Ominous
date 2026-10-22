package com.qiuyue.goetyominous.client.render.model.mm;

import com.alexander.mutantmore.animation.keyframe_animations.definition.MutantHoglinKeyframeAnimations;
import com.alexander.mutantmore.animation.sine_wave_animations.SineWaveAnimationUtils;
import com.alexander.mutantmore.entities.MutantHoglin;
import com.alexander.mutantmore.models.entities.MutantHoglinModel;
import com.qiuyue.goetyominous.client.render.model.animation.mm.MutantHoglinSineWaveAnimations;
import com.qiuyue.goetyominous.common.entities.ally.mm.MutantHoglinServant;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FixedMutantHoglinModel extends MutantHoglinModel<MutantHoglin> {
    private final MutantHoglinServantModel<MutantHoglinServant> parts;

    public FixedMutantHoglinModel(ModelPart root) {
        super(root);
        this.parts = new MutantHoglinServantModel<>(root);
    }

    @Override
    public void setupAnim(MutantHoglin entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        MutantMobAnimationState state = MutantMobAnimationStates.get(entity);

        float deltaTicks;
        float targetWalkSpeed = Math.min((float) entity.getDeltaMovement().horizontalDistance() * 8.5F, 6.0F);
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

        boolean shouldPlayChargingAnimation = entity.charging && entity.notCurrentlyPlayingKeyframeAnimation();
        boolean shouldPlayWalkAnimation = movingOnLand && !shouldPlayChargingAnimation && entity.notCurrentlyPlayingKeyframeAnimation();
        boolean shouldPlayIdleAnimation = !shouldPlayWalkAnimation && !shouldPlayChargingAnimation && entity.notCurrentlyPlayingKeyframeAnimation();
        boolean keyframeActive = entity.deathTime > 0
                || entity.stompAnimationTick > 0
                || entity.prepareChargeAnimationTick > 0
                || entity.kickAnimationTick > 0
                || entity.attackAnimationTick > 0
                || entity.introAnimationTick > 0
                || entity.noveltyAnimationTick > 0;
        float blendSpeed = 0.12F;
        state.chargeAnimationAmount = SineWaveAnimationUtils.tickAmountMultiplierChange(state.chargeAnimationAmount, shouldPlayChargingAnimation, deltaTicks * blendSpeed);
        state.walkAnimationAmount = SineWaveAnimationUtils.tickAmountMultiplierChange(state.walkAnimationAmount, shouldPlayWalkAnimation, deltaTicks * blendSpeed);
        state.idleAnimationAmount = SineWaveAnimationUtils.tickAmountMultiplierChange(state.idleAnimationAmount, shouldPlayIdleAnimation, deltaTicks * blendSpeed);
        state.keyframeAnimationAmount = SineWaveAnimationUtils.tickAmountMultiplierChange(state.keyframeAnimationAmount, keyframeActive, deltaTicks * 0.2F);
        this.parts.neck.xRot = headPitch * 0.017453292F;
        this.parts.neck.yRot = netHeadYaw * 0.017453292F;

        float sineTick = SineWaveAnimationUtils.getTick(entity.tickCount, true);
        MutantHoglinSineWaveAnimations.mutantHoglinChargingAnimation(this.parts, sineTick, 3.2F, state.chargeAnimationAmount);
        MutantHoglinSineWaveAnimations.mutantHoglinWalkAnimation(this.parts, walkTick, 1.2F, state.walkAnimationAmount);
        MutantHoglinSineWaveAnimations.mutantHoglinIdleAnimation(this.parts, sineTick, 1.0F, state.idleAnimationAmount);

        List<ModelPart> parts = this.root().getAllParts().collect(Collectors.toList());
        float k = state.keyframeAnimationAmount;
        boolean needBlend = k > 0.0F || keyframeActive;
        float[][] pre = null;
        if (needBlend) {
            if (state.heldPose == null || state.heldPose.length != parts.size()) {
                state.heldPose = new float[parts.size()][6];
            }
            pre = new float[parts.size()][6];
            for (int i = 0; i < parts.size(); i++) {
                ModelPart p = parts.get(i);
                pre[i][0] = p.xRot;
                pre[i][1] = p.yRot;
                pre[i][2] = p.zRot;
                pre[i][3] = p.x;
                pre[i][4] = p.y;
                pre[i][5] = p.z;
            }
        }
        if (entity.deathTime > 0) {
            this.animate(entity.deathAnimationState, MutantHoglinKeyframeAnimations.MUTANT_HOGLIN_DEATH, ageInTicks);
            this.animate(entity.danceAnimationState, MutantHoglinKeyframeAnimations.MUTANT_HOGLIN_DANCE, ageInTicks);
        } else if (entity.stompAnimationTick > 0) {
            this.animate(entity.stompAnimationState, MutantHoglinKeyframeAnimations.MUTANT_HOGLIN_STOMP, ageInTicks);
        } else if (entity.prepareChargeAnimationTick > 0) {
            this.animate(entity.prepareChargeAnimationState, MutantHoglinKeyframeAnimations.MUTANT_HOGLIN_PREPARE_CHARGE, ageInTicks, 1.5F);
        } else if (entity.kickAnimationTick > 0) {
            this.animate(entity.kickAnimationState, MutantHoglinKeyframeAnimations.MUTANT_HOGLIN_KICK, ageInTicks);
        } else if (entity.attackAnimationTick > 0) {
            this.animate(entity.attackAnimationState, MutantHoglinKeyframeAnimations.MUTANT_HOGLIN_ATTACK, ageInTicks);
        } else if (entity.introAnimationTick > 0) {
            this.animate(entity.introAnimationState, MutantHoglinKeyframeAnimations.MUTANT_HOGLIN_INTRO, ageInTicks);
        } else if (entity.noveltyAnimationTick > 0) {
            this.animate(entity.noveltyAnimationState, MutantHoglinKeyframeAnimations.MUTANT_HOGLIN_NOVELTY, ageInTicks);
        }
        if (keyframeActive) {
            for (int i = 0; i < parts.size(); i++) {
                ModelPart p = parts.get(i);
                state.heldPose[i][0] = p.xRot;
                state.heldPose[i][1] = p.yRot;
                state.heldPose[i][2] = p.zRot;
                state.heldPose[i][3] = p.x;
                state.heldPose[i][4] = p.y;
                state.heldPose[i][5] = p.z;
            }
        }
        if (needBlend) {
            for (int i = 0; i < parts.size(); i++) {
                ModelPart p = parts.get(i);
                p.xRot = pre[i][0] + k * (state.heldPose[i][0] - pre[i][0]);
                p.yRot = pre[i][1] + k * (state.heldPose[i][1] - pre[i][1]);
                p.zRot = pre[i][2] + k * (state.heldPose[i][2] - pre[i][2]);
                p.x = pre[i][3] + k * (state.heldPose[i][3] - pre[i][3]);
                p.y = pre[i][4] + k * (state.heldPose[i][4] - pre[i][4]);
                p.z = pre[i][5] + k * (state.heldPose[i][5] - pre[i][5]);
            }
        }
    }
}
