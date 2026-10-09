package com.qiuyue.goetyominous.client.render.am;

import com.github.alexthe666.alexsmobs.client.render.AMRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.client.render.model.am.ModelSoulVultureServant;
import com.qiuyue.goetyominous.common.entities.ally.am.SoulVultureServant;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderSoulVultureServant extends MobRenderer<SoulVultureServant, ModelSoulVultureServant> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/soul_vulture/soul_vulture.png");
    private static final ResourceLocation TEXTURE_GLOW = new ResourceLocation("alexsmobs:textures/entity/soul_vulture/soul_vulture_glow.png");

    public RenderSoulVultureServant(EntityRendererProvider.Context context) {
        super(context, new ModelSoulVultureServant(), 0.3F);
        this.addLayer(new SoulVultureGlowLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(SoulVultureServant entity) {
        return TEXTURE;
    }

    @OnlyIn(Dist.CLIENT)
    private static class SoulVultureGlowLayer extends RenderLayer<SoulVultureServant, ModelSoulVultureServant> {

        public SoulVultureGlowLayer(RenderSoulVultureServant renderer) {
            super(renderer);
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, SoulVultureServant entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            int overlay = LivingEntityRenderer.getOverlayCoords(entity, 0.0F);
            this.getParentModel().renderToBuffer(poseStack, buffer.getBuffer(AMRenderTypes.getGhost(TEXTURE_GLOW)), 240, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}
