package com.qiuyue.goetyominous.client.render.lm;

import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.lm.ChoruslingServantModel;
import com.qiuyue.goetyominous.common.entities.ally.lm.ChoruslingServant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChoruslingServantRenderer extends MobRenderer<ChoruslingServant, ChoruslingServantModel> {

    private static final ResourceLocation CHORUSLING_SERVANT_TEXTURE =
            new ResourceLocation("legendary_monsters", "textures/entity/chorusling.png");

    public ChoruslingServantRenderer(EntityRendererProvider.Context context) {
        super(context, new ChoruslingServantModel(context.bakeLayer(ModEntityLayers.CHORUSLING_SERVANT_LAYER)), 1.0F);
    }

    @Override
    public ResourceLocation getTextureLocation(ChoruslingServant entity) {
        return CHORUSLING_SERVANT_TEXTURE;
    }
}
