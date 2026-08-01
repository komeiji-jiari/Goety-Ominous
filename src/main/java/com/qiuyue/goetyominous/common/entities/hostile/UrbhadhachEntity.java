package com.qiuyue.goetyominous.common.entities.hostile;

import com.Polarice3.Goety.api.entities.ICustomAttributes;
import com.Polarice3.Goety.common.effects.GoetyEffects;
import com.Polarice3.Goety.utils.EntityFinder;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.qiuyue.goetyominous.common.init.ModSounds;
import com.qiuyue.goetyominous.config.AttributesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UrbhadhachEntity extends Monster implements ICustomAttributes {
    private static final EntityDataAccessor<Boolean> DATA_STANDING_ID = SynchedEntityData.defineId(UrbhadhachEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_THRALLING_ID = SynchedEntityData.defineId(UrbhadhachEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_ROAR_ID = SynchedEntityData.defineId(UrbhadhachEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Optional<UUID>> THRALL_UUID = SynchedEntityData.defineId(UrbhadhachEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private float clientSideStandAnimationO;
    private float clientSideStandAnimation;
    private int warningSoundTicks;
    private int roarCooldown;
    private int thrallCooldown;
    private int healTime;

    public UrbhadhachEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.setMaxUpStep(1.0F);
        this.xpReward = 20;
        this.moveControl = new UrbhadhachMoveControl(this);
        this.setPathfindingMalus(net.minecraft.world.level.pathfinder.BlockPathTypes.WATER, 0.0F);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new JumpAtTargetGoal(this, 0.4F));
        this.goalSelector.addGoal(2, new AttackGoal());
        this.goalSelector.addGoal(3, new RestrictSunGoal(this));
        this.goalSelector.addGoal(4, new FleeSunGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, AgeableMob.class, 10, true, false, LivingEntity::isBaby));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, net.minecraft.world.entity.animal.IronGolem.class, true));
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.UrbhadhachHealth.get())
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.UrbhadhachDamage.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.25D);
    }

    @Override
    public void setConfigurableAttributes() {
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(AttributesConfig.UrbhadhachHealth.get());
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(AttributesConfig.UrbhadhachDamage.get());
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effectInstance) {
        if (effectInstance.getEffect() == MobEffects.MOVEMENT_SLOWDOWN
                || effectInstance.getEffect() == GoetyEffects.FREEZING.get()) {
            return false;
        }
        return super.canBeAffected(effectInstance);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.URBHADHACH_AMBIENT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSounds.URBHADHACH_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.URBHADHACH_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        this.playSound(ModSounds.URBHADHACH_STEP.get(), 0.15F, 1.0F);
    }

    protected void playWarningSound() {
        if (this.warningSoundTicks <= 0) {
            this.playSound(ModSounds.URBHADHACH_ROAR.get(), 1.0F, 1.0F);
            this.warningSoundTicks = 40;
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_THRALLING_ID, false);
        this.entityData.define(DATA_STANDING_ID, false);
        this.entityData.define(DATA_ROAR_ID, 0);
        this.entityData.define(THRALL_UUID, Optional.empty());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.getThrallUUID() != null) {
            compound.putUUID("Thrall", this.getThrallUUID());
        }
        compound.putInt("ThrallCooldown", this.thrallCooldown);
        compound.putInt("RoarCooldown", this.roarCooldown);
        compound.putInt("HealTime", this.healTime);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.hasUUID("Thrall")) {
            this.setThrallUuid(compound.getUUID("Thrall"));
        }
        this.thrallCooldown = compound.getInt("ThrallCooldown");
        this.roarCooldown = compound.getInt("RoarCooldown");
        this.healTime = compound.getInt("HealTime");
    }

    @Nullable
    public AgeableMob getThrall() {
        try {
            UUID uuid = this.getThrallUUID();
            if (uuid != null) {
                LivingEntity entity = EntityFinder.getLivingEntityByUuiD(uuid);
                if (entity instanceof AgeableMob && entity.isBaby()) {
                    return (AgeableMob) entity;
                }
            }
            return null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Nullable
    public UUID getThrallUUID() {
        return this.entityData.get(THRALL_UUID).orElse(null);
    }

    public void setThrallUuid(UUID uuid) {
        this.entityData.set(THRALL_UUID, Optional.ofNullable(uuid));
    }

    public void setThrall(AgeableMob ageableMob) {
        this.setThrallUuid(ageableMob.getUUID());
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (this.thrallCooldown > 0) {
            --this.thrallCooldown;
        }

        if (this.getLastHurtByMob() == null
                && this.lastHurtByPlayer == null
                && !this.isUnderWater()
                && this.thrallCooldown <= 0
                && (!this.level().isDay() || this.level().isRainingAt(this.blockPosition()))) {
            this.setThralling(true);
        }

        if (this.isThralling()) {
            if (this.getThrall() == null) {
                List<AgeableMob> list = this.level().getEntitiesOfClass(AgeableMob.class, this.getBoundingBox().inflate(64.0D, 8.0D, 64.0D));
                if (!list.isEmpty()) {
                    for (AgeableMob ageableMob : list) {
                        if (ageableMob.isBaby() && !ageableMob.isDeadOrDying() && !ageableMob.hasEffect(MobEffects.NIGHT_VISION)) {
                            this.setThrall(ageableMob);
                        }
                    }
                }
            }
            if (this.getThrall() != null) {
                if (this.getThrall().distanceToSqr(this) > 16 && !this.getThrall().isUnderWater()) {
                    if (this.getThrall().isSleeping()) {
                        this.getThrall().stopSleeping();
                    } else {
                        float speed = 0.5F;
                        if (this.getThrall().getAttribute(Attributes.FOLLOW_RANGE) != null) {
                            if (this.getThrall().getAttributeValue(Attributes.MOVEMENT_SPEED) < 0.5F) {
                                speed = 1.0F;
                            }
                            if (this.pathToUrbhadhach(this.getThrall()) != null && this.getThrall().getNavigation().isDone()) {
                                this.getThrall().getNavigation().moveTo(this.pathToUrbhadhach(this.getThrall()), speed);
                            }
                        } else {
                            this.thrallCooldown = 600;
                            this.setThralling(false);
                        }
                    }
                    if (this.tickCount % 20 == 0) {
                        this.getThrall().addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 40));
                        this.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 40));
                    }
                    if (this.tickCount % 100 == 0) {
                        this.playSound(SoundEvents.BELL_BLOCK, 2.0F, 0.25F);
                        this.getThrall().playSound(SoundEvents.BELL_RESONATE, 1.0F, 0.5F);
                    }
                } else {
                    this.thrallCooldown = 600;
                    this.setThralling(false);
                }
                if (this.getThrall().isDeadOrDying()) {
                    this.setThrallUuid(null);
                }
            }
        }

        if (this.hurtTime > 0) {
            this.thrallCooldown = 600;
            this.setThralling(false);
        }

        boolean hasEnemyNearby = false;
        for (LivingEntity livingEntity : this.level().getEntitiesOfClass(LivingEntity.class,
                this.getBoundingBox().inflate(8),
                com.Polarice3.Goety.utils.MobUtil.NO_CREATIVE_OR_SPECTATOR)) {
            if (livingEntity instanceof Mob mob && mob.getTarget() == this) {
                hasEnemyNearby = true;
                this.setTarget(livingEntity);
            } else if (livingEntity instanceof Player) {
                hasEnemyNearby = true;
                this.setTarget(livingEntity);
            }
        }
        if (hasEnemyNearby) {
            this.thrallCooldown = 600;
            this.setThralling(false);
        }

        if (this.isSunBurnTick()) {
            this.setSecondsOnFire(8);
        }

        if (this.healTime > 0) {
            --this.healTime;
            if (this.tickCount % 20 == 0) {
                this.heal(1.0F);
            }
        }

        if (this.roarCooldown > 0) {
            --this.roarCooldown;
        }

        if (this.roarCooldown <= 0) {
            if (this.getTarget() != null && !this.getTarget().isBaby() && !this.isStanding() && !this.isThralling() && this.isAlive()) {
                this.getLookControl().setLookAt(this.getTarget().position());
                if (this.hasEffect(MobEffects.INVISIBILITY)) {
                    this.removeEffect(MobEffects.INVISIBILITY);
                }
                this.roarCooldown = 300;
                this.setRoarTick(20);
                this.level().broadcastEntityEvent(this, (byte) 104);
            }
        }

        if (this.getRoarTick() > 0) {
            this.decreaseRoarTick();
            if (this.getRoarTick() == 10) {
                this.roar();
            }
        }

        if (this.getHealth() <= this.getMaxHealth() / 2) {
            for (AgeableMob ageableMob : this.level().getEntitiesOfClass(AgeableMob.class, this.getBoundingBox().inflate(32))) {
                if (ageableMob.isBaby() && this.getTarget() != ageableMob) {
                    this.setTarget(ageableMob);
                }
            }
            if (this.getTarget() != null && this.getTarget().isBaby()) {
                this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20, 1, false, false));
            }
        }
    }

    public net.minecraft.world.level.pathfinder.Path pathToUrbhadhach(AgeableMob ageableMob) {
        if (this.getY() < this.level().getMinBuildHeight()
                || this.getY() > this.level().getMaxBuildHeight()
                || ageableMob.getY() < ageableMob.level().getMinBuildHeight()
                || ageableMob.getY() > ageableMob.level().getMaxBuildHeight()) {
            return null;
        }
        return ageableMob.getNavigation().createPath(this, 0);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            if (this.clientSideStandAnimation != this.clientSideStandAnimationO) {
                this.refreshDimensions();
            }

            this.clientSideStandAnimationO = this.clientSideStandAnimation;
            if (this.isStanding()) {
                this.clientSideStandAnimation = Mth.clamp(this.clientSideStandAnimation + 1.0F, 0.0F, 6.0F);
            } else {
                this.clientSideStandAnimation = Mth.clamp(this.clientSideStandAnimation - 1.0F, 0.0F, 6.0F);
            }
        }

        if (this.warningSoundTicks > 0) {
            --this.warningSoundTicks;
        }
    }

    public int getRoarTick() {
        return this.entityData.get(DATA_ROAR_ID);
    }

    public void setRoarTick(int roarTick) {
        this.entityData.set(DATA_ROAR_ID, roarTick);
    }

    public void decreaseRoarTick() {
        this.setRoarTick(this.getRoarTick() - 1);
    }

    @Override
    public boolean isSunBurnTick() {
        return !this.level().isClientSide
                && this.level().isDay()
                && this.level().canSeeSky(this.blockPosition())
                && !this.level().isRainingAt(this.blockPosition());
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        if (this.clientSideStandAnimation > 0.0F) {
            float f = this.clientSideStandAnimation / 6.0F;
            float f1 = 1.0F + f;
            return super.getDimensions(pose).scale(1.0F, f1);
        } else {
            return super.getDimensions(pose);
        }
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean flag = target.hurt(this.damageSources().mobAttack(this), (int) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
        if (flag) {
            this.level().broadcastEntityEvent(this, (byte) 105);
            this.playSound(ModSounds.URBHADHACH_ATTACK.get(), 1.0F, 1.0F);
            if (target instanceof LivingEntity livingEntity) {
                livingEntity.addEffect(new MobEffectInstance(GoetyEffects.FREEZING.get(), 100, 1), this);
                if (this.random.nextFloat() < 0.25F) {
                    if (livingEntity instanceof Player player) {
                        if (player.isUsingItem() && player.getUseItem().is(Items.SHIELD)) {
                            player.getCooldowns().addCooldown(Items.SHIELD, 100);
                            player.stopUsingItem();
                            player.level().broadcastEntityEvent(player, (byte) 30);
                        }
                    }
                    livingEntity.knockback(4.0F,
                            Mth.sin(livingEntity.getYRot() * ((float) Math.PI / 180F)),
                            -Mth.cos(livingEntity.getYRot() * ((float) Math.PI / 180F)));
                    livingEntity.setDeltaMovement(livingEntity.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
                }
                if (livingEntity.isBaby()) {
                    livingEntity.hurt(this.damageSources().mobAttack(this), livingEntity.getMaxHealth());
                }
                if (livingEntity.isDeadOrDying()) {
                    this.onKillBaby(livingEntity);
                }
            }
            this.doEnchantDamageEffects(this, target);
        }
        return flag;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else {
            float damage = amount;

            if (com.Polarice3.Goety.utils.ModDamageSource.freezeAttacks(source)
                    || source.is(net.minecraft.world.damagesource.DamageTypes.FREEZE)) {
                return false;
            }

            if (source.is(net.minecraft.tags.DamageTypeTags.IS_FIRE)) {
                damage = amount * 2;
            }

            if (this.isStanding()) {
                damage = amount * 1.5F;
            }

            return super.hurt(source, damage);
        }
    }

    private void onKillBaby(LivingEntity killedEntity) {
        if (killedEntity.isBaby() && killedEntity.getMobType() != MobType.UNDEAD) {
            this.playSound(SoundEvents.GENERIC_EAT, 2.0F, 0.25F);
            this.playSound(SoundEvents.PLAYER_BURP, 2.0F, 0.25F);
            this.heal(killedEntity.getMaxHealth() * 2);
            this.healTime = 600;
        }
    }

    private void roar() {
        if (this.isAlive() && !this.isSilent()) {
            for (Entity entity : this.level().getEntitiesOfClass(LivingEntity.class,
                    this.getBoundingBox().inflate(4.0D),
                    com.Polarice3.Goety.utils.MobUtil.NO_CREATIVE_OR_SPECTATOR.and(e -> e != this))) {
                if (entity.hurt(ModDamageSource.directFreeze(this), 6.0F)) {
                    this.knockBack(entity);
                }
            }

            if (this.isOnFire()) {
                this.clearFire();
            }

            if (!this.level().isClientSide) {
                ServerLevel serverLevel = (ServerLevel) this.level();
                for (LivingEntity livingEntity : this.level().getEntitiesOfClass(LivingEntity.class,
                        this.getBoundingBox().inflate(16.0D),
                        com.Polarice3.Goety.utils.MobUtil.NO_CREATIVE_OR_SPECTATOR.and(e -> e != this))) {
                    if (livingEntity.canFreeze() && livingEntity.getMaxHealth() < this.getMaxHealth()) {
                        livingEntity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 300));
                    }
                }
                this.heal(2.0F);
                Vec3 center = this.getBoundingBox().getCenter();

                int particleRadius = 4;
                for (int ix = -particleRadius; ix <= particleRadius; ++ix) {
                    for (int j = -particleRadius; j <= particleRadius; ++j) {
                        for (int k = -particleRadius; k <= particleRadius; ++k) {
                            double d13 = (double) ix + (this.random.nextDouble() - this.random.nextDouble()) * 0.5D;
                            double d15 = (double) j + (this.random.nextDouble() - this.random.nextDouble()) * 0.5D;
                            double d17 = (double) k + (this.random.nextDouble() - this.random.nextDouble()) * 0.5D;
                            double d19 = Math.sqrt(d13 * d13 + d15 * d15 + d17 * d17) / 0.5D + this.random.nextGaussian() * 0.05D;

                            serverLevel.sendParticles(com.Polarice3.Goety.client.particles.ModParticleTypes.FROST_NOVA.get(),
                                    center.x, center.y, center.z,
                                    0,
                                    d13 / d19, d15 / d19, d17 / d19,
                                    0.5F);

                            if (ix != -particleRadius && ix != particleRadius && j != -particleRadius && j != particleRadius) {
                                k += particleRadius * 2 - 1;
                            }
                        }
                    }
                }

                serverLevel.sendParticles(
                        new com.Polarice3.Goety.client.particles.ShockwaveParticleOption(0.0F, (float)particleRadius * 2.0F, 1),
                        center.x, center.y + 0.5D, center.z,
                        0, 0.0D, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    private void knockBack(Entity entity) {
        double d0 = entity.getX() - this.getX();
        double d1 = entity.getZ() - this.getZ();
        double d2 = Math.max(d0 * d0 + d1 * d1, 0.001D);
        entity.push(d0 / d2 * 2.0D, 0.1D, d1 / d2 * 2.0D);
    }

    @Override
    protected boolean isImmobile() {
        return super.isImmobile() || this.getRoarTick() > 0;
    }

    public boolean isThralling() {
        return this.entityData.get(DATA_THRALLING_ID);
    }

    public void setThralling(boolean thralling) {
        this.entityData.set(DATA_THRALLING_ID, thralling);
    }

    public boolean isStanding() {
        return this.entityData.get(DATA_STANDING_ID);
    }

    public void setStanding(boolean standing) {
        this.entityData.set(DATA_STANDING_ID, standing);
    }

    public float getStandingAnimationScale(float partialTick) {
        return Mth.lerp(partialTick, this.clientSideStandAnimationO, this.clientSideStandAnimation) / 6.0F;
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 104) {
            this.roarCooldown = 300;
            this.setRoarTick(20);
            this.playSound(ModSounds.URBHADHACH_STRONG_ROAR.get(), 5.0F, 1.0F);
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    protected float getWaterSlowDown() {
        return 0.98F;
    }

    class AttackGoal extends MeleeAttackGoal {
        private int attackSpeed;

        public AttackGoal() {
            super(UrbhadhachEntity.this, 1.25D, true);
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
            double d0 = this.getAttackReachSqr(enemy);
            if (distToEnemySqr <= d0 && this.attackTime()) {
                this.resetAttack();
                this.mob.doHurtTarget(enemy);
                UrbhadhachEntity.this.setStanding(false);
            } else if (distToEnemySqr <= d0 * 2.0D) {
                if (this.attackTime()) {
                    UrbhadhachEntity.this.setStanding(false);
                    this.resetAttack();
                }

                if (this.ticksUntilNextAttack() <= 10) {
                    UrbhadhachEntity.this.setStanding(true);
                    UrbhadhachEntity.this.playWarningSound();
                }
            } else {
                this.resetAttack();
                UrbhadhachEntity.this.setStanding(false);
            }
        }

        @Override
        public void start() {
            super.start();
            this.attackSpeed = 0;
        }

        @Override
        public void stop() {
            UrbhadhachEntity.this.setStanding(false);
            super.stop();
        }

        @Override
        public void tick() {
            this.attackSpeed = Math.max(this.attackSpeed - 1, 0);
            super.tick();
        }

        protected void resetAttack() {
            this.attackSpeed = this.mob.getRandom().nextFloat() <= 0.25 ? 10 : 20;
        }

        protected boolean attackTime() {
            return this.attackSpeed <= 0;
        }

        @Override
        protected int getAttackInterval() {
            return this.attackSpeed;
        }

        protected int ticksUntilNextAttack() {
            return this.attackSpeed;
        }
    }

    static class JumpAtTargetGoal extends Goal {
        private final UrbhadhachEntity mob;
        private LivingEntity target;
        private final float yd;

        public JumpAtTargetGoal(UrbhadhachEntity mob, float yd) {
            this.mob = mob;
            this.yd = yd;
            this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (this.mob.isVehicle()) {
                return false;
            } else {
                this.target = this.mob.getTarget();
                if (target == null) {
                    return false;
                } else {
                    double d0 = this.mob.distanceToSqr(target);
                    if (d0 >= Mth.square(4.0F) && d0 < Mth.square(8.0F)) {
                        if (!this.mob.onGround() || this.mob.isUnderWater()) {
                            return false;
                        } else {
                            return this.mob.getRandom().nextFloat() < 0.25F;
                        }
                    } else {
                        return false;
                    }
                }
            }
        }

        @Override
        public boolean canContinueToUse() {
            return !this.mob.onGround();
        }

        @Override
        public void start() {
            this.mob.setStanding(true);
            Vec3 delta = this.mob.getDeltaMovement();
            Vec3 dir = new Vec3(this.target.getX() - this.mob.getX(), 0.0D, this.target.getZ() - this.mob.getZ());
            if (dir.lengthSqr() > 1.0E-7D) {
                dir = dir.normalize().add(delta);
            }
            double d2 = this.target.getX() - this.mob.getX();
            double d1 = this.target.getZ() - this.mob.getZ();
            this.mob.setYRot(-((float) Mth.atan2(d2, d1)) * (180F / (float) Math.PI));
            this.mob.yBodyRot = this.mob.getYRot();

            this.mob.setDeltaMovement(dir.x, this.yd, dir.z);
        }

        @Override
        public void stop() {
            this.mob.setStanding(false);
        }
    }

    static class UrbhadhachMoveControl extends MoveControl {
        private final UrbhadhachEntity urbhadhach;

        public UrbhadhachMoveControl(UrbhadhachEntity urbhadhach) {
            super(urbhadhach);
            this.urbhadhach = urbhadhach;
        }

        @Override
        public void tick() {
            LivingEntity target = this.urbhadhach.getTarget();
            if (this.urbhadhach.isInWater()) {
                if (target != null && target.getY() > this.urbhadhach.getY()) {
                    this.urbhadhach.setDeltaMovement(this.urbhadhach.getDeltaMovement().add(0.0D, 0.002D, 0.0D));
                }

                if (this.operation != Operation.MOVE_TO || this.urbhadhach.getNavigation().isDone()) {
                    this.urbhadhach.setSpeed(0.0F);
                    return;
                }

                double d0 = this.wantedX - this.urbhadhach.getX();
                double d1 = this.wantedY - this.urbhadhach.getY();
                double d2 = this.wantedZ - this.urbhadhach.getZ();
                double d3 = Mth.sqrt((float) (d0 * d0 + d1 * d1 + d2 * d2));
                d1 = d1 / d3;
                float f = (float) (Mth.atan2(d2, d0) * (double) (180F / (float) Math.PI)) - 90.0F;
                this.urbhadhach.setYRot(this.rotlerp(this.urbhadhach.getYRot(), f, 90.0F));
                this.urbhadhach.yBodyRot = this.urbhadhach.getYRot();
                float speed = (float) (this.speedModifier * this.urbhadhach.getAttributeValue(Attributes.MOVEMENT_SPEED));
                float f2 = Mth.lerp(0.125F, this.urbhadhach.getSpeed(), speed);
                this.urbhadhach.setSpeed(f2);
                this.urbhadhach.setDeltaMovement(this.urbhadhach.getDeltaMovement().add(
                        f2 * d0 * 0.005D, f2 * d1 * 0.1D, f2 * d2 * 0.005D));
            } else {
                if (!this.urbhadhach.onGround()) {
                    this.urbhadhach.setDeltaMovement(this.urbhadhach.getDeltaMovement().add(0.0D, -0.008D, 0.0D));
                }
                super.tick();
            }
        }
    }
}
