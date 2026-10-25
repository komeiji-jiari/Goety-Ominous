package com.qiuyue.goetyominous.common.explore.network;

import com.qiuyue.goetyominous.common.explore.ExpeditionSnapshot;
import com.qiuyue.goetyominous.common.network.ModNetwork;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

import java.util.List;

public final class ExpeditionNetwork {
    private ExpeditionNetwork() {
    }

    public static void sendOpen(ServerPlayer player, ExpeditionSnapshot snapshot) {
        ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new ExpeditionOpenPacket(snapshot));
    }

    public static void sendProgress(ServerPlayer player, List<ExpeditionSnapshot.TravelerEntry> travelers,
                                    List<ExpeditionSnapshot.ExpeditionInfo> expeditions) {
        ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                new ExpeditionProgressPacket(travelers, expeditions));
    }
}
