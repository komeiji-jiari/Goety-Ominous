package com.qiuyue.goetyominous.common.blocks.entities.ac;

import com.Polarice3.Goety.common.blocks.entities.PedestalBlockEntity;
import com.qiuyue.goetyominous.common.init.ac.AcBlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class AcPedestalBlockEntity extends PedestalBlockEntity {

    public AcPedestalBlockEntity(BlockPos pos, BlockState state) {
        super(AcBlockEntityRegistry.PEDESTAL.get(), pos, state);
    }
}
