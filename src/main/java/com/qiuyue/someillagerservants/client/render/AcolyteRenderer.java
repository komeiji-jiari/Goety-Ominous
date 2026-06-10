package com.qiuyue.someillagerservants.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.someillagerservants.SomeIllagerServants;
import com.qiuyue.someillagerservants.client.init.ModEntityLayers;
import com.qiuyue.someillagerservants.client.render.layer.AcolyteEyesLayer;
import com.qiuyue.someillagerservants.client.render.model.AcolyteModel;
import com.qiuyue.someillagerservants.common.entities.hostile.Acolyte;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class AcolyteRenderer extends MobRenderer<Acolyte, AcolyteModel<Acolyte>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(SomeIllagerServants.MOD_ID, "textures/entity/acolyte.png");

    public AcolyteRenderer(EntityRendererProvider.Context context) {
        super(context, new AcolyteModel<>(context.bakeLayer(ModEntityLayers.ACOLYTE_LAYER)), 0.6F);
        this.addLayer(new AcolyteEyesLayer(this));
    }

    @Override
    protected void scale(Acolyte acolyte, PoseStack poseStack, float partialTick) {
        poseStack.scale(1.0F, 1.0F, 1.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(Acolyte acolyte) {
        return TEXTURE;
    }
}