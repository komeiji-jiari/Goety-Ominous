package com.qiuyue.goetyominous.client.render.lm;

import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.lm.HoveringHurricaneServantModel;
import com.qiuyue.goetyominous.common.entities.ally.lm.HoveringHurricaneServant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HoveringHurricaneServantRenderer extends MobRenderer<HoveringHurricaneServant, HoveringHurricaneServantModel<HoveringHurricaneServant>> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(
            "legendary_monsters", "textures/entity/hovering_hurricane.png");

    public HoveringHurricaneServantRenderer(EntityRendererProvider.Context context) {
        super(context, new HoveringHurricaneServantModel(
                context.bakeLayer(ModEntityLayers.HOVERING_HURRICANE_SERVANT_LAYER)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(HoveringHurricaneServant entity) {
        return TEXTURE;
    }
}
