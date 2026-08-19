package com.qiuyue.goetyominous.common.entities.ai.ac;

import com.qiuyue.goetyominous.common.entities.ally.ac.GrottoceratopsServant;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;

/**
 * 与原版 alexscaves 的 {@code AnimalBreedEggsGoal} 行为对齐的繁殖目标：
 * <ul>
 *   <li>带着蛋时不能再繁殖（{@code !hasEgg()} 守卫）；</li>
 *   <li>近距离判定用原版宽度公式 {@code max(bbWidth*2+0.5, 3.0)}，而非 Goety 固定的 3 格；</li>
 *   <li>交配成功后经由 {@code spawnChildFromBreeding}（本模组覆写为下仆从蛋，不直接生幼崽）。</li>
 * </ul>
 * 与 Goety 自带 BreedGoal 的差异在于原版守卫与近距离判定；BRED_ANIMALS 进度因仆从非原版 Animal
 * 无法触发（会 ClassCastException），ANIMALS_BRED 统计由 spawnChildFromBreeding 内的 finalize 补上。
 */
public class ServantBreedGoal extends Goal {

    private static final TargetingConditions PARTNER_TARGETING = TargetingConditions.forNonCombat().range(8.0D).ignoreLineOfSight();

    private final GrottoceratopsServant animal;
    private final double speedModifier;
    private final Level level;
    @Nullable
    private GrottoceratopsServant partner;
    private int eggLoveTime;

    public ServantBreedGoal(GrottoceratopsServant animal, double speedModifier) {
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
    private GrottoceratopsServant getFreePartner() {
        List<GrottoceratopsServant> list = this.level.getNearbyEntities(GrottoceratopsServant.class, PARTNER_TARGETING, this.animal, this.animal.getBoundingBox().inflate(8.0D));
        double d0 = Double.MAX_VALUE;
        GrottoceratopsServant servant = null;
        for (GrottoceratopsServant s : list) {
            if (this.animal.canMate(s) && this.animal.distanceToSqr(s) < d0) {
                servant = s;
                d0 = this.animal.distanceToSqr(s);
            }
        }
        return servant;
    }
}
