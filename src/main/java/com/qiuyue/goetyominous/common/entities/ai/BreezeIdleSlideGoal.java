package com.qiuyue.goetyominous.common.entities.ai;

import com.qiuyue.goetyominous.common.init.ModSounds;
import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;

public class BreezeIdleSlideGoal<T extends PathfinderMob & BreezeLike> extends Goal {
    private static final float SLIDE_SPEED = 0.6F;
    private static final int MIN_RUN_TIME = 20;
    private static final int MAX_RUN_TIME = 40;
    private static final int MIN_COOLDOWN = 40;
    private static final int MAX_COOLDOWN = 100;
    private static final int SEARCH_HORIZONTAL = 10;
    private static final int SEARCH_VERTICAL = 7;

    private final T mob;
    private int runTime;
    private int timer;
    private int cooldown;
    @Nullable private Vec3 destination;

    public BreezeIdleSlideGoal(T mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.cooldown > 0) {
            --this.cooldown;
            return false;
        }
        if (this.mob.getTarget() != null || !this.mob.onGround() || this.mob.isInWater()) {
            return false;
        }
        if (!this.mob.getNavigation().isDone() || !this.mob.isBreezeStanding() || !this.mob.canIdleSlide()) {
            return false;
        }
        Vec3 pos = LandRandomPos.getPos(this.mob, SEARCH_HORIZONTAL, SEARCH_VERTICAL);
        if (pos == null) {
            return false;
        }
        this.destination = pos;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return this.timer < this.runTime
                && this.mob.getTarget() == null
                && this.destination != null
                && !this.mob.getNavigation().isDone();
    }

    @Override
    public void start() {
        this.timer = 0;
        this.runTime = MIN_RUN_TIME + this.mob.getRandom().nextInt(MAX_RUN_TIME - MIN_RUN_TIME + 1);
        this.mob.playSound(ModSounds.BREEZE_SLIDE.get());
        this.mob.setBreezeSliding();
        this.mob.getNavigation().moveTo(this.destination.x, this.destination.y, this.destination.z, SLIDE_SPEED);
    }

    @Override
    public void tick() {
        ++this.timer;
    }

    @Override
    public void stop() {
        this.mob.getNavigation().stop();
        this.mob.setBreezeStanding();
        this.destination = null;
        this.cooldown = MIN_COOLDOWN + this.mob.getRandom().nextInt(MAX_COOLDOWN - MIN_COOLDOWN + 1);
    }
}
