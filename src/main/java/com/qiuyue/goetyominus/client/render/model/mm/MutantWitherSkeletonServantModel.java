package com.qiuyue.goetyominus.client.render.model.mm;

import com.alexander.mutantmore.advanced_animation_utils.animation_utils.AdvancedAnimationUtils;
import com.alexander.mutantmore.advanced_animation_utils.animation_utils.model_animations.AdvancedAnimator;
import com.alexander.mutantmore.advanced_animation_utils.armour_utils.AdvancedArmourLayer;
import com.alexander.mutantmore.advanced_animation_utils.armour_utils.AdvancedArmourWearingModel;
import com.alexander.mutantmore.animation.definitions.mutant_wither_skeleton.MutantWitherSkeletonAggressiveAdvancedAnimations;
import com.alexander.mutantmore.animation.definitions.mutant_wither_skeleton.MutantWitherSkeletonIdleAdvancedAnimations;
import com.alexander.mutantmore.animation.definitions.mutant_wither_skeleton.MutantWitherSkeletonLungeAdvancedAnimations;
import com.alexander.mutantmore.util.PositionUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;

import com.qiuyue.goetyominus.common.entities.ally.mobs.mm.MutantWitherSkeletonServant;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MutantWitherSkeletonServantModel<T extends MutantWitherSkeletonServant> extends HierarchicalModel<T> implements ArmedModel, AdvancedArmourWearingModel {
    public static final ModelLayerLocation MAIN = new ModelLayerLocation(new ResourceLocation("mutantmore", "mutant_wither_skeleton"), "main");
    public static final ModelLayerLocation INNER_ARMOUR = new ModelLayerLocation(new ResourceLocation("mutantmore", "mutant_wither_skeleton"), "inner_armour");
    public static final ModelLayerLocation OUTER_ARMOUR = new ModelLayerLocation(new ResourceLocation("mutantmore", "mutant_wither_skeleton"), "outer_armour");
    private final ModelPart root;
    private final ModelPart head;

    public MutantWitherSkeletonServantModel(ModelPart root) {
        this.root = root;
        this.head = (ModelPart)this.getAnyDescendantWithName("head").get();
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition everything = partdefinition.addOrReplaceChild("everything", CubeListBuilder.create(), PartPose.offset(0.0F, 4.0F, 0.0F));
        PartDefinition pelvis1 = everything.addOrReplaceChild("pelvis1", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, -3.0F, -3.0F, 8.0F, 6.0F, 6.0F, new CubeDeformation(-0.35F)), PartPose.offset(0.0F, -8.0F, 0.0F));
        PartDefinition pelvis2 = pelvis1.addOrReplaceChild("pelvis2", CubeListBuilder.create().texOffs(32, 0).addBox(-2.0F, -6.0F, -2.0F, 5.0F, 8.0F, 4.0F, new CubeDeformation(-0.5F)), PartPose.offset(-0.5F, -2.0F, 0.0F));
        PartDefinition spine1 = pelvis2.addOrReplaceChild("spine1", CubeListBuilder.create().texOffs(50, 0).addBox(-2.5F, -4.0F, -2.0F, 5.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, -5.0F, 0.0F));
        PartDefinition rib13 = spine1.addOrReplaceChild("rib13", CubeListBuilder.create().texOffs(32, 12).mirror().addBox(-6.0F, -1.0F, -1.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-2.5F, -2.0F, 0.5F));
        PartDefinition rib14 = rib13.addOrReplaceChild("rib14", CubeListBuilder.create().texOffs(32, 12).mirror().addBox(-6.0F, -1.02F, -1.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-6.0F, 0.0F, 0.0F));
        rib14.addOrReplaceChild("rib15", CubeListBuilder.create().texOffs(33, 12).mirror().addBox(-5.0F, -1.01F, -1.0F, 5.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-6.0F, 0.0F, 0.0F));
        PartDefinition rib = spine1.addOrReplaceChild("rib", CubeListBuilder.create().texOffs(32, 12).addBox(0.0F, -1.0F, -1.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(2.5F, -2.0F, 0.5F));
        PartDefinition rib2 = rib.addOrReplaceChild("rib2", CubeListBuilder.create().texOffs(32, 12).addBox(0.0F, -1.02F, -1.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(6.0F, 0.0F, 0.0F));
        rib2.addOrReplaceChild("rib3", CubeListBuilder.create().texOffs(33, 12).addBox(0.0F, -1.01F, -1.0F, 5.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(6.0F, 0.0F, 0.0F));
        PartDefinition spine2 = spine1.addOrReplaceChild("spine2", CubeListBuilder.create().texOffs(50, 0).addBox(-2.5F, -4.0F, -2.0F, 5.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.0F, 0.0F));
        PartDefinition rib10 = spine2.addOrReplaceChild("rib10", CubeListBuilder.create().texOffs(32, 12).mirror().addBox(-6.0F, -1.0F, -1.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-2.5F, -2.0F, 0.5F));
        PartDefinition rib11 = rib10.addOrReplaceChild("rib11", CubeListBuilder.create().texOffs(32, 12).mirror().addBox(-6.0F, -1.02F, -1.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-6.0F, 0.0F, 0.0F));
        rib11.addOrReplaceChild("rib12", CubeListBuilder.create().texOffs(34, 12).mirror().addBox(-4.0F, -1.01F, -1.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-6.0F, 0.0F, 0.0F));
        PartDefinition rib4 = spine2.addOrReplaceChild("rib4", CubeListBuilder.create().texOffs(32, 12).addBox(0.0F, -1.0F, -1.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(2.5F, -2.0F, 0.5F));
        PartDefinition rib5 = rib4.addOrReplaceChild("rib5", CubeListBuilder.create().texOffs(32, 12).addBox(0.0F, -1.02F, -1.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(6.0F, 0.0F, 0.0F));
        rib5.addOrReplaceChild("rib6", CubeListBuilder.create().texOffs(32, 12).addBox(0.0F, -1.01F, -1.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(6.0F, 0.0F, 0.0F));
        PartDefinition spine3 = spine2.addOrReplaceChild("spine3", CubeListBuilder.create().texOffs(50, 0).addBox(-2.5F, -4.0F, -2.0F, 5.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.0F, 0.0F));
        spine3.addOrReplaceChild("rightShoulderPad", CubeListBuilder.create().texOffs(65, 42).addBox(-10.0F, -2.5F, -4.0F, 10.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, -5.0F, 0.0F, -0.3155F, 0.2494F, -0.0804F));
        spine3.addOrReplaceChild("leftShoulderPad", CubeListBuilder.create().texOffs(28, 16).addBox(0.2989F, -1.6477F, -2.9377F, 8.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, -5.0F, 0.0F, -0.3155F, -0.2494F, 0.0804F));
        PartDefinition rib16 = spine3.addOrReplaceChild("rib16", CubeListBuilder.create().texOffs(32, 12).mirror().addBox(-6.0F, -1.0F, -1.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-2.5F, -2.0F, 0.5F));
        PartDefinition rib17 = rib16.addOrReplaceChild("rib17", CubeListBuilder.create().texOffs(32, 12).mirror().addBox(-6.0F, -1.02F, -1.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-6.0F, 0.0F, 0.0F));
        rib17.addOrReplaceChild("rib18", CubeListBuilder.create().texOffs(32, 12).mirror().addBox(-6.0F, -1.01F, -1.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-6.0F, 0.0F, 0.0F));
        PartDefinition rib7 = spine3.addOrReplaceChild("rib7", CubeListBuilder.create().texOffs(32, 12).addBox(0.0F, -1.0F, -1.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(2.5F, -2.0F, 0.5F));
        PartDefinition rib8 = rib7.addOrReplaceChild("rib8", CubeListBuilder.create().texOffs(32, 12).addBox(0.0F, -1.02F, -1.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(6.0F, 0.0F, 0.0F));
        rib8.addOrReplaceChild("rib9", CubeListBuilder.create().texOffs(35, 12).addBox(0.0F, -1.01F, -1.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(6.0F, 0.0F, 0.0F));
        PartDefinition neck = spine3.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(68, 0).addBox(-1.5F, -4.0F, -2.0F, 3.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.0F, 0.5F));
        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -5.5F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.0F, 0.0F));
        head.addOrReplaceChild("rightHorn_r1", CubeListBuilder.create().texOffs(62, 21).mirror().addBox(-2.0F, -12.0F, 2.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false).texOffs(62, 21).addBox(8.0F, -10.0F, 2.0F, 2.0F, 10.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, -1.0F, -1.0F, 1.0036F, 0.0F, 0.0F));
        head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(72, 0).addBox(-4.0F, -2.8935F, -8.0351F, 8.0F, 3.0F, 8.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(0.0F, 0.0F, 2.5F, 0.1745F, 0.0F, 0.0F));
        PartDefinition rightArm = spine3.addOrReplaceChild("rightArm", CubeListBuilder.create().texOffs(16, 28).mirror().addBox(-4.0F, 0.0F, -2.0F, 4.0F, 14.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false).texOffs(57, 11).addBox(-5.0F, 11.0F, 1.0F, 6.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, -3.0F, 0.0F));
        PartDefinition rightArmLower = rightArm.addOrReplaceChild("rightArmLower", CubeListBuilder.create().texOffs(32, 28).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 14.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false).texOffs(0, 64).addBox(-2.0F, 6.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(-2.0F, 14.0F, 0.0F));
        PartDefinition rightHand = rightArmLower.addOrReplaceChild("rightHand", CubeListBuilder.create(), PartPose.offset(0.0F, 11.0F, 0.0F));
        PartDefinition leftArm = spine3.addOrReplaceChild("leftArm", CubeListBuilder.create().texOffs(16, 28).addBox(0.0F, 0.0F, -2.0F, 4.0F, 14.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(98, 60).addBox(-1.5F, 10.5F, 1.0F, 7.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, -3.0F, 0.0F));
        PartDefinition leftArmLower = leftArm.addOrReplaceChild("leftArmLower", CubeListBuilder.create().texOffs(32, 28).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 14.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(0, 64).addBox(-2.0F, 6.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(2.0F, 14.0F, 0.0F));
        PartDefinition leftHand = leftArmLower.addOrReplaceChild("leftHand", CubeListBuilder.create(), PartPose.offset(0.0F, 11.0F, 0.0F));
        PartDefinition rightLeg = everything.addOrReplaceChild("rightLeg", CubeListBuilder.create().texOffs(0, 28).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 14.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false).texOffs(57, 11).addBox(-3.0F, 11.0F, -3.0F, 6.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.0F, -8.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        rightLeg.addOrReplaceChild("rightLegLower", CubeListBuilder.create().texOffs(0, 46).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 14.0F, 4.0F, new CubeDeformation(0.01F)).mirror(false).texOffs(68, 59).mirror().addBox(-2.5F, 2.0F, -2.5F, 5.0F, 12.0F, 5.0F, new CubeDeformation(0.1F)).mirror(false), PartPose.offset(0.0F, 14.0F, 0.0F));
        PartDefinition leftLeg = everything.addOrReplaceChild("leftLeg", CubeListBuilder.create().texOffs(0, 28).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 14.0F, 4.0F, new CubeDeformation(0.0F)).texOffs(57, 11).addBox(-3.0F, 11.0F, -3.0F, 6.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, -8.0F, 0.0F, 0.0F, 0.0F, 0.0F));
        leftLeg.addOrReplaceChild("leftLegLower", CubeListBuilder.create().texOffs(0, 46).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 14.0F, 4.0F, new CubeDeformation(0.01F)), PartPose.offset(0.0F, 14.0F, 0.0F));
        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!Minecraft.getInstance().isPaused()) {
            this.root().getAllParts().forEach(ModelPart::resetPose);
            this.animateHeadLookTarget(netHeadYaw, headPitch);
            List<ModelPart> parts = this.root().getAllParts().toList();
            AdvancedAnimationUtils.addWalkModifiers(entity.getAnimation("walk"), entity, 3.0F, 1.0F);
            AdvancedAnimationUtils.addLegHeightOffsetModifier(entity, PositionUtils.getOffsetPos(entity, -0.75, 0.0, 0.0, 0.0F, entity.yBodyRot), 2, "offset_legs", "left_leg_y", 0.2F, -0.75F, 1.5F, 1.2F);
            AdvancedAnimationUtils.addLegHeightOffsetModifier(entity, PositionUtils.getOffsetPos(entity, 0.75, 0.0, 0.0, 0.0F, entity.yBodyRot), 2, "offset_legs", "right_leg_y", 0.2F, -0.75F, 1.5F, 1.2F);
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

    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    private void animateHeadLookTarget(float yRot, float xRot) {
        this.head.xRot = xRot * 0.017453292F;
        this.head.yRot = yRot * 0.017453292F;
    }

    public ModelPart root() {
        return this.root;
    }

    public void translateToHand(HumanoidArm arm, PoseStack stack) {
        if (arm == HumanoidArm.RIGHT) {
            AdvancedAnimationUtils.translateAndRotateForAllParts(stack, AdvancedAnimationUtils.allParts(this, new String[]{"root", "everything", "pelvis1", "pelvis2", "spine1", "spine2", "spine3", "rightArm", "rightArmLower", "rightHand"}));
            stack.translate(0.0625F, -0.5625F, 0.0F);
        } else {
            AdvancedAnimationUtils.translateAndRotateForAllParts(stack, AdvancedAnimationUtils.allParts(this, new String[]{"root", "everything", "pelvis1", "pelvis2", "spine1", "spine2", "spine3", "leftArm", "leftArmLower", "leftHand"}));
            stack.translate(-0.0625F, -0.5625F, 0.0F);
        }

    }

    public void translateArmour(AdvancedArmourLayer.ArmourModelPart modelPart, PoseStack stack, boolean innerModel) {
        switch (modelPart) {
            case HEAD:
                AdvancedAnimationUtils.translateAndRotateForAllParts(stack, AdvancedAnimationUtils.allParts(this, new String[]{"root", "everything", "pelvis1", "pelvis2", "spine1", "spine2", "spine3", "neck", "head"}));
                stack.translate(0.0F, 0.0F, -0.09375F);
                break;
            case BODY:
                if (innerModel) {
                    AdvancedAnimationUtils.translateAndRotateForAllParts(stack, AdvancedAnimationUtils.allParts(this, new String[]{"root", "everything", "pelvis1"}));
                    stack.translate(0.0F, -0.8125F, 0.0F);
                    stack.scale(1.0F, 1.3F, 1.1F);
                } else {
                    AdvancedAnimationUtils.translateAndRotateForAllParts(stack, AdvancedAnimationUtils.allParts(this, new String[]{"root", "everything", "pelvis1", "pelvis2", "spine1", "spine2"}));
                    stack.translate(0.0F, -0.5F, 0.0F);
                    stack.scale(1.0F, 1.2F, 1.5F);
                }
                break;
            case RIGHT_ARM:
                AdvancedAnimationUtils.translateAndRotateForAllParts(stack, AdvancedAnimationUtils.allParts(this, new String[]{"root", "everything", "pelvis1", "pelvis2", "spine1", "spine2", "spine3", "rightShoulderPad"}));
                stack.translate(0.34375F, -0.15625F, 0.0F);
                stack.scale(1.75F, 1.0F, 1.4F);
                break;
            case LEFT_ARM:
                AdvancedAnimationUtils.translateAndRotateForAllParts(stack, AdvancedAnimationUtils.allParts(this, new String[]{"root", "everything", "pelvis1", "pelvis2", "spine1", "spine2", "spine3", "leftShoulderPad"}));
                stack.translate(-0.28125F, -0.0625F, 0.0F);
                stack.scale(1.5F, 1.0F, 1.1F);
                break;
            case RIGHT_LEG:
                if (innerModel) {
                    AdvancedAnimationUtils.translateAndRotateForAllParts(stack, AdvancedAnimationUtils.allParts(this, new String[]{"root", "everything", "rightLeg"}));
                    stack.translate(0.125F, -0.75F, 0.0F);
                } else {
                    AdvancedAnimationUtils.translateAndRotateForAllParts(stack, AdvancedAnimationUtils.allParts(this, new String[]{"root", "everything", "rightLeg", "rightLegLower"}));
                    stack.translate(0.125F, -0.65625F, 0.0F);
                }
                break;
            case LEFT_LEG:
                if (innerModel) {
                    AdvancedAnimationUtils.translateAndRotateForAllParts(stack, AdvancedAnimationUtils.allParts(this, new String[]{"root", "everything", "leftLeg"}));
                    stack.translate(-0.125F, -0.75F, 0.0F);
                } else {
                    AdvancedAnimationUtils.translateAndRotateForAllParts(stack, AdvancedAnimationUtils.allParts(this, new String[]{"root", "everything", "leftLeg", "leftLegLower"}));
                    stack.translate(-0.125F, -0.65625F, 0.0F);
                }
        }

    }
}
