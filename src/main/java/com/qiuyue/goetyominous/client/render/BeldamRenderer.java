package com.qiuyue.goetyominous.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.Polarice3.Goety.client.render.model.ModWitchModel;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.entities.hostile.cultists.Beldam;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BeldamRenderer extends MobRenderer<Beldam, ModWitchModel<Beldam>> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/cultist/beldam.png");

    public BeldamRenderer(EntityRendererProvider.Context context) {
        super(context, new ModWitchModel<>(context.bakeLayer(
                com.Polarice3.Goety.client.render.ModModelLayer.MOD_WITCH)), 0.5F);
        this.addLayer(new com.Polarice3.Goety.client.render.WitchServantRenderer.ModWitchItemLayer<>(this, context.getItemInHandRenderer()));
    }

    @Override
    public ResourceLocation getTextureLocation(Beldam entity) {
        return TEXTURE;
    }

    @Override
    public void render(Beldam entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        this.model.setHoldingItem(!entity.getMainHandItem().isEmpty());
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    protected void scale(Beldam entity, PoseStack poseStack, float partialTickTime) {
        poseStack.scale(0.9375F, 0.9375F, 0.9375F);
    }
}