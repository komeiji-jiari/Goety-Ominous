package com.qiuyue.goetyominous.client.render;

import com.Polarice3.Goety.client.render.visual.TrailRenderer;
import com.Polarice3.Goety.utils.TrailEffect;
import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.entities.projectile.ChasseurArrow;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

public class ChasseurArrowRenderer extends ArrowRenderer<ChasseurArrow> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(GoetyOminous.MOD_ID, "textures/entity/projectile/chasseur_arrow.png");
    private static final ResourceLocation TRAIL_TEXTURE =
            new ResourceLocation("goety", "textures/entity/projectiles/solid_trail.png");
    private final RandomSource random = RandomSource.create();

    public ChasseurArrowRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(ChasseurArrow entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        if (entity.hasTrail()) {
            double x = Mth.lerp(partialTicks, entity.xo, entity.getX());
            double y = Mth.lerp(partialTicks, entity.yo, entity.getY()) + entity.getBbHeight() / 2.0F;
            double z = Mth.lerp(partialTicks, entity.zo, entity.getZ());
            entity.trail.prepareRender(new Vec3(x, y, z), partialTicks);
            entity.trailOuter.prepareRender(new Vec3(x, y, z), partialTicks);
            poseStack.pushPose();
            poseStack.translate(-x, -y, -z);
            TrailRenderer.render(entity.trailOuter, buffer.getBuffer(RenderType.entityTranslucent(TRAIL_TEXTURE)),
                    poseStack, TrailEffect.TrailOffsetFunction.FACE_CAMERA, true,
                    0.55F, 0.02F, 0.02F, 0.35F, 15728880);
            float jitter = 0.04F;
            float r = 1.0F - this.random.nextFloat() * jitter;
            float g = 0.72F - this.random.nextFloat() * jitter;
            float b = 0.1F - this.random.nextFloat() * jitter;
            TrailRenderer.render(entity.trail, buffer.getBuffer(RenderType.entityCutoutNoCull(TRAIL_TEXTURE)),
                    poseStack, TrailEffect.TrailOffsetFunction.FACE_CAMERA, true,
                    r, g, b, 1.0F, 15728880);
            poseStack.popPose();
        }
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    protected int getBlockLightLevel(ChasseurArrow entity, BlockPos pos) {
        return 15;
    }

    @Override
    public ResourceLocation getTextureLocation(ChasseurArrow entity) {
        return TEXTURE;
    }
}
