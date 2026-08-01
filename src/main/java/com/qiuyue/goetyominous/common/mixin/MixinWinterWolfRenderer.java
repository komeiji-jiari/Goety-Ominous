package com.qiuyue.goetyominous.common.mixin;

import com.Polarice3.Goety.client.render.WinterWolfRenderer;
import com.qiuyue.goetyominous.client.render.layer.CursedBlackWolfArmorLayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WinterWolfRenderer.class)
public class MixinWinterWolfRenderer {

    @Inject(method = "<init>", at = @At("TAIL"), remap = false)
    private void goetyominous$addArmorLayer(EntityRendererProvider.Context context, CallbackInfo ci) {
        WinterWolfRenderer renderer = (WinterWolfRenderer) (Object) this;
        renderer.addLayer(new CursedBlackWolfArmorLayer<>(renderer, context.getModelSet()));
    }
}
