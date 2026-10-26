package com.qiuyue.goetyominous.client.item;

import com.qiuyue.goetyominous.common.items.explore.ExpeditionCompassItem;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class TravelerCompassAngleProperty implements ClampedItemPropertyFunction {

    public static final TravelerCompassAngleProperty INSTANCE = new TravelerCompassAngleProperty();

    private TravelerCompassAngleProperty() {
    }

    @Override
    public float unclampedCall(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
        Entity viewer = entity != null ? entity : stack.getEntityRepresentation();
        if (viewer == null) {
            return 0.0F;
        }
        if (level == null) {
            if (viewer.level() instanceof ClientLevel clientLevel) {
                level = clientLevel;
            } else {
                return 0.0F;
            }
        }

        Set<UUID> bound = new HashSet<>(ExpeditionCompassItem.getBound(stack));
        if (bound.isEmpty()) {
            return 0.0F;
        }

        Entity nearest = null;
        double best = Double.MAX_VALUE;
        for (Entity candidate : level.entitiesForRendering()) {
            if (candidate == viewer
                    || !(candidate instanceof LivingEntity living)
                    || !living.isAlive()
                    || !bound.contains(candidate.getUUID())) {
                continue;
            }
            double dx = candidate.getX() - viewer.getX();
            double dz = candidate.getZ() - viewer.getZ();
            double distance = dx * dx + dz * dz;
            if (distance < best) {
                best = distance;
                nearest = candidate;
            }
        }
        if (nearest == null) {
            return 0.0F;
        }

        double angle = Math.atan2(nearest.getZ() - viewer.getZ(), nearest.getX() - viewer.getX()) / (Math.PI * 2.0);
        double visual = Mth.positiveModulo(viewer.getVisualRotationYInDegrees() / 360.0F, 1.0);
        return Mth.positiveModulo((float) (0.5 - (visual - 0.25 - angle)), 1.0F);
    }
}
