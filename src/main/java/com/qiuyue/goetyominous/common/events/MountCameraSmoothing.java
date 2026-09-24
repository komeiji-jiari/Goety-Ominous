package com.qiuyue.goetyominous.common.events;

import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ViewportEvent;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public final class MountCameraSmoothing {

    private static final double MAX_PUSH = 8.0;

    private MountCameraSmoothing() {
    }

    public static void apply(ViewportEvent.ComputeCameraAngles event) {
        Camera camera = event.getCamera();
        if (!camera.isDetached()) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        Entity cameraEntity = minecraft.getCameraEntity();
        if (cameraEntity == null || !cameraEntity.isPassenger()) {
            return;
        }
        float partialTick = minecraft.getPartialTick();
        boolean mirrored = minecraft.options.getCameraType() == CameraType.THIRD_PERSON_FRONT;
        float yaw = cameraEntity.getViewYRot(partialTick) + (mirrored ? 180.0F : 0.0F);
        float pitch = cameraEntity.getViewXRot(partialTick) * (mirrored ? -1.0F : 1.0F);
        Vector3f forwards = forwards(yaw, pitch);
        Vec3 eye = cameraEntity.getEyePosition(partialTick);
        double push = maxZoom(cameraEntity, eye, forwards);
        Vec3 target = eye.add(-forwards.x() * push, -forwards.y() * push, -forwards.z() * push);
        Vec3 delta = target.subtract(camera.getPosition());
        camera.move(dot(delta, camera.getLookVector()), dot(delta, camera.getUpVector()), dot(delta, camera.getLeftVector()));
        event.setYaw(yaw);
        event.setPitch(pitch);
        event.setRoll(0.0F);
    }

    private static Vector3f forwards(float yaw, float pitch) {
        Quaternionf rotation = new Quaternionf().rotationYXZ(-yaw * Mth.DEG_TO_RAD, pitch * Mth.DEG_TO_RAD, 0.0F);
        return new Vector3f(0.0F, 0.0F, 1.0F).rotate(rotation);
    }

    private static double maxZoom(Entity entity, Vec3 eye, Vector3f forwards) {
        double distance = MAX_PUSH;
        for (int i = 0; i < 8; ++i) {
            float fx = (float) ((i & 1) * 2 - 1) * 0.1F;
            float fy = (float) ((i >> 1 & 1) * 2 - 1) * 0.1F;
            float fz = (float) ((i >> 2 & 1) * 2 - 1) * 0.1F;
            Vec3 from = eye.add(fx, fy, fz);
            Vec3 to = new Vec3(eye.x - forwards.x() * MAX_PUSH + fx, eye.y - forwards.y() * MAX_PUSH + fy, eye.z - forwards.z() * MAX_PUSH + fz);
            HitResult hit = entity.level().clip(new ClipContext(from, to, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, entity));
            if (hit.getType() != HitResult.Type.MISS) {
                double d = hit.getLocation().distanceTo(eye);
                if (d < distance) {
                    distance = d;
                }
            }
        }
        return Math.max(distance, 0.0D);
    }

    private static double dot(Vec3 vec, Vector3f axis) {
        return vec.x * axis.x() + vec.y * axis.y() + vec.z * axis.z();
    }
}
