package com.qiuyue.goetyominous.common.mixin.trial;

import com.mojang.serialization.DataResult;
import com.qiuyue.goetyominous.common.worldgen.alias.PoolAliasBinding;
import com.qiuyue.goetyominous.common.worldgen.alias.PoolAliasContext;
import com.qiuyue.goetyominous.common.worldgen.alias.PoolAliasLookup;
import com.qiuyue.goetyominous.common.worldgen.alias.PoolAliasReloadListener;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(JigsawStructure.class)
public abstract class JigsawStructureMixin {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger("goetyominous/pool_alias");
    private static final java.util.Set<ResourceLocation> REPORTED = java.util.concurrent.ConcurrentHashMap.newKeySet();

    @Inject(method = "verifyRange", at = @At("HEAD"), cancellable = true, require = 1)
    private static void goetyominous$verifyRange(JigsawStructure structure, CallbackInfoReturnable<DataResult<JigsawStructure>> cir) {
        TerrainAdjustment adaptation = structure.terrainAdaptation();
        if (adaptation == TerrainAdjustment.NONE || adaptation == TerrainAdjustment.BURY
                || adaptation == TerrainAdjustment.BEARD_THIN || adaptation == TerrainAdjustment.BEARD_BOX) {
            return;
        }
        int distance = ((JigsawStructureAccessor) (Object) structure).goetyominous$maxDistanceFromCenter();
        cir.setReturnValue(distance + 12 > 128
                ? DataResult.error(() -> "Structure size including terrain adaptation must not exceed 128")
                : DataResult.success(structure));
    }

    @ModifyArg(method = "lambda$static$7(Lcom/mojang/serialization/codecs/RecordCodecBuilder$Instance;)Lcom/mojang/datafixers/kinds/App;",
            at = @At(value = "INVOKE",
                    target = "Lcom/mojang/serialization/Codec;intRange(II)Lcom/mojang/serialization/Codec;"), index = 1, require = 1)
    private static int goetyominous$widenSize(int maxInclusive) {
        return maxInclusive == 7 ? 20 : maxInclusive;
    }

    @Redirect(method = "findGenerationPoint", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/levelgen/structure/pools/JigsawPlacement;addPieces(Lnet/minecraft/world/level/levelgen/structure/Structure$GenerationContext;Lnet/minecraft/core/Holder;Ljava/util/Optional;ILnet/minecraft/core/BlockPos;ZLjava/util/Optional;I)Ljava/util/Optional;"),
            require = 1)
    private Optional<Structure.GenerationStub> goetyominous$addPiecesWithExtras(Structure.GenerationContext context,
                                                                                Holder<StructureTemplatePool> startPool, Optional<ResourceLocation> startJigsawName, int maxDepth, BlockPos pos,
                                                                                boolean useExpansionHack, Optional<Heightmap.Types> projectStartToHeightmap, int maxDistanceFromCenter) {
        ResourceLocation startPoolId = startPool.unwrapKey().map(ResourceKey::location).orElse(null);
        List<PoolAliasBinding> bindings = PoolAliasReloadListener.forStartPool(startPoolId);
        if (REPORTED.add(startPoolId)) {
            LOGGER.info("goetyominous: findGenerationPoint start_pool={} → 别名 {} 条，目标示例 {}",
                    startPoolId, bindings.size(),
                    bindings.stream().flatMap(PoolAliasBinding::allTargets).limit(3).toList());
        }
        if (bindings.isEmpty()) {
            return JigsawPlacement.addPieces(context, startPool, startJigsawName, maxDepth, pos, useExpansionHack,
                    projectStartToHeightmap, maxDistanceFromCenter);
        }
        return PoolAliasContext.with(PoolAliasLookup.create(bindings, pos, context.seed()), () ->
                JigsawPlacement.addPieces(context, startPool, startJigsawName, maxDepth, pos, useExpansionHack,
                        projectStartToHeightmap, maxDistanceFromCenter));
    }
}
