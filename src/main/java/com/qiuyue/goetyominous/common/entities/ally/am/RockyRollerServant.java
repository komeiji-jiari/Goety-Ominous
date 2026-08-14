package com.qiuyue.goetyominous.common.entities.ally.am;

import com.Polarice3.Goety.api.entities.IOwned;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.init.ModMobType;
import com.Polarice3.Goety.utils.MobUtil;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.github.alexthe666.alexsmobs.entity.ai.AdvancedPathNavigateNoTeleport;
import com.github.alexthe666.alexsmobs.entity.ai.AnimalAIWanderRanged;
import com.github.alexthe666.alexsmobs.entity.util.Maths;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.citadel.server.entity.collision.ICustomCollisions;

import java.util.EnumSet;
import java.util.List;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Fallable;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RockyRollerServant extends Summoned implements ICustomCollisions {

    private static final EntityDataAccessor<Boolean> ROLLING = SynchedEntityData.defineId(RockyRollerServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> ANGRY = SynchedEntityData.defineId(RockyRollerServant.class, EntityDataSerializers.BOOLEAN);
    public float rollProgress;
    public float prevRollProgress;
    public int rollCounter = 0;
    public float clientRoll = 0;
    private int maxRollTime = 50;
    private Vec3 rollDelta;
    private float rollYRot;
    private int rollCooldown = 0;
    private int earthquakeCooldown = 0;

    public RockyRollerServant(EntityType<? extends Owned> type, Level level) {
        super(type, level);
        this.setLimitedLife(MobUtil.getSummonLifespan(level));
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        if (reason == MobSpawnType.SPAWN_EGG) {
            this.setHasLifespan(false);
            this.setLifespan(0);
        }
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.RockyRollerServantHealth.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.RockyRollerServantFollowRange.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.RockyRollerServantDamage.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, AttributesConfig.RockyRollerServantKnockbackResistance.get())
                .add(Attributes.ARMOR, AttributesConfig.RockyRollerServantArmor.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.RockyRollerServantMovementSpeed.get());
    }

    @Override
    public void setConfigurableAttributes() {
        if (this.getAttribute(Attributes.MAX_HEALTH) != null) {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(AttributesConfig.RockyRollerServantHealth.get());
        }
        if (this.getAttribute(Attributes.FOLLOW_RANGE) != null) {
            this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(AttributesConfig.RockyRollerServantFollowRange.get());
        }
        if (this.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(AttributesConfig.RockyRollerServantDamage.get());
        }
        if (this.getAttribute(Attributes.KNOCKBACK_RESISTANCE) != null) {
            this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(AttributesConfig.RockyRollerServantKnockbackResistance.get());
        }
        if (this.getAttribute(Attributes.ARMOR) != null) {
            this.getAttribute(Attributes.ARMOR).setBaseValue(AttributesConfig.RockyRollerServantArmor.get());
        }
        if (this.getAttribute(Attributes.MOVEMENT_SPEED) != null) {
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(AttributesConfig.RockyRollerServantMovementSpeed.get());
        }
    }

    @Override
    public MobType getMobType() {
        return ModMobType.NATURAL;
    }

    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.ROCKY_ROLLER_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return AMSoundRegistry.ROCKY_ROLLER_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.ROCKY_ROLLER_HURT.get();
    }

    public boolean causeFallDamage(float distance, float damageMultiplier) {
        return false;
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    public int getMaxFallDistance() {
        return super.getMaxFallDistance() * 2;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new AIMelee());
        this.goalSelector.addGoal(2, new AIRollIdle(this));
        this.goalSelector.addGoal(6, new AnimalAIWanderRanged(this, 90, 1.0D, 7, 7) {
            @Override
            public boolean canUse() {
                return !RockyRollerServant.this.isSitting() && super.canUse();
            }
        });
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, LivingEntity.class, 15.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ANGRY, false);
        this.entityData.define(ROLLING, false);
    }

    @Override
    public void tick() {
        super.tick();
        prevRollProgress = rollProgress;

        if (isRolling()) {
            if (rollProgress < 5F)
                rollProgress++;
        } else {
            if (rollProgress > 0F)
                rollProgress--;
        }

        if (!this.level().isClientSide) {
            this.setAngry(this.getTarget() != null && this.getTarget().isAlive() && this.distanceToSqr(this.getTarget()) < 20 * 20);
        }
        if (this.isRolling() && rollCooldown <= 0) {
            this.handleRoll();
            if (!this.level().isClientSide) {
                if (this.isAngry() && this.isAlive()) {
                    for (Entity entity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(0.3F))) {
                        if (entity != this && !this.isAlliedTo(entity)) {
                            entity.hurt(this.getServantAttack(), (isTarget(entity) ? 5.0F : 2.0F) + random.nextFloat() * 2.0F);
                            launch(entity, isTarget(entity));
                            if (isTarget(entity)) {
                                maxRollTime = rollCounter + 10;
                            }
                        }
                    }
                }
            }
            if ((this.rollCounter > 2 && !this.isRollMoving()) || !this.isAlive()) {
                this.setRolling(false);
            }
            this.setMaxUpStep(1F);
        } else {
            this.setMaxUpStep(0.66F);
            this.rollCounter = 0;
        }
        if (rollCooldown > 0) {
            rollCooldown--;
        }
        if (earthquakeCooldown > 0) {
            earthquakeCooldown--;
        }
    }

    private boolean isRollMoving() {
        return this.getDeltaMovement().lengthSqr() > 0.02D;
    }

    private void earthquake() {
        boolean flag = false;
        LivingEntity trueOwner = this.getTrueOwner();
        List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(6, 8, 6));
        for (LivingEntity e : list) {
            if (!(e instanceof RockyRollerServant) && e.isAlive() && e != trueOwner
                    && !(e instanceof IOwned owned && owned.getTrueOwner() == trueOwner)
                    && !this.isAlliedTo(e)) {
                e.addEffect(new MobEffectInstance(AMEffectRegistry.EARTHQUAKE.get(), 20, 0, false, false, true));
                flag = true;
            }
        }
        if (!this.level().canSeeSky(this.blockPosition()) && this.level().getGameRules().getBoolean(net.minecraft.world.level.GameRules.RULE_MOBGRIEFING)) {
            BlockPos ceil = this.blockPosition().offset(0, 2, 0);
            while ((!level().getBlockState(ceil).isSolid() || level().getBlockState(ceil).getBlock() == Blocks.POINTED_DRIPSTONE) && ceil.getY() < level().getMaxBuildHeight()) {
                ceil = ceil.above();
            }
            final int i = 2 + random.nextInt(2);
            final int j = 2 + random.nextInt(2);
            final int k = 2 + random.nextInt(2);
            final float f = (float) (i + j + k) * 0.333F + 0.5F;
            final double fTimesF = f * f;

            for (BlockPos blockpos1 : BlockPos.betweenClosed(ceil.offset(-i, -j, -k), ceil.offset(i, j, k))) {
                if (blockpos1.distSqr(ceil) <= fTimesF && level().getBlockState(blockpos1).getBlock() instanceof Fallable) {
                    if (isHangingDripstone(blockpos1)) {
                        while (isHangingDripstone(blockpos1.above()) && blockpos1.getY() < level().getMaxBuildHeight()) {
                            blockpos1 = blockpos1.above();
                        }
                        if (isHangingDripstone(blockpos1)) {
                            Vec3 vec3 = Vec3.atBottomCenterOf(blockpos1);
                            FallingBlockEntity fallingblockentity = FallingBlockEntity.fall(level(), new BlockPos((int) vec3.x, (int) vec3.y, (int) vec3.z), level().getBlockState(blockpos1));
                            this.level().destroyBlock(blockpos1, false);
                            this.level().addFreshEntity(fallingblockentity);
                        }
                    } else {
                        this.level().scheduleTick(blockpos1, level().getBlockState(blockpos1).getBlock(), 2);
                    }
                    flag = true;
                }
            }
        }
        if (flag) {
            this.gameEvent(GameEvent.ENTITY_ROAR);
            this.playSound(AMSoundRegistry.ROCKY_ROLLER_EARTHQUAKE.get(), this.getSoundVolume(), this.getVoicePitch());
        }
    }

    private boolean isHangingDripstone(BlockPos pos) {
        return level().getBlockState(pos).getBlock() instanceof PointedDripstoneBlock && level().getBlockState(pos).getValue(PointedDripstoneBlock.TIP_DIRECTION) == Direction.DOWN;
    }

    private boolean isTarget(Entity entity) {
        return this.getTarget() != null && this.getTarget().is(entity);
    }

    public boolean isRolling() {
        return this.entityData.get(ROLLING);
    }

    public void setRolling(boolean rolling) {
        this.entityData.set(ROLLING, Boolean.valueOf(rolling));
    }

    public boolean isAngry() {
        return this.entityData.get(ANGRY);
    }

    public void setAngry(boolean angry) {
        this.entityData.set(ANGRY, Boolean.valueOf(angry));
    }

    private void handleRoll() {
        ++this.rollCounter;
        if (!this.level().isClientSide) {
            if (this.horizontalCollision && earthquakeCooldown == 0 && this.isAngry()) {
                earthquakeCooldown = maxRollTime;
                this.earthquake();
            }
            if (this.rollCounter > maxRollTime) {
                this.setRolling(false);
                this.rollCooldown = 10 + random.nextInt(10);
                this.rollCounter = 0;
                this.setDeltaMovement(Vec3.ZERO);
            } else {
                Vec3 vec3 = this.getDeltaMovement();
                if (this.rollCounter == 1) {
                    float f = this.getYRot() * Mth.DEG_TO_RAD;
                    float f1 = this.isBaby() ? 0.2F : 0.35F;
                    this.rollYRot = this.getYRot();
                    this.rollDelta = new Vec3(vec3.x + (double) (-Mth.sin(f) * f1), 0.0D, vec3.z + (double) (Mth.cos(f) * f1));
                    this.setDeltaMovement(this.rollDelta.add(0.0D, 0.27D, 0.0D));
                } else {
                    this.setYRot(rollYRot);
                    this.setYHeadRot(rollYRot);
                    this.setYBodyRot(rollYRot);
                    this.setDeltaMovement(this.rollDelta.x, vec3.y, this.rollDelta.z);
                }
            }
        }
    }

    private void rollFor(int time) {
        if (this.rollCooldown == 0) {
            this.maxRollTime = time;
            earthquakeCooldown = 0;
            this.setRolling(true);
        }
    }

    private void launch(Entity e, boolean huge) {
        if (e.onGround()) {
            final double d0 = e.getX() - this.getX();
            final double d1 = e.getZ() - this.getZ();
            final double d2 = Math.max(d0 * d0 + d1 * d1, 0.001D);
            final float f = huge ? 1.0F : 0.35F;
            e.push(d0 / d2 * f, huge ? 0.5D : 0.2F, d1 / d2 * f);
        }
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source.is(DamageTypes.FALLING_STALACTITE) || super.isInvulnerableTo(source);
    }

    public boolean canBeCollidedWith() {
        return this.isAlive();
    }

    public void push(Entity entity) {
        entity.setDeltaMovement(entity.getDeltaMovement().add(this.getDeltaMovement()));
    }

    @Override
    public boolean canPassThrough(BlockPos blockPos, BlockState blockstate, VoxelShape voxelShape) {
        return blockstate.getBlock() instanceof PointedDripstoneBlock;
    }

    public boolean isColliding(BlockPos pos, BlockState blockstate) {
        return !(blockstate.getBlock() instanceof PointedDripstoneBlock) && super.isColliding(pos, blockstate);
    }

    public Vec3 collide(Vec3 vec3) {
        return ICustomCollisions.getAllowedMovementForEntity(this, vec3);
    }

    public boolean hurt(DamageSource dmg, float amount) {
        if (!this.isRollMoving() && !dmg.is(DamageTypes.MAGIC) && dmg.getDirectEntity() instanceof LivingEntity livingentity
                && !(livingentity instanceof RockyRollerServant) && !this.isAlliedTo(livingentity)) {
            if (!dmg.is(DamageTypes.EXPLOSION) && !livingentity.hurtMarked) {
                livingentity.hurt(damageSources().thorns(this), 2.0F);
            }
        }
        return super.hurt(dmg, amount);
    }

    @Override
    protected PathNavigation createNavigation(Level worldIn) {
        return new Navigator(this, worldIn);
    }

    public boolean isSitting() {
        return this.isStaying();
    }

    static class RockyRollerNodeEvaluator extends WalkNodeEvaluator {
        protected BlockPathTypes evaluateBlockPathType(BlockGetter level, BlockPos pos, BlockPathTypes typeIn) {
            return level.getBlockState(pos).getBlock() instanceof PointedDripstoneBlock ? BlockPathTypes.OPEN : super.evaluateBlockPathType(level, pos, typeIn);
        }
    }

    class AIRollIdle extends Goal {
        RockyRollerServant rockyRoller;

        public AIRollIdle(RockyRollerServant p_29328_) {
            this.rockyRoller = p_29328_;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
        }

        public boolean canUse() {
            if (this.rockyRoller.onGround() && !this.rockyRoller.isSitting()) {
                if (rockyRoller.isRolling() || rockyRoller.rollCooldown > 0 || rockyRoller.getTarget() != null && rockyRoller.getTarget().isAlive()) {
                    return false;
                } else {
                    float f = rockyRoller.getYRot() * Mth.DEG_TO_RAD;
                    int i = 0;
                    int j = 0;
                    float f1 = -Mth.sin(f);
                    float f2 = Mth.cos(f);
                    if ((double) Math.abs(f1) > 0.5D) {
                        i = (int) ((float) i + f1 / Math.abs(f1));
                    }

                    if ((double) Math.abs(f2) > 0.5D) {
                        j = (int) ((float) j + f2 / Math.abs(f2));
                    }

                    return rockyRoller.level().getBlockState(rockyRoller.blockPosition().offset(i, -1, j)).isAir();
                }
            }
            return false;
        }

        public boolean canContinueToUse() {
            return false;
        }

        public void start() {
            this.rockyRoller.rollFor(30 + random.nextInt(30));
        }

        public boolean isInterruptable() {
            return false;
        }
    }

    private class AIMelee extends Goal {

        private BlockPos rollFromPos = null;
        private int rollTimeout = 0;

        public AIMelee() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            return !RockyRollerServant.this.isSitting() && RockyRollerServant.this.getTarget() != null
                    && RockyRollerServant.this.getTarget().isAlive() && !RockyRollerServant.this.isRolling();
        }

        public boolean canContinueToUse() {
            return !RockyRollerServant.this.isSitting() && super.canContinueToUse();
        }

        public void tick() {
            LivingEntity enemy = RockyRollerServant.this.getTarget();
            double d0 = this.validRollDistance(enemy);
            double distToEnemySqr = RockyRollerServant.this.distanceTo(enemy);
            if (rollFromPos == null || enemy.distanceToSqr(rollFromPos.getX() + 0.5F, rollFromPos.getY() + 0.5F, rollFromPos.getZ() + 0.5) > 60 || !canEntitySeePosition(enemy, rollFromPos)) {
                rollFromPos = getRollAtPosition(enemy);
            }
            RockyRollerServant.this.lookAt(enemy, 100, 5);

            if (rollTimeout < 40 && rollFromPos != null && (distToEnemySqr <= d0 && RockyRollerServant.this.distanceToSqr(rollFromPos.getX() + 0.5F, rollFromPos.getY() + 0.5F, rollFromPos.getZ() + 0.5) > 2.25F)) {
                RockyRollerServant.this.getNavigation().moveTo(rollFromPos.getX() + 0.5F, rollFromPos.getY() + 0.5F, rollFromPos.getZ() + 0.5F, 1.6D);
                rollTimeout++;
            } else {
                double d1 = enemy.getX() - RockyRollerServant.this.getX();
                double d2 = enemy.getZ() - RockyRollerServant.this.getZ();
                float f = (float) (Mth.atan2(d2, d1) * (double) Mth.RAD_TO_DEG) - 90.0F;
                RockyRollerServant.this.setYRot(f);
                RockyRollerServant.this.yBodyRot = f;
                RockyRollerServant.this.rollFor(30 + random.nextInt(40));
            }
        }

        public void stop() {
            super.stop();
            rollTimeout = 0;
        }

        protected double validRollDistance(LivingEntity attackTarget) {
            return 3.0F + attackTarget.getBbWidth();
        }

        private boolean canEntitySeePosition(LivingEntity entity, BlockPos destinationBlock) {
            Vec3 Vector3d = new Vec3(entity.getX(), entity.getY() + 0.5F, entity.getZ());
            Vec3 blockVec = net.minecraft.world.phys.Vec3.atCenterOf(destinationBlock);
            BlockHitResult result = entity.level().clip(new ClipContext(Vector3d, blockVec, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity));
            return result != null && (result.getBlockPos().equals(destinationBlock) || entity.level().getBlockState(result.getBlockPos()).getBlock() == Blocks.POINTED_DRIPSTONE);
        }


        public BlockPos getRollAtPosition(Entity target) {
            float radius = RockyRollerServant.this.getRandom().nextInt(2) + 6 + target.getBbWidth();
            int orbit = RockyRollerServant.this.getRandom().nextInt(360);
            float angle = (Maths.STARTING_ANGLE * orbit);
            double extraX = radius * Mth.sin(Mth.PI + angle);
            double extraZ = radius * Mth.cos(angle);
            BlockPos circlePos = new BlockPos((int) (target.getX() + extraX), (int) target.getEyeY(), (int) (target.getZ() + extraZ));
            while (!RockyRollerServant.this.level().getBlockState(circlePos).isAir() && circlePos.getY() < RockyRollerServant.this.level().getMaxBuildHeight()) {
                circlePos = circlePos.above();
            }
            while (!RockyRollerServant.this.level().getBlockState(circlePos.below()).entityCanStandOn(RockyRollerServant.this.level(), circlePos.below(), RockyRollerServant.this) && circlePos.getY() > 1) {
                circlePos = circlePos.below();
            }
            if (RockyRollerServant.this.getWalkTargetValue(circlePos) > -1) {
                return circlePos;
            }
            return null;
        }
    }

    static class Navigator extends AdvancedPathNavigateNoTeleport {

        public Navigator(Mob mob, Level world) {
            super(mob, world, true);
        }

        protected PathFinder createPathFinder(int i) {
            this.nodeEvaluator = new RockyRollerNodeEvaluator();
            return new PathFinder(this.nodeEvaluator, i);
        }
    }
}
