package com.qiuyue.goetyominous.client.particle.ac;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.client.render.ac.RenderTremorzillaServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.TremorzillaServant;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.Vec3;

public class TremorzillaServantSteamParticle extends TextureSheetParticle {
    private final SpriteSet sprites;
    private final int tremorzillaId;
    private final Vec3 inMouthOffset;

    protected TremorzillaServantSteamParticle(ClientLevel level, double x, double y, double z, int tremorzillaId, SpriteSet sprites) {
        super(level, x, y, z, 0.0D, 0.0D, 0.0D);
        this.friction = 0.96F;
        this.speedUpWhenYMotionIsBlocked = true;
        this.sprites = sprites;
        this.quadSize *= 1.0F + this.random.nextFloat() * 3.0F;
        this.lifetime = 40 + this.random.nextInt(10);
        try {
            this.setSpriteFromAge(sprites);
        } catch (RuntimeException e) {
            this.remove();
        }
        this.hasPhysics = true;
        this.tremorzillaId = tremorzillaId;
        this.inMouthOffset = new Vec3(this.random.nextBoolean() ? 0.9F : -0.9F, 0.7F + this.random.nextFloat() * 0.3F, this.random.nextFloat() * 2.0F - 1.2F);
        Vec3 vec3 = this.getInMouthPos(1.0F);
        if (vec3 != null) {
            this.setPos(vec3.x, vec3.y, vec3.z);
        }
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.xd = 0.0D;
        this.yd = 0.0D;
        this.zd = 0.0D;
        this.gravity = -0.05F - this.random.nextFloat() * 0.05F;
    }

    public void tick() {
        super.tick();
        try {
            this.setSpriteFromAge(this.sprites);
        } catch (RuntimeException e) {
            this.remove();
            return;
        }
        float f = (float) this.age / (float) this.lifetime;
        this.setAlpha(1.0F - f);
    }

    public Vec3 getInMouthPos(float partialTick) {
        if (this.tremorzillaId != -1 && this.level.getEntity(this.tremorzillaId) instanceof TremorzillaServant entity) {
            Vec3 mouthPos = RenderTremorzillaServant.getMouthPositionFor(this.tremorzillaId);
            if (mouthPos != null) {
                Vec3 translate = mouthPos.add(this.inMouthOffset).yRot((float) (Math.PI - entity.yBodyRot * ((float) Math.PI / 180F)));
                return new Vec3(entity.getX() + translate.x, entity.getY() + translate.y, entity.getZ() + translate.z);
            }
        }
        return null;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        if (this.sprite == null) {
            this.remove();
            return;
        }
        super.render(buffer, camera, partialTicks);
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            TremorzillaServantSteamParticle particle = new TremorzillaServantSteamParticle(worldIn, x, y, z, (int) xSpeed, this.spriteSet);
            float color = 0.2F * worldIn.random.nextFloat() + 0.6F;
            particle.setColor(color, color, color);
            return particle;
        }
    }
}
