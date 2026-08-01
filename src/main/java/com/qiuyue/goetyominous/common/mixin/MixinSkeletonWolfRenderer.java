package com.qiuyue.goetyominous.common.mixin;

import com.Polarice3.Goety.client.render.SkeletonWolfRenderer;
import com.qiuyue.goetyominous.client.render.layer.CursedSkeletonWolfArmorLayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SkeletonWolfRenderer.class)
public class MixinSkeletonWolfRenderer {

    @Inject(method = "<init>", at = @At("TAIL"), remap = false)
    private void goetyominous$addArmorLayer(EntityRendererProvider.Context context, CallbackInfo ci) {
        SkeletonWolfRenderer renderer = (SkeletonWolfRenderer) (Object) this;
        renderer.addLayer(new CursedSkeletonWolfArmorLayer(renderer, context.getModelSet()));
    }
}