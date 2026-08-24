/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 */
package com.vivideru.masteryofmagic.block.entity;

import com.vivideru.masteryofmagic.block.entity.ChargedRunedLazethystBlockEntity;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ChargedFrostRunedLazethystBlockEntity
extends ChargedRunedLazethystBlockEntity {
    public ChargedFrostRunedLazethystBlockEntity(BlockPos pos, BlockState state) {
        super((BlockEntityType)GoetyMasteryOfMagicModBlockEntities.FROST_RUNED_LAZETHYST_BLOCK_CHARGED.get(), pos, state);
    }
}

