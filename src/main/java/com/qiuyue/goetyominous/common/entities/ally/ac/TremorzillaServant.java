package com.qiuyue.goetyominous.common.entities.ally.ac;

import com.Polarice3.Goety.api.entities.ally.IServant;
import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.ally.AnimalSummon;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.entities.projectiles.FlyingItem;
import com.Polarice3.Goety.init.ModMobType;
import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.block.ACBlockRegistry;
import com.github.alexmodguy.alexscaves.server.block.blockentity.NuclearSirenBlockEntity;
import com.github.alexmodguy.alexscaves.server.block.poi.ACPOIRegistry;
import com.github.alexmodguy.alexscaves.server.entity.util.LaysEggs;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityDataRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ai.AdvancedPathNavigateNoTeleport;
import com.github.alexmodguy.alexscaves.server.entity.ai.AllFluidsPathNavigator;
import com.github.alexmodguy.alexscaves.server.entity.ai.DirectAquaticMoveControl;
import com.github.alexmodguy.alexscaves.server.entity.ai.LookAtLargeMobsGoal;
import com.github.alexmodguy.alexscaves.server.entity.item.NuclearBombEntity;
import com.github.alexmodguy.alexscaves.server.entity.util.ActivatesSirens;
import com.github.alexmodguy.alexscaves.server.entity.util.KaijuMob;
import com.github.alexmodguy.alexscaves.server.entity.util.KeybindUsingMount;
import com.github.alexmodguy.alexscaves.server.entity.util.ShakesScreen;
import com.github.alexmodguy.alexscaves.server.entity.util.TremorzillaLegSolver;
import com.github.alexmodguy.alexscaves.server.message.MountedEntityKeyMessage;
import com.github.alexmodguy.alexscaves.server.misc.ACAdvancementTriggerRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACDamageTypes;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.github.alexmodguy.alexscaves.server.potion.ACEffectRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.server.entity.pathfinding.raycoms.IAdvancedPathingMob;
import com.github.alexthe666.citadel.server.entity.pathfinding.raycoms.ITallWalker;
import com.qiuyue.goetyominous.common.init.ac.AcBlockRegistry;
import com.qiuyue.goetyominous.common.items.ac.AcItems;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.qiuyue.goetyominous.config.MobsConfig;
import net.minecraft.commands.arguments.EntityAnchorArgument;
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
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public class TremorzillaServant extends AnimalSummon
        implements LaysEggs, KeybindUsingMount, IAnimatedEntity, ShakesScreen, KaijuMob, ActivatesSirens, ITallWalker, IAdvancedPathingMob {

    private static final EntityDataAccessor<Optional<Vec3>> BEAM_END_POSITION =
            SynchedEntityData.defineId(TremorzillaServant.class, ACEntityDataRegistry.OPTIONAL_VEC_3.get());
    private static final EntityDataAccessor<Boolean> SWIMMING =
            SynchedEntityData.defineId(TremorzillaServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> CHARGE =
            SynchedEntityData.defineId(TremorzillaServant.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> MAX_BEAM_BREAK_LENGTH =
            SynchedEntityData.defineId(TremorzillaServant.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> SPIKES_DOWN_PROGRESS =
            SynchedEntityData.defineId(TremorzillaServant.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> FIRING =
            SynchedEntityData.defineId(TremorzillaServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> ALT_SKIN =
            SynchedEntityData.defineId(TremorzillaServant.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_HAS_EGG =
            SynchedEntityData.defineId(TremorzillaServant.class, EntityDataSerializers.BOOLEAN);

    public static final Animation ANIMATION_SPEAK = Animation.create(20);
    public static final Animation ANIMATION_ROAR_1 = Animation.create(60);
    public static final Animation ANIMATION_ROAR_2 = Animation.create(60);
    public static final Animation ANIMATION_RIGHT_SCRATCH = Animation.create(35);
    public static final Animation ANIMATION_LEFT_SCRATCH = Animation.create(35);
    public static final Animation ANIMATION_RIGHT_TAIL = Animation.create(40);
    public static final Animation ANIMATION_LEFT_TAIL = Animation.create(40);
    public static final Animation ANIMATION_RIGHT_STOMP = Animation.create(35);
    public static final Animation ANIMATION_LEFT_STOMP = Animation.create(35);
    public static final Animation ANIMATION_BITE = Animation.create(25);
    public static final Animation ANIMATION_PREPARE_BREATH = Animation.create(20);
    public static final Animation ANIMATION_CHEW = Animation.create(35);

    private static final int MAX_CHARGE = 1000;
    /** 吼叫只能吓退最大生命值 ≤ 该值的敌人;更高血量的强敌完全不受吼叫影响(不逃跑也不中虚弱) */
    private static final float SCARE_MAX_HEALTH = 100.0F;
    private static final EntityDimensions SWIMMING_SIZE = new EntityDimensions(4.0F, 5.0F, true);
    /** 与 Goety 暗兽(Summoned.FollowOwnerGoal)一致的跟随启动距离:主人超出该距离才启动跟随 */
    private static final float FOLLOW_START_DISTANCE = 10.0F;

    private final TremorzillaServantPartEntity[] allParts;
    public final TremorzillaServantPartEntity tailPart1;
    public final TremorzillaServantPartEntity tailPart2;
    public final TremorzillaServantPartEntity tailPart3;
    public final TremorzillaServantPartEntity tailPart4;
    public final TremorzillaServantPartEntity tailPart5;

    private final float[] yawBuffer = new float[128];
    private int yawPointer = -1;
    protected float tailXRot;
    protected float tailYRot;
    public TremorzillaLegSolver legSolver = new TremorzillaLegSolver(1.0F, 2.15F, 3.0F);

    private Animation currentAnimation;
    private int animationTick;
    private float lastYawBeforeWhip;
    protected boolean isLandNavigator;
    private double lastStompX = 0.0;
    private double lastStompZ = 0.0;
    private float prevScreenShakeAmount;
    private float screenShakeAmount;
    private float beamProgress;
    private float prevBeamProgress;
    private int lSteps;
    private double lx, ly, lz, lyr, lxr, lxd, lyd, lzd;
    private int lastScareTimestamp;
    private int blockBreakCounter = 0;
    private int steamFromMouthFor = 0;
    private int roarCooldown = 0;
    public Vec3 beamServerTarget;
    public Vec3 prevClientBeamEndPosition;
    public Vec3 clientBeamEndPosition;
    public boolean wantsToUseBeamFromServer = false;
    private float prevClientSpikesDownAmount = 0.0F;
    private float clientSpikesDownAmount = 0.0F;
    private int beamTime = 0;
    private int maxBeamTime = 200;
    private int timeWithoutTarget = 0;
    public int timeSwimming;
    private boolean wasPreviouslyChild;
    private final Explosion dummyExplosion;
    private int chargeSoundCooldown = 0;
    private boolean makingBeamSoundOnClient = false;
    private Player lastFedPlayer = null;
    private int killCountFromBeam = 0;
    private boolean servantLimitEnforced = false;
    private float prevSitProgress;
    private float sitProgress;
    private float prevBuryEggsProgress;
    private float buryEggsProgress;
    public boolean buryingEggs;
    private boolean followingStanceEnforced = false;

    public TremorzillaServant(EntityType<? extends Owned> type, Level level) {
        super(type, level);
        this.switchNavigator(true);
        this.tailPart1 = new TremorzillaServantPartEntity(this, this, 3.0F, 3.0F);
        this.tailPart2 = new TremorzillaServantPartEntity(this, this.tailPart1, 2.5F, 2.0F);
        this.tailPart3 = new TremorzillaServantPartEntity(this, this.tailPart2, 2.5F, 1.5F);
        this.tailPart4 = new TremorzillaServantPartEntity(this, this.tailPart3, 2.5F, 1.5F);
        this.tailPart5 = new TremorzillaServantPartEntity(this, this.tailPart4, 2.0F, 1.0F);
        this.allParts = new TremorzillaServantPartEntity[]{this.tailPart1, this.tailPart2, this.tailPart3, this.tailPart4, this.tailPart5};
        this.setPathfindingMalus(BlockPathTypes.UNPASSABLE_RAIL, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_OTHER, 8.0F);
        this.setPathfindingMalus(BlockPathTypes.POWDER_SNOW, 8.0F);
        this.setPathfindingMalus(BlockPathTypes.LAVA, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.LEAVES, 0.0F);
        this.setMaxUpStep(1.6F);
        this.dummyExplosion = new Explosion(this.level(), null, this.getX(), this.getY(), this.getZ(), 10.0F, List.of());
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.TremorzillaServantHealth.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.TremorzillaServantFollowRange.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.TremorzillaServantDamage.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, AttributesConfig.TremorzillaServantKnockbackResistance.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.TremorzillaServantMovementSpeed.get())
                .add(Attributes.ARMOR, AttributesConfig.TremorzillaServantArmor.get());
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new TremorzillaServantAttackGoal());
        this.goalSelector.addGoal(5, new TemptGoal(this, 1.1, Ingredient.of(ACBlockRegistry.WASTE_DRUM.get(), ACBlockRegistry.NUCLEAR_BOMB.get()), false));
        this.goalSelector.addGoal(6, new TremorzillaServantWanderGoal());
        this.goalSelector.addGoal(7, new LookAtLargeMobsGoal(this, 3.0F, 30.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }

    @Override
    public void followGoal() {
        this.goalSelector.addGoal(5, new TremorzillaServantFollowGoal(this, 1.0D, FOLLOW_START_DISTANCE, 2.0F));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ALT_SKIN, 0);
        this.entityData.define(DATA_HAS_EGG, false);
        this.entityData.define(BEAM_END_POSITION, Optional.empty());
        this.entityData.define(SWIMMING, false);
        this.entityData.define(CHARGE, 1000);
        this.entityData.define(SPIKES_DOWN_PROGRESS, 0.0F);
        this.entityData.define(MAX_BEAM_BREAK_LENGTH, 100.0F);
        this.entityData.define(FIRING, false);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new AdvancedPathNavigateNoTeleport(this, level);
    }

    /**
     * 与原版 AC DinosaurEntity 一致:被骑乘/待命时让 Citadel 高级导航器停止寻路,
     * 避免骑乘时残留路径持续驱动 MoveControl 与玩家操控冲突(第三人称视角抖动/卡死)。
     */
    @Override
    public boolean stopTickingPathing() {
        return this.isVehicle() || this.isStaying();
    }

    /**
     * 尾巴 part 的友伤免疫依赖客户端 isAlliedTo(攻击者) 求值,而客户端 Owned.getTrueOwner()
     * 靠 OWNER_CLIENT_ID(默认 -1)解析主人;蛋块孵化路径只调 setOwnerId(UUID)、从不
     * setOwnerClientId,导致客户端解析不出主人、isAlliedTo 对主人返回 false,主人攻击尾巴
     * 会被 MultipartEntityMessage 转发伤到本体。这里用已同步的 OWNER_UNIQUE_ID 按 UUID 兜底。
     */
    @Override
    public boolean isAlliedTo(Entity entity) {
        if (super.isAlliedTo(entity)) {
            return true;
        }
        if (this.level().isClientSide && entity != null
                && this.getOwnerId() != null && this.getOwnerId().equals(entity.getUUID())) {
            return true;
        }
        return false;
    }

    protected void switchNavigator(boolean onLand) {
        if (onLand) {
            this.lookControl = new LookControl(this);
            this.moveControl = new MoveControl(this);
            this.navigation = this.createNavigation(this.level());
            this.isLandNavigator = true;
        } else {
            this.lookControl = new SmoothSwimmingLookControl(this, 10);
            this.moveControl = new DirectAquaticMoveControl(this, 0.8F, 40.0F);
            this.navigation = new AllFluidsPathNavigator(this, this.level());
            this.isLandNavigator = false;
        }
    }

    @Override
    public int getMaxFallDistance() {
        return super.getMaxFallDistance() + 10;
    }

    /**
     * 单次受击限伤(参考 Goety Vizier 的 actuallyHurt + VizierDamageCap):
     * 每一下伤害最高 30 点,避免被超高单次伤害秒杀;BYPASSES_INVULNERABILITY 的伤害(虚空、/kill 等)不限制。
     */
    @Override
    protected void actuallyHurt(DamageSource source, float amount) {
        float initialAmount = amount;
        if (!source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            amount = Math.min(initialAmount, AttributesConfig.TremorzillaServantDamageCap.get().floatValue());
        }
        super.actuallyHurt(source, amount);
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    protected void updateControlFlags() {
        super.updateControlFlags();
        boolean steering = this.getControllingPassenger() instanceof Player player && (player.zza != 0.0F || player.xxa != 0.0F);
        boolean notInBoat = !(this.getVehicle() instanceof net.minecraft.world.entity.vehicle.Boat);
        this.goalSelector.setControlFlag(Goal.Flag.MOVE, !steering);
        this.goalSelector.setControlFlag(Goal.Flag.JUMP, !steering && notInBoat);
        this.goalSelector.setControlFlag(Goal.Flag.LOOK, !steering);
    }

    public boolean isFakeEntity() {
        return this.firstTick;
    }

    @Override
    public void tick() {
        super.tick();
        this.enforceFollowingStanceOnce();
        this.prevBuryEggsProgress = this.buryEggsProgress;
        if (this.buryingEggs && this.buryEggsProgress < 5.0F) {
            this.buryEggsProgress++;
        }
        if (!this.buryingEggs && this.buryEggsProgress > 0.0F) {
            this.buryEggsProgress--;
        }
        this.enforceServantLimitOnce();
        AnimationHandler.INSTANCE.updateAnimations(this);
        this.legSolver.update(this, this.yBodyRot, this.getScale());
        this.prevScreenShakeAmount = this.screenShakeAmount;
        this.prevBeamProgress = this.beamProgress;
        this.prevClientBeamEndPosition = this.clientBeamEndPosition;
        this.prevClientSpikesDownAmount = this.clientSpikesDownAmount;
        this.prevSitProgress = this.sitProgress;
        if (this.isStaying() && this.sitProgress < this.maxSitTicks()) {
            ++this.sitProgress;
        }
        if (!this.isStaying() && this.sitProgress > 0.0F) {
            --this.sitProgress;
        }
        boolean water = this.isInFluidType();
        if (water && this.isLandNavigator) {
            this.switchNavigator(false);
        }
        if (!water && !this.isLandNavigator) {
            this.switchNavigator(true);
        }
        if (this.isTremorzillaSwimming()) {
            ++this.timeSwimming;
            this.setAirSupply(this.getMaxAirSupply());
        } else {
            this.timeSwimming = 0;
        }
        if (this.screenShakeAmount > 0.0F) {
            this.screenShakeAmount = Math.max(0.0F, this.screenShakeAmount - 0.34F);
        }
        if (this.isFiring() && this.beamProgress < 5.0F) {
            this.beamProgress += 1.0F;
        }
        if (!this.isFiring() && this.beamProgress > 0.0F) {
            this.beamProgress -= 1.0F;
        }
        this.clientSpikesDownAmount = Mth.approach(this.clientSpikesDownAmount, this.getSpikesDownAmount(), 0.1F);
        Vec3 beamEnd = this.getBeamEndPosition();
        this.clientBeamEndPosition = beamEnd;
        if (this.isFiring()) {
            boolean flag = false;
            if (beamEnd != null) {
                Vec3 vec3 = beamEnd.subtract(this.getBeamShootFrom(1.0F));
                float beamYaw = -((float) Mth.atan2(vec3.x, vec3.z)) * 57.295776F;
                if (Mth.degreesDifferenceAbs(beamYaw, Mth.wrapDegrees(this.yBodyRot)) > 80.0F) {
                    flag = true;
                    this.setYRot(Mth.approachDegrees(this.getYRot(), beamYaw, 10.0F));
                    this.yBodyRot = Mth.approachDegrees(this.yBodyRotO, beamYaw, 10.0F);
                    this.lastYawBeforeWhip = beamYaw;
                }
            }
            if (!flag) {
                this.yBodyRot = Mth.approachDegrees(this.yBodyRotO, this.lastYawBeforeWhip, 15.0F);
            }
        } else if (this.getAnimation() != ANIMATION_RIGHT_TAIL && this.getAnimation() != ANIMATION_LEFT_TAIL) {
            this.yBodyRot = Mth.approachDegrees(this.yBodyRotO, this.getYRot(), 4.0F);
            this.yHeadRot = Mth.approachDegrees(this.yHeadRotO, this.yHeadRot, 2.0F);
            this.lastYawBeforeWhip = this.yBodyRot;
        } else {
            float negative = this.getAnimation() == ANIMATION_RIGHT_TAIL ? -1.0F : 1.0F;
            float target;
            if (this.getAnimationTick() < 5) {
                float f = (float) this.getAnimationTick() / 5.0F;
                target = f * -10.0F;
            } else {
                float f = (float) (this.getAnimationTick() - 10) / 15.0F;
                target = Mth.clamp(f, 0.0F, 1.0F) * 170.0F;
            }
            if ((float) this.getAnimationTick() > 25.0F) {
                this.yBodyRot = Mth.approachDegrees(this.yBodyRotO, this.lastYawBeforeWhip, 15.0F);
            } else {
                this.walkAnimation.setSpeed(1.0F + AlexsCaves.PROXY.getPartialTicks());
                this.yBodyRot = Mth.approachDegrees(this.yBodyRotO, this.lastYawBeforeWhip + negative * target, 70.0F);
            }
        }
        if (this.screenShakeAmount > 0.0F) {
            this.screenShakeAmount = Math.max(0.0F, this.screenShakeAmount - 0.15F);
        }
        if (this.onGround() && !this.isInFluidType() && this.walkAnimation.speed() > 0.1F && !this.isBaby() && !this.isNoAi() && this.isAlive()) {
            float f = (float) Math.cos(this.walkAnimation.position() * 0.25F - 1.5F);
            float f1 = (float) Math.cos(this.walkAnimation.position() * 0.25F - 1.0F);
            float f2 = (float) Math.sin(this.walkAnimation.position() * 0.25F - 1.0F);
            if (Math.abs(f) < 0.2F) {
                if (this.screenShakeAmount <= 0.3) {
                    this.playSound(ACSoundRegistry.TREMORZILLA_STOMP.get(), 6.0F, 0.7F);
                }
            }
            if (this.walkAnimation.speed() > 0.5F && Math.abs(f1) < 0.1F) {
                this.stompEffect(f2 > 0.0F, 1.0F, 1.3F, 0.4F + this.walkAnimation.speed(), 2.0F);
            }
        }
        this.tickMultipart();
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
            Player player = AlexsCaves.PROXY.getClientSidePlayer();
            if (player != null && player.isPassengerOfSameVehicle(this)) {
                if (AlexsCaves.PROXY.isKeyDown(2) && this.getMeterAmount() >= 1.0F) {
                    AlexsCaves.sendMSGToServer(new MountedEntityKeyMessage(this.getId(), player.getId(), 2));
                }
                if (AlexsCaves.PROXY.isKeyDown(3) && (this.getAnimation() == NO_ANIMATION || this.getAnimation() == null)) {
                    AlexsCaves.sendMSGToServer(new MountedEntityKeyMessage(this.getId(), player.getId(), 3));
                }
            }
            if (this.isFiring() && this.beamProgress > 0.0F && !this.makingBeamSoundOnClient) {
                AlexsCaves.PROXY.playWorldSound(this, (byte) 16);
                this.makingBeamSoundOnClient = true;
            }
            if (!this.isFiring() && this.makingBeamSoundOnClient) {
                AlexsCaves.PROXY.clearSoundCacheFor(this);
                this.makingBeamSoundOnClient = false;
            }
        } else {
            double waterHeight = this.getMaxFluidHeight();
            if (waterHeight > 0.0 && waterHeight < (double) (this.getBbHeight() - 1.0F) && !this.verticalCollision) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.02, 0.0));
            }
            this.setTremorzillaSwimming(waterHeight > 2.0);
        }
        if (this.isAlive()) {
            if (this.isFiring()) {
                this.tickBreath();
            } else if (this.steamFromMouthFor > 0 && this.level().isClientSide) {
                // AC 的 TREMORZILLA_STEAM 在 getInMouthPos 里强校验 entity instanceof TremorzillaEntity,
                // 对 TremorzillaServant 恒返回 Vec3.ZERO(粒子全喷到世界原点),故改用自带坐标的白色浓烟。
                Vec3 steamPos = this.getBeamShootFrom(1.0F).add(new Vec3(this.random.nextBoolean() ? -0.9F : 0.9F, 0.8F, 1.8F)
                        .scale(this.getScale())
                        .xRot((float) Math.toRadians(-this.getXRot()))
                        .yRot((float) Math.toRadians(-this.getYHeadRot())));
                this.level().addAlwaysVisibleParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, true,
                        steamPos.x, steamPos.y, steamPos.z, 0.0, 0.0, 0.0);
            }
            if (!this.isFiring() && this.killCountFromBeam > 0) {
                if (this.killCountFromBeam > 20 && !this.level().isClientSide && this.isVehicle()) {
                    for (Entity passenger : this.getPassengers()) {
                        ACAdvancementTriggerRegistry.TREMORZILLA_KILL_BEAM.triggerForEntity(passenger);
                    }
                }
                this.killCountFromBeam = 0;
            }
            if ((this.getAnimation() == ANIMATION_RIGHT_SCRATCH || this.getAnimation() == ANIMATION_LEFT_SCRATCH) && this.getAnimationTick() == 18) {
                Vec3 center = new Vec3(0.0, 5.0F * this.getScale(), 6.0F * this.getScale()).yRot(-this.yBodyRot * ((float) Math.PI / 180)).add(this.position());
                this.hurtEntitiesAround(center, 6.0F, 25.0F, 2.0F, false, true, true);
                if (!this.level().isClientSide) {
                    this.breakBlocksAround(center, 3.0F, false, false, 0.6F);
                }
            }
            if ((this.getAnimation() == ANIMATION_RIGHT_TAIL || this.getAnimation() == ANIMATION_LEFT_TAIL) && this.getAnimationTick() >= 10 && this.getAnimationTick() < 25) {
                // 甩尾:25 基础伤害 + 目标最大生命值 5% 百分比伤害(参考 Goety 红石怪兽 RedstoneMonstrosity 的 HP percent damage 机制)
                float tailHpPercent = AttributesConfig.TremorzillaServantTailHpPercentDamage.get().floatValue();
                this.hurtEntitiesAround(this.tailPart1.centeredPosition(), 4.0F, 25.0F, tailHpPercent, 2.0F, false, true, true);
                this.hurtEntitiesAround(this.tailPart2.centeredPosition(), 4.0F, 25.0F, tailHpPercent, 2.0F, false, true, true);
                this.hurtEntitiesAround(this.tailPart3.centeredPosition(), 4.0F, 25.0F, tailHpPercent, 2.0F, false, true, true);
                this.hurtEntitiesAround(this.tailPart4.centeredPosition(), 3.0F, 25.0F, tailHpPercent, 2.0F, false, true, true);
                this.hurtEntitiesAround(this.tailPart5.centeredPosition(), 3.0F, 25.0F, tailHpPercent, 2.0F, false, true, true);
                if (!this.level().isClientSide) {
                    this.breakBlocksAround(this.tailPart1.centeredPosition(), 2.0F, false, false, 0.6F);
                    this.breakBlocksAround(this.tailPart2.centeredPosition(), 2.0F, false, false, 0.6F);
                    this.breakBlocksAround(this.tailPart3.centeredPosition(), 2.0F, false, false, 0.6F);
                    this.breakBlocksAround(this.tailPart4.centeredPosition(), 1.0F, false, false, 0.6F);
                    this.breakBlocksAround(this.tailPart5.centeredPosition(), 1.0F, false, false, 0.6F);
                }
            }
            if ((this.getAnimation() == ANIMATION_LEFT_STOMP || this.getAnimation() == ANIMATION_RIGHT_STOMP) && this.getAnimationTick() == 18) {
                this.stompEffect(this.getAnimation() == ANIMATION_LEFT_STOMP, 2.0F, 5.0F, 1.2F, 25.0F);
                this.screenShakeAmount = 4.0F;
            }
            if (this.getAnimation() == ANIMATION_BITE && this.getAnimationTick() == 10) {
                Vec3 center = new Vec3(0.0, 7.0F * this.getScale(), 5.0F * this.getScale()).yRot(-this.yBodyRot * ((float) Math.PI / 180)).add(this.position());
                this.hurtEntitiesAround(center, 7.5F, 30.0F, 2.0F, false, true, true);
                if (!this.level().isClientSide) {
                    this.breakBlocksAround(center, 4.0F, false, false, 0.6F);
                }
            }
            if (this.getAnimation() == ANIMATION_ROAR_1 && this.getAnimationTick() > 10 && this.getAnimationTick() < 50 || this.getAnimation() == ANIMATION_ROAR_2 && this.getAnimationTick() > 15 && this.getAnimationTick() < 50) {
                this.screenShakeAmount = 8.0F;
                if (!this.level().isClientSide) {
                    this.scareMobs();
                }
            }
            if (this.getAnimation() == ANIMATION_SPEAK && this.getAnimationTick() == 5 && !this.isFiring()) {
                this.actuallyPlayAmbientSound();
            }
        }
        if (!this.level().isClientSide) {
            LivingEntity target = this.getTarget();
            this.timeWithoutTarget = target == null || !target.isAlive() ? ++this.timeWithoutTarget : 0;
            if (this.wantsToUseBeamFromServer && (this.timeWithoutTarget > 100 && !this.isVehicle() || this.isStaying())) {
                this.wantsToUseBeamFromServer = false;
            }
            if (this.isFiring()) {
                this.wantsToUseBeamFromServer = false;
                int iterateBy = 1;
                if (!this.isVehicle()) {
                    if (target == null || !target.isAlive()) {
                        iterateBy = 3;
                    } else if (target.distanceTo(this) > 100.0F) {
                        iterateBy = 8;
                    }
                }
                this.beamTime += iterateBy;
                if (this.beamTime > this.maxBeamTime) {
                    this.beamTime = 0;
                    this.setFiring(false);
                    this.playSound(ACSoundRegistry.TREMORZILLA_BEAM_END.get(), 8.0F, 1.0F);
                    this.beamServerTarget = null;
                    this.setBeamEndPosition(null);
                    this.setCharge(0);
                } else {
                    if (!this.isStunned()) {
                        this.tickBeamTargeting();
                    }
                    this.setCharge(1000);
                }
            } else if (this.wantsToUseBeamFromServer && this.isPowered()) {
                float spikesThreshold = 0.95F;
                if (this.getAnimation() == ANIMATION_PREPARE_BREATH && this.getSpikesDownAmount() >= spikesThreshold && this.getAnimationTick() > 15 && !this.isFiring()) {
                    this.maxBeamTime = 100 + this.random.nextInt(150);
                    this.beamServerTarget = this.createInitialBeamVec();
                    this.lookAt(EntityAnchorArgument.Anchor.EYES, this.beamServerTarget);
                    this.setFiring(true);
                    this.setMaxBeamBreakLength(100.0F);
                }
                if (this.getSpikesDownAmount() >= spikesThreshold && this.getAnimation() == NO_ANIMATION && !this.isStunned()) {
                    this.syncAnimation(ANIMATION_PREPARE_BREATH);
                    this.playSound(ACSoundRegistry.TREMORZILLA_BEAM_START.get(), 8.0F, 1.0F);
                }
                this.setSpikesDownAmount(Math.min(this.getSpikesDownAmount() + 0.005F, 1.0F));
                if ((this.tickCount + this.getId()) % 10 == 0 && this.level() instanceof ServerLevel serverLevel) {
                    this.getNearbySirens(serverLevel, 256).forEach(this::activateSiren);
                }
                float f = calculateSpikesDownAmount(this.getSpikesDownAmount(), 6.0F);
                if (Math.floor(f - 0.005F) != Math.floor(f) && this.chargeSoundCooldown <= 0 && f <= 5.0F) {
                    float pitch = 0.7F + this.getSpikesDownAmount() * 0.7F;
                    this.playSound(f > 4.0F ? ACSoundRegistry.TREMORZILLA_CHARGE_COMPLETE.get() : ACSoundRegistry.TREMORZILLA_CHARGE_NORMAL.get(), 8.0F, pitch);
                    this.chargeSoundCooldown = 19;
                }
                if (this.chargeSoundCooldown > 0) {
                    --this.chargeSoundCooldown;
                }
            } else {
                this.setSpikesDownAmount(Math.max(this.getSpikesDownAmount() - 0.05F, 0.0F));
                if (this.getDeltaMovement().horizontalDistance() < 0.05 && this.getAnimation() == NO_ANIMATION && !this.isStaying() && !this.isNoAi() && this.random.nextInt(800) == 0 && !this.isVehicle()) {
                    this.tryRoar();
                }
            }
            if (!this.isPowered()) {
                this.setCharge(this.getCharge() + 1);
            }
            float healthAmount = this.getHealth() / this.getMaxHealth();
            if (healthAmount <= 0.2F) {
                this.healEveryTick(10, 5.0F);
            } else if (healthAmount <= 0.5F) {
                this.healEveryTick(20, 3.0F);
            } else {
                this.healEveryTick(100, 2.0F);
            }
        }
        if (!this.isPowered()) {
            this.setSpikesDownAmount(0.0F);
        }
        if (this.steamFromMouthFor > 0) {
            --this.steamFromMouthFor;
        }
        if (this.roarCooldown > 0) {
            --this.roarCooldown;
        }
        if (this.wasPreviouslyChild != this.isBaby()) {
            this.wasPreviouslyChild = this.isBaby();
            this.refreshDimensions();
            for (TremorzillaServantPartEntity tremorzillaServantPartEntity : this.allParts) {
                tremorzillaServantPartEntity.refreshDimensions();
            }
        }
        if (this.hasEffect(ACEffectRegistry.IRRADIATED.get())) {
            MobEffectInstance instance = this.getEffect(ACEffectRegistry.IRRADIATED.get());
            int level = instance == null ? 1 : 1 + instance.getAmplifier();
            this.heal(level * 12);
            this.removeEffect(ACEffectRegistry.IRRADIATED.get());
        }
        if (this.getAnimation() == ANIMATION_BITE && this.getAnimationTick() == 2) {
            this.playSound(ACSoundRegistry.TREMORZILLA_BITE.get(), 4.0F, this.getVoicePitch());
        }
        if ((this.getAnimation() == ANIMATION_RIGHT_SCRATCH || this.getAnimation() == ANIMATION_LEFT_SCRATCH) && this.getAnimationTick() == 2) {
            this.playSound(ACSoundRegistry.TREMORZILLA_SCRATCH_ATTACK.get(), 4.0F, this.getVoicePitch());
        }
        if ((this.getAnimation() == ANIMATION_RIGHT_STOMP || this.getAnimation() == ANIMATION_LEFT_STOMP) && this.getAnimationTick() == 2) {
            this.playSound(ACSoundRegistry.TREMORZILLA_STOMP_ATTACK.get(), 4.0F, this.getVoicePitch());
        }
        if ((this.getAnimation() == ANIMATION_RIGHT_TAIL || this.getAnimation() == ANIMATION_LEFT_TAIL) && this.getAnimationTick() == 2) {
            this.playSound(ACSoundRegistry.TREMORZILLA_TAIL_ATTACK.get(), 4.0F, this.getVoicePitch());
        }
        if (this.getAnimation() == ANIMATION_CHEW && this.getAnimationTick() % 6 == 0 && this.getAnimationTick() <= 30) {
            this.playSound(ACSoundRegistry.TREMORZILLA_EAT.get(), 4.0F, this.getVoicePitch());
            if (this.level().isClientSide) {
                BlockParticleOption particleOption1 = new BlockParticleOption(ParticleTypes.BLOCK, ACBlockRegistry.BLOCK_OF_URANIUM.get().defaultBlockState());
                BlockParticleOption particleOption2 = new BlockParticleOption(ParticleTypes.BLOCK, ACBlockRegistry.WASTE_DRUM.get().defaultBlockState());
                for (int i = 0; i < 8; ++i) {
                    Vec3 particlesPos = this.getBeamShootFrom(1.0F).add(new Vec3(this.random.nextBoolean() ? -0.8F : 0.8F, 2.0, 2.5F + this.random.nextFloat()).scale(this.getScale()).xRot((float) Math.toRadians(-this.getXRot())).yRot((float) Math.toRadians(-this.getYHeadRot())));
                    this.level().addAlwaysVisibleParticle(this.random.nextInt(3) == 0 ? particleOption2 : particleOption1, true, particlesPos.x, particlesPos.y, particlesPos.z, 0.0, 0.0, 0.0);
                }
            }
        }
        if (!this.level().isClientSide && this.getAnimation() == ANIMATION_CHEW && this.getAnimationTick() == 34 && this.lastFedPlayer != null) {
            this.heal(50.0F);
            this.lastFedPlayer = null;
        }
        this.lastStompX = this.xo;
        this.lastStompZ = this.zo;
    }

    private double getMaxFluidHeight() {
        return this.getFluidTypeHeight(this.getMaxHeightFluidType());
    }

    private void healEveryTick(int i, float health) {
        if (this.tickCount % i == 0) {
            this.heal(health);
        }
    }

    private void tickBeamTargeting() {
        LivingEntity target = this.getTarget();
        Vec3 vec3 = this.beamServerTarget == null ? this.position() : this.beamServerTarget;
        Vec3 shootFrom = this.getBeamShootFrom(1.0F);
        if (this.isVehicle() && this.getControllingPassenger() instanceof Player player) {
            Vec3 riderPointing = player.getViewVector(1.0F).scale(100.0);
            this.beamServerTarget = shootFrom.add(riderPointing).subtract(vec3).scale(0.2F).add(vec3);
        } else if (target != null && target.isAlive()) {
            float time = (float) this.beamTime / (float) this.maxBeamTime;
            float accuracy = 1.0F - Math.min(0.75F, time) / 0.75F;
            Vec3 position = target.position();
            Vec3 swingVec = new Vec3(Math.sin((float) this.tickCount * 0.2F) * 6.0, 0.0, Math.cos((float) this.tickCount * 0.2F) * -6.0).yRot((float) Math.toRadians(-this.yBodyRot)).scale(accuracy);
            this.beamServerTarget = position.add(swingVec).subtract(vec3).scale(0.1F).add(vec3);
        } else {
            Vec3 newTarget = new Vec3(Math.sin((float) this.tickCount * 0.1F) * 10.0, vec3.y - shootFrom.y, 6.0).yRot((float) Math.toRadians(-this.yBodyRot));
            this.beamServerTarget = shootFrom.add(newTarget).subtract(vec3).scale(0.1F).add(vec3);
        }
    }

    private Vec3 createInitialBeamVec() {
        LivingEntity target = this.getTarget();
        if (target != null && target.isAlive()) {
            Vec3 randomRot = new Vec3(-100.0F + this.random.nextFloat() * 200.0F, 0.0, 15.0F + 15.0F * this.random.nextFloat()).yRot((float) Math.toRadians(-this.yBodyRot + 50.0F - this.random.nextFloat() * 100.0F));
            Vec3 position = target instanceof KaijuMob ? target.getEyePosition() : target.position();
            return position.add(randomRot);
        }
        if (this.isVehicle()) {
            Vec3 vec3 = new Vec3(0.0, 0.0, 10.0).yRot((float) Math.toRadians(-this.yBodyRot));
            return this.getBeamShootFrom(1.0F).add(vec3);
        }
        Vec3 vec3 = new Vec3(0.0, this.random.nextBoolean() ? 100.0 : 20.0, 6.0).yRot((float) Math.toRadians(-this.yBodyRot));
        return this.getBeamShootFrom(1.0F).add(vec3);
    }

    @Override
    public void travel(Vec3 vec3d) {
        if (this.getAnimation() == ANIMATION_LEFT_STOMP || this.getAnimation() == ANIMATION_RIGHT_STOMP || this.getAnimation() == ANIMATION_LEFT_TAIL || this.getAnimation() == ANIMATION_RIGHT_TAIL || this.isFiring() && !this.isVehicle()) {
            vec3d = Vec3.ZERO;
            super.travel(vec3d);
        } else if (this.isInFluidType() && (this.isEffectiveAi() || this.isVehicle())) {
            this.moveRelative(this.getSpeed(), vec3d);
            Vec3 delta = this.getDeltaMovement();
            this.move(MoverType.SELF, delta);
            if (this.horizontalCollision) {
                delta = delta.add(0.0, 0.05, 0.0);
            }
            this.setDeltaMovement(delta.scale(0.8));
            this.calculateEntityAnimation(false);
        } else {
            super.travel(vec3d);
        }
    }

    public int getHeadRotSpeed() {
        return 3;
    }

    @Override
    public void calculateEntityAnimation(boolean flying) {
        float f1 = (float) Mth.length(this.getX() - this.lastStompX, 0.0, this.getZ() - this.lastStompZ);
        float walkSpeed = 4.0F;
        if (this.isVehicle()) {
            walkSpeed = 1.5F;
        }
        float f2 = Math.min(f1 * walkSpeed, 1.0F);
        this.walkAnimation.update(f2, 0.4F);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            if (ForgeEventFactory.getMobGriefingEvent(this.level(), this) && this.blockBreakCounter <= 0) {
                this.breakBlocksInBoundingBox(0.1F);
                this.blockBreakCounter = 10;
            }
            if (this.blockBreakCounter > 0) {
                --this.blockBreakCounter;
            }
        }
    }

    @Override
    public void playAmbientSound() {
        if (this.getAnimation() == NO_ANIMATION && !this.level().isClientSide && !this.isFiring()) {
            this.syncAnimation(ANIMATION_SPEAK);
        }
    }

    public void actuallyPlayAmbientSound() {
        SoundEvent soundevent = this.getAmbientSound();
        if (soundevent != null) {
            this.playSound(soundevent, this.getSoundVolume(), this.getVoicePitch());
        }
    }

    @Override
    public void remove(Entity.RemovalReason removalReason) {
        AlexsCaves.PROXY.clearSoundCacheFor(this);
        super.remove(removalReason);
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
    protected void tickDeath() {
        ++this.deathTime;
        if (this.deathTime >= 20 && !this.level().isClientSide() && !this.isRemoved()) {
            if (this.getTrueOwner() != null && MobsConfig.TremorzillaServantReturnEgg.get()) {
                FlyingItem flyingItem = new FlyingItem(ModEntityType.FLYING_ITEM.get(), this.level(), this.getX(), this.getY(), this.getZ());
                flyingItem.setOwner(this.getTrueOwner());
                flyingItem.setItem(new ItemStack(AcItems.TREMORZILLA_SERVANT_EGG.get()));
                this.level().addFreshEntity(flyingItem);
            }
            this.level().broadcastEntityEvent(this, (byte) 60);
            this.remove(Entity.RemovalReason.KILLED);
        }
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
        Vec3[] positions = new Vec3[this.allParts.length];
        for (int j = 0; j < this.allParts.length; ++j) {
            positions[j] = new Vec3(this.allParts[j].getX(), this.allParts[j].getY(), this.allParts[j].getZ());
        }
        boolean tail = this.getAnimation() == ANIMATION_LEFT_TAIL || this.getAnimation() == ANIMATION_RIGHT_TAIL;
        float tailRotateSpeed = tail ? 25.0F : (this.isTremorzillaSwimming() ? 20.0F : 5.0F);
        this.tailXRot = this.wrapTailDegrees(Mth.approachDegrees(this.tailXRot, this.getTargetTailXRot(), tailRotateSpeed));
        this.tailYRot = this.wrapTailDegrees(Mth.approachDegrees(this.tailYRot, this.getTargetTailYRot(), tailRotateSpeed));
        Vec3 center = this.position().add(0.0, this.getBbHeight() * 0.5F - this.getLegSolverBodyOffset(), 0.0);
        float tailXStep = this.tailXRot / 5.0F;
        float tailYStep = this.tailYRot / 5.0F;
        this.tailPart1.setPosCenteredY(this.rotateOffsetVec(new Vec3(0.0, this.isTremorzillaSwimming() ? 0.0 : -4.0, -3.5).scale(this.getScale()), tailXStep, this.yBodyRot + tailYStep).add(center));
        this.tailPart2.setPosCenteredY(this.rotateOffsetVec(new Vec3(0.0, -0.25, -3.25).scale(this.getScale()), tailXStep, this.yBodyRot + tailYStep * 2.0F).add(this.tailPart1.centeredPosition()));
        this.tailPart3.setPosCenteredY(this.rotateOffsetVec(new Vec3(0.0, 0.0, -2.5).scale(this.getScale()), tailXStep, this.yBodyRot + tailYStep * 3.0F).add(this.tailPart2.centeredPosition()));
        this.tailPart4.setPosCenteredY(this.rotateOffsetVec(new Vec3(0.0, 0.0, -2.5).scale(this.getScale()), tailXStep, this.yBodyRot + tailYStep * 4.0F).add(this.tailPart3.centeredPosition()));
        this.tailPart5.setPosCenteredY(this.rotateOffsetVec(new Vec3(0.0, 0.0, -2.0).scale(this.getScale()), tailXStep, this.yBodyRot + tailYStep * 5.0F).add(this.tailPart4.centeredPosition()));
        for (int l = 0; l < this.allParts.length; ++l) {
            this.allParts[l].xo = positions[l].x;
            this.allParts[l].yo = positions[l].y;
            this.allParts[l].zo = positions[l].z;
            this.allParts[l].xOld = positions[l].x;
            this.allParts[l].yOld = positions[l].y;
            this.allParts[l].zOld = positions[l].z;
        }
    }

    private float getTargetTailXRot() {
        if (this.getAnimation() == ANIMATION_LEFT_TAIL || this.getAnimation() == ANIMATION_RIGHT_TAIL) {
            return this.getAnimationTick() > 10 ? 45.0F : 0.0F;
        }
        return 0.0F;
    }

    private float getTargetTailYRot() {
        float target = this.getYawFromBuffer(this.isTremorzillaSwimming() ? 2 : 20, 1.0F) - this.yBodyRot;
        float swimAmount = this.getSwimAmount(1.0F);
        float swimAddition = (float) ((double) swimAmount * Math.sin((float) this.tickCount * 0.4F) * 25.0);
        float swingAddition = (float) (Math.sin((float) this.tickCount * 0.03F) * 10.0);
        if (this.isStaying()) {
            return target + 90.0F;
        }
        if (this.getAnimation() == ANIMATION_LEFT_TAIL || this.getAnimation() == ANIMATION_RIGHT_TAIL) {
            return this.lastYawBeforeWhip - this.yBodyRot + (float) this.getAnimationTick() > 15.0F ? -70.0F : 70.0F;
        }
        return target + swimAddition + swingAddition;
    }

    public float getLegSolverBodyOffset() {
        float swimAmount = this.getSwimAmount(1.0F);
        float heightBackLeft = this.legSolver.backLeft.getHeight(1.0F);
        float heightBackRight = this.legSolver.backRight.getHeight(1.0F);
        return Math.max(heightBackLeft, heightBackRight) * 0.8F * (1.0F - swimAmount);
    }

    protected Vec3 rotateOffsetVec(Vec3 offset, float xRot, float yRot) {
        return offset.xRot(-xRot * ((float) Math.PI / 180)).yRot(-yRot * ((float) Math.PI / 180));
    }

    public boolean isStunned() {
        return this.hasEffect(ACEffectRegistry.STUNNED.get());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setAltSkin(compound.getInt("AltSkin"));
        this.setHasEgg(compound.getBoolean("HasEgg"));
        this.followingStanceEnforced = compound.getBoolean("FollowingStanceEnforced");
        this.setCharge(compound.getInt("Charge"));
        this.setSpikesDownAmount(compound.getFloat("SpikesDownAmount"));
        this.wantsToUseBeamFromServer = compound.getBoolean("ServerBeamTrigger");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("AltSkin", this.getAltSkin());
        compound.putBoolean("HasEgg", this.hasEgg());
        compound.putBoolean("FollowingStanceEnforced", this.followingStanceEnforced);
        compound.putInt("Charge", this.getCharge());
        compound.putFloat("SpikesDownAmount", this.getSpikesDownAmount());
        compound.putBoolean("ServerBeamTrigger", this.wantsToUseBeamFromServer);
    }

    private void tickBreath() {
        if (this.level().isClientSide) {
            Vec3 endBeamPos = this.getClientBeamEndPosition(1.0F);
            if (endBeamPos != null) {
                Vec3 particleVec = endBeamPos.add((this.random.nextFloat() - 0.5F) * 3.0F, (this.random.nextFloat() - 0.5F) * 3.0F, (this.random.nextFloat() - 0.5F) * 3.0F);
                this.level().addAlwaysVisibleParticle(this.getAltSkin() == 2 ? ACParticleRegistry.TREMORZILLA_TECTONIC_EXPLOSION.get() : (this.getAltSkin() == 1 ? ACParticleRegistry.TREMORZILLA_RETRO_EXPLOSION.get() : ACParticleRegistry.TREMORZILLA_EXPLOSION.get()), true, particleVec.x, particleVec.y, particleVec.z, 0.0, 0.0, 0.0);
                this.level().addAlwaysVisibleParticle(this.getAltSkin() == 2 ? ACParticleRegistry.TREMORZILLA_TECTONIC_LIGHTNING.get() : (this.getAltSkin() == 1 ? ACParticleRegistry.TREMORZILLA_RETRO_LIGHTNING.get() : ACParticleRegistry.TREMORZILLA_LIGHTNING.get()), true, this.getX(), this.getEyeY(), this.getZ(), (double) this.getId(), 0.0, 0.0);
                if (this.getRandom().nextFloat() < 0.3F) {
                    // AC 的 TREMORZILLA_PROTON 同样强依赖 instanceof TremorzillaEntity(会喷到世界原点),改用沿光束方向飞行的电火花。
                    Vec3 protonMouth = this.getBeamShootFrom(1.0F);
                    Vec3 protonDir = endBeamPos.subtract(protonMouth).normalize().scale(0.4D);
                    this.level().addAlwaysVisibleParticle(ParticleTypes.ELECTRIC_SPARK, true,
                            protonMouth.x, protonMouth.y, protonMouth.z,
                            protonDir.x + (this.random.nextFloat() - 0.5F) * 0.05D,
                            protonDir.y + (this.random.nextFloat() - 0.5F) * 0.05D,
                            protonDir.z + (this.random.nextFloat() - 0.5F) * 0.05D);
                }
            }
        } else {
            if (this.beamServerTarget != null) {
                Vec3 from = this.getBeamShootFrom(1.0F);
                Vec3 normalized = from.add(this.beamServerTarget.subtract(from).normalize().scale(100.0));
                this.setBeamEndPosition(this.level().clip(new ClipContext(from, normalized, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getLocation());
            }
            Vec3 endBeamPos = this.getBeamEndPosition();
            boolean brokenClosestBlocks = false;
            float furthestBlockDist = 10.0F;
            if (endBeamPos != null && this.beamTime % 3 == 0) {
                Vec3 start = this.getBeamShootFrom(1.0F);
                Vec3 startClip = start;
                Vec3 viewVec = endBeamPos.subtract(startClip).normalize();
                float destructionScale = 5.0F;
                float walkThroughBeam = 1.0F;
                while ((double) walkThroughBeam < this.getMaxBeamBreakLength()) {
                    startClip = startClip.add(viewVec.scale(destructionScale * 1.5F));
                    if (!brokenClosestBlocks) {
                        brokenClosestBlocks = this.breakBlocksAround(startClip, AlexsCaves.COMMON_CONFIG.devastatingTremorzillaBeam.get() ? destructionScale : destructionScale * 0.75F, false, true, 0.08F);
                        furthestBlockDist = (float) startClip.distanceTo(start);
                    }
                    this.hurtEntitiesAround(startClip, destructionScale + 1.0F, 20.0F, 1.0F, true, true, false);
                    walkThroughBeam += destructionScale;
                }
                this.hurtEntitiesAround(endBeamPos, 6.0F, 20.0F, 1.0F, true, true, false);
                if (AlexsCaves.COMMON_CONFIG.devastatingTremorzillaBeam.get() && this.beamTime % 6 == 0) {
                    this.breakBlocksAround(endBeamPos, 4.0F, false, true, 0.08F);
                }
            }
            if (brokenClosestBlocks) {
                this.setMaxBeamBreakLength((float) Math.max(furthestBlockDist, this.getMaxBeamBreakLength() - 5.0));
            }
        }
        this.steamFromMouthFor = 200;
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> dataAccessor) {
        super.onSyncedDataUpdated(dataAccessor);
        if (SWIMMING.equals(dataAccessor)) {
            this.refreshDimensions();
        }
    }

    private void stompEffect(boolean left, float size, float hurtSize, float forwards, float damage) {
        float particleRadius = 0.3F + size * this.getScale();
        Vec3 center = this.position().add(new Vec3(left ? 2.2F : -2.2F, 0.0, forwards).yRot(-this.yBodyRot * ((float) Math.PI / 180)));
        if (this.level().isClientSide) {
            for (int i = 0; i < 4; ++i) {
                for (int i1 = 0; i1 < 10 + this.random.nextInt(10); ++i1) {
                    double motionX = this.getRandom().nextGaussian() * 0.07;
                    double motionY = 0.07 + this.getRandom().nextGaussian() * 0.07;
                    double motionZ = this.getRandom().nextGaussian() * 0.07;
                    float angle = (float) Math.PI / 180 * this.yBodyRot + (float) i1;
                    double extraX = particleRadius * Mth.sin((float) (Math.PI + (double) angle));
                    double extraY = 1.0;
                    double extraZ = particleRadius * Mth.cos(angle);
                    Vec3 groundedVec = ACMath.getGroundBelowPosition(this.level(), new Vec3((double) Mth.floor(center.x + extraX), (double) (Mth.floor(center.y + extraY) - 1), (double) Mth.floor(center.z + extraZ)));
                    BlockPos ground = BlockPos.containing(groundedVec.subtract(0.0, 0.5, 0.0));
                    BlockState state = this.level().getBlockState(ground);
                    if (!state.isSolid()) continue;
                    this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, state), true, center.x + extraX, (double) ground.getY() + extraY, center.z + extraZ, motionX, motionY, motionZ);
                }
            }
        }
        this.hurtEntitiesAround(center, particleRadius + hurtSize, damage, 0.5F, false, false, false);
    }

    public boolean hurtEntitiesAround(Vec3 center, float radius, float damageAmount, float knockbackAmount, boolean radioactive, boolean hurtsOtherKaiju, boolean stretchY) {
        return this.hurtEntitiesAround(center, radius, damageAmount, 0.0F, knockbackAmount, radioactive, hurtsOtherKaiju, stretchY);
    }

    /**
     * 对范围内实体造成伤害。damageAmount 为基础伤害;hpPercentDamage &gt; 0 时额外附加目标最大生命值百分比伤害,
     * 机制参考 Goety 红石怪兽(RedstoneMonstrosity)的 RedstoneMonstrosityHPPercentDamage 配置:总伤害 = 基础 + 目标最大生命值 * 百分比。
     */
    public boolean hurtEntitiesAround(Vec3 center, float radius, float damageAmount, float hpPercentDamage, float knockbackAmount, boolean radioactive, boolean hurtsOtherKaiju, boolean stretchY) {
        AABB aabb = new AABB(center.subtract(radius, radius, radius), center.add(radius, radius, radius));
        if (stretchY) {
            aabb.setMinY(this.getY() - 1.0);
            aabb.setMaxY(this.getEyeY() + 3.0);
        }
        boolean flag = false;
        DamageSource damageSource = radioactive ? ACDamageTypes.causeTremorzillaBeamDamage(this.level().registryAccess(), this) : this.damageSources().mobAttack(this);
        for (LivingEntity living : this.level().getEntitiesOfClass(LivingEntity.class, aabb, EntitySelector.NO_CREATIVE_OR_SPECTATOR)) {
            if (living.is(this) || this.isAlliedTo(living) || living.getType() == this.getType()) continue;
            double d = center.x;
            double d2 = stretchY ? living.getY() : center.y;
            if (!(living.distanceToSqr(d, d2, center.z) <= (double) (radius * radius)) || radioactive && !this.canEntityBeHurtByBeam(living, center) || !hurtsOtherKaiju && living instanceof KaijuMob) continue;
            float damage = hpPercentDamage > 0.0F ? damageAmount + living.getMaxHealth() * hpPercentDamage : damageAmount;
            if (!living.hurt(damageSource, damage)) continue;
            flag = true;
            this.knockbackTarget(living, knockbackAmount, this.getX() - living.getX(), this.getZ() - living.getZ(), !(living instanceof KaijuMob));
            if (!radioactive) continue;
            if (living.getHealth() <= 0.0F && living instanceof Enemy) {
                ++this.killCountFromBeam;
            }
            living.addEffect(new MobEffectInstance(ACEffectRegistry.IRRADIATED.get(), 6000, 2));
        }
        return flag;
    }

    private boolean canEntityBeHurtByBeam(LivingEntity living, Vec3 center) {
        return this.level().clip(new ClipContext(center, living.position().add(0.0, living.getBbHeight() * 0.5, 0.0), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() == HitResult.Type.MISS;
    }

    public void knockbackTarget(Entity target, double strength, double x, double z, boolean ignoreResistance) {
        LivingKnockBackEvent event = ForgeHooks.onLivingKnockBack(this, (float) strength, x, z);
        if (event.isCanceled()) {
            return;
        }
        strength = event.getStrength();
        x = event.getRatioX();
        z = event.getRatioZ();
        if (!ignoreResistance) {
            strength *= 1.0 - this.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
        }
        if (!(strength <= 0.0)) {
            this.hasImpulse = true;
            Vec3 vec3 = this.getDeltaMovement();
            Vec3 vec31 = new Vec3(x, 0.0, z).normalize().scale(strength);
            target.setDeltaMovement(vec3.x / 2.0 - vec31.x, this.onGround() ? Math.min(0.4, vec3.y / 2.0 + strength) : vec3.y, vec3.z / 2.0 - vec31.z);
        }
    }

    public boolean breakBlocksAround(Vec3 center, float radius, boolean square, boolean triggerExplosions, float dropChance) {
        if (!MobsConfig.TremorzillaServantBreakBlocks.get() || this.isBaby() || !ForgeEventFactory.getMobGriefingEvent(this.level(), this) || this.level().isClientSide) {
            return false;
        }
        boolean flag = false;
        for (BlockPos blockpos : BlockPos.betweenClosed((int) Mth.floor(center.x - (double) radius), (int) Mth.floor(center.y - (double) radius), (int) Mth.floor(center.z - (double) radius), (int) Mth.floor(center.x + (double) radius), (int) Mth.floor(center.y + (double) radius), (int) Mth.floor(center.z + (double) radius))) {
            BlockState blockstate = this.level().getBlockState(blockpos);
            boolean nuke = blockstate.is(ACBlockRegistry.NUCLEAR_BOMB.get());
            if (blockstate.is(ACTagRegistry.NUKE_PROOF) || !blockstate.blocksMotion() || !(blockstate.getBlock().getExplosionResistance() <= 15.0F) && !nuke || !square && !(blockpos.distToCenterSqr(center.x, center.y, center.z) < (double) (radius * radius))) continue;
            if (this.random.nextFloat() <= dropChance && !nuke) {
                this.level().destroyBlock(blockpos, true);
            } else {
                blockstate.onBlockExploded(this.level(), blockpos, this.dummyExplosion);
            }
            if (triggerExplosions && nuke) {
                NuclearBombEntity bomb = ACEntityRegistry.NUCLEAR_BOMB.get().create(this.level());
                bomb.setPos((double) blockpos.getX() + 0.5, blockpos.getY(), (double) blockpos.getZ() + 0.5);
                bomb.setTime(300);
                this.level().addFreshEntity(bomb);
            }
            flag = true;
        }
        return flag;
    }

    public boolean breakBlocksInBoundingBox(float dropChance) {
        if (!MobsConfig.TremorzillaServantBreakBlocks.get() || this.isBaby() || !ForgeEventFactory.getMobGriefingEvent(this.level(), this) || this.level().isClientSide) {
            return false;
        }
        boolean flag = false;
        AABB boundingBox = this.getBoundingBox().inflate(2.0);
        int swimUp = this.isTremorzillaSwimming() ? 3 : 1 - (int) this.getLegSolverBodyOffset();
        for (BlockPos blockpos : BlockPos.betweenClosed((int) Mth.floor(boundingBox.minX), (int) Mth.floor(boundingBox.minY + (double) swimUp), (int) Mth.floor(boundingBox.minZ), (int) Mth.floor(boundingBox.maxX), (int) Mth.floor(boundingBox.maxY), (int) Mth.floor(boundingBox.maxZ))) {
            BlockState blockstate = this.level().getBlockState(blockpos);
            if (blockstate.isAir() || blockstate.is(ACTagRegistry.NUKE_PROOF) || !blockstate.is(BlockTags.LEAVES) && blockpos.getY() <= this.getBlockY() || !(blockstate.getBlock().getExplosionResistance() <= 15.0F) || !blockstate.is(Blocks.COBWEB) && blockstate.getCollisionShape(this.level(), blockpos).isEmpty()) continue;
            if (this.random.nextFloat() <= dropChance) {
                this.level().destroyBlock(blockpos, true);
            } else {
                this.level().setBlockAndUpdate(blockpos, Blocks.AIR.defaultBlockState());
            }
            flag = true;
        }
        return flag;
    }

    public void tryRoar() {
        if (!(this.roarCooldown != 0 || this.getAnimation() != NO_ANIMATION || this.isFiring() || this.isStunned() || this.isBaby())) {
            this.syncAnimation(this.random.nextBoolean() ? ANIMATION_ROAR_2 : ANIMATION_ROAR_1);
            this.playSound(ACSoundRegistry.TREMORZILLA_ROAR.get(), 8.0F, 1.0F);
            this.roarCooldown = 300 + this.random.nextInt(400);
        }
    }

    /**
     * 头部最大偏转角度。AC 原版为 60(比原版 MC Mob 默认 50 还高),配合 setupAnim 里
     * netHeadYaw 按 50/50 分配到长脖子与脑袋,视觉摆动幅度过大(原版继承问题),故降至 40。
     * 仅约束 AI 看向/巡逻时的头部摆动;骑乘(tickRidden 直设 yHeadRot)与光束瞄准不受影响。
     */
    @Override
    public int getMaxHeadYRot() {
        return 40;
    }

    private float wrapTailDegrees(float f) {
        return f % 360.0F;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
    }

    private void scareMobs() {
        if (this.tickCount - this.lastScareTimestamp > 5) {
            this.lastScareTimestamp = this.tickCount;
        }
        List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(64.0, 20.0, 64.0));
        for (LivingEntity e : list) {
            if (e.getType().is(ACTagRegistry.RESISTS_TREMORSAURUS_ROAR) || this.isAlliedTo(e)
                    || e.getMaxHealth() > SCARE_MAX_HEALTH) continue;
            if (e instanceof PathfinderMob mob && !(mob instanceof TamableAnimal && ((TamableAnimal) mob).isInSittingPose())) {
                mob.setTarget(null);
                mob.setLastHurtByMob(null);
                if (mob.onGround()) {
                    Vec3 randomShake = new Vec3((double) (this.random.nextFloat() - 0.5F), 0.0, (double) (this.random.nextFloat() - 0.5F)).scale(0.1F);
                    mob.setDeltaMovement(mob.getDeltaMovement().multiply(0.7F, 1.0, 0.7F).add(randomShake));
                }
                if (this.lastScareTimestamp == this.tickCount) {
                    mob.getNavigation().stop();
                }
                if (mob.getNavigation().isDone()) {
                    Vec3 vec = LandRandomPos.getPosAway(mob, 30, 7, this.position());
                    if (vec != null) {
                        mob.getNavigation().moveTo(vec.x, vec.y, vec.z, 2.0);
                    }
                }
            }
            if (this.getTrueOwner() == null) continue;
            e.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 0, true, true));
        }
    }

    @Override
    public float getScreenShakeAmount(float partialTicks) {
        if (this.isBaby()) {
            return 0.0F;
        }
        return this.prevScreenShakeAmount + (this.screenShakeAmount - this.prevScreenShakeAmount) * partialTicks;
    }

    @Override
    public double getShakeDistance() {
        return 64.0;
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

    @Override
    public BlockState createEggBlockState() {
        return AcBlockRegistry.TREMORZILLA_SERVANT_EGG.get().defaultBlockState();
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

    private void enforceFollowingStanceOnce() {
        if (this.level().isClientSide || this.followingStanceEnforced) {
            return;
        }
        this.followingStanceEnforced = true;
        if (this.getTrueOwner() != null) {
            this.setFollowing();
        }
    }

    public float getBuryEggsProgress(float partialTicks) {
        return (this.prevBuryEggsProgress + (this.buryEggsProgress - this.prevBuryEggsProgress) * partialTicks) * 0.2F;
    }

    @Override
    public void handleEntityEvent(byte b) {
        if (b == 77) {
            this.buryingEggs = true;
            float radius = this.getBbWidth() * 0.55F;
            float particleCount = (5 + random.nextInt(5)) * radius;
            for (int i1 = 0; i1 < particleCount; i1++) {
                double motionX = (getRandom().nextFloat() - 0.5F) * 0.7D;
                double motionY = getRandom().nextFloat() * 0.7D + 0.8F;
                double motionZ = (getRandom().nextFloat() - 0.5F) * 0.7D;
                float angle = (float) (0.01745329251F * (this.yBodyRot + (i1 / particleCount) * 360F));
                double extraX = radius * Mth.sin((float) (Math.PI + angle));
                double extraY = 1.2F;
                double extraZ = radius * Mth.cos(angle);
                BlockPos ground = BlockPos.containing(ACMath.getGroundBelowPosition(level(), new Vec3(Mth.floor(this.getX() + extraX), Mth.floor(this.getY() + extraY), Mth.floor(this.getZ() + extraZ))));
                BlockState groundState = this.level().getBlockState(ground.below());
                if (groundState.isSolid() && level().isClientSide) {
                    level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, groundState), true, this.getX() + extraX, ground.getY(), this.getZ() + extraZ, motionX, motionY, motionZ);
                }
            }
        } else if (b == 78) {
            this.buryingEggs = false;
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

    @Override
    public boolean isMultipartEntity() {
        return true;
    }

    @Nullable
    @Override
    public PartEntity<?>[] getParts() {
        return this.allParts;
    }

    public static float calculateSpikesDownAmount(float progress, float spikeCount) {
        float scaledTo = progress * spikeCount;
        float remains = scaledTo % 1.0F;
        return (float) Mth.floor(scaledTo) + (float) Math.pow(remains, 5.0);
    }

    public static float calculateSpikesDownAmountAtIndex(float progress, float spikeCount, float spikeIndex) {
        return Mth.clamp(TremorzillaServant.calculateSpikesDownAmount(progress, spikeCount) - spikeIndex, 0.0F, 1.0F);
    }

    @Override
    public void onKeyPacket(Entity keyPresser, int type) {
        if (keyPresser.isPassengerOfSameVehicle(this)) {
            if (type == 2 && this.getMeterAmount() >= 1.0F && (this.getAnimation() == NO_ANIMATION || this.getAnimation() == null) && !this.wantsToUseBeamFromServer) {
                this.yBodyRot = keyPresser.getYHeadRot();
                this.setYRot(keyPresser.getYHeadRot());
                this.wantsToUseBeamFromServer = true;
                this.maxBeamTime = 200;
            }
            if (type == 3 && (this.getAnimation() == NO_ANIMATION || this.getAnimation() == null)) {
                this.setYHeadRot(keyPresser.getYHeadRot());
                this.setXRot(keyPresser.getXRot());
                float decision = this.getRandom().nextFloat();
                if (decision < 0.33F) {
                    this.syncAnimation(this.getRandom().nextBoolean() ? ANIMATION_LEFT_SCRATCH : ANIMATION_RIGHT_SCRATCH);
                } else if (decision < 0.66F && !this.isSwimming()) {
                    this.syncAnimation(this.getRandom().nextBoolean() ? ANIMATION_LEFT_STOMP : ANIMATION_RIGHT_STOMP);
                } else {
                    this.syncAnimation(ANIMATION_BITE);
                }
            }
        }
    }

    public float maxSitTicks() {
        return 20.0F;
    }

    private Stream<BlockPos> getNearbySirens(ServerLevel world, int range) {
        PoiManager pointofinterestmanager = world.getPoiManager();
        return pointofinterestmanager.findAll(poiTypeHolder -> poiTypeHolder.is(ACPOIRegistry.NUCLEAR_SIREN.getKey()), pos -> true, this.blockPosition(), range, PoiManager.Occupancy.ANY);
    }

    private void activateSiren(BlockPos pos) {
        BlockEntity blockEntity = this.level().getBlockEntity(pos);
        if (blockEntity instanceof NuclearSirenBlockEntity nuclearSirenBlock) {
            nuclearSirenBlock.setNearestNuclearBomb(this);
        }
    }

    @Override
    public boolean shouldStopBlaringSirens() {
        return !this.isPowered() || this.getSpikesDownAmount() <= 0.0F || this.isRemoved();
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!this.level().isClientSide) {
            InteractionResult altSkinResult = this.tryChangeAltSkin(player, hand);
            if (altSkinResult != null) {
                return altSkinResult;
            }
            ItemStack itemstack = player.getItemInHand(hand);
            if (this.getTrueOwner() != null && player == this.getTrueOwner()) {
                if (this.isFood(itemstack)) {
                    // 繁殖系统已删除:不再走 AnimalSummon.mobInteract(那会 setInLove 触发交配),
                    // 喂食核弹直接为光束充能(保留 AC 吃放射性物质回能的设定);旧存档幼体仍可加速成长。
                    if (this.isBaby()) {
                        return super.mobInteract(player, hand);
                    }
                    this.usePlayerItem(player, hand, itemstack);
                    this.setCharge(Math.min(1000, this.getCharge() + 300));
                    return InteractionResult.SUCCESS;
                }
                if (itemstack.is(ACBlockRegistry.WASTE_DRUM.get().asItem())) {
                    // 拿着废料桶时绝不进入下面的骑乘分支;动画期间喂不了就直接 PASS,避免误骑乘
                    if (this.getAnimation() == NO_ANIMATION) {
                        this.usePlayerItem(player, hand, itemstack);
                        this.syncAnimation(ANIMATION_CHEW);
                        this.lastFedPlayer = player;
                        return InteractionResult.SUCCESS;
                    }
                    return InteractionResult.PASS;
                }
                if (!player.isCrouching() && !this.isBaby()) {
                    Entity entity = this.getFirstPassenger();
                    if (entity != null && entity != player) {
                        entity.stopRiding();
                        return InteractionResult.SUCCESS;
                    }
                    if (!(itemstack.getItem() instanceof IWand)) {
                        this.doPlayerRide(player);
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        return super.mobInteract(player, hand);
    }

    protected void doPlayerRide(Player player) {
        if (!this.level().isClientSide) {
            player.setYRot(this.getYRot());
            player.setXRot(this.getXRot());
            player.startRiding(this);
        }
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return passenger instanceof Player;
    }

    public boolean canOwnerMount(Player player) {
        return !this.isBaby();
    }

    public boolean canOwnerCommand(Player ownerPlayer) {
        return ownerPlayer.isShiftKeyDown();
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity living) {
        return new Vec3(this.getX(), this.getBoundingBox().minY, this.getZ());
    }

    @Override
    protected void tickRidden(Player player, Vec3 vec3) {
        super.tickRidden(player, vec3);
        this.setTarget(null);
        if ((player.zza != 0.0F || player.xxa != 0.0F) && this.getAnimation() != ANIMATION_LEFT_TAIL && this.getAnimation() != ANIMATION_RIGHT_TAIL) {
            this.setRot(player.getYRot(), player.getXRot() * 0.25F);
            this.setYHeadRot(player.getYHeadRot());
        }
    }

    @Override
    protected float getRiddenSpeed(Player rider) {
        return (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED);
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        Entity entity = this.getFirstPassenger();
        if (entity instanceof Player player) {
            return player;
        }
        return null;
    }

    @Override
    public boolean canBeRiddenUnderFluidType(FluidType type, Entity rider) {
        return true;
    }

    @Override
    protected float getBlockSpeedFactor() {
        return this.isTremorzillaSwimming() || this.onSoulSpeedBlock() ? 1.0F : super.getBlockSpeedFactor();
    }

    @Override
    protected float getWaterSlowDown() {
        return 0.98F;
    }

    @Override
    protected Vec3 getRiddenInput(Player player, Vec3 deltaIn) {
        float f = player.zza < 0.0F ? 0.5F : 1.0F;
        if (this.isInFluidType()) {
            Vec3 lookVec = player.getLookAngle();
            float y = (float) lookVec.y;
            return new Vec3(player.xxa * 0.25F, y, player.zza * 0.8F * f);
        }
        return new Vec3(player.xxa * 0.35F, 0.0, player.zza * 0.8F * f);
    }

    @Override
    public void positionRider(Entity passenger, Entity.MoveFunction moveFunction) {
        if (this.isPassengerOfSameVehicle(passenger) && passenger instanceof LivingEntity living) {
            if (!this.touchingUnloadedChunk()) {
                float swimAmount = this.getSwimAmount(1.0F);
                float walkSwing = (float) (Math.cos(this.walkAnimation.position() * 0.25F + 1.0F) * 0.75 * (double) this.walkAnimation.speed() - (double) (1.5F * this.walkAnimation.speed())) * (1.0F - swimAmount);
                float animationExtraBack = 0.0F;
                if (this.getAnimation() == ANIMATION_ROAR_2) {
                    animationExtraBack = 4.0F * ACMath.cullAnimationTick(this.getAnimationTick(), 1.0F, this.getAnimation(), 1.0F, 10, 60);
                }
                if (this.getAnimation() == ANIMATION_PREPARE_BREATH) {
                    animationExtraBack = 4.0F * ACMath.cullAnimationTick(this.getAnimationTick(), 1.0F, this.getAnimation(), 1.0F, 0, 20);
                }
                Vec3 seatOffset = new Vec3(0.0, 2.0 - 6.5 * (double) swimAmount, 1.0F + 6.0F * swimAmount - walkSwing - animationExtraBack).yRot((float) Math.toRadians(-this.yBodyRot));
                passenger.setYBodyRot(this.yBodyRot);
                passenger.fallDistance = 0.0F;
                if (!this.isFiring()) {
                    this.clampRotation(living, 105.0F);
                }
                float heightBackLeft = this.legSolver.legs[0].getHeight(1.0F);
                float heightBackRight = this.legSolver.legs[1].getHeight(1.0F);
                float maxLegSolverHeight = (1.0F - ACMath.smin(1.0F - heightBackLeft, 1.0F - heightBackRight, 0.1F)) * 0.8F * (1.0F - swimAmount);
                moveFunction.accept(passenger, this.getX() + seatOffset.x, this.getY() + seatOffset.y + this.getPassengersRidingOffset() - (double) maxLegSolverHeight, this.getZ() + seatOffset.z);
                return;
            }
        }
        super.positionRider(passenger, moveFunction);
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
    public double getPassengersRidingOffset() {
        return 8.25 * (double) this.getScale();
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return Math.sqrt(distance) < 1024.0;
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        return this.getBoundingBox().inflate(6.0);
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

    public void syncAnimation(Animation animation) {
        if (this.level().isClientSide) {
            this.setAnimation(animation);
        } else {
            AnimationHandler.INSTANCE.sendAnimationMessage(this, animation);
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ACSoundRegistry.TREMORZILLA_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.TREMORZILLA_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.TREMORZILLA_DEATH.get();
    }

    @Override
    public Animation[] getAnimations() {
        return new Animation[]{ANIMATION_SPEAK, ANIMATION_ROAR_1, ANIMATION_ROAR_2, ANIMATION_RIGHT_SCRATCH, ANIMATION_LEFT_SCRATCH, ANIMATION_RIGHT_TAIL, ANIMATION_LEFT_TAIL, ANIMATION_RIGHT_STOMP, ANIMATION_LEFT_STOMP, ANIMATION_BITE, ANIMATION_PREPARE_BREATH, ANIMATION_CHEW};
    }

    @Override
    public boolean isVisuallySwimming() {
        return this.isTremorzillaSwimming();
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public EntityDimensions getDimensions(Pose poseIn) {
        return this.isTremorzillaSwimming() ? SWIMMING_SIZE.scale(this.getScale()) : super.getDimensions(poseIn);
    }

    public boolean isTremorzillaSwimming() {
        return this.entityData.get(SWIMMING);
    }

    public void setTremorzillaSwimming(boolean bool) {
        this.entityData.set(SWIMMING, bool);
    }

    public float getSpikesDownAmount() {
        return this.entityData.get(SPIKES_DOWN_PROGRESS);
    }

    public void setSpikesDownAmount(float spikesDownProgress) {
        this.entityData.set(SPIKES_DOWN_PROGRESS, spikesDownProgress);
    }

    public float getClientSpikeDownAmount(float partialTicks) {
        return this.prevClientSpikesDownAmount + (this.clientSpikesDownAmount - this.prevClientSpikesDownAmount) * partialTicks;
    }

    public boolean isFiring() {
        return this.entityData.get(FIRING);
    }

    public void setFiring(boolean firing) {
        this.entityData.set(FIRING, firing);
    }

    public float getBeamProgress(float partialTicks) {
        return (this.prevBeamProgress + (this.beamProgress - this.prevBeamProgress) * partialTicks) * 0.2F;
    }

    public int getCharge() {
        return this.entityData.get(CHARGE);
    }

    public void setCharge(int charge) {
        this.entityData.set(CHARGE, charge);
    }

    public boolean isPowered() {
        return this.getCharge() >= 1000;
    }

    @Nullable
    public Vec3 getBeamEndPosition() {
        return this.entityData.get(BEAM_END_POSITION).orElse(null);
    }

    public boolean hasRidingMeter() {
        return true;
    }

    public float getMeterAmount() {
        return (float) this.getCharge() / 1000.0F;
    }

    public void setBeamEndPosition(@Nullable Vec3 vec3) {
        this.entityData.set(BEAM_END_POSITION, Optional.ofNullable(vec3));
    }

    @Nullable
    public Vec3 getClientBeamEndPosition(float partialTicks) {
        if (this.clientBeamEndPosition != null && this.prevClientBeamEndPosition != null) {
            return this.prevClientBeamEndPosition.add(this.clientBeamEndPosition.subtract(this.prevClientBeamEndPosition).scale(partialTicks));
        }
        return null;
    }

    @Override
    public int getExperienceReward() {
        return 70;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(ACBlockRegistry.NUCLEAR_BOMB.get().asItem());
    }

    public Vec3 getBodyRotViewVector(float partialTicks) {
        return this.calculateViewVector(this.getViewXRot(partialTicks), this.yBodyRotO + (this.yBodyRot - this.yBodyRotO) * partialTicks);
    }

    public void setMaxBeamBreakLength(float f) {
        this.entityData.set(MAX_BEAM_BREAK_LENGTH, f);
    }

    public double getMaxBeamBreakLength() {
        return this.entityData.get(MAX_BEAM_BREAK_LENGTH);
    }

    @Override
    public float getStepHeight() {
        return 1.6F;
    }

    public Vec3 getBeamShootFrom(float partialTicks) {
        return this.getPosition(partialTicks).add(0.0, 7.5F * this.getScale(), 0.0);
    }

    @Override
    public int getMaxNavigableDistanceToGround() {
        return 4;
    }

    @Override
    public float getScale() {
        return this.isBaby() ? 0.15F : 1.0F;
    }

    @Override
    public MobType getMobType() {
        return ModMobType.NATURAL;
    }

    public float getDanceProgress(float partialTicks) {
        return 0.0F;
    }

    public float getSitProgress(float partialTicks) {
        return (this.prevSitProgress + (this.sitProgress - this.prevSitProgress) * partialTicks) / this.maxSitTicks();
    }

    /**
     * 仆从数量上限(参考 WarpedMoscoServant 写法):MOB_SUMMONED 召唤且 owner 已赋值时,
     * 超限直接拒绝生成。注:Goety 的 ServantSpawnEggItem 在 EntityType.spawn 返回后才 setTrueOwner,
     * 此路径 finalizeSpawn 时 owner 恒为 null,仍由 enforceServantLimitOnce(首个 tick)与蛋块前置检查兜底。
     */
    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        MobSpawnType spawnType, @Nullable SpawnGroupData groupData,
                                        @Nullable CompoundTag tag) {
        if (spawnType == MobSpawnType.MOB_SUMMONED && this.getTrueOwner() instanceof Player player) {
            if (countServants(player) >= MobsConfig.TremorzillaServantLimit.get()) {
                return null;
            }
        }
        return super.finalizeSpawn(level, difficulty, spawnType, groupData, tag);
    }

    /**
     * 仆从数量上限兜底检查。原本放在 finalizeSpawn 里是无效的:
     * 1. Goety 的 ServantSpawnEggItem 在 EntityType.spawn 返回后才 setTrueOwner,spawn 时 owner 恒为 null;
     * 2. finalizeSpawn 返回 null 并不会取消 EntityType.spawn(返回的 SpawnGroupData 未被判空)。
     * 故改为实体加入世界后的首个服务端 tick 检查:owner 已赋值,超限则直接移除。
     */
    private void enforceServantLimitOnce() {
        if (this.level().isClientSide || this.servantLimitEnforced) {
            return;
        }
        this.servantLimitEnforced = true;
        UUID ownerId = this.getOwnerId();
        if (ownerId == null || !(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        if (countServants(serverLevel, ownerId) > MobsConfig.TremorzillaServantLimit.get()) {
            this.discard();
        }
    }

    public static int countServants(ServerLevel level, UUID ownerId) {
        int count = 0;
        if (ownerId == null) {
            return count;
        }
        for (Entity entity : level.getAllEntities()) {
            if (entity instanceof TremorzillaServant servant) {
                if (ownerId.equals(servant.getOwnerId())) {
                    count++;
                }
            }
        }
        return count;
    }

    private int countServants(Player player) {
        int count = 0;
        if (player.level() instanceof ServerLevel serverLevel) {
            for (Entity entity : serverLevel.getAllEntities()) {
                if (entity instanceof TremorzillaServant servant) {
                    if (servant.getTrueOwner() == player) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    public class TremorzillaServantFollowGoal extends Goal {
        private final TremorzillaServant summonedEntity;
        private LivingEntity owner;
        private final double followSpeed;
        private final float startDistance;
        private final float stopDistance;
        private int timeToRecalcPath;

        public TremorzillaServantFollowGoal(TremorzillaServant summonedEntity, double speed, float startDistance, float stopDistance) {
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
            if (this.summonedEntity.getNavigation().isDone()) {
                return false;
            } else if (this.summonedEntity.getTarget() != null) {
                return false;
            } else if (this.summonedEntity.isPassenger()) {
                return false;
            } else if (this.owner == null || !this.owner.isAlive()) {
                return false;
            } else if (!this.summonedEntity.isFollowing() || this.summonedEntity.isCommanded()) {
                return false;
            } else {
                return this.summonedEntity.distanceToSqr(this.owner) > (double) (Mth.square(this.stopDistance));
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
                    if (!this.summonedEntity.isLeashed() && !this.summonedEntity.isPassenger()) {
                        double range = this.owner instanceof Mob ? 32.0D : 16.0D;
                        boolean teleport = this.summonedEntity.distanceToSqr(this.owner) >= Mth.square(range);
                        teleport = this.owner instanceof Mob
                                ? (teleport || (!this.summonedEntity.hasLineOfSight(this.owner) && this.summonedEntity.distanceToSqr(this.owner) >= Mth.square(8.0D)))
                                : (teleport && this.canTeleport());
                        if (teleport) {
                            this.tryToTeleportNearEntity();
                        } else {
                            this.summonedEntity.getNavigation().moveTo(this.owner, this.followSpeed);
                        }
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
                this.summonedEntity.getNavigation().stop();
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

    private class TremorzillaServantAttackGoal extends Goal {
        private Vec3 lastNavToPos;

        private TremorzillaServantAttackGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = TremorzillaServant.this.getTarget();
            return target != null && target.isAlive();
        }

        @Override
        public void start() {
            this.lastNavToPos = null;
        }

        @Override
        public void stop() {
        }

        @Override
        public void tick() {
            LivingEntity target = TremorzillaServant.this.getTarget();
            if (target != null) {
                double dist = TremorzillaServant.this.distanceTo(target);
                float combinedDist = TremorzillaServant.this.getBbWidth() + target.getBbWidth();
                if (!TremorzillaServant.this.isFiring()) {
                    if (TremorzillaServant.this.isPowered() && !TremorzillaServant.this.wantsToUseBeamFromServer && TremorzillaServant.this.getRandom().nextInt(100) == 0 && !TremorzillaServant.this.isBaby() && !TremorzillaServant.this.isStaying()) {
                        TremorzillaServant.this.wantsToUseBeamFromServer = true;
                    }
                    if (!TremorzillaServant.this.wantsToUseBeamFromServer && TremorzillaServant.this.getAnimation() != ANIMATION_RIGHT_TAIL && TremorzillaServant.this.getAnimation() != ANIMATION_LEFT_TAIL && TremorzillaServant.this.getAnimation() != ANIMATION_LEFT_STOMP && TremorzillaServant.this.getAnimation() != ANIMATION_RIGHT_STOMP) {
                        TremorzillaServant.this.getLookControl().setLookAt(target.getX(), target.getEyeY(), target.getZ(), 1.0F, (float) TremorzillaServant.this.getMaxHeadXRot());
                    }
                    if (this.lastNavToPos == null || TremorzillaServant.this.getNavigation().isDone() && dist > (double) combinedDist + 1.0 || this.lastNavToPos.distanceTo(target.position()) > (double) TremorzillaServant.this.getBbWidth() - 1.0) {
                        TremorzillaServant.this.getNavigation().moveTo(target, 1.0);
                    }
                }
                if (dist < (double) combinedDist + 3.0 && !TremorzillaServant.this.isFiring() && TremorzillaServant.this.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
                    float decision = TremorzillaServant.this.getRandom().nextFloat();
                    if (decision < 0.25) {
                        this.tryAnimation(TremorzillaServant.this.getRandom().nextBoolean() ? ANIMATION_LEFT_SCRATCH : ANIMATION_RIGHT_SCRATCH);
                    } else if (decision < 0.5 && !TremorzillaServant.this.isSwimming() && !TremorzillaServant.this.isBaby()) {
                        this.tryAnimation(TremorzillaServant.this.getRandom().nextBoolean() ? ANIMATION_LEFT_STOMP : ANIMATION_RIGHT_STOMP);
                    } else if (decision < 0.75 && !TremorzillaServant.this.isSwimming() && !TremorzillaServant.this.isBaby()) {
                        this.tryAnimation(TremorzillaServant.this.getRandom().nextBoolean() ? ANIMATION_LEFT_TAIL : ANIMATION_RIGHT_TAIL);
                    } else {
                        this.tryAnimation(ANIMATION_BITE);
                    }
                }
                if (!TremorzillaServant.this.wantsToUseBeamFromServer && !TremorzillaServant.this.isBaby()) {
                    TremorzillaServant.this.tryRoar();
                }
            }
        }

        private boolean tryAnimation(Animation animation) {
            if (TremorzillaServant.this.getAnimation() == IAnimatedEntity.NO_ANIMATION) {
                TremorzillaServant.this.syncAnimation(animation);
                return true;
            }
            return false;
        }
    }

    private class TremorzillaServantWanderGoal extends Goal {
        private double x;
        private double y;
        private double z;
        private boolean tryLandTarget;

        private TremorzillaServantWanderGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            LivingEntity owner = TremorzillaServant.this.getTrueOwner();
            if (owner != null
                    && (TremorzillaServant.this.isStaying() || TremorzillaServant.this.isCommanded())) {
                return false;
            }
            // 与 Goety 暗兽一致:主人还在跟随启动距离内时原地待命不游荡,只有主人走远后才跟随/游荡,
            // 避免待命时原地频繁踱步、看起来像一直贴着玩家走。
            if (owner != null && TremorzillaServant.this.distanceToSqr(owner) < Mth.square(FOLLOW_START_DISTANCE)) {
                return false;
            }
            if (TremorzillaServant.this.getRandom().nextInt(40) != 0 && !TremorzillaServant.this.isTremorzillaSwimming()) {
                return false;
            }
            this.tryLandTarget = TremorzillaServant.this.isTremorzillaSwimming() ? TremorzillaServant.this.timeSwimming > 300 || TremorzillaServant.this.getRandom().nextFloat() < 0.1F : TremorzillaServant.this.getRandom().nextFloat() > 0.1F;
            Vec3 target = this.getPosition();
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
            return !TremorzillaServant.this.getNavigation().isDone() && TremorzillaServant.this.distanceToSqr(this.x, this.y, this.z) > 8.0;
        }

        @Override
        public void start() {
            TremorzillaServant.this.getNavigation().moveTo(this.x, this.y, this.z, 1.0);
        }

        public BlockPos findWaterBlock(int range) {
            BlockPos around = TremorzillaServant.this.blockPosition();
            BlockPos.MutableBlockPos move = new BlockPos.MutableBlockPos();
            move.set(TremorzillaServant.this.getX(), TremorzillaServant.this.getY(), TremorzillaServant.this.getZ());
            while (move.getY() < TremorzillaServant.this.level().getMaxBuildHeight() && !TremorzillaServant.this.level().getFluidState(move).isEmpty()) {
                move.move(0, 1, 0);
            }
            int surfaceY = move.getY();
            around = around.atY(Math.min(surfaceY - 1, around.getY()));
            for (int i = 0; i < 15; ++i) {
                BlockPos blockPos = around.offset(TremorzillaServant.this.getRandom().nextInt(range) - range / 2, TremorzillaServant.this.getRandom().nextInt(range) - range / 2, TremorzillaServant.this.getRandom().nextInt(range) - range / 2);
                if (TremorzillaServant.this.level().getFluidState(blockPos).isEmpty() || this.isTargetBlocked(Vec3.atCenterOf(blockPos)) || blockPos.getY() <= TremorzillaServant.this.level().getMinBuildHeight() + 1) continue;
                return blockPos;
            }
            return around;
        }

        public boolean isTargetBlocked(Vec3 target) {
            Vec3 Vector3d = new Vec3(TremorzillaServant.this.getX(), TremorzillaServant.this.getEyeY(), TremorzillaServant.this.getZ());
            return TremorzillaServant.this.level().clip(new ClipContext(Vector3d, target, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, TremorzillaServant.this)).getType() != HitResult.Type.MISS;
        }

        @Nullable
        protected Vec3 getPosition() {
            if (TremorzillaServant.this.isGuardingArea()) {
                return this.randomBoundPos();
            }
            Vec3 landTarget;
            if (this.tryLandTarget && (landTarget = LandRandomPos.getPos(TremorzillaServant.this, 30, 8)) != null) {
                return landTarget;
            }
            BlockPos water = this.findWaterBlock(20);
            if (water != null) {
                return Vec3.atCenterOf(water);
            }
            return null;
        }

        private Vec3 randomBoundPos() {
            BlockPos bound = TremorzillaServant.this.getBoundPos();
            if (bound == null) {
                return null;
            }
            int range = Math.max(6, IServant.GUARDING_RANGE / 2);
            for (int i = 0; i < 15; ++i) {
                BlockPos offset = bound.offset(
                        TremorzillaServant.this.getRandom().nextInt(range * 2 + 1) - range,
                        0,
                        TremorzillaServant.this.getRandom().nextInt(range * 2 + 1) - range);
                BlockPos grounded = LandRandomPos.movePosUpOutOfSolid(TremorzillaServant.this, offset);
                if (grounded == null) {
                    continue;
                }
                return Vec3.atBottomCenterOf(grounded);
            }
            return Vec3.atBottomCenterOf(bound);
        }
    }
}
