package com.qiuyue.goetyominous.common.blocks.ac;

import com.Polarice3.Goety.common.blocks.DarkAltarBlock;
import com.qiuyue.goetyominous.common.blocks.entities.ac.AcDarkAltarBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class AcDarkAltarBlock extends DarkAltarBlock {

    public AcDarkAltarBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AcDarkAltarBlockEntity(pos, state);
    }
}
