package com.qiuyue.goetyominous.common.mixin.trial;

import com.qiuyue.goetyominous.common.worldgen.protect.StructureProtection;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlacedFeature.class)
public abstract class PlacedFeatureProtectionMixin {

    @Inject(method = "placeWithBiomeCheck", at = @At("HEAD"), require = 1)
    private void goetyominous$resetFeatureDepth(WorldGenLevel level, ChunkGenerator generator, RandomSource random,
                                                BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        StructureProtection.resetFeatureDepth();
    }

    @Inject(method = "placeWithContext", at = @At("HEAD"), require = 1)
    private void goetyominous$beginFeature(PlacementContext context, RandomSource random, BlockPos pos,
                                           CallbackInfoReturnable<Boolean> cir) {
        StructureProtection.beginFeature();
    }

    @Inject(method = "placeWithContext", at = @At("RETURN"), require = 1)
    private void goetyominous$endFeature(PlacementContext context, RandomSource random, BlockPos pos,
                                         CallbackInfoReturnable<Boolean> cir) {
        StructureProtection.endFeature();
    }
}
