package com.qiuyue.goetyominous.client.particle.lm;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

/**
 * 「拖尾粒子」的基类 —— 画出一条跟在物体屁股后面的飘带。
 *
 * <p>从传奇怪物的同名类照搬，<b>只有一处改动</b>：原版用的是它自家的渲染类型
 * {@code LMRenderTypes.entityTranslucent(...)}，我们换成原版自带的
 * {@link RenderType#entityTranslucent(ResourceLocation)} ——
 * 两者是同一个东西（原版那个方法体里就是原封不动转发到原版方法的），
 * 所以效果完全一致，只是省掉了一个中间类。
 *
 * <h2>它到底在干什么</h2>
 * 一句话：<b>把粒子最近走过的 64 个位置连成一条带子</b>。
 * 分成三步：
 * <ol>
 *   <li><b>记账</b>（{@link #tickTrail()}）—— 每 tick 把当前位置塞进一个长度 64 的
 *       环形数组 {@code trailPositions}。<b>注意这是「环形」的</b>：
 *       写到第 64 个就绕回第 0 个覆盖旧的，所以内存固定、永不增长；</li>
 *   <li><b>取点</b>（{@link #getTrailPosition(int, float)}）—— 从当前位置往回数
 *       第 N 个历史点。那个 {@code & 63} 是环形数组的标准算法：
 *       因为 64 是 2 的幂，{@code & 63} 等价于「对 64 取余」但更快；</li>
 *   <li><b>画</b>（{@link #render(VertexConsumer, Camera, float)}）—— 把这些点
 *       首尾相接，每两点之间铺一个四边形（quad），
 *       上下两条边分别是 {@code topAngleVec} / {@code bottomAngleVec}，
 *       离得越远的点透明度越低（{@code trailA} 从 1 衰减到 0），于是就有了拖尾。</li>
 * </ol>
 *
 * <h2>为什么顶点要写四遍</h2>
 * {@code render} 里那一大段重复的 {@code vertexconsumer.vertex(...)...endVertex()}
 * 看着吓人，其实就是<b>同一个四边形画了四条边</b>：
 * 先是上下两个「起点」顶点，再是上下两个「终点」顶点。
 * Minecraft 的四边形必须按「左下 → 右下 → 右上 → 左上」的顺序提交顶点，
 * 所以不能合并成循环，只能老老实实写四遍。原版就是这么写的，照抄。
 *
 * <h2>子类要提供什么</h2>
 * 两个抽象方法：{@link #getTrailTexture()}（用哪张图）和
 * {@link #getTrailHeight()}（飘带多宽）。其余都能覆写但不必须。
 *
 * @see PhantomDaggerTrail 幻影匕首的拖尾，本类唯一的实现者
 */
@OnlyIn(Dist.CLIENT)
public abstract class AbstractNotGlowingTrailParticle extends Particle {

    /** 历史位置的环形数组。「64」这个长度是原版定的，改的话记得连 {@code & 63} 一起改。 */
    private final Vec3[] trailPositions = new Vec3[64];

    /** 环形数组的写指针，-1 表示「还没开始记账」。 */
    private int trailPointer = -1;

    /** 飘带颜色（0~1 的浮点 RGB），由子类通过构造函数传进来。 */
    public float r;
    public float g;
    public float b;

    /** 透明度。子类通常会随着粒子变老把它压下去，做出「渐渐消失」的效果。 */
    protected float trailA = 0.1F;

    public AbstractNotGlowingTrailParticle(ClientLevel world, double x, double y, double z,
                                           double xd, double yd, double zd,
                                           float r, float g, float b) {
        super(world, x, y, z);
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.r = r;
        this.g = g;
        this.b = b;
    }

    /**
     * 每 tick 的例行公事。
     *
     * <p>⚠️ 这里<b>完全覆写</b>了基类 {@code Particle.tick()}，没有调 {@code super}。
     * 也就是说基类那套「受重力、撞方块、被地形挡」的逻辑<b>统统不要了</b> ——
     * 拖尾粒子只需要自己那条轨迹，所以这里自己写了一份最小实现。
     * 原版如此，照抄。
     */
    @Override
    public void tick() {
        this.tickTrail();
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.xd *= 0.99;
        this.yd *= 0.99;
        this.zd *= 0.99;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.move(this.xd, this.yd, this.zd);
            this.yd = this.yd - this.gravity;
        }
    }

    /** 把当前位置记进环形数组。见类注释第 1 步。 */
    public void tickTrail() {
        Vec3 currentPosition = new Vec3(this.x, this.y, this.z);
        // 头一次跑的时候，先把 64 个格子全填成当前位置。
        // 不填的话前面几十格是 null，后面 getTrailPosition 会直接空指针崩掉。
        if (this.trailPointer == -1) {
            for (int i = 0; i < this.trailPositions.length; i++) {
                this.trailPositions[i] = currentPosition;
            }
        }

        if (++this.trailPointer == this.trailPositions.length) {
            this.trailPointer = 0;
        }

        this.trailPositions[this.trailPointer] = currentPosition;
    }

    /** 见类注释第 3 步。 */
    @Override
    public void render(VertexConsumer consumer, Camera camera, float partialTick) {
        if (this.trailPointer > -1) {
            // 拿到「粒子专用」的那批缓冲区，并告诉它我们要用半透明渲染层画这张贴图。
            MultiBufferSource.BufferSource multibuffersource$buffersource =
                    Minecraft.getInstance().renderBuffers().bufferSource();
            VertexConsumer vertexconsumer =
                    multibuffersource$buffersource.getBuffer(RenderType.entityTranslucent(this.getTrailTexture()));

            // 相机坐标。下面要把整个坐标系平移到相机原点，
            // 这样顶点坐标就是「相对相机的偏移」—— 粒子在世界里跑多远都不会有精度问题。
            Vec3 cameraPos = camera.getPosition();
            double x = (float) Mth.lerp(partialTick, this.xo, this.x);
            double y = (float) Mth.lerp(partialTick, this.yo, this.y);
            double z = (float) Mth.lerp(partialTick, this.zo, this.z);
            PoseStack posestack = new PoseStack();
            posestack.pushPose();
            posestack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

            int samples = 0;
            Vec3 drawFrom = new Vec3(x, y, z);

            // 飘带的「宽度方向」：绕相机朝向旋转 90 度，永远是横着的。
            // 这样无论从哪个角度看，飘带都是正对着你的、不会变成一条线。
            float zRot = this.getTrailRot(camera);
            Vec3 topAngleVec = new Vec3(0.0, this.getTrailHeight() / 2.0F, 0.0).zRot(zRot);
            Vec3 bottomAngleVec = new Vec3(0.0, this.getTrailHeight() / -2.0F, 0.0).zRot(zRot);

            int j = this.getLightColor(partialTick);

            while (samples < this.sampleCount()) {
                Vec3 sample = this.getTrailPosition(samples * this.sampleStep(), partialTick);
                // u 是贴图的横向坐标。每段四边形分到 1/sampleCount 这么宽的一条贴图，
                // 拼起来正好是整张贴图拉伸铺满 —— 这就是飘带贴图不重复的原因。
                float u1 = (float) samples / this.sampleCount();
                float u2 = u1 + 1.0F / this.sampleCount();
                Vec3 draw1 = drawFrom;
                Vec3 draw2 = sample;
                Pose posestack$pose = posestack.last();
                Matrix4f matrix4f = posestack$pose.pose();
                Matrix3f matrix3f = posestack$pose.normal();
                // 四条边，顺序：左下 → 右下 → 右上 → 左上（见类注释「为什么顶点要写四遍」）
                vertexconsumer.vertex(matrix4f,
                                (float) draw1.x + (float) bottomAngleVec.x,
                                (float) draw1.y + (float) bottomAngleVec.y,
                                (float) draw1.z + (float) bottomAngleVec.z)
                        .color(this.r, this.g, this.b, this.trailA)
                        .uv(u1, 1.0F)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(j)
                        .normal(matrix3f, 0.0F, 1.0F, 0.0F)
                        .endVertex();
                vertexconsumer.vertex(matrix4f,
                                (float) draw2.x + (float) bottomAngleVec.x,
                                (float) draw2.y + (float) bottomAngleVec.y,
                                (float) draw2.z + (float) bottomAngleVec.z)
                        .color(this.r, this.g, this.b, this.trailA)
                        .uv(u2, 1.0F)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(j)
                        .normal(matrix3f, 0.0F, 1.0F, 0.0F)
                        .endVertex();
                vertexconsumer.vertex(matrix4f,
                                (float) draw2.x + (float) topAngleVec.x,
                                (float) draw2.y + (float) topAngleVec.y,
                                (float) draw2.z + (float) topAngleVec.z)
                        .color(this.r, this.g, this.b, this.trailA)
                        .uv(u2, 0.0F)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(j)
                        .normal(matrix3f, 0.0F, 1.0F, 0.0F)
                        .endVertex();
                vertexconsumer.vertex(matrix4f,
                                (float) draw1.x + (float) topAngleVec.x,
                                (float) draw1.y + (float) topAngleVec.y,
                                (float) draw1.z + (float) topAngleVec.z)
                        .color(this.r, this.g, this.b, this.trailA)
                        .uv(u1, 0.0F)
                        .overlayCoords(OverlayTexture.NO_OVERLAY)
                        .uv2(j)
                        .normal(matrix3f, 0.0F, 1.0F, 0.0F)
                        .endVertex();
                samples++;
                drawFrom = sample;
            }

            // ⚠️ 这两句必须留在最后：把攒下来的四边形一次性送进显卡。
            //    放在循环里的话每段都提交一次，拖尾会长短不一、还会明显卡顿。
            multibuffersource$buffersource.endBatch();
            posestack.popPose();
        }
    }

    /** 飘带的横向角度 —— 跟着相机的上下俯仰走，让飘带永远「横躺」在你眼前。 */
    public float getTrailRot(Camera camera) {
        return (float) (-Math.PI / 180.0) * camera.getXRot();
    }

    /** 飘带有多宽（世界坐标格）。 */
    public abstract float getTrailHeight();

    /** 用哪张贴图。 */
    public abstract ResourceLocation getTrailTexture();

    /** 飘带分成多少段。段数越多越平滑、也越吃性能。 */
    public int sampleCount() {
        return 20;
    }

    /** 取历史点时每次往回跳几个。默认 1 = 每隔一 tick 取一个点。 */
    public int sampleStep() {
        return 1;
    }

    /** 见类注释第 2 步：从当前位置往回数第 {@code pointer} 个历史点。 */
    public Vec3 getTrailPosition(int pointer, float partialTick) {
        // 粒子已经没了的话，别再插值了，直接用最后一帧的真实位置。
        if (this.removed) {
            partialTick = 1.0F;
        }

        int i = this.trailPointer - pointer & 63;
        int j = this.trailPointer - pointer - 1 & 63;
        Vec3 d0 = this.trailPositions[j];
        Vec3 d1 = this.trailPositions[i].subtract(d0);
        // 在两个历史点之间按 partialTick 插值 —— 这样即使游戏帧率比 tick 快，
        // 飘带也是丝滑的，不会一格一格地跳。
        return d0.add(d1.scale(partialTick));
    }

    /** 拖尾永远按最亮渲染（240 是亮度上限），这样夜里也看得见。 */
    @Override
    public int getLightColor(float f) {
        return 240;
    }

    /** 用自定义渲染层 —— 也就是走上面 {@link #render} 这条路，而不是走普通粒子的贴图集。 */
    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }
}
