package com.qiuyue.goetyominous.client.render.ac;

import com.github.alexmodguy.alexscaves.client.render.ACRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.client.render.model.ac.ModelHullbreakerServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.HullbreakerServant;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.entity.PartEntity;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class RenderHullbreakerServant extends MobRenderer<HullbreakerServant, ModelHullbreakerServant> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("alexscaves:textures/entity/hullbreaker.png");
    private static final ResourceLocation TEXTURE_GLOW = new ResourceLocation("alexscaves:textures/entity/hullbreaker_glow.png");

    public RenderHullbreakerServant(EntityRendererProvider.Context context) {
        super(context, new ModelHullbreakerServant(), 2.25F);
        this.addLayer(new LayerGlow(this));
    }

    @Override
    protected void scale(HullbreakerServant entity, PoseStack poseStack, float partialTicks) {
    }

    @Override
    @Nullable
    protected RenderType getRenderType(HullbreakerServant mob, boolean normal, boolean translucent, boolean outline) {
        ResourceLocation resourcelocation = this.getTextureLocation(mob);
        if (translucent) {
            return RenderType.itemEntityTranslucentCull(resourcelocation);
        } else if (normal) {
            return RenderType.entityTranslucent(resourcelocation);
        } else {
            return outline ? RenderType.outline(resourcelocation) : null;
        }
    }

    @Override
    public ResourceLocation getTextureLocation(HullbreakerServant entity) {
        return TEXTURE;
    }

    @Override
    public boolean shouldRender(HullbreakerServant entity, Frustum camera, double x, double y, double z) {
        if (super.shouldRender(entity, camera, x, y, z)) {
            return true;
        }
        for (PartEntity<?> part : entity.getParts()) {
            if (camera.isVisible(part.getBoundingBoxForCulling())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected float getFlipDegrees(HullbreakerServant hullbreakerEntity) {
        return 0.0F;
    }

    public static class LayerGlow extends RenderLayer<HullbreakerServant, ModelHullbreakerServant> {

        public LayerGlow(RenderLayerParent<HullbreakerServant, ModelHullbreakerServant> renderLayerParent) {
            super(renderLayerParent);
        }

        @Override
        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, HullbreakerServant entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            VertexConsumer builder = bufferIn.getBuffer(ACRenderTypes.getEyesAlphaEnabled(TEXTURE_GLOW));
            float alpha = (float) ((Math.sin(entity.getPulseAmount(partialTicks)) + 1.0F) * 0.5F);
            this.getParentModel().renderToBuffer(matrixStackIn, builder, packedLightIn, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), 1.0F, 1.0F, 1.0F, alpha);
        }
    }
}
