package com.qiuyue.goetyominous.client.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.common.blocks.trial.TrialSpawner;
import com.qiuyue.goetyominous.common.blocks.trial.TrialSpawnerBlockEntity;
import com.qiuyue.goetyominous.common.blocks.trial.TrialSpawnerData;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class TrialSpawnerRenderer implements BlockEntityRenderer<TrialSpawnerBlockEntity> {
    private final EntityRenderDispatcher entityRenderer;

    public TrialSpawnerRenderer(BlockEntityRendererProvider.Context context) {
        this.entityRenderer = context.getEntityRenderer();
    }

    @Override
    public void render(TrialSpawnerBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        Level level = blockEntity.getLevel();
        if (level == null) {
            return;
        }
        TrialSpawner trialSpawner = blockEntity.getTrialSpawner();
        TrialSpawnerData data = trialSpawner.getData();
        Entity entity = data.getOrCreateDisplayEntity(trialSpawner, level, trialSpawner.getState());
        if (entity != null) {
            renderEntityInSpawner(partialTick, poseStack, bufferSource, packedLight, entity,
                    this.entityRenderer, data.getOSpin(), data.getSpin());
        }
    }

    public static void renderEntityInSpawner(float partialTick, PoseStack poseStack, MultiBufferSource buffer,
                                             int packedLight, Entity entity, EntityRenderDispatcher entityRenderer,
                                             double oSpin, double spin) {
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.0F, 0.5F);
        float scale = 0.53125F;
        float maxDimension = Math.max(entity.getBbWidth(), entity.getBbHeight());
        if ((double) maxDimension > 1.0D) {
            scale /= maxDimension;
        }
        poseStack.translate(0.0F, 0.4F, 0.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees((float) Mth.lerp(partialTick, oSpin, spin) * 10.0F));
        poseStack.translate(0.0F, -0.2F, 0.0F);
        poseStack.mulPose(Axis.XP.rotationDegrees(-30.0F));
        poseStack.scale(scale, scale, scale);
        entityRenderer.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, partialTick, poseStack, buffer, packedLight);
        poseStack.popPose();
    }
}
