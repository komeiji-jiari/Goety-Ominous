/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.ShaderInstance
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.inventory.InventoryMenu
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.RegisterShadersEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 */
package com.vivideru.masteryofmagic.goldification.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="goety_mastery_of_magic", value={Dist.CLIENT}, bus=Mod.EventBusSubscriber.Bus.MOD)
public final class GoldificationShaders {
    private static final ResourceLocation GOLD_BLOCK_TEXTURE = new ResourceLocation("minecraft", "block/gold_block");
    private static ShaderInstance goldificationBlockShader;

    private GoldificationShaders() {
    }

    @SubscribeEvent
    public static void registerShaders(RegisterShadersEvent event) throws IOException {
        event.registerShader(new ShaderInstance(event.getResourceProvider(), new ResourceLocation("goety_mastery_of_magic", "goldification_block"), DefaultVertexFormat.f_85811_), shader -> {
            goldificationBlockShader = shader;
        });
    }

    public static void useForChunkLayer(RenderType renderType) {
        ShaderInstance shader = goldificationBlockShader;
        if (shader == null || !GoldificationShaders.isChunkLayer(renderType)) {
            return;
        }
        float alphaCutoff = 0.0f;
        if (renderType == RenderType.m_110463_()) {
            alphaCutoff = 0.1f;
        } else if (renderType == RenderType.m_110457_()) {
            alphaCutoff = 0.5f;
        }
        if (shader.m_173348_("AlphaCutoff") != null) {
            shader.m_173348_("AlphaCutoff").m_5985_(alphaCutoff);
        }
        if (shader.m_173348_("GoldSpriteBounds") != null) {
            TextureAtlasSprite goldSprite = (TextureAtlasSprite)Minecraft.m_91087_().m_91258_(InventoryMenu.f_39692_).apply(GOLD_BLOCK_TEXTURE);
            double halfPixelU = 8.0 / (double)Math.max(1, goldSprite.m_245424_().m_246492_());
            double halfPixelV = 8.0 / (double)Math.max(1, goldSprite.m_245424_().m_245330_());
            shader.m_173348_("GoldSpriteBounds").m_5805_(goldSprite.m_118367_(halfPixelU), goldSprite.m_118393_(halfPixelV), goldSprite.m_118367_(16.0 - halfPixelU), goldSprite.m_118393_(16.0 - halfPixelV));
        }
        RenderSystem.setShader(() -> shader);
    }

    private static boolean isChunkLayer(RenderType renderType) {
        return renderType == RenderType.m_110451_() || renderType == RenderType.m_110463_() || renderType == RenderType.m_110457_() || renderType == RenderType.m_110466_() || renderType == RenderType.m_110503_();
    }
}

