package com.qiuyue.goetyominus.common.entities.ally.mobs.mm.goals.MutantWitherSkeletonServant;

import com.alexander.mutantmore.config.mutant_wither_skeleton.MutantWitherSkeletonCommonConfig;
import com.alexander.mutantmore.events.ShakeCameraEvent;
import com.alexander.mutantmore.init.EffectInit;
import com.alexander.mutantmore.init.SoundEventInit;
import com.alexander.mutantmore.util.MiscUtils;
import com.alexander.mutantmore.util.PositionUtils;
import com.qiuyue.goetyominus.common.entities.ally.mobs.mm.MutantWitherSkeletonServant;
import java.util.EnumSet;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class MutantWitherSkeletonBlockGoal extends Goal {
    public MutantWitherSkeletonServant mob;
    @Nullable
    public LivingEntity target;

    public MutantWitherSkeletonBlockGoal(MutantWitherSkeletonServant mob) {
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
        return this.mob.getAnimation("block").isPlaying();
    }

    public boolean canContinueToUse() {
        return this.mob.getAnimation("block").isPlaying();
    }

    public void start() {
        super.start();
        this.mob.playSound((SoundEvent)SoundEventInit.MUTANT_WITHER_SKELETON_LAND_HIT.get());
        ShakeCameraEvent.shake(this.mob.level(), 20, 0.05F, this.mob.blockPosition(), 5);
    }

    public void tick() {
        this.target = this.mob.getTarget();
        this.mob.getNavigation().stop();
        if (MiscUtils.isEntityValid(this.target)) {
            this.mob.lookAt(Anchor.EYES, this.target.getEyePosition());
        }

        if (this.mob.getAnimation("block").isProgressAt(1.0F)) {
            this.mob.playSound((SoundEvent)SoundEventInit.MUTANT_WITHER_SKELETON_DOUBLESWORD.get());
            ShakeCameraEvent.shake(this.mob.level(), 10, 0.15F, this.mob.blockPosition(), 20);
            float width = this.mob.getBbWidth() * 1.25F;
            Vec3 bbOffset = PositionUtils.getOffsetPos(this.mob, 0.0, 0.0, (double)(width * 2.0F), 0.0F, this.mob.yBodyRot);
            AABB bb = (new AABB(bbOffset.add((double)width, (double)width, (double)width), bbOffset.subtract((double)width, (double)width, (double)width))).move(0.0, (double)(this.mob.getBbHeight() / 2.0F), 0.0);
            Iterator var4 = this.mob.level().getEntitiesOfClass(LivingEntity.class, bb).iterator();

            while(var4.hasNext()) {
                LivingEntity entity = (LivingEntity)var4.next();
                Vec3 knockback = PositionUtils.getOffsetMotion(entity, 0.0, 0.5, entity instanceof Player ? 10.0 : 4.0, 0.0F, this.mob.yBodyRot);
                entity.hurtMarked = true;
                entity.moveTo(entity.position().add(0.0, 0.1, 0.0));
                entity.setDeltaMovement(entity.getDeltaMovement().add(knockback.x, knockback.y, knockback.z));
                entity.hurt(this.mob.damageSources().mobAttack(this.mob), ((Double)MutantWitherSkeletonCommonConfig.block_damage.get()).floatValue());
                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, (Integer)MutantWitherSkeletonCommonConfig.block_slowness_length.get(), (Integer)MutantWitherSkeletonCommonConfig.block_slowness_level.get()));
                entity.addEffect(new MobEffectInstance((MobEffect)EffectInit.JUMPING_FATIGUE.get(), (Integer)MutantWitherSkeletonCommonConfig.block_slowness_length.get(), 0));
            }
        }

    }
}