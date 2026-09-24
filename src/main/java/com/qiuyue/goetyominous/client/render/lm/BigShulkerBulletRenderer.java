package com.qiuyue.goetyominous.client.render.lm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.common.entities.ally.lm.projectile.BigShulkerBullet;
import net.miauczel.legendary_monsters.entity.client.Model.BigShulkerBulletModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BigShulkerBulletRenderer extends EntityRenderer<BigShulkerBullet> {

    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/entity/shulker/spark.png");
    private static final RenderType RENDER_TYPE = RenderType.entityTranslucent(TEXTURE_LOCATION);

    private final BigShulkerBulletModel<BigShulkerBullet> model;

    public BigShulkerBulletRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new BigShulkerBulletModel<>(context.bakeLayer(ModEntityLayers.BIG_SHULKER_BULLET_LAYER));
    }

    @Override
    public void render(BigShulkerBullet entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        float yaw = Mth.rotLerp(partialTicks, entity.yRotO, entity.getYRot());
        float pitch = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());
        float ticks = entity.tickCount + partialTicks;
        poseStack.translate(0.0F, 0.5F, 0.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.sin(ticks * 0.1F) * 180.0F));
        poseStack.mulPose(Axis.XP.rotationDegrees(Mth.cos(ticks * 0.1F) * 180.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.sin(ticks * 0.15F) * 360.0F));
        float size = entity.getBulletSize();
        poseStack.scale(2.0F * size, 2.0F * size, 2.0F * size);
        this.model.setupAnim(entity, 0.0F, 0.0F, 0.0F, yaw, pitch);
        VertexConsumer solid = buffer.getBuffer(this.model.renderType(TEXTURE_LOCATION));
        this.model.renderToBuffer(poseStack, solid, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.scale(1.5F, 1.5F, 1.5F);
        VertexConsumer glow = buffer.getBuffer(RENDER_TYPE);
        this.model.renderToBuffer(poseStack, glow, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 0.15F);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(BigShulkerBullet entity) {
        return TEXTURE_LOCATION;
    }
}
