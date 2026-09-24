package com.qiuyue.goetyominous.client.render.layer.of;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.client.render.model.of.TerrorServantModel;
import com.qiuyue.goetyominous.common.entities.ally.of.TerrorServant;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class TerrorServantGlowLayer extends RenderLayer<TerrorServant, TerrorServantModel> {
    private static final RenderType TERROR_GLOW = RenderType.eyes(
            new ResourceLocation("opposing_force", "textures/entity/terror/terror_glow.png"));
    private static final RenderType ANTEDILUVIAN_TERROR_GLOW = RenderType.eyes(
            new ResourceLocation("opposing_force", "textures/entity/terror/antediluvian_terror_glow.png"));

    public TerrorServantGlowLayer(RenderLayerParent<TerrorServant, TerrorServantModel> parentModel) {
        super(parentModel);
    }

    @Override
    public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource multiBufferSource, int packedLight,
                       @NotNull TerrorServant entity, float limbSwing, float limbSwingAmount, float partialTick,
                       float ageInTicks, float netHeadYaw, float headPitch) {
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(entity.isElite() ? ANTEDILUVIAN_TERROR_GLOW : TERROR_GLOW);
        this.getParentModel().renderToBuffer(poseStack, vertexConsumer, 0xF00000, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
