/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.Container
 *  net.minecraft.world.Containers
 *  net.minecraft.world.MenuProvider
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.EntityBlock
 *  net.minecraft.world.level.block.SoundType
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityTicker
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.material.PushReaction
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.Shapes
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package com.vivideru.masteryofmagic.block;

import com.vivideru.masteryofmagic.block.entity.SoulBarrierBlockEntity;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SoulBarrierBlock
extends Block
implements EntityBlock {
    public SoulBarrierBlock() {
        super(BlockBehaviour.Properties.m_284310_().m_60918_(SoundType.f_56744_).m_60978_(50.0f).m_60953_(s -> 10).m_60955_().m_278166_(PushReaction.IGNORE).m_60982_((bs, br, bp) -> true).m_60991_((bs, br, bp) -> true).m_60924_((bs, br, bp) -> false));
    }

    public <T extends BlockEntity> BlockEntityTicker<T> m_142354_(Level level, BlockState state, BlockEntityType<T> type) {
        if (type == GoetyMasteryOfMagicModBlockEntities.SOUL_BARRIER_BLOCK.get()) {
            return new BlockEntityTicker<T>(){

                public void m_155252_(Level level, BlockPos pos, BlockState state, T blockEntity) {
                    if (blockEntity instanceof SoulBarrierBlockEntity) {
                        SoulBarrierBlockEntity barrierBlockEntity = (SoulBarrierBlockEntity)((Object)blockEntity);
                        SoulBarrierBlockEntity.tick(level, pos, state, barrierBlockEntity);
                    }
                }
            };
        }
        return null;
    }

    public boolean m_6104_(BlockState state, BlockState adjacentBlockState, Direction side) {
        return adjacentBlockState.m_60734_() == this ? true : super.m_6104_(state, adjacentBlockState, side);
    }

    public int m_7753_(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return 15;
    }

    public VoxelShape m_5909_(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Shapes.m_83040_();
    }

    public MenuProvider m_7246_(BlockState state, Level worldIn, BlockPos pos) {
        MenuProvider menuProvider;
        BlockEntity tileEntity = worldIn.m_7702_(pos);
        return tileEntity instanceof MenuProvider ? (menuProvider = (MenuProvider)tileEntity) : null;
    }

    public BlockEntity m_142194_(BlockPos pos, BlockState state) {
        return new SoulBarrierBlockEntity(pos, state);
    }

    public boolean m_8133_(BlockState state, Level world, BlockPos pos, int eventID, int eventParam) {
        super.m_8133_(state, world, pos, eventID, eventParam);
        BlockEntity blockEntity = world.m_7702_(pos);
        return blockEntity == null ? false : blockEntity.m_7531_(eventID, eventParam);
    }

    public void m_6810_(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.m_60734_() != newState.m_60734_()) {
            BlockEntity blockEntity = world.m_7702_(pos);
            if (blockEntity instanceof SoulBarrierBlockEntity) {
                SoulBarrierBlockEntity be = (SoulBarrierBlockEntity)blockEntity;
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
        if (tileentity instanceof SoulBarrierBlockEntity) {
            SoulBarrierBlockEntity be = (SoulBarrierBlockEntity)tileentity;
            return AbstractContainerMenu.m_38938_((Container)be);
        }
        return 0;
    }
}

