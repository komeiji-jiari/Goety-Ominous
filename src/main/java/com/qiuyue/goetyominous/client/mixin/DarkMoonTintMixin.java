package com.qiuyue.goetyominous.client.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.qiuyue.goetyominous.client.render.DarkSceneRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class DarkMoonTintMixin {

    private static final ResourceLocation GOETYOMINOUS$MOON =
            new ResourceLocation("textures/environment/moon_phases.png");

    @Redirect(method = "renderSky",
            at = @At(value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V"),
            require = 1)
    private void goetyominous$tintMoon(int slot, ResourceLocation texture) {
        RenderSystem.setShaderTexture(slot, texture);
        if (GOETYOMINOUS$MOON.equals(texture)) {
            DarkSceneRenderer.applyMoonTint();
        }
    }

    @Inject(method = "renderSky", at = @At("RETURN"))
    private void goetyominous$restoreShaderColor(CallbackInfo callback) {
        if (DarkSceneRenderer.moonTintActive()) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}
