package com.qiuyue.goetyominous.common.blocks.entities.mm;

import com.alexander.mutantmore.blocks.QuicksandBlock;
import com.alexander.mutantmore.config.QuicksandCommonConfig;
import com.qiuyue.goetyominous.common.init.mm.MmBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ServantQuicksandBlockEntity extends BlockEntity {

    private int tickCount;
    private final int lifeSpan;
    private final RandomSource random = RandomSource.create();

    public ServantQuicksandBlockEntity(BlockPos pos, BlockState state) {
        super(MmBlockEntities.SERVANT_QUICKSAND.get(), pos, state);
        int min = QuicksandCommonConfig.min_lifespan.get();
        int max = QuicksandCommonConfig.max_lifespan.get();
        int randomAdded = max - min <= 0 ? 0 : this.random.nextInt(max - min);
        this.lifeSpan = min + randomAdded;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ServantQuicksandBlockEntity blockEntity) {
        if (!state.getValue(QuicksandBlock.PERSISTENT)) {
            ++blockEntity.tickCount;
            if (!level.isClientSide && blockEntity.tickCount >= blockEntity.lifeSpan) {
                level.removeBlock(pos, false);
            }
        }
    }
}
