package com.qiuyue.goetyominous.client.render.model.mm;

import com.alexander.mutantmore.advanced_animation_utils.animation_utils.AdvancedAnimationUtils;
import com.alexander.mutantmore.advanced_animation_utils.armour_utils.AdvancedArmourLayer;
import com.alexander.mutantmore.advanced_animation_utils.armour_utils.AdvancedArmourWearingModel;
import com.alexander.mutantmore.animation.keyframe_animations.definition.MutantFrozenZombieKeyframeAnimations;
import com.alexander.mutantmore.animation.sine_wave_animations.SineWaveAnimationUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.client.render.model.animation.mm.MutantFrozenZombieServantKeyframeAnimations;
import com.qiuyue.goetyominous.client.render.model.animation.mm.MutantFrozenZombieSineWaveAnimations;
import com.qiuyue.goetyominous.common.entities.ally.mm.MutantFrozenZombieServant;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MutantFrozenZombieServantModel<T extends MutantFrozenZombieServant> extends HierarchicalModel<T> implements ArmedModel, AdvancedArmourWearingModel {
    private final ModelPart root;
    public final ModelPart everything;
    public final ModelPart pelvis;
    public final ModelPart body;
    public final ModelPart head;
    public final ModelPart leftArm;
    public final ModelPart leftArmLower;
    public final ModelPart rightArm;
    public final ModelPart rightArmLower;
    public final ModelPart projectile;
    public final ModelPart snowBlock;
    public final ModelPart rightLeg;
    public final ModelPart rightLegLower;
    public final ModelPart leftLeg;
    public final ModelPart leftLegLower;

    public MutantFrozenZombieServantModel(ModelPart root) {
        super(RenderType::entityTranslucent);
        this.root = root;
        this.everything = root.getChild("everything");
        this.pelvis = this.everything.getChild("pelvis");
        this.body = this.pelvis.getChild("body");
        this.head = this.body.getChild("head");
        this.leftArm = this.body.getChild("leftArm");
        this.leftArmLower = this.leftArm.getChild("leftArmLower");
        this.rightArm = this.body.getChild("rightArm");
        this.rightArmLower = this.rightArm.getChild("rightArmLower");
        this.projectile = this.rightArmLower.getChild("projectile");
        this.leftLeg = this.everything.getChild("leftLeg");
        this.leftLegLower = this.leftLeg.getChild("leftLegLower");
        this.rightLeg = this.everything.getChild("rightLeg");
        this.rightLegLower = this.rightLeg.getChild("rightLegLower");
        this.snowBlock = this.everything.getChild("snowBlock");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition everything = partdefinition.addOrReplaceChild("everything", CubeListBuilder.create(), PartPose.offset(0.0F, 9.0F, 2.0F));
        PartDefinition pelvis = everything.addOrReplaceChild("pelvis", CubeListBuilder.create().texOffs(0, 44).addBox(-7.0F, -14.0F, -6.0F, 14.0F, 16.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, 0.0F));
        PartDefinition body = pelvis.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 16).addBox(-12.0F, -12.0F, -8.0F, 24.0F, 12.0F, 16.0F, new CubeDeformation(0.0F)).texOffs(0, 86).addBox(-12.0F, -12.0F, -8.0F, 24.0F, 10.0F, 16.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, -10.0F, 0.0F));
        PartDefinition iceSpikes = body.addOrReplaceChild("iceSpikes", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        iceSpikes.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 94).mirror().addBox(5.0F, -23.0F, -3.0F, 0.0F, 16.0F, 18.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, -2.0F, -1.0F, -0.2973F, -0.1308F, 0.3138F));
        iceSpikes.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 94).addBox(0.0F, -23.0F, -1.0F, 0.0F, 16.0F, 18.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, -1.0F, -0.3054F, 0.0F, 0.0F));
        iceSpikes.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 94).addBox(-5.0F, -23.0F, -3.0F, 0.0F, 16.0F, 18.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.0F, -1.0F, -0.2973F, 0.1308F, -0.3138F));
        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -7.6037F, -7.5837F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)).texOffs(32, 0).addBox(-4.0F, -7.6F, -7.6F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, -9.0F, -4.0F));
        PartDefinition rightArm = body.addOrReplaceChild("rightArm", CubeListBuilder.create().texOffs(104, 0).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 16.0F, 6.0F, new CubeDeformation(0.0F)).texOffs(54, 46).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 7.0F, 6.0F, new CubeDeformation(0.25F)), PartPose.offset(-13.0F, -6.0F, 1.0F));
        PartDefinition rightArmLower = rightArm.addOrReplaceChild("rightArmLower", CubeListBuilder.create().texOffs(104, 22).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 16.0F, 6.0F, new CubeDeformation(0.01F)), PartPose.offset(0.0F, 13.0F, 0.0F));
        PartDefinition projectile = rightArmLower.addOrReplaceChild("projectile", CubeListBuilder.create().texOffs(0, 29).addBox(0.0F, -6.5F, -23.0F, 0.0F, 13.0F, 43.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 16.5F, 1.0F));
        projectile.addOrReplaceChild("projectiles_r1", CubeListBuilder.create().texOffs(0, 29).addBox(0.0F, -6.5F, -1.0F, 0.0F, 13.0F, 43.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -22.0F, 0.0F, 0.0F, 1.5708F));
        PartDefinition leftArm = body.addOrReplaceChild("leftArm", CubeListBuilder.create().texOffs(104, 0).mirror().addBox(-3.0F, -3.0F, -3.0F, 6.0F, 16.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false).texOffs(54, 46).mirror().addBox(-3.0F, -3.0F, -3.0F, 6.0F, 7.0F, 6.0F, new CubeDeformation(0.25F)).mirror(false), PartPose.offset(12.0F, -3.0F, 0.0F));
        PartDefinition leftArmLower = leftArm.addOrReplaceChild("leftArmLower", CubeListBuilder.create().texOffs(104, 22).mirror().addBox(-3.0F, 0.0F, -3.0F, 6.0F, 16.0F, 6.0F, new CubeDeformation(0.01F)).mirror(false), PartPose.offset(0.0F, 13.0F, 0.0F));
        PartDefinition rightLeg = everything.addOrReplaceChild("rightLeg", CubeListBuilder.create().texOffs(80, 0).addBox(-3.0F, -0.5F, -3.0F, 6.0F, 11.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 0.0F, 1.0F));
        rightLeg.addOrReplaceChild("rightLegLower", CubeListBuilder.create().texOffs(80, 17).addBox(-3.0F, -1.0F, -3.0F, 6.0F, 8.0F, 6.0F, new CubeDeformation(0.1F)).texOffs(104, 114).addBox(-3.0F, -1.0F, -3.0F, 6.0F, 8.0F, 6.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, 10.0F, 0.0F));
        PartDefinition leftLeg = everything.addOrReplaceChild("leftLeg", CubeListBuilder.create().texOffs(80, 0).addBox(-3.0F, -0.5F, -3.0F, 6.0F, 11.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 0.0F, 1.0F));
        leftLeg.addOrReplaceChild("leftLegLower", CubeListBuilder.create().texOffs(80, 17).addBox(-3.0F, -1.0F, -3.0F, 6.0F, 8.0F, 6.0F, new CubeDeformation(0.1F)).texOffs(104, 114).mirror().addBox(-3.0F, -1.0F, -3.0F, 6.0F, 8.0F, 6.0F, new CubeDeformation(0.25F)).mirror(false), PartPose.offset(0.0F, 10.0F, 0.0F));
        everything.addOrReplaceChild("snowBlock", CubeListBuilder.create().texOffs(128, 0).addBox(-20.0F, -20.0F, -20.0F, 40.0F, 40.0F, 40.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.0F, -2.0F));
        return LayerDefinition.create(meshdefinition, 288, 128);
    }

    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        float deltaTicks;
        if (entity.walkPhaseAge < 0.0F) {
            deltaTicks = 1.0F;
        } else {
            deltaTicks = Mth.clamp(ageInTicks - entity.walkPhaseAge, 0.0F, 2.0F);
        }
        entity.walkPhaseAge = ageInTicks;
        double xDiff = entity.getX() - entity.xo;
        double zDiff = entity.getZ() - entity.zo;
        float walkSpeedFactor = Math.min((float) Math.sqrt(xDiff * xDiff + zDiff * zDiff) * 8.64F, 6.0F);
        entity.walkPhase += walkSpeedFactor * deltaTicks;
        float walkTick = entity.walkPhase * ((float) Math.PI / 180.0F) / 20.0F;
        boolean movingOnLand = entity.onGround() && !entity.isInWaterOrBubble() && limbSwingAmount > 0.005F;
        boolean keyframeActive = !entity.notCurrentlyPlayingKeyframeAnimation();
        boolean shouldPlayJumpingAnimation = entity.jumping && !keyframeActive;
        boolean shouldPlayWalkAnimation = movingOnLand && !shouldPlayJumpingAnimation && !keyframeActive;
        boolean shouldPlayIdleAnimation = !shouldPlayWalkAnimation && !shouldPlayJumpingAnimation && !keyframeActive;
        float blendSpeed = 0.12F;
        entity.jumpingAnimationAmount = SineWaveAnimationUtils.tickAmountMultiplierChange(entity.jumpingAnimationAmount, shouldPlayJumpingAnimation, deltaTicks * blendSpeed);
        entity.walkAnimationAmount = SineWaveAnimationUtils.tickAmountMultiplierChange(entity.walkAnimationAmount, shouldPlayWalkAnimation, deltaTicks * blendSpeed);
        entity.idleAnimationAmount = SineWaveAnimationUtils.tickAmountMultiplierChange(entity.idleAnimationAmount, shouldPlayIdleAnimation, deltaTicks * blendSpeed);
        entity.keyframeAnimationAmount = SineWaveAnimationUtils.tickAmountMultiplierChange(entity.keyframeAnimationAmount, keyframeActive, deltaTicks * 0.2F);
        this.animateHeadLookTarget(netHeadYaw, headPitch);
        float sineTick = SineWaveAnimationUtils.getTick(entity.tickCount, true);
        MutantFrozenZombieSineWaveAnimations.mutantFrozenZombieJumpingAnimation(this, sineTick, 1.0F, entity.jumpingAnimationAmount);
        MutantFrozenZombieSineWaveAnimations.mutantFrozenZombieWalkAnimation(this, walkTick, 1.0F, entity.walkAnimationAmount);
        MutantFrozenZombieSineWaveAnimations.mutantFrozenZombieIdleAnimation(this, sineTick, 1.0F, entity.idleAnimationAmount);
        List<ModelPart> parts = this.root().getAllParts().collect(Collectors.toList());
        float keyframeAnimationAmount = entity.keyframeAnimationAmount;
        boolean needBlend = keyframeAnimationAmount > 0.0F || keyframeActive;
        float[][] pre = null;
        if (needBlend) {
            if (entity.heldPose == null || entity.heldPose.length != parts.size()) {
                entity.heldPose = new float[parts.size()][6];
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
            this.animate(entity.downAnimationState, MutantFrozenZombieServantKeyframeAnimations.MUTANT_FROZEN_ZOMBIE_DOWN(entity), ageInTicks);
        } else if (entity.roarAnimationTick > 0) {
            this.animate(entity.roarAnimationState, MutantFrozenZombieKeyframeAnimations.MUTANT_FROZEN_ZOMBIE_ROAR, ageInTicks);
        } else if (entity.snowThrowAnimationTick > 0) {
            this.animate(entity.snowThrowAnimationState, MutantFrozenZombieKeyframeAnimations.MUTANT_FROZEN_ZOMBIE_SNOW_THROW, ageInTicks);
        } else if (entity.slamAnimationTick > 0) {
            this.animate(entity.slamAnimationState, MutantFrozenZombieKeyframeAnimations.MUTANT_FROZEN_ZOMBIE_SLAM, ageInTicks);
        } else if (entity.jumpAnimationTick > 0) {
            this.animate(entity.jumpAnimationState, MutantFrozenZombieKeyframeAnimations.MUTANT_FROZEN_ZOMBIE_JUMP, ageInTicks);
        } else if (entity.iciclesAnimationTick > 0) {
            this.animate(entity.iciclesAnimationState, MutantFrozenZombieServantKeyframeAnimations.MUTANT_FROZEN_ZOMBIE_ICICLES(entity), ageInTicks);
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
                entity.heldPose[i][0] = part.xRot;
                entity.heldPose[i][1] = part.yRot;
                entity.heldPose[i][2] = part.zRot;
                entity.heldPose[i][3] = part.x;
                entity.heldPose[i][4] = part.y;
                entity.heldPose[i][5] = part.z;
            }
        }
        if (needBlend) {
            for (int i = 0; i < parts.size(); i++) {
                ModelPart part = parts.get(i);
                part.xRot = pre[i][0] + keyframeAnimationAmount * (entity.heldPose[i][0] - pre[i][0]);
                part.yRot = pre[i][1] + keyframeAnimationAmount * (entity.heldPose[i][1] - pre[i][1]);
                part.zRot = pre[i][2] + keyframeAnimationAmount * (entity.heldPose[i][2] - pre[i][2]);
                part.x = pre[i][3] + keyframeAnimationAmount * (entity.heldPose[i][3] - pre[i][3]);
                part.y = pre[i][4] + keyframeAnimationAmount * (entity.heldPose[i][4] - pre[i][4]);
                part.z = pre[i][5] + keyframeAnimationAmount * (entity.heldPose[i][5] - pre[i][5]);
            }
        }
        if (entity.throwAnimationTick <= 0) {
            this.projectile.xScale = 0.0F;
            this.projectile.yScale = 0.0F;
            this.projectile.zScale = 0.0F;
        }
        if (entity.snowThrowAnimationTick <= 0) {
            this.snowBlock.xScale = 0.0F;
            this.snowBlock.yScale = 0.0F;
            this.snowBlock.zScale = 0.0F;
        }
    }

    private void animateHeadLookTarget(float yRot, float xRot) {
        this.head.xRot = xRot * ((float) Math.PI / 180);
        this.head.yRot = yRot * ((float) Math.PI / 180);
    }

    public ModelPart root() {
        return this.root;
    }

    public void translateToHand(HumanoidArm arm, PoseStack stack) {
        if (arm == HumanoidArm.RIGHT) {
            AdvancedAnimationUtils.translateAndRotateForAllParts(stack, AdvancedAnimationUtils.allParts(this, new String[]{"root", "everything", "pelvis", "body", "rightArm", "rightArmLower"}));
            stack.translate(0.0625F, 0.25F, -0.0625F);
        } else {
            AdvancedAnimationUtils.translateAndRotateForAllParts(stack, AdvancedAnimationUtils.allParts(this, new String[]{"root", "everything", "pelvis", "body", "leftArm", "leftArmLower"}));
            stack.translate(-0.0625F, 0.25F, -0.0625F);
        }
    }

    public void translateArmour(AdvancedArmourLayer.ArmourModelPart modelPart, PoseStack stack, boolean innerModel) {
        switch (modelPart) {
            case HEAD:
                AdvancedAnimationUtils.translateAndRotateForAllParts(stack, AdvancedAnimationUtils.allParts(this, new String[]{"root", "everything", "pelvis", "body", "head"}));
                stack.translate(0.0, 0.0, -0.21875);
                break;
            case BODY:
                AdvancedAnimationUtils.translateAndRotateForAllParts(stack, AdvancedAnimationUtils.allParts(this, new String[]{"root", "everything", "pelvis"}));
                if (innerModel) {
                    stack.translate(0.0F, -2.125F, 0.0F);
                    stack.scale(1.8F, 2.9F, 2.4F);
                    break;
                }
                this.body.translateAndRotate(stack);
                stack.translate(0.0F, -0.6875F, 0.0F);
                stack.scale(2.5F, 1.2F, 2.75F);
                break;
            case RIGHT_ARM:
                AdvancedAnimationUtils.translateAndRotateForAllParts(stack, AdvancedAnimationUtils.allParts(this, new String[]{"root", "everything", "pelvis", "body", "rightArm"}));
                stack.translate(0.4375F, -0.125F, 0.0F);
                stack.scale(1.2F, 1.6F, 1.2F);
                break;
            case LEFT_ARM:
                AdvancedAnimationUtils.translateAndRotateForAllParts(stack, AdvancedAnimationUtils.allParts(this, new String[]{"root", "everything", "pelvis", "body", "leftArm"}));
                stack.translate(-0.4375F, -0.125F, 0.0F);
                stack.scale(1.2F, 1.6F, 1.2F);
                break;
            case RIGHT_LEG:
                AdvancedAnimationUtils.translateAndRotateForAllParts(stack, AdvancedAnimationUtils.allParts(this, new String[]{"root", "everything", "rightLeg"}));
                if (innerModel) {
                    stack.translate(0.1875F, -1.0625F, 0.03125F);
                    stack.scale(1.5F, 1.3F, 1.5F);
                    break;
                }
                this.rightLegLower.translateAndRotate(stack);
                stack.translate(0.15625F, -1.875F, 0.0F);
                stack.scale(1.25F, 1.5F, 1.25F);
                break;
            case LEFT_LEG:
                AdvancedAnimationUtils.translateAndRotateForAllParts(stack, AdvancedAnimationUtils.allParts(this, new String[]{"root", "everything", "leftLeg"}));
                if (innerModel) {
                    stack.translate(-0.1875F, -1.0625F, 0.03125F);
                    stack.scale(1.5F, 1.3F, 1.5F);
                    break;
                }
                this.leftLegLower.translateAndRotate(stack);
                stack.translate(-0.15625F, -1.875F, 0.0F);
                stack.scale(1.25F, 1.5F, 1.25F);
        }
    }
}
