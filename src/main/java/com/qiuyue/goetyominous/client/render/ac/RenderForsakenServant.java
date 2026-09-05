package com.qiuyue.goetyominous.client.render.ac;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.client.render.model.ac.ModelForsakenServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.ForsakenServant;
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
public class RenderForsakenServant extends MobRenderer<ForsakenServant, ModelForsakenServant> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexscaves:textures/entity/forsaken.png");
    private static final ResourceLocation TEXTURE_EYES = new ResourceLocation("alexscaves:textures/entity/forsaken_eyes.png");
    private static final ResourceLocation TEXTURE_DARKNESS = new ResourceLocation("alexscaves:textures/entity/forsaken_darkness.png");

    public RenderForsakenServant(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelForsakenServant(), 1.15F);
        this.addLayer(new LayerGlow(this));
        this.addLayer(new ForsakenServantHeldMobLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(ForsakenServant entity) {
        return TEXTURE;
    }

    public class LayerGlow extends RenderLayer<ForsakenServant, ModelForsakenServant> {

        public LayerGlow(RenderLayerParent<ForsakenServant, ModelForsakenServant> renderLayerParent) {
            super(renderLayerParent);
        }

        @Override
        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, ForsakenServant entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            VertexConsumer darknessConsumer = bufferIn.getBuffer(RenderType.entityTranslucent(TEXTURE_DARKNESS));
            this.getParentModel().renderToBuffer(matrixStackIn, darknessConsumer, packedLightIn, LivingEntityRenderer.getOverlayCoords(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, entitylivingbaseIn.getDarknessAmount(partialTicks));
            VertexConsumer eyesConsumer = bufferIn.getBuffer(RenderType.eyes(TEXTURE_EYES));
            this.getParentModel().renderToBuffer(matrixStackIn, eyesConsumer, packedLightIn, LivingEntityRenderer.getOverlayCoords(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}
