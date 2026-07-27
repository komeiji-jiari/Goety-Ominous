package com.qiuyue.goetyominus.client.render.layer;

import com.qiuyue.goetyominus.GoetyOminous;
import net.minecraft.client.model.SpiderModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CrimsonSpiderEyesLayer<T extends Entity, M extends SpiderModel<T>> extends EyesLayer<T, M> {

    private static final RenderType SPIDER_EYES = RenderType.eyes(
            new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/cultist/crimson_spider_eyes.png"));

    public CrimsonSpiderEyesLayer(RenderLayerParent<T, M> renderer) {
        super(renderer);
    }

    @Override
    public RenderType renderType() {
        return SPIDER_EYES;
    }
}
