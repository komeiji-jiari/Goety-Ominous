/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.material.Fluid
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.vivideru.masteryofmagic.mixins;

import com.vivideru.masteryofmagic.goldification.GoldificationManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ServerLevel.class})
public abstract class GoldificationFluidTickMixin {
    @Inject(method={"tickFluid(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/material/Fluid;)V"}, at={@At(value="HEAD")}, cancellable=true, remap=false, require=0)
    private void freezeGoldifiedFluidDev(BlockPos position, Fluid fluid, CallbackInfo callbackInfo) {
        this.cancelGoldifiedFluidTick(position, callbackInfo);
    }

    @Inject(method={"m_184076_(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/material/Fluid;)V"}, at={@At(value="HEAD")}, cancellable=true, remap=false, require=0)
    private void freezeGoldifiedFluidProduction(BlockPos position, Fluid fluid, CallbackInfo callbackInfo) {
        this.cancelGoldifiedFluidTick(position, callbackInfo);
    }

    private void cancelGoldifiedFluidTick(BlockPos position, CallbackInfo callbackInfo) {
        ServerLevel level = (ServerLevel)this;
        if (GoldificationManager.isBlockGoldified(level, position)) {
            callbackInfo.cancel();
        }
    }
}

