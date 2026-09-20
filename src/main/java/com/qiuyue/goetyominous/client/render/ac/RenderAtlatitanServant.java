package com.qiuyue.goetyominous.client.render.ac;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.client.render.model.ac.ModelAtlatitanServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.AtlatitanServant;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.entity.PartEntity;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class RenderAtlatitanServant extends MobRenderer<AtlatitanServant, ModelAtlatitanServant> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("alexscaves:textures/entity/atlatitan.png");
    private static final ResourceLocation TEXTURE_RETRO = new ResourceLocation("alexscaves:textures/entity/atlatitan_retro.png");
    private static final ResourceLocation TEXTURE_TECTONIC = new ResourceLocation("alexscaves:textures/entity/atlatitan_tectonic.png");

    public RenderAtlatitanServant(EntityRendererProvider.Context context) {
        super(context, new ModelAtlatitanServant(), 4.0F);
        this.addLayer(new AtlatitanServantRiderLayer(this));
    }

    @Override
    protected void scale(AtlatitanServant mob, PoseStack poseStack, float partialTicks) {
    }

    @Nullable
    @Override
    protected RenderType getRenderType(AtlatitanServant mob, boolean normal, boolean translucent, boolean outline) {
        ResourceLocation resourceLocation = this.getTextureLocation(mob);
        if (translucent) {
            return RenderType.itemEntityTranslucentCull(resourceLocation);
        }
        if (normal) {
            return RenderType.entityCutoutNoCull(resourceLocation);
        }
        return outline ? RenderType.outline(resourceLocation) : null;
    }

    @Override
    public ResourceLocation getTextureLocation(AtlatitanServant entity) {
        return entity.getAltSkin() == 2 ? TEXTURE_TECTONIC : (entity.getAltSkin() == 1 ? TEXTURE_RETRO : TEXTURE);
    }

    @Override
    public boolean shouldRender(AtlatitanServant entity, Frustum camera, double x, double y, double z) {
        if (super.shouldRender(entity, camera, x, y, z)) {
            return true;
        }
        for (PartEntity<?> part : entity.getParts()) {
            if (camera.isVisible(part.getBoundingBoxForCulling())) {
                return true;
            }
        }
        return false;
    }
}
