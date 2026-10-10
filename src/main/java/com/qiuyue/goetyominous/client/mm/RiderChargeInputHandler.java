package com.qiuyue.goetyominous.client.mm;

import com.qiuyue.goetyominous.compat.mod.MutantMoreCompat;
import com.qiuyue.goetyominous.common.network.ModNetwork;
import com.qiuyue.goetyominous.common.network.mm.RiderChargePacket;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.qiuyue.goetyominous.client.ModKeyBindings;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class RiderChargeInputHandler {

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        var player = net.minecraft.client.Minecraft.getInstance().player;
        if (player == null || player.getVehicle() == null) {
            return;
        }
        if (!MutantMoreCompat.isMutantMoreLoaded()) {
            return;
        }
        if (!player.getVehicle().getClass().getName().equals(
                "com.qiuyue.goetyominous.common.entities.ally.mobs.mm.MutantHoglinServant")) {
            return;
        }
        if (ModKeyBindings.RIDER_CHARGE_KEY.consumeClick()) {
            ModNetwork.CHANNEL.sendToServer(
                    new RiderChargePacket(player.getVehicle().getId()));
        }
    }
}