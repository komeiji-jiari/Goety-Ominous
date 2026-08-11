package com.qiuyue.goetyominous.common.network;

import com.qiuyue.goetyominous.common.entities.ally.am.IllagerElephantServant;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ElephantChargePacket {
    private final int entityId;

    public ElephantChargePacket(int entityId) {
        this.entityId = entityId;
    }

    public static void encode(ElephantChargePacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.entityId);
    }

    public static ElephantChargePacket decode(FriendlyByteBuf buf) {
        return new ElephantChargePacket(buf.readInt());
    }

    public static void handle(ElephantChargePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                Entity vehicle = player.getVehicle();
                if (vehicle instanceof IllagerElephantServant elephant && vehicle.getId() == msg.entityId) {
                    elephant.triggerCharge();
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
