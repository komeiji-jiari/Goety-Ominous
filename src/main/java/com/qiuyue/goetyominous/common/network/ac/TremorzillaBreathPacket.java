package com.qiuyue.goetyominous.common.network.ac;

import com.qiuyue.goetyominous.common.events.ac.TremorzillaBreathHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class TremorzillaBreathPacket {

    private final int casterId;
    private final double ex, ey, ez;
    private final float progress;

    public TremorzillaBreathPacket(int casterId, double ex, double ey, double ez, float progress) {
        this.casterId = casterId;
        this.ex = ex;
        this.ey = ey;
        this.ez = ez;
        this.progress = progress;
    }

    public static void encode(TremorzillaBreathPacket msg, FriendlyByteBuf buf) {
        buf.writeVarInt(msg.casterId);
        buf.writeDouble(msg.ex);
        buf.writeDouble(msg.ey);
        buf.writeDouble(msg.ez);
        buf.writeFloat(msg.progress);
    }

    public static TremorzillaBreathPacket decode(FriendlyByteBuf buf) {
        int casterId = buf.readVarInt();
        return new TremorzillaBreathPacket(casterId, buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readFloat());
    }

    public static void handle(TremorzillaBreathPacket msg, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        if (context.getDirection().getReceptionSide().isServer()) {
            return;
        }
        context.enqueueWork(() -> TremorzillaBreathHandler.acceptClientBeam(msg.casterId, msg.ex, msg.ey, msg.ez, msg.progress));
        context.setPacketHandled(true);
    }
}
