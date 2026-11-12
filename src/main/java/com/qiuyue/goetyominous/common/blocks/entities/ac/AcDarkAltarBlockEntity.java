package com.qiuyue.goetyominous.common.blocks.entities.ac;

import com.Polarice3.Goety.common.blocks.entities.DarkAltarBlockEntity;
import com.qiuyue.goetyominous.common.init.ac.AcBlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class AcDarkAltarBlockEntity extends DarkAltarBlockEntity {

    public AcDarkAltarBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    public BlockEntityType<?> getType() {
        return AcBlockEntityRegistry.DARK_ALTAR.get();
    }
}
