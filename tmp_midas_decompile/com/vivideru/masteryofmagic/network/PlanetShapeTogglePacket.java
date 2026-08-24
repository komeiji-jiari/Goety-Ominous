/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package com.vivideru.masteryofmagic.network;

import com.vivideru.masteryofmagic.PlanetMasteryEvents;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public record PlanetShapeTogglePacket() {
    public static void encode(PlanetShapeTogglePacket packet, FriendlyByteBuf buffer) {
    }

    public static PlanetShapeTogglePacket decode(FriendlyByteBuf buffer) {
        return new PlanetShapeTogglePacket();
    }

    public static void handle(PlanetShapeTogglePacket packet, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                PlanetMasteryEvents.toggleShape(player);
            }
        });
        context.setPacketHandled(true);
    }
}

