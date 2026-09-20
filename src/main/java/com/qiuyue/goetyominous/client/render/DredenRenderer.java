package com.qiuyue.goetyominous.client.render;

import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.DredenModel;
import com.qiuyue.goetyominous.common.entities.ally.neutral.AbstractDredenEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class DredenRenderer<T extends AbstractDredenEntity> extends MobRenderer<T, DredenModel<T>> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("goetyominous", "textures/entity/dreden.png");

    public DredenRenderer(EntityRendererProvider.Context context) {
        super(context, new DredenModel<>(context.bakeLayer(ModEntityLayers.DREDEN_LAYER)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TEXTURE;
    }
}
