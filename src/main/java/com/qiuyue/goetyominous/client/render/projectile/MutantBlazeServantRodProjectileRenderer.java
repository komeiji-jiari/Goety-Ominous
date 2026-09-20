package com.qiuyue.goetyominous.client.render.projectile;

import com.alexander.mutantmore.renderers.entities.ModeledNonLivingEntityRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.client.render.model.mm.RodlingServantModel;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.MutantBlazeServantRodProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public class MutantBlazeServantRodProjectileRenderer<T extends MutantBlazeServantRodProjectile>
        extends ModeledNonLivingEntityRenderer<T, RodlingServantModel<T>> {
    private static final ResourceLocation TAME = new ResourceLocation("mutantmore", "textures/entities/rodling.png");
    private static final ResourceLocation HOSTILE = new ResourceLocation("mutantmore", "textures/entities/rodling_hostile.png");

    public MutantBlazeServantRodProjectileRenderer(EntityRendererProvider.Context context) {
        super(context, new RodlingServantModel<>(context.bakeLayer(RodlingServantModel.LAYER_LOCATION)), 0.0F);
        this.addLayer(new MutantBlazeServantRodProjectileGlowLayer(this));
    }

    @Override
    public void render(T entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        if (!entity.isRodling()) {
            poseStack.scale(1.5F, 1.5F, 1.5F);
        }
        poseStack.mulPose(Axis.YP.rotationDegrees(entity.getId() * 500));
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        poseStack.popPose();
    }

    @Override
    protected int getBlockLightLevel(T entity, BlockPos pos) {
        return 15;
    }

    public static <T extends MutantBlazeServantRodProjectile> ResourceLocation getTexture(T entity) {
        return entity.isCollectable() ? TAME : HOSTILE;
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return getTexture(entity);
    }
}
