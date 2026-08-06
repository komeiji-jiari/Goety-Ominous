package com.qiuyue.goetyominous.client.render.am;

import com.github.alexthe666.alexsmobs.client.render.AMRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.client.render.model.am.ModelWarpedMoscoServant;
import com.qiuyue.goetyominous.common.entities.ally.am.WarpedMoscoServant;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderWarpedMoscoServant extends MobRenderer<WarpedMoscoServant, ModelWarpedMoscoServant> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("goetyominous", "textures/entity/warped_mosco_servant.png");
    private static final ResourceLocation TEXTURE_EYES = new ResourceLocation("alexsmobs:textures/entity/warped_mosco_glow.png");

    public RenderWarpedMoscoServant(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelWarpedMoscoServant(), 1F);
        this.addLayer(new WarpedMoscoServantGlowLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(WarpedMoscoServant entity) {
        return TEXTURE;
    }

    @OnlyIn(Dist.CLIENT)
    static class WarpedMoscoServantGlowLayer extends RenderLayer<WarpedMoscoServant, ModelWarpedMoscoServant> {

        public WarpedMoscoServantGlowLayer(RenderWarpedMoscoServant renderer) {
            super(renderer);
        }

        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, WarpedMoscoServant entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            VertexConsumer ivertexbuilder = bufferIn.getBuffer(AMRenderTypes.getEyesFlickering(TEXTURE_EYES, 0));
            float alpha = 0.5F + (Mth.cos(ageInTicks * 0.2F) + 1F) * 0.2F;
            this.getParentModel().renderToBuffer(matrixStackIn, ivertexbuilder, 240, LivingEntityRenderer.getOverlayCoords(entitylivingbaseIn, 0.0F), 0.5F, 1.0F, 1.0F, alpha);

        }
    }
}
