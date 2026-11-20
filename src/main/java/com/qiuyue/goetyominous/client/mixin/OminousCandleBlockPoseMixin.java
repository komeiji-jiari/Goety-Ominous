package com.qiuyue.goetyominous.client.mixin;

import com.qiuyue.goetyominous.common.items.ModItems;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public abstract class OminousCandleBlockPoseMixin {

    @Shadow
    public ModelPart rightArm;

    @Shadow
    public ModelPart leftArm;

    @Inject(method = "setupAnim", require = 1,
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/model/HumanoidModel;setupAttackAnimation(Lnet/minecraft/world/entity/LivingEntity;F)V"))
    private void goetyominous$ominousCandleBlockPose(LivingEntity entity, float limbSwing, float limbSwingAmount,
                                                     float ageInTicks, float netHeadYaw, float headPitch,
                                                     CallbackInfo ci) {
        if (!(entity instanceof Player player)) {
            return;
        }
        if (!player.isUsingItem() || !player.getUseItem().is(ModItems.OMINOUS_CANDLE.get())) {
            return;
        }
        InteractionHand rightArmHand = player.getMainArm() == HumanoidArm.RIGHT
                ? InteractionHand.MAIN_HAND
                : InteractionHand.OFF_HAND;
        if (player.getUsedItemHand() == rightArmHand) {
            this.rightArm.xRot -= 0.62831855F;
        } else {
            this.leftArm.xRot -= 0.62831855F;
        }
    }
}
