package com.qiuyue.goetyominous.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.UrbhadhachModel;
import com.qiuyue.goetyominous.client.render.layer.UrbhadhachVisageLayer;
import com.qiuyue.goetyominous.common.entities.hostile.UrbhadhachEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class UrbhadhachRenderer extends MobRenderer<UrbhadhachEntity, UrbhadhachModel<UrbhadhachEntity>> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/urbhadhach.png");

    public UrbhadhachRenderer(EntityRendererProvider.Context context) {
        super(context, new UrbhadhachModel<>(context.bakeLayer(ModEntityLayers.URBHADHACH_LAYER)), 0.5F);
        this.addLayer(new UrbhadhachVisageLayer(this));
    }

    @Override
    protected void scale(UrbhadhachEntity entity, PoseStack poseStack, float partialTickTime) {
        poseStack.scale(1.2F, 1.2F, 1.2F);
    }

    @Override
    public ResourceLocation getTextureLocation(UrbhadhachEntity entity) {
        return TEXTURE;
    }
}
