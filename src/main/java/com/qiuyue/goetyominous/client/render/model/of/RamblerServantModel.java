package com.qiuyue.goetyominous.client.render.model.of;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.client.render.model.animation.of.RamblerServantAnimations;
import com.qiuyue.goetyominous.common.entities.ally.of.RamblerServant;
import com.unusualmodding.opposing_force.client.models.entity.RamblerModel;
import com.unusualmodding.opposing_force.client.models.entity.base.OPModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class RamblerServantModel extends OPModel<RamblerServant> {
    private final ModelPart root;
    private final ModelPart body_main;
    private final ModelPart roll_control;
    private final ModelPart body;
    private final ModelPart crown;
    public final ModelPart middle_skull;
    public final ModelPart left_skull;
    public final ModelPart right_skull;
    private final ModelPart left_front_top_arm;
    private final ModelPart left_front_bottom_arm;
    private final ModelPart left_back_top_arm;
    private final ModelPart left_back_bottom_arm;
    private final ModelPart right_front_top_arm;
    private final ModelPart right_front_bottom_arm;
    private final ModelPart right_back_top_arm;
    private final ModelPart right_back_bottom_arm;
    private final ModelPart leg_control;
    private final ModelPart left_front_leg;
    private final ModelPart right_front_leg;
    private final ModelPart left_back_leg;
    private final ModelPart right_back_leg;

    public RamblerServantModel(ModelPart root) {
        this.root = root.getChild("root");
        this.body_main = this.root.getChild("body_main");
        this.roll_control = this.body_main.getChild("roll_control");
        this.body = this.roll_control.getChild("body");
        this.crown = this.body.getChild("crown");
        this.middle_skull = this.body.getChild("middle_skull");
        this.left_skull = this.body.getChild("left_skull");
        this.right_skull = this.body.getChild("right_skull");
        this.left_front_top_arm = this.body.getChild("left_front_top_arm");
        this.left_front_bottom_arm = this.body.getChild("left_front_bottom_arm");
        this.left_back_top_arm = this.body.getChild("left_back_top_arm");
        this.left_back_bottom_arm = this.body.getChild("left_back_bottom_arm");
        this.right_front_top_arm = this.body.getChild("right_front_top_arm");
        this.right_front_bottom_arm = this.body.getChild("right_front_bottom_arm");
        this.right_back_top_arm = this.body.getChild("right_back_top_arm");
        this.right_back_bottom_arm = this.body.getChild("right_back_bottom_arm");
        this.leg_control = this.roll_control.getChild("leg_control");
        this.left_front_leg = this.leg_control.getChild("left_front_leg");
        this.right_front_leg = this.leg_control.getChild("right_front_leg");
        this.left_back_leg = this.leg_control.getChild("left_back_leg");
        this.right_back_leg = this.leg_control.getChild("right_back_leg");
    }

    public static LayerDefinition createBodyLayer() {
        return RamblerModel.createBodyLayer();
    }

    public void setupAnim(RamblerServant entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.animateWalk(RamblerServantAnimations.WALK, limbSwing, limbSwingAmount, 3.1F, 6.2F);
        this.animateIdle(entity.idleAnimationState, RamblerServantAnimations.IDLE, ageInTicks, 1.0F, limbSwingAmount * 4.0F);
        this.animate(entity.recoverAnimationState, RamblerServantAnimations.RECOVER_BLEND, ageInTicks);
        this.animate(entity.flailStartAnimationState, RamblerServantAnimations.FLAIL_START_BLEND, ageInTicks);
        this.animate(entity.flailAnimationState, RamblerServantAnimations.FLAIL_BLEND, ageInTicks);
        this.animate(entity.flailEndAnimationState, RamblerServantAnimations.FLAIL_END_BLEND, ageInTicks);
        this.animate(entity.jab1AnimationState, RamblerServantAnimations.JAB_BLEND1, ageInTicks);
        this.animate(entity.jab2AnimationState, RamblerServantAnimations.JAB_BLEND2, ageInTicks);
        this.animate(entity.jab3AnimationState, RamblerServantAnimations.JAB_BLEND3, ageInTicks);
        this.animate(entity.jab4AnimationState, RamblerServantAnimations.JAB_BLEND4, ageInTicks);
        this.animate(entity.jabRushAnimationState, RamblerServantAnimations.JAB_RUSH_BLEND, ageInTicks);
    }

    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public @NotNull ModelPart root() {
        return this.root;
    }
}
