/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.multiplayer.MultiPlayerGameMode
 *  net.minecraft.client.resources.sounds.SimpleSoundInstance
 *  net.minecraft.client.resources.sounds.SoundInstance
 *  net.minecraft.client.sounds.SoundManager
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package com.vivideru.masteryofmagic.mixins;

import com.vivideru.masteryofmagic.goldification.client.GoldificationClientState;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={MultiPlayerGameMode.class})
public abstract class GoldificationMultiPlayerGameModeMixin {
    @Redirect(method={"continueDestroyBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Z"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/sounds/SoundManager;play(Lnet/minecraft/client/resources/sounds/SoundInstance;)V"), remap=false, require=0)
    private void replaceMiningSoundDev(SoundManager manager, SoundInstance original, BlockPos position, Direction direction) {
        GoldificationMultiPlayerGameModeMixin.playEffectiveMiningSound(manager, original, position);
    }

    @Redirect(method={"m_105283_(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Z"}, at=@At(value="INVOKE", target="Lnet/minecraft/client/sounds/SoundManager;m_120367_(Lnet/minecraft/client/resources/sounds/SoundInstance;)V"), remap=false, require=0)
    private void replaceMiningSoundProduction(SoundManager manager, SoundInstance original, BlockPos position, Direction direction) {
        GoldificationMultiPlayerGameModeMixin.playEffectiveMiningSound(manager, original, position);
    }

    private static void playEffectiveMiningSound(SoundManager manager, SoundInstance original, BlockPos position) {
        if (!GoldificationClientState.isBlockGoldifiedForSound(position)) {
            manager.m_120367_(original);
            return;
        }
        manager.m_120367_((SoundInstance)new SimpleSoundInstance(SoundEvents.f_12064_, SoundSource.BLOCKS, 0.25f, 0.72f, SoundInstance.m_235150_(), position));
    }
}

