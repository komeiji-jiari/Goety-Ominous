package com.qiuyue.goetyominous.client.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.common.entities.ally.neutral.AbstractDredenEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public class DredenModel<T extends LivingEntity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            new ResourceLocation("goetyominous", "dreden"), "main");

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight,
                               int packedOverlay, float red, float green, float blue, float alpha) {
        this.ghost.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    private final ModelPart ghost;
    private final ModelPart head;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart body;

    public DredenModel(ModelPart root) {
        this.ghost = root.getChild("ghost");
        this.head = this.ghost.getChild("head");
        this.leftArm = this.ghost.getChild("left_arm");
        this.rightArm = this.ghost.getChild("right_arm");
        this.body = this.ghost.getChild("body");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition ghost = mesh.getRoot().addOrReplaceChild("ghost", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        ghost.addOrReplaceChild("head", CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F)
                        .texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F)),
                PartPose.offset(0.0F, -23.0F, -2.0F));

        ghost.addOrReplaceChild("hat_tip", CubeListBuilder.create()
                        .texOffs(42, 60).addBox(-4.0F, -1.5F, 0.0F, 8.0F, 1.0F, 3.0F, new CubeDeformation(0.5F)),
                PartPose.offsetAndRotation(0.0F, -29.5F, 2.0F, -0.4363F, 0.0F, 0.0F));

        ghost.addOrReplaceChild("right_arm", CubeListBuilder.create()
                        .texOffs(12, 18).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F)
                        .texOffs(0, 33).addBox(-2.0F, -3.0F, -2.0F, 4.0F, 14.0F, 4.0F),
                PartPose.offset(-5.0F, -22.0F, 0.0F));

        ghost.addOrReplaceChild("left_arm", CubeListBuilder.create()
                        .texOffs(12, 18).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F)
                        .texOffs(0, 33).addBox(-2.0F, -3.0F, -2.0F, 4.0F, 14.0F, 4.0F),
                PartPose.offset(5.0F, -22.0F, 0.0F));

        ghost.addOrReplaceChild("body", CubeListBuilder.create()
                        .texOffs(20, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F)
                        .texOffs(32, 32).addBox(-5.0F, -1.0F, -3.0F, 10.0F, 20.0F, 6.0F)
                        .texOffs(0, 51).addBox(-5.0F, -3.0F, -2.0F, 10.0F, 7.0F, 6.0F),
                PartPose.offsetAndRotation(0.0F, -24.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                          float netHeadYaw, float headPitch) {
        float f = ageInTicks * 0.0025F;
        this.ghost.y = Mth.sin(f * 40.0F) + 24.0F;
        this.body.xRot = 0.1745F + Mth.cos(limbSwing * 0.6662F) * 0.31F * limbSwingAmount
                + Mth.cos(ageInTicks * 0.09F) * 0.1F + 0.1F;
        this.head.yRot = netHeadYaw * Mth.DEG_TO_RAD;
        this.head.xRot = headPitch * Mth.DEG_TO_RAD;
        if (entity instanceof AbstractDredenEntity dregen) {
            animateArms(this.leftArm, this.rightArm, dregen.isBreathing(), this.attackTime, ageInTicks);
        }
    }

    public static void animateArms(ModelPart leftArm, ModelPart rightArm, boolean spewing,
                                   float attackTime, float ageInTicks) {
        float f = Mth.sin(attackTime * Mth.PI);
        float f1 = Mth.sin((1.0F - (1.0F - attackTime) * (1.0F - attackTime)) * Mth.PI);
        rightArm.zRot = 0.0F;
        leftArm.zRot = 0.0F;
        rightArm.yRot = -(0.1F - f * 0.6F);
        leftArm.yRot = 0.1F - f * 0.6F;
        float f2 = -(spewing ? 0.17F : 0.7854F);
        rightArm.xRot = f2 - f * 1.2F + f1 * 0.4F;
        leftArm.xRot = f2 - f * 1.2F + f1 * 0.4F;
        float bob = Mth.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
        rightArm.xRot += bob;
        leftArm.xRot -= bob;
    }
}
