package com.qiuyue.goetyominous.common.mixin.trial;

import com.qiuyue.goetyominous.common.worldgen.jigsaw.EncapsulatingBeardifier;
import com.qiuyue.goetyominous.common.worldgen.jigsaw.TrialJigsawStructure;
import java.util.List;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.levelgen.Beardifier;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Beardifier.class)
public abstract class BeardifierEncapsulateMixin {

    @Inject(method = "forStructuresInChunk", at = @At("RETURN"), cancellable = true, require = 1)
    private static void goetyominous$encapsulateTrialStructures(StructureManager structures, ChunkPos chunk,
                                                                CallbackInfoReturnable<Beardifier> cir) {
        List<StructureStart> trial = structures.startsForStructure(chunk,
                structure -> structure instanceof TrialJigsawStructure trialStructure && trialStructure.encapsulates());
        if (!trial.isEmpty()) {
            cir.setReturnValue(EncapsulatingBeardifier.wrap(cir.getReturnValue(), trial, chunk));
        }
    }
}
