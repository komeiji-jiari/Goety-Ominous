package com.qiuyue.goetyominous.common.network;

import com.qiuyue.goetyominous.client.WindChargeImpulseHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class WindChargeImpulsePacket {

    private final int playerId;
    private final double x;
    private final double y;
    private final double z;

    public WindChargeImpulsePacket(int playerId, double x, double y, double z) {
        this.playerId = playerId;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static void send(ServerPlayer player, Vec3 impulse) {
        ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                new WindChargeImpulsePacket(player.getId(), impulse.x, impulse.y, impulse.z));
    }

    public static void encode(WindChargeImpulsePacket msg, FriendlyByteBuf buf) {
        buf.writeVarInt(msg.playerId);
        buf.writeDouble(msg.x);
        buf.writeDouble(msg.y);
        buf.writeDouble(msg.z);
    }

    public static WindChargeImpulsePacket decode(FriendlyByteBuf buf) {
        return new WindChargeImpulsePacket(buf.readVarInt(), buf.readDouble(), buf.readDouble(), buf.readDouble());
    }

    public static void handle(WindChargeImpulsePacket msg, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        if (context.getDirection().getReceptionSide().isServer()) {
            return;
        }
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> WindChargeImpulseHandler.apply(msg.playerId, msg.x, msg.y, msg.z)));
        context.setPacketHandled(true);
    }
}
