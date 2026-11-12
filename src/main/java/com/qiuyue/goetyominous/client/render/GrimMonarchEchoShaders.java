package com.qiuyue.goetyominous.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.qiuyue.goetyominous.GoetyOminous;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class GrimMonarchEchoShaders {
    private static ShaderInstance body;
    private static ShaderInstance glow;
    private static float dissolve;

    private GrimMonarchEchoShaders() {
    }

    public static void setDissolve(float value) {
        dissolve = Mth.clamp(value, 0.0F, 1.0F);
    }

    static ShaderInstance body() {
        return body;
    }

    static ShaderInstance glow() {
        return glow;
    }

    static float dissolve() {
        return dissolve;
    }

    @SubscribeEvent
    public static void onRegisterShaders(RegisterShadersEvent event) throws IOException {
        event.registerShader(new ShaderInstance(event.getResourceProvider(),
                        new ResourceLocation(GoetyOminous.MOD_ID, "grimmonarch_echo_body"),
                        DefaultVertexFormat.NEW_ENTITY),
                shader -> body = shader);
        event.registerShader(new ShaderInstance(event.getResourceProvider(),
                        new ResourceLocation(GoetyOminous.MOD_ID, "grimmonarch_echo_glow"),
                        DefaultVertexFormat.NEW_ENTITY),
                shader -> glow = shader);
    }
}
