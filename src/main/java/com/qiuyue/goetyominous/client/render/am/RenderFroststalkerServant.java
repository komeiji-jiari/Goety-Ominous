package com.qiuyue.goetyominous.client.render.am;

import com.qiuyue.goetyominous.client.render.model.am.ModelFroststalkerServant;
import com.qiuyue.goetyominous.common.entities.ally.am.FroststalkerServant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderFroststalkerServant extends MobRenderer<FroststalkerServant, ModelFroststalkerServant> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("goetyominous:textures/entity/froststalker_servant.png");
    private static final ResourceLocation TEXTURE_NOSPIKES = new ResourceLocation("goetyominous:textures/entity/froststalker_servant_nospikes.png");

    public RenderFroststalkerServant(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelFroststalkerServant(), 0.4F);
    }

    @Override
    public ResourceLocation getTextureLocation(FroststalkerServant entity) {
        return entity.hasSpikes() ? TEXTURE : TEXTURE_NOSPIKES;
    }

    @Override
    protected boolean isShaking(FroststalkerServant entity) {
        return entity.isInWaterRainOrBubble() && !entity.hasSpikes();
    }
}
