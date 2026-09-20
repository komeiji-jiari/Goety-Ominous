package com.qiuyue.goetyominous.common.entities.hostile;

import com.qiuyue.goetyominous.common.entities.ally.neutral.AbstractDredenEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.FleeSunGoal;
import net.minecraft.world.entity.ai.goal.RestrictSunGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;

public class DredenEntity extends AbstractDredenEntity implements Enemy {

    public DredenEntity(EntityType<? extends AbstractDredenEntity> type, Level level) {
        super(type, level);
        this.xpReward = 10;
        this.setHostile(true);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new RestrictSunGoal(this));
        this.goalSelector.addGoal(3, new FleeSunGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
    }
}
