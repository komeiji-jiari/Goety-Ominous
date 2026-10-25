package com.qiuyue.goetyominous.common.explore.network;

import com.qiuyue.goetyominous.common.explore.ExpeditionViewSessions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ExpeditionViewPacket(boolean active) {
    public static void encode(ExpeditionViewPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBoolean(packet.active());
    }

    public static ExpeditionViewPacket decode(FriendlyByteBuf buffer) {
        return new ExpeditionViewPacket(buffer.readBoolean());
    }

    public static void handle(ExpeditionViewPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player != null) {
                ExpeditionViewSessions.heartbeat(player, packet.active());
            }
        });
        context.get().setPacketHandled(true);
    }
}
