package com.qiuyue.goetyominous.client.render.lm;

import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.layer.lm.FracturedApostleBodyLayer;
import com.qiuyue.goetyominous.client.render.layer.lm.FracturedApostleBookLayer;
import com.qiuyue.goetyominous.client.render.layer.lm.FracturedApostleKnifeLayer;
import com.qiuyue.goetyominous.client.render.model.lm.FracturedApostleServantModel;
import com.qiuyue.goetyominous.common.entities.ally.lm.FracturedApostleServant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FracturedApostleServantRenderer
        extends MobRenderer<FracturedApostleServant, FracturedApostleServantModel<FracturedApostleServant>> {

    private static final ResourceLocation HOOD = new ResourceLocation(
            "legendary_monsters", "textures/entity/fractured_apostle/fractured_apostle_hood.png");

    public FracturedApostleServantRenderer(EntityRendererProvider.Context context) {
        super(context, new FracturedApostleServantModel<>(
                context.bakeLayer(ModEntityLayers.FRACTURED_APOSTLE_SERVANT_LAYER)), 1.0F);
        this.addLayer(new FracturedApostleBodyLayer(this));
        this.addLayer(new FracturedApostleKnifeLayer(this));
        this.addLayer(new FracturedApostleBookLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(FracturedApostleServant entity) {
        return HOOD;
    }
}
