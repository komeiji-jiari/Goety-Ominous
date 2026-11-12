package com.qiuyue.goetyominous.common.blocks.ac;

import com.Polarice3.Goety.common.blocks.PedestalBlock;
import com.qiuyue.goetyominous.common.blocks.entities.ac.AcPedestalBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class AcPedestalBlock extends PedestalBlock {

    public AcPedestalBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AcPedestalBlockEntity(pos, state);
    }
}
