/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.fml.DistExecutor
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package com.vivideru.masteryofmagic.network;

import com.vivideru.masteryofmagic.client.midas.MidasNarrationOverlay;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

public record MidasNarrationPacket(String translationKey) {
    public static void encode(MidasNarrationPacket packet, FriendlyByteBuf buffer) {
        buffer.m_130072_(packet.translationKey, 160);
    }

    public static MidasNarrationPacket decode(FriendlyByteBuf buffer) {
        return new MidasNarrationPacket(buffer.m_130136_(160));
    }

    public static void handle(MidasNarrationPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn((Dist)Dist.CLIENT, () -> () -> MidasNarrationOverlay.show(packet.translationKey)));
        context.setPacketHandled(true);
    }
}

