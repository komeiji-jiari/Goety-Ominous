package com.qiuyue.goetyominous.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

public class WindChargeImpulseHandler {

    public static void apply(int playerId, double x, double y, double z) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || player.getId() != playerId) {
            return;
        }
        player.setDeltaMovement(player.getDeltaMovement().add(x, y, z));
    }
}
