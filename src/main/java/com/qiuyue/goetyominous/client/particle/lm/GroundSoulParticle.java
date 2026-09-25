package com.qiuyue.goetyominous.client.particle.lm;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * 「地面灵魂」—— 灵魂柱从地里冒出来之前，地面上先亮起的那团光
 * （红色版 {@code ground_soul_red}，灵魂柱用的就是它）。
 *
 * <p>从传奇怪物的同名类逐字照搬，<b>只换了包名</b>。贴图在
 * {@code assets/goetyominous/textures/particle/ground_soul_red_0..6.png}（7 帧）。
 *
 * <h2>它和 {@link SoulPillarExplosion} 是双胞胎</h2>
 * 两个类<b>逐字一样</b>，只有一个数字不同：这里的 {@code lifetime = 14}，
 * 那边是 16。原版就是这么复制粘贴出来的两份，我们照搬保留两份，
 * 这样将来和原版对照时能一一对上（合并成一份省不了多少事，反而会对不上号）。
 *
 * <h2>它为什么自己写了一个 {@code render()}</h2>
 * 基类 {@code TextureSheetParticle} 的默认渲染是「球面公告板」—— 粒子始终正对镜头，
 * 你抬头低头它也跟着转。那对一团地面上的光是不对的。
 *
 * <p>这里的写法是<b>只绕 Y 轴旋转</b>（{@code rotateY(-相机偏航角)}）：
 * 粒子永远<b>水平地</b>朝着你，你仰头看它也不会跟着翻过去 —— 就是一张「贴在地上的图」。
 * 这种做法叫「Y 轴公告板」，很多地面法阵、光圈都用它。
 *
 * <p>下面四个顶点是按「左下 → 左上 → 右上 → 右下」的<b>逆时针</b>顺序摆的，
 * 因为 Minecraft 默认剔除背面，顺序反了会看不见。
 *
 * <h2>⚠️ 两处原版的死代码，照抄保留</h2>
 * <ul>
 *   <li>{@link #getLightColor} 里算了大半天 {@code j}、{@code k}，
 *       还写了 {@code if (j > 240)} 的分支 —— 但<b>最后无条件 {@code return 240}</b>，
 *       前面全是白算。效果就是「永远满亮」。</li>
 *   <li>构造器里 {@code this.xd += pXSpeed} 连着写两遍（先赋值再加一遍），
 *       也就是速度被<b>加了两次</b>。看着像手滑，但改了行为就不一样了，保留。</li>
 * </ul>
 */
@OnlyIn(Dist.CLIENT)
public class GroundSoulParticle extends TextureSheetParticle {

    private final SpriteSet sprites;

    GroundSoulParticle(ClientLevel pLevel, double pX, double pY, double pZ,
                       double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet pSprites) {
        super(pLevel, pX, pY, pZ, 0.0, 0.0, 0.0);
        this.sprites = pSprites;
        // ↓ 赋值一次、又 += 一次 —— 等价于「速度乘 2」。原版如此，见类注释。
        this.xd = pXSpeed;
        this.yd = pYSpeed;
        this.zd = pZSpeed;
        this.xd += pXSpeed;
        this.yd += pYSpeed;
        this.zd += pZSpeed;
        this.quadSize = 2.0F;
        this.lifetime = 14;
        this.setSize(3.0F, 3.0F);
        this.setSpriteFromAge(pSprites);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    /** 尺寸恒定不变 —— 覆写成常数，把基类「随年龄缩放」的行为摁掉。 */
    @Override
    public float getQuadSize(float pScaleFactor) {
        return this.quadSize;
    }

    /**
     * 见类注释：不调 {@code super.tick()}，所以<b>没有物理</b> ——
     * 重力、摩擦、碰撞全都不过，速度永远不变，粒子就这么匀速飘着。
     */
    @Override
    public void tick() {
        this.setSpriteFromAge(this.sprites);
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        }
    }

    /** Y 轴公告板渲染，见类注释。 */
    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        Vec3 vec3 = camera.getPosition();
        float f = (float) (Mth.lerp(partialTicks, this.xo, this.x) - vec3.x());
        float f1 = (float) (Mth.lerp(partialTicks, this.yo, this.y) - vec3.y());
        float f2 = (float) (Mth.lerp(partialTicks, this.zo, this.z) - vec3.z());
        Quaternionf quaternionf = new Quaternionf();
        quaternionf.rotateY((float) Math.toRadians(-camera.getYRot()));
        Vector3f[] avector3f = new Vector3f[]{
                new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F),
                new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)
        };
        float f3 = this.getQuadSize(partialTicks);

        for (int i = 0; i < 4; i++) {
            Vector3f vector3f = avector3f[i];
            vector3f.rotate(quaternionf);
            vector3f.mul(f3);
            vector3f.add(f, f1, f2);
        }

        float f6 = this.getU0();
        float f7 = this.getU1();
        float f4 = this.getV0();
        float f5 = this.getV1();
        int j = this.getLightColor(partialTicks);
        buffer.vertex(avector3f[0].x(), avector3f[0].y(), avector3f[0].z()).uv(f7, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        buffer.vertex(avector3f[1].x(), avector3f[1].y(), avector3f[1].z()).uv(f7, f4).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        buffer.vertex(avector3f[2].x(), avector3f[2].y(), avector3f[2].z()).uv(f6, f4).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        buffer.vertex(avector3f[3].x(), avector3f[3].y(), avector3f[3].z()).uv(f6, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
    }

    /** ⚠️ 前面全是白算的，最后无条件返回 240（满亮）。原版如此，见类注释。 */
    @Override
    public int getLightColor(float p_106821_) {
        float f = (this.age + p_106821_) / this.lifetime;
        f = Mth.clamp(f, 0.0F, 1.0F);
        int i = super.getLightColor(p_106821_);
        int j = i & 0xFF;
        int k = i >> 16 & 0xFF;
        j += (int) (f * 15.0F * 16.0F);
        if (j > 240) {
            int var8 = 240;
        }

        return 240;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {

        private final SpriteSet spriteSet;

        public Factory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn,
                                       double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new GroundSoulParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
        }
    }

    /**
     * 原版还有个「打喷嚏」用的染色版 provider —— 注册表里暂时没给它派活。
     * 照抄保留，不删（将来想换个颜色时直接换 provider 就行）。
     */
    @OnlyIn(Dist.CLIENT)
    public static class SneezeProvider implements ParticleProvider<SimpleParticleType> {

        private final SpriteSet sprites;

        public SneezeProvider(SpriteSet pSprites) {
            this.sprites = pSprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel,
                                       double pX, double pY, double pZ,
                                       double pXSpeed, double pYSpeed, double pZSpeed) {
            Particle particle = new GroundSoulParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, this.sprites);
            particle.setColor(200.0F, 50.0F, 120.0F);
            return particle;
        }
    }
}
