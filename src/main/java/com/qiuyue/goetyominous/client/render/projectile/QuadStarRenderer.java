package com.qiuyue.goetyominous.client.render.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.entities.projectile.QuadStarProjectile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class QuadStarRenderer extends EntityRenderer<QuadStarProjectile> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(GoetyOminous.MOD_ID,
            "textures/entity/projectile/quad_star.png");
    private static final float ROLL = 32.0F;
    private static final float EXTENT = 0.93F;

    private static final float LINE_START = 0.35F;
    private static final float LINE_END_PAD = 0.03F;
    private static final float LINE_CORE_HALF = 0.026F;
    private static final float LINE_GLOW_HALF = 0.078F;
    private static final float LINE_MIN_SCREEN = 0.0016F;
    private static final int LINE_SEGMENTS = 14;
    private static final float LINE_CORE_TAPER = 0.55F;
    private static final float LINE_GLOW_TAPER = 0.70F;
    private static final float LINE_CORE_ALPHA = 0.95F;
    private static final float LINE_GLOW_ALPHA = 0.32F;
    private static final float LINE_CORE_R = 0.44F;
    private static final float LINE_CORE_G = 0.92F;
    private static final float LINE_CORE_B = 1.00F;
    private static final float LINE_GLOW_R = 0.26F;
    private static final float LINE_GLOW_G = 0.72F;
    private static final float LINE_GLOW_B = 1.00F;

    private static final int LAYER_VEIL = 0;
    private static final int LAYER_BAND = 1;
    private static final int LAYER_EDGE = 2;
    private static final float TRAIL_START = 0.22F;
    private static final int TRAIL_U_SAMPLES = 8;
    private static final float TRAIL_MIN_SCREEN = 0.0012F;
    private static final float TRAIL_FLOW_RATE = 1.70F;
    private static final float TRAIL_FLOW_PHASE = 0.70F;
    private static final float[] TRAIL_LENGTH = {2.30F, 1.30F, 1.95F};
    private static final int[] TRAIL_SEGMENTS = {24, 20, 22};
    private static final float[] TRAIL_BOW_AMP = {0.0375F, 0.075F, 0.075F};
    private static final float[] TRAIL_BOW_POW = {1.30F, 1.30F, 1.30F};
    private static final float[] TRAIL_DRIFT_AMP = {0.0F, 0.0F, 0.052F};
    private static final float[] TRAIL_DRIFT_POW = {0.55F, 0.55F, 0.55F};
    private static final float[] TRAIL_DRIFT_MIN = {0.0F, 0.0F, 0.008F};
    private static final float[] TRAIL_WIDTH_A = {0.135F, 0.105F, 0.011F};
    private static final float[] TRAIL_WIDTH_B = {0.150F, 0.0F, 0.0F};
    private static final float[] TRAIL_WIDTH_POW = {0.60F, 0.55F, 1.00F};
    private static final float[] TRAIL_WIDTH_MIN = {0.018F, 0.015F, 0.005F};
    private static final float[] TRAIL_WIDTH_GROW = {0.30F, 0.0F, 0.0F};
    private static final float[] TRAIL_ALPHA = {0.26F, 0.55F, 1.00F};
    private static final float[] TRAIL_ALPHA_POW = {1.45F, 1.50F, 1.30F};
    private static final float[] TRAIL_FLOW = {0.20F, 0.0F, 0.0F};
    private static final float[] TRAIL_PROFILE_EDGE = {0.0F, 0.50F, 0.45F};
    private static final float[] TRAIL_PROFILE_POW = {2.20F, 1.0F, 1.0F};
    private static final float[] TRAIL_COLOR_POW = {1.10F, 0.0F, 0.0F};
    private static final float[] TRAIL_HEAD_RGB = {
            0.74F, 0.97F, 1.00F,
            0.74F, 0.97F, 1.00F,
            0.88F, 1.00F, 1.00F};
    private static final float[] TRAIL_TAIL_RGB = {
            0.16F, 0.55F, 1.00F,
            0.74F, 0.97F, 1.00F,
            0.88F, 1.00F, 1.00F};

    private static final float CORNER_A = -EXTENT;
    private static final float CORNER_B = EXTENT;

    private static final float WING_MAIN = 1.0F;
    private static final float BRIGHT_MAIN = 1.0F;
    private static final float HALO_ON = 1.0F;
    private static final float SOLID = 1.0F;

    /**
     * 副面，照 komeji 的做法：常驻的第二个面，不是我这边的「正对弹道才淡入」折中。
     *
     * <p>主面的法线在 X 轴上，顺着弹道（±Z）看过去只有一条线，这个副面就是补那个角度。他的配色是
     * 尺寸 42%、亮度 62%、不画光晕 —— 副面只是别让星消失，不该抢主面的亮度，所以三个数原样照抄。
     */
    private static final float WING_CROSS = 0.42F;
    private static final float BRIGHT_CROSS = 0.62F;
    private static final float HALO_OFF = 0.0F;

    public QuadStarRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(QuadStarProjectile entity, float entityYaw, float partialTicks, PoseStack poseStack,
            MultiBufferSource buffer, int packedLight) {
        this.draw(entity, partialTicks, poseStack, buffer);
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private void draw(QuadStarProjectile entity, float partialTicks, PoseStack poseStack, MultiBufferSource buffer) {
        this.body(entity, partialTicks, poseStack, buffer);
        if (entity.getState() == QuadStarProjectile.CHARGE) {
            this.renderLine(entity, partialTicks, poseStack, buffer);
        } else if (entity.getState() == QuadStarProjectile.FLIGHT) {
            this.renderTrail(entity, partialTicks, poseStack, buffer);
        }
    }

    /**
     * 星体两个面：主面 + komeji 的副面。
     *
     * <p>🔴 <b>姿态用的是插值后的方向，不是最新那一份。</b>方向本身来自同步数据，两端同一份；但它是
     * 每 tick 才更新一次的离散量，追踪时一 tick 能转十几度 —— 直接拿最新值算角度，姿态就是 20 Hz 的
     * 台阶。{@link QuadStarProjectile#getRenderAim(float)} 在上一 tick 和这一 tick 之间取值，和
     * {@code getPosition(partialTicks)} 同一个时间基准。
     *
     * <p>（早先这里注释说「客户端旋转每 tick 只走剩余差的 1/3，插值输入不均匀」—— 那是
     * {@code LivingEntity} 的行为，不是 {@code Entity} 的：普通实体的 {@code lerpTo} 直接 {@code setRot}，
     * 所以那个理由不成立，路线本身也没问题。）
     *
     * <p>🔴 <b>没有扩张过程。</b>原来蓄力期按 tick 比例从 0.35 长到 1.0，主人 11-13 要求去掉：星一出现
     * 就是全尺寸。
     */
    private void body(QuadStarProjectile entity, float partialTicks, PoseStack poseStack, MultiBufferSource buffer) {
        if (FourPointStarShaders.fourPointStar() == null) {
            return;
        }
        Vec3 aim = entity.getRenderAim(partialTicks);
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(-QuadStarProjectile.yawOf(aim)));
        poseStack.mulPose(Axis.XP.rotationDegrees(QuadStarProjectile.pitchOf(aim)));
        poseStack.mulPose(Axis.ZP.rotationDegrees(ROLL));
        Matrix4f matrix = poseStack.last().pose();
        VertexConsumer solid = buffer.getBuffer(QuadStarRenderTypes.star());
        this.plane(matrix, solid, 0.0F, 1.0F, WING_MAIN, BRIGHT_MAIN, HALO_ON, SOLID);
        this.plane(matrix, solid, 1.0F, 0.0F, WING_CROSS, BRIGHT_CROSS, HALO_OFF, SOLID);
        poseStack.popPose();
    }

    private void plane(Matrix4f matrix, VertexConsumer consumer, float lateralX, float lateralY,
            float wing, float bright, float halo, float alpha) {
        this.vertex(matrix, consumer, CORNER_A, CORNER_A, lateralX, lateralY, wing, bright, halo, alpha);
        this.vertex(matrix, consumer, CORNER_B, CORNER_A, lateralX, lateralY, wing, bright, halo, alpha);
        this.vertex(matrix, consumer, CORNER_B, CORNER_B, lateralX, lateralY, wing, bright, halo, alpha);
        this.vertex(matrix, consumer, CORNER_A, CORNER_B, lateralX, lateralY, wing, bright, halo, alpha);
    }

    private void vertex(Matrix4f matrix, VertexConsumer consumer, float along, float across,
            float lateralX, float lateralY, float wing, float bright, float halo, float alpha) {
        consumer.vertex(matrix, lateralX * across, lateralY * across, along)
                .color(bright, wing, halo, alpha)
                .uv(along, across)
                .endVertex();
    }

    /**
     * 预警线：从星体前方一直画到第一个挡路的方块，端点就是这一发会落在哪儿。
     *
     * <p>⚠ 这个方法的两个随时间变动的输入都是插值过的：{@code origin} 来自
     * {@code getPosition(partialTicks)}，{@code dir} 来自 {@code getRenderAim(partialTicks)}；射线判定
     * 本身是确定性的。所以线不会因为「方向每 tick 才更新一次」而跳 —— 之前它和星一起抖，根因是两者都
     * 直接吃了未插值的采样，而不是画法有问题。
     */
    private void renderLine(QuadStarProjectile entity, float partialTicks, PoseStack poseStack,
            MultiBufferSource buffer) {
        Vec3 dir = entity.getRenderAim(partialTicks);
        Vec3 origin = entity.getPosition(partialTicks);
        Vec3 start = origin.add(dir.scale(LINE_START));
        Vec3 end = start.add(dir.scale(entity.getLineLength()));
        BlockHitResult clip = entity.level().clip(new ClipContext(start, end,
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity));
        if (clip.getType() != HitResult.Type.MISS) {
            end = clip.getLocation().subtract(dir.scale(LINE_END_PAD));
        }
        Vec3 camera = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        Vec3 side = this.side(dir, camera, start);
        Matrix4f matrix = poseStack.last().pose();
        this.beam(buffer.getBuffer(QuadStarRenderTypes.beamGlow()), matrix, origin, start, end, side, camera,
                LINE_GLOW_HALF, LINE_GLOW_TAPER, LINE_GLOW_ALPHA, LINE_GLOW_R, LINE_GLOW_G, LINE_GLOW_B);
        this.beam(buffer.getBuffer(QuadStarRenderTypes.beamCore()), matrix, origin, start, end, side, camera,
                LINE_CORE_HALF, LINE_CORE_TAPER, LINE_CORE_ALPHA, LINE_CORE_R, LINE_CORE_G, LINE_CORE_B);
    }

    private void renderTrail(QuadStarProjectile entity, float partialTicks, PoseStack poseStack,
            MultiBufferSource buffer) {
        Vec3 dir = entity.getRenderAim(partialTicks);
        Vec3 origin = entity.getPosition(partialTicks);
        Vec3 head = origin.subtract(dir.scale(TRAIL_START));
        Vec3 camera = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        Vec3 side = this.side(dir, camera, head);
        Matrix4f matrix = poseStack.last().pose();
        VertexConsumer consumer = buffer.getBuffer(QuadStarRenderTypes.beamTrail());
        for (int layer = LAYER_VEIL; layer <= LAYER_EDGE; layer++) {
            this.trailLayer(consumer, matrix, origin, dir, side, camera, layer);
        }
    }

    private void trailLayer(VertexConsumer consumer, Matrix4f matrix, Vec3 origin, Vec3 dir, Vec3 side,
            Vec3 camera, int layer) {
        int segments = TRAIL_SEGMENTS[layer];
        for (int i = 0; i < segments; i++) {
            float t0 = i / (float) segments;
            float t1 = (i + 1) / (float) segments;
            this.trailRow(consumer, matrix, origin, dir, side, camera, layer, t0, t1);
        }
    }

    private void trailRow(VertexConsumer consumer, Matrix4f matrix, Vec3 origin, Vec3 dir, Vec3 side,
            Vec3 camera, int layer, float t0, float t1) {
        Vec3 world0 = this.trailCenter(origin, dir, side, layer, t0);
        Vec3 world1 = this.trailCenter(origin, dir, side, layer, t1);
        Vec3 c0 = world0.subtract(origin);
        Vec3 c1 = world1.subtract(origin);
        float w0 = this.trailHalf(layer, t0, camera, world0);
        float w1 = this.trailHalf(layer, t1, camera, world1);
        Vec3 s0 = side.scale(w0);
        Vec3 s1 = side.scale(w1);
        float a0 = this.trailAlpha(layer, t0);
        float a1 = this.trailAlpha(layer, t1);
        float f0 = (float) Math.pow(1.0F - t0, TRAIL_COLOR_POW[layer]);
        float f1 = (float) Math.pow(1.0F - t1, TRAIL_COLOR_POW[layer]);
        int base = layer * 3;
        float r0 = Mth.lerp(f0, TRAIL_HEAD_RGB[base], TRAIL_TAIL_RGB[base]);
        float g0 = Mth.lerp(f0, TRAIL_HEAD_RGB[base + 1], TRAIL_TAIL_RGB[base + 1]);
        float b0 = Mth.lerp(f0, TRAIL_HEAD_RGB[base + 2], TRAIL_TAIL_RGB[base + 2]);
        float r1 = Mth.lerp(f1, TRAIL_HEAD_RGB[base], TRAIL_TAIL_RGB[base]);
        float g1 = Mth.lerp(f1, TRAIL_HEAD_RGB[base + 1], TRAIL_TAIL_RGB[base + 1]);
        float b1 = Mth.lerp(f1, TRAIL_HEAD_RGB[base + 2], TRAIL_TAIL_RGB[base + 2]);
        for (int j = 0; j < TRAIL_U_SAMPLES; j++) {
            float u0 = -1.0F + 2.0F * j / TRAIL_U_SAMPLES;
            float u1 = -1.0F + 2.0F * (j + 1) / TRAIL_U_SAMPLES;
            float e0 = this.trailProfile(layer, u0);
            float e1 = this.trailProfile(layer, u1);
            Vec3 p00 = c0.add(s0.scale(u0));
            Vec3 p01 = c0.add(s0.scale(u1));
            Vec3 p11 = c1.add(s1.scale(u1));
            Vec3 p10 = c1.add(s1.scale(u0));
            float fU0 = (u0 + 1.0F) * 0.5F;
            float fU1 = (u1 + 1.0F) * 0.5F;
            this.lineVertex(matrix, consumer, p00, fU0, t0, a0 * e0, r0, g0, b0);
            this.lineVertex(matrix, consumer, p01, fU1, t0, a0 * e1, r0, g0, b0);
            this.lineVertex(matrix, consumer, p11, fU1, t1, a1 * e1, r1, g1, b1);
            this.lineVertex(matrix, consumer, p00, fU0, t0, a0 * e0, r0, g0, b0);
            this.lineVertex(matrix, consumer, p11, fU1, t1, a1 * e1, r1, g1, b1);
            this.lineVertex(matrix, consumer, p10, fU0, t1, a1 * e0, r1, g1, b1);
        }
    }

    private Vec3 trailCenter(Vec3 origin, Vec3 dir, Vec3 side, int layer, float t) {
        float bow = TRAIL_BOW_AMP[layer] * (float) Math.pow(t, TRAIL_BOW_POW[layer]);
        float drift = TRAIL_DRIFT_AMP[layer] * (float) Math.pow(1.0F - t, TRAIL_DRIFT_POW[layer])
                + TRAIL_DRIFT_MIN[layer];
        return origin.subtract(dir.scale(TRAIL_LENGTH[layer] * t)).add(side.scale(bow + drift));
    }

    private float trailHalf(int layer, float t, Vec3 camera, Vec3 point) {
        float grow = TRAIL_WIDTH_B[layer] == 0.0F ? 0.0F : smoothstep(0.0F, TRAIL_WIDTH_GROW[layer], t);
        float half = (TRAIL_WIDTH_A[layer] + TRAIL_WIDTH_B[layer] * grow)
                * (float) Math.pow(1.0F - t, TRAIL_WIDTH_POW[layer]) + TRAIL_WIDTH_MIN[layer];
        return Math.max(half, (float) (camera.distanceTo(point) * TRAIL_MIN_SCREEN));
    }

    private float trailAlpha(int layer, float t) {
        float flow = TRAIL_FLOW[layer];
        float pulse = flow <= 0.0F ? 1.0F
                : 1.0F - flow + flow * Mth.sin((float) (Math.PI * 2.0D * TRAIL_FLOW_RATE * t) + TRAIL_FLOW_PHASE);
        return TRAIL_ALPHA[layer] * (float) Math.pow(1.0F - t, TRAIL_ALPHA_POW[layer]) * pulse;
    }

    private float trailProfile(int layer, float u) {
        float edge = TRAIL_PROFILE_EDGE[layer];
        if (edge <= 0.0F) {
            float k = 1.0F - u * u;
            return (float) Math.pow(k < 0.0F ? 0.0F : k, TRAIL_PROFILE_POW[layer]);
        }
        return 1.0F - smoothstep(edge, 1.0F, Math.abs(u));
    }

    private static float smoothstep(float from, float to, float value) {
        float t = Mth.clamp((value - from) / (to - from), 0.0F, 1.0F);
        return t * t * (3.0F - 2.0F * t);
    }

    private Vec3 side(Vec3 dir, Vec3 camera, Vec3 anchor) {
        Vec3 side = dir.cross(camera.subtract(anchor));
        if (side.lengthSqr() < 1.0E-6D) {
            side = dir.cross(new Vec3(0.0D, 1.0D, 0.0D));
            if (side.lengthSqr() < 1.0E-6D) {
                side = new Vec3(1.0D, 0.0D, 0.0D);
            }
        }
        return side.normalize();
    }

    /**
     * 一条带锥度的带状线。
     *
     * <p>锥度是这一版和上一版的区别：上一版整条等宽，末端是个齐口的矩形，看着像贴上去的色条；现在从星
     * 体那一端往落点逐渐收细，末端仍然是硬边 —— 落点就是要画清楚，不能淡掉。
     */
    private void beam(VertexConsumer consumer, Matrix4f matrix, Vec3 origin, Vec3 start, Vec3 end, Vec3 side,
            Vec3 camera, float half, float taper, float alpha, float red, float green, float blue) {
        for (int i = 0; i < LINE_SEGMENTS; i++) {
            float t0 = i / (float) LINE_SEGMENTS;
            float t1 = (i + 1) / (float) LINE_SEGMENTS;
            Vec3 p0 = start.lerp(end, t0);
            Vec3 p1 = start.lerp(end, t1);
            float w0 = this.lineWidth(camera, p0, half * (1.0F - (1.0F - taper) * t0));
            float w1 = this.lineWidth(camera, p1, half * (1.0F - (1.0F - taper) * t1));
            Vec3 s0 = side.scale(w0);
            Vec3 s1 = side.scale(w1);
            this.lineVertex(matrix, consumer, p0.subtract(s0).subtract(origin), 0.0F, t0, alpha, red, green, blue);
            this.lineVertex(matrix, consumer, p0.add(s0).subtract(origin), 1.0F, t0, alpha, red, green, blue);
            this.lineVertex(matrix, consumer, p1.add(s1).subtract(origin), 1.0F, t1, alpha, red, green, blue);
            this.lineVertex(matrix, consumer, p1.subtract(s1).subtract(origin), 0.0F, t1, alpha, red, green, blue);
        }
    }

    private float lineWidth(Vec3 camera, Vec3 point, float half) {
        return Math.max(half, (float) (camera.distanceTo(point) * LINE_MIN_SCREEN));
    }

    private void lineVertex(Matrix4f matrix, VertexConsumer consumer, Vec3 pos, float u, float v, float alpha,
            float red, float green, float blue) {
        consumer.vertex(matrix, (float) pos.x, (float) pos.y, (float) pos.z)
                .color(red, green, blue, alpha)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .normal(0.0F, 1.0F, 0.0F)
                .endVertex();
    }

    @Override
    public ResourceLocation getTextureLocation(QuadStarProjectile entity) {
        return TEXTURE;
    }
}
