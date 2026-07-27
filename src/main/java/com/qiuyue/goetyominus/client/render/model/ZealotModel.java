package com.qiuyue.goetyominus.client.render.model;

import com.Polarice3.Goety.client.render.layer.HierarchicalArmor;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominus.common.entities.hostile.cultists.Zealot;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.InstrumentItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpyglassItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ZealotModel<T extends Zealot> extends HierarchicalModel<T> implements ArmedModel, HeadedModel, HierarchicalArmor {
    public final ModelPart root;
    public final ModelPart body;
    public final ModelPart clothes;
    public final ModelPart head;
    public final ModelPart hat;
    public final ModelPart collar;
    public final ModelPart arms;
    public final ModelPart RightArm;
    public final ModelPart LeftArm;
    public final ModelPart RightLeg;
    public final ModelPart LeftLeg;
    public HumanoidModel.ArmPose leftArmPose;
    public HumanoidModel.ArmPose rightArmPose;

    public ZealotModel(ModelPart root) {
        this.leftArmPose = ArmPose.EMPTY;
        this.rightArmPose = ArmPose.EMPTY;
        this.root = root;
        this.head = root.getChild("head");
        this.hat = this.head.getChild("hat");
        this.hat.visible = false;
        this.collar = this.head.getChild("collar");
        this.body = root.getChild("body");
        this.clothes = this.body.getChild("clothes");
        this.arms = root.getChild("arms");
        this.RightArm = root.getChild("right_arm");
        this.LeftArm = root.getChild("left_arm");
        this.LeftLeg = root.getChild("left_leg");
        this.RightLeg = root.getChild("right_leg");
    }

    public static MeshDefinition createMesh() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition partdefinition1 = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F), PartPose.offset(0.0F, 0.0F, 0.0F));
        partdefinition1.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 12.0F, 8.0F, new CubeDeformation(0.45F)), PartPose.ZERO);
        partdefinition1.addOrReplaceChild("collar", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.45F)), PartPose.ZERO);
        partdefinition1.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(24, 0).addBox(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F), PartPose.offset(0.0F, -2.0F, 0.0F));
        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 20).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        body.addOrReplaceChild("clothes", CubeListBuilder.create().texOffs(0, 38).addBox(-4.0F, -24.0F, -3.0F, 8.0F, 20.0F, 6.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition partdefinition2 = partdefinition.addOrReplaceChild("arms", CubeListBuilder.create().texOffs(44, 22).addBox(-8.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F).texOffs(40, 38).addBox(-4.0F, 2.0F, -2.0F, 8.0F, 4.0F, 4.0F), PartPose.offsetAndRotation(0.0F, 3.0F, -1.0F, -0.75F, 0.0F, 0.0F));
        partdefinition2.addOrReplaceChild("left_shoulder", CubeListBuilder.create().texOffs(44, 22).mirror().addBox(4.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F), PartPose.ZERO);
        partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 22).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F), PartPose.offset(-2.0F, 12.0F, 0.0F));
        partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 22).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F), PartPose.offset(2.0F, 12.0F, 0.0F));
        partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 46).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F), PartPose.offset(-5.0F, 2.0F, 0.0F));
        partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(40, 46).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F), PartPose.offset(5.0F, 2.0F, 0.0F));
        return meshdefinition;
    }

    public static LayerDefinition createBodyLayer() {
        return LayerDefinition.create(createMesh(), 64, 64);
    }

    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.head.yRot = netHeadYaw * 0.017453292F;
        this.head.xRot = headPitch * 0.017453292F;

        if (this.riding) {
            this.RightArm.xRot = -0.62831855F;
            this.RightArm.yRot = 0.0F;
            this.RightArm.zRot = 0.0F;
            this.LeftArm.xRot = -0.62831855F;
            this.LeftArm.yRot = 0.0F;
            this.LeftArm.zRot = 0.0F;
            this.RightLeg.xRot = -1.4137167F;
            this.RightLeg.yRot = 0.31415927F;
            this.RightLeg.zRot = 0.07853982F;
            this.LeftLeg.xRot = -1.4137167F;
            this.LeftLeg.yRot = -0.31415927F;
            this.LeftLeg.zRot = -0.07853982F;
        } else {
            this.RightArm.xRot = Mth.cos(limbSwing * 0.6662F + 3.1415927F) * 2.0F * limbSwingAmount * 0.5F;
            this.RightArm.yRot = 0.0F;
            this.RightArm.zRot = 0.0F;
            this.LeftArm.xRot = Mth.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
            this.LeftArm.yRot = 0.0F;
            this.LeftArm.zRot = 0.0F;
            this.RightLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount * 0.5F;
            this.RightLeg.yRot = 0.0F;
            this.RightLeg.zRot = 0.0F;
            this.LeftLeg.xRot = Mth.cos(limbSwing * 0.6662F + 3.1415927F) * 1.4F * limbSwingAmount * 0.5F;
            this.LeftLeg.yRot = 0.0F;
            this.LeftLeg.zRot = 0.0F;
        }

        boolean attacking = entity.isAggressive();
        this.arms.visible = false;
        this.LeftArm.visible = true;
        this.RightArm.visible = true;

        if (attacking) {
            if (entity.getMainHandItem().is(Items.CROSSBOW)) {
                if (entity.isChargingCrossbow()) {
                    AnimationUtils.animateCrossbowCharge(this.RightArm, this.LeftArm, entity, true);
                } else {
                    AnimationUtils.animateCrossbowHold(this.RightArm, this.LeftArm, this.head, true);
                }
            } else if (entity.getMainHandItem().is(Items.BOW)) {
                this.RightArm.yRot = -0.1F + this.head.yRot;
                this.RightArm.xRot = -1.5707964F + this.head.xRot;
                this.LeftArm.xRot = -0.9424779F + this.head.xRot;
                this.LeftArm.yRot = this.head.yRot - 0.4F;
                this.LeftArm.zRot = 1.5707964F;
            } else {
                this.RightArm.xRot = -1.5F;
                this.LeftArm.xRot = -1.5F;
            }
        }
    }

    public void renderToBuffer(PoseStack p_102034_, VertexConsumer p_102035_, int p_102036_, int p_102037_, float p_102038_, float p_102039_, float p_102040_, float p_102041_) {
        if (this.young) {
            p_102034_.pushPose();
            float f = 0.75F;
            p_102034_.scale(f, f, f);
            p_102034_.translate(0.0F, 1.0F, 0.0F);
            this.headParts().forEach((p_102081_) -> {
                p_102081_.render(p_102034_, p_102035_, p_102036_, p_102037_, p_102038_, p_102039_, p_102040_, p_102041_);
            });
            p_102034_.popPose();
            p_102034_.pushPose();
            float f1 = 0.5F;
            p_102034_.scale(f1, f1, f1);
            p_102034_.translate(0.0F, 1.5F, 0.0F);
            this.bodyParts().forEach((p_102071_) -> {
                p_102071_.render(p_102034_, p_102035_, p_102036_, p_102037_, p_102038_, p_102039_, p_102040_, p_102041_);
            });
            p_102034_.popPose();
        } else {
            super.renderToBuffer(p_102034_, p_102035_, p_102036_, p_102037_, p_102038_, p_102039_, p_102040_, p_102041_);
        }

    }

    public void copyPropertiesTo(com.Polarice3.Goety.client.render.model.IllagerServantModel<T> p_102873_) {
        super.copyPropertiesTo(p_102873_);
        p_102873_.leftArmPose = this.leftArmPose;
        p_102873_.rightArmPose = this.rightArmPose;
    }

    public void useItemRight(InteractionHand hand, T entityIn) {
        if (entityIn.getUsedItemHand() == hand) {
            if (entityIn.getUseItem().getItem() instanceof SpyglassItem) {
                this.RightArm.xRot = Mth.clamp(this.head.xRot - 1.9198622F - (entityIn.isCrouching() ? 0.2617994F : 0.0F), -2.4F, 3.3F);
                this.RightArm.yRot = this.head.yRot - 0.2617994F;
            } else if (entityIn.getUseItem().getItem() instanceof InstrumentItem) {
                this.RightArm.xRot = Mth.clamp(this.head.xRot, -1.2F, 1.2F) - 1.4835298F;
                this.RightArm.yRot = this.head.yRot - 0.5235988F;
            }
        }

    }

    public void useItemLeft(InteractionHand hand, T entityIn) {
        if (entityIn.getUsedItemHand() == hand) {
            if (entityIn.getUseItem().getItem() instanceof SpyglassItem) {
                this.LeftArm.xRot = Mth.clamp(this.head.xRot - 1.9198622F - (entityIn.isCrouching() ? 0.2617994F : 0.0F), -2.4F, 3.3F);
                this.LeftArm.yRot = this.head.yRot + 0.2617994F;
            } else if (entityIn.getUseItem().getItem() instanceof InstrumentItem) {
                this.LeftArm.xRot = Mth.clamp(this.head.xRot, -1.2F, 1.2F) - 1.4835298F;
                this.LeftArm.yRot = this.head.yRot + 0.5235988F;
            }
        }

    }

    public ModelPart root() {
        return this.root;
    }

    private ModelPart getArm(HumanoidArm p_102923_) {
        return p_102923_ == HumanoidArm.LEFT ? this.LeftArm : this.RightArm;
    }

    public ModelPart getHat() {
        return this.hat;
    }

    public ModelPart getCollar() {
        return this.collar;
    }

    public ModelPart getHead() {
        return this.head;
    }

    protected Iterable<ModelPart> headParts() {
        return ImmutableList.of(this.head);
    }

    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of(this.body, this.RightArm, this.LeftArm, this.arms, this.RightLeg, this.LeftLeg);
    }

    public void translateToHand(HumanoidArm arm, PoseStack poseStack) {
        this.getArm(arm).translateAndRotate(poseStack);
    }

    public void translateToHead(ModelPart modelPart, PoseStack poseStack) {
        modelPart.translateAndRotate(poseStack);
        poseStack.translate(0.0F, 0.0F, 0.0F);
    }

    public void translateToChest(ModelPart modelPart, PoseStack poseStack) {
        modelPart.translateAndRotate(poseStack);
        poseStack.translate(0.0F, 0.0F, 0.0F);
        poseStack.scale(1.05F, 1.05F, 1.05F);
    }

    public void translateToLeg(ModelPart modelPart, PoseStack poseStack) {
        modelPart.translateAndRotate(poseStack);
    }

    public void translateToArms(ModelPart modelPart, PoseStack poseStack) {
        modelPart.translateAndRotate(poseStack);
        poseStack.scale(1.05F, 1.05F, 1.05F);
    }

    public Iterable<ModelPart> rightHandArmors() {
        return ImmutableList.of(this.RightArm);
    }

    public Iterable<ModelPart> leftHandArmors() {
        return ImmutableList.of(this.LeftArm);
    }

    public Iterable<ModelPart> rightLegPartArmors() {
        return ImmutableList.of(this.RightLeg);
    }

    public Iterable<ModelPart> leftLegPartArmors() {
        return ImmutableList.of(this.LeftLeg);
    }

    public Iterable<ModelPart> bodyPartArmors() {
        return ImmutableList.of(this.body);
    }

    public Iterable<ModelPart> headPartArmors() {
        return ImmutableList.of(this.head);
    }
}
