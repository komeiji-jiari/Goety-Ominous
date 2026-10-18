package com.qiuyue.goetyominous.common.entities.ally.lm;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.projectiles.FlyingItem;
import com.Polarice3.Goety.config.ItemConfig;
import com.Polarice3.Goety.utils.MobUtil;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.IAttackGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.IAttackGoalMin;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.IMoveGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.IStateGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.projectile.IceShockwaveSpawner;
import com.qiuyue.goetyominous.compat.lm.LmScreenShake;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.qiuyue.goetyominous.config.MobsConfig;
import com.qiuyue.goetyominous.utils.ServantAllyUtil;
import net.miauczel.legendary_monsters.Particle.custom.Circle;
import net.miauczel.legendary_monsters.effect.ModEffects;
import net.miauczel.legendary_monsters.item.ModItems;
import net.miauczel.legendary_monsters.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
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
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class AncientGuardianServant extends IAnimatedMiniBossServant {
    private static final int AWAKE_TICKS = 20;
    private static final int SMASH_COOLDOWN = 100;
    private static final int SMASH_ANCHOR_COOLDOWN = 60;
    private static final int STUN_COOLDOWN = 200;
    private static final int[][] GRAB_PLAYER_RANGES = {
            {3, 6, 1}, {6, 9, 2}, {9, 12, 3}, {12, 13, 2}, {13, 14, 1}, {14, 20, -1},
            {20, 23, 1}, {23, 26, 2}, {26, 33, 3}, {33, 36, 2}, {36, 39, 1},
            {42, 46, -1}, {46, 49, 1}, {52, 55, 2}, {55, 61, 3}, {61, 64, 2}, {64, 66, 1}, {66, 69, -1}
    };
    private static final int[][] GRAB_MOB_RANGES = {
            {3, 6, 1, 0}, {6, 9, 0, 1}, {9, 12, 0, 2}, {12, 13, 0, 1}, {13, 14, 1, 0}, {14, 20, 2, -1},
            {20, 23, 1, 0}, {23, 26, 0, 1}, {26, 33, 0, 2}, {33, 36, 0, 1}, {36, 39, 1, 0},
            {42, 46, 2, -1}, {46, 49, 1, 0}, {52, 55, 0, 1}, {55, 61, 0, 2}, {61, 64, 0, 1}, {64, 66, 1, 0}, {66, 69, 2, -1}
    };

    private int stunCooldown;
    private int smashCooldown;
    private int smashAnchorCooldown;
    public int guardianDeathTime;

    public AnimationState idleAnimationState = new AnimationState();
    public AnimationState sleepAnimationState = new AnimationState();
    public AnimationState awakeAnimationState = new AnimationState();
    public AnimationState attackarm1AnimationState = new AnimationState();
    public AnimationState attackarm2AnimationState = new AnimationState();
    public AnimationState attackComboAnimationState = new AnimationState();
    public AnimationState deathAnimationState = new AnimationState();
    public AnimationState grabAnimationState = new AnimationState();
    public AnimationState runAnimationState = new AnimationState();
    public AnimationState runpreAnimationState = new AnimationState();
    public AnimationState postRunAnimationState = new AnimationState();

    public AncientGuardianServant(EntityType<? extends IAnimatedMiniBossServant> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 100;
        this.setNoAi(false);
        this.setPersistenceRequired();
    }

    @Override
    public int getSummonLimit(LivingEntity owner) {
        return MobsConfig.AncientGuardianServantLimit.get();
    }

    @Override
    public double damageMultiplier() {
        return MobsConfig.AncientGuardianServantDamageMultiplier.get();
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

    public void resetSmashCooldown() {
        if (this.smashCooldown <= 0) {
            this.smashCooldown = SMASH_COOLDOWN;
        }
    }

    public void resetSmashAnchorCooldown() {
        if (this.smashAnchorCooldown <= 0) {
            this.smashAnchorCooldown = SMASH_ANCHOR_COOLDOWN;
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
            this.updateWithAttack();
        }
        if (this.smashCooldown > 0) {
            --this.smashCooldown;
        }
        if (this.stunCooldown > 0) {
            --this.stunCooldown;
        }
        if (this.smashAnchorCooldown > 0) {
            --this.smashAnchorCooldown;
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.attackParticle();
        if (this.horizontalCollision && this.isInWall()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.1D, 0.0D, 0.1D));
        }
        if (!this.level().isClientSide && this.getTarget() != null) {
            this.updateFacing();
        }
        if (!this.level().isClientSide) {
            AttributeInstance armor = this.getAttribute(Attributes.ARMOR);
            Crackiness crackiness = this.getCrackiness();
            if (armor != null
                    && (crackiness == Crackiness.MEDIUM || crackiness == Crackiness.HIGH)
                    && armor.getBaseValue() == AttributesConfig.AncientGuardianServantArmor.get()) {
                armor.setBaseValue(20.0D);
            }
        }
        if (this.level().isClientSide && !this.walkAnimation.isMoving() && this.getAttackState() == 0) {
            this.idleAnimationState.startIfStopped(this.tickCount);
        }
    }

    private void updateFacing() {
        LivingEntity target = this.getTarget();
        if (target == null) {
            return;
        }
        double targetX = target.getX() - this.getX();
        double targetY = target.getEyeY() - this.getEyeY();
        double targetZ = target.getZ() - this.getZ();
        double horizontalDistance = Math.sqrt(targetX * targetX + targetZ * targetZ);
        float yaw = (float) (Math.atan2(targetZ, targetX) * 57.29577951308232) - 90.0F;
        float pitch = (float) (-(Math.atan2(targetY, horizontalDistance) * 57.29577951308232));
        this.yHeadRot = this.yBodyRot = yaw;
        this.yBodyRot = pitch;
    }

    private void attackParticle() {
        if (!this.level().isClientSide) {
            return;
        }
        if (this.getAttackState() == 17 && this.attackTicks == 2) {
            this.level().addParticle(
                    new Circle.RingData(0.0F, (float) Math.PI / 2.0F, 30, 1.0F, 1.0F, 1.0F, 1.0F,
                            85.0F, false, Circle.EnumRingBehavior.GROW),
                    this.getX(), this.getY() + 0.1D, this.getZ(), 0.0D, 0.0D, 0.0D);
        }
        if (this.getAttackState() == 6 && this.attackTicks == 68) {
            float radius = 2.0F;
            float angle = (float) Math.PI / 180.0F * this.yBodyRot;
            double extraX = radius * Mth.sin((float) Math.PI + angle) - 0.5D;
            double extraZ = radius * Mth.cos(angle) + 0.5D * Mth.sin(angle);
            this.level().addParticle(
                    new Circle.RingData(0.0F, (float) Math.PI / 2.0F, 30, 1.0F, 1.0F, 1.0F, 1.0F,
                            50.0F, false, Circle.EnumRingBehavior.GROW),
                    this.getX() + extraX, this.getY() + 0.1D, this.getZ() + extraZ, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(6, new Summoned.WanderGoal<>(this, this.getFollowSpeed()));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(2, new IMoveGoal(this, false, 1.0D));

        this.goalSelector.addGoal(1, new IAttackGoalMin(this, 0, 15, 16, 20, 10, 6.5F, 5.0F) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && AncientGuardianServant.this.getRandom().nextFloat() * 40.0F < 16.0F
                        && AncientGuardianServant.this.stunCooldown <= 0
                        && AncientGuardianServant.this.getTarget() != null
                        && AncientGuardianServant.this.getAttackState() != 3;
            }
        });

        this.goalSelector.addGoal(1, new IStateGoal(this, 16, 16, 17, 100, 100));

        this.goalSelector.addGoal(0, new IStateGoal(this, 17, 17, 0, 20, 0) {
            @Override
            public void stop() {
                super.stop();
                AncientGuardianServant.this.stunCooldown = STUN_COOLDOWN;
            }
        });

        this.goalSelector.addGoal(1, new IAttackGoal(this, 0, 4, 0, 48, 15, 5.0F) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && AncientGuardianServant.this.getRandom().nextFloat() * 40.0F < 16.0F
                        && AncientGuardianServant.this.smashAnchorCooldown <= 0
                        && AncientGuardianServant.this.getTarget() != null
                        && AncientGuardianServant.this.getAttackState() != 3
                        && AncientGuardianServant.this.smashCooldown > 0;
            }

            @Override
            public void stop() {
                super.stop();
                AncientGuardianServant.this.resetSmashAnchorCooldown();
            }
        });

        this.goalSelector.addGoal(1, new IAttackGoal(this, 0, 3, 0, 78, 15, 6.0F) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && AncientGuardianServant.this.getRandom().nextFloat() * 100.0F < 16.0F
                        && AncientGuardianServant.this.smashCooldown <= 0
                        && AncientGuardianServant.this.getTarget() != null
                        && AncientGuardianServant.this.getAttackState() != 4;
            }

            @Override
            public void stop() {
                super.stop();
                AncientGuardianServant.this.resetSmashCooldown();
            }
        });

        this.goalSelector.addGoal(1, new IAttackGoal(this, 0, 6, 0, 90, 64, 5.0F));

        this.goalSelector.addGoal(1, new IStateGoal(this, 8, 8, 0, 75, 0) {
            @Override
            public void tick() {
                this.entity.setDeltaMovement(0.0D, this.entity.getDeltaMovement().y, 0.0D);
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

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.AncientGuardianServantHealth.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, AttributesConfig.AncientGuardianServantKnockbackResistance.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.AncientGuardianServantFollowRange.get())
                .add(Attributes.ARMOR, AttributesConfig.AncientGuardianServantArmor.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.AncientGuardianServantMovementSpeed.get())
                .add(Attributes.ATTACK_KNOCKBACK, AttributesConfig.AncientGuardianServantAttackKnockback.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.AncientGuardianServantDamage.get());
    }

    public void setConfigurableAttributes() {
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.MAX_HEALTH), AttributesConfig.AncientGuardianServantHealth.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ARMOR), AttributesConfig.AncientGuardianServantArmor.get());
        MobUtil.setBaseAttributes(this.getAttribute(Attributes.ATTACK_DAMAGE), AttributesConfig.AncientGuardianServantDamage.get());
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.ANCIENT_GUARDIAN_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.OVERGROWN_COLOSSUS_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState block) {
        this.playSound(ModSounds.STEP_SOUND.get(), 1.0F, 1.0F);
    }

    @Override
    public void die(DamageSource source) {
        if (!this.level().isClientSide) {
            this.returnAnchorHandle();
        }
        super.die(source);
        this.guardianDeathTime = 0;
        this.setAttackState(5);
    }

    private void returnAnchorHandle() {
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
        flyingItem.setItem(new ItemStack(ModItems.ANCHOR_HANDLE.get()));
        flyingItem.setParticle(ParticleTypes.CRIT);
        flyingItem.setSecondsCool(ItemConfig.ReviveSecondsCool.get());
        this.level().addFreshEntity(flyingItem);
    }

    @Override
    protected void tickDeath() {
        ++this.guardianDeathTime;
        if (this.level() instanceof ServerLevel && this.guardianDeathTime > 1 && !this.isSilent()) {
            this.setNoAi(true);
        }
        if (this.guardianDeathTime == 30) {
            LmScreenShake.shake(this.level(), this.position(), 40.0F, 0.1F, 0, 20);
        }
        if (this.guardianDeathTime == 60 && this.level() instanceof ServerLevel) {
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
        if (source.is(DamageTypeTags.IS_FALL)) {
            return false;
        }
        if (MobsConfig.AncientGuardianServantProjectileImmunity.get()
                && source.getDirectEntity() instanceof AbstractArrow) {
            return false;
        }
        if (source.getDirectEntity() instanceof ThrownPotion
                || source.getDirectEntity() instanceof AreaEffectCloud) {
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
    public void setDeltaMovement(Vec3 movement) {
        if (this.getAttackState() != 8) {
            super.setDeltaMovement(movement);
        }
    }

    @Override
    public boolean isPushable() {
        return this.getAttackState() != 8;
    }

    @Override
    public boolean canBePushedByEntity(Entity entity) {
        return false;
    }

    @Override
    public boolean canBeSeenAsEnemy() {
        return !this.isSleep() && super.canBeSeenAsEnemy();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("is_Sleep", this.isSleep());
        compound.putInt("StunCooldown", this.stunCooldown);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setSleep(compound.getBoolean("is_Sleep"));
        this.stunCooldown = compound.getInt("StunCooldown");
    }

    public AnimationState getAnimationState(String input) {
        return switch (input) {
            case "sleep" -> this.sleepAnimationState;
            case "awake" -> this.awakeAnimationState;
            case "idle" -> this.idleAnimationState;
            case "attackarmright" -> this.attackarm1AnimationState;
            case "attackarmleft" -> this.attackarm2AnimationState;
            case "attackcombo" -> this.attackComboAnimationState;
            case "death" -> this.deathAnimationState;
            case "grab" -> this.grabAnimationState;
            case "run" -> this.runAnimationState;
            case "runpre" -> this.runpreAnimationState;
            case "postrun" -> this.postRunAnimationState;
            default -> new AnimationState();
        };
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
                    this.deathAnimationState.startIfStopped(this.tickCount);
                }
                case 6 -> {
                    this.stopAllAnimationStates();
                    this.attackComboAnimationState.startIfStopped(this.tickCount);
                }
                case 8 -> {
                    this.stopAllAnimationStates();
                    this.grabAnimationState.startIfStopped(this.tickCount);
                }
                case 15 -> {
                    this.stopAllAnimationStates();
                    this.runpreAnimationState.startIfStopped(this.tickCount);
                }
                case 16 -> {
                    this.stopAllAnimationStates();
                    this.runAnimationState.startIfStopped(this.tickCount);
                }
                case 17 -> {
                    this.stopAllAnimationStates();
                    this.postRunAnimationState.startIfStopped(this.tickCount);
                }
            }
        }
        super.onSyncedDataUpdated(key);
    }

    public void stopAllAnimationStates() {
        this.idleAnimationState.stop();
        this.sleepAnimationState.stop();
        this.awakeAnimationState.stop();
        this.attackarm1AnimationState.stop();
        this.attackarm2AnimationState.stop();
        this.attackComboAnimationState.stop();
        this.deathAnimationState.stop();
        this.grabAnimationState.stop();
        this.runAnimationState.stop();
        this.runpreAnimationState.stop();
        this.postRunAnimationState.stop();
    }

    public Crackiness getCrackiness() {
        return Crackiness.byFraction(this.getHealth() / this.getMaxHealth());
    }

    private void AreaAttack(float range, float height, float arc, float damage, int shieldBreakTicks) {
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
            float entityHitDistance = (float) Math.sqrt(
                    (entityHit.getZ() - this.getZ()) * (entityHit.getZ() - this.getZ())
                            + (entityHit.getX() - this.getX()) * (entityHit.getX() - this.getX()));
            if (entityHitDistance <= range
                    && (entityRelativeAngle <= arc / 2 && entityRelativeAngle >= -arc / 2)
                    || (entityRelativeAngle >= 360 - arc / 2 || entityRelativeAngle <= -360 + arc / 2)) {
                if (this.isAlliedTo(entityHit) || ServantAllyUtil.areAllied(this, entityHit)
                        || entityHit instanceof AncientGuardianServant || entityHit == this) {
                    continue;
                }

                boolean hurt = entityHit.hurt(this.damageSources().mobAttack(this), damage);

                if (this.getAttackState() == 4) {
                    if (hurt) {
                        this.launch(entityHit, true);
                    }
                    if (entityHit instanceof Player && !entityHit.isBlocking()) {
                        entityHit.addEffect(new MobEffectInstance(ModEffects.STUN.get(), 40, 0));
                    } else if (!(entityHit instanceof Player)) {
                        entityHit.addEffect(new MobEffectInstance(ModEffects.STUN.get(), 80, 0));
                    }
                }
                if (this.getAttackState() == 17 && hurt) {
                    this.launch(entityHit, true);
                }
                if (this.getAttackState() == 3 && this.attackTicks == 20) {
                    LivingEntity target = this.getTarget();
                    if (target != null && !target.isBlocking() && hurt
                            && (target instanceof Player || !this.isVehicle())) {
                        this.setAttackState(8);
                        this.stopAllAnimationStates();
                    }
                }
                if (this.getAttackState() == 6 && this.attackTicks == 68 && hurt) {
                    this.launch(entityHit, true);
                }
                if (entityHit instanceof Player && entityHit.isBlocking() && shieldBreakTicks > 0) {
                    disableShield(entityHit, shieldBreakTicks);
                }
            }
        }
    }

    private void updateWithAttack() {
        if (this.getAttackState() == 15 && this.attackTicks == 17) {
            LivingEntity target = this.getTarget();
            if (target != null) {
                this.getLookControl().setLookAt(target, 60.0F, 30.0F);
                Vec3 direction = new Vec3(
                        target.getX() - this.getX(),
                        target.getY() - this.getY(),
                        target.getZ() - this.getZ()).normalize();
                this.setDeltaMovement(this.getDeltaMovement().add(
                        direction.x * 0.9D, 1.0D, direction.z * 0.9D));
            }
        }

        if (this.getAttackState() == 16 && (this.onGround() || !this.getFeetBlockState().getFluidState().isEmpty())) {
            this.setAttackState(17);
        }

        if (this.getAttackState() == 17 && this.attackTicks == 2) {
            int standingOnY = Mth.floor(this.getY());
            this.spawnShockwave(this.getX(), this.getZ(), standingOnY, this.getY(), 0.0F, 0);
            this.AreaAttack(6.0F, 6.0F, 360.0F, 16.0F, 100);
            this.playSound(ModSounds.ANCHOR_SLAM.get(), 1.0F, 1.0F);
            LmScreenShake.shake(this.level(), this.position(), 20.0F, 0.1F, 0, 20);
        }

        if (this.getAttackState() == 5 && this.attackTicks == 32) {
            LmScreenShake.shake(this.level(), this.position(), 20.0F, 0.1F, 0, 20);
        }

        if (this.getAttackState() == 3) {
            if (this.attackTicks == 15) {
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 1.0F, 1.0F);
            }
            if (this.attackTicks == 20) {
                this.AreaAttack(6.5F, 4.0F, 180.0F, 5.0F, 0);
                this.playSound(ModSounds.POSESSED_PALADIN_ATTACK.get(), 1.0F, 1.0F);
            }
        }

        if (this.getAttackState() == 8) {
            this.grabTarget();
            LivingEntity target = this.getTarget();
            if (target != null && this.distanceToSqr(target.getX(), target.getY(), target.getZ()) < 36.0D) {
                if (this.attackTicks == 16) {
                    this.grabShockwave(14.0F);
                }
                if (this.attackTicks == 42) {
                    this.grabShockwave(15.0F);
                }
                if (this.attackTicks == 67) {
                    this.grabShockwave(16.0F);
                }
            }
        }

        if (this.getAttackState() == 4) {
            if (this.attackTicks == 15) {
                this.playSound(ModSounds.ANCHOR_SWING1.get(), 1.0F, 1.0F);
            }
            if (this.attackTicks == 20) {
                this.AreaAttack(6.5F, 4.0F, 100.0F, 16.0F, 40);
                this.playSound(ModSounds.ANCHOR_HIT.get(), 1.0F, 1.0F);
            }
        }

        if (this.getAttackState() == 6) {
            if (this.attackTicks == 13) {
                this.playSound(ModSounds.ANCHOR_SWING1.get(), 1.0F, 1.0F);
            }
            if (this.attackTicks == 18) {
                this.AreaAttack(6.5F, 4.0F, 75.0F, 14.0F, 40);
            }
            if (this.attackTicks == 36) {
                LivingEntity target = this.getTarget();
                if (this.onGround() && target != null && this.distanceToSqr(target.getX(), target.getY(), target.getZ()) > 16.0D) {
                    Vec3 movement = this.getDeltaMovement();
                    float facing = this.getYRot() * ((float) Math.PI / 180.0F);
                    Vec3 leap = new Vec3(-Mth.sin(facing), movement.y, Mth.cos(facing))
                            .scale(1.6D).add(movement.scale(1.3D));
                    this.setDeltaMovement(leap.x, movement.y, leap.z);
                }
                this.playSound(ModSounds.ANCHOR_SWING2.get(), 1.0F, 1.0F);
            }
            if (this.attackTicks == 41) {
                this.AreaAttack(6.5F, 4.0F, 75.0F, 15.0F, 40);
            }
            if (this.attackTicks == 65) {
                this.playSound(ModSounds.ANCHOR_SWING2.get(), 1.0F, 1.0F);
            }
            if (this.attackTicks == 68) {
                this.AreaAttack(7.0F, 4.0F, 160.0F, 17.0F, 80);
                this.playSound(ModSounds.ANCHOR_SLAM.get(), 1.0F, 1.0F);
                LmScreenShake.shake(this.level(), this.position(), 20.0F, 0.1F, 0, 20);
            }
        }
    }

    private void grabShockwave(float damage) {
        LivingEntity target = this.getTarget();
        if (target == null) {
            return;
        }
        int standingOnY = Mth.floor(target.getY());
        this.spawnShockwave(target.getX(), target.getZ(), standingOnY, this.getY(), 0.0F, 0);
        this.AreaAttack(6.0F, 6.0F, 360.0F, damage, 100);
        this.playSound(SoundEvents.DRAGON_FIREBALL_EXPLODE, 2.0F, 1.0F);
    }

    private void grabTarget() {
        LivingEntity target = this.getTarget();
        if (target == null) {
            return;
        }
        if (this.distanceToSqr(target.getX(), target.getY(), target.getZ()) >= 64.0D) {
            return;
        }
        float angle = (float) Math.PI / 180.0F * this.yBodyRot;
        if (target instanceof Player) {
            for (int[] range : GRAB_PLAYER_RANGES) {
                if (this.attackTicks < range[0] || this.attackTicks > range[1]) {
                    continue;
                }
                float radius = range[2] == 3 ? 1.0F : 2.0F;
                Vec3 targetPosition = this.grabPosition(radius, range[2], angle);
                Vec3 currentPosition = target.position();
                Vec3 velocity = targetPosition.subtract(currentPosition).scale(0.2D);
                if (target instanceof ServerPlayer serverPlayer) {
                    serverPlayer.connection.teleport(
                            currentPosition.x + velocity.x,
                            currentPosition.y + velocity.y,
                            currentPosition.z + velocity.z,
                            serverPlayer.getYRot(),
                            serverPlayer.getXRot());
                }
                break;
            }
            return;
        }
        for (int[] range : GRAB_MOB_RANGES) {
            if (this.attackTicks < range[0] || this.attackTicks > range[1]) {
                continue;
            }
            Vec3 targetPosition = this.grabPosition(range[2], range[3], angle);
            target.setDeltaMovement(targetPosition.subtract(target.position()).scale(0.2D));
            break;
        }
    }

    private Vec3 grabPosition(float radius, int heightOffset, float angle) {
        double extraX = radius * Mth.sin((float) Math.PI + angle) + 0.5D;
        double extraZ = radius * Mth.cos(angle) - 0.5D * Mth.sin(angle);
        return new Vec3(this.getX() + extraX, this.getY() + heightOffset, this.getZ() + extraZ);
    }

    private void spawnShockwave(double x, double z, double minY, double maxY, float rotation, int delay) {
        BlockPos blockpos = new BlockPos((int) x, (int) maxY, (int) z);
        boolean found = false;
        double offset = 0.0D;
        do {
            BlockPos below = blockpos.below();
            BlockState blockstate = this.level().getBlockState(below);
            if (blockstate.isFaceSturdy(this.level(), below, Direction.UP)) {
                if (!this.level().isEmptyBlock(blockpos)) {
                    BlockState inside = this.level().getBlockState(blockpos);
                    VoxelShape shape = inside.getCollisionShape(this.level(), blockpos);
                    if (!shape.isEmpty()) {
                        offset = shape.max(Direction.Axis.Y);
                    }
                }
                found = true;
                break;
            }
            blockpos = blockpos.below();
        } while (blockpos.getY() >= Mth.floor(minY) - 1);

        if (found && !this.level().isClientSide) {
            this.level().addFreshEntity(new IceShockwaveSpawner(
                    this.level(), x, blockpos.getY() + offset, z, rotation, delay, this));
        }
    }

    public enum Crackiness {
        NONE(1.0F),
        LOW(0.75F),
        MEDIUM(0.5F),
        HIGH(0.25F);

        private static final List<Crackiness> BY_DAMAGE = Stream.of(values())
                .sorted(Comparator.comparingDouble(c -> c.fraction)).toList();
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
