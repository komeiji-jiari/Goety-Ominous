package com.qiuyue.goetyominous.common.entities.ai.ac;

import com.Polarice3.Goety.api.entities.ally.IServant;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class DeepOneWanderGoal extends Goal {

    private final Mob mob;
    private final IDeepOneWanderer wanderer;
    private final int chance;
    private final double speed;
    private BlockPos goal;
    private boolean groundTarget;

    public DeepOneWanderGoal(Mob mob, int chance, double speed) {
        this.setFlags(EnumSet.of(Flag.MOVE));
        this.mob = mob;
        if (!(mob instanceof IDeepOneWanderer deepOne)) {
            throw new IllegalArgumentException("DeepOneWanderGoal requires an IDeepOneWanderer entity");
        }
        this.wanderer = deepOne;
        this.chance = chance;
        this.speed = speed;
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.mob.getTarget();
        if (!this.mob.isInWaterOrBubble() || target != null && target.isAlive()) {
            return false;
        }
        if (this.chance != 0 && this.mob.getRandom().nextInt(this.chance) != 0) {
            return false;
        }
        return !this.wanderer.isTrading() && !this.isHeld();
    }

    @Override
    public boolean canContinueToUse() {
        return this.goal != null && !this.mob.getNavigation().isDone() && this.mob.getRandom().nextInt(200) != 0
                && !this.wanderer.isTrading() && !this.isHeld();
    }

    @Override
    public void start() {
        this.groundTarget = this.mob.onGround() ? this.mob.getRandom().nextFloat() < 0.7F : this.mob.getRandom().nextFloat() < 0.2F;
        this.goal = this.findSwimToPos();
    }

    @Override
    public void tick() {
        if (this.goal == null) {
            return;
        }
        this.mob.getNavigation().moveTo(this.goal.getX(), this.goal.getY(), this.goal.getZ(), this.speed);
        if (this.groundTarget) {
            if (this.mob.onGround()) {
                this.wanderer.setDeepOneSwimming(false);
            } else if (this.mob.distanceToSqr(Vec3.atCenterOf(this.goal)) < 4.0) {
                this.mob.setDeltaMovement(this.mob.getDeltaMovement().scale(0.8).add(0.0, -0.1F, 0.0));
            }
        } else {
            this.wanderer.setDeepOneSwimming(true);
        }
    }

    private boolean isHeld() {
        if (this.mob instanceof IServant servant) {
            return servant.isStaying() || servant.isCommanded();
        }
        return false;
    }

    private boolean isTargetBlocked(Vec3 target) {
        Vec3 eye = new Vec3(this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
        return this.mob.level().clip(new ClipContext(eye, target, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this.mob))
                .getType() != HitResult.Type.MISS;
    }

    private BlockPos findSwimToPos() {
        BlockPos around = this.mob.blockPosition();
        BlockPos bound = this.getGuardPos();
        int range = 18;
        if (bound != null) {
            around = bound;
            range = Math.max(6, IServant.GUARDING_RANGE);
        } else {
            BlockPos.MutableBlockPos move = new BlockPos.MutableBlockPos();
            move.set(this.mob.getX(), this.mob.getY(), this.mob.getZ());
            while (move.getY() < this.mob.level().getMaxBuildHeight() && this.mob.level().getFluidState(move).is(FluidTags.WATER)) {
                move.move(0, 5, 0);
            }
            around = around.atY(Math.max(move.getY() - 40, around.getY()));
        }
        for (int i = 0; i < 15; ++i) {
            BlockPos blockPos = around.offset(this.mob.getRandom().nextInt(range) - range / 2,
                    this.mob.getRandom().nextInt(range) - range / 2,
                    this.mob.getRandom().nextInt(range) - range / 2);
            if (!this.mob.level().getFluidState(blockPos).is(FluidTags.WATER)
                    || this.isTargetBlocked(Vec3.atCenterOf(blockPos))
                    || blockPos.getY() <= this.mob.level().getMinBuildHeight() + 1) {
                continue;
            }
            if (this.groundTarget) {
                while (this.mob.level().getFluidState(blockPos.below()).is(FluidTags.WATER)
                        && blockPos.getY() > this.mob.level().getMinBuildHeight()) {
                    blockPos = blockPos.below();
                }
            }
            return blockPos;
        }
        return around;
    }

    @Nullable
    private BlockPos getGuardPos() {
        if (this.mob instanceof IServant servant && servant.isGuardingArea()) {
            return servant.getBoundPos();
        }
        return null;
    }
}
