package com.qiuyue.goetyominous.common.entities.ai.ac;

import com.Polarice3.Goety.common.entities.ally.AnimalSummon;
import com.github.alexmodguy.alexscaves.server.entity.util.LaysEggs;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;

public class ServantBreedGoal<T extends AnimalSummon & LaysEggs> extends Goal {

    private static final TargetingConditions PARTNER_TARGETING = TargetingConditions.forNonCombat().range(8.0D).ignoreLineOfSight();

    private final T animal;
    private final double speedModifier;
    private final Level level;
    @Nullable
    private T partner;
    private int eggLoveTime;

    public ServantBreedGoal(T animal, double speedModifier) {
        this.animal = animal;
        this.level = animal.level();
        this.speedModifier = speedModifier;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.animal.hasEgg() || !this.animal.isInLove()) {
            return false;
        }
        this.partner = this.getFreePartner();
        return this.partner != null;
    }

    @Override
    public boolean canContinueToUse() {
        return this.partner != null && this.partner.isAlive() && this.partner.isInLove() && this.eggLoveTime < 60;
    }

    @Override
    public void stop() {
        this.partner = null;
        this.eggLoveTime = 0;
    }

    @Override
    public void tick() {
        this.animal.getLookControl().setLookAt(this.partner, 10.0F, (float) this.animal.getMaxHeadXRot());
        this.animal.getNavigation().moveTo(this.partner, this.speedModifier);
        ++this.eggLoveTime;
        double width = Math.max(this.animal.getBbWidth() * 2.0F + 0.5F, 3.0D);
        if (this.eggLoveTime >= this.adjustedTickDelay(60) && Mth.sqrt((float) this.animal.distanceToSqr(this.partner)) < width) {
            this.animal.spawnChildFromBreeding((ServerLevel) this.level, this.partner);
        }
    }

    @Nullable
    @SuppressWarnings("unchecked")
    private T getFreePartner() {
        List<AnimalSummon> list = this.level.getNearbyEntities(AnimalSummon.class, PARTNER_TARGETING, this.animal, this.animal.getBoundingBox().inflate(8.0D));
        double d0 = Double.MAX_VALUE;
        T servant = null;
        for (AnimalSummon animal1 : list) {
            if (this.animal.canMate(animal1) && this.animal.distanceToSqr(animal1) < d0) {
                servant = (T) animal1;
                d0 = this.animal.distanceToSqr(animal1);
            }
        }
        return servant;
    }
}
