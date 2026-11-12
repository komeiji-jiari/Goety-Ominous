package com.qiuyue.goetyominous.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.render.model.EliteFungusThrowerModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ZEliteFungusThrowerRenderer extends LivingEntityRenderer<Mob, EliteFungusThrowerModel<Mob>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(GoetyOminous.MOD_ID,
            "textures/entity/piglin/zelite_fungus_thrower.png");

    public ZEliteFungusThrowerRenderer(EntityRendererProvider.Context context) {
        super(context, new EliteFungusThrowerModel<>(context.bakeLayer(EliteFungusThrowerModel.LAYER_LOCATION)), 0.5F);
    }

    @Override
    protected void scale(Mob entity, PoseStack poseStack, float partialTicks) {
        poseStack.scale(1.25F, 1.25F, 1.25F);
        super.scale(entity, poseStack, partialTicks);
    }

    @Override
    public ResourceLocation getTextureLocation(Mob entity) {
        return TEXTURE;
    }
}
