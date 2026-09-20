package com.qiuyue.goetyominous.client.render.ac;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.client.render.model.ac.ModelGammaroachServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.GammaroachServant;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderGammaroachServant extends MobRenderer<GammaroachServant, ModelGammaroachServant> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("alexscaves:textures/entity/gammaroach.png");
    private static final ResourceLocation TEXTURE_EYES = new ResourceLocation("alexscaves:textures/entity/gammaroach_eyes.png");

    public RenderGammaroachServant(EntityRendererProvider.Context context) {
        super(context, new ModelGammaroachServant(), 0.25F);
        this.addLayer(new LayerGlow(this));
    }

    @Override
    public ResourceLocation getTextureLocation(GammaroachServant entity) {
        return TEXTURE;
    }

    public static class LayerGlow extends RenderLayer<GammaroachServant, ModelGammaroachServant> {

        public LayerGlow(RenderLayerParent<GammaroachServant, ModelGammaroachServant> renderLayerParent) {
            super(renderLayerParent);
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, GammaroachServant entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            VertexConsumer eyesBuffer = bufferSource.getBuffer(RenderType.eyes(TEXTURE_EYES));
            this.getParentModel().renderToBuffer(poseStack, eyesBuffer, packedLight, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}
