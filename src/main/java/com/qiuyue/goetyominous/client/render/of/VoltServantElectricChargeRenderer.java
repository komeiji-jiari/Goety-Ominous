package com.qiuyue.goetyominous.client.render.of;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.common.entities.projectile.VoltServantElectricCharge;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;

public class VoltServantElectricChargeRenderer extends EntityRenderer<VoltServantElectricCharge> {

    public VoltServantElectricChargeRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(VoltServantElectricCharge entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        if (entity.tickCount >= 2 || !(this.entityRenderDispatcher.camera.getEntity().distanceToSqr(entity) < 12.25D)) {
            super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(VoltServantElectricCharge entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
