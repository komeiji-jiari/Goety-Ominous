package com.qiuyue.goetyominous.common.entities.ally.of;

import com.Polarice3.Goety.api.items.magic.IWand;
import com.Polarice3.Goety.common.entities.ally.Summoned;
import com.Polarice3.Goety.common.entities.neutral.Owned;
import com.Polarice3.Goety.utils.ServantUtil;
import com.qiuyue.goetyominous.client.sound.of.FrowzyServantPantingSoundHandler;
import com.qiuyue.goetyominous.common.entities.ally.of.goals.FrowzyServantAttackGoal;
import com.qiuyue.goetyominous.common.entities.ally.of.goals.FrowzyServantLeapGoal;
import com.unusualmodding.opposing_force.entity.Frowzy;
import com.unusualmodding.opposing_force.entity.utils.AttackState;
import com.unusualmodding.opposing_force.entity.utils.OPPoses;
import com.unusualmodding.opposing_force.registry.OPSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import com.qiuyue.goetyominous.config.of.OfConfig;

public class FrowzyServant extends Summoned implements AttackState, VariantHolder<Frowzy.FrowzyVariant> {
    private static final EntityDataAccessor<Integer> ATTACK_STATE;
    private static final EntityDataAccessor<Integer> VARIANT;

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState attack1AnimationState = new AnimationState();
    public final AnimationState attack2AnimationState = new AnimationState();

    public int jumpCooldown = 100 + this.random.nextInt(50);
    private int attackTicks;

    public FrowzyServant(EntityType<? extends Owned> entityType, Level level) {
        super(entityType, level);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_POWDER_SNOW, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_OTHER, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_CAUTIOUS, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.LAVA, 0.0F);
    }

    public static AttributeSupplier.Builder setCustomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, OfConfig.FrowzyServantHealth.get())
                .add(Attributes.FOLLOW_RANGE, 16.0D)
                .add(Attributes.MOVEMENT_SPEED, OfConfig.FrowzyServantMovementSpeed.get())
                .add(Attributes.ATTACK_DAMAGE, OfConfig.FrowzyServantAttackDamage.get());
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new FrowzyServantLeapGoal(this));
        this.goalSelector.addGoal(2, new FrowzyServantAttackGoal(this));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(7, new Summoned.WanderGoal<>(this, 1.0D, 110, 0.001F));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ATTACK_STATE, 0);
        this.entityData.define(VARIANT, Frowzy.FrowzyVariant.BLUE.getId());
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("AttackState", this.getAttackState());
        compoundTag.putInt("Variant", this.getVariant().getId());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.setAttackState(compoundTag.getInt("AttackState"));
        this.setVariant(Frowzy.FrowzyVariant.byId(compoundTag.getInt("Variant")));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.jumpCooldown > 0) {
            --this.jumpCooldown;
        }
        if (this.level().isClientSide) {
            this.setupAnimationStates();
            if (this.isAggressive() && this.isAlive()) {
                FrowzyServantPantingSoundHandler.playFor(this);
            } else {
                FrowzyServantPantingSoundHandler.clearFor(this);
            }
        }
        if (this.attackTicks > 0) {
            --this.attackTicks;
        }
        if (this.attackTicks == 0 && this.getPose() == OPPoses.ATTACKING.get()) {
            this.setPose(Pose.STANDING);
        }
    }

    private void setupAnimationStates() {
        if (this.attackTicks == 0 && (this.attack1AnimationState.isStarted() || this.attack2AnimationState.isStarted())) {
            this.attack1AnimationState.stop();
            this.attack2AnimationState.stop();
        }
        this.idleAnimationState.animateWhen(this.isAlive(), this.tickCount);
    }

    @Override
    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> entityDataAccessor) {
        if (DATA_POSE.equals(entityDataAccessor)) {
            if (this.getPose() == OPPoses.ATTACKING.get()) {
                this.attackTicks = 20;
                if (this.getDeltaMovement().lengthSqr() <= 1.0E-5D) {
                    this.attack1AnimationState.startIfStopped(this.tickCount);
                } else {
                    this.attack2AnimationState.startIfStopped(this.tickCount);
                }
            } else if (this.getPose() == Pose.STANDING) {
                this.attack1AnimationState.stop();
                this.attack2AnimationState.stop();
            }
        }
        super.onSyncedDataUpdated(entityDataAccessor);
    }

    @Override
    public void remove(@NotNull RemovalReason reason) {
        if (this.level().isClientSide) {
            FrowzyServantPantingSoundHandler.clearFor(this);
        }
        super.remove(reason);
    }

    @Override
    protected boolean isSunSensitive() {
        return true;
    }

    @Override
    public void setConfigurableAttributes() {
        if (this.getAttribute(Attributes.MAX_HEALTH) != null) {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(OfConfig.FrowzyServantHealth.get());
        }
        if (this.getAttribute(Attributes.MOVEMENT_SPEED) != null) {
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(OfConfig.FrowzyServantMovementSpeed.get());
        }
        if (this.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
            this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(OfConfig.FrowzyServantAttackDamage.get());
        }
    }

    @Override
    public int getAttackState() {
        return this.entityData.get(ATTACK_STATE);
    }

    @Override
    public void setAttackState(int attackState) {
        this.entityData.set(ATTACK_STATE, attackState);
    }

    @Override
    public void setVariant(Frowzy.FrowzyVariant variant) {
        this.entityData.set(VARIANT, variant.getId());
    }

    @Override
    public @NotNull Frowzy.FrowzyVariant getVariant() {
        return Frowzy.FrowzyVariant.byId(this.entityData.get(VARIANT));
    }

    @Override
    public double getMyRidingOffset() {
        return -0.45D;
    }

    @Override
    protected float getStandingEyeHeight(@NotNull Pose pose, EntityDimensions dimensions) {
        return dimensions.height * 0.9F;
    }

    @Override
    public @NotNull MobType getMobType() {
        return MobType.UNDEAD;
    }

    @Override
    protected void playStepSound(@NotNull BlockPos pos, @NotNull BlockState blockState) {
        this.playSound(SoundEvents.ZOMBIE_STEP, 0.15F, 1.2F);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return OPSoundEvents.FROWZY_IDLE.get();
    }

    @Override
    protected @NotNull SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return OPSoundEvents.FROWZY_HURT.get();
    }

    @Override
    protected @NotNull SoundEvent getDeathSound() {
        return OPSoundEvents.FROWZY_DEATH.get();
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();
        if (this.getTrueOwner() != null && player == this.getTrueOwner()) {
            if (itemstack.is(Items.ROTTEN_FLESH) && this.getHealth() < this.getMaxHealth()) {
                if (!player.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }
                this.playSound(SoundEvents.GENERIC_EAT, 1.0F, 1.0F);
                this.heal(2.0F);
                if (this.level() instanceof ServerLevel serverLevel) {
                    for (int i = 0; i < 7; ++i) {
                        double d0 = this.random.nextGaussian() * 0.02D;
                        double d1 = this.random.nextGaussian() * 0.02D;
                        double d2 = this.random.nextGaussian() * 0.02D;
                        serverLevel.sendParticles(ParticleTypes.HEART, this.getRandomX(1.0D), this.getY() + this.getBbHeight() + 0.3D, this.getRandomZ(1.0D), 0, d0, d1, d2, 0.5F);
                    }
                }
                return InteractionResult.SUCCESS;
            }
            if (!(player.getOffhandItem().getItem() instanceof IWand)
                    && (item instanceof SwordItem || item instanceof AxeItem)) {
                ItemStack itemstack2 = this.getMainHandItem();
                this.playSound(SoundEvents.ARMOR_EQUIP_GENERIC, 1.0F, 1.0F);
                this.setItemSlot(EquipmentSlot.MAINHAND, itemstack.copyWithCount(1));
                this.dropEquipment(EquipmentSlot.MAINHAND, itemstack2);
                this.setGuaranteedDrop(EquipmentSlot.MAINHAND);
                for (int i = 0; i < 7; ++i) {
                    double d0 = this.random.nextGaussian() * 0.02D;
                    double d1 = this.random.nextGaussian() * 0.02D;
                    double d2 = this.random.nextGaussian() * 0.02D;
                    this.level().addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), d0, d1, d2);
                }
                if (!player.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }
                return InteractionResult.SUCCESS;
            }
            if ((item instanceof ArmorItem armorItem && this.canWearArmorSlot(armorItem.getEquipmentSlot()))
                    || (itemstack.is(Items.CARVED_PUMPKIN) && this.canWearArmorSlot(EquipmentSlot.HEAD))) {
                return ServantUtil.equipServantArmor(player, this, itemstack, super.mobInteract(player, hand));
            }
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public boolean canWearArmor() {
        return true;
    }

    @Override
    public boolean canWearArmorSlot(EquipmentSlot slot) {
        return slot == EquipmentSlot.HEAD;
    }

    @Override
    public boolean canHaveWeapon() {
        return true;
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        MobSpawnType spawnType, @Nullable SpawnGroupData spawnData,
                                        @Nullable CompoundTag compoundTag) {
        spawnData = super.finalizeSpawn(level, difficulty, spawnType, spawnData, compoundTag);
        this.setVariant(Frowzy.FrowzyVariant.byId(level.getRandom().nextInt(Frowzy.FrowzyVariant.values().length)));
        return spawnData;
    }

    @Override
    public int getSummonLimit(LivingEntity owner) {
        return OfConfig.FrowzyServantLimit.get();
    }

    static {
        ATTACK_STATE = SynchedEntityData.defineId(FrowzyServant.class, EntityDataSerializers.INT);
        VARIANT = SynchedEntityData.defineId(FrowzyServant.class, EntityDataSerializers.INT);
    }
}
