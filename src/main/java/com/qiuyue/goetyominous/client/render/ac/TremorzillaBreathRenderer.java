package com.qiuyue.goetyominous.client.render.ac;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.ClientProxy;
import com.github.alexmodguy.alexscaves.client.model.TremorzillaBeamModel;
import com.github.alexmodguy.alexscaves.client.render.ACRenderTypes;
import com.github.alexthe666.citadel.client.shader.PostEffectRegistry;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.client.sound.TremorzillaBreathLoopSoundHandler;
import com.qiuyue.goetyominous.common.events.TremorzillaBreathHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class TremorzillaBreathRenderer {

    private static final ResourceLocation TEXTURE_BEAM_INNER = new ResourceLocation("alexscaves", "textures/entity/tremorzilla/tremorzilla_beam_inner.png");
    private static final ResourceLocation TEXTURE_BEAM_OUTER = new ResourceLocation("alexscaves", "textures/entity/tremorzilla/tremorzilla_beam_outer.png");
    private static final ResourceLocation[] TEXTURE_BEAM_END = new ResourceLocation[]{
            new ResourceLocation("alexscaves", "textures/entity/tremorzilla/tremorzilla_beam_end_0.png"),
            new ResourceLocation("alexscaves", "textures/entity/tremorzilla/tremorzilla_beam_end_1.png"),
            new ResourceLocation("alexscaves", "textures/entity/tremorzilla/tremorzilla_beam_end_2.png")};
    private static final TremorzillaBeamModel BEAM_END_MODEL = new TremorzillaBeamModel();

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || Minecraft.getInstance().isPaused()) {
            return;
        }
        List<TremorzillaBreathHandler.ClientBeam> beams = TremorzillaBreathHandler.clientBeams();
        for (TremorzillaBreathHandler.ClientBeam beam : beams) {
            TremorzillaBreathLoopSoundHandler.playFor(beam.casterId());
        }
        TremorzillaBreathLoopSoundHandler.retainOnly(beams);
    }

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            return;
        }
        List<TremorzillaBreathHandler.ClientBeam> beams = TremorzillaBreathHandler.clientBeams();
        if (beams.isEmpty() || Minecraft.getInstance().level == null) {
            return;
        }
        Vec3 camera = event.getCamera().getPosition();
        PoseStack poseStack = event.getPoseStack();
        float partialTick = event.getPartialTick();
        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        for (TremorzillaBreathHandler.ClientBeam beam : beams) {
            if (!(Minecraft.getInstance().level.getEntity(beam.casterId()) instanceof LivingEntity caster)) {
                continue;
            }
            renderBreathBeam(poseStack, buffer, camera, caster, beam, partialTick);
        }
        buffer.endBatch();
    }

    private static void renderBreathBeam(PoseStack poseStack, MultiBufferSource source, Vec3 camera,
                                         LivingEntity caster, TremorzillaBreathHandler.ClientBeam beam, float partialTick) {
        Vec3 from = TremorzillaBreathHandler.beamOrigin(caster, partialTick);
        Vec3 raw = beam.lerpTo(partialTick).subtract(from);
        float length = (float) raw.length();
        if (length <= 0.05F) {
            return;
        }
        Vec3 dir = raw.normalize();
        float xRot = (float) Math.acos(dir.y);
        float yRot = (float) Math.atan2(dir.z, dir.x);
        float width = beam.progress() * 1.5F;
        float ageInTicks = (float) caster.tickCount + partialTick;
        float shakeByX = Mth.sin(ageInTicks * 4.0F) * 0.075F;
        float shakeByY = Mth.sin(ageInTicks * 4.0F + 1.2F) * 0.075F;
        float shakeByZ = Mth.sin(ageInTicks * 4.0F + 2.4F) * 0.075F;

        poseStack.pushPose();
        poseStack.translate(from.x + (double) shakeByX - camera.x,
                from.y + (double) shakeByY - camera.y,
                from.z + (double) shakeByZ - camera.z);
        poseStack.mulPose(Axis.YP.rotationDegrees((1.5707964F - yRot) * 57.295776F));
        poseStack.mulPose(Axis.XP.rotationDegrees((-1.5707964F + xRot) * 57.295776F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(45.0F));

        boolean glow = AlexsCaves.CLIENT_CONFIG.radiationGlowEffect.get();
        renderBeam(poseStack, source, ageInTicks, width, length, true, false, glow);
        if (glow) {
            renderBeam(poseStack, source, ageInTicks, width, length, true, true, true);
        }
        renderBeam(poseStack, source, ageInTicks, width, length, false, false, glow);

        poseStack.popPose();
    }

    private static void renderBeam(PoseStack poseStack, MultiBufferSource source, float ageInTicks,
                                   float width, float length, boolean inner, boolean glowSecondPass, boolean glowEffect) {
        int segments = inner ? 4 : 8;
        ResourceLocation texture = inner ? TEXTURE_BEAM_INNER : TEXTURE_BEAM_OUTER;
        float speed = inner ? 0.5F : 1.0F;
        float startAlpha = 1.0F;
        float endAlpha = inner ? 1.0F : 0.0F;
        VertexConsumer consumer;
        if (inner) {
            if (glowEffect && glowSecondPass) {
                PostEffectRegistry.renderEffectForNextTick(ClientProxy.IRRADIATED_SHADER);
                consumer = source.getBuffer(ACRenderTypes.getTremorzillaBeam(texture, true));
                endAlpha = 0.5F;
            } else {
                consumer = source.getBuffer(ACRenderTypes.getTremorzillaBeam(texture, false));
            }
        } else {
            if (glowEffect) {
                PostEffectRegistry.renderEffectForNextTick(ClientProxy.IRRADIATED_SHADER);
                consumer = source.getBuffer(ACRenderTypes.getTremorzillaBeam(texture, true));
            } else {
                consumer = source.getBuffer(ACRenderTypes.getTremorzillaBeam(texture, false));
            }
            width += 0.25F;
        }

        poseStack.pushPose();

        float uvScroll = ageInTicks * -0.25F * speed;
        float v1 = uvScroll + length * (inner ? 0.5F : 0.15F);
        float x = -width;
        float y = 0.0F;
        float u = 0.0F;
        Matrix4f matrix4f = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();

        for (int i = 0; i <= segments; ++i) {
            float cx = Mth.cos((float) Math.PI + (float) i * ((float) Math.PI * 2) / (float) segments) * width;
            float cy = Mth.sin((float) Math.PI + (float) i * ((float) Math.PI * 2) / (float) segments) * width;
            float nextU = (float) i + 1.0F;
            consumer.vertex(matrix4f, x * 0.55F, y * 0.55F, 0.0F).color(1.0F, 1.0F, 1.0F, startAlpha).uv(u, uvScroll).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(normal, 0.0F, -1.0F, 0.0F).endVertex();
            consumer.vertex(matrix4f, x, y, length).color(1.0F, 1.0F, 1.0F, endAlpha).uv(u, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(normal, 0.0F, -1.0F, 0.0F).endVertex();
            consumer.vertex(matrix4f, cx, cy, length).color(1.0F, 1.0F, 1.0F, endAlpha).uv(nextU, v1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(normal, 0.0F, -1.0F, 0.0F).endVertex();
            consumer.vertex(matrix4f, cx * 0.55F, cy * 0.55F, 0.0F).color(1.0F, 1.0F, 1.0F, startAlpha).uv(nextU, uvScroll).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(240).normal(normal, 0.0F, -1.0F, 0.0F).endVertex();
            x = cx;
            y = cy;
            u = nextU;
        }

        if (inner) {
            ResourceLocation endTexture = TEXTURE_BEAM_END[((int) ageInTicks / 2) % TEXTURE_BEAM_END.length];
            VertexConsumer endConsumer;
            if (glowEffect && glowSecondPass) {
                PostEffectRegistry.renderEffectForNextTick(ClientProxy.IRRADIATED_SHADER);
                endConsumer = source.getBuffer(ACRenderTypes.getTremorzillaBeam(endTexture, true));
            } else {
                endConsumer = source.getBuffer(ACRenderTypes.getTremorzillaBeam(endTexture, false));
            }
            poseStack.pushPose();
            poseStack.translate(0.0F, 0.0F, length - 1.5F);
            poseStack.mulPose(Axis.ZP.rotationDegrees(45.0F));
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            poseStack.scale(width, width, width);
            BEAM_END_MODEL.resetToDefaultPose();
            BEAM_END_MODEL.renderToBuffer(poseStack, endConsumer, 240, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            poseStack.popPose();
        }

        poseStack.popPose();
    }
}
