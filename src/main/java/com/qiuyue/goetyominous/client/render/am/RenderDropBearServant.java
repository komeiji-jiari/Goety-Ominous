package com.qiuyue.goetyominous.client.render.am;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.client.render.model.am.ModelDropBearServant;
import com.qiuyue.goetyominous.common.entities.ally.am.DropBearServant;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderDropBearServant extends MobRenderer<DropBearServant, ModelDropBearServant> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("goetyominous:textures/entity/dropbear.png");
    private static final ResourceLocation TEXTURE_EYES = new ResourceLocation("goetyominous:textures/entity/dropbear_eyes.png");

    public RenderDropBearServant(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelDropBearServant(), 0.7F);
        this.addLayer(new EyeLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(DropBearServant entity) {
        return TEXTURE;
    }

    @OnlyIn(Dist.CLIENT)
    private static class EyeLayer extends RenderLayer<DropBearServant, ModelDropBearServant> {

        public EyeLayer(RenderDropBearServant renderer) {
            super(renderer);
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, DropBearServant entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.eyes(TEXTURE_EYES));
            this.getParentModel().renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}
