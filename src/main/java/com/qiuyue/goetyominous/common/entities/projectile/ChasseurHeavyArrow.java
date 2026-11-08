package com.qiuyue.goetyominous.common.entities.projectile;

import com.Polarice3.Goety.common.entities.projectiles.HeavyArrow;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class ChasseurHeavyArrow extends HeavyArrow {

    private static final double MAX_DISTANCE_SQR = 72.0D * 72.0D;

    public ChasseurHeavyArrow(Level level, LivingEntity shooter) {
        super(level, shooter);
        this.setNoGravity(true);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            return;
        }
        Entity owner = this.getOwner();
        if (owner == null || !owner.isAlive() || this.distanceToSqr(owner) > MAX_DISTANCE_SQR) {
            this.discard();
        }
    }
}
