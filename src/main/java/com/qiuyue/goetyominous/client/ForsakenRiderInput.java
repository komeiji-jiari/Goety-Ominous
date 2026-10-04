package com.qiuyue.goetyominous.client;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;

public final class ForsakenRiderInput {
    private ForsakenRiderInput() {}

    public static boolean isLocalPlayer(Player player) {
        return player instanceof LocalPlayer;
    }

    public static boolean isJumpHeld(Player player) {
        return player instanceof LocalPlayer local && local.input.jumping;
    }
}
