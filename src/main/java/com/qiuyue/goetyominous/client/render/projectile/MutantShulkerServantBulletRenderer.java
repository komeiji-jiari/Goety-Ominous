package com.qiuyue.goetyominous.client.render.projectile;

import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.MutantShulkerServantBullet;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import com.alexander.mutantmore.renderers.entities.ModeledNonLivingEntityRenderer;
import com.qiuyue.goetyominous.client.render.model.mm.MutantShulkerServantBulletModel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

public class MutantShulkerServantBulletRenderer<T extends MutantShulkerServantBullet> extends ModeledNonLivingEntityRenderer<T, MutantShulkerServantBulletModel<T>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("mutantmore", "textures/entities/mutant_shulker_bullet.png");

    public MutantShulkerServantBulletRenderer(EntityRendererProvider.Context context) {
        super(context, new MutantShulkerServantBulletModel<>(context.bakeLayer(MutantShulkerServantBulletModel.LAYER_LOCATION)), 0.0F);
        this.addLayer(new MutantShulkerServantBulletGlowLayer(this));
    }

    @Override
    public Vec3 getRenderOffset(T entity, float partialTicks) {
        RandomSource random = entity.level().random;
        if (entity.getRemainingHits() < 3) {
            return new Vec3(random.nextGaussian() * 0.02D, random.nextGaussian() * 0.02D, random.nextGaussian() * 0.02D);
        }
        if (entity.getRemainingHits() < 2) {
            return new Vec3(random.nextGaussian() * 0.04D, random.nextGaussian() * 0.04D, random.nextGaussian() * 0.04D);
        }
        return super.getRenderOffset(entity, partialTicks);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return TEXTURE;
    }
}
