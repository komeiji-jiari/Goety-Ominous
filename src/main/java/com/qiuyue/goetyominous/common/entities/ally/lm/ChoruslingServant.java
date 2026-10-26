package com.qiuyue.goetyominous.common.entities.ally.lm;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import net.miauczel.legendary_monsters.effect.ModEffects;
import net.miauczel.legendary_monsters.sound.ModSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;

import javax.annotation.Nullable;
import com.qiuyue.goetyominous.config.lm.LmConfig;

public class ChoruslingServant extends IAnimatedMobServant {

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState attackAnimationState = new AnimationState();
    public final AnimationState deathAnimationState = new AnimationState();
    public int idleAnimationTimeout;
    public int attackAnimationTimeout;

    public ChoruslingServant(EntityType<? extends ChoruslingServant> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 5;
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, LmConfig.ChoruslingServantHealth.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5)
                .add(Attributes.FOLLOW_RANGE, 14.0)
                .add(Attributes.ARMOR, LmConfig.ChoruslingServantArmor.get())
                .add(Attributes.MOVEMENT_SPEED, 0.1)
                .add(Attributes.ATTACK_KNOCKBACK, 0.5)
                .add(Attributes.ATTACK_DAMAGE, LmConfig.ChoruslingServantDamage.get());
    }

    @Override
    public double damageMultiplier() {
        return LmConfig.ChoruslingServantDamageMultiplier.get();
    }

    @Override
    public double getFollowSpeed() {
        return 3.0D;
    }

    @Override
    public double getCommandSpeed() {
        return 3.0D;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty,
                                        MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData,
                                        @Nullable CompoundTag pDataTag) {
        if (pReason == MobSpawnType.MOB_SUMMONED && this.getTrueOwner() instanceof Player player) {
            if (countServants(player) >= LmConfig.ChoruslingServantLimit.get()) {
                return null;
            }
        }
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    private int countServants(Player player) {
        int count = 0;
        if (player.level() instanceof ServerLevel serverLevel) {
            for (Entity entity : serverLevel.getAllEntities()) {
                if (entity instanceof ChoruslingServant servant && servant.getTrueOwner() == player) {
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(4, new AttackGoal(this, 3.0D));
        this.goalSelector.addGoal(6, new Summoned.WanderGoal<>(this, this.getFollowSpeed()));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            this.setupAnimationStates();
        }
    }

    private void setupAnimationStates() {
        if (this.getAttackState() != 2) {
            if (this.idleAnimationTimeout <= 0) {
                this.idleAnimationTimeout = this.random.nextInt(40) + 80;
                this.idleAnimationState.start(this.tickCount);
            } else {
                --this.idleAnimationTimeout;
            }
        }
        if (this.getAttackState() == 1 && this.attackAnimationTimeout <= 0) {
            this.attackAnimationTimeout = 17;
            this.attackAnimationState.start(this.tickCount);
        } else {
            --this.attackAnimationTimeout;
        }
        if (this.getAttackState() != 1 && this.attackAnimationTimeout <= 0) {
            this.attackAnimationState.stop();
        }
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        if (ATTACK_STATE.equals(accessor) && this.level().isClientSide) {
            if (this.getAttackState() == 2) {
                this.idleAnimationState.stop();
                this.attackAnimationState.stop();
                this.deathAnimationState.startIfStopped(this.tickCount);
            }
        }
        super.onSyncedDataUpdated(accessor);
    }

    public void stopAllAnimationStates() {
        this.idleAnimationState.stop();
        this.attackAnimationState.stop();
        this.deathAnimationState.stop();
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        this.customDeathTime = 0;
        this.stopAllAnimationStates();
        this.setAttackState(2);
    }

    @Override
    protected void tickDeath() {
        ++this.customDeathTime;
        if (this.customDeathTime >= 60) {
            this.remove(Entity.RemovalReason.KILLED);
            this.gameEvent(GameEvent.ENTITY_DIE);
        }
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.ON_FIRE) || source.is(DamageTypes.IN_FIRE) || source.is(DamageTypes.LAVA)) {
            return false;
        }
        if (source.getDirectEntity() instanceof AbstractArrow) {
            return false;
        }
        return super.hurt(source, amount);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.CHORUSLING_AMBIENT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.CHORUSLING_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.CHORUSLING_DEATH.get();
    }

    public static class AttackGoal extends MeleeAttackGoal {

        private final ChoruslingServant chorusling;
        private int attackDelay = 9;
        private int ticksUntilNextAttack = 9;
        private boolean shouldCountTillNextAttack;
        private boolean hasPlayedAttackSound;

        public AttackGoal(ChoruslingServant chorusling, double speedModifier) {
            super(chorusling, speedModifier, true);
            this.chorusling = chorusling;
        }

        @Override
        public void start() {
            super.start();
            this.attackDelay = 9;
            this.ticksUntilNextAttack = 9;
            this.hasPlayedAttackSound = false;
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
            if (distToEnemySqr <= 12.25D) {
                this.shouldCountTillNextAttack = true;
                if (this.isTimeToStartAttackAnimation() && !this.hasPlayedAttackSound) {
                    this.chorusling.playSound(ModSounds.CHORUSLING_ATTACK.get(), 1.0F, 1.0F);
                    this.chorusling.setAttackState(1);
                    this.hasPlayedAttackSound = true;
                }
                if (this.isTimeToAttack()) {
                    this.chorusling.getLookControl().setLookAt(enemy.getX(), enemy.getEyeY(), enemy.getZ());
                    this.hasPlayedAttackSound = false;
                    this.performAttack(enemy);
                }
            } else {
                this.resetAttackCooldown();
                this.shouldCountTillNextAttack = false;
                if (this.chorusling.getAttackState() != 0) {
                    this.chorusling.setAttackState(0);
                }
                this.chorusling.attackAnimationTimeout = 5;
                this.hasPlayedAttackSound = false;
            }
        }

        private boolean isTimeToStartAttackAnimation() {
            return this.ticksUntilNextAttack <= this.attackDelay;
        }

        @Override
        protected boolean isTimeToAttack() {
            return this.ticksUntilNextAttack <= 0;
        }

        @Override
        protected void resetAttackCooldown() {
            this.ticksUntilNextAttack = this.adjustedTickDelay(this.attackDelay * 2);
        }

        @Override
        public void tick() {
            super.tick();
            if (this.shouldCountTillNextAttack) {
                this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);
            }
        }

        @Override
        public void stop() {
            this.chorusling.setAttackState(0);
            super.stop();
            this.hasPlayedAttackSound = false;
        }

        private void performAttack(LivingEntity enemy) {
            this.resetAttackCooldown();
            this.chorusling.doHurtTarget(enemy);
            if (enemy.isAlive()) {
                enemy.addEffect(new MobEffectInstance(ModEffects.CHORUSINFECTION.get(), 60, 0));
            }
            if (enemy instanceof Player player && player.isBlocking()
                    && (player.getMainHandItem().getItem() instanceof ShieldItem
                    || player.getOffhandItem().getItem() instanceof ShieldItem)) {
                Item shieldItem = player.getMainHandItem().getItem() instanceof ShieldItem
                        ? player.getMainHandItem().getItem() : player.getOffhandItem().getItem();
                if (!player.getCooldowns().isOnCooldown(shieldItem)) {
                    player.getCooldowns().addCooldown(shieldItem, 40);
                    if (player.isBlocking()) {
                        player.stopUsingItem();
                    }
                }
            }
        }
    }
}
