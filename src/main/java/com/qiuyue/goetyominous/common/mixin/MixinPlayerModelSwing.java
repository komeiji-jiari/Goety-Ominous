package com.qiuyue.goetyominous.common.mixin;

import com.qiuyue.goetyominous.client.BarbaricAxeSwingClient;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerModel.class)
public abstract class MixinPlayerModelSwing {

    @Inject(method = "setupAnim", at = @At("RETURN"), require = 1)
    private void goetyominous$barbaricAxeSwing(LivingEntity entity, float limbSwing, float limbSwingAmount,
                                               float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        float[] pose = BarbaricAxeSwingClient.pose(entity.getId(), false);
        if (pose == null) {
            return;
        }
        PlayerModel<?> model = (PlayerModel<?>) (Object) this;
        model.body.xRot = (float) Math.toRadians(pose[0]);
        model.rightArm.xRot = (float) Math.toRadians(pose[2]);
        model.rightArm.yRot = (float) Math.toRadians(pose[3]);
        model.rightArm.zRot = (float) Math.toRadians(pose[4]);
        model.rightSleeve.copyFrom(model.rightArm);
        model.jacket.copyFrom(model.body);
    }
}
