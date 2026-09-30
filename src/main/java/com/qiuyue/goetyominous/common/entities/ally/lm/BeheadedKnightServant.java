package com.qiuyue.goetyominous.common.entities.ally.lm;

import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.knight.BHGrabAndThrowGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.knight.BHSynergyStateGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.knight.GhostUppercutGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.IMoveGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.knight.KnightAttackGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.goals.knight.KnightStateGoal;
import com.qiuyue.goetyominous.common.entities.ally.lm.projectile.SoulPillar;
import com.qiuyue.goetyominous.common.entities.ally.lm.projectile.ThrownKnight;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.qiuyue.goetyominous.config.MobsConfig;
import com.qiuyue.goetyominous.utils.ServantAllyUtil;
import net.miauczel.legendary_monsters.Particle.ModParticles;
import net.miauczel.legendary_monsters.Particle.custom.BeheadedKnightSweepParticle;
import net.miauczel.legendary_monsters.Particle.custom.Circle;
import net.miauczel.legendary_monsters.Particle.custom.SoulSweepParticle;
import net.miauczel.legendary_monsters.Particle.custom.SoulSweepRedParticle;
import net.miauczel.legendary_monsters.effect.ModEffects;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.Effect.CameraShakeEntity;
import net.miauczel.legendary_monsters.entity.client.ControlledAnim;
import net.miauczel.legendary_monsters.sound.ModSounds;
import net.miauczel.legendary_monsters.util.EntityUtil;
import net.miauczel.legendary_monsters.util.MathUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
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
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class BeheadedKnightServant extends IAnimatedMiniBossServant {

    private static final EntityDataAccessor<Integer> TEXTURE_VARIANT =
            SynchedEntityData.defineId(BeheadedKnightServant.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> ENHANCED =
            SynchedEntityData.defineId(BeheadedKnightServant.class, EntityDataSerializers.BOOLEAN);

    public final int UPPERCUT_COOLDOWN = 0;
    public int upperCutCooldown = 0;
    public final int GHOST_COMBO_COOLDOWN = 100;
    public int ghostComboCooldown = 100;
    public final int GHOST_UPPERCUT_COOLDOWN = 40;
    public int ghostUppercutCooldown = 40;
    public final int GRAB_AND_THROW_COOLDOWN = 100;
    public int grab_and_throw_cooldown = 100;
    public final int SYNERGY_COOLDOWN = 300;
    public int synergyCooldown = 300;
    public final int STAB_COOLDOWN = 40;
    public int stab_cooldown = 40;

    private final Random random1 = new Random();

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState upperCutAnimationState = new AnimationState();
    public final AnimationState ghostComboAnimationState = new AnimationState();
    public final AnimationState ghostUppercutAnimationState = new AnimationState();
    public final AnimationState stabDoubleAnimationState = new AnimationState();
    public final AnimationState stabAnimationState = new AnimationState();
    public final AnimationState deathAnimationState = new AnimationState();
    public final AnimationState sleepAnimationState = new AnimationState();
    public final AnimationState awakenAnimationState = new AnimationState();
    public final AnimationState synergyAnimationState = new AnimationState();
    public final AnimationState grabAndThrowAnimationState = new AnimationState();
    public final AnimationState grabAndThrowSuccessAnimationState = new AnimationState();
    public final AnimationState grabAndThrowFailAnimationState = new AnimationState();

    public boolean duoFight = false;
    public boolean shouldAttack = true;
    public ResurrectedKnightServant syncedEntity = null;
    public ControlledAnim armFade = new ControlledAnim(10);
    public float getR;
    public float getG;
    public float getB;
    public boolean grab = false;
    public int deathTicks;

    public BeheadedKnightServant(EntityType<? extends BeheadedKnightServant> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 15;
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.BeheadedKnightServantHealth.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, AttributesConfig.BeheadedKnightServantKnockbackResistance.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.BeheadedKnightServantFollowRange.get())
                .add(Attributes.ARMOR, AttributesConfig.BeheadedKnightServantArmor.get())
                .add(Attributes.ARMOR_TOUGHNESS, AttributesConfig.BeheadedKnightServantArmorToughness.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.BeheadedKnightServantMovementSpeed.get())
                .add(Attributes.ATTACK_KNOCKBACK, AttributesConfig.BeheadedKnightServantAttackKnockback.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.BeheadedKnightServantDamage.get());
    }

    @Override
    public double damageMultiplier() {
        return MobsConfig.BeheadedKnightServantDamageMultiplier.get();
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
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty,
                                        MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData,
                                        @Nullable CompoundTag pDataTag) {
        if (pReason == MobSpawnType.MOB_SUMMONED && this.getTrueOwner() instanceof Player player) {
            if (countServants(player) >= MobsConfig.BeheadedKnightServantLimit.get()) {
                return null;
            }
        }
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    private int countServants(Player player) {
        int count = 0;
        if (player.level() instanceof ServerLevel serverLevel) {
            for (Entity entity : serverLevel.getAllEntities()) {
                if (entity instanceof BeheadedKnightServant servant
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

        this.goalSelector.addGoal(1, new KnightAttackGoal(this, 0, 2, 0, MathUtils.toTicks(2.38F), 15, 5.5F) {
            @Override
            public void stop() {
                BeheadedKnightServant.this.upperCutCooldown = 0;
                super.stop();
            }

            @Override
            public boolean canUse() {
                return super.canUse()
                        && BeheadedKnightServant.this.getRandom().nextFloat() * 100.0F < 16.0F
                        && BeheadedKnightServant.this.getTarget() != null
                        && BeheadedKnightServant.this.upperCutCooldown <= 0
                        && BeheadedKnightServant.this.canAttack()
                        && !BeheadedKnightServant.this.isReadyForSynergyAttack();
            }
        });
        this.goalSelector.addGoal(1, new KnightAttackGoal(this, 0, 7, 0, MathUtils.toTicks(2.71F), 10, 6.0F) {
            @Override
            public void stop() {
                BeheadedKnightServant.this.stab_cooldown = 40;
                super.stop();
            }

            @Override
            public boolean canUse() {
                return super.canUse()
                        && BeheadedKnightServant.this.getRandom().nextFloat() * 100.0F < 16.0F
                        && BeheadedKnightServant.this.getTarget() != null
                        && BeheadedKnightServant.this.stab_cooldown <= 0
                        && !BeheadedKnightServant.this.isReadyForSynergyAttack();
            }
        });
        this.goalSelector.addGoal(1, new KnightAttackGoal(this, 0, 3, 0, MathUtils.toTicks(6.46F), 63, 6.0F) {
            @Override
            public void stop() {
                BeheadedKnightServant.this.ghostComboCooldown = 100;
                super.stop();
            }

            @Override
            public boolean canUse() {
                return super.canUse()
                        && BeheadedKnightServant.this.getRandom().nextFloat() * 100.0F < 16.0F
                        && BeheadedKnightServant.this.getTarget() != null
                        && BeheadedKnightServant.this.ghostComboCooldown <= 0
                        && !BeheadedKnightServant.this.isReadyForSynergyAttack();
            }
        });
        this.goalSelector.addGoal(1, new GhostUppercutGoal(this, 0, 4, 0, MathUtils.toTicks(5.58F), 70, 5.0F) {
            @Override
            public void stop() {
                BeheadedKnightServant.this.ghostUppercutCooldown = 40;
                super.stop();
            }

            @Override
            public boolean canUse() {
                return super.canUse()
                        && BeheadedKnightServant.this.getRandom().nextFloat() * 100.0F < 20.0F
                        && BeheadedKnightServant.this.getTarget() != null
                        && BeheadedKnightServant.this.ghostUppercutCooldown <= 0
                        && !BeheadedKnightServant.this.isReadyForSynergyAttack();
            }
        });
        this.goalSelector.addGoal(1, new BHGrabAndThrowGoal(this, 0, 11, MathUtils.toTicks(1.08F), MathUtils.toTicks(1.08F), 5.0F) {
            @Override
            public boolean canUse() {
                return super.canUse()
                        && BeheadedKnightServant.this.getRandom().nextFloat() * 100.0F < 20.0F
                        && BeheadedKnightServant.this.getTarget() != null
                        && BeheadedKnightServant.this.grab_and_throw_cooldown <= 0
                        && !BeheadedKnightServant.this.isReadyForSynergyAttack();
            }
        });
        this.goalSelector.addGoal(0, new KnightStateGoal(this, 12, 12, 0, MathUtils.toTicks(5.04F), MathUtils.toTicks(2.58F)) {
            @Override
            public void stop() {
                BeheadedKnightServant.this.grab_and_throw_cooldown = 100;
                super.stop();
            }
        });
        this.goalSelector.addGoal(0, new KnightStateGoal(this, 13, 13, 0, MathUtils.toTicks(0.92F), 0) {
            @Override
            public void stop() {
                BeheadedKnightServant.this.grab_and_throw_cooldown = 100;
                super.stop();
            }
        });
        this.goalSelector.addGoal(0, new BHSynergyStateGoal(this, 10, 10, 0, MathUtils.toTicks(9.92F), 100));
        this.goalSelector.addGoal(0, new KnightStateGoal(this, 5, 5, 0, 60, 0));
        this.goalSelector.addGoal(1, new KnightStateGoal(this, 8, 8, 9, 0, 0) {
            @Override
            public void tick() {
                this.entity.setDeltaMovement(0.0D, this.entity.getDeltaMovement().y, 0.0D);
            }
        });
        this.goalSelector.addGoal(0, new KnightAttackGoal(this, 8, 9, 0, 45, 0, 10.0F));
    }

    @Override
    public void tick() {
        if (!this.level().isClientSide && this.getAttackState() != 11 && this.getAttackState() != 12) {
            for (Entity passenger : List.copyOf(this.getPassengers())) {
                passenger.stopRiding();
            }
        }
        if (this.upperCutCooldown > 0) {
            --this.upperCutCooldown;
        }
        if (this.ghostComboCooldown > 0) {
            --this.ghostComboCooldown;
        }
        if (this.ghostUppercutCooldown > 0) {
            --this.ghostUppercutCooldown;
        }
        if (this.synergyCooldown > 0) {
            --this.synergyCooldown;
        }
        if (this.stab_cooldown > 0) {
            --this.stab_cooldown;
        }
        if (this.grab_and_throw_cooldown > 0) {
            --this.grab_and_throw_cooldown;
        }
        if (this.tickCount % 15 == 0) {
            this.attractParticles(this.soulParticle(), 0.5F, 3, 4.0F, 0.0F, 1.0F, 0.0F, 3.0F, 6.0F, 0.025F);
        }
        this.detectDuoFight(10.0F);
        if (this.level().isClientSide) {
            this.idleAnimationState.animateWhen(this.getAttackState() == 0, this.tickCount);
        }
        this.UpdateWithAttack();
        if (!this.level().isClientSide) {
            if (this.getAttackState() == 0 && this.isStandby()) {
                this.setSleep(true);
            }
        }
        super.tick();
        if (!this.level().isClientSide) {
            if (this.getAttackState() == 8 && !this.isStandby()) {
                this.setAttackState(9);
            } else if (this.getAttackState() == 9 && this.attackTicks >= 45) {
                this.setSleep(false);
            }
        }
    }

    private boolean isStandby() {
        return this.isStaying() && !this.isCommanded() && this.getTarget() == null;
    }

    public void detectDuoFight(float range) {
        List<ResurrectedKnightServant> knights = this.level().getEntitiesOfClass(
                ResurrectedKnightServant.class,
                this.getBoundingBox().inflate(range),
                e -> e != (Entity) this && ServantAllyUtil.areAllied(this, e));
        for (ResurrectedKnightServant duoKnight : knights) {
            this.duoFight = true;
            this.shouldAttack = duoKnight.getAttackState() != 3;
            this.syncedEntity = duoKnight;
        }
    }

    public ResurrectedKnightServant synchronisedDuoKnight() {
        return this.syncedEntity;
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
        return this.getIsDuoFight() && this.synchronisedDuoKnight().synergyCooldown <= 0;
    }

    public boolean canAttack() {
        return this.shouldAttack;
    }

    public boolean hasGhostArm() {
        return this.getAttackState() == 3 || this.getAttackState() == 4 || this.getAttackState() == 10;
    }

    @Override
    public boolean canCollideWith(Entity pEntity) {
        return this.getAttackState() != 10 && super.canCollideWith(pEntity);
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return this.getAttackState() != 10;
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
        this.setAttackState(sleep ? 8 : 0);
    }

    public boolean isSleep() {
        return this.getAttackState() == 8 || this.getAttackState() == 9;
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

    public void setRGB(float r, float g, float b) {
        this.getR = r / 255.0F;
        this.getG = g / 255.0F;
        this.getB = b / 255.0F;
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
                    this.upperCutAnimationState.startIfStopped(this.tickCount);
                }
                case 3 -> {
                    this.stopAllAnimationStates();
                    this.ghostComboAnimationState.startIfStopped(this.tickCount);
                }
                case 4 -> {
                    this.stopAllAnimationStates();
                    this.ghostUppercutAnimationState.startIfStopped(this.tickCount);
                }
                case 5 -> {
                    this.stopAllAnimationStates();
                    this.deathAnimationState.startIfStopped(this.tickCount);
                }
                case 6 -> {
                    this.stopAllAnimationStates();
                    this.stabDoubleAnimationState.startIfStopped(this.tickCount);
                }
                case 7 -> {
                    this.stopAllAnimationStates();
                    this.stabAnimationState.startIfStopped(this.tickCount);
                }
                case 8 -> {
                    this.stopAllAnimationStates();
                    this.sleepAnimationState.startIfStopped(this.tickCount);
                }
                case 9 -> {
                    this.stopAllAnimationStates();
                    this.awakenAnimationState.startIfStopped(this.tickCount);
                }
                case 10 -> {
                    this.stopAllAnimationStates();
                    this.synergyAnimationState.startIfStopped(this.tickCount);
                }
                case 11 -> {
                    this.stopAllAnimationStates();
                    this.grabAndThrowAnimationState.startIfStopped(this.tickCount);
                }
                case 12 -> {
                    this.stopAllAnimationStates();
                    this.grabAndThrowSuccessAnimationState.startIfStopped(this.tickCount);
                }
                case 13 -> {
                    this.stopAllAnimationStates();
                    this.grabAndThrowFailAnimationState.startIfStopped(this.tickCount);
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
        if (input == "uppercut") {
            return this.upperCutAnimationState;
        }
        if (input == "ghost_combo") {
            return this.ghostComboAnimationState;
        }
        if (input == "ghost_uppercut") {
            return this.ghostUppercutAnimationState;
        }
        if (input == "death") {
            return this.deathAnimationState;
        }
        if (input == "stab_double") {
            return this.stabDoubleAnimationState;
        }
        if (input == "stab") {
            return this.stabAnimationState;
        }
        if (input == "sleep") {
            return this.sleepAnimationState;
        }
        if (input == "awaken") {
            return this.awakenAnimationState;
        }
        if (input == "synergy") {
            return this.synergyAnimationState;
        }
        if (input == "grab_and_throw") {
            return this.grabAndThrowAnimationState;
        }
        if (input == "grab_and_throw_success") {
            return this.grabAndThrowSuccessAnimationState;
        }
        if (input == "grab_and_throw_fail") {
            return this.grabAndThrowFailAnimationState;
        }
        return new AnimationState();
    }

    public void stopAllAnimationStates() {
        this.upperCutAnimationState.stop();
        this.idleAnimationState.stop();
        this.ghostComboAnimationState.stop();
        this.ghostUppercutAnimationState.stop();
        this.deathAnimationState.stop();
        this.stabDoubleAnimationState.stop();
        this.stabAnimationState.stop();
        this.sleepAnimationState.stop();
        this.awakenAnimationState.stop();
        this.synergyAnimationState.stop();
        this.grabAndThrowSuccessAnimationState.stop();
        this.grabAndThrowAnimationState.stop();
        this.grabAndThrowFailAnimationState.stop();
    }

    public void UpdateWithAttack() {
        int stab1;
        int ARC;
        int upperCut;
        if (this.isEnhanced()) {
            switch (this.getTextureVariant()) {
                case 2 -> this.setRGB(238.0F, 60.0F, 60.0F);
                case 3 -> this.setRGB(251.0F, 63.0F, 63.0F);
                default -> this.setRGB(214.0F, 54.0F, 54.0F);
            }
        } else {
            switch (this.getTextureVariant()) {
                case 2 -> this.setRGB(234.0F, 238.0F, 87.0F);
                case 3 -> this.setRGB(161.0F, 251.0F, 232.0F);
                default -> this.setRGB(203.0F, 212.0F, 214.0F);
            }
        }

        float sweepSize = 2.0F;
        float sweepRot = 20.0F;
        float bigSweepHeight = 3.0F;
        float bigSweepAdditionalY = 1.0F;
        float f = Mth.cos(this.yBodyRot * ((float) Math.PI / 180F));
        float f1 = Mth.sin(this.yBodyRot * ((float) Math.PI / 180F));
        double theta = this.yBodyRot * (Math.PI / 180);
        double vecX = Math.cos(theta + 1.5707963267948966);
        double vecZ = Math.sin(theta + 1.5707963267948966);
        float slashRange = 5.0F;
        float sweepShakeAmount = 0.075F;
        int duration = 10;
        float swordWidth = -1.0F;

        if (this.getAttackState() == 2) {
            upperCut = 18;
            if (this.attackTicks == upperCut - 3) {
                this.createSweep(0.0F, 0.0F, bigSweepHeight, bigSweepAdditionalY, false, sweepSize, sweepRot, -90, false);
                this.playSound(ModSounds.HEAVY_SWING.get(), 1.0F, 1.0F);
                this.calculatedDash(0.25F);
            }
            if (this.attackTicks == upperCut) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, sweepShakeAmount, 0, duration);
                this.SideAreaAttack(3.5F, 5.0F, 220.0F, -90.0F, 0.0F, 17.0F, 100, false, true, 0.5F, 0.75F, SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, 0.75F);
            }
        }

        if (this.getAttackState() == 4) {
            upperCut = 22;
            int slash1 = 52;
            int slash2 = 55;
            int disappearTick = 100;
            ARC = 120;
            if (this.attackTicks == 2) {
                this.armFade.setTimer(5);
            }
            if (this.attackTicks >= 3 && this.attackTicks <= 100 && this.armFade.getTimer() > 0) {
                this.armFade.decreaseTimer();
            }
            if (this.attackTicks == disappearTick - 2) {
                this.armFade.setTimer(0);
            }
            if (this.attackTicks >= disappearTick) {
                this.armFade.increaseTimer();
            }
            if (this.attackTicks == upperCut - 3) {
                this.createSweep(0.0F, 0.0F, bigSweepHeight, bigSweepAdditionalY, false, sweepSize, sweepRot, -70, true);
                this.playSound(ModSounds.HEAVY_SWING.get(), 1.0F, 1.25F);
                this.calculatedDash(0.25F);
            }
            if (this.attackTicks == upperCut) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, sweepShakeAmount, 0, duration);
                this.SideAreaAttack(3.5F, 5.0F, 220.0F, -90.0F, 0.0F, 15.0F, 100, false, true, 0.5F, 0.75F, SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, 0.75F);
            }
            if (this.attackTicks == slash1 - 3) {
                this.createSweep(0.0F, 0.0F, bigSweepHeight, bigSweepAdditionalY, true, sweepSize, sweepRot, 0, false);
                this.playSound(ModSounds.HEAVY_SWING.get(), 1.0F, 1.0F);
                this.basicDash(2.0F, 2.0F, true);
            }
            if (this.attackTicks == slash1) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, sweepShakeAmount, 0, duration);
                this.SideAreaAttack(3.5F, 5.0F, ARC, 0.0F, 0.0F, 17.0F, 100, false, true, 0.5F, 0.0F, SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, 0.75F);
            }
            if (this.attackTicks == slash2 - 3) {
                this.createSweep(0.0F, 0.0F, bigSweepHeight, bigSweepAdditionalY, true, sweepSize, sweepRot, 0, true);
                this.playSound(ModSounds.HEAVY_SWING.get(), 1.0F, 1.0F);
                this.basicDash(2.0F, 2.0F, true);
            }
            if (this.attackTicks == slash2) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, sweepShakeAmount, 0, duration);
                this.SideAreaAttack(3.5F, 5.0F, ARC, 0.0F, 0.0F, 15.0F, 100, false, true, 0.5F, 0.0F, SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, 0.75F);
            }
            if (this.attackTicks >= slash1 - 3 && this.attackTicks < slash2 && this.tickCount % 3 == 0) {
                this.spawnSoulPillar(-1.0F, 2.0F, 1);
            }
        }

        if (this.getAttackState() == 3) {
            int headSlash1 = 26;
            int headSlash2 = 40;
            int swordSlash = 45;
            int swordSlam = 75;
            int headSlam = 81;
            if (this.attackTicks == 2) {
                this.armFade.setTimer(5);
            }
            if (this.attackTicks >= 3 && this.attackTicks <= 100 && this.armFade.getTimer() > 0) {
                this.armFade.decreaseTimer();
            }
            if (this.attackTicks == 98) {
                this.armFade.setTimer(0);
            }
            if (this.attackTicks >= 100) {
                this.armFade.increaseTimer();
            }
            if (this.attackTicks == headSlash1 - 3) {
                this.createSweep(0.0F, 0.0F, bigSweepHeight, bigSweepAdditionalY, true, sweepSize, sweepRot, 20, true);
                this.playSound(ModSounds.HEAVY_SWING.get(), 1.0F, 1.25F);
                this.calculatedDash(0.25F);
            }
            if (this.attackTicks == headSlash1) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, sweepShakeAmount, 0, duration);
                this.SideAreaAttack(3.5F, 5.0F, 180.0F, 0.0F, 0.0F, 15.0F, 100, false, true, 0.5F, 0.0F, SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, 0.75F);
            }
            if (this.attackTicks == headSlash2 - 3) {
                this.createSweep(0.0F, 0.0F, bigSweepHeight, bigSweepAdditionalY, false, sweepSize, sweepRot, 20, true);
                this.playSound(ModSounds.HEAVY_SWING.get(), 1.0F, 1.25F);
                this.calculatedDash(0.25F);
            }
            if (this.attackTicks == headSlash2) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, sweepShakeAmount, 0, duration);
                this.SideAreaAttack(3.5F, 5.0F, 180.0F, 0.0F, 0.0F, 15.0F, 100, false, true, 0.5F, 0.0F, SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, 0.75F);
            }
            if (this.attackTicks == swordSlash - 3) {
                this.createSweep(0.0F, 0.0F, bigSweepHeight, bigSweepAdditionalY, false, sweepSize, sweepRot, 20, false);
                this.playSound(ModSounds.HEAVY_SWING.get(), 1.0F, 1.0F);
                this.calculatedDash(0.15F);
            }
            if (this.attackTicks == swordSlash) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, sweepShakeAmount, 0, duration);
                this.SideAreaAttack(3.25F, 5.0F, 180.0F, 0.0F, 0.0F, 17.0F, 100, false, true, 0.5F, 0.0F, SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, 0.75F);
            }
            if (this.attackTicks == 67 && this.targetIsNotNull()) {
                this.saveTargetPos(this.target().getX(), this.target().getY(), this.target().getZ());
            }
            if (this.attackTicks == swordSlam - 3) {
                if (this.level().isClientSide) {
                    float yaw = (float) Math.toRadians(-this.yBodyRot + 90.0F);
                    float pitch = (float) Math.toRadians(-this.getXRot() + 180.0F);
                    this.level().addParticle(new BeheadedKnightSweepParticle.SweepData(this.getScale() * 2.0F, yaw, pitch, this.getR, this.getG, this.getB),
                            this.getX(), this.getY() + bigSweepAdditionalY, this.getZ(), 0.0D, 0.0D, 0.0D);
                }
                this.playSound(ModSounds.HEAVY_SWING.get(), 1.0F, 1.0F);
                this.calculatedDashToPositon(0.2F, this.lastTargetPos());
            }
            if (this.attackTicks == headSlam - 3) {
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
                this.playSound(ModSounds.HEAVY_SWING.get(), 1.0F, 1.25F);
                this.basicDash(1.0F, 3.0F, true);
            }
            if (this.attackTicks == swordSlam) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 10.0F, 0.15F, 5, 5);
                this.StraightLineAreaAttack(swordWidth, 2.0F, 5.5F, 120, 17.0F, true, 0.5F, 0.4F);
            }
            if (this.attackTicks == headSlam) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 10.0F, 0.15F, 5, 5);
                this.StraightLineAreaAttack(swordWidth, 2.0F, 5.5F, 120, 15.0F, true, 0.5F, 0.4F);
            }
        }

        if (this.getAttackState() == 6) {
            stab1 = 21;
            int stab2 = 45;
            if (this.attackTicks == 15 && this.targetIsNotNull()) {
                this.saveTargetPos(this.target().getX(), this.target().getY(), this.target().getZ());
            }
            if (this.attackTicks == stab1 - 3) {
                this.calculatedDashToPositon(0.25F, this.lastTargetPos());
                this.playSound(ModSounds.POSSESSED_PALADIN_STAB.get(), 1.0F, 0.9F);
            }
            if (this.attackTicks == stab1) {
                this.StraightLineAreaAttack(swordWidth, 2.0F, 5.5F, 120, 17.0F, true, 0.5F, 0.4F);
            }
            if (this.attackTicks == 40 && this.targetIsNotNull()) {
                this.saveTargetPos(this.target().getX(), this.target().getY(), this.target().getZ());
            }
            if (this.attackTicks == stab2 - 3) {
                this.calculatedDashToPositon(0.25F, this.lastTargetPos());
                this.playSound(ModSounds.POSSESSED_PALADIN_STAB.get(), 1.0F, 0.9F);
            }
            if (this.attackTicks == stab2) {
                this.StraightLineAreaAttack(swordWidth, 2.0F, 5.5F, 120, 17.0F, true, 0.5F, 0.4F);
            }
        }

        if (this.getAttackState() == 7) {
            stab1 = 25;
            if (this.attackTicks == 10 && this.targetIsNotNull()) {
                this.saveTargetPos(this.target().getX(), this.target().getY(), this.target().getZ());
            }
            if (this.attackTicks == stab1 - 3) {
                this.calculatedDashToPositon(0.25F, this.lastTargetPos());
                this.playSound(ModSounds.POSSESSED_PALADIN_STAB.get(), 1.0F, 0.75F);
            }
            if (this.attackTicks == stab1) {
                this.StraightLineAreaAttack(swordWidth, 2.0F, 5.5F, 120, 17.0F, true, 1.0F, 0.2F);
            }
        }

        if (this.getAttackState() == 10) {
            int slash1 = 36;
            int slash2 = 41;
            int slash3 = 82;
            int slash4 = 86;
            ARC = 120;
            if (this.attackTicks == 2) {
                this.armFade.setTimer(5);
            }
            if (this.attackTicks >= 5 && this.attackTicks <= 183 && this.armFade.getTimer() > 0) {
                this.armFade.decreaseTimer();
            }
            if (this.attackTicks == 182) {
                this.armFade.setTimer(0);
            }
            if (this.attackTicks >= 183) {
                this.armFade.increaseTimer();
            }
            if (this.attackTicks == slash1 - 3 || this.attackTicks == slash4 - 3) {
                this.createSweep(0.0F, 0.0F, bigSweepHeight, bigSweepAdditionalY, true, sweepSize, sweepRot, 0, false);
                this.playSound(ModSounds.HEAVY_SWING.get(), 1.0F, 1.0F);
                this.basicDash(2.0F, 2.0F, true);
            }
            if (this.attackTicks == slash1 || this.attackTicks == slash4) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, sweepShakeAmount, 0, duration);
                this.SideAreaAttack(3.5F, 5.0F, ARC, 0.0F, 0.0F, 17.0F, 100, false, true, 0.5F, 0.0F, SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, 0.75F);
            }
            if (this.attackTicks == slash2 - 3 || this.attackTicks == slash3 - 3) {
                this.createSweep(0.0F, 0.0F, bigSweepHeight, bigSweepAdditionalY, true, sweepSize, sweepRot, 0, true);
                this.playSound(ModSounds.HEAVY_SWING.get(), 1.0F, 1.0F);
                this.basicDash(2.0F, 2.0F, true);
            }
            if (this.attackTicks == slash2 || this.attackTicks == slash3) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, sweepShakeAmount, 0, duration);
                this.SideAreaAttack(3.5F, 5.0F, ARC, 0.0F, 0.0F, 15.0F, 100, false, true, 0.5F, 0.0F, SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, 0.75F);
            }
            if ((this.attackTicks >= slash1 - 3 && this.attackTicks < slash2
                 || this.attackTicks >= slash3 - 3 && this.attackTicks < slash4) && this.tickCount % 3 == 0) {
                this.spawnSoulPillar(-1.0F, 2.0F, 1);
            }
            if (this.attackTicks == 115) {
                this.setNoGravity(true);
            }
            if (this.attackTicks >= 115 && this.attackTicks <= 125) {
                this.setDeltaMovement(this.getDeltaMovement().x, 0.25D, this.getDeltaMovement().z);
                int ucap = 5;
                float v = 0.075F;
                float endY = 5.0F;
                this.attractParticles(this.soulParticle(), ucap, 4, 0.0F, 0.0F, 5.0F, endY, v);
                this.attractParticles(this.soulParticle(), ucap, 4, 0.0F, 0.0F, 3.0F, endY, v);
                this.attractParticles(this.soulParticle(), ucap, 4, 0.0F, 0.0F, 2.0F, endY, v);
            }
            if (this.attackTicks == 115) {
                this.playSound(ModSounds.OMINOUS_WIND_UP.get(), 1.0F, 1.0F);
            }
            if (this.attackTicks == 135) {
                this.setNoGravity(false);
                if (this.targetIsNotNull()) {
                    Vec3 normal = this.target().position().subtract(this.position()).normalize();
                    this.setDeltaMovement(normal.x, normal.y, normal.z);
                } else {
                    this.setDeltaMovement(this.getDeltaMovement().x, -0.5D, this.getDeltaMovement().z);
                }
            }
            if (this.attackTicks == 145) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 10.0F, 0.15F, 10, 10);
                this.playSound(ModSounds.STAB_HIT.get(), 1.0F, 1.0F);
                this.SideAreaAttack(2.75F, 3.0F, 360.0F, 0.0F, 2.0F, 18.0F, 150, false, true, 1.0F, 0.5F, SoundEvents.EMPTY, 0.0F);
                this.xPillars(6, 3.0D, 8.0F);
                this.spawnCircleParticle(0.0F, 0.0F, 100.0F, true, 2.0F,
                        this.isEnhanced() ? 1.0F : 0.25F, this.isEnhanced() ? 0.25F : 1.0F,
                        this.isEnhanced() ? 0.25F : 0.75F, 0.8F, Circle.EnumRingBehavior.GROW, 30);
            }
        }

        if (this.getAttackState() == 11) {
            int grab = 20;
            if (this.attackTicks == 1) {
                this.grab = false;
            }
            if (this.attackTicks == grab - 4 && this.synchronisedDuoKnight() != null && this.synchronisedDuoKnight().isAlive()) {
                this.saveTargetPos(this.synchronisedDuoKnight().getX(), this.synchronisedDuoKnight().getY(), this.synchronisedDuoKnight().getZ());
            }
            if (this.attackTicks == grab - 3) {
                this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.0F, 0.5F);
                this.calculatedDashToPositon(0.25F, this.lastTargetPos());
            }
            if (this.attackTicks == grab) {
                this.SyncedKnightGrab(4.0F, 4.0F, 180.0F, 0.0F, 0.0F);
            }
        }

        if (this.getAttackState() == 12) {
            int Rattack = 29;
            int attack = 31;
            if (this.attackTicks == Rattack - 3) {
                this.playSound(ModSounds.GENERIC_ARM_SWING.get(), 1.0F, 1.0F);
                this.createSweep(0.0F, 0.0F, bigSweepHeight, bigSweepAdditionalY, false, sweepSize, sweepRot, 0, true);
                this.calculatedDash(0.25F);
            }
            if (this.attackTicks == Rattack) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, sweepShakeAmount, 0, duration);
                this.SideAreaAttack(3.5F, 5.0F, 180.0F, 0.0F, 0.0F, 17.0F, 100, false, true, 0.5F, 0.0F, SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, 0.75F);
            }
            if (this.attackTicks == attack - 3) {
                this.playSound(ModSounds.HEAVY_SWING.get(), 1.0F, 1.0F);
                this.createSweep(0.0F, 0.0F, bigSweepHeight, bigSweepAdditionalY, false, sweepSize, sweepRot, 0, false);
            }
            if (this.attackTicks == 33) {
                this.advancedDash(this, -3.0F, 2.5F, 1.0F);
            }
            if (this.attackTicks == attack) {
                CameraShakeEntity.cameraShake(this.level(), this.position(), 20.0F, sweepShakeAmount, 0, duration);
                this.SideAreaAttack(3.5F, 5.0F, 180.0F, 0.0F, 0.0F, 17.0F, 100, false, true, 0.5F, 0.0F, SoundEvents.ZOMBIE_ATTACK_IRON_DOOR, 0.75F);
            }
            if (this.attackTicks == 57 && this.targetIsNotNull()
                    && this.getFirstPassenger() instanceof ResurrectedKnightServant passenger) {
                this.throwAnGravityEntity(1.0F, this.target().getX(), this.target().getY(), this.target().getZ(),
                        this.getX(), this.getY() + 3.0D, this.getZ(), 8.0F, passenger);
            }
        }
    }

    public void attractParticles(ParticleOptions particleOptions, float cap, int reps, float vec, float offset,
                                 float startVec, float startOffset, float startY, float endY, float velocity) {
        float f = Mth.cos(this.yBodyRot * ((float) Math.PI / 180F));
        float f1 = Mth.sin(this.yBodyRot * ((float) Math.PI / 180F));
        double theta = this.yBodyRot * (Math.PI / 180) + 1.5707963267948966;
        double vecX = Math.cos(theta);
        double vecZ = Math.sin(theta);
        float rX = this.random1.nextFloat(-cap, cap);
        float rZ = this.random1.nextFloat(-cap, cap);
        float f2 = this.random.nextFloat() * 0.5F;
        double d1 = this.getX() + rX;
        double d2 = this.getY() + startY + f2;
        double d3 = this.getZ() + rZ;
        double d4 = this.getX() + (double) startVec * vecX + f * startOffset;
        double d5 = this.getZ() + (double) startVec * vecZ + f1 * startOffset;
        Vec3 vec3 = new Vec3(d4, d2, d5);
        Vec3 vec4 = new Vec3(this.getX() + (double) vec * vecX + f * offset,
                this.position().y + endY,
                this.getZ() + (double) vec * vecZ + f1 * offset);
        Vec3 v = vec4.subtract(vec3).scale(velocity);
        for (int i = 0; i <= reps; ++i) {
            if (this.level().isClientSide) {
                this.level().addParticle(particleOptions, d4, d2, d5, v.x, v.y, v.z);
            }
        }
    }

    public void attractParticles(ParticleOptions particleOptions, int cap, int reps, float vec, float offset,
                                 float startY, float endY, float velocity) {
        float f = Mth.cos(this.yBodyRot * ((float) Math.PI / 180F));
        float f1 = Mth.sin(this.yBodyRot * ((float) Math.PI / 180F));
        double theta = this.yBodyRot * (Math.PI / 180) + 1.5707963267948966;
        double vecX = Math.cos(theta);
        double vecZ = Math.sin(theta);
        int rX = this.random1.nextInt(-cap, cap);
        int rZ = this.random1.nextInt(-cap, cap);
        float f2 = this.random.nextFloat() * 0.5F;
        double d1 = this.getX() + rX;
        double d2 = this.getY() + startY + f2;
        double d3 = this.getZ() + rZ;
        Vec3 vec3 = new Vec3(d1, d2, d3);
        Vec3 vec4 = new Vec3(this.getX() + (double) vec * vecX + f * offset,
                this.position().y + endY,
                this.getZ() + (double) vec * vecZ + f1 * offset);
        Vec3 v = vec4.subtract(vec3).scale(velocity);
        for (int i = 0; i <= reps; ++i) {
            if (this.level().isClientSide) {
                this.level().addParticle(particleOptions, d1, d2, d3, v.x, v.y, v.z);
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
            if (!soul) {
                this.level().addParticle(new BeheadedKnightSweepParticle.SweepData(this.getScale() * scale, yaw, pitch, this.getR, this.getG, this.getB),
                        x, d1, z, 0.0D, 0.0D, 0.0D);
            } else if (this.isEnhanced()) {
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
                EntityUtil.cancelBuffs(entityHit);
                entityHit.invulnerableTime = 0;
                this.launch(entityHit, Vxz, Vy);
            }
            if (entityHit instanceof Player player && player.isBlocking() && brokenShieldTicks > 0) {
                disableShield(player, brokenShieldTicks);
            }
        }
    }

    public void spawnSoulPillar(float vec, float offset, int amount) {
        float f = Mth.cos(this.yBodyRot * ((float) Math.PI / 180F));
        float f1 = Mth.sin(this.yBodyRot * ((float) Math.PI / 180F));
        double theta = this.yBodyRot * (Math.PI / 180) + 1.5707963267948966;
        double vecX = Math.cos(theta);
        double vecZ = Math.sin(theta);
        int standingOnY = Mth.floor(this.getY()) - 1;
        double headY = this.getY() + 1.0D;
        float yawRadians = (float) Math.toRadians(90.0F + this.getYRot());
        for (int l = 0; l < amount; ++l) {
            double d2 = 1.25D * (l + 1);
            int j = l;
            this.spawnSoulPillars(this.getX() + (double) vec * vecX + f * offset + Mth.cos(yawRadians) * d2,
                    headY,
                    this.getZ() + (double) vec * vecZ + f1 * offset + Mth.sin(yawRadians) * d2,
                    standingOnY, yawRadians, j, this.level());
        }
    }

    private void xPillars(int pillar, double delay, float distance) {
        for (int i = 0; i < pillar; ++i) {
            float throwAngle = (float) i * (float) Math.PI / (float) (pillar / 2);
            for (int k = 0; (float) k < distance; ++k) {
                double d2 = 1.15D * (k + 1);
                int d3 = (int) (delay * (k + 1));
                this.spawnSoulPillars(this.getX() + Mth.cos(throwAngle) * 1.25D * d2,
                        this.getY(),
                        this.getZ() + Mth.sin(throwAngle) * 1.25D * d2,
                        this.getY() - 2.0D, throwAngle, d3, this.level());
            }
        }
    }

    private boolean spawnSoulPillars(double x, double y, double z, double lowestYCheck, float yRot,
                                      int warmupDelayTicks, Level world) {
        BlockPos blockpos = BlockPos.containing(x, y, z);
        boolean flag = false;
        double d0 = 0.0D;
        do {
            BlockPos belowPos = blockpos.below();
            BlockState below = world.getBlockState(belowPos);
            if (below.isFaceSturdy(world, belowPos, Direction.UP)) {
                if (!world.isEmptyBlock(blockpos)) {
                    BlockState here = world.getBlockState(blockpos);
                    VoxelShape shape = here.getCollisionShape(world, blockpos);
                    if (!shape.isEmpty()) {
                        d0 = shape.max(Direction.Axis.Y);
                    }
                }
                flag = true;
                break;
            }
            blockpos = blockpos.below();
        } while ((double) blockpos.getY() >= lowestYCheck);
        if (flag) {
            world.addFreshEntity(new SoulPillar(world, x, (double) blockpos.getY() + d0, z, yRot,
                    warmupDelayTicks, this, 20, 8.0F, this.isEnhanced()));
            return true;
        }
        return false;
    }

    public void SyncedKnightGrab(float range, float height, float arc, float boxOffset, float forwardOffset) {
        double theta = Math.toRadians(this.yBodyRot) + 1.5707963267948966;
        double forwardX = Math.cos(theta) * forwardOffset;
        double forwardZ = Math.sin(theta) * forwardOffset;
        for (ResurrectedKnightServant entityHit : this.getEntitiesNearby(ResurrectedKnightServant.class, range, height, range, range)) {
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
                    || entityHit != this.synchronisedDuoKnight()) {
                continue;
            }
            this.grab = entityHit.startRiding(this);
        }
    }

    public void throwAnGravityEntity(float velocity, double destX, double destY, double destZ,
                                     double x, double y, double z, float damage,
                                     ResurrectedKnightServant passenger) {
        if (passenger == null) {
            return;
        }
        ThrownKnight thrownEntity = new ThrownKnight(this.level(), this, x, y, z, damage, passenger, this.isEnhanced());
        double d0 = destX - x;
        double d1 = destY + 0.5D - thrownEntity.getY();
        double d2 = destZ - z;
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        thrownEntity.shoot(d0, d1 + d3 * 0.2D, d2, velocity, 14 - this.level().getDifficulty().getId() * 4);
        this.level().addFreshEntity(thrownEntity);
    }

    @Override
    public boolean shouldRiderSit() {
        return false;
    }

    @Override
    public LivingEntity getControllingPassenger() {
        return null;
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
        this.setAttackState(5);
    }

    @Override
    protected void tickDeath() {
        ++this.deathTicks;
        this.deathTime = 0;
        if (this.deathTicks == 60) {
            this.remove(Entity.RemovalReason.KILLED);
            this.gameEvent(GameEvent.ENTITY_DIE);
        }
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return ModSounds.LIVING_ARMOR_HURT.get();
    }
}
