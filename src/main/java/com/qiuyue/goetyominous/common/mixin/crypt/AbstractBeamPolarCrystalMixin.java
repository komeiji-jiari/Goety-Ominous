package com.qiuyue.goetyominous.common.mixin.crypt;

import com.Polarice3.Goety.common.entities.projectiles.AbstractBeam;
import com.qiuyue.goetyominous.common.blocks.PolarCrystalBlock;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBeam.class)
public abstract class AbstractBeamPolarCrystalMixin {

    @Inject(method = "beamTraceResult", at = @At("RETURN"), remap = false, require = 1)
    private void goetyominous$togglePolarCrystal(double distance, float ticks, boolean passesWater,
                                                 CallbackInfoReturnable<HitResult> cir) {
        AbstractBeam beam = (AbstractBeam) (Object) this;
        if (cir.getReturnValue() instanceof BlockHitResult hit) {
            PolarCrystalBlock.hit(beam.level(), hit.getBlockPos());
        }
    }
}
