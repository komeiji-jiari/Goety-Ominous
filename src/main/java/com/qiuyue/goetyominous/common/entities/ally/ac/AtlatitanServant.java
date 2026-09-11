package com.qiuyue.goetyominous.common.entities.ally.ac;

import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.AnimalSummon;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.entities.projectiles.FlyingItem;
import com.Polarice3.Goety.init.ModMobType;
import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.DinosaurEggBlock;
import com.github.alexmodguy.alexscaves.server.block.PewenBranchBlock;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ai.AdvancedPathNavigateNoTeleport;
import com.github.alexmodguy.alexscaves.server.entity.item.CrushedBlockEntity;
import com.github.alexmodguy.alexscaves.server.entity.item.FallingTreeBlockEntity;
import com.github.alexmodguy.alexscaves.server.entity.util.KaijuMob;
import com.github.alexmodguy.alexscaves.server.entity.util.KeybindUsingMount;
import com.github.alexmodguy.alexscaves.server.entity.util.LaysEggs;
import com.github.alexmodguy.alexscaves.server.entity.util.LuxtructosaurusLegSolver;
import com.github.alexmodguy.alexscaves.server.entity.util.MovingBlockData;
import com.github.alexmodguy.alexscaves.server.entity.util.ShakesScreen;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.message.MountedEntityKeyMessage;
import com.github.alexmodguy.alexscaves.server.misc.ACAdvancementTriggerRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.server.entity.pathfinding.raycoms.IAdvancedPathingMob;
import com.github.alexthe666.citadel.server.entity.pathfinding.raycoms.ITallWalker;
import com.qiuyue.goetyominous.common.entities.ai.ac.ServantBreedGoal;
import com.qiuyue.goetyominous.common.entities.ai.ac.ServantLayEggGoal;
import com.qiuyue.goetyominous.common.init.ac.AcBlockRegistry;
import com.qiuyue.goetyominous.common.init.ac.AcEntityRegistry;
import com.qiuyue.goetyominous.common.items.ac.AcItems;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.qiuyue.goetyominous.config.MobsConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
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
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.PlayerRideable;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.event.ForgeEventFactory;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Optional;
import java.util.UUID;

public class AtlatitanServant extends AnimalSummon
        implements LaysEggs, KeybindUsingMount, IAnimatedEntity, ShakesScreen, KaijuMob,
        ITallWalker, IAdvancedPathingMob, PlayerRideable {

    protected static final EntityDataAccessor<Boolean> WALKING =
            SynchedEntityData.defineId(AtlatitanServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Optional<BlockPos>> EATING_POS =
            SynchedEntityData.defineId(AtlatitanServant.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    private static final EntityDataAccessor<Optional<BlockState>> LAST_EATEN_BLOCK =
            SynchedEntityData.defineId(AtlatitanServant.class, EntityDataSerializers.OPTIONAL_BLOCK_STATE);
    private static final EntityDataAccessor<Float> METER_AMOUNT =
            SynchedEntityData.defineId(AtlatitanServant.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> DATA_HAS_EGG =
            SynchedEntityData.defineId(AtlatitanServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> ALT_SKIN =
            SynchedEntityData.defineId(AtlatitanServant.class, EntityDataSerializers.INT);

    public static final Animation ANIMATION_SPEAK = Animation.create(15);
    public static final Animation ANIMATION_STOMP = Animation.create(50);
    public static final Animation ANIMATION_LEFT_KICK = Animation.create(20);
    public static final Animation ANIMATION_RIGHT_KICK = Animation.create(20);
    public static final Animation ANIMATION_LEFT_WHIP = Animation.create(40);
    public static final Animation ANIMATION_RIGHT_WHIP = Animation.create(40);
    public static final Animation ANIMATION_EAT_LEAVES = Animation.create(100);
    private static final int STOMP_CRUSH_HEIGHT = 6;

    public LuxtructosaurusLegSolver legSolver = new LuxtructosaurusLegSolver(0.2F, 2.0F, 1.2F, 1.9F, 2.0F);
    private Animation currentAnimation;
    private int animationTick;
    private final AtlatitanServantPartEntity[] allParts;
    public final AtlatitanServantPartEntity neckPart1;
    public final AtlatitanServantPartEntity neckPart2;
    public final AtlatitanServantPartEntity neckPart3;
    public final AtlatitanServantPartEntity headPart;
    public final AtlatitanServantPartEntity tailPart1;
    public final AtlatitanServantPartEntity tailPart2;
    public final AtlatitanServantPartEntity tailPart3;
    private int lSteps;
    private double lx;
    private double ly;
    private double lz;
    private double lyr;
    private double lxr;
    private double lxd;
    private double lyd;
    private double lzd;
    private float prevWalkAnimPosition;
    private float walkAnimPosition;
    private float prevWalkAnimSpeed;
    private float walkAnimSpeed;
    private double lastStompX;
    private double lastStompZ;
    private float prevLegBackAmount = 0.0F;
    private float legBackAmount = 0.0F;
    private float prevRaiseArmsAmount = 0.0F;
    private float raiseArmsAmount = 0.0F;
    protected float neckXRot;
    protected float neckYRot;
    protected float tailXRot;
    protected float tailYRot;
    private float prevScreenShakeAmount;
    protected float screenShakeAmount;
    private final float[] yawBuffer = new float[128];
    private int yawPointer = -1;
    private float lastYawBeforeWhip;
    public boolean turningFast;
    private boolean wasPreviouslyChild;
    private int stepSoundCooldown = 0;
    private boolean followingStanceEnforced = false;

    public AtlatitanServant(EntityType<? extends Owned> type, Level level) {
        super(type, level);
        this.setPathfindingMalus(BlockPathTypes.LAVA, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.LEAVES, 0.0F);
        this.setMaxUpStep(1.6F);
        this.moveControl = new SauropodMoveHelper();
        this.neckPart1 = new AtlatitanServantPartEntity(this, this, 3.0F, 3.0F);
        this.neckPart2 = new AtlatitanServantPartEntity(this, this.neckPart1, 2.0F, 2.0F);
        this.neckPart3 = new AtlatitanServantPartEntity(this, this.neckPart2, 2.0F, 1.5F);
        this.headPart = new AtlatitanServantPartEntity(this, this.neckPart3, 2.0F, 2.0F);
        this.tailPart1 = new AtlatitanServantPartEntity(this, this, 3.0F, 2.0F);
        this.tailPart2 = new AtlatitanServantPartEntity(this, this.tailPart1, 2.5F, 1.5F);
        this.tailPart3 = new AtlatitanServantPartEntity(this, this.tailPart2, 2.0F, 1.0F);
        this.allParts = new AtlatitanServantPartEntity[]{
                this.neckPart1, this.neckPart2, this.neckPart3, this.headPart,
                this.tailPart1, this.tailPart2, this.tailPart3};
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.AtlatitanServantHealth.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.AtlatitanServantFollowRange.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.AtlatitanServantDamage.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, AttributesConfig.AtlatitanServantKnockbackResistance.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.AtlatitanServantMovementSpeed.get())
                .add(Attributes.ARMOR, AttributesConfig.AtlatitanServantArmor.get());
    }

    public static int countServants(ServerLevel level, UUID ownerId) {
        int count = 0;
        if (ownerId == null) {
            return count;
        }
        for (Entity entity : level.getAllEntities()) {
            if (entity instanceof AtlatitanServant servant && ownerId.equals(servant.getOwnerId())) {
                count++;
            }
        }
        return count;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(WALKING, false);
        this.entityData.define(EATING_POS, Optional.empty());
        this.entityData.define(LAST_EATEN_BLOCK, Optional.empty());
        this.entityData.define(METER_AMOUNT, 1.0F);
        this.entityData.define(DATA_HAS_EGG, false);
        this.entityData.define(ALT_SKIN, 0);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new AdvancedPathNavigateNoTeleport(this, level);
    }

    @Override
    public MobType getMobType() {
        return ModMobType.NATURAL;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new AtlatitanServantMeleeGoal(this));
        this.goalSelector.addGoal(2, new ServantBreedGoal<>(this, 1.0D));
        this.goalSelector.addGoal(3, new ServantLayEggGoal<>(this, (DinosaurEggBlock) this.createEggBlockState().getBlock(), 100, 1.0D));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.1D, Ingredient.of(ACBlockRegistry.TREE_STAR.get()), false));
        this.goalSelector.addGoal(6, new AtlatitanServantNibbleTreesGoal(this, 30));
        this.goalSelector.addGoal(7, new Summoned.WanderGoal<>(this, 1.0D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 32.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Mob.class, 8.0F));
    }

    @Override
    public void followGoal() {
        this.goalSelector.addGoal(5, new AtlatitanServantFollowGoal(this, 1.0D, 12.0F, 4.0F));
    }

    @Override
    protected void updateControlFlags() {
        super.updateControlFlags();
        boolean steering = this.getControllingPassenger() instanceof Player player
                && (player.zza != 0.0F || player.xxa != 0.0F);
        this.goalSelector.setControlFlag(Goal.Flag.MOVE, !steering);
        this.goalSelector.setControlFlag(Goal.Flag.JUMP, !steering);
        this.goalSelector.setControlFlag(Goal.Flag.LOOK, !steering);
    }

    @Override
    public boolean stopTickingPathing() {
        return this.getControllingPassenger() != null || this.isStaying();
    }

    @Override
    public boolean isAlliedTo(Entity entity) {
        if (super.isAlliedTo(entity)) {
            return true;
        }
        return this.level().isClientSide && entity != null
                && this.getOwnerId() != null && this.getOwnerId().equals(entity.getUUID());
    }

    public boolean isFakeEntity() {
        return this.firstTick;
    }

    @Override
    public void tick() {
        super.tick();
        this.enforceFollowingStanceOnce();
        AnimationHandler.INSTANCE.updateAnimations(this);
        this.prevRaiseArmsAmount = this.raiseArmsAmount;
        this.prevScreenShakeAmount = this.screenShakeAmount;
        this.legSolver.update(this, this.yBodyRot, this.getScale());
        if (this.shouldRaiseArms() && this.raiseArmsAmount < 5.0F) {
            this.raiseArmsAmount += 1.0F;
        }
        if (!this.shouldRaiseArms() && this.raiseArmsAmount > 0.0F) {
            this.raiseArmsAmount -= 1.0F;
        }
        if (this.screenShakeAmount > 0.0F) {
            this.screenShakeAmount = Math.max(0.0F, this.screenShakeAmount - 0.3F);
        }
        if (this.getAnimation() != ANIMATION_LEFT_WHIP && this.getAnimation() != ANIMATION_RIGHT_WHIP) {
            this.yBodyRot = Mth.approachDegrees(this.yBodyRotO, this.getYRot(), this.turningFast ? 10.0F : 2.0F);
            this.lastYawBeforeWhip = this.getYRot();
        } else {
            float negative = this.getAnimation() == ANIMATION_RIGHT_WHIP ? -1.0F : 1.0F;
            float target = 0.0F;
            if (this.getAnimationTick() > 10) {
                float f = (float) (this.getAnimationTick() - 10) / 30.0F;
                target = f * 230.0F;
            }
            this.yBodyRot = (float) this.getAnimationTick() > 30.0F
                    ? Mth.approachDegrees(this.yBodyRotO, this.lastYawBeforeWhip, 15.0F)
                    : Mth.approachDegrees(this.yBodyRotO, this.lastYawBeforeWhip + negative * target, 90.0F);
        }
        this.tickMultipart();
        this.tickWalking();
        if (this.level().isClientSide) {
            if (this.lSteps > 0) {
                double d5 = this.getX() + (this.lx - this.getX()) / (double) this.lSteps;
                double d6 = this.getY() + (this.ly - this.getY()) / (double) this.lSteps;
                double d7 = this.getZ() + (this.lz - this.getZ()) / (double) this.lSteps;
                this.setYRot(Mth.wrapDegrees((float) this.lyr));
                this.setXRot(this.getXRot() + (float) (this.lxr - (double) this.getXRot()) / (float) this.lSteps);
                --this.lSteps;
                this.setPos(d5, d6, d7);
            } else {
                this.reapplyPosition();
            }
        }
        if (this.getAnimation() == ANIMATION_STOMP && this.getAnimationTick() > 25 && this.getAnimationTick() < 35
                && this.screenShakeAmount <= 2.0F) {
            this.screenShakeAmount = 2.0F;
        }
        if (this.wasPreviouslyChild != this.isBaby()) {
            this.wasPreviouslyChild = this.isBaby();
            this.refreshDimensions();
            for (AtlatitanServantPartEntity part : this.allParts) {
                part.refreshDimensions();
            }
        }
        this.tickAtlatitan();
    }

    private void tickAtlatitan() {
        if (this.level().isClientSide) {
            if (this.getAnimation() == ANIMATION_EAT_LEAVES && this.getAnimationTick() > 35 && this.getAnimationTick() < 90) {
                BlockState lastEatenBlock = this.getLastEatenBlock();
                if (lastEatenBlock != null) {
                    Vec3 crumbPos = this.headPart.position().add(
                            (this.random.nextFloat() - 0.5F) * 2.0F * this.getScale(),
                            (0.5F + (this.random.nextFloat() - 0.5F) * 0.2F) * this.getScale(),
                            (this.random.nextFloat() - 0.5F) * 2.0F * this.getScale());
                    this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, lastEatenBlock),
                            crumbPos.x, crumbPos.y, crumbPos.z,
                            (this.random.nextFloat() - 0.5D) * 0.1D,
                            (this.random.nextFloat() - 0.5D) * 0.1D,
                            (this.random.nextFloat() - 0.5D) * 0.1D);
                }
            }
            Player player = AlexsCaves.PROXY.getClientSidePlayer();
            if (player != null && player.isPassengerOfSameVehicle(this)
                    && AlexsCaves.PROXY.isKeyDown(2) && this.getMeterAmount() >= 1.0F) {
                AlexsCaves.sendMSGToServer(new MountedEntityKeyMessage(this.getId(), player.getId(), 2));
            }
        } else {
            if (this.getAnimation() == ANIMATION_STOMP && this.getAnimationTick() == 30) {
                this.playSound(ACSoundRegistry.ATLATITAN_STOMP.get(), 3.0F, 1.0F);
                if (this.screenShakeAmount < 4.0F) {
                    this.screenShakeAmount = 4.0F;
                }
                this.crushBlocksInRing(15, this.getBlockX(), this.getBlockZ(), 1.0F);
                if (this.isVehicle()) {
                    for (Entity passenger : this.getPassengers()) {
                        ACAdvancementTriggerRegistry.ATLATITAN_STOMP.triggerForEntity(passenger);
                    }
                }
            }
            if (this.tickCount % 100 == 0 && this.getHealth() < this.getMaxHealth()) {
                this.heal(2.0F);
            }
            if (this.getAnimation() == ANIMATION_RIGHT_KICK && this.getAnimationTick() == 8) {
                Vec3 armPos = this.position().add(this.rotateOffsetVec(new Vec3(-2, 0, 2.5F), 0, this.yBodyRot));
                this.hurtEntitiesAround(armPos, 5.0F, (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.8F, 1.0F, false, false);
            }
            if (this.getAnimation() == ANIMATION_LEFT_KICK && this.getAnimationTick() == 8) {
                Vec3 armPos = this.position().add(this.rotateOffsetVec(new Vec3(2, 0, 2.5F), 0, this.yBodyRot));
                this.hurtEntitiesAround(armPos, 5.0F, (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.8F, 1.0F, false, false);
            }
            if ((this.getAnimation() == ANIMATION_LEFT_WHIP || this.getAnimation() == ANIMATION_RIGHT_WHIP)
                    && this.getAnimationTick() > 20 && this.getAnimationTick() < 30) {
                this.hurtEntitiesAround(this.tailPart2.position(), 12.0F,
                        (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE), 1.0F, false, false);
            }
        }
        if (this.getControllingPassenger() != null) {
            if (this.getMeterAmount() < 1.0F) {
                this.setMeterAmount(Math.min(this.getMeterAmount() + 0.0025F, 1.0F));
            }
        } else {
            this.setMeterAmount(0.0F);
        }
    }

    private void onStep() {
        if (!this.isBaby() && this.screenShakeAmount <= 1.0F) {
            this.playSound(ACSoundRegistry.ATLATITAN_STEP.get(), 2.0F, 1.0F);
            this.screenShakeAmount = 1.0F;
        }
    }

    private void tickWalking() {
        this.prevWalkAnimPosition = this.walkAnimPosition;
        this.prevWalkAnimSpeed = this.walkAnimSpeed;
        this.prevLegBackAmount = this.legBackAmount;
        float f = this.getLegSlamAmount(2.0F, 0.333F);
        float f1 = this.getLegSlamAmount(2.0F, 0.666F);
        Vec3 movement = this.getDeltaMovement();
        float speed = (float) movement.length();
        if (this.areLegsMoving()) {
            this.walkAnimPosition += this.walkAnimSpeed;
            this.walkAnimSpeed = this.getAnimation() == ANIMATION_LEFT_WHIP || this.getAnimation() == ANIMATION_RIGHT_WHIP
                    ? Mth.approach(this.walkAnimSpeed, 4.0F, 0.66F)
                    : Mth.approach(this.walkAnimSpeed, this.isBaby() ? 2.0F : 1.0F, this.isBaby() ? 0.2F : 0.1F);
        } else {
            if (f > 0.05F) {
                this.walkAnimPosition += Math.min(f - 0.05F, this.walkAnimSpeed);
            }
            if (this.walkAnimSpeed > 0.0F) {
                this.walkAnimSpeed = Math.max(0.0F, this.walkAnimSpeed - 0.025F);
            }
        }
        if (f <= 0.05F && this.walkAnimSpeed > 0.0F && this.onGround()
                && (speed > 0.003F || this.getControllingPassenger() != null) && this.stepSoundCooldown <= 0) {
            this.onStep();
            this.stepSoundCooldown = 5;
        }
        if (f1 < 0.65F) {
            this.lastStompX = this.getX();
            this.lastStompZ = this.getZ();
        }
        if (this.stepSoundCooldown > 0) {
            --this.stepSoundCooldown;
        }
        double stompX = this.getX() - this.lastStompX;
        double stompZ = this.getZ() - this.lastStompZ;
        float stompDist = Mth.sqrt((float) (stompX * stompX + stompZ * stompZ));
        if (speed <= 0.003F || !this.areLegsMoving()) {
            stompDist = 0.0F;
        }
        if (this.getAnimation() == ANIMATION_SPEAK && this.getAnimationTick() == 2) {
            this.actuallyPlayAmbientSound();
        }
        this.legBackAmount = Mth.clamp(Mth.approach(this.legBackAmount, stompDist, speed), -1.0F, 1.0F);
    }

    public boolean shouldRaiseArms() {
        return this.getAnimation() == ANIMATION_LEFT_KICK || this.getAnimation() == ANIMATION_RIGHT_KICK
                || this.getAnimation() == ANIMATION_STOMP;
    }

    private void tickMultipart() {
        if (this.yawPointer == -1) {
            for (int i = 0; i < this.yawBuffer.length; ++i) {
                this.yawBuffer[i] = this.yBodyRot;
            }
        }
        if (++this.yawPointer == this.yawBuffer.length) {
            this.yawPointer = 0;
        }
        this.yawBuffer[this.yawPointer] = this.yBodyRot;
        Vec3[] prevPositions = new Vec3[this.allParts.length];
        for (int j = 0; j < this.allParts.length; ++j) {
            prevPositions[j] = new Vec3(this.allParts[j].getX(), this.allParts[j].getY(), this.allParts[j].getZ());
        }
        float neckRotateSpeed = this.getNeckRotateSpeed();
        float tailRotateSpeed = this.getTailRotateSpeed();
        if (this.turningFast) {
            neckRotateSpeed += 30.0F;
            tailRotateSpeed += 30.0F;
        }
        this.neckXRot = this.wrapNeckDegrees(Mth.approachDegrees(this.neckXRot, this.getTargetNeckXRot(), neckRotateSpeed));
        this.neckYRot = this.wrapNeckDegrees(Mth.approachDegrees(this.neckYRot, this.getTargetNeckYRot(), neckRotateSpeed));
        this.tailXRot = this.wrapNeckDegrees(Mth.approachDegrees(this.tailXRot, this.getTargetTailXRot(), tailRotateSpeed));
        this.tailYRot = this.wrapNeckDegrees(Mth.approachDegrees(this.tailYRot, this.getTargetTailYRot(), tailRotateSpeed));
        Vec3 center = this.position().add(0.0, this.getBbHeight() * 0.5F - this.getLegSolverBodyOffset(), 0.0);
        float headXStep = this.neckXRot / 4.0F;
        float headYStep = this.neckYRot / 4.0F;
        float tailXStep = this.tailXRot / 3.0F;
        float tailYStep = this.tailYRot / 3.0F;
        float neckAdditionalY = 0.0F;
        float neckAdditionalZ = 0.0F;
        if (this.getAnimation() == ANIMATION_STOMP) {
            float f = ACMath.cullAnimationTick(this.getAnimationTick(), 2.0F, ANIMATION_STOMP, 1.0F, 0, 30);
            neckAdditionalY = 4.0F * f;
            neckAdditionalZ = -4.0F * f;
        }
        this.neckPart1.setPosCenteredY(this.rotateOffsetVec(new Vec3(0.0, 2.0F + neckAdditionalY, 5.0F + neckAdditionalZ).scale(this.getScale()), headXStep, this.yBodyRot + headYStep).add(center));
        this.neckPart2.setPosCenteredY(this.rotateOffsetVec(new Vec3(0.0, 0.0, 2.5).scale(this.getScale()), headXStep, this.yBodyRot + headYStep * 2.0F).add(this.neckPart1.centeredPosition()));
        this.neckPart3.setPosCenteredY(this.rotateOffsetVec(new Vec3(0.0, 0.0, 2.0).scale(this.getScale()), headXStep, this.yBodyRot + headYStep * 3.0F).add(this.neckPart2.centeredPosition()));
        this.headPart.setPosCenteredY(this.rotateOffsetVec(new Vec3(0.0, 0.0, 2.5).scale(this.getScale()), headXStep, this.yBodyRot + headYStep * 4.0F).add(this.neckPart3.centeredPosition()));
        this.tailPart1.setPosCenteredY(this.rotateOffsetVec(new Vec3(0.0, -0.5, -3.5).scale(this.getScale()), tailXStep, this.yBodyRot + tailYStep).add(center));
        this.tailPart2.setPosCenteredY(this.rotateOffsetVec(new Vec3(0.0, -0.25, -3.25).scale(this.getScale()), tailXStep, this.yBodyRot + tailYStep * 2.0F).add(this.tailPart1.centeredPosition()));
        this.tailPart3.setPosCenteredY(this.rotateOffsetVec(new Vec3(0.0, 0.0, -2.5).scale(this.getScale()), tailXStep, this.yBodyRot + tailYStep * 3.0F).add(this.tailPart2.centeredPosition()));
        for (int l = 0; l < this.allParts.length; ++l) {
            this.allParts[l].xo = prevPositions[l].x;
            this.allParts[l].yo = prevPositions[l].y;
            this.allParts[l].zo = prevPositions[l].z;
            this.allParts[l].xOld = prevPositions[l].x;
            this.allParts[l].yOld = prevPositions[l].y;
            this.allParts[l].zOld = prevPositions[l].z;
        }
    }

    private float wrapNeckDegrees(float f) {
        return f % 360.0F;
    }

    protected void crushBlocksInRing(int width, int ringStartX, int ringStartZ, float dropChance) {
        if (!ForgeEventFactory.getMobGriefingEvent(this.level(), this)
                || !MobsConfig.AtlatitanServantBreakBlocks.get()) {
            return;
        }
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        float lowestFoot = 0.0F;
        for (LuxtructosaurusLegSolver.Leg leg : this.legSolver.legs) {
            float height = leg.getHeight(1.0F);
            if (height > lowestFoot) {
                lowestFoot = height;
            }
        }
        int feetY = this.blockPosition().getY() - (int) lowestFoot;
        BlockPos center = new BlockPos(ringStartX, feetY, ringStartZ);
        float maxResistance = AlexsCaves.COMMON_CONFIG.atlatitanMaxExplosionResistance.get().floatValue();
        for (int y = 0; y <= STOMP_CRUSH_HEIGHT; ++y) {
            ArrayList<MovingBlockData> dataPerYLevel = new ArrayList<>();
            int currentBlocksInChunk = 0;
            for (int i = -width - 1; i <= width + 1; ++i) {
                for (int j = -width - 1; j <= width + 1; ++j) {
                    mutableBlockPos.set(this.getBlockX() + i, feetY + y, this.getBlockZ() + j);
                    double dist = Math.sqrt(mutableBlockPos.distSqr(center));
                    if (dist > (double) width || !this.level().isLoaded(mutableBlockPos)) {
                        continue;
                    }
                    BlockState state = this.level().getBlockState(mutableBlockPos);
                    if (state.is(ACTagRegistry.UNMOVEABLE) || state.isAir() || state.canBeReplaced()
                            || state.getBlock().getExplosionResistance() > maxResistance) {
                        continue;
                    }
                    BlockEntity blockEntity = this.level().getBlockEntity(mutableBlockPos);
                    BlockPos offset = mutableBlockPos.immutable().subtract(center);
                    MovingBlockData data = new MovingBlockData(state,
                            state.getShape(this.level(), mutableBlockPos), offset,
                            blockEntity == null ? null : blockEntity.saveWithoutMetadata());
                    dataPerYLevel.add(data);
                    if (currentBlocksInChunk < 16) {
                        ++currentBlocksInChunk;
                    } else {
                        this.spawnCrushedBlock(center.above(y), dataPerYLevel, dropChance, 10);
                        dataPerYLevel.clear();
                        currentBlocksInChunk = 0;
                    }
                    this.level().setBlockAndUpdate(mutableBlockPos, Blocks.AIR.defaultBlockState());
                }
            }
            if (!dataPerYLevel.isEmpty()) {
                this.spawnCrushedBlock(center.above(y), dataPerYLevel, dropChance, 1);
            }
        }
    }

    private void spawnCrushedBlock(BlockPos pos, ArrayList<MovingBlockData> data, float dropChance, int placementCooldown) {
        CrushedBlockEntity crushed = ACEntityRegistry.CRUSHED_BLOCK.get().create(this.level());
        if (crushed == null) {
            return;
        }
        crushed.moveTo(Vec3.atCenterOf(pos));
        crushed.setAllBlockData(FallingTreeBlockEntity.createTagFromData(data));
        crushed.setDropChance(dropChance);
        crushed.setPlacementCooldown(placementCooldown);
        this.level().addFreshEntity(crushed);
    }

    protected Vec3 rotateOffsetVec(Vec3 offset, float xRot, float yRot) {
        return offset.xRot(-xRot * ((float) Math.PI / 180)).yRot(-yRot * ((float) Math.PI / 180));
    }

    public float getLegSolverBodyOffset() {
        float heightBackLeft = this.legSolver.backLeft.getHeight(1.0F);
        float heightBackRight = this.legSolver.backRight.getHeight(1.0F);
        float heightFrontLeft = this.legSolver.frontLeft.getHeight(1.0F);
        float heightFrontRight = this.legSolver.frontRight.getHeight(1.0F);
        float armsWalkAmount = 1.0F - this.raiseArmsAmount / 5.0F;
        return Math.max(Math.max(heightBackLeft, heightBackRight),
                armsWalkAmount * Math.max(heightFrontLeft, heightFrontRight)) * 0.8F;
    }

    @Override
    public int getMaxHeadYRot() {
        return 90;
    }

    @Override
    public int getHeadRotSpeed() {
        return 3;
    }

    @Override
    public void playAmbientSound() {
        if (this.getAnimation() == NO_ANIMATION && !this.level().isClientSide) {
            this.setAnimation(ANIMATION_SPEAK);
        }
    }

    public void actuallyPlayAmbientSound() {
        SoundEvent soundevent = this.getAmbientSound();
        if (soundevent != null) {
            this.playSound(soundevent, this.getSoundVolume(), this.getVoicePitch());
        }
    }

    @Override
    public boolean isMultipartEntity() {
        return true;
    }

    @Nullable
    @Override
    public PartEntity<?>[] getParts() {
        return this.allParts;
    }

    @Override
    public void lerpTo(double x, double y, double z, float yr, float xr, int steps, boolean b) {
        this.lx = x;
        this.ly = y;
        this.lz = z;
        this.lyr = yr;
        this.lxr = xr;
        this.lSteps = steps;
        this.setDeltaMovement(this.lxd, this.lyd, this.lzd);
    }

    @Override
    public void lerpMotion(double lerpX, double lerpY, double lerpZ) {
        this.lxd = lerpX;
        this.lyd = lerpY;
        this.lzd = lerpZ;
        this.setDeltaMovement(this.lxd, this.lyd, this.lzd);
    }

    public float getYawFromBuffer(int pointer, float partialTick) {
        int i = this.yawPointer - pointer & 0x7F;
        int j = this.yawPointer - pointer - 1 & 0x7F;
        float d0 = this.yawBuffer[j];
        float d1 = this.yawBuffer[i] - d0;
        return d0 + d1 * partialTick;
    }

    public float getTargetNeckXRot() {
        if (this.getAnimation() == ANIMATION_EAT_LEAVES && this.getAnimationTick() <= 35) {
            BlockPos eatingPos = this.getEatingPos();
            if (eatingPos != null) {
                float peckDist = Mth.clamp((float) (eatingPos.getY() + 0.5F - this.getY()), -6.0F, 12.0F) - 6.0F;
                return peckDist * 1.2F / 6.0F * -90F;
            }
        }
        if (this.getAnimation() == ANIMATION_STOMP && this.getAnimationTick() <= 30) {
            return 30.0F;
        }
        return -30.0F;
    }

    public float getTargetNeckYRot() {
        float buffered = this.getYawFromBuffer(10, 1.0F) - this.yBodyRot;
        return this.getYHeadRot() - this.yBodyRot + buffered;
    }

    private float getNeckRotateSpeed() {
        if (this.getAnimation() == ANIMATION_LEFT_WHIP || this.getAnimation() == ANIMATION_RIGHT_WHIP) {
            return 30.0F;
        }
        return 10.0F;
    }

    public float getTargetTailXRot() {
        if (this.getAnimation() == ANIMATION_LEFT_WHIP || this.getAnimation() == ANIMATION_RIGHT_WHIP) {
            return this.getAnimationTick() > 20 ? -20.0F : 20.0F;
        }
        return 0.0F;
    }

    public float getTargetTailYRot() {
        if (this.getAnimation() == ANIMATION_LEFT_WHIP) {
            return this.getAnimationTick() > 24 ? 70.0F : -70.0F;
        }
        if (this.getAnimation() == ANIMATION_RIGHT_WHIP) {
            return this.getAnimationTick() > 24 ? -70.0F : 70.0F;
        }
        return this.getYawFromBuffer(20, 1.0F) - this.yBodyRot;
    }

    private float getTailRotateSpeed() {
        return this.getAnimation() == ANIMATION_LEFT_WHIP || this.getAnimation() == ANIMATION_RIGHT_WHIP ? 30.0F : 10.0F;
    }

    public boolean areLegsMoving() {
        return (this.entityData.get(WALKING) || this.getAnimation() == ANIMATION_LEFT_WHIP
                || this.getAnimation() == ANIMATION_RIGHT_WHIP) && !this.isImmobile() && !this.isNoAi();
    }

    public float getLegSlamAmount(float speed, float offset) {
        float walkSpeed = 0.05F;
        return Math.abs((float) Math.cos(this.getWalkAnimPosition(1.0F) * walkSpeed * speed - Math.PI * (double) offset));
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
    }

    @Override
    public float getStepHeight() {
        return 1.6F;
    }

    @Override
    public int getMaxNavigableDistanceToGround() {
        return 4;
    }

    public boolean hurtEntitiesAround(Vec3 center, float radius, float damageAmount, float knockbackAmount,
                                      boolean setsOnFire, boolean disablesShields) {
        AABB aabb = new AABB(center.subtract(radius, radius, radius), center.add(radius, radius, radius));
        boolean flag = false;
        DamageSource damageSource = this.damageSources().mobAttack(this);
        for (LivingEntity living : this.level().getEntitiesOfClass(LivingEntity.class, aabb, EntitySelector.NO_CREATIVE_OR_SPECTATOR)) {
            if (living.is(this) || this.isAlliedTo(living) || living.getType() == this.getType()
                    || living.distanceToSqr(center.x, center.y, center.z) > (double) (radius * radius)) {
                continue;
            }
            if (living.isDamageSourceBlocked(damageSource) && disablesShields && living instanceof Player player) {
                player.disableShield(true);
            }
            if (!living.hurt(damageSource, damageAmount)) {
                continue;
            }
            flag = true;
            if (setsOnFire) {
                living.setSecondsOnFire(10);
            }
            living.knockback(knockbackAmount, center.x - living.getX(), center.z - living.getZ());
        }
        return flag;
    }

    @Override
    public boolean isImmobile() {
        return super.isImmobile();
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void push(Entity entity) {
        double d0 = entity.getX() - this.getX();
        double d1 = entity.getZ() - this.getZ();
        double d2 = Mth.absMax(d0, d1);
        if (!this.isPassengerOfSameVehicle(entity) && !entity.noPhysics && !this.noPhysics && d2 >= 0.01F) {
            d2 = Math.sqrt(d2);
            d0 /= d2;
            d1 /= d2;
            double d3 = 1.0 / d2;
            if (d3 > 1.0) {
                d3 = 1.0;
            }
            d0 *= d3;
            d1 *= d3;
            d0 *= 0.05F;
            d1 *= 0.05F;
            if (!entity.isVehicle() && (entity.isPushable() || entity instanceof KaijuMob)) {
                entity.push(d0, 0.0, d1);
            }
        }
    }

    @Override
    public float getScreenShakeAmount(float partialTicks) {
        if (!this.isAlive() || this.isBaby()) {
            return 0.0F;
        }
        return this.prevScreenShakeAmount + (this.screenShakeAmount - this.prevScreenShakeAmount) * partialTicks;
    }

    public float getRaiseArmsAmount(float partialTicks) {
        return (this.prevRaiseArmsAmount + (this.raiseArmsAmount - this.prevRaiseArmsAmount) * partialTicks) * 0.2F;
    }

    public float getWalkAnimPosition(float partialTicks) {
        return this.prevWalkAnimPosition + (this.walkAnimPosition - this.prevWalkAnimPosition) * partialTicks;
    }

    public float getWalkAnimSpeed(float partialTicks) {
        return this.prevWalkAnimSpeed + (this.walkAnimSpeed - this.prevWalkAnimSpeed) * partialTicks;
    }

    public float getLegBackAmount(float partialTicks) {
        return this.prevLegBackAmount + (this.legBackAmount - this.prevLegBackAmount) * partialTicks;
    }

    @Override
    public int getAnimationTick() {
        return this.animationTick;
    }

    @Override
    public void setAnimationTick(int tick) {
        this.animationTick = tick;
    }

    @Override
    public Animation getAnimation() {
        return this.currentAnimation;
    }

    @Override
    public void setAnimation(Animation animation) {
        if (this.getAnimation() != animation) {
            this.animationTick = 0;
            this.currentAnimation = animation;
        }
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_SPEAK, ANIMATION_STOMP, ANIMATION_LEFT_KICK,
                ANIMATION_RIGHT_KICK, ANIMATION_LEFT_WHIP, ANIMATION_RIGHT_WHIP, ANIMATION_EAT_LEAVES};
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypeTags.IS_PROJECTILE)) {
            amount *= this.getProjectileDamageReduction();
        }
        return super.hurt(source, amount);
    }

    @Override
    protected void actuallyHurt(DamageSource source, float amount) {
        float initialAmount = amount;
        if (!source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            amount = Math.min(initialAmount, AttributesConfig.AtlatitanServantDamageCap.get().floatValue());
        }
        super.actuallyHurt(source, amount);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        return super.isInvulnerableTo(damageSource) || damageSource.is(DamageTypes.IN_WALL);
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return Math.sqrt(distance) < 1024.0;
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        return this.getBoundingBox().inflate(2.0);
    }

    public float getProjectileDamageReduction() {
        return 0.75F;
    }

    @Override
    public float getScale() {
        return this.isBaby() ? 0.15F : 1.0F;
    }

    public BlockPos getEatingPos() {
        return this.entityData.get(EATING_POS).orElse(null);
    }

    public void setEatingPos(BlockPos eatingPos) {
        this.entityData.set(EATING_POS, Optional.ofNullable(eatingPos));
    }

    public BlockState getLastEatenBlock() {
        return this.entityData.get(LAST_EATEN_BLOCK).orElse(null);
    }

    public void setLastEatenBlock(BlockState state) {
        this.entityData.set(LAST_EATEN_BLOCK, Optional.ofNullable(state));
    }

    public float getMeterAmount() {
        return this.entityData.get(METER_AMOUNT);
    }

    public void setMeterAmount(float amount) {
        this.entityData.set(METER_AMOUNT, amount);
    }

    public boolean hasRidingMeter() {
        return true;
    }

    public BlockPos getStandAtTreePos(BlockPos target) {
        Vec3 vec3 = Vec3.atCenterOf(target).subtract(this.position());
        float f = -((float) Mth.atan2(vec3.x, vec3.z)) * 180.0F / (float) Math.PI;
        BlockState state = this.level().getBlockState(target);
        Direction dir = Direction.fromYRot(f);
        if (state.is(ACBlockRegistry.PEWEN_BRANCH.get())) {
            dir = Direction.fromYRot(state.getValue(PewenBranchBlock.ROTATION) * 45);
        }
        BlockPos standPos = target;
        if (this.level().getBlockState(target.below()).isAir()) {
            standPos = target.relative(dir);
        }
        return standPos.relative(dir.getOpposite(), (int) Math.floor(13 * this.getScale())).atY((int) this.getY());
    }

    public boolean lockTreePosition(BlockPos target) {
        Vec3 vec3 = Vec3.atCenterOf(target).subtract(this.position());
        float f = -((float) Mth.atan2(vec3.x, vec3.z)) * 180.0F / (float) Math.PI;
        int headDistToBody = (int) Math.floor(14 * this.getScale());
        BlockState state = this.level().getBlockState(target);
        Direction dir = Direction.fromYRot(f);
        if (state.is(ACBlockRegistry.PEWEN_BRANCH.get())) {
            dir = Direction.fromYRot(state.getValue(PewenBranchBlock.ROTATION) * 45);
        }
        float targetRot = Mth.approachDegrees(this.getYRot(), dir.toYRot(), 20);
        this.setYRot(targetRot);
        this.setYHeadRot(targetRot);
        this.yBodyRot = Mth.approachDegrees(this.yBodyRot, targetRot, 10);
        this.getLookControl().setLookAt(target.getX(), target.getY(), target.getZ());
        BlockPos standPos = target;
        if (this.level().getBlockState(target.below()).isAir()) {
            standPos = target.relative(dir);
        }
        Vec3 vec31 = Vec3.atCenterOf(standPos.relative(dir.getOpposite(), headDistToBody - 1));
        if (vec31.distanceToSqr(this.getX(), vec31.y, this.getZ()) > 1.0F) {
            this.getMoveControl().setWantedPosition(vec31.x, this.getY(), vec31.z, 1.0D);
        }
        return this.distanceToSqr(vec31.x, this.getY(), vec31.z) < headDistToBody
                && Mth.degreesDifferenceAbs(this.yBodyRot, dir.toYRot()) < 7;
    }

    protected void doPlayerRide(Player player) {
        if (!this.level().isClientSide) {
            player.setYRot(this.getYRot());
            player.setXRot(this.getXRot());
            player.startRiding(this);
        }
    }

    @Override
    protected Vec3 getRiddenInput(Player player, Vec3 deltaIn) {
        float f = player.zza < 0.0F ? 0.5F : 1.0F;
        return new Vec3(player.xxa * 0.35F, 0.0D, player.zza * 0.8F * f);
    }

    @Override
    protected void tickRidden(Player player, Vec3 vec3) {
        super.tickRidden(player, vec3);
        if (player.zza != 0.0F || player.xxa != 0.0F) {
            this.setRot(player.getYRot(), player.getXRot() * 0.25F);
            this.setTarget(null);
            this.entityData.set(WALKING, true);
        } else {
            this.entityData.set(WALKING, false);
        }
    }

    @Override
    protected float getRiddenSpeed(Player rider) {
        float f1 = 0.0F;
        if (this.areLegsMoving()) {
            float f = this.getLegSlamAmount(2.0F, 0.66F);
            float threshold = 0.65F;
            if (f >= threshold) {
                f1 = (f - threshold) / (1.0F - threshold);
            }
        }
        return (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED) * f1;
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        Entity entity = this.getFirstPassenger();
        return entity instanceof Player player ? player : null;
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        if (this.isBaby()) {
            return false;
        }
        if (passenger instanceof Player) {
            return true;
        }
        return passenger instanceof IServant
                && (this.getTrueOwner() == null || this.getTrueOwner() == ((IServant) passenger).getTrueOwner());
    }

    @Override
    public boolean isAbleToRide(LivingEntity livingEntity) {
        return false;
    }

    @Override
    public void positionRider(Entity passenger, Entity.MoveFunction moveFunction) {
        if (this.isPassengerOfSameVehicle(passenger) && passenger instanceof LivingEntity living
                && !this.touchingUnloadedChunk()) {
            float seatY = 0.5F;
            float seatZ = 0.5F;
            if (this.getAnimation() == ANIMATION_STOMP) {
                float animationIntensity = ACMath.cullAnimationTick(this.getAnimationTick(), 1.0F, ANIMATION_STOMP, 1.0F, 0, 30);
                seatY += animationIntensity * 1.5F;
                seatZ += animationIntensity * -4.5F;
            }
            Vec3 seatOffset = new Vec3(0.0F, seatY, seatZ).yRot((float) Math.toRadians(-this.yBodyRot));
            passenger.setYBodyRot(this.yBodyRot);
            passenger.fallDistance = 0.0F;
            this.clampRotation(living, 105.0F);
            moveFunction.accept(passenger, this.getX() + seatOffset.x,
                    this.getY() + seatOffset.y + this.getPassengersRidingOffset() - this.getLegSolverBodyOffset(),
                    this.getZ() + seatOffset.z);
        } else {
            super.positionRider(passenger, moveFunction);
        }
    }

    protected void clampRotation(LivingEntity livingEntity, float clampRange) {
        livingEntity.setYBodyRot(this.getYRot());
        float f = Mth.wrapDegrees(livingEntity.getYRot() - this.getYRot());
        float f1 = Mth.clamp(f, -clampRange, clampRange);
        livingEntity.yRotO += f1 - f;
        livingEntity.yBodyRotO += f1 - f;
        livingEntity.setYRot(livingEntity.getYRot() + f1 - f);
        livingEntity.setYHeadRot(livingEntity.getYRot());
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity passenger) {
        return new Vec3(this.getX(), this.getBoundingBox().minY, this.getZ());
    }

    @Override
    public void onKeyPacket(Entity keyPresser, int type) {
        if (!keyPresser.isPassengerOfSameVehicle(this)) {
            return;
        }
        if (type == 2 && this.getMeterAmount() >= 1.0F && this.getAnimation() == NO_ANIMATION) {
            this.yBodyRot = keyPresser.getYHeadRot();
            this.setYRot(keyPresser.getYHeadRot());
            this.setAnimation(ANIMATION_STOMP);
            this.setMeterAmount(0.0F);
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (!this.level().isClientSide) {
            InteractionResult altSkinResult = this.tryChangeAltSkin(player, hand);
            if (altSkinResult != null) {
                return altSkinResult;
            }
        }
        if (this.getTrueOwner() != null && player == this.getTrueOwner()) {
            float healAmount = this.getHealAmountFor(itemstack);
            if (healAmount > 0.0F) {
                if (this.getHealth() >= this.getMaxHealth()) {
                    return InteractionResult.PASS;
                }
                if (!this.level().isClientSide) {
                    this.heal(healAmount);
                    this.playSound(SoundEvents.ITEM_PICKUP, 1.0F, 1.0F);
                    this.gameEvent(GameEvent.EAT, this);
                    if (this.level() instanceof ServerLevel serverLevel) {
                        for (int i = 0; i < 8; ++i) {
                            double d0 = this.random.nextGaussian() * 0.02;
                            double d1 = this.random.nextGaussian() * 0.02 + 0.1;
                            double d2 = this.random.nextGaussian() * 0.02;
                            serverLevel.sendParticles(ParticleTypes.HEART,
                                    this.getRandomX(1.0F), this.getY() + this.getBbHeight() + 0.3F + this.random.nextDouble() * 0.5F,
                                    this.getRandomZ(1.0F), 0, d0, d1, d2, 0.5);
                        }
                    }
                }
                if (!itemstack.getCraftingRemainingItem().isEmpty()) {
                    this.spawnAtLocation(itemstack.getCraftingRemainingItem().copy());
                }
                this.usePlayerItem(player, hand, itemstack);
                player.swing(hand);
                return InteractionResult.SUCCESS;
            }
            if (!this.isBaby() && !this.isFood(itemstack) && !player.isCrouching()) {
                Entity firstPassenger = this.getFirstPassenger();
                if (firstPassenger != null && firstPassenger != player) {
                    firstPassenger.stopRiding();
                    return InteractionResult.SUCCESS;
                }
                if (!(itemstack.getItem() instanceof IWand)) {
                    this.doPlayerRide(player);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(ACBlockRegistry.TREE_STAR.get().asItem());
    }

    private float getHealAmountFor(ItemStack stack) {
        if (stack.is(ACItemRegistry.SERENE_SALAD.get())) {
            return 20.0F;
        }
        if (stack.is(ACBlockRegistry.PEWEN_BRANCH.get().asItem())) {
            return 10.0F;
        }
        return 0.0F;
    }

    @Override
    public boolean hasEgg() {
        return this.entityData.get(DATA_HAS_EGG);
    }

    @Override
    public void setHasEgg(boolean hasEgg) {
        this.entityData.set(DATA_HAS_EGG, hasEgg);
    }

    @Override
    public BlockState createEggBlockState() {
        return AcBlockRegistry.ATLATITAN_SERVANT_EGG.get().defaultBlockState();
    }

    public BlockState createEggBeddingBlockState() {
        return ACBlockRegistry.FERN_THATCH.get().defaultBlockState();
    }

    @Override
    public void onLayEggTick(BlockPos belowEgg, int time) {
        this.walkAnimation.update(0.5F, 0.4F);
        this.level().broadcastEntityEvent(this, (byte) 77);
    }

    @Override
    public void spawnChildFromBreeding(ServerLevel level, AnimalSummon partner) {
        this.setHasEgg(true);
        this.finalizeSpawnChildFromBreeding(level, partner, partner);
    }

    public int getAltSkin() {
        return this.entityData.get(ALT_SKIN);
    }

    public void setAltSkin(int altSkin) {
        this.entityData.set(ALT_SKIN, altSkin);
    }

    public int getAltSkinForItem(ItemStack stack) {
        if (stack.is(ACItemRegistry.AMBER_CURIOSITY.get())) {
            return 1;
        }
        if (stack.is(ACItemRegistry.TECTONIC_SHARD.get())) {
            return 2;
        }
        return 0;
    }

    @Nullable
    public InteractionResult tryChangeAltSkin(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        int newSkin = this.getAltSkinForItem(itemstack);
        if (newSkin > 0 && this.getTrueOwner() != null && player == this.getTrueOwner()) {
            if (!player.getAbilities().instabuild) {
                itemstack.shrink(1);
            }
            this.playSound(newSkin == 2
                    ? ACSoundRegistry.TECTONIC_SHARD_TRANSFORM.get()
                    : ACSoundRegistry.AMBER_MONOLITH_SUMMON.get());
            if (newSkin == this.getAltSkin()) {
                this.setAltSkin(0);
            } else {
                this.setAltSkin(newSkin);
            }
            this.level().broadcastEntityEvent(this, (byte) (newSkin == 2 ? 83 : 82));
            return InteractionResult.SUCCESS;
        }
        return null;
    }

    @Nullable
    @Override
    public AnimalSummon getBreedOffspring(ServerLevel level, AnimalSummon mob) {
        AtlatitanServant baby = AcEntityRegistry.ATLATITAN_SERVANT.get().create(level);
        if (baby != null) {
            baby.setPersistenceRequired();
        }
        return baby;
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        MobSpawnType spawnType, @Nullable SpawnGroupData groupData,
                                        @Nullable CompoundTag tag) {
        if (spawnType == MobSpawnType.MOB_SUMMONED && this.getTrueOwner() instanceof Player player
                && countServants(level.getLevel(), player.getUUID()) >= MobsConfig.AtlatitanServantLimit.get()) {
            return null;
        }
        return super.finalizeSpawn(level, difficulty, spawnType, groupData, tag);
    }

    @Override
    public int getExperienceReward() {
        return 30;
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime >= 20 && !this.level().isClientSide() && !this.isRemoved()) {
            if (this.getTrueOwner() != null && MobsConfig.AtlatitanServantReturnEgg.get()) {
                FlyingItem flyingItem = new FlyingItem(ModEntityType.FLYING_ITEM.get(), this.level(), this.getX(), this.getY(), this.getZ());
                flyingItem.setOwner(this.getTrueOwner());
                flyingItem.setItem(new ItemStack(AcItems.ATLATITAN_SERVANT_EGG.get()));
                this.level().addFreshEntity(flyingItem);
            }
            this.level().broadcastEntityEvent(this, (byte) 60);
            this.remove(Entity.RemovalReason.KILLED);
        }
    }

    @Override
    public void handleEntityEvent(byte b) {
        if (b == 77) {
            float radius = this.getBbWidth() * 0.55F;
            float particleCount = (5 + this.random.nextInt(5)) * radius;
            for (int i1 = 0; i1 < particleCount; i1++) {
                double motionX = (this.getRandom().nextFloat() - 0.5F) * 0.7D;
                double motionY = this.getRandom().nextFloat() * 0.7D + 0.8F;
                double motionZ = (this.getRandom().nextFloat() - 0.5F) * 0.7D;
                float angle = (float) (0.01745329251F * (this.yBodyRot + (i1 / particleCount) * 360F));
                double extraX = radius * Mth.sin((float) (Math.PI + angle));
                double extraY = 1.2F;
                double extraZ = radius * Mth.cos(angle);
                BlockPos ground = BlockPos.containing(ACMath.getGroundBelowPosition(this.level(),
                        new Vec3(Mth.floor(this.getX() + extraX), Mth.floor(this.getY() + extraY), Mth.floor(this.getZ() + extraZ))));
                BlockState groundState = this.level().getBlockState(ground.below());
                if (groundState.isSolid() && this.level().isClientSide) {
                    this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, groundState), true,
                            this.getX() + extraX, ground.getY(), this.getZ() + extraZ, motionX, motionY, motionZ);
                }
            }
        } else if (b == 82 || b == 83) {
            ParticleOptions particle = b == 82
                    ? ACParticleRegistry.DINOSAUR_TRANSFORMATION_AMBER.get()
                    : ACParticleRegistry.DINOSAUR_TRANSFORMATION_TECTONIC.get();
            for (int i = 0; i < 15; ++i) {
                if (this.level().random.nextInt(8) < 3) {
                    this.level().addParticle(particle,
                            this.getRandomX(1.0F), this.getY() + this.getBbHeight() + 0.3F, this.getRandomZ(1.0F),
                            this.random.nextGaussian() * 0.05, this.random.nextFloat() * 0.2, this.random.nextGaussian() * 0.05);
                }
            }
        } else {
            super.handleEntityEvent(b);
        }
    }

    private void enforceFollowingStanceOnce() {
        if (this.level().isClientSide || this.followingStanceEnforced) {
            return;
        }
        this.followingStanceEnforced = true;
        if (this.getTrueOwner() != null) {
            this.setFollowing();
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("HasEgg", this.hasEgg());
        tag.putBoolean("FollowingStanceEnforced", this.followingStanceEnforced);
        tag.putInt("AltSkin", this.getAltSkin());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setHasEgg(tag.getBoolean("HasEgg"));
        this.followingStanceEnforced = tag.getBoolean("FollowingStanceEnforced");
        int altSkin = tag.getInt("AltSkin");
        if (tag.contains("Retro") && tag.getBoolean("Retro")) {
            altSkin = 1;
        }
        this.setAltSkin(altSkin);
    }

    public int getMaxFallDistance() {
        return super.getMaxFallDistance() + 10;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ACSoundRegistry.ATLATITAN_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.ATLATITAN_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.ATLATITAN_DEATH.get();
    }

    private class SauropodMoveHelper extends MoveControl {
        public SauropodMoveHelper() {
            super(AtlatitanServant.this);
        }

        @Override
        public void tick() {
            if (this.operation == Operation.WAIT) {
                AtlatitanServant.this.entityData.set(WALKING, false);
                this.speedModifier = 0.0D;
            } else {
                AtlatitanServant.this.entityData.set(WALKING, true);
                float f = AtlatitanServant.this.getLegSlamAmount(2.0F, 0.66F);
                boolean facingTarget = true;
                if (this.operation == Operation.MOVE_TO) {
                    double d0 = this.wantedX - this.mob.getX();
                    double d1 = this.wantedZ - this.mob.getZ();
                    float moveToRot = (float) (Mth.atan2(d1, d0) * 57.2957763671875) - 90.0F;
                    facingTarget = Mth.degreesDifferenceAbs(AtlatitanServant.this.yBodyRot, moveToRot) < 15.0F;
                }
                if (AtlatitanServant.this.getAnimation() == ANIMATION_LEFT_WHIP
                        || AtlatitanServant.this.getAnimation() == ANIMATION_RIGHT_WHIP) {
                    facingTarget = true;
                }
                float threshold = 0.65F;
                if (f >= threshold && facingTarget) {
                    float f1 = (f - threshold) / (1.0F - threshold);
                    if (AtlatitanServant.this.isBaby()) {
                        f1 *= 0.5F;
                    }
                    this.speedModifier = f1;
                } else {
                    this.speedModifier = 0.0D;
                }
            }
            super.tick();
        }
    }

    public class AtlatitanServantFollowGoal extends Goal {
        private final AtlatitanServant summonedEntity;
        private LivingEntity owner;
        private final double followSpeed;
        private final float startDistance;
        private final float stopDistance;
        private int timeToRecalcPath;

        public AtlatitanServantFollowGoal(AtlatitanServant summonedEntity, double speed, float startDistance, float stopDistance) {
            this.summonedEntity = summonedEntity;
            this.followSpeed = speed;
            this.startDistance = startDistance;
            this.stopDistance = stopDistance;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity livingentity = this.summonedEntity.getTrueOwner();
            if (livingentity == null || livingentity.isSpectator()) {
                return false;
            }
            if (this.summonedEntity.getControllingPassenger() != null || this.summonedEntity.isPassenger()) {
                return false;
            }
            if (this.summonedEntity.distanceToSqr(livingentity) < (double) Mth.square(this.startDistance)) {
                return false;
            }
            if (!this.summonedEntity.isFollowing() || this.summonedEntity.isCommanded()) {
                return false;
            }
            if (this.summonedEntity.getTarget() != null) {
                return false;
            }
            this.owner = livingentity;
            return true;
        }

        @Override
        public boolean canContinueToUse() {
            if (this.summonedEntity.getNavigation().isDone()) {
                return false;
            }
            if (this.summonedEntity.getTarget() != null) {
                return false;
            }
            if (this.summonedEntity.getControllingPassenger() != null || this.summonedEntity.isPassenger()) {
                return false;
            }
            if (this.owner == null || !this.owner.isAlive()) {
                return false;
            }
            if (!this.summonedEntity.isFollowing() || this.summonedEntity.isCommanded()) {
                return false;
            }
            return this.summonedEntity.distanceToSqr(this.owner) > (double) Mth.square(this.stopDistance);
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
            if (this.owner == null) {
                return;
            }
            this.summonedEntity.getLookControl().setLookAt(this.owner, 10.0F, (float) this.summonedEntity.getMaxHeadXRot());
            if (--this.timeToRecalcPath > 0) {
                return;
            }
            this.timeToRecalcPath = 10;
            if (this.summonedEntity.isLeashed() || this.summonedEntity.isPassenger()) {
                return;
            }
            double range = this.owner instanceof Mob ? 48.0D : 24.0D;
            boolean teleport = this.summonedEntity.distanceToSqr(this.owner) >= Mth.square(range);
            teleport = teleport && this.canTeleport();
            if (teleport) {
                this.tryToTeleportNearEntity();
            } else {
                this.summonedEntity.getNavigation().moveTo(this.owner, this.followSpeed);
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
                if (this.tryToTeleportToLocation(blockpos.getX() + j, blockpos.getY() + k, blockpos.getZ() + l)) {
                    return;
                }
            }
        }

        protected boolean tryToTeleportToLocation(int x, int y, int z) {
            if (Math.abs((double) x - this.owner.getX()) < 2.0D && Math.abs((double) z - this.owner.getZ()) < 2.0D) {
                return false;
            }
            if (!this.isTeleportFriendlyBlock(new BlockPos(x, y, z))) {
                return false;
            }
            this.summonedEntity.moveTo((double) x + 0.5D, y, (double) z + 0.5D,
                    this.summonedEntity.getYRot(), this.summonedEntity.getXRot());
            this.summonedEntity.getNavigation().stop();
            return true;
        }

        protected boolean isTeleportFriendlyBlock(BlockPos pos) {
            LevelReader level = this.summonedEntity.level();
            BlockState below = level.getBlockState(pos.below());
            if (!below.isFaceSturdy(level, pos.below(), Direction.UP) || below.is(ACTagRegistry.UNMOVEABLE)) {
                return false;
            }
            return level.noCollision(this.summonedEntity,
                    this.summonedEntity.getBoundingBox().move(pos.subtract(this.summonedEntity.blockPosition())));
        }

        protected int getRandomNumber(int min, int max) {
            return this.summonedEntity.getRandom().nextInt(max - min + 1) + min;
        }
    }
}
