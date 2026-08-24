package com.qiuyue.goetyominous.client.particle.ac;

import com.github.alexmodguy.alexscaves.client.particle.MushroomCloudParticle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.SimpleParticleType;

/**
 * 核能苦力怕仆从 noGriefing 核爆的专属蘑菇云。
 *
 * 参考原版 Alex's Caves 的 MushroomCloudParticle,直接继承它的渲染/生长/子粒子逻辑,
 * 只覆写光照:
 * - getLightColor 恒返回满亮度(block 15 &lt;&lt; 20 | sky 15 &lt;&lt; 4),等效永远处于亮区,
 *   夜间/暗处不会渲染成纯黑。
 * - 原版那朵 MUSHROOM_CLOUD 由 NucleeperNukeProtectionHandler 在 noGriefing 时通过
 *   置位 spawnedParticle 标志抑制(反射,不用 mixin),本粒子成为唯一一朵。
 */
public class NucleeperMushroomCloudParticle extends MushroomCloudParticle {

    public NucleeperMushroomCloudParticle(ClientLevel level, double x, double y, double z, float size, boolean pink) {
        super(level, x, y, z, size, pink);
    }

    @Override
    public int getLightColor(float partialTick) {
        return 0xF000F0;
    }

    /**
     * 参数映射与原版 MUSHROOM_CLOUD 的 Factory 一致:xSpeed → size,ySpeed 非零 → pink。
     * NucleeperServant 生成时传 (size, 0.0, 0.0),即普通配色、不启用粉色变体。
     */
    public static class Provider implements ParticleProvider<SimpleParticleType> {

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new NucleeperMushroomCloudParticle(level, x, y, z, (float) xSpeed, ySpeed != 0.0);
        }
    }
}
