package com.qiuyue.goetyominous.common.init.ac;

import com.qiuyue.goetyominous.GoetyOminous;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Alex's Caves 联动的自定义粒子类型。
 *
 * NUCLEEPER_MUSHROOM_CLOUD:核能苦力怕仆从 noGriefing 核爆的专属蘑菇云。原版
 * NuclearExplosionEntity 在 noGriefing 时会把蘑菇云粒子沉到世界底部(扫描"第一个不可
 * 破坏方块"对空气也成立),根本看不见;这里注册一个独立粒子类型,由 NucleeperServant
 * 在爆炸时直接在地表生成(见 NucleeperMushroomCloudParticle),不依赖任何 mixin。
 */
public class AcParticles {

    private static final DeferredRegister<ParticleType<?>> AC_PARTICLES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, GoetyOminous.MOD_ID);

    public static final RegistryObject<ParticleType<?>> NUCLEEPER_MUSHROOM_CLOUD =
            AC_PARTICLES.register("nucleeper_mushroom_cloud", () -> new SimpleParticleType(true));

    public static void register(IEventBus modEventBus) {
        AC_PARTICLES.register(modEventBus);
    }
}
