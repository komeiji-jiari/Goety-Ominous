package com.qiuyue.goetyominous.common.init.ac;

import com.qiuyue.goetyominous.GoetyOminous;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class AcParticles {

    private static final DeferredRegister<ParticleType<?>> AC_PARTICLES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, GoetyOminous.MOD_ID);

    public static final RegistryObject<ParticleType<?>> NUCLEEPER_MUSHROOM_CLOUD =
            AC_PARTICLES.register("nucleeper_mushroom_cloud", () -> new SimpleParticleType(true));

    public static void register(IEventBus modEventBus) {
        AC_PARTICLES.register(modEventBus);
    }
}
