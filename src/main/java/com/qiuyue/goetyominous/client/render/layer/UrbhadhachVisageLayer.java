package com.qiuyue.goetyominous.client.render.layer;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.render.model.UrbhadhachModel;
import com.qiuyue.goetyominous.common.entities.ally.neutral.AbstractUrbhadhach;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class UrbhadhachVisageLayer<T extends AbstractUrbhadhach> extends EyesLayer<T, UrbhadhachModel<T>> {
    private static final RenderType VISAGE = RenderType.eyes(
            new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/urbhadhach_overlay.png"));

    public UrbhadhachVisageLayer(RenderLayerParent<T, UrbhadhachModel<T>> renderer) {
        super(renderer);
    }

    @Override
    public RenderType renderType() {
        return VISAGE;
    }
}
