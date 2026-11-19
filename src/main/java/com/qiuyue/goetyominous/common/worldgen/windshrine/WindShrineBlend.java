package com.qiuyue.goetyominous.common.worldgen.windshrine;

import com.qiuyue.goetyominous.common.mixin.BlenderAccessor;
import com.qiuyue.goetyominous.common.worldgen.crypt.CryptTerrain;
import com.qiuyue.goetyominous.config.WorldGenConfig;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class WindShrineBlend {
    private static final ThreadLocal<int[]> SUPPRESSED = ThreadLocal.withInitial(() -> new int[1]);
    private static final int SEARCH_MARGIN_CHUNKS = 2;
    private static final int AREA_CACHE_LIMIT = 1024;
    private static final ConcurrentHashMap<CacheKey, Optional<Area>> AREA_CACHES = new ConcurrentHashMap<>();
    private static volatile MinecraftServer areaCacheOwner;
    private static final double FILL_GRADIENT = 10.0D / 128.0D;
    private static final int FILL_TOP_MARGIN = 8;
    private static final double[] NO_FILL = new double[0];
    private static final int LAND_TOP_MARGIN = 2;
    private static final int SOIL_DEPTH = 3;

    public interface Blended {
        void goety$setShrineBlend(Area area);
    }

    private WindShrineBlend() {
    }

    public static void suppress() {
        SUPPRESSED.get()[0]++;
    }

    public static void resume() {
        SUPPRESSED.get()[0]--;
    }

    public static Blender blender(Blender original, RandomState randomState, int minX, int minZ, int size) {
        Area area = area(randomState, minX, minZ, size);
        if (area == null) {
            return original;
        }
        Blender blender = original == Blender.empty() ? BlenderAccessor.goety$create(new Long2ObjectOpenHashMap<>(), new Long2ObjectOpenHashMap<>()) : original;
        ((Blended) blender).goety$setShrineBlend(area);
        return blender;
    }

    public static void beginCarving(RandomState randomState, ChunkPos chunkPos) {
        Area area = area(randomState, chunkPos.getMinBlockX(), chunkPos.getMinBlockZ(), 16);
        CARVING.set(area == null ? null : new LandGrid(area, chunkPos));
    }

    public static void endCarving() {
        CARVING.remove();
    }

    public static boolean protects(int x, int y, int z) {
        LandGrid grid = CARVING.get();
        return grid != null && grid.protects(x, y, z);
    }

    private static final ThreadLocal<LandGrid> CARVING = new ThreadLocal<>();

    public static void settle(ChunkAccess chunk, RandomState randomState) {
        ChunkPos chunkPos = chunk.getPos();
        Area area = area(randomState, chunkPos.getMinBlockX(), chunkPos.getMinBlockZ(), 16);
        if (area == null) {
            return;
        }
        LandGrid grid = new LandGrid(area, chunkPos);
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        boolean changed = false;
        for (int x = chunkPos.getMinBlockX(); x <= chunkPos.getMaxBlockX(); x++) {
            for (int z = chunkPos.getMinBlockZ(); z <= chunkPos.getMaxBlockZ(); z++) {
                double ground = grid.ground(x, z);
                double top = grid.top(x, z);
                if (Double.isNaN(ground) || Double.isNaN(top) || top <= ground + 0.5D) {
                    continue;
                }
                int highest = Math.min(chunk.getMaxBuildHeight() - 1, Mth.floor(top) + FILL_TOP_MARGIN);
                int lowest = Math.max(chunk.getMinBuildHeight(), Mth.floor(ground) + 1);
                int cover = Integer.MIN_VALUE;
                boolean soil = false;
                for (int y = highest; y >= lowest; y--) {
                    BlockState state = chunk.getBlockState(cursor.set(x, y, z));
                    boolean open = state.isAir() || state.getFluidState().is(FluidTags.WATER) && state.getFluidState().isSource();
                    if (!open) {
                        if (isLand(state)) {
                            cover = y;
                            soil = state.is(BlockTags.DIRT);
                        }
                        continue;
                    }
                    if (cover != Integer.MIN_VALUE) {
                        chunk.setBlockState(cursor, soil && cover - y <= SOIL_DEPTH ? Blocks.DIRT.defaultBlockState() : Blocks.STONE.defaultBlockState(), false);
                        changed = true;
                    }
                }
            }
        }
        if (changed) {
            CryptTerrain.refreshHeightmaps(chunk);
        }
    }

    private static boolean isLand(BlockState state) {
        return state.blocksMotion() && !state.ignitedByLava() && !state.is(BlockTags.LEAVES) && !state.is(BlockTags.LOGS);
    }

    static final class LandGrid {
        private static final int CELL = 4;
        private final int minX;
        private final int minZ;
        private final double[][] ground = new double[5][5];
        private final double[][] top = new double[5][5];

        LandGrid(Area area, ChunkPos chunkPos) {
            this.minX = chunkPos.getMinBlockX();
            this.minZ = chunkPos.getMinBlockZ();
            for (int i = 0; i <= 4; i++) {
                for (int j = 0; j <= 4; j++) {
                    double[] column = area.column(this.minX + i * CELL, this.minZ + j * CELL);
                    this.ground[i][j] = column.length == 0 ? Double.NaN : column[0];
                    this.top[i][j] = column.length == 0 ? Double.NaN : column[1];
                }
            }
        }

        boolean protects(int x, int y, int z) {
            double ground = this.ground(x, z);
            double top = this.top(x, z);
            return !Double.isNaN(ground) && !Double.isNaN(top) && top > ground + 0.5D && y >= ground && y <= top + LAND_TOP_MARGIN;
        }

        double ground(int x, int z) {
            return this.at(this.ground, x, z);
        }

        double top(int x, int z) {
            return this.at(this.top, x, z);
        }

        private double at(double[][] values, int x, int z) {
            int i = Math.floorDiv(x - this.minX, CELL);
            int j = Math.floorDiv(z - this.minZ, CELL);
            if (i < 0 || j < 0 || i >= 4 || j >= 4) {
                return Double.NaN;
            }
            return lerp2(values, i, j, (x - this.minX - i * CELL) / (double) CELL, (z - this.minZ - j * CELL) / (double) CELL);
        }

        private static double lerp2(double[][] values, int i, int j, double tx, double tz) {
            double low = values[i][j] + (values[i + 1][j] - values[i][j]) * tx;
            double high = values[i][j + 1] + (values[i + 1][j + 1] - values[i][j + 1]) * tx;
            return low + (high - low) * tz;
        }
    }

    @Nullable
    private static Area area(RandomState randomState, int minX, int minZ, int size) {
        if (SUPPRESSED.get()[0] > 0 || WorldGenConfig.classicWindShrine() || onRiverPlanningThread()) {
            return null;
        }
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return null;
        }
        int chunkX = Math.floorDiv(minX, 16);
        int chunkZ = Math.floorDiv(minZ, 16);
        int chunkMinX = chunkX * 16;
        int chunkMinZ = chunkZ * 16;
        if (minX < chunkMinX || minZ < chunkMinZ || minX + size > chunkMinX + 16 || minZ + size > chunkMinZ + 16) {
            return areaAt(randomState, server, minX, minZ, minX + size - 1, minZ + size - 1);
        }
        return cachedArea(randomState, server, ChunkPos.asLong(chunkX, chunkZ), chunkMinX, chunkMinZ).orElse(null);
    }

    private static Optional<Area> cachedArea(RandomState randomState, MinecraftServer server, long chunk, int minX, int minZ) {
        if (areaCacheOwner != server) {
            synchronized (AREA_CACHES) {
                if (areaCacheOwner != server) {
                    AREA_CACHES.clear();
                    areaCacheOwner = server;
                }
            }
        }
        CacheKey key = new CacheKey(randomState, chunk);
        Optional<Area> cached = AREA_CACHES.get(key);
        if (cached != null) {
            return cached;
        }
        Optional<Area> area = Optional.ofNullable(areaAt(randomState, server, minX, minZ, minX + 15, minZ + 15));
        if (AREA_CACHES.size() >= AREA_CACHE_LIMIT) {
            AREA_CACHES.clear();
        }
        Optional<Area> stored = AREA_CACHES.putIfAbsent(key, area);
        return stored != null ? stored : area;
    }

    private record CacheKey(RandomState randomState, long chunk) {
        @Override
        public boolean equals(Object other) {
            return other instanceof CacheKey key && key.randomState == this.randomState && key.chunk == this.chunk;
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(this.randomState) * 31 + Long.hashCode(this.chunk);
        }
    }

    @Nullable
    private static Area areaAt(RandomState randomState, MinecraftServer server, int minX, int minZ, int maxX, int maxZ) {
        ServerLevel level = null;
        for (ServerLevel candidate : server.getAllLevels()) {
            if (candidate.getChunkSource().randomState() == randomState) {
                level = candidate;
                break;
            }
        }
        if (level == null) {
            return null;
        }
        ChunkGeneratorStructureState state = level.getChunkSource().getGeneratorState();
        if (!WindShrinePlans.possibleIn(state)) {
            return null;
        }
        Optional<WindShrineStructure> shrine = WindShrinePlans.shrine(server.registryAccess());
        if (shrine.isEmpty()) {
            return null;
        }
        ChunkGenerator generator = level.getChunkSource().getGenerator();
        WindShrinePlans.Resolver resolver = new WindShrinePlans.Resolver(server.registryAccess(), generator, randomState, server.getStructureManager(), level.getSeed(), level);
        int searchChunks = Math.floorDiv(shrine.get().maxDistanceFromCenter + shrine.get().slopeRadius + 160 + WindShrineTerrain.FOOT_BLEND_MAX, 16) + SEARCH_MARGIN_CHUNKS;
        ChunkPos around = new ChunkPos(Math.floorDiv(minX + maxX, 32), Math.floorDiv(minZ + maxZ, 32));
        List<WindShrinePlans.Plan> plans = new ArrayList<>();
        for (ChunkPos candidate : WindShrinePlans.candidates(resolver.registryAccess(), resolver.seed(), around, searchChunks, state)) {
            Optional<WindShrinePlans.Plan> plan = WindShrinePlans.plan(resolver, candidate);
            if (plan.isPresent() && plan.get().touches(minX, minZ, maxX, maxZ, WindShrineTerrain.footReach(plan.get()))) {
                plans.add(plan.get());
            }
        }
        return plans.isEmpty() ? null : new Area(plans, generator, level, randomState);
    }

    private static boolean onRiverPlanningThread() {
        String thread = Thread.currentThread().getName();
        return thread.startsWith("streamsreflowing-") || thread.startsWith("sr-chunkfill");
    }

    public static final class Area {
        private final List<WindShrinePlans.Plan> plans;
        private final ChunkGenerator generator;
        private final LevelHeightAccessor heights;
        private final RandomState randomState;
        private final Long2ObjectOpenHashMap<double[]> columns = new Long2ObjectOpenHashMap<>();

        Area(List<WindShrinePlans.Plan> plans, ChunkGenerator generator, LevelHeightAccessor heights, RandomState randomState) {
            this.plans = plans;
            this.generator = generator;
            this.heights = heights;
            this.randomState = randomState;
        }

        public double fill(int x, int y, int z, double density) {
            double[] column = this.column(x, z);
            if (column.length == 0 || column[1] <= column[0] + 0.5D || y <= column[0] || y > column[1] + FILL_TOP_MARGIN) {
                return density;
            }
            double depth = column[1] - y;
            return Math.max(density, depth * FILL_GRADIENT * (depth > 0.0D ? 4.0D : 1.0D));
        }

        synchronized double[] column(int x, int z) {
            long key = ChunkPos.asLong(x, z);
            double[] column = this.columns.get(key);
            if (column != null) {
                return column;
            }
            column = NO_FILL;
            int ground = Integer.MIN_VALUE;
            double top = Double.NEGATIVE_INFINITY;
            for (WindShrinePlans.Plan plan : this.plans) {
                double[] foot = WindShrineTerrain.foot(plan, x, z);
                if (foot == null) {
                    continue;
                }
                if (ground == Integer.MIN_VALUE) {
                    suppress();
                    try {
                        ground = this.generator.getBaseHeight(x, z, Heightmap.Types.OCEAN_FLOOR_WG, this.heights, this.randomState) - 1;
                    } finally {
                        resume();
                    }
                }
                top = Math.max(top, ground + foot[0] * Math.max(0.0D, foot[1] - ground));
            }
            if (ground != Integer.MIN_VALUE) {
                column = new double[]{ground, Math.max(ground, top)};
            }
            this.columns.put(key, column);
            return column;
        }
    }
}
