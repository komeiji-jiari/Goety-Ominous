package com.qiuyue.goetyominous.client.render.ac;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.client.render.model.ac.ModelGumbeeperServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.GumbeeperServant;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EnergySwirlLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

/**
 * 糖球苦力怕仆从渲染:移植 AC GumbeeperRenderer,保证与原版外观一致。
 * 三层绘制:
 * <ol>
 *   <li>主模型 gumbeeper.png(无剔除 cutout,保证半透明糖球层正确叠色);</li>
 *   <li>LayerGlow:外层玻璃 gumbeeper_glass.png(translucent) + 自爆进度 flicker 的 explode 贴图;</li>
 *   <li>ChargedSwirl:被雷击"蓄电"时叠加 gumbeeper_charged.png 能量外圈(模型外扩复制体)。</li>
 * </ol>
 * 去掉了 AC 的 Licowitch 附身/书页 sepia。
 */
@OnlyIn(Dist.CLIENT)
public class RenderGumbeeperServant extends MobRenderer<GumbeeperServant, ModelGumbeeperServant> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("alexscaves:textures/entity/gumbeeper.png");
    private static final ResourceLocation TEXTURE_GLASS = new ResourceLocation("alexscaves:textures/entity/gumbeeper_glass.png");
    private static final ResourceLocation TEXTURE_EXPLODE = new ResourceLocation("alexscaves:textures/entity/gumbeeper_explode.png");
    private static final ResourceLocation TEXTURE_CHARGED = new ResourceLocation("alexscaves:textures/entity/gumbeeper_charged.png");

    public RenderGumbeeperServant(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelGumbeeperServant(0.0F), 0.8F);
        this.addLayer(new LayerGlow());
        this.addLayer(new ChargedSwirl(this));
    }

    @Override
    protected void scale(GumbeeperServant mob, PoseStack poseStack, float partialTicks) {
    }

    @Override
    public ResourceLocation getTextureLocation(GumbeeperServant entity) {
        return TEXTURE;
    }

    @Override
    @Nullable
    protected RenderType getRenderType(GumbeeperServant gumbeeper, boolean normal, boolean translucent, boolean outline) {
        ResourceLocation resourcelocation = this.getTextureLocation(gumbeeper);
        if (translucent) {
            return RenderType.itemEntityTranslucentCull(resourcelocation);
        } else if (normal) {
            return RenderType.entityCutoutNoCull(resourcelocation);
        } else {
            return outline ? RenderType.outline(resourcelocation) : null;
        }
    }

    public class LayerGlow extends RenderLayer<GumbeeperServant, ModelGumbeeperServant> {

        public LayerGlow() {
            super(RenderGumbeeperServant.this);
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn, GumbeeperServant entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (!entity.isInvisible()) {
                VertexConsumer glassConsumer = bufferIn.getBuffer(RenderType.entityTranslucent(TEXTURE_GLASS));
                this.getParentModel().renderToBuffer(poseStack, glassConsumer, packedLightIn, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
                float explodeProgress = entity.getExplodeProgress(partialTicks);
                float alpha = (float) (Math.sin(ageInTicks * 1.2F) + 1.0F) * 0.5F * explodeProgress * 0.8F;
                VertexConsumer explodeConsumer = bufferIn.getBuffer(RenderType.entityTranslucentEmissive(TEXTURE_EXPLODE));
                this.getParentModel().renderToBuffer(poseStack, explodeConsumer, packedLightIn, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), 1.0F, 1.0F, 1.0F, alpha);
            }
        }
    }

    public static class ChargedSwirl extends EnergySwirlLayer<GumbeeperServant, ModelGumbeeperServant> {

        private final ModelGumbeeperServant model = new ModelGumbeeperServant(1.0F);

        public ChargedSwirl(RenderLayerParent<GumbeeperServant, ModelGumbeeperServant> renderer) {
            super(renderer);
        }

        @Override
        protected float xOffset(float f) {
            return f * 0.01F;
        }

        @Override
        protected ResourceLocation getTextureLocation() {
            return TEXTURE_CHARGED;
        }

        @Override
        protected EntityModel<GumbeeperServant> model() {
            return this.model;
        }
    }
}
