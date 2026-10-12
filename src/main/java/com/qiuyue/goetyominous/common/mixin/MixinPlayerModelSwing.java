package com.qiuyue.goetyominous.common.mixin;

import com.qiuyue.goetyominous.client.ac.BarbaricAxeSwingClient;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerModel.class)
public abstract class MixinPlayerModelSwing {

    @Inject(method = "setupAnim", at = @At("HEAD"), require = 1)
    private void goetyominous$barbaricAxeSuppressVanilla(LivingEntity entity, float limbSwing, float limbSwingAmount,
                                                         float ageInTicks, float netHeadYaw, float headPitch,
                                                         CallbackInfo ci) {
        if (BarbaricAxeSwingClient.isFirstPersonHand()) {
            return;
        }
        if (!BarbaricAxeSwingClient.swinging(entity.getId())) {
            return;
        }
        ((EntityModel<?>) (Object) this).attackTime = 0.0F;
    }

    @Inject(method = "setupAnim", at = @At("RETURN"), require = 1)
    private void goetyominous$barbaricAxeSwing(LivingEntity entity, float limbSwing, float limbSwingAmount,
                                               float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (BarbaricAxeSwingClient.isFirstPersonHand()) {
            return;
        }
        float[] pose = BarbaricAxeSwingClient.thirdPersonPose(entity.getId());
        if (pose == null) {
            return;
        }
        PlayerModel<?> model = (PlayerModel<?>) (Object) this;
        boolean left = entity.getMainArm() == HumanoidArm.LEFT;
        float flip = left ? -1.0F : 1.0F;
        float bodyPitch = (float) Math.toRadians(pose[0]);
        float bodyYaw = (float) Math.toRadians(pose[1] * flip);
        float armPitch = (float) Math.toRadians(pose[2]);
        float armYaw = (float) Math.toRadians(pose[3] * flip);
        float armRoll = (float) Math.toRadians(pose[4] * flip);
        float follow = pose[5];

        model.body.xRot = bodyPitch;
        model.body.yRot = bodyYaw;
        model.jacket.copyFrom(model.body);

        ModelPart main = left ? model.leftArm : model.rightArm;
        ModelPart sleeve = left ? model.leftSleeve : model.rightSleeve;
        main.xRot = armPitch;
        main.yRot = armYaw;
        main.zRot = armRoll;
        main.x = left ? 5.0F : -5.0F;
        main.z = 0.0F;
        sleeve.copyFrom(main);

        if (follow != 0.0F) {
            ModelPart off = left ? model.rightArm : model.leftArm;
            ModelPart offSleeve = left ? model.rightSleeve : model.leftSleeve;
            off.xRot = armPitch * follow;
            off.yRot = -armYaw * follow;
            off.zRot = -armRoll * follow;
            off.x = left ? -5.0F : 5.0F;
            off.z = 0.0F;
            offSleeve.copyFrom(off);
        }
    }
}
