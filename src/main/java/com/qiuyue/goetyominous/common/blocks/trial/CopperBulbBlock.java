package com.qiuyue.goetyominous.common.blocks.trial;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class CopperBulbBlock extends Block {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public CopperBulbBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(LIT, false).setValue(POWERED, false));
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (oldState.getBlock() != state.getBlock() && level instanceof ServerLevel serverLevel) {
            this.checkAndFlip(state, serverLevel, pos);
        }
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (level instanceof ServerLevel serverLevel) {
            this.checkAndFlip(state, serverLevel, pos);
        }
    }

    public void checkAndFlip(BlockState state, ServerLevel level, BlockPos pos) {
        boolean powered = level.hasNeighborSignal(pos);
        if (powered == state.getValue(POWERED)) {
            return;
        }
        BlockState newState = state;
        if (!state.getValue(POWERED)) {
            newState = newState.cycle(LIT);
            level.playSound(null, pos, newState.getValue(LIT) ? TrialSounds.COPPER_BULB_TURN_ON.get() : TrialSounds.COPPER_BULB_TURN_OFF.get(), SoundSource.BLOCKS);
        }
        level.setBlock(pos, newState.setValue(POWERED, powered), 3);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT, POWERED);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return level.getBlockState(pos).getValue(LIT) ? 15 : 0;
    }
}
