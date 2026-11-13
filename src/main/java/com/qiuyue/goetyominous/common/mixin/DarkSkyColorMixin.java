package com.qiuyue.goetyominous.common.mixin;

import com.qiuyue.goetyominous.client.render.DarkSceneRenderer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientLevel.class)
public abstract class DarkSkyColorMixin {

    @Inject(method = "getSkyColor", at = @At("RETURN"), cancellable = true, require = 1)
    private void goetyominous$darkSkyColor(Vec3 pos, float partialTick, CallbackInfoReturnable<Vec3> cir) {
        if (!DarkSceneRenderer.active()) {
            return;
        }
        cir.setReturnValue(DarkSceneRenderer.skyColor());
    }
}
