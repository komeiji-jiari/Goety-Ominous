package com.qiuyue.goetyominous.common.worldgen.crypt;

import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.qiuyue.goetyominous.GoetyOminous;
import com.qiuyue.goetyominous.config.WorldGenConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class CryptGeneration {

    public static final int ATTEMPTS = 8;
    private static final int SAMPLE_RADIUS = 14;
    private static final int MAX_DEPTH = 5;
    private static final int MAX_DISTANCE = 80;
    private static final int SURFACE_OFFSET = -59;
    private static final int MAX_SLOPE = 15;
    private static final int MIN_SURFACE = 55;
    private static final int MAX_SURFACE = 80;

    public static final ResourceLocation MAIN_STRUCTURE_SET = new ResourceLocation("goety", "crypt");
    public static final ResourceKey<StructureTemplatePool> MAIN_POOL = ResourceKey.create(
            Registries.TEMPLATE_POOL, new ResourceLocation(GoetyOminous.MOD_ID, "crypt/entrance"));

    public static boolean classic() {
        return WorldGenConfig.ClassicCrypt.get();
    }

    public static boolean replacesClassic(Structure.GenerationContext context) {
        if (classic()) {
            return false;
        }
        Registry<StructureTemplatePool> pools = context.registryAccess().registryOrThrow(Registries.TEMPLATE_POOL);
        return pools.getHolder(MAIN_POOL).isPresent();
    }

    public static Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext context) {
        Holder<StructureTemplatePool> pool = context.registryAccess().registryOrThrow(Registries.TEMPLATE_POOL)
                .getHolderOrThrow(MAIN_POOL);
        ChunkPos chunkPos = context.chunkPos();
        BlockPos start = new BlockPos(chunkPos.getMinBlockX(), groundAround(context, chunkPos) + SURFACE_OFFSET,
                chunkPos.getMinBlockZ());
        Optional<Structure.GenerationStub> stub = JigsawPlacement.addPieces(context, pool, Optional.empty(),
                MAX_DEPTH, start, false, Optional.empty(), MAX_DISTANCE);
        if (stub.isEmpty() || !isSiteSuitable(context, stub.get().position())) {
            return Optional.empty();
        }
        Structure.GenerationStub s = stub.get();
        int ground = context.chunkGenerator().getFirstFreeHeight(s.position().getX(), s.position().getZ(),
                Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState());
        return Optional.of(new Structure.GenerationStub(
                new BlockPos(s.position().getX(), ground, s.position().getZ()), builder -> {
            List<StructurePiece> best = null;
            CryptPlacementRules.Outcome bestOutcome = CryptPlacementRules.Outcome.NONE;
            for (int attempt = 0; attempt < ATTEMPTS; attempt++) {
                Optional<Structure.GenerationStub> retry = attempt == 0 ? Optional.of(s)
                        : JigsawPlacement.addPieces(retryContext(context, attempt), pool, Optional.empty(),
                        MAX_DEPTH, start, false, Optional.empty(), MAX_DISTANCE);
                if (retry.isEmpty()) {
                    continue;
                }
                StructurePiecesBuilder temp = new StructurePiecesBuilder();
                CryptPlacementRules.beginMain(chunkPos, MAX_DEPTH, context.heightAccessor().getMinBuildHeight());
                CryptPlacementRules.Outcome outcome;
                try {
                    retry.get().generator().ifLeft(generator -> generator.accept(temp));
                } finally {
                    outcome = CryptPlacementRules.end();
                }
                if (best == null || outcome.betterThan(bestOutcome, CryptPlacementRules.Kind.MAIN)) {
                    best = new ArrayList<>(temp.build().pieces());
                    bestOutcome = outcome;
                }
                if (outcome.satisfies(CryptPlacementRules.Kind.MAIN)) {
                    break;
                }
            }
            if (best == null) {
                return;
            }
            int lift = doorLift(context, best);
            if (lift != 0) {
                for (StructurePiece piece : best) {
                    piece.move(0, lift, 0);
                }
            }
            CryptExits.openExits(best, context.structureTemplateManager(), context.seed());
            CryptExits.closeOpenDoors(best, context.structureTemplateManager(),
                    context.registryAccess().registryOrThrow(Registries.TEMPLATE_POOL), context.seed());
            for (StructurePiece piece : best) {
                builder.addPiece(piece);
            }
        }));
    }

    static Structure.GenerationContext retryContext(Structure.GenerationContext context, int attempt) {
        return new Structure.GenerationContext(context.registryAccess(), context.chunkGenerator(),
                context.biomeSource(), context.randomState(), context.structureTemplateManager(),
                context.seed() + attempt * 7919L, context.chunkPos(), context.heightAccessor(), context.validBiome());
    }

    private static int doorLift(Structure.GenerationContext context, List<StructurePiece> pieces) {
        for (StructurePiece piece : pieces) {
            if (!(piece instanceof PoolElementStructurePiece pool) || !CryptMound.isSurfacePiece(piece)) {
                continue;
            }
            List<BlockPos> door = CryptShell.templateBlocks(pool, context.structureTemplateManager(),
                    ModBlocks.SHADE_STONE_BLOCK.get());
            if (door.isEmpty()) {
                return 0;
            }
            BlockPos lowest = door.get(0);
            for (BlockPos pos : door) {
                if (pos.getY() < lowest.getY()) {
                    lowest = pos;
                }
            }
            int ground = context.chunkGenerator().getBaseHeight(lowest.getX(), lowest.getZ(),
                    Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState());
            return ground - lowest.getY();
        }
        return 0;
    }

    private static int groundAround(Structure.GenerationContext context, ChunkPos chunkPos) {
        return context.chunkGenerator().getBaseHeight(chunkPos.getMinBlockX(), chunkPos.getMinBlockZ(),
                Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState());
    }

    private static boolean isSiteSuitable(Structure.GenerationContext context, BlockPos center) {
        int surface = context.chunkGenerator().getFirstFreeHeight(center.getX(), center.getZ(),
                Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState());
        int floor = context.chunkGenerator().getFirstFreeHeight(center.getX(), center.getZ(),
                Heightmap.Types.OCEAN_FLOOR_WG, context.heightAccessor(), context.randomState());
        if (surface - floor > 3 || floor < MIN_SURFACE || floor > MAX_SURFACE) {
            return false;
        }
        int lowest = surface;
        int highest = surface;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) {
                    continue;
                }
                int height = context.chunkGenerator().getFirstFreeHeight(center.getX() + dx * SAMPLE_RADIUS,
                        center.getZ() + dz * SAMPLE_RADIUS, Heightmap.Types.WORLD_SURFACE_WG,
                        context.heightAccessor(), context.randomState());
                lowest = Math.min(lowest, height);
                highest = Math.max(highest, height);
            }
        }
        return highest - lowest <= MAX_SLOPE;
    }

    private CryptGeneration() {
    }
}
