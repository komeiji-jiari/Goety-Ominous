package com.qiuyue.goetyominous.common.mixin.crypt;

import com.Polarice3.Goety.common.world.structures.CryptStructure;
import com.qiuyue.goetyominous.common.worldgen.crypt.CryptGeneration;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(CryptStructure.class)
public abstract class CryptStructureMixin {

    @Inject(method = "findGenerationPoint", at = @At("HEAD"), cancellable = true, require = 1)
    private void goetyominous$replaceCrypt(Structure.GenerationContext context,
                                           CallbackInfoReturnable<Optional<Structure.GenerationStub>> cir) {
        if (!CryptGeneration.replacesClassic(context)) {
            return;
        }
        Structure classicCrypt = context.registryAccess().registryOrThrow(Registries.STRUCTURE)
                .get(new ResourceLocation("goety", "crypt"));
        if (classicCrypt == (Structure) (Object) this) {
            cir.setReturnValue(CryptGeneration.findGenerationPoint(context));
        }
    }
}
