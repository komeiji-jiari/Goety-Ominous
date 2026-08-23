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

/**
 * S2C:把核能苦力怕仆从爆炸的"保护 zone"同步给爆炸附近的客户端。
 *
 * NuclearExplosionEntity.tick() 的伤害/击退/辐照循环不受 isClientSide 门控,服务端和客户端
 * 都会对爆炸范围内的生物直接 addEffect(IRRADIATED, 48000, ...) 和 setDeltaMovement(...)。
 * 辐照与原版 Alex's Caves 一致,对所有人(敌人/友军/主人)生效,服务端正常同步,客户端无需干预;
 * 但爆炸直接伤害与冲击波击退会掀飞本地玩家(位置由客户端主导)和友军,而服务端对 zone 内
 * 友方的伤害拦截/速度中和救不了本地客户端。
 *
 * 此包把 zone(位置+半径+游戏刻过期+ownerIds)同步到客户端,让客户端的 onLivingTick
 * 同样中和冲击波击退。zone 与服务端 protectOwnerAndServants 登记的完全一致,客户端按
 * level.getGameTime() 过期(与服务端同步)。
 */
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
