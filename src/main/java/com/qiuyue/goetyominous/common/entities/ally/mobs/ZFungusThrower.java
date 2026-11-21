package com.qiuyue.goetyominous.common.entities.ally.mobs;

import com.Polarice3.Goety.common.entities.neutral.ZPiglinServant;
import com.qiuyue.goetyominous.common.entities.projectile.AcidFungus;
import com.qiuyue.goetyominous.common.init.ModSounds;
import com.qiuyue.goetyominous.config.AttributesConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class ZFungusThrower extends ZPiglinServant {

    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState walkAnimationState = new AnimationState();
    public final AnimationState throwAnimationState = new AnimationState();

    private static final EntityDataAccessor<Boolean> DATA_THROWING =
            SynchedEntityData.defineId(ZFungusThrower.class, EntityDataSerializers.BOOLEAN);

    private int fleeTime;
    private boolean lastThrowing;
    private float walkWeight;

    public ZFungusThrower(EntityType<? extends ZPiglinServant> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_THROWING, false);
    }

    public boolean isThrowing() {
        return this.entityData.get(DATA_THROWING);
    }

    public void setThrowing(boolean throwing) {
        this.entityData.set(DATA_THROWING, throwing);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, AttributesConfig.FungusThrowerServantHealth.get())
                .add(Attributes.ARMOR, AttributesConfig.FungusThrowerServantArmor.get())
                .add(Attributes.FOLLOW_RANGE, 32.0)
                .add(Attributes.MOVEMENT_SPEED, 0.35)
                .add(Attributes.ATTACK_DAMAGE, AttributesConfig.FungusThrowerServantDamage.get());
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new HurtByTargetGoal(this));
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, LivingEntity.class, 8.0F, 1.2D, 1.2D,
                target -> target == this.getTarget() && target != this.getTrueOwner()));
        this.goalSelector.addGoal(2, new ThrowAcidGoal(this));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 15.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 15.0F));
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
        this.setItemSlot(EquipmentSlot.CHEST, new ItemStack(com.qiuyue.goetyominous.common.items.ModItems.RAGGED_FUNGUS_PACK.get()));
        this.setGuaranteedDrop(EquipmentSlot.CHEST);
        return data;
    }

    @Override
    public boolean isBaby() {
        return false;
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        Item item = stack.getItem();
        if ((item instanceof net.minecraft.world.item.ArmorItem armor
                && armor.getType() == net.minecraft.world.item.ArmorItem.Type.CHESTPLATE)
                || item instanceof net.minecraft.world.item.SwordItem
                || item instanceof net.minecraft.world.item.AxeItem
                || item instanceof net.minecraft.world.item.CrossbowItem) {
            return InteractionResult.PASS;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public void populateDefaultWeapons(net.minecraft.util.RandomSource random, net.minecraft.world.DifficultyInstance difficulty) {
    }

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

    public float getWalkWeight() {
        return this.walkWeight;
    }

    public AnimationState getIdleAnimationState() {
        return this.idleAnimationState;
    }

    public AnimationState getWalkAnimationState() {
        return this.walkAnimationState;
    }

    public AnimationState getThrowAnimationState() {
        return this.throwAnimationState;
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

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ZOMBIFIED_PIGLIN_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ZOMBIFIED_PIGLIN_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ZOMBIFIED_PIGLIN_DEATH;
    }

    public class ThrowAcidGoal extends Goal {
        public final ZFungusThrower thrower;
        public int throwTick;

        public ThrowAcidGoal(ZFungusThrower thrower) {
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