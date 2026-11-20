package com.qiuyue.goetyominous.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;

public class OminousCandleBlockRenderer {

    public static void renderBlockingWithSword(ItemInHandRenderer itemInHandRenderer, ArmedModel model,
                                               LivingEntity entity, ItemStack stack, ItemDisplayContext transform,
                                               HumanoidArm arm, PoseStack poseStack,
                                               MultiBufferSource multiBufferSource, int combinedLight) {
        poseStack.pushPose();
        model.translateToHand(arm, poseStack);
        boolean leftHand = arm == HumanoidArm.LEFT;
        applyItemBlockingTransform(poseStack, leftHand);
        poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
        applyItemTransformInverse(entity, stack, transform, poseStack, leftHand);
        itemInHandRenderer.renderItem(entity, stack, transform, leftHand, poseStack, multiBufferSource, combinedLight);
        poseStack.popPose();
    }

    private static void applyItemBlockingTransform(PoseStack poseStack, boolean leftHand) {
        poseStack.translate((leftHand ? 1.0F : -1.0F) / 16.0F, 0.4375F, 0.0625F);
        poseStack.translate(leftHand ? -0.035F : 0.05F, leftHand ? 0.045F : 0.0F, leftHand ? -0.135F : -0.1F);
        poseStack.mulPose(Axis.YP.rotationDegrees((leftHand ? -1.0F : 1.0F) * -50.0F));
        poseStack.mulPose(Axis.XP.rotationDegrees(-10.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees((leftHand ? -1.0F : 1.0F) * -60.0F));
        poseStack.translate(0.0F, 0.1875F, 0.0F);
        poseStack.scale(0.625F, 0.625F, 0.625F);
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
        poseStack.mulPose(Axis.XN.rotationDegrees(-100.0F));
        poseStack.mulPose(Axis.YN.rotationDegrees(leftHand ? 35.0F : 45.0F));
        poseStack.translate(0.0F, -0.3F, 0.0F);
        poseStack.scale(1.5F, 1.5F, 1.5F);
        poseStack.mulPose(Axis.ZP.rotationDegrees(50.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(335.0F));
        poseStack.translate(-0.9375F, -0.0625F, 0.0F);
        poseStack.translate(0.5F, 0.5F, 0.25F);
        poseStack.mulPose(Axis.YN.rotationDegrees(180.0F));
        poseStack.translate(0.0F, 0.0F, 0.28125F);
    }

    private static void applyItemTransformInverse(LivingEntity entity, ItemStack stack,
                                                  ItemDisplayContext transform, PoseStack poseStack,
                                                  boolean leftHand) {
        Minecraft minecraft = Minecraft.getInstance();
        BakedModel model = minecraft.getItemRenderer().getModel(stack, entity.level(), entity, 0);
        applyTransformInverse(model.getTransforms().getTransform(transform), leftHand, poseStack);
    }

    private static void applyTransformInverse(ItemTransform transform, boolean leftHand, PoseStack poseStack) {
        if (transform != ItemTransform.NO_TRANSFORM) {
            float angleX = transform.rotation.x();
            float angleY = leftHand ? -transform.rotation.y() : transform.rotation.y();
            float angleZ = leftHand ? -transform.rotation.z() : transform.rotation.z();
            Quaternionf quaternion = new Quaternionf().rotationXYZ(
                    angleX * ((float) Math.PI / 180.0F),
                    angleY * ((float) Math.PI / 180.0F),
                    angleZ * ((float) Math.PI / 180.0F));
            quaternion.conjugate();
            poseStack.scale(1.0F / transform.scale.x(), 1.0F / transform.scale.y(), 1.0F / transform.scale.z());
            poseStack.mulPose(quaternion);
            poseStack.translate((leftHand ? -1.0F : 1.0F) * -transform.translation.x(),
                    -transform.translation.y(), -transform.translation.z());
        }
    }
}
