package com.qiuyue.goetyominous.client.render.projectile.mm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.MutantShulkerServantBullet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import com.alexander.mutantmore.renderers.entities.ModeledNonLivingEntityRenderer;
import com.qiuyue.goetyominous.client.render.model.mm.MutantShulkerServantBulletModel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.phys.Vec3;
import javax.annotation.Nullable;

public class MutantShulkerServantBulletRenderer<T extends MutantShulkerServantBullet> extends ModeledNonLivingEntityRenderer<T, MutantShulkerServantBulletModel<T>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("mutantmore", "textures/entities/mutant_shulker_bullet.png");
    private static final float[] UNTINTED = {1.0F, 1.0F, 1.0F};
    private static final float[] TEXTURE_MEAN = {0.707F, 0.514F, 0.563F};

    public MutantShulkerServantBulletRenderer(EntityRendererProvider.Context context) {
        super(context, new MutantShulkerServantBulletModel<>(context.bakeLayer(MutantShulkerServantBulletModel.LAYER_LOCATION)), 0.0F);
        this.addLayer(new MutantShulkerServantBulletGlowLayer(this));
    }

    public static float[] tint(@Nullable DyeColor color) {
        if (color == null) {
            return UNTINTED;
        }
        float[] dye = color.getTextureDiffuseColors();
        return new float[]{dye[0] / TEXTURE_MEAN[0], dye[1] / TEXTURE_MEAN[1], dye[2] / TEXTURE_MEAN[2]};
    }

    @Override
    protected void renderModel(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T entity, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return;
        }
        boolean bodyVisible = this.isBodyVisible(entity);
        boolean translucent = !bodyVisible && !entity.isInvisibleTo(minecraft.player);
        RenderType type = this.getRenderType(entity, bodyVisible, translucent, minecraft.shouldEntityAppearGlowing(entity));
        if (type != null) {
            float[] tint = tint(entity.getColor());
            this.model.renderToBuffer(poseStack, buffer.getBuffer(type), packedLight, this.getOverlayCoords(entity, partialTick),
                    tint[0], tint[1], tint[2], translucent ? 0.15F : 1.0F);
        }
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
