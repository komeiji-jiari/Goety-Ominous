/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongArrayList
 *  it.unimi.dsi.fastutil.longs.LongCollection
 *  it.unimi.dsi.fastutil.longs.LongIterator
 *  javax.annotation.Nullable
 *  net.minecraft.core.BlockPos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.saveddata.SavedData
 */
package com.vivideru.masteryofmagic.goldification;

import com.vivideru.masteryofmagic.goldification.GoldifiedBlockEntry;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import java.util.Comparator;
import java.util.PriorityQueue;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.SavedData;

public final class GoldificationSavedData
extends SavedData {
    private static final String DATA_NAME = "goety_mastery_of_magic_goldification";
    private static final String BLOCKS = "Blocks";
    private static final String POSITION = "Position";
    private final Long2ObjectOpenHashMap<GoldifiedBlockEntry> blocks = new Long2ObjectOpenHashMap();
    private final PriorityQueue<Expiry> expiries = new PriorityQueue<Expiry>(Comparator.comparingLong(Expiry::expireGameTime));

    public static GoldificationSavedData get(ServerLevel level) {
        return (GoldificationSavedData)level.m_8895_().m_164861_(GoldificationSavedData::load, GoldificationSavedData::new, DATA_NAME);
    }

    public static GoldificationSavedData load(CompoundTag root) {
        GoldificationSavedData data = new GoldificationSavedData();
        ListTag list = root.m_128437_(BLOCKS, 10);
        for (int i = 0; i < list.size(); ++i) {
            CompoundTag entryTag = list.m_128728_(i);
            long position = entryTag.m_128454_(POSITION);
            GoldifiedBlockEntry entry = GoldifiedBlockEntry.load(entryTag);
            data.blocks.put(position, (Object)entry);
            data.expiries.add(new Expiry(position, entry.expireGameTime()));
        }
        return data;
    }

    public CompoundTag m_7176_(CompoundTag root) {
        ListTag list = new ListTag();
        for (Long2ObjectMap.Entry mapEntry : this.blocks.long2ObjectEntrySet()) {
            CompoundTag entryTag = ((GoldifiedBlockEntry)mapEntry.getValue()).save();
            entryTag.m_128356_(POSITION, mapEntry.getLongKey());
            list.add((Object)entryTag);
        }
        root.m_128365_(BLOCKS, (Tag)list);
        return root;
    }

    @Nullable
    public GoldifiedBlockEntry get(long position) {
        return (GoldifiedBlockEntry)this.blocks.get(position);
    }

    public void put(long position, GoldifiedBlockEntry entry) {
        this.blocks.put(position, (Object)entry);
        this.expiries.add(new Expiry(position, entry.expireGameTime()));
        this.m_77762_();
    }

    @Nullable
    public GoldifiedBlockEntry remove(long position) {
        GoldifiedBlockEntry removed = (GoldifiedBlockEntry)this.blocks.remove(position);
        if (removed != null) {
            this.m_77762_();
        }
        return removed;
    }

    public LongArrayList removeExpired(long gameTime) {
        LongArrayList removed = new LongArrayList();
        while (!this.expiries.isEmpty() && this.expiries.peek().expireGameTime <= gameTime) {
            Expiry expiry = this.expiries.poll();
            GoldifiedBlockEntry current = (GoldifiedBlockEntry)this.blocks.get(expiry.position);
            if (current == null || current.expireGameTime() != expiry.expireGameTime) continue;
            this.blocks.remove(expiry.position);
            removed.add(expiry.position);
        }
        if (!removed.isEmpty()) {
            this.m_77762_();
        }
        return removed;
    }

    public LongArrayList getValidPositionsInChunk(ChunkPos chunk, long gameTime) {
        LongArrayList positions = new LongArrayList();
        for (Long2ObjectMap.Entry mapEntry : this.blocks.long2ObjectEntrySet()) {
            long packed = mapEntry.getLongKey();
            if (((GoldifiedBlockEntry)mapEntry.getValue()).expireGameTime() <= gameTime || BlockPos.m_121983_((long)packed) >> 4 != chunk.f_45578_ || BlockPos.m_122015_((long)packed) >> 4 != chunk.f_45579_) continue;
            positions.add(packed);
        }
        return positions;
    }

    public LongArrayList getValidPositionsNear(double x, double z, double radius, long gameTime) {
        double radiusSquared = radius * radius;
        LongArrayList positions = new LongArrayList();
        for (Long2ObjectMap.Entry mapEntry : this.blocks.long2ObjectEntrySet()) {
            double dz;
            double dx;
            long packed = mapEntry.getLongKey();
            if (((GoldifiedBlockEntry)mapEntry.getValue()).expireGameTime() <= gameTime || !((dx = (double)BlockPos.m_121983_((long)packed) + 0.5 - x) * dx + (dz = (double)BlockPos.m_122015_((long)packed) + 0.5 - z) * dz <= radiusSquared)) continue;
            positions.add(packed);
        }
        return positions;
    }

    public LongArrayList clearWithin(double x, double y, double z, double radius) {
        double radiusSquared = radius * radius;
        LongArrayList removed = new LongArrayList();
        LongIterator iterator = this.blocks.keySet().iterator();
        while (iterator.hasNext()) {
            double dz;
            double dy;
            long packed = iterator.nextLong();
            double dx = (double)BlockPos.m_121983_((long)packed) + 0.5 - x;
            if (!(dx * dx + (dy = (double)BlockPos.m_122008_((long)packed) + 0.5 - y) * dy + (dz = (double)BlockPos.m_122015_((long)packed) + 0.5 - z) * dz <= radiusSquared)) continue;
            iterator.remove();
            removed.add(packed);
        }
        if (!removed.isEmpty()) {
            this.m_77762_();
        }
        return removed;
    }

    public LongArrayList clearAll() {
        LongArrayList removed = new LongArrayList((LongCollection)this.blocks.keySet());
        if (!removed.isEmpty()) {
            this.blocks.clear();
            this.expiries.clear();
            this.m_77762_();
        }
        return removed;
    }

    private record Expiry(long position, long expireGameTime) {
    }
}

