package com.qiuyue.goetyominous.client.particle.ac;

import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.ClientProxy;
import com.github.alexmodguy.alexscaves.client.particle.MushroomCloudParticle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.SimpleParticleType;


public class NucleeperMushroomCloudParticle extends MushroomCloudParticle {

    public NucleeperMushroomCloudParticle(ClientLevel level, double x, double y, double z, float size, boolean pink) {
        super(level, x, y, z, size, pink);
    }

    @Override
    public void tick() {
        super.tick();
        // 父类 MushroomCloudParticle.tick() 每帧会把 renderNukeSkyDarkFor 写到 AC 客户端代理,
        // 触发核爆屏幕震动(renderNukeSkyDarkFor>0 → ClientEvents.computeCameraAngles 加 1.5F 基础震动)
        // 与天空变暗。仆从只想要地表蘑菇云且不摇晃,故清零该字段;
        // renderNukeFlashFor(白闪)与 muteNonNukeSoundsFor(其它声音静音)保留父类行为。
        if (AlexsCaves.PROXY instanceof ClientProxy proxy) {
            proxy.renderNukeSkyDarkFor = 0;
        }
    }

    @Override
    public int getLightColor(float partialTick) {
        return 0xF000F0;
    }


    public static class Provider implements ParticleProvider<SimpleParticleType> {

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new NucleeperMushroomCloudParticle(level, x, y, z, (float) xSpeed, ySpeed != 0.0);
        }
    }
}
