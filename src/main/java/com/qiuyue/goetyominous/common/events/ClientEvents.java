package com.qiuyue.goetyominous.common.events;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.particle.ac.NucleeperMushroomCloudParticle;
import com.qiuyue.goetyominous.client.render.EmptyRenderer;
import com.qiuyue.goetyominous.common.init.ac.AcParticles;
import com.qiuyue.goetyominous.common.init.mm.MmEntityRegistry;
import com.qiuyue.goetyominous.compat.mod.AlexCavesCompat;
import com.qiuyue.goetyominous.compat.mod.MutantMoreCompat;
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

        if (MutantMoreCompat.isMutantMoreLoaded()) {
            event.registerEntityRenderer(MmEntityRegistry.AREA_DAMAGE.get(), EmptyRenderer::new);
        }
    }

    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        if (!AlexCavesCompat.isAlexCavesLoaded()) {
            return;
        }
        event.registerSpecial((ParticleType<SimpleParticleType>) AcParticles.NUCLEEPER_MUSHROOM_CLOUD.get(),
                new NucleeperMushroomCloudParticle.Provider());
    }
}
