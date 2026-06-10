package com.qiuyue.someillagerservants.common.events;

import com.qiuyue.someillagerservants.SomeIllagerServants;
import com.qiuyue.someillagerservants.client.render.EmptyRenderer;
import com.qiuyue.someillagerservants.common.init.mm.MmEntityRegistry;
import com.qiuyue.someillagerservants.compat.mod.MutantMoreCompat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SomeIllagerServants.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientEvents {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {

        if (MutantMoreCompat.isMutantMoreLoaded()) {
            event.registerEntityRenderer(MmEntityRegistry.AREA_DAMAGE.get(), EmptyRenderer::new);
        }
    }
}