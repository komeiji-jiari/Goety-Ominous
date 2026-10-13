package com.qiuyue.goetyominous.common.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.client.ac.BarbaricAxeSwingClient;
import com.qiuyue.goetyominous.common.items.ac.BarbaricAxeItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class MixinItemInHandRendererSwing {

    @ModifyVariable(method = "renderArmWithItem", at = @At("HEAD"), argsOnly = true, index = 5, require = 1)
    private float goetyominous$barbaricAxeSuppressVanilla(float swingProgress) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return swingProgress;
        }
        if (player.swingingArm == InteractionHand.OFF_HAND) {
            return swingProgress;
        }
        if (!(player.getMainHandItem().getItem() instanceof BarbaricAxeItem)) {
            return swingProgress;
        }
        if (!BarbaricAxeSwingClient.swinging(player.getId())) {
            return swingProgress;
        }
        return 0.0F;
    }

    @Inject(method = "renderPlayerArm", at = @At("HEAD"), require = 1)
    private void goetyominous$enterFirstPersonArm(PoseStack poseStack, MultiBufferSource bufferSource, int light,
                                                  float equippedProgress, float swingProgress, HumanoidArm arm,
                                                  CallbackInfo ci) {
        BarbaricAxeSwingClient.setFirstPersonHand(true);
    }

    @Inject(method = "renderPlayerArm", at = @At("RETURN"), require = 1)
    private void goetyominous$exitFirstPersonArm(PoseStack poseStack, MultiBufferSource bufferSource, int light,
                                                 float equippedProgress, float swingProgress, HumanoidArm arm,
                                                 CallbackInfo ci) {
        BarbaricAxeSwingClient.setFirstPersonHand(false);
    }

    @Inject(method = "applyItemArmAttackTransform", at = @At("HEAD"), cancellable = true, require = 1)
    private void goetyominous$barbaricAxeSwing(PoseStack poseStack, HumanoidArm arm, float swingProgress,
                                               CallbackInfo ci) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || player.getMainArm() != arm) {
            return;
        }
        if (!(player.getMainHandItem().getItem() instanceof BarbaricAxeItem)) {
            return;
        }
        float[] pose = BarbaricAxeSwingClient.firstPersonPose(player.getId());
        if (pose == null) {
            return;
        }
        poseStack.translate(pose[4], pose[5], pose[6]);
        poseStack.mulPose(new Quaternionf().setAngleAxis(pose[0], pose[1], pose[2], pose[3]));
        ci.cancel();
    }
}
