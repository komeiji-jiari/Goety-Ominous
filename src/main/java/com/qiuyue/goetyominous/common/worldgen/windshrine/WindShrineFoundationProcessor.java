package com.qiuyue.goetyominous.common.worldgen.windshrine;

import com.Polarice3.Goety.common.blocks.ModBlocks;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.ArrayList;
import java.util.List;

public class WindShrineFoundationProcessor extends StructureProcessor {
    public static final WindShrineFoundationProcessor INSTANCE = new WindShrineFoundationProcessor();
    public static final Codec<WindShrineFoundationProcessor> CODEC = MapCodec.unit(() -> INSTANCE).codec();
    private static final int MAX_DEPTH = 24;

    private WindShrineFoundationProcessor() {
    }

    @Override
    public List<StructureTemplate.StructureBlockInfo> finalizeProcessing(ServerLevelAccessor level, BlockPos offset, BlockPos pos, List<StructureTemplate.StructureBlockInfo> originalBlocks, List<StructureTemplate.StructureBlockInfo> processedBlocks, StructurePlaceSettings settings) {
        int templateBottom = Integer.MAX_VALUE;
        for (StructureTemplate.StructureBlockInfo info : originalBlocks) {
            templateBottom = Math.min(templateBottom, info.pos().getY());
        }
        for (StructureTemplate.StructureBlockInfo info : originalBlocks) {
            if (info.pos().getY() == templateBottom && info.state().is(Blocks.JIGSAW) && JigsawBlock.getFrontFacing(info.state()) == Direction.DOWN) {
                return processedBlocks;
            }
        }
        int bottomY = Integer.MAX_VALUE;
        for (StructureTemplate.StructureBlockInfo info : processedBlocks) {
            if (isPieceBlock(info.state())) {
                bottomY = Math.min(bottomY, info.pos().getY());
            }
        }
        if (bottomY == Integer.MAX_VALUE) {
            return processedBlocks;
        }
        int bottomBlocks = 0;
        for (StructureTemplate.StructureBlockInfo info : processedBlocks) {
            if (info.pos().getY() == bottomY && isPieceBlock(info.state())) {
                bottomBlocks++;
            }
        }
        if (bottomBlocks <= 9) {
            return processedBlocks;
        }
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (StructureTemplate.StructureBlockInfo info : processedBlocks) {
            if (info.pos().getY() == bottomY && isPieceBlock(info.state())) {
                BlockState below = level.getBlockState(cursor.set(info.pos().getX(), bottomY - 1, info.pos().getZ()));
                if (isMasonry(below)) {
                    return processedBlocks;
                }
            }
        }
        List<StructureTemplate.StructureBlockInfo> plinth = new ArrayList<>();
        for (StructureTemplate.StructureBlockInfo info : processedBlocks) {
            BlockPos blockPos = info.pos();
            if (blockPos.getY() != bottomY || !isPieceBlock(info.state())) {
                continue;
            }
            BlockState state = info.state();
            if (!state.isCollisionShapeFullBlock(level, blockPos) && !(state.getBlock() instanceof StairBlock)) {
                continue;
            }
            for (int y = blockPos.getY() - 1; y >= Math.max(level.getMinBuildHeight(), blockPos.getY() - MAX_DEPTH); y--) {
                cursor.set(blockPos.getX(), y, blockPos.getZ());
                BlockState existing = level.getBlockState(cursor);
                if (!(existing.isAir() || !existing.getFluidState().isEmpty() || existing.canBeReplaced())) {
                    break;
                }
                plinth.add(new StructureTemplate.StructureBlockInfo(cursor.immutable(), pillarBlock(cursor), null));
            }
        }
        if (plinth.isEmpty()) {
            return processedBlocks;
        }
        List<StructureTemplate.StructureBlockInfo> result = new ArrayList<>(processedBlocks);
        result.addAll(plinth);
        return result;
    }

    private static boolean isMasonry(BlockState state) {
        if (state.isAir() || !state.getFluidState().isEmpty() || state.canBeReplaced()) {
            return false;
        }
        return !(state.is(BlockTags.BASE_STONE_OVERWORLD) || state.is(BlockTags.DIRT) || state.is(BlockTags.SNOW)
                || state.is(BlockTags.SAND) || state.is(BlockTags.TERRACOTTA) || state.is(BlockTags.LEAVES)
                || state.is(BlockTags.LOGS) || state.is(Blocks.GRAVEL) || state.is(Blocks.CALCITE) || state.is(Blocks.PACKED_ICE) || state.is(Blocks.ICE)
                || state.is(Blocks.POWDER_SNOW) || state.is(Blocks.CLAY) || state.is(Blocks.MOSS_BLOCK));
    }

    private static boolean isPieceBlock(BlockState state) {
        return !state.isAir() && !state.is(Blocks.STRUCTURE_VOID) && !state.is(Blocks.JIGSAW) && !state.is(Blocks.STRUCTURE_BLOCK) && state.getFluidState().isEmpty() && !isWood(state);
    }

    private static boolean isWood(BlockState state) {
        return state.is(BlockTags.LOGS) || state.is(BlockTags.PLANKS) || state.is(BlockTags.WOODEN_SLABS) || state.is(BlockTags.WOODEN_STAIRS)
                || state.is(BlockTags.WOODEN_FENCES) || state.is(BlockTags.FENCE_GATES) || state.is(BlockTags.WOODEN_TRAPDOORS) || state.is(BlockTags.WOODEN_DOORS)
                || state.getSoundType() == SoundType.WOOD || state.getSoundType() == SoundType.BAMBOO_WOOD || state.getSoundType() == SoundType.CHERRY_WOOD;
    }

    private static BlockState pillarBlock(BlockPos pos) {
        long hash = pos.asLong() * 0x9E3779B97F4A7C15L;
        hash ^= hash >>> 32;
        int roll = (int) Math.floorMod(hash, 7L);
        return roll == 0 ? ModBlocks.WEATHERED_SLATE_MARBLE_BLOCK.get().defaultBlockState() : roll == 1 ? ModBlocks.WORN_SLATE_MARBLE_BLOCK.get().defaultBlockState() : ModBlocks.SLATE_MARBLE_BLOCK.get().defaultBlockState();
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return WindShrineStructures.WIND_SHRINE_FOUNDATION.get();
    }
}
