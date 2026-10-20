package com.qiuyue.goetyominous.common.entities.ally.mm.goals.MutantHuskServant;

import com.alexander.mutantmore.config.mutant_husk.MutantHuskCommonConfig;
import com.alexander.mutantmore.init.SoundEventInit;
import com.alexander.mutantmore.init.TagInit.EntityTypes;
import com.alexander.mutantmore.util.MiscUtils;
import com.alexander.mutantmore.util.PositionUtils;
import com.google.common.collect.Lists;
import com.qiuyue.goetyominous.common.entities.ally.mm.AreaDamage;
import com.qiuyue.goetyominous.common.entities.ally.mm.MutantHuskServant;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

public class MutantHuskMeleeAttackGoal extends Goal {
    public MutantHuskServant mob;
    @Nullable
    public LivingEntity target;
    public int nextUseTime;
    public List<AreaDamage> areaDamages = Lists.newArrayList();

    public MutantHuskMeleeAttackGoal(MutantHuskServant mob) {
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
        return this.target != null && this.mob.tickCount >= this.nextUseTime && !this.target.isRemoved() && !this.target.isDeadOrDying() && (double) this.mob.distanceTo(this.target) <= (Double) MutantHuskCommonConfig.max_basic_attack_distance.get() && this.animationsUseable() && this.mob.hasLineOfSight(this.target);
    }

    public boolean canContinueToUse() {
        return !this.animationsUseable();
    }

    public void start() {
        this.areaDamages.clear();
        this.mob.attackAnimationTick = this.mob.attackAnimationLength;
        this.mob.noveltyAnimationTick = 0;
        this.mob.level().broadcastEntityEvent(this.mob, (byte) 8);
        this.mob.playSound(SoundEventInit.MUTANT_HUSK_ATTACK.get(), 2.0F, MiscUtils.randomSoundPitch());
    }

    public void tick() {
        this.target = this.mob.getTarget();
        this.mob.getNavigation().stop();
        if (this.target != null) {
            this.mob.lookAt(Anchor.EYES, this.target.position());
        }
        if (this.target != null && this.mob.attackAnimationTick == this.mob.attackAnimationActionPoint) {
            this.mob.playSound(SoundEventInit.MUTANT_HUSK_THUD.get(), 2.0F, MiscUtils.randomSoundPitch());
            for (int i = 0; i < (Integer) MutantHuskCommonConfig.basic_attack_shockwave_length.get(); ++i) {
                Vec3 areaDamagePos = PositionUtils.getOffsetPos(this.mob, 0.0, 0.0, 2.0 + (double) i * 2.5, 0.0F, this.mob.yBodyRot);
                AreaDamage areaDamage = AreaDamage.spawnAreaDamage(this.mob.level(), areaDamagePos, this.mob, ((Double) MutantHuskCommonConfig.basic_attack_damage.get()).floatValue(), this.mob.damageSources().mobAttack(this.mob), 3.5F, 3.5F, 0.0F, 0.4F, 5, i * 2, false, false, (Double) MutantHuskCommonConfig.basic_attack_knockback.get(), 0.05, true, false, 0, (Boolean) MutantHuskCommonConfig.ignores_invulnerability_time.get(), EntityTypes.MUTANT_HUSK_CANT_HURT, 1);
                this.areaDamages.add(areaDamage);
                PositionUtils.moveToCorrectHeight(areaDamage, true);
                areaDamage.setSandType(MiscUtils.sandStringForPos(this.mob.level(), areaDamage.getOnPos()));
                ((ServerLevel) this.mob.level()).addFreshEntityWithPassengers(areaDamage);
            }
            for (AreaDamage areaDamage : this.areaDamages) {
                areaDamage.connectedAreaDamages = Lists.newArrayList(this.areaDamages.iterator());
                areaDamage.connectedAreaDamages.remove(areaDamage);
            }
        }
    }

    public void stop() {
        super.stop();
        this.nextUseTime = this.mob.tickCount + (Integer) MutantHuskCommonConfig.basic_attack_cooldown.get();
    }

    public boolean animationsUseable() {
        return this.mob.attackAnimationTick <= 0;
    }
}
