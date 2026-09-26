package com.qiuyue.goetyominous.common.entities.ally.ac;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.utils.MobUtil;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.qiuyue.goetyominous.config.MobsConfig;
import com.qiuyue.goetyominous.common.events.MineGuardianExplosionProtectionHandler;
import com.github.alexmodguy.alexscaves.server.entity.ai.VerticalSwimmingMoveControl;
import com.github.alexmodguy.alexscaves.server.entity.util.MineExplosion;
import com.github.alexmodguy.alexscaves.server.level.storage.ACWorldData;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.*;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

public class MineGuardianServant extends Summoned {

    private float explodeProgress;
    private float prevExplodeProgress;
    private float scanProgress;
    private float prevScanProgress;
    private boolean clientSideTouchedGround;
    private int scanTime = 0;
    private int maxScanTime = 0;
    private int maxSleepTime = 200 + random.nextInt(100);
    private int lastScanTime = 0;
    private int timeSinceHadTarget = 0;
    private int maxChainLength = 8;
    private int anchorMissingTime = 0;
    private UUID anchorUUID;
    private static final EntityDataAccessor<Boolean> EXPLODING = SynchedEntityData.defineId(MineGuardianServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> EYE_CLOSED = SynchedEntityData.defineId(MineGuardianServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SCANNING = SynchedEntityData.defineId(MineGuardianServant.class, EntityDataSerializers.BOOLEAN);

    public MineGuardianServant(EntityType<? extends Summoned> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new VerticalSwimmingMoveControl(this, 0.7F, 30);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.25D).add(Attributes.MAX_HEALTH, 20.0D).add(Attributes.ATTACK_DAMAGE, 1.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(EXPLODING, false);
        this.entityData.define(EYE_CLOSED, false);
        this.entityData.define(SCANNING, false);
    }

    @Override
    public int getSummonLimit(LivingEntity player) {
        return MobsConfig.MineGuardianServantLimit.get();
    }

    @Override
    public Predicate<Entity> summonPredicate() {
        return entity -> entity instanceof MineGuardianServant;
    }

    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new MeleeGoal());
        this.goalSelector.addGoal(2, new RandomLookAroundGoal(this) {
            @Override
            public boolean canUse() {
                return super.canUse() && MineGuardianServant.this.isInWaterOrBubble() && !MineGuardianServant.this.isStaying();
            }

            @Override
            public boolean canContinueToUse() {
                return super.canContinueToUse() && MineGuardianServant.this.isInWaterOrBubble() && !MineGuardianServant.this.isStaying();
            }
        });
    }

    @Override
    public void followGoal() {
        this.goalSelector.addGoal(5, new Summoned.FollowOwnerWaterGoal(this, 1.0D, 10.0F, 2.0F));
    }

    protected PathNavigation createNavigation(Level level) {
        return new WaterBoundPathNavigation(this, level);
    }

    public void travel(Vec3 travelVector) {
        if (this.isEffectiveAi() && this.isInWaterOrBubble()) {
            this.moveRelative(this.getSpeed(), travelVector);
            Vec3 delta = this.getDeltaMovement();
            this.move(MoverType.SELF, delta);
            this.setDeltaMovement(delta.scale(0.9D));
        } else {
            super.travel(travelVector);
        }
    }

    protected SoundEvent getAmbientSound() {
        return isInWaterOrBubble() ? ACSoundRegistry.MINE_GUARDIAN_IDLE.get() : ACSoundRegistry.MINE_GUARDIAN_LAND_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return isInWaterOrBubble() ? ACSoundRegistry.MINE_GUARDIAN_HURT.get() : ACSoundRegistry.MINE_GUARDIAN_LAND_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return isInWaterOrBubble() ? ACSoundRegistry.MINE_GUARDIAN_DEATH.get() : ACSoundRegistry.MINE_GUARDIAN_LAND_DEATH.get();
    }

    public boolean isExploding() {
        return this.entityData.get(EXPLODING);
    }

    public void setExploding(boolean explode) {
        this.entityData.set(EXPLODING, explode);
    }

    public boolean isEyeClosed() {
        return this.entityData.get(EYE_CLOSED);
    }

    public void setEyeClosed(boolean eyeClosed) {
        this.entityData.set(EYE_CLOSED, eyeClosed);
    }

    public boolean isScanning() {
        return this.entityData.get(SCANNING);
    }

    public void setScanning(boolean scanning) {
        this.entityData.set(SCANNING, scanning);
    }

    public int getMaxChainLength() {
        return this.maxChainLength;
    }

    public void setMaxChainLength(int length) {
        this.maxChainLength = length;
    }

    public Entity getAnchor() {
        if (this.anchorUUID == null || this.level().isClientSide) {
            return null;
        }
        return ((ServerLevel) this.level()).getEntity(this.anchorUUID);
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
    public boolean checkSpawnObstruction(LevelReader levelReader) {
        return levelReader.isUnobstructed(this);
    }

    public void tick() {
        super.tick();
        prevExplodeProgress = explodeProgress;
        prevScanProgress = scanProgress;
        if (this.isScanning() && scanProgress < 5F) {
            scanProgress++;
        }
        if (!this.isScanning() && scanProgress > 0F) {
            scanProgress--;
        }
        if (this.isExploding() && explodeProgress < 10F) {
            explodeProgress += 0.5F;
        }
        if (!this.isExploding() && explodeProgress > 0F) {
            explodeProgress -= 0.5F;
        }
        if (this.isExploding()) {
            if (explodeProgress >= 10.0F) {
                this.remove(RemovalReason.KILLED);
                boolean noGriefing = !AttributesConfig.MineGuardianServantExplosionGriefing.get()
                        || !level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
                Explosion.BlockInteraction blockinteraction = noGriefing ? Explosion.BlockInteraction.KEEP
                        : level().getGameRules().getBoolean(GameRules.RULE_MOB_EXPLOSION_DROP_DECAY) ? Explosion.BlockInteraction.DESTROY_WITH_DECAY : Explosion.BlockInteraction.DESTROY;
                List<Map.Entry<Entity, Vec3>> protectedSnapshots = MineGuardianExplosionProtectionHandler.snapshotProtectedVelocities(level(), this, this.getX(), this.getY(0.5), this.getZ(), 5.0F);
                MineExplosion explosion = new MineExplosion(level(), this, this.getX(), this.getY(0.5), this.getZ(), 5.0F, this.isInWaterOrBubble(), blockinteraction);
                explosion.explode();
                explosion.finalizeExplosion(true);
                MineGuardianExplosionProtectionHandler.restoreProtectedVelocities(protectedSnapshots);
            }
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.3F, 1, 0.3F));
        }
        if (!level().isClientSide) {
            Entity anchor = this.getAnchor();
            if (anchor == null) {
                if (this.anchorUUID == null || ++this.anchorMissingTime > 20) {
                    this.anchorMissingTime = 0;
                    this.setMaxChainLength(7 + this.random.nextInt(6));
                    MineGuardianAnchorServantEntity created = new MineGuardianAnchorServantEntity(this);
                    this.level().addFreshEntity(created);
                    this.anchorUUID = created.getUUID();
                }
            } else {
                this.anchorMissingTime = 0;
                if (anchor instanceof MineGuardianAnchorServantEntity anchorEntity) {
                    anchorEntity.linkWithGuardian(this);
                }
            }
            if (this.isInWaterOrBubble()) {
                this.setAirSupply(300);
            } else if (this.onGround()) {
                this.setDeltaMovement(this.getDeltaMovement().add((this.random.nextFloat() * 2.0F - 1.0F) * 0.6F, 0.6D, (this.random.nextFloat() * 2.0F - 1.0F) * 0.6F));
                this.setYRot(this.random.nextFloat() * 360.0F);
                this.setOnGround(false);
                this.playSound(ACSoundRegistry.MINE_GUARDIAN_FLOP.get());
                this.hasImpulse = true;
            }
            Entity target = this.getTarget();
            if (target == null || !target.isAlive()) {
                timeSinceHadTarget++;
            } else {
                timeSinceHadTarget = 0;
            }
            if (this.isFollowing()) {
                if (this.isEyeClosed() || this.isScanning()) {
                    this.setEyeClosed(false);
                    this.setScanning(false);
                }
                if (this.getTarget() == null && this.tickCount % 5 == 0) {
                    this.scanForTarget();
                }
            } else if (this.isStaying()) {
                this.setScanning(false);
                this.setExploding(false);
                this.setTarget(null);
                this.setEyeClosed(true);
            } else if (this.isScanning()) {
                this.setEyeClosed(false);
                if (scanTime < maxScanTime) {
                    if(scanTime % 20 == 0){
                        this.playSound(ACSoundRegistry.MINE_GUARDIAN_SCAN.get());
                    }
                    if (scanTime % 5 == 0 && scanProgress >= 5.0F) {
                        this.scanForTarget();
                        if (this.getTarget() != null) {
                            this.setScanning(false);
                        }
                    }
                    scanTime++;
                } else {
                    scanTime = 0;
                    lastScanTime = tickCount;
                    this.setScanning(false);
                }
            } else if (this.isEyeClosed()) {
                int j = this.tickCount - lastScanTime;
                if (timeSinceHadTarget == 0 || !this.isInWaterOrBubble()) {
                    this.setEyeClosed(false);
                } else if (this.isInWaterOrBubble() && (timeSinceHadTarget > maxSleepTime && j > 200 || this.hurtTime > 0)) {
                    maxSleepTime = 200 + random.nextInt(100);
                    this.setScanning(true);
                    scanTime = 0;
                    maxScanTime = 100 + random.nextInt(100);
                }
            } else {
                if (this.isInWaterOrBubble() && timeSinceHadTarget > 100) {
                    this.setEyeClosed(true);
                }
            }
        } else {
            Vec3 vec3 = this.getDeltaMovement();
            if (vec3.y > 0.0D && this.clientSideTouchedGround && !this.isSilent()) {
                this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), this.getFlopSound(), this.getSoundSource(), 1.0F, 1.0F, false);
            }

            this.clientSideTouchedGround = vec3.y < 0.0D && this.level().loadedAndEntityCanStandOn(this.blockPosition().below(), this);

        }
    }

    private boolean isValidTarget(Entity entity) {
        return entity instanceof LivingEntity living && living.isAlive()
                && !living.isAlliedTo(this) && !this.isAlliedTo(living)
                && MobUtil.isOwnedTargetable(this, living);
    }

    private void scanForTarget() {
        if (this.getTarget() != null) {
            return;
        }
        Entity found = null;
        Vec3 start = this.getEyePosition();
        Vec3 end = start.add(this.getLookAngle().scale(8));
        EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(level(), this, start, end,
                this.getBoundingBox().expandTowards(end.subtract(start)).inflate(1.0D), this::isValidTarget, 8.0F);
        if (entityHitResult != null) {
            Entity inSight = entityHitResult.getEntity();
            if (!inSight.equals(this) && !inSight.isAlliedTo(this) && !this.isAlliedTo(inSight) && this.hasLineOfSight(inSight)) {
                found = inSight;
            }
        } else {
            BlockHitResult hitresult = level().clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
            AABB around = new AABB(hitresult.getLocation().add(-0.5F, -0.5F, -0.5F), hitresult.getLocation().add(0.5F, 0.5F, 0.5F)).inflate(3);
            for (Entity inSight : level().getEntitiesOfClass(LivingEntity.class, around)) {
                if (!inSight.equals(this) && !inSight.isAlliedTo(this) && !this.isAlliedTo(inSight) && this.hasLineOfSight(inSight)) {
                    if (found == null && isValidTarget(inSight) || found != null && isValidTarget(inSight) && inSight.distanceTo(this) < found.distanceTo(this)) {
                        found = inSight;
                    }
                }
            }
        }
        if (found instanceof LivingEntity living) {
            this.setTarget(living);
        }
    }

    public float getScanProgress(float partialTick) {
        return (prevScanProgress + (scanProgress - prevScanProgress) * partialTick) * 0.2F;
    }

    public float getExplodeProgress(float partialTick) {
        return (prevExplodeProgress + (explodeProgress - prevExplodeProgress) * partialTick) * 0.1F;
    }

    protected SoundEvent getFlopSound() {
        return SoundEvents.GUARDIAN_FLOP;
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.hasUUID("AnchorUUID")) {
            this.anchorUUID = compound.getUUID("AnchorUUID");
        }
        this.setMaxChainLength(compound.getInt("MaxChainLength"));
        this.scanTime = compound.getInt("ScanTime");
        this.setEyeClosed(compound.getBoolean("EyeClosed"));
        this.timeSinceHadTarget = compound.getInt("SleepTime");
        this.maxSleepTime = compound.getInt("MaxSleepTime");
        this.maxScanTime = compound.getInt("MaxScanTime");
        this.lastScanTime = compound.getInt("LastScanTime");
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.anchorUUID != null) {
            compound.putUUID("AnchorUUID", this.anchorUUID);
        }
        compound.putInt("MaxChainLength", this.maxChainLength);
        compound.putBoolean("EyeClosed", this.isEyeClosed());
        compound.putInt("ScanTime", this.scanTime);
        compound.putInt("SleepTime", this.timeSinceHadTarget);
        compound.putInt("MaxSleepTime", this.maxSleepTime);
        compound.putInt("MaxScanTime", this.maxScanTime);
        compound.putInt("LastScanTime", this.lastScanTime);
    }

    @Override
    @javax.annotation.Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @javax.annotation.Nullable SpawnGroupData spawnDataIn, @javax.annotation.Nullable CompoundTag dataTag) {
        this.setEyeClosed(true);
        timeSinceHadTarget = 10;
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }
    @Override
    protected void dropAllDeathLoot(DamageSource damageSource) {
        super.dropAllDeathLoot(damageSource);
        if (!level().isClientSide && damageSource.getEntity() instanceof Player player) {
            ACWorldData worldData = ACWorldData.get(level());
            int relations = worldData.getDeepOneReputation(player.getUUID());
            if (relations < 0) {
                worldData.setDeepOneReputation(player.getUUID(), relations + random.nextInt(3) + 1);
            }
        }
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return 2;
    }

    private class MeleeGoal extends Goal {

        private int timer = 0;

        public MeleeGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = MineGuardianServant.this.getTarget();
            return target != null && !MineGuardianServant.this.isStaying();
        }

        public void start() {
            timer = 0;
        }

        public void tick() {
            LivingEntity target = MineGuardianServant.this.getTarget();
            if (target != null) {
                timer++;
                double dist = MineGuardianServant.this.distanceTo(target);
                MineGuardianServant.this.lookAt(EntityAnchorArgument.Anchor.EYES, target.getEyePosition());
                if (dist > 2.0F) {
                    if (MineGuardianServant.this.isInWaterOrBubble()) {
                        MineGuardianServant.this.getNavigation().moveTo(target, 1.6D);
                    }
                } else {
                    MineGuardianServant.this.setExploding(true);
                }
                if (timer > 300) {
                    MineGuardianServant.this.lastScanTime = MineGuardianServant.this.tickCount;
                    MineGuardianServant.this.timeSinceHadTarget = 5;
                    MineGuardianServant.this.setEyeClosed(true);
                    MineGuardianServant.this.setTarget(null);
                    MineGuardianServant.this.setLastHurtByMob(null);
                }
            }
        }
    }
}
