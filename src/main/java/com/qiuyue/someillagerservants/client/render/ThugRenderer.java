package com.qiuyue.someillagerservants.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.someillagerservants.SomeIllagerServants;
import com.qiuyue.someillagerservants.client.init.ModEntityLayers;
import com.qiuyue.someillagerservants.client.render.model.ThugModel;
import com.qiuyue.someillagerservants.common.entities.hostile.cultists.Thug;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class ThugRenderer extends MobRenderer<Thug, ThugModel<Thug>> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(SomeIllagerServants.MOD_ID, "textures/entity/cultist/thug.png");
    private static final ResourceLocation TEXTURE_ANGRY =
            new ResourceLocation(SomeIllagerServants.MOD_ID, "textures/entity/cultist/thug_angry.png");

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
