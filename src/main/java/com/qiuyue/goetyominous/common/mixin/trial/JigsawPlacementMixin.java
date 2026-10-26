package com.qiuyue.goetyominous.common.mixin.trial;

import com.qiuyue.goetyominous.common.worldgen.alias.PoolAliasBinding;
import com.qiuyue.goetyominous.common.worldgen.alias.PoolAliasContext;
import com.qiuyue.goetyominous.common.worldgen.alias.PoolAliasLookup;
import com.qiuyue.goetyominous.common.worldgen.alias.PoolAliasReloadListener;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(JigsawPlacement.class)
public abstract class JigsawPlacementMixin {

    @Inject(method = "addPieces(Lnet/minecraft/world/level/levelgen/structure/Structure$GenerationContext;Lnet/minecraft/core/Holder;Ljava/util/Optional;ILnet/minecraft/core/BlockPos;ZLjava/util/Optional;I)Ljava/util/Optional;",
            at = @At("RETURN"), cancellable = true, require = 1)
    private static void goetyominous$wrapWithPoolAliases(Structure.GenerationContext context,
                                                         Holder<StructureTemplatePool> startPool,
                                                         Optional<ResourceLocation> startJigsawName, int maxDepth, BlockPos pos,
                                                         boolean useExpansionHack, Optional<Heightmap.Types> projectStartToHeightmap,
                                                         int maxDistanceFromCenter,
                                                         CallbackInfoReturnable<Optional<Structure.GenerationStub>> cir) {
        Optional<Structure.GenerationStub> stub = cir.getReturnValue();
        if (stub == null) {
            return;
        }
        ResourceLocation startPoolId = startPool.unwrapKey().map(ResourceKey::location).orElse(null);
        List<PoolAliasBinding> bindings = PoolAliasReloadListener.forStartPool(startPoolId);
        if (bindings.isEmpty()) {
            return;
        }
        PoolAliasLookup lookup = PoolAliasLookup.create(bindings, pos, context.seed());
        cir.setReturnValue(stub.map(s -> s.generator().left()
                .map(original -> {
                    Consumer<StructurePiecesBuilder> wrapped =
                            builder -> PoolAliasContext.run(lookup, () -> original.accept(builder));
                    return new Structure.GenerationStub(s.position(), wrapped);
                })
                .orElse(s)));
    }
}
