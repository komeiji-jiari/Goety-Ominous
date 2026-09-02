package com.qiuyue.goetyominous.client.render.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.client.render.model.mm.MutantShulkerServantBulletModel;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.MutantShulkerServantBullet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class MutantShulkerServantBulletGlowLayer<T extends MutantShulkerServantBullet, M extends MutantShulkerServantBulletModel<T>> extends RenderLayer<T, M> {
    private static final RenderType TYPE = RenderType.entityTranslucentEmissive(new ResourceLocation("mutantmore", "textures/entities/mutant_shulker_bullet.png"));

    public MutantShulkerServantBulletGlowLayer(RenderLayerParent<T, M> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!entity.isInvisible()) {
            this.getParentModel().renderToBuffer(poseStack, buffer.getBuffer(TYPE), packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}
