package com.qiuyue.goetyominous.client.render.am;

import com.github.alexthe666.alexsmobs.client.render.AMRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.client.render.model.am.ModelZombieCrocodileServant;
import com.qiuyue.goetyominous.common.entities.ally.am.ZombieCrocodileServant;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderZombieCrocodileServant extends MobRenderer<ZombieCrocodileServant, ModelZombieCrocodileServant> {
    private static final ResourceLocation TEXTURE_0 = new ResourceLocation("goetyominous:textures/entity/crocodile_0.png");
    private static final ResourceLocation TEXTURE_1 = new ResourceLocation("alexsmobs:textures/entity/crocodile_1.png");
    private static final ResourceLocation TEXTURE_CROWN = new ResourceLocation("alexsmobs:textures/entity/crocodile_crown.png");

    public RenderZombieCrocodileServant(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelZombieCrocodileServant(), 0.8F);
        this.addLayer(new CrownLayer(this));
    }

    @Override
    protected void scale(ZombieCrocodileServant entity, PoseStack matrixStack, float partialTicks) {
        this.model.young = entity.isBaby();
        matrixStack.scale(0.9F, 0.9F, 0.9F);
    }

    @Override
    public ResourceLocation getTextureLocation(ZombieCrocodileServant entity) {
        return entity.isDesert() ? TEXTURE_1 : TEXTURE_0;
    }

    static class CrownLayer extends RenderLayer<ZombieCrocodileServant, ModelZombieCrocodileServant> {

        public CrownLayer(RenderZombieCrocodileServant p_i50928_1_) {
            super(p_i50928_1_);
        }

        public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, ZombieCrocodileServant entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
            if (entitylivingbaseIn.isCrowned()) {
                VertexConsumer shoeBuffer = bufferIn.getBuffer(AMRenderTypes.entityCutoutNoCull(TEXTURE_CROWN));
                matrixStackIn.pushPose();
                this.getParentModel().renderToBuffer(matrixStackIn, shoeBuffer, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
                matrixStackIn.popPose();
            }
        }
    }
}
