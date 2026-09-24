package com.qiuyue.goetyominous.client.render.ac;

import com.github.alexmodguy.alexscaves.client.render.ACRenderTypes;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.client.render.model.ac.ModelTremorsaurusServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.TremorsaurusSpiritEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderTremorsaurusSpirit extends EntityRenderer<TremorsaurusSpiritEntity> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("alexscaves", "textures/entity/tremorsaurus.png");
    private static final ModelTremorsaurusServant MODEL = new ModelTremorsaurusServant();

    public RenderTremorsaurusSpirit(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(TremorsaurusSpiritEntity entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0.0D, 1.5D, 0.0D);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - entity.getViewYRot(partialTicks)));
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.mulPose(Axis.XN.rotationDegrees(entity.getViewXRot(partialTicks)));
        float alpha = entity.getFadeIn(partialTicks);

        boolean prevBaby = MODEL.young;
        MODEL.young = false;
        VertexConsumer consumer = buffer.getBuffer(ACRenderTypes.getRedGhost(TEXTURE));
        MODEL.animateSpirit(entity, partialTicks);
        MODEL.renderSpiritToBuffer(poseStack, consumer, 240, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, alpha);
        MODEL.young = prevBaby;

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(TremorsaurusSpiritEntity entity) {
        return TEXTURE;
    }
}
