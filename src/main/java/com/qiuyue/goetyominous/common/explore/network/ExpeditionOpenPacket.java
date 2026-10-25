package com.qiuyue.goetyominous.common.explore.network;

import com.qiuyue.goetyominous.client.gui.explore.ExpeditionCompassScreen;
import com.qiuyue.goetyominous.common.explore.ExpeditionSnapshot;
import com.qiuyue.goetyominous.common.explore.ExpeditionTuning;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ExpeditionOpenPacket {
    private final ExpeditionSnapshot snapshot;

    public ExpeditionOpenPacket(ExpeditionSnapshot snapshot) {
        this.snapshot = snapshot;
    }

    public ExpeditionSnapshot snapshot() {
        return snapshot;
    }

    public static void encode(ExpeditionOpenPacket packet, FriendlyByteBuf buffer) {
        ExpeditionSnapshot snapshot = packet.snapshot;
        buffer.writeResourceLocation(snapshot.dimension());
        ExpeditionPacketCodec.writeOptionalId(buffer, snapshot.playerBiome());
        buffer.writeEnum(snapshot.hand());
        ExpeditionPacketCodec.writeTuning(buffer, snapshot.tuning());
        buffer.writeBoolean(snapshot.authoritative());
        buffer.writeBoolean(snapshot.oresAvailable());
        buffer.writeBoolean(snapshot.mobsAvailable());
        buffer.writeComponent(snapshot.feedback());
        ExpeditionPacketCodec.writeDimensions(buffer, snapshot.dimensions());
        ExpeditionPacketCodec.writeBiomes(buffer, snapshot.biomes());
        ExpeditionPacketCodec.writeRuins(buffer, snapshot.ruins());
        ExpeditionPacketCodec.writeTravelers(buffer, snapshot.travelers());
        ExpeditionPacketCodec.writeExpeditions(buffer, snapshot.expeditions());
    }

    public static ExpeditionOpenPacket decode(FriendlyByteBuf buffer) {
        ResourceLocation dimension = buffer.readResourceLocation();
        ResourceLocation playerBiome = ExpeditionPacketCodec.readOptionalId(buffer);
        InteractionHand hand = buffer.readEnum(InteractionHand.class);
        ExpeditionTuning tuning = ExpeditionPacketCodec.readTuning(buffer);
        boolean authoritative = buffer.readBoolean();
        boolean oresAvailable = buffer.readBoolean();
        boolean mobsAvailable = buffer.readBoolean();
        Component feedback = buffer.readComponent();
        if (feedback == null) {
            feedback = Component.empty();
        }
        return new ExpeditionOpenPacket(new ExpeditionSnapshot(
                dimension,
                playerBiome,
                hand,
                tuning,
                authoritative,
                oresAvailable,
                mobsAvailable,
                ExpeditionPacketCodec.readDimensions(buffer),
                ExpeditionPacketCodec.readBiomes(buffer),
                ExpeditionPacketCodec.readRuins(buffer),
                ExpeditionPacketCodec.readTravelers(buffer),
                ExpeditionPacketCodec.readExpeditions(buffer),
                feedback
        ));
    }

    public static void handle(ExpeditionOpenPacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> ExpeditionCompassScreen.open(packet.snapshot())));
        context.get().setPacketHandled(true);
    }
}
