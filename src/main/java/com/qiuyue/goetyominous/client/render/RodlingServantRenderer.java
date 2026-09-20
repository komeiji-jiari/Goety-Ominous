package com.qiuyue.goetyominous.client.render;

import com.qiuyue.goetyominous.client.render.layer.mm.RodlingServantGlowLayer;
import com.qiuyue.goetyominous.client.render.model.mm.RodlingServantModel;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.RodlingServant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public class RodlingServantRenderer<T extends RodlingServant> extends MobRenderer<T, RodlingServantModel<T>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("mutantmore", "textures/entities/rodling.png");

    public RodlingServantRenderer(EntityRendererProvider.Context context) {
        super(context, new RodlingServantModel<>(context.bakeLayer(RodlingServantModel.LAYER_LOCATION)), 0.5F);
        this.addLayer(new RodlingServantGlowLayer(this));
    }

    @Override
    protected int getBlockLightLevel(T entity, BlockPos pos) {
        return 15;
    }

    @Override
    public boolean shouldShowName(T entity) {
        return entity.alwaysShowsName() || super.shouldShowName(entity);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TEXTURE;
    }
}
