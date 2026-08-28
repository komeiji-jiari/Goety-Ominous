package com.qiuyue.goetyominous.client.render.am;

import com.github.alexthe666.citadel.client.model.AdvancedEntityModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.common.entities.ally.am.ServantCentipedeHead;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class LayerServantCentipedeHeadEyes extends RenderLayer<ServantCentipedeHead, AdvancedEntityModel<ServantCentipedeHead>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/cave_centipede_eyes.png");

    public LayerServantCentipedeHeadEyes(RenderServantCentipedeHead render) {
        super(render);
    }

    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, ServantCentipedeHead entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.eyes(TEXTURE));
        ((AdvancedEntityModel)this.getParentModel()).renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, LivingEntityRenderer.getOverlayCoords(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
