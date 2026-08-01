package com.qiuyue.goetyominous.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.ThugModel;
import com.qiuyue.goetyominous.common.entities.hostile.cultists.Thug;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ThugRenderer extends MobRenderer<Thug, ThugModel<Thug>> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/cultist/thug.png");
    private static final ResourceLocation TEXTURE_ANGRY =
            new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/cultist/thug_angry.png");

    public ThugRenderer(EntityRendererProvider.Context context) {
        super(context, new ThugModel<>(context.bakeLayer(ModEntityLayers.THUG_LAYER)), 0.7F);
    }

    @Override
    protected void scale(Thug entity, PoseStack poseStack, float partialTickTime) {
        poseStack.scale(1.15F, 1.15F, 1.15F);
    }

    @Override
    public ResourceLocation getTextureLocation(Thug entity) {
        return entity.isRaging() ? TEXTURE_ANGRY : TEXTURE;
    }
}
