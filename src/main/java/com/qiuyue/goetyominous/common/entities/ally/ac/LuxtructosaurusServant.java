package com.qiuyue.goetyominous.common.entities.ally.ac;

import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.common.entities.ModEntityType;
import com.Polarice3.Goety.common.entities.projectiles.FlyingItem;
import com.Polarice3.Goety.common.items.revive.ReviveServantItem;
import com.Polarice3.Goety.config.ItemConfig;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.ai.SummonTargetGoal;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.common.entities.util.CameraShake;
import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.client.particle.ACParticleRegistry;
import com.github.alexmodguy.alexscaves.server.entity.ai.AdvancedPathNavigateNoTeleport;
import com.github.alexmodguy.alexscaves.server.entity.living.DinosaurEntity;
import com.github.alexmodguy.alexscaves.server.entity.living.TremorzillaEntity;
import com.github.alexmodguy.alexscaves.server.entity.util.KaijuMob;
import com.github.alexmodguy.alexscaves.server.entity.util.KeybindUsingMount;
import com.github.alexmodguy.alexscaves.server.entity.util.LuxtructosaurusLegSolver;
import com.github.alexmodguy.alexscaves.server.entity.util.TephraExplosion;
import com.github.alexmodguy.alexscaves.server.entity.util.ShakesScreen;
import com.github.alexmodguy.alexscaves.server.message.MountedEntityKeyMessage;
import com.github.alexmodguy.alexscaves.server.item.ACItemRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACMath;
import com.github.alexmodguy.alexscaves.server.misc.ACSoundRegistry;
import com.github.alexmodguy.alexscaves.server.misc.ACTagRegistry;
import com.github.alexthe666.citadel.animation.Animation;
import com.github.alexthe666.citadel.animation.AnimationHandler;
import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.server.entity.pathfinding.raycoms.IAdvancedPathingMob;
import com.github.alexthe666.citadel.server.entity.pathfinding.raycoms.ITallWalker;
import com.qiuyue.goetyominous.common.entities.ai.ac.ServantTemptGoal;
import com.qiuyue.goetyominous.common.entities.projectile.ServantTephraEntity;
import com.qiuyue.goetyominous.common.entities.util.ServantMagmaLink;
import com.qiuyue.goetyominous.common.init.ac.AcParticles;
import com.qiuyue.goetyominous.common.items.ac.AcItems;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.qiuyue.goetyominous.config.MobsConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.PlayerRideable;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LuxtructosaurusServant extends Summoned
        implements IAnimatedEntity, ShakesScreen, KaijuMob, ITallWalker, IAdvancedPathingMob,
        PlayerRideable, KeybindUsingMount {

    protected static final EntityDataAccessor<Boolean> WALKING =
            SynchedEntityData.defineId(LuxtructosaurusServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> ENRAGED =
            SynchedEntityData.defineId(LuxtructosaurusServant.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> METER_AMOUNT =
            SynchedEntityData.defineId(LuxtructosaurusServant.class, EntityDataSerializers.FLOAT);

    public static final Animation ANIMATION_SPEAK = Animation.create(15);
    public static final Animation ANIMATION_ROAR = Animation.create(60);
    public static final Animation ANIMATION_EPIC_DEATH = Animation.create(120);
    public static final Animation ANIMATION_STOMP = Animation.create(50);
    public static final Animation ANIMATION_SPEW_FLAMES = Animation.create(80);
    public static final Animation ANIMATION_JUMP = Animation.create(45);
    public static final Animation ANIMATION_LEFT_KICK = Animation.create(20);
    public static final Animation ANIMATION_RIGHT_KICK = Animation.create(20);
    public static final Animation ANIMATION_LEFT_WHIP = Animation.create(40);
    public static final Animation ANIMATION_RIGHT_WHIP = Animation.create(40);

    public final LuxtructosaurusLegSolver legSolver = new LuxtructosaurusLegSolver(0.2F, 2.0F, 1.2F, 1.9F, 2.0F);

    private Animation currentAnimation;
    private int animationTick;
    private final LuxtructosaurusServantPartEntity[] allParts;
    public final LuxtructosaurusServantPartEntity neckPart1;
    public final LuxtructosaurusServantPartEntity neckPart2;
    public final LuxtructosaurusServantPartEntity neckPart3;
    public final LuxtructosaurusServantPartEntity headPart;
    public final LuxtructosaurusServantPartEntity tailPart1;
    public final LuxtructosaurusServantPartEntity tailPart2;
    public final LuxtructosaurusServantPartEntity tailPart3;

    private int lSteps;
    private double lx;
    private double ly;
    private double lz;
    private double lyr;
    private double lxr;
    private double lxd;
    private double lyd;
    private double lzd;

    private float prevEnragedProgress;
    private float enragedProgress;
    public Vec3 jumpTarget;
    private static final int ENRAGE_OUT_OF_COMBAT_TICKS = 100;
    private static final int ENRAGE_COOLDOWN_TICKS = 400;
    private static final float JUMP_TURN_SPEED = 20.0F;
    private int outOfCombatTicks;
    private int enrageCooldown;
    private int roarFallbackTicks;
    private boolean pendingRoar;
    private int postStopTicks;
    private boolean prevOnGround;
    private int reducedDamageTicks;
    private int lastScareTimestamp;

    private float prevWalkAnimPosition;
    private float walkAnimPosition;
    private float prevWalkAnimSpeed;
    private float walkAnimSpeed;
    private double lastStompX;
    private double lastStompZ;
    private float prevLegBackAmount;
    private float legBackAmount;
    private float prevRaiseArmsAmount;
    private float raiseArmsAmount;
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
    private int stepSoundCooldown;
    private boolean followingStanceEnforced;

    public LuxtructosaurusServant(EntityType<? extends Owned> type, Level level) {
        super(type, level);
        this.setPathfindingMalus(BlockPathTypes.LAVA, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.LEAVES, 0.0F);
        this.setMaxUpStep(1.6F);
        this.moveControl = new SauropodMoveHelper();
        this.neckPart1 = new LuxtructosaurusServantPartEntity(this, this, 3.0F, 3.0F);
        this.neckPart2 = new LuxtructosaurusServantPartEntity(this, this.neckPart1, 2.0F, 2.0F);
        this.neckPart3 = new LuxtructosaurusServantPartEntity(this, this.neckPart2, 2.0F, 1.5F);
        this.headPart = new LuxtructosaurusServantPartEntity(this, this.neckPart3, 2.0F, 2.0F);
        this.tailPart1 = new LuxtructosaurusServantPartEntity(this, this, 3.0F, 2.0F);
        this.tailPart2 = new LuxtructosaurusServantPartEntity(this, this.tailPart1, 2.5F, 1.5F);
        this.tailPart3 = new LuxtructosaurusServantPartEntity(this, this.tailPart2, 2.0F, 1.0F);
        this.allParts = new LuxtructosaurusServantPartEntity[]{
                this.neckPart1, this.neckPart2, this.neckPart3, this.headPart,
                this.tailPart1, this.tailPart2, this.tailPart3};
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.LuxtructosaurusServantHealth.get())
                .add(Attributes.FOLLOW_RANGE, AttributesConfig.LuxtructosaurusServantFollowRange.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.LuxtructosaurusServantDamage.get())
                .add(Attributes.KNOCKBACK_RESISTANCE, AttributesConfig.LuxtructosaurusServantKnockbackResistance.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.LuxtructosaurusServantMovementSpeed.get())
                .add(Attributes.ARMOR, AttributesConfig.LuxtructosaurusServantArmor.get());
    }

    public static int countServants(ServerLevel level, UUID ownerId, @Nullable Entity excluded) {
        int count = 0;
        if (ownerId == null) {
            return count;
        }
        for (Entity entity : level.getAllEntities()) {
            if (entity != excluded && entity instanceof LuxtructosaurusServant servant
                    && ownerId.equals(servant.getOwnerId())) {
                count++;
            }
        }
        return count;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(WALKING, false);
        this.entityData.define(ENRAGED, false);
        this.entityData.define(METER_AMOUNT, 1.0F);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        MobSpawnType spawnType, @Nullable SpawnGroupData groupData,
                                        @Nullable CompoundTag tag) {
        if (spawnType == MobSpawnType.MOB_SUMMONED && this.getTrueOwner() instanceof Player player
                && countServants(level.getLevel(), player.getUUID(), this) >= MobsConfig.LuxtructosaurusServantLimit.get()) {
            return null;
        }
        return super.finalizeSpawn(level, difficulty, spawnType, groupData, tag);
    }

    @Override
    public void setTrueOwner(@Nullable LivingEntity livingEntity) {
        super.setTrueOwner(livingEntity);
        if (!this.level().isClientSide && livingEntity instanceof Player player
                && this.level() instanceof ServerLevel serverLevel
                && countServants(serverLevel, player.getUUID(), this) >= MobsConfig.LuxtructosaurusServantLimit.get()) {
            this.discard();
        }
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new AdvancedPathNavigateNoTeleport(this, level);
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        List<WrappedGoal> inherited = new ArrayList<>(this.targetSelector.getAvailableGoals());
        for (WrappedGoal wrapped : inherited) {
            if (wrapped.getGoal() instanceof SummonTargetGoal) {
                this.targetSelector.removeGoal(wrapped.getGoal());
            }
        }
        this.targetSelector.addGoal(1, new LuxtructosaurusTargetGoal(this));

        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new LuxtructosaurusServantMeleeGoal(this));
        this.goalSelector.addGoal(2, new ServantTemptGoal(this, 1.1D, Ingredient.of(ACItemRegistry.TECTONIC_SHARD.get()), false));
        this.goalSelector.addGoal(6, new Summoned.WanderGoal<>(this, 1.0D) {
            @Override
            public boolean canUse() {
                return !LuxtructosaurusServant.this.isRiddenByPlayer() && super.canUse();
            }
        });
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 32.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Mob.class, 8.0F));
    }

    @Override
    public void followGoal() {
        this.goalSelector.addGoal(5, new LuxtructosaurusFollowGoal(this, 1.0D, 12.0F, 4.0F));
    }

    @Override
    protected void updateControlFlags() {
        super.updateControlFlags();
        boolean steering = this.isSteeredByPlayer();
        this.goalSelector.setControlFlag(Goal.Flag.MOVE, !steering);
        this.goalSelector.setControlFlag(Goal.Flag.JUMP, !steering);
        this.goalSelector.setControlFlag(Goal.Flag.LOOK, !steering);
        this.goalSelector.setControlFlag(Goal.Flag.TARGET, !steering);
    }

    @Override
    public boolean stopTickingPathing() {
        return this.isRiddenByPlayer() || this.isStaying();
    }

    @Override
    public boolean isStaying() {
        return super.isStaying() && !this.isRiddenByPlayer();
    }

    @Override
    public boolean isAlliedTo(Entity entity) {
        if (super.isAlliedTo(entity)) {
            return true;
        }
        return this.level().isClientSide && entity != null
                && this.getOwnerId() != null && this.getOwnerId().equals(entity.getUUID());
    }

    @Override
    public float getScale() {
        return 1.0F;
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public boolean isAbleToRide(LivingEntity livingEntity) {
        return false;
    }

    @Override
    public void tryKill(Player player) {
        if (this.killChance <= 0) {
            this.warnKill(player);
        } else {
            super.tryKill(player);
        }
    }

    public boolean hasRidingMeter() {
        return true;
    }

    public float getMeterAmount() {
        return this.entityData.get(METER_AMOUNT);
    }

    public void setMeterAmount(float amount) {
        this.entityData.set(METER_AMOUNT, amount);
    }

    protected void doPlayerRide(Player player) {
        if (!this.level().isClientSide) {
            if (this.getAnimation() == ANIMATION_JUMP) {
                this.setAnimation(NO_ANIMATION);
            }
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
        this.setTarget(null);
        if (player.zza != 0.0F || player.xxa != 0.0F) {
            this.setRot(player.getYRot(), player.getXRot() * 0.25F);
            this.setYHeadRot(player.getYHeadRot());
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
        if (entity instanceof Player player && (player.zza != 0.0F || player.xxa != 0.0F)) {
            return player;
        }
        return null;
    }

    public boolean isRiddenByPlayer() {
        return this.getFirstPassenger() instanceof Player;
    }

    public boolean isSteeredByPlayer() {
        return this.getControllingPassenger() instanceof Player;
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return passenger instanceof Player;
    }

    @Override
    public void positionRider(Entity passenger, Entity.MoveFunction moveFunction) {
        if (this.isPassengerOfSameVehicle(passenger) && passenger instanceof LivingEntity living
                && !this.touchingUnloadedChunk()) {
            Vec3 seatOffset = new Vec3(0.0F, 0.5F, 0.5F).yRot((float) Math.toRadians(-this.yBodyRot));
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
        if (type == 2 && this.getMeterAmount() >= 1.0F
                && this.getAnimation() != ANIMATION_ROAR && this.getAnimation() != ANIMATION_EPIC_DEATH) {
            this.yBodyRot = keyPresser.getYHeadRot();
            this.setYRot(keyPresser.getYHeadRot());
            this.setMeterAmount(0.0F);
            this.startEnrage();
        }
    }

    private void tickEnrage() {
        if (this.level().isClientSide || !this.isAlive()) {
            return;
        }
        if (this.enrageCooldown > 0) {
            --this.enrageCooldown;
        }
        boolean inCombat = this.isInCombat();
        if (this.isEnraged()) {
            if (inCombat) {
                this.outOfCombatTicks = 0;
            } else if (++this.outOfCombatTicks >= ENRAGE_OUT_OF_COMBAT_TICKS) {
                this.outOfCombatTicks = 0;
                this.setEnraged(false);
                this.enrageCooldown = ENRAGE_COOLDOWN_TICKS;
            }
        } else if (inCombat && this.enrageCooldown <= 0 && !this.isRiddenByPlayer()) {
            this.outOfCombatTicks = 0;
            this.pendingRoar = true;
        }
    }

    public void startEnrage() {
        this.outOfCombatTicks = 0;
        this.pendingRoar = false;
        this.setEnraged(true);
        this.setAnimation(ANIMATION_ROAR);
    }

    private boolean isInCombat() {
        LivingEntity target = this.getTarget();
        if (target != null && target.isAlive()) {
            return true;
        }
        return this.getLastHurtByMob() != null
                && this.tickCount - this.getLastHurtByMobTimestamp() < ENRAGE_OUT_OF_COMBAT_TICKS;
    }

    private void tickRoarFallback() {
        if (this.level().isClientSide || !this.isAlive()) {
            return;
        }
        if (this.pendingRoar) {
            if (!this.isInCombat() || this.isRiddenByPlayer()) {
                this.pendingRoar = false;
            } else if (this.getAnimation() == NO_ANIMATION && this.isRoarStanceReady()) {
                this.pendingRoar = false;
                this.roarFallbackTicks = 0;
                this.setEnraged(true);
                this.setAnimation(ANIMATION_ROAR);
                return;
            } else {
                return;
            }
        }
        int interval = MobsConfig.LuxtructosaurusServantRoarInterval.get();
        if (interval <= 0 || !this.isEnraged() || this.isRiddenByPlayer()) {
            this.roarFallbackTicks = 0;
            return;
        }
        if (this.getAnimation() == ANIMATION_ROAR) {
            this.roarFallbackTicks = 0;
            return;
        }
        if (this.getAnimation() == ANIMATION_EPIC_DEATH) {
            return;
        }
        if (this.roarFallbackTicks < interval * 20) {
            ++this.roarFallbackTicks;
        }
        if (this.roarFallbackTicks >= interval * 20 && this.getAnimation() == NO_ANIMATION
                && this.isRoarStanceReady()) {
            this.roarFallbackTicks = 0;
            this.setAnimation(ANIMATION_ROAR);
        }
    }

    private boolean isRoarStanceReady() {
        return !this.areLegsMoving() && this.getWalkAnimSpeed(1.0F) < 0.05F;
    }

    private static final ResourceLocation PRIMAL_MAGMA_ID =
            new ResourceLocation("alexscaves", "primal_magma");

    private static boolean isMagmaHealBlock(ItemStack stack) {
        if (stack.is(Items.MAGMA_BLOCK)) {
            return true;
        }
        return stack.getItem() instanceof BlockItem blockItem
                && PRIMAL_MAGMA_ID.equals(ForgeRegistries.BLOCKS.getKey(blockItem.getBlock()));
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        if (player.isShiftKeyDown() && player == this.getTrueOwner()
                && this.getHealth() < this.getMaxHealth() && isMagmaHealBlock(itemstack)) {
            if (!player.getAbilities().instabuild) {
                itemstack.shrink(1);
            }
            this.heal(4.0F);
            if (this.level() instanceof ServerLevel serverLevel) {
                this.playSound(ACSoundRegistry.LUXTRUCTOSAURUS_ROAR.get(), 1.0F, 1.0F);
                for (int i = 0; i < 7; ++i) {
                    double d0 = this.random.nextGaussian() * 0.02D;
                    double d1 = this.random.nextGaussian() * 0.02D;
                    double d2 = this.random.nextGaussian() * 0.02D;
                    serverLevel.sendParticles(ParticleTypes.HEART,
                            this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D),
                            0, d0, d1, d2, 0.5F);
                }
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        if (!this.level().isClientSide && this.getTrueOwner() != null && player == this.getTrueOwner()
                && !player.isCrouching()
                && !(itemstack.getItem() instanceof IWand)
                && !itemstack.is(ACItemRegistry.TECTONIC_SHARD.get())) {
            Entity firstPassenger = this.getFirstPassenger();
            if (firstPassenger != null && firstPassenger != player) {
                firstPassenger.stopRiding();
                return InteractionResult.SUCCESS;
            }
            this.doPlayerRide(player);
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public boolean canFeelShake(Entity player) {
        return player.onGround() || this.getAnimation() == ANIMATION_ROAR && this.isAlive();
    }

    @Override
    public void aiStep() {
        this.tickEnrage();
        this.tickRoarFallback();
        super.aiStep();
        if (!this.level().isClientSide) {
            if (this.reducedDamageTicks > 0) {
                --this.reducedDamageTicks;
            }
            if (this.isRiddenByPlayer()) {
                if (this.getMeterAmount() < 1.0F) {
                    this.setMeterAmount(Math.min(this.getMeterAmount() + 0.0025F, 1.0F));
                }
            } else if (this.getMeterAmount() != 0.0F) {
                this.setMeterAmount(0.0F);
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        this.enforceFollowingStanceOnce();
        AnimationHandler.INSTANCE.updateAnimations(this);
        this.prevEnragedProgress = this.enragedProgress;
        if (this.isEnraged() && this.enragedProgress < 20.0F) {
            this.enragedProgress += 1.0F;
        }
        if (!this.isEnraged() && this.enragedProgress > 0.0F) {
            this.enragedProgress -= 1.0F;
        }
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
            boolean jumping = this.getAnimation() == ANIMATION_JUMP;
            if (jumping && !this.level().isClientSide && this.jumpTarget != null) {
                Vec3 toward = this.jumpTarget.subtract(this.position());
                if (toward.horizontalDistanceSqr() > 1.0E-4) {
                    float jumpYaw = -((float) Mth.atan2(toward.x, toward.z)) * 57.295776F;
                    this.setYRot(Mth.approachDegrees(this.getYRot(), jumpYaw, JUMP_TURN_SPEED));
                }
            }
            this.yBodyRot = Mth.approachDegrees(this.yBodyRotO, this.getYRot(),
                    jumping ? JUMP_TURN_SPEED : this.turningFast ? 10.0F : 2.0F);
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
        if (!this.level().isClientSide && !this.isRiddenByPlayer() && !this.turningFast
                && this.getAnimation() != ANIMATION_JUMP && this.areLegsMoving()) {
            this.setYHeadRot(Mth.approachDegrees(this.getYHeadRot(), this.getYRot(), 10.0F));
        }
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
        } else {
            ServantMagmaLink.tick((ServerLevel) this.level(), this);
        }
        this.tickMultipart();
        this.tickWalking();
        if ((this.getAnimation() == ANIMATION_STOMP && this.getAnimationTick() > 25 && this.getAnimationTick() < 35
                || this.getAnimation() == ANIMATION_ROAR && this.getAnimationTick() > 5 && this.getAnimationTick() < 45)
                && this.screenShakeAmount <= 2.0F) {
            this.screenShakeAmount = 2.0F;
        }
        this.tickLuxtructosaurus();
        this.prevOnGround = this.onGround();
    }

    private void tickLuxtructosaurus() {
        if (this.level().isClientSide) {
            if (!this.isAlive()) {
                return;
            }
            Player rider = AlexsCaves.PROXY.getClientSidePlayer();
            if (rider != null && rider.isPassengerOfSameVehicle(this)
                    && AlexsCaves.PROXY.isKeyDown(2) && this.getMeterAmount() >= 1.0F) {
                AlexsCaves.sendMSGToServer(new MountedEntityKeyMessage(this.getId(), rider.getId(), 2));
            }
            if (this.isEnraged() && this.random.nextInt(8) == 0) {
                this.level().addParticle(AcParticles.LUXTRUCTOSAURUS_SERVANT_SPIT.get(),
                        this.getX(), this.getY() + 0.5, this.getZ(), this.getId(), 0.0, 0.0);
            }
            if (this.isEnraged() && (this.getAnimation() == ANIMATION_RIGHT_WHIP || this.getAnimation() == ANIMATION_LEFT_WHIP)) {
                float tailPitch = this.tailPart1.calculateAnimationAngle(1.0F, true)
                        + this.tailPart2.calculateAnimationAngle(1.0F, true)
                        + this.tailPart3.calculateAnimationAngle(1.0F, true);
                float tailYaw = this.yBodyRot + this.tailPart1.calculateAnimationAngle(1.0F, false)
                        + this.tailPart2.calculateAnimationAngle(1.0F, false)
                        + this.tailPart3.calculateAnimationAngle(1.0F, false);
                Vec3 tailOffset = this.rotateOffsetVec(new Vec3(
                        (this.random.nextFloat() - 0.5F) * 0.1F,
                        0.5F + (this.random.nextFloat() - 0.5F) * 0.2F,
                        -2.0F + (this.random.nextFloat() - 0.5F) * 2.0F), tailPitch, tailYaw);
                Vec3 tailCenter = this.tailPart3.centeredPosition().add(tailOffset);
                this.level().addParticle(ACParticleRegistry.TEPHRA_FLAME.get(), tailCenter.x, tailCenter.y, tailCenter.z,
                        (this.random.nextFloat() - 0.5F) * 0.1F,
                        this.random.nextFloat() * 0.1F,
                        (this.random.nextFloat() - 0.5F) * 0.1F);
            }
            if (this.getAnimation() == ANIMATION_SPEW_FLAMES && this.getAnimationTick() > 10 && this.getAnimationTick() < 70) {
                Vec3 headPos = this.headPart.centeredPosition().add(
                        (this.random.nextFloat() - 0.5F) * 0.1F,
                        (this.random.nextFloat() - 0.5F) * 0.1F,
                        (this.random.nextFloat() - 0.5F) * 0.1F);
                float flameRot = this.yBodyRot + (this.neckPart1.calculateAnimationAngle(1.0F, false)
                        + this.neckPart2.calculateAnimationAngle(1.0F, false)
                        + this.neckPart3.calculateAnimationAngle(1.0F, false)) / 3.0F;
                for (int i = -3; i <= 3; ++i) {
                    Vec3 flameDelta = this.rotateOffsetVec(new Vec3(0.0, this.random.nextFloat() * 0.2F - 0.1F,
                            this.random.nextFloat() * 0.5F + 0.5F), (this.random.nextFloat() - 0.5F) * 5.0F, 180.0F + flameRot + i * 10);
                    this.level().addParticle(ACParticleRegistry.TEPHRA_FLAME.get(), headPos.x, headPos.y, headPos.z,
                            flameDelta.x, flameDelta.y, flameDelta.z);
                }
            }
            if (this.getAnimation() == ANIMATION_JUMP && this.getAnimationTick() > 25 && this.onGround() && this.screenShakeAmount < 3.0F) {
                this.screenShakeAmount = 3.0F;
            }
            return;
        }
        if (this.isInWater() || this.horizontalCollision) {
            this.solidifyWater();
        }
        if (this.getAnimation() == ANIMATION_JUMP) {
            if (this.getAnimationTick() >= 15 && this.getAnimationTick() <= 25 && this.jumpTarget != null) {
                Vec3 movement = this.getDeltaMovement();
                Vec3 toward = new Vec3(this.jumpTarget.x - this.getX(), 0.0, this.jumpTarget.z - this.getZ());
                if (toward.length() > 250.0) {
                    toward = movement.normalize().scale(250.0);
                }
                if (toward.lengthSqr() > 1.0E-7) {
                    toward = toward.scale(0.155F).add(movement.scale(0.2));
                }
                this.setDeltaMovement(toward.x,
                        (10 - Math.min(this.getAnimationTick() - 10, 10)) * 0.2F + toward.length() * 0.3F,
                        toward.z);
            } else {
                if (this.onGround() && !this.prevOnGround) {
                    this.hurtEntitiesAround(this.position(), 10.0F,
                            (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.5F, 2.0F, false, false);
                    this.explodeOnLanding();
                }
                this.setDeltaMovement(this.getDeltaMovement().subtract(0.0, 0.2, 0.0));
            }
        }
        if (this.getAnimation() == ANIMATION_STOMP && this.getAnimationTick() == 30 && this.postStopTicks <= 0) {
            this.postStopTicks = this.isEnraged() ? 15 : 50;
            this.playSound(ACSoundRegistry.LUXTRUCTOSAURUS_STOMP.get(), 3.0F, 1.0F);
            this.hurtEntitiesAround(this.position(), 10.0F,
                    (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 1.2F, 2.0F, false, false);
        }
        if (this.getAnimation() == ANIMATION_RIGHT_KICK && this.getAnimationTick() == 8) {
            Vec3 armPos = this.position().add(this.rotateOffsetVec(new Vec3(-2.0, 0.0, 2.5), 0.0F, this.yBodyRot));
            this.hurtEntitiesAround(armPos, 5.0F,
                    (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.8F, 2.0F, false, false);
        }
        if (this.getAnimation() == ANIMATION_LEFT_KICK && this.getAnimationTick() == 8) {
            Vec3 armPos = this.position().add(this.rotateOffsetVec(new Vec3(2.0, 0.0, 2.5), 0.0F, this.yBodyRot));
            this.hurtEntitiesAround(armPos, 5.0F,
                    (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.8F, 2.0F, false, false);
        }
        if ((this.getAnimation() == ANIMATION_LEFT_WHIP || this.getAnimation() == ANIMATION_RIGHT_WHIP)
                && this.getAnimationTick() > 20 && this.getAnimationTick() < 30) {
            this.hurtEntitiesAround(this.tailPart2.position(), 12.0F,
                    (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE), 2.0F, this.isEnraged(), true);
        }
        if (this.getAnimation() == ANIMATION_SPEW_FLAMES && this.getAnimationTick() > 10 && this.getAnimationTick() < 70) {
            this.burnWithBreath(13.0F);
        }
        if (this.postStopTicks > 0) {
            --this.postStopTicks;
            if (this.screenShakeAmount < 3.0F) {
                this.screenShakeAmount = 3.0F;
            }
        }
        if (this.tickCount % 60 == 0 && this.getAnimation() != ANIMATION_ROAR && this.isAlive()) {
            this.playSound(ACSoundRegistry.LUXTRUCTOSAURUS_SNORT.get(), this.getSoundVolume(), this.getVoicePitch());
            Vec3 headCenter = this.headPart.centeredPosition();
            Vec3 nostrilRightDelta = this.rotateOffsetVec(new Vec3(-0.25, 0.5, 0.75), this.getXRot(), this.getYHeadRot());
            Vec3 nostrilLeftDelta = this.rotateOffsetVec(new Vec3(0.25, 0.5, 0.75), this.getXRot(), this.getYHeadRot());
            Vec3 nostrilRight = headCenter.add(nostrilRightDelta);
            Vec3 nostrilLeft = headCenter.add(nostrilLeftDelta);
            nostrilRightDelta = nostrilRightDelta.scale(0.1F);
            nostrilLeftDelta = nostrilLeftDelta.scale(0.1F);
            ParticleOptions type = ACParticleRegistry.TEPHRA_SMALL.get();
            this.level().addParticle(type, nostrilRight.x, nostrilRight.y, nostrilRight.z,
                    nostrilRightDelta.x, nostrilRightDelta.y, nostrilRightDelta.z);
            this.level().addParticle(type, nostrilLeft.x, nostrilLeft.y, nostrilLeft.z,
                    nostrilLeftDelta.x, nostrilLeftDelta.y, nostrilLeftDelta.z);
        }
        if (this.getAnimation() == ANIMATION_ROAR && this.getAnimationTick() > 10 && this.getAnimationTick() < 50) {
            this.scareMobs();
        }
        if (this.getAnimation() == ANIMATION_ROAR && this.getAnimationTick() == 2 && this.isAlive()) {
            this.playSound(ACSoundRegistry.LUXTRUCTOSAURUS_ROAR.get(), 5.0F, this.getVoicePitch());
        }
        if (this.getAnimation() == ANIMATION_ROAR && this.getAnimationTick() >= 10 && this.getAnimationTick() < 55
                && this.getAnimationTick() % 5 == 0 && this.isAlive() && MobsConfig.LuxtructosaurusServantTephra.get()) {
            this.summonTephra();
        }
        if (this.getAnimation() == ANIMATION_SPEW_FLAMES && this.getAnimationTick() == 10) {
            this.playSound(ACSoundRegistry.LUXTRUCTOSAURUS_BREATH.get(), 5.0F, this.getVoicePitch());
        }
    }

    private void solidifyWater() {
        if (!MobsConfig.LuxtructosaurusServantWaterToStone.get()
                || !this.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            return;
        }
        AABB aabb = this.getBoundingBox().inflate(0.2);
        for (BlockPos blockpos : BlockPos.betweenClosed(
                Mth.floor(aabb.minX - 1.0), Mth.floor(aabb.minY - 1.0), Mth.floor(aabb.minZ - 1.0),
                Mth.ceil(aabb.maxX + 1.0), Mth.ceil(aabb.maxY + 2.0), Mth.ceil(aabb.maxZ + 1.0))) {
            if (!this.level().getFluidState(blockpos).is(FluidTags.WATER)) {
                continue;
            }
            this.level().setBlock(blockpos, ForgeEventFactory.fireFluidPlaceBlockEvent(this.level(), blockpos, blockpos,
                    Blocks.STONE.defaultBlockState()), 3);
            this.level().levelEvent(1501, blockpos, 0);
        }
    }

    private void summonTephra() {
        BlockPos spawnAt = this.blockPosition().offset(this.random.nextInt(20) - 10, 2, this.random.nextInt(20) - 10);
        while (spawnAt.getY() < Math.min(this.level().getMaxBuildHeight(), this.getBlockY() + 100)
                && !this.level().getBlockState(spawnAt).isSolid()) {
            spawnAt = spawnAt.above();
        }
        spawnAt = spawnAt.below();
        ServantTephraEntity tephra = new ServantTephraEntity(this.level(), this);
        tephra.setPos(spawnAt.getCenter());
        tephra.setMaxScale(1.0F + 2.0F * this.level().random.nextFloat());
        tephra.setFireSeconds(0);
        tephra.setFireRadius(0.0F);
        Vec3 targetVec = new Vec3(this.level().random.nextFloat() - 0.5F, -1.0, this.level().random.nextFloat() - 0.5F)
                .normalize().scale(this.level().random.nextInt(20) + 20);
        tephra.shoot(targetVec.x, targetVec.y, targetVec.z, 5.0F + this.level().random.nextFloat() * 2.0F,
                1.0F + this.level().random.nextFloat() * 0.5F);
        this.level().addFreshEntity(tephra);
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

    private void onStep() {
        if (this.screenShakeAmount <= 1.0F) {
            this.playSound(ACSoundRegistry.LUXTRUCTOSAURUS_STEP.get(), 4.0F, 1.0F);
            CameraShake.cameraShake(this.level(), this.position(), 20.0F, 0.03F, 0, 20);
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
                    : Mth.approach(this.walkAnimSpeed, 1.0F, 0.1F);
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
                || this.getAnimation() == ANIMATION_STOMP || this.getAnimation() == ANIMATION_JUMP;
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

    public boolean isFakeEntity() {
        return this.firstTick;
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
        if (this.getAnimation() == ANIMATION_ROAR && this.getAnimationTick() < 50) {
            return -140.0F;
        }
        if (this.getAnimation() == ANIMATION_EPIC_DEATH && this.getAnimationTick() < 110) {
            return -140.0F;
        }
        if (this.getAnimation() == ANIMATION_STOMP && this.getAnimationTick() <= 30) {
            return 30.0F;
        }
        if (this.getAnimation() == ANIMATION_SPEW_FLAMES && this.getAnimationTick() < 70) {
            return 60.0F + (float) (Math.sin(this.getAnimationTick() * 0.4F) * 10.0);
        }
        return -30.0F;
    }

    public float getTargetNeckYRot() {
        if (this.getAnimation() == ANIMATION_ROAR && this.getAnimationTick() < 50) {
            return (float) (Math.sin(this.getAnimationTick() * 0.2F) * 40.0);
        }
        if (this.getAnimation() == ANIMATION_EPIC_DEATH && this.getAnimationTick() < 110) {
            return (float) (Math.sin(this.getAnimationTick() * 0.1F) * 20.0);
        }
        if (this.getAnimation() == ANIMATION_SPEW_FLAMES && this.getAnimationTick() < 70) {
            return (float) (Math.sin(this.getAnimationTick() * 0.15F) * 40.0);
        }
        float buffered = this.getYawFromBuffer(10, 1.0F) - this.yBodyRot;
        return this.getYHeadRot() - this.yBodyRot + buffered;
    }

    private float getNeckRotateSpeed() {
        if (this.getAnimation() == ANIMATION_ROAR && this.getAnimationTick() < 50) {
            return 30.0F;
        }
        if (this.getAnimation() == ANIMATION_SPEW_FLAMES && this.getAnimationTick() < 70) {
            return 40.0F;
        }
        if (this.getAnimation() == ANIMATION_LEFT_WHIP || this.getAnimation() == ANIMATION_RIGHT_WHIP) {
            return 30.0F;
        }
        return 10.0F;
    }

    public float getTargetTailXRot() {
        if (this.getAnimation() == ANIMATION_ROAR && this.getAnimationTick() < 50) {
            return -20.0F;
        }
        if (this.getAnimation() == ANIMATION_EPIC_DEATH && this.getAnimationTick() < 110) {
            return -20.0F;
        }
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
        return 3;
    }

    private void explodeOnLanding() {
        if (!MobsConfig.LuxtructosaurusServantJumpExplosion.get()) {
            return;
        }
        float radius = this.isEnraged() ? 3.0F : 2.0F;
        TephraExplosion explosion = new TephraExplosion(this.level(), this,
                this.getX(), this.getY() + 0.5, this.getZ(), radius, Explosion.BlockInteraction.KEEP);
        explosion.explode();
        explosion.finalizeExplosion(true);
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

    private boolean canBurnMoveThrough(BlockPos.MutableBlockPos blockPos) {
        if (blockPos.getY() <= this.level().getMinBuildHeight()) {
            return false;
        }
        BlockState state = this.level().getBlockState(blockPos);
        return !state.isSolid() || state.is(BlockTags.LEAVES) || state.is(BlockTags.LOGS)
                || !state.isCollisionShapeFullBlock(this.level(), blockPos);
    }

    private void burnWithBreath(float maxDistance) {
        float burnWidth = 1.0F;
        Vec3 headPos = this.headPart.centeredPosition();
        float burnAngle = this.yBodyRot + this.neckYRot;
        boolean canIgnite = MobsConfig.LuxtructosaurusServantFire.get()
                && this.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
        for (float distanceBurned = 0.0F; distanceBurned < maxDistance; distanceBurned += burnWidth) {
            burnWidth += 1.0F;
            Vec3 burnPos = headPos.add(this.rotateOffsetVec(new Vec3(0.0, 0.0, distanceBurned), 0.0F, burnAngle));
            if (canIgnite && this.random.nextFloat() < 0.5F * (1.0F - (maxDistance - distanceBurned) / maxDistance)) {
                BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
                pos.set(burnPos.x + (this.random.nextFloat() - 0.5F) * 2.0F * distanceBurned,
                        burnPos.y,
                        burnPos.z + (this.random.nextFloat() - 0.5F) * 2.0F * distanceBurned);
                while (this.canBurnMoveThrough(pos)) {
                    pos.move(0, -1, 0);
                }
                pos.move(0, 1, 0);
                if (this.level().getBlockState(pos).canBeReplaced()) {
                    this.level().setBlockAndUpdate(pos, Blocks.FIRE.defaultBlockState());
                }
            }
            this.hurtEntitiesAround(burnPos, burnWidth, 3.0F, 0.3F, true, false);
        }
    }

    private void scareMobs() {
        if (this.tickCount - this.lastScareTimestamp <= 5) {
            return;
        }
        this.lastScareTimestamp = this.tickCount;
        List<LivingEntity> nearby = this.level().getEntitiesOfClass(LivingEntity.class,
                this.getBoundingBox().inflate(64.0, 20.0, 64.0));
        for (LivingEntity e : nearby) {
            if (e.getType().is(ACTagRegistry.RESISTS_TREMORSAURUS_ROAR) || this.isAlliedTo(e)) {
                continue;
            }
            if (e instanceof PathfinderMob mob && (!(mob instanceof TamableAnimal tamable) || !tamable.isInSittingPose())) {
                mob.setTarget(null);
                mob.setLastHurtByMob(null);
                if (mob.onGround()) {
                    Vec3 randomShake = new Vec3(this.random.nextFloat() - 0.5F, 0.0, this.random.nextFloat() - 0.5F).scale(0.1F);
                    mob.setDeltaMovement(mob.getDeltaMovement().multiply(0.7F, 1.0, 0.7F).add(randomShake));
                }
                mob.getNavigation().stop();
                if (mob.getNavigation().isDone()) {
                    Vec3 away = LandRandomPos.getPosAway(mob, 30, 7, this.position());
                    if (away != null) {
                        mob.getNavigation().moveTo(away.x, away.y, away.z, 2.0);
                    }
                }
            }
            if (this.getTrueOwner() == null) {
                continue;
            }
            e.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 0, true, true));
        }
    }

    @Override
    public boolean isImmobile() {
        return this.getAnimation() == ANIMATION_ROAR && !this.isRiddenByPlayer() || super.isImmobile();
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
        if (!this.isAlive()) {
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

    public float getEnragedProgress(float partialTicks) {
        return (this.prevEnragedProgress + (this.enragedProgress - this.prevEnragedProgress) * partialTicks) * 0.05F;
    }

    public boolean isEnraged() {
        return this.entityData.get(ENRAGED);
    }

    public void setEnraged(boolean enraged) {
        this.entityData.set(ENRAGED, enraged);
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
        return new Animation[]{ANIMATION_SPEAK, ANIMATION_ROAR, ANIMATION_EPIC_DEATH, ANIMATION_STOMP,
                ANIMATION_SPEW_FLAMES, ANIMATION_JUMP, ANIMATION_LEFT_KICK, ANIMATION_RIGHT_KICK,
                ANIMATION_LEFT_WHIP, ANIMATION_RIGHT_WHIP};
    }

    @Override
    public boolean hurt(DamageSource damageSource, float damageAmount) {
        if (damageSource.is(DamageTypeTags.IS_PROJECTILE)) {
            damageAmount *= this.getProjectileDamageReduction();
        }
        if (this.reducedDamageTicks > 0) {
            damageAmount *= 0.35F;
        }
        if (damageSource.getDirectEntity() instanceof DinosaurEntity
                && !(damageSource.getDirectEntity() instanceof TremorzillaEntity)
                && !(damageSource.getDirectEntity() instanceof TremorzillaServant)) {
            damageAmount *= 0.65F;
        }
        if (damageSource.getEntity() instanceof AbstractGolem) {
            damageAmount *= 0.5F;
        }
        if (damageSource.getEntity() instanceof Warden) {
            damageAmount *= 0.25F;
        }
        boolean hurt = super.hurt(damageSource, damageAmount);
        if (hurt && this.reducedDamageTicks == 0) {
            this.reducedDamageTicks = 10;
        }
        return hurt;
    }

    @Override
    protected void actuallyHurt(DamageSource source, float amount) {
        if (!source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            amount = Math.min(amount, AttributesConfig.LuxtructosaurusServantDamageCap.get().floatValue());
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
        return 0.55F;
    }

    public int getMaxFallDistance() {
        return super.getMaxFallDistance() + 10;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    @Override
    protected void tickDeath() {
        ++this.deathTime;
        this.setAnimation(ANIMATION_EPIC_DEATH);
        this.setEnraged(true);
        this.screenShakeAmount = 0.0F;
        this.setXRot(0.0F);
        this.setYHeadRot(this.getYRot());
        if (this.getAnimation() == ANIMATION_EPIC_DEATH) {
            if (this.getAnimationTick() >= 100 && this.getAnimationTick() <= 110 && this.level().isClientSide) {
                for (int i = 0; i < 50; ++i) {
                    this.level().addAlwaysVisibleParticle(AcParticles.LUXTRUCTOSAURUS_SERVANT_ASH.get(), true,
                            this.getX(), this.getY(), this.getZ(), this.getId(), 0.0, 0.0);
                }
            }
            if (this.getAnimationTick() > 110 && !this.level().isClientSide && !this.isRemoved()) {
                if (this.getTrueOwner() != null && MobsConfig.LuxtructosaurusServantExtinctionCatalyst.get()) {
                    ItemStack itemStack = new ItemStack(AcItems.EXTINCTION_CATALYST.get());
                    ReviveServantItem.setOwnerName(this.getTrueOwner(), itemStack);
                    ReviveServantItem.setSummon(this, itemStack);
                    Vec3 headPos = this.headPart.centeredPosition();
                    FlyingItem flyingItem = new FlyingItem(ModEntityType.FLYING_ITEM.get(), this.level(),
                            headPos.x, headPos.y, headPos.z);
                    flyingItem.setOwner(this.getTrueOwner());
                    flyingItem.setItem(itemStack);
                    flyingItem.setParticle(ParticleTypes.ASH);
                    flyingItem.setSecondsCool(ItemConfig.ReviveSecondsCool.get());
                    this.level().addFreshEntity(flyingItem);
                }
                this.level().broadcastEntityEvent(this, (byte) 60);
                this.remove(Entity.RemovalReason.KILLED);
            }
        }
    }

    @Override
    public void remove(Entity.RemovalReason removalReason) {
        if (this.level() instanceof ServerLevel serverLevel) {
            ServantMagmaLink.release(serverLevel, this);
        }
        super.remove(removalReason);
        if (this.allParts != null) {
            for (PartEntity<?> part : this.allParts) {
                part.remove(Entity.RemovalReason.KILLED);
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Enraged", this.isEnraged());
        tag.putInt("OutOfCombatTicks", this.outOfCombatTicks);
        tag.putInt("EnrageCooldown", this.enrageCooldown);
        tag.putInt("RoarFallbackTicks", this.roarFallbackTicks);
        tag.putBoolean("PendingRoar", this.pendingRoar);
        tag.putBoolean("FollowingStanceEnforced", this.followingStanceEnforced);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setEnraged(tag.getBoolean("Enraged"));
        this.outOfCombatTicks = tag.getInt("OutOfCombatTicks");
        this.enrageCooldown = tag.getInt("EnrageCooldown");
        this.roarFallbackTicks = tag.getInt("RoarFallbackTicks");
        this.pendingRoar = tag.getBoolean("PendingRoar");
        this.followingStanceEnforced = tag.getBoolean("FollowingStanceEnforced");
    }

    protected float getSoundVolume() {
        return super.getSoundVolume() + 2.0F;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ACSoundRegistry.LUXTRUCTOSAURUS_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return ACSoundRegistry.LUXTRUCTOSAURUS_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ACSoundRegistry.LUXTRUCTOSAURUS_DEATH.get();
    }

    private class LuxtructosaurusTargetGoal extends SummonTargetGoal {

        private LuxtructosaurusTargetGoal(Mob mob) {
            super(mob);
        }

        @Override
        protected double getFollowDistance() {
            return AttributesConfig.LuxtructosaurusServantTargetRange.get();
        }
    }

    private class LuxtructosaurusFollowGoal extends Summoned.FollowOwnerGoal<LuxtructosaurusServant> {

        private LuxtructosaurusFollowGoal(LuxtructosaurusServant servant, double speed,
                                          float startDistance, float stopDistance) {
            super(servant, speed, startDistance, stopDistance);
        }

        @Override
        public boolean canUse() {
            if (this.summonedEntity.isRiddenByPlayer() || this.summonedEntity.isPassenger()) {
                return false;
            }
            return super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            if (this.summonedEntity.isRiddenByPlayer() || this.summonedEntity.isPassenger()) {
                return false;
            }
            return super.canContinueToUse();
        }
    }

    private class SauropodMoveHelper extends MoveControl {
        public SauropodMoveHelper() {
            super(LuxtructosaurusServant.this);
        }

        @Override
        public void tick() {
            if (this.operation == Operation.WAIT) {
                LuxtructosaurusServant.this.entityData.set(WALKING, false);
                this.speedModifier = 0.0D;
            } else {
                LuxtructosaurusServant.this.entityData.set(WALKING, true);
                float f = LuxtructosaurusServant.this.getLegSlamAmount(2.0F, 0.66F);
                boolean facingTarget = true;
                if (this.operation == Operation.MOVE_TO) {
                    double d0 = this.wantedX - this.mob.getX();
                    double d1 = this.wantedZ - this.mob.getZ();
                    float moveToRot = (float) (Mth.atan2(d1, d0) * 57.2957763671875) - 90.0F;
                    facingTarget = Mth.degreesDifferenceAbs(LuxtructosaurusServant.this.yBodyRot, moveToRot) < 15.0F;
                }
                if (LuxtructosaurusServant.this.getAnimation() == ANIMATION_LEFT_WHIP
                        || LuxtructosaurusServant.this.getAnimation() == ANIMATION_RIGHT_WHIP) {
                    facingTarget = true;
                }
                float threshold = 0.65F;
                if (f >= threshold && facingTarget) {
                    this.speedModifier = (f - threshold) / (1.0F - threshold);
                } else {
                    this.speedModifier = 0.0D;
                }
            }
            super.tick();
        }
    }
}
