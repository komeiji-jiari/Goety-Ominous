package com.qiuyue.goetyominus.common.entities.ally.mobs.mm.goals.MutantHoglinServant;

import com.alexander.mutantmore.config.mutant_hoglin.MutantHoglinCommonConfig;
import com.alexander.mutantmore.util.PositionUtils;
import com.qiuyue.goetyominus.common.entities.ally.mobs.mm.MutantHoglinServant;
import java.util.EnumSet;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;

public class MutantHoglinChargeAttackGoal extends Goal {
    public MutantHoglinServant mob;
    @Nullable
    public LivingEntity target;
    public int nextUseTime;
    public float bonusCharges;
    public int chargingFor;

    public MutantHoglinChargeAttackGoal(MutantHoglinServant mob) {
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
        if (this.mob.wantsToCharge) {
            return this.mob.notCurrentlyPlayingKeyframeAnimation() && this.animationsUseable();
        }
        return this.target != null
                && this.mob.notCurrentlyPlayingKeyframeAnimation()
                && this.mob.tickCount >= this.nextUseTime
                && !this.target.isRemoved()
                && !this.target.isDeadOrDying()
                && this.animationsUseable();
    }

    public boolean canContinueToUse() {
        return this.mob.prepareChargeAnimationTick > 0 || this.mob.charging;
    }

    public void start() {
        MutantHoglinServant var10000 = this.mob;
        Objects.requireNonNull(this.mob);
        var10000.prepareChargeAnimationTick = 26;
        this.mob.level().broadcastEntityEvent(this.mob, (byte)10);
        this.mob.wantsToCharge = false;
        if (this.mob.riderChargeRequested) {
            this.bonusCharges = 0.0F;
            this.mob.riderChargeRequested = false;
        } else {
            this.bonusCharges = (100.0F - this.mob.getHealth() / this.mob.getMaxHealth() * 100.0F) / (float)(Integer)MutantHoglinCommonConfig.lost_health_percent_for_bonus_charge.get();
        }
    }

    public void tick() {
        this.target = this.mob.getTarget();
        this.mob.getNavigation().stop();
        if (this.target != null && !this.mob.charging) {
            this.mob.lookAt(Anchor.EYES, this.target.position());
        }

        if (this.mob.prepareChargeAnimationTick == 1) {
            this.mob.charging = true;
            this.mob.level().broadcastEntityEvent(this.mob, (byte)12);
        }

        if (this.mob.charging) {
            ++this.chargingFor;
            double moveSpeed = this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED);
            this.mob.setDeltaMovement(this.mob.getDeltaMovement().add(PositionUtils.getOffsetMotion(this.mob, 0.0, 0.0, this.mob.onGround() ? moveSpeed * 1.25 : moveSpeed * 0.75, 0.0F, this.mob.yBodyRot)));
        }

        if (this.chargingFor >= 30 || this.willFallToDoom(this.mob)) {
            this.mob.setDeltaMovement(this.mob.getDeltaMovement().scale(0.2));
            this.chargingFor = 0;
            this.mob.charging = false;
            this.mob.level().broadcastEntityEvent(this.mob, (byte)13);
            if (this.bonusCharges >= 1.0F) {
                --this.bonusCharges;
                MutantHoglinServant var10000 = this.mob;
                Objects.requireNonNull(this.mob);
                var10000.prepareChargeAnimationTick = 26;
                this.mob.level().broadcastEntityEvent(this.mob, (byte)10);
            }
        }

    }

    boolean willFallToDoom(MutantHoglinServant mob) {
        boolean blockBeneath = false;
        boolean lavaBeneath = false;
        BlockPos pos = PositionUtils.getOffsetBlockPos(mob, 0.0, 0.0, 2.0, 0.0F, mob.yBodyRot);

        for(int i = 0; i < 10; ++i) {
            if (!mob.level().getBlockState(pos.offset(0, -i, 0)).isAir() && mob.level().getFluidState(pos.offset(0, -i, 0)).isEmpty()) {
                blockBeneath = true;
            }

            if (!blockBeneath && !mob.level().getFluidState(pos.offset(0, -i, 0)).isEmpty()) {
                lavaBeneath = true;
                break;
            }
        }

        return !mob.level().isClientSide && (!blockBeneath || lavaBeneath);
    }

    public void stop() {
        super.stop();
        int randomAddedCooldown = (Integer)MutantHoglinCommonConfig.max_charge_cooldown.get() - (Integer)MutantHoglinCommonConfig.min_charge_cooldown.get() <= 0 ? 0 : this.mob.getRandom().nextInt((Integer)MutantHoglinCommonConfig.max_charge_cooldown.get() - (Integer)MutantHoglinCommonConfig.min_charge_cooldown.get());
        int cooldown = (Integer)MutantHoglinCommonConfig.min_charge_cooldown.get() + randomAddedCooldown;
        this.nextUseTime = this.mob.tickCount + cooldown;
        this.mob.charging = false;
        this.mob.level().broadcastEntityEvent(this.mob, (byte)13);
    }

    public boolean animationsUseable() {
        return this.mob.prepareChargeAnimationTick <= 0;
    }
}
