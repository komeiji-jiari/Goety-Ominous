package com.qiuyue.goetyominous.client.render.ac;

import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.utils.ModelUtil;
import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.ClientProxy;
import com.github.alexmodguy.alexscaves.client.render.ACRenderTypes;
import com.github.alexthe666.citadel.client.shader.PostEffectRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.common.events.XRayHandler;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.List;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class XRayRenderer {

    private static final ResourceLocation RAY_TEXTURE =
            new ResourceLocation("alexscaves", "textures/entity/raygun/raygun_ray.png");
    private static final ResourceLocation BLUE_RAY_TEXTURE =
            new ResourceLocation("alexscaves", "textures/entity/raygun/raygun_blue_ray.png");

    private static final float END_WIDTH = 1.3F;

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            return;
        }
        List<XRayHandler.ClientRay> rays = XRayHandler.clientRays();
        if (rays.isEmpty() || Minecraft.getInstance().level == null) {
            return;
        }
        Vec3 camera = event.getCamera().getPosition();
        float partialTick = event.getPartialTick();
        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        boolean glow = AlexsCaves.CLIENT_CONFIG.radiationGlowEffect.get();

        for (XRayHandler.ClientRay ray : rays) {
            if (!(Minecraft.getInstance().level.getEntity(ray.casterId()) instanceof LivingEntity caster)) {
                continue;
            }
            Vec3 from = muzzle(caster, partialTick, event.getCamera()).subtract(camera);
            Vec3 delta = ray.lerpTo(partialTick).subtract(camera).subtract(from);
            float length = (float) delta.length();
            if (length <= 0.05F) {
                continue;
            }
            float ageInTicks = (float) caster.tickCount + partialTick;

            poseStack.pushPose();
            poseStack.translate(from.x, from.y, from.z);
            orientAlongRay(poseStack, delta);
            renderRay(poseStack, buffer, length, ageInTicks, ray.gamma(), false);
            if (ray.gamma() && glow) {
                renderRay(poseStack, buffer, length, ageInTicks, ray.gamma(), true);
            }
            poseStack.popPose();
        }
        buffer.endBatch();
    }

    private static Vec3 muzzle(LivingEntity caster, float partialTick, Camera camera) {
        ItemStack staff = caster.getMainHandItem();
        float staffHeight = staff.getItem() instanceof IWand wand
                ? wand.getWandVisualHeight(caster.level(), caster, staff)
                : 0.8F;

        if (caster == Minecraft.getInstance().player
                && Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON) {
            int arm = caster.getMainArm() == HumanoidArm.RIGHT ? 1 : -1;
            double fovFactor = 960.0D / (double) Minecraft.getInstance().options.fov().get().intValue();
            Vec3 offset = camera.getNearPlane()
                    .getPointOnPlane((0.125F * staffHeight + 0.35F) * (float) arm,
                            1.5F * staffHeight - 0.45F)
                    .scale(fovFactor);
            return caster.getEyePosition(partialTick).add(offset);
        }

        if (caster instanceof Player player) {
            Optional<Vec3> hand = ModelUtil.getThirdPersonPlayerHandPosition(player,
                    Minecraft.getInstance().getEntityRenderDispatcher(),
                    Mth.lerp(partialTick, player.yBodyRotO, player.yBodyRot), partialTick,
                    player.getMainArm(), new Vec3(0.0D, 0.55D, (double) (-staffHeight)));
            if (hand.isPresent()) {
                return hand.get();
            }
        }
        return caster.getEyePosition(partialTick);
    }

    private static void orientAlongRay(PoseStack poseStack, Vec3 dir) {
        Vec3 unit = dir.normalize();
        float xRot = (float) Math.acos(unit.y);
        float yRot = (float) Math.atan2(unit.z, unit.x);
        poseStack.mulPose(Axis.YP.rotationDegrees(((float) Math.PI / 2F - yRot) * (180F / (float) Math.PI)));
        poseStack.mulPose(Axis.XP.rotationDegrees(xRot * (180F / (float) Math.PI)));
    }

    private static void renderRay(PoseStack poseStack, MultiBufferSource buffer, float length,
                                  float ageInTicks, boolean blue, boolean irradiated) {
        if (irradiated) {
            PostEffectRegistry.renderEffectForNextTick(ClientProxy.IRRADIATED_SHADER);
        }
        float v = -1.0F + (-1.0F * (ageInTicks * 0.25F % 1.0F));
        float v1 = length + v;

        poseStack.pushPose();
        Matrix4f pose = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();
        VertexConsumer consumer = buffer.getBuffer(ACRenderTypes.getRaygunRay(
                blue ? BLUE_RAY_TEXTURE : RAY_TEXTURE, irradiated));

        vertex(consumer, pose, normal, 0.0F, 0.0F, 0.0F, 0.5F, v);
        vertex(consumer, pose, normal, -END_WIDTH, length, 0.0F, 0.0F, v1);
        vertex(consumer, pose, normal, END_WIDTH, length, 0.0F, 1.0F, v1);
        vertex(consumer, pose, normal, 0.0F, 0.0F, 0.0F, 0.5F, v);
        vertex(consumer, pose, normal, 0.0F, length, END_WIDTH, 1.0F, v1);
        vertex(consumer, pose, normal, 0.0F, length, -END_WIDTH, 0.0F, v1);

        poseStack.popPose();
    }

    private static void vertex(VertexConsumer consumer, Matrix4f pose, Matrix3f normal,
                               float x, float y, float z, float u, float v) {
        consumer.vertex(pose, x, y, z).color(255, 255, 255, 255).uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240)
                .normal(normal, 0.0F, 1.0F, 0.0F).endVertex();
    }
}
