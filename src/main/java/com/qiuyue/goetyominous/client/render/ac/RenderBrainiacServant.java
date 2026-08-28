package com.qiuyue.goetyominous.client.render.ac;

import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.client.render.model.ac.ModelBrainiacServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.BrainiacServant;
import net.minecraft.client.Minecraft;
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
public class RenderBrainiacServant extends MobRenderer<BrainiacServant, ModelBrainiacServant> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("alexscaves:textures/entity/brainiac.png");
    private static final ResourceLocation TEXTURE_EYES = new ResourceLocation("alexscaves:textures/entity/brainiac_glow.png");

    public RenderBrainiacServant(EntityRendererProvider.Context context) {
        super(context, new ModelBrainiacServant(), 0.25F);
        this.addLayer(new LayerBarrel(this));
        this.addLayer(new LayerGlow(this));
    }

    @Override
    public ResourceLocation getTextureLocation(BrainiacServant entity) {
        return TEXTURE;
    }

    

    public static class LayerGlow extends RenderLayer<BrainiacServant, ModelBrainiacServant> {

        public LayerGlow(RenderLayerParent<BrainiacServant, ModelBrainiacServant> renderLayerParent) {
            super(renderLayerParent);
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, BrainiacServant entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            VertexConsumer glowBuffer = bufferSource.getBuffer(RenderType.eyes(TEXTURE_EYES));
            this.getParentModel().renderToBuffer(poseStack, glowBuffer, packedLight, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    public static class LayerBarrel extends RenderLayer<BrainiacServant, ModelBrainiacServant> {

        public LayerBarrel(RenderLayerParent<BrainiacServant, ModelBrainiacServant> renderLayerParent) {
            super(renderLayerParent);
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, BrainiacServant entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (entity.hasBarrel()) {
                boolean hand = (entity.getAnimation() == BrainiacServant.ANIMATION_THROW_BARREL || entity.getAnimation() == BrainiacServant.ANIMATION_DRINK_BARREL) && entity.getAnimationTick() > 10;
                poseStack.pushPose();
                ((ModelBrainiacServant) this.getParentModel()).translateToArmOrChest(poseStack, hand);
                poseStack.translate(-0.5F, -0.7F, 1.01F);
                if (hand) {
                    poseStack.translate(1.25F, 2.1F, -1.5F);
                    poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
                } else {
                    poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
                }
                Minecraft.getInstance().getBlockRenderer().renderSingleBlock(ACBlockRegistry.WASTE_DRUM.get().defaultBlockState(), poseStack, bufferSource, packedLight, OverlayTexture.NO_OVERLAY);
                poseStack.popPose();
            }
        }
    }
}
