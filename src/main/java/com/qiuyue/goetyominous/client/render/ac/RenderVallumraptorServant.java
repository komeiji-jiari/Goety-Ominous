package com.qiuyue.goetyominous.client.render.ac;

import com.qiuyue.goetyominous.client.render.model.ac.ModelVallumraptorServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.VallumraptorServant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderVallumraptorServant extends MobRenderer<VallumraptorServant, ModelVallumraptorServant> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("alexscaves:textures/entity/vallumraptor.png");
    private static final ResourceLocation TEXTURE_ELDER = new ResourceLocation("alexscaves:textures/entity/vallumraptor_elder.png");

    public RenderVallumraptorServant(EntityRendererProvider.Context context) {
        super(context, new ModelVallumraptorServant(), 0.4F);
    }

    @Override
    public ResourceLocation getTextureLocation(VallumraptorServant entity) {
        return entity.isElder() ? TEXTURE_ELDER : TEXTURE;
    }
}
