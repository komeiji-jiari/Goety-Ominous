package com.qiuyue.goetyominous.common.explore.network;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.explore.ExpeditionManager;
import com.qiuyue.goetyominous.common.explore.ExpeditionViewSessions;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class ExpeditionClaimPacket {
    private final UUID expeditionId;

    public ExpeditionClaimPacket(UUID expeditionId) {
        this.expeditionId = expeditionId;
    }

    public static void encode(ExpeditionClaimPacket packet, FriendlyByteBuf buffer) {
        buffer.writeUUID(packet.expeditionId);
    }

    public static ExpeditionClaimPacket decode(FriendlyByteBuf buffer) {
        return new ExpeditionClaimPacket(buffer.readUUID());
    }

    public static void handle(ExpeditionClaimPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player == null) {
                return;
            }
            Component failure = null;
            try {
                failure = ExpeditionManager.claimExploration(player, packet.expeditionId);
            } catch (RuntimeException | StackOverflowError broken) {
                GoetyOminous.LOGGER.error("[goetyominous] Expedition claim failed.", broken);
                failure = Component.translatable("message.goetyominous.expedition.operationFailed");
            } finally {
                if (failure != null) {
                    player.displayClientMessage(failure, true);
                }
                try {
                    ExpeditionViewSessions.refresh(player, failure);
                } catch (RuntimeException refreshFailed) {
                    GoetyOminous.LOGGER.warn("[goetyominous] Failed to refresh expedition screen after claim.");
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}
