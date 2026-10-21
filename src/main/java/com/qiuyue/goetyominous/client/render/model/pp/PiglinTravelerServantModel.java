package com.qiuyue.goetyominous.client.render.model.pp;

import com.qiuyue.goetyominous.common.entities.ally.pp.PiglinTravelerServant;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.PiglinModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.piglin.PiglinArmPose;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PiglinTravelerServantModel<T extends PiglinTravelerServant> extends PiglinModel<T> {

    public final ModelPart jacket;
    public final ModelPart hatBrim;

    public PiglinTravelerServantModel(ModelPart root) {
        super(root);
        this.jacket = this.body.getChild("jacket");
        this.hatBrim = this.head.getChild("hat_brim");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = PiglinModel.createMesh(CubeDeformation.NONE);
        PartDefinition root = mesh.getRoot();
        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16)
                .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, -24.0F, 0.0F));
        body.addOrReplaceChild("jacket", CubeListBuilder.create().texOffs(56, 16)
                .addBox(-4.0F, 0.0F, -2.0F, 8.0F, 24.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.ZERO);
        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(62, 0)
                .addBox(-5.0F, -8.0F, -4.0F, 10.0F, 8.0F, 8.0F, new CubeDeformation(0.25F)),
                PartPose.offset(0.0F, -24.0F, 0.0F));
        head.addOrReplaceChild("hat_brim", CubeListBuilder.create().texOffs(80, 19)
                .addBox(-8.0F, -5.0F, -8.0F, 16.0F, 1.0F, 16.0F, new CubeDeformation(0.1F)), PartPose.ZERO);
        head.addOrReplaceChild("hat_big", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-5.0F, -8.0F, -4.0F, 10.0F, 8.0F, 8.0F, new CubeDeformation(-0.02F))
                .texOffs(31, 1).addBox(-2.0F, -4.0F, -5.0F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(2, 4).addBox(2.0F, -2.0F, -5.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(2, 0).addBox(-3.0F, -2.0F, -5.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.ZERO);
        return LayerDefinition.create(mesh, 148, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        PiglinArmPose armPose = entity.getArmPose();
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
        } else if (armPose == PiglinArmPose.CROSSBOW_HOLD) {
            AnimationUtils.animateCrossbowHold(this.rightArm, this.leftArm, this.head, !entity.isLeftHanded());
        } else if (armPose == PiglinArmPose.CROSSBOW_CHARGE) {
            AnimationUtils.animateCrossbowCharge(this.rightArm, this.leftArm, entity, !entity.isLeftHanded());
        }
        if (entity.isSitting()) {
            this.rightArm.xRot += -0.62831855F;
            this.leftArm.xRot += -0.62831855F;
            this.rightLeg.xRot = -1.4137167F;
            this.rightLeg.yRot = 0.31415927F;
            this.rightLeg.zRot = 0.07853982F;
            this.leftLeg.xRot = -1.4137167F;
            this.leftLeg.yRot = -0.31415927F;
            this.leftLeg.zRot = -0.07853982F;
        }
        this.hatBrim.visible = entity.getItemBySlot(EquipmentSlot.HEAD).isEmpty();
        this.leftSleeve.copyFrom(this.leftArm);
        this.rightSleeve.copyFrom(this.rightArm);
        if (entity.getKickTicks() > 0) {
            float f = 1.0F - (float) Mth.abs(10 - 2 * entity.getKickTicks()) / 10.0F;
            this.rightLeg.xRot = Mth.lerp(f, this.rightLeg.xRot, -1.4F);
        }
    }
}
