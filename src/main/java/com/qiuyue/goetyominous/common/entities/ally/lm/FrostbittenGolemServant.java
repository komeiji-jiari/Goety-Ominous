package com.qiuyue.goetyominous.common.entities.ally.lm;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.projectiles.FlyingItem;
import com.Polarice3.Goety.config.ItemConfig;
import com.Polarice3.Goety.utils.MobUtil;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.IAttackGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.IMoveGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.IStateGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.projectile.IceSpikeEntity;
import com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry;
import com.qiuyue.goetyominous.compat.lm.LmScreenShake;
import com.qiuyue.goetyominous.utils.ServantAllyUtil;
import net.miauczel.legendary_monsters.effect.ModEffects;
import net.miauczel.legendary_monsters.item.ModItems;
import net.miauczel.legendary_monsters.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import com.qiuyue.goetyominous.config.lm.LmConfig;

public class FrostbittenGolemServant extends IAnimatedMiniBossServant {
    private static final EntityDataAccessor<Boolean> DROPPED_ICE_1 =
            SynchedEntityData.defineId(FrostbittenGolemServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DROPPED_ICE_2 =
            SynchedEntityData.defineId(FrostbittenGolemServant.class, EntityDataSerializers.BOOLEAN);
    private static final int AWAKE_TICKS = 20;
    private static final int SMASH_COOLDOWN = 160;
    private static final int SMASH_ANCHOR_COOLDOWN = 160;
    private static final int SMASH_ANCHOR2_COOLDOWN = 100;

    private int teleportCooldown;
    private int smashCooldown;
    public int bigsmashCooldown;
    public int bigsmash2Cooldown;
    public int golemDeathTime;

    public AnimationState idleAnimationState = new AnimationState();
    public AnimationState awakeAnimationState = new AnimationState();
    public AnimationState sleepAnimationState = new AnimationState();
    public AnimationState attackarm1AnimationState = new AnimationState();
    public AnimationState attackarm2AnimationState = new AnimationState();
    public AnimationState attackarmsAnimationState = new AnimationState();
    public AnimationState iceSpikeSummonAnimationState = new AnimationState();
    public AnimationState aggresiveIceSpikeSummonAnimationState = new AnimationState();
    public AnimationState deathAnimationState = new AnimationState();

    public FrostbittenGolemServant(EntityType<? extends IAnimatedMiniBossServant> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.xpReward = 100;
        this.setNoAi(false);
        this.setPersistenceRequired();
    }

    @Override
    public int getSummonLimit(LivingEntity owner) {
        return LmConfig.FrostbittenGolemServantLimit.get();
    }

    @Override
    public double damageMultiplier() {
        return LmConfig.FrostbittenGolemServantDamageMultiplier.get();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DROPPED_ICE_1, false);
        this.entityData.define(DROPPED_ICE_2, false);
    }

    @Override
    public boolean addEffect(MobEffectInstance effect, @Nullable Entity entity) {
        if (this.isSleep()) {
            return false;
        }
        return super.addEffect(effect, entity);
    }

    private boolean isStandby() {
        return this.isStaying() && !this.isCommanded() && this.getTarget() == null;
    }

    public boolean isSleep() {
        return this.getAttackState() == 1 || this.getAttackState() == 2;
    }

    public void setSleep(boolean sleep) {
        this.setAttackState(sleep ? 1 : 0);
    }

    public void resetSmashAnchorCooldown() {
        if (this.bigsmashCooldown <= 0) {
            this.bigsmashCooldown = SMASH_ANCHOR_COOLDOWN;
        }
    }

    public void resetSmash2Cooldown() {
        if (this.bigsmash2Cooldown <= 0) {
            this.bigsmash2Cooldown = SMASH_ANCHOR2_COOLDOWN;
        }
    }

    public void resetSmashCooldown() {
        if (this.smashCooldown <= 0) {
            this.smashCooldown = SMASH_COOLDOWN;
        }
    }

    @Override
    public void tick() {
        if (!this.level().isClientSide && this.isStandby() && this.getAttackState() == 0) {
            this.setSleep(true);
        }
        super.tick();
        if (!this.level().isClientSide) {
            if (this.getAttackState() == 1 && !this.isStandby()) {
                this.setAttackState(2);
            } else if (this.getAttackState() == 2 && this.attackTicks >= AWAKE_TICKS) {
                this.setSleep(false);
            }
        }
        this.updateWithAttack();
        if (this.smashCooldown > 0) {
            --this.smashCooldown;
        }
        if (this.teleportCooldown > 0) {
            --this.teleportCooldown;
        }
        if (this.bigsmashCooldown > 0) {
            --this.bigsmashCooldown;
        }
        if (this.bigsmash2Cooldown > 0) {
            --this.bigsmash2Cooldown;
        }
        if (!this.level().isClientSide) {
            Crackiness crackiness = this.getCrackiness();
            if (crackiness == Crackiness.MEDIUM && !this.entityData.get(DROPPED_ICE_1)) {
                this.playCrackSound();
                this.entityData.set(DROPPED_ICE_1, true);
            }
            if (crackiness == Crackiness.HIGH && !this.entityData.get(DROPPED_ICE_2)) {
                this.playCrackSound();
                this.entityData.set(DROPPED_ICE_2, true);
            }
        }
    }

    private void returnRune() {
        if (this.getTrueOwner() == null) {
            return;
        }
        FlyingItem flyingItem = new FlyingItem(
                com.Polarice3.Goety.common.entities.ModEntityType.FLYING_ITEM.get(),
                this.level(),
                this.getX(),
                this.getY() + 1.0D,
                this.getZ());
        flyingItem.setOwner(this.getTrueOwner());
        flyingItem.setItem(new ItemStack(ModItems.FROZEN_RUNE.get()));
        flyingItem.setParticle(ParticleTypes.SNOWFLAKE);
        flyingItem.setSecondsCool(ItemConfig.ReviveSecondsCool.get());
        this.level().addFreshEntity(flyingItem);
    }

    private void playCrackSound() {
        this.level().playSound(null, this.blockPosition(), SoundEvents.AMETHYST_BLOCK_BREAK, SoundSource.NEUTRAL, 3.0F, 1.0F);
        this.level().playSound(null, this.blockPosition(), SoundEvents.GLASS_BREAK, SoundSource.NEUTRAL, 2.0F, 1.0F);
        this.level().playSound(null, this.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.NEUTRAL, 15.0F, 1.0F);
    }

    public Crackiness getCrackiness() {
        return Crackiness.byFraction(this.getHealth() / this.getMaxHealth());
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(6, new Summoned.WanderGoal<>(this, this.getFollowSpeed()));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(2, new IMoveGoal(this, false, 1.0D));

        this.goalSelector.addGoal(1, new IAttackGoal(this, 0, 7, 0, 35, 10, 5.0F) {
            public boolean canUse() {
                return super.canUse() && FrostbittenGolemServant.this.getRandom().nextFloat() * 45.0F < 16.0F
                        && FrostbittenGolemServant.this.bigsmashCooldown <= 0
                        && FrostbittenGolemServant.this.getTarget() != null;
            }

            public void stop() {
                super.stop();
                resetSmashAnchorCooldown();
            }
        });

        this.goalSelector.addGoal(1, new IAttackGoal(this, 0, 3, 0, 46, 10, 5.0F) {
            public boolean canUse() {
                return super.canUse() && FrostbittenGolemServant.this.getRandom().nextFloat() * 25.0F < 16.0F
                        && FrostbittenGolemServant.this.bigsmash2Cooldown <= 0
                        && FrostbittenGolemServant.this.getTarget() != null;
            }

            public void stop() {
                super.stop();
                resetSmash2Cooldown();
            }
        });

        this.goalSelector.addGoal(1, new IAttackGoal(this, 0, 4, 0, 46, 15, 5.0F) {
            public boolean canUse() {
                return super.canUse() && FrostbittenGolemServant.this.getRandom().nextFloat() * 35.0F < 16.0F
                        && FrostbittenGolemServant.this.smashCooldown <= 0
                        && FrostbittenGolemServant.this.getTarget() != null;
            }

            public void stop() {
                super.stop();
                resetSmashCooldown();
            }
        });

        this.goalSelector.addGoal(1, new IAttackGoal(this, 0, 6, 0, 46, 75, 5.0F));

        this.goalSelector.addGoal(1, new IAttackGoal(this, 0, 5, 0, 46, 30, 12.0F) {
            public boolean canUse() {
                return super.canUse() && FrostbittenGolemServant.this.getRandom().nextFloat() * 20.0F < 9.0F
                        && FrostbittenGolemServant.this.teleportCooldown <= 0;
            }

            public void stop() {
                super.stop();
                FrostbittenGolemServant.this.setInvulnerable(false);
                FrostbittenGolemServant.this.teleportCooldown = 60;
            }
        });

        this.goalSelector.addGoal(1, new IStateGoal(this, 1, 1, 0, 0, 0) {
            @Override
            public boolean canContinueToUse() {
                return this.entity.getAttackState() == 1 || this.entity.getAttackState() == 2;
            }

            @Override
            public void stop() {
                if (this.entity.getAttackState() == 1) {
                    super.stop();
                }
            }

            @Override
            public void tick() {
                this.entity.setDeltaMovement(0.0D, this.entity.getDeltaMovement().y, 0.0D);
            }
        });

        this.goalSelector.addGoal(0, new IAttackGoal(this, 1, 2, 0, AWAKE_TICKS, 0, 15.0F));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.isDeadOrDying()) {
            this.setYRot(0.0F);
            this.setXRot(0.0F);
            this.yBodyRot = 90.0F;
            this.yHeadRot = 0.0F;
        }
        if (!this.isDeadOrDying()) {
            LivingEntity target = this.getTarget();
            if (target != null) {
                double targetX = target.getX() - this.getX();
                double targetY = target.getEyeY() - this.getEyeY();
                double targetZ = target.getZ() - this.getZ();
                double horizontalDistance = Math.sqrt(targetX * targetX + targetZ * targetZ);
                float yaw = (float) (Math.atan2(targetZ, targetX) * 57.29577951308232) - 90.0F;
                float pitch = (float) (-(Math.atan2(targetY, horizontalDistance) * 57.29577951308232));
                this.yHeadRot = this.yBodyRot = yaw;
                this.yBodyRot = pitch;
            }
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, LmConfig.FrostbittenGolemServantHealth.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, LmConfig.FrostbittenGolemServantKnockbackResistance.get())
                .add(Attributes.FOLLOW_RANGE, LmConfig.FrostbittenGolemServantFollowRange.get())
                .add(Attributes.ARMOR, LmConfig.FrostbittenGolemServantArmor.get())
                .add(Attributes.MOVEMENT_SPEED, LmConfig.FrostbittenGolemServantMovementSpeed.get())
                .add(Attributes.ATTACK_KNOCKBACK, LmConfig.FrostbittenGolemServantAttackKnockback.get())
                .add(Attributes.ATTACK_DAMAGE, LmConfig.FrostbittenGolemServantDamage.get());
    }

    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), LmConfig.FrostbittenGolemServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), LmConfig.FrostbittenGolemServantArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), LmConfig.FrostbittenGolemServantDamage.get());
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return ModSounds.OVERGROWN_COLOSSUS_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.OVERGROWN_COLOSSUS_DEATH.get();
    }

    @Override
    public void die(DamageSource source) {
        if (!this.level().isClientSide) {
            this.returnRune();
        }
        super.die(source);
        this.golemDeathTime = 0;
        this.setAttackState(8);
        this.yBodyRot = 0.0F;
        this.yHeadRot = 0.0F;
        this.setYRot(0.0F);
        this.setXRot(0.0F);
    }

    @Override
    protected void tickDeath() {
        ++this.golemDeathTime;
        if (this.level() instanceof ServerLevel && this.golemDeathTime > 1 && !this.isSilent()) {
            this.setNoAi(true);
        }
        if (this.golemDeathTime == 25) {
            LmScreenShake.shake(this.level(), this.position(), 40.0F, 0.1F, 0, 20);
        }
        if (this.golemDeathTime == 60 && this.level() instanceof ServerLevel) {
            this.remove(Entity.RemovalReason.KILLED);
            this.gameEvent(GameEvent.ENTITY_DIE);
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isSleep() && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            if (!this.level().isClientSide && source.getEntity() instanceof LivingEntity attacker) {
                this.setLastHurtByMob(attacker);
                if (this.getAttackState() == 1) {
                    this.setAttackState(2);
                }
            }
            return false;
        }
        if (LmConfig.FrostbittenGolemServantProjectileImmunity.get()) {
            if (source.getDirectEntity() instanceof AbstractArrow) {
                return false;
            }
        }
        if (source.getDirectEntity() instanceof ThrownPotion || source.getDirectEntity() instanceof AreaEffectCloud) {
            return false;
        }
        return super.hurt(source, amount);
    }

    @Override
    public void tryKill(Player player) {
        if (this.killChance <= 0) {
            this.warnKill(player);
        } else {
            super.tryKill(player);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("is_Sleep", this.isSleep());
        compound.putBoolean("DroppedIce1", this.entityData.get(DROPPED_ICE_1));
        compound.putBoolean("DroppedIce2", this.entityData.get(DROPPED_ICE_2));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setSleep(compound.getBoolean("is_Sleep"));
        this.entityData.set(DROPPED_ICE_1, compound.getBoolean("DroppedIce1"));
        this.entityData.set(DROPPED_ICE_2, compound.getBoolean("DroppedIce2"));
    }

    public AnimationState getAnimationState(String input) {
        if (input == "sleep") {
            return this.sleepAnimationState;
        }
        if (input == "awake") {
            return this.awakeAnimationState;
        }
        if (input == "idle") {
            return this.idleAnimationState;
        }
        if (input == "attackarmright") {
            return this.attackarm1AnimationState;
        }
        if (input == "attackarmleft") {
            return this.attackarm2AnimationState;
        }
        if (input == "attackarms") {
            return this.attackarmsAnimationState;
        }
        if (input == "attackicespike") {
            return this.iceSpikeSummonAnimationState;
        }
        if (input == "attackaggresive") {
            return this.aggresiveIceSpikeSummonAnimationState;
        }
        if (input == "death") {
            return this.deathAnimationState;
        }
        return new AnimationState();
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (ATTACK_STATE.equals(key) && this.level().isClientSide) {
            switch (this.getAttackState()) {
                case 0 -> this.stopAllAnimationStates();
                case 1 -> {
                    this.stopAllAnimationStates();
                    this.sleepAnimationState.startIfStopped(this.tickCount);
                }
                case 2 -> {
                    this.stopAllAnimationStates();
                    this.awakeAnimationState.startIfStopped(this.tickCount);
                }
                case 3 -> {
                    this.stopAllAnimationStates();
                    this.attackarm1AnimationState.startIfStopped(this.tickCount);
                }
                case 4 -> {
                    this.stopAllAnimationStates();
                    this.attackarm2AnimationState.startIfStopped(this.tickCount);
                }
                case 5 -> {
                    this.stopAllAnimationStates();
                    this.iceSpikeSummonAnimationState.startIfStopped(this.tickCount);
                }
                case 6 -> {
                    this.stopAllAnimationStates();
                    this.attackarmsAnimationState.startIfStopped(this.tickCount);
                }
                case 7 -> {
                    this.stopAllAnimationStates();
                    this.aggresiveIceSpikeSummonAnimationState.startIfStopped(this.tickCount);
                }
                case 8 -> {
                    this.stopAllAnimationStates();
                    this.deathAnimationState.startIfStopped(this.tickCount);
                }
            }
        }
        super.onSyncedDataUpdated(key);
    }

    public void stopAllAnimationStates() {
        this.sleepAnimationState.stop();
        this.awakeAnimationState.stop();
        this.attackarmsAnimationState.stop();
        this.iceSpikeSummonAnimationState.stop();
        this.aggresiveIceSpikeSummonAnimationState.stop();
        this.attackarm1AnimationState.stop();
        this.attackarm2AnimationState.stop();
        this.deathAnimationState.stop();
    }

    private void launchMini(LivingEntity entity, boolean huge) {
        double deltaX = entity.getX() - this.getX();
        double deltaZ = entity.getZ() - this.getZ();
        double distanceSquared = Math.max(deltaX * deltaX + deltaZ * deltaZ, 0.001);
        float multiplier = huge ? 1.2F : 0.5F;
        entity.push(deltaX / distanceSquared * (double) multiplier, huge ? 0.3 : 0.2, deltaZ / distanceSquared * (double) multiplier);
    }

    private void AreaAttack(float range, float height, float arc, float damage, int shieldbreakticks, boolean stun, float knockback, boolean BNknockback, float Lstrenght, boolean launch) {
        List<LivingEntity> entitiesHit = this.getEntityLivingBaseNearby(range, height, range, range);
        for (LivingEntity entityHit : entitiesHit) {
            float entityHitAngle = (float) ((Math.atan2(entityHit.getZ() - this.getZ(), entityHit.getX() - this.getX()) * (180 / Math.PI) - 90) % 360);
            float entityAttackingAngle = this.yBodyRot % 360;
            if (entityHitAngle < 0) {
                entityHitAngle += 360;
            }
            if (entityAttackingAngle < 0) {
                entityAttackingAngle += 360;
            }

            float entityRelativeAngle = entityHitAngle - entityAttackingAngle;
            float entityHitDistance = (float) Math.sqrt((entityHit.getZ() - this.getZ()) * (entityHit.getZ() - this.getZ()) + (entityHit.getX() - this.getX()) * (entityHit.getX() - this.getX()));
            if (entityHitDistance <= range && (entityRelativeAngle <= arc / 2 && entityRelativeAngle >= -arc / 2) || (entityRelativeAngle >= 360 - arc / 2 || entityRelativeAngle <= -360 + arc / 2)) {
                if (!isAlliedTo(entityHit) && !ServantAllyUtil.areAllied(this, entityHit) && !(entityHit instanceof FrostbittenGolemServant) && entityHit != this) {
                    if (!stun && !entityHit.isBlocking() && Math.random() > 0.5) {
                        entityHit.addEffect(new MobEffectInstance(ModEffects.FREEZE.get(), 40, 5));
                    }

                    entityHit.hurt(this.damageSources().mobAttack(this), damage);

                    if ((this.getAttackState() == 3 || this.getAttackState() == 4) && this.attackTicks == 20) {
                        if (BNknockback && !entityHit.isBlocking()) {
                            double knockbackRadius = 5.0;
                            double dx = entityHit.getX() - this.getX();
                            double dz = entityHit.getZ() - this.getZ();
                            double distance = Math.sqrt(dx * dx + dz * dz);
                            double knockbackStrength = (double) knockback + 0.5 * (knockbackRadius - distance);
                            entityHit.push(dx / distance * knockbackStrength, 0.4, dz / distance * knockbackStrength);
                        }
                        LmScreenShake.shake(this.level(), this.position(), 20.0F, 0.15F, 0, 20);
                    }

                    if (launch) {
                        if (this.getAttackState() == 6 || this.getAttackState() == 7) {
                            this.launch(entityHit, true);
                        }
                        if (this.getAttackState() == 3 || this.getAttackState() == 4) {
                            this.launchMini(entityHit, true);
                        }
                    }

                    if (entityHit instanceof Player && entityHit.isBlocking() && shieldbreakticks > 0) {
                        disableShield(entityHit, shieldbreakticks);
                    }
                }
            }
        }
    }

    private void updateWithAttack() {
        if (this.getAttackState() == 3 && this.attackTicks == 16) {
            this.playSound(ModSounds.ENDERSENT_ATTACK.get(), 1.0F, 1.0F);
            this.AreaAttack(6.0F, 5.0F, 150.0F, 12.0F, 40, false, 0.5F, true, 0.25F, true);
            LmScreenShake.shake(this.level(), this.position(), 20.0F, 0.15F, 0, 20);
        }
        if (this.getAttackState() == 4 && this.attackTicks == 16) {
            this.playSound(ModSounds.ENDERSENT_ATTACK.get(), 1.0F, 1.0F);
            this.AreaAttack(6.0F, 5.0F, 150.0F, 12.0F, 40, false, 0.5F, true, 0.25F, true);
            LmScreenShake.shake(this.level(), this.position(), 20.0F, 0.15F, 0, 20);
        }
        if (this.getAttackState() == 6) {
            if (this.attackTicks == 18) {
                this.spawnCircleParticle(3.0F, 0.0F, 20.0F, true, 2.0F, 0.25F, 0.5F, 1.0F, 1.0F);
                this.playSound(ModSounds.ENDERSENT_ATTACK.get(), 1.0F, 1.0F);
                this.AreaAttack(7.0F, 6.0F, 170.0F, 13.0F, 100, false, 0.5F, true, 0.25F, true);
                LmScreenShake.shake(this.level(), this.position(), 20.0F, 0.15F, 0, 20);
            }
            if (this.attackTicks == 28) {
                LivingEntity target = this.getTarget();
                Crackiness crackiness = this.getCrackiness();
                if (target != null && (crackiness == Crackiness.MEDIUM || crackiness == Crackiness.HIGH)) {
                    int standingOnY = Mth.floor(this.getY());
                    for (int k = 0; k < 6; ++k) {
                        float f2 = (float) k * (float) Math.PI * 2.0F / 6.0F + 1.2566371F;
                        this.spawnIceSpikesAdvanced(target.getX() + (double) Mth.cos(f2) * 2.5D, target.getZ() + (double) Mth.sin(f2) * 2.5D, standingOnY, target.getY() + 1.0D, f2, 0);
                    }
                    for (int k = 0; k < 11; ++k) {
                        float f3 = (float) k * (float) Math.PI * 2.0F / 11.0F + 0.62831855F;
                        this.spawnIceSpikesAdvanced(target.getX() + (double) Mth.cos(f3) * 3.5D, target.getZ() + (double) Mth.sin(f3) * 3.5D, standingOnY, target.getY() + 1.0D, f3, 2);
                    }
                }
                if (target != null && crackiness == Crackiness.HIGH) {
                    int standingOnY = Mth.floor(this.getY());
                    for (int k = 0; k < 14; ++k) {
                        float f4 = (float) k * (float) Math.PI * 2.0F / 14.0F + 0.31415927F;
                        this.spawnIceSpikesAdvanced(target.getX() + (double) Mth.cos(f4) * 4.5D, target.getZ() + (double) Mth.sin(f4) * 4.5D, standingOnY, target.getY() + 1.0D, f4, 4);
                    }
                }
            }
        }
        if (this.getAttackState() == 7 && this.attackTicks == 12) {
            this.playSound(ModSounds.ENDERSENT_ATTACK.get(), 1.0F, 1.0F);
            this.AreaAttack(4.5F, 5.0F, 100.0F, 13.0F, 80, false, 0.5F, true, 0.25F, true);
            LmScreenShake.shake(this.level(), this.position(), 20.0F, 0.15F, 0, 20);
            this.spawnCircleParticle(1.5F, 0.0F, 15.0F, true, 2.0F, 0.25F, 0.25F, 1.0F, 1.0F);
            this.strikeFire(19, 2.0D, 8.0F);
        }
        if (this.getAttackState() == 5 && this.attackTicks == 14 && this.getTarget() != null) {
            this.spawnIceSpikes();
            if (this.getCrackiness() == Crackiness.HIGH) {
                this.strikeFire(7, 1.0D, 6.0F);
            }
        }
    }

    private void spawnFire(double x, double z, double minY, double maxY, float rotation, int delay) {
        BlockPos blockpos = new BlockPos((int) x, (int) maxY, (int) z);
        boolean flag = false;
        double d0 = 0.0D;
        do {
            BlockPos blockpos1 = blockpos.below();
            BlockState blockstate = this.level().getBlockState(blockpos1);
            if (blockstate.isFaceSturdy(this.level(), blockpos1, Direction.UP)) {
                if (!this.level().isEmptyBlock(blockpos)) {
                    BlockState blockstate1 = this.level().getBlockState(blockpos);
                    VoxelShape voxelshape = blockstate1.getCollisionShape(this.level(), blockpos);
                    if (!voxelshape.isEmpty()) {
                        d0 = voxelshape.max(Direction.Axis.Y);
                    }
                }
                flag = true;
                break;
            }
            blockpos = blockpos.below();
        } while (blockpos.getY() >= Mth.floor(minY) - 1);

        if (flag && !this.level().isClientSide) {
            this.level().addFreshEntity(new IceSpikeEntity(this.level(), x, (double) blockpos.getY() + d0, z, rotation, delay, this));
        }
    }

    private void strikeFire(int ice, double time, float distance) {
        for (int i = 0; i < ice; ++i) {
            float throwAngle = (float) i * (float) Math.PI / (float) (ice / 2);
            int k = 0;
            while ((float) k < distance) {
                double d2 = 1.15D * (double) (k + 1);
                int d3 = (int) (time * (double) (k + 1));
                this.spawnFire(this.getX() + (double) Mth.cos(throwAngle) * 1.25D * d2, this.getZ() + (double) Mth.sin(throwAngle) * 1.25D * d2, this.getY(), this.getY() + 2.0D, throwAngle, d3);
                ++k;
            }
        }
    }

    private void createSpellEntity(double x, double z, double minY, double maxY, float yRot, int warmupDelay) {
        BlockPos blockpos = BlockPos.containing(x, maxY, z);
        boolean flag = false;
        double d0 = 0.0D;
        do {
            BlockPos blockpos1 = blockpos.below();
            BlockState blockstate = this.level().getBlockState(blockpos1);
            if (blockstate.isFaceSturdy(this.level(), blockpos1, Direction.UP)) {
                if (!this.level().isEmptyBlock(blockpos)) {
                    BlockState blockstate1 = this.level().getBlockState(blockpos);
                    VoxelShape voxelshape = blockstate1.getCollisionShape(this.level(), blockpos);
                    if (!voxelshape.isEmpty()) {
                        d0 = voxelshape.max(Direction.Axis.Y);
                    }
                }
                flag = true;
                break;
            }
            blockpos = blockpos.below();
        } while (blockpos.getY() >= Mth.floor(minY) - 1);

        if (flag && !this.level().isClientSide) {
            this.level().addFreshEntity(new IceSpikeEntity(this.level(), x, (double) blockpos.getY() + d0, z, yRot, warmupDelay, this));
        }
    }

    private void spawnIceSpikes() {
        LivingEntity target = this.getTarget();
        if (target != null) {
            double d0 = Math.min(target.getY(), this.getY());
            double d1 = Math.max(target.getY(), this.getY()) + 2.0D;
            float f = (float) Mth.atan2(target.getZ() - this.getZ(), target.getX() - this.getX());
            float f2 = Mth.cos(this.getYRot() * ((float) Math.PI / 180)) * 1.1F;
            float f3 = Mth.sin(this.getYRot() * ((float) Math.PI / 180)) * 1.1F;
            for (int l = 0; l < 10; ++l) {
                double d2 = 1.25D * (double) (l + 1);
                int j = (int) (1.25F * (float) l);
                this.createSpellEntity(this.getX() + (double) f2 + (double) Mth.cos(f) * d2, this.getZ() + (double) f3 + (double) Mth.sin(f) * d2, d0, d1, f, j);
                this.createSpellEntity(this.getX() - (double) f2 + (double) Mth.cos(f) * d2, this.getZ() - (double) f3 + (double) Mth.sin(f) * d2, d0, d1, f, j);
            }
        }
    }

    private void spawnIceSpikesAdvanced(double x, double z, double minY, double maxY, float rotation, int delay) {
        BlockPos blockpos = new BlockPos((int) x, (int) maxY, (int) z);
        boolean flag = false;
        double d0 = 0.0D;
        do {
            BlockPos blockpos1 = blockpos.below();
            BlockState blockstate = this.level().getBlockState(blockpos1);
            if (blockstate.isFaceSturdy(this.level(), blockpos1, Direction.UP)) {
                if (!this.level().isEmptyBlock(blockpos)) {
                    BlockState blockstate1 = this.level().getBlockState(blockpos);
                    VoxelShape voxelshape = blockstate1.getCollisionShape(this.level(), blockpos);
                    if (!voxelshape.isEmpty()) {
                        d0 = voxelshape.max(Direction.Axis.Y);
                    }
                }
                flag = true;
                break;
            }
            blockpos = blockpos.below();
        } while (blockpos.getY() >= Mth.floor(minY) - 1);

        if (flag && !this.level().isClientSide) {
            this.level().addFreshEntity(new IceSpikeEntity(this.level(), x, (double) blockpos.getY() + d0, z, rotation, delay, this));
        }
    }

    public enum Crackiness {
        NONE(1.0F),
        LOW(0.75F),
        MEDIUM(0.5F),
        HIGH(0.25F);

        private static final List<Crackiness> BY_DAMAGE = Stream.of(values()).sorted(Comparator.comparingDouble(c -> c.fraction)).toList();
        public final float fraction;

        Crackiness(float fraction) {
            this.fraction = fraction;
        }

        public static Crackiness byFraction(float fraction) {
            for (Crackiness crackiness : BY_DAMAGE) {
                if (fraction < crackiness.fraction) {
                    return crackiness;
                }
            }
            return NONE;
        }
    }
}
