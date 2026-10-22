package com.qiuyue.goetyominous.client.render.model.mm;

import com.alexander.mutantmore.advanced_animation_utils.animation_utils.AdvancedAnimationUtils;
import com.alexander.mutantmore.advanced_animation_utils.animation_utils.animation_trackers.EntityAdvancedAnimation;
import com.alexander.mutantmore.advanced_animation_utils.animation_utils.model_animations.AdvancedAnimator;
import com.alexander.mutantmore.animation.definitions.mutant_wither_skeleton.MutantWitherSkeletonAggressiveAdvancedAnimations;
import com.alexander.mutantmore.animation.definitions.mutant_wither_skeleton.MutantWitherSkeletonIdleAdvancedAnimations;
import com.alexander.mutantmore.animation.definitions.mutant_wither_skeleton.MutantWitherSkeletonLungeAdvancedAnimations;
import com.alexander.mutantmore.entities.MutantWitherSkeleton;
import com.alexander.mutantmore.models.entities.MutantWitherSkeletonModel;
import com.alexander.mutantmore.util.PositionUtils;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FixedMutantWitherSkeletonModel extends MutantWitherSkeletonModel<MutantWitherSkeleton> {
    private final ModelPart head;

    public FixedMutantWitherSkeletonModel(ModelPart root) {
        super(root);
        this.head = this.getAnyDescendantWithName("head").get();
    }

    @Override
    public void setupAnim(MutantWitherSkeleton entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!Minecraft.getInstance().isPaused()) {
            this.root().getAllParts().forEach(ModelPart::resetPose);
            this.head.xRot = headPitch * ((float) Math.PI / 180);
            this.head.yRot = netHeadYaw * ((float) Math.PI / 180);
            List<ModelPart> parts = this.root().getAllParts().toList();
            MutantMobAnimationState state = MutantMobAnimationStates.get(entity);

            float deltaTicks;
            boolean firstFrame = state.walkPhaseAge < 0.0F;
            if (firstFrame) {
                deltaTicks = 1.0F;
            } else {
                deltaTicks = Mth.clamp(ageInTicks - state.walkPhaseAge, 0.0F, 2.0F);
            }
            state.walkPhaseAge = ageInTicks;
            double xDiff = entity.getX() - entity.xo;
            double zDiff = entity.getZ() - entity.zo;
            float distanceTravelled = (float) Math.sqrt(xDiff * xDiff + zDiff * zDiff);
            if (firstFrame) {
                state.smoothedWalkSpeed = distanceTravelled;
            } else {
                state.smoothedWalkSpeed += (distanceTravelled - state.smoothedWalkSpeed) * Math.min(1.0F, deltaTicks * 0.35F);
            }

            addWalkModifiers(state, entity.getAnimation("walk"), deltaTicks, state.smoothedWalkSpeed, 3.0F, 1.0F);
            addLegHeightOffsetModifier(entity, PositionUtils.getOffsetPos(entity, -0.75, 0.0, 0.0, 0.0F, entity.yBodyRot), 2, "offset_legs", "left_leg_y", deltaTicks, 0.2F, -0.75F, 1.5F, 1.2F);
            addLegHeightOffsetModifier(entity, PositionUtils.getOffsetPos(entity, 0.75, 0.0, 0.0, 0.0F, entity.yBodyRot), 2, "offset_legs", "right_leg_y", deltaTicks, 0.2F, -0.75F, 1.5F, 1.2F);
            AdvancedAnimator.animate(parts, entity.getAnimation("attack_left"), MutantWitherSkeletonAggressiveAdvancedAnimations.MUTANT_WITHER_SKELETON_ATTACK_LEFT);
            AdvancedAnimator.animate(parts, entity.getAnimation("attack_right"), MutantWitherSkeletonAggressiveAdvancedAnimations.MUTANT_WITHER_SKELETON_ATTACK_RIGHT);
            AdvancedAnimator.animate(parts, entity.getAnimation("block"), MutantWitherSkeletonAggressiveAdvancedAnimations.MUTANT_WITHER_SKELETON_BLOCK);
            AdvancedAnimator.animate(parts, entity.getAnimation("rib_crush"), MutantWitherSkeletonAggressiveAdvancedAnimations.MUTANT_WITHER_SKELETON_RIB_CRUSH);
            AdvancedAnimator.animate(parts, entity.getAnimation("wither_breath"), MutantWitherSkeletonAggressiveAdvancedAnimations.MUTANT_WITHER_SKELETON_WITHER_BREATH);
            AdvancedAnimator.animate(parts, entity.getAnimation("wither_slash"), MutantWitherSkeletonAggressiveAdvancedAnimations.MUTANT_WITHER_SKELETON_WITHER_SLASH);
            AdvancedAnimator.animate(parts, entity.getAnimation("crouching_pose"), MutantWitherSkeletonIdleAdvancedAnimations.MUTANT_WITHER_SKELETON_CROUCHING_POSE);
            AdvancedAnimator.animate(parts, entity.getAnimation("death"), MutantWitherSkeletonIdleAdvancedAnimations.MUTANT_WITHER_SKELETON_DEATH);
            AdvancedAnimator.animate(parts, entity.getAnimation("idle"), MutantWitherSkeletonIdleAdvancedAnimations.MUTANT_WITHER_SKELETON_IDLE);
            AdvancedAnimator.animate(parts, entity.getAnimation("hurt"), MutantWitherSkeletonIdleAdvancedAnimations.MUTANT_WITHER_SKELETON_HURT);
            AdvancedAnimator.animate(parts, entity.getAnimation("intro"), MutantWitherSkeletonIdleAdvancedAnimations.MUTANT_WITHER_SKELETON_INTRO);
            AdvancedAnimator.animate(parts, entity.getAnimation("offset_legs"), MutantWitherSkeletonIdleAdvancedAnimations.MUTANT_WITHER_SKELETON_OFFSET_LEGS);
            AdvancedAnimator.animate(parts, entity.getAnimation("walk"), MutantWitherSkeletonIdleAdvancedAnimations.MUTANT_WITHER_SKELETON_WALK);
            AdvancedAnimator.animate(parts, entity.getAnimation("lunge"), MutantWitherSkeletonLungeAdvancedAnimations.MUTANT_WITHER_SKELETON_LUNGE);
            AdvancedAnimator.animate(parts, entity.getAnimation("lunge_land"), MutantWitherSkeletonLungeAdvancedAnimations.MUTANT_WITHER_SKELETON_LUNGE_LAND);
            AdvancedAnimator.animate(parts, entity.getAnimation("lunging"), MutantWitherSkeletonLungeAdvancedAnimations.MUTANT_WITHER_SKELETON_LUNGING);
            AdvancedAnimator.animate(parts, entity.getAnimation("stunned"), MutantWitherSkeletonLungeAdvancedAnimations.MUTANT_WITHER_SKELETON_STUNNED);
        }
    }

    private static void addWalkModifiers(MutantMobAnimationState state, EntityAdvancedAnimation animation, float deltaTicks, float distanceTravelled, float groundSpeedMultiplier, float walkProgressMultiplier) {
        state.walkPhase += distanceTravelled / 300.0F * walkProgressMultiplier * deltaTicks;
        animation.getModifiers().put("walk_progress", state.walkPhase);
        animation.getModifiers().put("ground_speed", distanceTravelled * groundSpeedMultiplier);
    }

    private static void addLegHeightOffsetModifier(MutantWitherSkeleton entity, Vec3 footPos, int maxSearchRange, String animationName, String modifierName, float deltaTicks, float lerpSpeed, float minLegHeight, float maxLegHeight, float modelScale) {
        EntityAdvancedAnimation animation = entity.getAnimation(animationName);
        float target = Mth.clamp(AdvancedAnimationUtils.getLegHeightOffset(entity, footPos, maxSearchRange), minLegHeight, maxLegHeight) / modelScale;
        animation.getModifiers().put(modifierName, Mth.lerp(Math.min(deltaTicks * lerpSpeed, 1.0F), AdvancedAnimationUtils.modifierIfPresent(animation, modifierName), target));
    }
}
