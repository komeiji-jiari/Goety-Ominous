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

    private static final ResourceLocation TEXTURE = new ResourceLocation("goetyominous:textures/entity/grottoceratops_servant.png");

    public RenderGrottoceratopsServant(EntityRendererProvider.Context context) {
        super(context, new ModelGrottoceratopsServant(), 1.1F);
    }

    @Override
    public ResourceLocation getTextureLocation(GrottoceratopsServant entity) {
        return TEXTURE;
    }
}
