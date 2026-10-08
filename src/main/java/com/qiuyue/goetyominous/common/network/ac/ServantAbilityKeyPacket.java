package com.qiuyue.goetyominous.common.network.ac;

import com.qiuyue.goetyominous.common.entities.ally.ac.CorrodentServant;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ServantAbilityKeyPacket {
    private final int entityId;

    public ServantAbilityKeyPacket(int entityId) {
        this.entityId = entityId;
    }

    public static void encode(ServantAbilityKeyPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.entityId);
    }

    public static ServantAbilityKeyPacket decode(FriendlyByteBuf buf) {
        return new ServantAbilityKeyPacket(buf.readInt());
    }

    public static void handle(ServantAbilityKeyPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                Entity entity = player.level().getEntity(msg.entityId);
                if (entity instanceof CorrodentServant corrodent && player.getUUID().equals(corrodent.getOwnerId())) {
                    corrodent.requestEmerge();
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
