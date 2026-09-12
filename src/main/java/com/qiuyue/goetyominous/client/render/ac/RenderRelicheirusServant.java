package com.qiuyue.goetyominous.client.render.ac;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.client.render.model.ac.ModelRelicheirusServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.RelicheirusServant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderRelicheirusServant extends MobRenderer<RelicheirusServant, ModelRelicheirusServant> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("alexscaves:textures/entity/relicheirus.png");
    private static final ResourceLocation TEXTURE_RETRO = new ResourceLocation("alexscaves:textures/entity/relicheirus_retro.png");
    private static final ResourceLocation TEXTURE_TECTONIC = new ResourceLocation("alexscaves:textures/entity/relicheirus_tectonic.png");

    public RenderRelicheirusServant(EntityRendererProvider.Context context) {
        super(context, new ModelRelicheirusServant(), 1.0F);
        this.addLayer(new RelicheirusServantHeldTrilocarisLayer(this));
    }

    @Override
    protected void scale(RelicheirusServant mob, PoseStack poseStack, float partialTicks) {
    }

    @Override
    public ResourceLocation getTextureLocation(RelicheirusServant entity) {
        return entity.getAltSkin() == 2 ? TEXTURE_TECTONIC : (entity.getAltSkin() == 1 ? TEXTURE_RETRO : TEXTURE);
    }
}
