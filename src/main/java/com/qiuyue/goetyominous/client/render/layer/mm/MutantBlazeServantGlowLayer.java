package com.qiuyue.goetyominous.client.render.layer.mm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.client.render.MutantBlazeServantRenderer;
import com.qiuyue.goetyominous.client.render.model.mm.MutantBlazeServantModel;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.MutantBlazeServant;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;

public class MutantBlazeServantGlowLayer<T extends MutantBlazeServant, M extends MutantBlazeServantModel<T>> extends RenderLayer<T, M> {
    private final MutantBlazeServantRenderer<T> blazeRenderer;

    public MutantBlazeServantGlowLayer(RenderLayerParent<T, M> pRenderer) {
        super(pRenderer);
        this.blazeRenderer = (MutantBlazeServantRenderer<T>) pRenderer;
    }

    public void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, T pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTick, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        if (!pLivingEntity.isInvisible()) {
            this.getParentModel().renderToBuffer(pPoseStack,
                    pBuffer.getBuffer(RenderType.entityTranslucentEmissive(this.blazeRenderer.getTextureLocation(pLivingEntity))),
                    pPackedLight, LivingEntityRenderer.getOverlayCoords(pLivingEntity, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}
