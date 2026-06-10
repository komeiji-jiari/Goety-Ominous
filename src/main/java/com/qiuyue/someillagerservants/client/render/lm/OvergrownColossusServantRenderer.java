package com.qiuyue.someillagerservants.client.render.lm;

import com.qiuyue.someillagerservants.client.init.ModEntityLayers;
import com.qiuyue.someillagerservants.client.render.model.lm.OvergrownColossusServantModel;
import com.qiuyue.someillagerservants.common.entities.ally.lm.OvergrownColossusServant;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OvergrownColossusServantRenderer extends MobRenderer<OvergrownColossusServant, OvergrownColossusServantModel<OvergrownColossusServant>> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(
            "legendary_monsters", "textures/entity/overgrown_colosuss/overgrown_colossus.png");

    public OvergrownColossusServantRenderer(EntityRendererProvider.Context context) {
        super(context, new OvergrownColossusServantModel(
                context.bakeLayer(ModEntityLayers.OVERGROWN_COLOSSUS_SERVANT_LAYER)), 1.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(OvergrownColossusServant entity) {
        return TEXTURE;
    }
}
