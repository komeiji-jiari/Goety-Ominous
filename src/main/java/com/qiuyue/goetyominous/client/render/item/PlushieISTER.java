package com.qiuyue.goetyominous.client.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.client.render.block.PlushieBlockEntityRenderer;
import com.qiuyue.goetyominous.common.blocks.PlushieBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PlushieISTER extends BlockEntityWithoutLevelRenderer {

    private static PlushieISTER instance;

    public static PlushieISTER get() {
        if (instance == null) {
            instance = new PlushieISTER();
        }
        return instance;
    }
    public PlushieISTER() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack poseStack,
                             MultiBufferSource buffer, int light, int overlay) {
        Item item = stack.getItem();
        if (!(item instanceof BlockItem blockItem)) {
            return;
        }
        Block block = blockItem.getBlock();
        if (!(block instanceof PlushieBlock)) {
            return;
        }
        if (context == ItemDisplayContext.GUI) {
            poseStack.pushPose();
            poseStack.translate(0.5F, 0.5F, 0.5F);
            poseStack.mulPose(Axis.XP.rotationDegrees(30.0F));
            poseStack.mulPose(Axis.YN.rotationDegrees(-45.0F));
            poseStack.translate(-0.5F, -0.5F, -0.5F);
            poseStack.translate(0.0F, 0.25F, 0.0F);
            PlushieBlockEntityRenderer.renderItemPlushie(stack, block.defaultBlockState(),
                    180.0F, poseStack, buffer, light);
            poseStack.popPose();
        } else {
            PlushieBlockEntityRenderer.renderItemPlushie(stack, block.defaultBlockState(),
                    180.0F, poseStack, buffer, light);
        }
    }
}
