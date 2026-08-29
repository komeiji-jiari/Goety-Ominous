package com.qiuyue.goetyominous.common.entities.ally.am;

import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.projectiles.FlyingItem;
import com.Polarice3.Goety.common.items.ModItems;
import com.Polarice3.Goety.config.ItemConfig;
import com.Polarice3.Goety.config.MobsConfig;
import com.github.alexthe666.alexsmobs.entity.ai.DirectPathNavigator;
import com.github.alexthe666.alexsmobs.entity.ai.FlightMoveController;
import com.github.alexthe666.alexsmobs.entity.ai.GroundPathNavigatorWide;
import com.github.alexthe666.alexsmobs.entity.util.Maths;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.misc.AMBlockPos;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;

import java.util.EnumSet;
import java.util.UUID;
import javax.annotation.Nullable;

import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.qiuyue.goetyominous.common.entities.projectile.EntityServantHemolymph;
import com.qiuyue.goetyominous.common.items.am.AmItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class WarpedMoscoServant extends Summoned implements IAnimatedEntity {

    public static final Animation ANIMATION_PUNCH_R = Animation.create(25);
    public static final Animation ANIMATION_PUNCH_L = Animation.create(25);
    public static final Animation ANIMATION_SLAM = Animation.create(35);
    public static final Animation ANIMATION_SUCK = Animation.create(60);
    public static final Animation ANIMATION_SPIT = Animation.create(60);
    private static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(WarpedMoscoServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> HAND_SIDE = SynchedEntityData.defineId(WarpedMoscoServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> UNHOLY_BLOOD = SynchedEntityData.defineId(WarpedMoscoServant.class, EntityDataSerializers.BOOLEAN);
    public float flyLeftProgress;
    public float prevLeftFlyProgress;
    public float flyRightProgress;
    public float prevFlyRightProgress;
    private int animationTick;
    private Animation currentAnimation;
    private boolean isLandNavigator;
    private int timeFlying;
    private int idleFlightTimeLimit = 0;
    private int loopSoundTick = 0;
    private boolean leechingFocusEnhancement;
    private int unholyBloodInvulnTime;
    private static final UUID UNHOLY_BLOOD_HEALTH_UUID = UUID.fromString("d4040404-0000-4000-8000-000000000001");
    private static final UUID UNHOLY_BLOOD_ATTACK_UUID = UUID.fromString("d4040404-0000-4000-8000-000000000002");
    private static final UUID LEECHING_FOCUS_HEALTH_UUID = UUID.fromString("d4040404-0000-4000-8000-000000000003");

    public WarpedMoscoServant(EntityType entityType, Level world) {
        super(entityType, world);
        switchNavigator(false);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.WarpedMoscoServantHealth.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.WarpedMoscoServantFollowRange.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.WarpedMoscoServantDamage.get())
                .add(Attributes.ARMOR, AttributesConfig.WarpedMoscoServantArmor.get())
                .add(Attributes.ARMOR_TOUGHNESS, AttributesConfig.WarpedMoscoServantArmorToughness.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, AttributesConfig.WarpedMoscoServantKnockbackResistance.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.WarpedMoscoServantMovementSpeed.get());
    }

    public MobType getMobType() {
        return MobType.ARTHROPOD;
    }

    @Override
    public int getMaxHeadYRot() {
        return 90;
    }

    public boolean hasUnholyBlood() {
        return this.entityData.get(UNHOLY_BLOOD);
    }

    public void setUnholyBlood(boolean value) {
        this.entityData.set(UNHOLY_BLOOD, value);
    }

    public boolean hasLeechingFocus() {
        return this.leechingFocusEnhancement;
    }

    public void setLeechingFocus(boolean value) {
        this.leechingFocusEnhancement = value;
    }

    private void addModIfMissing(AttributeInstance instance, UUID uuid, String name, double value) {
        if (instance != null && instance.getModifier(uuid) == null) {
            instance.addPermanentModifier(new AttributeModifier(uuid, name, value, AttributeModifier.Operation.ADDITION));
        }
    }

    private void applyEnhancementModifiers() {
        AttributeInstance health = this.getAttribute(Attributes.MAX_HEALTH);
        AttributeInstance attack = this.getAttribute(Attributes.ATTACK_DAMAGE);
        if (this.hasUnholyBlood()) {
            this.addModIfMissing(health, UNHOLY_BLOOD_HEALTH_UUID, "Unholy Blood Health", com.qiuyue.goetyominous.config.MobsConfig.WarpedMoscoUnholyBloodHealthBouns.get());
            this.addModIfMissing(attack, UNHOLY_BLOOD_ATTACK_UUID, "Unholy Blood Attack", com.qiuyue.goetyominous.config.MobsConfig.WarpedMoscoUnholyBloodDamageBouns.get());
        }
        if (this.hasLeechingFocus()) {
            this.addModIfMissing(health, LEECHING_FOCUS_HEALTH_UUID, "Leeching Focus Health", com.qiuyue.goetyominous.config.MobsConfig.WarpedMoscoLeechingFocusHealthBouns.get());
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("UnholyBloodEnhancement", this.hasUnholyBlood());
        compound.putBoolean("LeechingFocusEnhancement", this.leechingFocusEnhancement);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setUnholyBlood(compound.getBoolean("UnholyBloodEnhancement"));
        this.leechingFocusEnhancement = compound.getBoolean("LeechingFocusEnhancement");
        this.applyEnhancementModifiers();
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_21434_, DifficultyInstance p_21435_,
                                        MobSpawnType p_21436_, @Nullable SpawnGroupData p_21437_,
                                        @Nullable CompoundTag p_21438_) {
        if (p_21436_ == MobSpawnType.MOB_SUMMONED && this.getTrueOwner() instanceof Player player) {
            if (countServants(player) >= com.qiuyue.goetyominous.config.MobsConfig.WarpedMoscoServantLimit.get()) {
                return null;
            }
        }
        return super.finalizeSpawn(p_21434_, p_21435_, p_21436_, p_21437_, p_21438_);
    }

    private int countServants(Player player) {
        int count = 0;
        if (player.level() instanceof ServerLevel serverLevel) {
            for (Entity entity : serverLevel.getAllEntities()) {
                if (entity instanceof WarpedMoscoServant servant) {
                    if (servant.getTrueOwner() == player) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source.is(DamageTypes.FALL) || source.is(DamageTypes.DROWN) || source.is(DamageTypes.IN_WALL) || source.is(DamageTypes.LAVA) || source.is(DamageTypeTags.IS_FIRE) || super.isInvulnerableTo(source);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.hasUnholyBlood()) {
            if (this.unholyBloodInvulnTime > 0) {
                return false;
            }
            boolean inNether = this.level().dimension() == Level.NETHER;
            amount = inNether ? amount * 0.5F : amount * 0.85F;
            if (amount <= 0.0F) {
                return false;
            }
        }
        boolean flag = super.hurt(source, amount);
        if (flag && this.hasUnholyBlood()) {
            this.unholyBloodInvulnTime = 10;
        }
        return flag;
    }

    private static Animation getRandomAttack(RandomSource rand) {
        return switch (rand.nextInt(4)) {
            case 0 -> ANIMATION_PUNCH_L;
            case 1 -> ANIMATION_PUNCH_R;
            case 2 -> ANIMATION_SLAM;
            case 3 -> ANIMATION_SUCK;
            default -> ANIMATION_SUCK;
        };
    }

    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.WARPED_MOSCO_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.WARPED_MOSCO_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.WARPED_MOSCO_HURT.get();
    }

    @Override
    protected void tickDeath() {
        super.tickDeath();
        if (this.deathTime == 20 && !this.level().isClientSide() && this.getTrueOwner() != null) {
            ItemStack itemStack = new ItemStack(AmItems.WARPED_STEROIDS.get());
            FlyingItem flyingItem = new FlyingItem(ModEntityType.FLYING_ITEM.get(), this.level(), this.getX(), this.getY(), this.getZ());
            flyingItem.setOwner(this.getTrueOwner());
            flyingItem.setItem(itemStack);
            flyingItem.setParticle(ParticleTypes.WARPED_SPORE);

            flyingItem.setSecondsCool(ItemConfig.ReviveSecondsCool.get());
            this.level().addFreshEntity(flyingItem);
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(ModItems.UNHOLY_BLOOD.get()) && this.getMasterOwner() == player) {
            if (!this.hasUnholyBlood()) {
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                this.setUnholyBlood(true);
                this.applyEnhancementModifiers();
                this.heal(50.0F);
                this.playSound(AMSoundRegistry.WARPED_MOSCO_IDLE.get(), 1.0F, 1.0F);
                if (this.level() instanceof ServerLevel serverLevel) {
                    for (int k = 0; k < 60; ++k) {
                        float f2 = this.random.nextFloat() * 4.0F;
                        float f1 = this.random.nextFloat() * ((float) Math.PI * 2F);
                        double d1 = (double) (Mth.cos(f1) * f2);
                        double d2 = 0.01D + this.random.nextFloat() * 0.5D;
                        double d3 = (double) (Mth.sin(f1) * f2);
                        serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                                this.getX() + d1 * 0.1D, this.getY() + 0.3D, this.getZ() + d3 * 0.1D,
                                0, d1, d2, d3, 0.25F);
                    }
                }
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.CONSUME;
        }
        if (stack.is(ModItems.LEECHING_FOCUS.get()) && this.getMasterOwner() == player) {
            if (!this.hasLeechingFocus()) {
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                this.setLeechingFocus(true);
                this.applyEnhancementModifiers();
                this.playSound(AMSoundRegistry.WARPED_MOSCO_IDLE.get(), 1.0F, 1.0F);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.CONSUME;
        }
        float healAmount = 0.0F;
        if (stack.is(AMItemRegistry.BLOOD_SAC.get())) {
            healAmount = 20.0F;
        } else if (stack.is(AMItemRegistry.HEMOLYMPH_SAC.get())) {
            healAmount = 50.0F;
        }
        if (healAmount > 0.0F
                && this.getMasterOwner() == player
                && this.getHealth() < this.getMaxHealth()) {
            if (!this.level().isClientSide) {
                this.heal(healAmount);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                this.playSound(SoundEvents.ITEM_PICKUP, 1.0F, 1.0F);

                for (int i = 0; i < 7; ++i) {
                    double d0 = this.random.nextGaussian() * 0.02D;
                    double d1 = this.random.nextGaussian() * 0.02D;
                    double d2 = this.random.nextGaussian() * 0.02D;
                    ((ServerLevel) this.level()).sendParticles(ParticleTypes.HEART,
                            this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D),
                            1, d0, d1, d2, 0.5F);
                }
            }
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(0, new AttackGoal());
        this.goalSelector.addGoal(4, new AIWalkIdle());
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 15.0F, 1.0F));
    }

    @Override
    public void followGoal() {

        this.goalSelector.addGoal(2, new WarpedMoscoServant.FlyToOwnerGoal(this, 1.0D, 10.0F, 2.0F));
    }

    private void switchNavigator(boolean onLand) {
        if (onLand) {
            this.moveControl = new MoveControl(this);
            this.navigation = new GroundPathNavigatorWide(this, level());
            this.isLandNavigator = true;
        } else {
            this.moveControl = new FlightMoveController(this, 0.7F, false);
            this.navigation = new DirectPathNavigator(this, level());
            this.isLandNavigator = false;
        }
    }

    public static class FlyToOwnerGoal extends Goal {
        public final WarpedMoscoServant summonedEntity;
        public LivingEntity owner;
        public final double followSpeed;
        public int timeToRecalcPath;
        public final float stopDistance;
        public final float startDistance;

        public FlyToOwnerGoal(WarpedMoscoServant summonedEntity, double speed, float startDistance, float stopDistance) {
            this.summonedEntity = summonedEntity;
            this.followSpeed = speed;
            this.startDistance = startDistance;
            this.stopDistance = stopDistance;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

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

        public boolean canContinueToUse() {
            if (this.summonedEntity.getTarget() != null) {
                return false;
            } else if (this.summonedEntity.isPassenger()) {
                return false;
            } else if (this.owner == null || !this.owner.isAlive()) {
                return false;
            } else if (!this.summonedEntity.isFollowing() || this.summonedEntity.isCommanded()) {
                return false;
            } else if (!this.summonedEntity.isFlying() && this.summonedEntity.getNavigation().isDone()
                    && this.summonedEntity.distanceToSqr(this.owner) <= (double) (Mth.square(this.stopDistance + 2.0F))) {
                return false;
            } else {
                return !(this.summonedEntity.distanceToSqr(this.owner) <= (double) (Mth.square(this.stopDistance)));
            }
        }

        public void start() {
            this.timeToRecalcPath = 0;
        }

        public void stop() {
            this.owner = null;
            this.summonedEntity.getNavigation().stop();
            this.summonedEntity.getMoveControl().setWantedPosition(this.summonedEntity.getX(), this.summonedEntity.getY(), this.summonedEntity.getZ(), 0.0D);
        }

        public void tick() {
            if (this.owner != null) {
                this.summonedEntity.getLookControl().setLookAt(this.owner, 10.0F, (float) this.summonedEntity.getMaxHeadXRot());
                if (--this.timeToRecalcPath <= 0) {
                    this.timeToRecalcPath = 10;
                    boolean far = this.summonedEntity.distanceToSqr(this.owner) >= Mth.square(this.startDistance);
                    if (far || this.summonedEntity.isOverLiquid()) {
                        if (!this.summonedEntity.isFlying()) {
                            this.summonedEntity.setFlying(true);
                        }
                        double range = this.owner instanceof Mob ? 32.0D : 16.0D;
                        boolean flag = this.summonedEntity.distanceToSqr(this.owner) >= Mth.square(range);
                        if (this.owner instanceof Mob) {
                            flag |= !this.summonedEntity.hasLineOfSight(this.owner) && this.summonedEntity.distanceToSqr(this.owner) >= Mth.square(8.0D);
                        } else {
                            flag &= this.canTeleport();
                        }
                        if (flag) {
                            this.tryToTeleportNearEntity();
                        } else {

                            this.summonedEntity.getMoveControl().setWantedPosition(this.owner.getX(), this.owner.getY() + this.summonedEntity.getBbHeight() * 0.5F, this.owner.getZ(), this.followSpeed);
                        }
                    } else {
                        if (this.summonedEntity.isFlying()) {
                            this.summonedEntity.setFlying(false);
                        }
                        this.summonedEntity.getNavigation().moveTo(this.owner, this.followSpeed);
                    }
                }
            }
        }

        protected boolean canTeleport() {
            return MobsConfig.ServantTeleport.get();
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
                if (blockstate.getBlock() instanceof LeavesBlock) {
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

    @Override
    public void tryKill(Player player) {
        if (this.killChance <= 0) {
            this.warnKill(player);
        } else {
            super.tryKill(player);
        }
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FLYING, false);
        this.entityData.define(HAND_SIDE, true);
        this.entityData.define(UNHOLY_BLOOD, false);
    }

    public boolean causeFallDamage(float distance, float damageMultiplier) {
        return false;
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    public boolean isFlying() {
        return this.entityData.get(FLYING);
    }

    public void setFlying(boolean flying) {
        setDashRight(flying != this.isFlying() ? random.nextBoolean() : this.isDashRight());
        this.entityData.set(FLYING, flying);
    }

    public boolean isDashRight() {
        return this.entityData.get(HAND_SIDE);
    }

    public void setDashRight(boolean right) {
        this.entityData.set(HAND_SIDE, right);
    }

    public void tick() {
        super.tick();
        if (this.unholyBloodInvulnTime > 0) {
            --this.unholyBloodInvulnTime;
        }
        prevFlyRightProgress = flyRightProgress;
        prevLeftFlyProgress = flyLeftProgress;
        if (!this.level().isClientSide && this.isFlying()) {
            boolean allowFlight;
            if (this.isStaying()) {
                allowFlight = false;
            } else {
                LivingEntity owner = this.getTrueOwner();
                boolean followFar = this.isFollowing() && owner != null && owner.isAlive() && this.distanceToSqr(owner) >= Mth.square(10.0F);
                allowFlight = this.isOverLiquid() || this.getTarget() != null || followFar;
                if (!allowFlight && this.isWandering()) {
                    allowFlight = this.timeFlying < this.idleFlightTimeLimit;
                }
            }
            if (!allowFlight) {
                this.setFlying(false);
            }
        }
        final boolean dashRight = isDashRight();
        final boolean flying = isFlying();
        if (flying && dashRight && flyRightProgress < 5F) {
            flyRightProgress++;
        }
        if ((!flying || !dashRight) && flyRightProgress > 0F) {
            flyRightProgress--;
        }
        if (flying && !dashRight && flyLeftProgress < 5F) {
            flyLeftProgress++;
        }
        if ((!flying || dashRight) && flyLeftProgress > 0F) {
            flyLeftProgress--;
        }
        if (!this.level().isClientSide) {
            if (flying) {
                if (this.isLandNavigator)
                    switchNavigator(false);
            } else {
                if (!this.isLandNavigator)
                    switchNavigator(true);
            }
        }
        if (flying) {
            if (loopSoundTick == 0) {
                this.playSound(AMSoundRegistry.MOSQUITO_LOOP.get(), this.getSoundVolume(), this.getVoicePitch() * 0.3F);
            }
            loopSoundTick++;
            if (loopSoundTick > 100) {
                loopSoundTick = 0;
            }
            timeFlying++;
            this.setNoGravity(true);
            if (this.isPassenger() || this.isVehicle()) {
                this.setFlying(false);
            }
        } else {
            timeFlying = 0;
            this.setNoGravity(false);
        }
        if (this.horizontalCollision && net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level(), this)) {
            boolean flag = false;
            AABB axisalignedbb = this.getBoundingBox().inflate(0.2D);
            for (BlockPos blockpos : BlockPos.betweenClosed(Mth.floor(axisalignedbb.minX), Mth.floor(axisalignedbb.minY), Mth.floor(axisalignedbb.minZ), Mth.floor(axisalignedbb.maxX), Mth.floor(axisalignedbb.maxY), Mth.floor(axisalignedbb.maxZ))) {
                BlockState blockstate = this.level().getBlockState(blockpos);
                if (blockstate.is(AMTagRegistry.WARPED_MOSCO_BREAKABLES)) {
                    flag = this.level().destroyBlock(blockpos, true, this) || flag;
                }
            }
            if (!flag && this.onGround()) {
                this.jumpFromGround();
            }
        }

        LivingEntity target = this.getTarget();
        if (!this.level().isClientSide && target != null && this.isAlive()) {
            if (this.getAnimation() == ANIMATION_SUCK && this.getAnimationTick() == 3 && this.distanceTo(target) < 4.7F) {
                target.startRiding(this, true);
            }
            if (this.hasLeechingFocus() && this.getAnimation() == ANIMATION_SUCK
                    && this.getAnimationTick() > 0 && (this.getAnimationTick() - 10) % 10 == 0) {
                this.heal(this.getMaxHealth() * com.qiuyue.goetyominous.config.MobsConfig.WarpedMoscoLeechingFocusHeal.get() / 100.0F);
            }
            if (this.getAnimation() == ANIMATION_SLAM) {
                if (this.getAnimationTick() == 19) {
                    for (Entity entity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(5.0D))) {
                        if (!isAlliedTo(entity) && !(entity instanceof WarpedMoscoServant) && entity != this) {
                            entity.hurt(this.getServantAttack(), 10.0F + random.nextFloat() * 8.0F);
                            launch(entity, true);
                        }
                    }

                }
            }
            if ((this.getAnimation() == ANIMATION_PUNCH_R || this.getAnimation() == ANIMATION_PUNCH_L) && this.getAnimationTick() == 13) {
                if (this.distanceTo(target) < 4.7F) {
                    target.hurt(this.getServantAttack(), (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
                    knockbackRidiculous(target, 0.9F);
                }
            }
        }
        if (this.getAnimation() == ANIMATION_SLAM && this.getAnimationTick() == 19) {
            spawnGroundEffects();
        }

        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    public void spawnGroundEffects() {
        final float radius = 2.3F;
        final double extraY = 0.8F;
        for (int i = 0; i < 4; i++) {
            for (int i1 = 0; i1 < 20 + random.nextInt(12); i1++) {
                final double motionX = getRandom().nextGaussian() * 0.07D;
                final double motionY = getRandom().nextGaussian() * 0.07D;
                final double motionZ = getRandom().nextGaussian() * 0.07D;
                final float angle = (Maths.STARTING_ANGLE * this.yBodyRot) + i1;
                final double extraX = radius * Mth.sin(Mth.PI + angle);
                final double extraZ = radius * Mth.cos(angle);
                BlockPos ground = getMoscoGround(new BlockPos(Mth.floor(this.getX() + extraX), Mth.floor(this.getY() + extraY) - 1, Mth.floor(this.getZ() + extraZ)));
                BlockState state = this.level().getBlockState(ground);
                if (state.isSolid()) {
                    if (this.level().isClientSide) {
                        level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, state), true, this.getX() + extraX, ground.getY() + extraY, this.getZ() + extraZ, motionX, motionY, motionZ);
                    }
                }
            }
        }
    }

    private void launch(Entity e, boolean huge) {
        if (e.onGround()) {
            final double d0 = e.getX() - this.getX();
            final double d1 = e.getZ() - this.getZ();
            final double d2 = Math.max(d0 * d0 + d1 * d1, 0.001D);
            final float f = huge ? 2F : 0.5F;
            e.push(d0 / d2 * f, huge ? 0.5D : 0.2F, d1 / d2 * f);
        }
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
    public int getAnimationTick() {
        return animationTick;
    }

    @Override
    public void setAnimationTick(int i) {
        animationTick = i;
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_PUNCH_L, ANIMATION_PUNCH_R, ANIMATION_SLAM, ANIMATION_SUCK, ANIMATION_SPIT};
    }

    private static final byte EVENT_ANIM_PUNCH_R = 100;
    private static final byte EVENT_ANIM_PUNCH_L = 101;
    private static final byte EVENT_ANIM_SLAM = 102;
    private static final byte EVENT_ANIM_SUCK = 103;
    private static final byte EVENT_ANIM_SPIT = 104;

    private void setAttackAnimation(Animation animation) {
        if (this.getAnimation() == animation) {
            return;
        }
        this.setAnimation(animation);
        this.setAnimationTick(0);
        if (!this.level().isClientSide) {
            byte event;
            if (animation == ANIMATION_PUNCH_R) {
                event = EVENT_ANIM_PUNCH_R;
            } else if (animation == ANIMATION_PUNCH_L) {
                event = EVENT_ANIM_PUNCH_L;
            } else if (animation == ANIMATION_SLAM) {
                event = EVENT_ANIM_SLAM;
            } else if (animation == ANIMATION_SUCK) {
                event = EVENT_ANIM_SUCK;
            } else {
                event = EVENT_ANIM_SPIT;
            }
            this.level().broadcastEntityEvent(this, event);
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == EVENT_ANIM_PUNCH_R) {
            this.setAnimation(ANIMATION_PUNCH_R);
            this.setAnimationTick(0);
        } else if (id == EVENT_ANIM_PUNCH_L) {
            this.setAnimation(ANIMATION_PUNCH_L);
            this.setAnimationTick(0);
        } else if (id == EVENT_ANIM_SLAM) {
            this.setAnimation(ANIMATION_SLAM);
            this.setAnimationTick(0);
        } else if (id == EVENT_ANIM_SUCK) {
            this.setAnimation(ANIMATION_SUCK);
            this.setAnimationTick(0);
        } else if (id == EVENT_ANIM_SPIT) {
            this.setAnimation(ANIMATION_SPIT);
            this.setAnimationTick(0);
        } else {
            super.handleEntityEvent(id);
        }
    }

    private BlockPos getMoscoGround(BlockPos in) {
        BlockPos position = new BlockPos(in.getX(),
                (int) this.getY(),
                in.getZ());
        while (position.getY() > -62 && !level().getBlockState(position).isSolid() && level().getFluidState(position).isEmpty()) {
            position = position.below();
        }
        return position;
    }

    public Vec3 getBlockGrounding(Vec3 fleePos) {
        float radius = 0.75F * (0.7F * 6) * -3 - this.getRandom().nextInt(24);
        float neg = this.getRandom().nextBoolean() ? 1 : -1;
        float renderYawOffset = this.yBodyRot;
        float angle = (Maths.STARTING_ANGLE * renderYawOffset) + 3.15F + (this.getRandom().nextFloat() * neg);
        double extraX = radius * Mth.sin(Mth.PI + angle);
        double extraZ = radius * Mth.cos(angle);
        BlockPos radialPos = AMBlockPos.fromCoords(fleePos.x() + extraX, getY(), fleePos.z() + extraZ);
        BlockPos ground = this.getMoscoGround(radialPos);
        if (ground.getY() == -62) {
            return this.position();
        }
        if (!this.isTargetBlocked(Vec3.atCenterOf(ground.above()))) {
            return Vec3.atCenterOf(ground);
        }
        return null;
    }

    public Vec3 getBlockInViewAway(Vec3 fleePos, float radiusAdd) {
        float radius = 0.75F * (0.7F * 6) * -3 - this.getRandom().nextInt(24) - radiusAdd;
        float neg = this.getRandom().nextBoolean() ? 1 : -1;
        float renderYawOffset = this.yBodyRot;
        float angle = (Maths.STARTING_ANGLE * renderYawOffset) + 3.15F + (this.getRandom().nextFloat() * neg);
        double extraX = radius * Mth.sin(Mth.PI + angle);
        double extraZ = radius * Mth.cos(angle);
        BlockPos radialPos = new BlockPos((int) (fleePos.x() + extraX), 0, (int) (fleePos.z() + extraZ));
        BlockPos ground = getMoscoGround(radialPos);
        int distFromGround = (int) this.getY() - ground.getY();
        int flightHeight = 4 + this.getRandom().nextInt(10);
        BlockPos newPos = ground.above(distFromGround > 8 ? flightHeight : this.getRandom().nextInt(6) + 1);
        if (!this.isTargetBlocked(Vec3.atCenterOf(newPos)) && this.distanceToSqr(Vec3.atCenterOf(newPos)) > 1) {
            return Vec3.atCenterOf(newPos);
        }
        return null;
    }

    public void knockbackRidiculous(LivingEntity target, float power) {
        target.knockback(power, this.getX() - target.getX(), this.getZ() - target.getZ());
        float knockbackResist = (float) Mth.clamp((1.0D - this.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)), 0, 1);
        target.setDeltaMovement(target.getDeltaMovement().add(0, knockbackResist * power * 0.45F, 0));
    }

    public boolean isTargetBlocked(Vec3 target) {
        Vec3 Vector3d = new Vec3(this.getX(), this.getEyeY(), this.getZ());

        return this.level().clip(new ClipContext(Vector3d, target, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() != HitResult.Type.MISS;
    }

    private boolean isOverLiquid() {
        BlockPos position = this.blockPosition();
        while (position.getY() > 2 && level().isEmptyBlock(position)) {
            position = position.below();
        }
        return !level().getFluidState(position).isEmpty();
    }

    public void travel(Vec3 travelVector) {
        if ((this.getAnimation() == ANIMATION_SUCK || this.getAnimation() == ANIMATION_SLAM) && this.getAnimationTick() > 8) {
            if (this.getNavigation().getPath() != null) {
                this.getNavigation().stop();
            }
            travelVector = Vec3.ZERO;
            super.travel(travelVector);
            return;
        }
        super.travel(travelVector);
    }

    public void positionRider(Entity passenger, Entity.MoveFunction moveFunc) {
        super.positionRider(passenger, moveFunc);
        if (hasPassenger(passenger)) {
            int tick = 5;
            if (this.getAnimation() == ANIMATION_SUCK) {
                tick = this.getAnimationTick();
            } else {
                if (!this.level().isClientSide) {
                    passenger.stopRiding();
                }
            }
            float radius = 2F;
            float angle = (Maths.STARTING_ANGLE * this.yBodyRot);
            double extraX = radius * Mth.sin(Mth.PI + angle);
            double extraZ = radius * Mth.cos(angle);
            double extraY = tick < 10 ? 0 : 0.15F * Mth.clamp(tick - 10, 0, 15);
            passenger.setPos(this.getX() + extraX, this.getY() + extraY + 0.1F, this.getZ() + extraZ);
            if (!this.level().isClientSide && (tick - 10) % 4 == 0) {
                this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 1));
                passenger.hurt(this.getServantAttack(), (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
            }
        }
    }

    @Override
    public boolean canRiderInteract() {
        return true;
    }

    public boolean shouldRiderSit() {
        return false;
    }

    private void spit(LivingEntity target) {
        if (this.getAnimation() != ANIMATION_SPIT) {
            return;
        }
        this.lookAt(target, 100, 100);
        double d0 = target.getX() - this.getX();
        double d2 = target.getZ() - this.getZ();
        float yawToTarget = (float) (Mth.atan2(d2, d0) * (double) (180F / (float) Math.PI)) - 90.0F;
        this.setYRot(yawToTarget);
        this.yBodyRot = yawToTarget;
        this.yHeadRot = yawToTarget;
        for (int i = 0; i < 2 + random.nextInt(2); i++) {
            EntityServantHemolymph llamaspitentity = new EntityServantHemolymph(this.level(), this);
            d0 = target.getX() - this.getX();
            double d1 = target.getY(0.3333333333333333D) - llamaspitentity.getY();
            d2 = target.getZ() - this.getZ();
            float f = Mth.sqrt((float) (d0 * d0 + d2 * d2)) * 0.2F;
            llamaspitentity.shoot(d0, d1 + (double) f, d2, 1.5F, 5.0F);
            if (!this.isSilent()) {
                this.gameEvent(GameEvent.PROJECTILE_SHOOT);
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.LLAMA_SPIT, this.getSoundSource(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
            }
            this.level().addFreshEntity(llamaspitentity);
        }
    }

    private class AIWalkIdle extends Goal {
        protected final WarpedMoscoServant mosco;
        protected double x;
        protected double y;
        protected double z;
        private boolean flightTarget = false;

        public AIWalkIdle() {
            super();
            this.setFlags(EnumSet.of(Flag.MOVE));
            this.mosco = WarpedMoscoServant.this;
        }

        @Override
        public boolean canUse() {
            if (this.mosco.isVehicle() || (mosco.getTarget() != null && mosco.getTarget().isAlive()) || this.mosco.isPassenger()) {
                return false;
            } else if (mosco.isStaying()) {
                return false;
            } else if (mosco.isFollowing()) {
                return false;
            } else if (mosco.isGuardingArea()) {
                if (mosco.isCommanded()) {
                    return false;
                }
                if (mosco.isOverLiquid()) {
                    this.flightTarget = true;
                } else if (mosco.isFlying()) {
                    return false;
                } else {
                    this.flightTarget = false;
                    if (mosco.getRandom().nextInt(60) != 0) {
                        return false;
                    }
                }
                Vec3 guardPos = this.getPosition();
                if (guardPos == null) {
                    return false;
                }
                this.x = guardPos.x;
                this.y = guardPos.y;
                this.z = guardPos.z;
                return true;
            } else {
                if (this.mosco.getRandom().nextInt(30) != 0 && !mosco.isFlying()) {
                    return false;
                }
                if (mosco.isFlying()) {
                    this.flightTarget = true;
                } else {
                    this.flightTarget = mosco.isOverLiquid() || random.nextInt(8) == 0;
                    if (this.flightTarget) {
                        mosco.idleFlightTimeLimit = 60 + mosco.getRandom().nextInt(41);
                    }
                }
                Vec3 lvt_1_1_ = this.getPosition();
                if (lvt_1_1_ == null) {
                    return false;
                } else {
                    this.x = lvt_1_1_.x;
                    this.y = lvt_1_1_.y;
                    this.z = lvt_1_1_.z;
                    return true;
                }
            }
        }

        public void tick() {
            if (flightTarget) {
                mosco.getMoveControl().setWantedPosition(x, y, z, 1F);
            } else {
                this.mosco.getNavigation().moveTo(this.x, this.y, this.z, 1F);
            }
            if (!flightTarget && isFlying() && mosco.onGround()) {
                mosco.setFlying(false);
            }
            if (isFlying() && mosco.onGround() && mosco.timeFlying > 10) {
                mosco.setFlying(false);
            }
            if (isFlying() && mosco.isWandering()
                    && mosco.timeFlying >= mosco.idleFlightTimeLimit && !mosco.isOverLiquid()) {
                mosco.setFlying(false);
            }
        }

        @Nullable
        protected Vec3 getGuardHoverPos() {
            BlockPos bound = mosco.getBoundPos();
            if (bound == null) {
                return null;
            }
            Vec3 hover = mosco.vec3BoundPos().add(0, 4.0, 0);
            if (!mosco.isTargetBlocked(hover)) {
                return hover;
            }
            return null;
        }

        @Nullable
        protected Vec3 getGuardWanderPos() {
            BlockPos bound = mosco.getBoundPos();
            if (bound == null) {
                return null;
            }
            int safeRadius = Math.max(2, (int) (IServant.GUARDING_RANGE * 0.75F));
            if (mosco.distanceToSqr(mosco.vec3BoundPos()) > Mth.square(safeRadius)) {
                return null;
            }
            for (int i = 0; i < 8; ++i) {
                double angle = mosco.getRandom().nextDouble() * Math.PI * 2.0;
                double radius = 3.0 + mosco.getRandom().nextDouble() * Math.max(0.0, safeRadius - 3.0);
                double x = bound.getX() + 0.5 + Math.cos(angle) * radius;
                double z = bound.getZ() + 0.5 + Math.sin(angle) * radius;
                BlockPos ground = mosco.getMoscoGround(new BlockPos((int) x, (int) mosco.getY(), (int) z));
                if (ground.getY() == -62) {
                    continue;
                }
                Vec3 target = Vec3.atCenterOf(ground.above());
                if (!mosco.isTargetBlocked(target)) {
                    return target;
                }
            }
            return null;
        }

        @Nullable
        protected Vec3 getPosition() {
            Vec3 vector3d = mosco.position();

            if (mosco.isGuardingArea()) {
                if (flightTarget) {
                    return getGuardHoverPos();
                }
                return getGuardWanderPos();
            }

            if (mosco.isOverLiquid()) {
                flightTarget = true;
            }
            if (flightTarget) {
                if (mosco.timeFlying < mosco.idleFlightTimeLimit || mosco.isOverLiquid()) {
                    return mosco.getBlockInViewAway(vector3d, 0);
                } else {
                    return mosco.getBlockGrounding(vector3d);
                }
            } else {

                return LandRandomPos.getPos(this.mosco, 20, 7);
            }
        }

        public boolean canContinueToUse() {
            if (flightTarget) {
                return mosco.isFlying() && mosco.distanceToSqr(x, y, z) > 20F && !mosco.horizontalCollision;
            } else {
                return (!this.mosco.getNavigation().isDone()) && !this.mosco.isVehicle();
            }
        }

        public void start() {
            if (flightTarget) {
                mosco.setFlying(true);
                mosco.getMoveControl().setWantedPosition(x, y, z, 1F);
            } else {
                this.mosco.getNavigation().moveTo(this.x, this.y, this.z, 1F);
            }
        }

        public void stop() {
            this.mosco.getNavigation().stop();
            super.stop();
        }
    }

    private class AttackGoal extends Goal {
        private int upTicks = 0;
        private int dashCooldown = 0;
        private boolean ranged = false;
        private BlockPos farTarget = null;

        public AttackGoal() {
        }

        public boolean canUse() {
            return WarpedMoscoServant.this.getTarget() != null;
        }

        public void tick() {
            if (dashCooldown > 0) {
                dashCooldown--;
            }
            if (WarpedMoscoServant.this.getTarget() != null) {
                LivingEntity target = WarpedMoscoServant.this.getTarget();
                ranged = WarpedMoscoServant.this.shouldRangeAttack(target);
                boolean staying = WarpedMoscoServant.this.isStaying();
                if (!staying && (WarpedMoscoServant.this.isFlying() || ranged || WarpedMoscoServant.this.distanceTo(target) > 12 && !WarpedMoscoServant.this.isTargetBlocked(target.position().add(0, target.getBbHeight() * 0.6F, 0)))) {
                    float speedRush = 5F;
                    upTicks++;
                    WarpedMoscoServant.this.setFlying(true);
                    if (ranged) {
                        if (farTarget == null || WarpedMoscoServant.this.distanceToSqr(Vec3.atCenterOf(farTarget)) < Mth.square(WarpedMoscoServant.this.getBoundingBox().getSize() + 0.5F)) {
                            farTarget = this.getAvoidTarget(target);
                        }
                        if (farTarget != null) {
                            WarpedMoscoServant.this.getMoveControl().setWantedPosition(farTarget.getX(), farTarget.getY() + target.getEyeHeight() * 0.6F, farTarget.getZ(), 3D);
                        } else {
                            WarpedMoscoServant.this.getMoveControl().setWantedPosition(target.getX(), target.getY() + target.getEyeHeight() * 0.6F, target.getZ(), 5D);
                        }
                        WarpedMoscoServant.this.setAttackAnimation(ANIMATION_SPIT);
                        if(upTicks % 30 == 0){
                            WarpedMoscoServant.this.heal(1);
                        }
                        final int tick = WarpedMoscoServant.this.getAnimationTick();
                        switch (tick) {
                            case 10, 20, 30, 40 -> WarpedMoscoServant.this.spit(target);
                        }
                    } else {
                        WarpedMoscoServant.this.getMoveControl().setWantedPosition(target.getX(), target.getY() + target.getEyeHeight() * 0.6F, target.getZ(), speedRush);
                    }
                } else {
                    WarpedMoscoServant.this.getNavigation().moveTo(WarpedMoscoServant.this.getTarget(), 1.25F);
                }
                if (WarpedMoscoServant.this.isFlying()) {
                    if (WarpedMoscoServant.this.distanceTo(target) < 4.3F) {
                        if (dashCooldown == 0 || target.onGround() || target.isInLava() || target.isInWater()) {
                            target.hurt(WarpedMoscoServant.this.getServantAttack(), 5F);
                            WarpedMoscoServant.this.knockbackRidiculous(target, 1.0F);
                            dashCooldown = 30;
                        }
                    }
                    if (!ranged && !WarpedMoscoServant.this.isOverLiquid()) {
                        final float groundHeight = WarpedMoscoServant.this.getMoscoGround(WarpedMoscoServant.this.blockPosition()).getY();
                        boolean lowToGround = Math.abs(WarpedMoscoServant.this.getY() - groundHeight) < 3.0F;
                        boolean closeOrProlonged = WarpedMoscoServant.this.distanceTo(target) < 6.0F || upTicks > 40;
                        if (lowToGround && closeOrProlonged) {
                            WarpedMoscoServant.this.setFlying(false);
                        }
                    }
                } else {
                    if (WarpedMoscoServant.this.distanceTo(target) < 4F && WarpedMoscoServant.this.getAnimation() == NO_ANIMATION) {
                        Animation animation = getRandomAttack(random);
                        if (animation == ANIMATION_SUCK && target.isPassenger()) {
                            animation = ANIMATION_SLAM;
                        }
                        WarpedMoscoServant.this.setAttackAnimation(animation);
                    }
                }
            }
        }

        public BlockPos getAvoidTarget(LivingEntity target) {
            final double minDistSqr = Mth.square(8.0D);
            for (int i = 0; i < 10; ++i) {
                final float radius = 10 + WarpedMoscoServant.this.getRandom().nextInt(8);

                final float angle = (Maths.STARTING_ANGLE * (target.yHeadRot + 90F + WarpedMoscoServant.this.getRandom().nextInt(180)));
                final double extraX = radius * Mth.sin(Mth.PI + angle);
                final double extraZ = radius * Mth.cos(angle);
                BlockPos ground = AMBlockPos.fromCoords(target.getX() + extraX, target.getY() + 1, target.getZ() + extraZ);
                if (WarpedMoscoServant.this.distanceToSqr(Vec3.atCenterOf(ground)) > minDistSqr
                        && !WarpedMoscoServant.this.isTargetBlocked(Vec3.atCenterOf(ground))) {
                    return ground;
                }
            }
            for (int i = 0; i < 10; ++i) {
                final float radius = 8 + WarpedMoscoServant.this.getRandom().nextInt(4);
                final float angle = (Maths.STARTING_ANGLE * (WarpedMoscoServant.this.yBodyRot + 90F + WarpedMoscoServant.this.getRandom().nextInt(360)));
                final double extraX = radius * Mth.sin(Mth.PI + angle);
                final double extraZ = radius * Mth.cos(angle);
                BlockPos ground = new BlockPos((int) (WarpedMoscoServant.this.getX() + extraX), (int) WarpedMoscoServant.this.getY(), (int) (WarpedMoscoServant.this.getZ() + extraZ));
                if (WarpedMoscoServant.this.distanceToSqr(Vec3.atCenterOf(ground)) > minDistSqr
                        && !WarpedMoscoServant.this.isTargetBlocked(Vec3.atCenterOf(ground))) {
                    return ground;
                }
            }
            for (int lift = 10; lift <= 26; lift += 4) {
                BlockPos up = new BlockPos((int) WarpedMoscoServant.this.getX(), (int) (WarpedMoscoServant.this.getY() + lift), (int) WarpedMoscoServant.this.getZ());
                if (!WarpedMoscoServant.this.isTargetBlocked(Vec3.atCenterOf(up))) {
                    return up;
                }
            }
            return null;
        }

        public void stop() {
            upTicks = 0;
            dashCooldown = 0;
            ranged = false;
            farTarget = null;
        }
    }

    private boolean shouldRangeAttack(LivingEntity target) {
        if(this.getHealth() < Math.floor(this.getMaxHealth() * 0.25F)){
            return true;
        }

        return this.getHealth() < this.getMaxHealth() * 0.25F && this.distanceTo(target) > 10;
    }
}