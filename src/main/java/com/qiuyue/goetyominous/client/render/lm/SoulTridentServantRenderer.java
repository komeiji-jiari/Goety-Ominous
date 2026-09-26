package com.qiuyue.goetyominous.client.render.lm;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.common.entities.ally.lm.projectile.SoulTrident;
import net.miauczel.legendary_monsters.entity.ProjectileEntityRenderer.SoulTridentModel;
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
public class SoulTridentServantRenderer extends EntityRenderer<SoulTrident> {

    private static final ResourceLocation SOUL_TRIDENT = new ResourceLocation(
            "legendary_monsters",
            "textures/entity/posessed_paladin/layer/possessed_paladin_trident_red_layer.png");

    private static final float MODEL_SCALE = 1.5F;

    private final SoulTridentModel model;

    public SoulTridentServantRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new SoulTridentModel(SoulTridentModel.createBodyLayer().bakeRoot());
    }

    @Override
    public void render(SoulTrident entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot())));
        poseStack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(partialTicks, -entity.xRotO, -entity.getXRot())));
        poseStack.scale(MODEL_SCALE, MODEL_SCALE, MODEL_SCALE);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        VertexConsumer consumer = ItemRenderer.getFoilBuffer(buffer,
                this.model.renderType(this.getTextureLocation(entity)), false, entity.isFoil());
        this.model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY,
                1.0F, 1.0F, 1.0F, 0.5F);
        RenderSystem.disableBlend();

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(SoulTrident entity) {
        return SOUL_TRIDENT;
    }
}
