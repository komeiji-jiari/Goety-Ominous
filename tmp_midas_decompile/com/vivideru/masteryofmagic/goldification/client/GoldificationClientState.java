/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntIterator
 *  it.unimi.dsi.fastutil.ints.IntOpenHashSet
 *  it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongCollection
 *  it.unimi.dsi.fastutil.longs.LongIterator
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  javax.annotation.Nullable
 *  net.minecraft.client.Minecraft
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.SectionPos
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.ChunkPos
 */
package com.vivideru.masteryofmagic.goldification.client;

import com.vivideru.masteryofmagic.TimeFreezeRenderAnimationState;
import com.vivideru.masteryofmagic.goldification.network.GoldifiedBlocksPacket;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.HashSet;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;

public final class GoldificationClientState {
    private static final Object LOCK = new Object();
    private static final LongOpenHashSet BLOCKS = new LongOpenHashSet();
    private static final Long2LongOpenHashMap RECENTLY_REMOVED_BLOCKS = new Long2LongOpenHashMap();
    private static final IntOpenHashSet ENTITIES = new IntOpenHashSet();
    @Nullable
    private static ResourceLocation dimension;

    private GoldificationClientState() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void handleBlocks(ResourceLocation packetDimension, GoldifiedBlocksPacket.Action action, long[] positions, long chunkPosition) {
        Minecraft minecraft = Minecraft.m_91087_();
        if (minecraft.f_91073_ == null || !minecraft.f_91073_.m_46472_().m_135782_().equals((Object)packetDimension)) {
            return;
        }
        LongOpenHashSet changed = new LongOpenHashSet();
        Object object = LOCK;
        synchronized (object) {
            GoldificationClientState.ensureDimension(packetDimension, changed);
            switch (action) {
                case ADD: {
                    for (long position : positions) {
                        RECENTLY_REMOVED_BLOCKS.remove(position);
                        if (!BLOCKS.add(position)) continue;
                        changed.add(position);
                    }
                    break;
                }
                case REMOVE: {
                    long soundGraceExpiry = minecraft.f_91073_.m_46467_() + 5L;
                    for (long position : positions) {
                        if (!BLOCKS.remove(position)) continue;
                        changed.add(position);
                        RECENTLY_REMOVED_BLOCKS.put(position, soundGraceExpiry);
                    }
                    break;
                }
                case CLEAR_DIMENSION: {
                    changed.addAll((LongCollection)BLOCKS);
                    BLOCKS.clear();
                    RECENTLY_REMOVED_BLOCKS.clear();
                    GoldificationClientState.clearEntityAnimations();
                    ENTITIES.clear();
                    break;
                }
                case CLEAR_CHUNK: {
                    ChunkPos chunk = new ChunkPos(chunkPosition);
                    LongIterator iterator = BLOCKS.iterator();
                    while (iterator.hasNext()) {
                        long packed = iterator.nextLong();
                        if (BlockPos.m_121983_((long)packed) >> 4 != chunk.f_45578_ || BlockPos.m_122015_((long)packed) >> 4 != chunk.f_45579_) continue;
                        iterator.remove();
                        changed.add(packed);
                    }
                    break;
                }
            }
        }
        GoldificationClientState.invalidateSections(changed);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void handleEntity(int entityId, boolean goldified) {
        Minecraft minecraft = Minecraft.m_91087_();
        ResourceLocation current = minecraft.f_91073_ == null ? null : minecraft.f_91073_.m_46472_().m_135782_();
        Object object = LOCK;
        synchronized (object) {
            if (current == null) {
                return;
            }
            if (!current.equals((Object)dimension)) {
                BLOCKS.clear();
                GoldificationClientState.clearEntityAnimations();
                ENTITIES.clear();
                dimension = current;
            }
            if (goldified) {
                ENTITIES.add(entityId);
            } else {
                ENTITIES.remove(entityId);
            }
            TimeFreezeRenderAnimationState.setAnimationFrozen(entityId, goldified);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean isBlockGoldified(BlockPos position) {
        Object object = LOCK;
        synchronized (object) {
            return BLOCKS.contains(position.m_121878_());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean isBlockGoldifiedForSound(BlockPos position) {
        Object object = LOCK;
        synchronized (object) {
            long packed = position.m_121878_();
            if (BLOCKS.contains(packed)) {
                return true;
            }
            Minecraft minecraft = Minecraft.m_91087_();
            if (minecraft.f_91073_ == null) {
                return false;
            }
            long expiry = RECENTLY_REMOVED_BLOCKS.getOrDefault(packed, Long.MIN_VALUE);
            if (expiry >= minecraft.f_91073_.m_46467_()) {
                return true;
            }
            RECENTLY_REMOVED_BLOCKS.remove(packed);
            return false;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean isEntityGoldified(int entityId) {
        Object object = LOCK;
        synchronized (object) {
            return ENTITIES.contains(entityId);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void resetForCurrentLevel() {
        Minecraft minecraft = Minecraft.m_91087_();
        ResourceLocation current = minecraft.f_91073_ == null ? null : minecraft.f_91073_.m_46472_().m_135782_();
        LongOpenHashSet changed = new LongOpenHashSet();
        Object object = LOCK;
        synchronized (object) {
            if (minecraft.f_91073_ != null) {
                long gameTime = minecraft.f_91073_.m_46467_();
                RECENTLY_REMOVED_BLOCKS.long2LongEntrySet().removeIf(entry -> entry.getLongValue() < gameTime);
            }
            if (current == null || !current.equals((Object)dimension)) {
                changed.addAll((LongCollection)BLOCKS);
                BLOCKS.clear();
                RECENTLY_REMOVED_BLOCKS.clear();
                GoldificationClientState.clearEntityAnimations();
                ENTITIES.clear();
                dimension = current;
            }
        }
        GoldificationClientState.invalidateSections(changed);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void clear() {
        LongOpenHashSet changed = new LongOpenHashSet();
        Object object = LOCK;
        synchronized (object) {
            changed.addAll((LongCollection)BLOCKS);
            BLOCKS.clear();
            RECENTLY_REMOVED_BLOCKS.clear();
            GoldificationClientState.clearEntityAnimations();
            ENTITIES.clear();
            dimension = null;
        }
        GoldificationClientState.invalidateSections(changed);
    }

    private static void ensureDimension(ResourceLocation packetDimension, LongOpenHashSet changed) {
        if (!packetDimension.equals((Object)dimension)) {
            changed.addAll((LongCollection)BLOCKS);
            BLOCKS.clear();
            RECENTLY_REMOVED_BLOCKS.clear();
            GoldificationClientState.clearEntityAnimations();
            ENTITIES.clear();
            dimension = packetDimension;
        }
    }

    private static void clearEntityAnimations() {
        IntIterator intIterator = ENTITIES.iterator();
        while (intIterator.hasNext()) {
            int entityId = (Integer)intIterator.next();
            TimeFreezeRenderAnimationState.setAnimationFrozen(entityId, false);
        }
    }

    private static void invalidateSections(LongOpenHashSet positions) {
        Minecraft minecraft = Minecraft.m_91087_();
        if (minecraft.f_91073_ == null || positions.isEmpty()) {
            return;
        }
        HashSet<SectionPos> sections = new HashSet<SectionPos>();
        LongIterator longIterator = positions.iterator();
        while (longIterator.hasNext()) {
            long packed = (Long)longIterator.next();
            sections.add(SectionPos.m_123199_((BlockPos)BlockPos.m_122022_((long)packed)));
        }
        for (SectionPos section : sections) {
            minecraft.f_91060_.m_109770_(section.m_123170_(), section.m_123206_(), section.m_123222_());
        }
    }
}

