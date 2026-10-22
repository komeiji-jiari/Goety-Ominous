package com.qiuyue.goetyominous.client.render.model.mm;

import com.alexander.mutantmore.animation.math_animation.definition.MutantShulkerMathAnimations1;
import com.alexander.mutantmore.animation.math_animation.definition.MutantShulkerMathAnimations2;
import com.alexander.mutantmore.animation.math_animation.definition.MutantShulkerMathAnimations3;
import com.alexander.mutantmore.animation.sine_wave_animations.SineWaveAnimationUtils;
import com.alexander.mutantmore.entities.MutantShulker;
import com.alexander.mutantmore.models.entities.MutantShulkerModel;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FixedMutantShulkerModel extends MutantShulkerModel<MutantShulker> {
    private final ModelPart headPart;
    private List<ModelPart> parts;
    private float[][] basePose;
    private float[][] walkPose;

    public FixedMutantShulkerModel(ModelPart root) {
        super(root);
        this.headPart = root.getChild("everything").getChild("box").getChild("head");
    }

    @Override
    public void setupAnim(MutantShulker entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        MutantMobAnimationState state = MutantMobAnimationStates.get(entity);
        float deltaTicks;
        if (state.walkPhaseAge < 0.0F) {
            deltaTicks = 1.0F;
        } else {
            deltaTicks = Mth.clamp(ageInTicks - state.walkPhaseAge, 0.0F, 2.0F);
        }
        state.walkPhaseAge = ageInTicks;

        this.mathAnimateState(entity, entity.deathAnimation, MutantShulkerMathAnimations3.DEATH, 1.0F, 1.0F, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        if (entity.introAnimationTick > 0) {
            this.mathAnimateState(entity, entity.idleRareAnimation, MutantShulkerMathAnimations1.IDLE_RARE, 1.0F, 1.0F, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        }
        this.mathAnimateState(entity, entity.spinningAnimation, MutantShulkerMathAnimations1.SPINNING, 1.0F, 1.0F, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        this.mathAnimateState(entity, entity.enterSpinAnimation, MutantShulkerMathAnimations2.ENTER_SPIN, 1.0F, 1.0F, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        this.mathAnimateState(entity, entity.anvilCrushAnimation, MutantShulkerMathAnimations3.ANVIL_CRUSH, 1.0F, 1.0F, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        this.mathAnimateState(entity, entity.summonTrapsInShellAnimation, MutantShulkerMathAnimations3.SUMMON_TRAPS_IN_SHELL, 1.0F, 1.0F, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        this.mathAnimateState(entity, entity.shootInShellAnimation, MutantShulkerMathAnimations3.SHOOT_IN_SHELL, 1.0F, 1.0F, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        this.mathAnimateState(entity, entity.idleRareInShellAnimation, MutantShulkerMathAnimations3.IDLE_RARE_IN_SHELL, 1.0F, 1.0F, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        this.mathAnimateState(entity, entity.idleInShellAnimation, MutantShulkerMathAnimations3.IDLE_IN_SHELL, 1.0F, 1.0F, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        this.mathAnimateState(entity, entity.summonTrapsAnimation, MutantShulkerMathAnimations3.SUMMON_TRAPS, 1.0F, 1.0F, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        this.mathAnimateState(entity, entity.biteAnimation, MutantShulkerMathAnimations2.BITE, 1.0F, 1.0F, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        this.mathAnimateState(entity, entity.shootAnimation, MutantShulkerMathAnimations2.SHOOT, 1.0F, 1.0F, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        boolean priorityActive = entity.isInBox()
                || entity.deathAnimation.isStarted()
                || entity.spinningAnimation.isStarted()
                || entity.enterSpinAnimation.isStarted()
                || entity.anvilCrushAnimation.isStarted()
                || entity.summonTrapsInShellAnimation.isStarted()
                || entity.shootInShellAnimation.isStarted()
                || entity.idleRareInShellAnimation.isStarted()
                || entity.idleInShellAnimation.isStarted()
                || entity.summonTrapsAnimation.isStarted()
                || entity.biteAnimation.isStarted()
                || entity.shootAnimation.isStarted()
                || (entity.idleRareAnimation.isStarted() && entity.introAnimationTick > 0);

        float walkSpeed = Math.abs(entity.walkAnimation.speed());
        boolean walking = entity.onGround();
        if (state.wasWalking) {
            if (!walking || walkSpeed < 0.03F) {
                state.wasWalking = false;
            }
        } else if (walking && walkSpeed > 0.075F) {
            state.wasWalking = true;
        }
        state.walkAmount = Mth.clamp(SineWaveAnimationUtils.tickAmountMultiplierChange(state.walkAmount, state.wasWalking, deltaTicks * 0.18F), 0.0F, 1.0F);

        if (!priorityActive) {
            if (state.walkAmount > 0.001F) {
                float targetScale = Mth.clamp(walkSpeed * 2.8F, 0.0F, 5.0F);
                state.walkScale += (targetScale - state.walkScale) * Math.min(1.0F, deltaTicks * 0.4F);
                state.walkPhaseSeconds += state.walkScale * (deltaTicks / 20.0F);
                this.ensurePoseBuffers();

                this.resetAll();
                this.applyIdleBase(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
                this.capturePose(this.basePose);
                this.resetAll();
                this.mathAnimate(entity, MutantShulkerMathAnimations1.WALK, (long) (state.walkPhaseSeconds * 1000.0F), 1.0F, limbSwing, limbSwingAmount, state.walkPhaseSeconds * 20.0F, netHeadYaw, headPitch);
                this.capturePose(this.walkPose);
                this.resetAll();
                this.applyIdleBase(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
                this.lerpTo(this.basePose, this.walkPose, state.walkAmount);
            } else {
                this.applyIdleBase(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            }
        }

        this.headPart.xRot += (float) Math.toRadians(headPitch);
        this.headPart.yRot += (float) Math.toRadians(netHeadYaw);
    }

    private void applyIdleBase(MutantShulker entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.mathAnimateState(entity, entity.idleAnimation, MutantShulkerMathAnimations1.IDLE, 1.0F, 1.0F, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        if (!MutantMobAnimationStates.get(entity).wasWalking) {
            this.mathAnimateState(entity, entity.idleRareAnimation, MutantShulkerMathAnimations1.IDLE_RARE, 1.0F, 1.0F, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        }
    }

    private List<ModelPart> parts() {
        if (this.parts == null) {
            this.parts = this.root().getAllParts().collect(Collectors.toList());
        }
        return this.parts;
    }

    private void resetAll() {
        this.parts().forEach(ModelPart::resetPose);
    }

    private void ensurePoseBuffers() {
        int size = this.parts().size();
        if (this.basePose == null || this.basePose.length != size) {
            this.basePose = new float[size][6];
        }
        if (this.walkPose == null || this.walkPose.length != size) {
            this.walkPose = new float[size][6];
        }
    }

    private void capturePose(float[][] pose) {
        List<ModelPart> allParts = this.parts();
        for (int i = 0; i < allParts.size(); i++) {
            ModelPart part = allParts.get(i);
            pose[i][0] = part.xRot;
            pose[i][1] = part.yRot;
            pose[i][2] = part.zRot;
            pose[i][3] = part.x;
            pose[i][4] = part.y;
            pose[i][5] = part.z;
        }
    }

    private void lerpTo(float[][] from, float[][] to, float k) {
        List<ModelPart> allParts = this.parts();
        for (int i = 0; i < allParts.size(); i++) {
            ModelPart part = allParts.get(i);
            part.xRot = from[i][0] + k * (to[i][0] - from[i][0]);
            part.yRot = from[i][1] + k * (to[i][1] - from[i][1]);
            part.zRot = from[i][2] + k * (to[i][2] - from[i][2]);
            part.x = from[i][3] + k * (to[i][3] - from[i][3]);
            part.y = from[i][4] + k * (to[i][4] - from[i][4]);
            part.z = from[i][5] + k * (to[i][5] - from[i][5]);
        }
    }
}
