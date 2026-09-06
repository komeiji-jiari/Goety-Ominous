package com.qiuyue.goetyominous.client.particle.ac;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.qiuyue.goetyominous.common.entities.ally.ac.CandicornServant;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
public class CandicornServantChargeParticle extends TextureSheetParticle {
    private final int candicornId;
    private float xRot;
    private float yRot;
    private float fadeR;
    private float fadeG;
    private float fadeB;
    private boolean passedTarget;

    protected CandicornServantChargeParticle(ClientLevel world, double x, double y, double z, int entityId, float xRot, float yRot) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        this.xd = 0.0;
        this.yd = 0.0;
        this.zd = 0.0;
        this.quadSize = 0.3f;
        this.setSize(5.0f, 5.0f);
        this.setColor(1.0f, 1.0f, 1.0f);
        this.candicornId = entityId;
        this.lifetime = 35;
        this.setOnHornPos();
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.friction = 1.0f;
        this.xRot = xRot;
        this.yRot = yRot;
        this.hasPhysics = false;
    }

    public void setFadeColor(int i) {
        this.fadeR = (float) ((i & 0xFF0000) >> 16) / 255.0f;
        this.fadeG = (float) ((i & 0xFF00) >> 8) / 255.0f;
        this.fadeB = (float) ((i & 0xFF) >> 0) / 255.0f;
    }

    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        float f = ((float) this.age - (float) (this.lifetime / 2)) / (float) this.lifetime;
        float f1 = (float) this.age / (float) this.lifetime;
        float f2 = 1.0f - 0.1f * f1;
        this.friction = 1.0f - 0.65f * f1;
        if (this.age > this.lifetime / 2) {
            this.setAlpha(1.0f - f * 2.0f);
        }
        this.rCol += (this.fadeR - this.rCol) * 0.1f;
        this.gCol += (this.fadeG - this.gCol) * 0.1f;
        this.bCol += (this.fadeB - this.bCol) * 0.1f;
        Vec3 motionVec = new Vec3(0.0, 0.0, (double) -0.05f).xRot((float) Math.toRadians(this.xRot)).yRot(-((float) Math.toRadians(this.yRot)));
        this.xd += motionVec.x * (double) f2;
        this.yd += motionVec.y * (double) f2;
        this.zd += motionVec.z * (double) f2;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.move(this.xd, this.yd, this.zd);
            this.xd *= (double) this.friction;
            this.yd *= (double) this.friction;
            this.zd *= (double) this.friction;
        }
    }

    public void setOnHornPos() {
        Entity entity;
        if (this.candicornId != -1 && (entity = this.level.getEntity(this.candicornId)) instanceof CandicornServant) {
            CandicornServant candicorn = (CandicornServant) entity;
            Vec3 chargeFocalPoint = new Vec3(0.0, (double) (candicorn.getEyeHeight() - 0.1f), (double) (candicorn.getBbWidth() + 0.8f)).yRot((float) Math.toRadians(-candicorn.getChargeYaw()));
            this.setPos(candicorn.getX() + chargeFocalPoint.x, candicorn.getY() + chargeFocalPoint.y, candicorn.getZ() + chargeFocalPoint.z);
            this.setFadeColor(candicorn.getParticleColor());
        } else {
            this.setFadeColor(16772951);
        }
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public int getLightColor(float partialTicks) {
        return 240;
    }

    public float getQuadSize(float scaleFactor) {
        float f = 8.0f;
        return this.quadSize * Mth.clamp((float) (((float) this.age + scaleFactor) * 2.0f / (float) this.lifetime), (float) 0.0f, (float) 1.0f) * f;
    }

    public void render(VertexConsumer vertexConsumer, Camera camera, float partialTick) {
        this.renderSignal(vertexConsumer, camera, partialTick, quaternionf -> quaternionf.rotateY(-((float) Math.toRadians(this.yRot))).rotateX(-((float) Math.toRadians(this.xRot))));
        this.renderSignal(vertexConsumer, camera, partialTick, quaternionf -> quaternionf.rotateY((float) (-Math.PI) - (float) Math.toRadians(this.yRot)).rotateX((float) Math.toRadians(this.xRot)));
    }

    private void renderSignal(VertexConsumer consumer, Camera camera, float partialTicks, Consumer<Quaternionf> rots) {
        Vec3 vec3 = camera.getPosition();
        float f = (float) (Mth.lerp((double) partialTicks, (double) this.xo, (double) this.x) - vec3.x());
        float f1 = (float) (Mth.lerp((double) partialTicks, (double) this.yo, (double) this.y) - vec3.y());
        float f2 = (float) (Mth.lerp((double) partialTicks, (double) this.zo, (double) this.z) - vec3.z());
        Vector3f vector3f = new Vector3f(0.5f, 0.5f, 0.5f).normalize();
        Quaternionf quaternionf = new Quaternionf().setAngleAxis(0.0f, vector3f.x(), vector3f.y(), vector3f.z());
        rots.accept(quaternionf);
        Vector3f[] avector3f = new Vector3f[]{new Vector3f(-1.0f, -1.0f, 0.0f), new Vector3f(-1.0f, 1.0f, 0.0f), new Vector3f(1.0f, 1.0f, 0.0f), new Vector3f(1.0f, -1.0f, 0.0f)};
        float f3 = this.getQuadSize(partialTicks);
        for (int i = 0; i < 4; ++i) {
            Vector3f vector3f1 = avector3f[i];
            vector3f1.rotate((Quaternionfc) quaternionf);
            vector3f1.mul(f3);
            vector3f1.add(f, f1, f2);
        }
        float f6 = this.getU0();
        float f7 = this.getU1();
        float f4 = this.getV0();
        float f5 = this.getV1();
        int j = this.getLightColor(partialTicks);
        consumer.vertex((double) avector3f[0].x(), (double) avector3f[0].y(), (double) avector3f[0].z()).uv(f7, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        consumer.vertex((double) avector3f[1].x(), (double) avector3f[1].y(), (double) avector3f[1].z()).uv(f7, f4).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        consumer.vertex((double) avector3f[2].x(), (double) avector3f[2].y(), (double) avector3f[2].z()).uv(f6, f4).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
        consumer.vertex((double) avector3f[3].x(), (double) avector3f[3].y(), (double) avector3f[3].z()).uv(f6, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            CandicornServantChargeParticle particle = new CandicornServantChargeParticle(worldIn, x, y, z, (int) xSpeed, (float) ySpeed, (float) zSpeed);
            particle.pickSprite(this.spriteSet);
            return particle;
        }
    }
}
