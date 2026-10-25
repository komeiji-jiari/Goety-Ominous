package com.qiuyue.goetyominous.common.explore.network;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.explore.ExpeditionViewSessions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ExpeditionGatherPacket() {
    public static void encode(ExpeditionGatherPacket packet, FriendlyByteBuf buffer) {
    }

    public static ExpeditionGatherPacket decode(FriendlyByteBuf buffer) {
        return new ExpeditionGatherPacket();
    }

    public static void handle(ExpeditionGatherPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player == null) {
                return;
            }
            Component feedback = null;
            try {
                feedback = ExpeditionViewSessions.gather(player);
            } catch (RuntimeException | StackOverflowError broken) {
                GoetyOminous.LOGGER.error("[goetyominous] Failed to gather travelers.", broken);
                feedback = Component.translatable("message.goetyominous.expedition.operationFailed");
            }
            if (feedback != null) {
                player.displayClientMessage(feedback, true);
            }
            try {
                ExpeditionViewSessions.refresh(player, feedback);
            } catch (RuntimeException refreshFailed) {
                GoetyOminous.LOGGER.warn("[goetyominous] Failed to refresh expedition screen after gathering.");
            }
        });
        context.get().setPacketHandled(true);
    }
}
