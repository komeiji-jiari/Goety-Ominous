package com.qiuyue.goetyominous.client;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.client.render.DarkSceneRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID, value = Dist.CLIENT)
public class DarkSceneInputHandler {

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        if (!ModKeyBindings.DARK_SCENE_KEY.consumeClick()) {
            return;
        }
        DarkSceneRenderer.toggle();
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            player.displayClientMessage(Component.translatable(DarkSceneRenderer.active()
                    ? "message.goetyominous.dark_scene.on"
                    : "message.goetyominous.dark_scene.off"), true);
        }
    }
}
