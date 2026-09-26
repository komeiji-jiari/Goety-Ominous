package com.qiuyue.goetyominous.client.render.lm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.lm.ThrownPhantomDaggerModel;
import com.qiuyue.goetyominous.common.entities.ally.lm.projectile.ThrownPhantomDagger;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ThrownPhantomDaggerRenderer extends EntityRenderer<ThrownPhantomDagger> {

    public static final ResourceLocation DAGGER_TEXTURE = new ResourceLocation(
            "legendary_monsters", "textures/entity/posessed_paladin/phantom_dagger.png");
    public static final ResourceLocation DAGGER_RED_TEXTURE = new ResourceLocation(
            "legendary_monsters", "textures/entity/posessed_paladin/phantom_dagger_red.png");

    private final ThrownPhantomDaggerModel model;

    public ThrownPhantomDaggerRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new ThrownPhantomDaggerModel(context.bakeLayer(ModEntityLayers.PHANTOM_DAGGER));
    }

    @Override
    public void render(ThrownPhantomDagger entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        float yaw = -entity.getYRot();
        float pitch = entity.getXRot();
        this.model.setupAnim(entity, 0.0F, 0.0F, (float) entity.tickCount + partialTicks, yaw, pitch);

        poseStack.translate(0.0F, -2.5F, 0.0F);
        float scale = 1.75F;
        poseStack.scale(scale, scale, scale);

        float animationProgress = Math.min(entity.fade.getAnimationFraction(), 1.0F);
        float alpha = Math.max(1.0F - animationProgress - 0.45F, 0.0F);

        VertexConsumer vertexConsumer = buffer.getBuffer(this.model.renderType(this.getTextureLocation(entity)));
        this.model.renderToBuffer(poseStack, vertexConsumer, 15728880, OverlayTexture.NO_OVERLAY,
                1.0F, 1.0F, 1.0F, alpha);

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(ThrownPhantomDagger entity) {
        return entity.getRed() ? DAGGER_RED_TEXTURE : DAGGER_TEXTURE;
    }
}
