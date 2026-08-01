package com.qiuyue.goetyominous.client.render.layer;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.render.model.DiscipleModel;
import com.qiuyue.goetyominous.common.entities.hostile.cultists.Disciple;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DiscipleEyesLayer extends EyesLayer<Disciple, DiscipleModel<Disciple>> {
    private static final RenderType EYES = RenderType.eyes(new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/cultist/disciple_glow.png"));

    public DiscipleEyesLayer(RenderLayerParent<Disciple, DiscipleModel<Disciple>> renderer) {
        super(renderer);
    }

    @Override
    public RenderType renderType() {
        return EYES;
    }
}
