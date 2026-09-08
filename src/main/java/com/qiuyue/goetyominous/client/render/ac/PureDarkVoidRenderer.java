package com.qiuyue.goetyominous.client.render.ac;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.client.particle.ac.RitualNoise;
import com.qiuyue.goetyominous.common.entities.util.PureDarkVoid;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Map;
import java.util.Random;
import java.util.WeakHashMap;

/**
 * 仿 Alex's Caves Underzealot 献祭仪式的虚空特效渲染。
 *
 * 结构对齐 AC VoidBeingCloudParticle/Eye/Tendril:
 *  - 黑色噪点云牌(quad half≈2,与 AC size=1 同尺寸同 64px 纹理);
 *  - 3~5 只 void_eye 环绕云边缘成环,生命周期后 1/3 向中心收拢并下坠到
 *    祭坛焦点(模拟 AC 眼 age>200 后聚向牺牲物 + 抖动 + 末段下沉);
 *  - 6 条触须从云下缘垂挂到祭坛焦点下(对应 AC tendril 俯冲向下方牺牲物),
 *    临近结束被抽回云中(虚空"吞没"),整体 alpha 进出与 AC getAlphaFromAge 一致。
 *
 * 实体渲染器是单例、跨世界/跨资源重载存活。TextureManager 重载(F3+T/进存档)会
 * close() 已注册的 DynamicTexture(释放 NativeImage + GL id,getPixels() 归 null),
 * 此后不再自动重建;本渲染器需自愈重建并重新注册,否则云贴图永久失效。同族见粒子坑。
 */
public class PureDarkVoidRenderer extends EntityRenderer<PureDarkVoid> {

    private static final ResourceLocation[] EYE_TEXTURES = {
            new ResourceLocation("alexscaves", "textures/particle/void_eye_0.png"),
            new ResourceLocation("alexscaves", "textures/particle/void_eye_1.png"),
            new ResourceLocation("alexscaves", "textures/particle/void_eye_2.png")
    };
    private static final ResourceLocation TENDRIL_TEXTURE =
            new ResourceLocation("alexscaves", "textures/particle/void_being_cloud_tendril.png");
    private static final ResourceLocation CLOUD_LOCATION =
            new ResourceLocation("goetyominous", "ritual_void_cloud_live");

    private static final int TEXTURE_SIZE = 64;
    private static final int LIFETIME = PureDarkVoid.LIFETIME;
    // AC VoidBeingCloud size 参数 1 -> quad half = size(2),64px 纹理,与此一致
    private static final float CLOUD_HALF = 2.0F;
    private static final float EYE_HALF = 0.42F;

    // 云下缘到"祭坛焦点"的相对高度(虚空实体在 altar+3,焦点≈祭坛顶/产物落点)
    private static final float FOCUS_Y = -2.6F;
    // AC 眼中 age>200(300 寿命)开始聚拢 -> 用寿命后 100 tick
    private static final int CONVERGE_FROM = LIFETIME - 100;
    // 最后 60 tick 触须被抽回云中(吞没感)
    private static final int RETRACT_FROM = LIFETIME - 60;
    // AC 眼在 lifetime-20 起每 tick 下沉 0.25;这里焦点只到祭坛顶,下沉封顶以免穿地
    private static final int SINK_FROM = LIFETIME - 18;
    private static final float SINK_RATE = 0.25F;
    private static final float SINK_MAX = 0.9F;

    private DynamicTexture cloudTexture;
    private final RenderType cloudRenderType;
    private final Map<PureDarkVoid, VisualSpec> specs = new WeakHashMap<>();
    private int lastUploadedAge = -1;

    public PureDarkVoidRenderer(EntityRendererProvider.Context context) {
        super(context);
        // entityTranslucentEmissive = COLOR_WRITE(只写颜色、不写深度)+ LEQUAL 深度测试:
        // 云不再写入深度墙,不会把绕飞时落在云平面后的眼睛深度剔除;世界地形仍能正常遮挡。
        this.cloudRenderType = RenderType.entityTranslucentEmissive(CLOUD_LOCATION);
        this.ensureCloudTexture();
    }

    private void ensureCloudTexture() {
        if (this.cloudTexture == null || this.cloudTexture.getPixels() == null) {
            this.cloudTexture = new DynamicTexture(TEXTURE_SIZE, TEXTURE_SIZE, true);
            Minecraft.getInstance().textureManager.register(CLOUD_LOCATION, this.cloudTexture);
            this.lastUploadedAge = -1;
        }
    }

    @Override
    public void render(PureDarkVoid entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {
        int spawnTime = entity.getSpawnTime();
        int age = spawnTime < 0 ? -1 : (int) (entity.level().getGameTime() - spawnTime);
        if (age < 0 || age > LIFETIME) {
            return;
        }
        float t = (float) age + partialTicks;
        float alpha = getAlphaFromAge(age, LIFETIME);
        if (alpha <= 0.0F) {
            return;
        }
        this.ensureCloudTexture();
        if (age != this.lastUploadedAge) {
            this.updateCloudTexture(age);
            this.lastUploadedAge = age;
        }
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        Quaternionf cameraQuat = camera.rotation();
        VisualSpec spec = this.specs.computeIfAbsent(entity, PureDarkVoidRenderer::buildSpec);
        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix4f = pose.pose();
        Matrix3f matrix3f = pose.normal();

        // 云 -> 垂挂触须 -> 眼(最后画在云上更醒目),与 AC 观感一致
        this.renderCloud(bufferSource, matrix4f, matrix3f, t, alpha, cameraQuat);
        this.renderTendrils(bufferSource, matrix4f, matrix3f, spec, t, age, alpha, cameraQuat);
        this.renderEyes(bufferSource, matrix4f, matrix3f, spec, t, age, alpha, cameraQuat);
    }

    private static float getAlphaFromAge(int age, int lifetime) {
        float fadeIn = Math.min(20, age) / 20.0F;
        float fadeOut = age > lifetime - 10 ? (float) (lifetime - age) / 10.0F : 1.0F;
        return fadeIn * fadeOut;
    }

    private void updateCloudTexture(int age) {
        if (this.cloudTexture.getPixels() == null) {
            return;
        }
        int center = TEXTURE_SIZE / 2;
        double radiusSq = (double) (center * center) * getAlphaFromAge(age, LIFETIME);
        for (int i = 0; i < TEXTURE_SIZE; i++) {
            for (int j = 0; j < TEXTURE_SIZE; j++) {
                double d0 = center - i;
                double d1 = center - j;
                double d2 = d0 * d0 + d1 * d1;
                double f1 = RitualNoise.noise(i / 14.0, age / 14.0, j / 14.0);
                double denom = radiusSq * (1.0 - f1 * f1);
                double a = denom <= 0 ? 0 : (1.0 - d2 / denom);
                if (a <= 0.0) {
                    this.cloudTexture.getPixels().setPixelRGBA(j, i, 0);
                } else {
                    int alpha = (int) Math.min(a * 255.0, 255.0);
                    this.cloudTexture.getPixels().setPixelRGBA(j, i, FastColor.ARGB32.color(alpha, 6, 5, 12));
                }
            }
        }
        this.cloudTexture.upload();
    }

    private void renderCloud(MultiBufferSource bufferSource, Matrix4f matrix4f, Matrix3f matrix3f,
                             float t, float alpha, Quaternionf cameraQuat) {
        VertexConsumer buffer = bufferSource.getBuffer(this.cloudRenderType);
        float bob = 0.2F * (float) Math.sin(t * 0.1);
        float pulse = 1.0F + 0.04F * (float) Math.sin(t * 0.06);
        float half = CLOUD_HALF * pulse;
        Vector3f[] corners = {
                new Vector3f(-1.0F, -1.0F, 0.0F),
                new Vector3f(-1.0F, 1.0F, 0.0F),
                new Vector3f(1.0F, 1.0F, 0.0F),
                new Vector3f(1.0F, -1.0F, 0.0F)
        };
        for (int i = 0; i < 4; i++) {
            corners[i].add(0.0F, bob, 0.0F);
            corners[i].rotate(cameraQuat);
            corners[i].mul(half);
        }
        this.emitQuad(buffer, matrix4f, matrix3f, corners, 1.0F, 1.0F, 1.0F, alpha);
    }

    private void renderEyes(MultiBufferSource bufferSource, Matrix4f matrix4f, Matrix3f matrix3f,
                            VisualSpec spec, float t, int age, float alpha, Quaternionf cameraQuat) {        float conv = Mth.clamp((age - CONVERGE_FROM) / 100.0F, 0.0F, 1.0F);
        float sink = age > SINK_FROM ? Math.min((t - SINK_FROM) * SINK_RATE, SINK_MAX) : 0.0F;
        float shakeAmt = conv * 0.35F;
        for (int i = 0; i < spec.eyes.length; i++) {
            Eye eye = spec.eyes[i];
            float appear = Mth.clamp((t - (8.0F + i * 3.0F)) / 12.0F, 0.0F, 1.0F);
            if (appear <= 0.0F) {
                continue;
            }
            // 环绕云的假轨道 + 后 1/3 收拢到中心并下坠(AC: converge to (0,-5) + 抖动)
            float ang = eye.baseAngle + t * eye.drift;
            float radius = Mth.lerp(conv, eye.radius, 0.0F);
            float bob = 0.1F * (float) Math.sin(t * 0.09 + eye.phase);
            float shakeX = shakeAmt * (float) Math.sin(t * 0.54 + eye.phase * 3.0F);
            float shakeY = shakeAmt * (float) -Math.sin(t * 0.54 + eye.phase * 3.0F + 2.0F);
            float ox = (float) Math.cos(ang) * radius + shakeX;
            float oz = (float) Math.sin(ang) * radius;
            float oy = Mth.lerp(conv, eye.height, FOCUS_Y) + bob + shakeY - sink;

            float scale = EYE_HALF * (1.0F + 0.12F * (float) Math.sin(t * 0.13 + eye.phase));
            VertexConsumer eyeBuffer = bufferSource.getBuffer(
                    RenderType.entityTranslucentEmissive(EYE_TEXTURES[eye.textureIndex]));
            this.emitBillboard(eyeBuffer, matrix4f, matrix3f, ox, oy, oz, scale, cameraQuat,
                    1.0F, 1.0F, 1.0F, alpha * appear);
        }
    }

    private void renderTendrils(MultiBufferSource bufferSource, Matrix4f matrix4f, Matrix3f matrix3f,
                                VisualSpec spec, float t, int age, float alpha, Quaternionf cameraQuat) {
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(TENDRIL_TEXTURE));
        Vector3f right = new Vector3f(1.0F, 0.0F, 0.0F);
        right.rotate(cameraQuat);
        float retract = Mth.clamp((age - RETRACT_FROM) / 60.0F, 0.0F, 1.0F);
        for (int i = 0; i < spec.tendrils.length; i++) {
            Tendril ten = spec.tendrils[i];
            float unfurl = Mth.clamp((t - ten.start) / 55.0F, 0.0F, 1.0F);
            if (unfurl <= 0.0F) {
                continue;
            }
            float ease = (float) Math.sin(unfurl * (float) Math.PI * 0.5F);
            Vec3 root = new Vec3(ten.rootX, ten.rootY, ten.rootZ);
            // 尖端默认垂到祭坛焦点;临近结束被抽回云中(吞没)
            Vec3 tip = new Vec3(
                    Mth.lerp(retract, ten.tipX, ten.rootX),
                    Mth.lerp(retract, ten.tipY, ten.rootY - 0.3F),
                    Mth.lerp(retract, ten.tipZ, ten.rootZ));
            if (ease < 1.0F) {
                tip = root.lerp(tip, ease);
            }
            float phase = t * 0.06F + ten.phase;
            float halfW = 0.55F * (1.0F - 0.55F * ease) * (0.35F + 0.65F * ease) * (1.0F - 0.5F * retract);
            float sampleCount = 18.0F;
            Vec3 drawFrom = root;
            float segAlphaMul = alpha * (0.4F + 0.6F * ease);
            for (int s = 1; s <= sampleCount; s++) {
                float f = s / sampleCount;
                float wiggle = (float) Math.sin(f * (float) Math.PI);
                Vec3 drawTo = new Vec3(
                        Mth.lerp(f, root.x, tip.x),
                        Mth.lerp(f, root.y, tip.y),
                        Mth.lerp(f, root.z, tip.z));
                drawTo = drawTo.add(
                        Math.sin(phase + f * 1.6F + ten.phase) * wiggle * 0.2F,
                        Math.sin(phase * 0.7F + f * 2.1F) * wiggle * 0.2F,
                        Math.cos(phase + f * 1.6F) * wiggle * 0.2F);
                float u1 = (s - 1) / sampleCount;
                float u2 = s / sampleCount;
                float startA = Math.min(1.0F, s / (sampleCount - 7.0F)) * segAlphaMul;
                float endA = Math.min(1.0F, (s + 1) / (sampleCount - 7.0F)) * segAlphaMul;
                this.emitRibbon(buffer, matrix4f, matrix3f, drawFrom, drawTo, right, halfW, u1, u2, startA, endA);
                drawFrom = drawTo;
            }
        }
    }

    /** 画一块以 (ox,oy,oz) 为中心、面向相机的广告牌。偏移在旋转前加入 -> 世界坐标放置。 */
    private void emitBillboard(VertexConsumer buffer, Matrix4f matrix4f, Matrix3f matrix3f,
                               float ox, float oy, float oz, float half, Quaternionf cameraQuat,
                               float r, float g, float b, float alpha) {
        Vector3f[] corners = {
                new Vector3f(-1.0F, -1.0F, -0.05F),
                new Vector3f(-1.0F, 1.0F, -0.05F),
                new Vector3f(1.0F, 1.0F, -0.05F),
                new Vector3f(1.0F, -1.0F, -0.05F)
        };
        for (int i = 0; i < 4; i++) {
            corners[i].rotate(cameraQuat);
            corners[i].mul(half);
            corners[i].add(ox, oy, oz);
        }
        this.emitQuad(buffer, matrix4f, matrix3f, corners, r, g, b, alpha);
    }

    private void emitRibbon(VertexConsumer buffer, Matrix4f matrix4f, Matrix3f matrix3f,
                            Vec3 from, Vec3 to, Vector3f widthDir, float halfW,
                            float u1, float u2, float startA, float endA) {
        float wx = widthDir.x() * halfW;
        float wy = widthDir.y() * halfW;
        float wz = widthDir.z() * halfW;
        int light = 240;
        buffer.vertex(matrix4f, (float) from.x - wx, (float) from.y - wy, (float) from.z - wz)
                .color(1.0F, 1.0F, 1.0F, startA).uv(u1, 1.0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
        buffer.vertex(matrix4f, (float) to.x - wx, (float) to.y - wy, (float) to.z - wz)
                .color(1.0F, 1.0F, 1.0F, endA).uv(u2, 1.0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
        buffer.vertex(matrix4f, (float) to.x + wx, (float) to.y + wy, (float) to.z + wz)
                .color(1.0F, 1.0F, 1.0F, endA).uv(u2, 0.0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
        buffer.vertex(matrix4f, (float) from.x + wx, (float) from.y + wy, (float) from.z + wz)
                .color(1.0F, 1.0F, 1.0F, startA).uv(u1, 0.0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
    }

    private void emitQuad(VertexConsumer buffer, Matrix4f matrix4f, Matrix3f matrix3f,
                          Vector3f[] corners, float r, float g, float b, float alpha) {
        int light = 240;
        buffer.vertex(matrix4f, corners[0].x(), corners[0].y(), corners[0].z()).color(r, g, b, alpha)
                .uv(1.0F, 1.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
        buffer.vertex(matrix4f, corners[1].x(), corners[1].y(), corners[1].z()).color(r, g, b, alpha)
                .uv(1.0F, 0.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
        buffer.vertex(matrix4f, corners[2].x(), corners[2].y(), corners[2].z()).color(r, g, b, alpha)
                .uv(0.0F, 0.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
        buffer.vertex(matrix4f, corners[3].x(), corners[3].y(), corners[3].z()).color(r, g, b, alpha)
                .uv(0.0F, 1.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
    }

    private static VisualSpec buildSpec(PureDarkVoid entity) {
        Random random = new Random(entity.getId() * 2654435761L + 7919L);
        VisualSpec spec = new VisualSpec();

        int eyeCount = 3 + random.nextInt(3);
        spec.eyes = new Eye[eyeCount];
        for (int i = 0; i < eyeCount; i++) {
            Eye eye = new Eye();
            eye.baseAngle = (float) (i * 2.0 * Math.PI / eyeCount) + (random.nextFloat() - 0.5F) * 0.7F;
            // AC: 环半径 = (0.5+rnd*0.7)*size(2)*1.1 ≈ 1.1~2.6,再乘其 quad f4 0.5 → 云缘内侧小环
            eye.radius = 0.55F + random.nextFloat() * 0.75F;
            eye.height = (random.nextFloat() - 0.5F) * 1.2F;
            eye.phase = random.nextFloat() * (float) Math.PI * 2.0F;
            // 与 AC VoidBeingEyeParticle 一致:出生随机固定 0/1 两张之一(nextInt(2)),2 号不出现在仪式里
            eye.textureIndex = random.nextInt(2);
            eye.drift = 0.008F + random.nextFloat() * 0.012F;
            spec.eyes[i] = eye;
        }

        int tendrilCount = 6;
        spec.tendrils = new Tendril[tendrilCount];
        for (int i = 0; i < tendrilCount; i++) {
            Tendril ten = new Tendril();
            double angle = i * 2.0 * Math.PI / tendrilCount + (random.nextFloat() - 0.5F) * 0.8;
            double radius = 0.5 + random.nextDouble() * 1.0;
            ten.rootX = (float) (Math.cos(angle) * radius);
            ten.rootZ = (float) (Math.sin(angle) * radius);
            ten.rootY = -0.4F - random.nextFloat() * 0.8F;
            // 尖端垂到祭坛焦点附近(AC tendril 俯冲到下方牺牲物)
            ten.tipX = (random.nextFloat() - 0.5F) * 0.9F;
            ten.tipZ = (random.nextFloat() - 0.5F) * 0.9F;
            ten.tipY = FOCUS_Y - 0.2F - random.nextFloat() * 0.5F;
            ten.start = 10 + i * 7 + random.nextInt(6);
            ten.phase = random.nextFloat() * (float) Math.PI * 2.0F;
            spec.tendrils[i] = ten;
        }
        return spec;
    }

    private static class VisualSpec {
        Eye[] eyes;
        Tendril[] tendrils;
    }

    private static class Eye {
        float baseAngle;
        float radius;
        float height;
        float phase;
        float drift;
        int textureIndex;
    }

    private static class Tendril {
        float rootX;
        float rootY;
        float rootZ;
        float tipX;
        float tipY;
        float tipZ;
        float start;
        float phase;
    }

    @Override
    public ResourceLocation getTextureLocation(PureDarkVoid entity) {
        return EYE_TEXTURES[0];
    }
}
