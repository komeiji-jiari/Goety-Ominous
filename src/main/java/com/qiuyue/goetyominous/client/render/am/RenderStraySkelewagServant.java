package com.qiuyue.goetyominous.client.render.am;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.client.render.model.am.ModelSkelewagServant;
import com.qiuyue.goetyominous.common.entities.ally.am.StraySkelewagServant;
import net.minecraft.core.BlockPos;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderStraySkelewagServant extends MobRenderer<StraySkelewagServant, ModelSkelewagServant<StraySkelewagServant>> {
    private static final ResourceLocation TEXTURE_0 = new ResourceLocation("goetyominous:textures/entity/stray_skelewag_servant_0.png");
    private static final ResourceLocation TEXTURE_1 = new ResourceLocation("goetyominous:textures/entity/stray_skelewag_servant_1.png");

    public RenderStraySkelewagServant(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelSkelewagServant<>(), 0.5F);
    }

    protected void scale(StraySkelewagServant entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime) {
    }

    protected int getBlockLightLevel(StraySkelewagServant entityIn, BlockPos partialTicks) {
        return Math.max(2, super.getBlockLightLevel(entityIn, partialTicks));
    }

    public ResourceLocation getTextureLocation(StraySkelewagServant entity) {
        return entity.getVariant() == 1 ? TEXTURE_1 : TEXTURE_0;
    }
}
