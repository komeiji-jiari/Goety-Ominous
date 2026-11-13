package com.qiuyue.goetyominous.common.network;

import com.qiuyue.goetyominous.client.events.GrimMonarchClientEvents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class GrimMonarchNightPacket {
    private final ResourceKey<Level> dimension;
    private final boolean locked;

    public GrimMonarchNightPacket(ResourceKey<Level> dimension, boolean locked) {
        this.dimension = dimension;
        this.locked = locked;
    }

    public static void encode(GrimMonarchNightPacket msg, FriendlyByteBuf buf) {
        buf.writeResourceKey(msg.dimension);
        buf.writeBoolean(msg.locked);
    }

    public static GrimMonarchNightPacket decode(FriendlyByteBuf buf) {
        return new GrimMonarchNightPacket(buf.readResourceKey(Registries.DIMENSION), buf.readBoolean());
    }

    public static void handle(GrimMonarchNightPacket msg, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        if (context.getDirection().getReceptionSide().isServer()) {
            return;
        }
        context.enqueueWork(() -> GrimMonarchClientEvents.setNightLocked(msg.dimension, msg.locked));
        context.setPacketHandled(true);
    }
}
