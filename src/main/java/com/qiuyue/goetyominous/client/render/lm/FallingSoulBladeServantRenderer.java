package com.qiuyue.goetyominous.client.render.lm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.client.render.model.lm.FallingSoulBladeModel;
import com.qiuyue.goetyominous.common.entities.ally.lm.projectile.FallingSoulBlade;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FallingSoulBladeServantRenderer extends EntityRenderer<FallingSoulBlade> {

    private static final ResourceLocation SOUL_BLADE = new ResourceLocation(
            "legendary_monsters", "textures/entity/posessed_paladin/soul_sword3.png");
    private static final ResourceLocation SOUL_BLADE_RED = new ResourceLocation(
            "legendary_monsters", "textures/entity/posessed_paladin/soul_sword_red.png");

    private final FallingSoulBladeModel model;

    public FallingSoulBladeServantRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new FallingSoulBladeModel(FallingSoulBladeModel.createBodyLayer().bakeRoot());
    }

    @Override
    public void render(FallingSoulBlade entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        float alpha = 1.0F - Math.min(entity.controlledAnim.getAnimationFraction(), 1.0F);

        this.model.setupAnim(entity, 0.0F, 0.0F, (float) entity.tickCount + partialTicks, 0.0F, 0.0F);
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees((float) entity.randomRot));

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucentEmissive(this.getTextureLocation(entity)));
        if (alpha > 0.0F) {
            this.model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY,
                    1.0F, 1.0F, 1.0F, alpha - 0.25F);
        }

        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(FallingSoulBlade entity) {
        return entity.getRed() ? SOUL_BLADE_RED : SOUL_BLADE;
    }
}
