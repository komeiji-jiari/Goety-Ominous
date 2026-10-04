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
public final class WindChargeShaders {

    @Nullable private static ShaderInstance breezeWind;

    private WindChargeShaders() {
    }

    @SubscribeEvent
    public static void onRegisterShaders(RegisterShadersEvent event) {
        try {
            event.registerShader(new ShaderInstance(event.getResourceProvider(),
                    new ResourceLocation(GoetyOminous.MOD_ID, "rendertype_breeze_wind"),
                    DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP), s -> breezeWind = s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable public static ShaderInstance breezeWind() {
        return breezeWind;
    }
}
