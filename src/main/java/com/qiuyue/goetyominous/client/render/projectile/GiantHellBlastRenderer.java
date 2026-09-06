package com.qiuyue.goetyominous.client.render.projectile;

import com.Polarice3.Goety.client.render.HellBlastRenderer;
import com.Polarice3.Goety.common.entities.projectiles.HellBlast;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class GiantHellBlastRenderer extends HellBlastRenderer {
    public GiantHellBlastRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(HellBlast entity, float entityYaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.scale(1.5F, 1.5F, 1.5F);
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        poseStack.popPose();
    }
}
