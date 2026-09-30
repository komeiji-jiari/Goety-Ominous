package com.qiuyue.goetyominous.common.network;

import com.qiuyue.goetyominous.common.events.XRayHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class XRayPacket {

    private final int casterId;
    private final double tx, ty, tz;
    private final boolean gamma;

    public XRayPacket(int casterId, Vec3 to, boolean gamma) {
        this.casterId = casterId;
        this.tx = to.x;
        this.ty = to.y;
        this.tz = to.z;
        this.gamma = gamma;
    }

    public static void encode(XRayPacket msg, FriendlyByteBuf buf) {
        buf.writeVarInt(msg.casterId);
        buf.writeDouble(msg.tx);
        buf.writeDouble(msg.ty);
        buf.writeDouble(msg.tz);
        buf.writeBoolean(msg.gamma);
    }

    public static XRayPacket decode(FriendlyByteBuf buf) {
        int casterId = buf.readVarInt();
        Vec3 to = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        return new XRayPacket(casterId, to, buf.readBoolean());
    }

    public static void handle(XRayPacket msg, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        if (context.getDirection().getReceptionSide().isServer()) {
            return;
        }
        context.enqueueWork(() -> XRayHandler.acceptClientRay(
                msg.casterId, new Vec3(msg.tx, msg.ty, msg.tz), msg.gamma));
        context.setPacketHandled(true);
    }
}
