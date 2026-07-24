package com.qiuyue.someillagerservants.common.entities.ai;

import com.qiuyue.someillagerservants.common.entities.hostile.cultists.AbstractSISCultist;
import com.qiuyue.someillagerservants.common.entities.hostile.cultists.Beldam;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.util.Mth;

import java.util.EnumSet;
import java.util.List;

public class PotionGroupGoal<T extends AbstractSISCultist> extends Goal {
    private final T mob;
    private Beldam beldam;
    private final double speedModifier;

    public PotionGroupGoal(T mob, double speedModifier) {
        this.mob = mob;
        this.speedModifier = speedModifier;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        List<Beldam> beldams = this.mob.level().getEntitiesOfClass(Beldam.class,
                this.mob.getBoundingBox().inflate(8.0F, 4.0F, 8.0F));
        for (Beldam beldamEntity : beldams) {
            if (beldamEntity.isAlive() && !beldamEntity.isDeadOrDying() && !beldamEntity.isInvisible()) {
                this.beldam = beldamEntity;
            }
        }
        if (this.beldam != null) {
            return this.mob.getActiveEffects().isEmpty()
                    && this.mob.getTarget() != null
                    && this.mob.getSensing().hasLineOfSight(this.beldam);
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return this.mob.getActiveEffects().isEmpty()
                && this.mob.getTarget() != null
                && this.mob.getSensing().hasLineOfSight(this.beldam)
                && this.beldam != null
                && !this.beldam.isDeadOrDying()
                && !this.beldam.isInvisible();
    }

    @Override
    public void stop() {
        this.beldam = null;
        this.mob.getNavigation().stop();
    }

    @Override
    public void tick() {
        double d0 = this.mob.distanceToSqr(this.beldam.getX(), this.beldam.getY(), this.beldam.getZ());
        boolean flag = this.mob.getSensing().hasLineOfSight(this.beldam);

        if (flag && d0 > Mth.square(3)) {
            this.mob.getNavigation().moveTo(this.beldam, this.speedModifier);
        }
    }
}


