package com.qiuyue.goetyominous.common.network.ac;

import com.qiuyue.goetyominous.client.ac.BarbaricAxeSwingClient;
import com.qiuyue.goetyominous.common.network.ModNetwork;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class CBarbaricAxeSwingPacket {

    public static final int TYPE_SWEEP = 0;
    public static final int TYPE_SLAM = 1;

    private final int entityId;
    private final int type;

    public CBarbaricAxeSwingPacket(int entityId, int type) {
        this.entityId = entityId;
        this.type = type;
    }

    public static void send(Player player, int type) {
        ModNetwork.CHANNEL.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player),
                new CBarbaricAxeSwingPacket(player.getId(), type));
    }

    public static void sendToTrackers(Player player, int type) {
        ModNetwork.CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> player),
                new CBarbaricAxeSwingPacket(player.getId(), type));
    }

    public static void encode(CBarbaricAxeSwingPacket msg, FriendlyByteBuf buf) {
        buf.writeVarInt(msg.entityId);
        buf.writeVarInt(msg.type);
    }

    public static CBarbaricAxeSwingPacket decode(FriendlyByteBuf buf) {
        return new CBarbaricAxeSwingPacket(buf.readVarInt(), buf.readVarInt());
    }

    public static void handle(CBarbaricAxeSwingPacket msg, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        if (context.getDirection().getReceptionSide().isServer()) {
            return;
        }
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> BarbaricAxeSwingClient.start(msg.entityId, msg.type)));
        context.setPacketHandled(true);
    }
}
