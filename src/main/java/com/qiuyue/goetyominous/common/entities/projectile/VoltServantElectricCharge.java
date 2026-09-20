package com.qiuyue.goetyominous.common.entities.projectile;

import com.unusualmodding.opposing_force.entity.projectile.ElectricCharge;
import com.unusualmodding.opposing_force.entity.projectile.FrictionlessProjectile;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class VoltServantElectricCharge extends ElectricCharge {

    public VoltServantElectricCharge(EntityType<? extends FrictionlessProjectile> type, Level level) {
        super(type, level);
    }

    @Override
    protected void createExplosion(float f) {
        if (!this.level().isClientSide) {
            this.spawnElectricParticles(this, (int) (f + 1.0F + this.random.nextInt((int) f + 1)), 0.25F, 16.0F);
            VoltServantElectricExplosion explosion = new VoltServantElectricExplosion(
                    this.level(), this, this.getX(), this.getY() + 0.0625D, this.getZ(), f);
            explosion.explode();
            explosion.finalizeExplosion(true);
        }
    }
}
