package com.qiuyue.goetyominous.common.mixin.crypt;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.qiuyue.goetyominous.common.worldgen.crypt.CryptPlacementRules;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(targets = "net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement$Placer", priority = 1100)
public abstract class CryptPlacerMixin {

    @Shadow
    @Final
    private Registry<StructureTemplatePool> pools;

    @Shadow
    @Final
    private StructureTemplateManager structureTemplateManager;

    @Inject(method = "tryPlacingChildren", at = @At("HEAD"), require = 1)
    private void goetyominous$beginParent(PoolElementStructurePiece parent, MutableObject<VoxelShape> free, int depth,
                                          boolean expansionHack, LevelHeightAccessor heightAccessor, RandomState randomState,
                                          CallbackInfo ci) {
        CryptPlacementRules.beginParent(parent, depth);
    }

    @ModifyExpressionValue(method = "tryPlacingChildren", require = 1,
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/JigsawBlock;getFrontFacing(Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/core/Direction;",
                    ordinal = 0))
    private Direction goetyominous$captureParentDirection(Direction direction) {
        CryptPlacementRules.parentDirection(direction);
        return direction;
    }

    @WrapOperation(method = "tryPlacingChildren", require = 1,
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/level/levelgen/structure/pools/StructureTemplatePool;getShuffledTemplates(Lnet/minecraft/util/RandomSource;)Ljava/util/List;"))
    private List<StructurePoolElement> goetyominous$reweightCandidates(StructureTemplatePool pool, RandomSource random,
                                                                       Operation<List<StructurePoolElement>> original) {
        return CryptPlacementRules.reweight(original.call(pool, random));
    }

    @WrapOperation(method = "tryPlacingChildren", require = 1,
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/level/levelgen/structure/pools/StructurePoolElement;getBoundingBox(Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplateManager;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Rotation;)Lnet/minecraft/world/level/levelgen/structure/BoundingBox;",
                    ordinal = 1))
    private BoundingBox goetyominous$captureCandidate(StructurePoolElement element, StructureTemplateManager templateManager,
                                                      BlockPos pos, Rotation rotation, Operation<BoundingBox> original) {
        CryptPlacementRules.candidate(element, this.structureTemplateManager);
        return original.call(element, templateManager, pos, rotation);
    }

    @ModifyExpressionValue(method = "tryPlacingChildren", require = 1,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/AABB;deflate(D)Lnet/minecraft/world/phys/AABB;"))
    private AABB goetyominous$captureCandidateBounds(AABB bounds) {
        CryptPlacementRules.candidateBounds(bounds);
        return bounds;
    }

    @WrapOperation(method = "tryPlacingChildren", require = 1,
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/phys/shapes/Shapes;joinIsNotEmpty(Lnet/minecraft/world/phys/shapes/VoxelShape;Lnet/minecraft/world/phys/shapes/VoxelShape;Lnet/minecraft/world/phys/shapes/BooleanOp;)Z"))
    private boolean goetyominous$collisionTest(VoxelShape free, VoxelShape candidate, BooleanOp op,
                                               Operation<Boolean> original) {
        return CryptPlacementRules.rejectOrCollides(original.call(free, candidate, op));
    }
}
