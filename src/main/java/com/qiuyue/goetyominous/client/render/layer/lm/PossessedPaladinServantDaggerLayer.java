package com.qiuyue.goetyominous.client.render.layer.lm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.client.render.model.lm.PossessedPaladinServantModel;
import com.qiuyue.goetyominous.common.entities.ally.lm.PossessedPaladinServant;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PossessedPaladinServantDaggerLayer
        extends RenderLayer<PossessedPaladinServant, PossessedPaladinServantModel<PossessedPaladinServant>> {

    private static final ResourceLocation DAGGER = new ResourceLocation(
            "legendary_monsters",
            "textures/entity/posessed_paladin/layer/possessed_paladin_dagger_layer.png");
    private static final ResourceLocation DAGGER_RED = new ResourceLocation(
            "legendary_monsters",
            "textures/entity/posessed_paladin/layer/possessed_paladin_dagger_red_layer.png");

    public PossessedPaladinServantDaggerLayer(
            RenderLayerParent<PossessedPaladinServant, PossessedPaladinServantModel<PossessedPaladinServant>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                       PossessedPaladinServant entity, float limbSwing, float limbSwingAmount,
                       float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!entity.hasDagger()) {
            return;
        }

        VertexConsumer consumer = buffer.getBuffer(
                RenderType.entityTranslucentEmissive(entity.getPhase() >= 2 ? DAGGER_RED : DAGGER));
        this.getParentModel().renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY,
                1.0F, 1.0F, 1.0F, 1.0F);
    }
}
