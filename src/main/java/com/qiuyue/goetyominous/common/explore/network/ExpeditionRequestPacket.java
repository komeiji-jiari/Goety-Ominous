package com.qiuyue.goetyominous.common.explore.network;

import com.qiuyue.goetyominous.common.explore.ExpeditionViewSessions;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ExpeditionRequestPacket {
    private final ResourceLocation dimension;

    public ExpeditionRequestPacket(ResourceLocation dimension) {
        this.dimension = dimension;
    }

    public ResourceLocation dimension() {
        return dimension;
    }

    public static void encode(ExpeditionRequestPacket packet, FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(packet.dimension);
    }

    public static ExpeditionRequestPacket decode(FriendlyByteBuf buffer) {
        return new ExpeditionRequestPacket(buffer.readResourceLocation());
    }

    public static void handle(ExpeditionRequestPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player == null) {
                return;
            }
            ResourceKey<Level> dimension = ResourceKey.create(Registries.DIMENSION, packet.dimension);
            if (player.getServer() == null || player.getServer().getLevel(dimension) == null) {
                return;
            }
            ExpeditionViewSessions.select(player, dimension);
            ExpeditionViewSessions.refresh(player, null);
        });
        context.get().setPacketHandled(true);
    }
}
