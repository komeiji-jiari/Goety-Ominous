package com.qiuyue.goetyominous.client.render.ac;

import com.github.alexmodguy.alexscaves.client.render.ACRenderTypes;
import com.github.alexmodguy.alexscaves.client.render.entity.layer.ACPotionEffectLayer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.client.render.model.ac.ModelDeepOneMageServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.DeepOneMageServant;
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
public class RenderDeepOneMageServant extends MobRenderer<DeepOneMageServant, ModelDeepOneMageServant> {

    public static final ResourceLocation TEXTURE = new ResourceLocation("alexscaves:textures/entity/deep_one/deep_one_mage.png");
    private static final ResourceLocation TEXTURE_GLOW = new ResourceLocation("alexscaves:textures/entity/deep_one/deep_one_mage_glow.png");
    private boolean sepia;

    public RenderDeepOneMageServant(EntityRendererProvider.Context context) {
        super(context, new ModelDeepOneMageServant(), 0.45F);
        this.addLayer(new LayerGlow(this));
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
        // 与原版一致:AC 通过全局 ClientLayerRegistry 给所有活体渲染器附加气泡层,
        // 但该钩子对第三方注册的渲染器并不总是生效,这里显式加上以保证气泡渲染。
        this.addLayer(new ACPotionEffectLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(DeepOneMageServant entity) {
        return TEXTURE_GLOW;
    }

    @Override
    protected RenderType getRenderType(DeepOneMageServant entity, boolean pVisible, boolean pTransparent, boolean pGlowing) {
        ResourceLocation texture = this.getTextureLocation(entity);
        if (pTransparent) {
            return RenderType.entityTranslucentCull(texture);
        }
        if (pVisible) {
            return this.sepia ? ACRenderTypes.getBookWidget(texture, true) : ACRenderTypes.getTeslaBulb(texture);
        }
        return pGlowing ? RenderType.outline(texture) : null;
    }

    public void setSepiaFlag(boolean sepia) {
        this.sepia = sepia;
    }

    public class LayerGlow extends RenderLayer<DeepOneMageServant, ModelDeepOneMageServant> {

        public LayerGlow(RenderLayerParent<DeepOneMageServant, ModelDeepOneMageServant> renderLayerParent) {
            super(renderLayerParent);
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, DeepOneMageServant entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (!entity.isInvisible()) {
                VertexConsumer glowBuffer = bufferSource.getBuffer(RenderDeepOneMageServant.this.sepia ? ACRenderTypes.getBookWidget(TEXTURE, true) : ACRenderTypes.getGhostly(TEXTURE));
                this.getParentModel().renderToBuffer(poseStack, glowBuffer, packedLight, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }
}
