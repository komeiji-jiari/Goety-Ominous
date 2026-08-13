package com.qiuyue.goetyominous.common.mixin;

import com.Polarice3.Goety.client.render.BlackBeastRenderer;
import com.qiuyue.goetyominous.client.render.layer.CursedBlackBeastArmorLayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlackBeastRenderer.class)
public class MixinBlackBeastRenderer {

    @Inject(method = "<init>", at = @At("TAIL"), remap = false)
    private void goetyominous$addArmorLayer(EntityRendererProvider.Context context, CallbackInfo ci) {
        BlackBeastRenderer renderer = (BlackBeastRenderer) (Object) this;
        renderer.addLayer(new CursedBlackBeastArmorLayer<>(renderer, context.getModelSet()));
    }
}
