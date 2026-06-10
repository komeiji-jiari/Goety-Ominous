package com.qiuyue.someillagerservants.common.entities.ally.mobs.mm.goals.MutantWitherSkeletonServant;

import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.alexander.mutantmore.config.mutant_wither_skeleton.MutantWitherSkeletonCommonConfig;
import com.alexander.mutantmore.init.EffectInit;
import com.alexander.mutantmore.init.SoundEventInit;
import com.alexander.mutantmore.util.MiscUtils;
import com.alexander.mutantmore.util.PositionUtils;
import com.qiuyue.someillagerservants.common.entities.ally.mobs.mm.AreaDamage;
import com.qiuyue.someillagerservants.common.entities.ally.mobs.mm.MutantWitherSkeletonServant;
import java.util.EnumSet;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.commands.arguments.EntityAnchorArgument.Anchor;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class MutantWitherSkeletonWitherBreathAttackGoal extends Goal {
    public MutantWitherSkeletonServant mob;
    @Nullable
    public LivingEntity target;
    public int nextUseTime;
    public boolean madeAreaDamage;

    public MutantWitherSkeletonWitherBreathAttackGoal(MutantWitherSkeletonServant mob) {
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
        return this.mob.tickCount >= this.nextUseTime && MiscUtils.isEntityValid(this.target) && MiscUtils.isEntityAttackable(this.mob, this.target, (Double)MutantWitherSkeletonCommonConfig.max_wither_breath_distance.get()) && this.mob.getBoundingBox().intersects(this.target.getBoundingBox().move(this.mob.getX() - this.target.getX(), 0.0, this.mob.getZ() - this.target.getZ()));
    }

    public boolean canContinueToUse() {
        return this.mob.getAnimation("wither_breath").isPlaying();
    }

    public void start() {
        this.mob.stopAndLockIdleAnimations(4, false);
        this.mob.getAnimation("wither_breath").start(5.0F, 4, () -> {
            this.mob.unlockIdleAnimations();
        });
        this.mob.playSound((SoundEvent) SoundEventInit.MUTANT_WITHER_SKELETON_DOUBLEATTACK.get());
        this.madeAreaDamage = false;
    }

    public void tick() {
        this.target = this.mob.getTarget();
        this.mob.getNavigation().stop();
        if (MiscUtils.isEntityValid(this.target)) {
            this.mob.lookAt(Anchor.EYES, this.target.getEyePosition());
        }

        if (this.mob.getAnimation("wither_breath").progress() > 1.0F && !this.madeAreaDamage) {
            this.mob.level().broadcastEntityEvent(this.mob, (byte)11);
            float width = 16.0F;
            Vec3 offsetPos = PositionUtils.getOffsetPos(this.mob, 0.0, 0.0, (double)(width / 2.0F + this.mob.getBbWidth()), 0.0F, this.mob.yBodyRot);
            AABB bb = (new AABB(offsetPos.add((double)(width / 2.0F), 1.0, (double)(width / 2.0F)), offsetPos.subtract((double)(width / 2.0F), 1.0, (double)(width / 2.0F)))).move(0.0, (double)(this.mob.getBbHeight() / 2.0F), 0.0);
            float damageDistance = (this.mob.getAnimation("wither_breath").progress() - 1.0F) * (width / 2.0F + this.mob.getBbWidth()) * 0.7F;
            Iterator var5 = this.mob.level().getEntitiesOfClass(LivingEntity.class, bb).iterator();

            while(true) {
                LivingEntity entity;
                Player player;
                do {
                    do {
                        if (!var5.hasNext()) {
                            if ((double)this.mob.getAnimation("wither_breath").progress() >= 3.75) {
                                AreaDamage areaDamage = AreaDamage.spawnAreaDamage(
                                        this.mob.level(),
                                        offsetPos,
                                        this.mob,
                                        0.0F,
                                        null,
                                        width,
                                        width,
                                        0.0F,
                                        3.0F,
                                        200,
                                        0,
                                        false,
                                        false,
                                        0.0,
                                        0.0,
                                        false,
                                        false,
                                        0,
                                        false,
                                        null,
                                        4);
                                areaDamage.setSentFrom(BlockPos.containing(this.mob.position().add(0.0, (double)this.mob.getBbHeight() * 0.6, 0.0)));
                                ((ServerLevel)this.mob.level()).addFreshEntity(areaDamage);
                                this.madeAreaDamage = true;
                            }

                            return;
                        }

                        entity = (LivingEntity)var5.next();
                    } while(entity == this.mob);

                    if (entity instanceof Player) {
                        player = (Player)entity;
                        if (player.isCreative() || player.isSpectator()) {
                            continue;
                        }

                        if (this.mob.getTrueOwner() != null && player == this.mob.getTrueOwner()) {
                            continue;
                        }
                    } else if (entity instanceof Owned ownedEntity && this.mob.getTrueOwner() != null && ownedEntity.getTrueOwner() == this.mob.getTrueOwner()) {
                        continue;
                    }

                    break;
                } while(true);

                if (MiscUtils.isEntityValid(entity) && this.mob.canHarm(entity) && this.mob.hasLineOfSight(entity) && this.mob.distanceTo(entity) <= damageDistance) {
                    entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, (Integer)MutantWitherSkeletonCommonConfig.wither_breath_blindness_length.get()));
                    entity.addEffect(new MobEffectInstance(MobEffects.WITHER, (Integer)MutantWitherSkeletonCommonConfig.wither_breath_wither_length.get(), (Integer)MutantWitherSkeletonCommonConfig.wither_breath_wither_level.get()));
                    entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, (Integer)MutantWitherSkeletonCommonConfig.wither_breath_slowness_length.get(), (Integer)MutantWitherSkeletonCommonConfig.wither_breath_slowness_level.get()));
                    entity.addEffect(new MobEffectInstance((MobEffect)EffectInit.JUMPING_FATIGUE.get(), (Integer)MutantWitherSkeletonCommonConfig.wither_breath_slowness_length.get()));
                    entity.hurtMarked = true;
                    double d0 = entity.getX() - this.mob.getX();
                    double d1 = entity.getZ() - this.mob.getZ();
                    double d2 = Math.max(d0 * d0 + d1 * d1, 0.001);
                    entity.push(d0 / d2 * 1.0, 0.0, d1 / d2 * 1.0);
                }
            }
        }
    }

    public void stop() {
        super.stop();
        this.nextUseTime = this.mob.tickCount + MiscUtils.randomIntBetween((Integer)MutantWitherSkeletonCommonConfig.min_wither_breath_cooldown.get(), (Integer)MutantWitherSkeletonCommonConfig.max_wither_breath_cooldown.get());
    }
}