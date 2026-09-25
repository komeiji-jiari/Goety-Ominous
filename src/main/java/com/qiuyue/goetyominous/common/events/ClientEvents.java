package com.qiuyue.goetyominous.common.events;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.render.block.PlushieBlockEntityRenderer;
import com.qiuyue.goetyominous.client.particle.ac.CandicornServantChargeParticle;
import com.qiuyue.goetyominous.client.particle.ac.ForsakenServantSpitParticle;
import com.qiuyue.goetyominous.client.particle.ac.LuxtructosaurusServantAshParticle;
import com.qiuyue.goetyominous.client.particle.ac.LuxtructosaurusServantSpitParticle;
import com.qiuyue.goetyominous.client.particle.ac.NucleeperMushroomCloudParticle;
import com.qiuyue.goetyominous.client.particle.lm.GhostlySoul;
import com.qiuyue.goetyominous.client.particle.lm.PhantomDaggerTrail;
import com.qiuyue.goetyominous.client.particle.lm.SmallGreenFlame;
import com.qiuyue.goetyominous.client.render.EmptyRenderer;
import com.qiuyue.goetyominous.client.render.curios.PlushieCurioRenderer;
import com.qiuyue.goetyominous.common.init.ModBlockEntities;
import com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry;
import com.qiuyue.goetyominous.common.init.ac.AcParticles;
import com.qiuyue.goetyominous.common.init.lm.LmParticles;
import com.qiuyue.goetyominous.common.init.mm.MmEntityRegistry;
import com.qiuyue.goetyominous.compat.mod.AlexCavesCompat;
import com.qiuyue.goetyominous.compat.mod.LegendaryMonstersCompat;
import com.qiuyue.goetyominous.compat.mod.MutantMoreCompat;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientEvents {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {

        event.registerBlockEntityRenderer(ModBlockEntities.PLUSHIE.get(), PlushieBlockEntityRenderer::new);
        PlushieCurioRenderer.register();

        if (MutantMoreCompat.isMutantMoreLoaded()) {
            event.registerEntityRenderer(MmEntityRegistry.AREA_DAMAGE.get(), EmptyRenderer::new);
        }

        if (AlexCavesCompat.isAlexCavesLoaded()) {
            event.registerEntityRenderer(AcEntityRegistry.EXTINCTION_CATALYST.get(), ItemEntityRenderer::new);
        }
    }

    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        registerLmParticles(event);

        if (!AlexCavesCompat.isAlexCavesLoaded()) {
            return;
        }
        event.registerSpecial((ParticleType<SimpleParticleType>) AcParticles.NUCLEEPER_MUSHROOM_CLOUD.get(),
                new NucleeperMushroomCloudParticle.Provider());
        event.registerSpriteSet((ParticleType<SimpleParticleType>) AcParticles.CANDICORN_CHARGE.get(),
                CandicornServantChargeParticle.Factory::new);
        event.registerSpriteSet((ParticleType<SimpleParticleType>) AcParticles.FORSAKEN_SERVANT_SPIT.get(),
                ForsakenServantSpitParticle.Factory::new);
        event.registerSpriteSet(AcParticles.LUXTRUCTOSAURUS_SERVANT_SPIT.get(),
                LuxtructosaurusServantSpitParticle.Factory::new);
        event.registerSpriteSet(AcParticles.LUXTRUCTOSAURUS_SERVANT_ASH.get(),
                LuxtructosaurusServantAshParticle.Factory::new);
    }

    /**
     * 传奇怪物（LM）那批粒子的「生产车间」登记。
     *
     * <p>注册粒子的<b>类型</b>（{@code LmParticles}，在 {@code LmCompatManager} 里）
     * 和注册粒子的<b>外观</b>（这里）是两件不同的事，很容易搞混：
     * <ul>
     *   <li>类型走 {@code DeferredRegister}，是<b>通用</b>代码（两边都跑）；</li>
     *   <li>外观（{@code ParticleProvider}）只能用<b>客户端</b>的类来写，
     *       所以必须在客户端事件里登记;</li>
     *   <li>少登记了不会崩，表现是<b>「粒子不显示」</b> —— 服务端照发报文，
     *       客户端收到却没有车间能生产它，报文被默默丢掉。
     *       这类「不报错的坏事」最难查，别漏。</li>
     * </ul>
     *
     * <p>三种粒子的登记方式各不相同，是因为它们的参数形态不同：
     * <ul>
     *   <li>{@code registerSpriteSet} —— 给「一帧一图」的普通粒子，
     *       游戏会把 json 里那张贴图表交给工厂（{@code SpriteSet}）；</li>
     *   <li>{@code registerSpecial} —— 给「自带全部参数、不需要贴图表」的粒子
     *       （比如拖尾：它自己知道要用哪张图、什么颜色）。</li>
     * </ul>
     *
     * <p>⚠️ {@code RED_SOUL_FLAME} 配的是 {@code SmallFlameProvider} 而不是
     * {@code Provider} —— 两者只差一句「缩小一半」，配错的话火苗会大一倍。
     * 依据见 {@code LmParticles.RED_SOUL_FLAME} 的注释。
     */
    private static void registerLmParticles(RegisterParticleProvidersEvent event) {
        if (!LegendaryMonstersCompat.isLegendaryMonstersLoaded()) {
            return;
        }

        event.registerSpriteSet(LmParticles.GHOSTLY_SOUL.get(), GhostlySoul.Provider::new);
        event.registerSpriteSet(LmParticles.GHOSTLY_SOUL_RED.get(), GhostlySoul.Provider::new);
        event.registerSpriteSet(LmParticles.RED_SOUL_FLAME.get(), SmallGreenFlame.SmallFlameProvider::new);
        event.registerSpecial(LmParticles.PHANTOM_DAGGER_TRAIL.get(), new PhantomDaggerTrail.OrbFactory());
    }
}
