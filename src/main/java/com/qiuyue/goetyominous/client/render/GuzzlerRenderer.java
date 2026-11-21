package com.qiuyue.goetyominous.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.render.model.GuzzlerModel;
import com.qiuyue.goetyominous.common.entities.ally.mobs.Guzzler;
import com.qiuyue.goetyominous.common.entities.ally.mobs.ZGuzzler;
import com.qiuyue.goetyominous.common.entities.hostile.GuzzlerBoss;
import com.qiuyue.goetyominous.common.entities.hostile.ZGuzzlerBoss;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuzzlerRenderer<T extends Mob> extends MobRenderer<T, GuzzlerModel<T>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(GoetyOminous.MOD_ID,
            "textures/entity/piglin/guzzler.png");
    private static final ResourceLocation HOSTILE_TEXTURE = new ResourceLocation(GoetyOminous.MOD_ID,
            "textures/entity/piglin/hostile_guzzler.png");
    private static final ResourceLocation ZOMBIE_TEXTURE = new ResourceLocation(GoetyOminous.MOD_ID,
            "textures/entity/piglin/zguzzler.png");
    private static final ResourceLocation HOSTILE_ZOMBIE_TEXTURE = new ResourceLocation(GoetyOminous.MOD_ID,
            "textures/entity/piglin/hostile_zguzzler.png");
    private static final ResourceLocation LIANGZI_TEXTURE = new ResourceLocation(GoetyOminous.MOD_ID,
            "textures/entity/piglin/liangzi.png");
    private static final ResourceLocation ZOMBIE_LIANGZI_TEXTURE = new ResourceLocation(GoetyOminous.MOD_ID,
            "textures/entity/piglin/zombie_liangzi.png");

    public GuzzlerRenderer(EntityRendererProvider.Context context) {
        super(context, new GuzzlerModel<>(context.bakeLayer(GuzzlerModel.LAYER_LOCATION)), 1.2F);
    }

    @Override
    protected void scale(T entity, PoseStack poseStack, float partialTicks) {
        poseStack.scale(1.5F, 1.5F, 1.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        if (entity instanceof ZGuzzlerBoss) {
            return HOSTILE_ZOMBIE_TEXTURE;
        }
        if (entity instanceof ZGuzzler) {
            return this.isLiangzi(entity) ? ZOMBIE_LIANGZI_TEXTURE : ZOMBIE_TEXTURE;
        }
        if (entity instanceof GuzzlerBoss) {
            return HOSTILE_TEXTURE;
        }
        return this.isLiangzi(entity) ? LIANGZI_TEXTURE : TEXTURE;
    }

    private boolean isLiangzi(T entity) {
        return entity.hasCustomName() && "liangzi".equalsIgnoreCase(entity.getName().getString());
    }

    @Override
    protected float getFlipDegrees(T entity) {
        return 0.0F;
    }

    @Override
    protected boolean isShaking(T entity) {
        return super.isShaking(entity)
                || entity instanceof Guzzler guzzler && guzzler.isConverting()
                || entity instanceof GuzzlerBoss boss && boss.isConverting();
    }

    @Override
    public void render(T entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        int hurtTime = entity.hurtTime;
        int deathTime = entity.deathTime;
        if (entity.isDeadOrDying()) {
            entity.hurtTime = 0;
            entity.deathTime = 0;
        }
        try {
            super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        } finally {
            entity.hurtTime = hurtTime;
            entity.deathTime = deathTime;
        }
    }
}
