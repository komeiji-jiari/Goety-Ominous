package com.qiuyue.goetyominous.common.mixin.crypt;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.qiuyue.goetyominous.common.worldgen.crypt.CryptCleanup;
import com.qiuyue.goetyominous.common.worldgen.crypt.CryptExclusion;
import com.qiuyue.goetyominous.common.worldgen.crypt.CryptMound;
import com.qiuyue.goetyominous.common.worldgen.crypt.CryptPlans;
import com.qiuyue.goetyominous.common.worldgen.crypt.CryptShell;
import com.qiuyue.goetyominous.common.worldgen.crypt.CryptTerrain;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkGenerator.class)
public abstract class CryptChunkGeneratorMixin {

    @Inject(method = "applyBiomeDecoration", at = @At("HEAD"), require = 1)
    private void goetyominous$cryptTerrainHead(WorldGenLevel level, ChunkAccess chunk, StructureManager structureManager,
                                               CallbackInfo ci) {
        if (structureManager.shouldGenerateStructures()) {
            CryptShell.apply(level, chunk, structureManager);
            CryptMound.remember(chunk);
            CryptExclusion.begin(level, chunk, structureManager);
        }
    }

    @WrapOperation(method = "applyBiomeDecoration", require = 1,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/WorldgenRandom;setFeatureSeed(JII)V"))
    private void goetyominous$growMoundBeforeVegetation(WorldgenRandom random, long decorationSeed, int index, int step,
                                                        Operation<Void> original,
                                                        @Local(argsOnly = true) WorldGenLevel level,
                                                        @Local(argsOnly = true) ChunkAccess chunk,
                                                        @Local(argsOnly = true) StructureManager structureManager) {
        if (step >= GenerationStep.Decoration.VEGETAL_DECORATION.ordinal() && structureManager.shouldGenerateStructures()) {
            CryptMound.growOnce(level, chunk, structureManager);
        }
        original.call(random, decorationSeed, index, step);
    }

    @Inject(method = "applyBiomeDecoration", at = @At("TAIL"), require = 1)
    private void goetyominous$cryptTerrainTail(WorldGenLevel level, ChunkAccess chunk, StructureManager structureManager,
                                               CallbackInfo ci) {
        if (structureManager.shouldGenerateStructures()) {
            CryptCleanup.apply(level, chunk, structureManager);
            CryptMound.growOnce(level, chunk, structureManager);
        }
        CryptExclusion.end();
    }

    @Inject(method = "createStructures", at = @At("HEAD"), require = 1)
    private void goetyominous$enterStructureState(RegistryAccess registryAccess, ChunkGeneratorStructureState state,
                                                  StructureManager structureManager, ChunkAccess chunk,
                                                  StructureTemplateManager templates, CallbackInfo ci) {
        CryptTerrain.enterStructureState(state);
    }

    @Inject(method = "createStructures", at = @At("RETURN"), require = 1)
    private void goetyominous$exitStructureState(RegistryAccess registryAccess, ChunkGeneratorStructureState state,
                                                 StructureManager structureManager, ChunkAccess chunk,
                                                 StructureTemplateManager templates, CallbackInfo ci) {
        CryptTerrain.exitStructureState();
    }

    @Inject(method = "tryGenerateStructure", at = @At("HEAD"), cancellable = true, require = 1)
    private void goetyominous$reserveGround(StructureSet.StructureSelectionEntry entry, StructureManager structureManager,
                                            RegistryAccess registryAccess, RandomState randomState,
                                            StructureTemplateManager templates, long seed, ChunkAccess chunk,
                                            ChunkPos chunkPos, SectionPos sectionPos,
                                            CallbackInfoReturnable<Boolean> cir) {
        ChunkGenerator generator = (ChunkGenerator) (Object) this;
        if (CryptPlans.reserves(entry.structure().value(), registryAccess, generator, randomState, templates, seed,
                chunk, chunkPos)) {
            cir.setReturnValue(false);
        }
    }
}
