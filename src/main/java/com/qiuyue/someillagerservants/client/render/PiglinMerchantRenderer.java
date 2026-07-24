package com.qiuyue.someillagerservants.client.render;

import com.qiuyue.someillagerservants.SomeIllagerServants;
import com.qiuyue.someillagerservants.client.render.model.PiglinMerchantModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PiglinMerchantRenderer extends LivingEntityRenderer<Mob, PiglinMerchantModel<Mob>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(SomeIllagerServants.MOD_ID,
            "textures/entity/piglin/piglin_merchant.png");

    public PiglinMerchantRenderer(EntityRendererProvider.Context context) {
        super(context, new PiglinMerchantModel<>(context.bakeLayer(PiglinMerchantModel.LAYER_LOCATION)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(Mob entity) {
        return TEXTURE;
    }
}
