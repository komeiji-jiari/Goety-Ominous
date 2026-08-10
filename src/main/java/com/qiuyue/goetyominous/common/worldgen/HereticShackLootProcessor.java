package com.qiuyue.goetyominous.common.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class HereticShackLootProcessor extends StructureProcessor {

    public static final Codec<HereticShackLootProcessor> CODEC = Codec.unit(new HereticShackLootProcessor());

    private static final Map<ResourceLocation, ResourceLocation> LOOT_BY_BLOCK = Map.of(
            new ResourceLocation("minecraft", "chest"),
            new ResourceLocation("goety", "chests/graveyard_treasure"),
            new ResourceLocation("goety", "rotten_chest"),
            new ResourceLocation("goety", "chests/shack_potion")
    );

    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo process(
            LevelReader level, BlockPos offset, BlockPos pos,
            StructureTemplate.StructureBlockInfo blockInfo,
            StructureTemplate.StructureBlockInfo relativeBlockInfo,
            StructurePlaceSettings settings, @Nullable StructureTemplate template) {

        ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(relativeBlockInfo.state().getBlock());
        ResourceLocation loot = blockId == null ? null : LOOT_BY_BLOCK.get(blockId);
        if (loot == null) {
            return relativeBlockInfo;
        }

        CompoundTag nbt = relativeBlockInfo.nbt();
        if (nbt == null) {
            nbt = new CompoundTag();
        }
        nbt.putString("LootTable", loot.toString());
        nbt.putLong("LootTableSeed", relativeBlockInfo.pos().asLong());
        return new StructureTemplate.StructureBlockInfo(relativeBlockInfo.pos(), relativeBlockInfo.state(), nbt);
    }

    @Override
    public StructureProcessorType<?> getType() {
        return com.qiuyue.goetyominous.common.init.ModProcessorTypes.HERETIC_SHACK_LOOT.get();
    }
}