/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.Polarice3.Goety.api.items.magic.IFocus
 *  com.Polarice3.Goety.api.magic.IBlockSpell
 *  com.Polarice3.Goety.api.magic.ISpell
 *  com.Polarice3.Goety.api.magic.ITouchSpell
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.SoundType
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.NoteBlockInstrument
 *  net.minecraft.world.phys.BlockHitResult
 */
package com.vivideru.masteryofmagic.block;

import com.Polarice3.Goety.api.items.magic.IFocus;
import com.Polarice3.Goety.api.magic.IBlockSpell;
import com.Polarice3.Goety.api.magic.ISpell;
import com.Polarice3.Goety.api.magic.ITouchSpell;
import com.vivideru.masteryofmagic.block.entity.ChargedRunedLazethystBlockEntity;
import com.vivideru.masteryofmagic.config.RunedBlocksConfig;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModBlocks;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.phys.BlockHitResult;

public class RunedLazethystBlock
extends Block {
    private static final ResourceLocation WITHER_SKULL_FOCUS = new ResourceLocation("goety", "wither_skull_focus");

    public RunedLazethystBlock() {
        super(BlockBehaviour.Properties.m_284310_().m_280658_(NoteBlockInstrument.BASEDRUM).m_60918_(SoundType.f_56742_).m_60913_(3.0f, 10.0f));
    }

    public int m_7753_(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return 15;
    }

    public InteractionResult m_6227_(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack stack = player.m_21120_(hand);
        Item item = stack.m_41720_();
        if (!(item instanceof IFocus)) {
            return InteractionResult.PASS;
        }
        IFocus focus = (IFocus)item;
        ResourceLocation focusId = BuiltInRegistries.f_257033_.m_7981_((Object)stack.m_41720_());
        if (focusId != null && ((List)RunedBlocksConfig.FOCUS_BLACKLIST.get()).contains(focusId.toString())) {
            return InteractionResult.PASS;
        }
        ISpell iSpell = focus.getSpell();
        if (iSpell == null) {
            return InteractionResult.PASS;
        }
        if (iSpell instanceof IBlockSpell || iSpell instanceof ITouchSpell) {
            return InteractionResult.PASS;
        }
        if (level.f_46443_) {
            return InteractionResult.SUCCESS;
        }
        level.m_5594_(null, pos, SoundEvents.f_11887_, SoundSource.BLOCKS, 1.0f, 1.2f);
        level.m_7731_(pos, ((Block)GoetyMasteryOfMagicModBlocks.CHARGED_RUNED_LAZETHYST_BLOCK.get()).m_49966_(), 3);
        BlockEntity blockEntity = level.m_7702_(pos);
        if (blockEntity instanceof ChargedRunedLazethystBlockEntity) {
            ChargedRunedLazethystBlockEntity be = (ChargedRunedLazethystBlockEntity)blockEntity;
            be.applyFocus(stack, player);
        }
        return InteractionResult.CONSUME;
    }
}

