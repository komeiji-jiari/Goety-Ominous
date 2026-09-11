package com.qiuyue.goetyominous.client.render.block;

import com.Polarice3.Goety.client.render.block.ModBlockLayer;
import com.Polarice3.Goety.client.render.model.PlushieModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.blocks.PlushieBlock;
import com.qiuyue.goetyominous.common.blocks.entities.PlushieBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class PlushieBlockEntityRenderer implements BlockEntityRenderer<PlushieBlockEntity> {
    protected static final ResourceLocation TEXTURE =
            new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/plushie/0.png");

    public PlushieBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    private static PlushieModel model() {
        return new PlushieModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModBlockLayer.PLUSHIE));
    }

    @Override
    public void render(PlushieBlockEntity blockEntity, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        BlockState blockstate = blockEntity.getBlockState();
        float rotateY = 22.5F * blockstate.getValue(PlushieBlock.ROTATION);
        renderPlushie(blockEntity, partialTicks, rotateY, poseStack, buffer, combinedLight);
    }

    public static void renderPlushie(PlushieBlockEntity blockEntity, float partialTicks, float rotateY,
                                     PoseStack poseStack, MultiBufferSource buffer, int light) {
        PlushieModel plushieModel = model();
        poseStack.pushPose();
        poseStack.translate(0.5D, 0.0D, 0.5D);
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        float f = blockEntity.getAnimation(partialTicks);
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(getTexture(blockEntity.getBlockState())));
        plushieModel.setupAnim(f, rotateY, 0.0F);
        plushieModel.renderToBuffer(poseStack, consumer, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
    }

    public static void renderItemPlushie(ItemStack stack, BlockState blockState, float rotateY,
                                         PoseStack poseStack, MultiBufferSource buffer, int light) {
        PlushieModel plushieModel = model();
        poseStack.pushPose();
        poseStack.translate(0.5D, 0.0D, 0.5D);
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.scale(0.5F, 0.5F, 0.5F);
        VertexConsumer consumer = ItemRenderer.getFoilBufferDirect(buffer,
                RenderType.entityCutoutNoCull(getTexture(blockState)), true, stack.hasFoil());
        plushieModel.setupAnim(0.0F, rotateY, 0.0F);
        plushieModel.renderToBuffer(poseStack, consumer, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
    }

    public static ResourceLocation getTexture(BlockState blockState) {
        ResourceLocation texture = TEXTURE;
        Block block = blockState.getBlock();
        if (block instanceof PlushieBlock plushieBlock) {
            texture = new ResourceLocation(GoetyOminous.MOD_ID,
                    "textures/entity/plushie/" + plushieBlock.getPlushieName() + ".png");
        }
        return texture;
    }
}