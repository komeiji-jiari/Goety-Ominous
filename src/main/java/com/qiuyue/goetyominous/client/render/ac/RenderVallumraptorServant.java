package com.qiuyue.goetyominous.client.render.ac;

import com.qiuyue.goetyominous.client.render.model.ac.ModelVallumraptorServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.VallumraptorServant;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderVallumraptorServant extends MobRenderer<VallumraptorServant, ModelVallumraptorServant> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("alexscaves:textures/entity/vallumraptor.png");
    private static final ResourceLocation TEXTURE_ELDER = new ResourceLocation("alexscaves:textures/entity/vallumraptor_elder.png");
    private static final ResourceLocation TEXTURE_RETRO = new ResourceLocation("alexscaves:textures/entity/vallumraptor_retro.png");
    private static final ResourceLocation TEXTURE_RETRO_ELDER = new ResourceLocation("alexscaves:textures/entity/vallumraptor_retro_elder.png");
    private static final ResourceLocation TEXTURE_TECTONIC = new ResourceLocation("alexscaves:textures/entity/vallumraptor_tectonic.png");
    private static final ResourceLocation TEXTURE_TECTONIC_ELDER = new ResourceLocation("alexscaves:textures/entity/vallumraptor_tectonic_elder.png");

    public RenderVallumraptorServant(EntityRendererProvider.Context context) {
        super(context, new ModelVallumraptorServant(), 0.4F);
    }

    @Override
    protected void scale(VallumraptorServant entity, PoseStack poseStack, float partialTick) {
        if (entity.isElder()) {
            poseStack.scale(1.1F, 1.1F, 1.1F);
        }
        float f = 1.0F - 0.9F * entity.getHideProgress(partialTick);
        this.model.setAlpha(f);
    }

    @Override
    protected RenderType getRenderType(VallumraptorServant entity, boolean bodyVisible, boolean translucent, boolean glint) {
        if (entity.getHideProgress(1.0F) > 0.0F) {
            return RenderType.entityTranslucent(this.getTextureLocation(entity));
        }
        return super.getRenderType(entity, bodyVisible, translucent, glint);
    }

    @Override
    public ResourceLocation getTextureLocation(VallumraptorServant entity) {
        if (entity.isElder()) {
            if (entity.getAltSkin() == 1) {
                return TEXTURE_RETRO_ELDER;
            } else if (entity.getAltSkin() >= 2) {
                return TEXTURE_TECTONIC_ELDER;
            }
            return TEXTURE_ELDER;
        }
        if (entity.getAltSkin() == 1) {
            return TEXTURE_RETRO;
        } else if (entity.getAltSkin() >= 2) {
            return TEXTURE_TECTONIC;
        }
        return TEXTURE;
    }
}
