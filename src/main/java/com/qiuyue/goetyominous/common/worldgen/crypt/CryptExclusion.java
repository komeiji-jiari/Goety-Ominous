package com.qiuyue.goetyominous.common.worldgen.crypt;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;

import java.util.ArrayList;
import java.util.List;

public final class CryptExclusion {
    private static final int MARGIN = 3;
    private static final ThreadLocal<List<BoundingBox>> ACTIVE = new ThreadLocal<>();

    private CryptExclusion() {
    }

    public static void begin(WorldGenLevel level, ChunkAccess chunk, StructureManager structureManager) {
        ACTIVE.remove();
        if (CryptGeneration.classic()) {
            return;
        }
        Registry<Structure> registry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
        ChunkPos chunkPos = chunk.getPos();

        java.util.Set<StructureStart> starts = CryptShell.startsAround(level, chunkPos, registry);
        if (starts.isEmpty()) {
            return;
        }
        BoundingBox around = new BoundingBox(chunkPos.getMinBlockX(), chunk.getMinBuildHeight(), chunkPos.getMinBlockZ(), chunkPos.getMaxBlockX(), chunk.getMaxBuildHeight(), chunkPos.getMaxBlockZ()).inflatedBy(MARGIN);
        BoundingBox wide = around.inflatedBy(WIDE_REACH);
        List<BoundingBox> boxes = new ArrayList<>();
        for (StructureStart start : starts) {
            for (StructurePiece piece : start.getPieces()) {
                BoundingBox box = piece.getBoundingBox();
                if (CryptMound.isSurfacePiece(piece)) {
                    box = new BoundingBox(box.minX(), box.minY(), box.minZ(), box.maxX(), box.minY() + CryptMound.GROUND_OFFSET - 1, box.maxZ());
                }
                box = box.inflatedBy(MARGIN);
                if (box.intersects(wide)) {
                    boxes.add(box);
                }
            }
        }
        if (!boxes.isEmpty()) {
            ACTIVE.set(boxes);
        }
    }

    public static void end() {
        ACTIVE.remove();
    }

    public static final int WIDE_REACH = 16;

    public static boolean excludes(BlockPos pos, int reach) {
        List<BoundingBox> boxes = ACTIVE.get();
        if (boxes == null) {
            return false;
        }
        for (BoundingBox box : boxes) {
            if (reach > 0 ? box.inflatedBy(reach).isInside(pos) : box.isInside(pos)) {
                return true;
            }
        }
        return false;
    }
}
