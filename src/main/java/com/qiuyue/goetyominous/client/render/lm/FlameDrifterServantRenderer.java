package com.qiuyue.goetyominous.client.render.lm;

import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.lm.FlameDrifterServantModel;
import com.qiuyue.goetyominous.common.entities.ally.lm.FlameDrifterServant;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FlameDrifterServantRenderer extends MobRenderer<FlameDrifterServant, FlameDrifterServantModel<FlameDrifterServant>> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("legendary_monsters", "textures/entity/flame_drifter/flame_drifter.png");
    private static final RenderType GLOW = RenderType.eyes(new ResourceLocation("legendary_monsters", "textures/entity/flame_drifter/flame_drifter_glow.png"));

    public FlameDrifterServantRenderer(EntityRendererProvider.Context context) {
        super(context, new FlameDrifterServantModel<>(context.bakeLayer(ModEntityLayers.FLAME_DRIFTER_SERVANT_LAYER)), 1.0F);
        this.addLayer(new EyesLayer<>(this) {
            @Override
            public RenderType renderType() {
                return GLOW;
            }
        });
    }

    @Override
    public ResourceLocation getTextureLocation(FlameDrifterServant entity) {
        return TEXTURE;
    }
}
