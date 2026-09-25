package com.qiuyue.goetyominous.client.particle.lm;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.RisingParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * 「灵魂粒」—— 灵魂冲击弹幕身上抖出来的那种青蓝色鬼火
 * （二阶段换成红色版，用的是同一个类）。
 *
 * <p>从传奇怪物的同名类逐字照搬，<b>只换了包名</b>。贴图在
 * {@code assets/goetyominous/textures/particle/ghostly_soul_0..11.png}（12 帧循环）。
 *
 * <h2>它为什么这么短</h2>
 * 因为绝大部分行为都白送了 —— 它继承的是原版的
 * {@link RisingParticle}（「上升粒子」），那个类已经把灵魂粒该有的样子全写好了：
 * <b>往上飘、越来越慢、越来越淡、慢慢缩小</b>。这里只需要调整几个旋钮：
 *
 * <table border="1">
 *   <caption>构造器里那三行都干了什么</caption>
 *   <tr><th>代码</th><th>官方名</th><th>作用</th></tr>
 *   <tr><td>{@code this.setScale(2.0F)}</td><td>{@code scale}</td><td>放大到 2 倍（默认太小）</td></tr>
 *   <tr><td>{@code this.setSpriteFromAge(sprites)}</td><td>{@code setSpriteFromAge}</td>
 *       <td>按「活了几 tick」挑贴图帧 —— 就是那 12 张图循环播放的来源</td></tr>
 *   <tr><td>{@code this.setAlpha(1.0F)}</td><td>{@code setAlpha}</td>
 *       <td>不透明。放在 Provider 里，因为 RisingParticle 默认是半透明的</td></tr>
 * </table>
 *
 * <h2>两个 Provider 的区别</h2>
 * <ul>
 *   <li>{@link Provider} —— 普通版，原版 {@code ghostly_soul} / {@code ghostly_soul_red}
 *       用的就是这个；</li>
 *   <li>{@link EmissiveProvider} —— 多打开了自发光（{@code isGlowing}），
 *       让粒子在暗处也亮。原版注册表里暂时没给它派活，我们照抄保留，
 *       将来想让它发光时直接换一个 provider 就行。</li>
 * </ul>
 *
 * <p><b>注意这个类的构造函数是包私有的</b>（没有 {@code public}）——
 * 原版就是为了让外部只能通过下面两个 Provider 来创建它。我们照搬，所以
 * Provider 也必须在同一个包里。
 */
@OnlyIn(Dist.CLIENT)
public class GhostlySoul extends RisingParticle {

    private final SpriteSet sprites;

    /** 见类注释「两个 Provider 的区别」。 */
    protected boolean isGlowing;

    GhostlySoul(ClientLevel pLevel, double pX, double pY, double pZ,
                double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet pSprites) {
        super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
        this.sprites = pSprites;
        this.scale(2.0F);
        this.setSpriteFromAge(pSprites);
    }

    /** 自发光的粒子亮度顶到 240（最大值），普通粒子走基类的正常亮度。 */
    @Override
    public int getLightColor(float pPartialTick) {
        return this.isGlowing ? 240 : super.getLightColor(pPartialTick);
    }

    /**
     * 半透明渲染层。
     *
     * <p>基类 {@code RisingParticle} 默认用的是「不透明」层，
     * 那样粒子边缘的透明部分会糊成黑块。灵魂粒必须是半透明的才好看。
     */
    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    /** 每 tick 重新按寿命挑一帧贴图 —— 动画就是这么动起来的。 */
    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.sprites);
    }

    @OnlyIn(Dist.CLIENT)
    public static class EmissiveProvider implements ParticleProvider<SimpleParticleType> {

        private final SpriteSet sprite;

        public EmissiveProvider(SpriteSet pSprite) {
            this.sprite = pSprite;
        }

        @Override
        public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel,
                                       double pX, double pY, double pZ,
                                       double pXSpeed, double pYSpeed, double pZSpeed) {
            GhostlySoul soulparticle = new GhostlySoul(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, this.sprite);
            soulparticle.setAlpha(1.0F);
            soulparticle.isGlowing = true;
            return soulparticle;
        }
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
            GhostlySoul soulparticle = new GhostlySoul(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, this.sprite);
            soulparticle.setAlpha(1.0F);
            return soulparticle;
        }
    }
}
