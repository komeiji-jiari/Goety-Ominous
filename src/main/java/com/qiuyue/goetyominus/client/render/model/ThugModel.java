package com.qiuyue.goetyominus.client.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominus.common.entities.hostile.cultists.Thug;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ThugModel<T extends Thug> extends EntityModel<T> {
    private final ModelPart body;
    private final ModelPart bodyChest;
    private final ModelPart rLegParts;
    private final ModelPart rThigh;
    private final ModelPart rLeg;
    private final ModelPart lLegParts;
    private final ModelPart lThigh;
    private final ModelPart lLeg;
    private final ModelPart rArmParts;
    private final ModelPart rUpperArm;
    private final ModelPart rArm;
    private final ModelPart lArmParts;
    private final ModelPart lUpperArm;
    private final ModelPart lArm;
    private final ModelPart head;
    private final ModelPart nose;
    private final ModelPart root;

    public ThugModel(ModelPart root) {
        this.root = root;
        this.body = root.getChild("body");
        this.bodyChest = this.body.getChild("body_chest");
        this.rLegParts = root.getChild("right_leg_parts");
        this.rThigh = this.rLegParts.getChild("right_thigh");
        this.rLeg = this.rLegParts.getChild("right_leg");
        this.lLegParts = root.getChild("left_leg_parts");
        this.lThigh = this.lLegParts.getChild("left_thigh");
        this.lLeg = this.lLegParts.getChild("left_leg");
        this.rArmParts = root.getChild("right_arm_parts");
        this.rUpperArm = this.rArmParts.getChild("right_upper_arm");
        this.rArm = this.rArmParts.getChild("right_arm");
        this.lArmParts = root.getChild("left_arm_parts");
        this.lUpperArm = this.lArmParts.getChild("left_upper_arm");
        this.lArm = this.lArmParts.getChild("left_arm");
        this.head = root.getChild("head");
        this.nose = this.head.getChild("nose");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create()
                        .texOffs(0, 39).addBox(-5.0F, -4.8277F, -1.3765F, 10.0F, 8.0F, 7.0F)
                        .texOffs(22, 55).addBox(-6.0F, 3.1723F, -2.3765F, 12.0F, 5.0F, 9.0F),
                PartPose.offset(0.0F, 1.8277F, -0.3765F));

        body.addOrReplaceChild("body_chest", CubeListBuilder.create()
                        .texOffs(0, 21).addBox(-6.0F, -7.0F, -2.5F, 12.0F, 8.0F, 10.0F),
                PartPose.offsetAndRotation(0.0F, -2.8447F, -0.7471F, 0.2618F, 0.0F, 0.0F));

        PartDefinition rLegParts = root.addOrReplaceChild("right_leg_parts", CubeListBuilder.create(),
                PartPose.offset(-4.0F, 10.0F, 0.0F));
        rLegParts.addOrReplaceChild("right_thigh", CubeListBuilder.create()
                        .texOffs(25, 88).addBox(-4.0F, -4.0F, -2.5F, 6.0F, 6.0F, 7.0F),
                PartPose.offset(1.0F, 4.0F, 1.5F));
        rLegParts.addOrReplaceChild("right_leg", CubeListBuilder.create()
                        .texOffs(0, 82).addBox(-4.0F, -5.0F, -1.5F, 6.0F, 8.0F, 6.0F),
                PartPose.offset(1.0F, 11.0F, 1.5F));

        PartDefinition lLegParts = root.addOrReplaceChild("left_leg_parts", CubeListBuilder.create(),
                PartPose.offset(4.0F, 10.0F, 0.0F));
        lLegParts.addOrReplaceChild("left_thigh", CubeListBuilder.create()
                        .texOffs(25, 88).addBox(-5.0F, -5.5F, -1.5F, 6.0F, 6.0F, 7.0F, true),
                PartPose.offset(2.0F, 5.5F, 0.5F));
        lLegParts.addOrReplaceChild("left_leg", CubeListBuilder.create()
                        .texOffs(0, 82).addBox(-4.0F, -6.0937F, 2.9226F, 6.0F, 8.0F, 6.0F, true),
                PartPose.offset(1.0F, 12.0937F, -2.9226F));

        PartDefinition rArmParts = root.addOrReplaceChild("right_arm_parts", CubeListBuilder.create(),
                PartPose.offset(-6.0F, -6.0F, 0.0F));
        rArmParts.addOrReplaceChild("right_upper_arm", CubeListBuilder.create()
                        .texOffs(27, 71).addBox(-5.0F, -8.0F, -1.5F, 6.0F, 10.0F, 7.0F),
                PartPose.offset(-1.0F, 6.0F, -0.5F));
        rArmParts.addOrReplaceChild("right_arm", CubeListBuilder.create()
                        .texOffs(0, 64).addBox(-5.0F, -6.0F, -1.5F, 6.0F, 10.0F, 7.0F),
                PartPose.offsetAndRotation(-1.0F, 12.1805F, -2.1288F, -0.2618F, 0.0F, 0.0F));

        PartDefinition lArmParts = root.addOrReplaceChild("left_arm_parts", CubeListBuilder.create(),
                PartPose.offset(6.0F, -6.0F, 0.0F));
        lArmParts.addOrReplaceChild("left_upper_arm", CubeListBuilder.create()
                        .texOffs(27, 71).addBox(-5.0F, -7.0F, -1.5F, 6.0F, 10.0F, 7.0F, true),
                PartPose.offset(5.0F, 5.0F, -0.5F));
        lArmParts.addOrReplaceChild("left_arm", CubeListBuilder.create()
                        .texOffs(0, 64).addBox(-2.0F, -7.0F, -2.5F, 6.0F, 10.0F, 7.0F, true),
                PartPose.offsetAndRotation(2.0F, 13.1805F, -1.1288F, -0.2618F, 0.0F, 0.0F));

        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 11.0F, 8.0F)
                        .texOffs(32, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 12.0F, 8.0F, new CubeDeformation(0.5F)),
                PartPose.offset(0.0F, -6.0F, -4.0F));

        head.addOrReplaceChild("nose", CubeListBuilder.create()
                        .texOffs(24, 0).addBox(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F),
                PartPose.offset(0.0F, -2.0F, 0.0F));

        return LayerDefinition.create(mesh, 64, 128);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.root.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
        this.head.xRot = headPitch * ((float) Math.PI / 180F);
        this.animateWalk(entity, limbSwing, limbSwingAmount);
    }

    @Override
    public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
        int i = entity.getAttackTick();
        float f = Math.min(0.5F, 3.0F * limbSwingAmount);
        float f1 = limbSwing * 0.8662F;
        float f2 = Mth.cos(f1);
        float f3 = Mth.sin(f1);
        if (i > 0) {
            this.rArmParts.xRot = -2.0F + 1.5F * Mth.triangleWave((float) i - partialTick, 10.0F);
            this.lArmParts.xRot = -2.0F + 1.5F * Mth.triangleWave((float) i - partialTick, 10.0F);
        } else {
            this.lArmParts.xRot = -(0.8F * f2 * f);
            this.rArmParts.xRot = -(0.8F * f3 * f);
        }
    }

    private void animateWalk(T entity, float limbSwing, float limbSwingAmount) {
        float f = Math.min(0.5F, 3.0F * limbSwingAmount);
        float f1 = limbSwing * 0.8662F;
        float f2 = Mth.cos(f1);
        this.lLegParts.xRot = 1.0F * f2 * f;
        this.rLegParts.xRot = 1.0F * Mth.cos(f1 + (float) Math.PI) * f;
    }
}
