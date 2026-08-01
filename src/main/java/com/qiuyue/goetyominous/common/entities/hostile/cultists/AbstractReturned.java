package com.qiuyue.goetyominous.common.entities.hostile.cultists;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import com.Polarice3.Goety.common.entities.ally.undead.bound.AbstractBoundIllager;

public abstract class AbstractReturned extends AbstractBoundIllager {
    protected AbstractReturned(EntityType<? extends AbstractReturned> type, Level worldIn) {
        super(type, worldIn);
        this.xpReward = 10;
    }

    @Override
    protected boolean isSunSensitive() {
        return false;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide) {
            for (int i = 0; i < 2; ++i) {
                this.level().addParticle(
                        com.Polarice3.Goety.client.particles.ModParticleTypes.TOTEM_EFFECT.get(),
                        this.getRandomX(0.5),
                        this.getY() + 0.5,
                        this.getRandomZ(0.5),
                        (this.random.nextDouble() - 0.5) * 0.15D,
                        0.01D,
                        (this.random.nextDouble() - 0.5) * 0.15D);
            }
        }
    }
}