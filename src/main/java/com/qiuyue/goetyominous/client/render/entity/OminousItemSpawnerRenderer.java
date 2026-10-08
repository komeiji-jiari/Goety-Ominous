package com.qiuyue.goetyominous.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.client.render.block.VaultRenderer;
import com.qiuyue.goetyominous.common.blocks.trial.OminousItemSpawner;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class OminousItemSpawnerRenderer extends EntityRenderer<OminousItemSpawner> {
    private static final float ROTATION_SPEED = 40.0F;
    private static final int FULL_SIZE_TICKS = 50;
    private final ItemRenderer itemRenderer;

    public OminousItemSpawnerRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(OminousItemSpawner entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        ItemStack stack = entity.getItem();
        if (stack.isEmpty()) {
            return;
        }
        poseStack.pushPose();
        if (entity.tickCount <= FULL_SIZE_TICKS) {
            float scale = Math.min((float) entity.tickCount + partialTick, (float) FULL_SIZE_TICKS)
                    / (float) FULL_SIZE_TICKS;
            poseStack.scale(scale, scale, scale);
        }
        Level level = entity.level();
        float previousSpin = Mth.wrapDegrees((float) (level.getGameTime() - 1L)) * ROTATION_SPEED;
        float spin = Mth.wrapDegrees((float) level.getGameTime()) * ROTATION_SPEED;
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.rotLerp(partialTick, previousSpin, spin)));
        VaultRenderer.renderMultipleFromCount(this.itemRenderer, poseStack, buffer, 0xF000F0, stack,
                level.random, level);
        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(OminousItemSpawner entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
