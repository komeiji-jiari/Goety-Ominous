package com.qiuyue.goetyominous.client.render.ac;

import com.qiuyue.goetyominous.client.render.model.ac.ModelCaniacServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.CaniacServant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderCaniacServant extends MobRenderer<CaniacServant, ModelCaniacServant> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("alexscaves:textures/entity/caniac.png");

    public RenderCaniacServant(EntityRendererProvider.Context context) {
        super(context, new ModelCaniacServant(), 0.65F);
    }

    @Override
    public ResourceLocation getTextureLocation(CaniacServant entity) {
        return TEXTURE;
    }
}
