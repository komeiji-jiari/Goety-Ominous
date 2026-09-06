package com.qiuyue.goetyominous.client.render.projectile;

import com.qiuyue.goetyominous.client.render.model.mm.RodlingServantModel;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.MutantBlazeServantRodProjectile;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;

public class MutantBlazeServantRodProjectileGlowLayer<T extends MutantBlazeServantRodProjectile, M extends RodlingServantModel<T>>
        extends RenderLayer<T, M> {

    public MutantBlazeServantRodProjectileGlowLayer(RenderLayerParent<T, M> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T entity,
                       float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks,
                       float netHeadYaw, float headPitch) {
        if (!entity.isInvisible()) {
            this.getParentModel().renderToBuffer(poseStack,
                    buffer.getBuffer(RenderType.eyes(MutantBlazeServantRodProjectileRenderer.getTexture(entity))),
                    packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}
