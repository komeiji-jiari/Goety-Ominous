package com.qiuyue.goetyominous.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.layer.DiscipleEyesLayer;
import com.qiuyue.goetyominous.client.render.model.DiscipleModel;
import com.qiuyue.goetyominous.common.entities.hostile.cultists.Disciple;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DiscipleRenderer extends MobRenderer<Disciple, DiscipleModel<Disciple>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/cultist/disciple.png");

    public DiscipleRenderer(EntityRendererProvider.Context context) {
        super(context, new DiscipleModel<>(context.bakeLayer(ModEntityLayers.DISCIPLE_LAYER)), 0.6F);
        this.addLayer(new DiscipleEyesLayer(this));
    }

    @Override
    protected void scale(Disciple disciple, PoseStack poseStack, float partialTick) {
        poseStack.scale(1.0F, 1.0F, 1.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(Disciple disciple) {
        return TEXTURE;
    }
}