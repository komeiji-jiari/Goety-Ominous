package com.qiuyue.goetyominous.common.entities.ally.mobs.mm.goals.MutantBlazeServant;

import com.alexander.mutantmore.config.MutantMoreGroupedOptionsCommonConfig;
import com.alexander.mutantmore.config.mutant_blaze.MutantBlazeCommonConfig;
import com.alexander.mutantmore.entities.Rodling;
import com.alexander.mutantmore.init.SoundEventInit;
import com.alexander.mutantmore.util.MiscUtils;
import com.alexander.mutantmore.util.PositionUtils;
import com.google.common.collect.Lists;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;

import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.MutantBlazeServant;
import com.qiuyue.goetyominous.common.entities.ally.mobs.mm.MutantBlazeServantRodProjectile;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.phys.Vec3;

public class MutantBlazeServantRodShotAttackGoal extends Goal {
    private final TargetingConditions rodlingCountTargeting = TargetingConditions.forNonCombat().range(50.0).ignoreLineOfSight().ignoreInvisibilityTesting().selector((entity) -> {
        return entity instanceof Rodling && !((Rodling)entity).isTame();
    });
    public MutantBlazeServant mob;
    @Nullable
    public LivingEntity target;
    public int nextUseTime;
    public int currentRod = 0;
    public List<MutantBlazeServantRodProjectile> totalProjectiles = Lists.newArrayList();
    public List<MutantBlazeServantRodProjectile> nonRodlingProjectiles = Lists.newArrayList();

    public MutantBlazeServantRodShotAttackGoal(MutantBlazeServant mob) {
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
        return this.target != null && !this.target.isRemoved() && !this.target.isDeadOrDying() && this.mob.hasLineOfSight(this.target) && this.mob.tickCount >= this.nextUseTime && this.animationsUseable();
    }

    public boolean canContinueToUse() {
        return this.target != null && !this.target.isRemoved() && !this.target.isDeadOrDying() && !this.animationsUseable();
    }

    public void start() {
        this.currentRod = 0;
        this.totalProjectiles.clear();
        this.nonRodlingProjectiles.clear();
        this.mob.playSound((SoundEvent)SoundEventInit.MUTANT_BLAZE_RODSHOT.get(), 2.0F, 1.0F);
        this.mob.rodShotAnimationTick = this.mob.rodShotAnimationLength;
        this.mob.level().broadcastEntityEvent(this.mob, (byte)13);

        int nearbyRodlings;
        for(nearbyRodlings = 0; nearbyRodlings < 13; ++nearbyRodlings) {
            MutantBlazeServantRodProjectile rod = new MutantBlazeServantRodProjectile(this.mob.level(), this.mob);
            this.totalProjectiles.add(rod);
            this.nonRodlingProjectiles.add(rod);
        }

        nearbyRodlings = this.mob.level().getNearbyEntities(Rodling.class, this.rodlingCountTargeting, this.mob, this.mob.getBoundingBox().inflate(50.0)).size();
        if (nearbyRodlings <= (Integer)MutantBlazeCommonConfig.rodling_shot_max_nearby_rodlings.get()) {
            for(int i = 0; i < (Integer)MutantBlazeCommonConfig.rodling_shot_rodling_amount.get(); ++i) {
                MutantBlazeServantRodProjectile rod = (MutantBlazeServantRodProjectile)this.nonRodlingProjectiles.get(this.mob.getRandom().nextInt(this.nonRodlingProjectiles.size()));
                rod.setIsRodling(true);
                if (this.mob.getHealth() <= this.mob.getMaxHealth() * (((Integer)MutantBlazeCommonConfig.rodling_shot_armoured_rodlings_health_threshold.get()).floatValue() / 100.0F) && MiscUtils.randomPercent((Integer)MutantBlazeCommonConfig.rodling_shot_shoot_armoured_rodlings_chance.get())) {
                    rod.setHasArmour(true);
                }

                if (this.mob.getHealth() <= this.mob.getMaxHealth() * (((Integer)MutantBlazeCommonConfig.rodling_shot_shielded_rodlings_health_threshold.get()).floatValue() / 100.0F) && MiscUtils.randomPercent((Integer)MutantBlazeCommonConfig.rodling_shot_shoot_shielded_rodlings_chance.get())) {
                    rod.setHasShields(true);
                }

                this.nonRodlingProjectiles.remove(rod);
            }
        }

    }

    public void tick() {
        this.target = this.mob.getTarget();
        this.mob.getNavigation().stop();
        if (this.target != null) {
            this.mob.getLookControl().setLookAt(this.target);
            if (this.mob.rodShotAnimationTick <= this.mob.rodShotAnimationActionStartPoint && this.mob.rodShotAnimationTick >= this.mob.rodShotAnimationActionEndPoint) {
                Vec3 pos = PositionUtils.getOffsetPos(this.mob, 0.0, 5.1, 0.5, 0.0F, this.mob.yBodyRot);
                double d1 = this.target.getX() - pos.x;
                double d2 = this.target.getEyeY() - pos.y;
                double d3 = this.target.getZ() - pos.z;
                MutantBlazeServantRodProjectile rod = (MutantBlazeServantRodProjectile)this.totalProjectiles.get(Mth.clamp(this.currentRod, 0, this.totalProjectiles.size() - 1));
                rod.damage = ((Double)MutantBlazeCommonConfig.rod_damage.get()).floatValue();
                rod.fireLength = (Integer)MutantBlazeCommonConfig.rod_fire_length.get();
                rod.ignoresInvulTime = (Boolean)MutantBlazeCommonConfig.ignores_invulnerability_time.get();
                rod.griefing = false;
                rod.griefingDropsBlocks = false;
                rod.fireGriefing = (Boolean)MutantBlazeCommonConfig.rod_fire.get() && !(Boolean)MutantMoreGroupedOptionsCommonConfig.mob_griefing_off.get();
                rod.moveTo(pos);
                rod.shoot(d1, d2, d3, 1.8F, 5.0F);
                this.mob.level().addFreshEntity(rod);
                ++this.currentRod;
            }
        }

    }

    public void stop() {
        this.nextUseTime = this.mob.tickCount + (Integer)MutantBlazeCommonConfig.rod_shot_cooldown.get();
    }

    public boolean animationsUseable() {
        return this.mob.rodShotAnimationTick <= 0;
    }
}