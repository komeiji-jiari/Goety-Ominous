package com.qiuyue.goetyominous.client.render.model;

import com.Polarice3.Goety.common.entities.hostile.BoneLord;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OminousBoneLordModel<T extends BoneLord> extends HierarchicalModel<T> implements ArmedModel {

    private final ModelPart root;
    private final ModelPart boneLord;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;

    public OminousBoneLordModel(ModelPart root) {
        this.root = root;
        this.boneLord = root.getChild("bone_lord");
        this.rightArm = this.boneLord.getChild("right_arm2");
        this.leftArm = this.boneLord.getChild("left_arm2");
        this.rightLeg = this.boneLord.getChild("right_leg2");
        this.leftLeg = this.boneLord.getChild("left_leg2");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bone_lord = partdefinition.addOrReplaceChild("bone_lord", CubeListBuilder.create(),
                PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition body2 = bone_lord.addOrReplaceChild("body2", CubeListBuilder.create()
                .texOffs(0, 27).addBox(-4.0F, -3.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, -21.0F, 0.0F));

        PartDefinition armor = body2.addOrReplaceChild("armor", CubeListBuilder.create(),
                PartPose.offset(0.0F, -3.0F, 0.0F));

        PartDefinition chestplate_top = armor.addOrReplaceChild("chestplate_top", CubeListBuilder.create()
                .texOffs(0, 43).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 7.0F, 4.0F, new CubeDeformation(0.45F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        chestplate_top.addOrReplaceChild("cage5_r1", CubeListBuilder.create()
                .texOffs(38, 106).mirror().addBox(0.0F, -4.5F, -6.5F, 0.0F, 11.0F, 11.0F, new CubeDeformation(0.0F)).mirror(false),
                PartPose.offsetAndRotation(3.9063F, 1.8125F, 7.9063F, -0.1108F, 0.026F, -1.4073F));

        chestplate_top.addOrReplaceChild("cage2_r1", CubeListBuilder.create()
                .texOffs(38, 82).mirror().addBox(0.0F, -6.0F, -4.0F, 0.0F, 10.0F, 12.0F, new CubeDeformation(0.0F)).mirror(false),
                PartPose.offsetAndRotation(3.4375F, 1.0625F, 2.7188F, -0.2413F, 0.9833F, -0.0629F));

        chestplate_top.addOrReplaceChild("cage3_r1", CubeListBuilder.create()
                .texOffs(38, 82).mirror().addBox(0.0F, -6.0F, -4.0F, 0.0F, 10.0F, 12.0F, new CubeDeformation(0.0F)).mirror(false),
                PartPose.offsetAndRotation(2.0625F, 1.5313F, 5.375F, 0.234F, 0.092F, -0.0336F));

        chestplate_top.addOrReplaceChild("cage4_r1", CubeListBuilder.create()
                .texOffs(38, 91).mirror().addBox(0.0F, -6.5F, -6.5F, 0.0F, 13.0F, 13.0F, new CubeDeformation(0.0F)).mirror(false),
                PartPose.offsetAndRotation(5.6563F, -1.4063F, 6.1563F, -0.0356F, 0.2892F, -1.3887F));

        chestplate_top.addOrReplaceChild("cage4_r2", CubeListBuilder.create()
                .texOffs(38, 106).addBox(0.0F, -4.5F, -6.5F, 0.0F, 11.0F, 11.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-3.9063F, 1.8125F, 7.9063F, -0.1108F, -0.026F, 1.4073F));

        chestplate_top.addOrReplaceChild("cage3_r2", CubeListBuilder.create()
                .texOffs(38, 91).addBox(0.0F, -6.5F, -6.5F, 0.0F, 13.0F, 13.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-5.6563F, -1.4063F, 6.1563F, -0.0356F, -0.2892F, 1.3887F));

        chestplate_top.addOrReplaceChild("cage2_r2", CubeListBuilder.create()
                .texOffs(38, 82).addBox(0.0F, -6.0F, -4.0F, 0.0F, 10.0F, 12.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-2.0625F, 1.5313F, 5.375F, 0.234F, -0.092F, 0.0336F));

        chestplate_top.addOrReplaceChild("cage1_r1", CubeListBuilder.create()
                .texOffs(38, 82).addBox(0.0F, -6.0F, -4.0F, 0.0F, 10.0F, 12.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-3.4375F, 1.0625F, 2.7188F, -0.2413F, -0.9833F, 0.0629F));

        chestplate_top.addOrReplaceChild("part2_r1", CubeListBuilder.create()
                .texOffs(0, 54).mirror().addBox(-2.0F, -0.5F, -0.5F, 5.0F, 1.0F, 2.0F, new CubeDeformation(0.25F)).mirror(false),
                PartPose.offsetAndRotation(-2.9375F, 2.4864F, -1.9241F, -0.3432F, 0.045F, 0.258F));

        chestplate_top.addOrReplaceChild("part1_r1", CubeListBuilder.create()
                .texOffs(0, 54).addBox(-3.0F, -0.5F, -0.5F, 5.0F, 1.0F, 2.0F, new CubeDeformation(0.25F)),
                PartPose.offsetAndRotation(2.9375F, 2.5176F, -1.9554F, -0.3432F, -0.045F, -0.258F));

        armor.addOrReplaceChild("chestplate_bottom", CubeListBuilder.create()
                .texOffs(0, 57).addBox(-4.0F, 11.0F, -2.0F, 8.0F, 8.0F, 4.0F, new CubeDeformation(0.55F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition right_arm2 = bone_lord.addOrReplaceChild("right_arm2", CubeListBuilder.create()
                .texOffs(24, 29).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-5.0F, -22.0F, 0.0F));

        PartDefinition armor2 = right_arm2.addOrReplaceChild("armor2", CubeListBuilder.create(),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        armor2.addOrReplaceChild("arm_armor_part1", CubeListBuilder.create()
                .texOffs(40, 29).addBox(-3.0F, -3.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(40, 37).addBox(-2.3F, 0.5938F, -2.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(-0.05F))
                .texOffs(40, 44).addBox(-2.5F, 4.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.5F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition arm_armor_part2 = armor2.addOrReplaceChild("arm_armor_part2", CubeListBuilder.create(),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        arm_armor_part2.addOrReplaceChild("cube_r1", CubeListBuilder.create()
                .texOffs(40, 54).addBox(-1.0F, 0.0F, -2.5F, 5.0F, 1.0F, 5.0F, new CubeDeformation(0.1F)),
                PartPose.offsetAndRotation(-3.0313F, 0.1563F, 0.125F, 0.0F, 0.0F, -0.0873F));

        PartDefinition left_arm2 = bone_lord.addOrReplaceChild("left_arm2", CubeListBuilder.create()
                .texOffs(24, 29).mirror().addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false),
                PartPose.offset(5.0F, -22.0F, 0.0F));

        PartDefinition armor3 = left_arm2.addOrReplaceChild("armor3", CubeListBuilder.create(),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        armor3.addOrReplaceChild("arm_armor_part3", CubeListBuilder.create()
                .texOffs(40, 29).mirror().addBox(-1.0F, -3.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(40, 37).mirror().addBox(-1.7F, 0.5938F, -2.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(-0.05F)).mirror(false)
                .texOffs(40, 44).mirror().addBox(-1.5F, 4.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.5F)).mirror(false),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition arm_armor_part4 = armor3.addOrReplaceChild("arm_armor_part4", CubeListBuilder.create(),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        arm_armor_part4.addOrReplaceChild("cube_r8", CubeListBuilder.create()
                .texOffs(40, 54).mirror().addBox(-4.0F, 0.0F, -2.5F, 5.0F, 1.0F, 5.0F, new CubeDeformation(0.1F)).mirror(false),
                PartPose.offsetAndRotation(3.0313F, 0.1563F, 0.125F, 0.0F, 0.0F, 0.0873F));

        PartDefinition right_leg2 = bone_lord.addOrReplaceChild("right_leg2", CubeListBuilder.create()
                .texOffs(32, 29).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-2.0F, -12.0F, 0.0F));

        right_leg2.addOrReplaceChild("leggings1", CubeListBuilder.create()
                .texOffs(0, 69).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        right_leg2.addOrReplaceChild("boots1", CubeListBuilder.create()
                .texOffs(0, 85).addBox(-2.0F, 8.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.25F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition left_leg2 = bone_lord.addOrReplaceChild("left_leg2", CubeListBuilder.create()
                .texOffs(32, 29).mirror().addBox(3.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false),
                PartPose.offset(-2.0F, -12.0F, 0.0F));

        left_leg2.addOrReplaceChild("leggings2", CubeListBuilder.create()
                .texOffs(0, 69).mirror().addBox(2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        left_leg2.addOrReplaceChild("boots2", CubeListBuilder.create()
                .texOffs(0, 85).mirror().addBox(2.0F, 8.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.25F)).mirror(false),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 128);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                          float netHeadYaw, float headPitch) {
        this.rightArm.z = 0.0F;
        this.rightArm.x = -5.0F;
        this.leftArm.z = 0.0F;
        this.leftArm.x = 5.0F;

        this.rightArm.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * limbSwingAmount;
        this.leftArm.xRot = Mth.cos(limbSwing * 0.6662F) * limbSwingAmount;
        this.rightArm.zRot = 0.0F;
        this.leftArm.zRot = 0.0F;

        this.rightLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.leftLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
        this.rightLeg.yRot = 0.005F;
        this.leftLeg.yRot = -0.005F;
        this.rightLeg.zRot = 0.005F;
        this.leftLeg.zRot = -0.005F;

        this.rightArm.yRot = 0.0F;
        this.leftArm.yRot = 0.0F;

        AnimationUtils.bobModelPart(this.rightArm, ageInTicks, 1.0F);
        AnimationUtils.bobModelPart(this.leftArm, ageInTicks, -1.0F);

        ItemStack mainHand = entity.getMainHandItem();
        if (entity.isAggressive() && (mainHand.isEmpty() || !mainHand.is(Items.BOW))) {
            float f = Mth.sin(this.attackTime * (float) Math.PI);
            float f1 = Mth.sin((1.0F - (1.0F - this.attackTime) * (1.0F - this.attackTime)) * (float) Math.PI);
            this.rightArm.zRot = 0.0F;
            this.leftArm.zRot = 0.0F;
            this.rightArm.yRot = -(0.1F - f * 0.6F);
            this.leftArm.yRot = 0.1F - f * 0.6F;
            this.rightArm.xRot = -1.5707964F;
            this.leftArm.xRot = -1.5707964F;
            this.rightArm.xRot -= f * 1.2F - f1 * 0.4F;
            this.leftArm.xRot -= f * 1.2F - f1 * 0.4F;
            AnimationUtils.bobArms(this.rightArm, this.leftArm, ageInTicks);
        }
    }

    @Override
    public void translateToHand(HumanoidArm arm, PoseStack poseStack) {
        this.boneLord.translateAndRotate(poseStack);
        float f = arm == HumanoidArm.RIGHT ? 1.0F : -1.0F;
        ModelPart part = arm == HumanoidArm.RIGHT ? this.rightArm : this.leftArm;
        part.x += f;
        part.translateAndRotate(poseStack);
        part.x -= f;
    }

    @Override
    public ModelPart root() {
        return this.root;
    }
}
