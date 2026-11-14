package com.qiuyue.goetyominous.client.render.mm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.init.ModEntityLayers;
import com.qiuyue.goetyominous.client.render.model.mm.MutantHoglinServantModel;
import com.qiuyue.goetyominous.common.entities.ally.mm.MutantHoglinServant;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HogChargeShellRenderer extends MobRenderer<MutantHoglinServant, MutantHoglinServantModel<MutantHoglinServant>> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/mutant_burning_hoglin_servant.png");

    public HogChargeShellRenderer(EntityRendererProvider.Context context) {
        super(context, new MutantHoglinServantModel<>(context.bakeLayer(ModEntityLayers.MUTANT_HOGLIN_SERVANT_LAYER)), 2.25F);
    }

    @Override
    protected void scale(MutantHoglinServant entity, PoseStack pose, float partialTick) {
        super.scale(entity, pose, partialTick);
        pose.scale(1.1F, 1.1F, 1.1F);
    }

    @Override
    protected float getFlipDegrees(MutantHoglinServant entity) {
        return 0.0F;
    }

    @Nullable
    @Override
    protected RenderType getRenderType(MutantHoglinServant entity, boolean bodyVisible, boolean translucent, boolean glowing) {
        ResourceLocation texture = this.getTextureLocation(entity);
        if (translucent) {
            return RenderType.itemEntityTranslucentCull(texture);
        }
        if (bodyVisible) {
            return RenderType.entityTranslucent(texture);
        }
        return glowing ? RenderType.outline(texture) : null;
    }

    @Override
    public ResourceLocation getTextureLocation(MutantHoglinServant entity) {
        return TEXTURE;
    }
}
