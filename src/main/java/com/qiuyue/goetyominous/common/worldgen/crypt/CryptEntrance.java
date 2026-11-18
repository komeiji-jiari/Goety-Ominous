package com.qiuyue.goetyominous.common.worldgen.crypt;

import com.Polarice3.Goety.common.blocks.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;

import java.util.List;

public final class CryptEntrance {
    private CryptEntrance() {
    }

    public static BlockPos doorFor(ServerLevel level, BlockPos located) {
        if (CryptGeneration.classic()) {
            return located;
        }
        Structure crypt = level.registryAccess().registryOrThrow(Registries.STRUCTURE).get(CryptPlans.ID);
        if (crypt == null) {
            return located;
        }
        ChunkPos chunkPos = new ChunkPos(located);
        StructureStart start = level.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.STRUCTURE_STARTS).getStartForStructure(crypt);
        if (start == null || !start.isValid()) {
            return located;
        }
        for (StructurePiece piece : start.getPieces()) {
            if (!(piece instanceof PoolElementStructurePiece pool) || !CryptMound.isSurfacePiece(piece)) {
                continue;
            }
            List<BlockPos> door = CryptShell.templateBlocks(pool, level.getStructureManager(), ModBlocks.SHADE_STONE_BLOCK.get());
            if (door.isEmpty()) {
                return located;
            }
            double x = 0.0D;
            double y = 0.0D;
            double z = 0.0D;
            for (BlockPos pos : door) {
                x += pos.getX();
                y += pos.getY();
                z += pos.getZ();
            }
            BlockPos centre = BlockPos.containing(x / door.size(), y / door.size(), z / door.size());
            BlockPos middle = piece.getBoundingBox().getCenter();
            int dx = centre.getX() - middle.getX();
            int dz = centre.getZ() - middle.getZ();
            Direction out = Math.abs(dx) >= Math.abs(dz) ? (dx >= 0 ? Direction.EAST : Direction.WEST) : (dz >= 0 ? Direction.SOUTH : Direction.NORTH);
            return centre.relative(out);
        }
        return located;
    }
}
