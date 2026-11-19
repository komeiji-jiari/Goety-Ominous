package com.qiuyue.goetyominous.common.mixin.windshrine;

import com.mojang.datafixers.util.Either;
import com.qiuyue.goetyominous.common.worldgen.windshrine.WindShrineTerrain;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

@Mixin(ChunkStatus.class)
public abstract class ChunkStatusWindShrineMixin {
    @Inject(method = "initializeLight", at = @At("HEAD"), require = 1)
    private static void goetyominous$windShrineCleanup(ThreadedLevelLightEngine lightEngine, ChunkAccess chunk,
                                                       CallbackInfoReturnable<CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> cir) {
        WindShrineTerrain.cleanup(chunk);
    }
}
