package com.qiuyue.goetyominous.common.entities.ally.ac;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.utils.MobUtil;
import com.github.alexmodguy.alexscaves.server.block.AbyssalAltarBlock;
import com.github.alexmodguy.alexscaves.server.block.blockentity.AbyssalAltarBlockEntity;
import com.github.alexmodguy.alexscaves.server.entity.ai.AnimalRandomlySwimGoal;
import com.github.alexmodguy.alexscaves.server.entity.ai.SemiAquaticPathNavigator;
import com.github.alexmodguy.alexscaves.server.entity.ai.VerticalSwimmingMoveControl;
import com.github.alexmodguy.alexscaves.server.entity.living.DeepOneKnightEntity;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.qiuyue.goetyominous.common.entities.ai.ac.DeepOneBarterGoal;
import com.qiuyue.goetyominous.common.entities.ai.ac.IDeepOneBarterer;
import com.qiuyue.goetyominous.common.entities.projectile.ac.DeepOneKnightServantWave;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.qiuyue.goetyominous.config.MobsConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import java.util.function.Predicate;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

public class DeepOneKnightServant extends Summoned implements IDeepOneBarterer, IAnimatedEntity {

    public static final Animation ANIMATION_THROW = DeepOneKnightEntity.ANIMATION_THROW;
    public static final Animation ANIMATION_BITE = DeepOneKnightEntity.ANIMATION_BITE;
    public static final Animation ANIMATION_SCRATCH = DeepOneKnightEntity.ANIMATION_SCRATCH;
    public static final Animation ANIMATION_TRADE = DeepOneKnightEntity.ANIMATION_TRADE;
    public static final ResourceLocation BARTER_LOOT = new ResourceLocation("alexscaves", "gameplay/deep_one_knight_barter");

    private static final EntityDimensions SWIMMING_SIZE = new EntityDimensions(1.2F, 1.3F, false);

    private static final EntityDataAccessor<Boolean> SWIMMING = SynchedEntityData.defineId(DeepOneKnightServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SOUNDS_ANGRY = SynchedEntityData.defineId(DeepOneKnightServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> FOCUS_SUMMONED = SynchedEntityData.defineId(DeepOneKnightServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Optional<BlockPos>> ALTAR_POS = SynchedEntityData.defineId(DeepOneKnightServant.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);

    protected boolean isLandNavigator;
    private boolean hasSwimmingSize = false;
    private float fishPitch = 0.0F;
    private float prevFishPitch = 0.0F;
    private float swimAmount = 0.0F;
    private float prevSwimAmount = 0.0F;
    private Animation currentAnimation;
    private int animationTick;
    private ItemStack swappedItem = ItemStack.EMPTY;
    private boolean spawnedLootItem = false;
    private int throwCooldown = 0;
    private int waveCooldown = 0;
    private int dashCooldown = 0;
    private int dashTicks = 0;
    private Vec3 dashDirection = Vec3.ZERO;

    public DeepOneKnightServant(EntityType<? extends Summoned> entityType, Level level) {
        super(entityType, level);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 0.0F);
        this.switchNavigator(false);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.DeepOneKnightServantMovementSpeed.get())
                .add(Attributes.MAX_HEALTH, AttributesConfig.DeepOneKnightServantHealth.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.DeepOneKnightServantDamage.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.DeepOneKnightServantFollowRange.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, AttributesConfig.DeepOneKnightServantKnockbackResistance.get())
                .add(Attributes.ARMOR, AttributesConfig.DeepOneKnightServantArmor.get());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SWIMMING, false);
        this.entityData.define(SOUNDS_ANGRY, false);
        this.entityData.define(FOCUS_SUMMONED, false);
        this.entityData.define(ALTAR_POS, Optional.empty());
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficulty,
                                        MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData,
                                        @Nullable CompoundTag tag) {
        if (spawnType == MobSpawnType.MOB_SUMMONED && this.getTrueOwner() instanceof Player player) {
            if (countServants(player) >= MobsConfig.DeepOneKnightServantLimit.get()) {
                return null;
            }
        }
        SpawnGroupData data = super.finalizeSpawn(levelAccessor, difficulty, spawnType, spawnGroupData, tag);
        this.setItemSlot(EquipmentSlot.MAINHAND, this.random.nextFloat() < AttributesConfig.DeepOneKnightServantOrtholanceChance.get().floatValue() ? new ItemStack(ACItemRegistry.ORTHOLANCE.get()) : new ItemStack(Items.TRIDENT));
        return data;
    }

    private int countServants(Player player) {
        int count = 0;
        if (player.level() instanceof ServerLevel serverLevel) {
            for (Entity entity : serverLevel.getAllEntities()) {
                if (entity instanceof DeepOneKnightServant servant && servant != this) {
                    if (servant.getTrueOwner() == player) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    @Override
    public void setTrueOwner(@Nullable LivingEntity livingEntity) {
        super.setTrueOwner(livingEntity);
        if (!this.level().isClientSide && livingEntity instanceof Player player) {
            if (countServants(player) >= MobsConfig.DeepOneKnightServantLimit.get()) {
                this.discard();
            }
        }
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new KnightMeleeGoal());
        this.goalSelector.addGoal(1, new DeepOneBarterGoal(this));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 16.0F));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(6, new AnimalRandomlySwimGoal(this, 12, 18, 18, 1.0D));
    }

    @Override
    public void followGoal() {
        this.goalSelector.addGoal(5, new Summoned.FollowOwnerWaterGoal(this, 1.0D, 10.0F, 2.0F));
    }

    protected void switchNavigator(boolean onLand) {
        if (onLand) {
            this.moveControl = new MoveControl(this);
            this.navigation = new GroundPathNavigation(this, this.level());
            this.isLandNavigator = true;
        } else {
            this.moveControl = new VerticalSwimmingMoveControl(this, 0.8F, 10.0F);
            this.navigation = this.createNavigation(this.level());
            this.isLandNavigator = false;
        }
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new DeepOneNavigator(level);
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public MobType getMobType() {
        return MobType.WATER;
    }

    @Override
    public int getSummonLimit(LivingEntity player) {
        return MobsConfig.DeepOneKnightServantLimit.get();
    }

    @Override
    public Predicate<Entity> summonPredicate() {
        return entity -> entity instanceof DeepOneKnightServant;
    }

    @Override
    public void tick() {
        if (this.dashTicks > 0) {
            this.dashTicks--;
            double dashSpeed = AttributesConfig.DeepOneKnightServantOrtholanceDashSpeed.get();
            this.setDeltaMovement(this.dashDirection.x * dashSpeed, this.getDeltaMovement().y, this.dashDirection.z * dashSpeed);
        }
        super.tick();
        this.prevFishPitch = this.fishPitch;
        this.prevSwimAmount = this.swimAmount;
        boolean water = this.isInWaterOrBubble();
        if (water && this.isLandNavigator) {
            this.switchNavigator(false);
        }
        if (!water && !this.isLandNavigator) {
            this.switchNavigator(true);
        }
        if (water && !this.onGround() && !this.getNavigation().isDone() && !this.isDeepOneSwimming()) {
            this.setDeepOneSwimming(true);
        }
        float pitchTarget;
        if (this.isDeepOneSwimming()) {
            pitchTarget = (float) this.getDeltaMovement().y * 2.0F;
            if (!this.level().isClientSide && this.getNavigation().isDone() && this.onGround()) {
                this.setDeepOneSwimming(false);
            }
        } else {
            pitchTarget = 0.0F;
        }
        if (this.isDeepOneSwimming()) {
            if (!this.hasSwimmingSize) {
                this.hasSwimmingSize = true;
                this.refreshDimensions();
            }
        } else if (this.hasSwimmingSize) {
            this.hasSwimmingSize = false;
            this.refreshDimensions();
        }
        float targetSwimAmount = this.isDeepOneSwimming() ? 1.0F : 0.0F;
        this.swimAmount = this.swimAmount + (targetSwimAmount - this.swimAmount) * 0.1F;
        if (Math.abs(this.swimAmount - targetSwimAmount) < 0.01F) {
            this.swimAmount = targetSwimAmount;
        }
        if (!this.level().isClientSide && this.getAnimation() == this.getTradingAnimation()
                && this.getMainHandItem().is(ACTagRegistry.DEEP_ONE_BARTERS)
                && this.getLastAltarPos() != null) {
            BlockPos altarPos = this.getLastAltarPos();
            Vec3 center = Vec3.atCenterOf(altarPos);
            if (this.getAnimationTick() > this.getTradingAnimation().getDuration() - 10) {
                BlockEntity blockEntity = this.level().getBlockEntity(altarPos);
                if (blockEntity instanceof AbyssalAltarBlockEntity altar) {
                    if (!this.spawnedLootItem) {
                        List<ItemStack> possibles = this.generateBarterLoot();
                        ItemStack stack = possibles.isEmpty() ? ItemStack.EMPTY : possibles.get(0);
                        if (altar.getItem(0).isEmpty()) {
                            altar.setItem(0, stack);
                            this.level().setBlockAndUpdate(altarPos, altar.getBlockState().setValue(AbyssalAltarBlock.ACTIVE, true));
                        } else {
                            Vec3 dropPos = center.add(0.0, 0.5, 0.0);
                            this.level().addFreshEntity(new ItemEntity(this.level(), dropPos.x, dropPos.y, dropPos.z, stack));
                        }
                        this.spawnedLootItem = true;
                    }
                }
                this.restoreSwappedItem();
            }
            this.getLookControl().setLookAt(center.x, center.y, center.z, 20.0F, this.getMaxHeadXRot());
        }
        if (this.spawnedLootItem && this.getAnimation() != this.getTradingAnimation()) {
            this.spawnedLootItem = false;
        }
        if (this.throwCooldown > 0) {
            this.throwCooldown--;
        }
        if (this.waveCooldown > 0) {
            this.waveCooldown--;
        }
        if (this.dashCooldown > 0) {
            this.dashCooldown--;
        }
        this.fishPitch = Mth.approachDegrees(this.fishPitch, Mth.clamp(pitchTarget, -1.4F, 1.4F) * -57.295776F, 5.0F);
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (this.isEffectiveAi() && this.isInWaterOrBubble()) {
            this.moveRelative(this.getSpeed(), travelVector);
            Vec3 delta = this.getDeltaMovement();
            if (Double.isNaN(delta.y)) {
                delta = new Vec3(delta.x, 0.0, delta.z);
            }
            if (this.sinksWhenNotSwimming() && !this.isDeepOneSwimming()) {
                delta = delta.scale(0.8);
                delta = this.jumping || this.horizontalCollision ? delta.add(0.0, 0.1F, 0.0) : delta.add(0.0, -0.05F, 0.0);
            }
            this.move(MoverType.SELF, delta);
            this.setDeltaMovement(delta.scale(0.8));
        } else {
            super.travel(travelVector);
        }
    }

    protected boolean sinksWhenNotSwimming() {
        return true;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public EntityDimensions getDimensions(Pose poseIn) {
        return this.isDeepOneSwimming() ? SWIMMING_SIZE : super.getDimensions(poseIn);
    }

    public EntityDimensions getSwimmingSize() {
        return SWIMMING_SIZE;
    }

    @Override
    public void calculateEntityAnimation(boolean flying) {
        if (this.isDeepOneSwimming()) {
            float f1 = (float) Mth.length(this.getX() - this.xo, this.getY() - this.yo, this.getZ() - this.zo);
            float f2 = Math.min(f1 * 6.0F, 1.0F);
            this.walkAnimation.update(f2, 0.4F);
        } else {
            super.calculateEntityAnimation(flying);
        }
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        return super.isInvulnerableTo(damageSource) || damageSource.is(DamageTypes.IN_WALL);
    }

    public void startAttackBehavior(LivingEntity target) {
        if (this.dashTicks > 0) {
            return;
        }
        double distance = this.distanceTo(target);
        float f = this.getBbWidth() + target.getBbWidth();
        boolean meleeOnly = this.getItemInHand(InteractionHand.MAIN_HAND).is(ACItemRegistry.ORTHOLANCE.get());
        boolean hasTrident = this.getItemInHand(InteractionHand.MAIN_HAND).is(Items.TRIDENT);
        if (hasTrident) {
            if (this.hasLineOfSight(target) && distance < 35.0D) {
                if (this.throwCooldown <= 0) {
                    this.getNavigation().stop();
                    if (this.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
                        this.setAnimation(ANIMATION_THROW);
                    } else if (this.getAnimation() == ANIMATION_THROW && this.getAnimationTick() > 8) {
                        this.throwTrident(target);
                        this.throwCooldown = 20;
                    }
                } else {
                    this.getNavigation().moveTo(target, 1.2);
                }
            } else {
                this.getNavigation().moveTo(target, 1.2);
            }
            if (distance < (double) f + 1.0F && this.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
                this.setAnimation(this.getRandom().nextBoolean() ? ANIMATION_SCRATCH : ANIMATION_BITE);
                this.playSound(ACSoundRegistry.DEEP_ONE_KNIGHT_ATTACK.get());
            }
        } else if (meleeOnly) {
            boolean inDashRange = distance >= AttributesConfig.DeepOneKnightServantOrtholanceDashMinRange.get()
                    && distance <= AttributesConfig.DeepOneKnightServantOrtholanceDashMaxRange.get();
            if (this.hasLineOfSight(target) && inDashRange && this.dashCooldown <= 0) {
                this.getNavigation().stop();
                this.performOrtholanceDash(target);
                this.dashCooldown = AttributesConfig.DeepOneKnightServantOrtholanceDashCooldown.get();
            } else if (this.hasLineOfSight(target) && distance < AttributesConfig.DeepOneKnightServantOrtholanceWaveRange.get()) {
                if (this.waveCooldown <= 0) {
                    this.getNavigation().stop();
                    this.performOrtholanceWaveAttack(target);
                    this.waveCooldown = AttributesConfig.DeepOneKnightServantOrtholanceWaveCooldown.get();
                } else {
                    this.getNavigation().moveTo(target, 1.2);
                }
            } else {
                this.getNavigation().moveTo(target, 1.2);
            }
            if (distance < (double) f + 1.0F && this.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
                this.setAnimation(this.getRandom().nextBoolean() ? ANIMATION_SCRATCH : ANIMATION_BITE);
                this.playSound(ACSoundRegistry.DEEP_ONE_KNIGHT_ATTACK.get());
            }
        }
        if (this.getAnimation() == ANIMATION_SCRATCH && (this.getAnimationTick() > 5 && this.getAnimationTick() < 9 || this.getAnimationTick() > 12 && this.getAnimationTick() < 16)) {
            this.checkAndDealMeleeDamage(target, 1.0F);
        }
        if (this.getAnimation() == ANIMATION_BITE && this.getAnimationTick() > 3 && this.getAnimationTick() <= 7) {
            this.checkAndDealMeleeDamage(target, 1.0F);
        }
    }

    protected void checkAndDealMeleeDamage(LivingEntity target, float multiplier) {
        this.checkAndDealMeleeDamage(target, multiplier, 1.0F);
    }

    protected void checkAndDealMeleeDamage(LivingEntity target, float multiplier, float knockback) {
        if (this.hasLineOfSight(target) && (double) this.distanceTo(target) < (double) (this.getBbWidth() + target.getBbWidth()) + 5.0) {
            float f = (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() * multiplier;
            target.hurt(this.damageSources().mobAttack(this), f);
            target.knockback(knockback * multiplier, this.getX() - target.getX(), this.getZ() - target.getZ());
            Entity entity = target.getVehicle();
            if (entity != null) {
                entity.setDeltaMovement(target.getDeltaMovement());
                entity.hurt(this.damageSources().mobAttack(this), f);
            }
        }
    }

    public void throwTrident(LivingEntity target) {
        ThrownTrident thrownTrident = new ThrownTrident(this.level(), this, new ItemStack(Items.TRIDENT));
        double d0 = target.getX() - this.getX();
        double d1 = target.getY(0.3333333333333333D) - thrownTrident.getY();
        double d2 = target.getZ() - this.getZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        thrownTrident.shoot(d0, d1 + d3 * (double) 0.2F, d2, 1.6F, (float) (14 - this.level().getDifficulty().getId() * 4));
        this.playSound(SoundEvents.DROWNED_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level().addFreshEntity(thrownTrident);
    }

    public void performOrtholanceDash(LivingEntity target) {
        Vec3 toTarget = new Vec3(target.getX() - this.getX(), 0.0D, target.getZ() - this.getZ());
        if (toTarget.lengthSqr() < 1.0E-4D) {
            toTarget = this.getLookAngle();
        }
        Vec3 direction = toTarget.normalize();
        this.dashDirection = direction;
        double dashSpeed = AttributesConfig.DeepOneKnightServantOrtholanceDashSpeed.get();
        double dist = toTarget.horizontalDistance();
        this.dashTicks = Mth.clamp((int) Math.ceil(dist / dashSpeed), 5, 20);
        this.getNavigation().stop();
        this.playSound(ACSoundRegistry.ORTHOLANCE_WAVE.get(), 4.0F, 1.0F);
        this.dealOrtholanceDashDamage(this.dashTicks * dashSpeed);
    }

    protected void dealOrtholanceDashDamage(double dashDistance) {
        AABB aabb = new AABB(this.position(), this.position().add(this.dashDirection.scale(dashDistance))).inflate(1.0D);
        DamageSource source = this.damageSources().mobAttack(this);
        float dashDamage = AttributesConfig.DeepOneKnightServantOrtholanceDashDamage.get().floatValue();
        for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, aabb)) {
            if (!entity.equals(this) && !MobUtil.areAllies(entity, this) && this.hasLineOfSight(entity)) {
                entity.hurt(source, dashDamage);
                entity.stopRiding();
            }
        }
    }

    public void performOrtholanceWaveAttack(LivingEntity target) {
        Vec3 toTarget = new Vec3(target.getX() - this.getX(), 0.0D, target.getZ() - this.getZ());
        if (toTarget.lengthSqr() < 1.0E-4D) {
            toTarget = this.getLookAngle();
        }
        Vec3 direction = toTarget.normalize();
        this.getNavigation().stop();
        if (this.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
            this.setAnimation(ANIMATION_THROW);
        }
        this.playSound(ACSoundRegistry.ORTHOLANCE_WAVE.get(), 4.0F, 1.0F);
        this.spawnOrtholanceWaves(direction);
    }

    protected void spawnOrtholanceWaves(Vec3 direction) {
        int maxWaves = AttributesConfig.DeepOneKnightServantOrtholanceWaveCount.get();
        float waveScale = AttributesConfig.DeepOneKnightServantOrtholanceWaveScale.get().floatValue();
        float baseYaw = -(float) (Mth.atan2(direction.x, direction.z) * (180.0D / Math.PI));
        for (int wave = 0; wave < maxWaves; wave++) {
            float f1 = (float) wave / (float) maxWaves;
            int lifespan = 3 + (int) ((1.0F - f1) * 3.0F);
            Vec3 waveCenterPos = this.position().add(direction.scale(f1 * 2.0F));
            DeepOneKnightServantWave leftWave = new DeepOneKnightServantWave(this.level(), this);
            leftWave.moveTo(waveCenterPos.x, this.getY(), waveCenterPos.z);
            leftWave.setLifespan(lifespan);
            leftWave.setWaveScale(waveScale);
            leftWave.setYRot(baseYaw + 60.0F - (float) (15 * wave));
            this.level().addFreshEntity(leftWave);

            DeepOneKnightServantWave rightWave = new DeepOneKnightServantWave(this.level(), this);
            rightWave.moveTo(waveCenterPos.x, this.getY(), waveCenterPos.z);
            rightWave.setLifespan(lifespan);
            rightWave.setWaveScale(waveScale);
            rightWave.setYRot(baseYaw - 60.0F + (float) (15 * wave));
            this.level().addFreshEntity(rightWave);
        }
    }

    public boolean isNoon() {
        String s = ChatFormatting.stripFormatting(this.getName().getString());
        return s != null && (s.toLowerCase().contains("noon") || s.toLowerCase().contains("stinkyfish"));
    }

    @Override
    protected void dropFromLootTable(DamageSource damageSource, boolean b) {
    }

    protected SoundEvent getSwimSound() {
        return SoundEvents.HOSTILE_SWIM;
    }

    protected SoundEvent getSwimSplashSound() {
        return SoundEvents.HOSTILE_SPLASH;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.soundsAngry() ? ACSoundRegistry.DEEP_ONE_KNIGHT_HOSTILE.get() : ACSoundRegistry.DEEP_ONE_KNIGHT_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.DEEP_ONE_KNIGHT_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.DEEP_ONE_KNIGHT_DEATH.get();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("FocusSummoned", this.isFocusSummoned());
        BlockPos altarPos = this.getLastAltarPos();
        if (altarPos != null) {
            compound.putInt("AltarX", altarPos.getX());
            compound.putInt("AltarY", altarPos.getY());
            compound.putInt("AltarZ", altarPos.getZ());
        }
        if (!this.swappedItem.isEmpty()) {
            compound.put("SwappedItem", this.swappedItem.save(new CompoundTag()));
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setFocusSummoned(compound.getBoolean("FocusSummoned"));
        if (compound.contains("AltarX") && compound.contains("AltarY") && compound.contains("AltarZ")) {
            this.setLastAltarPos(new BlockPos(compound.getInt("AltarX"), compound.getInt("AltarY"), compound.getInt("AltarZ")));
        }
        if (compound.contains("SwappedItem")) {
            this.swappedItem = ItemStack.of(compound.getCompound("SwappedItem"));
        }
    }

    @Override
    public int getAnimationTick() {
        return animationTick;
    }

    @Override
    public void setAnimationTick(int tick) {
        animationTick = tick;
    }

    @Override
    public Animation getAnimation() {
        return currentAnimation;
    }

    @Override
    public void setAnimation(Animation animation) {
        currentAnimation = animation;
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_THROW, ANIMATION_BITE, ANIMATION_SCRATCH, ANIMATION_TRADE};
    }

    public Animation getTradingAnimation() {
        return ANIMATION_TRADE;
    }

    public boolean isTrading() {
        return this.getAnimation() == this.getTradingAnimation();
    }

    public boolean isFocusSummoned() {
        return this.entityData.get(FOCUS_SUMMONED);
    }

    public void setFocusSummoned(boolean focusSummoned) {
        this.entityData.set(FOCUS_SUMMONED, focusSummoned);
    }

    public BlockPos getLastAltarPos() {
        return this.entityData.get(ALTAR_POS).orElse(null);
    }

    public void setLastAltarPos(BlockPos lastAltarPos) {
        this.entityData.set(ALTAR_POS, Optional.ofNullable(lastAltarPos));
    }

    public void swapItemsForAnimation(ItemStack item) {
        if (!this.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
            this.swappedItem = this.getItemInHand(InteractionHand.MAIN_HAND).copy();
        }
        this.setItemInHand(InteractionHand.MAIN_HAND, item);
    }

    public void restoreSwappedItem() {
        this.setItemInHand(InteractionHand.MAIN_HAND, this.swappedItem);
    }

    public SoundEvent getAdmireSound() {
        return ACSoundRegistry.DEEP_ONE_KNIGHT_ADMIRE.get();
    }

    private List<ItemStack> generateBarterLoot() {
        if (this.level() instanceof ServerLevel serverLevel && serverLevel.getServer() != null) {
            LootTable lootTable = serverLevel.getServer().getLootData().getLootTable(BARTER_LOOT);
            return lootTable.getRandomItems(new LootParams.Builder(serverLevel)
                    .withParameter(LootContextParams.THIS_ENTITY, this)
                    .create(LootContextParamSets.PIGLIN_BARTER));
        }
        return List.of();
    }

    public boolean isDeepOneSwimming() {
        return this.entityData.get(SWIMMING);
    }

    public void setDeepOneSwimming(boolean bool) {
        this.entityData.set(SWIMMING, bool);
    }

    public float getSwimAmount(float partialTick) {
        return this.prevSwimAmount + (this.swimAmount - this.prevSwimAmount) * partialTick;
    }

    public float getFishPitch(float partialTick) {
        return this.prevFishPitch + (this.fishPitch - this.prevFishPitch) * partialTick;
    }

    public boolean soundsAngry() {
        return this.entityData.get(SOUNDS_ANGRY);
    }

    public void setSoundsAngry(boolean angrySounding) {
        this.entityData.set(SOUNDS_ANGRY, angrySounding);
    }

    private class DeepOneNavigator extends SemiAquaticPathNavigator {

        public DeepOneNavigator(Level worldIn) {
            super(DeepOneKnightServant.this, worldIn);
        }

        @Override
        protected Vec3 getTempMobPos() {
            return new Vec3(this.mob.getX(), this.mob.getY(0.5), this.mob.getZ());
        }

        @Override
        protected double getGroundY(Vec3 vec3) {
            if (DeepOneKnightServant.this.isDeepOneSwimming() || !DeepOneKnightServant.this.isInWaterOrBubble()) {
                return super.getGroundY(vec3);
            }
            BlockPos blockpos = BlockPos.containing(vec3);
            return this.level.getFluidState(blockpos.below()).isEmpty() ? vec3.y : WalkNodeEvaluator.getFloorLevel(this.level, blockpos);
        }
    }

    private class KnightMeleeGoal extends Goal {

        private KnightMeleeGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = DeepOneKnightServant.this.getTarget();
            return target != null && target.isAlive() && !DeepOneKnightServant.this.isStaying() && !DeepOneKnightServant.this.isTrading();
        }

        @Override
        public void start() {
            super.start();
            DeepOneKnightServant.this.setSoundsAngry(true);
        }

        @Override
        public void stop() {
            super.stop();
            DeepOneKnightServant.this.setSoundsAngry(false);
        }

        @Override
        public void tick() {
            LivingEntity target = DeepOneKnightServant.this.getTarget();
            if (target == null) {
                return;
            }
            DeepOneKnightServant.this.getLookControl().setLookAt(target.getX(), target.getEyeY(), target.getZ(), 20.0F, DeepOneKnightServant.this.getMaxHeadXRot());
            DeepOneKnightServant.this.startAttackBehavior(target);
        }
    }
}
