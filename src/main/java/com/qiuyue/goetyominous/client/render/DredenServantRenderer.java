package com.qiuyue.goetyominous.client.render;

import com.qiuyue.goetyominous.client.render.model.DredenModel;
import com.qiuyue.goetyominous.common.entities.ally.neutral.AbstractDredenEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class DredenServantRenderer<T extends AbstractDredenEntity> extends MobRenderer<T, DredenModel<T>> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("goetyominous", "textures/entity/dreden.png");

    public DredenServantRenderer(EntityRendererProvider.Context context) {
        super(context, new DredenModel<>(context.bakeLayer(DredenModel.LAYER_LOCATION)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TEXTURE;
    }
}
