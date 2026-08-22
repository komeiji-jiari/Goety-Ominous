package com.qiuyue.goetyominous.client.render.ac;

import com.qiuyue.goetyominous.client.render.model.ac.ModelTremorsaurusServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.TremorsaurusServant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderTremorsaurusServant extends MobRenderer<TremorsaurusServant, ModelTremorsaurusServant> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("alexscaves:textures/entity/tremorsaurus.png");

    public RenderTremorsaurusServant(EntityRendererProvider.Context context) {
        super(context, new ModelTremorsaurusServant(), 1.1F);
        this.addLayer(new TremorsaurusHeldMobLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(TremorsaurusServant entity) {
        return TEXTURE;
    }
}
