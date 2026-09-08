package com.qiuyue.goetyominous.common.entities.ally.mobs.mm;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.init.ModMobType;
import com.alexander.mutantmore.config.mutant_blaze.RodlingCommonConfig;
import com.alexander.mutantmore.init.ParticleTypeInit;
import com.alexander.mutantmore.init.SoundEventInit;
import com.alexander.mutantmore.particles.AdvancedParticleOption;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;

import com.qiuyue.goetyominous.config.MobsConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;

public class RodlingServant extends Summoned {
    private static final EntityDataAccessor<Integer> SHIELDS;
    private static final EntityDataAccessor<Boolean> HAS_HELMET;
    private static final EntityDataAccessor<Integer> COALS;
    private static final EntityDataAccessor<Float> AMOUNT_TO_HEAL;
    private static final EntityDataAccessor<Byte> FLAGS;
    private static final EntityDataAccessor<Boolean> SUMMONED_BY_MUTANT_BLAZE;
    public int regenerateShieldTick;
    public int regenerateShieldTime = 150;
    public final AnimationState noveltyAnimation = new AnimationState();
    public final AnimationState shootAnimation = new AnimationState();
    public int shootAnimationTick;
    public int shootAnimationLength = 22;
    public int shootAnimationActionPoint = 7;
    public int noveltyAnimationTick;
    public int noveltyAnimationLength = 30;
    private static final EntityDataAccessor<Float> FIRE_BALL_DAMAGE;
    public boolean summonedByMutantBlaze() { return this.entityData.get(SUMMONED_BY_MUTANT_BLAZE); }
    public void setSummonedByMutantBlaze(boolean value) { this.entityData.set(SUMMONED_BY_MUTANT_BLAZE, value); }

    public RodlingServant(EntityType<? extends Owned> type, Level level) {
        super(type, level);
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.LAVA, 8.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.POWDER_SNOW, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_POWDER_SNOW, -1.0F);
        this.moveControl = new FlyingMoveControl(this, 20, true);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new com.Polarice3.Goety.common.entities.ally.Summoned.WanderGoal(this, 1.0D, 0.0F));
        this.goalSelector.addGoal(0, new RemainStationaryGoal());
        this.goalSelector.addGoal(1, new ShootAttackGoal());
        this.goalSelector.addGoal(2, new com.alexander.mutantmore.ai.goals.ApproachTargetGoal(this,
                RodlingCommonConfig.tamed_follow_target_wanted_distance.get(),
                RodlingCommonConfig.tamed_following_movement_speed_multiplier.get(), true));
        this.goalSelector.addGoal(3, new com.alexander.mutantmore.ai.goals.LookAtTargetGoal(this));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 20.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(0, (new HurtByTargetGoal(this)).setUnseenMemoryTicks(6000));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<LivingEntity>(this, LivingEntity.class, 20, false, false,
                entity -> com.Polarice3.Goety.utils.MobUtil.isOwnedTargetable(this, entity)));
    }

    public boolean canAttack(LivingEntity target) {
        return this.canTarget(target) && super.canAttack(target);
    }

    boolean canTarget(Entity target) {
        return target instanceof LivingEntity living
                && com.Polarice3.Goety.utils.MobUtil.isOwnedTargetable(this, living);
    }

    public static AttributeSupplier.Builder createConfiguredAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, com.qiuyue.goetyominous.config.AttributesConfig.RodlingServantHealth.get())
                .add(Attributes.ARMOR, com.qiuyue.goetyominous.config.AttributesConfig.RodlingServantArmor.get())
                .add(Attributes.ARMOR_TOUGHNESS, com.qiuyue.goetyominous.config.AttributesConfig.RodlingServantArmorToughness.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, com.qiuyue.goetyominous.config.AttributesConfig.RodlingServantKnockbackResistance.get())
                .add(Attributes.FOLLOW_RANGE, com.qiuyue.goetyominous.config.AttributesConfig.RodlingServantFollowRange.get())
                .add(Attributes.MOVEMENT_SPEED, com.qiuyue.goetyominous.config.AttributesConfig.RodlingServantMovementSpeed.get())
                .add(Attributes.FLYING_SPEED, com.qiuyue.goetyominous.config.AttributesConfig.RodlingServantMovementSpeed.get());
    }

    @Override
    public MobType getMobType() {
        return ModMobType.NETHER;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty,
                                        MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData,
                                        @Nullable CompoundTag pDataTag) {
        if (pReason == MobSpawnType.MOB_SUMMONED && this.getTrueOwner() instanceof Player player) {
            if (countServants(player) >= MobsConfig.RodlingServantLimit.get()) {
                return null;
            }
        }
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    public float getFireBallDamage() {
        float stored = this.entityData.get(FIRE_BALL_DAMAGE);
        return stored > 0.0F ? stored : RodlingCommonConfig.tamed_fireball_damage.get().floatValue();
    }

    public void setFireBallDamage(float value) {
        this.entityData.set(FIRE_BALL_DAMAGE, value);
    }

    private int countServants(Player player) {
        int count = 0;
        if (player.level() instanceof ServerLevel serverLevel) {
            for (Entity entity : serverLevel.getAllEntities()) {
                if (entity instanceof RodlingServant servant && servant.getTrueOwner() == player) {
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    public void die(DamageSource p_21014_) {
        if (!this.level().isClientSide
                && !this.summonedByMutantBlaze()
                && !this.hasLifespan()
                && this.getTrueOwner() instanceof Player) {
            if (this.hasHelmet()) {
                this.spawnAtLocation(new ItemStack(com.alexander.mutantmore.init.ItemInit.RODLING_HELMET.get()));
            }
            if (this.getShields() > 0) {
                this.spawnAtLocation(new ItemStack(com.alexander.mutantmore.init.ItemInit.RODLING_SHIELDS.get()));
            }
            int coals = this.getCoals();
            while (coals > 0) {
                int count = Math.min(coals, 64);
                this.spawnAtLocation(new ItemStack(Items.COAL, count));
                coals -= count;
            }
        }
        super.die(p_21014_);
    }

    public boolean shouldBeStationary() {
        return this.isDeadOrDying();
    }

    @Override
    protected PathNavigation createNavigation(Level p_218342_) {
        FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, p_218342_) {
            @Override
            public boolean isStableDestination(BlockPos pos) {
                return !this.level.getBlockState(pos.below()).isAir();
            }
        };
        flyingpathnavigation.setCanOpenDoors(false);
        flyingpathnavigation.setCanFloat(true);
        flyingpathnavigation.setCanPassDoors(true);
        return flyingpathnavigation;
    }

    @Override
    public void travel(Vec3 p_218382_) {
        if (this.isEffectiveAi() || this.isControlledByLocalInstance()) {
            if (this.isInWater()) {
                this.moveRelative(0.02F, p_218382_);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.800000011920929));
            } else if (this.isInLava()) {
                this.moveRelative(0.02F, p_218382_);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.5));
            } else {
                this.moveRelative(this.getSpeed(), p_218382_);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.9100000262260437));
            }
        }
        this.calculateEntityAnimation(false);
    }

    protected void updateAnimations() {
        this.noveltyAnimation.animateWhen(this.noveltyAnimationTick > 0, this.tickCount);
        this.shootAnimation.animateWhen(this.shootAnimationTick > 0, this.tickCount);
    }

    public void tickDownAnimTimers() {
        if (this.noveltyAnimationTick > 0) {
            --this.noveltyAnimationTick;
        }
        if (this.shootAnimationTick > 0) {
            --this.shootAnimationTick;
        }
    }

    @Override
    protected float getSoundVolume() {
        return 0.5F;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEventInit.RODLING_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource p_21239_) {
        return SoundEventInit.RODLING_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEventInit.RODLING_DEATH.get();
    }

    @Override
    public int getAmbientSoundInterval() {
        return 120;
    }

    @Override
    public boolean hurt(DamageSource p_27567_, float p_27568_) {
        if (this.hasShields() && this.getShields() > 0
                && !p_27567_.is(DamageTypes.GENERIC_KILL) && !p_27567_.is(DamageTypes.FELL_OUT_OF_WORLD)
                && !this.isInvulnerableTo(p_27567_) && p_27567_.getEntity() != null) {
            Entity entity1 = p_27567_.getEntity();
            if (entity1 instanceof LivingEntity && !p_27567_.is(DamageTypeTags.NO_ANGER)) {
                this.setLastHurtByMob((LivingEntity) entity1);
            }
            if (entity1 instanceof Player player) {
                this.lastHurtByPlayerTime = 100;
                this.lastHurtByPlayer = player;
            }
            this.invulnerableTime = 20;
            this.breakShield();
            return false;
        }
        return this.hasHelmet() && !p_27567_.is(DamageTypeTags.BYPASSES_ARMOR)
                ? super.hurt(p_27567_, p_27568_ / 2.0F)
                : super.hurt(p_27567_, p_27568_);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SHIELDS, 0);
        this.entityData.define(HAS_HELMET, false);
        this.entityData.define(COALS, 0);
        this.entityData.define(AMOUNT_TO_HEAL, 0.0F);
        this.entityData.define(FLAGS, (byte) 0);
        this.setFlag(32, true);
        this.setFlag(128, true);
        this.entityData.define(SUMMONED_BY_MUTANT_BLAZE, false);
        this.entityData.define(FIRE_BALL_DAMAGE, 0.0F);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Griefing", this.griefing());
        tag.putBoolean("AlwaysShowsName", this.alwaysShowsName());
        tag.putBoolean("SmokeParticles", this.smokeParticles());
        tag.putInt("Shields", this.getShields());
        tag.putBoolean("HasHelmet", this.hasHelmet());
        tag.putInt("Coals", this.getCoals());
        tag.putFloat("AmountToHeal", this.getAmountToHeal());
        tag.putBoolean("SummonedByMutantBlaze", this.summonedByMutantBlaze());
        tag.putFloat("FireBallDamage", this.entityData.get(FIRE_BALL_DAMAGE));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setFlag(32, tag.getBoolean("Griefing"));
        this.setFlag(64, tag.getBoolean("AlwaysShowsName"));
        this.setFlag(128, tag.getBoolean("SmokeParticles"));
        this.setSummonedByMutantBlaze(tag.getBoolean("SummonedByMutantBlaze"));
        if (tag.contains("Shields")) this.setShields(tag.getInt("Shields"));
        if (tag.contains("HasHelmet")) this.setHasHelmet(tag.getBoolean("HasHelmet"));
        if (tag.contains("Coals")) this.setCoals(tag.getInt("Coals"));
        if (tag.contains("AmountToHeal")) this.setAmountToHeal(tag.getFloat("AmountToHeal"));
        if (tag.contains("FireBallDamage")) {
            this.entityData.set(FIRE_BALL_DAMAGE, tag.getFloat("FireBallDamage"));
        }
    }

    public boolean getFlag(int flag) {
        return (this.entityData.get(FLAGS) & flag) != 0;
    }

    public void setFlag(int flag, boolean value) {
        byte b0 = this.entityData.get(FLAGS);
        this.entityData.set(FLAGS, value ? (byte) (b0 | flag) : (byte) (b0 & ~flag));
    }

    public boolean hasArmour() { return this.hasHelmet(); }
    public boolean hasShields() { return this.getShields() > 0; }
    public boolean hasFood() { return this.getCoals() > 0; }
    public boolean griefing() { return this.getFlag(32); }
    public boolean alwaysShowsName() { return this.getFlag(64); }
    public boolean smokeParticles() { return this.getFlag(128); }

    public boolean hasHelmet() { return this.entityData.get(HAS_HELMET); }
    public void setHasHelmet(boolean value) { this.entityData.set(HAS_HELMET, value); }
    public int getCoals() { return this.entityData.get(COALS); }
    public void setCoals(int value) { this.entityData.set(COALS, value); }
    public int getShields() { return Mth.clamp(this.entityData.get(SHIELDS), 0, 2); }
    public void setShields(int value) { this.entityData.set(SHIELDS, value); }
    public float getAmountToHeal() { return this.entityData.get(AMOUNT_TO_HEAL); }
    public void setAmountToHeal(float value) { this.entityData.set(AMOUNT_TO_HEAL, value); }

    @Override
    public boolean isSensitiveToWater() {
        return !this.hasArmour();
    }

    @Override
    public void aiStep() {
        if (this.level().isClientSide && this.smokeParticles()) {
            this.level().addParticle(ParticleTypes.SMOKE, this.getRandomX(0.5), this.getRandomY(), this.getRandomZ(0.5), 0.0, 0.0, 0.0);
        }
        super.aiStep();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            this.updateAnimations();
        }
    }

    @Override
    public void baseTick() {
        super.baseTick();
        this.tickDownAnimTimers();
        this.xpReward = 0;
        this.regenerateShieldTime = RodlingCommonConfig.tamed_shield_regenerate_speed.get();

        if (this.hasShields() && this.getShields() < 2 && this.regenerateShieldTick > 0) {
            --this.regenerateShieldTick;
            if (this.regenerateShieldTick == 0) {
                this.regenerateShield();
            }
        }

        Vec3 velocity = this.getDeltaMovement();
        float groundSpeed = Mth.sqrt((float) (velocity.x * velocity.x + velocity.z * velocity.z));
        if (!this.level().isClientSide && groundSpeed <= 0.1F && this.noveltyAnimationTick <= 0
                && this.random.nextInt(400) == 0
                && (this.getTarget() == null || this.getTarget().isDeadOrDying() || this.getTarget().isRemoved())) {
            this.noveltyAnimationTick = this.noveltyAnimationLength;
            this.level().broadcastEntityEvent(this, (byte) 4);
        }

        if (this.getHealth() < this.getMaxHealth()) {
            if (this.getAmountToHeal() > 0.0F) {
                float healSpeed = this.healAmount() / (float) (int) RodlingCommonConfig.tamed_heal_time.get();
                this.setAmountToHeal(this.getAmountToHeal() - healSpeed);
                this.heal(healSpeed);
            } else if (this.hasFood()) {
                if (!this.level().isClientSide) {
                    this.setCoals(this.getCoals() - 1);
                }
                this.setAmountToHeal(this.healAmount());
                this.playSound(SoundEventInit.RODLING_HEAL.get(), this.getSoundVolume() / 2.0F, this.getVoicePitch());
                this.level().addParticle(ParticleTypes.HAPPY_VILLAGER,
                        this.getRandomX(0.5), this.getEyeY(), this.getRandomZ(0.5), 0.0, 0.0, 0.0);
            }
        }
    }

    public float healAmount() {
        return this.getMaxHealth() * (RodlingCommonConfig.tamed_fed_heal_percent.get() / 100.0F);
    }

    public void breakShield() {
        this.regenerateShieldTick = this.regenerateShieldTime;
        this.setShields(this.getShields() - 1);
        this.playSound(SoundEventInit.RODLING_SHIELD_BREAK.get(), this.getSoundVolume(), this.getVoicePitch());
    }

    public void regenerateShield() {
        this.setShields(this.getShields() + 1);
        this.playSound(SoundEventInit.RODLING_REGENERATE_SHIELD.get(), this.getSoundVolume(), this.getVoicePitch());
        this.regenerateShieldTick = this.regenerateShieldTime;
    }

    @Override
    public void handleEntityEvent(byte p_28844_) {
        if (p_28844_ == 4) {
            this.noveltyAnimationTick = this.noveltyAnimationLength;
        } else if (p_28844_ == 11) {
            this.shootAnimationTick = this.shootAnimationLength;
        } else {
            super.handleEntityEvent(p_28844_);
        }
    }

    @Override
    public boolean isBaby() {
        return false;
    }

    @Override
    public boolean causeFallDamage(float p_148989_, float p_148990_, DamageSource p_148991_) {
        return false;
    }

    @Override
    protected void playStepSound(BlockPos p_20135_, BlockState p_20136_) {
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (this.getTrueOwner() == player) {
            if (stack.is(com.alexander.mutantmore.init.ItemInit.RODLING_SHIELDS.get()) && this.getShields() < 2) {
                if (!player.getAbilities().instabuild) stack.shrink(1);
                this.setShields(2);
                this.regenerateShieldTick = this.regenerateShieldTime;
                this.playSound(SoundEventInit.RODLING_EQUIP_SHIELDS.get(), 1.5F, this.getVoicePitch());
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
            if (stack.is(com.alexander.mutantmore.init.ItemInit.RODLING_HELMET.get()) && !this.hasHelmet()) {
                if (!player.getAbilities().instabuild) stack.shrink(1);
                this.setHasHelmet(true);
                this.playSound(SoundEventInit.RODLING_EQUIP_ARMOUR.get(), 1.5F, this.getVoicePitch());
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
            if ((stack.is(Items.COAL) || stack.is(Items.CHARCOAL)) && this.getCoals() < 64) {
                int give = Math.min(stack.getCount(), 64 - this.getCoals());
                if (!player.getAbilities().instabuild) stack.shrink(give);
                this.setCoals(this.getCoals() + give);
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
        }
        return super.mobInteract(player, hand);
    }

    class RemainStationaryGoal extends Goal {
        public RemainStationaryGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.TARGET, Flag.JUMP));
        }

        @Override
        public boolean canUse() {
            return RodlingServant.this.shouldBeStationary();
        }

        @Override
        public boolean canContinueToUse() {
            return RodlingServant.this.shouldBeStationary();
        }

        @Override
        public void tick() {
            RodlingServant.this.getNavigation().stop();
            RodlingServant.this.getMoveControl().setWantedPosition(
                    RodlingServant.this.getX(), RodlingServant.this.getY(), RodlingServant.this.getZ(), 0.0D);
        }
    }

    class ShootAttackGoal extends Goal {
        @Nullable
        public LivingEntity target;
        public int nextUseTime;

        public ShootAttackGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP, Flag.LOOK));
        }

        @Override
        public boolean isInterruptable() {
            return RodlingServant.this.shouldBeStationary();
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public boolean canUse() {
            this.target = RodlingServant.this.getTarget();
            double distance = RodlingCommonConfig.tamed_shoot_max_distance.get();
            return this.target != null && !this.target.isRemoved() && !this.target.isDeadOrDying()
                    && RodlingServant.this.tickCount >= this.nextUseTime
                    && RodlingServant.this.hasLineOfSight(this.target)
                    && (double) RodlingServant.this.distanceTo(this.target) <= distance
                    && this.animationsUseable();
        }

        @Override
        public boolean canContinueToUse() {
            return this.target != null && !this.target.isRemoved() && !this.target.isDeadOrDying()
                    && !this.animationsUseable();
        }

        @Override
        public void start() {
            RodlingServant.this.shootAnimationTick = RodlingServant.this.shootAnimationLength;
            RodlingServant.this.level().broadcastEntityEvent(RodlingServant.this, (byte) 11);
            RodlingServant.this.playSound(SoundEventInit.RODLING_CHARGE_SHOT.get(), RodlingServant.this.getSoundVolume(), 1.0F);
        }

        @Override
        public void tick() {
            this.target = RodlingServant.this.getTarget();
            RodlingServant.this.getNavigation().stop();
            if (RodlingServant.this.shootAnimationTick == RodlingServant.this.shootAnimationLength - 5) {
                ((ServerLevel) RodlingServant.this.level()).sendParticles(
                        new AdvancedParticleOption(ParticleTypeInit.RODLING_CHARGE_SHOT,
                                List.of((float) RodlingServant.this.getId(), 0.0F,
                                        RodlingServant.this.getBbHeight() * 0.45F, 0.0F)),
                        RodlingServant.this.getX(), RodlingServant.this.getY(0.45), RodlingServant.this.getZ(),
                        1, 0.0, 0.0, 0.0, 0.0);
            }
            if (this.target != null) {
                RodlingServant.this.getLookControl().setLookAt(this.target);
                double d1 = this.target.getX() - RodlingServant.this.getX();
                double d2 = this.target.getY(0.5) - RodlingServant.this.getY(0.5);
                double d3 = this.target.getZ() - RodlingServant.this.getZ();
                if (RodlingServant.this.shootAnimationTick == RodlingServant.this.shootAnimationActionPoint) {
                    LivingEntity owner = RodlingServant.this.getTrueOwner();
                    boolean hellBolt = owner != null && com.Polarice3.Goety.utils.CuriosFinder.hasUnholySet(owner);
                    if (!hellBolt && owner instanceof MutantBlazeServant blaze && blaze.hasUnholyBlood()) {
                        hellBolt = true;
                    }
                    if (hellBolt) {
                        com.Polarice3.Goety.common.entities.projectiles.HellBolt bolt =
                                new com.Polarice3.Goety.common.entities.projectiles.HellBolt(
                                        RodlingServant.this, d1, d2, d3, RodlingServant.this.level());
                        bolt.setPos(bolt.getX(), RodlingServant.this.getY(0.5), bolt.getZ());
                        bolt.setDamage(RodlingServant.this.getFireBallDamage());
                        RodlingServant.this.level().addFreshEntity(bolt);
                    } else {
                        RodlingServantFireball fireball = new RodlingServantFireball(RodlingServant.this.level(),
                                RodlingServant.this, d1, d2, d3);
                        fireball.damage = RodlingServant.this.getFireBallDamage();
                        fireball.fireLength = RodlingCommonConfig.tamed_fireball_fire_length.get();
                        fireball.griefing = RodlingServant.this.griefing();
                        fireball.ignoresInvulTime = RodlingCommonConfig.tamed_ignores_invulnerability_time.get();
                        fireball.setPos(fireball.getX(), RodlingServant.this.getY(0.5), fireball.getZ());
                        RodlingServant.this.level().addFreshEntity(fireball);
                    }
                    RodlingServant.this.playSound(SoundEventInit.RODLING_SHOOT.get(),
                            RodlingServant.this.getSoundVolume(), RodlingServant.this.getVoicePitch());
                }
            }
        }

        @Override
        public void stop() {
            this.nextUseTime = RodlingServant.this.tickCount + 20;
        }

        public boolean animationsUseable() {
            return RodlingServant.this.shootAnimationTick <= 0;
        }
    }

    static {
        SHIELDS = SynchedEntityData.defineId(RodlingServant.class, EntityDataSerializers.INT);
        HAS_HELMET = SynchedEntityData.defineId(RodlingServant.class, EntityDataSerializers.BOOLEAN);
        COALS = SynchedEntityData.defineId(RodlingServant.class, EntityDataSerializers.INT);
        AMOUNT_TO_HEAL = SynchedEntityData.defineId(RodlingServant.class, EntityDataSerializers.FLOAT);
        FLAGS = SynchedEntityData.defineId(RodlingServant.class, EntityDataSerializers.BYTE);
        SUMMONED_BY_MUTANT_BLAZE = SynchedEntityData.defineId(RodlingServant.class, EntityDataSerializers.BOOLEAN);
        FIRE_BALL_DAMAGE = SynchedEntityData.defineId(RodlingServant.class, EntityDataSerializers.FLOAT);
    }
}
