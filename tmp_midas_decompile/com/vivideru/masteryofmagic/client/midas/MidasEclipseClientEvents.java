/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager$DestFactor
 *  com.mojang.blaze3d.platform.GlStateManager$SourceFactor
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.BufferBuilder$RenderedBuffer
 *  com.mojang.blaze3d.vertex.BufferUploader
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.Tesselator
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  com.mojang.math.Axis
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  net.minecraftforge.client.event.RenderLevelStageEvent
 *  net.minecraftforge.client.event.RenderLevelStageEvent$Stage
 *  net.minecraftforge.client.event.ViewportEvent$ComputeFogColor
 *  net.minecraftforge.event.TickEvent$ClientTickEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  org.joml.Matrix4f
 */
package com.vivideru.masteryofmagic.client.midas;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import com.vivideru.masteryofmagic.client.midas.MidasEclipseClientState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.joml.Matrix4f;

public final class MidasEclipseClientEvents {
    private static final ResourceLocation VANILLA_MOON = new ResourceLocation("minecraft", "textures/environment/moon_phases.png");
    private static final ResourceLocation MIDAS_ALCHEMY_SYMBOLS = new ResourceLocation("goety_mastery_of_magic", "textures/environment/midas_alchemy_symbols.png");
    private static final ResourceLocation MIDAS_ALCHEMY_SYMBOLS_EXTENDED = new ResourceLocation("goety_mastery_of_magic", "textures/environment/midas_alchemy_symbols_extended.png");
    private static final float[] VAULT_RING_ANGLES = new float[]{47.0f, 57.0f, 66.0f, 74.0f, 81.0f, 87.0f};
    private static final int[] VAULT_RING_COUNTS = new int[]{16, 20, 24, 28, 32, 36};

    private MidasEclipseClientEvents() {
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            MidasEclipseClientState.clientTick(Minecraft.m_91087_());
        }
    }

    @SubscribeEvent
    public static void onFogColor(ViewportEvent.ComputeFogColor event) {
        float partialTick = (float)event.getPartialTick();
        float intensity = MidasEclipseClientState.intensity(partialTick);
        if (intensity <= 0.001f) {
            return;
        }
        float pulse = MidasEclipseClientState.pulse(partialTick);
        float strength = intensity * 0.84f;
        float targetRed = Mth.m_14179_((float)pulse, (float)0.088f, (float)0.153f);
        float targetGreen = Mth.m_14179_((float)pulse, (float)0.002f, (float)0.008f);
        float targetBlue = Mth.m_14179_((float)pulse, (float)0.126f, (float)0.222f);
        event.setRed(Mth.m_14179_((float)strength, (float)event.getRed(), (float)targetRed));
        event.setGreen(Mth.m_14179_((float)strength, (float)event.getGreen(), (float)targetGreen));
        event.setBlue(Mth.m_14179_((float)strength, (float)event.getBlue(), (float)targetBlue));
    }

    @SubscribeEvent
    public static void onRenderSky(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_SKY) {
            return;
        }
        Minecraft minecraft = Minecraft.m_91087_();
        if (minecraft.f_91073_ == null) {
            return;
        }
        float intensity = MidasEclipseClientState.intensity(event.getPartialTick());
        if (intensity <= 0.001f) {
            return;
        }
        float pulse = MidasEclipseClientState.pulse(event.getPartialTick());
        PoseStack poseStack = event.getPoseStack();
        poseStack.m_85836_();
        poseStack.m_252781_(Axis.f_252436_.m_252977_(-90.0f));
        poseStack.m_252781_(Axis.f_252529_.m_252977_(minecraft.f_91073_.m_46942_(event.getPartialTick()) * 360.0f));
        Matrix4f matrix = poseStack.m_85850_().m_252922_();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask((boolean)false);
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::m_172811_);
        RenderSystem.blendFunc((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE);
        MidasEclipseClientEvents.drawShatteredSky(matrix, intensity, (float)minecraft.f_91073_.m_46467_() + event.getPartialTick());
        MidasEclipseClientEvents.drawPixelatedCorona(matrix, -99.6f, intensity, pulse);
        RenderSystem.setShader(GameRenderer::m_172820_);
        RenderSystem.setShaderTexture((int)0, (ResourceLocation)MIDAS_ALCHEMY_SYMBOLS);
        MidasEclipseClientEvents.drawAlchemyRings(matrix, -99.72f, intensity, (float)minecraft.f_91073_.m_46467_() + event.getPartialTick());
        RenderSystem.setShaderTexture((int)0, (ResourceLocation)MIDAS_ALCHEMY_SYMBOLS_EXTENDED);
        MidasEclipseClientEvents.drawAlchemyVault(matrix, intensity, (float)minecraft.f_91073_.m_46467_() + event.getPartialTick());
        RenderSystem.setShader(GameRenderer::m_172811_);
        MidasEclipseClientEvents.drawAlchemyLightning(matrix, intensity, (float)minecraft.f_91073_.m_46467_() + event.getPartialTick());
        RenderSystem.defaultBlendFunc();
        MidasEclipseClientEvents.drawSquare(matrix, 21.4f, -99.35f, 8, 0, 14, Math.round(255.0f * intensity));
        RenderSystem.setShader(GameRenderer::m_172820_);
        RenderSystem.setShaderTexture((int)0, (ResourceLocation)VANILLA_MOON);
        RenderSystem.blendFunc((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE);
        MidasEclipseClientEvents.drawNewMoon(matrix, 20.7f, -99.15f, 255, 25, 255, Math.round((145.0f + pulse * 55.0f) * intensity));
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.enableCull();
        RenderSystem.depthMask((boolean)true);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        poseStack.m_85849_();
    }

    private static void drawSquare(Matrix4f matrix, float radius, float height, int red, int green, int blue, int alpha) {
        BufferBuilder builder = Tesselator.m_85913_().m_85915_();
        builder.m_166779_(VertexFormat.Mode.QUADS, DefaultVertexFormat.f_85815_);
        builder.m_252986_(matrix, -radius, height, radius).m_6122_(red, green, blue, alpha).m_5752_();
        builder.m_252986_(matrix, radius, height, radius).m_6122_(red, green, blue, alpha).m_5752_();
        builder.m_252986_(matrix, radius, height, -radius).m_6122_(red, green, blue, alpha).m_5752_();
        builder.m_252986_(matrix, -radius, height, -radius).m_6122_(red, green, blue, alpha).m_5752_();
        BufferUploader.m_231202_((BufferBuilder.RenderedBuffer)builder.m_231175_());
    }

    private static void drawPixelatedCorona(Matrix4f matrix, float height, float intensity, float pulse) {
        float pixelSize = 3.0f;
        BufferBuilder builder = Tesselator.m_85913_().m_85915_();
        builder.m_166779_(VertexFormat.Mode.QUADS, DefaultVertexFormat.f_85815_);
        for (int x = -16; x < 16; ++x) {
            for (int z = -16; z < 16; ++z) {
                float ring = Math.max(Math.abs((float)x + 0.5f), Math.abs((float)z + 0.5f));
                if (ring < 7.5f || ring > 15.5f) continue;
                int hash = Math.floorMod(x * 31 + z * 17 + x * z * 7, 12);
                float distanceFromCore = ring - 7.5f;
                float falloff = (float)Math.pow(0.74, distanceFromCore);
                float pixelVariation = 0.9f + (float)hash / 11.0f * 0.1f;
                float baseAlpha = 244.0f * falloff * pixelVariation;
                int alpha = Mth.m_14045_((int)Math.round(baseAlpha * intensity * Mth.m_14179_((float)pulse, (float)0.88f, (float)1.0f)), (int)0, (int)255);
                float outerBlend = Mth.m_14036_((float)(distanceFromCore / 8.0f), (float)0.0f, (float)1.0f);
                int red = Math.round(Mth.m_14179_((float)outerBlend, (float)255.0f, (float)182.0f));
                int green = Math.round(Mth.m_14179_((float)outerBlend, (float)Mth.m_14179_((float)pulse, (float)28.0f, (float)58.0f), (float)12.0f));
                float minX = (float)x * 3.0f;
                float maxX = minX + 3.0f;
                float minZ = (float)z * 3.0f;
                float maxZ = minZ + 3.0f;
                builder.m_252986_(matrix, minX, height, maxZ).m_6122_(red, green, 255, alpha).m_5752_();
                builder.m_252986_(matrix, maxX, height, maxZ).m_6122_(red, green, 255, alpha).m_5752_();
                builder.m_252986_(matrix, maxX, height, minZ).m_6122_(red, green, 255, alpha).m_5752_();
                builder.m_252986_(matrix, minX, height, minZ).m_6122_(red, green, 255, alpha).m_5752_();
            }
        }
        BufferUploader.m_231202_((BufferBuilder.RenderedBuffer)builder.m_231175_());
    }

    private static void drawAlchemyRings(Matrix4f matrix, float height, float intensity, float time) {
        BufferBuilder builder = Tesselator.m_85913_().m_85915_();
        builder.m_166779_(VertexFormat.Mode.QUADS, DefaultVertexFormat.f_85819_);
        for (int ring = 0; ring < 2; ++ring) {
            float orbitRadius = ring == 0 ? 61.0f : 82.0f;
            float halfSize = ring == 0 ? 6.6f : 7.4f;
            float direction = ring == 0 ? 1.0f : -1.0f;
            float speed = ring == 0 ? 0.034f : 0.021f;
            float orbitOffset = time * speed * direction;
            for (int slot = 0; slot < 8; ++slot) {
                int symbol = slot * 2 + ring;
                float angle = orbitOffset + (float)slot * 45.0f;
                float radians = angle * ((float)Math.PI / 180);
                float centerX = Mth.m_14089_((float)radians) * orbitRadius;
                float centerZ = Mth.m_14031_((float)radians) * orbitRadius;
                float iconRotation = radians - 1.5707964f;
                float rightX = Mth.m_14089_((float)iconRotation) * halfSize;
                float rightZ = Mth.m_14031_((float)iconRotation) * halfSize;
                float upX = -Mth.m_14031_((float)iconRotation) * halfSize;
                float upZ = Mth.m_14089_((float)iconRotation) * halfSize;
                float flicker = 0.5f + 0.5f * Mth.m_14031_((float)(time * 0.047f + (float)symbol * 1.731f));
                int alpha = Mth.m_14045_((int)Math.round(Mth.m_14179_((float)flicker, (float)74.0f, (float)134.0f) * intensity), (int)0, (int)255);
                int red = ring == 0 ? 255 : 205;
                int green = Math.round(Mth.m_14179_((float)flicker, (float)22.0f, (float)48.0f));
                int column = symbol & 3;
                int row = symbol >> 2;
                float minU = (float)column * 0.25f;
                float maxU = minU + 0.25f;
                float minV = (float)row * 0.25f;
                float maxV = minV + 0.25f;
                MidasEclipseClientEvents.addSymbolVertex(builder, matrix, centerX - rightX + upX, height, centerZ - rightZ + upZ, minU, minV, red, green, 255, alpha);
                MidasEclipseClientEvents.addSymbolVertex(builder, matrix, centerX + rightX + upX, height, centerZ + rightZ + upZ, maxU, minV, red, green, 255, alpha);
                MidasEclipseClientEvents.addSymbolVertex(builder, matrix, centerX + rightX - upX, height, centerZ + rightZ - upZ, maxU, maxV, red, green, 255, alpha);
                MidasEclipseClientEvents.addSymbolVertex(builder, matrix, centerX - rightX - upX, height, centerZ - rightZ - upZ, minU, maxV, red, green, 255, alpha);
            }
        }
        BufferUploader.m_231202_((BufferBuilder.RenderedBuffer)builder.m_231175_());
    }

    private static void drawAlchemyVault(Matrix4f matrix, float intensity, float time) {
        BufferBuilder builder = Tesselator.m_85913_().m_85915_();
        builder.m_166779_(VertexFormat.Mode.QUADS, DefaultVertexFormat.f_85819_);
        for (int ring = 0; ring < VAULT_RING_ANGLES.length; ++ring) {
            int count = VAULT_RING_COUNTS[ring];
            float theta = VAULT_RING_ANGLES[ring] * ((float)Math.PI / 180);
            float direction = (ring & 1) == 0 ? 1.0f : -1.0f;
            float rotation = time * (0.02f - (float)ring * 0.0016f) * direction;
            float halfSize = Mth.m_14179_((float)((float)ring / 5.0f), (float)6.0f, (float)4.15f);
            float ringFade = Mth.m_14179_((float)((float)ring / 5.0f), (float)1.0f, (float)0.62f);
            for (int slot = 0; slot < count; ++slot) {
                int symbol = Math.floorMod(slot * 13 + ring * 19, 64);
                float phi = (rotation + (float)slot * (360.0f / (float)count)) * ((float)Math.PI / 180);
                float flicker = 0.5f + 0.5f * Mth.m_14031_((float)(time * 0.036f + (float)symbol * 1.193f + (float)ring * 0.71f));
                int alpha = Mth.m_14045_((int)Math.round(Mth.m_14179_((float)flicker, (float)46.0f, (float)102.0f) * ringFade * intensity), (int)0, (int)255);
                int red = Math.round(Mth.m_14179_((float)((float)ring / 5.0f), (float)255.0f, (float)225.0f));
                int green = Math.round(Mth.m_14179_((float)flicker, (float)20.0f, (float)43.0f));
                MidasEclipseClientEvents.addVaultSymbol(builder, matrix, symbol, theta, phi, halfSize, red, green, 245, alpha);
            }
        }
        BufferUploader.m_231202_((BufferBuilder.RenderedBuffer)builder.m_231175_());
    }

    private static void addVaultSymbol(BufferBuilder builder, Matrix4f matrix, int symbol, float theta, float phi, float halfSize, int red, int green, int blue, int alpha) {
        float radius = 100.0f;
        float sinTheta = Mth.m_14031_((float)theta);
        float cosTheta = Mth.m_14089_((float)theta);
        float sinPhi = Mth.m_14031_((float)phi);
        float cosPhi = Mth.m_14089_((float)phi);
        float centerX = 100.0f * sinTheta * cosPhi;
        float centerY = -100.0f * cosTheta;
        float centerZ = 100.0f * sinTheta * sinPhi;
        float rightX = -sinPhi * halfSize;
        float rightY = 0.0f;
        float rightZ = cosPhi * halfSize;
        float upX = cosTheta * cosPhi * halfSize;
        float upY = sinTheta * halfSize;
        float upZ = cosTheta * sinPhi * halfSize;
        int column = symbol & 7;
        int row = symbol >> 3;
        float minU = (float)column * 0.125f;
        float maxU = minU + 0.125f;
        float minV = (float)row * 0.125f;
        float maxV = minV + 0.125f;
        MidasEclipseClientEvents.addSymbolVertex(builder, matrix, centerX - rightX + upX, centerY - rightY + upY, centerZ - rightZ + upZ, minU, minV, red, green, blue, alpha);
        MidasEclipseClientEvents.addSymbolVertex(builder, matrix, centerX + rightX + upX, centerY + rightY + upY, centerZ + rightZ + upZ, maxU, minV, red, green, blue, alpha);
        MidasEclipseClientEvents.addSymbolVertex(builder, matrix, centerX + rightX - upX, centerY + rightY - upY, centerZ + rightZ - upZ, maxU, maxV, red, green, blue, alpha);
        MidasEclipseClientEvents.addSymbolVertex(builder, matrix, centerX - rightX - upX, centerY - rightY - upY, centerZ - rightZ - upZ, minU, maxV, red, green, blue, alpha);
    }

    private static void drawShatteredSky(Matrix4f matrix, float intensity, float time) {
        RenderSystem.lineWidth((float)2.5f);
        MidasEclipseClientEvents.drawFracturePass(matrix, intensity, time, false);
        RenderSystem.lineWidth((float)1.0f);
        MidasEclipseClientEvents.drawFracturePass(matrix, intensity, time, true);
    }

    private static void drawFracturePass(Matrix4f matrix, float intensity, float time, boolean core) {
        int fractureCount = 56;
        float radius = core ? 99.12f : 99.18f;
        BufferBuilder builder = Tesselator.m_85913_().m_85915_();
        builder.m_166779_(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.f_85815_);
        for (int fracture = 0; fracture < 56; ++fracture) {
            int seed = fracture * 92821 + 7319;
            float theta = (24.0f + MidasEclipseClientEvents.fractureHash(seed, 1) * 68.0f) * ((float)Math.PI / 180);
            float phi = MidasEclipseClientEvents.fractureHash(seed, 2) * ((float)Math.PI * 2);
            float direction = MidasEclipseClientEvents.fractureHash(seed, 3) * ((float)Math.PI * 2);
            float length = 2.8f + MidasEclipseClientEvents.fractureHash(seed, 4) * 4.8f;
            int segments = 4 + Math.floorMod(seed, 4);
            float shimmer = 0.5f + 0.5f * Mth.m_14031_((float)(time * (0.038f + MidasEclipseClientEvents.fractureHash(seed, 5) * 0.022f) + (float)fracture * 1.317f));
            float flash = (float)Math.pow(shimmer, 2.2);
            int alpha = Mth.m_14045_((int)Math.round((core ? Mth.m_14179_((float)flash, (float)34.0f, (float)82.0f) : Mth.m_14179_((float)flash, (float)9.0f, (float)24.0f)) * intensity), (int)0, (int)255);
            int red = core ? 255 : 224;
            int green = core ? Math.round(Mth.m_14179_((float)flash, (float)24.0f, (float)52.0f)) : 10;
            int blue = 255;
            float previousX = -Mth.m_14089_((float)direction) * length * 0.5f;
            float previousY = -Mth.m_14031_((float)direction) * length * 0.5f;
            for (int segment = 1; segment <= segments; ++segment) {
                float progress = (float)segment / (float)segments;
                float along = (progress - 0.5f) * length;
                float jagged = (MidasEclipseClientEvents.fractureHash(seed, 20 + segment) - 0.5f) * 0.82f;
                float currentX = Mth.m_14089_((float)direction) * along - Mth.m_14031_((float)direction) * jagged;
                float currentY = Mth.m_14031_((float)direction) * along + Mth.m_14089_((float)direction) * jagged;
                MidasEclipseClientEvents.addFractureSegment(builder, matrix, radius, theta, phi, previousX, previousY, currentX, currentY, red, green, blue, alpha);
                if (segment == segments / 2 || segment == segments - 1) {
                    float side = segment == segments / 2 ? 1.0f : -1.0f;
                    float branchDirection = direction + side * (0.72f + MidasEclipseClientEvents.fractureHash(seed, 40 + segment) * 0.62f);
                    float branchLength = length * (0.2f + MidasEclipseClientEvents.fractureHash(seed, 50 + segment) * 0.14f);
                    float middleX = currentX + Mth.m_14089_((float)branchDirection) * branchLength * 0.52f;
                    float middleY = currentY + Mth.m_14031_((float)branchDirection) * branchLength * 0.52f;
                    float endX = currentX + Mth.m_14089_((float)branchDirection) * branchLength;
                    float endY = currentY + Mth.m_14031_((float)branchDirection) * branchLength;
                    MidasEclipseClientEvents.addFractureSegment(builder, matrix, radius, theta, phi, currentX, currentY, middleX, middleY, red, green, blue, alpha);
                    MidasEclipseClientEvents.addFractureSegment(builder, matrix, radius, theta, phi, middleX, middleY, endX, endY, red, green, blue, Math.max(1, Math.round((float)alpha * 0.72f)));
                }
                previousX = currentX;
                previousY = currentY;
            }
        }
        BufferUploader.m_231202_((BufferBuilder.RenderedBuffer)builder.m_231175_());
    }

    private static void addFractureSegment(BufferBuilder builder, Matrix4f matrix, float radius, float theta, float phi, float x0, float y0, float x1, float y1, int red, int green, int blue, int alpha) {
        MidasEclipseClientEvents.addFractureVertex(builder, matrix, radius, theta, phi, x0, y0, red, green, blue, alpha);
        MidasEclipseClientEvents.addFractureVertex(builder, matrix, radius, theta, phi, x1, y1, red, green, blue, alpha);
    }

    private static void addFractureVertex(BufferBuilder builder, Matrix4f matrix, float radius, float theta, float phi, float localX, float localY, int red, int green, int blue, int alpha) {
        float sinTheta = Mth.m_14031_((float)theta);
        float cosTheta = Mth.m_14089_((float)theta);
        float sinPhi = Mth.m_14031_((float)phi);
        float cosPhi = Mth.m_14089_((float)phi);
        float centerX = radius * sinTheta * cosPhi;
        float centerY = -radius * cosTheta;
        float centerZ = radius * sinTheta * sinPhi;
        float pointX = centerX - sinPhi * localX + cosTheta * cosPhi * localY;
        float pointY = centerY + sinTheta * localY;
        float pointZ = centerZ + cosPhi * localX + cosTheta * sinPhi * localY;
        float scale = radius / Mth.m_14116_((float)(pointX * pointX + pointY * pointY + pointZ * pointZ));
        builder.m_252986_(matrix, pointX * scale, pointY * scale, pointZ * scale).m_6122_(red, green, blue, alpha).m_5752_();
    }

    private static float fractureHash(int seed, int salt) {
        int value = seed ^ salt * 73244475;
        value = (value ^ value >>> 16) * 73244475;
        value = (value ^ value >>> 16) * 73244475;
        value ^= value >>> 16;
        return (float)(value & Integer.MAX_VALUE) / 2.14748365E9f;
    }

    private static void drawAlchemyLightning(Matrix4f matrix, float intensity, float time) {
        int bolt;
        int cycle = Mth.m_14143_((float)(time / 120.0f));
        float phase = time - (float)cycle * 120.0f;
        if (phase >= 20.0f) {
            return;
        }
        float envelope = Mth.m_14031_((float)(phase / 20.0f * (float)Math.PI)) * intensity;
        if (envelope <= 0.01f) {
            return;
        }
        RenderSystem.lineWidth((float)2.0f);
        for (bolt = 0; bolt < 2; ++bolt) {
            MidasEclipseClientEvents.drawHorizontalBolt(matrix, cycle, bolt, envelope, 18);
        }
        RenderSystem.lineWidth((float)1.0f);
        for (bolt = 0; bolt < 2; ++bolt) {
            MidasEclipseClientEvents.drawHorizontalBolt(matrix, cycle, bolt, envelope, 92);
        }
        RenderSystem.lineWidth((float)1.0f);
    }

    private static void drawHorizontalBolt(Matrix4f matrix, int cycle, int bolt, float envelope, int baseAlpha) {
        int hash = Math.floorMod(cycle * 73 + bolt * 41, 997);
        int ring = 1 + Math.floorMod(hash, VAULT_RING_ANGLES.length - 1);
        float thetaBase = VAULT_RING_ANGLES[ring] * ((float)Math.PI / 180);
        float startDegrees = Math.floorMod(hash * 37, 360);
        float lengthDegrees = 12.0f + (float)Math.floorMod(hash * 11, 15);
        int segments = 14;
        BufferBuilder builder = Tesselator.m_85913_().m_85915_();
        builder.m_166779_(VertexFormat.Mode.DEBUG_LINE_STRIP, DefaultVertexFormat.f_85815_);
        for (int segment = 0; segment <= segments; ++segment) {
            float progress = (float)segment / (float)segments;
            float jagged = (float)((segment * 17 + hash) % 7 - 3) * 0.0052f;
            float theta = thetaBase + jagged;
            float phi = (startDegrees + lengthDegrees * progress) * ((float)Math.PI / 180);
            float radius = 99.35f;
            int alpha = Mth.m_14045_((int)Math.round((float)baseAlpha * envelope * (0.82f + 0.18f * Mth.m_14031_((float)((float)segment * 2.1f + (float)hash)))), (int)0, (int)255);
            builder.m_252986_(matrix, radius * Mth.m_14031_((float)theta) * Mth.m_14089_((float)phi), -radius * Mth.m_14089_((float)theta), radius * Mth.m_14031_((float)theta) * Mth.m_14031_((float)phi)).m_6122_(255, 34, 238, alpha).m_5752_();
        }
        BufferUploader.m_231202_((BufferBuilder.RenderedBuffer)builder.m_231175_());
    }

    private static void addSymbolVertex(BufferBuilder builder, Matrix4f matrix, float x, float y, float z, float u, float v, int red, int green, int blue, int alpha) {
        builder.m_252986_(matrix, x, y, z).m_7421_(u, v).m_6122_(red, green, blue, alpha).m_5752_();
    }

    private static void drawNewMoon(Matrix4f matrix, float radius, float height, int red, int green, int blue, int alpha) {
        float minU = 0.0f;
        float maxU = 0.25f;
        float minV = 0.5f;
        float maxV = 1.0f;
        BufferBuilder builder = Tesselator.m_85913_().m_85915_();
        builder.m_166779_(VertexFormat.Mode.QUADS, DefaultVertexFormat.f_85819_);
        builder.m_252986_(matrix, -radius, height, radius).m_7421_(maxU, maxV).m_6122_(red, green, blue, alpha).m_5752_();
        builder.m_252986_(matrix, radius, height, radius).m_7421_(minU, maxV).m_6122_(red, green, blue, alpha).m_5752_();
        builder.m_252986_(matrix, radius, height, -radius).m_7421_(minU, minV).m_6122_(red, green, blue, alpha).m_5752_();
        builder.m_252986_(matrix, -radius, height, -radius).m_7421_(maxU, minV).m_6122_(red, green, blue, alpha).m_5752_();
        BufferUploader.m_231202_((BufferBuilder.RenderedBuffer)builder.m_231175_());
    }
}

