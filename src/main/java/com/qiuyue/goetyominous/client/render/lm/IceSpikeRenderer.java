package com.qiuyue.goetyominous.client.render.lm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.lm.IceSpikeModel;
import com.qiuyue.goetyominous.common.entities.ally.lm.projectile.IceSpikeEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IceSpikeRenderer extends EntityRenderer<IceSpikeEntity> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(
            "legendary_monsters", "textures/entity/ice_spike.png");

    private final IceSpikeModel<IceSpikeEntity> model;

    public IceSpikeRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new IceSpikeModel<>(context.bakeLayer(ModEntityLayers.ICE_SPIKE_LAYER));
    }

    @Override
    public void render(IceSpikeEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        float progress = entity.getAnimationProgress(partialTicks);
        if (progress == 0.0F) {
            return;
        }
        float scale = 2.0F;
        if (progress > 0.9F) {
            scale *= (1.0F - progress) / 0.1F;
        }
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F - entity.getYRot()));
        poseStack.scale(-scale, -scale, scale);
        poseStack.translate(0.0D, -0.626D, 0.0D);
        poseStack.scale(0.7F, 0.7F, 0.7F);
        this.model.setupAnim(entity, progress, 0.0F, 0.0F, entity.getYRot(), entity.getXRot());
        VertexConsumer vertexConsumer = buffer.getBuffer(this.model.renderType(TEXTURE));
        this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(IceSpikeEntity entity) {
        return TEXTURE;
    }
}
