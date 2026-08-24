/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.block.LiquidBlock
 *  net.minecraft.world.level.block.state.BlockBehaviour$BlockStateBase
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.Shapes
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.vivideru.masteryofmagic.mixins;

import com.vivideru.masteryofmagic.goldification.client.GoldificationClientState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={BlockBehaviour.BlockStateBase.class})
public abstract class GoldificationLiquidCollisionClientMixin {
    @Inject(method={"getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;"}, at={@At(value="HEAD")}, cancellable=true, remap=false, require=0)
    private void makeGoldifiedLiquidSolidDev(BlockGetter blockGetter, BlockPos position, CollisionContext context, CallbackInfoReturnable<VoxelShape> callbackInfo) {
        this.makeGoldifiedLiquidSolid(position, callbackInfo);
    }

    @Inject(method={"m_60742_(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;"}, at={@At(value="HEAD")}, cancellable=true, remap=false, require=0)
    private void makeGoldifiedLiquidSolidProduction(BlockGetter blockGetter, BlockPos position, CollisionContext context, CallbackInfoReturnable<VoxelShape> callbackInfo) {
        this.makeGoldifiedLiquidSolid(position, callbackInfo);
    }

    private void makeGoldifiedLiquidSolid(BlockPos position, CallbackInfoReturnable<VoxelShape> callbackInfo) {
        BlockState state = (BlockState)this;
        if (state.m_60734_() instanceof LiquidBlock && GoldificationClientState.isBlockGoldified(position)) {
            callbackInfo.setReturnValue((Object)Shapes.m_83144_());
        }
    }
}

