package com.qiuyue.goetyominous.common.entities.ally.ac;

import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.common.entities.ai.SummonTargetGoal;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.init.ModMobType;
import com.github.alexmodguy.alexscaves.server.entity.ai.GroundPathNavigatorNoSpin;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.server.entity.collision.ICustomCollisions;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.qiuyue.goetyominous.config.MobsConfig;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
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
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.FlyNodeEvaluator;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.entity.PartEntity;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.function.Predicate;

public class CorrodentServant extends Summoned implements IAnimatedEntity, ICustomCollisions {

    public static final int LIGHT_THRESHOLD = 7;
    public static final Animation ANIMATION_BITE = Animation.create(15);

    private static final EntityDataAccessor<Boolean> DIGGING = SynchedEntityData.defineId(CorrodentServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> AFRAID = SynchedEntityData.defineId(CorrodentServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> DIG_PITCH = SynchedEntityData.defineId(CorrodentServant.class, EntityDataSerializers.FLOAT);

    public final CorrodentServantTailEntity tailPart;
    public final CorrodentServantTailEntity[] allParts;
    private final float[][] trailTransformations = new float[64][2];
    protected boolean isLandNavigator;
    private int trailPointer = -1;
    private float prevDigPitch = 0.0F;
    private float fakeYRot = 0.0F;
    private float fearProgress;
    private float prevFearProgress;
    private float digProgress;
    private float prevDigProgress;
    public int timeDigging = 0;
    public int fleeLightFor = 0;
    private int regenTimer = 0;
    private boolean holdDigging = false;
    private int surfaceCooldown = 0;
    private boolean regenBurrow = false;
    private boolean prevDigging = false;
    private boolean noFallDamageOnSurface = false;
    private Vec3 surfacePosition;
    private Vec3 prevSurfacePosition;
    private Animation currentAnimation;
    private int animationTick;

    public CorrodentServant(EntityType<? extends Summoned> entityType, Level level) {
        super(entityType, level);
        this.tailPart = new CorrodentServantTailEntity(this);
        this.allParts = new CorrodentServantTailEntity[]{this.tailPart};
        this.setMaxUpStep(1.1F);
        this.switchNavigator(true);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.CorrodentServantMovementSpeed.get())
                .add(Attributes.MAX_HEALTH, AttributesConfig.CorrodentServantHealth.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.CorrodentServantFollowRange.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.CorrodentServantDamage.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, AttributesConfig.CorrodentServantKnockbackResistance.get())
                .add(Attributes.ARMOR, AttributesConfig.CorrodentServantArmor.get());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DIGGING, false);
        this.entityData.define(AFRAID, false);
        this.entityData.define(DIG_PITCH, 0.0F);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new CorrodentFearLightGoal());
        this.goalSelector.addGoal(2, new CorrodentAttackGoal());
        this.goalSelector.addGoal(2, new CorrodentDigFollowOwnerGoal());
        this.goalSelector.addGoal(2, new CorrodentDigRandomlyGoal());
        this.goalSelector.addGoal(2, new CorrodentDigInPlaceGoal());
        this.goalSelector.addGoal(7, new RandomStrollGoal(this, 1.0D, 20) {
            @Override
            public boolean canUse() {
                return !CorrodentServant.this.isStaying() && !CorrodentServant.this.isGuardingArea() && !CorrodentServant.this.regenBurrow && super.canUse();
            }

            @Override
            public boolean canContinueToUse() {
                return !CorrodentServant.this.isStaying() && !CorrodentServant.this.isGuardingArea() && !CorrodentServant.this.regenBurrow && super.canContinueToUse();
            }
        });
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 15.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
    }

    @Override
    public void followGoal() {
        this.goalSelector.addGoal(5, new CorrodentFollowOwnerGoal(this, 1.0D, 10.0F, 2.0F));
    }

    @Override
    public void targetSelectGoal() {
        super.targetSelectGoal();
        this.targetSelector.addGoal(1, new SummonTarget3DGoal());
    }

    protected void switchNavigator(boolean onLand) {
        if (onLand) {
            this.moveControl = new MoveControl(this);
            this.navigation = this.createNavigation(this.level());
            this.isLandNavigator = true;
        } else {
            this.moveControl = new DiggingMoveControl();
            this.navigation = new DiggingNavigator(this, this.level());
            this.isLandNavigator = false;
        }
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new GroundPathNavigatorNoSpin(this, level);
    }

    @Override
    public void tick() {
        super.tick();
        this.prevDigPitch = this.getDigPitch();
        this.prevDigProgress = this.digProgress;
        this.prevFearProgress = this.fearProgress;
        this.yBodyRot = Mth.approachDegrees(this.yBodyRotO, this.yBodyRot, 10.0F);
        if (!this.isDigging() || !this.isMoving()) {
            this.setDigPitch(Mth.approachDegrees(this.getDigPitch(), 0.0F, 10.0F));
        }
        this.tickMultipart();
        if (this.isDigging() && this.digProgress < 5.0F) {
            this.digProgress += 1.0F;
        }
        if (!this.isDigging() && this.digProgress > 0.0F) {
            if (this.digProgress == 5.0F) {
                this.playSound(ACSoundRegistry.CORRODENT_DIG_STOP.get());
            }
            this.digProgress -= 1.0F;
        }
        if (this.isAfraid() && this.fearProgress < 5.0F) {
            this.fearProgress += 1.0F;
        }
        if (!this.isAfraid() && this.fearProgress > 0.0F) {
            this.fearProgress -= 1.0F;
        }
        if (!this.level().isClientSide) {
            if (this.prevDigging && !this.isDigging()) {
                this.noFallDamageOnSurface = true;
            }
            this.prevDigging = this.isDigging();
            if (this.noFallDamageOnSurface && this.onGround()) {
                this.noFallDamageOnSurface = false;
            }
            if (this.surfaceCooldown > 0) {
                --this.surfaceCooldown;
            }
            if (this.isDigging()) {
                ++this.timeDigging;
                if (this.isLandNavigator) {
                    this.switchNavigator(false);
                    this.level().broadcastEntityEvent(this, (byte) 77);
                    this.setDigPitch(90.0F);
                }
                if (this.isOwnerSneaking() && this.getTarget() == null) {
                    this.holdDigging = false;
                    this.regenBurrow = false;
                    this.surfaceCooldown = 80;
                    if (this.isInWall()) {
                        this.nudgeUp(0.25F);
                    } else {
                        this.setDigging(false);
                        this.timeDigging = 0;
                        this.setPos(this.position().add(0.0, 1.0, 0.0));
                        this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.35, 0.0));
                    }
                }
                if (this.regenBurrow && this.isDigging() && !this.isRegenerating() && this.getHealth() >= this.getMaxHealth() && this.timeDigging > 40) {
                    this.regenBurrow = false;
                    if (this.isInWall()) {
                        this.nudgeUp(0.25F);
                    } else {
                        this.setDigging(false);
                        this.timeDigging = 0;
                        this.setPos(this.position().add(0.0, 1.0, 0.0));
                        this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.35, 0.0));
                    }
                }
                if (this.isDigging() && this.timeDigging > 40 && !this.isInWall() && !this.isRegenerating() && !this.holdDigging) {
                    this.setDigging(false);
                    this.setPos(this.position().add(0.0, 1.0, 0.0));
                    this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.35, 0.0));
                }
                if (this.isDigging() && this.isStaying()
                        && this.getHealth() > this.getMaxHealth() * 0.5F
                        && !this.regenBurrow && !this.holdDigging && this.getTarget() == null) {
                    if (this.isInWall()) {
                        this.nudgeUp(0.25F);
                    } else {
                        this.setDigging(false);
                        this.timeDigging = 0;
                    }
                }
                if (!CorrodentServant.isSafeDig(this.level(), this.blockPosition())) {
                    if (CorrodentServant.canDigBlock(this.level().getBlockState(this.blockPosition().above()))) {
                        this.setDeltaMovement(this.getDeltaMovement().add(0.0, 0.1, 0.0));
                    }
                    if (CorrodentServant.canDigBlock(this.level().getBlockState(this.blockPosition().below()))) {
                        this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.08, 0.0));
                    }
                }
                this.setNoGravity(this.isInWall());
            } else {
                this.timeDigging = 0;
                if (!this.isLandNavigator) {
                    this.switchNavigator(true);
                    this.level().broadcastEntityEvent(this, (byte) 77);
                    this.setDigPitch(-90.0F);
                }
                this.setNoGravity(false);
            }
            if (this.regenBurrow && !this.isDigging()) {
                this.regenBurrow = false;
            }
            if (this.isRegenerating()) {
                if (++this.regenTimer >= 20) {
                    this.regenTimer = 0;
                    this.heal(1.0F);
                }
            } else {
                this.regenTimer = 0;
            }
        } else if (this.isDigging() && this.isAlive() && this.random.nextFloat() < 0.05F) {
            this.playSound(ACSoundRegistry.CORRODENT_DIG_LOOP.get(), 0.4F, 1.0F);
        }
        this.prevSurfacePosition = this.surfacePosition;
        if (this.isMoving() || this.surfacePosition == null) {
            this.surfacePosition = this.calculateLightAbovePosition();
        }
        if (this.isDigging() && this.surfacePosition != null && this.level().isClientSide && this.isMoving()) {
            BlockState surfaceState = this.level().getBlockState(BlockPos.containing(this.surfacePosition).below());
            BlockState onState = this.getFeetBlockState();
            if (surfaceState.isSolid()) {
                Vec3 head = new Vec3(0.0, 0.0, 0.7F).yRot(-this.yBodyRot * ((float) Math.PI / 180)).add(this.getX(), this.surfacePosition.y, this.getZ());
                this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, surfaceState), true, head.x, head.y, head.z, this.random.nextFloat() - 0.5F, this.random.nextFloat() + 0.5F, this.random.nextFloat() - 0.5F);
                for (int i = 0; i < 4 + this.random.nextInt(4); ++i) {
                    float j = (float) Math.pow(i, 0.75);
                    Vec3 offset = new Vec3(i % 2 == 0 ? -j * 0.2F : j * 0.2F, 0.0, -0.3F * i).yRot(-this.yBodyRot * ((float) Math.PI / 180));
                    this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, surfaceState), true, offset.x + head.x, offset.y + head.y, offset.z + head.z, (this.random.nextFloat() - 0.5F) * 0.2F + offset.x, (this.random.nextFloat() - 0.5F) * 0.2F + offset.y, (this.random.nextFloat() - 0.5F) * 0.2F + offset.z);
                }
            }
            if (onState.isSolid()) {
                for (int i = 0; i < 2 + this.random.nextInt(2); ++i) {
                    this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, onState), true, this.getRandomX(0.8F), this.getRandomY(), this.getRandomZ(0.8F), (this.random.nextFloat() - 0.5F) * 0.2F, (this.random.nextFloat() - 0.5F) * 0.2F, (this.random.nextFloat() - 0.5F) * 0.2F);
                }
            }
        }
        if (this.fleeLightFor > 0) {
            --this.fleeLightFor;
        }
        AnimationHandler.INSTANCE.updateAnimations(this);
    }

    @Override
    public void handleEntityEvent(byte b) {
        if (b == 77) {
            float radius = 1.0F;
            float particleCount = 20 + this.random.nextInt(12);
            int i1 = 0;
            while ((float) i1 < particleCount) {
                double motionX = (this.getRandom().nextFloat() - 0.5F) * 0.7;
                double motionY = this.getRandom().nextFloat() * 0.7 + 0.8F;
                double motionZ = (this.getRandom().nextFloat() - 0.5F) * 0.7;
                float angle = (float) Math.PI / 180 * (this.yBodyRot + (float) i1 / particleCount * 360.0F);
                double extraX = radius * Mth.sin((float) (Math.PI + angle));
                double extraY = 1.2F;
                double extraZ = radius * Mth.cos((float) angle);
                BlockPos ground = BlockPos.containing(ACMath.getGroundBelowPosition(this.level(), new Vec3(Mth.floor(this.getX() + extraX), Mth.floor(this.getY() + extraY) + 2, Mth.floor(this.getZ() + extraZ))));
                BlockState groundState = this.level().getBlockState(ground);
                if (groundState.isSolid() && this.level().isClientSide) {
                    this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, groundState), true, this.getX() + extraX, ground.getY() + extraY, this.getZ() + extraZ, motionX, motionY, motionZ);
                }
                ++i1;
            }
        } else {
            super.handleEntityEvent(b);
        }
    }

    private boolean isRegenerating() {
        return this.isDigging() && !this.isAfraid() && this.getHealth() < this.getMaxHealth();
    }

    private boolean isOwnerSneaking() {
        LivingEntity owner = this.getTrueOwner();
        return owner != null && owner.isShiftKeyDown();
    }

    private void nudgeUp(float amount) {
        Vec3 current = this.getDeltaMovement();
        this.setDeltaMovement(current.x, Math.min(current.y + amount, 0.5F), current.z);
    }

    private Vec3 calculateLightAbovePosition() {
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        mutableBlockPos.set(this.getBlockX(), this.getBlockY(), this.getBlockZ());
        while (mutableBlockPos.getY() < this.level().getMaxBuildHeight() && this.level().getBlockState(mutableBlockPos).isSuffocating(this.level(), mutableBlockPos)) {
            mutableBlockPos.move(0, 1, 0);
        }
        return new Vec3(this.getX(), mutableBlockPos.getY(), this.getZ());
    }

    private void tickMultipart() {
        float digPitch = this.getDigPitch();
        if (this.trailPointer == -1) {
            this.fakeYRot = this.yBodyRot;
            for (int i = 0; i < this.trailTransformations.length; ++i) {
                this.trailTransformations[i][0] = digPitch;
                this.trailTransformations[i][1] = this.fakeYRot;
            }
        }
        this.fakeYRot = Mth.approachDegrees(this.fakeYRot, this.yBodyRot, 10.0F);
        if (++this.trailPointer == this.trailTransformations.length) {
            this.trailPointer = 0;
        }
        this.trailTransformations[this.trailPointer][0] = digPitch;
        this.trailTransformations[this.trailPointer][1] = this.fakeYRot;

        Vec3[] avector3d = new Vec3[this.allParts.length];
        for (int j = 0; j < this.allParts.length; ++j) {
            avector3d[j] = new Vec3(this.allParts[j].getX(), this.allParts[j].getY(), this.allParts[j].getZ());
        }
        this.tailPart.setToTransformation(new Vec3(0.0, 0.0, -1.0), this.getTrailTransformation(5, 0, 1.0F), this.getTrailTransformation(5, 1, 1.0F));
        for (int l = 0; l < this.allParts.length; ++l) {
            this.allParts[l].xo = avector3d[l].x;
            this.allParts[l].yo = avector3d[l].y;
            this.allParts[l].zo = avector3d[l].z;
            this.allParts[l].xOld = avector3d[l].x;
            this.allParts[l].yOld = avector3d[l].y;
            this.allParts[l].zOld = avector3d[l].z;
        }
    }

    @Override
    public boolean isMoving() {
        float f = (float) Mth.length(this.getX() - this.xo, this.getY() - this.yo, this.getZ() - this.zo);
        return f > 0.1F;
    }

    @Override
    public void calculateEntityAnimation(boolean flying) {
        float f1 = (float) Mth.length(this.getX() - this.xo, flying || this.isDigging() ? this.getY() - this.yo : 0, this.getZ() - this.zo);
        float f2 = Math.min(f1 * 8.0F, 1.0F);
        this.walkAnimation.update(f2, 0.4F);
    }

    public float getDigPitch(float partialTick) {
        return this.prevDigPitch + (this.getDigPitch() - this.prevDigPitch) * partialTick;
    }

    public float getDigAmount(float partialTick) {
        return (this.prevDigProgress + (this.digProgress - this.prevDigProgress) * partialTick) * 0.2F;
    }

    public float getAfraidAmount(float partialTick) {
        return (this.prevFearProgress + (this.fearProgress - this.prevFearProgress) * partialTick) * 0.2F;
    }

    public float getTrailTransformation(int pointer, int index, float partialTick) {
        if (this.isRemoved()) {
            partialTick = 1.0F;
        }
        int i = this.trailPointer - pointer & 0x3F;
        int j = this.trailPointer - pointer - 1 & 0x3F;
        float d0 = this.trailTransformations[j][index];
        float d1 = this.trailTransformations[i][index] - d0;
        return d0 + d1 * partialTick;
    }

    public int getCorrosionAmount(BlockPos pos) {
        double distance = this.distanceToSqr(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F);
        if (distance <= 10) {
            BlockState state = this.level().getBlockState(pos);
            if (canDigBlock(state) && !state.isAir() && !state.canBeReplaced()) {
                return 10 - (int) distance;
            }
        }
        return -1;
    }

    public Vec3 collide(Vec3 vec3) {
        return ICustomCollisions.getAllowedMovementForEntity(this, vec3);
    }

    @Override
    public boolean canPassThrough(BlockPos blockPos, BlockState blockState, VoxelShape voxelShape) {
        return this.isDigging() && canDigBlock(blockState);
    }

    @Override
    public boolean isColliding(BlockPos pos, BlockState blockstate) {
        return (!this.isDigging() || canDigBlock(blockstate)) && super.isColliding(pos, blockstate);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        return super.isInvulnerableTo(damageSource) || damageSource.is(DamageTypes.IN_WALL);
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        return this.getBoundingBox().inflate(1.0, 1.0, 1.0);
    }

    @Override
    public boolean isMultipartEntity() {
        return true;
    }

    @Override
    public PartEntity<?>[] getParts() {
        return this.allParts;
    }

    @Override
    public void remove(Entity.RemovalReason removalReason) {
        super.remove(removalReason);
        if (this.allParts != null) {
            for (PartEntity<?> part : this.allParts) {
                part.remove(Entity.RemovalReason.KILLED);
            }
        }
    }

    @Override
    public Vec3 getLightProbePosition(float f) {
        if (this.surfacePosition != null && this.prevSurfacePosition != null) {
            Vec3 difference = this.surfacePosition.subtract(this.prevSurfacePosition);
            return this.prevSurfacePosition.add(difference.scale(f)).add(0.0, this.getEyeHeight(), 0.0);
        }
        return super.getLightProbePosition(f);
    }

    @Override
    protected int calculateFallDamage(float f1, float f2) {
        if (this.noFallDamageOnSurface) {
            return 0;
        }
        return super.calculateFallDamage(f1, f2) - 5;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
        if (!this.isDigging()) {
            super.checkFallDamage(y, onGroundIn, state, pos);
        }
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        if (!this.isDigging()) {
            super.playStepSound(pos, state);
        }
    }

    public boolean canReach(BlockPos target) {
        Path path = this.getNavigation().createPath(target, 0);
        if (path == null) {
            return false;
        }
        Node node = path.getEndNode();
        if (node == null) {
            return false;
        }
        int i = node.x - target.getX();
        int j = node.y - target.getY();
        int k = node.z - target.getZ();
        return (double) (i * i + j * j + k * k) <= 3.0;
    }

    public static boolean canDigBlock(BlockState state) {
        return !state.is(ACTagRegistry.CORRODENT_BLOCKS_DIGGING) && state.getFluidState().isEmpty() && state.canOcclude();
    }

    public static boolean isSafeDig(BlockGetter level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        BlockState below = level.getBlockState(pos.below());
        return canDigBlock(state) && canDigBlock(below);
    }

    public Vec3 clampToGuardRange(Vec3 pos) {
        if (!this.isGuardingArea() || this.getBoundPos() == null) {
            return pos;
        }
        Vec3 center = Vec3.atCenterOf(this.getBoundPos());
        double dx = pos.x - center.x;
        double dz = pos.z - center.z;
        double maxDist = IServant.GUARDING_RANGE;
        if (dx * dx + dz * dz > maxDist * maxDist) {
            double dist = Math.sqrt(dx * dx + dz * dz);
            double scale = maxDist / dist;
            return new Vec3(center.x + dx * scale, pos.y, center.z + dz * scale);
        }
        return pos;
    }

    public boolean isDigging() {
        return this.entityData.get(DIGGING);
    }

    public void setDigging(boolean bool) {
        this.entityData.set(DIGGING, bool);
    }

    public boolean isAfraid() {
        return this.entityData.get(AFRAID);
    }

    public void setAfraid(boolean bool) {
        this.entityData.set(AFRAID, bool);
    }

    public void setDigPitch(float pitch) {
        this.entityData.set(DIG_PITCH, pitch);
    }

    public float getDigPitch() {
        return this.entityData.get(DIG_PITCH);
    }

    @Override
    public MobType getMobType() {
        return ModMobType.NATURAL;
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficulty,
                                        MobSpawnType spawnType, @Nullable SpawnGroupData spawnGroupData,
                                        @Nullable CompoundTag tag) {
        if (spawnType == MobSpawnType.MOB_SUMMONED && this.getTrueOwner() instanceof Player player) {
            if (countServants(player) >= MobsConfig.CorrodentServantLimit.get()) {
                return null;
            }
        }
        return super.finalizeSpawn(levelAccessor, difficulty, spawnType, spawnGroupData, tag);
    }

    @Override
    public void setTrueOwner(@Nullable LivingEntity livingEntity) {
        super.setTrueOwner(livingEntity);
        if (!this.level().isClientSide && livingEntity instanceof Player player) {
            if (countServants(player) >= MobsConfig.CorrodentServantLimit.get()) {
                this.discard();
            }
        }
    }

    @Override
    public int getSummonLimit(LivingEntity player) {
        return MobsConfig.CorrodentServantLimit.get();
    }

    @Override
    public Predicate<Entity> summonPredicate() {
        return entity -> entity instanceof CorrodentServant;
    }

    private int countServants(Player player) {
        int count = 0;
        if (player.level() instanceof ServerLevel serverLevel) {
            for (Entity entity : serverLevel.getAllEntities()) {
                if (entity instanceof CorrodentServant servant && servant != this) {
                    if (servant.getTrueOwner() == player) {
                        count++;
                    }
                }
            }
        }
        return count;
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
        return new Animation[]{ANIMATION_BITE};
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ACSoundRegistry.CORRODENT_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.CORRODENT_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.CORRODENT_DEATH.get();
    }

    private class DiggingNavigator extends FlyingPathNavigation {

        public DiggingNavigator(Mob mob, Level world) {
            super(mob, world);
        }

        @Override
        public boolean isStableDestination(BlockPos blockPos) {
            return !this.level.isEmptyBlock(blockPos) && CorrodentServant.isSafeDig(this.level, blockPos);
        }

        @Override
        protected PathFinder createPathFinder(int i) {
            this.nodeEvaluator = new DiggingNodeEvaluator();
            return new PathFinder(this.nodeEvaluator, i);
        }

        @Override
        protected double getGroundY(Vec3 vec3) {
            return vec3.y;
        }

        @Override
        protected boolean canUpdatePath() {
            return true;
        }

        @Override
        protected void followThePath() {
            Vec3 vector3d = this.getTempMobPos();
            this.maxDistanceToWaypoint = this.mob.getBbWidth();
            BlockPos vector3i = this.path.getNextNodePos();
            double d0 = Math.abs(this.mob.getX() - (vector3i.getX() + 0.5));
            double d1 = Math.abs(this.mob.getY() - vector3i.getY());
            double d2 = Math.abs(this.mob.getZ() - (vector3i.getZ() + 0.5));
            boolean flag = d0 < this.maxDistanceToWaypoint && d2 < this.maxDistanceToWaypoint && d1 <= 1.0;
            if (flag || this.canCutCorner(this.path.getNextNode().type) && this.shouldTargetNextNodeInDirection(vector3d)) {
                this.path.advance();
            }
            this.doStuckDetection(vector3d);
        }

        @Override
        protected boolean canMoveDirectly(Vec3 vec3, Vec3 vec31) {
            Vec3 vector3d = new Vec3(vec31.x, vec31.y + this.mob.getBbHeight() * 0.5, vec31.z);
            BlockHitResult result = this.level.clip(new ClipContext(vec3, vector3d, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this.mob));
            return CorrodentServant.isSafeDig(this.level, result.getBlockPos());
        }

        private boolean shouldTargetNextNodeInDirection(Vec3 currentPosition) {
            if (this.path.getNextNodeIndex() + 1 >= this.path.getNodeCount()) {
                return false;
            }
            Vec3 vector3d = Vec3.atBottomCenterOf(this.path.getNextNodePos());
            if (!currentPosition.closerThan(vector3d, 2.0)) {
                return false;
            }
            Vec3 vector3d1 = Vec3.atBottomCenterOf(this.path.getNodePos(this.path.getNextNodeIndex() + 1));
            Vec3 vector3d2 = vector3d1.subtract(vector3d);
            return vector3d2.dot(currentPosition.subtract(vector3d)) > 0.0;
        }
    }

    private class DiggingNodeEvaluator extends FlyNodeEvaluator {

        @Override
        protected BlockPathTypes evaluateBlockPathType(BlockGetter level, BlockPos pos, BlockPathTypes typeIn) {
            BlockPathTypes def = WalkNodeEvaluator.getBlockPathTypeStatic(level, pos.mutable());
            if (def == BlockPathTypes.LAVA || def == BlockPathTypes.OPEN || def == BlockPathTypes.WATER
                    || def == BlockPathTypes.WATER_BORDER || def == BlockPathTypes.DANGER_OTHER
                    || def == BlockPathTypes.DAMAGE_FIRE || def == BlockPathTypes.DANGER_POWDER_SNOW) {
                return BlockPathTypes.BLOCKED;
            }
            return CorrodentServant.isSafeDig(level, pos) && pos.getY() > level.getMinBuildHeight() ? BlockPathTypes.WALKABLE : BlockPathTypes.BLOCKED;
        }
    }

    private class DiggingMoveControl extends MoveControl {

        public DiggingMoveControl() {
            super(CorrodentServant.this);
        }

        @Override
        public void tick() {
            if (this.operation == MoveControl.Operation.MOVE_TO) {
                Vec3 vector3d = new Vec3(this.wantedX - this.mob.getX(), this.wantedY - this.mob.getY(), this.wantedZ - this.mob.getZ());
                double d0 = vector3d.length();
                double width = this.mob.getBoundingBox().getSize();
                float burySpeed = CorrodentServant.this.timeDigging < 40 ? 0.25F : 1.0F;
                double buryFactor = d0 < 1.0E-4 ? 0.0 : this.speedModifier * burySpeed * 0.025 / d0;
                Vec3 vector3d1 = vector3d.scale(buryFactor);
                if (CorrodentServant.isSafeDig(CorrodentServant.this.level(), BlockPos.containing(this.wantedX, this.wantedY, this.wantedZ))) {
                    this.mob.setDeltaMovement(this.mob.getDeltaMovement().add(vector3d1).scale(0.9F));
                } else {
                    this.mob.setDeltaMovement(this.mob.getDeltaMovement().add(0.0, 0.3, 0.0).scale(0.7F));
                    this.operation = MoveControl.Operation.WAIT;
                    this.mob.getNavigation().stop();
                }
                if (d0 < width * 0.15F) {
                    this.operation = MoveControl.Operation.WAIT;
                } else if (d0 >= width) {
                    this.mob.setYRot(-((float) Mth.atan2(vector3d1.x, vector3d1.z)) * 57.295776F);
                    float f2 = (float) (-(Mth.atan2(vector3d1.y, vector3d1.horizontalDistance()) * 57.2957763671875));
                    CorrodentServant.this.setDigPitch(Mth.approachDegrees(CorrodentServant.this.getDigPitch(), f2, 10.0F));
                }
            }
        }
    }

    private class CorrodentAttackGoal extends Goal {
        private boolean burrowing = false;
        private int burrowCheckTime = 0;
        private int evadeFor = 0;

        public CorrodentAttackGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return CorrodentServant.this.getTarget() != null && CorrodentServant.this.getTarget().isAlive()
                    && CorrodentServant.this.fleeLightFor <= 0 && !CorrodentServant.this.isRegenerating()
                    && !CorrodentServant.this.regenBurrow;
        }

        @Override
        public void tick() {
            if (CorrodentServant.this.isStaying()) {
                this.burrowing = false;
                this.evadeFor = 0;
            }
            LivingEntity target = CorrodentServant.this.getTarget();
            if (target != null) {
                double dist = CorrodentServant.this.distanceTo(target);
                float f = CorrodentServant.this.getBbWidth() + target.getBbWidth();
                if (this.burrowCheckTime++ > 40 && CorrodentServant.this.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
                    this.burrowCheckTime = 0;
                    if (!this.burrowing) {
                        if (CorrodentServant.this.onGround() && dist > f && !CorrodentServant.this.isStaying() && (!CorrodentServant.this.canReach(target.blockPosition()) || dist > 20.0 || CorrodentServant.this.getRandom().nextInt(20) == 0)) {
                            this.burrowing = true;
                            this.evadeFor = 60 + CorrodentServant.this.getRandom().nextInt(40);
                        }
                    } else if (dist < f + 1.0F || CorrodentServant.this.getRandom().nextInt(10) == 0) {
                        this.burrowing = false;
                        this.evadeFor = 0;
                    }
                }
                if (this.evadeFor > 0) {
                    --this.evadeFor;
                    this.burrowing = true;
                    CorrodentServant.this.setDigging(true);
                    Vec3 vec3 = this.generateEvadePosition(target.blockPosition());
                    if (vec3 != null) {
                        vec3 = CorrodentServant.this.clampToGuardRange(vec3);
                    }
                    if (CorrodentServant.this.getNavigation().isDone() && vec3 != null) {
                        CorrodentServant.this.getNavigation().moveTo(vec3.x, vec3.y, vec3.z, 1.0);
                    }
                } else if (this.burrowing) {
                    if (CorrodentServant.this.onGround()) {
                        CorrodentServant.this.setDigging(true);
                    }
                    this.moveToClamped(target, 2.0);
                    if (!CorrodentServant.this.isInWall()) {
                        CorrodentServant.this.setDigging(false);
                        this.burrowing = false;
                    }
                } else {
                    if (!CorrodentServant.this.isInWall()) {
                        CorrodentServant.this.setDigging(false);
                    } else {
                        CorrodentServant.this.setDigging(true);
                        CorrodentServant.this.setDeltaMovement(CorrodentServant.this.getDeltaMovement().add(0.0, 0.1, 0.0));
                    }
                    this.moveToClamped(target, 1.5);
                }
                if (dist < f + 1.0F) {
                    this.tryAnimation(CorrodentServant.ANIMATION_BITE);
                }
                if (CorrodentServant.this.getAnimation() == CorrodentServant.ANIMATION_BITE) {
                    CorrodentServant.this.setDigging(false);
                    if (CorrodentServant.this.getAnimationTick() == 8) {
                        this.checkAndDealDamage(target, 1.5F);
                        if (CorrodentServant.this.getRandom().nextBoolean()) {
                            this.evadeFor = 60 + CorrodentServant.this.getRandom().nextInt(40);
                        }
                    }
                }
            }
        }

        private Vec3 generateEvadePosition(BlockPos around) {
            BlockPos.MutableBlockPos check = new BlockPos.MutableBlockPos();
            for (int i = 0; i < 10; ++i) {
                check.move(around);
                check.move(CorrodentServant.this.getRandom().nextInt(16) - 8, CorrodentServant.this.getRandom().nextInt(16) - 8, CorrodentServant.this.getRandom().nextInt(16) - 8);
                if (!CorrodentServant.this.level().isLoaded(check) || check.getY() < CorrodentServant.this.level().getMinBuildHeight()) {
                    break;
                }
                while (CorrodentServant.this.level().isEmptyBlock(check) && CorrodentServant.this.level().isLoaded(check) && check.getY() > CorrodentServant.this.level().getMinBuildHeight() - 1) {
                    check.move(0, -1, 0);
                }
                if (!CorrodentServant.isSafeDig(CorrodentServant.this.level(), check.immutable()) || !CorrodentServant.this.canReach(check)) {
                    continue;
                }
                return Vec3.atCenterOf(check.immutable());
            }
            return null;
        }

        private void moveToClamped(LivingEntity target, double speed) {
            if (CorrodentServant.this.isGuardingArea() && CorrodentServant.this.getBoundPos() != null) {
                Vec3 clamped = CorrodentServant.this.clampToGuardRange(target.position());
                CorrodentServant.this.getNavigation().moveTo(clamped.x, clamped.y, clamped.z, speed);
            } else {
                CorrodentServant.this.getNavigation().moveTo(target, speed);
            }
        }

        private void checkAndDealDamage(LivingEntity target, float multiplier) {
            if (CorrodentServant.this.hasLineOfSight(target) && CorrodentServant.this.distanceTo(target) < CorrodentServant.this.getBbWidth() + target.getBbWidth() + 1.0) {
                float f = (float) CorrodentServant.this.getAttribute(Attributes.ATTACK_DAMAGE).getValue() * multiplier;
                target.hurt(target.damageSources().mobAttack(CorrodentServant.this), f);
                target.knockback(0.2 + 0.3 * multiplier, CorrodentServant.this.getX() - target.getX(), CorrodentServant.this.getZ() - target.getZ());
                Entity entity = target.getVehicle();
                if (entity != null) {
                    entity.setDeltaMovement(target.getDeltaMovement());
                    entity.hurt(target.damageSources().mobAttack(CorrodentServant.this), f * 0.5F);
                }
                this.burrowing = true;
            }
        }

        @Override
        public void stop() {
            this.burrowing = false;
            this.burrowCheckTime = 0;
            this.evadeFor = 0;
        }

        private boolean tryAnimation(Animation animation) {
            if (CorrodentServant.this.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
                CorrodentServant.this.setAnimation(animation);
                CorrodentServant.this.playSound(ACSoundRegistry.CORRODENT_ATTACK.get());
                return true;
            }
            return false;
        }
    }

    private class CorrodentFearLightGoal extends Goal {
        private Vec3 retreatTo = null;
        private int tryDigTime = 0;
        private BlockPos tryDigPos = null;

        public CorrodentFearLightGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return CorrodentServant.this.getHealth() <= CorrodentServant.this.getMaxHealth() * 0.5F
                    && CorrodentServant.this.level().getBrightness(LightLayer.BLOCK, CorrodentServant.this.blockPosition()) > LIGHT_THRESHOLD
                    && !CorrodentServant.this.isDigging()
                    && !CorrodentServant.this.isStaying()
                    && !CorrodentServant.this.isOwnerSneaking();
        }

        @Override
        public void tick() {
            CorrodentServant.this.fleeLightFor = 50;
            CorrodentServant.this.setAfraid(true);
            CorrodentServant.this.getNavigation().stop();
            if (this.retreatTo == null) {
                float dir = CorrodentServant.this.getRandom().nextFloat() * (float) Math.PI * 2.0F;
                this.retreatTo = CorrodentServant.this.position().add(Math.cos(dir) * 2.0F, 0, Math.sin(dir) * 2.0F);
            }
            Vec3 flip = this.retreatTo.subtract(CorrodentServant.this.position()).yRot(1.5707964F).add(CorrodentServant.this.position());
            CorrodentServant.this.lookAt(EntityAnchorArgument.Anchor.EYES, flip);
            if (CorrodentServant.this.onGround() && this.tryDigTime++ > 20) {
                this.tryDigTime = 0;
                if (this.tryDigPos != null && this.tryDigPos.distSqr(CorrodentServant.this.blockPosition()) < 2.25) {
                    CorrodentServant.this.setDigging(true);
                }
                this.tryDigPos = CorrodentServant.this.blockPosition();
            }
        }

        @Override
        public void stop() {
            CorrodentServant.this.setAfraid(false);
            if (CorrodentServant.this.onGround() && !CorrodentServant.this.isOwnerSneaking()) {
                CorrodentServant.this.fleeLightFor = 50;
                CorrodentServant.this.setDigging(true);
            }
            if (!CorrodentServant.this.isOwnerSneaking()) {
                CorrodentServant.this.regenBurrow = true;
            }
            this.tryDigPos = null;
            this.tryDigTime = 0;
            this.retreatTo = null;
        }
    }

    private class CorrodentDigRandomlyGoal extends Goal {
        private double x;
        private double y;
        private double z;
        private boolean surface = false;

        public CorrodentDigRandomlyGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (CorrodentServant.this.isVehicle() || CorrodentServant.this.getTarget() != null && CorrodentServant.this.getTarget().isAlive()
                    || CorrodentServant.this.isPassenger() || !CorrodentServant.this.isDigging() && !CorrodentServant.this.onGround() && !CorrodentServant.this.isInWall()) {
                return false;
            }
            if (CorrodentServant.this.isStaying()) {
                return false;
            }
            if (CorrodentServant.this.regenBurrow) {
                return false;
            }
            if (CorrodentServant.this.isOwnerSneaking()) {
                return false;
            }
            if (!CorrodentServant.this.isDigging() && !CorrodentServant.this.isInWall() && CorrodentServant.this.getRandom().nextInt(20) != 0) {
                return false;
            }
            if (CorrodentServant.this.isDigging() && CorrodentServant.this.timeDigging > 300) {
                this.surface = true;
            }
            if (CorrodentServant.this.fleeLightFor > 0 || CorrodentServant.this.isAfraid()
                    || CorrodentServant.this.isCommanded() || CorrodentServant.this.isRegenerating()) {
                return false;
            }
            Vec3 target = this.generatePosition();
            if (target == null) {
                return false;
            }
            this.x = target.x;
            this.y = target.y;
            this.z = target.z;
            return true;
        }

        @Override
        public void start() {
            CorrodentServant.this.setDigging(true);
            CorrodentServant.this.getNavigation().moveTo(this.x, this.y, this.z, 1.0);
        }

        @Override
        public boolean canContinueToUse() {
            return !CorrodentServant.this.getNavigation().isDone() && !CorrodentServant.this.getNavigation().isStuck() && CorrodentServant.this.isDigging();
        }

        @Override
        public void tick() {
            if (this.surface && CorrodentServant.this.distanceToSqr(this.x, this.y, this.z) < 4.0) {
                CorrodentServant.this.setDigging(false);
            }
        }

        @Override
        public void stop() {
            this.surface = false;
        }

        private Vec3 generatePosition() {
            BlockPos.MutableBlockPos check = new BlockPos.MutableBlockPos();
            for (int i = 0; i < 20; ++i) {
                if (CorrodentServant.this.isGuardingArea() && CorrodentServant.this.getBoundPos() != null) {
                    BlockPos bound = CorrodentServant.this.getBoundPos();
                    float angle = CorrodentServant.this.getRandom().nextFloat() * (float) Math.PI * 2.0F;
                    double radius = CorrodentServant.this.getRandom().nextDouble() * (double) IServant.GUARDING_RANGE;
                    int ox = (int) (Math.cos(angle) * radius);
                    int oz = (int) (Math.sin(angle) * radius);
                    check.set(bound.getX() + ox, CorrodentServant.this.blockPosition().getY() + CorrodentServant.this.getRandom().nextInt(32) - 16, bound.getZ() + oz);
                } else {
                    check.move(CorrodentServant.this.blockPosition());
                    check.move(CorrodentServant.this.getRandom().nextInt(32) - 16, CorrodentServant.this.getRandom().nextInt(32) - 16, CorrodentServant.this.getRandom().nextInt(32) - 16);
                }
                if (check.getY() < CorrodentServant.this.level().getMinBuildHeight() || !CorrodentServant.this.level().isLoaded(check)) {
                    break;
                }
                if (this.surface) {
                    while (!CorrodentServant.this.level().isEmptyBlock(check) && check.getY() < CorrodentServant.this.level().getMaxBuildHeight()) {
                        check.move(0, 1, 0);
                    }
                    if (!CorrodentServant.this.level().isEmptyBlock(check)) {
                        continue;
                    }
                    return check.immutable().getCenter();
                }
                while (CorrodentServant.this.level().isEmptyBlock(check) && check.getY() > CorrodentServant.this.level().getMinBuildHeight() - 1) {
                    check.move(0, -1, 0);
                }
                if (!CorrodentServant.isSafeDig(CorrodentServant.this.level(), check.immutable()) || !CorrodentServant.this.canReach(check)) {
                    continue;
                }
                return Vec3.atCenterOf(check.immutable());
            }
            return null;
        }
    }

    private class CorrodentDigInPlaceGoal extends Goal {
        private Vec3 anchor = null;

        public CorrodentDigInPlaceGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return CorrodentServant.this.isGuardingArea()
                    && CorrodentServant.this.getHealth() <= CorrodentServant.this.getMaxHealth() * 0.5F
                    && !CorrodentServant.this.isOwnerSneaking()
                    && CorrodentServant.this.getTarget() == null
                    && CorrodentServant.this.fleeLightFor <= 0
                    && !CorrodentServant.this.isAfraid()
                    && !CorrodentServant.this.isCommanded()
                    && CorrodentServant.this.surfaceCooldown <= 0;
        }

        @Override
        public boolean canContinueToUse() {
            return CorrodentServant.this.isGuardingArea()
                    && CorrodentServant.this.getHealth() <= CorrodentServant.this.getMaxHealth() * 0.5F
                    && !CorrodentServant.this.isOwnerSneaking()
                    && CorrodentServant.this.getTarget() == null
                    && CorrodentServant.this.fleeLightFor <= 0
                    && !CorrodentServant.this.isAfraid()
                    && !CorrodentServant.this.isCommanded()
                    && CorrodentServant.this.holdDigging;
        }

        @Override
        public void start() {
            CorrodentServant.this.holdDigging = true;
            this.anchor = CorrodentServant.this.clampToGuardRange(CorrodentServant.this.position());
        }

        @Override
        public void tick() {
            CorrodentServant.this.holdDigging = true;
            if (this.anchor == null) {
                this.anchor = CorrodentServant.this.clampToGuardRange(CorrodentServant.this.position());
            }
            if (CorrodentServant.this.position().distanceToSqr(this.anchor) > 1.0) {
                CorrodentServant.this.getNavigation().moveTo(this.anchor.x, this.anchor.y, this.anchor.z, 1.0);
            } else {
                this.digInPlace();
            }
        }

        @Override
        public void stop() {
            CorrodentServant.this.holdDigging = false;
            this.anchor = null;
        }

        private void digInPlace() {
            CorrodentServant.this.getNavigation().stop();
            if (CorrodentServant.this.isInWall()
                    || CorrodentServant.canDigBlock(CorrodentServant.this.level().getBlockState(CorrodentServant.this.blockPosition()))
                    || CorrodentServant.canDigBlock(CorrodentServant.this.level().getBlockState(CorrodentServant.this.blockPosition().below()))) {
                CorrodentServant.this.setDigging(true);
            }
        }
    }

    private class SummonTarget3DGoal extends SummonTargetGoal {
        public SummonTarget3DGoal() {
            super(CorrodentServant.this, false, false);
        }

        @Override
        protected AABB getTargetSearchArea(double distance) {
            return this.mob.getBoundingBox().inflate(distance, distance, distance);
        }

    }

    private class CorrodentFollowOwnerGoal extends Goal {
        private final CorrodentServant summonedEntity;
        private LivingEntity owner;
        private final double followSpeed;
        private final float startDistance;
        private final float stopDistance;
        private int timeToRecalcPath;

        public CorrodentFollowOwnerGoal(CorrodentServant summonedEntity, double speed, float startDistance, float stopDistance) {
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
            } else if (this.summonedEntity.isDigging() || this.summonedEntity.isAfraid()) {
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
            } else if (this.summonedEntity.isDigging() || this.summonedEntity.isAfraid()) {
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
            if (Math.abs(x - this.owner.getX()) < 2.0D && Math.abs(z - this.owner.getZ()) < 2.0D) {
                return false;
            } else if (!this.isTeleportFriendlyBlock(new BlockPos(x, y, z))) {
                return false;
            } else {
                this.summonedEntity.moveTo(x + 0.5D, y, z + 0.5D, this.summonedEntity.getYRot(), this.summonedEntity.getXRot());
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

    private class CorrodentDigFollowOwnerGoal extends Goal {
        private final CorrodentServant summonedEntity;
        private LivingEntity owner;
        private int timeToRecalcPath;
        private int noPathTime;

        public CorrodentDigFollowOwnerGoal() {
            this.summonedEntity = CorrodentServant.this;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
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
            } else if (this.summonedEntity.getTarget() != null) {
                return false;
            } else if (!this.summonedEntity.isFollowing() || this.summonedEntity.isCommanded()) {
                return false;
            } else if (!this.summonedEntity.isDigging()) {
                return false;
            } else if (this.summonedEntity.isAfraid() || this.summonedEntity.regenBurrow || this.summonedEntity.isOwnerSneaking()) {
                return false;
            } else if (this.summonedEntity.distanceToSqr(livingentity) < (double) Mth.square(8.0F)) {
                return false;
            } else {
                this.owner = livingentity;
                return true;
            }
        }

        @Override
        public boolean canContinueToUse() {
            if (this.owner == null || !this.owner.isAlive()) {
                return false;
            } else if (this.summonedEntity.getTarget() != null) {
                return false;
            } else if (this.summonedEntity.isPassenger()) {
                return false;
            } else if (!this.summonedEntity.isFollowing() || this.summonedEntity.isCommanded()) {
                return false;
            } else if (!this.summonedEntity.isDigging()) {
                return false;
            } else if (this.summonedEntity.isAfraid() || this.summonedEntity.regenBurrow || this.summonedEntity.isOwnerSneaking()) {
                return false;
            } else {
                return this.summonedEntity.distanceToSqr(this.owner) > (double) Mth.square(4.0F);
            }
        }

        @Override
        public void start() {
            this.timeToRecalcPath = 0;
            this.noPathTime = 0;
        }

        @Override
        public void stop() {
            this.owner = null;
            this.summonedEntity.getNavigation().stop();
        }

        @Override
        public void tick() {
            if (this.owner == null) {
                return;
            }
            if (this.summonedEntity.getNavigation().isDone()) {
                if (++this.noPathTime > 40) {
                    this.summonedEntity.getNavigation().stop();
                    this.owner = null;
                    return;
                }
            } else {
                this.noPathTime = 0;
            }
            if (--this.timeToRecalcPath <= 0) {
                this.timeToRecalcPath = 10;
                this.summonedEntity.getNavigation().moveTo(this.owner, 1.0D);
            }
        }
    }
}
