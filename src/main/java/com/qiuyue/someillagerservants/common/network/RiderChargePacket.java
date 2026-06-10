package com.qiuyue.someillagerservants.common.network;

import com.qiuyue.someillagerservants.common.entities.ally.mobs.mm.MutantHoglinServant;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class RiderChargePacket {
    private final int entityId;

    public RiderChargePacket(int entityId) {
        this.entityId = entityId;
    }

    public static void encode(RiderChargePacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.entityId);
    }

    public static RiderChargePacket decode(FriendlyByteBuf buf) {
        return new RiderChargePacket(buf.readInt());
    }

    public static void handle(RiderChargePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                Entity vehicle = player.getVehicle();
                if (vehicle instanceof MutantHoglinServant hoglin && vehicle.getId() == msg.entityId) {
                    hoglin.triggerRiderCharge();
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
