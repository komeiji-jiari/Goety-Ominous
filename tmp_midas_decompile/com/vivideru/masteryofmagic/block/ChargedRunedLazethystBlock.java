/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.Container
 *  net.minecraft.world.Containers
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.MenuProvider
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Items
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
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModItems;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
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

public class ChargedRunedLazethystBlock
extends Block
implements EntityBlock {
    public ChargedRunedLazethystBlock() {
        super(BlockBehaviour.Properties.m_284310_().m_280658_(NoteBlockInstrument.BASEDRUM).m_60918_(SoundType.f_56742_).m_60913_(3.0f, 10.0f).m_60953_(s -> 15).m_60977_().m_60982_((bs, br, bp) -> true).m_60991_((bs, br, bp) -> true));
    }

    public int m_7753_(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return 1;
    }

    public BlockEntity m_142194_(BlockPos pos, BlockState state) {
        return new ChargedRunedLazethystBlockEntity(pos, state);
    }

    public void m_213898_(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.m_276867_(pos)) {
            level.m_8767_((ParticleOptions)ParticleTypes.f_123809_, (double)pos.m_123341_() + 0.5, (double)pos.m_123342_() + 1.1, (double)pos.m_123343_() + 0.5, 6, 0.3, 0.3, 0.3, 0.0);
        }
    }

    public MenuProvider m_7246_(BlockState state, Level worldIn, BlockPos pos) {
        MenuProvider mp;
        BlockEntity tileEntity = worldIn.m_7702_(pos);
        return tileEntity instanceof MenuProvider ? (mp = (MenuProvider)tileEntity) : null;
    }

    public void m_6810_(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.m_60734_() != newState.m_60734_()) {
            BlockEntity blockEntity = world.m_7702_(pos);
            if (blockEntity instanceof ChargedRunedLazethystBlockEntity) {
                ChargedRunedLazethystBlockEntity be = (ChargedRunedLazethystBlockEntity)blockEntity;
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
        return 0;
    }

    public <T extends BlockEntity> BlockEntityTicker<T> m_142354_(Level level, BlockState state, BlockEntityType<T> type) {
        return level.f_46443_ ? null : (lvl, pos, st, be) -> {
            if (be instanceof ChargedRunedLazethystBlockEntity) {
                ChargedRunedLazethystBlockEntity tile = (ChargedRunedLazethystBlockEntity)be;
                ChargedRunedLazethystBlockEntity.tick(lvl, pos, st, tile);
            }
        };
    }

    public InteractionResult m_6227_(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BlockEntity blockEntity;
        if (level.f_46443_) {
            return InteractionResult.SUCCESS;
        }
        if (player.m_21120_(hand).m_150930_(Items.f_42409_) && (blockEntity = level.m_7702_(pos)) instanceof ChargedRunedLazethystBlockEntity) {
            ChargedRunedLazethystBlockEntity be = (ChargedRunedLazethystBlockEntity)blockEntity;
            be.ownerUUID = new UUID(0L, 776L);
            be.m_6596_();
            level.m_7260_(pos, state, state, 3);
            level.m_5594_(null, pos, SoundEvents.f_11705_, SoundSource.BLOCKS, 1.0f, 1.0f);
            return InteractionResult.CONSUME;
        }
        if (player.m_21120_(hand).m_150930_((Item)GoetyMasteryOfMagicModItems.PERMA_LAZETHYST.get()) && (blockEntity = level.m_7702_(pos)) instanceof ChargedRunedLazethystBlockEntity) {
            ChargedRunedLazethystBlockEntity be = (ChargedRunedLazethystBlockEntity)blockEntity;
            be.ISDUNGEON = true;
            be.m_6596_();
            level.m_7260_(pos, state, state, 3);
            level.m_5594_(null, pos, SoundEvents.f_11705_, SoundSource.BLOCKS, 1.0f, 1.0f);
            return InteractionResult.CONSUME;
        }
        return super.m_6227_(state, level, pos, player, hand, hit);
    }
}

