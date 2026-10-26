package com.qiuyue.goetyominous.common.entities.ally.lm;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.IAttackGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.IMoveGoal;
import com.qiuyue.goetyominous.utils.ServantAllyUtil;
import net.miauczel.legendary_monsters.entity.client.ControlledAnim;
import net.miauczel.legendary_monsters.util.EntityUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;

import javax.annotation.Nullable;
import java.util.List;
import com.qiuyue.goetyominous.config.lm.LmConfig;

public class FracturedServant extends IAnimatedMobServant {

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState sweepAnimationState = new AnimationState();
    public final AnimationState deathAnimationState = new AnimationState();
    public ControlledAnim bodyFadeAway = new ControlledAnim(10);
    public int deathAnimTicks;

    public FracturedServant(EntityType<? extends FracturedServant> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 5;
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, LmConfig.FracturedServantHealth.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.7)
                .add(Attributes.FOLLOW_RANGE, 30.0)
                .add(Attributes.ARMOR, LmConfig.FracturedServantArmor.get())
                .add(Attributes.ARMOR_TOUGHNESS, LmConfig.FracturedServantArmorToughness.get())
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.ATTACK_KNOCKBACK, 1.3)
                .add(Attributes.ATTACK_DAMAGE, LmConfig.FracturedServantDamage.get());
    }

    @Override
    public double damageMultiplier() {
        return LmConfig.FracturedServantDamageMultiplier.get();
    }

    @Override
    public double getFollowSpeed() {
        return 1.0D;
    }

    @Override
    public double getCommandSpeed() {
        return 1.0D;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty,
                                        MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData,
                                        @Nullable CompoundTag pDataTag) {
        if (pReason == MobSpawnType.MOB_SUMMONED && this.getTrueOwner() instanceof Player player) {
            if (countServants(player) >= LmConfig.FracturedServantLimit.get()) {
                return null;
            }
        }
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    private int countServants(Player player) {
        int count = 0;
        if (player.level() instanceof ServerLevel serverLevel) {
            for (Entity entity : serverLevel.getAllEntities()) {
                if (entity instanceof FracturedServant servant && servant.getTrueOwner() == player) {
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(6, new Summoned.WanderGoal<>(this, this.getFollowSpeed()));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(2, new IMoveGoal(this, false, 1.0D));
        this.goalSelector.addGoal(1, new IAttackGoal(this, 0, 2, 0, 35, 20, 3.5F) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && FracturedServant.this.getRandom().nextFloat() * 35.0F < 16.0F
                        && FracturedServant.this.getTarget() != null;
            }
        });
    }

    @Override
    public void tick() {
        if (this.level().isClientSide) {
            this.idleAnimationState.animateWhen(this.getAttackState() == 0, this.tickCount);
        }
        this.updateWithAttack();
        super.tick();
    }

    public void updateWithAttack() {
        if (this.getAttackState() == 2) {
            if (this.attackTicks == 5) {
                this.calculatedDash(0.25F);
            }
            if (this.attackTicks == 11) {
                this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.0F, 0.6F);
                this.sideAreaAttack(1.75F, 3.0F, 180.0F, 0.0F, 0.0F, 14.0F, 30,
                        SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, 1.0F);
            }
        }
    }

    public void sideAreaAttack(float range, float height, float arc, float boxOffset, float forwardOffset,
                               float damage, int brokenShieldTicks, SoundEvent soundEvent, float pitch) {
        double theta = Math.toRadians(this.yBodyRot) + 1.5707963267948966D;
        double forwardX = Math.cos(theta) * (double) forwardOffset;
        double forwardZ = Math.sin(theta) * (double) forwardOffset;
        List<LivingEntity> entitiesHit = this.getEntityLivingBaseNearby(range, height, range, range);
        for (LivingEntity entityHit : entitiesHit) {
            double dx = entityHit.getX() - (this.getX() + forwardX);
            double dz = entityHit.getZ() - (this.getZ() + forwardZ);
            float entityHitAngle = (float) ((Math.toDegrees(Math.atan2(dz, dx)) - 90.0D) % 360.0D);
            if (entityHitAngle < 0.0F) {
                entityHitAngle += 360.0F;
            }
            float entityAttackingAngle = (this.yBodyRot - boxOffset) % 360.0F;
            if (entityAttackingAngle < 0.0F) {
                entityAttackingAngle += 360.0F;
            }
            float entityHitDistance = (float) Math.sqrt(dx * dx + dz * dz);
            float entityRelativeAngle = entityHitAngle - entityAttackingAngle;
            if (entityHitDistance > range) {
                continue;
            }
            if (!(entityRelativeAngle <= arc / 2.0F && entityRelativeAngle >= -arc / 2.0F)
                    && !(entityRelativeAngle >= 360.0F - arc / 2.0F)
                    && !(entityRelativeAngle <= -360.0F + arc / 2.0F)) {
                continue;
            }
            if (ServantAllyUtil.areAllied(this, entityHit) || entityHit == this) {
                continue;
            }
            boolean flag = entityHit.hurt(this.damageSources().mobAttack(this),
                    (float) (damage * this.damageMultiplier()));
            if (flag) {
                EntityUtil.cancelBuffs(entityHit);
                this.playSound(soundEvent, 1.0F, pitch);
            }
            if (entityHit instanceof Player player && player.isBlocking() && brokenShieldTicks > 0) {
                disableShield(player, brokenShieldTicks);
            }
        }
    }

    public AnimationState getAnimationState(String input) {
        if (input.equals("idle")) {
            return this.idleAnimationState;
        }
        if (input.equals("sweep")) {
            return this.sweepAnimationState;
        }
        if (input.equals("death")) {
            return this.deathAnimationState;
        }
        return new AnimationState();
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        if (ATTACK_STATE.equals(accessor) && this.level().isClientSide) {
            switch (this.getAttackState()) {
                case 0 -> this.stopAllAnimationStates();
                case 1 -> {
                    this.stopAllAnimationStates();
                    this.idleAnimationState.startIfStopped(this.tickCount);
                }
                case 2 -> {
                    this.stopAllAnimationStates();
                    this.sweepAnimationState.startIfStopped(this.tickCount);
                }
                case 3 -> {
                    this.stopAllAnimationStates();
                    this.deathAnimationState.startIfStopped(this.tickCount);
                }
                default -> {
                }
            }
        }
        super.onSyncedDataUpdated(accessor);
    }

    public void stopAllAnimationStates() {
        this.idleAnimationState.stop();
        this.sweepAnimationState.stop();
        this.deathAnimationState.stop();
    }

    @Override
    public void die(DamageSource source) {
        this.deathAnimTicks = 0;
        this.setAttackState(3);
        this.stopAllAnimationStates();
        super.die(source);
    }

    @Override
    protected void tickDeath() {
        ++this.deathAnimTicks;
        if (this.deathAnimTicks > 2) {
            this.bodyFadeAway.increaseTimer();
        }
        if (this.deathAnimTicks == 7) {
            this.remove(Entity.RemovalReason.KILLED);
            this.gameEvent(GameEvent.ENTITY_DIE);
        }
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource pSource) {
        return SoundEvents.PHANTOM_HURT;
    }
}
