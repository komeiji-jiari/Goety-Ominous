package com.example.goetyastrology.client;

import com.example.goetyastrology.AstralMoteOptions;
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
 * The school's own mote of light — the particle this mod draws itself instead of borrowing one of
 * vanilla's sprites. Everything about it is shaped by the one job it has, which is to be a point of
 * light and nothing more: a soft round glow out of the mod's own texture (a gaussian falloff, no
 * square edge anywhere to read), blended additively so its brightness lands on top of whatever is
 * behind it, full-bright so it shines in the dark, and without physics, because light does not
 * bounce off the floor. It breathes gently while it lives and fades as it dies; nothing about it
 * switches off abruptly.
 *
 * <p>The colour and the size ride in the {@link AstralMoteOptions}, so this one class serves every
 * shade the school throws — the solar bolts' gold, the starlight beams' violet, the comet's cold
 * pink. Vanilla particles kept clashing with the shader-drawn stars and rays; these motes are cut
 * from the same light.
 */
public class AstralMoteParticle extends SingleQuadParticle {

    /** The soft round glow every mote is drawn from; the colour comes from the options. */
    private static final ResourceLocation GLOW_TEXTURE =
            new ResourceLocation(GoetyAstrologyMod.MODID, "textures/particle/astral_mote_glow.png");

    /**
     * This particle's own sheet. Additive blending, no depth write: the mote is light over the
     * world, and light neither hides what is behind it nor cares in which order two motes are
     * drawn. The depth test stays on, so a wall still hides its motes the way it hides everything.
     */
    private static final ParticleRenderType GLOW_SHEET = new ParticleRenderType() {
        @Override
        public void begin(BufferBuilder builder, TextureManager textures) {
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            RenderSystem.depthMask(false);
            RenderSystem.setShader(GameRenderer::getParticleShader);
            RenderSystem.setShaderTexture(0, GLOW_TEXTURE);
            builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public void end(Tesselator tesselator) {
            tesselator.end();
        }

        @Override
        public String toString() {
            return "goety_astrology:astral_mote";
        }
    };

    /** Quad size of a scale-1 mote, in blocks. The options' own size multiplies this. */
    private static final float BASE_SIZE = 0.085F;

    /** Where this mote sits in the breathing cycle, so a burst never pulses in unison. */
    private final float phase = this.random.nextFloat() * Mth.TWO_PI;
    /** A second, faster cadence beaten against the first so the shimmer never reads as one clean sine. */
    private final float phaseFast = this.random.nextFloat() * Mth.TWO_PI;
    /** How hard this particular mote scintillates: some points twinkle sharply, others barely at all. */
    private final float twinkleDepth = 0.35F + 0.45F * this.random.nextFloat();
    /** The twinkle worked out this tick, kept so the quad can swell on the flare and shrink between them. */
    private float twinkle = 1.0F;

    /** The colour it is born with. Kept so the per-tick cooling reads from it instead of compounding. */
    private final float baseR;
    private final float baseG;
    private final float baseB;
    /** A slow lateral wander unique to this mote, so a burst of them drifts apart rather than in step. */
    private final double swirlPhase = this.random.nextDouble() * Mth.TWO_PI;

    AstralMoteParticle(ClientLevel level, double x, double y, double z,
                       double xSpeed, double ySpeed, double zSpeed, Vector3f color, float size) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.friction = 0.92F;
        this.hasPhysics = false;
        this.lifetime = 14 + this.random.nextInt(10);
        // 🔴 `size`, not the base class's `scale`: that one is clamped to 4 and a beam's halo needs
        // more than that. See AstralMoteOptions.
        this.quadSize = BASE_SIZE * size * (0.75F + 0.5F * this.random.nextFloat());
        this.baseR = color.x();
        this.baseG = color.y();
        this.baseB = color.z();
        this.setColor(this.baseR, this.baseG, this.baseB);
        if (xSpeed * xSpeed + ySpeed * ySpeed + zSpeed * zSpeed < 1.0E-8D) {
            // A mote shed by light hangs and drifts; it does not sit still waiting to be seen.
            this.xd = (this.random.nextDouble() - 0.5D) * 0.03D;
            this.yd = this.random.nextDouble() * 0.02D + 0.005D;
            this.zd = (this.random.nextDouble() - 0.5D) * 0.03D;
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return GLOW_SHEET;
    }

    @Override
    public void tick() {
        float life = (float) this.age / (float) this.lifetime;
        // Warm light rising as it sheds heat: a small buoyancy that eases off as the mote cools, so it
        // lifts when it is born and settles to a drift by the time it dies, rather than climbing forever.
        this.yd += 0.0011D - 0.0020D * life;
        // A slow curl read off the mote's own position, so instead of coasting in a straight line it
        // wanders the way a speck of dust does in still air — no two motes on the same path.
        double swirl = 0.0018D;
        this.xd += Math.sin((this.y + this.swirlPhase) * 1.9D + this.age * 0.16D) * swirl;
        this.zd += Math.cos((this.x + this.swirlPhase) * 1.9D + this.age * 0.14D) * swirl;
        super.tick();
        // A star does not breathe evenly — it scintillates. Two shimmers of different pace beat against
        // each other and now and then land together in a sharp flare, and that uneven twinkle is what
        // tells the eye it is a star rather than a steady lamp. The flare owns the brightness and the
        // quad both, so the point seems to swell as it catches and shrink back between catches.
        float t = this.age + this.phase;
        float shimmer = 0.5F + 0.30F * Mth.sin(t * 0.85F)
                + 0.20F * Mth.sin(t * 2.35F + this.phaseFast);
        this.twinkle = 1.0F - this.twinkleDepth * (1.0F - Mth.clamp(shimmer, 0.0F, 1.0F));
        // Cooling drains the blue first and the green next, the way a cooling ember does: whatever its
        // hue, a mote reddens a touch as it dies rather than simply dimming to the same colour it was.
        this.rCol = this.baseR;
        this.gCol = this.baseG * (1.0F - 0.16F * life);
        this.bCol = this.baseB * (1.0F - 0.40F * life);
        float fade = Mth.clamp((1.0F - life) / 0.45F, 0.0F, 1.0F);
        this.alpha = this.twinkle * fade;
    }

    @Override
    public float getQuadSize(float partialTick) {
        float life = ((float) this.age + partialTick) / (float) this.lifetime;
        // The twinkle swells the point a touch on its flares, so it catches the eye as light does.
        return this.quadSize * (1.0F - 0.4F * life) * (0.85F + 0.25F * this.twinkle);
    }

    @Override
    protected int getLightColor(float partialTick) {
        // Self-lit: a mote of the school's light shines in the dark, like everything else it throws.
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

    public static class Provider implements ParticleProvider<AstralMoteOptions> {
        @Override
        public Particle createParticle(AstralMoteOptions options, ClientLevel level, double x,
                                       double y, double z, double xSpeed, double ySpeed,
                                       double zSpeed) {
            return new AstralMoteParticle(level, x, y, z, xSpeed, ySpeed, zSpeed,
                    options.getColor(), options.size());
        }
    }
}
