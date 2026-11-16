package com.qiuyue.goetyominous.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.qiuyue.goetyominous.GoetyOminous;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid = GoetyOminous.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class SoulMoonShaders {

    public static final ResourceLocation MOON_TEXTURE =
            new ResourceLocation(GoetyOminous.MOD_ID, "textures/sky/soul_moon_atlas.png");

    @Nullable private static ShaderInstance halo;

    private SoulMoonShaders() {
    }

    @SubscribeEvent
    public static void onRegisterShaders(RegisterShadersEvent event) {
        try {
            event.registerShader(new ShaderInstance(event.getResourceProvider(),
                    new ResourceLocation(GoetyOminous.MOD_ID, "soul_moon_halo"),
                    DefaultVertexFormat.POSITION_TEX), s -> halo = s);
        } catch (Exception ignored) {

        }
    }

    @Nullable public static ShaderInstance halo() {
        return halo;
    }
}
