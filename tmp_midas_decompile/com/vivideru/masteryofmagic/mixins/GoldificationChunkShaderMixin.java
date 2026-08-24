/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.RenderType
 *  org.joml.Matrix4f
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.vivideru.masteryofmagic.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import com.vivideru.masteryofmagic.goldification.client.GoldificationShaders;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={LevelRenderer.class}, priority=1100)
public abstract class GoldificationChunkShaderMixin {
    @Inject(method={"renderChunkLayer(Lnet/minecraft/client/renderer/RenderType;Lcom/mojang/blaze3d/vertex/PoseStack;DDDLorg/joml/Matrix4f;)V"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/RenderType;setupRenderState()V", shift=At.Shift.AFTER)}, remap=false, require=0)
    private void useGoldificationShaderDev(RenderType renderType, PoseStack poseStack, double cameraX, double cameraY, double cameraZ, Matrix4f projectionMatrix, CallbackInfo callbackInfo) {
        GoldificationShaders.useForChunkLayer(renderType);
    }

    @Inject(method={"m_172993_(Lnet/minecraft/client/renderer/RenderType;Lcom/mojang/blaze3d/vertex/PoseStack;DDDLorg/joml/Matrix4f;)V"}, at={@At(value="INVOKE", target="Lnet/minecraft/client/renderer/RenderType;m_110185_()V", shift=At.Shift.AFTER)}, remap=false, require=0)
    private void useGoldificationShaderProduction(RenderType renderType, PoseStack poseStack, double cameraX, double cameraY, double cameraZ, Matrix4f projectionMatrix, CallbackInfo callbackInfo) {
        GoldificationShaders.useForChunkLayer(renderType);
    }
}

