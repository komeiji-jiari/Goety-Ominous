package com.qiuyue.goetyominous.common.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.client.BarbaricAxeSwingClient;
import com.qiuyue.goetyominous.common.items.BarbaricAxeItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.entity.HumanoidArm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class MixinItemInHandRendererSwing {

    @Inject(method = "applyItemArmAttackTransform", at = @At("TAIL"), require = 1)
    private void goetyominous$barbaricAxeSwing(PoseStack poseStack, HumanoidArm arm, float swingProgress,
                                               CallbackInfo ci) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || player.getMainArm() != arm) {
            return;
        }
        if (!(player.getMainHandItem().getItem() instanceof BarbaricAxeItem)) {
            return;
        }
        float[] pose = BarbaricAxeSwingClient.pose(player.getId(), true);
        if (pose == null) {
            return;
        }
        int sign = arm == HumanoidArm.RIGHT ? 1 : -1;
        poseStack.mulPose(Axis.YP.rotationDegrees(pose[1] * sign));
        poseStack.mulPose(Axis.XP.rotationDegrees(pose[0]));
        poseStack.mulPose(Axis.ZP.rotationDegrees(pose[2] * sign));
    }
}
