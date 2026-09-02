package com.qiuyue.goetyominous.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.mm.MutantShulkerServantModel;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.MutantShulkerServant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MutantShulkerServantRenderer extends MobRenderer<MutantShulkerServant, MutantShulkerServantModel<MutantShulkerServant>> {
    public MutantShulkerServantRenderer(EntityRendererProvider.Context context) {
        super(context, new MutantShulkerServantModel<>(context.bakeLayer(ModEntityLayers.MUTANT_SHULKER_SERVANT_LAYER)), 2.75F);
    }

    protected void scale(MutantShulkerServant p_115314_, PoseStack p_115315_, float p_115316_) {
        p_115315_.scale(1.25F, 1.25F, 1.25F);
        super.scale(p_115314_, p_115315_, p_115316_);
    }

    protected float getFlipDegrees(MutantShulkerServant p_115337_) {
        return 0.0F;
    }

    public ResourceLocation getTextureLocation(MutantShulkerServant entity) {
        DyeColor color = entity.getColor();
        return color == null
                ? new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/mutant_shulker_servant/mutant_shulker.png")
                : new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/mutant_shulker_servant/mutant_shulker_" + color.getName() + ".png");
    }
}
