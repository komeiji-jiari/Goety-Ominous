package com.qiuyue.goetyominous.common.mixin.trial;

import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.Beardifier;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pools.JigsawJunction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Beardifier.class)
public abstract class BeardifierMixin {

    @Shadow @Final private ObjectListIterator<Beardifier.Rigid> pieceIterator;
    @Shadow @Final private ObjectListIterator<JigsawJunction> junctionIterator;

    @Shadow
    private static double getBeardContribution(int dx, int dy, int dz, int belowTop) { throw new AssertionError(); }

    @Inject(method = "compute(Lnet/minecraft/world/level/levelgen/DensityFunction$FunctionContext;)D",
            at = @At("HEAD"), cancellable = true, require = 1)
    private void goetyominous$compute(DensityFunction.FunctionContext context, CallbackInfoReturnable<Double> cir) {
        boolean encapsulate = false;
        while (this.pieceIterator.hasNext() && !encapsulate) {
            switch (this.pieceIterator.next().terrainAdjustment()) {
                case NONE, BURY, BEARD_THIN, BEARD_BOX -> { }
                default -> encapsulate = true;
            }
        }
        this.pieceIterator.back(Integer.MAX_VALUE);
        if (encapsulate) {
            cir.setReturnValue(this.goetyominous$computeEncapsulate(context));
        }
    }

    @Unique
    private double goetyominous$computeEncapsulate(DensityFunction.FunctionContext context) {
        int x = context.blockX();
        int y = context.blockY();
        int z = context.blockZ();
        double density = 0.0D;
        while (this.pieceIterator.hasNext()) {
            Beardifier.Rigid rigid = this.pieceIterator.next();
            BoundingBox box = rigid.box();
            int delta = rigid.groundLevelDelta();
            int dx = Math.max(0, Math.max(box.minX() - x, x - box.maxX()));
            int dz = Math.max(0, Math.max(box.minZ() - z, z - box.maxZ()));
            int minY = box.minY() + delta;
            int belowTop = y - minY;
            switch (rigid.terrainAdjustment()) {
                case NONE -> { }
                case BURY -> density += goetyominous$bury(dx, (double) belowTop / 2.0D, dz);
                case BEARD_THIN -> density += getBeardContribution(dx, belowTop, dz, belowTop) * 0.8D;
                case BEARD_BOX -> density += getBeardContribution(dx,
                        Math.max(0, Math.max(minY - y, y - box.maxY())), dz, belowTop) * 0.8D;
                default -> {
                    int outside = Math.max(0, Math.max(box.minY() - y, y - box.maxY()));
                    density += goetyominous$bury(dx / 2.0D, (double) outside / 2.0D, dz / 2.0D) * 0.8D;
                }
            }
        }
        this.pieceIterator.back(Integer.MAX_VALUE);
        while (this.junctionIterator.hasNext()) {
            JigsawJunction junction = this.junctionIterator.next();
            int jx = x - junction.getSourceX();
            int jy = y - junction.getSourceGroundY();
            int jz = z - junction.getSourceZ();
            density += getBeardContribution(jx, jy, jz, jy) * 0.4D;
        }
        this.junctionIterator.back(Integer.MAX_VALUE);
        return density;
    }

    @Unique
    private static double goetyominous$bury(double dx, double dy, double dz) {
        return Mth.clampedMap(Mth.length(dx, dy, dz), 0.0D, 6.0D, 1.0D, 0.0D);
    }
}
