package com.qiuyue.goetyominous.client.render;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.layer.WargArmorLayer;
import com.qiuyue.goetyominous.client.render.layer.WargSaddleLayer;
import com.qiuyue.goetyominous.client.render.layer.WargSwordLayer;
import com.qiuyue.goetyominous.client.render.model.WargModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.common.entities.ally.mobs.Warg;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class WargRenderer extends MobRenderer<Warg, WargModel> {
    private static final ResourceLocation BLACK = new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/black_warg.png");
    private static final ResourceLocation COLD = new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/warg_cold.png");
    private static final ResourceLocation MODERATE = new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/warg_moderate.png");
    private static final ResourceLocation WARM = new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/warg_warm.png");
    private static final ResourceLocation CHAINS = new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/black_warg_chain.png");

    public WargRenderer(EntityRendererProvider.Context context) {
        super(context, new WargModel(context.bakeLayer(ModEntityLayers.WARG)), 0.75F);
        this.addLayer(new WargTextureLayer(this, new WargModel(context.bakeLayer(ModEntityLayers.WARG)), CHAINS));
        this.addLayer(new WargArmorLayer(this, context.getModelSet()));
        this.addLayer(new WargSaddleLayer(this, context.getModelSet()));
        this.addLayer(new WargSwordLayer(this, context.getItemInHandRenderer()));
    }

    @Override
    public ResourceLocation getTextureLocation(Warg warg) {
        return switch (warg.getVariant()) {
            case COLD -> COLD;
            case MODERATE -> MODERATE;
            case WARM -> WARM;
            default -> BLACK;
        };
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
