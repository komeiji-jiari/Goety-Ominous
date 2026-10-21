package com.qiuyue.goetyominous.common.entities.ally.mm.goals.MutantFrozenZombieServant;

import com.alexander.mutantmore.config.mutant_frozen_zombie.MutantFrozenZombieCommonConfig;
import com.alexander.mutantmore.util.PositionUtils;
import com.google.common.collect.Lists;
import com.qiuyue.goetyominous.common.entities.ally.mm.IcicleSpikeServant;
import com.qiuyue.goetyominous.common.entities.ally.mm.MutantFrozenZombieServant;
import java.util.ArrayList;
import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

public class MutantFrozenZombieIcicleSlamGoal extends Goal {
    public MutantFrozenZombieServant mob;
    @Nullable
    public LivingEntity target;
    public int nextUseTime;
    public float degreesProgress;
    public float degreesProgress2;

    public MutantFrozenZombieIcicleSlamGoal(MutantFrozenZombieServant mob) {
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
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
        return this.target != null && this.mob.tickCount >= this.nextUseTime && this.mob.snowstormTime <= 0 && (double) this.mob.distanceTo(this.target) <= MutantFrozenZombieCommonConfig.max_icicle_slam_distance.get() && !this.target.isRemoved() && !this.target.isDeadOrDying() && this.animationsUseable() && this.mob.hasLineOfSight(this.target);
    }

    public boolean canContinueToUse() {
        return this.target != null && !this.target.isRemoved() && !this.target.isDeadOrDying() && !this.animationsUseable();
    }

    public void start() {
        this.mob.iciclesAnimationTick = this.mob.iciclesAnimationLength;
        this.mob.level().broadcastEntityEvent(this.mob, (byte) 9);
        this.degreesProgress = 0.0F;
        this.degreesProgress2 = 0.0F;
    }

    public void tick() {
        this.target = this.mob.getTarget();
        this.mob.getNavigation().stop();
        if (this.target != null) {
            this.mob.getLookControl().setLookAt(this.target);
        }
        if (this.mob.iciclesAnimationTick == this.mob.iciclesAnimationActionPoint) {
            IcicleSpikeServant icicle;
            Vec3 iciclePos;
            int i;
            float f;
            float startAngle = this.mob.yBodyRot;
            ArrayList<IcicleSpikeServant> icicleSpikes = Lists.newArrayList();
            ArrayList<IcicleSpikeServant> icicleSpikes2 = Lists.newArrayList();
            for (f = startAngle; f < startAngle + 360.0F; f += 1.0F) {
                this.degreesProgress += 1.0F;
                if ((int) this.degreesProgress % 18 != 0) continue;
                for (i = 0; i < MutantFrozenZombieCommonConfig.icicle_slam_icicle_row_length.get(); ++i) {
                    iciclePos = PositionUtils.getOffsetPos(this.mob, 0.0, 0.0, 3.0 + (double) i * 1.8, 0.0F, f);
                    icicle = new IcicleSpikeServant(this.mob.level(), 10 + i / 2, this.mob);
                    icicle.moveTo(iciclePos);
                    icicle.damage = MutantFrozenZombieCommonConfig.icicle_slam_icicle_damage.get().floatValue();
                    icicle.freezingLength = MutantFrozenZombieCommonConfig.icicle_slam_freezing_length.get();
                    icicle.freezingLevel = MutantFrozenZombieCommonConfig.icicle_slam_freezing_level.get();
                    icicle.ignoresInvulTime = MutantFrozenZombieCommonConfig.ignores_invulnerability_time.get();
                    PositionUtils.moveToCorrectHeight(icicle, true);
                    ((ServerLevel) this.mob.level()).addFreshEntityWithPassengers(icicle);
                    icicleSpikes.add(icicle);
                }
            }
            for (f = startAngle; f < startAngle + 360.0F; f += 1.0F) {
                this.degreesProgress2 += 1.0F;
                if ((int) this.degreesProgress2 % 18 != 9) continue;
                for (i = 0; i < MutantFrozenZombieCommonConfig.icicle_slam_icicle_row_length.get(); ++i) {
                    iciclePos = PositionUtils.getOffsetPos(this.mob, 0.0, 0.0, 3.0 + (double) i * 1.4, 0.0F, f);
                    icicle = new IcicleSpikeServant(this.mob.level(), 80 + i / 2, this.mob);
                    icicle.moveTo(iciclePos);
                    icicle.damage = MutantFrozenZombieCommonConfig.icicle_slam_icicle_damage.get().floatValue();
                    icicle.freezingLength = MutantFrozenZombieCommonConfig.icicle_slam_freezing_length.get();
                    icicle.freezingLevel = MutantFrozenZombieCommonConfig.icicle_slam_freezing_level.get();
                    icicle.ignoresInvulTime = MutantFrozenZombieCommonConfig.ignores_invulnerability_time.get();
                    PositionUtils.moveToCorrectHeight(icicle, true);
                    ((ServerLevel) this.mob.level()).addFreshEntityWithPassengers(icicle);
                    icicleSpikes2.add(icicle);
                }
            }
            for (IcicleSpikeServant spike : icicleSpikes) {
                spike.connectedSpikes.addAll(icicleSpikes);
            }
            for (IcicleSpikeServant spike : icicleSpikes2) {
                spike.connectedSpikes.addAll(icicleSpikes2);
            }
        }
    }

    public void stop() {
        super.stop();
        int randomAddedCooldown = MutantFrozenZombieCommonConfig.max_icicle_slam_cooldown.get() - MutantFrozenZombieCommonConfig.min_icicle_slam_cooldown.get() <= 0 ? 0 : this.mob.getRandom().nextInt(MutantFrozenZombieCommonConfig.max_icicle_slam_cooldown.get() - MutantFrozenZombieCommonConfig.min_icicle_slam_cooldown.get());
        int cooldown = MutantFrozenZombieCommonConfig.min_icicle_slam_cooldown.get() + randomAddedCooldown;
        this.nextUseTime = this.mob.tickCount + cooldown;
    }

    public boolean animationsUseable() {
        return this.mob.iciclesAnimationTick <= 0;
    }
}
