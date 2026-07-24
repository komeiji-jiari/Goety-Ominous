package com.qiuyue.someillagerservants.client.render.layer;

import com.qiuyue.someillagerservants.SomeIllagerServants;
import com.qiuyue.someillagerservants.client.render.model.UrbhadhachModel;
import com.qiuyue.someillagerservants.common.entities.hostile.UrbhadhachEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class UrbhadhachVisageLayer extends EyesLayer<UrbhadhachEntity, UrbhadhachModel<UrbhadhachEntity>> {
    private static final RenderType VISAGE = RenderType.eyes(
            new ResourceLocation(SomeIllagerServants.MOD_ID, "textures/entity/urbhadhach_overlay.png"));

    public UrbhadhachVisageLayer(RenderLayerParent<UrbhadhachEntity, UrbhadhachModel<UrbhadhachEntity>> renderer) {
        super(renderer);
    }

    @Override
    public RenderType renderType() {
        return VISAGE;
    }
}
