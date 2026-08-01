package com.qiuyue.goetyominous.client.render.sar;

import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.sar.TricksterServantModel;
import com.qiuyue.goetyominous.common.entities.ally.sar.TricksterServant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.resources.ResourceLocation;

public class TricksterServantRenderer extends MobRenderer<TricksterServant, TricksterServantModel> {
    private static final ResourceLocation NORMAL = new ResourceLocation("savage_and_ravage", "textures/entity/trickster/trickster.png");
    private static final ResourceLocation BASED = new ResourceLocation("savage_and_ravage", "textures/entity/trickster/trickster_based.png");

    public TricksterServantRenderer(EntityRendererProvider.Context context) {
        super(context, new TricksterServantModel(context.bakeLayer(ModEntityLayers.TRICKSTER_SERVANT_LAYER)), 0.5F);
        this.addLayer(new CustomHeadLayer<>(this, context.getModelSet(), context.getItemInHandRenderer()));
    }

    @Override
    public ResourceLocation getTextureLocation(TricksterServant entity) {
        return entity.getName().getString().equalsIgnoreCase("based") ? BASED : NORMAL;
    }
}
