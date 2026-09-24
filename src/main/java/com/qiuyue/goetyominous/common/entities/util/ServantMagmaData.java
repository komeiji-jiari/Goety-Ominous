package com.qiuyue.goetyominous.common.entities.util;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;

public class ServantMagmaData extends SavedData {

    private static final String DATA_NAME = "goetyominous_servant_magma";
    private static final String MARKED_TAG = "Marked";

    private final LongSet marked = new LongOpenHashSet();
    private final Map<Long, LongSet> pending = new HashMap<>();

    public static ServantMagmaData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(ServantMagmaData::load, ServantMagmaData::new, DATA_NAME);
    }

    public LongSet marked() {
        return this.marked;
    }

    public boolean isMarked(BlockPos pos) {
        return this.marked.contains(pos.asLong());
    }

    public void mark(BlockPos pos) {
        long packed = pos.asLong();
        if (this.marked.add(packed)) {
            this.setDirty();
            this.index(packed);
        }
    }

    public void forget(long packed) {
        if (this.marked.remove(packed)) {
            this.setDirty();
        }
        LongSet bucket = this.pending.get(chunkKey(packed));
        if (bucket != null) {
            bucket.remove(packed);
        }
    }

    public LongSet pendingFor(ChunkPos chunkPos) {
        return this.pending.get(chunkPos.toLong());
    }

    public void clearPending(ChunkPos chunkPos) {
        this.pending.remove(chunkPos.toLong());
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putLongArray(MARKED_TAG, this.marked.toLongArray());
        return tag;
    }

    private void index(long packed) {
        this.pending.computeIfAbsent(chunkKey(packed), key -> new LongOpenHashSet()).add(packed);
    }

    private static ServantMagmaData load(CompoundTag tag) {
        ServantMagmaData data = new ServantMagmaData();
        for (long packed : tag.getLongArray(MARKED_TAG)) {
            if (data.marked.add(packed)) {
                data.index(packed);
            }
        }
        return data;
    }

    private static long chunkKey(long packed) {
        return ChunkPos.asLong(BlockPos.getX(packed) >> 4, BlockPos.getZ(packed) >> 4);
    }
}
