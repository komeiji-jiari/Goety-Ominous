/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  net.minecraft.client.renderer.block.BlockRenderDispatcher
 *  net.minecraft.client.renderer.block.LiquidBlockRenderer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.material.FluidState
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package com.vivideru.masteryofmagic.mixins;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.vivideru.masteryofmagic.goldification.client.GoldificationBlockMarkerVertexConsumer;
import com.vivideru.masteryofmagic.goldification.client.GoldificationClientState;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.LiquidBlockRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={BlockRenderDispatcher.class})
public abstract class GoldificationLiquidRendererMixin {
    @Redirect(method={"renderLiquid(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/BlockAndTintGetter;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/FluidState;)V"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/renderer/block/LiquidBlockRenderer;tesselate(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/FluidState;)V"), remap=false, require=0)
    private void renderGoldifiedLiquidDev(LiquidBlockRenderer renderer, BlockAndTintGetter level, BlockPos position, VertexConsumer consumer, BlockState blockState, FluidState fluidState) {
        renderer.m_234369_(level, position, GoldificationLiquidRendererMixin.effectiveConsumer(position, consumer), blockState, fluidState);
    }

    @Redirect(method={"m_234363_(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/BlockAndTintGetter;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/FluidState;)V"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/renderer/block/LiquidBlockRenderer;m_234369_(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/FluidState;)V"), remap=false, require=0)
    private void renderGoldifiedLiquidProduction(LiquidBlockRenderer renderer, BlockAndTintGetter level, BlockPos position, VertexConsumer consumer, BlockState blockState, FluidState fluidState) {
        renderer.m_234369_(level, position, GoldificationLiquidRendererMixin.effectiveConsumer(position, consumer), blockState, fluidState);
    }

    private static VertexConsumer effectiveConsumer(BlockPos position, VertexConsumer consumer) {
        return GoldificationClientState.isBlockGoldified(position) ? GoldificationBlockMarkerVertexConsumer.wrap(consumer) : consumer;
    }
}

