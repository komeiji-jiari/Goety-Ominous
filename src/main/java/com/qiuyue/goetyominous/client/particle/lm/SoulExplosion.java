package com.qiuyue.goetyominous.client.particle.lm;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * 「灵魂爆炸」—— 灵魂三叉戟扎中东西那一瞬间炸开的那团红光
 * （一阶段用青蓝色那版，三叉戟用的是红色的 {@code soul_explosion_red}）。
 *
 * <p>从传奇怪物的同名类逐字照搬，<b>只换了包名</b>。贴图在
 * {@code assets/goetyominous/textures/particle/soul_explosion_red_0..7.png}（8 帧）。
 *
 * <h2>它和 {@link GhostlySoul} 不是一路货</h2>
 * 灵魂粒继承的是原版的 {@code RisingParticle}（会飘、会淡、会缩），
 * 而这个是<b>直接继承 {@code TextureSheetParticle}</b> —— 也就是说
 * 「怎么动」一点都没白送，全靠下面 {@link #tick()} 里那几行自己写。
 *
 * <p>但它其实<b>根本不动</b>：{@code tick()} 里只是把坐标原样存回 {@code xo/yo/zo}
 * （下一帧的插值起点），然后推进动画帧。所以它就是个<b>原地播放的贴图动画</b> ——
 * 12 tick 播完 8 帧就消失。爆炸要的就是这种「一闪而过」。
 *
 * <table border="1">
 *   <caption>构造器里那几个旋钮</caption>
 *   <tr><th>代码</th><th>作用</th></tr>
 *   <tr><td>{@code this.lifetime = 12}</td><td>活 12 tick（0.6 秒）</td></tr>
 *   <tr><td>{@code this.quadSize = 2.0F}</td><td>尺寸 2 倍 —— 爆炸得够大才看得见</td></tr>
 *   <tr><td>{@code this.setSpriteFromAge(sprites)}</td>
 *       <td>按「活了几 tick」挑贴图帧，8 帧贴图铺在 12 tick 上，这就是动画来源</td></tr>
 * </table>
 *
 * <p>⚠️ {@code this.rCol / gCol / bCol = 1.0F} 那三行是<b>拆开写的</b>，
 * 而上面算出来的 {@code float f} 压根没被用到 —— 这是<b>原版的死代码</b>，
 * 照抄保留，不要顺手删（将来和原版对照时能一眼对上）。
 * 构造器的第 5 个参数 {@code pQuadSizeMultiplier} 同样没被用到。
 *
 * <p>{@link #getLightColor} 返回的 {@code 15728880} 就是 {@code 0xF000F0}
 * —— 天空光和方块光的<b>最大值</b>，也就是「永远满亮、不受环境光照影响」。
 * 爆炸特效在夜里、在洞里都得是亮的，所以必须写死。
 */
@OnlyIn(Dist.CLIENT)
public class SoulExplosion extends TextureSheetParticle {

    private final SpriteSet sprites;

    protected SoulExplosion(ClientLevel pLevel, double pX, double pY, double pZ,
                            double pQuadSizeMultiplier, SpriteSet pSprites) {
        super(pLevel, pX, pY, pZ, 0.0, 0.0, 0.0);
        this.lifetime = 12;
        // ↓ 这行算出来的 f 没被用过，是原版的死代码，照抄保留。
        float f = this.random.nextFloat() * 0.6F + 0.4F;
        this.rCol = 1.0F;
        this.gCol = 1.0F;
        this.bCol = 1.0F;
        this.quadSize = 2.0F;
        this.sprites = pSprites;
        this.setSpriteFromAge(pSprites);
    }

    /** 见类注释：写死成满亮度，夜里也是亮的。 */
    @Override
    public int getLightColor(float pPartialTick) {
        return 15728880;
    }

    /**
     * 原地推进动画帧。
     *
     * <p>注意它<b>覆写时没有调 {@code super.tick()}</b> —— 原版就是这样的。
     * 基类 {@code TextureSheetParticle.tick()} 里有物理（重力、摩擦、碰撞），
     * 而爆炸粒子不该被那些影响，所以整个方法自己写。
     *
     * <p>那三行 {@code xo = x} 是把「上一帧的位置」对齐到当前位置 ——
     * 粒子不动，所以每帧都对齐，插值算出来才不会拉出残影。
     */
    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.setSpriteFromAge(this.sprites);
        }
    }

    /** 半透明层 —— 贴图边缘是渐隐的，用不透明层会糊成黑块。 */
    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {

        private final SpriteSet sprites;

        public Provider(SpriteSet pSprites) {
            this.sprites = pSprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel,
                                       double pX, double pY, double pZ,
                                       double pXSpeed, double pYSpeed, double pZSpeed) {
            return new SoulExplosion(pLevel, pX, pY, pZ, pXSpeed, this.sprites);
        }
    }
}
