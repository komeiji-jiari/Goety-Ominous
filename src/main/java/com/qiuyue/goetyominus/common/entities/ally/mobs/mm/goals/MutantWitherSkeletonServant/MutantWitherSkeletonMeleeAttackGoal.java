package com.qiuyue.goetyominus.common.entities.ally.mobs.mm.goals.MutantWitherSkeletonServant;

import com.alexander.mutantmore.config.mutant_wither_skeleton.MutantWitherSkeletonCommonConfig;
import com.alexander.mutantmore.init.SoundEventInit;
import com.alexander.mutantmore.util.MiscUtils;
import com.qiuyue.goetyominus.common.entities.ally.mobs.mm.MutantWitherSkeletonServant;
import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public class MutantWitherSkeletonMeleeAttackGoal extends Goal {
    public MutantWitherSkeletonServant mob;
    @Nullable
    public LivingEntity target;
    public int nextUseTime;

    public MutantWitherSkeletonMeleeAttackGoal(MutantWitherSkeletonServant mob) {
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
        return this.mob.tickCount >= this.nextUseTime && MiscUtils.isEntityValid(this.target) && MiscUtils.isEntityAttackable(this.mob, this.target, (Double)MutantWitherSkeletonCommonConfig.max_melee_attack_distance.get());
    }

    public boolean canContinueToUse() {
        return this.mob.getAnimation("attack_left").isPlaying() || this.mob.getAnimation("attack_right").isPlaying();
    }

    public void start() {
        this.mob.stopAndLockIdleAnimations(4, false);
        this.mob.getAnimation(this.mob.getRandom().nextBoolean() ? "attack_left" : "attack_right").start(1.13F, 4, () -> {
            this.mob.unlockIdleAnimations();
        });
        this.mob.playSound((SoundEvent)SoundEventInit.MUTANT_WITHER_SKELETON_ATTACK.get());
    }

    public void tick() {
        this.target = this.mob.getTarget();
        this.mob.getNavigation().stop();
        if (this.mob.getAnimation("attack_left").isProgressAt(0.42F) || this.mob.getAnimation("attack_right").isProgressAt(0.42F)) {
            this.mob.playSound((SoundEvent)SoundEventInit.MUTANT_WITHER_SKELETON_SWOOSH.get());
        }

        if (MiscUtils.isEntityValid(this.target)) {
            this.mob.lookAt(Anchor.EYES, this.target.getEyePosition());
            if (MiscUtils.isEntityAttackable(this.mob, this.target, (Double)MutantWitherSkeletonCommonConfig.max_melee_attack_damage_distance.get()) && (this.mob.getAnimation("attack_left").isProgressAt(0.5F) || this.mob.getAnimation("attack_right").isProgressAt(0.5F))) {
                float targetHealth = this.target.getHealth();
                if ((Boolean)MutantWitherSkeletonCommonConfig.ignores_invulnerability_time.get()) {
                    this.target.invulnerableTime = 0;
                }

                this.mob.doHurtTarget(this.target, this.mob.getAnimation("attack_left").isPlaying() ? EquipmentSlot.OFFHAND : EquipmentSlot.MAINHAND);
                if (!MiscUtils.isEntityValid(this.target) || this.target.getHealth() < targetHealth) {
                    this.mob.playSound((SoundEvent)SoundEventInit.MUTANT_WITHER_SKELETON_SWORD.get());
                }
            }
        }

    }

    public void stop() {
        super.stop();
        this.nextUseTime = this.mob.tickCount + (Integer)MutantWitherSkeletonCommonConfig.melee_attack_cooldown.get();
    }
}