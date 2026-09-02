package com.qiuyue.goetyominous.client.render.model.mm;

import com.alexander.mutantmore.animation.math_animation.definition.MutantShulkerMathAnimations1;
import com.alexander.mutantmore.animation.math_animation.definition.MutantShulkerMathAnimations2;
import com.alexander.mutantmore.animation.math_animation.definition.MutantShulkerMathAnimations3;
import com.alexander.mutantmore.animation.sine_wave_animations.SineWaveAnimationUtils;
import com.alexander.mutantmore.models.entities.MMBaseEntityModel;
import com.alexander.mutantmore.models.entities.MutantShulkerModel;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.MutantShulkerServant;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MutantShulkerServantModel<T extends MutantShulkerServant> extends MMBaseEntityModel<T> {
    private final ModelPart head;
    private float walkAmount;
    private boolean wasWalking;
    private float walkScale;
    private float walkPhaseSeconds;
    private float lastAgeInTicks = -1.0F;
    private List<ModelPart> parts;
    private float[][] basePose;
    private float[][] walkPose;

    public MutantShulkerServantModel(ModelPart root) {
        super(root);
        this.head = root.getChild("everything").getChild("box").getChild("head");
    }

    public static LayerDefinition createBodyLayer() {
        return MutantShulkerModel.createBodyLayer();
    }

    @Override
    public void setupAnim(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        super.setupAnim(pEntity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);

        float deltaTicks;
        if (this.lastAgeInTicks < 0.0F) {
            deltaTicks = 1.0F;
        } else {
            deltaTicks = Mth.clamp(pAgeInTicks - this.lastAgeInTicks, 0.0F, 2.0F);
        }
        this.lastAgeInTicks = pAgeInTicks;

        this.mathAnimateState(pEntity, pEntity.deathAnimation, MutantShulkerMathAnimations3.DEATH, 1.0F, 1.0F, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
        if (pEntity.introAnimationTick > 0) {
            this.mathAnimateState(pEntity, pEntity.idleRareAnimation, MutantShulkerMathAnimations1.IDLE_RARE, 1.0F, 1.0F, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
        }
        this.mathAnimateState(pEntity, pEntity.anvilCrushAnimation, MutantShulkerMathAnimations3.ANVIL_CRUSH, 1.0F, 1.0F, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
        this.mathAnimateState(pEntity, pEntity.summonTrapsInShellAnimation, MutantShulkerMathAnimations3.SUMMON_TRAPS_IN_SHELL, 1.0F, 1.0F, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
        this.mathAnimateState(pEntity, pEntity.shootInShellAnimation, MutantShulkerMathAnimations3.SHOOT_IN_SHELL, 1.0F, 1.0F, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
        this.mathAnimateState(pEntity, pEntity.idleRareInShellAnimation, MutantShulkerMathAnimations3.IDLE_RARE_IN_SHELL, 1.0F, 1.0F, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
        this.mathAnimateState(pEntity, pEntity.idleInShellAnimation, MutantShulkerMathAnimations3.IDLE_IN_SHELL, 1.0F, 1.0F, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
        this.mathAnimateState(pEntity, pEntity.summonTrapsAnimation, MutantShulkerMathAnimations3.SUMMON_TRAPS, 1.0F, 1.0F, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
        this.mathAnimateState(pEntity, pEntity.biteAnimation, MutantShulkerMathAnimations2.BITE, 1.0F, 1.0F, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
        this.mathAnimateState(pEntity, pEntity.spinningAnimation, MutantShulkerMathAnimations1.SPINNING, 1.0F, 1.0F, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
        this.mathAnimateState(pEntity, pEntity.enterSpinAnimation, MutantShulkerMathAnimations2.ENTER_SPIN, 1.0F, 1.0F, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
        this.mathAnimateState(pEntity, pEntity.shootAnimation, MutantShulkerMathAnimations2.SHOOT, 1.0F, 1.0F, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
        boolean priorityActive = pEntity.isInBox()
                || pEntity.deathAnimation.isStarted()
                || pEntity.anvilCrushAnimation.isStarted()
                || pEntity.summonTrapsInShellAnimation.isStarted()
                || pEntity.shootInShellAnimation.isStarted()
                || pEntity.idleRareInShellAnimation.isStarted()
                || pEntity.idleInShellAnimation.isStarted()
                || pEntity.summonTrapsAnimation.isStarted()
                || pEntity.biteAnimation.isStarted()
                || pEntity.shootAnimation.isStarted()
                || (pEntity.idleRareAnimation.isStarted() && pEntity.introAnimationTick > 0);
        if (!priorityActive) {

            float wSpeed = Math.abs(pEntity.walkAnimation.speed());
            if (this.wasWalking) {
                if (wSpeed < 0.03F) {
                    this.wasWalking = false;
                }
            } else if (wSpeed > 0.075F) {
                this.wasWalking = true;
            }
            this.walkAmount = Mth.clamp(SineWaveAnimationUtils.tickAmountMultiplierChange(this.walkAmount, this.wasWalking, deltaTicks * 0.18F), 0.0F, 1.0F);
            if (this.walkAmount > 0.001F) {

                float targetScale = Mth.clamp(wSpeed * 2.8F, 0.0F, 5.0F);
                this.walkScale += (targetScale - this.walkScale) * Math.min(1.0F, deltaTicks * 0.4F);
                this.walkPhaseSeconds += this.walkScale * (deltaTicks / 20.0F);
                this.ensurePoseBuffers();

                this.resetAll();
                this.applyIdleBase(pEntity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
                this.capturePose(this.basePose);
                this.resetAll();
                this.mathAnimate(pEntity, MutantShulkerMathAnimations1.WALK, (long)(this.walkPhaseSeconds * 1000.0F), 1.0F, pLimbSwing, pLimbSwingAmount, this.walkPhaseSeconds * 20.0F, pNetHeadYaw, pHeadPitch);
                this.capturePose(this.walkPose);
                this.resetAll();
                this.applyIdleBase(pEntity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
                this.lerpTo(this.basePose, this.walkPose, this.walkAmount);
            } else {
                this.applyIdleBase(pEntity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
            }
        }
        this.head.xRot += (float)Math.toRadians(pHeadPitch);
        this.head.yRot += (float)Math.toRadians(pNetHeadYaw);
    }

    private void applyIdleBase(T pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        this.mathAnimateState(pEntity, pEntity.idleAnimation, MutantShulkerMathAnimations1.IDLE, 1.0F, 1.0F, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
        if (!this.wasWalking) {
            this.mathAnimateState(pEntity, pEntity.idleRareAnimation, MutantShulkerMathAnimations1.IDLE_RARE, 1.0F, 1.0F, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
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
        int n = this.parts().size();
        if (this.basePose == null || this.basePose.length != n) {
            this.basePose = new float[n][6];
        }
        if (this.walkPose == null || this.walkPose.length != n) {
            this.walkPose = new float[n][6];
        }
    }

    private void capturePose(float[][] pose) {
        List<ModelPart> list = this.parts();
        for (int i = 0; i < list.size(); i++) {
            ModelPart p = list.get(i);
            pose[i][0] = p.xRot;
            pose[i][1] = p.yRot;
            pose[i][2] = p.zRot;
            pose[i][3] = p.x;
            pose[i][4] = p.y;
            pose[i][5] = p.z;
        }
    }

    private void lerpTo(float[][] from, float[][] to, float k) {
        List<ModelPart> list = this.parts();
        for (int i = 0; i < list.size(); i++) {
            ModelPart p = list.get(i);
            p.xRot = from[i][0] + k * (to[i][0] - from[i][0]);
            p.yRot = from[i][1] + k * (to[i][1] - from[i][1]);
            p.zRot = from[i][2] + k * (to[i][2] - from[i][2]);
            p.x = from[i][3] + k * (to[i][3] - from[i][3]);
            p.y = from[i][4] + k * (to[i][4] - from[i][4]);
            p.z = from[i][5] + k * (to[i][5] - from[i][5]);
        }
    }
}
