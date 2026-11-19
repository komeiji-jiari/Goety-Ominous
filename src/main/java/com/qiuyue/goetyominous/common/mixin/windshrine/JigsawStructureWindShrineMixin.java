package com.qiuyue.goetyominous.common.mixin.windshrine;

import com.qiuyue.goetyominous.common.worldgen.windshrine.WindShrineJigsawAccess;
import com.qiuyue.goetyominous.common.worldgen.windshrine.WindShrinePlans;
import com.qiuyue.goetyominous.common.worldgen.windshrine.WindShrineStructure;
import com.qiuyue.goetyominous.config.WorldGenConfig;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(JigsawStructure.class)
public abstract class JigsawStructureWindShrineMixin implements WindShrineJigsawAccess {
    @Shadow
    @Final
    private Holder<StructureTemplatePool> startPool;

    @Override
    public Holder<StructureTemplatePool> goetyominous$startPool() {
        return this.startPool;
    }

    @Inject(method = "findGenerationPoint", at = @At("HEAD"), cancellable = true, require = 1)
    private void goetyominous$delegateBaseWindShrine(Structure.GenerationContext context, CallbackInfoReturnable<Optional<Structure.GenerationStub>> cir) {
        if (WorldGenConfig.classicWindShrine()) {
            return;
        }
        Registry<Structure> structures = context.registryAccess().registryOrThrow(Registries.STRUCTURE);
        Optional<ResourceKey<Structure>> key = structures.getResourceKey((Structure) (Object) this);
        if (key.isPresent() && key.get().location().equals(WindShrinePlans.MAIN_ID)) {
            Structure shrine = structures.get(WindShrinePlans.ID);
            if (shrine instanceof WindShrineStructure windShrine) {
                cir.setReturnValue(windShrine.findGenerationPoint(context));
            } else {
                cir.setReturnValue(Optional.empty());
            }
        }
    }
}
