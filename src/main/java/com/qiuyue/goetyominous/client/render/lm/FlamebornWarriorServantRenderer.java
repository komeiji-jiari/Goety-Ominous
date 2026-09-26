package com.qiuyue.goetyominous.client.render.lm;

import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.lm.FlamebornWarriorServantModel;
import com.qiuyue.goetyominous.common.entities.ally.lm.FlamebornWarriorServant;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FlamebornWarriorServantRenderer extends MobRenderer<FlamebornWarriorServant, FlamebornWarriorServantModel<FlamebornWarriorServant>> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("legendary_monsters", "textures/entity/flameborn/flameborn_warrior/flameborn_warrior.png");
    private static final RenderType GLOW = RenderType.eyes(new ResourceLocation("legendary_monsters", "textures/entity/flameborn/flameborn_warrior/flameborn_warrior_glow.png"));

    public FlamebornWarriorServantRenderer(EntityRendererProvider.Context context) {
        super(context, new FlamebornWarriorServantModel<>(context.bakeLayer(ModEntityLayers.FLAMEBORN_WARRIOR_SERVANT_LAYER)), 0.5F);
        this.addLayer(new EyesLayer<>(this) {
            @Override
            public RenderType renderType() {
                return GLOW;
            }
        });
    }

    @Override
    public ResourceLocation getTextureLocation(FlamebornWarriorServant entity) {
        return TEXTURE;
    }
}
