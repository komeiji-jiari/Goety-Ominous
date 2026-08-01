package com.qiuyue.goetyominous.common.network;

import com.qiuyue.goetyominous.common.items.mm.WitherScytheItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CWitherScytheStrikePacket {

    public CWitherScytheStrikePacket() {}

    public static void encode(CWitherScytheStrikePacket msg, FriendlyByteBuf buf) {}

    public static CWitherScytheStrikePacket decode(FriendlyByteBuf buf) {
        return new CWitherScytheStrikePacket();
    }

    public static void handle(CWitherScytheStrikePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                ItemStack stack = player.getMainHandItem();
                if (stack.getItem() instanceof WitherScytheItem scythe && !stack.isEmpty()) {
                    scythe.strike(player.level(), player, stack);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
