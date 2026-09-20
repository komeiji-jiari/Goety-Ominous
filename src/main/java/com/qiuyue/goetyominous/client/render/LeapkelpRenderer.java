package com.qiuyue.goetyominous.client.render;

import com.Polarice3.Goety.client.render.ModModelLayer;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.render.model.LeapkelpModel;
import com.qiuyue.goetyominous.common.entities.ally.mobs.Leapkelp;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;

public class LeapkelpRenderer extends MobRenderer<Leapkelp, LeapkelpModel<Leapkelp>> {
    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(
            GoetyOminous.MOD_ID, "textures/entity/leapkelp/leapkelp.png");

    public LeapkelpRenderer(EntityRendererProvider.Context context) {
        super(context, new LeapkelpModel<>(context.bakeLayer(ModModelLayer.LEAPLEAF)), 1.0F);
        this.addLayer(new GlowLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(Leapkelp entity) {
        return TEXTURE_LOCATION;
    }

    public static class GlowLayer extends EyesLayer<Leapkelp, LeapkelpModel<Leapkelp>> {
        private static final RenderType RENDER_TYPE = RenderType.eyes(new ResourceLocation(
                GoetyOminous.MOD_ID, "textures/entity/leapkelp/leapkelp_glow.png"));

        public GlowLayer(RenderLayerParent<Leapkelp, LeapkelpModel<Leapkelp>> parent) {
            super(parent);
        }

        @Override
        public RenderType renderType() {
            return RENDER_TYPE;
        }
    }
}
