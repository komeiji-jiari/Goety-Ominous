package com.qiuyue.goetyominous.common.entities.hostile;

import com.qiuyue.goetyominous.common.entities.ally.neutral.AbstractUrbhadhach;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.FleeSunGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RestrictSunGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.EnumSet;

public class UrbhadhachEntity extends AbstractUrbhadhach implements Enemy {

    public UrbhadhachEntity(EntityType<? extends AbstractUrbhadhach> type, Level level) {
        super(type, level);
        this.setHostile(true);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(3, new RestrictSunGoal(this));
        this.goalSelector.addGoal(4, new FleeSunGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new ShadeSleepGoal());
        this.goalSelector.addGoal(6, new RandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, AgeableMob.class, 10, true, false, LivingEntity::isBaby));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
    }

    @Override
    public int xpReward() {
        return 20;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            int animation = this.getAnimationState();
            if ((animation == ANIM_SIT || animation == ANIM_SLEEP)
                    && (this.getTarget() != null || this.hurtTime > 0 || !this.canRestInShade())) {
                this.triggerWakeUp();
            }
        }
    }

    private boolean canRestInShade() {
        return this.level().isDay() && !this.level().canSeeSky(this.blockPosition());
    }

    class ShadeSleepGoal extends Goal {
        private static final int SIT_DELAY = 40;
        private int shadeTicks;
        private int sitTicks;

        ShadeSleepGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return UrbhadhachEntity.this.canRestInShade()
                    && UrbhadhachEntity.this.getTarget() == null
                    && UrbhadhachEntity.this.getAnimationState() == ANIM_NONE
                    && UrbhadhachEntity.this.onGround();
        }

        @Override
        public boolean canContinueToUse() {
            return UrbhadhachEntity.this.canRestInShade()
                    && UrbhadhachEntity.this.getTarget() == null
                    && UrbhadhachEntity.this.onGround();
        }

        @Override
        public void start() {
            this.shadeTicks = 0;
            this.sitTicks = 0;
        }

        @Override
        public void tick() {
            int animation = UrbhadhachEntity.this.getAnimationState();
            if (animation == ANIM_WAKE_UP) {
                this.shadeTicks = 0;
            } else {
                ++this.shadeTicks;
            }
            if (animation == ANIM_SIT) {
                ++this.sitTicks;
            } else {
                this.sitTicks = 0;
            }
            UrbhadhachEntity.this.getNavigation().stop();
            if (animation == ANIM_NONE && this.shadeTicks > SIT_DELAY) {
                UrbhadhachEntity.this.startAnimation(ANIM_SIT, SIT_DURATION);
            } else if (animation == ANIM_SIT && this.sitTicks >= SIT_DURATION) {
                UrbhadhachEntity.this.startAnimation(ANIM_SLEEP, SLEEP_DURATION);
            }
        }
    }
}
