package com.qiuyue.goetyominous.client.render.projectile.mm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.common.entities.ally.mm.MutantShulkerServantTrapBullet;
import net.minecraft.client.model.ShulkerBulletModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class MutantShulkerServantTrapBulletRenderer extends EntityRenderer<MutantShulkerServantTrapBullet> {
    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/entity/shulker/spark.png");
    private static final RenderType RENDER_TYPE = RenderType.entityTranslucent(TEXTURE_LOCATION);
    private final ShulkerBulletModel<MutantShulkerServantTrapBullet> model;

    public MutantShulkerServantTrapBulletRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new ShulkerBulletModel<>(context.bakeLayer(ModelLayers.SHULKER_BULLET));
    }

    @Override
    protected int getBlockLightLevel(MutantShulkerServantTrapBullet entity, BlockPos pos) {
        return 15;
    }

    @Override
    public void render(MutantShulkerServantTrapBullet entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        float yRot = Mth.rotLerp(entity.yRotO, entity.getYRot(), partialTick);
        float xRot = Mth.lerp(partialTick, entity.xRotO, entity.getXRot());
        float age = (float) entity.tickCount + partialTick;
        poseStack.translate(0.0D, 0.15F, 0.0D);
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.sin(age * 0.1F) * 180.0F));
        poseStack.mulPose(Axis.XP.rotationDegrees(Mth.cos(age * 0.1F) * 180.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.sin(age * 0.15F) * 360.0F));
        poseStack.scale(-0.5F, -0.5F, 0.5F);
        this.model.setupAnim(entity, 0.0F, 0.0F, 0.0F, yRot, xRot);
        VertexConsumer vertexConsumer = buffer.getBuffer(this.model.renderType(TEXTURE_LOCATION));
        this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.scale(1.5F, 1.5F, 1.5F);
        VertexConsumer glowConsumer = buffer.getBuffer(RENDER_TYPE);
        this.model.renderToBuffer(poseStack, glowConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 0.15F);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(MutantShulkerServantTrapBullet entity) {
        return TEXTURE_LOCATION;
    }
}
