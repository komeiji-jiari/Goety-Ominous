package com.qiuyue.goetyominous.client.render.am;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.client.render.model.am.ModelEmuServant;
import com.qiuyue.goetyominous.common.entities.ally.am.EmuServant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderEmuServant extends MobRenderer<EmuServant, ModelEmuServant> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("alexsmobs:textures/entity/emu.png");
    private static final ResourceLocation TEXTURE_BABY = new ResourceLocation("alexsmobs:textures/entity/emu_baby.png");
    private static final ResourceLocation TEXTURE_BLONDE = new ResourceLocation("alexsmobs:textures/entity/emu_blonde.png");
    private static final ResourceLocation TEXTURE_BLONDE_BABY = new ResourceLocation("alexsmobs:textures/entity/emu_baby_blonde.png");
    private static final ResourceLocation TEXTURE_BLUE = new ResourceLocation("alexsmobs:textures/entity/emu_blue.png");

    public RenderEmuServant(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new ModelEmuServant(), 0.45F);
    }

    @Override
    protected void scale(EmuServant entity, PoseStack matrixStack, float partialTicks) {
        matrixStack.scale(0.85F, 0.85F, 0.85F);
    }

    @Override
    public ResourceLocation getTextureLocation(EmuServant entity) {
        if (entity.getVariant() == 2) {
            return entity.isBaby() ? TEXTURE_BLONDE_BABY : TEXTURE_BLONDE;
        }
        if (entity.getVariant() == 1 && !entity.isBaby()) {
            return TEXTURE_BLUE;
        }
        return entity.isBaby() ? TEXTURE_BABY : TEXTURE;
    }
}
