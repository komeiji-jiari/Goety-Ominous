package com.qiuyue.goetyominous.common.network;

import com.qiuyue.goetyominous.common.events.NucleeperNukeProtectionHandler;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

public class NucleeperExplosionZonePacket {

    private final ResourceKey<Level> dimension;
    private final double x, y, z;
    private final double hRadius, vRadius;
    private final long untilGameTime;
    private final Set<UUID> ownerIds;

    public NucleeperExplosionZonePacket(ResourceKey<Level> dimension, double x, double y, double z,
                                        double hRadius, double vRadius, long untilGameTime, Set<UUID> ownerIds) {
        this.dimension = dimension;
        this.x = x;
        this.y = y;
        this.z = z;
        this.hRadius = hRadius;
        this.vRadius = vRadius;
        this.untilGameTime = untilGameTime;
        this.ownerIds = ownerIds;
    }

    public static void encode(NucleeperExplosionZonePacket msg, FriendlyByteBuf buf) {
        buf.writeResourceKey(msg.dimension);
        buf.writeDouble(msg.x);
        buf.writeDouble(msg.y);
        buf.writeDouble(msg.z);
        buf.writeDouble(msg.hRadius);
        buf.writeDouble(msg.vRadius);
        buf.writeLong(msg.untilGameTime);
        buf.writeVarInt(msg.ownerIds.size());
        for (UUID id : msg.ownerIds) {
            buf.writeUUID(id);
        }
    }

    public static NucleeperExplosionZonePacket decode(FriendlyByteBuf buf) {
        ResourceKey<Level> dimension = buf.readResourceKey(Registries.DIMENSION);
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();
        double hRadius = buf.readDouble();
        double vRadius = buf.readDouble();
        long untilGameTime = buf.readLong();
        int n = buf.readVarInt();
        Set<UUID> ownerIds = new HashSet<>();
        for (int i = 0; i < n; i++) {
            ownerIds.add(buf.readUUID());
        }
        return new NucleeperExplosionZonePacket(dimension, x, y, z, hRadius, vRadius, untilGameTime, ownerIds);
    }

    public static void handle(NucleeperExplosionZonePacket msg, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        if (context.getDirection().getReceptionSide().isServer()) {
            return;
        }
        context.enqueueWork(() -> NucleeperNukeProtectionHandler.registerClientZone(
                msg.dimension, msg.x, msg.y, msg.z, msg.hRadius, msg.vRadius, msg.untilGameTime, msg.ownerIds));
        context.setPacketHandled(true);
    }
}
