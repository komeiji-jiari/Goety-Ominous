package com.qiuyue.goetyominous.client.render.lm;

import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.lm.AnnihilationPursuerServantModel;
import com.qiuyue.goetyominous.common.entities.ally.lm.AnnihilationPursuerServant;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AnnihilationPursuerServantRenderer extends MobRenderer<AnnihilationPursuerServant, AnnihilationPursuerServantModel<AnnihilationPursuerServant>> {

    private static final ResourceLocation AWAKE = new ResourceLocation("legendary_monsters", "textures/entity/flameborn/annihilation_pursuer/annihilation_pursuer.png");
    private static final RenderType GLOW = RenderType.eyes(new ResourceLocation("legendary_monsters", "textures/entity/flameborn/annihilation_pursuer/annihilation_pursuer_glow.png"));

    public AnnihilationPursuerServantRenderer(EntityRendererProvider.Context context) {
        super(context, new AnnihilationPursuerServantModel<>(context.bakeLayer(ModEntityLayers.ANNIHILATION_PURSUER_SERVANT_LAYER)), 1.5F);
        this.addLayer(new AnnihilationPursuerServantGrabLayer(this, context.getEntityRenderDispatcher()));
        this.addLayer(new EyesLayer<>(this) {
            @Override
            public RenderType renderType() {
                return GLOW;
            }
        });
    }

    @Override
    public ResourceLocation getTextureLocation(AnnihilationPursuerServant entity) {
        return AWAKE;
    }
}
