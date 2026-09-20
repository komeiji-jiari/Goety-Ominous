package com.qiuyue.goetyominous.common.entities.ally.of;

import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.utils.MobUtil;
import com.qiuyue.goetyominous.common.entities.ally.of.goals.RamblerServantFlailGoal;
import com.qiuyue.goetyominous.common.entities.ally.of.goals.RamblerServantJabGoal;
import com.qiuyue.goetyominous.config.AttributesConfig;
import com.qiuyue.goetyominous.config.MobsConfig;
import com.unusualmodding.opposing_force.entity.ai.navigation.SmoothGroundPathNavigation;
import com.unusualmodding.opposing_force.entity.utils.AttackState;
import com.unusualmodding.opposing_force.entity.utils.OPPoses;
import com.unusualmodding.opposing_force.registry.OPItems;
import com.unusualmodding.opposing_force.registry.OPSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.random.Weight;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class RamblerServant extends Summoned implements AttackState {
    private static final EntityDataAccessor<Boolean> FLAILING;
    private static final EntityDataAccessor<Boolean> ROLLING;
    public static final EntityDataAccessor<Integer> MIDDLE_SKULL;
    public static final EntityDataAccessor<Integer> LEFT_SKULL;
    public static final EntityDataAccessor<Integer> RIGHT_SKULL;
    public static final EntityDataAccessor<Integer> ATTACK_STATE;
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState jab1AnimationState = new AnimationState();
    public final AnimationState jab2AnimationState = new AnimationState();
    public final AnimationState jab3AnimationState = new AnimationState();
    public final AnimationState jab4AnimationState = new AnimationState();
    public final AnimationState jabRushAnimationState = new AnimationState();
    public final AnimationState flailStartAnimationState = new AnimationState();
    public final AnimationState flailAnimationState = new AnimationState();
    public final AnimationState flailEndAnimationState = new AnimationState();
    public final AnimationState recoverAnimationState = new AnimationState();
    private int startFlailingTicks;
    private int stopFlailingTicks;
    private int recoveringTicks;
    private int jabTicks;
    private int jabRushTicks;
    public int flailCooldown = 200;
    private int selectedSkullSlot;
    private boolean middleSkullCustom;
    private boolean leftSkullCustom;
    private boolean rightSkullCustom;

    public RamblerServant(EntityType<? extends Owned> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.RamblerServantHealth.get())
                .add(Attributes.MOVEMENT_SPEED, AttributesConfig.RamblerServantMovementSpeed.get())
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.RamblerServantAttackDamage.get())
                .add(Attributes.ATTACK_KNOCKBACK, AttributesConfig.RamblerServantAttackKnockback.get());
    }

    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, false, false,
                (target) -> target instanceof Enemy && !MobUtil.areAllies(this, target)));
        this.goalSelector.addGoal(1, new RamblerServantFlailGoal(this));
        this.goalSelector.addGoal(2, new RamblerServantJabGoal(this));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(4, new FleeSunGoal(this, 1.2));
        this.goalSelector.addGoal(5, new AvoidEntityGoal(this, Wolf.class, 6.0F, 1.2, 1.2));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
    }

    protected @NotNull PathNavigation createNavigation(@NotNull Level level) {
        return new SmoothGroundPathNavigation(this, level);
    }

    public @NotNull MobType getMobType() {
        return MobType.UNDEAD;
    }

    @Override
    protected float getStandingEyeHeight(@NotNull Pose pose, EntityDimensions dimensions) {
        return dimensions.height * 0.7F;
    }

    public float getStepHeight() {
        return this.isFlailing() ? 1.0F : 0.6F;
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FLAILING, false);
        this.entityData.define(ROLLING, false);
        this.entityData.define(ATTACK_STATE, 0);
        this.entityData.define(MIDDLE_SKULL, 0);
        this.entityData.define(LEFT_SKULL, 0);
        this.entityData.define(RIGHT_SKULL, 0);
    }

    public void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("MiddleSkull", this.getMiddleSkull());
        compoundTag.putInt("LeftSkull", this.getLeftSkull());
        compoundTag.putInt("RightSkull", this.getRightSkull());
        compoundTag.putInt("SelectedSkullSlot", this.selectedSkullSlot);
        compoundTag.putBoolean("MiddleSkullCustom", this.middleSkullCustom);
        compoundTag.putBoolean("LeftSkullCustom", this.leftSkullCustom);
        compoundTag.putBoolean("RightSkullCustom", this.rightSkullCustom);
    }

    public void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.setMiddleSkull(compoundTag.getInt("MiddleSkull"));
        this.setLeftSkull(compoundTag.getInt("LeftSkull"));
        this.setRightSkull(compoundTag.getInt("RightSkull"));
        this.selectedSkullSlot = compoundTag.getInt("SelectedSkullSlot");
        this.middleSkullCustom = compoundTag.getBoolean("MiddleSkullCustom");
        this.leftSkullCustom = compoundTag.getBoolean("LeftSkullCustom");
        this.rightSkullCustom = compoundTag.getBoolean("RightSkullCustom");
    }

    @Override
    public int getSummonLimit(LivingEntity owner) {
        return MobsConfig.RamblerServantLimit.get();
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        if (this.getTrueOwner() == player) {

            RamblerSkulls variant = getSkullVariant(held);
            if (variant != null) {
                if (!player.getAbilities().instabuild) {
                    held.shrink(1);
                }

                if (isSlotCustom(this.selectedSkullSlot)) {
                    dropCurrentSkullAt(this.selectedSkullSlot);
                }
                setSkullAt(this.selectedSkullSlot, variant.getSkull());
                markSlotCustom(this.selectedSkullSlot, true);
                this.playSound(SoundEvents.BONE_MEAL_USE, 1.0F, 1.0F);
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }

            if (held.isEmpty()) {
                if (player.isShiftKeyDown()) {
                    removeSkullAt(this.selectedSkullSlot);
                    return InteractionResult.sidedSuccess(this.level().isClientSide);
                } else {
                    this.selectedSkullSlot = (this.selectedSkullSlot + 1) % 3;
                    player.displayClientMessage(
                            Component.translatable("info.goetyominous.rambler.slot",
                                    Component.translatable("info.goetyominous.rambler.slot." + slotName(this.selectedSkullSlot))), true);
                    return InteractionResult.sidedSuccess(this.level().isClientSide);
                }
            }

            if (this.getHealth() < this.getMaxHealth()) {
                float heal = 0.0F;
                if (held.is(Items.BONE_MEAL)) {
                    heal = 1.0F;
                } else if (held.is(Items.BONE)) {
                    heal = 3.0F;
                } else if (held.is(Items.BONE_BLOCK)) {
                    heal = 9.0F;
                }
                if (heal > 0.0F) {
                    if (!player.getAbilities().instabuild) {
                        held.shrink(1);
                    }
                    this.heal(heal);
                    this.playSound(SoundEvents.BONE_MEAL_USE, 1.0F, 1.0F);
                    if (this.level() instanceof ServerLevel serverLevel) {
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
            }
        }
        return super.mobInteract(player, hand);
    }

    private static RamblerSkulls getSkullVariant(ItemStack stack) {
        for (RamblerSkulls skull : RamblerSkulls.values()) {
            if (skull.getSkullItem() != null && stack.is(skull.getSkullItem())) {
                return skull;
            }
        }
        return null;
    }

    private void setSkullAt(int slot, int skullId) {
        if (slot == 0) this.setMiddleSkull(skullId);
        else if (slot == 1) this.setLeftSkull(skullId);
        else this.setRightSkull(skullId);
    }

    private void markSlotCustom(int slot, boolean custom) {
        if (slot == 0) this.middleSkullCustom = custom;
        else if (slot == 1) this.leftSkullCustom = custom;
        else this.rightSkullCustom = custom;
    }

    private boolean isSlotCustom(int slot) {
        if (slot == 0) return this.middleSkullCustom;
        if (slot == 1) return this.leftSkullCustom;
        return this.rightSkullCustom;
    }

    private void dropSkullItem(int skullId) {
        if (skullId != 0) {
            RamblerSkulls variant = RamblerSkulls.getVariantId(skullId);
            if (variant.getSkullItem() != null) {
                this.spawnAtLocation(new ItemStack(variant.getSkullItem()));
            }
        }
    }

    private void dropCurrentSkullAt(int slot) {
        int skullId;
        if (slot == 0) skullId = this.getMiddleSkull();
        else if (slot == 1) skullId = this.getLeftSkull();
        else skullId = this.getRightSkull();
        if (skullId != 0) {
            dropSkullItem(skullId);
        }
    }

    private void removeSkullAt(int slot) {
        if (!isSlotCustom(slot)) {
            return;
        }
        int skullId;
        if (slot == 0) { skullId = this.getMiddleSkull(); this.setMiddleSkull(0); }
        else if (slot == 1) { skullId = this.getLeftSkull(); this.setLeftSkull(0); }
        else { skullId = this.getRightSkull(); this.setRightSkull(0); }
        if (skullId != 0) {
            dropSkullItem(skullId);
        }
        markSlotCustom(slot, false);
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int damage, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, damage, recentlyHit);
        if (this.middleSkullCustom) dropSkullItem(this.getMiddleSkull());
        if (this.leftSkullCustom) dropSkullItem(this.getLeftSkull());
        if (this.rightSkullCustom) dropSkullItem(this.getRightSkull());
    }

    private static String slotName(int slot) {
        if (slot == 0) return "middle";
        if (slot == 1) return "left";
        return "right";
    }

    public int getAttackState() {
        return (Integer)this.entityData.get(ATTACK_STATE);
    }

    public void setAttackState(int attackState) {
        this.entityData.set(ATTACK_STATE, attackState);
    }

    public boolean isFlailing() {
        return (Boolean)this.entityData.get(FLAILING);
    }

    public void setFlailing(boolean flailing) {
        this.entityData.set(FLAILING, flailing);
    }

    public int getMiddleSkull() {
        return (Integer)this.entityData.get(MIDDLE_SKULL);
    }

    public void setMiddleSkull(int skull) {
        this.entityData.set(MIDDLE_SKULL, skull);
    }

    public int getLeftSkull() {
        return (Integer)this.entityData.get(LEFT_SKULL);
    }

    public void setLeftSkull(int skull) {
        this.entityData.set(LEFT_SKULL, skull);
    }

    public int getRightSkull() {
        return (Integer)this.entityData.get(RIGHT_SKULL);
    }

    public void setRightSkull(int skull) {
        this.entityData.set(RIGHT_SKULL, skull);
    }

    public void tick() {
        super.tick();
        if (this.flailCooldown > 0) {
            --this.flailCooldown;
        }

        if (this.level().isClientSide) {
            this.setupAnimationStates();
        }

        if (this.startFlailingTicks > 0) {
            --this.startFlailingTicks;
        }

        if (this.stopFlailingTicks > 0) {
            --this.stopFlailingTicks;
        }

        if (this.recoveringTicks > 0) {
            --this.recoveringTicks;
        }

        if (this.jabTicks > 0) {
            --this.jabTicks;
        }

        if (this.jabRushTicks > 0) {
            --this.jabRushTicks;
        }

        if (this.startFlailingTicks == 0 && this.getPose() == OPPoses.START_FLAILING.get()) {
            this.setPose(OPPoses.FLAILING.get());
        }

        if (this.stopFlailingTicks == 0 && this.getPose() == OPPoses.STOP_FLAILING.get()) {
            this.setPose(OPPoses.RECOVERING.get());
        }

        if (this.recoveringTicks == 0 && this.getPose() == OPPoses.RECOVERING.get()) {
            this.setPose(Pose.STANDING);
        }

        if (this.jabTicks == 0 && this.getPose() == OPPoses.JAB.get()) {
            this.setPose(Pose.STANDING);
        }

        if (this.jabRushTicks == 0 && this.getPose() == OPPoses.JAB_RUSH.get()) {
            this.setPose(Pose.STANDING);
        }

    }

    private void setupAnimationStates() {
        if (this.startFlailingTicks == 0 && this.flailStartAnimationState.isStarted()) {
            this.flailStartAnimationState.stop();
        }

        if (this.stopFlailingTicks == 0 && this.flailEndAnimationState.isStarted()) {
            this.flailEndAnimationState.stop();
        }

        if (this.recoveringTicks == 0 && this.recoverAnimationState.isStarted()) {
            this.recoverAnimationState.stop();
        }

        if (this.jabTicks == 0 && (this.jab1AnimationState.isStarted() || this.jab2AnimationState.isStarted() || this.jab3AnimationState.isStarted() || this.jab4AnimationState.isStarted())) {
            this.jab1AnimationState.stop();
            this.jab2AnimationState.stop();
            this.jab3AnimationState.stop();
            this.jab4AnimationState.stop();
        }

        if (this.jabRushTicks == 0 && this.jabRushAnimationState.isStarted()) {
            this.jabRushAnimationState.stop();
        }

        this.idleAnimationState.animateWhen(this.isAlive(), this.tickCount);
    }

    public void calculateEntityAnimation(boolean flying) {
        float f1 = (float) Mth.length(this.getX() - this.xo, this.getY() - this.yo, this.getZ() - this.zo);
        float f2 = Math.min(f1 * 12.0F, 1.0F);
        this.walkAnimation.update(f2, 0.4F);
    }

    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> entityDataAccessor) {
        if (DATA_POSE.equals(entityDataAccessor)) {
            if (this.getPose() == OPPoses.START_FLAILING.get()) {
                this.startFlailingTicks = 20;
                this.flailStartAnimationState.start(this.tickCount);
            } else if (this.getPose() == OPPoses.FLAILING.get()) {
                this.flailStartAnimationState.stop();
                this.flailAnimationState.start(this.tickCount);
            } else if (this.getPose() == OPPoses.STOP_FLAILING.get()) {
                this.stopFlailingTicks = 20;
                this.flailAnimationState.stop();
                this.flailEndAnimationState.start(this.tickCount);
            } else if (this.getPose() == OPPoses.RECOVERING.get()) {
                this.recoveringTicks = 70;
                this.flailAnimationState.stop();
                this.flailEndAnimationState.stop();
                this.flailStartAnimationState.stop();
                this.recoverAnimationState.start(this.tickCount);
            } else if (this.getPose() == OPPoses.JAB.get()) {
                this.jabTicks = 10;
                if (this.getRandom().nextFloat() < 0.25F) {
                    this.jab1AnimationState.start(this.tickCount);
                } else if (this.getRandom().nextFloat() < 0.5F) {
                    this.jab2AnimationState.start(this.tickCount);
                } else if (this.getRandom().nextFloat() < 0.75F) {
                    this.jab3AnimationState.start(this.tickCount);
                } else {
                    this.jab4AnimationState.start(this.tickCount);
                }
            } else if (this.getPose() == OPPoses.JAB_RUSH.get()) {
                this.jabRushTicks = 20;
                this.jabRushAnimationState.start(this.tickCount);
            } else if (this.getPose() == Pose.STANDING) {
                this.flailAnimationState.stop();
                this.flailStartAnimationState.stop();
                this.flailEndAnimationState.stop();
                this.recoverAnimationState.stop();
                this.jab1AnimationState.stop();
                this.jab2AnimationState.stop();
                this.jab3AnimationState.stop();
                this.jab4AnimationState.stop();
                this.jabRushAnimationState.stop();
            }
        }

        super.onSyncedDataUpdated(entityDataAccessor);
    }

    public boolean hurt(@NotNull DamageSource source, float f) {
        if (this.isFlailing() || source.is(DamageTypeTags.IS_PROJECTILE)) {
            f *= 0.5F;
        }

        return super.hurt(source, f);
    }

    protected boolean isSunSensitive() {
        return true;
    }

    public void aiStep() {
        if (this.isAlive()) {
            boolean flag = this.isSunSensitive() && this.isSunBurnTick();
            if (flag) {
                this.setSecondsOnFire(8);
            }
        }

        super.aiStep();
    }

    @Nullable
    protected SoundEvent getAmbientSound() {
        return (SoundEvent) OPSoundEvents.RAMBLER_IDLE.get();
    }

    protected @NotNull SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return (SoundEvent)OPSoundEvents.RAMBLER_HURT.get();
    }

    protected @NotNull SoundEvent getDeathSound() {
        return (SoundEvent)OPSoundEvents.RAMBLER_DEATH.get();
    }

    protected void playStepSound(@NotNull BlockPos pos, @NotNull BlockState state) {
        this.playSound(SoundEvents.SKELETON_STEP, 0.15F, 0.85F);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        MobSpawnType spawnType, @Nullable SpawnGroupData spawnData,
                                        @Nullable CompoundTag compoundTag) {
        spawnData = super.finalizeSpawn(level, difficulty, spawnType, spawnData, compoundTag);

        RamblerSkulls middle = RamblerSkulls.getRandom(this.getRandom());
        RamblerSkulls left = RamblerSkulls.getRandom(this.getRandom());
        RamblerSkulls right = RamblerSkulls.getRandom(this.getRandom());
        this.setMiddleSkull(middle.getSkull());
        this.setLeftSkull(left.getSkull());
        this.setRightSkull(right.getSkull());

        RandomSource random = level.getRandom();
        if (random.nextInt(100) == 0) {
            RamblerServant rider = com.qiuyue.goetyominous.common.init.of.OfEntityRegistry.RAMBLER_SERVANT.get().create(this.level());
            if (rider != null) {
                rider.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
                rider.finalizeSpawn(level, difficulty, spawnType, null, null);
                if (this.getTrueOwner() != null) {
                    rider.setTrueOwner(this.getTrueOwner());
                }
                rider.startRiding(this);
            }
        } else if (random.nextInt(100) == 1) {
            com.Polarice3.Goety.common.entities.ally.undead.skeleton.SkeletonServant skeleton =
                    com.Polarice3.Goety.common.entities.ModEntityType.SKELETON_SERVANT.get().create(this.level());
            if (skeleton != null) {
                skeleton.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
                skeleton.finalizeSpawn(level, difficulty, spawnType, null, null);
                if (this.getTrueOwner() != null) {
                    skeleton.setTrueOwner(this.getTrueOwner());
                }
                skeleton.startRiding(this);
            }
        }

        return spawnData;
    }

    public double getPassengersRidingOffset() {
        return (double)(this.getBbHeight() * 1.03F);
    }

    public void rideTick() {
        super.rideTick();
        Entity entity = this.getControlledVehicle();
        if (entity instanceof PathfinderMob pathfindermob) {
            this.yBodyRot = pathfindermob.yBodyRot;
        }

    }

    static {
        FLAILING = SynchedEntityData.defineId(RamblerServant.class, EntityDataSerializers.BOOLEAN);
        ROLLING = SynchedEntityData.defineId(RamblerServant.class, EntityDataSerializers.BOOLEAN);
        MIDDLE_SKULL = SynchedEntityData.defineId(RamblerServant.class, EntityDataSerializers.INT);
        LEFT_SKULL = SynchedEntityData.defineId(RamblerServant.class, EntityDataSerializers.INT);
        RIGHT_SKULL = SynchedEntityData.defineId(RamblerServant.class, EntityDataSerializers.INT);
        ATTACK_STATE = SynchedEntityData.defineId(RamblerServant.class, EntityDataSerializers.INT);
    }

    public static enum RamblerSkulls implements StringRepresentable, WeightedEntry {
        SKELETAL(0, "skeletal", 100, (Item) OPItems.SKELETAL_RAMBLER_SKULL.get()),
        ANGRY(1, "angry", 100, (Item)OPItems.ANGRY_RAMBLER_SKULL.get()),
        CLASSIC(2, "classic", 100, (Item)OPItems.CLASSIC_RAMBLER_SKULL.get()),
        EVIL(3, "evil", 100, (Item)OPItems.EVIL_RAMBLER_SKULL.get()),
        GRINNING(4, "grinning", 10, (Item)OPItems.GRINNING_RAMBLER_SKULL.get()),
        SMILING(5, "smiling", 100, (Item)OPItems.SMILING_RAMBLER_SKULL.get()),
        STRANGE(6, "strange", 100, (Item)OPItems.STRANGE_RAMBLER_SKULL.get()),
        MUSICAL(7, "musical", 1, (Item)OPItems.MUSICAL_RAMBLER_SKULL.get()),
        DWARVEN(8, "dwarven", 1, (Item)OPItems.DWARVEN_RAMBLER_SKULL.get()),
        INDOMITABLE(9, "indomitable", 1, (Item)OPItems.INDOMITABLE_RAMBLER_SKULL.get()),
        MAGMATIC(10, "magmatic", 1, (Item)OPItems.MAGMATIC_RAMBLER_SKULL.get()),
        CRUNDLY(11, "crundly", 1, (Item)OPItems.CRUNDLY_RAMBLER_SKULL.get()),
        IMPRISONED(12, "imprisoned", 1, (Item)OPItems.IMPRISONED_RAMBLER_SKULL.get()),
        NOSY(13, "nosy", 1, (Item)OPItems.NOSY_RAMBLER_SKULL.get()),
        LEERING(14, "leering", 1, (Item)OPItems.LEERING_RAMBLER_SKULL.get()),
        VALIANT(15, "valiant", 1, (Item)OPItems.VALIANT_RAMBLER_SKULL.get());

        private final int skull;
        private final String name;
        private final Weight weight;
        private final Item skullItem;

        private RamblerSkulls(int skull, String name, int weight, Item skullItem) {
            this.skull = skull;
            this.name = name;
            this.weight = Weight.of(weight);
            this.skullItem = skullItem;
        }

        public static RamblerSkulls getVariantId(int skulls) {
            RamblerSkulls[] var1 = values();
            int var2 = var1.length;

            for(int var3 = 0; var3 < var2; ++var3) {
                RamblerSkulls skull = var1[var3];
                if (skull.skull == skulls) {
                    return skull;
                }
            }

            return SKELETAL;
        }

        public static RamblerSkulls getRandom(RandomSource random) {
            int weight = 0;
            int skull = 0;
            RamblerSkulls[] var3 = values();
            int var4 = var3.length;

            int var5;
            RamblerSkulls skulls;
            for(var5 = 0; var5 < var4; ++var5) {
                skulls = var3[var5];
                weight += skulls.getWeight().asInt();
            }

            var3 = values();
            var4 = var3.length;

            for(var5 = 0; var5 < var4; ++var5) {
                skulls = var3[var5];
                skull += skulls.getWeight().asInt();
                if (random.nextInt(weight) < skull) {
                    return skulls;
                }
            }

            return SKELETAL;
        }

        public int getSkull() {
            return this.skull;
        }

        public Item getSkullItem() {
            return this.skullItem;
        }

        public @NotNull String getSerializedName() {
            return this.name;
        }

        public @NotNull Weight getWeight() {
            return this.weight;
        }
    }
}
