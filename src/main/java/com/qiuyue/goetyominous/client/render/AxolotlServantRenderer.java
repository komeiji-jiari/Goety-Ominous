package com.qiuyue.goetyominous.client.render;

import com.google.common.collect.Maps;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.AxolotlServantModel;
import com.qiuyue.goetyominous.common.entities.ally.mobs.AxolotlServant;
import net.minecraft.Util;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Locale;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class AxolotlServantRenderer extends MobRenderer<AxolotlServant, AxolotlServantModel<AxolotlServant>> {

    private static final Map<AxolotlServant.Variant, ResourceLocation> TEXTURE_BY_TYPE = Util.make(Maps.newHashMap(), (map) -> {
        for (AxolotlServant.Variant variant : AxolotlServant.Variant.values()) {
            map.put(variant, new ResourceLocation(String.format(Locale.ROOT,
                    "textures/entity/axolotl/axolotl_%s.png", variant.getName())));
        }
    });

    public AxolotlServantRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new AxolotlServantModel<>(pContext.bakeLayer(ModEntityLayers.AXOLOTL_SERVANT_LAYER)), 0.4F);
    }

    @Override
    public ResourceLocation getTextureLocation(AxolotlServant pEntity) {
        return TEXTURE_BY_TYPE.getOrDefault(pEntity.getVariant(),
                TEXTURE_BY_TYPE.get(AxolotlServant.Variant.LUCY));
    }
}
