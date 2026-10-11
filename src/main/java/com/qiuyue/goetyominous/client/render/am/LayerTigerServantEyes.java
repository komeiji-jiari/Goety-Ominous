package com.qiuyue.goetyominous.client.render.am;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.client.render.model.am.ModelTigerServant;
import com.qiuyue.goetyominous.common.entities.ally.am.TigerServant;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LightLayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LayerTigerServantEyes extends RenderLayer<TigerServant, ModelTigerServant> {

   private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/tiger/tiger_eyes.png");
   private static final ResourceLocation TEXTURE_WHITE = new ResourceLocation("alexsmobs:textures/entity/tiger/tiger_white_eyes.png");
   private static final ResourceLocation TEXTURE_ANGRY = new ResourceLocation("alexsmobs:textures/entity/tiger/tiger_angry_eyes.png");

   public LayerTigerServantEyes(RenderTigerServant render) {
      super(render);
   }

   @Override
   public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, TigerServant tiger, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
      if (!tiger.isInvisible()) {
         boolean angry = tiger.getTarget() != null && tiger.getTarget().isAlive();
         long roundedTime = tiger.level().getDayTime() % 24000L;
         boolean night = roundedTime >= 13000L && roundedTime <= 22000L;
         BlockPos pos = tiger.blockPosition();
         if (!tiger.level().getBlockState(pos).canOcclude()) {
            pos = pos.above();
         }
         int i = tiger.level().getBrightness(LightLayer.SKY, pos);
         int j = tiger.level().getBrightness(LightLayer.BLOCK, pos);
         int brightness = night ? j : Math.max(i, j);
         if (brightness < 7 || angry) {
            VertexConsumer ivertexbuilder = bufferIn.getBuffer(RenderType.eyes(angry ? TEXTURE_ANGRY : (tiger.isWhite() ? TEXTURE_WHITE : TEXTURE)));
            this.getParentModel().renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, LivingEntityRenderer.getOverlayCoords(tiger, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
         }
      }
   }
}
