/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package com.vivideru.masteryofmagic.network;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public class DodgeInputPacket {
    public static final Map<UUID, DodgeInput> INPUTS = new ConcurrentHashMap<UUID, DodgeInput>();
    private final boolean forward;
    private final boolean backward;
    private final boolean left;
    private final boolean right;
    private final boolean shift;

    public DodgeInputPacket(boolean forward, boolean backward, boolean left, boolean right, boolean shift) {
        this.forward = forward;
        this.backward = backward;
        this.left = left;
        this.right = right;
        this.shift = shift;
    }

    public static void encode(DodgeInputPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBoolean(packet.forward);
        buffer.writeBoolean(packet.backward);
        buffer.writeBoolean(packet.left);
        buffer.writeBoolean(packet.right);
        buffer.writeBoolean(packet.shift);
    }

    public static DodgeInputPacket decode(FriendlyByteBuf buffer) {
        return new DodgeInputPacket(buffer.readBoolean(), buffer.readBoolean(), buffer.readBoolean(), buffer.readBoolean(), buffer.readBoolean());
    }

    public static void handle(DodgeInputPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) {
                return;
            }
            INPUTS.put(player.m_20148_(), new DodgeInput(packet.forward, packet.backward, packet.left, packet.right, packet.shift));
        });
        context.setPacketHandled(true);
    }

    public record DodgeInput(boolean forward, boolean backward, boolean left, boolean right, boolean shift) {
    }
}

