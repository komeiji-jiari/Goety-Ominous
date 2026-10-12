package com.qiuyue.goetyominous.common.network.ac;

import com.qiuyue.goetyominous.client.ac.BarbaricAxeClientEffects;
import com.qiuyue.goetyominous.common.network.ModNetwork;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class CBarbaricAxeShakePacket {

    private final float strength;
    private final int ticks;

    public CBarbaricAxeShakePacket(float strength, int ticks) {
        this.strength = strength;
        this.ticks = ticks;
    }

    public static void send(Player player, float strength, int ticks) {
        if (player instanceof ServerPlayer serverPlayer) {
            ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> serverPlayer),
                    new CBarbaricAxeShakePacket(strength, ticks));
        }
    }

    public static void encode(CBarbaricAxeShakePacket msg, FriendlyByteBuf buf) {
        buf.writeFloat(msg.strength);
        buf.writeVarInt(msg.ticks);
    }

    public static CBarbaricAxeShakePacket decode(FriendlyByteBuf buf) {
        return new CBarbaricAxeShakePacket(buf.readFloat(), buf.readVarInt());
    }

    public static void handle(CBarbaricAxeShakePacket msg, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        if (context.getDirection().getReceptionSide().isServer()) {
            return;
        }
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> BarbaricAxeClientEffects.play(msg.strength, msg.ticks)));
        context.setPacketHandled(true);
    }
}
