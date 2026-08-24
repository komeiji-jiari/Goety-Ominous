package com.qiuyue.goetyominous.client.render.model.of;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.common.entities.ally.of.DicerServant;
import com.unusualmodding.opposing_force.client.animations.DicerAnimations;
import com.unusualmodding.opposing_force.client.models.entity.DicerModel;
import com.unusualmodding.opposing_force.client.models.entity.base.OPModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

/**
 * Dicer 仆从模型：直接复用原版 DicerModel 的几何（createBodyLayer 是纯几何，与实体类型无关）。
 * 部件查找链与 setupAnim 逻辑照搬原版 DicerModel，动画直接用 OF 的 DicerAnimations。
 */
@OnlyIn(Dist.CLIENT)
public class DicerServantModel extends OPModel<DicerServant> {
    private final ModelPart root;
    private final ModelPart spin_control;
    private final ModelPart body_main;
    private final ModelPart hips;
    private final ModelPart waist;
    private final ModelPart head;
    private final ModelPart visor;
    private final ModelPart chest;
    private final ModelPart left_arm_joint;
    private final ModelPart left_arm;
    private final ModelPart left_finger1;
    private final ModelPart left_finger2;
    private final ModelPart right_arm_joint;
    private final ModelPart right_arm;
    private final ModelPart right_finger1;
    private final ModelPart right_finger2;
    private final ModelPart tail1;
    private final ModelPart tail2;
    private final ModelPart tail3;
    private final ModelPart tail4;
    private final ModelPart leg_control;
    private final ModelPart left_leg;
    private final ModelPart right_leg;

    public DicerServantModel(ModelPart root) {
        this.root = root.getChild("root");
        this.spin_control = this.root.getChild("spin_control");
        this.body_main = this.spin_control.getChild("body_main");
        this.hips = this.body_main.getChild("hips");
        this.waist = this.hips.getChild("waist");
        this.head = this.waist.getChild("head");
        this.visor = this.head.getChild("visor");
        this.chest = this.waist.getChild("chest");
        this.left_arm_joint = this.chest.getChild("left_arm_joint");
        this.left_arm = this.left_arm_joint.getChild("left_arm");
        this.left_finger1 = this.left_arm.getChild("left_finger1");
        this.left_finger2 = this.left_arm.getChild("left_finger2");
        this.right_arm_joint = this.chest.getChild("right_arm_joint");
        this.right_arm = this.right_arm_joint.getChild("right_arm");
        this.right_finger1 = this.right_arm.getChild("right_finger1");
        this.right_finger2 = this.right_arm.getChild("right_finger2");
        this.tail1 = this.waist.getChild("tail1");
        this.tail2 = this.tail1.getChild("tail2");
        this.tail3 = this.tail2.getChild("tail3");
        this.tail4 = this.tail3.getChild("tail4");
        this.leg_control = this.body_main.getChild("leg_control");
        this.left_leg = this.leg_control.getChild("left_leg");
        this.right_leg = this.leg_control.getChild("right_leg");
    }

    public static LayerDefinition createBodyLayer() {
        return DicerModel.createBodyLayer();
    }

    public void setupAnim(DicerServant entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        int attackState = entity.getAttackState();
        if (attackState != 2 && attackState != 3 && attackState != 4) {
            if (entity.isRunning()) {
                this.animateWalk(DicerAnimations.RUN, limbSwing, limbSwingAmount, 1.0F, 1.0F);
            } else {
                this.animateWalk(DicerAnimations.WALK, limbSwing, limbSwingAmount, 2.0F, 4.0F);
            }
        }
        this.animateIdle(entity.idleAnimationState, DicerAnimations.IDLE, ageInTicks, 1.0F, limbSwingAmount * 4.0F);
        this.animate(entity.slash1AnimationState, DicerAnimations.SLASH_BLEND2, ageInTicks);
        this.animate(entity.slash2AnimationState, DicerAnimations.SLASH_BLEND1, ageInTicks);
        this.animate(entity.crossSlashAnimationState, DicerAnimations.CROSSSLASH, ageInTicks);
        this.animate(entity.tailSpinAnimationState, DicerAnimations.TAILWHIP, ageInTicks);
        this.animate(entity.laserAnimationState, DicerAnimations.LASER, ageInTicks);
        this.head.xRot += headPitch * 0.017453292F - headPitch * 0.017453292F / 2.0F;
        this.head.yRot += netHeadYaw * 0.017453292F - netHeadYaw * 0.017453292F / 2.0F;
    }

    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public @NotNull ModelPart root() {
        return this.root;
    }
}
