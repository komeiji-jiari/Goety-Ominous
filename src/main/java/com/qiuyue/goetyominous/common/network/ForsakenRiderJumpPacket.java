package com.qiuyue.goetyominous.common.network;

import com.qiuyue.goetyominous.common.entities.ally.ac.ForsakenServant;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ForsakenRiderJumpPacket {
    private final int entityId;
    private final boolean startCharge;
    private final float power;

    public ForsakenRiderJumpPacket(int entityId, boolean startCharge, float power) {
        this.entityId = entityId;
        this.startCharge = startCharge;
        this.power = power;
    }

    public static void encode(ForsakenRiderJumpPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.entityId);
        buf.writeBoolean(msg.startCharge);
        buf.writeFloat(msg.power);
    }

    public static ForsakenRiderJumpPacket decode(FriendlyByteBuf buf) {
        return new ForsakenRiderJumpPacket(buf.readInt(), buf.readBoolean(), buf.readFloat());
    }

    public static void handle(ForsakenRiderJumpPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                Entity vehicle = player.getVehicle();
                if (vehicle instanceof ForsakenServant forsaken && vehicle.getId() == msg.entityId
                        && forsaken.getControllingPassenger() == player) {
                    if (msg.startCharge) {
                        forsaken.serverStartRiderCharge();
                    } else {
                        forsaken.serverReleaseRiderCharge(msg.power);
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
