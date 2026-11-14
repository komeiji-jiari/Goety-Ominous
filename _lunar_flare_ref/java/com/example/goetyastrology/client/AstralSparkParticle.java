package com.example.goetyastrology.client;

import com.example.goetyastrology.AstralSparkOptions;
import com.example.goetyastrology.GoetyAstrologyMod;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

/**
 * The school's spark — the mote's opposite number. Where the mote hangs and glows, a spark is thrown:
 * it shoots outward, arcs down under real gravity, loses speed to the air and burns out in under a
 * second, dimming from a hot white flash to its own colour as it goes. It is what an impact throws off,
 * so the solar bolts, the comet and the beams have grit to their landings instead of a clean puff.
 *
 * <p>Drawn from a tight bright core rather than the mote's soft halo, additively and full-bright, so a
 * shower of them reads as embers rather than a cloud of light. No collision — a spark that has to test
 * the ground each tick is a spark that costs, and one that dies in the air reads the same.
 */
public class AstralSparkParticle extends SingleQuadParticle {

    private static final ResourceLocation CORE_TEXTURE =
            new ResourceLocation(GoetyAstrologyMod.MODID, "textures/particle/astral_core.png");

    /** Additive, no depth write, depth test on: a spark is light over the world, hidden by walls. */
    private static final ParticleRenderType CORE_SHEET = new ParticleRenderType() {
        @Override
        public void begin(BufferBuilder builder, TextureManager textures) {
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            RenderSystem.depthMask(false);
            RenderSystem.setShader(GameRenderer::getParticleShader);
            RenderSystem.setShaderTexture(0, CORE_TEXTURE);
            builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public void end(Tesselator tesselator) {
            tesselator.end();
        }

        @Override
        public String toString() {
            return "goety_astrology:astral_spark";
        }
    };

    /** Raised from 0.11: the high-res sparkle texture needs pixels for its tapered rays to read. */
    private static final float BASE_SIZE = 0.14F;

    /** The colour it settles to; it is born hotter and whiter than this and cools toward it. */
    private final float baseR;
    private final float baseG;
    private final float baseB;

    AstralSparkParticle(ClientLevel level, double x, double y, double z,
                        double xSpeed, double ySpeed, double zSpeed, Vector3f color, float scale) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.hasPhysics = false;
        // Real weight and real air: it arcs and slows rather than coasting in a line.
        this.gravity = 0.62F;
        this.friction = 0.86F;
        this.lifetime = 9 + this.random.nextInt(9);
        this.quadSize = BASE_SIZE * scale * (0.65F + 0.6F * this.random.nextFloat());
        this.baseR = color.x();
        this.baseG = color.y();
        this.baseB = color.z();
        // Kicked outward a touch even when handed no speed, so a burst sprays rather than dribbles.
        this.xd += (this.random.nextDouble() - 0.5D) * 0.08D;
        this.yd += this.random.nextDouble() * 0.06D;
        this.zd += (this.random.nextDouble() - 0.5D) * 0.08D;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return CORE_SHEET;
    }

    @Override
    public void tick() {
        super.tick();
        float life = (float) this.age / (float) this.lifetime;
        // Born as a hot white flash, cooling to its own colour over the first third, then reddening as
        // it dies — the blue drains first, the way a real cinder loses its heat.
        float flash = Mth.clamp(1.0F - life * 3.0F, 0.0F, 1.0F);
        this.rCol = Mth.lerp(flash, this.baseR, 1.0F);
        this.gCol = Mth.lerp(flash, this.baseG * (1.0F - 0.20F * life), 1.0F);
        this.bCol = Mth.lerp(flash, this.baseB * (1.0F - 0.55F * life), 1.0F);
        // Bright and steady while it flies, snuffing out only over the last third of its life.
        this.alpha = Mth.clamp((1.0F - life) / 0.35F, 0.0F, 1.0F);
    }

    @Override
    public float getQuadSize(float partialTick) {
        float life = ((float) this.age + partialTick) / (float) this.lifetime;
        // Shrinks to a pinpoint as it cools.
        return this.quadSize * (1.0F - 0.6F * life);
    }

    @Override
    protected int getLightColor(float partialTick) {
        return LightTexture.FULL_BRIGHT;
    }

    @Override
    protected float getU0() {
        return 0.0F;
    }

    @Override
    protected float getU1() {
        return 1.0F;
    }

    @Override
    protected float getV0() {
        return 0.0F;
    }

    @Override
    protected float getV1() {
        return 1.0F;
    }

    public static class Provider implements ParticleProvider<AstralSparkOptions> {
        @Override
        public Particle createParticle(AstralSparkOptions options, ClientLevel level, double x,
                                       double y, double z, double xSpeed, double ySpeed,
                                       double zSpeed) {
            return new AstralSparkParticle(level, x, y, z, xSpeed, ySpeed, zSpeed,
                    options.getColor(), options.getScale());
        }
    }
}
