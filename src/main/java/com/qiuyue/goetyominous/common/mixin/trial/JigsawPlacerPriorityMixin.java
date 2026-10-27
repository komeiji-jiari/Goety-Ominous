package com.qiuyue.goetyominous.common.mixin.trial;

import com.qiuyue.goetyominous.common.worldgen.jigsaw.PlacementPriorityQueue;
import com.qiuyue.goetyominous.common.worldgen.jigsaw.TrialDimensionPadding;
import com.qiuyue.goetyominous.common.worldgen.jigsaw.TrialJigsawContext;
import java.util.Deque;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement$Placer")
public abstract class JigsawPlacerPriorityMixin {

    @Shadow
    @Final
    @Mutable
    Deque<Object> placing;

    @Unique
    private int goetyominous$parentPlacementPriority;

    @Inject(method = "<init>", at = @At("RETURN"), require = 1)
    private void goetyominous$usePriorityQueue(CallbackInfo ci) {
        if (TrialJigsawContext.prioritiesActive()) {
            this.placing = new PlacementPriorityQueue<>(() -> this.goetyominous$parentPlacementPriority);
        }
    }

    @Inject(method = "tryPlacingChildren", at = @At("HEAD"), require = 1)
    private void goetyominous$applyDimensionPadding(PoolElementStructurePiece piece, MutableObject<VoxelShape> free,
                                                    int depth, boolean useExpansionHack, LevelHeightAccessor level,
                                                    RandomState randomState, CallbackInfo ci) {
        if (depth != 0 || !TrialJigsawContext.prioritiesActive()) {
            return;
        }
        TrialDimensionPadding padding = TrialJigsawContext.padding();
        VoxelShape shape = free.getValue();
        if (padding.isZero() || shape.isEmpty()) {
            return;
        }
        AABB bounds = shape.bounds();
        double minY = Math.max(bounds.minY, padding.minY(level));
        double maxY = Math.min(bounds.maxY, padding.maxYExclusive(level));
        free.setValue(minY >= maxY
                ? Shapes.empty()
                : Shapes.join(shape, Shapes.create(new AABB(bounds.minX, minY, bounds.minZ, bounds.maxX, maxY, bounds.maxZ)), BooleanOp.AND));
    }

    @ModifyArg(method = "tryPlacingChildren", index = 0, require = 1,
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/JigsawBlock;canAttach(Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate$StructureBlockInfo;Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate$StructureBlockInfo;)Z"))
    private StructureTemplate.StructureBlockInfo goetyominous$rememberParentConnector(StructureTemplate.StructureBlockInfo parent) {
        CompoundTag nbt = parent.nbt();
        this.goetyominous$parentPlacementPriority = nbt == null ? 0 : nbt.getInt("placement_priority");
        return parent;
    }
}
