package com.qiuyue.goetyominous.client.render.lm;

import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.layer.lm.FracturedServantBodyLayer;
import com.qiuyue.goetyominous.client.render.model.lm.FracturedServantModel;
import com.qiuyue.goetyominous.common.entities.ally.lm.FracturedServant;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class FracturedServantRenderer
        extends MobRenderer<FracturedServant, FracturedServantModel<FracturedServant>> {

    private static final ResourceLocation BODY = new ResourceLocation(
            "legendary_monsters", "textures/entity/fractured/fractured.png");

    public FracturedServantRenderer(EntityRendererProvider.Context context) {
        super(context, new FracturedServantModel<>(
                context.bakeLayer(ModEntityLayers.FRACTURED_SERVANT_LAYER)), 0.6F);
        this.addLayer(new FracturedServantBodyLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(FracturedServant entity) {
        return BODY;
    }

    @Nullable
    @Override
    protected RenderType getRenderType(FracturedServant entity, boolean bodyVisible, boolean translucent, boolean glowing) {
        return null;
    }
}
