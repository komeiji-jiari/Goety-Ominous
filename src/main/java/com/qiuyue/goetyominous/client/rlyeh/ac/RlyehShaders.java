package com.qiuyue.goetyominous.client.rlyeh.ac;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.qiuyue.goetyominous.GoetyOminous;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.function.Consumer;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID, value = Dist.CLIENT,
        bus = Mod.EventBusSubscriber.Bus.MOD)
public final class RlyehShaders {

    public static final float RLYEH_THEME = 3.0F;

    @Nullable private static ShaderInstance tooltip;
    @Nullable private static ShaderInstance frame;
    @Nullable private static ShaderInstance textGlyph;

    private RlyehShaders() {
    }

    @SubscribeEvent
    public static void onRegisterShaders(RegisterShadersEvent event) {
        register(event, "rlyeh_tooltip", DefaultVertexFormat.POSITION_COLOR_TEX, s -> tooltip = s);
        register(event, "rlyeh_frame", DefaultVertexFormat.POSITION_COLOR_TEX, s -> frame = s);
        register(event, "rlyeh_text_glyph", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, s -> textGlyph = s);
    }

    private static void register(RegisterShadersEvent event, String name, VertexFormat format,
                                 Consumer<ShaderInstance> sink) {
        try {
            event.registerShader(new ShaderInstance(event.getResourceProvider(),
                    new ResourceLocation(GoetyOminous.MOD_ID, name), format), sink);
        } catch (Exception ignored) {

        }
    }

    @Nullable public static ShaderInstance tooltip() {
        return tooltip;
    }

    @Nullable public static ShaderInstance frame() {
        return frame;
    }

    @Nullable public static ShaderInstance textGlyph() {
        return textGlyph;
    }
}
