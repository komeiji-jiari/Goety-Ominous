package com.qiuyue.goetyominous.client.render.layer;

import com.Polarice3.Goety.client.render.model.PlayerZombieModel;
import com.qiuyue.goetyominous.common.entities.ally.mobs.MiredServant;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;

public class MiredEyesLayer<T extends MiredServant> extends EyesLayer<T, PlayerZombieModel<T>> {

    private static final RenderType EYES = RenderType.entityTranslucentEmissive(
            new ResourceLocation("goetyominous", "textures/entity/mired_eye.png"));

    public MiredEyesLayer(RenderLayerParent<T, PlayerZombieModel<T>> renderer) {
        super(renderer);
    }

    @Override
    public RenderType renderType() {
        return EYES;
    }
}
