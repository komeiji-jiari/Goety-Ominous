package com.qiuyue.goetyominous.common.entities.ally.ac;

import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.utils.MobUtil;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.block.AbyssalAltarBlock;
import com.github.alexmodguy.alexscaves.server.block.blockentity.AbyssalAltarBlockEntity;
import com.github.alexmodguy.alexscaves.server.entity.ai.AnimalRandomlySwimGoal;
import com.github.alexmodguy.alexscaves.server.entity.ai.SemiAquaticPathNavigator;
import com.github.alexmodguy.alexscaves.server.entity.ai.VerticalSwimmingMoveControl;
import com.github.alexmodguy.alexscaves.server.entity.living.DeepOneMageEntity;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.qiuyue.goetyominous.common.entities.ai.ac.DeepOneBarterGoal;
import com.qiuyue.goetyominous.common.entities.ai.ac.IDeepOneBarterer;
import com.qiuyue.goetyominous.common.entities.projectile.DeepOneMageServantWaterBolt;
import com.qiuyue.goetyominous.common.entities.projectile.DeepOneMageServantWave;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.qiuyue.goetyominous.config.MobsConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
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
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class DeepOneMageServant extends Summoned implements IDeepOneBarterer, IAnimatedEntity {

    public static final Animation ANIMATION_DISAPPEAR = DeepOneMageEntity.ANIMATION_DISAPPEAR;
    public static final Animation ANIMATION_ATTACK = DeepOneMageEntity.ANIMATION_ATTACK;
    public static final Animation ANIMATION_SPIN = DeepOneMageEntity.ANIMATION_SPIN;
    public static final Animation ANIMATION_TRADE = DeepOneMageEntity.ANIMATION_TRADE;
    public static final ResourceLocation BARTER_LOOT = new ResourceLocation("alexscaves", "gameplay/deep_one_mage_barter");

    private static final EntityDimensions SWIMMING_SIZE = new EntityDimensions(1.2F, 1.5F, false);

    private static final EntityDataAccessor<Boolean> SWIMMING = SynchedEntityData.defineId(DeepOneMageServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SOUNDS_ANGRY = SynchedEntityData.defineId(DeepOneMageServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> FOCUS_SUMMONED = SynchedEntityData.defineId(DeepOneMageServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Optional<BlockPos>> ALTAR_POS = SynchedEntityData.defineId(DeepOneMageServant.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);

    protected boolean isLandNavigator;
    private boolean hasSwimmingSize = false;
    private boolean isMageInWater = false;
    private float fishPitch = 0.0F;
    private float prevFishPitch = 0.0F;
    private float swimAmount = 0.0F;
    private float prevSwimAmount = 0.0F;
    private Animation currentAnimation;
    private int animationTick;
    private ItemStack swappedItem = ItemStack.EMPTY;
    private boolean spawnedLootItem = false;
    private int spinCooldown = 0;
    private int rangedCooldown = 0;
    private Vec3 strafeTarget = null;

    public DeepOneMageServant(EntityType<? extends Summoned> entityType, Level level) {
        super(entityType, level);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 0.0F);
        this.switchNavigator(false);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.DeepOneMageServantMovementSpeed.get())
                .add(Attributes.MAX_HEALTH, AttributesConfig.DeepOneMageServantHealth.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.DeepOneMageServantDamage.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.DeepOneMageServantFollowRange.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, AttributesConfig.DeepOneMageServantKnockbackResistance.get())
                .add(Attributes.ARMOR, AttributesConfig.DeepOneMageServantArmor.get());
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
            if (countServants(player) >= MobsConfig.DeepOneMageServantLimit.get()) {
                return null;
            }
        }
        return super.finalizeSpawn(levelAccessor, difficulty, spawnType, spawnGroupData, tag);
    }

    private int countServants(Player player) {
        int count = 0;
        if (player.level() instanceof ServerLevel serverLevel) {
            for (Entity entity : serverLevel.getAllEntities()) {
                if (entity instanceof DeepOneMageServant servant && servant != this) {
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
            if (countServants(player) >= MobsConfig.DeepOneMageServantLimit.get()) {
                this.discard();
            }
        }
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new MageAttackGoal());
        this.goalSelector.addGoal(1, new DeepOneBarterGoal(this));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 16.0F));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(6, new AnimalRandomlySwimGoal(this, 12, 18, 18, 1.0D));
        this.goalSelector.addGoal(6, new RandomStrollGoal(this, 2.0D, 45, false) {
            @Override
            public boolean canUse() {
                return !DeepOneMageServant.this.isInWaterOrBubble() && !DeepOneMageServant.this.isStaying() && super.canUse() && DeepOneMageServant.this.getAnimation() != DeepOneMageServant.ANIMATION_TRADE;
            }

            @Override
            public boolean canContinueToUse() {
                return !DeepOneMageServant.this.isInWaterOrBubble() && !DeepOneMageServant.this.isStaying() && DeepOneMageServant.this.getAnimation() != DeepOneMageServant.ANIMATION_TRADE && super.canContinueToUse();
            }

            @Override
            protected Vec3 getPosition() {
                if (DeepOneMageServant.this.isGuardingArea()) {
                    BlockPos bound = DeepOneMageServant.this.getBoundPos();
                    if (bound != null) {
                        int range = IServant.GUARDING_RANGE / 2;
                        for (int i = 0; i < 10; ++i) {
                            int dx = DeepOneMageServant.this.getRandom().nextInt(2 * range + 1) - range;
                            int dz = DeepOneMageServant.this.getRandom().nextInt(2 * range + 1) - range;
                            BlockPos probe = bound.offset(dx, 0, dz);
                            int y = probe.getY();
                            while (y > DeepOneMageServant.this.level().getMinBuildHeight()
                                    && DeepOneMageServant.this.level().getBlockState(probe.atY(y)).isAir()) {
                                y--;
                            }
                            if (y < probe.getY()) {
                                return Vec3.atBottomCenterOf(probe.atY(y + 2));
                            }
                        }
                        int y = bound.getY();
                        while (y > DeepOneMageServant.this.level().getMinBuildHeight()
                                && DeepOneMageServant.this.level().getBlockState(bound.atY(y)).isAir()) {
                            y--;
                        }
                        if (y < bound.getY()) {
                            return Vec3.atBottomCenterOf(bound.atY(y + 2));
                        }
                    }
                }
                Vec3 prev = super.getPosition();
                if (prev != null) {
                    return prev.add(1, 2, 1);
                }
                BlockPos start = DeepOneMageServant.this.blockPosition();
                int startY = start.getY();
                for (int i = 0; i < 10; ++i) {
                    int dx = DeepOneMageServant.this.getRandom().nextInt(17) - 8;
                    int dz = DeepOneMageServant.this.getRandom().nextInt(17) - 8;
                    BlockPos probe = start.offset(dx, 0, dz);
                    int y = startY;
                    while (y > DeepOneMageServant.this.level().getMinBuildHeight()
                            && DeepOneMageServant.this.level().getBlockState(probe.atY(y)).isAir()) {
                        y--;
                    }
                    if (y < startY) {
                        return Vec3.atBottomCenterOf(probe.atY(y + 2));
                    }
                }
                return null;
            }
        });
    }

    @Override
    public void followGoal() {
        this.goalSelector.addGoal(5, new MageFollowOwnerGoal(this, 1.0D, 10.0F, 4.0F));
    }

    protected void switchNavigator(boolean onLand) {
        if (onLand) {
            if (this.isMageInWater) {
                this.setDeltaMovement(this.getDeltaMovement().add(1.0F, 0.1F, 1.0F));
            }
            this.navigation = createFlightNavigation(level());
            this.moveControl = new FlightMoveController();
            this.isLandNavigator = true;
        } else {
            this.navigation = createNavigation(level());
            this.moveControl = new VerticalSwimmingMoveControl(this, 0.8F, 10);
            this.isLandNavigator = false;
        }
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new DeepOneNavigator(level);
    }

    protected PathNavigation createFlightNavigation(Level level) {
        FlyingPathNavigation flyingPathNavigation = new FlyingPathNavigation(this, level) {
            @Override
            public boolean isStableDestination(BlockPos pos) {
                return !this.level.getBlockState(pos.below()).isAir();
            }
        };
        flyingPathNavigation.setCanOpenDoors(false);
        flyingPathNavigation.setCanFloat(false);
        flyingPathNavigation.setCanPassDoors(true);
        return flyingPathNavigation;
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
        return MobsConfig.DeepOneMageServantLimit.get();
    }

    @Override
    public Predicate<Entity> summonPredicate() {
        return entity -> entity instanceof DeepOneMageServant;
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader level) {
        return level.getBlockState(pos).isAir() ? 10.0F : super.getWalkTargetValue(pos, level);
    }

    @Override
    public void tick() {
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
        float pitchTarget;
        if (this.isDeepOneSwimming()) {
            pitchTarget = (float) this.getDeltaMovement().y;
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
        if (!this.isInWaterOrBubble() && !this.hasEffect(ACEffectRegistry.BUBBLED.get())) {
            this.addEffect(new MobEffectInstance(ACEffectRegistry.BUBBLED.get(), 200));
        }
        if (this.isInWaterOrBubble() && this.hasEffect(ACEffectRegistry.BUBBLED.get())) {
            this.removeEffect(ACEffectRegistry.BUBBLED.get());
        }
        this.isMageInWater = this.isInWaterOrBubble();
        if (this.getAnimation() == ANIMATION_SPIN) {
            this.setDeltaMovement(this.getDeltaMovement().scale(0.6F));
            LivingEntity target = this.getTarget();
            if (target != null && !this.isStaying()) {
                Vec3 away = this.position().subtract(target.position());
                if (away.lengthSqr() < 1.0E-4D) {
                    away = new Vec3(this.getRandom().nextFloat() - 0.5F, 0.0F, this.getRandom().nextFloat() - 0.5F);
                }
                if (this.getAnimationTick() < 20) {
                    this.setDeltaMovement(this.getDeltaMovement().add(away.normalize().scale(0.12)));
                }
            }
            if (this.getAnimationTick() % 6 == 0) {
                AABB bashBox = this.getBoundingBox().inflate(2.0D, 0, 2.0D);
                for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, bashBox)) {
                    if (entity != this && !MobUtil.areAllies(entity, this) && !(entity instanceof DeepOneMageServant)) {
                        checkAndDealMeleeDamage(entity, 0.4F, 1.0F);
                    }
                }
            }
        }
        if (this.getAnimation() == ANIMATION_ATTACK) {
            LivingEntity target = this.getTarget();
            if (target != null && target.isAlive()) {
                if (this.getAnimationTick() == 16) {
                    useMagicAttack(target);
                } else if (this.getAnimationTick() < 16) {
                    this.level().broadcastEntityEvent(this, (byte) 68);
                }
                this.getLookControl().setLookAt(target.getX(), target.getEyeY(), target.getZ(), 180.0F, 10.0F);
            }
        }
        if (spinCooldown > 0) {
            spinCooldown--;
        }
        if (rangedCooldown > 0) {
            rangedCooldown--;
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
    public boolean isNoGravity() {
        return !this.isDeepOneSwimming() || super.isNoGravity();
    }

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    @Override
    public float getStepHeight() {
        return 1.3F;
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
        this.yBodyRot = this.getYRot();
        double distance = this.distanceTo(target);
        float f = this.getBbWidth() + target.getBbWidth();
        if (this.isStaying()) {
            if (this.isTargetPointBlank(target) && this.spinCooldown <= 0 && this.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
                this.setAnimation(ANIMATION_SPIN);
                this.spinCooldown = 1000 + this.random.nextInt(60);
            }
            if (distance < 30.0D && this.rangedCooldown <= 0
                    && this.getAnimation() == IAnimatedEntity.NO_ANIMATION && this.hasLineOfSight(target)) {
                this.setAnimation(ANIMATION_ATTACK);
                this.playSound(ACSoundRegistry.DEEP_ONE_MAGE_ATTACK.get());
                this.rangedCooldown = 30 + this.random.nextInt(20);
            }
            this.getLookControl().setLookAt(target.getX(), target.getEyeY(), target.getZ(), 30.0F, 10.0F);
            return;
        }
        if (distance > 16 + f) {
            this.getNavigation().moveTo(target, 1.2);
        } else if (this.isTargetPointBlank(target) && spinCooldown <= 0) {
            if (this.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
                this.setAnimation(ANIMATION_SPIN);
                spinCooldown = 1000 + random.nextInt(60);
            }
        } else {
            if (strafeTarget == null || strafeTarget.distanceTo(this.position()) < 4) {
                Vec3 possible = null;
                float baseAngle = (float) Mth.atan2(this.getZ() - target.getZ(), this.getX() - target.getX());
                for (int i = 0; i < 8; ++i) {
                    float angle = baseAngle + (random.nextFloat() * 2.0F - 1.0F) * (70.0F * Mth.DEG_TO_RAD);
                    float radius = 8.0F + random.nextFloat() * 8.0F;
                    Vec3 candidate = target.position().add(Math.cos(angle) * radius, random.nextInt(2), Math.sin(angle) * radius);
                    if (!isTargetBlocked(candidate)) {
                        possible = candidate;
                        break;
                    }
                }
                if (possible != null) {
                    strafeTarget = possible;
                } else if (strafeTarget == null) {
                    strafeTarget = target.position().add(Math.cos(baseAngle) * 14.0F, 0.0F, Math.sin(baseAngle) * 14.0F);
                }
            } else {
                this.getNavigation().moveTo(strafeTarget.x, strafeTarget.y, strafeTarget.z, 1.5F);
            }
            if (rangedCooldown <= 0 && this.getAnimation() == IAnimatedEntity.NO_ANIMATION && hasLineOfSight(target)) {
                this.setAnimation(ANIMATION_ATTACK);
                this.playSound(ACSoundRegistry.DEEP_ONE_MAGE_ATTACK.get());
                rangedCooldown = 30 + random.nextInt(20);
            }
            this.getLookControl().setLookAt(target.getX(), target.getEyeY(), target.getZ(), 30.0F, 10.0F);
        }
    }

    public void useMagicAttack(LivingEntity target) {
        this.level().broadcastEntityEvent(this, (byte) 68);
        if (random.nextBoolean()) {
            int lifespan = (int) (Math.floor(this.distanceTo(target))) + 10;
            Vec3 vec3 = target.position().subtract(this.position());
            for (int i = -2; i <= 2; i++) {
                DeepOneMageServantWave waveEntity = new DeepOneMageServantWave(this.level(), this);
                waveEntity.setPos(this.getX(), target.getY(), this.getZ());
                waveEntity.setLifespan(lifespan);
                waveEntity.setYRot(-(float) (Mth.atan2(vec3.x, vec3.z) * (double) (180F / (float) Math.PI)) + (i * 10));
                this.level().addFreshEntity(waveEntity);
            }
        } else {
            DeepOneMageServantWaterBolt waterBolt = new DeepOneMageServantWaterBolt(this.level(), this);
            double d0 = target.getX() - this.getX();
            double d1 = target.getY(0.3333333333333333D) - waterBolt.getY();
            double d2 = target.getZ() - this.getZ();
            double d3 = Math.sqrt(d0 * d0 + d2 * d2);
            waterBolt.setBubbling(this.random.nextInt(2) == 0);
            waterBolt.setArcingTowards(target.getUUID());
            waterBolt.shoot(d0, d1 + d3 * 0.67D, d2, 0.6F, 30F);
            this.level().addFreshEntity(waterBolt);
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

    private boolean isTargetBlocked(Vec3 target) {
        Vec3 eyePos = new Vec3(this.getX(), this.getEyeY(), this.getZ());
        return this.level().clip(new ClipContext(eyePos, target, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() != HitResult.Type.MISS;
    }

    private boolean isTargetPointBlank(LivingEntity target) {
        double dx = target.getX() - this.getX();
        double dz = target.getZ() - this.getZ();
        float f = this.getBbWidth() + target.getBbWidth();
        return dx * dx + dz * dz < Mth.square(f * 0.5F + 0.3F);
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
        return this.soundsAngry() ? ACSoundRegistry.DEEP_ONE_MAGE_HOSTILE.get() : ACSoundRegistry.DEEP_ONE_MAGE_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.DEEP_ONE_MAGE_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.DEEP_ONE_MAGE_DEATH.get();
    }

    @Override
    public void handleEntityEvent(byte b) {
        if (b == 66) {
            for (int i = 0; i < 2 + random.nextInt(4); i++) {
                this.level().addParticle(random.nextBoolean() ? ACParticleRegistry.DEEP_ONE_MAGIC.get() : ParticleTypes.DOLPHIN, this.getRandomX(1F), this.getRandomY(), this.getRandomZ(1F), 0F, -0.1F, 0F);
            }
        } else if (b == 67) {
            for (int i = 0; i < 13 + random.nextInt(6); i++) {
                this.level().addParticle(ACParticleRegistry.DEEP_ONE_MAGIC.get(), this.getRandomX(1F), this.getRandomY(), this.getRandomZ(1F), random.nextFloat() - 0.5F, random.nextFloat() - 0.5F, random.nextFloat() - 0.5F);
                this.level().addParticle(ParticleTypes.NAUTILUS, this.getRandomX(1F), this.getRandomY() + 1, this.getRandomZ(1F), random.nextFloat() - 0.5F, random.nextFloat() - 0.5F, random.nextFloat() - 0.5F);
            }
        } else if (b == 68) {
            Vec3 deltaPos = this.position().add(getDeltaMovement());
            Vec3 rVec = new Vec3(0.65F, this.getBbHeight() * 0.5F + 0.15F, 0.2F).xRot(-this.getXRot() * ((float) Math.PI / 180F)).yRot(-this.getYHeadRot() * ((float) Math.PI / 180F)).add(deltaPos);
            Vec3 lVec = new Vec3(-0.65F, this.getBbHeight() * 0.5F + 0.15F, 0.2F).xRot(-this.getXRot() * ((float) Math.PI / 180F)).yRot(-this.getYHeadRot() * ((float) Math.PI / 180F)).add(deltaPos);
            this.level().addParticle(ACParticleRegistry.DEEP_ONE_MAGIC.get(), rVec.x + (random.nextFloat() - 0.5F) * 0.1F, rVec.y + (random.nextFloat() - 0.5F) * 0.1F, rVec.z + (random.nextFloat() - 0.5F) * 0.1F, (random.nextFloat() - 0.5F) * 0.3F + getDeltaMovement().x, 1, (random.nextFloat() - 0.5F) * 0.3F + getDeltaMovement().z);
            this.level().addParticle(ACParticleRegistry.DEEP_ONE_MAGIC.get(), lVec.x + (random.nextFloat() - 0.5F) * 0.1F, lVec.y + (random.nextFloat() - 0.5F) * 0.1F, lVec.z + (random.nextFloat() - 0.5F) * 0.1F, (random.nextFloat() - 0.5F) * 0.3F + getDeltaMovement().x, 1, (random.nextFloat() - 0.5F) * 0.3F + getDeltaMovement().z);
        } else {
            super.handleEntityEvent(b);
        }
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
        return new Animation[]{ANIMATION_DISAPPEAR, ANIMATION_ATTACK, ANIMATION_SPIN, ANIMATION_TRADE};
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
        return ACSoundRegistry.DEEP_ONE_MAGE_ADMIRE.get();
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
        return this.isMageInWater && !this.onGround();
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
            super(DeepOneMageServant.this, worldIn);
        }

        @Override
        protected Vec3 getTempMobPos() {
            return new Vec3(this.mob.getX(), this.mob.getY(0.5), this.mob.getZ());
        }

        @Override
        protected double getGroundY(Vec3 vec3) {
            if (DeepOneMageServant.this.isDeepOneSwimming() || !DeepOneMageServant.this.isInWaterOrBubble()) {
                return super.getGroundY(vec3);
            }
            BlockPos blockpos = BlockPos.containing(vec3);
            return this.level.getFluidState(blockpos.below()).isEmpty() ? vec3.y : WalkNodeEvaluator.getFloorLevel(this.level, blockpos);
        }
    }

    private class MageAttackGoal extends Goal {

        private MageAttackGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = DeepOneMageServant.this.getTarget();
            return target != null && target.isAlive() && !DeepOneMageServant.this.isTrading();
        }

        @Override
        public void start() {
            super.start();
            DeepOneMageServant.this.setSoundsAngry(true);
        }

        @Override
        public void stop() {
            super.stop();
            DeepOneMageServant.this.setSoundsAngry(false);
        }

        @Override
        public void tick() {
            LivingEntity target = DeepOneMageServant.this.getTarget();
            if (target == null) {
                return;
            }
            DeepOneMageServant.this.getLookControl().setLookAt(target.getX(), target.getEyeY(), target.getZ(), 20.0F, DeepOneMageServant.this.getMaxHeadXRot());
            DeepOneMageServant.this.startAttackBehavior(target);
        }
    }

    public class MageFollowOwnerGoal extends Goal {
        private final DeepOneMageServant summonedEntity;
        private LivingEntity owner;
        private final double followSpeed;
        private final float startDistance;
        private final float stopDistance;
        private int timeToRecalcPath;

        public MageFollowOwnerGoal(DeepOneMageServant summonedEntity, double speed, float startDistance, float stopDistance) {
            this.summonedEntity = summonedEntity;
            this.followSpeed = speed;
            this.startDistance = startDistance;
            this.stopDistance = stopDistance;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity livingentity = this.summonedEntity.getTrueOwner();
            if (livingentity == null) {
                return false;
            } else if (livingentity.isSpectator()) {
                return false;
            } else if (this.summonedEntity.isPassenger()) {
                return false;
            } else if (this.summonedEntity.distanceToSqr(livingentity) < (double) (Mth.square(this.startDistance))) {
                return false;
            } else if (!this.summonedEntity.isFollowing() || this.summonedEntity.isCommanded()) {
                return false;
            } else if (this.summonedEntity.getTarget() != null) {
                return false;
            } else {
                this.owner = livingentity;
                return true;
            }
        }

        @Override
        public boolean canContinueToUse() {
            if (this.summonedEntity.getTarget() != null) {
                return false;
            } else if (this.summonedEntity.isPassenger()) {
                return false;
            } else if (this.owner == null || !this.owner.isAlive()) {
                return false;
            } else if (!this.summonedEntity.isFollowing() || this.summonedEntity.isCommanded()) {
                return false;
            } else {
                return !(this.summonedEntity.distanceToSqr(this.owner) <= (double) (Mth.square(this.stopDistance)));
            }
        }

        @Override
        public void start() {
            this.timeToRecalcPath = 0;
        }

        @Override
        public void stop() {
            this.owner = null;
            this.summonedEntity.getNavigation().stop();
        }

        @Override
        public void tick() {
            if (this.owner != null) {
                this.summonedEntity.getLookControl().setLookAt(this.owner, 10.0F, (float) this.summonedEntity.getMaxHeadXRot());
                if (--this.timeToRecalcPath <= 0) {
                    this.timeToRecalcPath = 10;
                    if (this.canTeleport() && this.summonedEntity.distanceToSqr(this.owner) >= 144.0D) {
                        this.tryToTeleportNearEntity();
                    } else {
                        this.summonedEntity.getNavigation().moveTo(this.owner, this.followSpeed);
                    }
                }
            }
        }

        protected boolean canTeleport() {
            return com.Polarice3.Goety.config.MobsConfig.ServantTeleport.get();
        }

        protected void tryToTeleportNearEntity() {
            BlockPos blockpos = this.owner.blockPosition();
            for (int i = 0; i < 10; ++i) {
                int j = this.getRandomNumber(-3, 3);
                int k = this.getRandomNumber(-1, 1);
                int l = this.getRandomNumber(-3, 3);
                boolean flag = this.tryToTeleportToLocation(blockpos.getX() + j, blockpos.getY() + k, blockpos.getZ() + l);
                if (flag) {
                    return;
                }
            }
        }

        protected boolean tryToTeleportToLocation(int x, int y, int z) {
            if (Math.abs((double) x - this.owner.getX()) < 2.0D && Math.abs((double) z - this.owner.getZ()) < 2.0D) {
                return false;
            } else if (!this.isTeleportFriendlyBlock(new BlockPos(x, y, z))) {
                return false;
            } else {
                this.summonedEntity.moveTo((double) x + 0.5D, (double) y, (double) z + 0.5D, this.summonedEntity.getYRot(), this.summonedEntity.getXRot());
                this.summonedEntity.getMoveControl().setWantedPosition(this.summonedEntity.getX(), this.summonedEntity.getY(), this.summonedEntity.getZ(), 0.0D);
                return true;
            }
        }

        protected boolean isTeleportFriendlyBlock(BlockPos pos) {
            BlockPathTypes pathnodetype = WalkNodeEvaluator.getBlockPathTypeStatic(this.summonedEntity.level(), pos.mutable());
            if (pathnodetype != BlockPathTypes.WALKABLE) {
                return false;
            } else {
                BlockState blockstate = this.summonedEntity.level().getBlockState(pos.below());
                if (blockstate.is(BlockTags.LEAVES)) {
                    return false;
                } else {
                    BlockPos blockpos = pos.subtract(this.summonedEntity.blockPosition());
                    return this.summonedEntity.level().noCollision(this.summonedEntity, this.summonedEntity.getBoundingBox().move(blockpos));
                }
            }
        }

        protected int getRandomNumber(int min, int max) {
            return this.summonedEntity.getRandom().nextInt(max - min + 1) + min;
        }
    }

    class FlightMoveController extends MoveControl {
        private final Mob parentEntity;

        public FlightMoveController() {
            super(DeepOneMageServant.this);
            this.parentEntity = DeepOneMageServant.this;
        }

        @Override
        public void tick() {
            parentEntity.setDeltaMovement(parentEntity.getDeltaMovement().add(0, Math.sin(tickCount * 0.1) * 0.005F, 0));
            if (this.operation == MoveControl.Operation.MOVE_TO) {
                Vec3 vector3d = new Vec3(this.wantedX - parentEntity.getX(), this.wantedY - parentEntity.getY(), this.wantedZ - parentEntity.getZ());
                double d0 = vector3d.length();
                double width = parentEntity.getBoundingBox().getSize();
                LivingEntity attackTarget = parentEntity.getTarget();
                Vec3 vector3d1 = vector3d.scale(this.speedModifier * 0.025D / d0);
                parentEntity.setDeltaMovement(parentEntity.getDeltaMovement().add(vector3d1));
                if (d0 < width * 0.3F) {
                    this.operation = Operation.WAIT;
                } else if (d0 >= width && attackTarget == null) {
                    if (DeepOneMageServant.this.getTarget() != null) {
                        parentEntity.yBodyRot = parentEntity.getYRot();
                    } else {
                        parentEntity.setYRot(-((float) Mth.atan2(vector3d1.x, vector3d1.z)) * (180F / (float) Math.PI));
                    }
                }
            }
        }
    }
}
