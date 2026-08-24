/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.level.block.Blocks
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.vivideru.masteryofmagic.mixins;

import com.vivideru.masteryofmagic.goldification.client.GoldificationClientState;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={LevelRenderer.class})
public abstract class GoldificationLevelRendererSoundMixin {
    @Shadow
    private ClientLevel f_109465_;

    @Inject(method={"levelEvent(ILnet/minecraft/core/BlockPos;I)V"}, at={@At(value="HEAD")}, cancellable=true, remap=false, require=0)
    private void replaceBreakEventDev(int eventId, BlockPos position, int data, CallbackInfo callbackInfo) {
        this.replaceBreakEvent(eventId, position, callbackInfo);
    }

    @Inject(method={"m_234304_(ILnet/minecraft/core/BlockPos;I)V"}, at={@At(value="HEAD")}, cancellable=true, remap=false, require=0)
    private void replaceBreakEventProduction(int eventId, BlockPos position, int data, CallbackInfo callbackInfo) {
        this.replaceBreakEvent(eventId, position, callbackInfo);
    }

    private void replaceBreakEvent(int eventId, BlockPos position, CallbackInfo callbackInfo) {
        if (eventId != 2001 || !GoldificationClientState.isBlockGoldifiedForSound(position)) {
            return;
        }
        this.f_109465_.m_245747_(position, SoundEvents.f_12062_, SoundSource.BLOCKS, 1.0f, 0.72f, false);
        this.f_109465_.m_142052_(position, Blocks.f_50074_.m_49966_());
        callbackInfo.cancel();
    }
}

