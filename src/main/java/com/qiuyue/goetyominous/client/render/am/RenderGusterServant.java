package com.qiuyue.goetyominous.client.render.am;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.client.render.model.am.ModelGusterServant;
import com.qiuyue.goetyominous.common.entities.ally.am.GusterServant;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderGusterServant extends MobRenderer<GusterServant, ModelGusterServant> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("goetyominous:textures/entity/guster.png");
    private static final ResourceLocation TEXTURE_GOOGLY = new ResourceLocation("goetyominous:textures/entity/guster_silly.png");
    private static final ResourceLocation TEXTURE_EYES = new ResourceLocation("goetyominous:textures/entity/guster_eye.png");
    private static final ResourceLocation TEXTURE_RED = new ResourceLocation("goetyominous:textures/entity/guster_red.png");
    private static final ResourceLocation TEXTURE_SOUL = new ResourceLocation("goetyominous:textures/entity/guster_soul.png");
    private static final ResourceLocation TEXTURE_SOUL_EYES = new ResourceLocation("goetyominous:textures/entity/guster_eye_soul.png");

    public RenderGusterServant(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelGusterServant(), 0.25F);
        this.addLayer(new GusterEyesLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(GusterServant entity) {
        if (entity.isGooglyEyes()) {
            return TEXTURE_GOOGLY;
        }
        return switch (entity.getVariant()) {
            case 2 -> TEXTURE_SOUL;
            case 1 -> TEXTURE_RED;
            default -> TEXTURE;
        };
    }

    @OnlyIn(Dist.CLIENT)
    private static class GusterEyesLayer extends RenderLayer<GusterServant, ModelGusterServant> {

        public GusterEyesLayer(RenderGusterServant renderer) {
            super(renderer);
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, GusterServant entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (!entity.isGooglyEyes()) {
                VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.eyes(entity.getVariant() == 2 ? TEXTURE_SOUL_EYES : TEXTURE_EYES));
                this.getParentModel().renderToBuffer(poseStack, vertexConsumer, 15728640, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }
}
