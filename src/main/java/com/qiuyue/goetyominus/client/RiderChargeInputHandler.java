package com.qiuyue.goetyominus.client;

import com.qiuyue.goetyominus.compat.mod.MutantMoreCompat;
import com.qiuyue.goetyominus.common.network.ModNetwork;
import com.qiuyue.goetyominus.common.network.RiderChargePacket;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class RiderChargeInputHandler {

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        if (ModKeyBindings.RIDER_CHARGE_KEY.consumeClick()) {
            var player = net.minecraft.client.Minecraft.getInstance().player;
            if (player != null && player.getVehicle() != null
                    && MutantMoreCompat.isMutantMoreLoaded()
                    && player.getVehicle().getClass().getName().equals(
                    "com.qiuyue.goetyominus.common.entities.ally.mobs.mm.MutantHoglinServant")) {
                ModNetwork.CHANNEL.sendToServer(
                        new RiderChargePacket(player.getVehicle().getId()));
            }
        }
    }
}