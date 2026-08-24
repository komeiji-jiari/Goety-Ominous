/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.Container
 *  net.minecraft.world.Containers
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.MenuProvider
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.DirectionalBlock
 *  net.minecraft.world.level.block.EntityBlock
 *  net.minecraft.world.level.block.Mirror
 *  net.minecraft.world.level.block.Rotation
 *  net.minecraft.world.level.block.SoundType
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityTicker
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.DirectionProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 */
package com.vivideru.masteryofmagic.block;

import com.vivideru.masteryofmagic.block.entity.MovementForcerBlockEntity;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModItems;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;

public class MovementForcerBlock
extends Block
implements EntityBlock {
    public static final DirectionProperty FACING = DirectionalBlock.f_52588_;

    public MovementForcerBlock() {
        super(BlockBehaviour.Properties.m_284310_().m_60918_(SoundType.f_56742_).m_60913_(1.0f, 50.0f).m_60953_(s -> 1).m_60999_().m_60982_((bs, br, bp) -> true).m_60991_((bs, br, bp) -> true));
        this.m_49959_((BlockState)((BlockState)this.f_49792_.m_61090_()).m_61124_((Property)FACING, (Comparable)Direction.NORTH));
    }

    public int m_7753_(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return 15;
    }

    protected void m_7926_(StateDefinition.Builder<Block, BlockState> builder) {
        super.m_7926_(builder);
        builder.m_61104_(new Property[]{FACING});
    }

    public BlockState m_5573_(BlockPlaceContext context) {
        return (BlockState)super.m_5573_(context).m_61124_((Property)FACING, (Comparable)context.m_43719_());
    }

    public BlockState m_6843_(BlockState state, Rotation rot) {
        return (BlockState)state.m_61124_((Property)FACING, (Comparable)rot.m_55954_((Direction)state.m_61143_((Property)FACING)));
    }

    public BlockState m_6943_(BlockState state, Mirror mirrorIn) {
        return state.m_60717_(mirrorIn.m_54846_((Direction)state.m_61143_((Property)FACING)));
    }

    public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
        return true;
    }

    public InteractionResult m_6227_(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack stack = player.m_21120_(hand);
        if (stack.m_41619_()) {
            return InteractionResult.PASS;
        }
        BlockEntity blockEntity = world.m_7702_(pos);
        if (!(blockEntity instanceof MovementForcerBlockEntity)) {
            return InteractionResult.PASS;
        }
        MovementForcerBlockEntity movementForcer = (MovementForcerBlockEntity)blockEntity;
        boolean changed = false;
        boolean consumeItem = true;
        if (stack.m_150930_(Items.f_42584_)) {
            changed = movementForcer.increaseRange();
        } else if (stack.m_150930_(Items.f_42619_)) {
            changed = movementForcer.decreaseRange();
        } else if (stack.m_150930_(Items.f_42545_)) {
            changed = movementForcer.increaseAcceleration();
        } else if (stack.m_150930_(Items.f_42620_)) {
            changed = movementForcer.decreaseAcceleration();
        } else if (stack.m_150930_(Items.f_42732_)) {
            movementForcer.toggleInverted();
            changed = true;
        } else if (stack.m_150930_(Items.f_41829_)) {
            changed = movementForcer.increaseWidth();
        } else if (stack.m_150930_((Item)GoetyMasteryOfMagicModItems.PERMA_LAZETHYST.get())) {
            changed = movementForcer.refillSE();
            consumeItem = false;
        }
        if (!changed) {
            return InteractionResult.PASS;
        }
        if (!world.m_5776_() && consumeItem && !player.m_150110_().f_35937_) {
            stack.m_41774_(1);
        }
        return InteractionResult.m_19078_((boolean)world.m_5776_());
    }

    public MenuProvider m_7246_(BlockState state, Level worldIn, BlockPos pos) {
        MenuProvider menuProvider;
        BlockEntity tileEntity = worldIn.m_7702_(pos);
        return tileEntity instanceof MenuProvider ? (menuProvider = (MenuProvider)tileEntity) : null;
    }

    public BlockEntity m_142194_(BlockPos pos, BlockState state) {
        return new MovementForcerBlockEntity(pos, state);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> m_142354_(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.m_5776_()) {
            return null;
        }
        return (tickerLevel, tickerPos, tickerState, tickerBlockEntity) -> {
            if (tickerBlockEntity instanceof MovementForcerBlockEntity) {
                MovementForcerBlockEntity movementForcer = (MovementForcerBlockEntity)tickerBlockEntity;
                MovementForcerBlockEntity.serverTick(tickerLevel, tickerPos, tickerState, movementForcer);
            }
        };
    }

    public boolean m_8133_(BlockState state, Level world, BlockPos pos, int eventID, int eventParam) {
        super.m_8133_(state, world, pos, eventID, eventParam);
        BlockEntity blockEntity = world.m_7702_(pos);
        return blockEntity != null && blockEntity.m_7531_(eventID, eventParam);
    }

    public void m_6810_(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.m_60734_() != newState.m_60734_()) {
            BlockEntity blockEntity = world.m_7702_(pos);
            if (blockEntity instanceof MovementForcerBlockEntity) {
                MovementForcerBlockEntity be = (MovementForcerBlockEntity)blockEntity;
                Containers.m_19002_((Level)world, (BlockPos)pos, (Container)be);
                world.m_46717_(pos, (Block)this);
            }
            super.m_6810_(state, world, pos, newState, isMoving);
        }
    }

    public boolean m_7278_(BlockState state) {
        return true;
    }

    public int m_6782_(BlockState blockState, Level world, BlockPos pos) {
        BlockEntity tileentity = world.m_7702_(pos);
        if (tileentity instanceof MovementForcerBlockEntity) {
            MovementForcerBlockEntity be = (MovementForcerBlockEntity)tileentity;
            return AbstractContainerMenu.m_38938_((Container)be);
        }
        return 0;
    }
}

