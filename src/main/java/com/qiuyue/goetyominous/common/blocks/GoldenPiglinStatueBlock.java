package com.qiuyue.goetyominous.common.blocks;

import com.Polarice3.Goety.utils.BlockFinder;
import com.qiuyue.goetyominous.common.blocks.entities.CultStatueBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class GoldenPiglinStatueBlock extends CultStatueBlock {
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;

    public GoldenPiglinStatueBlock() {
        super(SoundType.METAL);
    }

    private static final VoxelShape LOWER_SHAPE_NS = Shapes.or(
            Block.box(2.0D, 0.0D, 2.0D, 14.0D, 7.0D, 14.0D),
            Block.box(4.0D, 7.0D, 6.0D, 12.0D, 16.0D, 10.0D),
            Block.box(0.0D, 7.0D, 6.0D, 4.0D, 16.0D, 10.0D),
            Block.box(12.0D, 7.0D, 6.0D, 16.0D, 16.0D, 10.0D));

    private static final VoxelShape LOWER_SHAPE_EW = Shapes.or(
            Block.box(2.0D, 0.0D, 2.0D, 14.0D, 7.0D, 14.0D),
            Block.box(6.0D, 7.0D, 4.0D, 10.0D, 16.0D, 12.0D),
            Block.box(6.0D, 7.0D, 0.0D, 10.0D, 16.0D, 4.0D),
            Block.box(6.0D, 7.0D, 12.0D, 10.0D, 16.0D, 16.0D));

    private static final VoxelShape UPPER_SHAPE_NS = Shapes.or(
            Block.box(4.0D, 0.0D, 6.0D, 12.0D, 3.0D, 10.0D),
            Block.box(3.0D, 3.0D, 3.0D, 13.0D, 12.0D, 13.0D),
            Block.box(0.0D, 0.0D, 6.0D, 4.0D, 3.0D, 10.0D),
            Block.box(12.0D, 0.0D, 6.0D, 16.0D, 3.0D, 10.0D));

    private static final VoxelShape UPPER_SHAPE_EW = Shapes.or(
            Block.box(6.0D, 0.0D, 4.0D, 10.0D, 3.0D, 12.0D),
            Block.box(3.0D, 3.0D, 3.0D, 13.0D, 12.0D, 13.0D),
            Block.box(6.0D, 0.0D, 12.0D, 10.0D, 3.0D, 16.0D),
            Block.box(6.0D, 0.0D, 0.0D, 10.0D, 3.0D, 4.0D));

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HALF);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        return state == null ? null : state.setValue(HALF, DoubleBlockHalf.LOWER);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        level.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER)
                .setValue(WATERLOGGED, level.getFluidState(pos.above()).getType() == Fluids.WATER), 3);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            return level.getBlockState(pos.below()).is(this);
        }
        return super.canSurvive(state, level, pos);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level,
                                  BlockPos currentPos, BlockPos facingPos) {
        BlockState updated = super.updateShape(state, facing, facingState, level, currentPos, facingPos);
        DoubleBlockHalf half = state.getValue(HALF);
        if (facing.getAxis() == Direction.Axis.Y && (half == DoubleBlockHalf.LOWER) == (facing == Direction.UP)) {
            if (!facingState.is(this) || facingState.getValue(HALF) == half) {
                return Blocks.AIR.defaultBlockState();
            }
            return state.setValue(ENABLED, facingState.getValue(ENABLED))
                    .setValue(POWERED, facingState.getValue(POWERED));
        }
        if (half == DoubleBlockHalf.LOWER && facing == Direction.DOWN && !state.canSurvive(level, currentPos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return updated;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        boolean eastWest = state.getValue(FACING).getAxis() == Direction.Axis.X;
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            return eastWest ? LOWER_SHAPE_EW : LOWER_SHAPE_NS;
        }
        return eastWest ? UPPER_SHAPE_EW : UPPER_SHAPE_NS;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && !player.isCreative() && state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            BlockEntity below = level.getBlockEntity(pos.below());
            ItemStack drop = new ItemStack(this);
            if (below instanceof CultStatueBlockEntity shrine) {
                this.setItemStackTags(drop, shrine);
            }
            popResource(level, pos, drop);
        }
        if (!level.isClientSide && player.isCreative()) {
            BlockFinder.preventCreativeDropFromBottomPart(level, pos, state, player);
        }
        super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state,
                              @Nullable BlockEntity blockEntity, ItemStack tool) {
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            return;
        }
        super.playerDestroy(level, player, pos, state, blockEntity, tool);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        ItemStack stack = new ItemStack(this);
        BlockPos blockEntityPos = state.getValue(HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos;
        if (level.getBlockEntity(blockEntityPos) instanceof CultStatueBlockEntity shrine) {
            this.setItemStackTags(stack, shrine);
        }
        return stack;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(HALF) == DoubleBlockHalf.LOWER ? new CultStatueBlockEntity(pos, state) : null;
    }
}
