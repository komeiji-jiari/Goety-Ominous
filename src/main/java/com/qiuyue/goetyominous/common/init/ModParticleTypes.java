package com.qiuyue.goetyominous.common.init;

import com.mojang.serialization.Codec;
import com.qiuyue.goetyominous.GoetyOminous;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModParticleTypes {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, GoetyOminous.MOD_ID);

    public static void init() {
        PARTICLE_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static final RegistryObject<SimpleParticleType> GUST = PARTICLE_TYPES.register("gust", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> GUST_EMITTER_SMALL = PARTICLE_TYPES.register("gust_emitter_small", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> GUST_EMITTER_LARGE = PARTICLE_TYPES.register("gust_emitter_large", () -> new SimpleParticleType(false));

    public static final RegistryObject<ParticleType<BlockParticleOption>> DUST_PILLAR = PARTICLE_TYPES.register("dust_pillar",
            () -> new ParticleType<BlockParticleOption>(true, BlockParticleOption.DESERIALIZER) {
                @Override
                public Codec<BlockParticleOption> codec() {
                    return BlockParticleOption.codec(this);
                }
            });
}
