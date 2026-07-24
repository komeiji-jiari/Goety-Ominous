package com.qiuyue.someillagerservants.client.render.model;

import com.Polarice3.Goety.client.render.model.ZPiglinModel;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.piglin.PiglinArmPose;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ZPiglinHunterModel extends ZPiglinModel<Mob> {

    public ZPiglinHunterModel(ModelPart root) {
        super(root);
    }

    @Override
    public void setupAnim(Mob entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        PiglinArmPose pose = PiglinArmPose.DEFAULT;
        if (entity instanceof com.qiuyue.someillagerservants.common.entities.ally.mobs.ZPiglinHunterServant hunter) {
            pose = hunter.getArmPose();
        }

        switch (pose) {
            case CROSSBOW_CHARGE -> {
                AnimationUtils.animateCrossbowCharge(this.rightArm, this.leftArm, entity, true);
            }
            case CROSSBOW_HOLD -> {
                AnimationUtils.animateCrossbowHold(this.rightArm, this.leftArm, this.head, true);
            }
            default -> {
                this.leftArm.xRot = -1.2F;
                this.rightArm.xRot = -1.2F;
                this.leftArm.yRot = 0.0F;
                this.rightArm.yRot = 0.0F;
            }
        }
    }
}