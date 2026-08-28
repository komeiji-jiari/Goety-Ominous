package com.qiuyue.goetyominous.client.render.am;

import com.github.alexthe666.alexsmobs.client.render.AMRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.client.render.model.am.ModelServantVoidWormShot;
import com.qiuyue.goetyominous.common.entities.projectile.EntityServantVoidWormShot;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderServantVoidWormShot extends EntityRenderer<EntityServantVoidWormShot> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/void_worm/void_worm_shot.png");
    private static final ModelServantVoidWormShot MODEL = new ModelServantVoidWormShot();

    public RenderServantVoidWormShot(EntityRendererProvider.Context renderManager) {
        super(renderManager);
    }

    @Override
    public ResourceLocation getTextureLocation(EntityServantVoidWormShot entity) {
        return TEXTURE;
    }

    @Override
    public void render(EntityServantVoidWormShot entityIn, float entityYaw, float partialTicks,
                       PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        matrixStackIn.pushPose();
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(180.0F));
        matrixStackIn.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot())));
        matrixStackIn.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot())));
        matrixStackIn.pushPose();
        MODEL.animate(entityIn, (float) entityIn.tickCount + partialTicks);
        float home = (entityIn.prevStopHomingProgress + (entityIn.getStopHomingProgress() - entityIn.prevStopHomingProgress) * partialTicks) / 40.0F;
        matrixStackIn.translate(0.0F, -1.5F, 0.0F);
        VertexConsumer vertexconsumer = bufferIn.getBuffer(AMRenderTypes.getFullBright(this.getTextureLocation(entityIn)));
        MODEL.renderToBuffer(matrixStackIn, vertexconsumer, 210, OverlayTexture.NO_OVERLAY,
                Math.max(home, 0.2F), Math.max(home, 0.2F), 1.0F, 1.0F);
        matrixStackIn.popPose();
        matrixStackIn.popPose();
    }
}
