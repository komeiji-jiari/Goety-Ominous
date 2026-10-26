package com.qiuyue.goetyominous.common.mixin.trial;

import com.mojang.serialization.DataResult;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(JigsawStructure.class)
public abstract class JigsawStructureMixin {

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
}
