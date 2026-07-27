package com.qiuyue.goetyominus.client.render.layer;

import com.qiuyue.goetyominus.GoetyOminous;
import com.qiuyue.goetyominus.client.render.model.AcolyteServantModel;
import com.qiuyue.goetyominus.common.entities.ally.mobs.AcolyteServant;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AcolyteServantEyesLayer extends EyesLayer<AcolyteServant, AcolyteServantModel<AcolyteServant>> {
    private static final RenderType EYES = RenderType.eyes(new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/cultist/acolyte_glow.png"));

    public AcolyteServantEyesLayer(RenderLayerParent<AcolyteServant, AcolyteServantModel<AcolyteServant>> renderer) {
        super(renderer);
    }

    @Override
    public RenderType renderType() {
        return EYES;
    }
}
