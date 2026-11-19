package com.qiuyue.goetyominous.common.mixin.windshrine;

import com.qiuyue.goetyominous.config.WorldGenConfig;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(JigsawStructure.class)
public abstract class JigsawStructureWindShrineMixin {
    @Inject(method = "findGenerationPoint", at = @At("HEAD"), cancellable = true, require = 1)
    private void goetyominous$disableBaseWindShrine(Structure.GenerationContext context, CallbackInfoReturnable<Optional<Structure.GenerationStub>> cir) {
        if (WorldGenConfig.classicWindShrine()) {
            return;
        }
        Registry<Structure> structures = context.registryAccess().registryOrThrow(Registries.STRUCTURE);
        Optional<ResourceKey<Structure>> key = structures.getResourceKey((Structure) (Object) this);
        if (key.isPresent() && key.get().location().equals(new ResourceLocation("goety", "wind_shrine"))) {
            cir.setReturnValue(Optional.empty());
        }
    }
}
