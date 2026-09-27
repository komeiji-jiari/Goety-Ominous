package com.qiuyue.goetyominous.client.render.lm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.client.render.model.lm.TheObliteratorServantModel;
import com.qiuyue.goetyominous.common.entities.ally.lm.TheObliteratorServant;
import net.miauczel.legendary_monsters.entity.client.Render.LMRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TheObliteratorServantPowerBallInnerLayer extends RenderLayer<TheObliteratorServant, TheObliteratorServantModel<TheObliteratorServant>> {

    private static final ResourceLocation LOCATION = new ResourceLocation("legendary_monsters", "textures/entity/the_warped_one/ball/inner_ball_layer.png");

    public TheObliteratorServantPowerBallInnerLayer(TheObliteratorServantRenderer renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, TheObliteratorServant entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        RenderType eyes = LMRenderTypes.getGlowEyes(LOCATION);
        VertexConsumer vertexConsumer = buffer.getBuffer(eyes);
        if (entity.isShootingClusterBomb()) {
            this.getParentModel().renderToBuffer(poseStack, vertexConsumer, 0xF00000, OverlayTexture.NO_OVERLAY, 0.95F, 0.95F, 0.95F, 1.0F);
        } else {
            this.getParentModel().renderToBuffer(poseStack, vertexConsumer, 0xF00000, OverlayTexture.NO_OVERLAY, 0.0F, 0.0F, 0.0F, 0.0F);
        }
    }
}
