package com.qiuyue.goetyominous.common.entities.util;

import com.github.alexmodguy.alexscaves.server.block.PrimalMagmaBlock;
import com.qiuyue.goetyominous.common.entities.ally.ac.LuxtructosaurusServant;
import com.qiuyue.goetyominous.config.MobsConfig;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public final class ServantMagmaLink {

    private static final int HALO_RADIUS = 8;
    private static final int HALO_HEIGHT = 3;
    private static final int SWEEP_RADIUS = 10;
    private static final int SWEEP_HEIGHT = 4;
    private static final int SWEEP_INTERVAL = 8;
    private static final long GRACE_TICKS = 20L;

    private static final Map<LuxtructosaurusServant, Anchor> ANCHORS = new WeakHashMap<>();

    private ServantMagmaLink() {
    }

    public static void tick(ServerLevel level, LuxtructosaurusServant servant) {
        if (!MobsConfig.LuxtructosaurusServantPrimalMagma.get()) {
            release(level, servant);
            return;
        }
        ANCHORS.put(servant, Anchor.of(level, servant));
        if (PrimalMagmaBlock.isBossActive(level)) {
            return;
        }
        cool(level, servant.getBoundingBox().inflate(0.0D, 1.0D, 0.0D));
        if (level.getGameTime() % SWEEP_INTERVAL == 0L) {
            sweep(level);
        }
    }

    public static void release(ServerLevel level, LuxtructosaurusServant servant) {
        if (ANCHORS.remove(servant) == null) {
            return;
        }
        AABB box = servant.getBoundingBox();
        cool(level, box.inflate(HALO_RADIUS, HALO_HEIGHT, HALO_RADIUS));
    }

    public static void restoreChunk(ServerLevel level, ChunkPos chunkPos) {
        ServantMagmaData data = ServantMagmaData.get(level);
        LongSet pending = data.pendingFor(chunkPos);
        if (pending == null) {
            return;
        }
        List<BlockPos> stale = new ArrayList<>();
        for (long packed : pending) {
            BlockPos pos = BlockPos.of(packed);
            if (level.hasChunkAt(pos)) {
                stale.add(pos);
            }
        }
        for (BlockPos pos : stale) {
            data.forget(pos.asLong());
            dropMark(level, pos);
        }
        if (pending.isEmpty()) {
            data.clearPending(chunkPos);
        }
    }

    public static void clear() {
        ANCHORS.clear();
    }

    private static void sweep(ServerLevel level) {
        List<Anchor> anchors = anchors(level);
        if (anchors.isEmpty()) {
            return;
        }
        ServantMagmaData data = ServantMagmaData.get(level);
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (Anchor anchor : anchors) {
            int centerX = Mth.floor(anchor.x());
            int centerY = Mth.floor(anchor.y());
            int centerZ = Mth.floor(anchor.z());
            for (int dy = -SWEEP_HEIGHT; dy <= SWEEP_HEIGHT; ++dy) {
                for (int dx = -SWEEP_RADIUS; dx <= SWEEP_RADIUS; ++dx) {
                    for (int dz = -SWEEP_RADIUS; dz <= SWEEP_RADIUS; ++dz) {
                        cursor.set(centerX + dx, centerY + dy, centerZ + dz);
                        BlockState state = magmaAt(level, cursor);
                        if (state == null) {
                            continue;
                        }
                        BlockPos pos = cursor.immutable();
                        boolean permanent = state.getValue(PrimalMagmaBlock.PERMANENT);
                        if (permanent && !data.isMarked(pos)) {
                            continue;
                        }
                        if (wantedMolten(level, pos, anchors)) {
                            data.mark(pos);
                            if (!permanent || !state.getValue(PrimalMagmaBlock.ACTIVE)) {
                                level.setBlockAndUpdate(pos, state.setValue(PrimalMagmaBlock.PERMANENT, true)
                                        .setValue(PrimalMagmaBlock.ACTIVE, true));
                            }
                        } else {
                            data.forget(pos.asLong());
                            if (permanent || state.getValue(PrimalMagmaBlock.ACTIVE)) {
                                dropMark(level, pos);
                            }
                        }
                    }
                }
            }
        }
        coolAbandoned(level, data, anchors);
    }

    private static void coolAbandoned(ServerLevel level, ServantMagmaData data, List<Anchor> anchors) {
        List<BlockPos> abandoned = null;
        for (long packed : data.marked()) {
            BlockPos pos = BlockPos.of(packed);
            if (!level.hasChunkAt(pos) || wantedMolten(level, pos, anchors)) {
                continue;
            }
            if (abandoned == null) {
                abandoned = new ArrayList<>();
            }
            abandoned.add(pos);
        }
        if (abandoned == null) {
            return;
        }
        for (BlockPos pos : abandoned) {
            data.forget(pos.asLong());
            dropMark(level, pos);
        }
    }

    private static void cool(ServerLevel level, AABB box) {
        ServantMagmaData data = ServantMagmaData.get(level);
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int y = Mth.floor(box.minY); y <= Mth.floor(box.maxY); ++y) {
            for (int x = Mth.floor(box.minX); x <= Mth.floor(box.maxX); ++x) {
                for (int z = Mth.floor(box.minZ); z <= Mth.floor(box.maxZ); ++z) {
                    cursor.set(x, y, z);
                    BlockState state = magmaAt(level, cursor);
                    if (state == null) {
                        continue;
                    }
                    boolean permanent = state.getValue(PrimalMagmaBlock.PERMANENT);
                    if (permanent && !data.isMarked(cursor)) {
                        continue;
                    }
                    if (!permanent && !state.getValue(PrimalMagmaBlock.ACTIVE)) {
                        continue;
                    }
                    data.forget(cursor.asLong());
                    dropMark(level, cursor);
                }
            }
        }
    }

    private static void dropMark(ServerLevel level, BlockPos pos) {
        BlockState state = magmaAt(level, pos);
        if (state == null) {
            return;
        }
        boolean permanent = state.getValue(PrimalMagmaBlock.PERMANENT);
        boolean active = state.getValue(PrimalMagmaBlock.ACTIVE);
        if (!permanent && !active) {
            return;
        }
        if (PrimalMagmaBlock.isBossActive(level)) {
            if (permanent) {
                level.setBlockAndUpdate(pos, state.setValue(PrimalMagmaBlock.PERMANENT, false));
            }
            return;
        }
        level.setBlockAndUpdate(pos, state.setValue(PrimalMagmaBlock.PERMANENT, false)
                .setValue(PrimalMagmaBlock.ACTIVE, false));
    }

    private static BlockState magmaAt(ServerLevel level, BlockPos pos) {
        if (!level.hasChunkAt(pos)) {
            return null;
        }
        BlockState state = level.getBlockState(pos);
        return state.getBlock() instanceof PrimalMagmaBlock ? state : null;
    }

    private static boolean wantedMolten(ServerLevel level, BlockPos pos, List<Anchor> anchors) {
        long gameTime = level.getGameTime();
        boolean molten = false;
        for (Anchor anchor : anchors) {
            if (gameTime - anchor.gameTime() > GRACE_TICKS) {
                continue;
            }
            if (anchor.underfoot(pos)) {
                return false;
            }
            if (anchor.inHalo(pos)) {
                molten = true;
            }
        }
        return molten;
    }

    private static List<Anchor> anchors(ServerLevel level) {
        long gameTime = level.getGameTime();
        List<Anchor> result = new ArrayList<>();
        for (Anchor anchor : ANCHORS.values()) {
            if (anchor.dimension().equals(level.dimension()) && gameTime - anchor.gameTime() <= GRACE_TICKS) {
                result.add(anchor);
            }
        }
        return result;
    }

    private record Anchor(ResourceKey<Level> dimension, double x, double y, double z, AABB box, long gameTime) {

        static Anchor of(ServerLevel level, LuxtructosaurusServant servant) {
            return new Anchor(level.dimension(), servant.getX(), servant.getY(), servant.getZ(),
                    servant.getBoundingBox(), level.getGameTime());
        }

        boolean inHalo(BlockPos pos) {
            double dx = (double) pos.getX() + 0.5D - this.x;
            double dz = (double) pos.getZ() + 0.5D - this.z;
            return dx * dx + dz * dz <= (double) (HALO_RADIUS * HALO_RADIUS)
                    && Math.abs((double) pos.getY() + 0.5D - this.y) <= (double) HALO_HEIGHT;
        }

        boolean underfoot(BlockPos pos) {
            return pos.getX() >= Mth.floor(this.box.minX) && pos.getX() <= Mth.floor(this.box.maxX)
                    && pos.getY() >= Mth.floor(this.box.minY - 1.0D) && pos.getY() <= Mth.floor(this.box.maxY)
                    && pos.getZ() >= Mth.floor(this.box.minZ) && pos.getZ() <= Mth.floor(this.box.maxZ);
        }
    }
}
