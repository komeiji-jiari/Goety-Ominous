package com.qiuyue.goetyominous.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.layer.WargArmorLayer;
import com.qiuyue.goetyominous.client.render.layer.WargSaddleLayer;
import com.qiuyue.goetyominous.client.render.layer.WargSwordLayer;
import com.qiuyue.goetyominous.client.render.model.WargModel;
import com.qiuyue.goetyominous.common.entities.ally.mobs.Warg;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class WargRenderer extends MobRenderer<Warg, WargModel> {
    private static final ResourceLocation BLACK = tex("black_warg.png");
    private static final ResourceLocation COLD = tex("winter_warg.png");
    private static final ResourceLocation MODERATE = tex("storm_warg.png");
    private static final ResourceLocation WARM = tex("warg_warm.png");
    private static final ResourceLocation SKELETAL = tex("skeletal_warg.png");
    private static final ResourceLocation GRAY = tex("gray_warg.png");
    private static final ResourceLocation HOSTILE = tex("hostile_warg.png");
    private static final ResourceLocation CHAINS = tex("black_warg_chain.png");

    private static final ResourceLocation BLACK_EYES = tex("black_warg_eyes.png");
    private static final ResourceLocation COLD_EYES = tex("winter_warg_eyes.png");
    private static final ResourceLocation MODERATE_EYES = tex("storm_warg_eyes.png");
    private static final ResourceLocation WARM_EYES = tex("warg_warm_eyes.png");
    private static final ResourceLocation SKELETAL_EYES = tex("skeletal_warg_eyes.png");
    private static final ResourceLocation GRAY_EYES = tex("gray_warg_eyes.png");
    private static final ResourceLocation HOSTILE_EYES = tex("hostile_warg_eyes.png");

    private static ResourceLocation tex(String name) {
        return new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/" + name);
    }

    public WargRenderer(EntityRendererProvider.Context context) {
        super(context, new WargModel(context.bakeLayer(ModEntityLayers.WARG)), 0.75F);
        this.addLayer(new WargEyesLayer(this));
        this.addLayer(new WargTextureLayer(this, new WargModel(context.bakeLayer(ModEntityLayers.WARG)), CHAINS));
        this.addLayer(new WargArmorLayer(this, context.getModelSet()));
        this.addLayer(new WargSaddleLayer(this, context.getModelSet()));
        this.addLayer(new WargSwordLayer(this, context.getItemInHandRenderer()));
    }

    @Override
    public ResourceLocation getTextureLocation(Warg warg) {
        if (warg.isHostile()) {
            return HOSTILE;
        }
        return switch (warg.getVariant()) {
            case COLD -> COLD;
            case MODERATE -> MODERATE;
            case WARM -> WARM;
            case SKELETAL -> SKELETAL;
            case GRAY -> GRAY;
            default -> BLACK;
        };
    }

    private static ResourceLocation getEyesTexture(Warg warg) {
        if (warg.isHostile()) {
            return HOSTILE_EYES;
        }
        return switch (warg.getVariant()) {
            case COLD -> COLD_EYES;
            case MODERATE -> MODERATE_EYES;
            case WARM -> WARM_EYES;
            case SKELETAL -> SKELETAL_EYES;
            case GRAY -> GRAY_EYES;
            default -> BLACK_EYES;
        };
    }

    private static class WargEyesLayer extends RenderLayer<Warg, WargModel> {
        private WargEyesLayer(WargRenderer parent) {
            super(parent);
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, Warg warg, float limbSwing,
                           float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (warg.getVariant() == Warg.Variant.SKELETAL) {
                return;
            }
            VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.eyes(getEyesTexture(warg)));
            this.getParentModel().renderToBuffer(poseStack, vertexconsumer, 15728880, OverlayTexture.NO_OVERLAY,
                    1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    private static class WargTextureLayer extends RenderLayer<Warg, WargModel> {
        private final WargModel layerModel;
        private final ResourceLocation texture;

        private WargTextureLayer(WargRenderer parent, WargModel layerModel, ResourceLocation texture) {
            super(parent);
            this.layerModel = layerModel;
            this.texture = texture;
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, Warg warg, float limbSwing,
                           float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            coloredCutoutModelCopyLayerRender(this.getParentModel(), this.layerModel, this.texture, poseStack, buffer,
                    packedLight, warg, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks,
                    1.0F, 1.0F, 1.0F);
        }
    }
}
