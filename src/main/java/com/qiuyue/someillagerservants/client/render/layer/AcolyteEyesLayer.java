package com.qiuyue.someillagerservants.client.render.layer;

import com.qiuyue.someillagerservants.SomeIllagerServants;
import com.qiuyue.someillagerservants.client.render.model.AcolyteModel;
import com.qiuyue.someillagerservants.common.entities.hostile.Acolyte;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AcolyteEyesLayer extends EyesLayer<Acolyte, AcolyteModel<Acolyte>> {
    private static final RenderType EYES = RenderType.eyes(new ResourceLocation(SomeIllagerServants.MOD_ID, "textures/entity/acolyte_glow.png"));

    public AcolyteEyesLayer(RenderLayerParent<Acolyte, AcolyteModel<Acolyte>> renderer) {
        super(renderer);
    }

    @Override
    public RenderType renderType() {
        return EYES;
    }
}
