package com.qiuyue.goetyominous.common.worldgen.crypt;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public record CryptResolver(RegistryAccess registryAccess, ChunkGenerator generator, RandomState randomState,
                            StructureTemplateManager templates, long seed, LevelHeightAccessor heightAccessor) {

    public static CryptResolver of(Structure.GenerationContext context) {
        return new CryptResolver(context.registryAccess(), context.chunkGenerator(), context.randomState(),
                context.structureTemplateManager(), context.seed(), context.heightAccessor());
    }

    public StructureStart generate(Structure structure, ChunkPos chunkPos) {
        return structure.generate(this.registryAccess, this.generator, this.generator.getBiomeSource(),
                this.randomState, this.templates, this.seed, chunkPos, 0, this.heightAccessor,
                structure.biomes()::contains);
    }

    public record Door(BlockPos jigsaw, Direction facing) {

        public BlockPos target() {
            return this.jigsaw.relative(this.facing);
        }
    }
}
