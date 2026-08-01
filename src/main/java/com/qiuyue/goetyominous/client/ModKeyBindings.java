package com.qiuyue.goetyominous.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModKeyBindings {
    public static final KeyMapping RIDER_CHARGE_KEY = new KeyMapping(
            "key.goetyominous.rider_charge",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_K,
            "key.categories.goetyominous"
    );

    public static final KeyMapping OPEN_FUNGUS_PACK_KEY = new KeyMapping(
            "key.goetyominous.open_fungus_pack",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_KP_0,
            "key.categories.goetyominous"
    );

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(RIDER_CHARGE_KEY);
        event.register(OPEN_FUNGUS_PACK_KEY);
    }
}
