package com.qiuyue.goetyominous.client.render.lm;

import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.lm.FlamebornGuardServantModel;
import com.qiuyue.goetyominous.common.entities.ally.lm.FlamebornGuardServant;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FlamebornGuardServantRenderer extends MobRenderer<FlamebornGuardServant, FlamebornGuardServantModel<FlamebornGuardServant>> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("legendary_monsters", "textures/entity/flameborn/flameborn_guard/flameborn_guard.png");
    private static final RenderType GLOW = RenderType.eyes(new ResourceLocation("legendary_monsters", "textures/entity/flameborn/flameborn_guard/flameborn_guard_glow.png"));

    public FlamebornGuardServantRenderer(EntityRendererProvider.Context context) {
        super(context, new FlamebornGuardServantModel<>(context.bakeLayer(ModEntityLayers.FLAMEBORN_GUARD_SERVANT_LAYER)), 0.5F);
        this.addLayer(new EyesLayer<>(this) {
            @Override
            public RenderType renderType() {
                return GLOW;
            }
        });
    }

    @Override
    public ResourceLocation getTextureLocation(FlamebornGuardServant entity) {
        return TEXTURE;
    }
}
