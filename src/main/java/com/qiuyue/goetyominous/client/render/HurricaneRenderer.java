package com.qiuyue.goetyominous.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.render.model.HurricaneModel;
import com.qiuyue.goetyominous.client.render.model.HurricaneTornadoModel;
import com.qiuyue.goetyominous.client.render.projectile.WindChargeRenderTypes;
import com.qiuyue.goetyominous.common.entities.ally.mobs.HurricaneServant;
import com.qiuyue.goetyominous.common.entities.ally.neutral.AbstractHurricane;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HurricaneRenderer<T extends AbstractHurricane> extends MobRenderer<T, HurricaneModel<T>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/hurricane.png");
    private static final ResourceLocation SERVANT_TEXTURE = new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/hurricane_servant.png");
    private static final ResourceLocation HEAVY_CORE_TEXTURE = new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/hurricane_heavy_core.png");
    private static final ResourceLocation SERVANT_HEAVY_CORE_TEXTURE = new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/hurricane_servant_heavy_core.png");
    private static final ResourceLocation WIND_TEXTURE = new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/hurricane_wind.png");
    private static final ResourceLocation BREEZE_WIND_TEXTURE = new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/breeze_wind.png");
    private static final ResourceLocation LAYER_TEXTURE = new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/hurricane_layer.png");
    private static final ResourceLocation OMINOUS_LAYER_TEXTURE = new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/ominous_hurricane_layer.png");
    private static final float WIND_SCROLL_SPEED = 0.02F;
    private static final float TORNADO_SCALE = 1.2F;

    public HurricaneRenderer(EntityRendererProvider.Context context) {
        super(context, new HurricaneModel<>(context.bakeLayer(ModModelLayers.HURRICANE)), 0.9F);
        this.addLayer(new OminousEyesLayer<>(this));
        this.addLayer(new EyesLayer<>(this));
        this.addLayer(new TornadoLayer<>(context, this));
        this.addLayer(new HeavyCoreLayer<>(this));
        this.addLayer(new WindLayer<>(this));
    }

    @Override
    public void render(T entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        this.getModel().showBody();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return isServant(entity) ? SERVANT_TEXTURE : TEXTURE;
    }

    @Nullable
    @Override
    protected RenderType getRenderType(T entity, boolean bodyVisible, boolean translucent, boolean glowing) {
        if (entity.deathTime > 0) {
            return RenderType.entityTranslucent(this.getTextureLocation(entity));
        }
        return super.getRenderType(entity, bodyVisible, translucent, glowing);
    }

    @Override
    protected float getFlipDegrees(T entity) {
        return 0.0F;
    }

    static boolean isServant(AbstractHurricane entity) {
        return entity instanceof HurricaneServant && !entity.isHostile();
    }

    static float windOffset(AbstractHurricane entity, float partialTicks) {
        return (((float) entity.tickCount + partialTicks) * WIND_SCROLL_SPEED) % 1.0F;
    }

    @OnlyIn(Dist.CLIENT)
    public static class HeavyCoreLayer<T extends AbstractHurricane> extends RenderLayer<T, HurricaneModel<T>> {
        public HeavyCoreLayer(RenderLayerParent<T, HurricaneModel<T>> parent) {
            super(parent);
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            HurricaneModel<T> model = this.getParentModel();
            model.showHeavyCore();
            VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucent(isServant(entity) ? SERVANT_HEAVY_CORE_TEXTURE : HEAVY_CORE_TEXTURE));
            model.renderToBuffer(poseStack, consumer, packedLight, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
            model.showBody();
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class WindLayer<T extends AbstractHurricane> extends RenderLayer<T, HurricaneModel<T>> {
        public WindLayer(RenderLayerParent<T, HurricaneModel<T>> parent) {
            super(parent);
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            HurricaneModel<T> model = this.getParentModel();
            model.showWind();
            VertexConsumer consumer = buffer.getBuffer(WindChargeRenderTypes.breezeWind(WIND_TEXTURE, windOffset(entity, partialTicks), 0.0F));
            model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            model.showBody();
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class TornadoLayer<T extends AbstractHurricane> extends RenderLayer<T, HurricaneModel<T>> {
        private final HurricaneTornadoModel model;

        public TornadoLayer(EntityRendererProvider.Context context, RenderLayerParent<T, HurricaneModel<T>> parent) {
            super(parent);
            this.model = new HurricaneTornadoModel(context.bakeLayer(ModModelLayers.BREEZE_SERVANT_WIND));
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            poseStack.pushPose();
            poseStack.translate(0.0F, 1.5F, 0.0F);
            poseStack.scale(TORNADO_SCALE, TORNADO_SCALE, TORNADO_SCALE);
            poseStack.translate(0.0F, -1.5F, 0.0F);
            this.model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            VertexConsumer consumer = buffer.getBuffer(WindChargeRenderTypes.breezeWind(BREEZE_WIND_TEXTURE, windOffset(entity, partialTicks), 0.0F));
            this.model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            poseStack.popPose();
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class EyesLayer<T extends AbstractHurricane> extends RenderLayer<T, HurricaneModel<T>> {
        private static final RenderType LAYER = RenderType.entityTranslucentEmissive(LAYER_TEXTURE, false);

        public EyesLayer(RenderLayerParent<T, HurricaneModel<T>> parent) {
            super(parent);
        }

        public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (entity.isOminousBuffed()) {
                return;
            }
            HurricaneModel<T> model = this.getParentModel();
            model.showBody();
            VertexConsumer consumer = buffer.getBuffer(LAYER);
            model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class OminousEyesLayer<T extends AbstractHurricane> extends RenderLayer<T, HurricaneModel<T>> {
        private static final RenderType LAYER = RenderType.entityTranslucentEmissive(OMINOUS_LAYER_TEXTURE, false);

        public OminousEyesLayer(RenderLayerParent<T, HurricaneModel<T>> parent) {
            super(parent);
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (!entity.isOminousBuffed()) {
                return;
            }
            HurricaneModel<T> model = this.getParentModel();
            model.showBody();
            VertexConsumer consumer = buffer.getBuffer(LAYER);
            model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}
