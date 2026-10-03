package com.qiuyue.goetyominous.common.entities.ai.ac;

import com.Polarice3.Goety.api.entities.ally.IServant;
import com.qiuyue.goetyominous.common.entities.ally.ac.GumWormServant;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class GumWormServantLeapRandomlyGoal extends Goal {

    private final GumWormServant entity;
    private double x;
    private double y;
    private double z;
    private boolean hasLept;
    private float leapRot;
    private int leapFor;
    private int maxLeapTime;
    private float leapHeight;
    private float leapRange;

    public GumWormServantLeapRandomlyGoal(GumWormServant worm) {
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        this.entity = worm;
    }

    @Override
    public boolean canUse() {
        LivingEntity attackTarget = this.entity.getTarget();
        if (!this.entity.onGround() || this.entity.isLeaping() || this.entity.isRidingMode() || this.entity.isStaying() || this.entity.isGuardingArea() || attackTarget != null && attackTarget.isAlive()) {
            return false;
        }
        Vec3 target = this.findLeapFromPosition();
        if (target == null) {
            return false;
        }
        this.x = target.x;
        this.y = target.y;
        this.z = target.z;
        return true;
    }

    @Override
    public void start() {
        this.hasLept = false;
        this.maxLeapTime = 15 + this.entity.getRandom().nextInt(10);
        this.leapFor = 0;
        this.leapHeight = 0.4F + this.entity.getRandom().nextFloat() * 0.25F;
        this.leapRange = 0.3F + this.entity.getRandom().nextFloat() * 0.2F;
    }

    @Override
    public void stop() {
        this.entity.getNavigation().stop();
        LivingEntity attackTarget = this.entity.getTarget();
        if (attackTarget == null || !attackTarget.isAlive()) {
            this.entity.setLeaping(false);
        }
    }

    @Override
    public boolean canContinueToUse() {
        if (this.hasLept) {
            return this.leapFor > 0;
        }
        return this.entity.getNavigation().isInProgress() && !this.entity.getNavigation().isStuck() && !this.hasLept && !this.entity.isRidingMode();
    }

    @Override
    public void tick() {
        if (this.hasLept) {
            this.entity.getNavigation().stop();
            this.entity.setLeaping(true);
            float forceDown = (float) this.leapFor / (float) this.maxLeapTime < 0.5F ? -0.45F : 0.0F;
            Vec3 leapDelta = new Vec3(0.0D, Math.sin((double) ((float) this.leapFor / (float) this.maxLeapTime) * Math.PI) * (double) this.leapHeight + (double) forceDown, (double) this.leapRange)
                    .yRot((float) (-Math.toRadians(this.leapRot)));
            this.entity.setDeltaMovement(this.entity.getDeltaMovement().add(leapDelta));
            this.entity.setYRot(this.leapRot);
            if (this.leapFor > 0) {
                --this.leapFor;
            }
        } else {
            this.entity.getNavigation().moveTo(this.x, this.y, this.z, 1.3D);
            if (this.entity.distanceToSqr(this.x, this.y, this.z) < 18.0D) {
                this.hasLept = true;
                this.leapFor = this.maxLeapTime;
                this.leapRot = this.entity.getYRot();
                this.entity.getNavigation().stop();
                this.entity.setDeltaMovement(this.entity.getDeltaMovement().add(0.0D, 0.4D, 0.0D));
            }
        }
    }

    private Vec3 findLeapFromPosition() {
        BlockPos.MutableBlockPos check = new BlockPos.MutableBlockPos();
        boolean guarding = this.entity.isGuardingArea();
        BlockPos origin = guarding ? this.entity.getBoundPos() : this.entity.blockPosition();
        int range = guarding ? Math.max(6, IServant.GUARDING_RANGE / 2) : 16;
        for (int i = 0; i < 20; ++i) {
            check.set(origin);
            check.move(this.entity.getRandom().nextInt(2 * range + 1) - range, this.entity.getRandom().nextInt(2 * range + 1) - range, this.entity.getRandom().nextInt(2 * range + 1) - range);
            if (check.getY() < this.entity.level().getMinBuildHeight() || !this.entity.level().isLoaded(check)) {
                break;
            }
            while (this.entity.level().isEmptyBlock(check) && check.getY() > this.entity.level().getMinBuildHeight()) {
                check.move(0, -1, 0);
            }
            while (!this.entity.level().isEmptyBlock(check) && check.getY() < this.entity.level().getMaxBuildHeight()) {
                check.move(0, 1, 0);
            }
            while (check.getY() < this.entity.level().getMinBuildHeight() + 1) {
                check.move(0, 1, 0);
            }
            if (!this.entity.level().isEmptyBlock(check)) {
                continue;
            }
            return Vec3.atCenterOf(check.immutable().below());
        }
        return null;
    }
}
