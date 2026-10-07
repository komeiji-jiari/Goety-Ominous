package com.qiuyue.goetyominous.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.common.blocks.trial.VaultBlockEntity;
import com.qiuyue.goetyominous.common.blocks.trial.VaultClientData;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class VaultRenderer implements BlockEntityRenderer<VaultBlockEntity> {
    private static final float ITEM_BUNDLE_OFFSET_SCALE = 0.15F;
    private static final float FLAT_ITEM_BUNDLE_OFFSET_X = 0.0F;
    private static final float FLAT_ITEM_BUNDLE_OFFSET_Y = 0.0F;
    private static final float FLAT_ITEM_BUNDLE_OFFSET_Z = 0.09375F;

    private final ItemRenderer itemRenderer;
    private final RandomSource random = RandomSource.create();

    public VaultRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(VaultBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        if (!VaultBlockEntity.Client.shouldDisplayActiveEffects(blockEntity.getSharedData())) {
            return;
        }
        Level level = blockEntity.getLevel();
        if (level == null) {
            return;
        }
        ItemStack itemStack = blockEntity.getSharedData().getDisplayItem();
        if (itemStack.isEmpty()) {
            return;
        }
        this.random.setSeed(getSeedForItemStack(itemStack));
        VaultClientData clientData = blockEntity.getClientData();
        renderItemInside(partialTick, level, poseStack, bufferSource, packedLight, itemStack, this.itemRenderer,
                clientData.previousSpin(), clientData.currentSpin(), this.random);
    }

    public static void renderItemInside(float partialTick, Level level, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, ItemStack itemStack, ItemRenderer itemRenderer, float rotation, float spin, RandomSource random) {
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.4F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.rotLerp(partialTick, rotation, spin)));
        renderMultipleFromCount(itemRenderer, poseStack, bufferSource, packedLight, itemStack, random, level);
        poseStack.popPose();
    }

    private static int getSeedForItemStack(ItemStack stack) {
        return stack.isEmpty() ? 187 : Item.getId(stack.getItem()) + stack.getDamageValue();
    }

    private static void renderMultipleFromCount(ItemRenderer itemRenderer, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, ItemStack itemStack, RandomSource random, Level level) {
        BakedModel model = itemRenderer.getModel(itemStack, level, null, 0);
        renderMultipleFromCount(itemRenderer, poseStack, bufferSource, packedLight, itemStack, model, model.isGui3d(), random);
    }

    private static void renderMultipleFromCount(ItemRenderer itemRenderer, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, ItemStack itemStack, BakedModel model, boolean isGui3d, RandomSource random) {
        int count = getRenderedAmount(itemStack.getCount());
        float x = model.getTransforms().ground.scale.x();
        float y = model.getTransforms().ground.scale.y();
        float z = model.getTransforms().ground.scale.z();
        if (!isGui3d) {
            poseStack.translate(-FLAT_ITEM_BUNDLE_OFFSET_X * (float) (count - 1) * 0.5F * x,
                    -FLAT_ITEM_BUNDLE_OFFSET_Y * (float) (count - 1) * 0.5F * y,
                    -FLAT_ITEM_BUNDLE_OFFSET_Z * (float) (count - 1) * 0.5F * z);
        }
        for (int i = 0; i < count; ++i) {
            poseStack.pushPose();
            if (i > 0) {
                if (isGui3d) {
                    poseStack.translate((random.nextFloat() * 2.0F - 1.0F) * ITEM_BUNDLE_OFFSET_SCALE,
                            (random.nextFloat() * 2.0F - 1.0F) * ITEM_BUNDLE_OFFSET_SCALE,
                            (random.nextFloat() * 2.0F - 1.0F) * ITEM_BUNDLE_OFFSET_SCALE);
                } else {
                    poseStack.translate((random.nextFloat() * 2.0F - 1.0F) * ITEM_BUNDLE_OFFSET_SCALE * 0.5F,
                            (random.nextFloat() * 2.0F - 1.0F) * ITEM_BUNDLE_OFFSET_SCALE * 0.5F,
                            0.0F);
                }
            }
            itemRenderer.render(itemStack, ItemDisplayContext.GROUND, false, poseStack, bufferSource, packedLight, OverlayTexture.NO_OVERLAY, model);
            poseStack.popPose();
            if (!isGui3d) {
                poseStack.translate(FLAT_ITEM_BUNDLE_OFFSET_X * x, FLAT_ITEM_BUNDLE_OFFSET_Y * y, FLAT_ITEM_BUNDLE_OFFSET_Z * z);
            }
        }
    }

    private static int getRenderedAmount(int count) {
        if (count <= 1) {
            return 1;
        }
        if (count <= 16) {
            return 2;
        }
        if (count <= 32) {
            return 3;
        }
        if (count <= 48) {
            return 4;
        }
        return 5;
    }
}
