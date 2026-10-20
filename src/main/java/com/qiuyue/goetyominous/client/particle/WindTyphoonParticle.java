package com.qiuyue.goetyominous.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;

/**
 * 台风粒子 —— 绕一个中心点旋转、上升、向内收紧的环形风迹，参考觉醒 {@code TyphoonParticle} 的视觉
 * （唤风者脚下的那圈风），但本类**自包含**：不继承 Goety 的 {@code WindTrailParticle}，而是用 vanilla
 * 的 {@link TextureSheetParticle} 自己写旋转数学，所以不需要自定义 {@code ParticleOption} ——
 * {@code SimpleParticleType} 就够，参数编进 {@code dx/dy/dz} 里。
 *
 * <p>{@code (x, y, z)} 是旋转中心；extra 参数编码：
 * <ul>
 *   <li>{@code dx} = 初始半径</li>
 *   <li>{@code dy} = 上升速度（block/tick）</li>
 *   <li>{@code dz} = 角速度（rad/tick，正值顺时针）</li>
 * </ul>
 * 半径随年龄线性收缩到 0（台风「收紧」的收尾），alpha 随之淡出，sprite 按 age 切帧。
 */
public class WindTyphoonParticle extends TextureSheetParticle {
    private final SpriteSet sprites;
    private final double centerX;
    private final double centerZ;
    private double yaw;
    private double radius;
    private final double yawSpeed;
    private final double riseSpeed;

    private WindTyphoonParticle(ClientLevel level, double x, double y, double z,
                               double radius, double riseSpeed, double yawSpeed, SpriteSet sprites) {
        super(level, x, y, z);
        this.sprites = sprites;
        this.centerX = x;
        this.centerZ = z;
        this.yaw = this.random.nextDouble() * Math.PI * 2.0;
        this.radius = Math.max(0.05, radius);
        this.yawSpeed = yawSpeed;
        this.riseSpeed = riseSpeed;
        this.lifetime = 18 + this.random.nextInt(8);
        this.quadSize = 0.6F;
        this.setSize(0.2F, 0.2F);
        this.setSpriteFromAge(this.sprites);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public int getLightColor(float partialTick) {
        return 0xF000F0;
    }

    @Override
    public void tick() {
        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.yaw += this.yawSpeed;
        this.radius *= 0.97;
        this.y += this.riseSpeed;
        this.x = this.centerX + Math.cos(this.yaw) * this.radius;
        this.z = this.centerZ + Math.sin(this.yaw) * this.radius;
        this.alpha = 1.0F - (float) this.age / (float) this.lifetime;
        this.setSpriteFromAge(this.sprites);
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z,
                                       double dx, double dy, double dz) {
            return new WindTyphoonParticle(level, x, y, z, dx, dy, dz, this.sprites);
        }
    }
}
