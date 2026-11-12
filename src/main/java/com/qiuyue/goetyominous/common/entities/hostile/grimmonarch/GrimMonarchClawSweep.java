package com.qiuyue.goetyominous.common.entities.hostile.grimmonarch;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class GrimMonarchClawSweep {
    private static final double PADDING = 0.7D;
    private static final double CLOSE_RADIUS = 2.0D;
    private static final double CLOSE_HEIGHT = 5.5D;
    private static final int STEPS = 4;

    private GrimMonarchClawSweep() {
    }

    public static void hitWindow(GrimMonarchEntity boss, String key, float old, float now,
                                 double damageMultiplier, float windowStart, float windowEnd,
                                 boolean combo) {
        float from = Math.max(old, windowStart);
        float to = Math.min(now, windowEnd);
        if (to <= from || now <= old || boss.level().isClientSide) {
            return;
        }
        float current = boss.actionSeconds(0.0F);
        float previous = Math.max(0.0F, current - 0.05F);
        Vec3 currentPos = boss.position();
        Vec3 previousPos = new Vec3(boss.xo, boss.yo, boss.zo);
        float currentYaw = boss.getYRot();
        float previousYaw = boss.yRotO;
        List<GrimMonarchClawGeometry.Segment> segments = new ArrayList<>();
        List<GrimMonarchClawGeometry.Segment> last = null;
        for (int step = 0; step <= STEPS; ++step) {
            float fraction = step / (float) STEPS;
            float time = previous + (current - previous) * fraction;
            Vec3 origin = previousPos.lerp(currentPos, fraction);
            float yaw = previousYaw + Mth.wrapDegrees(currentYaw - previousYaw) * fraction;
            List<GrimMonarchClawGeometry.Segment> frame =
                    GrimMonarchClawGeometry.segments(boss, time, origin, yaw);
            if (last != null) {
                int count = Math.min(last.size(), frame.size());
                for (int index = 0; index < count; ++index) {
                    segments.add(new GrimMonarchClawGeometry.Segment(
                            last.get(index).from(), frame.get(index).from()));
                    segments.add(new GrimMonarchClawGeometry.Segment(
                            last.get(index).to(), frame.get(index).to()));
                }
            }
            segments.addAll(frame);
            last = frame;
        }
        if (segments.isEmpty()) {
            return;
        }
        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double minZ = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        double maxZ = Double.NEGATIVE_INFINITY;
        for (GrimMonarchClawGeometry.Segment segment : segments) {
            minX = Math.min(minX, Math.min(segment.from().x, segment.to().x));
            minY = Math.min(minY, Math.min(segment.from().y, segment.to().y));
            minZ = Math.min(minZ, Math.min(segment.from().z, segment.to().z));
            maxX = Math.max(maxX, Math.max(segment.from().x, segment.to().x));
            maxY = Math.max(maxY, Math.max(segment.from().y, segment.to().y));
            maxZ = Math.max(maxZ, Math.max(segment.from().z, segment.to().z));
        }
        if (combo) {
            minX = Math.min(minX, boss.getX() - CLOSE_RADIUS);
            minY = Math.min(minY, boss.getY());
            minZ = Math.min(minZ, boss.getZ() - CLOSE_RADIUS);
            maxX = Math.max(maxX, boss.getX() + CLOSE_RADIUS);
            maxY = Math.max(maxY, boss.getY() + CLOSE_HEIGHT);
            maxZ = Math.max(maxZ, boss.getZ() + CLOSE_RADIUS);
        }
        AABB search = new AABB(
                minX - PADDING, minY - PADDING, minZ - PADDING,
                maxX + PADDING, maxY + PADDING, maxZ + PADDING);
        List<LivingEntity> candidates = boss.level().getEntitiesOfClass(LivingEntity.class, search, boss::validVictim);
        if (candidates.isEmpty()) {
            return;
        }
        Set<UUID> victims = boss.clawVictims.computeIfAbsent(key, value -> new HashSet<>());
        for (LivingEntity victim : candidates) {
            if (victims.contains(victim.getUUID()) || !boss.hasLineOfSight(victim)) {
                continue;
            }
            AABB raw = victim.getBoundingBox();
            boolean hit = combo && GrimMonarchCombatMath.boxDistanceSquaredXZ(
                    boss.getX(), boss.getZ(), raw.minX, raw.minZ, raw.maxX, raw.maxZ)
                    <= CLOSE_RADIUS * CLOSE_RADIUS
                    && raw.maxY >= boss.getY()
                    && raw.minY <= boss.getY() + CLOSE_HEIGHT;
            if (!hit) {
                AABB box = raw.inflate(PADDING);
                for (GrimMonarchClawGeometry.Segment segment : segments) {
                    if (box.clip(segment.from(), segment.to()).isPresent()) {
                        hit = true;
                        break;
                    }
                }
            }
            if (!hit) {
                continue;
            }
            victims.add(victim.getUUID());
            GrimMonarchCombat.hit(boss, victim, damageMultiplier, 1.1D, true, combo, combo, boss.position());
        }
    }
}
