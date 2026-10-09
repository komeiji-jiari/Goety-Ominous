package com.qiuyue.goetyominous.common.mixin.ac;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GameRenderer.class)
public interface GameRendererAccessor {

    @Accessor("rendertypeTextShader")
    static ShaderInstance getRlyehRendertypeTextShader() {
        throw new AssertionError();
    }

    @Accessor("rendertypeTextShader")
    static void setRlyehRendertypeTextShader(ShaderInstance shader) {
        throw new AssertionError();
    }
}
