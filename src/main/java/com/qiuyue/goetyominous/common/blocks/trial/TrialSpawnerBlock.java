package com.qiuyue.goetyominous.common.blocks.trial;

import com.qiuyue.goetyominous.common.init.ModBlockEntities;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class TrialSpawnerBlock extends BaseEntityBlock {
    public static final EnumProperty<TrialSpawnerState> STATE =
            EnumProperty.create("trial_spawner_state", TrialSpawnerState.class);
    public static final BooleanProperty OMINOUS = VaultBlock.OMINOUS;

    public TrialSpawnerBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(STATE, TrialSpawnerState.INACTIVE)
                .setValue(OMINOUS, Boolean.FALSE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STATE, OMINOUS);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TrialSpawnerBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level instanceof ServerLevel serverLevel) {
            return createTickerHelper(type, ModBlockEntities.TRIAL_SPAWNER.get(),
                    (tickLevel, pos, tickState, blockEntity) -> blockEntity.getTrialSpawner()
                            .tickServer(serverLevel, pos, tickState.getValue(OMINOUS)));
        }
        return createTickerHelper(type, ModBlockEntities.TRIAL_SPAWNER.get(),
                (tickLevel, pos, tickState, blockEntity) -> blockEntity.getTrialSpawner()
                        .tickClient(tickLevel, pos, tickState.getValue(OMINOUS)));
    }
}
