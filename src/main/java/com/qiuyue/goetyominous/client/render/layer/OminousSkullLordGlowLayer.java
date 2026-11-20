package com.qiuyue.goetyominous.client.render.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.render.model.OminousSkullLordModel;
import com.qiuyue.goetyominous.common.entities.hostile.OminousSkullLord;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OminousSkullLordGlowLayer extends RenderLayer<OminousSkullLord, OminousSkullLordModel<OminousSkullLord>> {

    private static final ResourceLocation NORMAL = new ResourceLocation(GoetyOminous.MOD_ID,
            "textures/entity/skull_lord/ominous_skulllord_glow.png");
    private static final ResourceLocation CHARGING = new ResourceLocation(GoetyOminous.MOD_ID,
            "textures/entity/skull_lord/ominous_skull_lord_charging_glow.png");
    private static final ResourceLocation VULNERABLE = new ResourceLocation(GoetyOminous.MOD_ID,
            "textures/entity/skull_lord/ominous_skull_lord_vulnerable_glow.png");

    public OminousSkullLordGlowLayer(RenderLayerParent<OminousSkullLord, OminousSkullLordModel<OminousSkullLord>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight,
                       OminousSkullLord entity, float limbSwing, float limbSwingAmount,
                       float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity.isInvisible()) {
            return;
        }
        ResourceLocation texture;
        if (entity.isCharging()) {
            texture = CHARGING;
        } else if (entity.isInvulnerable()) {
            texture = NORMAL;
        } else {
            texture = VULNERABLE;
        }
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.eyes(texture));
        this.getParentModel().renderToBuffer(poseStack, consumer, 0xF000F0,
                OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }
}
