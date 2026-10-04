package com.qiuyue.goetyominous.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.render.model.BreezeEntityModel;
import com.qiuyue.goetyominous.client.render.projectile.WindChargeRenderTypes;
import com.qiuyue.goetyominous.common.entities.hostile.BreezeEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BreezeRenderer extends MobRenderer<BreezeEntity, BreezeEntityModel<BreezeEntity>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/breeze.png");

    public BreezeRenderer(EntityRendererProvider.Context context) {
        super(context, new BreezeEntityModel<>(context.bakeLayer(ModModelLayers.BREEZE_SERVANT)), 0.5F);
        this.addLayer(new WindLayer(context, this));
        this.addLayer(new EyesLayer(this));
    }

    @Override
    public void render(BreezeEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        BreezeEntityModel<BreezeEntity> model = this.getModel();
        enable(model, model.head(), model.rods());
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(BreezeEntity entity) {
        return TEXTURE;
    }

    public static BreezeEntityModel<BreezeEntity> enable(BreezeEntityModel<BreezeEntity> model, ModelPart... parts) {
        model.head().visible = false;
        model.eyes().visible = false;
        model.rods().visible = false;
        model.wind().visible = false;
        for (ModelPart part : parts) {
            part.visible = true;
        }
        return model;
    }

    @OnlyIn(Dist.CLIENT)
    public static class WindLayer extends RenderLayer<BreezeEntity, BreezeEntityModel<BreezeEntity>> {
        private static final ResourceLocation WIND_TEXTURE = new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/breeze_wind.png");
        private final BreezeEntityModel<BreezeEntity> model;

        public WindLayer(EntityRendererProvider.Context context, RenderLayerParent<BreezeEntity, BreezeEntityModel<BreezeEntity>> parent) {
            super(parent);
            this.model = new BreezeEntityModel<>(context.bakeLayer(ModModelLayers.BREEZE_SERVANT_WIND));
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, BreezeEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            float time = (float) entity.tickCount + partialTicks;
            VertexConsumer consumer = buffer.getBuffer(WindChargeRenderTypes.breezeWind(WIND_TEXTURE, this.xOffset(time) % 1.0F, 0.0F));
            this.model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            enable(this.model, this.model.wind()).renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        }

        private float xOffset(float time) {
            return time * 0.02F;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class EyesLayer extends RenderLayer<BreezeEntity, BreezeEntityModel<BreezeEntity>> {
        private static final RenderType EYES = RenderType.entityTranslucentEmissive(
                new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/breeze_eyes.png"), false);

        public EyesLayer(RenderLayerParent<BreezeEntity, BreezeEntityModel<BreezeEntity>> parent) {
            super(parent);
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, BreezeEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            VertexConsumer consumer = buffer.getBuffer(EYES);
            BreezeEntityModel<BreezeEntity> model = this.getParentModel();
            enable(model, model.head(), model.eyes()).renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}
