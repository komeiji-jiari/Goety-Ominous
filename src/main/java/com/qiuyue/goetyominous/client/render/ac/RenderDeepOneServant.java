package com.qiuyue.goetyominous.client.render.ac;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.client.render.model.ac.ModelDeepOneServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.DeepOneServant;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderDeepOneServant extends MobRenderer<DeepOneServant, ModelDeepOneServant> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("alexscaves:textures/entity/deep_one/deep_one.png");
    private static final ResourceLocation TEXTURE_GLOW = new ResourceLocation("alexscaves:textures/entity/deep_one/deep_one_glow.png");

    public RenderDeepOneServant(EntityRendererProvider.Context context) {
        super(context, new ModelDeepOneServant(), 0.45F);
        this.addLayer(new LayerGlow(this));
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
    }

    @Override
    public ResourceLocation getTextureLocation(DeepOneServant entity) {
        return TEXTURE;
    }

    public static class LayerGlow extends RenderLayer<DeepOneServant, ModelDeepOneServant> {

        public LayerGlow(RenderLayerParent<DeepOneServant, ModelDeepOneServant> renderLayerParent) {
            super(renderLayerParent);
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, DeepOneServant entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (!entity.isInvisible()) {
                VertexConsumer glowBuffer = bufferSource.getBuffer(RenderType.eyes(TEXTURE_GLOW));
                this.getParentModel().renderToBuffer(poseStack, glowBuffer, 240, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }
}
