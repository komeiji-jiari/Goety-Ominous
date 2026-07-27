package com.qiuyue.goetyominus.client.render.layer.mm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominus.client.render.model.mm.MutantHoglinServantModel;
import com.qiuyue.goetyominus.common.entities.ally.mobs.mm.MutantHoglinServant;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MutantHoglinEnragedLayer<T extends MutantHoglinServant> extends RenderLayer<T, MutantHoglinServantModel<T>> {
    public MutantHoglinEnragedLayer(RenderLayerParent<T, MutantHoglinServantModel<T>> p_116964_) {
        super(p_116964_);
    }

    public void render(PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight, T pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        VertexConsumer vertexconsumer = pBuffer.getBuffer(RenderType.entityTranslucentEmissive(new ResourceLocation("mutantmore", "textures/entities/mutant_hoglin_enraged.png")));
        ((MutantHoglinServantModel)this.getParentModel()).renderToBuffer(pMatrixStack, vertexconsumer, pPackedLight, pLivingEntity.hurtTime > 0 ? 3 : OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, pLivingEntity.getEnragedAmount());
    }
}
