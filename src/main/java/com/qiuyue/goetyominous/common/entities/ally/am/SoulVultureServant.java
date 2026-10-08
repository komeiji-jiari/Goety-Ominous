package com.qiuyue.goetyominous.common.entities.ally.am;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.utils.CuriosFinder;
import com.github.alexthe666.alexsmobs.entity.ai.DirectPathNavigator;
import com.github.alexthe666.alexsmobs.entity.ai.GroundPathNavigatorWide;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.AMTagRegistry;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.qiuyue.goetyominous.config.MobsConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

public class SoulVultureServant extends Summoned implements FlyingAnimal {

    private static final EntityDataAccessor<Boolean> FLYING = SynchedEntityData.defineId(SoulVultureServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> TACKLING = SynchedEntityData.defineId(SoulVultureServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Optional<BlockPos>> PERCH_POS = SynchedEntityData.defineId(SoulVultureServant.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    private static final EntityDataAccessor<Integer> SOUL_LEVEL = SynchedEntityData.defineId(SoulVultureServant.class, EntityDataSerializers.INT);
    private static final int OWNER_CIRCLE_HEIGHT = 6;
    private static final float DESCEND_SPEED = 0.4F;
    private static final float ORBIT_SPEED = 0.022F;
    private static final float PURSUIT_ORBIT_SPEED = 0.052F;
    private static final float FORMATION_RADIUS_MOVING = 2.5F;
    private static final float FORMATION_RADIUS_IDLE = 6.0F;
    private static final double FORMATION_SPEED_MOVING = 1.4D;
    private static final double FORMATION_SPEED_IDLE = 0.8D;
    private static final double OWNER_MOVING_THRESHOLD = 0.0025D;

    public float prevFlyProgress;
    public float flyProgress;
    public float prevTackleProgress;
    public float tackleProgress;
    private boolean isLandNavigator;
    private boolean wasStationed;
    private int perchSearchCooldown;
    private int landingCooldown;
    private int tackleCooldown;

    public SoulVultureServant(EntityType<? extends Owned> type, Level level) {
        super(type, level);
        this.switchNavigator(true);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.SoulVultureServantHealth.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.SoulVultureServantFollowRange.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.SoulVultureServantDamage.get())
                .add(Attributes.MOVEMENT_SPEED, 0.25D);
    }

    @Override
    public void setConfigurableAttributes() {
        if (this.getAttribute(Attributes.MAX_HEALTH) != null) {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(AttributesConfig.SoulVultureServantHealth.get());
        }
        if (this.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(AttributesConfig.SoulVultureServantDamage.get());
        }
        if (this.getAttribute(Attributes.FOLLOW_RANGE) != null) {
            this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(AttributesConfig.SoulVultureServantFollowRange.get());
        }
    }

    @Override
    public int getSummonLimit(LivingEntity owner) {
        return MobsConfig.SoulVultureServantLimit.get();
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    protected SoundEvent getAmbientSound() {
        return AMSoundRegistry.SOUL_VULTURE_IDLE.get();
    }

    protected SoundEvent getHurtSound(DamageSource source) {
        return AMSoundRegistry.SOUL_VULTURE_HURT.get();
    }

    protected SoundEvent getDeathSound() {
        return AMSoundRegistry.SOUL_VULTURE_HURT.get();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new FollowFormationGoal());
        this.goalSelector.addGoal(2, new CircleTargetGoal());
        this.goalSelector.addGoal(3, new TackleGoal());
        this.goalSelector.addGoal(4, new PerchGoal());
        this.goalSelector.addGoal(5, new FlyRandomGoal());
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 20.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }

    @Override
    public void followGoal() {
        this.goalSelector.addGoal(6, new FlyToOwnerGoal(this, 1.0D, 10.0F, 2.0F));
    }

    protected PathNavigation createNavigation(Level level) {
        return new GroundPathNavigatorWide(this, level);
    }

    private void switchNavigator(boolean onLand) {
        if (onLand) {
            this.moveControl = new MoveControl(this);
            this.navigation = new GroundPathNavigatorWide(this, this.level());
            this.isLandNavigator = true;
        } else {
            this.moveControl = new FlyMoveControl();
            this.navigation = new DirectPathNavigator(this, this.level());
            this.isLandNavigator = false;
        }
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FLYING, false);
        this.entityData.define(TACKLING, false);
        this.entityData.define(PERCH_POS, Optional.empty());
        this.entityData.define(SOUL_LEVEL, 0);
    }

    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Flying", this.isFlying());
        BlockPos perchPos = this.getPerchPos();
        if (perchPos != null) {
            compound.putInt("PerchX", perchPos.getX());
            compound.putInt("PerchY", perchPos.getY());
            compound.putInt("PerchZ", perchPos.getZ());
        }
        compound.putInt("SoulLevel", this.getSoulLevel());
        compound.putInt("LandingCooldown", this.landingCooldown);
    }

    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setFlying(compound.getBoolean("Flying"));
        this.setSoulLevel(compound.getInt("SoulLevel"));
        this.landingCooldown = compound.getInt("LandingCooldown");
        if (compound.contains("PerchX") && compound.contains("PerchY") && compound.contains("PerchZ")) {
            this.setPerchPos(new BlockPos(compound.getInt("PerchX"), compound.getInt("PerchY"), compound.getInt("PerchZ")));
        }
    }

    @Override
    public boolean isFlying() {
        return this.entityData.get(FLYING);
    }

    public void setFlying(boolean flying) {
        this.entityData.set(FLYING, flying);
    }

    public boolean isTackling() {
        return this.entityData.get(TACKLING);
    }

    public void setTackling(boolean tackling) {
        this.entityData.set(TACKLING, tackling);
    }

    public BlockPos getPerchPos() {
        return this.entityData.get(PERCH_POS).orElse(null);
    }

    public void setPerchPos(BlockPos pos) {
        this.entityData.set(PERCH_POS, Optional.ofNullable(pos));
    }

    public int getSoulLevel() {
        return this.entityData.get(SOUL_LEVEL);
    }

    public void setSoulLevel(int soulLevel) {
        this.entityData.set(SOUL_LEVEL, soulLevel);
    }

    public boolean hasSoulHeart() {
        return this.getSoulLevel() > 2;
    }

    public boolean shouldSwoop() {
        return this.getTarget() != null && this.tackleCooldown == 0;
    }

    private boolean isStationed() {
        return !this.isFollowing() && !this.isCommanded() && this.getTarget() == null;
    }

    private boolean canCircleOwner() {
        LivingEntity owner = this.getTrueOwner();
        return this.isFollowing() && !this.isCommanded() && this.getTarget() == null && owner != null && owner.isAlive() && this.distanceToSqr(owner) <= 144.0D;
    }

    private boolean shouldTakeOff() {
        if (this.getTarget() != null) {
            return true;
        }
        if (!this.isStationed()) {
            return true;
        }
        if (this.isStaying()) {
            return false;
        }
        return this.landingCooldown == 0;
    }

    private BlockPos getGroundBelow() {
        BlockPos pos = this.blockPosition();
        while (pos.getY() > this.level().getMinBuildHeight()) {
            BlockState state = this.level().getBlockState(pos);
            if (!state.isAir() && !state.getFluidState().isEmpty()) {
                return null;
            }
            if (!state.getCollisionShape(this.level(), pos).isEmpty()) {
                return pos;
            }
            pos = pos.below();
        }
        return null;
    }

    public boolean isPerchBlock(BlockPos pos) {
        BlockState state = this.level().getBlockState(pos);
        return this.level().isEmptyBlock(pos.above()) && this.level().isEmptyBlock(pos.above(2)) && state.is(AMTagRegistry.SOUL_VULTURE_PERCHES);
    }

    public void tick() {
        super.tick();
        this.prevTackleProgress = this.tackleProgress;
        this.prevFlyProgress = this.flyProgress;
        if (!this.level().isClientSide) {
            if (this.perchSearchCooldown > 0) {
                this.perchSearchCooldown--;
            }
            boolean stationed = this.isStationed();
            if (stationed && !this.wasStationed) {
                this.perchSearchCooldown = 0;
            }
            this.wasStationed = stationed;
            if (stationed && !this.isStaying()) {
                BlockPos perchPos = this.getPerchPos();
                if (perchPos != null && !this.isPerchBlock(perchPos)) {
                    this.setPerchPos(null);
                }
                if (this.getPerchPos() == null && this.perchSearchCooldown == 0 && this.isFlying()) {
                    this.perchSearchCooldown = 20 + this.random.nextInt(20);
                    this.setPerchPos(this.findNewPerchPos());
                }
            } else if (this.getPerchPos() != null) {
                this.setPerchPos(null);
            }
            if (this.isStaying() && this.isFlying()) {
                BlockPos ground = this.getGroundBelow();
                if (ground != null) {
                    double groundY = ground.getY() + 1.1D;
                    this.getMoveControl().setWantedPosition(this.getX(), groundY, this.getZ(), DESCEND_SPEED);
                    if (this.getY() - groundY <= 0.5D) {
                        this.setFlying(false);
                        this.setDeltaMovement(Vec3.ZERO);
                    }
                }
            }
            if (!this.isFlying() && this.shouldTakeOff()) {
                this.setFlying(true);
            }
            if (this.isFlying() && this.landingCooldown > 0 && this.onGround() && this.getTarget() == null) {
                this.setFlying(false);
            }
            if (this.getTrueOwner() != null && CuriosFinder.hasNetherCrown(this.getTrueOwner())) {
                this.setHasLifespan(false);
            } else if (this.getLifespan() > 0) {
                this.setHasLifespan(true);
            }
        }
        if (this.isFlying()) {
            if (this.isLandNavigator) {
                this.switchNavigator(false);
            }
            if (this.flyProgress < 5.0F) {
                this.flyProgress += 1.0F;
            }
        } else {
            if (!this.isLandNavigator) {
                this.switchNavigator(true);
            }
            if (this.flyProgress > 0.0F) {
                this.flyProgress -= 1.0F;
            }
        }
        if (this.isTackling()) {
            if (this.tackleProgress < 5.0F) {
                this.tackleProgress += 1.0F;
            }
        } else if (this.tackleProgress > 0.0F) {
            this.tackleProgress -= 1.0F;
        }
        if (this.landingCooldown > 0) {
            this.landingCooldown--;
        }
        if (this.tackleCooldown > 0) {
            this.tackleCooldown--;
        }
        this.setNoGravity(this.isFlying());
        if (this.level().isClientSide && this.hasSoulHeart()) {
            float radius = 0.25F + this.random.nextFloat() * 1.0F;
            float fly = this.flyProgress * 0.2F;
            float wingSpread = 15.0F + 65.0F * fly + (float) this.random.nextInt(5);
            float angle = Mth.DEG_TO_RAD * ((this.random.nextBoolean() ? -1.0F : 1.0F) * (wingSpread + 180.0F) + this.yBodyRot);
            float angleMotion = Mth.DEG_TO_RAD * this.yBodyRot;
            double extraX = radius * Mth.sin((float) Math.PI + angle);
            double extraZ = radius * Mth.cos(angle);
            double mov = this.getDeltaMovement().length();
            double extraXMotion = -mov * Mth.sin((float) Math.PI + angleMotion);
            double extraZMotion = -mov * Mth.cos(angleMotion);
            double yRandom = 0.2F + this.random.nextFloat() * 0.3F;
            this.level().addParticle(ParticleTypes.SOUL_FIRE_FLAME, this.getX() + extraX, this.getY() + yRandom, this.getZ() + extraZ, extraXMotion, this.random.nextFloat() * 0.1F, extraZMotion);
        }
    }

    @Override
    public void commandMode() {
        if (!this.isCommanded()) {
            return;
        }
        LivingEntity commandEntity = this.getCommandPosEntity();
        if (commandEntity != null && commandEntity.isAlive()) {
            this.setCommandTick(this.getCommandTick() - 1);
            if (this.getCommandTick() <= 0) {
                this.setCommandPosEntity(null);
                this.setCommandPos(null);
            } else if (this.getBoundingBox().inflate(1.25D).intersects(commandEntity.getBoundingBox())) {
                this.setCommandPosEntity(null);
                this.setCommandPos(null);
            } else {
                this.getMoveControl().setWantedPosition(commandEntity.getX(), commandEntity.getY() + commandEntity.getBbHeight() * 0.5D, commandEntity.getZ(), this.getCommandSpeed());
            }
        } else {
            BlockPos commandPos = this.getCommandPos();
            if (commandPos != null) {
                this.setCommandTick(this.getCommandTick() - 1);
                if (this.getCommandTick() <= 0 || this.getBoundingBox().inflate(0.5D).intersects(new AABB(commandPos))) {
                    this.getMoveControl().setWantedPosition(this.getX(), this.getY(), this.getZ(), 0.0D);
                    this.setCommandPos(null);
                } else {
                    this.getMoveControl().setWantedPosition(commandPos.getX() + 0.5D, commandPos.getY() + 0.5D, commandPos.getZ() + 0.5D, this.getCommandSpeed());
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void handleEntityEvent(byte id) {
        if (id == 68) {
            for (int i = 0; i < 6 + this.random.nextInt(3); i++) {
                double d2 = this.random.nextGaussian() * 0.02D;
                double d0 = this.random.nextGaussian() * 0.02D;
                double d1 = this.random.nextGaussian() * 0.02D;
                this.level().addParticle(ParticleTypes.SOUL, this.getX() + (double) (this.random.nextFloat() * this.getBbWidth()) - (double) this.getBbWidth() * 0.5D, this.getY() + (double) (this.getBbHeight() * 0.5F) + (double) (this.random.nextFloat() * this.getBbHeight() * 0.5F), this.getZ() + (double) (this.random.nextFloat() * this.getBbWidth()) - (double) this.getBbWidth() * 0.5D, d0, d1, d2);
            }
        } else {
            super.handleEntityEvent(id);
        }
    }

    public BlockPos findNewPerchPos() {
        BlockPos below = this.getBlockPosBelowThatAffectsMyMovement();
        if (this.isPerchBlock(below)) {
            return below;
        }
        BlockPos blockpos = null;
        int range = 14;
        for (int i = 0; i < 15; i++) {
            BlockPos blockpos1 = this.blockPosition().offset(this.random.nextInt(range) - range / 2, 3, this.random.nextInt(range) - range / 2);
            while (this.level().isEmptyBlock(blockpos1) && blockpos1.getY() > 1) {
                blockpos1 = blockpos1.below();
            }
            if (!this.isPerchBlock(blockpos1)) {
                continue;
            }
            blockpos = blockpos1;
        }
        return blockpos;
    }

    public boolean isTargetBlocked(Vec3 target) {
        Vec3 eyes = new Vec3(this.getX(), this.getEyeY(), this.getZ());
        return this.level().clip(new ClipContext(eyes, target, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() != HitResult.Type.MISS;
    }

    public boolean causeFallDamage(float distance, float damageMultiplier) {
        return false;
    }

    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    private class FollowFormationGoal extends Goal {
        private float radius = FORMATION_RADIUS_IDLE;
        private float orbitScale = 1.0F;
        private float radiusOffset;
        private float heightOffset;
        private int formationIndex;
        private int formationSize = 1;
        private int rescanCooldown;
        private int movingTicks;

        public FollowFormationGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        public boolean canUse() {
            return SoulVultureServant.this.canCircleOwner() && SoulVultureServant.this.isFlying();
        }

        public boolean canContinueToUse() {
            return this.canUse();
        }

        public void start() {
            this.rescanCooldown = 0;
            this.movingTicks = 0;
            this.orbitScale = 0.88F + (float) (SoulVultureServant.this.getId() % 7) * 0.04F;
            this.radiusOffset = (float) (SoulVultureServant.this.getId() % 4) * 0.6F;
            this.heightOffset = (float) (SoulVultureServant.this.getId() % 3) - 1.0F;
        }

        public void tick() {
            LivingEntity owner = SoulVultureServant.this.getTrueOwner();
            if (owner == null) {
                return;
            }
            if (this.rescanCooldown-- <= 0) {
                this.rescanCooldown = 10;
                this.rescanFormation(owner);
            }
            if (owner.getDeltaMovement().horizontalDistanceSqr() > OWNER_MOVING_THRESHOLD) {
                this.movingTicks = 20;
            } else if (this.movingTicks > 0) {
                this.movingTicks--;
            }
            boolean moving = this.movingTicks > 0;
            this.radius = Mth.lerp(0.08F, this.radius, moving ? FORMATION_RADIUS_MOVING : FORMATION_RADIUS_IDLE);
            double angle = (double) SoulVultureServant.this.level().getGameTime() * ORBIT_SPEED * (double) this.orbitScale + Mth.TWO_PI * (double) this.formationIndex / (double) this.formationSize;
            double slotRadius = (double) (this.radius + this.radiusOffset);
            double slotX = owner.getX() + slotRadius * Mth.sin((float) angle);
            double slotY = owner.getY() + OWNER_CIRCLE_HEIGHT + (double) this.heightOffset;
            double slotZ = owner.getZ() + slotRadius * Mth.cos((float) angle);
            SoulVultureServant.this.getLookControl().setLookAt(owner, 10.0F, (float) SoulVultureServant.this.getMaxHeadXRot());
            SoulVultureServant.this.getMoveControl().setWantedPosition(slotX, slotY, slotZ, moving ? FORMATION_SPEED_MOVING : FORMATION_SPEED_IDLE);
        }

        private void rescanFormation(LivingEntity owner) {
            List<SoulVultureServant> flock = SoulVultureServant.this.level().getEntitiesOfClass(SoulVultureServant.class, owner.getBoundingBox().inflate(16.0D),
                    mate -> mate.getTrueOwner() == owner && mate.isFollowing() && !mate.isCommanded() && mate.getTarget() == null);
            flock.sort(Comparator.comparingInt(Entity::getId));
            int index = flock.indexOf(SoulVultureServant.this);
            this.formationSize = Math.max(1, flock.size());
            this.formationIndex = index < 0 ? 0 : index;
        }
    }

    private class PerchGoal extends Goal {
        private float speed = 1.0F;
        private float circlingTime;
        private float circleDistance = 5.0F;
        private float maxCirclingTime = 80.0F;
        private boolean clockwise;
        private int yLevel = 1;

        public PerchGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        public boolean canUse() {
            return !SoulVultureServant.this.shouldSwoop()
                    && SoulVultureServant.this.isFlying()
                    && SoulVultureServant.this.isStationed()
                    && SoulVultureServant.this.getPerchPos() != null;
        }

        public void start() {
            this.resetCircle();
        }

        public void stop() {
            this.resetCircle();
            SoulVultureServant.this.tackleCooldown = 0;
        }

        private void resetCircle() {
            this.circlingTime = 0.0F;
            this.speed = 0.8F + SoulVultureServant.this.random.nextFloat() * 0.4F;
            this.yLevel = SoulVultureServant.this.random.nextInt(3);
            this.maxCirclingTime = 360 + SoulVultureServant.this.random.nextInt(80);
            this.circleDistance = 5.0F + SoulVultureServant.this.random.nextFloat() * 5.0F;
            this.clockwise = SoulVultureServant.this.random.nextBoolean();
        }

        public void tick() {
            BlockPos perchPos = SoulVultureServant.this.getPerchPos();
            if (perchPos == null) {
                return;
            }
            double localSpeed = this.speed;
            if (SoulVultureServant.this.getTarget() != null) {
                localSpeed *= 1.55D;
            }
            this.circlingTime += 1.0F;
            if (this.circlingTime <= this.maxCirclingTime) {
                BlockPos circlePos = this.getCirclePos(perchPos);
                if (circlePos != null) {
                    SoulVultureServant.this.getMoveControl().setWantedPosition(circlePos.getX() + 0.5D, circlePos.getY() + 0.5D, circlePos.getZ() + 0.5D, localSpeed);
                }
                return;
            }
            SoulVultureServant.this.getMoveControl().setWantedPosition(perchPos.getX() + 0.5D, perchPos.getY() + 1.1D, perchPos.getZ() + 0.5D, localSpeed);
            double distToPerch = SoulVultureServant.this.distanceToSqr(perchPos.getX() + 0.5D, perchPos.getY() + 1.1D, perchPos.getZ() + 0.5D);
            if (SoulVultureServant.this.verticalCollision || distToPerch < 1.0D) {
                SoulVultureServant.this.setFlying(false);
                SoulVultureServant.this.setDeltaMovement(Vec3.ZERO);
                SoulVultureServant.this.landingCooldown = 400 + SoulVultureServant.this.random.nextInt(1200);
                this.stop();
            }
        }

        public boolean canContinueToUse() {
            return this.canUse();
        }

        private BlockPos getCirclePos(BlockPos perchPos) {
            float angle = Mth.DEG_TO_RAD * (this.clockwise ? -this.circlingTime : this.circlingTime);
            BlockPos pos = new BlockPos((int) (perchPos.getX() + this.circleDistance * Mth.sin(angle)), perchPos.getY() + 1 + this.yLevel, (int) (perchPos.getZ() + this.circleDistance * Mth.cos(angle)));
            return SoulVultureServant.this.level().isEmptyBlock(pos) ? pos : null;
        }
    }

    private class FlyRandomGoal extends Goal {
        private BlockPos target;

        public FlyRandomGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        public boolean canUse() {
            if (!this.canRoam()) {
                return false;
            }
            MoveControl moveControl = SoulVultureServant.this.getMoveControl();
            if (!moveControl.hasWanted() || this.target == null) {
                this.target = this.getBlockInView();
                if (this.target != null) {
                    SoulVultureServant.this.getMoveControl().setWantedPosition(this.target.getX() + 0.5D, this.target.getY() + 0.5D, this.target.getZ() + 0.5D, 1.0D);
                }
                return true;
            }
            return false;
        }

        public boolean canContinueToUse() {
            return this.canRoam() && this.target != null && SoulVultureServant.this.distanceToSqr(Vec3.atCenterOf(this.target)) > 2.4D
                    && SoulVultureServant.this.getMoveControl().hasWanted() && !SoulVultureServant.this.horizontalCollision;
        }

        public void stop() {
            this.target = null;
        }

        public void tick() {
            if (this.target == null) {
                this.target = this.getBlockInView();
            }
            if (this.target != null) {
                SoulVultureServant.this.getMoveControl().setWantedPosition(this.target.getX() + 0.5D, this.target.getY() + 0.5D, this.target.getZ() + 0.5D, 1.0D);
                if (SoulVultureServant.this.distanceToSqr(Vec3.atCenterOf(this.target)) < 2.5D) {
                    this.target = null;
                }
            }
        }

        private boolean canRoam() {
            return SoulVultureServant.this.isFlying() && SoulVultureServant.this.isStationed() && !SoulVultureServant.this.isStaying()
                    && SoulVultureServant.this.getPerchPos() == null && !SoulVultureServant.this.shouldSwoop();
        }

        private BlockPos getBlockInView() {
            float radius = -9.45F - (float) SoulVultureServant.this.getRandom().nextInt(10);
            float neg = SoulVultureServant.this.getRandom().nextBoolean() ? 1.0F : -1.0F;
            float angle = Mth.DEG_TO_RAD * SoulVultureServant.this.yBodyRot + 3.15F + SoulVultureServant.this.getRandom().nextFloat() * neg;
            double extraX = radius * Mth.sin((float) Math.PI + angle);
            double extraZ = radius * Mth.cos(angle);
            BlockPos radialPos = new BlockPos((int) (SoulVultureServant.this.getX() + extraX), (int) SoulVultureServant.this.getY(), (int) (SoulVultureServant.this.getZ() + extraZ));
            while (SoulVultureServant.this.level().isEmptyBlock(radialPos) && radialPos.getY() > 2) {
                radialPos = radialPos.below();
            }
            BlockPos newPos = radialPos.above(SoulVultureServant.this.getY() - (double) radialPos.getY() > 16.0D ? 4 : SoulVultureServant.this.getRandom().nextInt(5) + 5);
            if (!SoulVultureServant.this.isTargetBlocked(Vec3.atCenterOf(newPos)) && SoulVultureServant.this.distanceToSqr(Vec3.atCenterOf(newPos)) > 6.0D) {
                return newPos;
            }
            return null;
        }
    }

    private class CircleTargetGoal extends Goal {
        private float radius = 5.0F;
        private float height = 3.0F;
        private float orbitScale = 1.0F;
        private float phase;
        private float speed = 1.0F;

        public CircleTargetGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        public boolean canUse() {
            return SoulVultureServant.this.isFlying() && SoulVultureServant.this.getTarget() != null && !SoulVultureServant.this.shouldSwoop();
        }

        public boolean canContinueToUse() {
            return this.canUse();
        }

        public void start() {
            int id = SoulVultureServant.this.getId();
            this.radius = 4.0F + (float) (id % 3);
            this.height = 2.0F + (float) (id % 3);
            this.phase = (float) (id % 8) * (Mth.TWO_PI / 8.0F);
            this.orbitScale = 0.9F + (float) (id % 5) * 0.05F;
            this.speed = (0.8F + SoulVultureServant.this.random.nextFloat() * 0.4F) * 1.55F;
        }

        public void tick() {
            LivingEntity target = SoulVultureServant.this.getTarget();
            if (target == null) {
                return;
            }
            double angle = (double) SoulVultureServant.this.level().getGameTime() * PURSUIT_ORBIT_SPEED * (double) this.orbitScale + (double) this.phase;
            SoulVultureServant.this.getLookControl().setLookAt(target, 10.0F, (float) SoulVultureServant.this.getMaxHeadXRot());
            SoulVultureServant.this.getMoveControl().setWantedPosition(
                    target.getX() + (double) this.radius * Mth.sin((float) angle),
                    target.getY() + (double) this.height,
                    target.getZ() + (double) this.radius * Mth.cos((float) angle),
                    this.speed);
        }
    }

    private class TackleGoal extends Goal {

        public TackleGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        public boolean canUse() {
            if (SoulVultureServant.this.getTarget() != null && SoulVultureServant.this.shouldSwoop()) {
                SoulVultureServant.this.setFlying(true);
                return true;
            }
            return false;
        }

        public void stop() {
            SoulVultureServant.this.setTackling(false);
        }

        public void tick() {
            SoulVultureServant.this.setTackling(SoulVultureServant.this.isFlying());
            LivingEntity target = SoulVultureServant.this.getTarget();
            if (target != null) {
                SoulVultureServant.this.getMoveControl().setWantedPosition(target.getX(), target.getY() + (double) target.getEyeHeight(), target.getZ(), 2.0D);
                double d0 = SoulVultureServant.this.getX() - target.getX();
                double d2 = SoulVultureServant.this.getZ() - target.getZ();
                float f = (float) (Mth.atan2(d2, d0) * Mth.RAD_TO_DEG) - 90.0F;
                SoulVultureServant.this.setYRot(f);
                SoulVultureServant.this.yBodyRot = SoulVultureServant.this.getYRot();
                if (SoulVultureServant.this.getBoundingBox().inflate(0.3D).intersects(target.getBoundingBox()) && SoulVultureServant.this.tackleCooldown == 0) {
                    SoulVultureServant.this.tackleCooldown = 100 + SoulVultureServant.this.random.nextInt(200);
                    float dmg = (float) SoulVultureServant.this.getAttributeValue(Attributes.ATTACK_DAMAGE);
                    if (target.hurt(SoulVultureServant.this.damageSources().mobAttack(SoulVultureServant.this), dmg) && SoulVultureServant.this.getHealth() < SoulVultureServant.this.getMaxHealth() - dmg && SoulVultureServant.this.getSoulLevel() < 5) {
                        SoulVultureServant.this.setSoulLevel(SoulVultureServant.this.getSoulLevel() + 1);
                        SoulVultureServant.this.heal(dmg);
                        SoulVultureServant.this.level().broadcastEntityEvent(SoulVultureServant.this, (byte) 68);
                    }
                    this.stop();
                }
            }
        }
    }

    class FlyMoveControl extends MoveControl {
        private final SoulVultureServant parentEntity;

        public FlyMoveControl() {
            super(SoulVultureServant.this);
            this.parentEntity = SoulVultureServant.this;
        }

        public void tick() {
            if (this.operation == MoveControl.Operation.MOVE_TO) {
                Vec3 vector3d = new Vec3(this.wantedX - this.parentEntity.getX(), this.wantedY - this.parentEntity.getY(), this.wantedZ - this.parentEntity.getZ());
                double d5 = vector3d.length();
                if (d5 < 0.3D) {
                    this.operation = MoveControl.Operation.WAIT;
                    this.parentEntity.setDeltaMovement(this.parentEntity.getDeltaMovement().scale(0.5D));
                } else {
                    this.parentEntity.setDeltaMovement(this.parentEntity.getDeltaMovement().add(vector3d.scale(this.speedModifier * 0.05D / d5)));
                    Vec3 vector3d1 = this.parentEntity.getDeltaMovement();
                    this.parentEntity.setYRot(-((float) Mth.atan2(vector3d1.x, vector3d1.z)) * Mth.RAD_TO_DEG);
                    this.parentEntity.yBodyRot = this.parentEntity.getYRot();
                }
            }
        }
    }

    public static class FlyToOwnerGoal extends Goal {
        private final SoulVultureServant summonedEntity;
        private LivingEntity owner;
        private final double followSpeed;
        private final float stopDistance;
        private final float startDistance;
        private int timeToRecalcPath;

        public FlyToOwnerGoal(SoulVultureServant summonedEntity, double speed, float startDistance, float stopDistance) {
            this.summonedEntity = summonedEntity;
            this.followSpeed = speed;
            this.startDistance = startDistance;
            this.stopDistance = stopDistance;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        public boolean canUse() {
            LivingEntity owner = this.summonedEntity.getTrueOwner();
            if (owner == null || owner.isSpectator()) {
                return false;
            }
            if (!this.summonedEntity.isFollowing() || this.summonedEntity.isCommanded()) {
                return false;
            }
            if (this.summonedEntity.getTarget() != null) {
                return false;
            }
            if (this.summonedEntity.distanceToSqr(owner) < Mth.square(this.startDistance)) {
                return false;
            }
            this.owner = owner;
            return true;
        }

        public boolean canContinueToUse() {
            if (this.owner == null || !this.owner.isAlive()) {
                return false;
            }
            if (!this.summonedEntity.isFollowing() || this.summonedEntity.isCommanded()) {
                return false;
            }
            if (this.summonedEntity.getTarget() != null) {
                return false;
            }
            return !(this.summonedEntity.distanceToSqr(this.owner) <= (double) Mth.square(this.stopDistance));
        }

        public void start() {
            this.timeToRecalcPath = 0;
        }

        public void stop() {
            this.owner = null;
            this.summonedEntity.getMoveControl().setWantedPosition(this.summonedEntity.getX(), this.summonedEntity.getY(), this.summonedEntity.getZ(), 0.0D);
        }

        public void tick() {
            if (this.owner == null) {
                return;
            }
            this.summonedEntity.getLookControl().setLookAt(this.owner, 10.0F, (float) this.summonedEntity.getMaxHeadXRot());
            if (--this.timeToRecalcPath > 0) {
                return;
            }
            this.timeToRecalcPath = 10;
            double range = this.owner instanceof Mob ? 32.0D : 16.0D;
            if (this.summonedEntity.distanceToSqr(this.owner) >= Mth.square(range) && com.Polarice3.Goety.config.MobsConfig.ServantTeleport.get()) {
                this.teleportNearOwner();
            } else {
                this.summonedEntity.getMoveControl().setWantedPosition(this.owner.getX(), this.owner.getY() + this.summonedEntity.getBbHeight() * 0.5D, this.owner.getZ(), this.followSpeed);
            }
        }

        private void teleportNearOwner() {
            BlockPos ownerPos = this.owner.blockPosition();
            for (int i = 0; i < 10; i++) {
                int x = ownerPos.getX() + this.summonedEntity.getRandom().nextInt(7) - 3;
                int y = ownerPos.getY() + this.summonedEntity.getRandom().nextInt(5) - 1;
                int z = ownerPos.getZ() + this.summonedEntity.getRandom().nextInt(7) - 3;
                if (this.tryToTeleportToLocation(x, y, z)) {
                    return;
                }
            }
        }

        private boolean tryToTeleportToLocation(int x, int y, int z) {
            if (Math.abs((double) x - this.owner.getX()) < 2.0D && Math.abs((double) z - this.owner.getZ()) < 2.0D) {
                return false;
            }
            BlockPos target = new BlockPos(x, y, z);
            if (!this.summonedEntity.level().isEmptyBlock(target) || !this.summonedEntity.level().isEmptyBlock(target.above())) {
                return false;
            }
            this.summonedEntity.moveTo((double) x + 0.5D, (double) y, (double) z + 0.5D, this.summonedEntity.getYRot(), this.summonedEntity.getXRot());
            this.summonedEntity.getMoveControl().setWantedPosition(this.summonedEntity.getX(), this.summonedEntity.getY(), this.summonedEntity.getZ(), 0.0D);
            return true;
        }
    }
}
