package com.qiuyue.goetyominous.client.render.lm;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.common.entities.ally.lm.projectile.SoulJavelin;
import net.miauczel.legendary_monsters.entity.ProjectileEntityRenderer.SoulJavelinModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SoulJavelinServantRenderer extends EntityRenderer<SoulJavelin> {

    private static final ResourceLocation SOUL_JAVELIN = new ResourceLocation(
            "legendary_monsters",
            "textures/entity/resurrected_knight/javelin.png");
    private static final ResourceLocation SOUL_JAVELIN_RED = new ResourceLocation(
            "goetyominous",
            "textures/entity/lm_knight/resurrected_knight_javelin_red.png");

    private final SoulJavelinModel model;

    public SoulJavelinServantRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new SoulJavelinModel(SoulJavelinModel.createBodyLayer().bakeRoot());
    }

    @Override
    public void render(SoulJavelin entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot())));
        poseStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTicks, -entity.xRotO, -entity.getXRot())));

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        VertexConsumer consumer = ItemRenderer.getFoilBuffer(buffer,
                this.model.renderType(this.getTextureLocation(entity)), false, false);
        this.model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY,
                1.0F, 1.0F, 1.0F, 0.5F);
        RenderSystem.disableBlend();

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(SoulJavelin entity) {
        return entity.isEnhanced() ? SOUL_JAVELIN_RED : SOUL_JAVELIN;
    }
}
