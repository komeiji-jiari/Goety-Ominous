/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.Container
 *  net.minecraft.world.Containers
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.MenuProvider
 *  net.minecraft.world.entity.player.Player
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
 *  net.minecraft.world.level.block.state.properties.NoteBlockInstrument
 *  net.minecraft.world.phys.BlockHitResult
 */
package com.vivideru.masteryofmagic.block;

import com.vivideru.masteryofmagic.block.entity.ChargedRunedLazethystBlockEntity;
import com.vivideru.masteryofmagic.block.entity.ChargedWildRunedLazethystBlockEntity;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModBlockEntities;
import com.vivideru.masteryofmagic.magic.RunedLazethystHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
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
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.phys.BlockHitResult;

public class ChargedWildRunedLazethystBlock
extends Block
implements EntityBlock {
    public ChargedWildRunedLazethystBlock() {
        super(BlockBehaviour.Properties.m_284310_().m_280658_(NoteBlockInstrument.BASEDRUM).m_60918_(SoundType.f_56742_).m_60913_(3.0f, 10.0f));
    }

    public int m_7753_(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return 15;
    }

    public MenuProvider m_7246_(BlockState state, Level worldIn, BlockPos pos) {
        MenuProvider menuProvider;
        BlockEntity tileEntity = worldIn.m_7702_(pos);
        return tileEntity instanceof MenuProvider ? (menuProvider = (MenuProvider)tileEntity) : null;
    }

    public BlockEntity m_142194_(BlockPos pos, BlockState state) {
        return new ChargedWildRunedLazethystBlockEntity(pos, state);
    }

    public boolean m_8133_(BlockState state, Level world, BlockPos pos, int eventID, int eventParam) {
        super.m_8133_(state, world, pos, eventID, eventParam);
        BlockEntity blockEntity = world.m_7702_(pos);
        return blockEntity == null ? false : blockEntity.m_7531_(eventID, eventParam);
    }

    public void m_6810_(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.m_60734_() != newState.m_60734_()) {
            BlockEntity blockEntity = world.m_7702_(pos);
            if (blockEntity instanceof ChargedWildRunedLazethystBlockEntity) {
                ChargedWildRunedLazethystBlockEntity be = (ChargedWildRunedLazethystBlockEntity)blockEntity;
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
        if (tileentity instanceof ChargedWildRunedLazethystBlockEntity) {
            ChargedWildRunedLazethystBlockEntity be = (ChargedWildRunedLazethystBlockEntity)tileentity;
            return AbstractContainerMenu.m_38938_((Container)be);
        }
        return 0;
    }

    public <T extends BlockEntity> BlockEntityTicker<T> m_142354_(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.f_46443_) {
            return null;
        }
        if (type == GoetyMasteryOfMagicModBlockEntities.WILD_RUNED_LAZETHYST_BLOCK_CHARGED.get()) {
            return (lvl, p, st, be) -> ChargedRunedLazethystBlockEntity.tick(lvl, p, st, (ChargedRunedLazethystBlockEntity)be);
        }
        return null;
    }

    public InteractionResult m_6227_(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return RunedLazethystHandler.handleUse(state, level, pos, player, hand);
    }
}

