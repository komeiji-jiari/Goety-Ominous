package com.qiuyue.goetyominous.common.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

public class LootInjectorProcessor extends StructureProcessor {

    public static final Codec<LootInjectorProcessor> CODEC = Codec.unit(new LootInjectorProcessor());

    @Nullable
    @Override
    public StructureTemplate.StructureBlockInfo process(
            LevelReader level, BlockPos offset, BlockPos pos,
            StructureTemplate.StructureBlockInfo blockInfo,
            StructureTemplate.StructureBlockInfo relativeBlockInfo,
            StructurePlaceSettings settings, @Nullable StructureTemplate template) {

        if (relativeBlockInfo.state().is(Blocks.CHEST)) {
            CompoundTag nbt = relativeBlockInfo.nbt();
            if (nbt == null) {
                nbt = new CompoundTag();
            }
            nbt.putString("LootTable", "minecraft:chests/bastion_other");
            nbt.putLong("LootTableSeed", relativeBlockInfo.pos().asLong());
            return new StructureTemplate.StructureBlockInfo(relativeBlockInfo.pos(), relativeBlockInfo.state(), nbt);
        }
        return relativeBlockInfo;
    }

    @Override
    public StructureProcessorType<?> getType() {
        return com.qiuyue.goetyominous.common.init.ModProcessorTypes.LOOT_INJECTOR.get();
    }
}
