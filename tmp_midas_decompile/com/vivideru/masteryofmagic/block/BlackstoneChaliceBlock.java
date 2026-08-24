/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.core.BlockPos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.TooltipFlag
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.EntityBlock
 *  net.minecraft.world.level.block.SoundType
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.IntegerProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.Shapes
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package com.vivideru.masteryofmagic.block;

import com.vivideru.masteryofmagic.block.entity.BlackstoneChaliceBlockEntity;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlackstoneChaliceBlock
extends Block
implements EntityBlock {
    public static final IntegerProperty LEVEL = IntegerProperty.m_61631_((String)"level", (int)0, (int)3);

    public BlackstoneChaliceBlock() {
        super(BlockBehaviour.Properties.m_284310_().m_60918_(SoundType.f_56742_).m_60913_(2.0f, 20.0f).m_60999_().m_60955_().m_60924_((bs, br, bp) -> false));
        this.m_49959_((BlockState)((BlockState)this.f_49792_.m_61090_()).m_61124_((Property)LEVEL, (Comparable)Integer.valueOf(0)));
    }

    protected void m_7926_(StateDefinition.Builder<Block, BlockState> builder) {
        builder.m_61104_(new Property[]{LEVEL});
    }

    @Nullable
    public BlockEntity m_142194_(BlockPos pos, BlockState state) {
        return new BlackstoneChaliceBlockEntity(pos, state);
    }

    public void m_6402_(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (level.m_5776_()) {
            return;
        }
        BlockEntity blockEntity = level.m_7702_(pos);
        if (blockEntity instanceof BlackstoneChaliceBlockEntity) {
            BlackstoneChaliceBlockEntity be = (BlackstoneChaliceBlockEntity)blockEntity;
            if (stack.m_41782_() && stack.m_41783_().m_128441_("OwnerUUID")) {
                be.setOwner(stack.m_41783_().m_128461_("OwnerUUID"), stack.m_41783_().m_128461_("OwnerName"));
            } else if (placer != null) {
                be.setOwner(placer.m_20148_().toString(), placer.m_7755_().getString());
            }
        }
    }

    public void m_5871_(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        if (stack.m_41782_()) {
            CompoundTag tag = stack.m_41783_();
            if (tag.m_128441_("Blood")) {
                int blood = tag.m_128451_("Blood");
                int bloodLevel = Math.min(9, blood / 1000);
                tooltip.add((Component)Component.m_237113_((String)("Blood Level: " + bloodLevel + " / 9")));
            } else {
                tooltip.add((Component)Component.m_237113_((String)"Blood Level: 0 / 9"));
            }
            if (tag.m_128441_("OwnerName")) {
                tooltip.add((Component)Component.m_237113_((String)("Owner: " + tag.m_128461_("OwnerName"))));
            } else if (tag.m_128441_("OwnerUUID")) {
                tooltip.add((Component)Component.m_237113_((String)("Owner: " + tag.m_128461_("OwnerUUID"))));
            } else {
                tooltip.add((Component)Component.m_237113_((String)"Owner: none"));
            }
        } else {
            tooltip.add((Component)Component.m_237113_((String)"Blood Level: 0 / 9"));
            tooltip.add((Component)Component.m_237113_((String)"Owner: none"));
        }
    }

    public boolean m_7420_(BlockState state, BlockGetter reader, BlockPos pos) {
        return true;
    }

    public int m_7753_(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return 0;
    }

    public VoxelShape m_5909_(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Shapes.m_83040_();
    }

    public VoxelShape m_5940_(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return BlackstoneChaliceBlock.m_49796_((double)1.0, (double)0.0, (double)1.0, (double)14.0, (double)15.0, (double)15.0);
    }

    public ItemStack m_7397_(BlockGetter level, BlockPos pos, BlockState state) {
        ItemStack stack = super.m_7397_(level, pos, state);
        BlockEntity blockEntity = level.m_7702_(pos);
        if (blockEntity instanceof BlackstoneChaliceBlockEntity) {
            BlackstoneChaliceBlockEntity be = (BlackstoneChaliceBlockEntity)blockEntity;
            CompoundTag tag = stack.m_41784_();
            if (be.hasOwner()) {
                tag.m_128359_("OwnerUUID", be.getOwnerUUID());
                tag.m_128359_("OwnerName", be.getOwnerName());
            }
            tag.m_128405_("Blood", be.getBlood());
        }
        return stack;
    }
}

