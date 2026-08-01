package com.qiuyue.goetyominous.client.render.layer;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.render.model.DiscipleServantModel;
import com.qiuyue.goetyominous.common.entities.ally.mobs.DiscipleServant;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DiscipleServantEyesLayer extends EyesLayer<DiscipleServant, DiscipleServantModel<DiscipleServant>> {
    private static final RenderType EYES = RenderType.eyes(new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/cultist/disciple_glow.png"));

    public DiscipleServantEyesLayer(RenderLayerParent<DiscipleServant, DiscipleServantModel<DiscipleServant>> renderer) {
        super(renderer);
    }

    @Override
    public RenderType renderType() {
        return EYES;
    }
}
