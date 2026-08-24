/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.client.render.CorruptedBeamRenderer
 *  com.Polarice3.Goety.common.entities.projectiles.CorruptedBeam
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.mojang.blaze3d.vertex.VertexFormat
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  com.mojang.math.Axis
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.RenderType$CompositeState
 *  net.minecraft.client.renderer.culling.Frustum
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Matrix4f
 */
package com.vivideru.masteryofmagic.client.renderer;

import com.Polarice3.Goety.client.render.CorruptedBeamRenderer;
import com.Polarice3.Goety.common.entities.projectiles.CorruptedBeam;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import com.vivideru.masteryofmagic.entity.PhilosopherBeamEntity;
import java.util.Optional;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public final class PhilosopherBeamRenderer
extends CorruptedBeamRenderer<PhilosopherBeamEntity> {
    private static final RenderType CORE_RENDER_TYPE = PhilosopherBeamRenderType.createCore();

    public PhilosopherBeamRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    public boolean shouldRender(PhilosopherBeamEntity entity, Frustum frustum, double cameraX, double cameraY, double cameraZ) {
        double z;
        double y;
        double x = entity.m_20185_() - cameraX;
        return x * x + (y = entity.m_20186_() - cameraY) * y + (z = entity.m_20189_() - cameraZ) * z <= 36864.0;
    }

    public void render(PhilosopherBeamEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        super.render((CorruptedBeam)entity, entityYaw, partialTick, poseStack, buffers, packedLight);
        double distance = entity.beamTraceDistance(64.0, 1.0f, false);
        float yaw = -Mth.m_14189_((float)partialTick, (float)entity.f_19859_, (float)entity.m_146908_());
        float pitch = Mth.m_14189_((float)partialTick, (float)entity.f_19860_, (float)entity.m_146909_());
        Vec3 offset = Vec3.f_82478_;
        Optional optionalOwner = entity.getOptionalOwner();
        if (optionalOwner.isPresent()) {
            LivingEntity owner = (LivingEntity)optionalOwner.get();
            yaw = -Mth.m_14189_((float)partialTick, (float)owner.f_19859_, (float)owner.m_146908_());
            pitch = Mth.m_14189_((float)partialTick, (float)owner.f_19860_, (float)owner.m_146909_());
            offset = new Vec3(Mth.m_14139_((double)partialTick, (double)owner.f_19854_, (double)owner.m_20185_()) - Mth.m_14139_((double)partialTick, (double)entity.f_19854_, (double)entity.m_20185_()), Mth.m_14139_((double)partialTick, (double)owner.f_19855_, (double)owner.m_20186_()) - Mth.m_14139_((double)partialTick, (double)entity.f_19855_, (double)entity.m_20186_()), Mth.m_14139_((double)partialTick, (double)owner.f_19856_, (double)owner.m_20189_()) - Mth.m_14139_((double)partialTick, (double)entity.f_19856_, (double)entity.m_20189_())).m_82549_(entity.getOffsetVector(owner, partialTick));
        }
        float pulse = 0.92f + 0.08f * Mth.m_14031_((float)(((float)entity.f_19797_ + partialTick) * 0.65f));
        poseStack.m_85836_();
        poseStack.m_85837_(offset.f_82479_, offset.f_82480_, offset.f_82481_);
        poseStack.m_252781_(Axis.f_252436_.m_252977_(yaw));
        poseStack.m_252781_(Axis.f_252529_.m_252977_(pitch));
        Matrix4f matrix = poseStack.m_85850_().m_252922_();
        VertexConsumer consumer = buffers.m_6299_(CORE_RENDER_TYPE);
        PhilosopherBeamRenderer.drawPrism(consumer, matrix, 0.34f * pulse, (float)distance, 255, 25, 255, 34);
        PhilosopherBeamRenderer.drawPrism(consumer, matrix, 0.19f * pulse, (float)distance, 255, 75, 255, 112);
        PhilosopherBeamRenderer.drawPrism(consumer, matrix, 0.075f * pulse, (float)distance, 255, 242, 255, 238);
        poseStack.m_85849_();
    }

    private static void drawPrism(VertexConsumer consumer, Matrix4f matrix, float radius, float length, int red, int green, int blue, int alpha) {
        float min = -radius;
        float max = radius;
        float centerOffset = -0.115f;
        float yMin = min + centerOffset;
        float yMax = max + centerOffset;
        PhilosopherBeamRenderer.quad(consumer, matrix, min, yMin, 0.0f, min, yMin, length, min, yMax, length, min, yMax, 0.0f, red, green, blue, alpha);
        PhilosopherBeamRenderer.quad(consumer, matrix, max, yMax, 0.0f, max, yMax, length, max, yMin, length, max, yMin, 0.0f, red, green, blue, alpha);
        PhilosopherBeamRenderer.quad(consumer, matrix, min, yMax, 0.0f, min, yMax, length, max, yMax, length, max, yMax, 0.0f, red, green, blue, alpha);
        PhilosopherBeamRenderer.quad(consumer, matrix, max, yMin, 0.0f, max, yMin, length, min, yMin, length, min, yMin, 0.0f, red, green, blue, alpha);
    }

    private static void quad(VertexConsumer consumer, Matrix4f matrix, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4, int red, int green, int blue, int alpha) {
        PhilosopherBeamRenderer.vertex(consumer, matrix, x1, y1, z1, red, green, blue, alpha);
        PhilosopherBeamRenderer.vertex(consumer, matrix, x2, y2, z2, red, green, blue, alpha);
        PhilosopherBeamRenderer.vertex(consumer, matrix, x3, y3, z3, red, green, blue, alpha);
        PhilosopherBeamRenderer.vertex(consumer, matrix, x4, y4, z4, red, green, blue, alpha);
    }

    private static void vertex(VertexConsumer consumer, Matrix4f matrix, float x, float y, float z, int red, int green, int blue, int alpha) {
        consumer.m_252986_(matrix, x, y, z).m_6122_(red, green, blue, alpha).m_5752_();
    }

    private static abstract class PhilosopherBeamRenderType
    extends RenderType {
        private PhilosopherBeamRenderType(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState) {
            super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
        }

        private static RenderType createCore() {
            return RenderType.m_173215_((String)"philosopher_beam_core", (VertexFormat)DefaultVertexFormat.f_85815_, (VertexFormat.Mode)VertexFormat.Mode.QUADS, (int)512, (boolean)false, (boolean)true, (RenderType.CompositeState)RenderType.CompositeState.m_110628_().m_173292_(f_173104_).m_110685_(f_110139_).m_110663_(f_110113_).m_110661_(f_110110_).m_110687_(f_110115_).m_110691_(false));
        }
    }
}

