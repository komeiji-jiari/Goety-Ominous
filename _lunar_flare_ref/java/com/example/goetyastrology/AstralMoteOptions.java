package com.example.goetyastrology;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.DustParticleOptionsBase;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

/**
 * The options of the school's own mote: a colour and a size, and nothing else, because a mote of
 * light needs nothing else. Extending the dust base means the string form the {@code /particle}
 * command reads and the codec come from one place — the mote is serialized very like vanilla's
 * redstone dust, which keeps both sides of {@code sendParticles} in step without any custom packet
 * work.
 *
 * <p>🔴 <b>Why {@code size} is ours and not the base class's {@code scale}.</b>
 * {@link DustParticleOptionsBase}'s constructor does {@code Mth.clamp(scale, 0.01, 4.0)}, and there is
 * no way to get past it — the field is final and the clamp happens before the subclass sees the value.
 * Four is enough for a spark or a droplet and nowhere near enough for a beam's halo: a sheath meant to
 * be 0.72 of a block across asked for 8.5 and silently arrived as 4.0, which is the size of the core it
 * was supposed to be wrapping, which is the same as having no sheath at all. Worse, it fails quietly —
 * the number is right there in the source and simply is not what the particle is.
 *
 * <p>So the real size lives here, the inherited scale is left at a neutral 1.0, and this class writes
 * its own network form ({@link #writeToNetwork}) that carries {@link #size} instead of the clamped
 * field. Nothing else in the game reads the inherited value for a mote; {@code AstralMoteParticle} is
 * the only consumer and it reads {@link #size()}.
 */
public class AstralMoteOptions extends DustParticleOptionsBase {

    /** The real quad multiplier. Unbounded, unlike the base class's clamp at 4. */
    private final float size;

    public static final Codec<AstralMoteOptions> CODEC = RecordCodecBuilder.create(options ->
            options.group(ExtraCodecs.VECTOR3F.fieldOf("color").forGetter(o -> o.color),
                            Codec.FLOAT.fieldOf("scale").forGetter(AstralMoteOptions::size))
                    .apply(options, AstralMoteOptions::new));

    public static final ParticleOptions.Deserializer<AstralMoteOptions> DESERIALIZER =
            new ParticleOptions.Deserializer<>() {
                @Override
                public AstralMoteOptions fromCommand(ParticleType<AstralMoteOptions> type,
                                                     StringReader reader) throws CommandSyntaxException {
                    Vector3f color = DustParticleOptionsBase.readVector3f(reader);
                    reader.expect(' ');
                    return new AstralMoteOptions(color, reader.readFloat());
                }

                @Override
                public AstralMoteOptions fromNetwork(ParticleType<AstralMoteOptions> type,
                                                     FriendlyByteBuf buf) {
                    // Mirrors writeToNetwork: three colour floats then the real size, which is one
                    // float, in the same slot the base class would have put its clamped scale.
                    return new AstralMoteOptions(DustParticleOptionsBase.readVector3f(buf),
                            buf.readFloat());
                }
            };

    private static final ParticleType<AstralMoteOptions> TYPE = new ParticleType<>(false, DESERIALIZER) {
        @Override
        public Codec<AstralMoteOptions> codec() {
            return CODEC;
        }
    };

    /** The particle type itself; registered by {@link ModParticles#ASTRAL_MOTE}. */
    public static ParticleType<AstralMoteOptions> type() {
        return TYPE;
    }

    /** The shared shades every spawn site picks from, so the motes stay one family across the mod. */
    public static final AstralMoteOptions WHITE =
            new AstralMoteOptions(new Vector3f(1.0F, 0.98F, 0.93F), 0.9F);
    /** The school's own violet, for everything starlight. */
    public static final AstralMoteOptions VIOLET =
            new AstralMoteOptions(new Vector3f(0.86F, 0.62F, 1.0F), 0.9F);
    /** Warm gold, for the solar effects. */
    public static final AstralMoteOptions GOLD =
            new AstralMoteOptions(new Vector3f(1.0F, 0.82F, 0.45F), 1.0F);
    /** Pale moonlight blue, for the lunar ones. */
    public static final AstralMoteOptions LUNAR =
            new AstralMoteOptions(new Vector3f(0.82F, 0.90F, 1.0F), 0.9F);
    /**
     * 月耀's own cyan, sampled straight out of {@code lunar_flare_focus.png} (#09FFFA, the 20 brightest
     * pixels of the icon). Not a greyed moon-white: the focus's colour is a hard electric cyan, and the
     * rain of light it calls down has to be recognisably the same light the icon is holding.
     */
    public static final AstralMoteOptions LUNAR_FLARE =
            new AstralMoteOptions(new Vector3f(0.035F, 1.0F, 0.98F), 1.0F);
    /**
     * The hard core of a 月耀 thread — the same cyan as {@link #LUNAR_FLARE}, but four times the size,
     * which is what turns a string of beads into a rope of light. The red channel stays at a twentieth
     * on purpose: additive blending adds red as well as green and blue, so a mote tinted toward white
     * goes pure white the moment a few of them overlap, and a beam is nothing but overlaps. At 0.05 the
     * rope's spine can stack ten deep and still read as cyan rather than blown out.
     */
    public static final AstralMoteOptions LUNAR_BEAM =
            new AstralMoteOptions(new Vector3f(0.05F, 0.95F, 0.93F), 4.2F);
    /**
     * The sheath around that core: the identical hue carried at a tenth of the brightness and eight and
     * a half times the size. A halo does not have to be bright to be seen — it has to be <em>wide</em>,
     * and it is the many-mote overlap of the narrow core that supplies the brightness. Getting these two
     * roles the wrong way round is how a light beam turns into a white bar.
     */
    public static final AstralMoteOptions LUNAR_SHEATH =
            new AstralMoteOptions(new Vector3f(0.02F, 0.22F, 0.22F), 8.5F);
    /**
     * 蚀脑's violet, sampled out of {@code nebula_hex_focus.png} (#A344EB) — the mid tone of that icon's
     * ramp, which is what the cloud's body should read as.
     */
    public static final AstralMoteOptions NEBULA =
            new AstralMoteOptions(new Vector3f(0.64F, 0.27F, 0.92F), 1.1F);
    /** Cold cyan-white, for the quasar and the white hole's light. */
    public static final AstralMoteOptions COLD =
            new AstralMoteOptions(new Vector3f(0.62F, 0.82F, 1.0F), 1.0F);

    public AstralMoteOptions(Vector3f color, float size) {
        // 1.0 into the base on purpose: its scale is clamped to [0.01, 4.0] and is not used for
        // anything else here. The size that matters is the field below.
        super(color, 1.0F);
        this.size = Math.max(0.01F, size);
    }

    /** The real quad multiplier this mote is drawn at. */
    public float size() {
        return this.size;
    }

    /**
     * This option's own network form. Not the base's: the base would write the clamped scale, and a
     * mote would cross the wire three quarters the size it was sent at.
     */
    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {
        buf.writeFloat(this.color.x());
        buf.writeFloat(this.color.y());
        buf.writeFloat(this.color.z());
        buf.writeFloat(this.size);
    }

    /**
     * A copy of this shade pushed {@code amount} of the way along its own drift.
     *
     * <p>🔴 Five 月耀 threads land side by side, and five identical cyans is the one thing that makes a
     * spear of light look printed rather than forked: the eye reads an exact colour match as one object
     * stamped five times. A few percent of green and a few percent off the blue is enough to break the
     * match without any single thread ceasing to be the focus's own cyan.
     */
    public AstralMoteOptions tinted(float amount) {
        float k = Mth.clamp(amount, 0.0F, 1.0F);
        return new AstralMoteOptions(new Vector3f(
                this.color.x * (1.0F + 0.90F * k),
                this.color.y + (1.0F - this.color.y) * 0.06F * k,
                this.color.z * (1.0F - 0.09F * k)), this.size);
    }

    @Override
    public ParticleType<AstralMoteOptions> getType() {
        return TYPE;
    }
}
