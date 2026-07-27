package com.qiuyue.goetyominus.client.render.layer;

import com.qiuyue.goetyominus.client.render.model.FakeMagispellerModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FakeMagispellerLayer<T extends LivingEntity> extends EyesLayer<T, FakeMagispellerModel<T>> {
    private static final RenderType LAYER = RenderType.eyes(new ResourceLocation("illageandspillage", "textures/entity/magispeller/magispeller_faker.png"));

    public FakeMagispellerLayer(RenderLayerParent<T, FakeMagispellerModel<T>> p_i226039_1_) {
        super(p_i226039_1_);
    }

    public RenderType renderType() {
        return LAYER;
    }
}
