package com.qiuyue.goetyominous.client.render.projectile;

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
public final class FourPointStarShaders {

    @Nullable private static ShaderInstance fourPointStar;

    private FourPointStarShaders() {
    }

    @SubscribeEvent
    public static void onRegisterShaders(RegisterShadersEvent event) {
        try {
            event.registerShader(new ShaderInstance(event.getResourceProvider(),
                    new ResourceLocation(GoetyOminous.MOD_ID, "four_point_star"),
                    DefaultVertexFormat.POSITION_COLOR_TEX), s -> fourPointStar = s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable public static ShaderInstance fourPointStar() {
        return fourPointStar;
    }
}
