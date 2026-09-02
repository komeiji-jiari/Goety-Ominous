package com.qiuyue.goetyominous.common.events;

import com.Polarice3.Goety.init.ModKeybindings;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.network.CCroneRobePacket;
import com.qiuyue.goetyominous.common.network.CExtractPotionPacket;
import com.qiuyue.goetyominous.common.network.ModNetwork;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class CroneKeyBindings {

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        if (Minecraft.getInstance().screen == null) {
            while (ModKeybindings.keyBindings[3].consumeClick()) {
                ModNetwork.CHANNEL.sendToServer(new CCroneRobePacket());
            }
            while (ModKeybindings.keyBindings[7].consumeClick()) {
                ModNetwork.CHANNEL.sendToServer(new CExtractPotionPacket());
            }
        }
    }
}
