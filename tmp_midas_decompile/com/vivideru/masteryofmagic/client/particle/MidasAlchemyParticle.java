/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager$DestFactor
 *  com.mojang.blaze3d.platform.GlStateManager$SourceFactor
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.Tesselator
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.particle.Particle
 *  net.minecraft.client.particle.ParticleProvider
 *  net.minecraft.client.particle.ParticleRenderType
 *  net.minecraft.client.particle.SpriteSet
 *  net.minecraft.client.particle.TextureSheetParticle
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.client.renderer.texture.TextureAtlas
 *  net.minecraft.client.renderer.texture.TextureManager
 *  net.minecraft.core.particles.ParticleType
 *  net.minecraft.core.particles.SimpleParticleType
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraftforge.client.event.RegisterParticleProvidersEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  org.jetbrains.annotations.Nullable
 */
package com.vivideru.masteryofmagic.client.particle;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.vivideru.masteryofmagic.init.GoetyMasteryOfMagicModParticleTypes;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.Nullable;

public final class MidasAlchemyParticle
extends TextureSheetParticle {
    private static final ParticleRenderType EMISSIVE_ADDITIVE = new ParticleRenderType(){

        public void m_6505_(BufferBuilder builder, TextureManager textureManager) {
            RenderSystem.depthMask((boolean)false);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE);
            RenderSystem.setShader(GameRenderer::m_172829_);
            RenderSystem.setShaderTexture((int)0, (ResourceLocation)TextureAtlas.f_118260_);
            builder.m_166779_(VertexFormat.Mode.QUADS, DefaultVertexFormat.f_85813_);
        }

        public void m_6294_(Tesselator tesselator) {
            tesselator.m_85914_();
            RenderSystem.defaultBlendFunc();
            RenderSystem.depthMask((boolean)true);
        }

        public String toString() {
            return "MIDAS_ALCHEMY_EMISSIVE_ADDITIVE";
        }
    };
    private final float originalSize;
    private final float spin;

    private MidasAlchemyParticle(ClientLevel level, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteSet sprites, float sizeScale) {
        super(level, x, y, z, velocityX, velocityY, velocityZ);
        this.m_108335_(sprites);
        this.f_107225_ = 24 + this.f_107223_.m_188503_(17);
        this.f_107663_ = this.originalSize = (0.5f + this.f_107223_.m_188501_() * 0.7f) * sizeScale;
        this.spin = (this.f_107223_.m_188501_() - 0.5f) * 0.055f;
        this.f_107204_ = this.f_107231_ = this.f_107223_.m_188501_() * ((float)Math.PI * 2);
        this.f_107227_ = 1.0f;
        this.f_107228_ = 0.12f + this.f_107223_.m_188501_() * 0.12f;
        this.f_107229_ = 0.92f + this.f_107223_.m_188501_() * 0.08f;
        this.f_107230_ = 0.0f;
        this.f_172258_ = 0.92f;
        this.f_107226_ = 0.0f;
        this.f_107219_ = false;
        this.f_107215_ = velocityX;
        this.f_107216_ = velocityY;
        this.f_107217_ = velocityZ;
    }

    public void m_5989_() {
        super.m_5989_();
        if (this.f_107220_) {
            return;
        }
        this.f_107204_ = this.f_107231_;
        this.f_107231_ += this.spin;
        float progress = (float)this.f_107224_ / (float)this.f_107225_;
        float fadeIn = Math.min(1.0f, (float)this.f_107224_ / 4.0f);
        float fadeOut = (float)Math.pow(Math.max(0.0f, 1.0f - progress), 0.65);
        this.f_107230_ = fadeIn * fadeOut * 0.88f;
        this.f_107663_ = this.originalSize * (0.82f + (float)Math.sin((double)progress * Math.PI) * 0.28f);
    }

    protected int m_6355_(float partialTick) {
        return 0xF000F0;
    }

    public ParticleRenderType m_7556_() {
        return EMISSIVE_ADDITIVE;
    }

    public static final class Registration {
        private Registration() {
        }

        @SubscribeEvent
        public static void registerProviders(RegisterParticleProvidersEvent event) {
            event.registerSpriteSet((ParticleType)GoetyMasteryOfMagicModParticleTypes.MIDAS_ALCHEMY.get(), sprites -> new Provider(sprites, 1.0f));
            event.registerSpriteSet((ParticleType)GoetyMasteryOfMagicModParticleTypes.MIDAS_ALCHEMY_SMALL.get(), sprites -> new Provider(sprites, 0.32f));
        }
    }

    private static final class Provider
    implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;
        private final float sizeScale;

        private Provider(SpriteSet sprites, float sizeScale) {
            this.sprites = sprites;
            this.sizeScale = sizeScale;
        }

        @Nullable
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new MidasAlchemyParticle(level, x, y, z, velocityX, velocityY, velocityZ, this.sprites, this.sizeScale);
        }
    }
}

