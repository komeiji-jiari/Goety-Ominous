package com.qiuyue.goetyominous.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.PyrologerModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PyrologerRenderer<T extends Mob> extends MobRenderer<T, PyrologerModel<T>> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("goetyominous", "textures/entity/illager/pyrologer.png");

    public PyrologerRenderer(EntityRendererProvider.Context context) {
        super(context, new PyrologerModel<>(context.bakeLayer(ModEntityLayers.PYROLOGER_LAYER)), 0.5F);
    }

    @Override
    protected void scale(T entity, PoseStack poseStack, float partialTick) {
        poseStack.scale(0.9375F, 0.9375F, 0.9375F);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TEXTURE;
    }
}
