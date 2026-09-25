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
 * 「灵魂柱爆炸」—— 灵魂柱从地里炸出来那一瞬间的那团光。
 *
 * <p>从传奇怪物的同名类逐字照搬，<b>只换了包名</b>。贴图在
 * {@code assets/goetyominous/textures/particle/soul_pillar_explosion_0..7.png}（8 帧）。
 *
 * <p>⚠️ <b>别和 {@link SoulExplosion} 搞混</b>：那个是「灵魂三叉戟扎中目标」时炸的红光
 * （12 tick、8 帧）；这个是「灵魂柱冒出地面」时炸的光（16 tick、8 帧）。
 * 名字像、长得也像，但是两个不同的粒子，贴图也不是同一套。
 *
 * <p>它和 {@link GroundSoulParticle} <b>逐字一样</b>，只差一个 {@code lifetime}
 * （这里 16，那边 14）。原版就是复制粘贴的两份，我们照搬保留两份。
 *
 * <p>渲染上同样是「Y 轴公告板」（只绕 Y 转、不跟着你抬头低头翻），
 * 以及 {@link #getLightColor} 里那段「算了半天最后无条件返回 240」的原版死代码 ——
 * 详情见 {@link GroundSoulParticle} 的类注释，那边写得详细。
 */
@OnlyIn(Dist.CLIENT)
public class SoulPillarExplosion extends TextureSheetParticle {

    private final SpriteSet sprites;

    SoulPillarExplosion(ClientLevel pLevel, double pX, double pY, double pZ,
                        double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet pSprites) {
        super(pLevel, pX, pY, pZ, 0.0, 0.0, 0.0);
        this.sprites = pSprites;
        // ↓ 赋值一次、又 += 一次 —— 等价于「速度乘 2」。原版如此。
        this.xd = pXSpeed;
        this.yd = pYSpeed;
        this.zd = pZSpeed;
        this.xd += pXSpeed;
        this.yd += pYSpeed;
        this.zd += pZSpeed;
        this.quadSize = 2.0F;
        this.lifetime = 16;
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

    /** 不调 {@code super.tick()}，所以没有物理，粒子匀速飘到寿命结束。 */
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

    /** Y 轴公告板渲染，见 {@link GroundSoulParticle} 的类注释。 */
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

    /** ⚠️ 前面全是白算的，最后无条件返回 240（满亮）。原版如此。 */
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
            return new SoulPillarExplosion(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
        }
    }

    /** 原版的染色版 provider，注册表里没派活，照抄保留。 */
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
            Particle particle = new SoulPillarExplosion(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, this.sprites);
            particle.setColor(200.0F, 50.0F, 120.0F);
            return particle;
        }
    }
}
