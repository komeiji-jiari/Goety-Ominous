package com.Polarice3.Goety.init;

import com.Polarice3.Goety.client.render.ModPlayerRenderer;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.UnknownNullability;

public class ClientRendererInit {
    private ModPlayerRenderer renderer;

    private static ClientRendererInit INSTANCE;
    public static ClientRendererInit getInstance() {
        return INSTANCE;
    }

    public ClientRendererInit() {
        INSTANCE = this;
    }

    @UnknownNullability
    public ModPlayerRenderer getModPlayerRenderer() {
        return this.renderer;
    }

    @SubscribeEvent
    public void onAddLayers(EntityRenderersEvent.AddLayers event) {
        this.renderer = new ModPlayerRenderer(event.getContext());
    }
}
