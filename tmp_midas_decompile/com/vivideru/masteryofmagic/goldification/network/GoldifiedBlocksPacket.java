/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.fml.DistExecutor
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package com.vivideru.masteryofmagic.goldification.network;

import com.vivideru.masteryofmagic.goldification.client.GoldificationClientState;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

public final class GoldifiedBlocksPacket {
    private final ResourceLocation dimension;
    private final Action action;
    private final long[] positions;
    private final long chunkPosition;

    public GoldifiedBlocksPacket(ResourceLocation dimension, Action action, long[] positions, long chunkPosition) {
        this.dimension = dimension;
        this.action = action;
        this.positions = positions;
        this.chunkPosition = chunkPosition;
    }

    public static GoldifiedBlocksPacket changes(ResourceLocation dimension, boolean goldified, long[] positions) {
        return new GoldifiedBlocksPacket(dimension, goldified ? Action.ADD : Action.REMOVE, positions, 0L);
    }

    public static GoldifiedBlocksPacket clearDimension(ResourceLocation dimension) {
        return new GoldifiedBlocksPacket(dimension, Action.CLEAR_DIMENSION, new long[0], 0L);
    }

    public static GoldifiedBlocksPacket clearChunk(ResourceLocation dimension, long chunkPosition) {
        return new GoldifiedBlocksPacket(dimension, Action.CLEAR_CHUNK, new long[0], chunkPosition);
    }

    public static void encode(GoldifiedBlocksPacket packet, FriendlyByteBuf buffer) {
        buffer.m_130085_(packet.dimension);
        buffer.m_130068_((Enum)packet.action);
        buffer.writeLong(packet.chunkPosition);
        buffer.m_130130_(packet.positions.length);
        for (long position : packet.positions) {
            buffer.writeLong(position);
        }
    }

    public static GoldifiedBlocksPacket decode(FriendlyByteBuf buffer) {
        ResourceLocation dimension = buffer.m_130281_();
        Action action = (Action)buffer.m_130066_(Action.class);
        long chunkPosition = buffer.readLong();
        int size = buffer.m_130242_();
        if (size < 0 || size > 4096) {
            throw new IllegalArgumentException("Invalid goldification block batch size: " + size);
        }
        long[] positions = new long[size];
        for (int i = 0; i < size; ++i) {
            positions[i] = buffer.readLong();
        }
        return new GoldifiedBlocksPacket(dimension, action, positions, chunkPosition);
    }

    public static void handle(GoldifiedBlocksPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn((Dist)Dist.CLIENT, () -> () -> GoldificationClientState.handleBlocks(packet.dimension, packet.action, packet.positions, packet.chunkPosition)));
        context.setPacketHandled(true);
    }

    public static enum Action {
        ADD,
        REMOVE,
        CLEAR_DIMENSION,
        CLEAR_CHUNK;

    }
}

