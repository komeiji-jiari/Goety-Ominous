package com.qiuyue.goetyominous.common.mixin.trial;

import com.mojang.datafixers.util.Either;
import com.qiuyue.goetyominous.GoetyOminous;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SinglePoolElement.class)
public abstract class SinglePoolElementLiquidMixin {

    @Shadow
    @Final
    protected Either<ResourceLocation, StructureTemplate> template;

    @Inject(method = "getSettings", at = @At("RETURN"), require = 1)
    private void goetyominous$ignoreWaterlogging(Rotation rotation, BoundingBox box, boolean keepJigsaws,
                                                 CallbackInfoReturnable<StructurePlaceSettings> cir) {
        boolean trial = this.template.left()
                .map(id -> id.getNamespace().equals(GoetyOminous.MOD_ID) && id.getPath().startsWith("trial_chambers/"))
                .orElse(false);
        if (trial) {
            cir.getReturnValue().setKeepLiquids(false);
        }
    }
}
