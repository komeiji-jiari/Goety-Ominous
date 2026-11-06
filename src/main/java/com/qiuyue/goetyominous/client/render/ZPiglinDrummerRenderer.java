package com.qiuyue.goetyominous.client.render;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.render.model.PiglinDrummerModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ZPiglinDrummerRenderer extends LivingEntityRenderer<Mob, PiglinDrummerModel<Mob>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(GoetyOminous.MOD_ID,
            "textures/entity/piglin/zpiglin_drummer.png");

    public ZPiglinDrummerRenderer(EntityRendererProvider.Context context) {
        super(context, new PiglinDrummerModel<>(context.bakeLayer(PiglinDrummerModel.LAYER_LOCATION)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(Mob entity) {
        return TEXTURE;
    }

    @Override
    protected boolean shouldShowName(Mob entity) {
        return false;
    }
}
