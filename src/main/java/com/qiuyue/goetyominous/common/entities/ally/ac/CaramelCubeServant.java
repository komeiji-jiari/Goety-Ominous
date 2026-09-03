package com.qiuyue.goetyominous.common.entities.ally.ac;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.qiuyue.goetyominous.config.MobsConfig;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class CaramelCubeServant extends Summoned {

    private static final EntityDataAccessor<Integer> SIZE = SynchedEntityData.defineId(CaramelCubeServant.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> WANTS_TO_JUMP = SynchedEntityData.defineId(CaramelCubeServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> HAS_JUMPED = SynchedEntityData.defineId(CaramelCubeServant.class, EntityDataSerializers.BOOLEAN);
    private float squishProgress;
    private float prevSquishProgress;
    private float jumpProgress;
    private float prevJumpProgress;
    private float jiggleTime;
    private float prevJiggleTime;

    protected static final EntityDimensions SMALL_DIMENSIONS = EntityDimensions.fixed(0.8F, 0.8F);
    protected static final EntityDimensions MEDIUM_DIMENSIONS = EntityDimensions.fixed(1.5F, 1.5F);
    protected static final EntityDimensions LARGE_DIMENSIONS = EntityDimensions.fixed(3.5F, 3.5F);

    public CaramelCubeServant(EntityType<? extends Summoned> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new MoveHelper();
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.CaramelCubeServantMovementSpeed.get())
                .add(Attributes.MAX_HEALTH, AttributesConfig.CaramelCubeServantHealth.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.CaramelCubeServantFollowRange.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.CaramelCubeServantDamage.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, AttributesConfig.CaramelCubeServantKnockbackResistance.get())
                .add(Attributes.ARMOR, AttributesConfig.CaramelCubeServantArmor.get());
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new FloatGoal());
        this.goalSelector.addGoal(2, new AttackGoal());
        this.goalSelector.addGoal(3, new RandomDirectionGoal());
        this.goalSelector.addGoal(4, new KeepOnJumpingGoal());
    }

    @Override
    public void followGoal() {
        this.goalSelector.addGoal(8, new FollowCubeGoal(10.0F, 2.0F));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SIZE, 0);
        this.entityData.define(WANTS_TO_JUMP, false);
        this.entityData.define(HAS_JUMPED, false);
    }

    public float getJumpProgress(float partialTick) {
        return (prevJumpProgress + (jumpProgress - prevJumpProgress) * partialTick) * 0.33F;
    }

    public float getSquishProgress(float partialTick) {
        return (prevSquishProgress + (squishProgress - prevSquishProgress) * partialTick) * 0.2F;
    }

    public float getJiggleTime(float partialTick) {
        return (prevJiggleTime + (jiggleTime - prevJiggleTime) * partialTick) * 0.2F;
    }

    @Override
    protected void jumpFromGround() {
        Vec3 vec3 = this.getDeltaMovement();
        this.setDeltaMovement(vec3.x, this.getJumpPower(), vec3.z);
        this.hasImpulse = true;
    }

    @Override
    protected float getJumpPower() {
        float f = this.getSlimeSize() == 2 ? 0.3F : this.getSlimeSize() == 1 ? 0.1F : 0.0F;
        return super.getJumpPower() + f;
    }

    public int getMaxHeadXRot() {
        return 0;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setSlimeSize(compound.getInt("SlimeSize"), false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("SlimeSize", this.getSlimeSize());
    }

    public void setSlimeSize(int i, boolean heal) {
        int size = Mth.clamp(i, 0, 2);
        this.entityData.set(SIZE, size);
        this.reapplyPosition();
        this.refreshDimensions();
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(AttributesConfig.CaramelCubeServantHealth.get() + 6.0F * size);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(AttributesConfig.CaramelCubeServantMovementSpeed.get() + 0.1F * size);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(AttributesConfig.CaramelCubeServantDamage.get() + (double) (2.0F * size));
        if (heal) {
            this.setHealth(this.getMaxHealth());
        }
    }

    public int getSlimeSize() {
        return Math.min(this.entityData.get(SIZE), 2);
    }

    public void setWantsToJump(boolean wantsToJump) {
        this.entityData.set(WANTS_TO_JUMP, wantsToJump);
    }

    public boolean wantsToJump() {
        return this.entityData.get(WANTS_TO_JUMP);
    }

    public void setHasJumped(boolean hasJumped) {
        this.entityData.set(HAS_JUMPED, hasJumped);
    }

    public boolean hasJumped() {
        return this.entityData.get(HAS_JUMPED);
    }

    @Override
    protected int calculateFallDamage(float f, float f1) {
        return super.calculateFallDamage(f, f1) - 5;
    }

    @Override
    public void tick() {
        super.tick();
        prevJumpProgress = jumpProgress;
        prevSquishProgress = squishProgress;
        prevJiggleTime = jiggleTime;
        boolean jumping = !this.onGround() && tickCount > 4;
        boolean squish = !jumping && (this.wantsToJump() || this.hasJumped() && this.onGround());
        if (jumping && jumpProgress < 3.0F) {
            jumpProgress++;
        }
        if (!jumping && jumpProgress > 0.0F) {
            jumpProgress--;
        }
        if (squish && squishProgress < 5.0F) {
            squishProgress++;
            if (squishProgress >= 5.0F) {
                this.setHasJumped(false);
            }
        }
        if (!squish && squishProgress > 0.0F) {
            squishProgress--;
        }
        if (this.hasJumped() && this.onGround()) {
            jiggleTime = 5;
        } else if (jiggleTime > 0) {
            if (jiggleTime == 4) {
                this.playSound(this.getSquishSound(), this.getSoundVolume(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) / 0.8F);
            }
            if (jiggleTime > 4) {
                spawnLandParticles();
            }
            jiggleTime--;
        }
    }

    private void spawnLandParticles() {
        int i = 1 + this.getSlimeSize();
        for (int j = 0; j < i * 6; ++j) {
            float f = this.random.nextFloat() * ((float) Math.PI * 2F);
            float f1 = this.random.nextFloat() * 0.5F + 0.65F;
            float f2 = Mth.sin(f) * (float) i * 0.5F * f1;
            float f3 = Mth.cos(f) * (float) i * 0.5F * f1;
            this.level().addParticle(ACParticleRegistry.CARAMEL_DROP.get(), this.getX() + (double) f2, this.getY() + 0.15F, this.getZ() + (double) f3, 0.0D, 0.0D, 0.0D);
        }
    }

    private void spawnMeltedCaramel() {
        int i = 1 + this.getSlimeSize();
        for (int j = 0; j < i; ++j) {
            float f = this.random.nextFloat() * ((float) Math.PI * 2F);
            float f1 = this.random.nextFloat() * 0.5F + 0.65F;
            float f2 = Mth.sin(f) * (float) i * 0.5F * f1;
            float f3 = Mth.cos(f) * (float) i * 0.5F * f1;
            MeltedCaramelServantEntity meltedCaramel = AcEntityRegistry.MELTED_CARAMEL_SERVANT.get().create(level());
            if (meltedCaramel == null) {
                continue;
            }
            Vec3 vec3 = new Vec3(this.getX() + (double) f2, this.getY() + 0.02, this.getZ() + (double) f3);
            meltedCaramel.setPos(ACMath.getGroundBelowPosition(level(), vec3));
            meltedCaramel.setDespawnsIn(40 + (i - 1) * 40);
            meltedCaramel.setOwnerMaster(this.getOwnerId());
            meltedCaramel.setDeltaMovement(this.getDeltaMovement().multiply(-1.0F, 0.0F, -1.0F));
            level().addFreshEntity(meltedCaramel);
        }
    }

    @Override
    public boolean hurt(DamageSource damageSource, float damageValue) {
        boolean sup = super.hurt(damageSource, damageValue);
        if (sup) {
            spawnMeltedCaramel();
        }
        return sup;
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficulty,
                                        MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData,
                                        @Nullable CompoundTag tag) {
        if (this.getTrueOwner() instanceof Player player) {
            if (countServants(player) >= MobsConfig.CaramelCubeServantLimit.get()) {
                this.discard();
                return null;
            }
        }
        this.setSlimeSize(this.random.nextInt(3), true);
        return super.finalizeSpawn(levelAccessor, difficulty, spawnType, spawnGroupData, tag);
    }

    private int countServants(Player player) {
        int count = 0;
        if (player.level() instanceof ServerLevel serverLevel) {
            for (Entity entity : serverLevel.getAllEntities()) {
                if (entity instanceof CaramelCubeServant servant && servant != this) {
                    if (servant.getTrueOwner() == player) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> dataAccessor) {
        if (SIZE.equals(dataAccessor)) {
            this.refreshDimensions();
            this.setYRot(this.yHeadRot);
            this.yBodyRot = this.yHeadRot;
            if (this.isInWater() && this.random.nextInt(20) == 0) {
                this.doWaterSplashEffect();
            }
        }
        super.onSyncedDataUpdated(dataAccessor);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        switch (this.getSlimeSize()) {
            case 2:
                return LARGE_DIMENSIONS;
            case 1:
                return MEDIUM_DIMENSIONS;
            default:
                return SMALL_DIMENSIONS;
        }
    }

    @Override
    public void remove(Entity.RemovalReason reason) {
        int ourSize = this.getSlimeSize();
        if (!this.level().isClientSide && ourSize > 0 && this.isDeadOrDying()) {
            Component component = this.getCustomName();
            boolean flag = this.isNoAi();
            float f = (float) ourSize / 4.0F;
            int j = ourSize - 1;
            int slimesSpawned = ourSize >= 2 ? 2 : 2 + this.random.nextInt(2);

            for (int l = 0; l < slimesSpawned; ++l) {
                float f1 = ((float) (l % 2) - 0.5F) * f;
                float f2 = ((float) (l / 2) - 0.5F) * f;
                CaramelCubeServant slime = (CaramelCubeServant) this.getType().create(this.level());
                if (slime != null) {
                    if (this.isPersistenceRequired()) {
                        slime.setPersistenceRequired();
                    }
                    slime.setCustomName(component);
                    slime.setNoAi(flag);
                    slime.setInvulnerable(this.isInvulnerable());
                    slime.setSlimeSize(j, true);
                    if (this.getTrueOwner() != null) {
                        slime.setTrueOwner(this.getTrueOwner());
                    }
                    slime.limitedLifespan = this.limitedLifespan;
                    if (this.limitedLifeTicks > 0) {
                        slime.limitedLifeTicks = this.limitedLifeTicks;
                    }
                    slime.setHostile(this.isHostile());
                    slime.moveTo(this.getX() + (double) f1, this.getY() + 0.5D, this.getZ() + (double) f2, this.random.nextFloat() * 360.0F, 0.0F);
                    this.level().addFreshEntity(slime);
                }
            }
        }
        super.remove(reason);
    }

    @Override
    public void lifeSpanDamage() {
        this.dismiss();
    }

    @Override
    public void dismiss() {
        if (!this.level().isClientSide && this.level() instanceof ServerLevel serverLevel) {
            ServerParticleUtil.addParticlesAroundSelf(serverLevel, ParticleTypes.POOF, this);
        }
        this.discard();
    }

    @Override
    public void tryKill(Player player) {
        if (this.limitedLifespan || this.limitedLifeTicks > 0) {
            this.lifeSpanDamage();
        } else {
            super.tryKill(player);
        }
    }

    @Override
    protected void dropFromLootTable(DamageSource source, boolean b) {
        if (this.getSlimeSize() == 0) {
            super.dropFromLootTable(source, b);
        }
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 0.5F * dimensions.height;
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effectInstance) {
        return super.canBeAffected(effectInstance) && effectInstance.getEffect() != MobEffects.HUNGER;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource p_33631_) {
        return this.getSquishSound();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return this.getSquishSound();
    }

    protected SoundEvent getJumpSound() {
        return this.getSquishSound();
    }

    protected SoundEvent getSquishSound() {
        return this.getSlimeSize() == 0 ? ACSoundRegistry.CARAMEL_CUBE_SMALL.get() : ACSoundRegistry.CARAMEL_CUBE_BIG.get();
    }

    class MoveHelper extends MoveControl {
        private float yRot;
        private int jumpDelay;
        private boolean isAggressive;

        public MoveHelper() {
            super(CaramelCubeServant.this);
            this.yRot = 180.0F * CaramelCubeServant.this.getYRot() / (float) Math.PI;
        }

        public void setDirection(float yRot, boolean aggressive) {
            this.yRot = yRot;
            this.isAggressive = aggressive;
        }

        public void setWantedMovement(double speed) {
            this.speedModifier = speed;
            this.operation = MoveControl.Operation.MOVE_TO;
        }

        public void tick() {
            this.mob.setYRot(this.rotlerp(this.mob.getYRot(), this.yRot, 90.0F));
            this.mob.yHeadRot = this.mob.getYRot();
            this.mob.yBodyRot = this.mob.getYRot();
            if (this.operation != MoveControl.Operation.MOVE_TO) {
                this.mob.setZza(0.0F);
            } else {
                this.operation = MoveControl.Operation.WAIT;
                if (this.mob.onGround()) {
                    float f = this.isAggressive ? 1.5F : 1F;
                    this.mob.setSpeed((float) (this.speedModifier * f * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
                    if (this.jumpDelay-- <= 0) {
                        CaramelCubeServant.this.setWantsToJump(false);
                        this.jumpDelay = 20;
                        if (this.isAggressive) {
                            this.jumpDelay = 6;
                        }
                        CaramelCubeServant.this.getJumpControl().jump();
                        CaramelCubeServant.this.setHasJumped(true);
                        CaramelCubeServant.this.playSound(CaramelCubeServant.this.getJumpSound(), CaramelCubeServant.this.getSoundVolume(), CaramelCubeServant.this.getVoicePitch());
                    } else {
                        CaramelCubeServant.this.xxa = 0.0F;
                        CaramelCubeServant.this.zza = 0.0F;
                        this.mob.setSpeed(0.0F);
                        CaramelCubeServant.this.setWantsToJump(true);
                    }
                } else {
                    this.mob.setSpeed((float) (this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
                }
            }
        }
    }

    class AttackGoal extends Goal {
        private int growTiredTimer;
        private int attackLogicCooldown = 0;

        public AttackGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        public boolean canUse() {
            LivingEntity livingentity = CaramelCubeServant.this.getTarget();
            if (livingentity == null) {
                return false;
            } else {
                return CaramelCubeServant.this.canAttack(livingentity) && CaramelCubeServant.this.getMoveControl() instanceof MoveHelper;
            }
        }

        public void start() {
            this.growTiredTimer = reducedTickDelay(300);
            super.start();
        }

        public boolean canContinueToUse() {
            LivingEntity livingentity = CaramelCubeServant.this.getTarget();
            if (livingentity == null) {
                return false;
            } else if (!CaramelCubeServant.this.canAttack(livingentity)) {
                return false;
            } else {
                return --this.growTiredTimer > 0;
            }
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            LivingEntity livingentity = CaramelCubeServant.this.getTarget();
            if (livingentity != null) {
                CaramelCubeServant.this.lookAt(livingentity, 10.0F, 10.0F);
                double dist = CaramelCubeServant.this.distanceTo(livingentity);
                if (dist < CaramelCubeServant.this.getBbWidth() + 0.25D + livingentity.getBbWidth() && CaramelCubeServant.this.hasJumped() && CaramelCubeServant.this.onGround() && attackLogicCooldown == 0) {
                    attackLogicCooldown = 5;
                    CaramelCubeServant.this.playSound(ACSoundRegistry.CARAMEL_CUBE_ATTACK.get(), CaramelCubeServant.this.getSoundVolume(), CaramelCubeServant.this.getVoicePitch());
                    livingentity.hurt(CaramelCubeServant.this.getServantAttack(), (float) CaramelCubeServant.this.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
                }
            }
            if (attackLogicCooldown > 0) {
                attackLogicCooldown--;
            }
            MoveControl movecontrol = CaramelCubeServant.this.getMoveControl();
            if (movecontrol instanceof MoveHelper slime$slimemovecontrol) {
                slime$slimemovecontrol.setDirection(CaramelCubeServant.this.getYRot(), true);
            }
        }
    }

    class FloatGoal extends Goal {

        public FloatGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
            CaramelCubeServant.this.getNavigation().setCanFloat(true);
        }

        public boolean canUse() {
            return (CaramelCubeServant.this.isInWater() || CaramelCubeServant.this.isInLava()) && CaramelCubeServant.this.getMoveControl() instanceof MoveHelper;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            if (CaramelCubeServant.this.getRandom().nextFloat() < 0.8F) {
                CaramelCubeServant.this.getJumpControl().jump();
            }
            MoveControl movecontrol = CaramelCubeServant.this.getMoveControl();
            if (movecontrol instanceof MoveHelper slime$slimemovecontrol) {
                slime$slimemovecontrol.setWantedMovement(1.2D);
            }
        }
    }

    class KeepOnJumpingGoal extends Goal {

        public KeepOnJumpingGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
        }

        public boolean canUse() {
            return !CaramelCubeServant.this.isPassenger() && !CaramelCubeServant.this.isStaying()
                    && (CaramelCubeServant.this.getTrueOwner() == null || CaramelCubeServant.this.isWandering() || CaramelCubeServant.this.getTarget() != null);
        }

        public void tick() {
            MoveControl movecontrol = CaramelCubeServant.this.getMoveControl();
            if (movecontrol instanceof MoveHelper slime$slimemovecontrol) {
                slime$slimemovecontrol.setWantedMovement(1.0D);
            }
        }
    }

    class RandomDirectionGoal extends Goal {
        private float chosenDegrees;
        private int nextRandomizeTime;

        public RandomDirectionGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        public boolean canUse() {
            return CaramelCubeServant.this.getTarget() == null
                    && (CaramelCubeServant.this.getTrueOwner() == null || CaramelCubeServant.this.isWandering())
                    && (CaramelCubeServant.this.onGround() || CaramelCubeServant.this.isInWater() || CaramelCubeServant.this.isInLava() || CaramelCubeServant.this.hasEffect(MobEffects.LEVITATION))
                    && CaramelCubeServant.this.getMoveControl() instanceof MoveHelper;
        }

        public void tick() {
            if (--this.nextRandomizeTime <= 0) {
                this.nextRandomizeTime = this.adjustedTickDelay(40 + CaramelCubeServant.this.getRandom().nextInt(60));
                this.chosenDegrees = (float) CaramelCubeServant.this.getRandom().nextInt(360);
            }
            MoveControl movecontrol = CaramelCubeServant.this.getMoveControl();
            if (movecontrol instanceof MoveHelper slime$slimemovecontrol) {
                slime$slimemovecontrol.setDirection(this.chosenDegrees, false);
            }
        }
    }

    class FollowCubeGoal extends Goal {
        private final float startDistance;
        private final float stopDistance;
        private LivingEntity owner;

        public FollowCubeGoal(float startDistance, float stopDistance) {
            this.startDistance = startDistance;
            this.stopDistance = stopDistance;
            this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        public boolean canUse() {
            LivingEntity livingentity = CaramelCubeServant.this.getTrueOwner();
            if (livingentity == null) {
                return false;
            }
            if (!(CaramelCubeServant.this.getMoveControl() instanceof MoveHelper)) {
                return false;
            }
            if (livingentity.isSpectator()) {
                return false;
            }
            if (CaramelCubeServant.this.distanceToSqr(livingentity) < (double) Mth.square(this.startDistance)) {
                return false;
            }
            if (!CaramelCubeServant.this.isFollowing() || CaramelCubeServant.this.isCommanded()) {
                return false;
            }
            if (CaramelCubeServant.this.getTarget() != null) {
                return false;
            }
            this.owner = livingentity;
            return true;
        }

        public boolean canContinueToUse() {
            if (this.owner == null || this.owner.isRemoved() || !this.owner.isAlive()) {
                return false;
            }
            if (CaramelCubeServant.this.getTarget() != null) {
                return false;
            }
            if (!CaramelCubeServant.this.isFollowing() || CaramelCubeServant.this.isCommanded()) {
                return false;
            }
            return CaramelCubeServant.this.distanceToSqr(this.owner) > (double) Mth.square(this.stopDistance);
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            if (this.owner != null) {
                CaramelCubeServant.this.lookAt(this.owner, 10.0F, 10.0F);
            }
            MoveControl movecontrol = CaramelCubeServant.this.getMoveControl();
            if (movecontrol instanceof MoveHelper slime$slimemovecontrol) {
                slime$slimemovecontrol.setDirection(CaramelCubeServant.this.getYRot(), true);
                slime$slimemovecontrol.setWantedMovement(1.0D);
            }
        }
    }
}
