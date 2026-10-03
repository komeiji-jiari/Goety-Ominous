package com.qiuyue.goetyominous.common.network;

import com.qiuyue.goetyominous.client.ac.NukeScreenEffects;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class NuclearExplosionEffectPacket {

    private static final double RANGE = 128.0D;

    private final double x;
    private final double y;
    private final double z;
    private final float size;

    public NuclearExplosionEffectPacket(double x, double y, double z, float size) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.size = size;
    }

    public static void send(ServerLevel level, Vec3 pos, float size) {
        ModNetwork.CHANNEL.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(
                        pos.x, pos.y, pos.z, RANGE, level.dimension())),
                new NuclearExplosionEffectPacket(pos.x, pos.y, pos.z, size));
    }

    public static void encode(NuclearExplosionEffectPacket msg, FriendlyByteBuf buf) {
        buf.writeDouble(msg.x);
        buf.writeDouble(msg.y);
        buf.writeDouble(msg.z);
        buf.writeFloat(msg.size);
    }

    public static NuclearExplosionEffectPacket decode(FriendlyByteBuf buf) {
        return new NuclearExplosionEffectPacket(buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readFloat());
    }

    public static void handle(NuclearExplosionEffectPacket msg, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        if (context.getDirection().getReceptionSide().isServer()) {
            return;
        }
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> NukeScreenEffects.play(msg.x, msg.y, msg.z, msg.size)));
        context.setPacketHandled(true);
    }
}
