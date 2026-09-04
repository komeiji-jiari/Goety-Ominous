package com.qiuyue.goetyominous.common.entities.ally.ac;

import com.Polarice3.Goety.common.entities.ai.SummonTargetGoal;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.init.ModMobType;
import com.github.alexmodguy.alexscaves.server.entity.ai.FlightPathNavigatorNoSpin;
import com.github.alexmodguy.alexscaves.server.entity.ai.GroundPathNavigatorNoSpin;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.qiuyue.goetyominous.config.MobsConfig;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class VesperServant extends Summoned implements IAnimatedEntity {

    private static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(VesperServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> HANGING = SynchedEntityData.defineId(VesperServant.class, EntityDataSerializers.BOOLEAN);
    public static final Animation ANIMATION_BITE = Animation.create(15);

    private static final double FOLLOW_SPEED = 1.7D;

    private static final double FOLLOW_START_DISTANCE = 8.0D;

    private float flyProgress;
    private float prevFlyProgress;
    private float sleepProgress;
    private float prevSleepProgress;
    private float capturedProgress;
    private float prevCapturedProgress;
    private float groundProgress = 5.0F;
    private float prevGroundProgress = 5.0F;
    private boolean validHangingPos = false;
    private int checkHangingTime;
    private BlockPos prevHangPos;
    public int timeHanging = 0;
    public int timeFlying = 0;
    private float flightPitch = 0;
    private float prevFlightPitch = 0;
    private float flightRoll = 0;
    private float prevFlightRoll = 0;
    private Animation currentAnimation;
    private int animationTick;
    public int groundedFor = 0;
    private boolean wasOnGroundLast;
    private boolean isLandNavigator;
    private int lastTargetId = -1;

    public VesperServant(EntityType<? extends Summoned> entityType, Level level) {
        super(entityType, level);
        this.switchNavigator(false);
        this.setFlying(true);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.VesperServantMovementSpeed.get())
                .add(Attributes.MAX_HEALTH, AttributesConfig.VesperServantHealth.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.VesperServantDamage.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.VesperServantFollowRange.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, AttributesConfig.VesperServantKnockbackResistance.get())
                .add(Attributes.ARMOR, AttributesConfig.VesperServantArmor.get());
    }

    private void switchNavigator(boolean onLand) {
        if (onLand) {
            this.moveControl = new MoveControl(this);
            this.navigation = new GroundPathNavigatorNoSpin(this, level());
            this.isLandNavigator = true;
        } else {
            this.moveControl = new FlightMoveController();
            this.navigation = new FlightPathNavigatorNoSpin(this, level(), 1.0F);
            this.isLandNavigator = false;
        }
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new VesperAttackGoal());

        this.goalSelector.addGoal(2, new VesperFlyAndHangGoal());

        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F) {
            @Override
            public boolean canUse() {
                return !VesperServant.this.isHanging() && super.canUse();
            }

            @Override
            public boolean canContinueToUse() {
                return !VesperServant.this.isHanging() && super.canContinueToUse();
            }
        });
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this) {
            @Override
            public boolean canUse() {
                return !VesperServant.this.isHanging() && super.canUse();
            }

            @Override
            public boolean canContinueToUse() {
                return !VesperServant.this.isHanging() && super.canContinueToUse();
            }
        });
    }

    @Override
    public void followGoal() {
        this.goalSelector.addGoal(5, new VesperFollowOwnerGoal());
    }

    @Override
    public void targetSelectGoal() {

        this.targetSelector.addGoal(1, new PassiveAwareSummonTargetGoal(this));
    }

    @Override
    public void ownerHurtGoals() {

        this.targetSelector.addGoal(1, new PassiveAwareOwnerHurtByTargetGoal());
        this.targetSelector.addGoal(2, new PassiveAwareOwnerHurtTargetGoal());
    }

    @Override
    public void setStaying(boolean staying) {
        super.setStaying(staying);

        if (staying && !this.level().isClientSide && this.getTarget() != null) {
            this.setTarget(null);
        }
    }

    @Override
    public void commandMode() {
        if (this.isCommanded()) {
            this.setHanging(false);
            this.setFlying(true);
            this.groundedFor = 0; // 收到指令即起身飞行,不被地面休息计时拖累
            this.getNavigation().stop();
            LivingEntity commandEntity = this.getCommandPosEntity();
            if (commandEntity != null && commandEntity.isAlive()) {
                this.setCommandTick(this.getCommandTick() - 1);
                Vec3 targetVec = commandEntity.position().add(0.0D, commandEntity.getBbHeight() * 0.5F, 0.0D);
                this.getMoveControl().setWantedPosition(targetVec.x, targetVec.y, targetVec.z, this.getCommandSpeed());
                if (this.getCommandTick() <= 0) {
                    this.setCommandPosEntity(null);
                    this.setCommandPos(null);
                } else if (this.getBoundingBox().inflate(1.25D).intersects(commandEntity.getBoundingBox())) {

                    if (this.isAbleToRide(commandEntity) && !this.isPassenger()) {
                        if (this.startRiding(commandEntity)) {
                            if (this.getTrueOwner() instanceof Player player) {
                                player.displayClientMessage(Component.translatable("info.goety.servant.dismount"), true);
                            }
                        }
                    }
                    this.setCommandPosEntity(null);
                    this.setCommandPos(null);
                }
            } else {
                BlockPos commandPos = this.getCommandPos();
                if (commandPos != null) {
                    this.setCommandTick(this.getCommandTick() - 1);
                    AABB aabb = new AABB(commandPos);
                    if (this.getCommandTick() <= 0 || this.getBoundingBox().inflate(0.5F).intersects(aabb)) {

                        if (this.isGuardingArea()) {
                            this.setBoundPos(commandPos);
                        }
                        // 到达指令点:不再原地悬停,就地收翼——头顶能挂就挂上去,否则落地休息。
                        this.settleInPlace();
                        this.setCommandPos(null);
                    } else {
                        this.groundedFor = 0;
                        this.getMoveControl().setWantedPosition(commandPos.getX() + 0.5D, commandPos.getY() + 0.5D, commandPos.getZ() + 0.5D, this.getCommandSpeed());
                    }
                }
            }
        }
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new FlightPathNavigatorNoSpin(this, level, 1.0F);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FLYING, false);
        this.entityData.define(HANGING, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        // FLYING/HANGING 是纯 entityData 运行时标志,groundedFor/… 是普通字段,
        // 都不随 IServant.saveServantData 入档。不加这段的话区块重载/重进世界时
        // 只能靠构造器把姿态重置为 FLYING=true,原本挂着/落地休息的姿态全部丢失。
        compound.putBoolean("VesperFlying", this.isFlying());
        compound.putBoolean("VesperHanging", this.isHanging());
        compound.putInt("VesperGroundedFor", this.groundedFor);
        compound.putInt("VesperTimeFlying", this.timeFlying);
        compound.putBoolean("VesperWasOnGround", this.wasOnGroundLast);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        // 只在这些键存在时恢复,避免旧档/生成蛋(无此键)被意外强制落地。
        if (compound.contains("VesperFlying")) {
            this.setFlying(compound.getBoolean("VesperFlying"));
        }
        if (compound.contains("VesperHanging")) {
            this.setHanging(compound.getBoolean("VesperHanging"));
        }
        if (compound.contains("VesperGroundedFor")) {
            this.groundedFor = compound.getInt("VesperGroundedFor");
        }
        if (compound.contains("VesperTimeFlying")) {
            this.timeFlying = compound.getInt("VesperTimeFlying");
        }
        if (compound.contains("VesperWasOnGround")) {
            this.wasOnGroundLast = compound.getBoolean("VesperWasOnGround");
        }
        if (this.level() != null && !this.level().isClientSide) {
            // 与飞行姿态同步 noGravity,避免读档后的头几帧直接自由落体。
            // (挂壁姿态 HANGING 仍保持 noGravity=false,由 tick 里的悬空推力维持。)
            this.setNoGravity(this.isFlying());
        }
    }

    @Override
    public void tick() {
        super.tick();
        prevFlyProgress = flyProgress;
        prevSleepProgress = sleepProgress;
        prevGroundProgress = groundProgress;
        prevCapturedProgress = capturedProgress;
        prevFlightPitch = flightPitch;
        prevFlightRoll = flightRoll;
        if (isFlying() && flyProgress < 5F) {
            flyProgress++;
        }
        if (!isFlying() && flyProgress > 0F) {
            flyProgress--;
        }
        if (onGround() && groundProgress < 5F) {
            groundProgress++;
        }
        if (!onGround() && groundProgress > 0F) {
            groundProgress--;
        }
        if (isHanging() && sleepProgress < 5F) {
            sleepProgress++;
        }
        if (!isHanging() && sleepProgress > 0F) {
            sleepProgress--;
        }
        boolean captured = this.isPassenger();
        if (captured && capturedProgress < 5F) {
            capturedProgress++;
        }
        if (!captured && capturedProgress > 0F) {
            capturedProgress--;
        }
        if (!level().isClientSide) {
            if (captured) {
                this.setFlying(false);
                this.setHanging(false);
            }
            if (this.isHanging()) {
                BlockPos above = posAbove();
                if (checkHangingTime-- < 0 || random.nextFloat() < 0.1F || prevHangPos != above) {
                    validHangingPos = canHangFrom(above, level().getBlockState(above));
                    checkHangingTime = 5 + random.nextInt(5);
                    prevHangPos = above;
                }
                if (validHangingPos) {
                    this.setDeltaMovement(this.getDeltaMovement().multiply(0.1F, 0.3F, 0.1F).add(0, 0.08D, 0));
                } else {
                    this.setHanging(false);
                    this.setFlying(true);
                }
                timeHanging++;
            } else {
                timeHanging = 0;
                validHangingPos = false;
                prevHangPos = null;
            }
            if (this.isFlying()) {
                if (timeFlying % 10 == 0) {
                    this.playSound(ACSoundRegistry.VESPER_FLAP.get());
                }
                timeFlying++;
                this.setNoGravity(true);
                if (this.isLandNavigator) {
                    switchNavigator(false);
                }
                if (groundedFor > 0) {
                    this.setFlying(false);
                }
            } else {
                timeFlying = 0;
                this.setNoGravity(false);
                if (!this.isLandNavigator) {
                    switchNavigator(true);
                }
            }
            LivingEntity target = getTarget();
            if (target == null || !target.isAlive()) {
                lastTargetId = -1;
            } else if (target.getId() != lastTargetId) {
                lastTargetId = target.getId();
                this.playSound(ACSoundRegistry.VESPER_SCREAM.get(), 3.0F, 1.0F);
            }
            // 落地休息:从飞行/悬挂转为真正着地的当帧授予一次休息计时(groundedFor>0 期间
            // FlyAndHangGoal 不再起飞,贴身目标可留在地面缠斗)。只在"新落地"那一帧触发一次,
            // 不会在休息期间反复续期。
            if (groundedFor == 0 && this.onGround() && !this.isFlying() && !this.isHanging()
                    && !this.isPassenger() && !this.wasOnGroundLast) {
                this.groundedFor = 80 + this.random.nextInt(40);
            }
            this.wasOnGroundLast = this.onGround();
        }
        if (groundedFor > 0) {
            groundedFor--;
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
        tickRotation((float) this.getDeltaMovement().y * 2 * -(float) (180F / (float) Math.PI));
    }

    private void tickRotation(float yMov) {
        flightPitch = yMov;
        float threshold = 1F;
        boolean flag = false;
        if (isFlying() && this.yRotO - this.getYRot() > threshold) {
            flightRoll += 10;
            flag = true;
        }
        if (isFlying() && this.yRotO - this.getYRot() < -threshold) {
            flightRoll -= 10;
            flag = true;
        }
        if (!flag) {
            if (flightRoll > 0) {
                flightRoll = Math.max(flightRoll - 5, 0);
            }
            if (flightRoll < 0) {
                flightRoll = Math.min(flightRoll + 5, 0);
            }
        }
        flightRoll = Mth.clamp(flightRoll, -60, 60);
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn,
                                        MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn,
                                        @Nullable CompoundTag dataTag) {
        this.setFlying(true);
        this.setHanging(false);
        if (this.getTrueOwner() instanceof Player player) {
            if (countServants(player) >= MobsConfig.VesperServantLimit.get()) {
                this.discard();
                return null;
            }
        }
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    private int countServants(Player player) {
        int count = 0;
        if (player.level() instanceof ServerLevel serverLevel) {
            for (Entity entity : serverLevel.getAllEntities()) {
                if (entity instanceof VesperServant servant && servant != this) {
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
            if (countServants(player) >= MobsConfig.VesperServantLimit.get()) {
                this.discard();
            }
        }
    }

    @Override
    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    public boolean isFlying() {
        return this.entityData.get(FLYING);
    }

    public void setFlying(boolean flying) {
        this.entityData.set(FLYING, flying);
    }

    public boolean isHanging() {
        return this.entityData.get(HANGING);
    }

    public void setHanging(boolean hanging) {
        this.entityData.set(HANGING, hanging);
    }

    public float getFlightPitch(float partialTick) {
        return prevFlightPitch + (flightPitch - prevFlightPitch) * partialTick;
    }

    public float getFlightRoll(float partialTick) {
        return prevFlightRoll + (flightRoll - prevFlightRoll) * partialTick;
    }

    public float getCapturedProgress(float partialTick) {
        return (prevCapturedProgress + (capturedProgress - prevCapturedProgress) * partialTick) * 0.2F;
    }

    public float getSleepProgress(float partialTick) {
        return (prevSleepProgress + (sleepProgress - prevSleepProgress) * partialTick) * 0.2F;
    }

    public float getFlyProgress(float partialTick) {
        return (prevFlyProgress + (flyProgress - prevFlyProgress) * partialTick) * 0.2F;
    }

    public float getGroundProgress(float partialTick) {
        return (prevGroundProgress + (groundProgress - prevGroundProgress) * partialTick) * 0.2F;
    }

    public boolean canHangFrom(BlockPos pos, BlockState state) {
        return state.isFaceSturdy(level(), pos, Direction.DOWN) && level().isEmptyBlock(pos.below()) && level().isEmptyBlock(pos.below(2));
    }

    public BlockPos posAbove() {
        return BlockPos.containing(this.getX(), this.getBoundingBox().maxY + 0.1F, this.getZ());
    }

    /**
     * 指令点/守卫点到达后的收翼动作:头顶紧邻处有可挂的天花板就直接挂上去;
     * 否则落地休息一段时间(groundedFor 计时),而不是原地悬停扑腾。
     */
    private void settleInPlace() {
        this.getMoveControl().setWantedPosition(this.getX(), this.getY(), this.getZ(), 0.0D);
        this.getNavigation().stop();
        BlockPos above = this.posAbove();
        if (this.canHangFrom(above, this.level().getBlockState(above))) {
            this.setHanging(true);
            this.setFlying(false);
        } else {
            this.setFlying(false);
            this.groundedFor = Math.max(this.groundedFor, 60);
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
        if (this.getAnimation() != animation) {
            this.animationTick = 0;
            this.currentAnimation = animation;
        }
    }

    public void syncAnimation(Animation animation) {
        if (this.level().isClientSide) {
            this.setAnimation(animation);
        } else {
            AnimationHandler.INSTANCE.sendAnimationMessage(this, animation);
        }
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_BITE};
    }

    @Override
    public MobType getMobType() {
        return ModMobType.NATURAL;
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    public AABB getBoundingBoxForCulling() {
        return this.getBoundingBox().inflate(3, 3, 3);
    }

    public boolean shouldRenderAtSqrDistance(double distance) {
        return Math.sqrt(distance) < 1024.0D;
    }

    public void calculateEntityAnimation(boolean flying) {
        float f1 = (float) Mth.length(this.getX() - this.xo, flying ? this.getY() - this.yo : 0, this.getZ() - this.zo);
        float f2 = Math.min(f1 * 4.0F, 1.0F);
        this.walkAnimation.update(f2, 0.4F);
    }

    public int getAmbientSoundInterval() {
        return this.isHanging() ? 80 : 140;
    }

    protected SoundEvent getAmbientSound() {
        return this.isHanging() ? ACSoundRegistry.VESPER_QUIET_IDLE.get() : ACSoundRegistry.VESPER_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.VESPER_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.VESPER_DEATH.get();
    }

    class FlightMoveController extends MoveControl {
        private final Mob parentEntity;

        public FlightMoveController() {
            super(VesperServant.this);
            this.parentEntity = VesperServant.this;
        }

        @Override
        public void tick() {
            if (this.operation == MoveControl.Operation.MOVE_TO) {
                Vec3 vector3d = new Vec3(this.wantedX - parentEntity.getX(), this.wantedY - parentEntity.getY(), this.wantedZ - parentEntity.getZ());
                double d0 = vector3d.length();
                double width = parentEntity.getBoundingBox().getSize();
                Vec3 vector3d1 = vector3d.scale(this.speedModifier * 0.05D / d0);
                parentEntity.setDeltaMovement(parentEntity.getDeltaMovement().add(vector3d1).scale(0.95D).add(0, -0.01, 0));
                if (d0 < width) {
                    this.operation = Operation.WAIT;
                } else if (d0 >= width) {
                    float yaw = -((float) Mth.atan2(vector3d1.x, vector3d1.z)) * (180F / (float) Math.PI);
                    parentEntity.setYRot(Mth.approachDegrees(parentEntity.getYRot(), yaw, 8));
                }
            }
        }
    }

    class VesperFollowOwnerGoal extends Summoned.FollowOwnerGoal<VesperServant> {

        public VesperFollowOwnerGoal() {
            super(VesperServant.this, FOLLOW_SPEED, (float) FOLLOW_START_DISTANCE, 2.0F);
        }

        @Override
        public boolean canUse() {
            if (!super.canUse()) {
                return false;
            }
            LivingEntity owner = getTrueOwner();
            return owner != null && owner.isAlive() && owner.level() == VesperServant.this.level();
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity owner = getTrueOwner();
            if (owner == null || !owner.isAlive() || owner.isSpectator()) {
                return false;
            }
            if (owner.level() != VesperServant.this.level()) {
                return false;
            }
            if (!VesperServant.this.isFollowing() || VesperServant.this.isCommanded()) {
                return false;
            }
            if (VesperServant.this.getTarget() != null || VesperServant.this.isPassenger()) {
                return false;
            }

            return VesperServant.this.distanceToSqr(owner) > Mth.square(this.stopDistance);
        }

        @Override
        public void start() {
            super.start();
            VesperServant.this.groundedFor = 0;
            VesperServant.this.setHanging(false);
            VesperServant.this.setFlying(true);
        }

        @Override
        public void stop() {
            super.stop();
            VesperServant.this.getNavigation().stop();
        }

        @Override
        public void tick() {
            if (this.owner != null) {
                this.summonedEntity.getLookControl().setLookAt(this.owner, 10.0F, (float) this.summonedEntity.getMaxHeadXRot());
                if (this.summonedEntity.getControlledVehicle() != null) {
                    this.summonedEntity.getNavigation().moveTo(this.owner, this.followSpeed + 0.25D);
                    Entity controlledVehicle = this.summonedEntity.getControlledVehicle();
                    if (controlledVehicle instanceof Mob controlledMob) {
                        controlledMob.getNavigation().moveTo(this.owner, this.followSpeed + 0.25D);
                    }
                } else if (--this.timeToRecalcPath <= 0) {
                    this.timeToRecalcPath = 10;
                    if (!this.summonedEntity.isLeashed() && !this.summonedEntity.isPassenger()) {
                        double range = this.owner instanceof Mob ? 32.0D : 16.0D;
                        boolean flag = this.summonedEntity.distanceToSqr(this.owner) >= Mth.square(range);
                        if (this.owner instanceof Mob) {
                            flag |= !this.summonedEntity.hasLineOfSight(this.owner)
                                    && this.summonedEntity.distanceToSqr(this.owner) >= Mth.square(8.0D);
                        } else {
                            flag &= this.canTeleport();
                        }
                        if (flag) {
                            this.tryToTeleportNearEntity();
                        } else {
                            this.summonedEntity.getNavigation().moveTo(this.owner, this.followSpeed);
                        }
                    }
                }
            }
        }
    }

    class PassiveAwareSummonTargetGoal extends SummonTargetGoal {

        public PassiveAwareSummonTargetGoal(Mob mob) {
            super(mob);
        }

        @Override
        public boolean canUse() {
            return !VesperServant.this.isStaying() && super.canUse();
        }
    }

    class PassiveAwareOwnerHurtByTargetGoal extends Owned.OwnerHurtByTargetGoal<VesperServant> {

        public PassiveAwareOwnerHurtByTargetGoal() {
            super(VesperServant.this);
        }

        @Override
        public boolean canUse() {
            return !VesperServant.this.isStaying() && super.canUse();
        }
    }

    class PassiveAwareOwnerHurtTargetGoal extends Owned.OwnerHurtTargetGoal<VesperServant> {

        public PassiveAwareOwnerHurtTargetGoal() {
            super(VesperServant.this);
        }

        @Override
        public boolean canUse() {
            return !VesperServant.this.isStaying() && super.canUse();
        }
    }

    class VesperFlyAndHangGoal extends Goal {
        private boolean wantsToHang = false;

        private boolean restOnGround = false;

        private boolean restingOnGround = false;
        private double x;
        private double y;
        private double z;
        private int hangCheckIn = 0;

        public VesperFlyAndHangGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            VesperServant e = VesperServant.this;
            if (e.isVehicle() || e.isPassenger()) {
                return false;
            }

            if (e.isCommanded()) {
                return false;
            }
            if (e.getTarget() != null && e.getTarget().isAlive()) {
                this.restingOnGround = false;
                return false;
            }
            if (e.isHanging() || e.groundedFor > 0) {
                return false;
            }
            if (this.restingOnGround) {

                if (e.isStaying()) {
                    if (isGroundRestBroken(e)) {
                        this.restingOnGround = false;
                    } else {
                        return false;
                    }
                } else {
                    this.restingOnGround = false;
                }
            }

            if (isFollowOwnerFar()) {
                return false;
            }
            wantsToHang = shouldHang();
            restOnGround = false;
            if (wantsToHang) {
                Vec3 hangPos = findHangFromPos();
                if (hangPos != null) {
                    this.x = hangPos.x;
                    this.y = hangPos.y;
                    this.z = hangPos.z;
                    return true;
                }
                if (e.groundedFor <= 0 && hasHangableCeilingNear()) {
                    // 休息计时已结束,且附近确实存在可挂的天花板(只是当前脚下那根列搜不到):
                    // 先起飞到空中,让 tick 里的 hangCheck 边飞边找顶,而不是趴地上永久卡死。
                    Vec3 target = findFlightPos();
                    if (target != null) {
                        this.x = target.x;
                        this.y = target.y;
                        this.z = target.z;
                        return true;
                    }
                }
                // 找不到可悬挂的天花板(开阔地/无顶,或仍在休息期内):落地休息,而不是原地
                // 漫无目的地绕圈。待命/守卫/飞行过久(shouldHang)都适用;休息期由 groundedFor
                // 维持计时,计时结束上面的分支才会再次起飞。
                this.restOnGround = true;
                this.restingOnGround = true;
                return true;
            }
            Vec3 target = findFlightPos();
            if (target == null) {
                return false;
            }
            this.x = target.x;
            this.y = target.y;
            this.z = target.z;
            return true;
        }

        @Override
        public boolean canContinueToUse() {
            VesperServant e = VesperServant.this;
            if (e.isCommanded()) {
                return false;
            }
            if (restOnGround) {

                return !e.onGround() && e.groundedFor <= 0 && !isFollowOwnerFar();
            }
            if (wantsToHang) {
                return !e.getNavigation().isDone() && !e.isHanging() && e.groundedFor <= 0 && !isFollowOwnerFar();
            }
            return e.isFlying() && !e.getNavigation().isDone() && e.groundedFor <= 0 && !isFollowOwnerFar();
        }

        private boolean isFollowOwnerFar() {
            LivingEntity owner = VesperServant.this.getTrueOwner();
            return owner != null && owner.isAlive() && VesperServant.this.isFollowing()
                    && !VesperServant.this.isCommanded()
                    && VesperServant.this.distanceTo(owner) > FOLLOW_START_DISTANCE;
        }

        private boolean shouldHang() {
            VesperServant e = VesperServant.this;
            if (e.isStaying() || e.isCommanded() || e.isGuardingArea()) {
                return true;
            }
            return e.timeFlying > 300;
        }

        private BlockPos anchor() {
            VesperServant e = VesperServant.this;
            if (e.isGuardingArea() && e.getBoundPos() != null) {
                return e.getBoundPos();
            }
            if (e.isCommanded() && e.getCommandPos() != null) {
                return e.getCommandPos();
            }
            LivingEntity owner = e.getTrueOwner();
            if (owner != null && owner.isAlive() && e.isFollowing() && !e.isCommanded()
                    && e.distanceTo(owner) <= FOLLOW_START_DISTANCE) {
                return owner.blockPosition();
            }
            return e.blockPosition();
        }

        private int range() {
            VesperServant e = VesperServant.this;
            if (e.isGuardingArea()) {
                return 8;
            }
            if (e.isStaying() || e.isCommanded()) {
                return 6;
            }
            LivingEntity owner = e.getTrueOwner();
            if (owner != null && owner.isAlive() && e.isFollowing() && !e.isCommanded()
                    && e.distanceTo(owner) <= FOLLOW_START_DISTANCE) {
                return 4;
            }
            return 13;
        }

        @Override
        public void start() {
            hangCheckIn = 0;
            if (restOnGround) {

                VesperServant.this.getNavigation().stop();
                VesperServant.this.setHanging(false);
                VesperServant.this.setFlying(false);
                return;
            }
            VesperServant.this.setFlying(true);
            VesperServant.this.setHanging(false);
            VesperServant.this.getNavigation().moveTo(this.x, this.y, this.z, 1.0D);
        }

        @Override
        public void tick() {
            if (wantsToHang && !restOnGround) {
                if (hangCheckIn-- < 0) {
                    hangCheckIn = 5 + VesperServant.this.getRandom().nextInt(5);
                    if (!VesperServant.this.isHanging()
                            && VesperServant.this.canHangFrom(VesperServant.this.posAbove(),
                            VesperServant.this.level().getBlockState(VesperServant.this.posAbove()))) {
                        VesperServant.this.setHanging(true);
                        VesperServant.this.setFlying(false);
                    }
                }
            }
        }

        @Override
        public void stop() {
            if (wantsToHang) {
                VesperServant.this.getNavigation().stop();
            }
            wantsToHang = false;
            restOnGround = false;
        }

        private boolean isGroundRestBroken(VesperServant e) {
            return !e.onGround();
        }

        private Vec3 findFlightPos() {
            BlockPos anchor = anchor();
            int range = range();

            Vec3 heightAdjusted = new Vec3(anchor.getX() + 0.5F + random.nextInt(range * 2) - range,
                    anchor.getY() + 0.5F,
                    anchor.getZ() + 0.5F + random.nextInt(range * 2) - range);
            if (VesperServant.this.level().canSeeSky(BlockPos.containing(heightAdjusted))) {
                Vec3 ground = groundPosition(heightAdjusted);
                heightAdjusted = new Vec3(heightAdjusted.x, ground.y + 4 + random.nextInt(3), heightAdjusted.z);
            } else {
                Vec3 ground = groundPosition(heightAdjusted);
                BlockPos ceiling = BlockPos.containing(ground).above(2);
                while (ceiling.getY() < VesperServant.this.level().getMaxBuildHeight()
                        && !VesperServant.this.level().getBlockState(ceiling).isSolid()) {
                    ceiling = ceiling.above();
                }
                float randCeilVal = 0.3F + random.nextFloat() * 0.5F;
                heightAdjusted = new Vec3(heightAdjusted.x, ground.y + (ceiling.getY() - ground.y) * randCeilVal, heightAdjusted.z);
            }
            BlockHitResult result = VesperServant.this.level().clip(new ClipContext(
                    VesperServant.this.getEyePosition(), heightAdjusted,
                    ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, VesperServant.this));
            if (result.getType() == HitResult.Type.MISS) {
                return heightAdjusted;
            }
            return result.getLocation();
        }

        private Vec3 groundPosition(Vec3 airPosition) {
            BlockPos.MutableBlockPos ground = new BlockPos.MutableBlockPos();
            ground.set(airPosition.x, airPosition.y, airPosition.z);
            boolean flag = false;
            while (ground.getY() < VesperServant.this.level().getMaxBuildHeight()
                    && !VesperServant.this.level().getBlockState(ground).isSolid()
                    && VesperServant.this.level().getFluidState(ground).isEmpty()) {
                ground.move(0, 1, 0);
                flag = true;
            }
            ground.move(0, -1, 0);
            while (ground.getY() > VesperServant.this.level().getMinBuildHeight()
                    && !VesperServant.this.level().getBlockState(ground).isSolid()
                    && VesperServant.this.level().getFluidState(ground).isEmpty()) {
                ground.move(0, -1, 0);
            }
            return Vec3.atCenterOf(flag ? ground.above() : ground.below());
        }

        private Vec3 findHangFromPos() {
            BlockPos anchor = anchor();
            int range = Math.max(4, range());
            // 先确定性尝试实体/锚点所在列正上方的天花板:落地休息或读档后仍停在原位时,
            // 随机采样可能漏掉头顶那根梁,导致明明有顶却判成“无顶可挂”而趴死在地。
            BlockPos blockpos = tryHangColumn(anchor);
            if (blockpos == null) {
                for (int i = 0; i < 15; i++) {
                    BlockPos blockpos1 = anchor.offset(random.nextInt(range) - range / 2, 0, random.nextInt(range) - range / 2);
                    blockpos1 = tryHangColumn(blockpos1);
                    if (blockpos1 != null) {
                        blockpos = blockpos1;
                    }
                }
            }
            return blockpos == null ? null : Vec3.atCenterOf(blockpos);
        }

        /** 从给定列向上找第一个可供倒挂的天花板方块;底座本身是实心时自动从头顶一格起找。 */
        private BlockPos tryHangColumn(BlockPos start) {
            BlockPos current = start;
            if (!VesperServant.this.level().isEmptyBlock(current)) {
                current = current.above(); // 站在实心方块上(如落地休息)先从紧邻上方那格找
            }
            while (VesperServant.this.level().isEmptyBlock(current)
                    && current.getY() < VesperServant.this.level().getMaxBuildHeight()) {
                current = current.above();
            }
            if (current.getY() < VesperServant.this.level().getMaxBuildHeight()
                    && current.getY() > VesperServant.this.getY() - 1
                    && VesperServant.this.canHangFrom(current, VesperServant.this.level().getBlockState(current))
                    && hasLineOfToPos(current)) {
                return current;
            }
            return null;
        }

        /** 探测附近(较 hang 搜索更广的半径内)是否确实存在可挂的天花板。 */
        private boolean hasHangableCeilingNear() {
            BlockPos anchor = anchor();
            int probeRange = 12;
            int tries = 40;
            for (int i = 0; i < tries; i++) {
                BlockPos p = anchor.offset(random.nextInt(probeRange * 2) - probeRange, 0, random.nextInt(probeRange * 2) - probeRange);
                if (tryHangColumn(p) != null) {
                    return true;
                }
            }
            return false;
        }

        private boolean hasLineOfToPos(BlockPos in) {
            HitResult raytraceresult = VesperServant.this.level().clip(new ClipContext(
                    VesperServant.this.getEyePosition(1.0F),
                    new Vec3(in.getX() + 0.5, in.getY() + 0.5, in.getZ() + 0.5),
                    ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, VesperServant.this));
            if (raytraceresult instanceof BlockHitResult blockHitResult) {
                BlockPos pos = blockHitResult.getBlockPos();
                return pos.equals(in) || VesperServant.this.level().isEmptyBlock(pos);
            }
            return true;
        }
    }

    class VesperAttackGoal extends Goal {
        private Vec3 startOrbitFrom;
        private int orbitTime;
        private int maxOrbitTime;

        public VesperAttackGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = VesperServant.this.getTarget();
            return target != null && target.isAlive() && !VesperServant.this.isPassenger();
        }

        @Override
        public void start() {
            orbitTime = 0;
            maxOrbitTime = 80;
            startOrbitFrom = null;
        }

        @Override
        public void tick() {
            VesperServant e = VesperServant.this;
            LivingEntity target = e.getTarget();
            // 地面休息中遇到够不着的目标时打断休息起身追击,避免长时间留在原地等敌人靠近。
            if (e.groundedFor > 0 && target != null && target.isAlive()
                    && e.distanceTo(target) > e.getBbWidth() + target.getBbWidth() + 2.0D) {
                e.groundedFor = 0;
            }
            if (e.groundedFor <= 0) {
                if (e.isHanging()) {
                    e.setHanging(false);
                    e.setFlying(true);
                } else if (!e.isFlying()) {
                    e.setFlying(true);
                }
            } else {
                e.setFlying(false);
            }
            if (target != null && target.isAlive()) {
                double distance = e.distanceTo(target);
                float f = e.getBbWidth() + target.getBbWidth();
                if (startOrbitFrom == null) {
                    e.getNavigation().moveTo(target, e.isFlying() ? 2.5D : 1D);
                    e.lookAt(EntityAnchorArgument.Anchor.EYES, target.getEyePosition());
                } else if (orbitTime < maxOrbitTime && e.groundedFor <= 0) {
                    orbitTime++;
                    float zoomIn = 1F - orbitTime / (float) maxOrbitTime;
                    Vec3 orbitPos = orbitAroundPos(3.0F + zoomIn * 5.0F).add(0, 4 + zoomIn * 3, 0);
                    e.getNavigation().moveTo(orbitPos.x, orbitPos.y, orbitPos.z, e.isFlying() ? 2.5D : 1D);
                    e.lookAt(EntityAnchorArgument.Anchor.EYES, orbitPos);
                } else {
                    orbitTime = 0;
                    startOrbitFrom = null;
                }
                if (distance < f + 0.5D) {
                    if (e.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
                        e.syncAnimation(ANIMATION_BITE);
                    } else if (e.getAnimation() == ANIMATION_BITE && e.getAnimationTick() == 8 && e.hasLineOfSight(target)) {
                        target.hurt(e.damageSources().mobAttack(e), (float) e.getAttributeValue(Attributes.ATTACK_DAMAGE));
                        maxOrbitTime = 60 + e.getRandom().nextInt(80);
                        startOrbitFrom = target.getEyePosition();
                    }
                }
            }
        }

        private Vec3 orbitAroundPos(float circleDistance) {
            final float angle = 3 * (float) (Math.toRadians(orbitTime * 3F));
            final double extraX = circleDistance * Mth.sin(angle);
            final double extraZ = circleDistance * Mth.cos(angle);
            return startOrbitFrom.add(extraX, 0, extraZ);
        }
    }
}
