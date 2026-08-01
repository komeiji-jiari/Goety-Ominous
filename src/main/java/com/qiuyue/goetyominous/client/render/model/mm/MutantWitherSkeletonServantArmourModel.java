package com.qiuyue.goetyominous.client.render.model.mm;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MutantWitherSkeletonServantArmourModel<T extends LivingEntity> extends HumanoidModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("mutantmore", "mutant_wither_skeleton_armour"), "main");
    private final ModelPart helmet;
    private final ModelPart chestplate;
    private final ModelPart shirt;
    private final ModelPart leftSleeve;
    private final ModelPart rightSleeve;
    private final ModelPart leftLegging;
    private final ModelPart rightLegging;
    private final ModelPart leftBoot;
    private final ModelPart rightBoot;

    public MutantWitherSkeletonServantArmourModel(ModelPart pRoot) {
        super(pRoot);
        this.helmet = this.head.getChild("armorHead");
        this.chestplate = this.body.getChild("armorBody");
        this.shirt = this.body.getChild("armorLeggingsTop");
        this.leftSleeve = this.leftArm.getChild("armorLeftArm");
        this.rightSleeve = this.rightArm.getChild("armorRightArm");
        this.leftLegging = this.leftLeg.getChild("armorLeftLeg");
        this.rightLegging = this.rightLeg.getChild("armorRightLeg");
        this.leftBoot = this.leftLeg.getChild("armorLeftBoot");
        this.rightBoot = this.rightLeg.getChild("armorRightBoot");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(45, 86).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition armorHead = head.addOrReplaceChild("armorHead", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.55F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        armorHead.addOrReplaceChild("rightHorn_r1", CubeListBuilder.create().texOffs(56, 18).mirror().addBox(-2.5F, -12.0F, 2.0F, 2.0F, 12.0F, 2.0F, new CubeDeformation(0.1F)).mirror(false).texOffs(56, 18).addBox(8.5F, -10.0F, 2.0F, 2.0F, 10.0F, 2.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(-4.0F, -1.0F, 0.5F, 1.0036F, 0.0F, 0.0F));
        armorHead.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -2.8935F, -8.0351F, 8.0F, 3.0F, 8.0F, new CubeDeformation(0.6F)), PartPose.offsetAndRotation(0.0F, 0.0F, 4.0F, 0.2182F, 0.0F, 0.0F));
        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 112).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        body.addOrReplaceChild("armorBody", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.51F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        body.addOrReplaceChild("armorLeggingsTop", CubeListBuilder.create().texOffs(16, 48).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.251F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition right_arm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(112, 112).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 2.0F, 0.0F));
        right_arm.addOrReplaceChild("armorRightArm", CubeListBuilder.create().texOffs(40, 16).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition left_arm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(62, 112).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 2.0F, 0.0F));
        left_arm.addOrReplaceChild("armorLeftArm", CubeListBuilder.create().texOffs(40, 16).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)).mirror(false), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(36, 112).addBox(-1.9F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(1.9F, 12.0F, 0.0F));
        left_leg.addOrReplaceChild("armorLeftLeg", CubeListBuilder.create().texOffs(0, 48).addBox(2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(-3.9F, 0.0F, 0.0F));
        left_leg.addOrReplaceChild("armorLeftBoot", CubeListBuilder.create().texOffs(0, 16).addBox(2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.offset(-3.9F, 0.0F, 0.0F));
        PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(112, 78).addBox(-2.1F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.9F, 12.0F, 0.0F));
        right_leg.addOrReplaceChild("armorRightLeg", CubeListBuilder.create().texOffs(0, 48).addBox(-6.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(3.9F, 0.0F, 0.0F));
        right_leg.addOrReplaceChild("armorRightBoot", CubeListBuilder.create().texOffs(0, 16).addBox(-6.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.offset(3.9F, 0.0F, 0.0F));
        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    public void setVisibility(EquipmentSlot slot) {
        switch (slot) {
            case HEAD:
                this.helmet.visible = true;
                this.chestplate.visible = false;
                this.shirt.visible = false;
                this.leftSleeve.visible = false;
                this.rightSleeve.visible = false;
                this.leftLegging.visible = false;
                this.rightLegging.visible = false;
                this.leftBoot.visible = false;
                this.rightBoot.visible = false;
                break;
            case CHEST:
                this.helmet.visible = false;
                this.chestplate.visible = true;
                this.shirt.visible = true;
                this.leftSleeve.visible = true;
                this.rightSleeve.visible = true;
                this.leftLegging.visible = false;
                this.rightLegging.visible = false;
                this.leftBoot.visible = false;
                this.rightBoot.visible = false;
                break;
            case LEGS:
                this.helmet.visible = false;
                this.chestplate.visible = false;
                this.shirt.visible = false;
                this.leftSleeve.visible = false;
                this.rightSleeve.visible = false;
                this.leftLegging.visible = true;
                this.rightLegging.visible = true;
                this.leftBoot.visible = false;
                this.rightBoot.visible = false;
                break;
            case FEET:
                this.helmet.visible = false;
                this.chestplate.visible = false;
                this.shirt.visible = false;
                this.leftSleeve.visible = false;
                this.rightSleeve.visible = false;
                this.leftLegging.visible = false;
                this.rightLegging.visible = false;
                this.leftBoot.visible = true;
                this.rightBoot.visible = true;
        }

    }
}
