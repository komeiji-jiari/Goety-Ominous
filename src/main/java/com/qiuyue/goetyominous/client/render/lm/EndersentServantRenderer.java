package com.qiuyue.goetyominous.client.render.lm;

import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.lm.EndersentServantModel;
import com.qiuyue.goetyominous.common.entities.ally.lm.EndersentServant;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EndersentServantRenderer extends MobRenderer<EndersentServant, EndersentServantModel> {

    private static final ResourceLocation ENDERSENT_SERVANT_TEXTURE =
            new ResourceLocation("legendary_monsters", "textures/entity/endersent6.png");
    private static final ResourceLocation GLOW =
            new ResourceLocation("legendary_monsters", "textures/entity/endersent/glow/endersent_glow.png");

    public EndersentServantRenderer(EntityRendererProvider.Context context) {
        super(context, new EndersentServantModel(context.bakeLayer(ModEntityLayers.ENDERSENT_SERVANT_LAYER)), 1.0F);
        this.addLayer(new EyesLayer<EndersentServant, EndersentServantModel>(this) {

            @Override
            public RenderType renderType() {
                return RenderType.eyes(GLOW);
            }
        });
    }

    @Override
    public ResourceLocation getTextureLocation(EndersentServant entity) {
        return ENDERSENT_SERVANT_TEXTURE;
    }
}
