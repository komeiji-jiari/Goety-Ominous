package com.qiuyue.goetyominous.common.worldgen.protect;

import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.config.MobsConfig;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;

public final class StructureProtection {

    public static final TagKey<Structure> PROTECTED_STRUCTURES = TagKey.create(Registries.STRUCTURE, new ResourceLocation(GoetyOminous.MOD_ID, "worldgen_protected"));
    public static final TagKey<Block> DECORATION_ALLOWED = TagKey.create(Registries.BLOCK, new ResourceLocation(GoetyOminous.MOD_ID, "chamber_decoration_allowed"));

    private static final ThreadLocal<int[]> FEATURE_DEPTH = ThreadLocal.withInitial(() -> new int[1]);
    private static final ThreadLocal<ChunkBoxCache> CACHE = ThreadLocal.withInitial(ChunkBoxCache::new);

    public static void beginFeature() {
        FEATURE_DEPTH.get()[0]++;
    }

    public static void endFeature() {
        int[] depth = FEATURE_DEPTH.get();
        depth[0] = Math.max(0, depth[0] - 1);
    }

    public static void resetFeatureDepth() {
        FEATURE_DEPTH.get()[0] = 0;
    }

    public static boolean placingFeature() {
        return FEATURE_DEPTH.get()[0] > 0;
    }

    public static boolean protects(WorldGenLevel level, BlockPos pos, BlockState placing) {
        if (!placingFeature() || !MobsConfig.ProtectTrialChambers.get()) {
            return false;
        }
        List<BoundingBox> boxes = boxesFor(level, pos);
        if (boxes.isEmpty()) {
            return false;
        }
        for (BoundingBox box : boxes) {
            if (box.isInside(pos)) {
                return !placing.is(DECORATION_ALLOWED) || !level.getBlockState(pos).isAir();
            }
        }
        return false;
    }

    private static List<BoundingBox> boxesFor(WorldGenLevel level, BlockPos pos) {
        ServerLevel owner = level.getLevel();
        long key = ChunkPos.asLong(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()));
        ChunkBoxCache cache = CACHE.get();
        List<BoundingBox> cached = cache.get(owner, key);
        if (cached != null) {
            return cached;
        }
        List<BoundingBox> boxes = collect(level, owner, new ChunkPos(key));
        cache.put(key, boxes);
        return boxes;
    }

    private static List<BoundingBox> collect(WorldGenLevel level, ServerLevel owner, ChunkPos at) {
        ChunkAccess chunk = level.getChunk(at.x, at.z, ChunkStatus.STRUCTURE_STARTS, false);
        if (chunk == null) {
            return List.of();
        }
        Registry<Structure> registry = owner.registryAccess().registryOrThrow(Registries.STRUCTURE);
        List<BoundingBox> boxes = new ArrayList<>();
        for (Map.Entry<Structure, StructureStart> entry : chunk.getAllStarts().entrySet()) {
            if (isProtected(registry, entry.getKey())) {
                addPieces(boxes, entry.getValue());
            }
        }
        for (Map.Entry<Structure, LongSet> entry : chunk.getAllReferences().entrySet()) {
            if (!isProtected(registry, entry.getKey())) {
                continue;
            }
            LongIterator iterator = entry.getValue().iterator();
            while (iterator.hasNext()) {
                ChunkPos start = new ChunkPos(iterator.nextLong());
                ChunkAccess startChunk = level.getChunk(start.x, start.z, ChunkStatus.STRUCTURE_STARTS, false);
                if (startChunk != null) {
                    addPieces(boxes, startChunk.getStartForStructure(entry.getKey()));
                }
            }
        }
        return boxes.isEmpty() ? List.of() : List.copyOf(boxes);
    }

    private static void addPieces(List<BoundingBox> boxes, StructureStart start) {
        if (start == null || !start.isValid()) {
            return;
        }
        for (StructurePiece piece : start.getPieces()) {
            boxes.add(piece.getBoundingBox());
        }
    }

    private static boolean isProtected(Registry<Structure> registry, Structure structure) {
        return registry.getResourceKey(structure)
                .flatMap(key -> registry.getHolder(key))
                .map(holder -> holder.is(PROTECTED_STRUCTURES))
                .orElse(false);
    }

    private static final class ChunkBoxCache {

        private static final WeakReference<ServerLevel> NO_OWNER = new WeakReference<>(null);

        private WeakReference<ServerLevel> owner = NO_OWNER;
        private final Map<Long, List<BoundingBox>> boxes = new LinkedHashMap<>(32, 0.75F, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Long, List<BoundingBox>> eldest) {
                return this.size() > 64;
            }
        };

        private List<BoundingBox> get(ServerLevel level, long key) {
            if (this.owner.get() != level) {
                this.boxes.clear();
                this.owner = new WeakReference<>(level);
                return null;
            }
            return this.boxes.get(key);
        }

        private void put(long key, List<BoundingBox> value) {
            this.boxes.put(key, value);
        }
    }

    private StructureProtection() {
    }
}
