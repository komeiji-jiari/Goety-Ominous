package com.qiuyue.goetyominous.client.render;

import com.Polarice3.Goety.client.render.layer.HierarchicalArmorLayer;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.RogueChasseurModel;
import com.qiuyue.goetyominous.common.entities.ally.illager.RogueChasseurServant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class RogueChasseurRenderer extends MobRenderer<RogueChasseurServant, RogueChasseurModel<RogueChasseurServant>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(GoetyOminous.MOD_ID,
            "textures/entity/illager/rogue_chasseur_servant.png");

    public RogueChasseurRenderer(EntityRendererProvider.Context context) {
        super(context, new RogueChasseurModel<>(context.bakeLayer(ModEntityLayers.ROGUE_CHASSEUR_LAYER)), 0.5F);
        this.addLayer(new HierarchicalArmorLayer<>(this, context));
    }

    @Override
    public ResourceLocation getTextureLocation(RogueChasseurServant entity) {
        return TEXTURE;
    }
}
