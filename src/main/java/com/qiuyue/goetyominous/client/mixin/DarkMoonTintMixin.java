package com.qiuyue.goetyominous.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.qiuyue.goetyominous.client.render.DarkSceneRenderer;
import com.qiuyue.goetyominous.client.render.SoulMoonHaloRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class DarkMoonTintMixin {

    @WrapOperation(
            method = "renderSky",
            at = @At(value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/BufferUploader;drawWithShader"
                            + "(Lcom/mojang/blaze3d/vertex/BufferBuilder$RenderedBuffer;)V"),
            slice = @Slice(
                    from = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/LevelRenderer;"
                            + "MOON_LOCATION:Lnet/minecraft/resources/ResourceLocation;"),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;"
                            + "getStarBrightness(F)F")))
    private void goetyominous$hideVanillaMoon(BufferBuilder.RenderedBuffer buffer, Operation<Void> original) {
        if (DarkSceneRenderer.moonTintActive()) {
            buffer.release();
            return;
        }
        original.call(buffer);
    }

    @Inject(method = "renderSky(Lcom/mojang/blaze3d/vertex/PoseStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/Camera;ZLjava/lang/Runnable;)V",
            at = @At("RETURN"), require = 1)
    private void goetyominous$drawSoulMoonHalo(PoseStack poseStack, org.joml.Matrix4f matrix,
                                                float partialTick, net.minecraft.client.Camera camera,
                                                boolean bl, Runnable runnable, CallbackInfo callback) {
        if (DarkSceneRenderer.moonTintActive()) {
            SoulMoonHaloRenderer.draw(poseStack, partialTick);
        }
    }
}
