package com.qiuyue.goetyominous.common.entities.ally.lm;

import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.IMoveGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.knight.KnightAttackGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.knight.KnightStateGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.knight.RSAlertedStateGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.knight.RSAttackGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.knight.RSStabGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.knight.RSStateGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.knight.RSSynergyGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.projectile.SoulJavelin;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.qiuyue.goetyominous.config.MobsConfig;
import com.qiuyue.goetyominous.utils.ServantAllyUtil;
import net.minecraft.core.particles.ParticleOptions;
import net.miauczel.legendary_monsters.Particle.ModParticles;
import net.miauczel.legendary_monsters.Particle.custom.SoulSweepParticle;
import net.miauczel.legendary_monsters.Particle.custom.SoulSweepRedParticle;
import net.miauczel.legendary_monsters.effect.ModEffects;
import net.miauczel.legendary_monsters.entity.client.ControlledAnim;
import net.miauczel.legendary_monsters.sound.ModSounds;
import net.miauczel.legendary_monsters.util.EntityUtil;
import net.miauczel.legendary_monsters.util.MathUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class ResurrectedKnightServant extends IAnimatedMiniBossServant {

    private static final EntityDataAccessor<Integer> TEXTURE_VARIANT =
            SynchedEntityData.defineId(ResurrectedKnightServant.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> ENHANCED =
            SynchedEntityData.defineId(ResurrectedKnightServant.class, EntityDataSerializers.BOOLEAN);

    public final int FORWARD_STEP_COOLDOWN = 40;
    public int forwardStepCooldown = 40;
    public final int DOUBLE_STAB_COOLDOWN = 100;
    public int doubleStabCooldown = 100;
    public final int SHIELD_COMBO_COOLDOWN = 100;
    public int shield_combo_cooldown = 100;
    public final int SYNERGY_COOLDOWN = 300;
    public int synergyCooldown = 300;

    private final Random random1 = new Random();

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState alertedAnimationState = new AnimationState();
    public final AnimationState stabAnimationState = new AnimationState();
    public final AnimationState rightSideStepAnimationState = new AnimationState();
    public final AnimationState leftSideStepAnimationState = new AnimationState();
    public final AnimationState forwardStepAnimationState = new AnimationState();
    public final AnimationState doubleStabAnimationState = new AnimationState();
    public final AnimationState deathAnimationState = new AnimationState();
    public final AnimationState sleepAnimationState = new AnimationState();
    public final AnimationState awakenAnimationState = new AnimationState();
    public final AnimationState javelinThrowAnimationState = new AnimationState();
    public final AnimationState stabEndAnimationState = new AnimationState();
    public final AnimationState shieldComboAnimationState = new AnimationState();
    public final AnimationState synergyAnimationState = new AnimationState();

    public ControlledAnim bodyFadeAway = new ControlledAnim(10);
    public BeheadedKnightServant syncedEntity = null;
    public boolean shouldAttack = true;
    public boolean duoFight = false;
    public boolean gambit = false;
    public int deathTicks;

    public ResurrectedKnightServant(EntityType<? extends ResurrectedKnightServant> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 15;
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.ResurrectedKnightServantHealth.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, AttributesConfig.ResurrectedKnightServantKnockbackResistance.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.ResurrectedKnightServantFollowRange.get())
                .add(Attributes.ARMOR, AttributesConfig.ResurrectedKnightServantArmor.get())
                .add(Attributes.ARMOR_TOUGHNESS, AttributesConfig.ResurrectedKnightServantArmorToughness.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.ResurrectedKnightServantMovementSpeed.get())
                .add(Attributes.ATTACK_KNOCKBACK, AttributesConfig.ResurrectedKnightServantAttackKnockback.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.ResurrectedKnightServantDamage.get());
    }

    @Override
    public double damageMultiplier() {
        return MobsConfig.ResurrectedKnightServantDamageMultiplier.get();
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
    public int attackDelayTicksValue() {
        return this.canSynergyAttack() ? 5 : 0;
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        return this.getBoundingBox().inflate(12.0D);
    }

    @Override
    protected boolean canRide(Entity pVehicle) {
        return true;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty,
                                        MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData,
                                        @Nullable CompoundTag pDataTag) {
        if (pReason == MobSpawnType.MOB_SUMMONED && this.getTrueOwner() instanceof Player player) {
            if (countServants(player) >= MobsConfig.ResurrectedKnightServantLimit.get()) {
                return null;
            }
        }
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    private int countServants(Player player) {
        int count = 0;
        if (player.level() instanceof ServerLevel serverLevel) {
            for (Entity entity : serverLevel.getAllEntities()) {
                if (entity instanceof ResurrectedKnightServant servant
                        && servant.getTrueOwner() == player) {
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
        this.goalSelector.addGoal(2, new IMoveGoal(this, false, 3.0D));

        this.goalSelector.addGoal(1, new RSAlertedStateGoal(this, 2, 2, 0, 20, 20) {
            @Override
            public boolean canUse() {
                LivingEntity target = ResurrectedKnightServant.this.getTarget();
                return super.canUse()
                        && ResurrectedKnightServant.this.getRandom().nextFloat() * 100.0F < 15.0F
                        && ResurrectedKnightServant.this.canAttack()
                        && target != null;
            }
        });
        this.goalSelector.addGoal(1, new RSStabGoal(this, 0, 3, 0, MathUtils.toTicks(0.88F), 8, 6.0F) {
            @Override
            public boolean canUse() {
                LivingEntity target = ResurrectedKnightServant.this.getTarget();
                return super.canUse()
                        && ResurrectedKnightServant.this.getRandom().nextFloat() * 100.0F < 25.0F
                        && ResurrectedKnightServant.this.canAttack()
                        && !ResurrectedKnightServant.this.isReadyForSynergyAttack()
                        && target != null;
            }
        });
        this.goalSelector.addGoal(0, new RSStateGoal(this, 12, 12, 0, 30, 0, false, 50.0));
        this.goalSelector.addGoal(0, new RSStateGoal(this, 7, 7, 0, MathUtils.toTicks(2.38F), 8, true, 50.0));
        this.goalSelector.addGoal(1, new RSAttackGoal(this, 0, 4, 0, 15, 15, 5.0F, false, 25.0) {
            @Override
            public boolean canUse() {
                LivingEntity target = ResurrectedKnightServant.this.getTarget();
                return super.canUse()
                        && ResurrectedKnightServant.this.getRandom().nextFloat() * 100.0F < 20.0F
                        && !ResurrectedKnightServant.this.isReadyForSynergyAttack()
                        && target != null;
            }
        });
        this.goalSelector.addGoal(1, new RSAttackGoal(this, 0, 5, 0, 15, 15, 5.0F, false, 25.0) {
            @Override
            public boolean canUse() {
                LivingEntity target = ResurrectedKnightServant.this.getTarget();
                return super.canUse()
                        && ResurrectedKnightServant.this.getRandom().nextFloat() * 100.0F < 25.0F
                        && !ResurrectedKnightServant.this.isReadyForSynergyAttack()
                        && target != null;
            }
        });
        this.goalSelector.addGoal(1, new RSAttackGoal(this, 0, 6, 0, 18, 18, 15.0F, false, 25.0) {
            @Override
            public boolean canUse() {
                LivingEntity target = ResurrectedKnightServant.this.getTarget();
                return super.canUse()
                        && ResurrectedKnightServant.this.getRandom().nextFloat() * 100.0F < 20.0F
                        && target != null
                        && ResurrectedKnightServant.this.distanceTo(target) > 6.0F
                        && ResurrectedKnightServant.this.forwardStepCooldown <= 0
                        && ResurrectedKnightServant.this.canAttack()
                        && ResurrectedKnightServant.this.getIsDuoFight()
                        && !ResurrectedKnightServant.this.isReadyForSynergyAttack();
            }

            @Override
            public void stop() {
                super.stop();
                ResurrectedKnightServant.this.forwardStepCooldown = 40;
            }
        });
        this.goalSelector.addGoal(1, new RSAttackGoal(this, 0, 13, 0, MathUtils.toTicks(5.04F), 60, 6.0F, true, 25.0) {
            @Override
            public boolean canUse() {
                LivingEntity target = ResurrectedKnightServant.this.getTarget();
                return super.canUse()
                        && ResurrectedKnightServant.this.getRandom().nextFloat() * 100.0F < 20.0F
                        && target != null
                        && ResurrectedKnightServant.this.shield_combo_cooldown <= 0
                        && ResurrectedKnightServant.this.canAttack()
                        && !ResurrectedKnightServant.this.getIsDuoFight()
                        && !ResurrectedKnightServant.this.isReadyForSynergyAttack();
            }

            @Override
            public void stop() {
                super.stop();
                ResurrectedKnightServant.this.shield_combo_cooldown = 100;
            }
        });
        this.goalSelector.addGoal(1, new RSAttackGoal(this, 0, 11, 0, MathUtils.toTicks(2.63F), 20, 15.0F, false, 50.0) {
            @Override
            public boolean canUse() {
                LivingEntity target = ResurrectedKnightServant.this.getTarget();
                return super.canUse()
                        && ResurrectedKnightServant.this.getRandom().nextFloat() * 100.0F < 25.0F
                        && ResurrectedKnightServant.this.canAttack()
                        && !ResurrectedKnightServant.this.isReadyForSynergyAttack()
                        && target != null
                        && ResurrectedKnightServant.this.distanceTo(target) >= 7.0F;
            }
        });
        this.goalSelector.addGoal(1, new RSSynergyGoal(this, 0, 14, 0, MathUtils.toTicks(5.58F), 20, 15.0F, true, 50.0) {
            @Override
            public void start() {
                super.start();
                ResurrectedKnightServant.this.synchronisedDuoKnight().setAttackState(10);
            }

            @Override
            public boolean canUse() {
                LivingEntity target = ResurrectedKnightServant.this.getTarget();
                return super.canUse()
                        && ResurrectedKnightServant.this.canAttack()
                        && ResurrectedKnightServant.this.canSynergyAttack()
                        && target != null;
            }

            @Override
            public void stop() {
                super.stop();
                ResurrectedKnightServant.this.synergyCooldown = 300;
            }
        });
        this.goalSelector.addGoal(1, new KnightStateGoal(this, 9, 9, 10, 0, 0) {
            @Override
            public void tick() {
                this.entity.setDeltaMovement(0.0D, this.entity.getDeltaMovement().y, 0.0D);
            }
        });
        this.goalSelector.addGoal(0, new KnightAttackGoal(this, 9, 10, 0, 45, 0, 10.0F));
    }

    @Override
    public void tick() {
        if (this.forwardStepCooldown > 0) {
            --this.forwardStepCooldown;
        }
        if (this.doubleStabCooldown > 0) {
            --this.doubleStabCooldown;
        }
        if (this.shield_combo_cooldown > 0) {
            --this.shield_combo_cooldown;
        }
        if (this.synergyCooldown > 0) {
            --this.synergyCooldown;
        }
        if (this.level().isClientSide) {
            this.idleAnimationState.animateWhen(this.getAttackState() == 0, this.tickCount);
        }
        this.UpdateWithAttack();
        this.detectDuoFight(10.0F);
        if (!this.isSleep() && this.level().isClientSide) {
            this.level().addParticle(this.soulParticle(), this.getRandomX(1.0D), this.getRandomY(),
                    this.getRandomZ(1.0D), 0.0D, 0.025D, 0.0D);
        }
        if (!this.level().isClientSide) {
            if (this.getAttackState() == 0 && this.isStandby()) {
                this.setSleep(true);
            }
        }
        super.tick();
        if (!this.level().isClientSide) {
            if (this.getAttackState() == 9 && !this.isStandby()) {
                this.setAttackState(10);
            } else if (this.getAttackState() == 10 && this.attackTicks >= 45) {
                this.setSleep(false);
            }
        }
    }

    private boolean isStandby() {
        return this.isStaying() && !this.isCommanded() && this.getTarget() == null;
    }

    public void detectDuoFight(float range) {
        List<BeheadedKnightServant> knights = this.level().getEntitiesOfClass(
                BeheadedKnightServant.class,
                this.getBoundingBox().inflate(range),
                e -> e != (Entity) this && ServantAllyUtil.areAllied(this, e));
        if (knights.isEmpty()) {
            this.duoFight = false;
        }
        for (BeheadedKnightServant knight : knights) {
            if (this.synchronisedDuoKnight() != null) {
                this.setTextureVariant(this.synchronisedDuoKnight().getTextureVariant());
            }
            this.duoFight = true;
            this.shouldAttack = knight.getAttackState() != 2;
            this.syncedEntity = knight;
        }
    }

    public BeheadedKnightServant synchronisedDuoKnight() {
        return this.syncedEntity;
    }

    @Override
    protected void updateControlFlags() {
        if (this.isPassenger()) {
            this.goalSelector.setControlFlag(Goal.Flag.MOVE, false);
            this.goalSelector.setControlFlag(Goal.Flag.JUMP, false);
            this.goalSelector.setControlFlag(Goal.Flag.LOOK, false);
            this.goalSelector.setControlFlag(Goal.Flag.TARGET, true);
        } else {
            super.updateControlFlags();
        }
    }

    public boolean getIsDuoFight() {
        return this.synchronisedDuoKnight() != null
                && this.synchronisedDuoKnight().isAlive()
                && this.distanceTo(this.synchronisedDuoKnight()) <= 10.0F;
    }

    public boolean canSynergyAttack() {
        return this.synchronisedDuoKnight() != null
                && this.getIsDuoFight()
                && this.synchronisedDuoKnight().getAttackState() == 0
                && this.synergyCooldown == 0
                && this.synchronisedDuoKnight().synergyCooldown == 0;
    }

    public boolean isReadyForSynergyAttack() {
        return this.synergyCooldown <= 0
                && this.synchronisedDuoKnight() != null
                && this.synchronisedDuoKnight().isAlive()
                && this.synchronisedDuoKnight().getAttackState() == 0;
    }

    public boolean canAttack() {
        return this.shouldAttack && !this.isPassenger()
                && this.synchronisedDuoKnight() != null
                && this.synchronisedDuoKnight().getAttackState() != 11
                || !this.getIsDuoFight();
    }

    @Override
    public boolean canBeCollidedWith() {
        return this.getAttackState() == 14;
    }

    @Override
    public boolean canCollideWith(Entity pEntity) {
        if (this.getAttackState() == 14) {
            return false;
        }
        return super.canCollideWith(pEntity);
    }

    @Override
    public boolean isPushable() {
        return this.getAttackState() != 14;
    }

    @Override
    public void push(Entity pEntity) {
        if (this.getAttackState() != 14) {
            super.push(pEntity);
        }
    }

    @Override
    public void push(double pX, double pY, double pZ) {
        if (this.getAttackState() != 14) {
            super.push(pX, pY, pZ);
        }
    }

    @Override
    public boolean canBePushedByEntity(Entity entity) {
        return true;
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (this.isSleep() && !pSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return false;
        }
        return super.hurt(pSource, pAmount);
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
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        if (this.getTrueOwner() == pPlayer && !this.isEnhanced() && itemstack.is(Items.RED_DYE)) {
            if (!this.level().isClientSide) {
                if (!pPlayer.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }
                this.setEnhanced(true);
                this.playSound(SoundEvents.SOUL_ESCAPE, 1.0F, 0.75F);
                this.gameEvent(GameEvent.ENTITY_INTERACT, pPlayer);
                if (this.level() instanceof ServerLevel serverLevel) {
                    for (int i = 0; i < 12; ++i) {
                        double d0 = this.random.nextGaussian() * 0.02D;
                        double d1 = this.random.nextGaussian() * 0.02D;
                        double d2 = this.random.nextGaussian() * 0.02D;
                        serverLevel.sendParticles(ModParticles.GHOSTLY_SOUL_RED.get(),
                                this.getRandomX(1.0D), this.getY() + this.getBbHeight() + 0.3F, this.getRandomZ(1.0D),
                                0, d0, d1, d2, 0.5F);
                    }
                }
            }
            pPlayer.swing(pHand);
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return super.mobInteract(pPlayer, pHand);
    }

    @Override
    public boolean addEffect(MobEffectInstance pEffectInstance, @Nullable Entity pEntity) {
        if (this.isSleep()) {
            return false;
        }
        return super.addEffect(pEffectInstance, pEntity);
    }

    @Override
    public boolean isAlliedTo(Entity pEntity) {
        if (super.isAlliedTo(pEntity)) {
            return true;
        }
        if (pEntity == null) {
            return false;
        }
        LivingEntity owner = this.getOwner();
        if (owner != null) {
            if (pEntity == owner) {
                return true;
            }
            if (pEntity instanceof OwnableEntity other && other.getOwner() == owner) {
                return true;
            }
            return owner.isAlliedTo(pEntity);
        }
        return false;
    }

    public void setSleep(boolean sleep) {
        this.setAttackState(sleep ? 9 : 0);
    }

    public boolean isSleep() {
        return this.getAttackState() == 9 || this.getAttackState() == 10;
    }

    @Override
    public boolean canBeSeenAsEnemy() {
        return !this.isSleep() && super.canBeSeenAsEnemy();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(TEXTURE_VARIANT, 1);
        this.entityData.define(ENHANCED, false);
    }

    public int getTextureVariant() {
        return this.entityData.get(TEXTURE_VARIANT);
    }

    public void setTextureVariant(int textureVariant) {
        this.entityData.set(TEXTURE_VARIANT, textureVariant);
    }

    public boolean isEnhanced() {
        return this.entityData.get(ENHANCED);
    }

    public void setEnhanced(boolean enhanced) {
        this.entityData.set(ENHANCED, enhanced);
    }

    public ParticleOptions soulParticle() {
        return this.isEnhanced() ? ModParticles.GHOSTLY_SOUL_RED.get() : ModParticles.GHOSTLY_SOUL.get();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        pCompound.putBoolean("is_Sleep", this.isSleep());
        pCompound.putInt("texture_variant", this.getTextureVariant());
        pCompound.putBoolean("enhanced", this.isEnhanced());
        super.addAdditionalSaveData(pCompound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        this.setSleep(compound.getBoolean("is_Sleep"));
        this.setTextureVariant(compound.getInt("texture_variant"));
        this.setEnhanced(compound.getBoolean("enhanced"));
        super.readAdditionalSaveData(compound);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
        if (ATTACK_STATE.equals(pKey) && this.level().isClientSide) {
            switch (this.getAttackState()) {
                case 1 -> {
                    this.stopAllAnimationStates();
                    this.idleAnimationState.startIfStopped(this.tickCount);
                }
                case 2 -> {
                    this.stopAllAnimationStates();
                    this.alertedAnimationState.startIfStopped(this.tickCount);
                }
                case 3 -> {
                    this.stopAllAnimationStates();
                    this.stabAnimationState.startIfStopped(this.tickCount);
                }
                case 4 -> {
                    this.stopAllAnimationStates();
                    this.leftSideStepAnimationState.startIfStopped(this.tickCount);
                }
                case 5 -> {
                    this.stopAllAnimationStates();
                    this.rightSideStepAnimationState.startIfStopped(this.tickCount);
                }
                case 6 -> {
                    this.stopAllAnimationStates();
                    this.forwardStepAnimationState.startIfStopped(this.tickCount);
                }
                case 7 -> {
                    this.stopAllAnimationStates();
                    this.doubleStabAnimationState.startIfStopped(this.tickCount);
                }
                case 8 -> {
                    this.stopAllAnimationStates();
                    this.deathAnimationState.startIfStopped(this.tickCount);
                }
                case 9 -> {
                    this.stopAllAnimationStates();
                    this.sleepAnimationState.startIfStopped(this.tickCount);
                }
                case 10 -> {
                    this.stopAllAnimationStates();
                    this.awakenAnimationState.startIfStopped(this.tickCount);
                }
                case 11 -> {
                    this.stopAllAnimationStates();
                    this.javelinThrowAnimationState.startIfStopped(this.tickCount);
                }
                case 12 -> {
                    this.stopAllAnimationStates();
                    this.stabEndAnimationState.startIfStopped(this.tickCount);
                }
                case 13 -> {
                    this.stopAllAnimationStates();
                    this.shieldComboAnimationState.startIfStopped(this.tickCount);
                }
                case 14 -> {
                    this.stopAllAnimationStates();
                    this.synergyAnimationState.startIfStopped(this.tickCount);
                }
                default -> this.stopAllAnimationStates();
            }
        }
        super.onSyncedDataUpdated(pKey);
    }

    public AnimationState getAnimationState(String input) {
        if (input == "idle") {
            return this.idleAnimationState;
        }
        if (input == "stab") {
            return this.stabAnimationState;
        }
        if (input == "left_sidestep") {
            return this.leftSideStepAnimationState;
        }
        if (input == "right_sidestep") {
            return this.rightSideStepAnimationState;
        }
        if (input == "forwardstep") {
            return this.forwardStepAnimationState;
        }
        if (input == "double_stab") {
            return this.doubleStabAnimationState;
        }
        if (input == "death") {
            return this.deathAnimationState;
        }
        if (input == "sleep") {
            return this.sleepAnimationState;
        }
        if (input == "awaken") {
            return this.awakenAnimationState;
        }
        if (input == "throw") {
            return this.javelinThrowAnimationState;
        }
        if (input == "stab_end") {
            return this.stabEndAnimationState;
        }
        if (input == "shield_combo") {
            return this.shieldComboAnimationState;
        }
        if (input == "synergy") {
            return this.synergyAnimationState;
        }
        return new AnimationState();
    }

    public void stopAllAnimationStates() {
        this.alertedAnimationState.stop();
        this.stabAnimationState.stop();
        this.idleAnimationState.stop();
        this.leftSideStepAnimationState.stop();
        this.rightSideStepAnimationState.stop();
        this.doubleStabAnimationState.stop();
        this.forwardStepAnimationState.stop();
        this.deathAnimationState.stop();
        this.sleepAnimationState.stop();
        this.awakenAnimationState.stop();
        this.javelinThrowAnimationState.stop();
        this.stabEndAnimationState.stop();
        this.shieldComboAnimationState.stop();
        this.synergyAnimationState.stop();
    }

    public void UpdateWithAttack() {
        float sweepSize = 2.0F;
        float sweepRot = 20.0F;
        float bigSweepHeight = 3.0F;
        float bigSweepAdditionalY = 1.0F;
        float f = Mth.cos(this.yBodyRot * ((float) Math.PI / 180F));
        float f1 = Mth.sin(this.yBodyRot * ((float) Math.PI / 180F));
        double theta = this.yBodyRot * (Math.PI / 180) + 1.5707963267948966;
        double vecX = Math.cos(theta);
        double vecZ = Math.sin(theta);
        float swordWidth = -1.0F;
        float slashRange = 5.0F;
        int sideStep = 3;

        if (this.getAttackState() == 3) {
            int stab = 18;
            if (this.attackTicks == 8 && this.targetIsNotNull()) {
                this.saveTargetPos(this.target().getX(), this.target().getY(), this.target().getZ());
            }
            if (this.attackTicks == stab - 3) {
                this.calculatedDashToPositon(0.25F, this.lastTargetPos());
                this.playSound(ModSounds.SPEAR_STAB.get(), 1.0F, 0.75F);
            }
            if (this.attackTicks == stab) {
                this.StraightLineAreaAttackGambit(swordWidth, 1.0F, 4.0F, 80, 15.0F, true, 2.0F, 0.0F);
            }
        }

        if (this.getAttackState() == 7) {
            int stab = 18;
            if (this.attackTicks == 8 && this.targetIsNotNull()) {
                this.saveTargetPos(this.target().getX(), this.target().getY(), this.target().getZ());
            }
            if (this.attackTicks == stab - 3) {
                this.calculatedDashToPositon(0.25F, this.lastTargetPos());
                this.playSound(ModSounds.SPEAR_STAB.get(), 1.0F, 0.75F);
            }
            if (this.attackTicks == stab) {
                this.StraightLineAreaAttack(swordWidth, 1.0F, 4.0F, 80, 15.0F, true, 1.5F, 0.0F);
            }
        }

        if (this.getAttackState() == 4 && this.attackTicks == sideStep) {
            this.advancedDash(this, 0.0F, -2.0F, 1.0F);
        }

        if (this.getAttackState() == 5 && this.attackTicks == sideStep) {
            this.advancedDash(this, 0.0F, 2.0F, 1.0F);
        }

        if (this.getAttackState() == 6 && this.attackTicks == 3) {
            this.calculatedDash(0.25F);
        }

        if (this.getAttackState() == 8 && this.attackTicks >= 55) {
            this.bodyFadeAway.increaseTimer();
        }

        if (this.getAttackState() == 10) {
            if (this.attackTicks == 2) {
                this.bodyFadeAway.setTimer(5);
            }
            if (this.attackTicks >= 20 && this.attackTicks <= 25 && this.bodyFadeAway.getTimer() > 0) {
                this.bodyFadeAway.decreaseTimer();
            }
        }

        if (this.getAttackState() == 11 && this.attackTicks == 21 && this.targetIsNotNull()) {
            this.shootSoulJavelin(this.target(), 1.0F);
        }

        if (this.getAttackState() == 13) {
            int shieldTick = 23;
            int javelinSlash = 26;
            int sideStepp = 40;
            int stab2 = 70;
            if (this.attackTicks == shieldTick - 3) {
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 1.0F, 1.0F);
                this.calculatedDash(0.25F);
            }
            if (this.attackTicks == shieldTick) {
                this.createSweep(0.0F, 0.0F, bigSweepHeight, bigSweepAdditionalY, false, sweepSize * 0.75F, sweepRot, 0, true);
                this.SideAreaAttack(3.0F, 3.0F, 180.0F, 0.0F, 0.0F, 13.0F, 100, false, false, 0.0F, 0.0F, SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, 0.8F);
            }
            if (this.attackTicks == javelinSlash - 3) {
                this.playSound(ModSounds.SPEAR_STAB.get(), 1.0F, 0.75F);
                this.calculatedDash(0.2F);
            }
            if (this.attackTicks == javelinSlash) {
                this.createSweep(0.0F, 0.0F, bigSweepHeight, bigSweepAdditionalY, false, sweepSize, sweepRot, 0, true);
                this.SideAreaAttack(3.25F, 3.0F, 180.0F, 0.0F, 0.0F, 15.0F, 100, false, false, 0.0F, 0.0F, SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, 0.75F);
            }
            if (this.attackTicks == sideStepp) {
                this.advancedDash(this, 0.0F, -2.0F, 1.0F);
            }
            if (this.attackTicks == stab2 - 6 && this.targetIsNotNull()) {
                this.saveTargetPos(this.target().getX(), this.target().getY(), this.target().getZ());
            }
            if (this.attackTicks == stab2 - 3) {
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 1.0F, 1.25F);
                this.calculatedDashToPositon(0.25F, this.lastTargetPos());
                this.StraightLineAreaAttack(swordWidth, 1.0F, slashRange, 80, 15.0F, true, 1.5F, 0.0F);
            }
        }

        if (this.getAttackState() == 14) {
            int shieldBash = 24;
            int cut1 = 67;
            int cut2 = 73;
            if (this.attackTicks == 10 && this.targetIsNotNull()) {
                this.saveTargetPos(this.target().getX(), this.target().getY(), this.target().getZ());
            }
            if (this.attackTicks == shieldBash - 3) {
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 1.0F, 1.25F);
                this.calculatedDashToPositon(0.4F, this.lastTargetPos());
            }
            if (this.attackTicks == shieldBash) {
                this.StraightLineAreaAttack(swordWidth, 1.0F, 2.75F, 80, 15.0F, true, 2.0F, 0.5F);
            }
            if (this.attackTicks == cut1 - 3 || this.attackTicks == cut2 - 3) {
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 1.0F, 1.0F);
                this.basicDash(2.0F, 2.0F, true);
            }
            if (this.attackTicks == cut1 || this.attackTicks == cut2) {
                if (this.level().isClientSide) {
                    float yaw = (float) Math.toRadians(-this.yBodyRot + 90.0F);
                    float pitch = (float) Math.toRadians(-this.getXRot() + 180.0F);
                    if (this.isEnhanced()) {
                        this.level().addParticle(new SoulSweepRedParticle.SweepData(this.getScale() * 2.0F, yaw, pitch),
                                this.getX(), this.getY() + bigSweepAdditionalY, this.getZ(), 0.0D, 0.0D, 0.0D);
                    } else {
                        this.level().addParticle(new SoulSweepParticle.SweepData(this.getScale() * 2.0F, yaw, pitch),
                                this.getX(), this.getY() + bigSweepAdditionalY, this.getZ(), 0.0D, 0.0D, 0.0D);
                    }
                }
                this.StraightLineAreaAttack(swordWidth, 1.0F, slashRange, 80, 15.0F, true, 1.0F, 0.0F);
            }
        }
    }

    public void createSweep(float pos, float posOffset, float yHeight, double additionalY, boolean reverse,
                            float scale, float rot, int additionalSideAngle, boolean soul) {
        float f = Mth.cos(this.yBodyRot * ((float) Math.PI / 180F));
        float f1 = Mth.sin(this.yBodyRot * ((float) Math.PI / 180F));
        double theta = this.yBodyRot * (Math.PI / 180) + 1.5707963267948966;
        double vecX = Math.cos(theta);
        double vecZ = Math.sin(theta);
        double x = this.getX() + (double) pos * vecX + f * posOffset;
        double z = this.getZ() + (double) pos * vecZ + f1 * posOffset;
        if (this.level().isClientSide) {
            double d1 = this.getY() + this.getBbHeight() / 2.0F + additionalY;
            float yaw = (float) Math.toRadians(-this.yBodyRot + (reverse ? rot + additionalSideAngle : 180 + additionalSideAngle));
            double lookX = -Math.cos(yaw);
            double lookZ = -Math.sin(yaw);
            float pitch = (float) (reverse ? -1 : 1) * (float) Math.atan2(yHeight, Math.sqrt(lookX * lookX + lookZ * lookZ));
            if (this.isEnhanced()) {
                this.level().addParticle(new SoulSweepRedParticle.SweepData(this.getScale() * scale, yaw, pitch),
                        x, d1, z, 0.0D, 0.0D, 0.0D);
            } else {
                this.level().addParticle(new SoulSweepParticle.SweepData(this.getScale() * scale, yaw, pitch),
                        x, d1, z, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    public void SideAreaAttack(float range, float height, float arc, float boxOffset, float forwardOffset,
                               float damage, int brokenShieldTicks, boolean canStun, boolean canlaunch,
                               float Vxz, float Vy, SoundEvent soundEvent, float pitch) {
        double theta = Math.toRadians(this.yBodyRot) + 1.5707963267948966;
        double forwardX = Math.cos(theta) * forwardOffset;
        double forwardZ = Math.sin(theta) * forwardOffset;
        for (LivingEntity entityHit : this.getEntityLivingBaseNearby(range, height, range, range)) {
            double dx = entityHit.getX() - (this.getX() + forwardX);
            double dz = entityHit.getZ() - (this.getZ() + forwardZ);
            float entityHitAngle = (float) ((Math.toDegrees(Math.atan2(dz, dx)) - 90.0) % 360.0);
            if (entityHitAngle < 0.0F) {
                entityHitAngle += 360.0F;
            }
            float entityAttackingAngle = (this.yBodyRot - boxOffset) % 360.0F;
            if (entityAttackingAngle < 0.0F) {
                entityAttackingAngle += 360.0F;
            }
            float entityHitDistance = (float) Math.sqrt(dx * dx + dz * dz);
            float entityRelativeAngle = entityHitAngle - entityAttackingAngle;
            if (!(entityHitDistance <= range)
                    || !(entityRelativeAngle <= arc / 2.0F && entityRelativeAngle >= -arc / 2.0F || entityRelativeAngle >= 360.0F - arc / 2.0F)
                    && !(entityRelativeAngle <= -360.0F + arc / 2.0F)
                    || this.isAlliedTo(entityHit) || entityHit instanceof BeheadedKnightServant || entityHit == this) {
                continue;
            }
            boolean flag = entityHit.hurt(this.damageSources().mobAttack(this), (float) (damage * this.damageMultiplier()));
            if (flag) {
                EntityUtil.cancelBuffs(entityHit);
                entityHit.invulnerableTime = 0;
                entityHit.hurtDuration = 0;
                if (canlaunch) {
                    this.launch(entityHit, Vxz, Vy);
                }
                this.playSound(soundEvent, 1.0F, pitch);
                if (canStun) {
                    entityHit.addEffect(new MobEffectInstance(ModEffects.STUN.get(), 40, 1));
                }
            }
            if (entityHit instanceof Player player && player.isBlocking() && brokenShieldTicks > 0) {
                disableShield(player, brokenShieldTicks);
            }
        }
    }

    private void StraightLineAreaAttack(float boxWidth, float yHeight, float range, int brokenShieldTicks,
                                        float damage, boolean launch, float Vxz, float Vy) {
        double rad = Math.toRadians(this.getYRot() + 90.0F);
        double xRange = range * Math.cos(rad);
        double zRange = range * Math.sin(rad);
        AABB attackRange = this.getBoundingBox().inflate(boxWidth, yHeight, boxWidth).expandTowards(xRange, yHeight, zRange);
        for (LivingEntity entityHit : this.level().getEntitiesOfClass(LivingEntity.class, attackRange)) {
            if (this.isAlliedTo(entityHit) || entityHit == this) {
                continue;
            }
            boolean flag = entityHit.hurt(this.damageSources().mobAttack(this), (float) (damage * this.damageMultiplier()));
            if (flag && launch) {
                entityHit.invulnerableTime = 0;
                this.launch(entityHit, Vxz, Vy);
            }
            if (entityHit instanceof Player player && player.isBlocking() && brokenShieldTicks > 0) {
                disableShield(player, brokenShieldTicks);
            }
        }
    }

    private void StraightLineAreaAttackGambit(float boxWidth, float yHeight, float range, int brokenShieldTicks,
                                              float damage, boolean launch, float Vxz, float Vy) {
        double rad = Math.toRadians(this.getYRot() + 90.0F);
        double xRange = range * Math.cos(rad);
        double zRange = range * Math.sin(rad);
        AABB attackRange = this.getBoundingBox().inflate(boxWidth, yHeight, boxWidth).expandTowards(xRange, yHeight, zRange);
        for (LivingEntity entityHit : this.level().getEntitiesOfClass(LivingEntity.class, attackRange)) {
            if (this.isAlliedTo(entityHit) || entityHit == this) {
                continue;
            }
            boolean flag = entityHit.hurt(this.damageSources().mobAttack(this), (float) (damage * this.damageMultiplier()));
            if (flag && launch) {
                this.launch(entityHit, Vxz, Vy);
            }
            if (flag) {
                EntityUtil.cancelBuffs(entityHit);
                entityHit.invulnerableTime = 0;
                this.gambit = true;
            }
            if (entityHit instanceof Player player && player.isBlocking() && brokenShieldTicks > 0) {
                disableShield(player, brokenShieldTicks);
            }
        }
    }

    public void shootSoulJavelin(LivingEntity pTarget, float pDistanceFactor) {
        SoulJavelin soulJavelin = new SoulJavelin(this.level(), this, this.isEnhanced());
        double d0 = pTarget.getX() - this.getX();
        double d1 = pTarget.getY(0.3333333333333333D) - soulJavelin.getY();
        double d2 = pTarget.getZ() - this.getZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        soulJavelin.shoot(d0, d1 + d3 * 0.2D, d2, 1.6F, 14 - this.level().getDifficulty().getId() * 4);
        this.playSound(SoundEvents.DROWNED_SHOOT, 1.0F, 0.75F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level().addFreshEntity(soulJavelin);
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return passenger instanceof IServant servant
                && (this.getTrueOwner() == null || this.getTrueOwner() == servant.getTrueOwner());
    }

    @Override
    public void die(DamageSource pDamageSource) {
        super.die(pDamageSource);
        this.deathTicks = 0;
        this.deathTime = 0;
        this.setAttackState(8);
    }

    @Override
    protected void tickDeath() {
        ++this.deathTicks;
        this.deathTime = 0;
        if (this.deathTicks == 100) {
            this.remove(Entity.RemovalReason.KILLED);
            this.gameEvent(GameEvent.ENTITY_DIE);
        }
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return ModSounds.LIVING_ARMOR_HURT.get();
    }
}
