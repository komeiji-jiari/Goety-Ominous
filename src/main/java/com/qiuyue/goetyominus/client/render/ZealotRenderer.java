package com.qiuyue.goetyominus.client.render;

import com.Polarice3.Goety.client.render.layer.HierarchicalArmorLayer;
import com.qiuyue.goetyominus.client.init.ModEntityLayers;
import com.qiuyue.goetyominus.client.render.model.ZealotModel;
import com.qiuyue.goetyominus.common.entities.hostile.cultists.Zealot;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ZealotRenderer extends MobRenderer<Zealot, ZealotModel<Zealot>> {

    public ZealotRenderer(EntityRendererProvider.Context context) {
        super(context, new ZealotModel<>(context.bakeLayer(ModEntityLayers.ZEALOT_LAYER)), 0.5F);
        this.addLayer(new HierarchicalArmorLayer<>(this, context));
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
    }

    @Override
    public ResourceLocation getTextureLocation(Zealot entity) {
        return entity.getResourceLocation();
    }
}