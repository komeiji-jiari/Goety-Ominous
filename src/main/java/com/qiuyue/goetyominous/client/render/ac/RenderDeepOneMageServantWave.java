package com.qiuyue.goetyominous.client.render.ac;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.client.render.model.ac.ModelDeepOneMageServantWave;
import com.qiuyue.goetyominous.common.entities.projectile.DeepOneMageServantWave;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderDeepOneMageServantWave extends EntityRenderer<DeepOneMageServantWave> {

    private static final ResourceLocation TEXTURE_0 = new ResourceLocation("alexscaves", "textures/entity/deep_one/wave_0.png");
    private static final ResourceLocation TEXTURE_1 = new ResourceLocation("alexscaves", "textures/entity/deep_one/wave_1.png");
    private static final ResourceLocation TEXTURE_2 = new ResourceLocation("alexscaves", "textures/entity/deep_one/wave_2.png");
    private static final ResourceLocation TEXTURE_3 = new ResourceLocation("alexscaves", "textures/entity/deep_one/wave_3.png");
    private static final ResourceLocation OVERLAY_TEXTURE_0 = new ResourceLocation("alexscaves", "textures/entity/deep_one/wave_overlay_0.png");
    private static final ResourceLocation OVERLAY_TEXTURE_1 = new ResourceLocation("alexscaves", "textures/entity/deep_one/wave_overlay_1.png");
    private static final ResourceLocation OVERLAY_TEXTURE_2 = new ResourceLocation("alexscaves", "textures/entity/deep_one/wave_overlay_2.png");
    private static final ResourceLocation OVERLAY_TEXTURE_3 = new ResourceLocation("alexscaves", "textures/entity/deep_one/wave_overlay_3.png");
    private static final ModelDeepOneMageServantWave MODEL = new ModelDeepOneMageServantWave();

    public RenderDeepOneMageServantWave(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn);
    }

    @Override
    public void render(DeepOneMageServantWave entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn,
                       MultiBufferSource bufferIn, int packedLightIn) {
        if (!entityIn.isInvisible()) {
            matrixStackIn.pushPose();
            matrixStackIn.translate(0.0F, 1.5F, 0.0F);
            matrixStackIn.mulPose(Axis.YN.rotationDegrees(Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot()) + 180.0F));
            float ageInTicks = (float) entityIn.activeWaveTicks + partialTicks;
            float f = ageInTicks / 10.0F;
            matrixStackIn.translate(0.0F, -0.1F + (1.0F - f) * -1.0F, -0.5F);
            matrixStackIn.scale(1.0F, -(0.2F + f * 0.9F), 1.0F);
            MODEL.setupAnim(entityIn, 0.0F, 0.0F, ageInTicks, 0.0F, 0.0F);
            VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.entityTranslucent(this.getWaveTexture(entityIn.activeWaveTicks)));
            int waterColorAt = ((Biome) entityIn.level().getBiome(entityIn.blockPosition()).get()).getWaterColor();
            float colorR = (float) (waterColorAt >> 16 & 255) / 255.0F;
            float colorG = (float) (waterColorAt >> 8 & 255) / 255.0F;
            float colorB = (float) (waterColorAt & 255) / 255.0F;
            MODEL.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, colorR, colorG, colorB, 1.0F);
            VertexConsumer ivertexbuilder2 = bufferIn.getBuffer(RenderType.entityTranslucent(this.getOverlayTexture(entityIn.activeWaveTicks)));
            MODEL.renderToBuffer(matrixStackIn, ivertexbuilder2, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            matrixStackIn.popPose();
            super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(DeepOneMageServantWave entity) {
        return this.getWaveTexture(entity.activeWaveTicks);
    }

    private ResourceLocation getWaveTexture(int tickCount) {
        return switch (tickCount % 12 / 3) {
            case 0 -> TEXTURE_0;
            case 1 -> TEXTURE_1;
            case 2 -> TEXTURE_2;
            default -> TEXTURE_3;
        };
    }

    private ResourceLocation getOverlayTexture(int tickCount) {
        return switch (tickCount % 12 / 3) {
            case 0 -> OVERLAY_TEXTURE_0;
            case 1 -> OVERLAY_TEXTURE_1;
            case 2 -> OVERLAY_TEXTURE_2;
            default -> OVERLAY_TEXTURE_3;
        };
    }
}
