package com.qiuyue.goetyominous.common.explore.network;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.common.explore.ExpeditionKind;
import com.qiuyue.goetyominous.common.explore.ExpeditionManager;
import com.qiuyue.goetyominous.common.explore.ExpeditionViewSessions;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class ExpeditionStartPacket {
    private final ExpeditionKind kind;
    private final ResourceLocation dimension;
    private final ResourceLocation biome;
    private final ResourceLocation structure;
    private final InteractionHand hand;
    private final List<UUID> travelers;

    public ExpeditionStartPacket(ExpeditionKind kind, ResourceLocation dimension, ResourceLocation biome,
                                 ResourceLocation structure, InteractionHand hand, List<UUID> travelers) {
        this.kind = kind;
        this.dimension = dimension;
        this.biome = biome;
        this.structure = structure;
        this.hand = hand;
        this.travelers = List.copyOf(travelers);
    }

    public static void encode(ExpeditionStartPacket packet, FriendlyByteBuf buffer) {
        buffer.writeEnum(packet.kind);
        buffer.writeResourceLocation(packet.dimension);
        ExpeditionPacketCodec.writeOptionalId(buffer, packet.biome);
        ExpeditionPacketCodec.writeOptionalId(buffer, packet.structure);
        buffer.writeEnum(packet.hand);
        buffer.writeInt(packet.travelers.size());
        for (UUID id : packet.travelers) {
            buffer.writeUUID(id);
        }
    }

    public static ExpeditionStartPacket decode(FriendlyByteBuf buffer) {
        ExpeditionKind kind = buffer.readEnum(ExpeditionKind.class);
        ResourceLocation dimension = buffer.readResourceLocation();
        ResourceLocation biome = ExpeditionPacketCodec.readOptionalId(buffer);
        ResourceLocation structure = ExpeditionPacketCodec.readOptionalId(buffer);
        InteractionHand hand = buffer.readEnum(InteractionHand.class);
        int count = buffer.readInt();
        if (count < 0 || count > 64) {
            throw new IllegalArgumentException("Invalid traveler count: " + count);
        }
        List<UUID> travelers = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            travelers.add(buffer.readUUID());
        }
        return new ExpeditionStartPacket(kind, dimension, biome, structure, hand, travelers);
    }

    public static void handle(ExpeditionStartPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player == null) {
                return;
            }
            Component failure = null;
            try {
                ResourceKey<Level> dimension = ResourceKey.create(Registries.DIMENSION, packet.dimension);
                failure = ExpeditionManager.startExploration(player, packet.kind, dimension,
                        packet.biome, packet.structure, packet.travelers, packet.hand);
            } catch (RuntimeException | StackOverflowError broken) {
                GoetyOminous.LOGGER.error("[goetyominous] Expedition start failed.", broken);
                failure = Component.translatable("message.goetyominous.expedition.operationFailed");
            } finally {
                if (failure != null) {
                    player.displayClientMessage(failure, true);
                }
                try {
                    ExpeditionViewSessions.refresh(player, failure);
                } catch (RuntimeException refreshFailed) {
                    GoetyOminous.LOGGER.warn("[goetyominous] Failed to refresh expedition screen after start.");
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}
