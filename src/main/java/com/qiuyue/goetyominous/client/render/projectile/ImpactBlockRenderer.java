package com.qiuyue.goetyominous.client.render.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.common.entities.projectile.ImpactBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ImpactBlockRenderer extends EntityRenderer<ImpactBlockEntity> {

    public ImpactBlockRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(ImpactBlockEntity entity, float entityYaw, float partialTick, PoseStack poseStack,
            MultiBufferSource buffer, int packedLight) {
        if (entity.getBlockState().isAir()) {
            return;
        }
        poseStack.pushPose();

        poseStack.translate(0.0D, 0.49D, 0.0D);

        if (entity.getLocalPhase() == 2) {
            float rot = entity.getLocalPhaseTick() + partialTick;
            poseStack.mulPose(Axis.YP.rotationDegrees(rot * 4.5F));
            poseStack.mulPose(Axis.XP.rotationDegrees(rot * 3.5F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(rot * 2.5F));
        }
        poseStack.translate(0.0D, -0.49D, 0.0D);

        poseStack.translate(-0.5D, -0.01D, -0.5D);
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(entity.getBlockState(), poseStack, buffer,
                packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(ImpactBlockEntity entity) {
        return null;
    }
}
