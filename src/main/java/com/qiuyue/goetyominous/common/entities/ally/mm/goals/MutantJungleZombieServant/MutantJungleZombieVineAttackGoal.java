package com.qiuyue.goetyominous.common.entities.ally.mm.goals.MutantJungleZombieServant;

import com.alexander.mutantmore.config.mutant_jungle_zombie.MutantJungleZombieCommonConfig;
import com.alexander.mutantmore.init.TagInit.EntityTypes;
import com.alexander.mutantmore.util.MiscUtils;
import com.alexander.mutantmore.util.PositionUtils;
import com.qiuyue.goetyominous.common.entities.ally.mm.AreaDamage;
import com.qiuyue.goetyominous.common.entities.ally.mm.MutantJungleZombieServant;
import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

public class MutantJungleZombieVineAttackGoal extends Goal {
    public MutantJungleZombieServant mob;
    @Nullable
    public LivingEntity target;
    public int nextUseTime;
    public AreaDamage areaDamage;
    public boolean appliedPoison;
    public boolean hitTarget;

    public MutantJungleZombieVineAttackGoal(MutantJungleZombieServant mob) {
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP));
        this.mob = mob;
        this.target = mob.getTarget();
    }

    public boolean isInterruptable() {
        return false;
    }

    public boolean requiresUpdateEveryTick() {
        return true;
    }

    public boolean canUse() {
        this.target = this.mob.getTarget();
        return !this.mob.jumping
                && this.target != null
                && this.mob.tickCount >= this.nextUseTime
                && !this.target.isRemoved()
                && !this.target.isDeadOrDying()
                && (double) this.mob.distanceTo(this.target) <= (Double) MutantJungleZombieCommonConfig.max_vine_attack_distance.get()
                && this.animationsUseable()
                && this.mob.hasLineOfSight(this.target);
    }

    public boolean canContinueToUse() {
        return !this.mob.shouldBeStationary() && !this.animationsUseable();
    }

    public void start() {
        this.areaDamage = null;
        this.appliedPoison = false;
        if (MiscUtils.randomPercent(50)) {
            this.mob.lowAttackAnimationTick = 30;
            this.mob.noveltyAnimationTick = 0;
            this.mob.level().broadcastEntityEvent(this.mob, (byte) 10);
        } else {
            this.mob.highAttackAnimationTick = 30;
            this.mob.noveltyAnimationTick = 0;
            this.mob.level().broadcastEntityEvent(this.mob, (byte) 11);
        }
        this.hitTarget = false;
    }

    public void tick() {
        this.target = this.mob.getTarget();
        this.mob.getNavigation().stop();
        if (this.target != null) {
            this.mob.lookAt(Anchor.EYES, this.target.position());
        }
        if (this.target != null && (this.mob.lowAttackAnimationTick == this.mob.lowAttackAnimationActionPoint || this.mob.highAttackAnimationTick == this.mob.highAttackAnimationActionPoint)) {
            Vec3 areaDamagePos = PositionUtils.getOffsetPos(this.mob, 0.0, this.mob.lowAttackAnimationTick == this.mob.lowAttackAnimationActionPoint ? 0.0 : 1.6, 5.0, 0.0F, this.mob.yBodyRot);
            this.areaDamage = AreaDamage.spawnAreaDamage(this.mob.level(), areaDamagePos, this.mob, ((Double) MutantJungleZombieCommonConfig.vine_attack_damage.get()).floatValue(), this.mob.damageSources().mobAttack(this.mob), 11.0F, 11.0F, 0.0F, 0.5F, 5, 0, false, false, 0.5, 0.05, true, false, 0, (Boolean) MutantJungleZombieCommonConfig.ignores_invulnerability_time.get(), EntityTypes.MUTANT_JUNGLE_ZOMBIE_CANT_HURT, 0);
            ((ServerLevel) this.mob.level()).addFreshEntityWithPassengers(this.areaDamage);
        }
        if (!this.appliedPoison && this.target != null && this.areaDamage != null && this.areaDamage.damagedEntities.contains(this.target) && !this.areaDamage.blockedByEntities.contains(this.target)) {
            this.target.addEffect(new MobEffectInstance(MobEffects.POISON, (Integer) MutantJungleZombieCommonConfig.vine_attack_poison_length.get(), (Integer) MutantJungleZombieCommonConfig.vine_attack_poison_level.get()));
            this.appliedPoison = true;
            this.hitTarget = true;
        }
    }

    public void stop() {
        super.stop();
        this.nextUseTime = this.mob.tickCount + (Integer) MutantJungleZombieCommonConfig.vine_attack_cooldown.get();
        this.mob.failedAttacks = !this.hitTarget ? ++this.mob.failedAttacks : 0;
    }

    public boolean animationsUseable() {
        return this.mob.lowAttackAnimationTick <= 0 && this.mob.highAttackAnimationTick <= 0;
    }
}
