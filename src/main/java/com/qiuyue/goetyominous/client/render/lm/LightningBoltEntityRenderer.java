package com.qiuyue.goetyominous.client.render.lm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.common.entities.ally.lm.projectile.LightningBoltEntity;
import net.miauczel.legendary_monsters.entity.client.Model.LightningStrikeModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class LightningBoltEntityRenderer extends EntityRenderer<LightningBoltEntity> {

    private static final RandomSource RANDOM = RandomSource.create();
    private static final float LINEARITY = 0.8F;
    private static final ResourceLocation STRIKE_TEXTURE = new ResourceLocation(
            "legendary_monsters", "textures/entity/falling_cloud_angry.png");
    private static final ResourceLocation BOLT_TEXTURE = new ResourceLocation(
            "legendary_monsters", "textures/entity/cloud_golem/model.png");

    private final LightningStrikeModel<LightningBoltEntity> model;

    public LightningBoltEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new LightningStrikeModel<>(context.bakeLayer(ModEntityLayers.LM_LIGHTNING_STRIKE_LAYER));
    }

    @Override
    public void render(LightningBoltEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        float progress = entity.getAnimationProgress(partialTicks);

        poseStack.pushPose();
        if (progress != 0.0F) {
            float scale = 2.0F;
            if (progress > 0.9F) {
                scale *= (1.0F - progress) / 0.1F;
            }
            this.model.setupAnim(entity, progress, 0.0F, 0.0F, entity.getYRot(), entity.getXRot());
            VertexConsumer vertexConsumer = bufferSource.getBuffer(this.model.renderType(STRIKE_TEXTURE));
            this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            poseStack.scale(-scale, -scale, -scale);
        }
        poseStack.popPose();

        poseStack.pushPose();
        if (progress != 0.0F && Minecraft.getInstance().player != null) {
            double dirX = Minecraft.getInstance().player.getX() - entity.getX();
            double dirZ = Minecraft.getInstance().player.getZ() - entity.getZ();
            double magnitude = Math.sqrt(dirX * dirX + dirZ * dirZ);
            if (magnitude != 0.0D) {
                dirX /= magnitude;
                dirZ /= magnitude;
            }
            VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(entity)));
            float startX = 0.0F;
            float startZ = 0.0F;
            for (int i = 0; (float) i < 7.5F; ++i) {
                float maxDeviation = (1.0F - LINEARITY) * 2.0F;
                float endX = startX + RANDOM.nextFloat() * maxDeviation - maxDeviation / 2.0F;
                float endZ = startZ + RANDOM.nextFloat() * maxDeviation - maxDeviation / 2.0F;
                this.render3DSegment(poseStack.last().pose(), vertexConsumer, startX, startZ, i, endX, endZ, i + 1, dirX, dirZ, 0.8F, 0.8F, 1.0F, 0.7F);
                startX = endX;
                startZ = endZ;
            }
        }
        poseStack.popPose();

        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, 0xF000F0);
    }

    private void render3DSegment(Matrix4f matrix, VertexConsumer consumer, float startX, float startZ, int startY, float endX, float endZ, int endY, double dirX, double dirZ, float red, float green, float blue, float alpha) {
        float thickness = 0.1F;
        float perpX = (float) (-dirZ);
        float perpZ = (float) dirX;
        this.renderQuad(matrix, consumer, startX, startZ, startY, endX, endZ, endY, red, green, blue, alpha, perpX * thickness, perpZ * thickness);
        this.renderQuad(matrix, consumer, startX, startZ, startY, endX, endZ, endY, red, green, blue, alpha, -perpX * thickness, -perpZ * thickness);
    }

    private void renderQuad(Matrix4f matrix, VertexConsumer consumer, float x1, float z1, int y1, float x2, float z2, int y2, float red, float green, float blue, float alpha, float perpX, float perpZ) {
        consumer.vertex(matrix, x1 - perpX, (float) y1, z1 - perpZ).color(red, green, blue, alpha).uv(0.0F, 0.0F).overlayCoords(0, 10).uv2(0xF000F0).normal(0.0F, 1.0F, 0.0F).endVertex();
        consumer.vertex(matrix, x2 - perpX, (float) y2, z2 - perpZ).color(red, green, blue, alpha).uv(1.0F, 0.0F).overlayCoords(0, 10).uv2(0xF000F0).normal(0.0F, 1.0F, 0.0F).endVertex();
        consumer.vertex(matrix, x2 + perpX, (float) y2, z2 + perpZ).color(red, green, blue, alpha).uv(1.0F, 1.0F).overlayCoords(0, 10).uv2(0xF000F0).normal(0.0F, 1.0F, 0.0F).endVertex();
        consumer.vertex(matrix, x1 + perpX, (float) y1, z1 + perpZ).color(red, green, blue, alpha).uv(0.0F, 1.0F).overlayCoords(0, 10).uv2(0xF000F0).normal(0.0F, 1.0F, 0.0F).endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(LightningBoltEntity entity) {
        return BOLT_TEXTURE;
    }
}
