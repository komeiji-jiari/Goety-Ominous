package com.qiuyue.goetyominous.common.worldgen.crypt;

import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public final class CryptTerrain {

    private static final ThreadLocal<ChunkGeneratorStructureState> STRUCTURE_STATE = new ThreadLocal<>();

    public static void enterStructureState(ChunkGeneratorStructureState state) {
        STRUCTURE_STATE.set(state);
    }

    public static void exitStructureState() {
        STRUCTURE_STATE.remove();
    }

    @Nullable
    public static ChunkGeneratorStructureState structureState() {
        return STRUCTURE_STATE.get();
    }

    public static void refreshHeightmaps(ChunkAccess chunk) {
        Heightmap.primeHeightmaps(chunk, EnumSet.of(Heightmap.Types.WORLD_SURFACE_WG, Heightmap.Types.OCEAN_FLOOR_WG,
                Heightmap.Types.WORLD_SURFACE, Heightmap.Types.OCEAN_FLOOR, Heightmap.Types.MOTION_BLOCKING,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES));
    }

    private CryptTerrain() {
    }
}
