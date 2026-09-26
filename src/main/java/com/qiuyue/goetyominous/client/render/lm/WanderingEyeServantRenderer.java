package com.qiuyue.goetyominous.client.render.lm;

import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.lm.WanderingEyeServantModel;
import com.qiuyue.goetyominous.common.entities.ally.lm.WanderingEyeServant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WanderingEyeServantRenderer extends MobRenderer<WanderingEyeServant, WanderingEyeServantModel<WanderingEyeServant>> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(
            "legendary_monsters", "textures/entity/wandering_eye.png");

    public WanderingEyeServantRenderer(EntityRendererProvider.Context context) {
        super(context, new WanderingEyeServantModel(
                context.bakeLayer(ModEntityLayers.WANDERING_EYE_SERVANT_LAYER)), 0.75F);
    }

    @Override
    public ResourceLocation getTextureLocation(WanderingEyeServant entity) {
        return TEXTURE;
    }
}
