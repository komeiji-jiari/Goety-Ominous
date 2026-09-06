package com.qiuyue.goetyominous.client.render;

import com.alexander.mutantmore.config.mutant_blaze.MutantBlazeClientConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.client.render.layer.mm.MutantBlazeServantGlowLayer;
import com.qiuyue.goetyominous.client.render.model.mm.MutantBlazeServantModel;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.MutantBlazeServant;
import com.qiuyue.goetyominous.config.MobsConfig;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public class MutantBlazeServantRenderer<T extends MutantBlazeServant> extends MobRenderer<T, MutantBlazeServantModel<T>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("mutantmore", "textures/entities/mutant_blaze.png");
    private static final ResourceLocation ARMOURED = new ResourceLocation("goetyominous", "textures/entity/mutant_blaze_armoured.png");
    private static final ResourceLocation INFERNO = new ResourceLocation("goetyominous", "textures/entity/mutant_blaze_inferno.png");
    private static final ResourceLocation ENRAGED = new ResourceLocation("goetyominous", "textures/entity/mutant_blaze_inferno_enraged.png");

    public MutantBlazeServantRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new MutantBlazeServantModel(pContext.bakeLayer(MutantBlazeServantModel.LAYER_LOCATION)), 1.25F);
        this.addLayer(new MutantBlazeServantGlowLayer(this));
        this.shadowStrength = 0.5F;
    }

    protected void scale(T pEntity, PoseStack pPoseStack, float pPartialTicks) {
        pPoseStack.scale(1.5F, 1.5F, 1.5F);
        super.scale(pEntity, pPoseStack, pPartialTicks);
    }

    protected int getBlockLightLevel(T pEntity, BlockPos pPos) {
        return pEntity.stunnedTicks >= pEntity.stunnedAnimationActionPoint + 30 ? 1 : 15;
    }

    protected float getFlipDegrees(T pEntity) {
        return 0.0F;
    }

    public ResourceLocation getTextureLocation(T pEntity) {
        if (pEntity.hasUnholyBlood()) {
            if (MobsConfig.MBUnholyBloodLowHealthTexture.get() && pEntity.getHealth() <= pEntity.getMaxHealth() / 2.0F) {
                return ENRAGED;
            }
            return INFERNO;
        }
        return getTexture();
    }

    public static ResourceLocation getTexture() {
        return (Boolean)MutantBlazeClientConfig.armoured_texture.get() ? ARMOURED : TEXTURE;
    }
}
