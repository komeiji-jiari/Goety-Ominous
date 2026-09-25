package com.qiuyue.goetyominous.common.init.lm;

import com.mojang.serialization.Codec;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.particle.lm.Circle;
import com.qiuyue.goetyominous.client.particle.lm.PhantomDaggerTrail;
import com.qiuyue.goetyominous.client.particle.lm.SoulExplosion;
import com.qiuyue.goetyominous.client.particle.lm.SoulPillarExplosion;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * 传奇怪物（LM）粒子在本项目里的注册表。
 *
 * <p>只有圣骑仆从用到的那几个 —— <b>不是</b>把 LM 的 {@code ModParticles}（60 多个）
 * 整个抄过来。用不到的粒子搬过来只会白白占注册名。
 *
 * <p>命名空间是 {@code goetyominous}，但<b>注册名和 LM 一模一样</b>
 * （和 LM 的注册名逐字相同）—— 这是刻意的：贴图、json、粒子行为全部照搬原作，
 * 只有「属于哪个模组」变了。注册名保持一致，将来对照原版代码时不容易搞混。
 *
 * <h2>为什么这里是 {@code ParticleType<?>} 的 DeferredRegister</h2>
 * 和 {@code AcParticles} 同一套路，见那边的注释。要点是：
 * <ul>
 *   <li>无参粒子用 {@code SimpleParticleType}，它自带序列化器，什么都不用写；</li>
 *   <li>带参数的粒子（这里的幻影匕首拖尾 {@link PhantomDaggerTrail.OrbData}）
 *       必须自己写一个 {@code ParticleType} 匿名子类，实现 {@code codec()}
 *       —— 也就是「这个粒子的参数怎么编解码」。原版没有提供现成的。</li>
 * </ul>
 *
 * <p>注意 {@code SimpleParticleType} 的构造参数是
 * <b>{@code overrideLimiter}</b>（「是否无视粒子的距离限制」）。
 * 这四个原版全是 {@code true}，照抄。
 *
 * @see com.qiuyue.goetyominous.client.particle.lm.GhostlySoul     灵魂粒
 * @see com.qiuyue.goetyominous.client.particle.lm.SmallGreenFlame 红灵魂火
 * @see com.qiuyue.goetyominous.client.particle.lm.PhantomDaggerTrail 幻影匕首拖尾
 */
public class LmParticles {

    private static final DeferredRegister<ParticleType<?>> LM_PARTICLES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, GoetyOminous.MOD_ID);

    /** 灵魂冲击用的「灵魂粒」（一阶段青蓝色）。贴图 12 帧循环。 */
    public static final RegistryObject<SimpleParticleType> GHOSTLY_SOUL =
            LM_PARTICLES.register("ghostly_soul", () -> new SimpleParticleType(true));

    /** 灵魂冲击二阶段用的「灵魂粒」（红色）。贴图同样是 12 帧。 */
    public static final RegistryObject<SimpleParticleType> GHOSTLY_SOUL_RED =
            LM_PARTICLES.register("ghostly_soul_red", () -> new SimpleParticleType(true));

    /**
     * 二阶段的「红灵魂火」。
     *
     * <p>⚠️ 它的实现类在项目里叫 {@code SmallGreenFlame} —— 这名字看着很怪
     * （红的火为什么叫绿火焰），但<b>原版就是这样</b>：作者是先写了绿色的「歼灭之火」
     * （{@code small_green_flame}），后来做红色灵魂火时直接复用了同一个类，
     * 只换了贴图。我们照搬，所以名字保留，免得将来和原版对照时对不上号。
     */
    public static final RegistryObject<SimpleParticleType> RED_SOUL_FLAME =
            LM_PARTICLES.register("red_soul_flame", () -> new SimpleParticleType(true));

    /**
     * 灵魂三叉戟扎中东西那一瞬间炸开的红光。
     *
     * <p>贴图是 8 帧的 {@code soul_explosion_red_0..7}，粒子本体 12 tick 就消失。
     * 它和上面几个灵魂粒<b>不是同一个类</b>，见 {@link SoulExplosion} 的注释。
     */
    public static final RegistryObject<SimpleParticleType> SOUL_EXPLOSION_RED =
            LM_PARTICLES.register("soul_explosion_red", () -> new SimpleParticleType(true));

    /**
     * 灵魂柱冒出地面之前，地面上先亮起的那团红光。
     *
     * <p>7 帧贴图。⚠️ 和下面那个 {@code soul_pillar_explosion} 是两个不同的粒子，
     * 名字像、类也几乎一样，别搞混 —— 一个是「地上的光」，一个是「炸开的光」。
     */
    public static final RegistryObject<SimpleParticleType> GROUNDSOUL_RED =
            LM_PARTICLES.register("ground_soul_red", () -> new SimpleParticleType(true));

    /** 灵魂柱炸出地面那一瞬间的光。8 帧贴图。见 {@link SoulPillarExplosion}。 */
    public static final RegistryObject<SimpleParticleType> SOUL_PILLAR_EXPLOSION =
            LM_PARTICLES.register("soul_pillar_explosion", () -> new SimpleParticleType(true));

    /**
     * 幻影匕首的「拖尾」粒子 —— 这一条是<b>带参数的</b>粒子。
     *
     * <p>参数是 {@link PhantomDaggerTrail.OrbData}：颜色（RGB）、轨道宽高、以及
     * <b>要跟随哪个实体</b>（一颗整数实体 ID）。粒子自己做不出「跟着匕首转」这件事，
     * 是靠每 tick 用这个 ID 去世界里把实体捞出来、读它的坐标算出来的。
     *
     * <p>下面那个匿名子类只做一件事：告诉游戏「用 {@code OrbData.CODEC} 来编解码参数」。
     * 原版的 {@code RING}（光圈）、{@code LIGHTNING} 等带参粒子也是同一写法。
     */
    public static final RegistryObject<ParticleType<PhantomDaggerTrail.OrbData>> PHANTOM_DAGGER_TRAIL =
            LM_PARTICLES.register("phantom_dagger_trail",
                    () -> new ParticleType<PhantomDaggerTrail.OrbData>(false,
                            PhantomDaggerTrail.OrbData.DESERIALIZER) {
                        @Override
                        public Codec<PhantomDaggerTrail.OrbData> codec() {
                            return PhantomDaggerTrail.OrbData.CODEC(
                                    LmParticles.PHANTOM_DAGGER_TRAIL.get());
                        }
                    });

    /**
     * 灵魂柱爆炸时从地上炸开的<b>光圈</b> —— 也是带参数的粒子。
     *
     * <p>它的参数包 {@link Circle.RingData} 有 10 个数（朝向、颜色、大小、时长、
     * 是否正对镜头、半径变化方式），所以和上面那个一样，需要一个匿名子类
     * 告诉游戏「用 {@code RingData.CODEC} 编解码」。
     *
     * <p>⚠️ 在传奇怪物那边这个注册项的名字叫 {@code RING}（类名 {@code Circle}），
     * 我们<b>类名照搬、字段名改成 {@code CIRCLE}</b> —— 因为字段名跟着
     * 「贴图 json 叫 circle.json、注册名是 {@code circle}」走更不容易搞混。
     * 注册名两边逐字相同，都是 {@code circle}。
     */
    public static final RegistryObject<ParticleType<Circle.RingData>> CIRCLE =
            LM_PARTICLES.register("circle",
                    () -> new ParticleType<Circle.RingData>(false,
                            Circle.RingData.DESERIALIZER) {
                        @Override
                        public Codec<Circle.RingData> codec() {
                            return Circle.RingData.CODEC(
                                    LmParticles.CIRCLE.get());
                        }
                    });

    public static void register(IEventBus modEventBus) {
        LM_PARTICLES.register(modEventBus);
    }
}
