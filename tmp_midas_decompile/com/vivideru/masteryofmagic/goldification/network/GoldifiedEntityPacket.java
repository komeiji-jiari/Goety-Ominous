/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.fml.DistExecutor
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package com.vivideru.masteryofmagic.goldification.network;

import com.vivideru.masteryofmagic.goldification.client.GoldificationClientState;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

public record GoldifiedEntityPacket(int entityId, boolean goldified) {
    public static void encode(GoldifiedEntityPacket packet, FriendlyByteBuf buffer) {
        buffer.m_130130_(packet.entityId);
        buffer.writeBoolean(packet.goldified);
    }

    public static GoldifiedEntityPacket decode(FriendlyByteBuf buffer) {
        return new GoldifiedEntityPacket(buffer.m_130242_(), buffer.readBoolean());
    }

    public static void handle(GoldifiedEntityPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn((Dist)Dist.CLIENT, () -> () -> GoldificationClientState.handleEntity(packet.entityId, packet.goldified)));
        context.setPacketHandled(true);
    }
}

