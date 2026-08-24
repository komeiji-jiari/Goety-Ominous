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
 * NuclearExplosionEntity 无论是否 noGriefing 都会在爆炸位置生成蘑菇云(noGriefing 只
 * 跳过方块破坏,不影响粒子)。这里注册独立粒子类型,由 NucleeperServant 在 noGriefing
 * 时直接在地表生成(见 NucleeperMushroomCloudParticle);原版那朵云则通过
 * NucleeperNukeProtectionHandler 置位 spawnedParticle 标志抑制,保证同一时刻只有一朵。
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
