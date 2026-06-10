package com.qiuyue.someillagerservants.client;

import com.qiuyue.someillagerservants.common.entities.ally.mobs.mm.MutantHoglinServant;
import com.qiuyue.someillagerservants.common.network.ModNetwork;
import com.qiuyue.someillagerservants.common.network.RiderChargePacket;
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
            if (player != null && player.getVehicle() instanceof MutantHoglinServant) {
                ModNetwork.CHANNEL.sendToServer(
                        new RiderChargePacket(player.getVehicle().getId()));
            }
        }
    }
}