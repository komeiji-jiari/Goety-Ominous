package com.qiuyue.goetyominous.client.render.mm;

import com.alexander.mutantmore.entities.MutantBlaze;
import com.alexander.mutantmore.models.entities.MutantBlazeModel;
import com.alexander.mutantmore.renderers.entities.MutantBlazeRenderer;
import com.alexander.mutantmore.renderers.layers.MutantBlazeGlowLayer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.client.render.model.mm.FixedMutantBlazeModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FixedMutantBlazeRenderer extends MobRenderer<MutantBlaze, FixedMutantBlazeModel> {
    public FixedMutantBlazeRenderer(EntityRendererProvider.Context context) {
        super(context, new FixedMutantBlazeModel(context.bakeLayer(MutantBlazeModel.LAYER_LOCATION)), 1.25F);
        this.addLayer(new MutantBlazeGlowLayer<>(this));
        this.shadowRadius = 0.5F;
    }

    protected void scale(MutantBlaze entity, PoseStack poseStack, float partialTicks) {
        poseStack.scale(1.5F, 1.5F, 1.5F);
        super.scale(entity, poseStack, partialTicks);
    }

    protected int getBlockLightLevel(MutantBlaze entity, BlockPos pos) {
        return entity.stunnedTicks >= entity.stunnedAnimationActionPoint + 30 ? 1 : 15;
    }

    protected float getFlipDegrees(MutantBlaze entity) {
        return 0.0F;
    }

    public ResourceLocation getTextureLocation(MutantBlaze entity) {
        return MutantBlazeRenderer.getTexture();
    }
}
