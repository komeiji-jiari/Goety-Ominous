package com.qiuyue.goetyominous.client.render.am;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.client.render.model.am.ModelTusklinServant;
import com.qiuyue.goetyominous.common.entities.ally.am.TusklinServant;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * 獠牙兽仆从的装备层，移植 AlexMobs 原版 LayerTusklinGear：
 * 蹄铁槽（getShoeStack()）非空时渲染蹄铁纹理。不渲染鞍具。
 * 泛型从 EntityTusklin 换成 TusklinServant / ModelTusklinServant。
 */
@OnlyIn(Dist.CLIENT)
public class LayerTusklinServantGear extends RenderLayer<TusklinServant, ModelTusklinServant> {
   private static final ResourceLocation TEXTURE_SHOES = new ResourceLocation("alexsmobs:textures/entity/tusklin_hooves.png");

   public LayerTusklinServantGear(RenderTusklinServant render) {
      super(render);
   }

   @Override
   public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, TusklinServant entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
      if (!entitylivingbaseIn.getShoeStack().isEmpty()) {
         VertexConsumer ivertexbuilder = ItemRenderer.getArmorFoilBuffer(bufferIn, RenderType.armorCutoutNoCull(TEXTURE_SHOES), false, entitylivingbaseIn.getShoeStack().hasFoil());
         this.getParentModel().renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, LivingEntityRenderer.getOverlayCoords(entitylivingbaseIn, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
      }

   }
}
