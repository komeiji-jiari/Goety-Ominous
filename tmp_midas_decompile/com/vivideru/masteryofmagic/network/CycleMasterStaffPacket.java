/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package com.vivideru.masteryofmagic.network;

import com.vivideru.masteryofmagic.MasterStaffHelper;
import com.vivideru.masteryofmagic.capability.MasterStaffItemHandler;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

public record CycleMasterStaffPacket(InteractionHand hand) {
    public static void encode(CycleMasterStaffPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBoolean(packet.hand == InteractionHand.MAIN_HAND);
    }

    public static CycleMasterStaffPacket decode(FriendlyByteBuf buffer) {
        return new CycleMasterStaffPacket(buffer.readBoolean() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
    }

    public static void handle(CycleMasterStaffPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) {
                return;
            }
            ItemStack stack = player.m_21120_(packet.hand);
            MasterStaffItemHandler handler = MasterStaffHelper.getHandler(stack);
            if (handler == null || !handler.cycleSelectedWand()) {
                return;
            }
            MasterStaffHelper.playSwitchSound((Player)player);
            player.m_150109_().m_6596_();
            player.f_36095_.m_38946_();
            player.f_36096_.m_38946_();
        });
        context.setPacketHandled(true);
    }
}

