package com.qiuyue.goetyominous.client.render;

import com.Polarice3.Goety.client.render.layer.HierarchicalArmorLayer;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.AgonyModel;
import com.qiuyue.goetyominous.common.entities.hostile.cultists.Agony;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AgonyRenderer extends MobRenderer<Agony, AgonyModel<Agony>> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/cultist/agony.png");

    public AgonyRenderer(EntityRendererProvider.Context context) {
        super(context, new AgonyModel<>(context.bakeLayer(ModEntityLayers.AGONY_LAYER)), 0.5F);
        this.addLayer(new HierarchicalArmorLayer<>(this, context));
        this.addLayer(new ItemInHandLayer<Agony, AgonyModel<Agony>>(this, context.getItemInHandRenderer()));
    }

    @Override
    public ResourceLocation getTextureLocation(Agony entity) {
        return TEXTURE;
    }
}