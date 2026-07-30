package com.qiuyue.goetyominus.client.render;

import com.Polarice3.Goety.client.render.ModModelLayer;
import com.Polarice3.Goety.client.render.model.MinionModel;
import com.qiuyue.goetyominus.GoetyOminous;
import com.qiuyue.goetyominus.common.entities.hostile.Scorch;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ScorchRenderer extends MobRenderer<Scorch, MinionModel<Scorch>> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/cultist/scorch.png");
    private static final ResourceLocation CHARGING_TEXTURE =
            new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/cultist/scorch_charging.png");

    public ScorchRenderer(EntityRendererProvider.Context context) {
        super(context, new MinionModel<>(context.bakeLayer(ModModelLayer.MINION)), 0.3F);
    }

    @Override
    public ResourceLocation getTextureLocation(Scorch entity) {
        return entity.isCharging() ? CHARGING_TEXTURE : TEXTURE;
    }

    @Override
    protected int getBlockLightLevel(Scorch entity, net.minecraft.core.BlockPos pos) {
        return 15;
    }
}
