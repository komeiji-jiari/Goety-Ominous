package com.qiuyue.goetyominous.common.mixin.windshrine;

import com.qiuyue.goetyominous.common.worldgen.windshrine.WindShrineTerrain;
import com.qiuyue.goetyominous.config.WorldGenConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Biome.class)
public abstract class BiomeWindShrineMixin {
    @Inject(method = "shouldSnow", at = @At("HEAD"), cancellable = true, require = 1)
    private void goetyominous$noSnowOnWindsweptPeaks(LevelReader level, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (WorldGenConfig.classicWindShrine()) {
            return;
        }
        ChunkAccess chunk = level.getChunk(pos.getX() >> 4, pos.getZ() >> 4, ChunkStatus.BIOMES, false);
        if (chunk == null) {
            return;
        }
        Holder<Biome> biome = chunk.getNoiseBiome(pos.getX() >> 2, pos.getY() >> 2, pos.getZ() >> 2);
        if (biome.is(WindShrineTerrain.WINDSWEPT_PEAKS_BIOME) || biome.is(Biomes.PLAINS)) {
            cir.setReturnValue(false);
        }
    }
}
