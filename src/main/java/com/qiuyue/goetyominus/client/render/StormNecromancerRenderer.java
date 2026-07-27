package com.qiuyue.goetyominus.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominus.GoetyOminous;
import com.qiuyue.goetyominus.client.init.ModEntityLayers;
import com.qiuyue.goetyominus.client.render.model.StormNecromancerModel;
import com.qiuyue.goetyominus.common.entities.ally.neutral.AbstractStormNecromancer;
import com.qiuyue.goetyominus.common.entities.hostile.StormNecromancer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;

public class StormNecromancerRenderer extends MobRenderer<AbstractStormNecromancer, StormNecromancerModel<AbstractStormNecromancer>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/storm_necromancer/storm_necromancer.png");
    private static final ResourceLocation TEXTURE_SERVANT = new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/storm_necromancer/storm_necromancer_servant.png");
    private static final ResourceLocation GLOW = new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/storm_necromancer/storm_necromancer_glow.png");

    public StormNecromancerRenderer(EntityRendererProvider.Context context) {
        super(context, new StormNecromancerModel<>(context.bakeLayer(ModEntityLayers.STORM_NECROMANCER_LAYER)), 0.5F);
        this.addLayer(new NecromancerEyesLayer(this, GLOW));
    }

    @Override
    protected void scale(AbstractStormNecromancer entity, PoseStack poseStack, float partialTick) {
        float necroLevel = (float) entity.getNecroLevel();
        float size = 1.45F + Math.max(necroLevel * 0.15F, 0);
        poseStack.scale(size, size, size);
    }

    @Override
    public ResourceLocation getTextureLocation(AbstractStormNecromancer entity) {
        if (entity instanceof StormNecromancer storm) {
            return storm.getResourceLocation();
        }
        return TEXTURE_SERVANT;
    }

    private static class NecromancerEyesLayer<T extends AbstractStormNecromancer, M extends StormNecromancerModel<T>> extends EyesLayer<T, M> {
        private final ResourceLocation texture;

        public NecromancerEyesLayer(RenderLayerParent<T, M> renderer, ResourceLocation texture) {
            super(renderer);
            this.texture = texture;
        }

        @Override
        public RenderType renderType() {
            return RenderType.eyes(texture);
        }
    }
}
