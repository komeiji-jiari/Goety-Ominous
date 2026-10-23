package com.qiuyue.goetyominous.common.blocks.entities;

import com.qiuyue.goetyominous.common.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class BoggedSkullBlockEntity extends BlockEntity {

    public BoggedSkullBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BOGGED_SKULL.get(), pos, state);
    }
}
