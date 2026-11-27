package com.qiuyue.goetyominous.client.particle;

import com.Polarice3.Goety.client.particles.MCDEffectOption;
import com.Polarice3.Goety.client.render.ModRenderType;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.ParticleUtil;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class ShamanSoulParticle extends Particle {
    private static final float[] SOUL_GREEN_SHADER = {800.0F, 748.13F, 616.85F, 442.67F, 262.1F, 111.66F, 27.84F, 18.41F, 14.42F, 10.93F, 6.94F, 2.95F, 2.0F, 2.0F, 2.0F, 2.0F, 2.0F};
    private static final float[] SOUL_BLUE_SHADER = {1200.0F, 1125.09F, 935.5F, 683.96F, 423.19F, 205.92F, 84.88F, 65.17F, 50.06F, 36.83F, 21.71F, 6.59F, 3.0F, 3.0F, 3.0F, 3.0F, 3.0F};
    private static final float[] TRAIL_RGB = ColorUtil.toLdr(0.33F, 1.920377F, 2.5F);
    private static final float[] HUE = {0.0F, 0.6F, 1.0F};
    private static final int MAX_POINTS = 30;
    private static final float TRAIL_HALF_WIDTH = 0.15F;
    private static final float TRAIL_SECONDS = 8.0F;

    private final int targetEntityId;
    private double lastTargetX;
    private double lastTargetY;
    private double lastTargetZ;
    private final float width;
    private int soulLife;
    private final double[] hX = new double[MAX_POINTS];
    private final double[] hY = new double[MAX_POINTS];
    private final double[] hZ = new double[MAX_POINTS];
    private final float[] hT = new float[MAX_POINTS];
    private int hCount;

    protected ShamanSoulParticle(ClientLevel level, double x, double y, double z, MCDEffectOption option) {
        super(level, x, y, z);
        this.targetEntityId = (int) option.a;
        this.lastTargetX = x;
        this.lastTargetY = y;
        this.lastTargetZ = z;
        this.width = option.size;
        this.soulLife = Math.max(option.life, 1);
        this.lifetime = this.soulLife + 10;
        this.hasPhysics = false;
        this.friction = 1.0F;
        this.gravity = 0.0F;
        this.pushPoint(0.0F);
        this.updateColor(0.0F);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ModRenderType.PARTICLE_SOUL;
    }

    @Override
    public boolean shouldCull() {
        return false;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }
        if (this.age > this.soulLife) {
            return;
        }
        Entity entity = this.level.getEntity(this.targetEntityId);
        if (entity instanceof LivingEntity living && living.isAlive()) {
            this.lastTargetX = living.getX();
            this.lastTargetY = living.getY() + living.getBbHeight() * 0.5D;
            this.lastTargetZ = living.getZ();
        }
        double targetX = this.lastTargetX;
        double targetY = this.lastTargetY;
        double targetZ = this.lastTargetZ;
        double dx = targetX - this.x;
        double dy = targetY - this.y;
        double dz = targetZ - this.z;
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance > 0.1D) {
            double step = Math.min(0.45D, distance * 0.3D);
            this.xd = dx / distance * step;
            this.yd = dy / distance * step;
            this.zd = dz / distance * step;
            this.setPos(this.x + this.xd, this.y + this.yd, this.z + this.zd);
        } else {
            this.setPos(targetX, targetY, targetZ);
            this.xd = 0.0D;
            this.yd = 0.0D;
            this.zd = 0.0D;
            this.soulLife = Math.min(this.soulLife, this.age + 3);
            this.lifetime = Math.min(this.lifetime, this.soulLife + 10);
        }
        this.pushPoint((float) (this.age - 1));
        this.updateColor((float) this.age / (float) this.soulLife);
    }

    private void pushPoint(float time) {
        int count = Math.min(this.hCount, MAX_POINTS - 1);
        System.arraycopy(this.hX, 0, this.hX, 1, count);
        System.arraycopy(this.hY, 0, this.hY, 1, count);
        System.arraycopy(this.hZ, 0, this.hZ, 1, count);
        System.arraycopy(this.hT, 0, this.hT, 1, count);
        this.hX[0] = this.x;
        this.hY[0] = this.y;
        this.hZ[0] = this.z;
        this.hT[0] = time;
        this.hCount = count + 1;
    }

    private void updateColor(float progress) {
        float blue = ColorUtil.sample(SOUL_BLUE_SHADER, progress);
        float green = ColorUtil.sample(SOUL_GREEN_SHADER, progress);
        ParticleUtil.setHdrColor(this, HUE[0] * blue, HUE[1] * green, HUE[2] * blue);
        this.alpha = 1.0F;
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        Vec3 cameraPos = camera.getPosition();
        float renderTime = (float) (this.age - 1) + partialTicks;
        boolean alive = this.age <= this.soulLife;
        Vector3f head = new Vector3f(
                (float) (Mth.lerp(partialTicks, this.xo, this.x) - cameraPos.x),
                (float) (Mth.lerp(partialTicks, this.yo, this.y) - cameraPos.y),
                (float) (Mth.lerp(partialTicks, this.zo, this.z) - cameraPos.z));
        this.renderTrail(buffer, camera, cameraPos, head, alive, renderTime);
        if (alive) {
            this.renderLeader(buffer, camera, head);
        }
    }

    private void renderTrail(VertexConsumer buffer, Camera camera, Vec3 cameraPos, Vector3f head, boolean alive, float renderTime) {
        Vector3f[] points = new Vector3f[32];
        float[] ages = new float[32];
        int count = 0;
        if (alive) {
            points[count] = head;
            ages[count++] = 0.0F;
        }
        for (int i = 0; i < this.hCount && count < points.length; ++i) {
            float age = renderTime - this.hT[i];
            if (age <= 0.0F && alive) {
                continue;
            }
            Vector3f point = new Vector3f((float) (this.hX[i] - cameraPos.x),
                    (float) (this.hY[i] - cameraPos.y), (float) (this.hZ[i] - cameraPos.z));
            if (age >= TRAIL_SECONDS) {
                if (count <= 0) {
                    break;
                }
                float ratio = (TRAIL_SECONDS - ages[count - 1]) / Math.max(age - ages[count - 1], 1.0E-4F);
                points[count] = new Vector3f(points[count - 1]).lerp(point, Mth.clamp(ratio, 0.0F, 1.0F));
                ages[count++] = TRAIL_SECONDS;
                break;
            }
            points[count] = point;
            ages[count++] = Math.max(age, 0.0F);
        }
        if (count < 2) {
            return;
        }
        float[] u = new float[count];
        float total = 0.0F;
        for (int i = 1; i < count; ++i) {
            u[i] = total += points[i].distance(points[i - 1]);
        }
        if (total < 1.0E-4F) {
            return;
        }
        for (int i = 1; i < count; ++i) {
            u[i] = u[i] / total;
        }
        Vector3f[] sides = new Vector3f[count];
        for (int i = 0; i < count; ++i) {
            Vector3f tangent = new Vector3f(points[Math.max(i - 1, 0)]).sub(points[Math.min(i + 1, count - 1)]);
            Vector3f side = tangent.cross(new Vector3f(points[i]).negate(), new Vector3f());
            if (side.lengthSquared() < 1.0E-10F) {
                side = i > 0 ? new Vector3f(sides[i - 1]) : new Vector3f(camera.getLeftVector());
            }
            sides[i] = side.normalize().mul(TRAIL_HALF_WIDTH);
        }
        for (int i = 0; i < count - 1; ++i) {
            if (points[i].distanceSquared(points[i + 1]) < 1.0E-8F) {
                continue;
            }
            quad(buffer, 1,
                    new Vector3f(points[i]).add(sides[i]), u[i], 0.0F,
                    new Vector3f(points[i + 1]).add(sides[i + 1]), u[i + 1], 0.0F,
                    new Vector3f(points[i + 1]).sub(sides[i + 1]), u[i + 1], 1.0F,
                    new Vector3f(points[i]).sub(sides[i]), u[i], 1.0F,
                    TRAIL_RGB[0], TRAIL_RGB[1], TRAIL_RGB[2], 1.0F);
        }
    }

    private void renderLeader(VertexConsumer buffer, Camera camera, Vector3f center) {
        Vector3f axis = new Vector3f((float) this.xd, (float) this.yd, (float) this.zd);
        float speed = axis.length();
        if (speed < 1.0E-4F) {
            axis.set(0.0F, 1.0F, 0.0F);
        } else {
            axis.div(speed);
        }
        Vector3f side = axis.cross(new Vector3f(center).negate(), new Vector3f());
        if (side.lengthSquared() < 1.0E-10F) {
            side.set(camera.getLeftVector());
        }
        side.normalize();
        float halfWidth = this.width * 0.5F;
        float halfLength = this.width * Mth.lerp(Mth.clamp(speed / 3.0F, 0.0F, 1.0F), 1.5F, 2.0F) * 0.5F;
        Vector3f top = new Vector3f(axis).mul(halfLength).add(center);
        Vector3f bottom = new Vector3f(axis).mul(-halfLength).add(center);
        Vector3f widthOffset = new Vector3f(side).mul(halfWidth);
        quad(buffer, 0,
                new Vector3f(top).sub(widthOffset), 0.0F, 0.0F,
                new Vector3f(top).add(widthOffset), 1.0F, 0.0F,
                new Vector3f(bottom).add(widthOffset), 1.0F, 1.0F,
                new Vector3f(bottom).sub(widthOffset), 0.0F, 1.0F,
                this.rCol, this.gCol, this.bCol, this.alpha);
    }

    private static void quad(VertexConsumer buffer, int mode,
                             Vector3f p0, float u0, float v0, Vector3f p1, float u1, float v1,
                             Vector3f p2, float u2, float v2, Vector3f p3, float u3, float v3,
                             float red, float green, float blue, float alpha) {
        buffer.vertex(p0.x, p0.y, p0.z).uv(u0, v0).color(red, green, blue, alpha).uv2(mode, 0).endVertex();
        buffer.vertex(p1.x, p1.y, p1.z).uv(u1, v1).color(red, green, blue, alpha).uv2(mode, 0).endVertex();
        buffer.vertex(p2.x, p2.y, p2.z).uv(u2, v2).color(red, green, blue, alpha).uv2(mode, 0).endVertex();
        buffer.vertex(p3.x, p3.y, p3.z).uv(u3, v3).color(red, green, blue, alpha).uv2(mode, 0).endVertex();
    }

    public static class Provider implements ParticleProvider<MCDEffectOption> {
        @Override
        public Particle createParticle(MCDEffectOption option, ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
            return new ShamanSoulParticle(level, x, y, z, option);
        }
    }
}
