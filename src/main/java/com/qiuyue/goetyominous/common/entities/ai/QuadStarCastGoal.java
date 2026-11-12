package com.qiuyue.goetyominous.common.entities.ai;

import com.Polarice3.Goety.utils.MobUtil;
import com.qiuyue.goetyominous.common.entities.projectile.QuadStarProjectile;
import java.util.EnumSet;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;

public class QuadStarCastGoal extends Goal {

    private static final int CAST_TICKS = 14;
    private static final int RELEASE_TICK = 12;

    private final Mob mob;
    private final int cooldownTicks;
    private final double minRange;

    private int cooldown;
    private int castTick;

    public QuadStarCastGoal(Mob mob, int cooldownTicks) {
        this(mob, cooldownTicks, 4.0D);
    }

    public QuadStarCastGoal(Mob mob, int cooldownTicks, double minRange) {
        this.mob = mob;
        this.cooldownTicks = cooldownTicks;
        this.minRange = minRange;
        this.setFlags(EnumSet.of(Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.cooldown > 0) {
            --this.cooldown;
            return false;
        }
        LivingEntity target = this.mob.getTarget();
        if (target == null || !target.isAlive()) {
            return false;
        }
        if (this.mob.distanceToSqr(target) < this.minRange * this.minRange) {
            return false;
        }
        return this.mob.hasLineOfSight(target);
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.mob.getTarget();
        return this.castTick < CAST_TICKS && target != null && target.isAlive();
    }

    @Override
    public void start() {
        this.castTick = 0;
        this.mob.getNavigation().stop();
    }

    @Override
    public void stop() {
        this.cooldown = this.cooldownTicks;
        this.castTick = 0;
    }

    @Override
    public void tick() {
        LivingEntity target = this.mob.getTarget();
        if (target != null) {
            MobUtil.instaLook(this.mob, target);
        }
        if (++this.castTick == RELEASE_TICK) {
            this.release();
        }
    }

    private void release() {
        QuadStarProjectile star = new QuadStarProjectile(this.mob.level(), this.mob, this.mob.getLookAngle());
        this.mob.level().addFreshEntity(star);
        this.mob.level().playSound(null, this.mob.getX(), this.mob.getY(), this.mob.getZ(),
                SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.HOSTILE, 0.9F, 1.1F);
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}
