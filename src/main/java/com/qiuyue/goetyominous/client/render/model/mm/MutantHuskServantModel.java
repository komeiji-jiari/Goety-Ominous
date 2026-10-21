package com.qiuyue.goetyominous.client.render.model.mm;

import com.alexander.mutantmore.advanced_animation_utils.animation_utils.AdvancedAnimationUtils;
import com.alexander.mutantmore.advanced_animation_utils.armour_utils.AdvancedArmourLayer;
import com.alexander.mutantmore.advanced_animation_utils.armour_utils.AdvancedArmourWearingModel;
import com.alexander.mutantmore.animation.keyframe_animations.definition.MutantHuskKeyframeAnimations;
import com.alexander.mutantmore.animation.sine_wave_animations.SineWaveAnimationUtils;
import com.alexander.mutantmore.config.mutant_husk.MutantHuskClientConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.client.render.model.animation.mm.MutantHuskServantKeyframeAnimations;
import com.qiuyue.goetyominous.client.render.model.animation.mm.MutantHuskSineWaveAnimations;
import com.qiuyue.goetyominous.common.entities.ally.mm.MutantHuskServant;
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
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MutantHuskServantModel<T extends MutantHuskServant> extends HierarchicalModel<T> implements ArmedModel, AdvancedArmourWearingModel {
    private final ModelPart root;
    public final ModelPart everything;
    public final ModelPart pelvis;
    public final ModelPart body;
    public final ModelPart head;
    public final ModelPart leftArm;
    public final ModelPart leftArmLower;
    public final ModelPart rightArm;
    public final ModelPart rightArmLower;
    public final ModelPart sandBlock;
    public final ModelPart rightLeg;
    public final ModelPart rightLegLower;
    public final ModelPart leftLeg;
    public final ModelPart leftLegLower;
    public final ModelPart cacti;
    public final ModelPart cactus4;
    public final ModelPart cactus5;

    public MutantHuskServantModel(ModelPart root) {
        this.root = root;
        this.everything = root.getChild("everything");
        this.pelvis = this.everything.getChild("pelvis");
        this.body = this.pelvis.getChild("body");
        this.head = this.body.getChild("head");
        this.leftArm = this.body.getChild("leftArm");
        this.leftArmLower = this.leftArm.getChild("leftArmLower");
        this.rightArm = this.body.getChild("rightArm");
        this.rightArmLower = this.rightArm.getChild("rightArmLower");
        this.sandBlock = this.rightArmLower.getChild("sandBlock");
        this.leftLeg = this.everything.getChild("leftLeg");
        this.leftLegLower = this.leftLeg.getChild("leftLegLower");
        this.rightLeg = this.everything.getChild("rightLeg");
        this.rightLegLower = this.rightLeg.getChild("rightLegLower");
        this.cacti = this.body.getChild("cacti");
        this.cactus5 = this.pelvis.getChild("cactus5");
        this.cactus4 = this.rightArm.getChild("cactus4");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition everything = partdefinition.addOrReplaceChild("everything", CubeListBuilder.create(), PartPose.offset(0.0F, 5.5F, 3.0F));
        PartDefinition pelvis = everything.addOrReplaceChild("pelvis", CubeListBuilder.create().texOffs(0, 44).addBox(-8.0F, -14.0F, -6.0F, 16.0F, 16.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, 0.0F));
        PartDefinition body = pelvis.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 76).addBox(-13.0F, -14.0F, -8.0F, 26.0F, 14.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -11.0F, 0.0F));
        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -5.0F, -7.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.0F, -4.0F));
        PartDefinition leftArm = body.addOrReplaceChild("leftArm", CubeListBuilder.create().texOffs(95, 91).addBox(0.0F, -2.0F, -4.5F, 7.0F, 20.0F, 9.0F, new CubeDeformation(0.01F)), PartPose.offset(11.0F, -9.0F, 1.0F));
        PartDefinition leftArmLower = leftArm.addOrReplaceChild("leftArmLower", CubeListBuilder.create().texOffs(128, 91).addBox(-3.5F, -1.0F, -4.5F, 7.0F, 20.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(3.5F, 18.0F, 0.0F));
        PartDefinition rightArm = body.addOrReplaceChild("rightArm", CubeListBuilder.create().texOffs(95, 91).mirror().addBox(-7.0F, -2.0F, -4.5F, 7.0F, 20.0F, 9.0F, new CubeDeformation(0.01F)).mirror(false), PartPose.offset(-11.0F, -9.0F, 1.0F));
        PartDefinition rightArmLower = rightArm.addOrReplaceChild("rightArmLower", CubeListBuilder.create().texOffs(128, 91).mirror().addBox(-3.5F, -1.0F, -4.5F, 7.0F, 20.0F, 9.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-3.5F, 18.0F, 0.0F));
        rightArmLower.addOrReplaceChild("sandBlock", CubeListBuilder.create().texOffs(151, 2).addBox(-10.0F, -10.0F, -10.0F, 20.0F, 20.0F, 20.0F, new CubeDeformation(0.0F)).texOffs(169, 85).addBox(-10.0F, -10.0F, -10.0F, 20.0F, 20.0F, 20.0F, new CubeDeformation(0.5F)), PartPose.offset(7.5F, 26.5F, 0.0F));
        PartDefinition cactus4 = rightArm.addOrReplaceChild("cactus4", CubeListBuilder.create().texOffs(227, 50).addBox(-3.5F, -13.0F, -3.5F, 7.0F, 14.0F, 7.0F, new CubeDeformation(-1.0F)).texOffs(125, 69).addBox(-3.5F, -13.0F, -3.5F, 7.0F, 14.0F, 7.0F, new CubeDeformation(-0.75F)), PartPose.offsetAndRotation(-4.5F, 8.5F, 1.5F, -0.3288F, 0.121F, -1.3962F));
        cactus4.addOrReplaceChild("cactus_r1", CubeListBuilder.create().texOffs(227, 50).addBox(8.0F, -7.0F, -30.0F, 7.0F, 5.0F, 7.0F, new CubeDeformation(-1.0F)).texOffs(125, 69).addBox(8.0F, -7.0F, -30.0F, 7.0F, 5.0F, 7.0F, new CubeDeformation(-0.75F)), PartPose.offsetAndRotation(0.5F, 19.0F, -11.5F, 0.0F, -1.5708F, -1.5708F));
        PartDefinition cacti = body.addOrReplaceChild("cacti", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition cactus1 = cacti.addOrReplaceChild("cactus1", CubeListBuilder.create().texOffs(169, 43).addBox(-7.0F, -1.5F, -7.0F, 14.0F, 16.0F, 14.0F, new CubeDeformation(-2.0F)).texOffs(169, 43).addBox(-7.0F, 10.5F, -7.0F, 14.0F, 16.0F, 14.0F, new CubeDeformation(-2.0F)).texOffs(169, 43).addBox(-7.0F, 22.5F, -7.0F, 14.0F, 8.0F, 14.0F, new CubeDeformation(-2.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 2.5F, 2.138F, 0.0F, 0.0F));
        PartDefinition cactus2 = cacti.addOrReplaceChild("cactus2", CubeListBuilder.create().texOffs(227, 50).addBox(-3.5F, -15.0F, -3.5F, 7.0F, 16.0F, 7.0F, new CubeDeformation(-1.0F)).texOffs(125, 67).addBox(-3.5F, -15.0F, -3.5F, 7.0F, 16.0F, 7.0F, new CubeDeformation(-0.75F)), PartPose.offsetAndRotation(-9.5F, -11.5F, 5.5F, -0.5888F, -0.1733F, -0.2528F));
        cactus2.addOrReplaceChild("cactus_r2", CubeListBuilder.create().texOffs(227, 50).addBox(-4.0F, -20.0F, -30.0F, 7.0F, 7.0F, 7.0F, new CubeDeformation(-1.0F)).texOffs(125, 67).addBox(-4.0F, -20.0F, -30.0F, 7.0F, 7.0F, 7.0F, new CubeDeformation(-0.75F)), PartPose.offsetAndRotation(0.5F, 19.0F, -11.5F, -1.5708F, 0.0F, 0.0F));
        PartDefinition cactus3 = cacti.addOrReplaceChild("cactus3", CubeListBuilder.create().texOffs(227, 50).addBox(-3.5F, -15.0F, -3.5F, 7.0F, 16.0F, 7.0F, new CubeDeformation(-1.0F)).texOffs(227, 50).addBox(-3.5F, -23.0F, -3.5F, 7.0F, 10.0F, 7.0F, new CubeDeformation(-1.0F)).texOffs(128, 68).addBox(-3.5F, -15.0F, -3.5F, 7.0F, 16.0F, 7.0F, new CubeDeformation(-0.75F)).texOffs(128, 68).addBox(-3.5F, -23.0F, -3.5F, 7.0F, 10.0F, 7.0F, new CubeDeformation(-0.75F)), PartPose.offsetAndRotation(10.5F, -7.5F, 4.5F, -1.0619F, 0.1975F, 0.2898F));
        cactus3.addOrReplaceChild("cactus_r3", CubeListBuilder.create().texOffs(128, 68).addBox(-15.0F, -9.0F, -32.0F, 7.0F, 8.0F, 7.0F, new CubeDeformation(-0.75F)).texOffs(128, 68).addBox(-15.0F, 2.0F, -41.0F, 7.0F, 6.0F, 7.0F, new CubeDeformation(-0.75F)).texOffs(227, 50).addBox(-15.0F, 2.0F, -41.0F, 7.0F, 6.0F, 7.0F, new CubeDeformation(-1.0F)).texOffs(227, 50).addBox(-15.0F, -9.0F, -32.0F, 7.0F, 8.0F, 7.0F, new CubeDeformation(-1.0F)), PartPose.offsetAndRotation(0.5F, 19.0F, -11.5F, 0.0F, 1.5708F, 1.5708F));
        PartDefinition cactus5 = pelvis.addOrReplaceChild("cactus5", CubeListBuilder.create().texOffs(227, 50).addBox(-3.5F, -15.0F, -3.5F, 7.0F, 16.0F, 7.0F, new CubeDeformation(-1.0F)).texOffs(125, 67).addBox(-3.5F, -15.0F, -3.5F, 7.0F, 16.0F, 7.0F, new CubeDeformation(-0.75F)), PartPose.offsetAndRotation(5.5F, -2.5F, 4.5F, -1.1943F, -0.4807F, 2.4307F));
        cactus5.addOrReplaceChild("cactus_r4", CubeListBuilder.create().texOffs(125, 67).addBox(8.0F, -9.0F, -32.0F, 7.0F, 7.0F, 7.0F, new CubeDeformation(-0.75F)).texOffs(227, 50).addBox(8.0F, -9.0F, -32.0F, 7.0F, 7.0F, 7.0F, new CubeDeformation(-1.0F)), PartPose.offsetAndRotation(0.5F, 19.0F, -11.5F, 0.0F, -1.5708F, -1.5708F));
        PartDefinition rightLeg = everything.addOrReplaceChild("rightLeg", CubeListBuilder.create().texOffs(61, 49).mirror().addBox(-3.0F, -2.5F, -3.0F, 7.0F, 13.0F, 7.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-7.0F, 1.0F, -0.5F));
        rightLeg.addOrReplaceChild("rightLegLower", CubeListBuilder.create().texOffs(85, 67).mirror().addBox(-4.0F, -1.0F, -4.0F, 8.0F, 11.0F, 8.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.5F, 9.0F, 0.5F));
        PartDefinition leftLeg = everything.addOrReplaceChild("leftLeg", CubeListBuilder.create().texOffs(61, 49).addBox(-4.0F, -2.5F, -3.0F, 7.0F, 13.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(7.0F, 1.0F, -0.5F));
        leftLeg.addOrReplaceChild("leftLegLower", CubeListBuilder.create().texOffs(85, 67).addBox(-4.0F, -1.0F, -4.0F, 8.0F, 11.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, 9.0F, 0.5F));
        return LayerDefinition.create(meshdefinition, 256, 128);
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
        MutantHuskSineWaveAnimations.mutantHuskJumpingAnimation(this, sineTick, 1.0F, entity.jumpingAnimationAmount);
        MutantHuskSineWaveAnimations.mutantHuskWalkAnimation(this, walkTick, 1.0F, entity.walkAnimationAmount);
        MutantHuskSineWaveAnimations.mutantHuskIdleAnimation(this, sineTick, 1.0F, entity.idleAnimationAmount);
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
            this.animate(entity.downAnimationState, MutantHuskServantKeyframeAnimations.MUTANT_HUSK_DOWN(entity), ageInTicks);
        } else if (entity.roarAnimationTick > 0) {
            this.animate(entity.roarAnimationState, MutantHuskKeyframeAnimations.MUTANT_HUSK_ROAR, ageInTicks);
        } else if (entity.geyserAnimationTick > 0) {
            this.animate(entity.geyserAnimationState, MutantHuskServantKeyframeAnimations.MUTANT_HUSK_GEYSER(entity), ageInTicks);
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
            this.animate(entity.noveltyAnimationState, MutantHuskServantKeyframeAnimations.MUTANT_HUSK_NOVELTY(entity), ageInTicks);
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
        if (entity.throwSandAnimationTick <= 0) {
            this.sandBlock.xScale = 0.0F;
            this.sandBlock.yScale = 0.0F;
            this.sandBlock.zScale = 0.0F;
        }
        if (MutantHuskClientConfig.cacti.get()) {
            this.cacti.visible = true;
            this.cactus4.visible = true;
            this.cactus5.visible = true;
        } else {
            this.cacti.visible = false;
            this.cactus4.visible = false;
            this.cactus5.visible = false;
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
            stack.translate(0.0625F, 0.4375F, -0.125F);
        } else {
            AdvancedAnimationUtils.translateAndRotateForAllParts(stack, AdvancedAnimationUtils.allParts(this, new String[]{"root", "everything", "pelvis", "body", "leftArm", "leftArmLower"}));
            stack.translate(-0.0625F, 0.4375F, -0.125F);
        }
    }

    public void translateArmour(AdvancedArmourLayer.ArmourModelPart modelPart, PoseStack stack, boolean innerModel) {
        switch (modelPart) {
            case HEAD:
                AdvancedAnimationUtils.translateAndRotateForAllParts(stack, AdvancedAnimationUtils.allParts(this, new String[]{"root", "everything", "pelvis", "body", "head"}));
                stack.translate(0.0, 0.1875, -0.1875);
                break;
            case BODY:
                AdvancedAnimationUtils.translateAndRotateForAllParts(stack, AdvancedAnimationUtils.allParts(this, new String[]{"root", "everything", "pelvis"}));
                if (innerModel) {
                    stack.translate(0.0F, -1.4375F, 0.0F);
                    stack.scale(2.0F, 2.0F, 2.5F);
                } else {
                    this.body.translateAndRotate(stack);
                    stack.translate(0.0F, -0.8125F, 0.0F);
                    stack.scale(2.75F, 1.5F, 2.75F);
                }
                break;
            case RIGHT_ARM:
                AdvancedAnimationUtils.translateAndRotateForAllParts(stack, AdvancedAnimationUtils.allParts(this, new String[]{"root", "everything", "pelvis", "body", "rightArm"}));
                stack.translate(0.375F, -0.125F, 0.0F);
                stack.scale(1.6F, 1.6F, 1.6F);
                break;
            case LEFT_ARM:
                AdvancedAnimationUtils.translateAndRotateForAllParts(stack, AdvancedAnimationUtils.allParts(this, new String[]{"root", "everything", "pelvis", "body", "leftArm"}));
                stack.translate(-0.375F, -0.125F, 0.0F);
                stack.scale(1.6F, 1.6F, 1.6F);
                break;
            case RIGHT_LEG:
                AdvancedAnimationUtils.translateAndRotateForAllParts(stack, AdvancedAnimationUtils.allParts(this, new String[]{"root", "everything", "rightLeg"}));
                if (innerModel) {
                    stack.translate(0.21875F, -1.125F, 0.03125F);
                    stack.scale(1.5F, 1.3F, 1.5F);
                } else {
                    this.rightLegLower.translateAndRotate(stack);
                    stack.translate(0.1875F, -2.0625F, 0.0F);
                    stack.scale(1.5F, 1.75F, 1.5F);
                }
                break;
            case LEFT_LEG:
                AdvancedAnimationUtils.translateAndRotateForAllParts(stack, AdvancedAnimationUtils.allParts(this, new String[]{"root", "everything", "leftLeg"}));
                if (innerModel) {
                    stack.translate(-0.21875F, -1.125F, 0.03125F);
                    stack.scale(1.5F, 1.3F, 1.5F);
                } else {
                    this.leftLegLower.translateAndRotate(stack);
                    stack.translate(-0.1875F, -2.0625F, 0.0F);
                    stack.scale(1.5F, 1.75F, 1.5F);
                }
        }
    }
}
