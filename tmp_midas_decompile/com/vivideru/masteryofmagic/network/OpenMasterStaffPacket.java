/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.InteractionHand
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package com.vivideru.masteryofmagic.network;

import com.vivideru.masteryofmagic.MasterStaffHelper;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.network.NetworkEvent;

public record OpenMasterStaffPacket(InteractionHand hand) {
    public static void encode(OpenMasterStaffPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBoolean(packet.hand == InteractionHand.MAIN_HAND);
    }

    public static OpenMasterStaffPacket decode(FriendlyByteBuf buffer) {
        return new OpenMasterStaffPacket(buffer.readBoolean() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
    }

    public static void handle(OpenMasterStaffPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                MasterStaffHelper.openMenu(player, packet.hand);
            }
        });
        context.setPacketHandled(true);
    }
}

