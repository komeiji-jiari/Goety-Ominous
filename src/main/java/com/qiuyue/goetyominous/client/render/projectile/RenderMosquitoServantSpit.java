package com.qiuyue.goetyominous.client.render.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.common.entities.projectile.EntityMosquitoServantSpit;
import net.minecraft.client.model.LlamaSpitModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.LlamaSpit;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderMosquitoServantSpit extends EntityRenderer<EntityMosquitoServantSpit> {
    private static final ResourceLocation SPIT_TEXTURE = new ResourceLocation("alexsmobs", "textures/entity/mosquito_spit.png");
    private final LlamaSpitModel<LlamaSpit> model;

    public RenderMosquitoServantSpit(EntityRendererProvider.Context context) {
        super(context);
        this.model = new LlamaSpitModel<LlamaSpit>(context.bakeLayer(ModelLayers.LLAMA_SPIT));
    }

    @Override
    public void render(EntityMosquitoServantSpit entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0.0D, 0.15D, 0.0D);
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTicks, entity.xRotO, entity.getXRot())));
        // 与 AlexMobs 的 RenderMosquitoSpit 保持一致：固定全亮光照，避免黑暗中变暗
        VertexConsumer ivertexbuilder = buffer.getBuffer(this.model.renderType(this.getTextureLocation(entity)));
        this.model.renderToBuffer(poseStack, ivertexbuilder, 240, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(EntityMosquitoServantSpit entity) {
        return SPIT_TEXTURE;
    }
}
