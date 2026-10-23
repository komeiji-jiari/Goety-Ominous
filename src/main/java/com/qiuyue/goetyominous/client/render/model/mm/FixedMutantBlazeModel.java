package com.qiuyue.goetyominous.client.render.model.mm;

import com.alexander.mutantmore.animation.keyframe_animations.definition.MutantBlazeKeyframeAnimations;
import com.alexander.mutantmore.animation.math_animation.definition.MutantBlazeMathAnimations;
import com.alexander.mutantmore.entities.MutantBlaze;
import com.alexander.mutantmore.models.entities.MutantBlazeModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FixedMutantBlazeModel extends MutantBlazeModel<MutantBlaze> {
    private final ModelPart head;
    private final ModelPart shield1;
    private final ModelPart shield2;
    private final ModelPart shield3;
    private final ModelPart shield4;
    private final ModelPart shield5;
    private final ModelPart shield6;
    private final ModelPart shield7;

    public FixedMutantBlazeModel(ModelPart root) {
        super(root);
        this.head = root.getChild("everything").getChild("head");
        ModelPart shieldsLayer1 = root.getChild("everything").getChild("shields").getChild("shieldLayer1Rot").getChild("shieldLayer1");
        this.shield1 = shieldsLayer1.getChild("shield");
        this.shield2 = shieldsLayer1.getChild("shield2");
        this.shield3 = shieldsLayer1.getChild("shield3");
        ModelPart shieldsLayer2 = root.getChild("everything").getChild("shields").getChild("shieldLayer2Rot").getChild("shieldLayer2");
        this.shield4 = shieldsLayer2.getChild("shield4");
        this.shield5 = shieldsLayer2.getChild("shield5");
        this.shield6 = shieldsLayer2.getChild("shield6");
        this.shield7 = shieldsLayer2.getChild("shield7");
    }

    @Override
    public void setupAnim(MutantBlaze entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        MutantMobAnimationState state = MutantMobAnimationStates.get(entity);
        float targetWalkSpeed = (float) entity.getDeltaMovement().horizontalDistance() * 15.0F;
        if (state.walkPhaseAge < 0.0F) {
            state.smoothedWalkSpeed = targetWalkSpeed;
        } else {
            float deltaTicks = Mth.clamp(ageInTicks - state.walkPhaseAge, 0.0F, 2.0F);
            state.smoothedWalkSpeed += (targetWalkSpeed - state.smoothedWalkSpeed) * Math.min(1.0F, deltaTicks * 0.35F);
        }
        state.walkPhaseAge = ageInTicks;
        float walkSpeed = state.smoothedWalkSpeed;
        this.mathAnimateState(entity, entity.introAnimation, MutantBlazeMathAnimations.INTRO, 1.0F, walkSpeed, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        if (!entity.introAnimation.isStarted() && !entity.stunnedAnimation.isStarted()) {
            this.mathAnimate(entity, MutantBlazeMathAnimations.IDLE, 0L, walkSpeed, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        }
        if (entity.isCrouching()) {
            this.applyStatic(MutantBlazeKeyframeAnimations.CROUCH);
        }
        this.animate(entity.noveltyAnimation, MutantBlazeKeyframeAnimations.NOVELTY, ageInTicks);
        this.animate(entity.shootAnimation, MutantBlazeKeyframeAnimations.SHOOT, ageInTicks);
        if (entity.rodShotAnimation.isStarted()) {
            this.animate(entity.rodShotAnimation, MutantBlazeKeyframeAnimations.ROD_SHOT_KEYFRAME, ageInTicks);
            this.mathAnimate(entity, MutantBlazeMathAnimations.ROD_SHOT_MATH, entity.rodShotAnimation.getAccumulatedTime(), 1.0F, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        }
        this.mathAnimateState(entity, entity.stunnedAnimation, MutantBlazeMathAnimations.STUNNED, 1.0F, walkSpeed, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        if (entity.deathAnimation.isStarted()) {
            this.animate(entity.deathAnimation, MutantBlazeKeyframeAnimations.DEATH_KEYFRAME, ageInTicks);
            this.mathAnimate(entity, MutantBlazeMathAnimations.DEATH_MATH, entity.deathAnimation.getAccumulatedTime(), 1.0F, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        }
        this.head.xRot += (float) Math.toRadians(headPitch);
        this.head.yRot += (float) Math.toRadians(netHeadYaw);
        this.shield1.visible = !entity.isShieldBroken(5);
        this.shield2.visible = !entity.isShieldBroken(6);
        this.shield3.visible = !entity.isShieldBroken(4);
        this.shield4.visible = !entity.isShieldBroken(1);
        this.shield5.visible = !entity.isShieldBroken(2);
        this.shield6.visible = !entity.isShieldBroken(3);
        this.shield7.visible = !entity.isShieldBroken(0);
    }
}
