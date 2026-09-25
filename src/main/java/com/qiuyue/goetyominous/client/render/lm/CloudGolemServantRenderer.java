package com.qiuyue.goetyominous.client.render.lm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.lm.CloudGolemServantModel;
import com.qiuyue.goetyominous.common.entities.ally.lm.CloudGolemServant;
import net.miauczel.legendary_monsters.entity.client.Render.LMRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CloudGolemServantRenderer extends MobRenderer<CloudGolemServant, CloudGolemServantModel<CloudGolemServant>> {

    private static final ResourceLocation NORMAL2 = new ResourceLocation("legendary_monsters", "textures/entity/cloud_golem/cloud_golem_break1.png");
    private static final ResourceLocation ANGRY2 = new ResourceLocation("legendary_monsters", "textures/entity/cloud_golem/cloud_golem_angry_break.png");
    private static final ResourceLocation NORMAL = new ResourceLocation("legendary_monsters", "textures/entity/cloud_golem/cloud_golem.png");
    private static final ResourceLocation ANGRY = new ResourceLocation("legendary_monsters", "textures/entity/cloud_golem/cloud_golem_angry2.png");
    private static final ResourceLocation GLOW3 = new ResourceLocation("legendary_monsters", "textures/entity/cloud_golem/glow/cloud_golem_angry_glow3.png");
    private static final ResourceLocation GLOW4 = new ResourceLocation("legendary_monsters", "textures/entity/cloud_golem/glow/cloud_golem_angry_glow4.png");

    public CloudGolemServantRenderer(EntityRendererProvider.Context context) {
        super(context, new CloudGolemServantModel(
                context.bakeLayer(ModEntityLayers.CLOUD_GOLEM_SERVANT_LAYER)), 1.5F);
        this.addLayer(new LaserBallInnerLayer(this));
        this.addLayer(new LaserBallOuterLayer(this));
        this.addLayer(new EyesLayer<CloudGolemServant, CloudGolemServantModel<CloudGolemServant>>(this) {

            @Override
            public RenderType renderType() {
                return RenderType.eyes(GLOW3);
            }

            @Override
            public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, CloudGolemServant entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
                float alpha = Math.abs((float) Math.sin(entity.LayerTicks * 0.04));
                alpha = Math.min(1.0F, Math.max(0.0F, alpha));
                if (entity.getAttackState() != 32) {
                    if (entity.isAngry()) {
                        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.eyes(GLOW3));
                        this.getParentModel().renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, alpha, alpha, alpha, alpha);
                    } else {
                        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.eyes(GLOW4));
                        this.getParentModel().renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, alpha, alpha, alpha, alpha);
                    }
                } else {
                    VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.eyes(GLOW3));
                    this.getParentModel().renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 0.0F, 0.0F, 0.0F, 0.0F);
                }
            }
        });
    }

    @Override
    public ResourceLocation getTextureLocation(CloudGolemServant entity) {
        switch (entity.getTextureVariant()) {
            case 1: {
                return ANGRY;
            }
            default: {
                return NORMAL;
            }
            case 2: {
                return NORMAL2;
            }
            case 3:
        }
        return ANGRY2;
    }

    @OnlyIn(Dist.CLIENT)
    private static class LaserBallInnerLayer extends RenderLayer<CloudGolemServant, CloudGolemServantModel<CloudGolemServant>> {

        private static final RenderType GLOW = LMRenderTypes.getGlowEyes(new ResourceLocation(
                "legendary_monsters", "textures/entity/cloud_golem/laser_ball/laser_ball_inner.png"));

        public LaserBallInnerLayer(RenderLayerParent<CloudGolemServant, CloudGolemServantModel<CloudGolemServant>> renderer) {
            super(renderer);
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, CloudGolemServant entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            VertexConsumer vertexConsumer = buffer.getBuffer(GLOW);
            if (entity.getAttackState() == 14) {
                this.getParentModel().renderToBuffer(poseStack, vertexConsumer, 0xF00000, OverlayTexture.NO_OVERLAY, 0.76F, 0.76F, 0.76F, 0.6F);
            } else {
                this.getParentModel().renderToBuffer(poseStack, vertexConsumer, 0xF00000, OverlayTexture.NO_OVERLAY, 0.0F, 0.0F, 0.0F, 0.0F);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static class LaserBallOuterLayer extends RenderLayer<CloudGolemServant, CloudGolemServantModel<CloudGolemServant>> {

        private static final RenderType GLOW = LMRenderTypes.getGlowEyes(new ResourceLocation(
                "legendary_monsters", "textures/entity/cloud_golem/laser_ball/laser_ball_outer.png"));

        public LaserBallOuterLayer(RenderLayerParent<CloudGolemServant, CloudGolemServantModel<CloudGolemServant>> renderer) {
            super(renderer);
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, CloudGolemServant entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            VertexConsumer vertexConsumer = buffer.getBuffer(GLOW);
            if (entity.getAttackState() == 14) {
                this.getParentModel().renderToBuffer(poseStack, vertexConsumer, 0xF00000, OverlayTexture.NO_OVERLAY, 0.5F, 0.5F, 0.5F, 0.4F);
            } else {
                this.getParentModel().renderToBuffer(poseStack, vertexConsumer, 0xF00000, OverlayTexture.NO_OVERLAY, 0.0F, 0.0F, 0.0F, 0.0F);
            }
        }
    }
}
