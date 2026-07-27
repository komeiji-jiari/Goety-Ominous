package com.qiuyue.goetyominus.client.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominus.common.entities.hostile.cultists.Channeller;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChannellerModel<T extends Channeller> extends EntityModel<T> implements ArmedModel, HeadedModel {
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart hat;
    private final ModelPart nose;
    private final ModelPart halo;
    private final ModelPart arms;
    private final ModelPart leg0;
    private final ModelPart leg1;
    private final ModelPart rightArm;
    private final ModelPart leftArm;

    public ChannellerModel(ModelPart root) {
        this.body = root.getChild("body");
        this.head = this.body.getChild("head");
        this.hat = this.head.getChild("hat");
        this.nose = this.head.getChild("nose");
        this.halo = this.head.getChild("halo");
        this.arms = this.body.getChild("arms");
        this.leg0 = this.body.getChild("leg0");
        this.leg1 = this.body.getChild("leg1");
        this.rightArm = this.body.getChild("right_arm");
        this.leftArm = this.body.getChild("left_arm");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create()
                        .texOffs(16, 20).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F)
                        .texOffs(0, 38).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 18.0F, 6.0F, new CubeDeformation(0.5F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        head.addOrReplaceChild("hat", CubeListBuilder.create()
                        .texOffs(32, 0).addBox(-4.0F, -5.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.2F)),
                PartPose.offset(0.0F, -5.0F, 0.0F));

        head.addOrReplaceChild("nose", CubeListBuilder.create()
                        .texOffs(24, 0).addBox(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F),
                PartPose.offset(0.0F, -2.0F, 0.0F));

        PartDefinition halo = head.addOrReplaceChild("halo", CubeListBuilder.create()
                        .texOffs(35, 39).addBox(-5.0F, -11.0F, 4.0F, 1.0F, 8.0F, 1.0F)
                        .texOffs(35, 39).addBox(4.0F, -11.0F, 4.0F, 1.0F, 8.0F, 1.0F)
                        .texOffs(42, 62).addBox(-5.0F, -12.0F, 4.0F, 10.0F, 1.0F, 1.0F)
                        .texOffs(29, 59).addBox(-1.0F, -15.0F, 4.0F, 2.0F, 3.0F, 1.0F)
                        .texOffs(29, 39).addBox(5.0F, -8.0F, 4.0F, 2.0F, 2.0F, 1.0F)
                        .texOffs(29, 39).addBox(-7.0F, -8.0F, 4.0F, 2.0F, 2.0F, 1.0F),
                PartPose.offset(0.0F, 1.0F, -4.0F));

        PartDefinition cube_r1 = halo.addOrReplaceChild("cube_r1", CubeListBuilder.create()
                        .texOffs(29, 39).addBox(9.0F, -9.0F, 4.0F, 6.0F, 2.0F, 1.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.48F));

        PartDefinition cube_r2 = halo.addOrReplaceChild("cube_r2", CubeListBuilder.create()
                        .texOffs(29, 39).addBox(-15.0F, -9.0F, 4.0F, 6.0F, 2.0F, 1.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.48F));

        body.addOrReplaceChild("arms", CubeListBuilder.create()
                        .texOffs(44, 22).addBox(-8.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F)
                        .texOffs(44, 22).addBox(4.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F)
                        .texOffs(40, 38).addBox(-4.0F, 2.0F, -2.0F, 8.0F, 4.0F, 4.0F),
                PartPose.offset(0.0F, 2.0F, 0.0F));

        body.addOrReplaceChild("leg0", CubeListBuilder.create()
                        .texOffs(0, 22).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                PartPose.offset(-2.0F, 12.0F, 0.0F));

        body.addOrReplaceChild("leg1", CubeListBuilder.create()
                        .texOffs(0, 22).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, true),
                PartPose.offset(2.0F, 12.0F, 0.0F));

        body.addOrReplaceChild("right_arm", CubeListBuilder.create()
                        .texOffs(40, 46).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                PartPose.offset(-5.0F, 2.0F, 0.0F));

        body.addOrReplaceChild("left_arm", CubeListBuilder.create()
                        .texOffs(40, 46).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, true),
                PartPose.offset(5.0F, 2.0F, 0.0F));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
        this.head.xRot = headPitch * ((float) Math.PI / 180F);
        this.arms.y = 3.0F;
        this.arms.z = -1.0F;
        this.arms.xRot = -0.75F;
        this.nose.setPos(0.0F, -2.0F, 0.0F);

        float f = 0.01F * (float) (entity.getId() % 10);
        this.nose.xRot = Mth.sin((float) entity.tickCount * f) * 4.5F * ((float) Math.PI / 180F);
        this.nose.yRot = 0.0F;
        this.nose.zRot = Mth.cos((float) entity.tickCount * f) * 2.5F * ((float) Math.PI / 180F);

        if (this.riding) {
            this.rightArm.xRot = (-(float) Math.PI / 5F);
            this.rightArm.yRot = 0.0F;
            this.rightArm.zRot = 0.0F;
            this.leftArm.xRot = (-(float) Math.PI / 5F);
            this.leftArm.yRot = 0.0F;
            this.leftArm.zRot = 0.0F;
            this.leg0.xRot = -1.4137167F;
            this.leg0.yRot = ((float) Math.PI / 10F);
            this.leg0.zRot = 0.07853982F;
            this.leg1.xRot = -1.4137167F;
            this.leg1.yRot = (-(float) Math.PI / 10F);
            this.leg1.zRot = -0.07853982F;
        } else {
            this.rightArm.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 2.0F * limbSwingAmount * 0.5F;
            this.rightArm.yRot = 0.0F;
            this.rightArm.zRot = 0.0F;
            this.leftArm.xRot = Mth.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
            this.leftArm.yRot = 0.0F;
            this.leftArm.zRot = 0.0F;
            this.leg0.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount * 0.5F;
            this.leg0.yRot = 0.0F;
            this.leg0.zRot = 0.0F;
            this.leg1.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount * 0.5F;
            this.leg1.yRot = 0.0F;
            this.leg1.zRot = 0.0F;
        }

        if (entity.isPraying()) {
            this.rightArm.z = 0.0F;
            this.rightArm.x = -5.0F;
            this.leftArm.z = 0.0F;
            this.leftArm.x = 5.0F;
            this.rightArm.xRot = Mth.cos(ageInTicks * 0.6662F) * 0.25F;
            this.leftArm.xRot = Mth.cos(ageInTicks * 0.6662F) * 0.25F;
            this.rightArm.zRot = 2.3561945F;
            this.leftArm.zRot = -2.3561945F;
            this.rightArm.yRot = 0.0F;
            this.leftArm.yRot = 0.0F;
            this.arms.visible = false;
            this.leftArm.visible = true;
            this.rightArm.visible = true;
        } else {
            this.rightArm.xRot = 0;
            this.leftArm.xRot = 0;
            this.arms.visible = true;
            this.leftArm.visible = false;
            this.rightArm.visible = false;
        }
    }

    @Override
    public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
        super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.body.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    private ModelPart getArm(HumanoidArm side) {
        return side == HumanoidArm.LEFT ? this.leftArm : this.rightArm;
    }

    @Override
    public void translateToHand(HumanoidArm side, PoseStack poseStack) {
        this.getArm(side).translateAndRotate(poseStack);
    }

    @Override
    public ModelPart getHead() {
        return this.head;
    }
}
