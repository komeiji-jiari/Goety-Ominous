package com.qiuyue.goetyominous.common.mixin;

import com.qiuyue.goetyominous.client.render.layer.CursedWolfArmorLayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.WolfRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WolfRenderer.class)
public class MixinWolfRenderer {

    @Inject(method = "<init>", at = @At("TAIL"), remap = true)
    private void goetyominous$addArmorLayer(EntityRendererProvider.Context context, CallbackInfo ci) {
        WolfRenderer renderer = (WolfRenderer) (Object) this;
        renderer.addLayer(new CursedWolfArmorLayer(renderer, context.getModelSet()));
    }
}
