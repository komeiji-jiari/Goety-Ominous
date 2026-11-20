package com.qiuyue.goetyominous.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.common.items.OminousCandleItem;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class OminousCandleFirstPersonMixin {

    @Inject(method = "renderArmWithItem", at = @At("HEAD"), cancellable = true)
    private void goetyominous$ominousCandleFirstPerson(AbstractClientPlayer player, float partialTicks, float pitch,
                                                       InteractionHand hand, float swingProgress, ItemStack stack,
                                                       float equipProgress, PoseStack poseStack,
                                                       MultiBufferSource multiBufferSource, int packedLight,
                                                       CallbackInfo ci) {
        if (stack.isEmpty() || !(stack.getItem() instanceof OminousCandleItem)) {
            return;
        }
        if (!player.isUsingItem() || !player.getUseItem().is(stack.getItem())
                || player.getUsedItemHand() != hand) {
            return;
        }
        boolean mainHand = hand == InteractionHand.MAIN_HAND;
        HumanoidArm arm = mainHand ? player.getMainArm() : player.getMainArm().getOpposite();
        boolean right = arm == HumanoidArm.RIGHT;
        int direction = right ? 1 : -1;
        poseStack.pushPose();
        poseStack.translate(direction * 0.56F, -0.52F + equipProgress * -0.6F, -0.72F);
        poseStack.translate(direction * -0.14142136F, 0.08F, 0.14142136F);
        poseStack.mulPose(Axis.XP.rotationDegrees(-102.25F));
        poseStack.mulPose(Axis.YP.rotationDegrees(direction * 13.365F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(direction * 78.05F));
        ((ItemInHandRenderer) (Object) this).renderItem(player, stack,
                right ? ItemDisplayContext.FIRST_PERSON_RIGHT_HAND : ItemDisplayContext.FIRST_PERSON_LEFT_HAND,
                !right, poseStack, multiBufferSource, packedLight);
        poseStack.popPose();
        ci.cancel();
    }
}
