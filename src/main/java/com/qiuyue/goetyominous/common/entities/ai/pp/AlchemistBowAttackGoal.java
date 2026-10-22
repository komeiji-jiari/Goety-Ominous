package com.qiuyue.goetyominous.common.entities.ai.pp;

import com.qiuyue.goetyominous.common.entities.ally.pp.AlchemistAnimatable;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class AlchemistBowAttackGoal<T extends PathfinderMob & AlchemistAnimatable> extends Goal {

    private final T alchemist;
    private final double speedModifier;
    private final float attackRadiusSqr;
    private Path path;
    private int attackTime = -1;
    private int seeTime;
    private int avoidTime;

    public AlchemistBowAttackGoal(T alchemist) {
        this.alchemist = alchemist;
        this.speedModifier = 1.5D;
        this.attackRadiusSqr = 15.0F * 15.0F;
        this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.alchemist.getTarget();
        return target != null && target.isAlive()
                && this.alchemist.getMainHandItem().getItem() instanceof BowItem;
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.alchemist.getTarget();
        return target != null && target.isAlive()
                && this.alchemist.getMainHandItem().getItem() instanceof BowItem;
    }

    @Override
    public void tick() {
        LivingEntity target = this.alchemist.getTarget();
        if (target == null) {
            return;
        }
        double distanceSquared = this.alchemist.distanceToSqr(target);
        boolean canSee = this.alchemist.hasLineOfSight(target);
        boolean seeTimeGreaterThanZero = this.seeTime > 0;
        this.alchemist.getLookControl().setLookAt(target, 30.0F, 30.0F);
        this.alchemist.lookAt(target, 30.0F, 30.0F);
        if (this.alchemist.isUsingItem()) {
            if (!canSee && this.seeTime < -60) {
                this.alchemist.stopUsingItem();
            } else if (canSee) {
                int ticksUsing = this.alchemist.getTicksUsingItem();
                int timeToShoot = distanceSquared <= 40.0D ? Mth.floor(Mth.lerp(distanceSquared / (double) this.attackRadiusSqr, 5.0D, 20.0D)) : 20;
                if (ticksUsing >= timeToShoot) {
                    this.alchemist.stopUsingItem();
                    this.alchemist.performRangedAttack(target, BowItem.getPowerForTime(ticksUsing));
                    this.attackTime = 20;
                }
            }
        } else if (--this.attackTime <= 0 && this.seeTime >= -60) {
            this.alchemist.startUsingItem(ProjectileUtil.getWeaponHoldingHand(this.alchemist, item -> item instanceof BowItem));
        }
        if (distanceSquared > (double) this.attackRadiusSqr && this.seeTime >= 20) {
            this.path = this.alchemist.getNavigation().createPath(target, 0);
            this.alchemist.getNavigation().moveTo(this.path, this.speedModifier);
        } else if (distanceSquared < (double) this.attackRadiusSqr && this.seeTime >= 20) {
            this.alchemist.getNavigation().stop();
        }
        if (canSee != seeTimeGreaterThanZero) {
            this.seeTime = 0;
        }
        if (canSee) {
            ++this.seeTime;
        } else {
            --this.seeTime;
        }
        if (distanceSquared <= 6.0D || this.alchemist.getArrowsShot() >= 3) {
            if (target instanceof AbstractPiglin) {
                return;
            }
            if (this.avoidTime <= 0) {
                this.avoidTime = 60;
            } else {
                this.avoidTime -= 10;
            }
            if (this.alchemist.getArrowsShot() >= 3) {
                this.alchemist.setArrowsShot(0);
            }
        }
        if (this.avoidTime == 0) {
            this.alchemist.getNavigation().stop();
        }
        if (this.avoidTime < 0) {
            this.avoidTime = 0;
        }
        if (this.avoidTime > 60) {
            this.avoidTime = 60;
        }
        if (--this.avoidTime > 0) {
            Vec3 pos = this.getPosition(target);
            if (distanceSquared <= (double) this.attackRadiusSqr && pos != null && this.alchemist.getNavigation().isDone()) {
                this.path = this.alchemist.getNavigation().createPath(pos.x, pos.y, pos.z, 0);
                if (this.path != null && this.path.canReach()) {
                    this.alchemist.getNavigation().moveTo(this.path, this.speedModifier);
                    this.attackTime = -1;
                    this.alchemist.stopUsingItem();
                }
            }
        }
    }

    @Nullable
    protected Vec3 getPosition(LivingEntity target) {
        return LandRandomPos.getPosAway(this.alchemist, 5, 7, target.position());
    }

    @Override
    public void start() {
        this.alchemist.setAggressive(true);
    }

    @Override
    public void stop() {
        this.alchemist.setAggressive(false);
        this.seeTime = 0;
        this.attackTime = -1;
        this.alchemist.stopUsingItem();
        this.alchemist.getNavigation().stop();
    }
}
