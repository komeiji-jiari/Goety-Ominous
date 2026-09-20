package com.qiuyue.goetyominous.common.entities.ally.mobs.mm.goals.MutantShulkerServant;

import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.MutantShulkerServant;
import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

public class MutantShulkerServantFollowOwnerGoal extends Goal {
    private final MutantShulkerServant mob;
    private LivingEntity owner;
    private final double followSpeed;
    private final PathNavigation navigation;
    private int timeToRecalcPath;
    private final float stopDistance;
    private final float startDistance;
    private float oldWaterCost;

    public MutantShulkerServantFollowOwnerGoal(MutantShulkerServant mob, double followSpeed, float startDistance, float stopDistance) {
        this.mob = mob;
        this.followSpeed = followSpeed;
        this.navigation = mob.getNavigation();
        this.startDistance = startDistance;
        this.stopDistance = stopDistance;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        if (!(mob.getNavigation() instanceof GroundPathNavigation) && !(mob.getNavigation() instanceof FlyingPathNavigation)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
        }
    }

    public boolean canUse() {
        LivingEntity owner = this.mob.getTrueOwner();
        if (owner == null || owner.isSpectator()) {
            return false;
        }
        if (this.mob.distanceToSqr(owner) < this.startDistance * this.startDistance) {
            return false;
        }
        if (!this.mob.isFollowing() || this.mob.isCommanded()) {
            return false;
        }
        if (this.mob.getTarget() != null) {
            return false;
        }
        this.owner = owner;
        return true;
    }

    public boolean canContinueToUse() {
        return !this.navigation.isDone()
                && this.mob.getTarget() == null
                && this.mob.distanceToSqr(this.owner) > this.stopDistance * this.stopDistance;
    }

    public void start() {
        this.timeToRecalcPath = 0;
        this.oldWaterCost = this.mob.getPathfindingMalus(BlockPathTypes.WATER);
        this.mob.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
    }

    public void stop() {
        this.owner = null;
        this.navigation.stop();
        this.mob.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
    }

    public void tick() {
        if (this.owner == null) {
            return;
        }
        this.mob.getLookControl().setLookAt(this.owner, 10.0F, (float)this.mob.getMaxHeadXRot());
        if (this.mob.getControlledVehicle() != null) {
            this.navigation.moveTo(this.owner, this.followSpeed + 0.25);
            if (this.mob.getControlledVehicle() instanceof Mob mob) {
                mob.getNavigation().moveTo(this.owner, this.followSpeed + 0.25);
            }
            return;
        }
        if (--this.timeToRecalcPath > 0) {
            return;
        }
        this.timeToRecalcPath = 10;
        if (this.mob.isLeashed() || this.mob.isPassenger()) {
            return;
        }

        double range = this.owner instanceof Mob ? 32.0D : 16.0D;
        if (this.mob.distanceToSqr(this.owner) >= Mth.square(range)) {
            this.tryToTeleportNearEntity();
        } else {
            this.navigation.moveTo(this.owner, this.followSpeed);
        }
    }

    protected void tryToTeleportNearEntity() {
        BlockPos blockpos = this.owner.blockPosition();
        for (int i = 0; i < 10; ++i) {
            int j = this.mob.getRandom().nextInt(7) - 3;
            int k = this.mob.getRandom().nextInt(3) - 1;
            int l = this.mob.getRandom().nextInt(7) - 3;
            if (this.tryToTeleportToLocation(blockpos.getX() + j, blockpos.getY() + k, blockpos.getZ() + l)) {
                return;
            }
        }
    }

    protected boolean tryToTeleportToLocation(int x, int y, int z) {
        if (Math.abs(x - this.owner.getX()) < 2.0D && Math.abs(z - this.owner.getZ()) < 2.0D) {
            return false;
        } else {
            BlockPos pos = new BlockPos(x, y, z);
            BlockPathTypes pathnodetype = WalkNodeEvaluator.getBlockPathTypeStatic(this.mob.level(), pos.mutable());
            if (pathnodetype != BlockPathTypes.WALKABLE) {
                return false;
            } else {
                BlockPos blockpos = pos.subtract(this.mob.blockPosition());
                if (this.mob.level().noCollision(this.mob, this.mob.getBoundingBox().move(blockpos))) {
                    this.mob.moveTo(x + 0.5D, y, z + 0.5D, this.mob.getYRot(), this.mob.getXRot());
                    this.navigation.stop();
                    return true;
                }
                return false;
            }
        }
    }
}
