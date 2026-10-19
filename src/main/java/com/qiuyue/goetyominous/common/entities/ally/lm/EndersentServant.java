package com.qiuyue.goetyominous.common.entities.ally.lm;

import com.Polarice3.Goety.client.particles.CircleExplodeParticleOption;
import com.Polarice3.Goety.client.particles.MagicSmokeParticleOption;
import com.Polarice3.Goety.client.particles.ModParticleTypes;
import com.Polarice3.Goety.client.particles.SphereExplodeParticleOption;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.ender.AbstractEnderling;
import com.Polarice3.Goety.common.entities.projectiles.FlyingItem;
import com.Polarice3.Goety.common.entities.util.CameraShake;
import com.Polarice3.Goety.common.network.ModNetwork;
import com.Polarice3.Goety.common.network.server.SInstaLookPacket;
import com.Polarice3.Goety.init.ModSounds;
import com.Polarice3.Goety.utils.BlockFinder;
import com.Polarice3.Goety.utils.ColorUtil;
import com.Polarice3.Goety.utils.MathHelper;
import com.Polarice3.Goety.utils.MobUtil;
import com.Polarice3.Goety.utils.ModDamageSource;
import com.Polarice3.Goety.utils.ServerParticleUtil;
import com.qiuyue.goetyominous.compat.mod.LegendaryMonstersCompat;
import com.qiuyue.goetyominous.common.entities.ai.MovementBodyRotationControl;
import com.qiuyue.goetyominous.common.init.lm.LmEntityRegistry;
import net.miauczel.legendary_monsters.effect.ModEffects;
import net.miauczel.legendary_monsters.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityTeleportEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import com.qiuyue.goetyominous.config.lm.LmConfig;

public class EndersentServant extends AbstractEnderling implements LmServantDamageMultiplier {

    private static final EntityDataAccessor<Byte> DATA_FLAGS_ID =
            SynchedEntityData.defineId(EndersentServant.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Integer> ANIM_STATE =
            SynchedEntityData.defineId(EndersentServant.class, EntityDataSerializers.INT);
    private static final UUID TELEPORT_ATTACK_MODIFIER_UUID = UUID.fromString("0134e91f-a8c4-4d38-94d6-7201dae924b7");
    private static final AttributeModifier TELEPORT_ATTACK_MODIFIER =
            new AttributeModifier(TELEPORT_ATTACK_MODIFIER_UUID, "Teleport Attack Bonus", 1.15D, AttributeModifier.Operation.MULTIPLY_TOTAL);
    private static final UUID DE_ATTACK_MODIFIER_UUID = UUID.fromString("b9788c05-ee1b-4c1d-9701-f681c5d89fd1");
    private static final AttributeModifier DE_ATTACK_MODIFIER =
            new AttributeModifier(DE_ATTACK_MODIFIER_UUID, "Deadly Escape Attack Bonus", 1.85D, AttributeModifier.Operation.MULTIPLY_TOTAL);

    private static final long TELEPORT_ANIMATION_MILLIS = 1920L;

    public static final String IDLE = "idle";
    public static final String ATTACK = "attack";
    public static final String SWIPE = "swipe";
    public static final String DEADLY_ESCAPE = "deadly_escape";
    public static final String TELEPORT_IN = "teleport_in";
    public static final String TELEPORT_OUT = "teleport_out";
    public static final String DEATH = "death";

    public int teleportHideTime = MathHelper.secondsToTicks(5);
    public int deadlyEscapeCool = 0;
    public int projectileHit = 0;
    public int meleeHit = 0;
    public int recentHitTime = 0;
    public int attackTick;
    public int deathTime = 0;
    @Nullable
    private LivingEntity savedTeleportTarget = null;
    public DamageSource deathBlow = this.damageSources().generic();

    public final AnimationState attackAnimationState = new AnimationState();
    public final AnimationState swipeAnimationState = new AnimationState();
    public final AnimationState deadlyEscapeAnimationState = new AnimationState();
    public final AnimationState teleportInAnimationState = new AnimationState();
    public final AnimationState teleportOutAnimationState = new AnimationState();
    public final AnimationState deathAnimationState = new AnimationState();

    public EndersentServant(EntityType<? extends EndersentServant> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 50;
        this.moveControl = new ServantMoveControl(this);
        this.setPersistenceRequired();
    }

    @Override
    protected BodyRotationControl createBodyControl() {
        return new MovementBodyRotationControl(this);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, LmConfig.EndersentServantHealth.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, LmConfig.EndersentServantKnockbackResistance.get())
                .add(Attributes.FOLLOW_RANGE, LmConfig.EndersentServantFollowRange.get())
                .add(Attributes.ARMOR, LmConfig.EndersentServantArmor.get())
                .add(Attributes.ARMOR_TOUGHNESS, LmConfig.EndersentServantArmorToughness.get())
                .add(Attributes.MOVEMENT_SPEED, LmConfig.EndersentServantMovementSpeed.get())
                .add(Attributes.ATTACK_KNOCKBACK, LmConfig.EndersentServantAttackKnockback.get())
                .add(Attributes.ATTACK_DAMAGE, LmConfig.EndersentServantDamage.get())
                .add(ForgeMod.STEP_HEIGHT_ADDITION.get(), 1.0D);
    }

    @Override
    public double damageMultiplier() {
        return LmConfig.EndersentServantDamageMultiplier.get();
    }

    @Override
    public double getFollowSpeed() {
        return 1.0D;
    }

    @Override
    public double getCommandSpeed() {
        return 1.0D;
    }

    @Override
    public int getSummonLimit(LivingEntity owner) {
        return LmConfig.EndersentServantLimit.get();
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
            if (countServants(player) >= LmConfig.EndersentServantLimit.get()) {
                return null;
            }
        }
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    private int countServants(Player player) {
        int count = 0;
        if (player.level() instanceof ServerLevel serverLevel) {
            for (Entity entity : serverLevel.getAllEntities()) {
                if (entity instanceof EndersentServant servant && servant.getTrueOwner() == player) {
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(4, new AttackGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new Summoned.WanderGoal<>(this, this.getFollowSpeed()));
    }

    @Override
    protected void addBehaviourGoals() {
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_FLAGS_ID, (byte) 0);
        this.entityData.define(ANIM_STATE, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("DeadlyEscapeCool", this.deadlyEscapeCool);
        compound.putInt("ProjectileHit", this.projectileHit);
        compound.putInt("MeleeHit", this.meleeHit);
        compound.putInt("RecentlyHit", this.recentHitTime);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("DeadlyEscapeCool")) {
            this.deadlyEscapeCool = compound.getInt("DeadlyEscapeCool");
        }
        if (compound.contains("ProjectileHit")) {
            this.projectileHit = compound.getInt("ProjectileHit");
        }
        if (compound.contains("MeleeHit")) {
            this.meleeHit = compound.getInt("MeleeHit");
        }
        if (compound.contains("RecentlyHit")) {
            this.recentHitTime = compound.getInt("RecentlyHit");
        }
    }

    public void setAnimationState(String input) {
        this.setAnimationState(this.getAnimationState(input));
    }

    public void setAnimationState(int id) {
        this.entityData.set(ANIM_STATE, id);
    }

    public int getAnimationState(String animation) {
        if (animation.equals(IDLE)) {
            return 0;
        }
        if (animation.equals(ATTACK)) {
            return 1;
        }
        if (animation.equals(SWIPE)) {
            return 2;
        }
        if (animation.equals(DEADLY_ESCAPE)) {
            return 3;
        }
        if (animation.equals(TELEPORT_IN)) {
            return 4;
        }
        if (animation.equals(TELEPORT_OUT)) {
            return 5;
        }
        if (animation.equals(DEATH)) {
            return 6;
        }
        return 0;
    }

    public List<AnimationState> getAnimations() {
        List<AnimationState> animationStates = new ArrayList<>();
        animationStates.add(this.attackAnimationState);
        animationStates.add(this.swipeAnimationState);
        animationStates.add(this.deadlyEscapeAnimationState);
        animationStates.add(this.teleportInAnimationState);
        animationStates.add(this.teleportOutAnimationState);
        animationStates.add(this.deathAnimationState);
        return animationStates;
    }

    public void stopAllAnimations() {
        for (AnimationState state : this.getAnimations()) {
            state.stop();
        }
    }

    public void stopMostAnimation(AnimationState exception) {
        for (AnimationState state : this.getAnimations()) {
            if (state != exception) {
                state.stop();
            }
        }
    }

    public int getCurrentAnimation() {
        return this.entityData.get(ANIM_STATE);
    }

    public boolean isCurrentAnimation(String animation) {
        return this.getCurrentAnimation() == this.getAnimationState(animation);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (ANIM_STATE.equals(key) && this.level().isClientSide) {
            switch (this.entityData.get(ANIM_STATE)) {
                case 0 -> this.stopAllAnimations();
                case 1 -> {
                    this.stopMostAnimation(this.attackAnimationState);
                    this.attackAnimationState.start(this.tickCount);
                }
                case 2 -> {
                    this.stopMostAnimation(this.swipeAnimationState);
                    this.swipeAnimationState.start(this.tickCount);
                }
                case 3 -> {
                    this.stopMostAnimation(this.deadlyEscapeAnimationState);
                    this.deadlyEscapeAnimationState.start(this.tickCount);
                }
                case 4 -> {
                    this.stopMostAnimation(this.teleportInAnimationState);
                    this.teleportInAnimationState.start(this.tickCount);
                }
                case 5 -> {
                    this.stopMostAnimation(this.teleportOutAnimationState);
                    this.teleportOutAnimationState.start(this.tickCount);
                }
                case 6 -> {
                    this.stopMostAnimation(this.deathAnimationState);
                    this.deathAnimationState.start(this.tickCount);
                }
            }
        }
        super.onSyncedDataUpdated(key);
    }

    private boolean getFlag(int mask) {
        byte value = this.entityData.get(DATA_FLAGS_ID);
        return (value & mask) != 0;
    }

    private void setFlags(int mask, boolean value) {
        int flags = this.entityData.get(DATA_FLAGS_ID);
        flags = value ? (flags | mask) : (flags & ~mask);
        this.entityData.set(DATA_FLAGS_ID, (byte) (flags & 0xFF));
    }

    public boolean isMeleeAttacking() {
        return this.getFlag(1);
    }

    public void setMeleeAttacking(boolean attacking) {
        this.setFlags(1, attacking);
        this.attackTick = 0;
    }

    public boolean isTeleporting() {
        return this.getFlag(2);
    }

    public void setTeleporting(boolean teleporting) {
        this.setFlags(2, teleporting);
    }

    public boolean isDeadlyEscape() {
        return this.getFlag(4);
    }

    public void setDeadlyEscape(boolean escape) {
        this.setFlags(4, escape);
    }

    public boolean isAttacking() {
        return this.isCurrentAnimation(ATTACK) || this.isCurrentAnimation(SWIPE);
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
        if (!this.isHiding()) {
            super.checkFallDamage(y, onGround, state, pos);
        }
    }

    @Override
    public void makeStuckInBlock(BlockState state, Vec3 motionMultiplier) {
    }

    @Override
    protected boolean canRide(Entity vehicle) {
        return false;
    }

    @Override
    public boolean canChangeDimensions() {
        return false;
    }

    @Override
    public void die(DamageSource cause) {
        if (this.deathTime > 0) {
            this.returnChorusCrystal();
            super.die(cause);
        }
    }

    private void returnChorusCrystal() {
        LivingEntity owner = this.getTrueOwner();
        if (owner == null || this.level().isClientSide || !LegendaryMonstersCompat.isLegendaryMonstersLoaded()) {
            return;
        }
        FlyingItem flyingItem = new FlyingItem(
                com.Polarice3.Goety.common.entities.ModEntityType.FLYING_ITEM.get(),
                this.level(),
                this.getX(),
                this.getY(),
                this.getZ());
        flyingItem.setOwner(owner);
        flyingItem.setItem(new ItemStack(ModItems.CHORUS_CRYSTAL.get()));
        flyingItem.setParticle(ParticleTypes.PORTAL);
        this.level().addFreshEntity(flyingItem);
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        this.setAnimationState(DEATH);
        if (this.deathTime < MathHelper.secondsToTicks(2) && this.level() instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 16; ++i) {
                serverLevel.sendParticles(
                        new MagicSmokeParticleOption(0, 0, this.level().getRandom().nextInt(40, 80), 0.25F),
                        this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(0.5D), 0,
                        this.level().getRandom().nextBoolean() ? 0.01D : -0.01D, 0.1D,
                        this.level().getRandom().nextBoolean() ? 0.01D : -0.01D, 0.5D);
            }
        }
        this.move(MoverType.SELF, Vec3.ZERO);
        if (this.deathTime == 1) {
            this.die(this.deathBlow);
        }
        if (this.deathTime >= MathHelper.secondsToTicks(4)) {
            this.remove(Entity.RemovalReason.KILLED);
        }
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.ENDERSENT_AMBIENT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSounds.ENDERSENT_HURT.get();
    }

    @Override
    public void stepSound() {
        this.playSound(ModSounds.ENDERSENT_STEP.get(), 0.15F, 1.0F);
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.ENDERSENT_DEATH.get();
    }

    @Override
    protected float getSoundVolume() {
        return 2.0F;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getEntity() != null && !MobUtil.areAllies(this, source.getEntity())) {
            float threshold = this.getMaxHealth() * 0.025F;
            if (!ModDamageSource.physicalAttacks(source)) {
                amount /= 2.0F;
                if (amount >= threshold) {
                    this.projectileHit += 1 + Mth.ceil(amount / threshold);
                }
                ++this.projectileHit;
                if (this.projectileHit >= 7) {
                    this.projectileHit = 0;
                    this.recentHitTime = MathHelper.secondsToTicks(5);
                }
            } else {
                ++this.meleeHit;
                if ((this.getRandom().nextBoolean() || this.meleeHit >= 2)
                        && (this.getRandom().nextBoolean() || this.meleeHit >= 3)) {
                    this.meleeHit = 0;
                    this.recentHitTime = MathHelper.secondsToTicks(4);
                }
            }
        }
        if (!this.isHiding() && source.getDirectEntity() instanceof Projectile projectile) {
            Entity owner = projectile.getOwner();
            if (owner == null || !MobUtil.areAllies(this, owner)) {
                this.teleportRandomly();
                return false;
            }
        }
        return super.hurt(source, amount);
    }

    private void teleportRandomly() {
        if (this.level().isClientSide) {
            return;
        }
        Vec3 position = this.position();
        for (int i = 0; i < 10; ++i) {
            double x = position.x + (this.getRandom().nextDouble() - 0.5D) * 14.0D;
            double z = position.z + (this.getRandom().nextDouble() - 0.5D) * 14.0D;
            double y = position.y;
            BlockPos blockPos = BlockPos.containing(x, y, z);
            if (!this.level().isEmptyBlock(blockPos)
                    || this.level().getBlockState(blockPos.below()).isAir()) {
                continue;
            }
            this.teleportTo(x, y, z);
            return;
        }
    }

    @Override
    public boolean teleportHurt() {
        return true;
    }

    @Override
    public int getHidingDuration() {
        if (this.isTeleporting()) {
            return this.teleportHideTime;
        }
        return MathHelper.secondsToTicks(5);
    }

    @Override
    public boolean shouldStopHiding() {
        return this.isDeadlyEscape() && this.noChoruslings();
    }

    public int choruslingNumber() {
        return this.level().getEntitiesOfClass(ChoruslingServant.class, this.getBoundingBox().inflate(64.0D),
                chorusling -> chorusling.getTrueOwner() == this && chorusling.isAlive()).size();
    }

    public boolean noChoruslings() {
        return this.choruslingNumber() <= 0;
    }

    public void summonChoruslings() {
        if (this.noChoruslings() && this.level() instanceof ServerLevel serverLevel) {
            ChoruslingServant servant = new ChoruslingServant(LmEntityRegistry.CHORUSLING_SERVANT.get(), serverLevel);
            BlockPos blockPos = BlockFinder.SummonRadius(this.blockPosition(), servant, serverLevel);
            servant.setTrueOwner(this);
            servant.moveTo(blockPos, 0.0F, 0.0F);
            servant.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(this.blockPosition()),
                    MobSpawnType.MOB_SUMMONED, null, null);
            servant.setLimitedLife(MobUtil.getSummonLifespan(serverLevel));
            if (serverLevel.addFreshEntity(servant)) {
                ServerParticleUtil.summonedParticles(serverLevel, servant, ColorUtil.WHITE, 0xFFFFFF, 0xFFFFFF);
            }
        }
    }

    @Override
    public void teleportAfterHiding() {
        if (!this.isTeleporting()) {
            this.teleport(7.0D);
        } else {
            if (this.getTarget() != null) {
                this.teleportTowards(this.getTarget(), 2.0D);
            } else {
                this.teleportIn();
            }
            this.teleportCool = MathHelper.secondsToTicks(5);
            this.setTeleporting(false);
            this.savedTeleportTarget = null;
        }
        if (this.getTarget() != null && !this.level().isClientSide) {
            MobUtil.instaLook(this, this.getTarget());
            ModNetwork.sentToTrackingEntity(this, new SInstaLookPacket(this.getId(), this.getTarget().getId()));
        }
        if (this.isDeadlyEscape()) {
            this.deadlyEscapeCool = MathHelper.secondsToTicks(10);
            this.setDeadlyEscape(false);
        }
    }

    @Override
    public void teleportIn() {
        super.teleportIn();
        this.postTeleportTick = MathHelper.secondsToTicks(1.92F);
        if (!this.level().isClientSide) {
            if (this.isTeleporting()) {
                this.setAnimationState(TELEPORT_IN);
                this.playSound(ModSounds.ENDERSENT_TELEPORT_SMASH.get(), this.getSoundVolume(), this.getVoicePitch());
                this.setTeleporting(false);
                this.savedTeleportTarget = null;
            } else {
                this.setAnimationState(IDLE);
            }
        }
    }

    @Override
    protected boolean teleport(double range) {
        if (!this.level().isClientSide && this.isAlive()) {
            for (int i = 0; i < 128; ++i) {
                boolean canSee = true;
                double x = this.getX() + (this.getRandom().nextDouble() - 0.5D) * range;
                double y = this.getY() + (this.getRandom().nextInt(Mth.floor(range)) - range / 2.0D);
                if (this.getTarget() != null) {
                    y = this.getTarget().getY();
                }
                double z = this.getZ() + (this.getRandom().nextDouble() - 0.5D) * range;
                BlockPos blockPos = BlockPos.containing(x, y, z);
                if (this.getTarget() != null && i < 64) {
                    canSee = BlockFinder.canSeeBlock(this.getTarget(), blockPos);
                }
                EntityTeleportEvent.EnderEntity event = ForgeEventFactory.onEnderTeleport(
                        this, blockPos.getX(), blockPos.getY(), blockPos.getZ());
                if (event.isCanceled()) {
                    this.teleportIn();
                    return false;
                }
                if (!canSee) {
                    continue;
                }
                if (this.ownedTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ())) {
                    return true;
                }
                if (i == 127) {
                    this.teleportIn();
                    return false;
                }
            }
        }
        return false;
    }

    @Override
    public void teleportTowards(Entity entity, double range) {
        if (!this.level().isClientSide && this.isAlive()) {
            if (entity == null) {
                this.teleportIn();
                return;
            }
            try {
                Vec3 lookAngle = entity.getLookAngle();
                Vec3 behind = new Vec3(-lookAngle.x, 0.0D, -lookAngle.z);
                if (behind.lengthSqr() < 1.0E-4D) {
                    behind = new Vec3(0.0D, 0.0D, 1.0D);
                }
                behind = behind.normalize().scale(range);
                for (int i = 0; i < 128; ++i) {
                    double x = entity.getX() + behind.x + (this.getRandom().nextDouble() - 0.5D) * 1.0D;
                    double y = entity.getY();
                    double z = entity.getZ() + behind.z + (this.getRandom().nextDouble() - 0.5D) * 1.0D;
                    EntityTeleportEvent.EnderEntity event = ForgeEventFactory.onEnderTeleport(this, x, y, z);
                    if (event.isCanceled()) {
                        if (this.getHidingDuration() > 0) {
                            this.teleportIn();
                        } else {
                            this.teleportHits();
                        }
                    } else if (this.ownedTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ())) {
                        MobUtil.instaLook(this, entity);
                    } else {
                        if (i != 127) {
                            continue;
                        }
                        MobUtil.instaLook(this, entity);
                        if (this.getHidingDuration() > 0) {
                            this.teleportIn();
                        } else {
                            this.teleportHits();
                        }
                    }
                    break;
                }
            } catch (NullPointerException exception) {
                this.teleportIn();
            }
        }
    }

    private void aimAt(Entity entity, float maxDegrees) {
        if (entity == null) {
            return;
        }
        double x = entity.getX() - this.getX();
        double z = entity.getZ() - this.getZ();
        if (x * x + z * z < 0.0625D) {
            return;
        }
        float yaw = (float) (Mth.atan2(z, x) * (180.0D / Math.PI)) - 90.0F;
        if (maxDegrees < 180.0F) {
            yaw = Mth.approachDegrees(this.getYRot(), yaw, maxDegrees);
        }
        this.setYRot(yaw);
        this.yBodyRot = yaw;
        this.yHeadRot = yaw;
    }

    public void specialTeleport() {
        if (this.getTarget() == null && this.savedTeleportTarget != null && this.savedTeleportTarget.isAlive()) {
            this.setTarget(this.savedTeleportTarget);
        }
        ++this.preHidingTime;
        this.getNavigation().stop();
        this.getMoveControl().strafe(0.0F, 0.0F);
        this.aimAt(this.getTarget(), 20.0F);
        if (this.preHidingTime % 2 == 0) {
            this.playSound(ModSounds.ENDERLING_TELEPORT_OUT.get(), 1.0F, 1.0F);
        }
        if (this.preHidingTime >= MathHelper.secondsToTicks(1.92F)) {
            this.startHide();
            this.preHidingTime = 0;
        }
    }

    public void deadlyEscape() {
        ++this.preHidingTime;
        this.getNavigation().stop();
        this.getMoveControl().strafe(0.0F, 0.0F);
        this.aimAt(this.getTarget(), 20.0F);
        if (this.preHidingTime == 1) {
            this.playSound(ModSounds.ENDERSENT_DEADLY_ESCAPE.get(), this.getSoundVolume(), this.getVoicePitch());
        }
        if (this.preHidingTime == MathHelper.secondsToTicks(1.6F)) {
            this.aimAt(this.getTarget(), 360.0F);
            this.frontSmash();
            this.summonChoruslings();
        }
        if (this.preHidingTime >= MathHelper.secondsToTicks(2)) {
            this.startHide();
            this.preHidingTime = 0;
        }
    }

    public double getXLeft() {
        return this.getXFront() + MobUtil.getHorizontalLeftLookAngle(this).x;
    }

    public double getZLeft() {
        return this.getZFront() + MobUtil.getHorizontalLeftLookAngle(this).z;
    }

    public double getXRight() {
        return this.getXFront() + MobUtil.getHorizontalRightLookAngle(this).x;
    }

    public double getZRight() {
        return this.getZFront() + MobUtil.getHorizontalRightLookAngle(this).z;
    }

    public double getXFront() {
        return this.getX() + this.getHorizontalLookAngle().x;
    }

    public double getZFront() {
        return this.getZ() + this.getHorizontalLookAngle().z;
    }

    public Vec3 getHorizontalLookAngle() {
        return this.calculateViewVector(0.0F, this.getYRot());
    }

    @Override
    public double getMeleeAttackRangeSqr(LivingEntity livingEntity) {
        return this.getBbWidth() * 4.0F * this.getBbWidth() * 4.0F + livingEntity.getBbWidth();
    }

    @Override
    public boolean isWithinMeleeAttackRange(LivingEntity livingEntity) {
        double distance = this.distanceToSqr(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
        return distance <= this.getMeleeAttackRangeSqr(livingEntity);
    }

    public void frontSmash() {
        this.leftSmash();
        this.rightSmash();
        BlockPos blockPos = BlockPos.containing(this.getXFront(), this.getY() - 1.0D, this.getZFront());
        float size = this.isDeadlyEscape() ? 5.0F : 2.5F;
        AABB aabb = new AABB(blockPos).inflate(size);
        for (LivingEntity target : this.level().getEntitiesOfClass(LivingEntity.class, aabb)) {
            if (target == this || MobUtil.areAllies(target, this)) {
                continue;
            }
            this.smashTarget(target);
        }
        CameraShake.cameraShake(this.level(), blockPos.getCenter(), 20.0F, 0.2F, 0, 10);
        if (this.level() instanceof ServerLevel serverLevel) {
            if (this.isDeadlyEscape()) {
                ColorUtil colorUtil = new ColorUtil(15821289);
                serverLevel.sendParticles(
                        new CircleExplodeParticleOption(colorUtil.red(), colorUtil.green(), colorUtil.blue(), size, 1),
                        this.getXFront(), BlockFinder.moveDownToGround(this), this.getZFront(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                serverLevel.sendParticles(
                        new SphereExplodeParticleOption(colorUtil.red(), colorUtil.green(), colorUtil.blue(), size, 1),
                        this.getXFront(), BlockFinder.moveDownToGround(this), this.getZFront(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                ServerParticleUtil.circularParticles(serverLevel, ModParticleTypes.BIG_CULT_SPELL.get(),
                        this.getXFront(), BlockFinder.moveDownToGround(this) + 1.0D, this.getZFront(),
                        colorUtil.red(), colorUtil.green(), colorUtil.blue(), size);
            } else {
                this.groundSmashParticles(this.getXFront(), this.getZFront(), size);
            }
        }
    }

    private void groundSmashParticles(double x, double z, float size) {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        BlockPos blockPos = BlockPos.containing(x, this.getY() - 1.0D, z);
        BlockState blockState = serverLevel.getBlockState(blockPos);
        BlockParticleOption option = new BlockParticleOption(ParticleTypes.BLOCK, blockState);
        ServerParticleUtil.circularParticles(serverLevel, option,
                blockPos.getX(), blockPos.getY() + 1.0F, blockPos.getZ(), 0.0D, 0.0D, 0.0D, size);
        ColorUtil colorUtil = new ColorUtil(blockState.getMapColor(serverLevel, blockPos));
        serverLevel.sendParticles(
                new CircleExplodeParticleOption(colorUtil.red(), colorUtil.green(), colorUtil.blue(), size, 1),
                x, BlockFinder.moveDownToGround(this), z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        ColorUtil colorUtil1 = new ColorUtil(0x3C3C3E);
        ServerParticleUtil.circularParticles(serverLevel, ModParticleTypes.BIG_CULT_SPELL.get(),
                blockPos.getX(), blockPos.getY() + 1.0F, blockPos.getZ(),
                colorUtil1.red(), colorUtil1.green(), colorUtil1.blue(), size);
    }

    public void leftSmash() {
        BlockPos blockPos = BlockPos.containing(this.getXLeft(), this.getY() - 1.0D, this.getZLeft());
        float size = 2.5F;
        AABB aabb = new AABB(blockPos).inflate(size);
        for (LivingEntity target : this.level().getEntitiesOfClass(LivingEntity.class, aabb)) {
            if (target == this || MobUtil.areAllies(target, this)) {
                continue;
            }
            this.smashTarget(target);
        }
        if (this.level() instanceof ServerLevel serverLevel) {
            BlockState blockState = this.level().getBlockState(blockPos);
            BlockParticleOption option = new BlockParticleOption(ParticleTypes.BLOCK, blockState);
            ServerParticleUtil.circularParticles(serverLevel, option,
                    blockPos.getX(), blockPos.getY() + 1.0F, blockPos.getZ(), 0.0D, 0.0D, 0.0D, size);
            if (!this.isDeadlyEscape()) {
                ColorUtil colorUtil = new ColorUtil(serverLevel.getBlockState(blockPos).getMapColor(serverLevel, blockPos));
                serverLevel.sendParticles(
                        new CircleExplodeParticleOption(colorUtil.red(), colorUtil.green(), colorUtil.blue(), size, 1),
                        this.getXLeft(), BlockFinder.moveDownToGround(this), this.getZLeft(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                ColorUtil colorUtil1 = new ColorUtil(0x3C3C3E);
                ServerParticleUtil.circularParticles(serverLevel, ModParticleTypes.BIG_CULT_SPELL.get(),
                        blockPos.getX(), blockPos.getY() + 1.0F, blockPos.getZ(),
                        colorUtil1.red(), colorUtil1.green(), colorUtil1.blue(), size);
            }
        }
    }

    public void rightSmash() {
        BlockPos blockPos = BlockPos.containing(this.getXRight(), this.getY() - 1.0D, this.getZRight());
        float size = 2.5F;
        AABB aabb = new AABB(blockPos).inflate(size);
        for (LivingEntity target : this.level().getEntitiesOfClass(LivingEntity.class, aabb)) {
            if (target == this || MobUtil.areAllies(target, this)) {
                continue;
            }
            this.smashTarget(target);
        }
        if (this.level() instanceof ServerLevel serverLevel) {
            BlockState blockState = this.level().getBlockState(blockPos);
            BlockParticleOption option = new BlockParticleOption(ParticleTypes.BLOCK, blockState);
            ServerParticleUtil.circularParticles(serverLevel, option,
                    blockPos.getX(), blockPos.getY() + 1.0F, blockPos.getZ(), 0.0D, 0.0D, 0.0D, size);
            if (!this.isDeadlyEscape()) {
                ColorUtil colorUtil = new ColorUtil(serverLevel.getBlockState(blockPos).getMapColor(serverLevel, blockPos));
                serverLevel.sendParticles(
                        new CircleExplodeParticleOption(colorUtil.red(), colorUtil.green(), colorUtil.blue(), size, 1),
                        this.getXRight(), BlockFinder.moveDownToGround(this), this.getZRight(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                ColorUtil colorUtil1 = new ColorUtil(0x3C3C3E);
                ServerParticleUtil.circularParticles(serverLevel, ModParticleTypes.BIG_CULT_SPELL.get(),
                        blockPos.getX(), blockPos.getY() + 1.0F, blockPos.getZ(),
                        colorUtil1.red(), colorUtil1.green(), colorUtil1.blue(), size);
            }
        }
    }

    public void attackFrontSmash() {
        double x = this.getXFront();
        double z = this.getZFront();
        float size = 2.5F;
        BlockPos blockPos = BlockPos.containing(x, this.getY() - 1.0D, z);
        AABB aabb = new AABB(blockPos).inflate(size);
        for (LivingEntity target : this.level().getEntitiesOfClass(LivingEntity.class, aabb)) {
            if (target == this || MobUtil.areAllies(target, this)) {
                continue;
            }
            this.doHurtTarget(target);
        }
        CameraShake.cameraShake(this.level(), blockPos.getCenter(), 15.0F, 0.1F, 0, 10);
        this.groundSmashParticles(x, z, size);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            if (this.isCurrentAnimation(DEADLY_ESCAPE)) {
                this.stopMostAnimation(this.deadlyEscapeAnimationState);
            }
            this.stopFinishedAnimation(this.teleportInAnimationState);
            this.stopFinishedAnimation(this.teleportOutAnimationState);
        }
    }

    private void stopFinishedAnimation(AnimationState state) {
        if (state.isStarted() && state.getAccumulatedTime() >= TELEPORT_ANIMATION_MILLIS) {
            state.stop();
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide && !this.isHiding() && !this.isDeadOrDying()) {
            for (int i = 0; i < 2; ++i) {
                this.level().addParticle(ParticleTypes.PORTAL,
                        this.getRandomX(0.5D), this.getRandomY() - 0.25D, this.getRandomZ(0.5D),
                        (this.random.nextDouble() - 0.5D) * 2.0D, -this.random.nextDouble(),
                        (this.random.nextDouble() - 0.5D) * 2.0D);
            }
        }
        if (this.deadlyEscapeCool > 0) {
            if (this.isDeadlyEscape()) {
                this.setDeadlyEscape(false);
            }
            --this.deadlyEscapeCool;
        }
        if (this.recentHitTime > 0) {
            --this.recentHitTime;
        }
        if (!this.level().isClientSide && !this.isDeadOrDying()) {
            this.setAggressive(this.getTarget() != null);
            if (this.isMeleeAttacking()) {
                ++this.attackTick;
            } else if (this.isAttacking()) {
                if (this.isDeadlyEscape()) {
                    this.setAnimationState(DEADLY_ESCAPE);
                } else {
                    this.setAnimationState(IDLE);
                }
            }
            if (this.isCurrentAnimation(TELEPORT_IN)) {
                if (this.postTeleportTick <= 0) {
                    this.setAnimationState(IDLE);
                } else if (this.postTeleportTick == MathHelper.secondsToTicks(1.48F)) {
                    this.frontSmash();
                    this.playSound(ModSounds.ENDERSENT_ATTACK.get(), this.getSoundVolume(), this.getVoicePitch());
                }
            }
            AttributeInstance attackDamage = this.getAttribute(Attributes.ATTACK_DAMAGE);
            if (attackDamage != null) {
                if (this.isCurrentAnimation(TELEPORT_IN)) {
                    attackDamage.removeModifier(TELEPORT_ATTACK_MODIFIER);
                    attackDamage.addTransientModifier(TELEPORT_ATTACK_MODIFIER);
                } else if (attackDamage.hasModifier(TELEPORT_ATTACK_MODIFIER)) {
                    attackDamage.removeModifier(TELEPORT_ATTACK_MODIFIER);
                }
                if (this.isCurrentAnimation(DEADLY_ESCAPE)) {
                    attackDamage.removeModifier(DE_ATTACK_MODIFIER);
                    attackDamage.addTransientModifier(DE_ATTACK_MODIFIER);
                } else if (attackDamage.hasModifier(DE_ATTACK_MODIFIER)) {
                    attackDamage.removeModifier(DE_ATTACK_MODIFIER);
                }
            }
            if (!this.isHiding()) {
                if (this.getTarget() != null && !this.isTeleporting() && !this.isDeadlyEscape() && this.postTeleportTick <= 0) {
                    if (this.getTarget().distanceTo(this) <= 8.0F
                            && this.level().getRandom().nextBoolean()
                            && this.recentHitTime > 0
                            && this.deadlyEscapeCool <= 0
                            && this.choruslingNumber() < 2) {
                        if (!this.isMeleeAttacking() && !this.isAttacking()) {
                            this.setAnimationState(DEADLY_ESCAPE);
                        }
                        this.setDeadlyEscape(true);
                    } else if (!this.isWithinMeleeAttackRange(this.getTarget())
                            && !this.isMeleeAttacking()
                            && !this.isAttacking()
                            && this.recentHitTime > 0
                            && this.level().getRandom().nextBoolean()
                            && this.teleportCool <= 0) {
                        this.savedTeleportTarget = this.getTarget();
                        this.teleportHideTime = MathHelper.secondsToTicks(this.level().getRandom().nextInt(3, 5));
                        this.setTeleporting(true);
                        this.setAnimationState(TELEPORT_OUT);
                    }
                }
                if (this.isDeadlyEscape() && this.isCurrentAnimation(DEADLY_ESCAPE)) {
                    this.setTeleporting(false);
                    this.deadlyEscape();
                    this.savedTeleportTarget = null;
                } else if (this.isTeleporting()) {
                    this.setDeadlyEscape(false);
                    this.specialTeleport();
                }
            }
        }
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        boolean flag = super.doHurtTarget(entity);
        if (flag) {
            this.applyChorusInfection(entity);
        }
        this.applyHitEffects(entity);
        return flag;
    }

    private void smashTarget(LivingEntity target) {
        if (this.isCurrentAnimation(TELEPORT_IN)) {
            this.smashTarget(target, LmConfig.EndersentServantTeleportSmashDamage.get().floatValue());
        } else {
            this.doHurtTarget(target);
        }
    }

    private void smashTarget(LivingEntity target, float damage) {
        boolean flag = target.hurt(this.damageSources().mobAttack(this), damage);
        if (flag) {
            this.applyChorusInfection(target);
        }
        this.applyHitEffects(target);
    }

    private void applyChorusInfection(Entity entity) {
        if (LegendaryMonstersCompat.isLegendaryMonstersLoaded()
                && entity instanceof LivingEntity living
                && !living.isInvulnerable()
                && this.getRandom().nextBoolean()) {
            living.addEffect(new MobEffectInstance(ModEffects.CHORUSINFECTION.get(), 40, 2), this);
        }
    }

    private void applyHitEffects(Entity entity) {
        if ((this.getRandom().nextBoolean() || this.isDeadlyEscape() || this.isCurrentAnimation(TELEPORT_IN))
                && entity instanceof Player player && player.isBlocking()) {
            player.disableShield(true);
        }
        if (entity instanceof Mob) {
            MobUtil.disableShield(entity);
        }
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        if (this.isHiding()) {
            return 0.1F;
        }
        return super.getStandingEyeHeight(pose, dimensions) + 0.5F;
    }

    public static class AttackGoal extends MeleeAttackGoal {

        private final EndersentServant endersent;
        private final double moveSpeed;
        private int delayCounter;

        public AttackGoal(EndersentServant endersent, double moveSpeed) {
            super(endersent, moveSpeed, true);
            this.endersent = endersent;
            this.moveSpeed = moveSpeed;
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.endersent.getTarget();
            return target != null && target.isAlive()
                    && !this.endersent.isHiding()
                    && !this.endersent.isTeleporting()
                    && !this.endersent.isDeadlyEscape()
                    && this.endersent.preHidingTime <= 0
                    && this.endersent.postTeleportTick <= 0;
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse()
                    && !this.endersent.isHiding()
                    && !this.endersent.isTeleporting()
                    && !this.endersent.isDeadlyEscape()
                    && this.endersent.preHidingTime <= 0
                    && this.endersent.postTeleportTick <= 0;
        }

        @Override
        public void start() {
            this.delayCounter = 0;
        }

        @Override
        public void stop() {
            this.endersent.setMeleeAttacking(false);
            this.endersent.attackTick = 0;
        }

        @Override
        public void tick() {
            LivingEntity target = this.endersent.getTarget();
            if (target == null) {
                this.stop();
                return;
            }
            if (!this.endersent.isMeleeAttacking()) {
                this.endersent.getLookControl().setLookAt(target,
                        this.endersent.getMaxHeadYRot(), this.endersent.getMaxHeadXRot());
            }
            if (--this.delayCounter <= 0) {
                this.delayCounter = 10;
                if (!this.endersent.isMeleeAttacking()) {
                    this.endersent.getNavigation().moveTo(target, this.moveSpeed);
                }
            }
            this.checkAndPerformAttack(target,
                    this.endersent.distanceToSqr(target.getX(), target.getY(), target.getZ()));
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
            double reach = this.endersent.getMeleeAttackRangeSqr(enemy);
            boolean sweep = this.endersent.getRandom().nextBoolean();
            if (!this.endersent.isMeleeAttacking() && distToEnemySqr <= reach) {
                this.endersent.setMeleeAttacking(true);
                this.endersent.getNavigation().stop();
                if (sweep) {
                    this.endersent.setAnimationState(SWIPE);
                    this.endersent.playSound(ModSounds.ENDERSENT_SWING.get(),
                            this.endersent.getSoundVolume(), this.endersent.getVoicePitch());
                } else {
                    this.endersent.setAnimationState(ATTACK);
                    this.endersent.playSound(ModSounds.ENDERSENT_ATTACK.get(),
                            this.endersent.getSoundVolume(), this.endersent.getVoicePitch());
                }
            }
            if (this.endersent.isMeleeAttacking()) {
                float seconds = this.endersent.isCurrentAnimation(ATTACK) ? 2.44F : 2.84F;
                if (this.endersent.attackTick < MathHelper.secondsToTicks(seconds)) {
                    if (this.endersent.attackTick == MathHelper.secondsToTicks(1)) {
                        if (this.endersent.isCurrentAnimation(SWIPE)) {
                            this.massiveSweep(this.endersent, 4.5D, 200.0D);
                        } else if (this.endersent.isCurrentAnimation(ATTACK)) {
                            this.endersent.attackFrontSmash();
                        }
                    }
                } else {
                    if (!this.endersent.isHiding() && !this.endersent.isDeadlyEscape()
                            && this.endersent.getRandom().nextFloat() <= 0.3F) {
                        this.endersent.startHide();
                        if (this.endersent.getRandom().nextBoolean()
                                && this.endersent.choruslingNumber() <= 2) {
                            this.endersent.summonChoruslings();
                        }
                    }
                    this.endersent.setMeleeAttacking(false);
                }
            }
        }

        private void massiveSweep(LivingEntity source, double range, double arc) {
            List<LivingEntity> targets = MobUtil.getAttackableLivingEntitiesNearby(source, range, 1.0D, range, range);
            for (LivingEntity target : targets) {
                double targetX = target.getX() - source.getX();
                double targetZ = target.getZ() - source.getZ();
                float targetAngle = (float) ((Math.atan2(targetZ, targetX) * 57.29577951308232D - 90.0D) % 360.0D);
                float attackAngle = source.yBodyRot % 360.0F;
                if (targetAngle < 0.0F) {
                    targetAngle += 360.0F;
                }
                if (attackAngle < 0.0F) {
                    attackAngle += 360.0F;
                }
                float relativeAngle = targetAngle - attackAngle;
                float hitDistance = (float) Math.sqrt(targetZ * targetZ + targetX * targetX) - target.getBbWidth() / 2.0F;
                if (!(((double) hitDistance <= range
                        && (double) relativeAngle <= arc / 2.0D
                        && (double) relativeAngle >= -arc / 2.0D)
                        || (double) relativeAngle >= 360.0D - arc / 2.0D)
                        && !((double) relativeAngle <= -360.0D + arc / 2.0D)) {
                    continue;
                }
                this.endersent.doHurtTarget(target);
            }
        }

        @Override
        protected double getAttackReachSqr(LivingEntity target) {
            return this.endersent.getMeleeAttackRangeSqr(target);
        }
    }
}
