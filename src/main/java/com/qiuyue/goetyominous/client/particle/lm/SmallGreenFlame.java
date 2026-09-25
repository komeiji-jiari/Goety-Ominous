package com.qiuyue.goetyominous.client.particle.lm;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.RisingParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * 「小火苗」—— 灵魂冲击二阶段叠在灵魂粒上面的那层红火。
 *
 * <h2>⚠️ 名字为什么叫「绿火焰」</h2>
 * 因为原作里这个类最早是给「歼灭之火」（{@code small_green_flame}，绿色的）写的，
 * 后来做红灵魂火时<b>直接复用了同一个类，只换了贴图</b>，
 * 名字就这么留下来了。我们照搬，连名字一起抄 —— 将来和原版代码对照时才对得上号。
 *
 * <p>贴图：{@code assets/goetyominous/textures/particle/red_soul_flame.png}（单帧）。
 *
 * <h2>和 {@link GhostlySoul} 的关系</h2>
 * 两个都继承原版的 {@link RisingParticle}（往上飘的粒子），但调法不同：
 * <ul>
 *   <li>{@code GhostlySoul} 靠 {@code setSpriteFromAge} 播 12 帧动画；</li>
 *   <li>这个只有一张贴图，靠下面三个覆写做出「火苗」的感觉：</li>
 * </ul>
 *
 * <table border="1">
 *   <caption>三个覆写分别管什么</caption>
 *   <tr><th>方法</th><th>作用</th></tr>
 *   <tr><td>{@link #getQuadSize(float)}</td>
 *       <td><b>越烧越小</b>。基类是线性缩小，这里改成「先慢后快」的抛物线，
 *           看起来更像火苗烧尽</td></tr>
 *   <tr><td>{@link #getLightColor(float)}</td>
 *       <td><b>越烧越亮</b>。每 tick 给自己加亮度，封顶 240 ——
 *           这样即使在漆黑的洞里，火苗也是亮着的（火自己会发光）</td></tr>
 *   <tr><td>{@link #move(double, double, double)}</td>
 *       <td>只挪碰撞箱，<b>不做任何碰撞检测</b>。也就是「穿墙」——
 *           粒子不该被地形挡住</td></tr>
 * </table>
 *
 * <h2>为什么有两个 Provider</h2>
 * {@link SmallFlameProvider} 比 {@link Provider} 多一句
 * {@code scale(0.5F)}（缩小一半）。原版给 {@code red_soul_flame} 配的是
 * <b>{@link SmallFlameProvider}</b>，别配错了 —— 配错的话火苗会大一倍。
 */
@OnlyIn(Dist.CLIENT)
public class SmallGreenFlame extends RisingParticle {

    SmallGreenFlame(ClientLevel pLevel, double pX, double pY, double pZ,
                    double pXSpeed, double pYSpeed, double pZSpeed) {
        super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    /** 见类注释表格第 3 行：只挪碰撞箱，不撞地形。 */
    @Override
    public void move(double pX, double pY, double pZ) {
        this.setBoundingBox(this.getBoundingBox().move(pX, pY, pZ));
        this.setLocationFromBoundingbox();
    }

    /** 见类注释表格第 1 行：越烧越小，而且是「先慢后快」的抛物线。 */
    @Override
    public float getQuadSize(float pScaleFactor) {
        float f = (this.age + pScaleFactor) / this.lifetime;
        return this.quadSize * (1.0F - f * f * 0.5F);
    }

    /** 见类注释表格第 2 行：越烧越亮，封顶 240。 */
    @Override
    public int getLightColor(float pPartialTick) {
        float f = (this.age + pPartialTick) / this.lifetime;
        f = Mth.clamp(f, 0.0F, 1.0F);
        int i = super.getLightColor(pPartialTick);
        int j = i & 0xFF;
        int k = i >> 16 & 0xFF;
        j += (int) (f * 15.0F * 16.0F);
        if (j > 240) {
            j = 240;
        }
        return j | k << 16;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {

        private final SpriteSet sprite;

        public Provider(SpriteSet pSprites) {
            this.sprite = pSprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel,
                                       double pX, double pY, double pZ,
                                       double pXSpeed, double pYSpeed, double pZSpeed) {
            SmallGreenFlame flameparticle = new SmallGreenFlame(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
            flameparticle.pickSprite(this.sprite);
            return flameparticle;
        }
    }

    /** 原版给 {@code red_soul_flame} 配的就是这个（多一句缩小一半）。 */
    @OnlyIn(Dist.CLIENT)
    public static class SmallFlameProvider implements ParticleProvider<SimpleParticleType> {

        private final SpriteSet sprite;

        public SmallFlameProvider(SpriteSet pSprites) {
            this.sprite = pSprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel,
                                       double pX, double pY, double pZ,
                                       double pXSpeed, double pYSpeed, double pZSpeed) {
            SmallGreenFlame flameparticle = new SmallGreenFlame(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
            flameparticle.pickSprite(this.sprite);
            flameparticle.scale(0.5F);
            return flameparticle;
        }
    }
}
