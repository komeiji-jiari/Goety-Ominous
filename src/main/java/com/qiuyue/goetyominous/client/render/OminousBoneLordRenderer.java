package com.qiuyue.goetyominous.client.render;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.OminousBoneLordModel;
import com.qiuyue.goetyominous.common.entities.hostile.OminousBoneLord;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OminousBoneLordRenderer extends MobRenderer<OminousBoneLord, OminousBoneLordModel<OminousBoneLord>> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(GoetyOminous.MOD_ID,
            "textures/entity/skull_lord/ominous_skull_lord.png");

    public OminousBoneLordRenderer(EntityRendererProvider.Context context) {
        super(context, new OminousBoneLordModel<>(context.bakeLayer(ModEntityLayers.OMINOUS_BONE_LORD_LAYER)), 0.5F);
        this.addLayer(new ItemInHandLayer<>(this, context.getItemInHandRenderer()));
    }

    @Override
    public ResourceLocation getTextureLocation(OminousBoneLord entity) {
        return TEXTURE;
    }
}
