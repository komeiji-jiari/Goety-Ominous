package com.qiuyue.goetyominous.client.render.model.of;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.common.entities.ally.of.FrowzyServant;
import com.unusualmodding.opposing_force.client.animations.FrowzyAnimations;
import com.unusualmodding.opposing_force.client.models.entity.FrowzyModel;
import com.unusualmodding.opposing_force.client.models.entity.base.OPModel;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class FrowzyServantModel extends OPModel<FrowzyServant> implements ArmedModel, HeadedModel {
    private final ModelPart root;
    private final ModelPart upper_body;
    private final ModelPart head;
    private final ModelPart left_arm;
    private final ModelPart right_arm;
    private final ModelPart left_leg;
    private final ModelPart right_leg;

    public FrowzyServantModel(ModelPart root) {
        this.root = root.getChild("root");
        this.upper_body = this.root.getChild("upper_body");
        this.head = this.upper_body.getChild("head");
        this.left_arm = this.upper_body.getChild("left_arm");
        this.right_arm = this.upper_body.getChild("right_arm");
        this.left_leg = this.root.getChild("left_leg");
        this.right_leg = this.root.getChild("right_leg");
    }

    public static LayerDefinition createBodyLayer() {
        return FrowzyModel.createBodyLayer();
    }

    @Override
    public void setupAnim(FrowzyServant entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.animateWalk(FrowzyAnimations.WALK, limbSwing, limbSwingAmount, 1.0F, 2.0F);
        this.animateIdle(entity.idleAnimationState, FrowzyAnimations.IDLE, ageInTicks, 1.0F, limbSwingAmount * 4.0F);
        this.animate(entity.attack1AnimationState, FrowzyAnimations.ATTACK_IDLE, ageInTicks);
        this.animate(entity.attack2AnimationState, FrowzyAnimations.ATTACK_WALK, ageInTicks);
        this.head.xRot += headPitch * Mth.DEG_TO_RAD - headPitch * Mth.DEG_TO_RAD / 2.0F;
        this.head.yRot += netHeadYaw * Mth.DEG_TO_RAD - netHeadYaw * Mth.DEG_TO_RAD / 2.0F;
    }

    @Override
    public @NotNull ModelPart root() {
        return this.root;
    }

    @Override
    public void translateToHand(@NotNull HumanoidArm arm, @NotNull PoseStack poseStack) {
        this.root.translateAndRotate(poseStack);
        this.upper_body.translateAndRotate(poseStack);
        if (arm == HumanoidArm.RIGHT) {
            this.right_arm.translateAndRotate(poseStack);
        } else {
            this.left_arm.translateAndRotate(poseStack);
        }
        poseStack.translate(0.0F, 0.25F, 0.025F);
    }

    public void translateToHead(PoseStack poseStack) {
        this.root.translateAndRotate(poseStack);
        this.upper_body.translateAndRotate(poseStack);
        this.head.translateAndRotate(poseStack);
        poseStack.translate(0.0F, -0.025F, 0.0F);
    }

    @Override
    public @NotNull ModelPart getHead() {
        return this.head;
    }
}
