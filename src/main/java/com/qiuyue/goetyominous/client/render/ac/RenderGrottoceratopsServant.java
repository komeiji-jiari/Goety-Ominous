package com.qiuyue.goetyominous.client.render.ac;

import com.qiuyue.goetyominous.client.render.model.ac.ModelGrottoceratopsServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.GrottoceratopsServant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderGrottoceratopsServant extends MobRenderer<GrottoceratopsServant, ModelGrottoceratopsServant> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("alexscaves:textures/entity/grottoceratops.png");
    private static final ResourceLocation TEXTURE_BABY = new ResourceLocation("alexscaves:textures/entity/grottoceratops_baby.png");
    private static final ResourceLocation TEXTURE_RETRO = new ResourceLocation("alexscaves:textures/entity/grottoceratops_retro.png");
    private static final ResourceLocation TEXTURE_RETRO_BABY = new ResourceLocation("alexscaves:textures/entity/grottoceratops_retro_baby.png");
    private static final ResourceLocation TEXTURE_TECTONIC = new ResourceLocation("alexscaves:textures/entity/grottoceratops_tectonic.png");
    private static final ResourceLocation TEXTURE_TECTONIC_BABY = new ResourceLocation("alexscaves:textures/entity/grottoceratops_tectonic_baby.png");

    public RenderGrottoceratopsServant(EntityRendererProvider.Context context) {
        super(context, new ModelGrottoceratopsServant(), 1.1F);
    }

    @Override
    public ResourceLocation getTextureLocation(GrottoceratopsServant entity) {
        if (entity.isBaby()) {
            if (entity.getAltSkin() == 1) {
                return TEXTURE_RETRO_BABY;
            } else if (entity.getAltSkin() >= 2) {
                return TEXTURE_TECTONIC_BABY;
            }
            return TEXTURE_BABY;
        }
        if (entity.getAltSkin() == 1) {
            return TEXTURE_RETRO;
        } else if (entity.getAltSkin() >= 2) {
            return TEXTURE_TECTONIC;
        }
        return TEXTURE;
    }
}
