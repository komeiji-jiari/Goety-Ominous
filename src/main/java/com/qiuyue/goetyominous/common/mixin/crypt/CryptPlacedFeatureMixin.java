package com.qiuyue.goetyominous.common.mixin.crypt;

import com.qiuyue.goetyominous.common.worldgen.crypt.CryptExclusion;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.DripstoneClusterFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FossilFeature;
import net.minecraft.world.level.levelgen.feature.GeodeFeature;
import net.minecraft.world.level.levelgen.feature.LargeDripstoneFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ConfiguredFeature.class)
public abstract class CryptPlacedFeatureMixin {

    @Inject(method = "place", at = @At("HEAD"), cancellable = true, require = 1)
    private void goetyominous$keepOutOfCrypt(WorldGenLevel level, ChunkGenerator generator, RandomSource random,
                                             BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        Feature<?> feature = ((ConfiguredFeature<?, ?>) (Object) this).feature();
        int reach = feature instanceof GeodeFeature || feature instanceof LargeDripstoneFeature
                || feature instanceof DripstoneClusterFeature || feature instanceof FossilFeature
                ? CryptExclusion.WIDE_REACH : 0;
        if (CryptExclusion.excludes(pos, reach)) {
            cir.setReturnValue(false);
        }
    }
}
