package com.qiuyue.goetyominous.client.render.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.common.entities.projectile.TremorBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TremorBlockRenderer extends EntityRenderer<TremorBlockEntity> {

    public TremorBlockRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(TremorBlockEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity.getBlockState().isAir()) {
            return;
        }
        poseStack.pushPose();
        poseStack.translate(-0.5D, 0.0D, -0.5D);
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(entity.getBlockState(), poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(TremorBlockEntity entity) {
        return null;
    }
}
