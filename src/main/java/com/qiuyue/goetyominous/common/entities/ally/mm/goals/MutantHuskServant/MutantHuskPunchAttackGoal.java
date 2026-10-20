package com.qiuyue.goetyominous.common.entities.ally.mm.goals.MutantHuskServant;

import com.alexander.mutantmore.blocks.QuicksandBlock;
import com.alexander.mutantmore.config.MutantMoreGroupedOptionsCommonConfig;
import com.alexander.mutantmore.config.mutant_husk.MutantHuskCommonConfig;
import com.qiuyue.goetyominous.compat.mm.MmScreenShake;
import com.alexander.mutantmore.init.ParticleTypeInit;
import com.alexander.mutantmore.init.SoundEventInit;
import com.alexander.mutantmore.particles.AdvancedParticleOption;
import com.alexander.mutantmore.util.MiscUtils;
import com.alexander.mutantmore.util.PositionUtils;
import com.google.common.collect.Lists;
import com.qiuyue.goetyominous.common.blocks.mm.ServantQuicksandBlock;
import com.qiuyue.goetyominous.common.entities.ally.mm.AreaDamage;
import com.qiuyue.goetyominous.common.entities.ally.mm.MutantHuskServant;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class MutantHuskPunchAttackGoal extends Goal {
    public MutantHuskServant mob;
    @Nullable
    public LivingEntity target;
    public int nextUseTime;
    public List<AreaDamage> areaDamages = Lists.newArrayList();

    public MutantHuskPunchAttackGoal(MutantHuskServant mob) {
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
        return this.target != null && this.mob.tickCount >= this.nextUseTime && !this.target.isRemoved() && !this.target.isDeadOrDying() && (double) this.mob.distanceTo(this.target) <= (Double) MutantHuskCommonConfig.max_punch_attack_distance.get() && this.animationsUseable() && this.mob.hasLineOfSight(this.target);
    }

    public boolean canContinueToUse() {
        return !this.animationsUseable();
    }

    public void start() {
        this.areaDamages.clear();
        this.mob.trapAnimationTick = this.mob.trapAnimationLength;
        this.mob.noveltyAnimationTick = 0;
        this.mob.level().broadcastEntityEvent(this.mob, (byte) 9);
        this.mob.playSound(SoundEventInit.MUTANT_HUSK_PUNCH.get(), 2.0F, 1.0F);
    }

    public void tick() {
        this.target = this.mob.getTarget();
        this.mob.getNavigation().stop();
        if (this.target != null) {
            this.mob.lookAt(Anchor.EYES, this.target.position());
        }
        if (this.mob.trapAnimationTick == this.mob.trapAnimationActionPoint) {
            Vec3 slamAttackBoundingBoxOffset = PositionUtils.getOffsetPos(this.mob, 0.0, 0.0, 2.5, 0.0F, this.mob.yBodyRot);
            String sandType = MiscUtils.sandStringForPos(this.mob.level(), BlockPos.containing(slamAttackBoundingBoxOffset).below());
            AABB slamAttackBoundingBox = this.mob.getBoundingBox().deflate(0.0, 2.0, 0.0).move(0.0, -1.0, 0.0).inflate(0.5, 1.0, 0.5).move(slamAttackBoundingBoxOffset.x - this.mob.getX(), slamAttackBoundingBoxOffset.y - this.mob.getY(), slamAttackBoundingBoxOffset.z - this.mob.getZ());
            if (MutantHuskCommonConfig.punch_attack_griefing.get() && !MutantMoreGroupedOptionsCommonConfig.quicksand_griefing_off.get()) {
                for (BlockPos blockpos : BlockPos.betweenClosed(Mth.floor(slamAttackBoundingBox.minX), Mth.floor(slamAttackBoundingBox.minY), Mth.floor(slamAttackBoundingBox.minZ), Mth.floor(slamAttackBoundingBox.maxX), Mth.floor(slamAttackBoundingBox.maxY), Mth.floor(slamAttackBoundingBox.maxZ))) {
                    BlockState blockstate = this.mob.level().getBlockState(blockpos);
                    if (!blockstate.isAir() || !QuicksandBlock.survivable(ServantQuicksandBlock.forSandType(sandType).defaultBlockState(), this.mob.level(), blockpos)) continue;
                    this.mob.level().setBlockAndUpdate(blockpos, ServantQuicksandBlock.forSandType(sandType).defaultBlockState());
                }
            }
            ((ServerLevel) this.mob.level()).sendParticles(new AdvancedParticleOption(ParticleTypeInit.SAND, List.of((float) MiscUtils.sandColourForSandType(sandType))), slamAttackBoundingBoxOffset.x, slamAttackBoundingBoxOffset.y, slamAttackBoundingBoxOffset.z, 30, 1.0, 0.0, 1.0, 0.0);
            MmScreenShake.shake(this.mob.level(), 13, 0.15F, this.mob.blockPosition(), 15);
            for (LivingEntity entity : this.mob.level().getNearbyEntities(LivingEntity.class, TargetingConditions.forCombat(), this.mob, slamAttackBoundingBox)) {
                boolean flag;
                if (entity == this.mob || !this.mob.canHarm(entity)) continue;
                if (MutantHuskCommonConfig.ignores_invulnerability_time.get()) {
                    entity.invulnerableTime = 0;
                }
                if (flag = entity.hurt(this.mob.damageSources().mobAttack(this.mob), ((Double) MutantHuskCommonConfig.punch_attack_damage.get()).floatValue())) {
                    entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, (Integer) MutantHuskCommonConfig.punch_attack_hunger_length.get(), (Integer) MutantHuskCommonConfig.punch_attack_hunger_level.get()), this.mob);
                }
                MiscUtils.disableShield(this.target, (Integer) MutantHuskCommonConfig.punch_attack_disable_shield_length.get());
            }
        }
    }

    public void stop() {
        super.stop();
        this.nextUseTime = this.mob.tickCount + (Integer) MutantHuskCommonConfig.punch_attack_cooldown.get();
    }

    public boolean animationsUseable() {
        return this.mob.trapAnimationTick <= 0;
    }
}
