package com.qiuyue.goetyominous.common.explore.network;

import com.qiuyue.goetyominous.client.gui.explore.ExpeditionCompassScreen;
import com.qiuyue.goetyominous.common.explore.ExpeditionSnapshot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public record ExpeditionProgressPacket(
        List<ExpeditionSnapshot.TravelerEntry> travelers,
        List<ExpeditionSnapshot.ExpeditionInfo> expeditions
) {
    public ExpeditionProgressPacket {
        travelers = List.copyOf(travelers);
        expeditions = List.copyOf(expeditions);
    }

    public static void encode(ExpeditionProgressPacket packet, FriendlyByteBuf buffer) {
        ExpeditionPacketCodec.writeTravelers(buffer, packet.travelers());
        ExpeditionPacketCodec.writeExpeditions(buffer, packet.expeditions());
    }

    public static ExpeditionProgressPacket decode(FriendlyByteBuf buffer) {
        return new ExpeditionProgressPacket(
                ExpeditionPacketCodec.readTravelers(buffer),
                ExpeditionPacketCodec.readExpeditions(buffer)
        );
    }

    public static void handle(ExpeditionProgressPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> ExpeditionCompassScreen.progress(packet.travelers(), packet.expeditions())));
        context.get().setPacketHandled(true);
    }
}
