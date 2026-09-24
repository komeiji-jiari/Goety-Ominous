package com.qiuyue.goetyominous.common.entities.ally.of.goals;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.qiuyue.goetyominous.common.entities.ally.of.TerrorServant;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

public class TerrorServantFollowGoal extends Summoned.FollowOwnerGoal<TerrorServant> {

    public TerrorServantFollowGoal(TerrorServant servant) {
        super(servant, 1.0D, 10.0F, 2.0F);
    }

    @Override
    public boolean canContinueToUse() {
        PathNavigation navigation = this.summonedEntity.getNavigation();
        if (navigation.isDone()) {
            return false;
        }
        if (this.summonedEntity.getTarget() != null) {
            return false;
        }
        return this.owner != null && this.summonedEntity.distanceToSqr(this.owner) > (double) Mth.square(this.stopDistance);
    }

    @Override
    public void stop() {
        this.owner = null;
        this.summonedEntity.getNavigation().stop();
        this.summonedEntity.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
    }

    @Override
    public void tick() {
        if (this.owner == null) {
            return;
        }
        this.summonedEntity.getLookControl().setLookAt(this.owner, 10.0F, (float) this.summonedEntity.getMaxHeadXRot());
        if (this.summonedEntity.getControlledVehicle() != null) {
            this.summonedEntity.getNavigation().moveTo(this.owner, this.followSpeed + 0.25D);
            Entity vehicle = this.summonedEntity.getControlledVehicle();
            if (vehicle instanceof Mob mob) {
                mob.getNavigation().moveTo(this.owner, this.followSpeed + 0.25D);
            }
        } else if (--this.timeToRecalcPath <= 0) {
            this.timeToRecalcPath = 10;
            if (!this.summonedEntity.isLeashed() && !this.summonedEntity.isPassenger()) {
                boolean teleport;
                if (this.owner instanceof Mob) {
                    double distance = this.summonedEntity.distanceToSqr(this.owner);
                    teleport = distance >= Mth.square(32.0D)
                            || !this.summonedEntity.hasLineOfSight(this.owner) && distance >= Mth.square(8.0D);
                } else {
                    teleport = this.summonedEntity.distanceToSqr(this.owner) >= Mth.square(16.0D) && this.canTeleport();
                }
                if (teleport) {
                    this.tryToTeleportNearEntity();
                } else {
                    this.summonedEntity.getNavigation().moveTo(this.owner, this.followSpeed);
                }
            }
        }
    }
}
