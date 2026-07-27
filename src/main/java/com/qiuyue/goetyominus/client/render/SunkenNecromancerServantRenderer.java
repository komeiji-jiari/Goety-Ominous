package com.qiuyue.goetyominus.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominus.GoetyOminous;
import com.qiuyue.goetyominus.client.init.ModEntityLayers;
import com.qiuyue.goetyominus.client.render.model.SunkenNecromancerModel;
import com.qiuyue.goetyominus.common.entities.ally.mobs.SunkenNecromancerServant;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SunkenNecromancerServantRenderer extends MobRenderer<SunkenNecromancerServant, SunkenNecromancerModel<SunkenNecromancerServant>> {
    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/sunken_necromancer/sunken_necromancer_servant.png");
    private static final ResourceLocation GLOW_TEXTURE = new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/sunken_necromancer/sunken_necromancer_glow.png");

    public SunkenNecromancerServantRenderer(EntityRendererProvider.Context context) {
        super(context, new SunkenNecromancerModel<>(context.bakeLayer(ModEntityLayers.SUNKEN_NECROMANCER_LAYER)), 0.5F);
        this.addLayer(new SunkenNecromancerServantEyesLayer(this, GLOW_TEXTURE));
    }

    @Override
    protected void scale(SunkenNecromancerServant entity, PoseStack poseStack, float partialTick) {
        float original = 1.25F;
        float necroLevel = (float) entity.getNecroLevel();
        float size = original + Math.max(necroLevel * 0.15F, 0);
        poseStack.scale(size, size, size);
    }

    @Override
    public ResourceLocation getTextureLocation(SunkenNecromancerServant entity) {
        return TEXTURE_LOCATION;
    }

    public static class SunkenNecromancerServantEyesLayer<T extends SunkenNecromancerServant, M extends SunkenNecromancerModel<T>> extends EyesLayer<T, M> {
        private final ResourceLocation textures;

        public SunkenNecromancerServantEyesLayer(RenderLayerParent<T, M> renderer, ResourceLocation textures) {
            super(renderer);
            this.textures = textures;
        }

        @Override
        public RenderType renderType() {
            return RenderType.eyes(textures);
        }
    }
}
