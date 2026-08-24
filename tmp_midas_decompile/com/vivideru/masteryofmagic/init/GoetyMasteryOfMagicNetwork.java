/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.LongCollection
 *  it.unimi.dsi.fastutil.longs.LongIterator
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.Level
 *  net.minecraftforge.network.NetworkRegistry
 *  net.minecraftforge.network.PacketDistributor
 *  net.minecraftforge.network.simple.SimpleChannel
 */
package com.vivideru.masteryofmagic.init;

import com.vivideru.masteryofmagic.TimeFreezeSyncPacket;
import com.vivideru.masteryofmagic.goldification.network.GoldifiedBlocksPacket;
import com.vivideru.masteryofmagic.goldification.network.GoldifiedEntityPacket;
import com.vivideru.masteryofmagic.network.CSpellRingCastPacket;
import com.vivideru.masteryofmagic.network.CycleMasterStaffPacket;
import com.vivideru.masteryofmagic.network.DodgeInputPacket;
import com.vivideru.masteryofmagic.network.OpenMasterStaffPacket;
import com.vivideru.masteryofmagic.network.PlanetShapeTogglePacket;
import com.vivideru.masteryofmagic.network.PlanetShapeshiftPacket;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import java.util.UUID;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class GoetyMasteryOfMagicNetwork {
    private static final String PROTOCOL_VERSION = "1";
    private static SimpleChannel channel;
    private static int packetId;
    private static boolean registered;
    private static final int GOLDIFICATION_BLOCK_BATCH_SIZE = 2048;

    public static void register() {
        if (registered) {
            return;
        }
        channel = NetworkRegistry.newSimpleChannel((ResourceLocation)new ResourceLocation("goety_mastery_of_magic", "main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
        channel.registerMessage(packetId++, TimeFreezeSyncPacket.class, TimeFreezeSyncPacket::encode, TimeFreezeSyncPacket::decode, TimeFreezeSyncPacket::handle);
        channel.registerMessage(packetId++, CSpellRingCastPacket.class, CSpellRingCastPacket::encode, CSpellRingCastPacket::decode, CSpellRingCastPacket::consume);
        channel.registerMessage(packetId++, DodgeInputPacket.class, DodgeInputPacket::encode, DodgeInputPacket::decode, DodgeInputPacket::handle);
        channel.registerMessage(packetId++, OpenMasterStaffPacket.class, OpenMasterStaffPacket::encode, OpenMasterStaffPacket::decode, OpenMasterStaffPacket::handle);
        channel.registerMessage(packetId++, CycleMasterStaffPacket.class, CycleMasterStaffPacket::encode, CycleMasterStaffPacket::decode, CycleMasterStaffPacket::handle);
        channel.registerMessage(packetId++, GoldifiedBlocksPacket.class, GoldifiedBlocksPacket::encode, GoldifiedBlocksPacket::decode, GoldifiedBlocksPacket::handle);
        channel.registerMessage(packetId++, GoldifiedEntityPacket.class, GoldifiedEntityPacket::encode, GoldifiedEntityPacket::decode, GoldifiedEntityPacket::handle);
        channel.registerMessage(packetId++, PlanetShapeshiftPacket.class, PlanetShapeshiftPacket::encode, PlanetShapeshiftPacket::decode, PlanetShapeshiftPacket::handle);
        channel.registerMessage(packetId++, PlanetShapeTogglePacket.class, PlanetShapeTogglePacket::encode, PlanetShapeTogglePacket::decode, PlanetShapeTogglePacket::handle);
        registered = true;
    }

    public static void sendToServer(Object packet) {
        if (channel == null) {
            return;
        }
        channel.sendToServer(packet);
    }

    public static void sendToPlayer(ServerPlayer player, Object packet) {
        if (channel != null) {
            channel.send(PacketDistributor.PLAYER.with(() -> player), packet);
        }
    }

    public static void sendTimeFreezeSync(ServerPlayer player, double x, double y, double z, double radius, UUID immuneEntityUuid, boolean frozen) {
        if (channel == null) {
            return;
        }
        channel.send(PacketDistributor.PLAYER.with(() -> player), (Object)new TimeFreezeSyncPacket(x, y, z, radius, immuneEntityUuid, frozen));
    }

    public static void sendTimeFreezeSync(ServerPlayer player, int entityId, boolean frozen) {
        if (channel == null) {
            return;
        }
        Entity entity = player.m_9236_().m_6815_(entityId);
        if (entity instanceof LivingEntity) {
            LivingEntity entity2 = (LivingEntity)entity;
            GoetyMasteryOfMagicNetwork.sendTimeFreezeSync(player, entity2.m_20185_(), entity2.m_20186_(), entity2.m_20189_(), 4.0, entity2.m_20148_(), frozen);
        }
    }

    public static void sendGoldifiedBlocks(ServerLevel level, LongCollection positions, boolean goldified) {
        if (channel == null || positions.isEmpty()) {
            return;
        }
        LongIterator iterator = positions.iterator();
        while (iterator.hasNext()) {
            long[] batch = GoetyMasteryOfMagicNetwork.takeBatch(iterator);
            channel.send(PacketDistributor.DIMENSION.with(() -> ((ServerLevel)level).m_46472_()), (Object)GoldifiedBlocksPacket.changes(level.m_46472_().m_135782_(), goldified, batch));
        }
    }

    public static void sendGoldifiedBlocks(ServerPlayer player, ResourceKey<Level> dimension, LongCollection positions, boolean goldified) {
        if (channel == null || positions.isEmpty()) {
            return;
        }
        LongIterator iterator = positions.iterator();
        while (iterator.hasNext()) {
            channel.send(PacketDistributor.PLAYER.with(() -> player), (Object)GoldifiedBlocksPacket.changes(dimension.m_135782_(), goldified, GoetyMasteryOfMagicNetwork.takeBatch(iterator)));
        }
    }

    public static void clearGoldificationClientState(ServerPlayer player, ResourceKey<Level> dimension) {
        if (channel != null) {
            channel.send(PacketDistributor.PLAYER.with(() -> player), (Object)GoldifiedBlocksPacket.clearDimension(dimension.m_135782_()));
        }
    }

    public static void clearGoldificationChunk(ServerPlayer player, ResourceKey<Level> dimension, ChunkPos chunk) {
        if (channel != null) {
            channel.send(PacketDistributor.PLAYER.with(() -> player), (Object)GoldifiedBlocksPacket.clearChunk(dimension.m_135782_(), chunk.m_45588_()));
        }
    }

    public static void sendGoldifiedEntity(Entity entity, boolean goldified) {
        if (channel != null) {
            channel.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), (Object)new GoldifiedEntityPacket(entity.m_19879_(), goldified));
        }
    }

    public static void sendGoldifiedEntity(ServerPlayer player, Entity entity, boolean goldified) {
        if (channel != null) {
            channel.send(PacketDistributor.PLAYER.with(() -> player), (Object)new GoldifiedEntityPacket(entity.m_19879_(), goldified));
        }
    }

    private static long[] takeBatch(LongIterator iterator) {
        long[] buffer = new long[2048];
        int size = 0;
        while (size < buffer.length && iterator.hasNext()) {
            buffer[size++] = iterator.nextLong();
        }
        if (size == buffer.length) {
            return buffer;
        }
        long[] exact = new long[size];
        System.arraycopy(buffer, 0, exact, 0, size);
        return exact;
    }

    static {
        packetId = 0;
        registered = false;
    }
}

