package com.qiuyue.someillagerservants.client.render.layer;

import com.qiuyue.someillagerservants.SomeIllagerServants;
import com.qiuyue.someillagerservants.client.render.model.UrbhadhachServantModel;
import com.qiuyue.someillagerservants.common.entities.ally.mobs.UrbhadhachServant;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class UrbhadhachServantVisageLayer extends EyesLayer<UrbhadhachServant, UrbhadhachServantModel<UrbhadhachServant>> {
    private static final RenderType VISAGE = RenderType.eyes(
            new ResourceLocation(SomeIllagerServants.MOD_ID, "textures/entity/urbhadhach_overlay.png"));

    public UrbhadhachServantVisageLayer(RenderLayerParent<UrbhadhachServant, UrbhadhachServantModel<UrbhadhachServant>> renderer) {
        super(renderer);
    }

    @Override
    public RenderType renderType() {
        return VISAGE;
    }
}
