package com.qiuyue.goetyominous.common.blocks.trial;

import com.qiuyue.goetyominous.common.init.ModBlockEntities;
import javax.annotation.Nullable;

import com.qiuyue.goetyominous.common.init.ModSounds;
import com.qiuyue.goetyominous.common.items.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;

public class VaultBlock extends BaseEntityBlock {
    public static final EnumProperty<VaultState> STATE = EnumProperty.create("vault_state", VaultState.class);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty OMINOUS = BooleanProperty.create("ominous");

    public VaultBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(STATE, VaultState.INACTIVE)
                .setValue(OMINOUS, false));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack stack = player.getItemInHand(hand);
        if (isResetKey(stack) && state.getValue(STATE) == VaultState.INACTIVE) {
            if (!(level instanceof ServerLevel serverLevel)) {
                return InteractionResult.CONSUME;
            }
            if (!(level.getBlockEntity(pos) instanceof VaultBlockEntity vault)) {
                return InteractionResult.PASS;
            }
            if (stack.is(ModItems.OMINOUS_RESET_KEY.get()) != state.getValue(OMINOUS)) {
                level.playSound(null, pos, ModSounds.VAULT_INSERT_ITEM_FAIL.get(), SoundSource.BLOCKS);
                return InteractionResult.SUCCESS;
            }
            vault.getServerData().getRewardedPlayers().clear();
            vault.getServerData().isDirty = true;
            level.playSound(null, pos, ModSounds.VAULT_INSERT_ITEM.get(), SoundSource.BLOCKS);
            VaultBlockEntity.Server.setVaultState(serverLevel, pos, state,
                    state.setValue(STATE, VaultState.ACTIVE), vault.getConfig(), vault.getSharedData());
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            return InteractionResult.SUCCESS;
        }
        if (stack.isEmpty() || state.getValue(STATE) != VaultState.ACTIVE) {
            return InteractionResult.PASS;
        }
        if (!(level instanceof ServerLevel)) {
            return InteractionResult.CONSUME;
        }
        if (level.getBlockEntity(pos) instanceof VaultBlockEntity vault) {
            VaultBlockEntity.Server.tryInsertKey((ServerLevel) level, pos, state, vault.getConfig(),
                    vault.getServerData(), vault.getSharedData(), player, stack);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    private static boolean isResetKey(ItemStack stack) {
        return stack.is(ModItems.RESET_KEY.get()) || stack.is(ModItems.OMINOUS_RESET_KEY.get());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new VaultBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, STATE, OMINOUS);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level instanceof ServerLevel serverLevel) {
            return createTickerHelper(type, ModBlockEntities.VAULT.get(),
                    (level1, pos, state1, vault) -> VaultBlockEntity.Server.tick(serverLevel, pos, state1,
                            vault.getConfig(), vault.getServerData(), vault.getSharedData()));
        }
        return createTickerHelper(type, ModBlockEntities.VAULT.get(),
                (level1, pos, state1, vault) -> VaultBlockEntity.Client.tick(level1, pos, state1,
                        vault.getClientData(), vault.getSharedData()));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(FACING, mirror.mirror(state.getValue(FACING)));
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}
