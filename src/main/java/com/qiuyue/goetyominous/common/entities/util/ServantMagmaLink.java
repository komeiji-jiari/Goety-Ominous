package com.qiuyue.goetyominous.common.entities.util;

import com.github.alexmodguy.alexscaves.server.block.PrimalMagmaBlock;
import com.qiuyue.goetyominous.common.entities.ally.ac.LuxtructosaurusServant;
import com.qiuyue.goetyominous.config.MobsConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
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
        if (ANCHORS.remove(servant) != null && !PrimalMagmaBlock.isBossActive(level)) {
            AABB box = servant.getBoundingBox();
            cool(level, box.inflate(HALO_RADIUS, HALO_HEIGHT, HALO_RADIUS));
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
                        if (state == null || state.getValue(PrimalMagmaBlock.PERMANENT)) {
                            continue;
                        }
                        boolean molten = wantedMolten(level, cursor, anchors);
                        if (molten != state.getValue(PrimalMagmaBlock.ACTIVE)) {
                            level.setBlockAndUpdate(cursor.immutable(), state.setValue(PrimalMagmaBlock.ACTIVE, molten));
                        }
                    }
                }
            }
        }
    }

    private static void cool(ServerLevel level, AABB box) {
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int y = Mth.floor(box.minY); y <= Mth.floor(box.maxY); ++y) {
            for (int x = Mth.floor(box.minX); x <= Mth.floor(box.maxX); ++x) {
                for (int z = Mth.floor(box.minZ); z <= Mth.floor(box.maxZ); ++z) {
                    cursor.set(x, y, z);
                    BlockState state = magmaAt(level, cursor);
                    if (state == null || !state.getValue(PrimalMagmaBlock.ACTIVE)
                            || state.getValue(PrimalMagmaBlock.PERMANENT)) {
                        continue;
                    }
                    level.setBlockAndUpdate(cursor.immutable(), state.setValue(PrimalMagmaBlock.ACTIVE, false));
                }
            }
        }
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
