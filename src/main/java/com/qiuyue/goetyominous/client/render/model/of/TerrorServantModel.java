package com.qiuyue.goetyominous.client.render.model.of;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.common.entities.ally.of.TerrorServant;
import com.unusualmodding.opposing_force.client.animations.TerrorAnimations;
import com.unusualmodding.opposing_force.client.models.entity.TerrorModel;
import com.unusualmodding.opposing_force.client.models.entity.base.OPModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class TerrorServantModel extends OPModel<TerrorServant> {
    private final ModelPart root;
    private final ModelPart swim_control;
    private final ModelPart body_main;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart jaw;
    private final ModelPart saw;
    private final ModelPart left_arm;
    private final ModelPart right_arm;
    private final ModelPart dorsal1;
    private final ModelPart tail1;
    private final ModelPart dorsal2;
    private final ModelPart anal;
    private final ModelPart tail2;
    private final ModelPart leg_control;
    private final ModelPart left_leg1;
    private final ModelPart left_leg2;
    private final ModelPart left_leg3;
    private final ModelPart right_leg1;
    private final ModelPart right_leg2;
    private final ModelPart right_leg3;

    public TerrorServantModel(ModelPart root) {
        this.root = root.getChild("root");
        this.swim_control = this.root.getChild("swim_control");
        this.body_main = this.swim_control.getChild("body_main");
        this.body = this.body_main.getChild("body");
        this.head = this.body.getChild("head");
        this.jaw = this.head.getChild("jaw");
        this.saw = this.jaw.getChild("saw");
        this.left_arm = this.body.getChild("left_arm");
        this.right_arm = this.body.getChild("right_arm");
        this.dorsal1 = this.body.getChild("dorsal1");
        this.tail1 = this.body.getChild("tail1");
        this.dorsal2 = this.tail1.getChild("dorsal2");
        this.anal = this.tail1.getChild("anal");
        this.tail2 = this.tail1.getChild("tail2");
        this.leg_control = this.body_main.getChild("leg_control");
        this.left_leg1 = this.leg_control.getChild("left_leg1");
        this.left_leg2 = this.left_leg1.getChild("left_leg2");
        this.left_leg3 = this.left_leg2.getChild("left_leg3");
        this.right_leg1 = this.leg_control.getChild("right_leg1");
        this.right_leg2 = this.right_leg1.getChild("right_leg2");
        this.right_leg3 = this.right_leg2.getChild("right_leg3");
    }

    public static LayerDefinition createBodyLayer() {
        return TerrorModel.createBodyLayer();
    }

    @Override
    public void setupAnim(TerrorServant entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        if (entity.hasLegs() && !entity.isInWater()) {
            if (entity.isRunning()) {
                this.animateWalk(TerrorAnimations.RUN, limbSwing, limbSwingAmount, 1.0F, 2.0F);
            } else {
                this.animateWalk(TerrorAnimations.WALK, limbSwing, limbSwingAmount, 1.0F, 2.0F);
            }
        } else if (entity.isRunning()) {
            this.animateWalk(TerrorAnimations.SWIMFAST, limbSwing, limbSwingAmount, 1.25F, 2.5F);
        } else {
            this.animateWalk(TerrorAnimations.SWIM, limbSwing, limbSwingAmount, 2.0F, 4.0F);
        }
        this.animateIdle(entity.swimIdleAnimationState, TerrorAnimations.SWIM_IDLE, ageInTicks, 1.0F, limbSwingAmount);
        this.animateIdle(entity.idleAnimationState, TerrorAnimations.IDLE, ageInTicks, 1.0F, limbSwingAmount);
        this.animate(entity.flopAnimationState, TerrorAnimations.FLOP, ageInTicks);
        this.animate(entity.growLegsAnimationState, TerrorAnimations.FLOP_END, ageInTicks);
        this.animate(entity.startSawingAnimationState, TerrorAnimations.SAW_ATTACK_START_BLEND, ageInTicks);
        this.animate(entity.sawingAnimationState, TerrorAnimations.SAW_ATTACK_HOLD_BLEND, ageInTicks);
        this.animate(entity.cooldownAnimationState, TerrorAnimations.SAW_ATTACK_COOLDOWN_BLEND, ageInTicks);
        this.animate(entity.retractLegsAnimationState, TerrorAnimations.WATER_RETURN, ageInTicks);
        this.animate(entity.spinSawAnimationState, TerrorAnimations.SAW_IDLE_BLEND, ageInTicks);
        if (entity.isInWaterOrBubble()) {
            this.swim_control.xRot = headPitch * Mth.DEG_TO_RAD;
        }
    }

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public @NotNull ModelPart root() {
        return this.root;
    }
}
