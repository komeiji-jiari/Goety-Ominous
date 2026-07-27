package com.qiuyue.goetyominus.common.events;

import com.qiuyue.goetyominus.GoetyOminous;
import com.qiuyue.goetyominus.client.render.EmptyRenderer;
import com.qiuyue.goetyominus.common.init.mm.MmEntityRegistry;
import com.qiuyue.goetyominus.compat.mod.MutantMoreCompat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
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
}