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
import org.joml.Vector3f;

/**
 * The options of the school's spark: a colour and a size, exactly like {@link AstralMoteOptions}. A
 * spark is the mote's opposite number — where a mote hangs and glows, a spark is thrown, arcs under
 * gravity and burns out fast — but it is serialized the same way, so the network format and the
 * {@code /particle} string form come free from the dust base.
 */
public class AstralSparkOptions extends DustParticleOptionsBase {

    public static final Codec<AstralSparkOptions> CODEC = RecordCodecBuilder.create(options ->
            options.group(ExtraCodecs.VECTOR3F.fieldOf("color").forGetter(o -> o.color),
                            Codec.FLOAT.fieldOf("scale").forGetter(o -> o.scale))
                    .apply(options, AstralSparkOptions::new));

    public static final ParticleOptions.Deserializer<AstralSparkOptions> DESERIALIZER =
            new ParticleOptions.Deserializer<>() {
                @Override
                public AstralSparkOptions fromCommand(ParticleType<AstralSparkOptions> type,
                                                      StringReader reader) throws CommandSyntaxException {
                    Vector3f color = DustParticleOptionsBase.readVector3f(reader);
                    reader.expect(' ');
                    return new AstralSparkOptions(color, reader.readFloat());
                }

                @Override
                public AstralSparkOptions fromNetwork(ParticleType<AstralSparkOptions> type,
                                                      FriendlyByteBuf buf) {
                    return new AstralSparkOptions(DustParticleOptionsBase.readVector3f(buf),
                            buf.readFloat());
                }
            };

    private static final ParticleType<AstralSparkOptions> TYPE = new ParticleType<>(false, DESERIALIZER) {
        @Override
        public Codec<AstralSparkOptions> codec() {
            return CODEC;
        }
    };

    /** The particle type itself; registered by {@link ModParticles#ASTRAL_SPARK}. */
    public static ParticleType<AstralSparkOptions> type() {
        return TYPE;
    }

    /** Hot white-gold, thrown by the solar effects on impact. */
    public static final AstralSparkOptions SOLAR =
            new AstralSparkOptions(new Vector3f(1.0F, 0.86F, 0.5F), 1.0F);
    /** Cold blue-white, thrown by the quasar, comet and white hole. */
    public static final AstralSparkOptions COLD =
            new AstralSparkOptions(new Vector3f(0.66F, 0.85F, 1.0F), 0.9F);
    /** Starlight violet, thrown by the beams and the confuser. */
    public static final AstralSparkOptions VIOLET =
            new AstralSparkOptions(new Vector3f(0.9F, 0.66F, 1.0F), 0.9F);
    /** 月耀's cyan thrown off an impact — the grit a splash needs so it is not just a clean ring. */
    public static final AstralSparkOptions LUNAR =
            new AstralSparkOptions(new Vector3f(0.12F, 0.92F, 0.95F), 1.2F);

    public AstralSparkOptions(Vector3f color, float scale) {
        super(color, scale);
    }

    @Override
    public ParticleType<AstralSparkOptions> getType() {
        return TYPE;
    }
}
