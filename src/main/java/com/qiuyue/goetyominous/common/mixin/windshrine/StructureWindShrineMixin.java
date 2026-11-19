package com.qiuyue.goetyominous.common.mixin.windshrine;

import com.qiuyue.goetyominous.common.worldgen.windshrine.WindShrineJigsawAccess;
import com.qiuyue.goetyominous.common.worldgen.windshrine.WindShrinePlans;
import com.qiuyue.goetyominous.config.WorldGenConfig;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Structure.class)
public abstract class StructureWindShrineMixin {
    @Inject(method = "terrainAdaptation", at = @At("RETURN"), cancellable = true, require = 1)
    private void goetyominous$disableWindShrineBeard(CallbackInfoReturnable<TerrainAdjustment> cir) {
        if (!((Object) this instanceof WindShrineJigsawAccess jigsaw)) {
            return;
        }
        if (WorldGenConfig.classicWindShrine()) {
            return;
        }
        if (jigsaw.goetyominous$startPool().unwrapKey()
                .map(key -> key.location().equals(WindShrinePlans.CLASSIC_POOL)).orElse(false)) {
            cir.setReturnValue(TerrainAdjustment.NONE);
        }
    }
}
