package com.qiuyue.goetyominous.client.render.model.of;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.common.entities.ally.of.VoltServant;
import com.unusualmodding.opposing_force.client.animations.VoltAnimations;
import com.unusualmodding.opposing_force.client.models.entity.VoltModel;
import com.unusualmodding.opposing_force.client.models.entity.base.OPModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.world.entity.Pose;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

/**
 * Volt 仆从模型：直接复用原版 VoltModel 的几何（纯几何，与实体类型无关）。
 * 部件查找链照搬原版 VoltModel（javap 反汇编确认），动画直接用 OF 的 VoltAnimations。
 */
@OnlyIn(Dist.CLIENT)
public class VoltServantModel extends OPModel<VoltServant> {
    private final ModelPart root;
    private final ModelPart body_main;
    private final ModelPart body;
    private final ModelPart jowls;
    private final ModelPart left_crest;
    private final ModelPart right_crest;
    private final ModelPart jaw;
    private final ModelPart left_wing;
    private final ModelPart right_wing;
    private final ModelPart tail1;
    private final ModelPart tail_2;
    private final ModelPart tail3;
    private final ModelPart tail_tip;

    public VoltServantModel(ModelPart root) {
        this.root = root.getChild("root");
        this.body_main = this.root.getChild("body_main");
        this.body = this.body_main.getChild("body");
        this.jowls = this.body.getChild("jowls");
        this.left_crest = this.body.getChild("left_crest");
        this.right_crest = this.body.getChild("right_crest");
        this.jaw = this.body.getChild("jaw");
        this.left_wing = this.body.getChild("left_wing");
        this.right_wing = this.body.getChild("right_wing");
        this.tail1 = this.body_main.getChild("tail1");
        this.tail_2 = this.tail1.getChild("tail_2");
        this.tail3 = this.tail_2.getChild("tail3");
        this.tail_tip = this.tail3.getChild("tail_tip");
    }

    public static LayerDefinition createBodyLayer() {
        return VoltModel.createBodyLayer(CubeDeformation.NONE);
    }

    public void setupAnim(VoltServant entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        if (entity.getPose() == Pose.STANDING) {
            if (entity.isInWater()) {
                this.animateWalk(VoltAnimations.SWIM, limbSwing, limbSwingAmount, 1.0F, 1.0F);
            } else {
                this.animateWalk(VoltAnimations.WALK, limbSwing, limbSwingAmount, 2.0F, 2.0F);
            }
        }
        this.animateIdle(entity.idleAnimationState, VoltAnimations.IDLE, ageInTicks, 1.0F, limbSwingAmount * 4.0F);
        this.animateIdle(entity.swimIdleAnimationState, VoltAnimations.SWIM, ageInTicks, 1.0F, limbSwingAmount * 4.0F);
        this.animate(entity.shootAnimationState, VoltAnimations.SHOCK_LAND, ageInTicks);
        this.animate(entity.shootWaterAnimationState, VoltAnimations.SHOCK_SWIM, ageInTicks);
        this.animate(entity.jumpAnimationState, VoltAnimations.JUMP_START, ageInTicks);
        this.animate(entity.fallingAnimationState, VoltAnimations.JUMP_FALL, ageInTicks);
        this.animate(entity.landingAnimationState, VoltAnimations.JUMP_END, ageInTicks);
        this.animate(entity.leapAnimationState, VoltAnimations.JUMP_START, ageInTicks);
        // 随机抽搐动画（原版伏特鳐有 1/504、1/505 的抽搐，事件号 68/69）
        this.animate(entity.twitch1AnimationState, VoltAnimations.TWITCH1, ageInTicks);
        this.animate(entity.twitch2AnimationState, VoltAnimations.TWITCH2, ageInTicks);
    }

    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public @NotNull ModelPart root() {
        return this.root;
    }
}
