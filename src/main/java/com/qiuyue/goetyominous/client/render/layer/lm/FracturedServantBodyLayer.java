package com.qiuyue.goetyominous.client.render.layer.lm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.client.render.model.lm.FracturedServantModel;
import com.qiuyue.goetyominous.common.entities.ally.lm.FracturedServant;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FracturedServantBodyLayer
        extends RenderLayer<FracturedServant, FracturedServantModel<FracturedServant>> {

    private static final ResourceLocation BODY = new ResourceLocation(
            "legendary_monsters", "textures/entity/fractured/fractured.png");

    public FracturedServantBodyLayer(
            RenderLayerParent<FracturedServant, FracturedServantModel<FracturedServant>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                       FracturedServant entity, float limbSwing, float limbSwingAmount,
                       float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        float alpha = Math.max(1.0F - Math.min(entity.bodyFadeAway.getAnimationFraction(), 1.0F) - 0.5F, 0.0F);
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucentEmissive(BODY));
        int overlay = LivingEntityRenderer.getOverlayCoords(entity, 0.0F);
        this.getParentModel().renderToBuffer(poseStack, consumer, packedLight, overlay,
                1.0F, 1.0F, 1.0F, alpha);
    }
}
