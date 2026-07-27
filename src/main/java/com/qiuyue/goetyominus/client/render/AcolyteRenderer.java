package com.qiuyue.goetyominus.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominus.GoetyOminous;
import com.qiuyue.goetyominus.client.init.ModEntityLayers;
import com.qiuyue.goetyominus.client.render.layer.AcolyteEyesLayer;
import com.qiuyue.goetyominus.client.render.model.AcolyteModel;
import com.qiuyue.goetyominus.common.entities.hostile.cultists.Acolyte;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AcolyteRenderer extends MobRenderer<Acolyte, AcolyteModel<Acolyte>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/cultist/acolyte.png");

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