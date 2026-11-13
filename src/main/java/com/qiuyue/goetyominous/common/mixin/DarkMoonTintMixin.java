package com.qiuyue.goetyominous.common.mixin;

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

    /**
     * 🔴 <b>把颜色还回去。</b>{@code setShaderColor} 改的是全局的 ColorModulator，它不会随着月亮画完
     * 而自动失效 —— 天空穹顶之后在同一帧里画的云、天气、粒子和实体只要没自己设颜色，就都跟着变青。
     * 原版走完 renderSky 时这个值就是白的，所以还原成白色等于回到原版状态。
     *
     * <p>只在压暗生效时还原：没生效时我们根本没动过它，也就不该替原版写一个值。
     */
    @Inject(method = "renderSky", at = @At("RETURN"))
    private void goetyominous$restoreShaderColor(CallbackInfo callback) {
        if (DarkSceneRenderer.active()) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}
