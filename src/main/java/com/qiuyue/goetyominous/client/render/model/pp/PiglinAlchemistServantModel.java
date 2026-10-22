package com.qiuyue.goetyominous.client.render.model.pp;

import com.qiuyue.goetyominous.common.entities.ally.pp.PiglinAlchemistServant;
import com.qiuyue.goetyominous.common.entities.ally.pp.ZPiglinAlchemistServant;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.PiglinModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.piglin.PiglinArmPose;
import net.minecraft.world.item.ArmorItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PiglinAlchemistServantModel<T extends PiglinAlchemistServant> extends PiglinModel<T> {

    public final ModelPart bodyForLeggings;
    public final ModelPart bodyForChest;
    public final ModelPart belt;

    public PiglinAlchemistServantModel(ModelPart root) {
        super(root);
        this.bodyForLeggings = this.body.getChild("bodyForLeggings");
        this.bodyForChest = this.body.getChild("bodyForChest");
        this.belt = this.body.getChild("belt");
        this.bodyForLeggings.visible = false;
    }

    public static MeshDefinition createBodyLayer(CubeDeformation beltDeformation, CubeDeformation leggingsDeformation,
                                                CubeDeformation chestDeformation) {
        MeshDefinition mesh = PiglinModel.createMesh(CubeDeformation.NONE);
        PartDefinition root = mesh.getRoot();
        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16)
                .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.ZERO);
        body.addOrReplaceChild("belt", CubeListBuilder.create().texOffs(56, 16)
                .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 24.0F, 4.0F, beltDeformation), PartPose.ZERO);
        body.addOrReplaceChild("bodyForLeggings", CubeListBuilder.create().texOffs(56, 16)
                .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 24.0F, 4.0F, leggingsDeformation), PartPose.ZERO);
        body.addOrReplaceChild("bodyForChest", CubeListBuilder.create().texOffs(56, 16)
                .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 24.0F, 4.0F, chestDeformation), PartPose.ZERO);
        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(62, 0)
                .addBox(-5.0F, -8.0F, -4.0F, 10.0F, 8.0F, 8.0F, new CubeDeformation(0.25F))
                .texOffs(0, 0).addBox(-5.0F, -8.0F, -4.0F, 10.0F, 8.0F, 8.0F, new CubeDeformation(-0.02F))
                .texOffs(31, 1).addBox(-2.0F, -4.0F, -5.0F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(2, 4).addBox(2.0F, -2.0F, -5.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(2, 0).addBox(-3.0F, -2.0F, -5.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.ZERO);
        PartDefinition hat = head.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
        hat.addOrReplaceChild("googles", CubeListBuilder.create().texOffs(42, 0)
                .addBox(-5.0F, -31.0F, -5.0F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(52, 0).addBox(1.0F, -31.0F, -5.0F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.1F)),
                PartPose.offset(0.0F, 24.0F, 0.0F));
        return mesh;
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        PiglinArmPose armPose = entity.getArmPose();
        if (entity instanceof ZPiglinAlchemistServant
                && armPose != PiglinArmPose.ADMIRING_ITEM
                && !entity.isGonnaThrowPotion()
                && !entity.isUsingItem()) {
            AnimationUtils.animateZombieArms(this.leftArm, this.rightArm,
                    entity.isAggressive(), this.attackTime, ageInTicks);
        }
        if (armPose == PiglinArmPose.ADMIRING_ITEM) {
            this.head.xRot = 0.5F;
            this.head.yRot = 0.0F;
            if (entity.isLeftHanded()) {
                this.rightArm.yRot = -0.5F;
                this.rightArm.xRot = -0.9F;
            } else {
                this.leftArm.yRot = 0.5F;
                this.leftArm.xRot = -0.9F;
            }
        }
        boolean wearingChestplateOrLeggings = entity.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof ArmorItem
                || entity.getItemBySlot(EquipmentSlot.LEGS).getItem() instanceof ArmorItem;
        this.bodyForLeggings.visible = entity.getItemBySlot(EquipmentSlot.LEGS).getItem() instanceof ArmorItem
                && !(entity.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof ArmorItem);
        this.bodyForChest.visible = entity.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof ArmorItem;
        this.belt.visible = !wearingChestplateOrLeggings;
        if (entity.isGonnaThrowPotion()) {
            if (entity.isLeftHanded()) {
                this.rightArm.xRot = -1.8F;
            } else {
                this.leftArm.xRot = -1.8F;
            }
        }
        this.leftPants.copyFrom(this.leftLeg);
        this.rightPants.copyFrom(this.rightLeg);
        this.leftSleeve.copyFrom(this.leftArm);
        this.rightSleeve.copyFrom(this.rightArm);
        this.jacket.copyFrom(this.body);
    }
}
