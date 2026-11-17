package com.qiuyue.goetyominous.common.entities.hostile.piglin;

import com.google.common.collect.ImmutableList;
import com.qiuyue.goetyominous.common.entities.FungusThrowerAnimated;
import com.qiuyue.goetyominous.common.entities.projectile.AcidFungus;
import com.qiuyue.goetyominous.common.init.ModEntityTypes;
import com.qiuyue.goetyominous.common.init.ModSounds;
import com.qiuyue.goetyominous.config.AttributesConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.piglin.PiglinArmPose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nullable;
import java.util.EnumSet;

public abstract class AbstractFungusThrowerHostile extends AbstractPiglin implements FungusThrowerAnimated {
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState walkAnimationState = new AnimationState();
    public final AnimationState throwAnimationState = new AnimationState();

    private static final EntityDataAccessor<Boolean> DATA_THROWING =
            SynchedEntityData.defineId(AbstractFungusThrowerHostile.class, EntityDataSerializers.BOOLEAN);

    private int fleeTime;
    private boolean lastThrowing;
    private float walkWeight;

    protected AbstractFungusThrowerHostile(EntityType<? extends AbstractFungusThrowerHostile> type, Level level) {
        super(type, level);
    }

    @Override
    protected Brain.Provider<AbstractFungusThrowerHostile> brainProvider() {
        return Brain.provider(ImmutableList.of(MemoryModuleType.ATTACK_TARGET), ImmutableList.of());
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        super.setTarget(target);
        this.getBrain().setMemory(MemoryModuleType.ATTACK_TARGET, target);
    }

    @Override
    public void playAmbientSound() {
        SoundEvent sound = this.getAmbientSound();
        if (sound != null) {
            this.playSound(sound, this.getSoundVolume(), this.getVoicePitch());
        }
    }

    @Override
    protected boolean canHunt() {
        return false;
    }

    @Override
    public boolean wantsToPickUp(ItemStack stack) {
        return !stack.is(ItemTags.PIGLIN_LOVED) && super.wantsToPickUp(stack);
    }

    @Override
    public PiglinArmPose getArmPose() {
        return PiglinArmPose.DEFAULT;
    }

    @Override
    protected void customServerAiStep() {
        if (this.isConverting()) {
            ++this.timeInOverworld;
        } else {
            this.timeInOverworld = 0;
        }
        if (this.timeInOverworld > 300 && ForgeEventFactory.canLivingConvert(this, this.getConversionTarget(),
                timer -> this.timeInOverworld = timer)) {
            this.playConvertedSound();
            this.finishConversion((ServerLevel) this.level());
        }
    }

    protected EntityType<? extends AbstractFungusThrowerHostile> getConversionTarget() {
        return ModEntityTypes.ZFUNGUS_THROWER.get();
    }

    @Override
    protected void playConvertedSound() {
        this.playSound(SoundEvents.PIGLIN_CONVERTED_TO_ZOMBIFIED, 1.0F, 1.0F);
    }

    @Override
    protected void finishConversion(ServerLevel serverLevel) {
        ZHostileFungusThrower zombified = new ZHostileFungusThrower(
                ModEntityTypes.ZFUNGUS_THROWER.get(), serverLevel);
        zombified.copyPosition(this);
        zombified.setNoAi(this.isNoAi());
        if (this.hasCustomName()) {
            zombified.setCustomName(this.getCustomName());
            zombified.setCustomNameVisible(this.isCustomNameVisible());
        }
        if (this.isPersistenceRequired()) {
            zombified.setPersistenceRequired();
        }
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = this.getItemBySlot(slot);
            if (stack.isEmpty()) {
                continue;
            }
            if (slot == EquipmentSlot.CHEST) {
                this.setItemSlot(slot, ItemStack.EMPTY);
                this.spawnAtLocation(stack);
            } else {
                zombified.setItemSlot(slot, stack.copyAndClear());
                zombified.setDropChance(slot, this.getEquipmentDropChance(slot));
            }
        }
        zombified.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(this.blockPosition()),
                MobSpawnType.CONVERSION, null, null);
        zombified.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));
        zombified.setHealth(zombified.getMaxHealth());
        zombified.setYHeadRot(this.getYHeadRot());
        zombified.setYBodyRot(this.yBodyRot);
        zombified.yHeadRotO = this.yHeadRotO;
        zombified.yBodyRotO = this.yBodyRotO;
        zombified.yRotO = this.yRotO;
        zombified.xRotO = this.xRotO;
        ForgeEventFactory.onLivingConvert(this, zombified);
        this.discard();
        serverLevel.addFreshEntity(zombified);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.FungusThrowerServantHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.FungusThrowerServantArmor.get())
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.FungusThrowerServantDamage.get());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_THROWING, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, LivingEntity.class, 8.0F, 1.2D, 1.2D,
                target -> target == this.getTarget()));
        this.goalSelector.addGoal(2, new ThrowAcidGoal(this));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.6D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 15.0F, 1.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason,
                                        @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
        if (this.getItemBySlot(EquipmentSlot.CHEST).isEmpty()) {
            this.setItemSlot(EquipmentSlot.CHEST, new ItemStack(this.getPackItem()));
            this.setGuaranteedDrop(EquipmentSlot.CHEST);
        }
        return data;
    }

    protected abstract Item getPackItem();

    @Override
    public void tick() {
        super.tick();
        if (this.fleeTime > 0) {
            --this.fleeTime;
        }
        if (this.level().isClientSide) {
            this.setupAnimationStates();
        }
    }

    protected void setupAnimationStates() {
        boolean throwing = this.isThrowing();
        if (throwing && !this.lastThrowing) {
            this.throwAnimationState.start(this.tickCount);
        } else if (!throwing && this.lastThrowing) {
            this.throwAnimationState.stop();
            this.idleAnimationState.start(this.tickCount);
        }
        this.walkAnimationState.startIfStopped(this.tickCount);
        this.idleAnimationState.startIfStopped(this.tickCount);
        this.walkWeight = approach(this.walkWeight, this.walkAnimation.speed() > 0.01F ? 1.0F : 0.0F, 0.2F);
        this.lastThrowing = throwing;
    }

    private static float approach(float value, float target, float step) {
        return value < target ? Math.min(value + step, target) : Math.max(value - step, target);
    }

    @Override
    public AnimationState getIdleAnimationState() {
        return this.idleAnimationState;
    }

    @Override
    public AnimationState getWalkAnimationState() {
        return this.walkAnimationState;
    }

    @Override
    public AnimationState getThrowAnimationState() {
        return this.throwAnimationState;
    }

    @Override
    public float getWalkWeight() {
        return this.walkWeight;
    }

    public boolean isThrowing() {
        return this.entityData.get(DATA_THROWING);
    }

    public void setThrowing(boolean throwing) {
        this.entityData.set(DATA_THROWING, throwing);
    }

    protected float getFungusDamage() {
        return 4.0F;
    }

    public void throwAcid(@Nullable LivingEntity target) {
        if (this.level().isClientSide) return;

        AcidFungus acid = new AcidFungus(this.level(), this);
        acid.setDamage(this.getFungusDamage());
        double dx = target.getX() - this.getX();
        double dy = target.getY(0.33) - acid.getY();
        double dz = target.getZ() - this.getZ();
        double distance = Math.sqrt(dx * dx + dz * dz);
        acid.shoot(dx, dy + distance * 0.2, dz, 1.0F, 8.0F);
        this.level().addFreshEntity(acid);

        if (!this.isSilent()) {
            int sound = this.random.nextInt(3);
            SoundEvent throwSound;
            if (sound == 0) throwSound = ModSounds.FUNGUS_THROWER_THROW_1.get();
            else if (sound == 1) throwSound = ModSounds.FUNGUS_THROWER_THROW_2.get();
            else throwSound = ModSounds.FUNGUS_THROWER_THROW_3.get();
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    throwSound, this.getSoundSource(), 1.0F, 0.9F + this.random.nextFloat() * 0.2F);
        }
    }

    public class ThrowAcidGoal extends Goal {
        public final AbstractFungusThrowerHostile thrower;
        public int throwTick;

        public ThrowAcidGoal(AbstractFungusThrowerHostile thrower) {
            this.thrower = thrower;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (thrower.fleeTime > 0) return false;
            LivingEntity target = thrower.getTarget();
            if (target == null || !target.isAlive()) return false;
            double dist = thrower.distanceTo(target);
            if (dist <= 12.0 && thrower.hasLineOfSight(target)) {
                thrower.getNavigation().stop();
                thrower.getMoveControl().strafe(0, 0);
                return true;
            } else {
                thrower.getNavigation().moveTo(target, 1.0);
                return false;
            }
        }

        @Override
        public boolean canContinueToUse() {
            return this.throwTick < 33;
        }

        @Override
        public boolean isInterruptable() {
            return false;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void start() {
            this.throwTick = 0;
            thrower.setThrowing(true);
            thrower.level().broadcastEntityEvent(thrower, (byte) 4);
        }

        @Override
        public void stop() {
            thrower.fleeTime = 20;
            thrower.setThrowing(false);
        }

        @Override
        public void tick() {
            this.throwTick++;
            thrower.getNavigation().stop();
            thrower.getMoveControl().strafe(0, 0);
            LivingEntity target = thrower.getTarget();
            if (target != null) {
                thrower.getLookControl().setLookAt(target, 100.0F, 100.0F);
                thrower.lookAt(target, 100.0F, 100.0F);
            }
            if (this.throwTick == 16 && target != null) {
                thrower.swing(InteractionHand.MAIN_HAND);
                thrower.throwAcid(target);
            }
        }
    }
}
