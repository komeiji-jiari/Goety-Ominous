package com.qiuyue.goetyominous.common.mixin.trial;

import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TerrainAdjustment.class)
public abstract class TerrainAdjustmentMixin {

    @Unique
    private static TerrainAdjustment goetyominous$encapsulate;

    @Invoker("<init>")
    private static TerrainAdjustment goetyominous$newConstant(String name, int ordinal, String serializedName) {
        throw new AssertionError();
    }

    @Inject(method = "values", at = @At("HEAD"), cancellable = true, require = 1)
    private static void goetyominous$values(CallbackInfoReturnable<TerrainAdjustment[]> cir) {
        TerrainAdjustment encapsulate = goetyominous$encapsulate;
        if (encapsulate == null) {
            encapsulate = goetyominous$encapsulate = goetyominous$newConstant("ENCAPSULATE", 4, "encapsulate");
        }
        cir.setReturnValue(new TerrainAdjustment[]{
                TerrainAdjustment.NONE, TerrainAdjustment.BURY, TerrainAdjustment.BEARD_THIN,
                TerrainAdjustment.BEARD_BOX, encapsulate});
    }
}
