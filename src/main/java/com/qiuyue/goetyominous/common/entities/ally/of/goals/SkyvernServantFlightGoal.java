package com.qiuyue.goetyominous.common.entities.ally.of.goals;

import com.Polarice3.Goety.api.entities.ally.IServant;
import com.qiuyue.goetyominous.common.entities.ally.of.SkyvernServant;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class SkyvernServantFlightGoal extends Goal {

    private static final double START_RANGE_SQR = 1024.0D;
    private static final double CRUISE_RADIUS = 12.0D;
    private static final double CRUISE_HEIGHT = 4.0D;
    private static final int RECALC_INTERVAL = 10;

    private final SkyvernServant skyvern;
    private double x;
    private double y;
    private double z;
    private int recalcCooldown;

    public SkyvernServantFlightGoal(SkyvernServant skyvern) {
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        this.skyvern = skyvern;
    }

    private boolean withinOwnerRange(double distanceSqr) {
        LivingEntity owner = this.skyvern.getTrueOwner();
        return owner == null || this.skyvern.distanceToSqr(owner) < distanceSqr;
    }

    private boolean isGuarding() {
        return this.skyvern.isGuardingArea();
    }

    @Override
    public boolean canUse() {
        if (this.skyvern.isStaying() || this.skyvern.isCommanded() || this.skyvern.isPassenger() || this.skyvern.isVehicle()) {
            return false;
        }
        LivingEntity target = this.skyvern.getTarget();
        if (target != null && target.isAlive()) {
            return false;
        }
        if (!this.isGuarding() && !this.withinOwnerRange(START_RANGE_SQR)) {
            return false;
        }
        Vec3 destination = this.getForwardLocation();
        if (destination == null) {
            return false;
        }
        this.x = destination.x;
        this.y = destination.y;
        this.z = destination.z;
        return true;
    }

    @Override
    public void start() {
        this.recalcCooldown = RECALC_INTERVAL;
        this.skyvern.getNavigation().moveTo(this.x, this.y, this.z, 1.0D);
    }

    @Override
    public void stop() {
        this.skyvern.holdMoveControl();
        this.skyvern.getNavigation().stop();
        this.x = 0.0D;
        this.y = 0.0D;
        this.z = 0.0D;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.skyvern.isStaying() || this.skyvern.isCommanded()) {
            return false;
        }
        if (this.skyvern.getTarget() != null) {
            return false;
        }
        if (!this.isGuarding() && this.skyvern.getTrueOwner() == null) {
            return false;
        }
        return this.skyvern.isFlying();
    }

    @Override
    public void tick() {
        if (--this.recalcCooldown > 0 && !this.skyvern.getNavigation().isDone()) {
            return;
        }
        this.recalcCooldown = RECALC_INTERVAL;
        Vec3 destination = this.getForwardLocation();
        if (destination == null) {
            return;
        }
        this.x = destination.x;
        this.y = destination.y;
        this.z = destination.z;
        this.skyvern.getNavigation().moveTo(this.x, this.y, this.z, 1.0D);
    }

    @Nullable
    private Vec3 getGuardLocation() {
        BlockPos bound = this.skyvern.getBoundPos();
        if (bound == null) {
            return null;
        }
        RandomSource random = this.skyvern.getRandom();
        int radius = Math.max(1, IServant.GUARDING_RANGE / 2);
        for (int i = 0; i < 10; ++i) {
            double x = (double) bound.getX() + 0.5D + (random.nextDouble() - 0.5D) * 2.0D * (double) radius;
            double z = (double) bound.getZ() + 0.5D + (random.nextDouble() - 0.5D) * 2.0D * (double) radius;
            double y = (double) bound.getY() + 3.0D + (random.nextDouble() - 0.5D) * 6.0D;
            BlockPos pos = BlockPos.containing(x, y, z);
            if (this.canBlockPosBeSeen(pos) && this.skyvern.level().isEmptyBlock(pos)) {
                return new Vec3(x, y, z);
            }
        }
        return null;
    }

    @Nullable
    private Vec3 getForwardLocation() {
        if (this.isGuarding()) {
            return this.getGuardLocation();
        }
        RandomSource random = this.skyvern.getRandom();
        Vec3 look = this.skyvern.getLookAngle();
        double forwardDistance = 32.0D + random.nextDouble() * 8.0D;
        double sideOffset = 32.0D + random.nextDouble() * 8.0D;
        double verticalOffset = (random.nextDouble() - 0.5D) * 16.0D;
        Vec3 sideways = new Vec3(-look.z, 0.0D, look.x).normalize();
        Vec3 targetPos = this.skyvern.position().add(look.scale(forwardDistance)).add(sideways.scale(sideOffset)).add(0.0D, verticalOffset, 0.0D);
        LivingEntity owner = this.skyvern.getTrueOwner();
        if (owner != null) {
            targetPos = this.cruiseAround(owner, targetPos, verticalOffset);
        }
        BlockPos pos = BlockPos.containing(targetPos);
        if (this.canBlockPosBeSeen(pos) && this.skyvern.level().isEmptyBlock(pos)) {
            return targetPos;
        }
        return this.getRandomLocation();
    }

    private Vec3 cruiseAround(LivingEntity owner, Vec3 targetPos, double verticalOffset) {
        Vec3 offset = targetPos.subtract(owner.position());
        double horizontal = Math.sqrt(offset.x * offset.x + offset.z * offset.z);
        double y = owner.getY() + CRUISE_HEIGHT + verticalOffset * 0.5D;
        if (horizontal <= CRUISE_RADIUS) {
            return new Vec3(targetPos.x, y, targetPos.z);
        }
        double scale = CRUISE_RADIUS / horizontal;
        return new Vec3(owner.getX() + offset.x * scale, y, owner.getZ() + offset.z * scale);
    }

    @Nullable
    private Vec3 getRandomLocation() {
        RandomSource random = this.skyvern.getRandom();
        BlockPos blockpos = null;
        BlockPos origin = this.skyvern.hasRestriction() ? this.skyvern.getRestrictCenter() : this.skyvern.blockPosition();
        for (int i = 0; i < 15; ++i) {
            BlockPos targetPos = origin.offset(random.nextInt(32) - 16, random.nextInt(32) - 16, random.nextInt(32) - 16);
            if (!this.canBlockPosBeSeen(targetPos) || !this.skyvern.level().isEmptyBlock(targetPos)) {
                continue;
            }
            blockpos = targetPos;
        }
        return blockpos == null ? null : new Vec3((double) blockpos.getX() + 0.5D, (double) blockpos.getY() + 0.5D, (double) blockpos.getZ() + 0.5D);
    }

    public boolean canBlockPosBeSeen(BlockPos pos) {
        double x = (double) pos.getX() + 0.5D;
        double y = (double) pos.getY() + 0.5D;
        double z = (double) pos.getZ() + 0.5D;
        BlockHitResult result = this.skyvern.level().clip(new ClipContext(new Vec3(this.skyvern.getX(), this.skyvern.getY() + (double) this.skyvern.getBbHeight(), this.skyvern.getZ()), new Vec3(x, y, z), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this.skyvern));
        double dist = result.getLocation().distanceTo(new Vec3(x, y, z));
        return dist <= 1.0D || result.getType() == HitResult.Type.MISS;
    }
}
