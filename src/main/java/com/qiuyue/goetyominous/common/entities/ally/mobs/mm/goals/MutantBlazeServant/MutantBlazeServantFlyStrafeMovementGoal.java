package com.qiuyue.goetyominous.common.entities.ally.mobs.mm.goals.MutantBlazeServant;

import com.alexander.mutantmore.config.mutant_blaze.MutantBlazeCommonConfig;
import com.alexander.mutantmore.init.SoundEventInit;
import com.alexander.mutantmore.util.PositionUtils;
import javax.annotation.Nullable;

import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.MutantBlazeServant;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

public class MutantBlazeServantFlyStrafeMovementGoal extends Goal {
    public MutantBlazeServant mob;
    @Nullable
    public LivingEntity target;
    public int goalRunningTime;
    public int nextUseTime;
    public double sidewaysMotionSpeed = 0.0;
    public double maxSidewaysMotionSpeed;
    public double sidewaysMotionSpeedChangeAmount;
    public boolean desiredSidewaysMotionDirection;
    public Vec3 groundPos;
    public boolean stopUsing;

    public MutantBlazeServantFlyStrafeMovementGoal(MutantBlazeServant mob) {
        this.maxSidewaysMotionSpeed = (Double)MutantBlazeCommonConfig.flying_sideways_speed.get();
        this.sidewaysMotionSpeedChangeAmount = (Double)MutantBlazeCommonConfig.flying_sideways_change_speed.get();
        this.desiredSidewaysMotionDirection = false;
        this.groundPos = null;
        this.mob = mob;
        this.target = mob.getTarget();
    }

    public boolean isInterruptable() {
        return this.mob.shouldBeStationary();
    }

    public boolean requiresUpdateEveryTick() {
        return true;
    }

    public boolean canUse() {
        this.target = this.mob.getTarget();
        return !this.mob.shouldBeStationary() && this.target != null && !this.target.isRemoved() && !this.target.isDeadOrDying() && (this.mob.hasLineOfSight(this.target) && this.mob.tickCount >= this.nextUseTime && this.mob.getHealth() <= this.mob.getMaxHealth() * (((Integer)MutantBlazeCommonConfig.fly_health_threshold.get()).floatValue() / 100.0F) || this.mob.getY() <= this.target.getY() - (Double)MutantBlazeCommonConfig.fly_below_target_start_distance.get());
    }

    public boolean canContinueToUse() {
        return !this.mob.shouldBeStationary() && this.target != null && !this.target.isRemoved() && !this.target.isDeadOrDying() && !this.stopUsing;
    }

    public void start() {
        this.mob.playSound((SoundEvent)SoundEventInit.MUTANT_BLAZE_START_FLYING.get(), 2.0F, this.mob.getVoicePitch());
        this.stopUsing = false;
        this.groundPos = null;
        this.goalRunningTime = 0;
    }

    public void tick() {
        this.target = this.mob.getTarget();
        ++this.goalRunningTime;
        this.mob.getNavigation().stop();
        Vec3 sidewaysMotion;
        if (this.goalRunningTime >= (Integer)MutantBlazeCommonConfig.stop_flying_time.get() && this.groundPos == null) {
            sidewaysMotion = DefaultRandomPos.getPosAway(this.mob, (Integer)MutantBlazeCommonConfig.fly_land_search_range_horizontal.get(), (Integer)MutantBlazeCommonConfig.fly_land_search_range_vertical.get(), this.target != null ? this.target.position() : this.mob.position());
            if (sidewaysMotion != null && !this.mob.shouldCrouchAt(BlockPos.containing(sidewaysMotion.x, sidewaysMotion.y, sidewaysMotion.z))) {
                this.groundPos = sidewaysMotion;
            }
        }

        if (this.groundPos != null) {
            this.mob.setFlying(true);
            if (this.target != null) {
                this.mob.lookAt(Anchor.EYES, this.target.position());
            }

            this.mob.setDeltaMovement(0.0, 0.0, 0.0);
            double x = this.groundPos.x - this.mob.getX();
            double y = this.groundPos.y - this.mob.getY();
            double z = this.groundPos.z - this.mob.getZ();
            double d = Math.sqrt(x * x + y * y + z * z);
            this.mob.setDeltaMovement(this.mob.getDeltaMovement().add(x / d * 0.20000000298023224, y / d * 0.20000000298023224, z / d * 0.20000000298023224).scale(1.0));
            boolean blocksBelow = false;

            for(int i = 0; i < 5; ++i) {
                if (!this.mob.level().getBlockState(this.mob.blockPosition().below(i)).isAir()) {
                    blocksBelow = true;
                    break;
                }
            }

            if (blocksBelow) {
                this.stopUsing = true;
            } else if (this.mob.getY() < this.groundPos.y + (Double)MutantBlazeCommonConfig.fly_height.get() || !this.mob.level().getBlockState(this.mob.blockPosition().below()).isAir()) {
                this.mob.setDeltaMovement(this.mob.getDeltaMovement().add(0.0, (Double)MutantBlazeCommonConfig.flying_vertical_speed.get(), 0.0));
            }
        } else if (this.target != null) {
            this.mob.setFlying(true);
            this.mob.lookAt(Anchor.EYES, this.target.position());
            if (this.sidewaysMotionSpeed <= -this.maxSidewaysMotionSpeed || this.sidewaysMotionSpeed >= this.maxSidewaysMotionSpeed) {
                this.desiredSidewaysMotionDirection = !this.desiredSidewaysMotionDirection;
            }

            if (this.desiredSidewaysMotionDirection) {
                this.sidewaysMotionSpeed += this.sidewaysMotionSpeedChangeAmount;
            } else {
                this.sidewaysMotionSpeed -= this.sidewaysMotionSpeedChangeAmount;
            }

            sidewaysMotion = PositionUtils.getOffsetMotion(this.mob, this.sidewaysMotionSpeed, 0.0, 0.0, 0.0F, this.mob.yBodyRot);
            if (this.mob.getY() < this.target.getY() + (Double)MutantBlazeCommonConfig.fly_height.get() || !this.mob.level().getBlockState(this.mob.blockPosition().below()).isAir()) {
                this.mob.setDeltaMovement(this.mob.getDeltaMovement().add(0.0, (Double)MutantBlazeCommonConfig.flying_vertical_speed.get(), 0.0));
            }

            this.mob.setDeltaMovement(0.0, this.mob.getDeltaMovement().y, 0.0);
            if ((double)this.mob.distanceTo(this.target) <= (Double)MutantBlazeCommonConfig.fly_avoid_target_max_distance.get()) {
                this.mob.setDeltaMovement(this.mob.getDeltaMovement().add(PositionUtils.getOffsetMotion(this.mob, 0.0, 0.0, -(Double)MutantBlazeCommonConfig.flying_movement_speed.get(), 0.0F, this.mob.yBodyRot).add(sidewaysMotion)));
            } else if ((double)this.mob.distanceTo(this.target) >= (Double)MutantBlazeCommonConfig.fly_approach_target_min_distance.get()) {
                this.mob.setDeltaMovement(this.mob.getDeltaMovement().add(PositionUtils.getOffsetMotion(this.mob, 0.0, 0.0, (Double)MutantBlazeCommonConfig.flying_movement_speed.get(), 0.0F, this.mob.yBodyRot).add(sidewaysMotion)));
            } else {
                this.mob.setDeltaMovement(this.mob.getDeltaMovement().add(sidewaysMotion));
            }
        }

    }

    public void stop() {
        super.stop();
        this.stopUsing = false;
        this.nextUseTime = this.mob.tickCount + (Integer)MutantBlazeCommonConfig.fly_cooldown.get();
        this.mob.setFlying(false);
    }
}
